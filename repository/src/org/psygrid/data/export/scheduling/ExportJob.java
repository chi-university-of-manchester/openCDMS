/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as 
published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.psygrid.data.export.scheduling;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletContext;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.export.CsvExportFormatter;
import org.psygrid.data.export.ExcelExportFormatter;
import org.psygrid.data.export.ExportDAO;
import org.psygrid.data.export.ExportFormat;
import org.psygrid.data.export.ExportService;
import org.psygrid.data.export.ExportServiceInternal;
import org.psygrid.data.export.SASExportFormatter;
import org.psygrid.data.export.SPSSExportFormatter;
import org.psygrid.data.export.STATAExportFormatter;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.export.hibernate.ExportSecurityActionMap;
import org.psygrid.data.export.metadata.DataSetMetaData;
import org.psygrid.data.export.metadata.Document;
import org.psygrid.data.query.QueryDAO;
import org.psygrid.data.query.QueryService;
import org.psygrid.data.query.QueryServiceInternal;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.NoDatasetException;
import org.psygrid.data.repository.transformer.TransformerException;
import org.psygrid.data.utils.scheduling.DefaultJob;
import org.psygrid.data.utils.wrappers.AAQCWrapper;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.mail.SimpleMailMessage;

/**
 * Job to process export requests.
 * 
 * @author Rob Harper
 *
 */
public class ExportJob extends DefaultJob {

	private static final Log LOG = LogFactory.getLog(ExportJob.class);

	private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd-MMM-yyyy");

	private String exportWebUrl;

	private String exportFilePath;

	private String exportXSDLocation;

	private AAQCWrapper aaqc;
	
	private QueryServiceInternal queryService;
	
	private ExportServiceInternal exportService;
		
	public void setExportFilePath(String exportFilePath) {
		this.exportFilePath = exportFilePath;
	}

	public void setAaqc(AAQCWrapper aaqc) {
		this.aaqc = aaqc;
	}

	public void setExportWebUrl(String exportWebUrl) {
		this.exportWebUrl = exportWebUrl;
	}

	public void setExportXSDLocation(String exportXSDLocation) {
		this.exportXSDLocation = exportXSDLocation;
	}
	
	/**
	 * @param queryService the queryService to set
	 */
	public void setQueryService(QueryServiceInternal queryService) {
		this.queryService = queryService;
	}

	/**
	 * @param exportService the exportService to set
	 */
	public void setExportService(ExportServiceInternal exportService) {
		this.exportService = exportService;
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		
		try {

			org.psygrid.data.export.dto.ExportRequest r = exportService.getNextPendingRequest(false);

			if ( r != null ){
	
				ExportRequest req = r.toHibernate();
				LOG.info("Servicing export request "+req.getId());
				try{
					LOG.info("Setting status to Processing");
					exportService.updateRequestStatus(req.getId(), ExportRequest.STATUS_PROCESSING);
	
					//STAGE 1 - export the data to XML files. We do this group by group to limit
					//the strain on the database at any one time
					DataSetMetaData meta = null;
	
					if (req.getQueryId() == null ){
						meta = export(req);
					}
					else{
						meta = exportFromQuery(req);
					}
						
					for ( Document d:meta.getDocuments() ) {
						LOG.info("Meta doc: "+d.getName());
					}
											
					//STAGE 2 - format the XML into the final output format

					// Grab the list of all generated XML files matching the request
					File[] xmlFiles = getXMLFiles(req);
					
					List<File> outfiles = null;

					//use the XML and metadata to create the CSV file(s)
					ExportFormat format = ExportFormat.valueOf(req.getFormat());
					File path = new File(exportFilePath+File.separator+"export"+req.getId()+".csv");
					switch (format) {
					case SINGLE_CSV:
						outfiles = CsvExportFormatter.toSingleCSV(xmlFiles, meta, path, true, req.isShowCodes(), req.isShowValues());
						break;
					case MULTIPLE_CSV:
						String pathStart = exportFilePath+File.separator+"export"+req.getId();
						outfiles = CsvExportFormatter.toMultipleCsv(xmlFiles, meta, pathStart, true,req.isShowCodes(),req.isShowValues());
						break;
					case EXCEL:
						File xlsPath = new File(exportFilePath+File.separator+"export"+req.getId()+ExcelExportFormatter.EXCEL_SUFFIX);
						ExcelExportFormatter excelExport = new ExcelExportFormatter();
						excelExport.setShowCodes(req.isShowCodes());
						excelExport.setShowHeaders(true);
						excelExport.setShowValues(req.isShowValues());
						outfiles = excelExport.toExcelDocument(xmlFiles, meta, xlsPath);
						break;
					case XML:
						outfiles = Arrays.asList(xmlFiles);
						break;
					case STATA:
						String sysmis = ".";
						outfiles = CsvExportFormatter.toSingleCSV(xmlFiles, meta, path, sysmis,false,true,false);
						STATAExportFormatter stataFormatter = new STATAExportFormatter();
						outfiles.add(stataFormatter.getSetupFile("export"+req.getId()+".csv", meta, exportFilePath+File.separator+"export"+req.getId()));
						break;
					case SPSS:
						sysmis = null;
						if (meta.getMissingValues().size() > 3) {
							//Replace any standard codes with the system missing value if missing values can't be specified (in this 
							//case because SPSS can't handle more than 3 missing codes).
							sysmis = ".";
						}
						outfiles = CsvExportFormatter.toSingleCSV(xmlFiles, meta, path, sysmis,false,true,false);
						SPSSExportFormatter spssFormatter = new SPSSExportFormatter();
						outfiles.add(spssFormatter.getSetupFile("C:\\export"+req.getId()+".csv", meta, exportFilePath+File.separator+"export"+req.getId()));
						break;
					case SAS:
						sysmis = ".";
						outfiles = CsvExportFormatter.toSingleCSV(xmlFiles, meta, path, sysmis, false,true,false);
						SASExportFormatter sasFormatter = new SASExportFormatter();
						outfiles.add(sasFormatter.getSetupFile("export"+req.getId()+".csv", meta, exportFilePath+File.separator+"export"+req.getId()));
						break;
					default:
						throw new Exception("Unknown export format: "+format);
					}
	
					// Include any csv files generated by export plugins
					includePluginFiles(outfiles,req.getId());
					
					for ( File f:outfiles){
						LOG.info("File: "+f);
					}
	
					//create a zip file
					String zipPath = exportFilePath+File.separator+"export"+req.getId()+".zip";
					ZipOutputStream zip = null;
					try{
						zip = new ZipOutputStream(new FileOutputStream(zipPath));
						zip.setLevel(Deflater.BEST_COMPRESSION);
						byte[] buffer = new byte[4096];
						int bytesRead;
						for ( File f : outfiles ){
							zip.putNextEntry(new ZipEntry("export"+req.getId()+File.separator+f.getName()));
							FileInputStream in = null;
							try{
								in = new FileInputStream(f);
								while ( (bytesRead = in.read(buffer)) != -1){
									zip.write(buffer, 0, bytesRead);
								}
							}
							finally{
								in.close(); 
							}
							zip.closeEntry();
						}
					}
					finally {
						zip.close();
					}
					
					//Generate the MD5 and SHA-1 hashes of the zip file 
					String md5Path = exportFilePath+File.separator+"export"+req.getId()+".md5";
					String shaPath = exportFilePath+File.separator+"export"+req.getId()+".sha1";
					exportService.generateHashes(zipPath, md5Path, shaPath);
	
					exportService.updateRequestSetComplete(req.getId(), zipPath, md5Path, shaPath);
					LOG.info("Export request "+req.getId()+" complete");
	
					if ( sendMails ){
						// Failure to send an email should not set the export status to error.
						try {
							//send notification email
							InternetAddress address = aaqc.lookUpEmailAddress(req.getRequestor());
							SimpleMailMessage smm = new SimpleMailMessage();
							smm.setFrom(this.sysAdminEmail);
							smm.setTo(address.getAddress());
							smm.setSentDate(new Date());
							smm.setSubject("PSYGRID: Data export request complete");
							StringBuilder body = new StringBuilder();
							body.append("Your export request has been completed.\n\n");
							body.append("  ID="+req.getId()+"\n");
							body.append("  Project="+req.getProjectCode()+"\n");
							body.append("  Request Date="+formatter.format(req.getRequestDate())+"\n\n");
							body.append("The data may be downloaded by visiting the PsyGrid Clinical Portal:\n\n");
							body.append(this.exportWebUrl);
							smm.setText(body.toString());
							mailSender.send(smm);
						} catch (Exception e) {
							LOG.error("Unable to send an email following a completed export for "+req.getRequestor(), e);
						}
					}
						
					//delete the temporary files we created along the way
					cleanTemporaryFiles(req.getId());
				}
				catch(Exception ex){
					//an error occurred during the export process
					LOG.error("An error occurred during data export.", ex);
					cleanTemporaryFiles(req.getId());
					req.setStatus(ExportRequest.STATUS_ERROR);
					try{
						exportService.updateRequestStatus(req.getId(), ExportRequest.STATUS_ERROR);
					}
					catch(DAOException daoe){
						LOG.error("Setting request status to Error - invalid request id", daoe);
					}
				}
			}
			else{
				LOG.debug("No outstanding export requests");
			}
		}
		catch(Exception ex){
			//an error occurred during the export process
			LOG.error("An error occurred during data export.", ex);
		}
	}

	private DataSetMetaData export(ExportRequest req) throws NoDatasetException, IOException, DAOException, TransformerException, XMLStreamException {

		DataSetMetaData meta = new DataSetMetaData();

		for ( String group: req.getGroups() ){
			List<ExportSecurityActionMap> actionMap = req.getActionsMap();
			FileOutputStream xmlOut = null;
			try {
				// For a normal export the intermediate xml files end in the group code.
				String xmlFile = exportFilePath+"/export"+req.getId()+"-"+group+".xml";
				xmlOut = new FileOutputStream(xmlFile);
				repositoryService.exportToXml(req, group, actionMap, xmlOut, meta);
			}
			finally {
				if ( null != xmlOut ) xmlOut.close();
			}
		}
		return meta;
	}
	
	private DataSetMetaData exportFromQuery(ExportRequest req) throws NoDatasetException, IOException, DAOException, TransformerException, XMLStreamException {

		DataSetMetaData meta = new DataSetMetaData();

		String[] ids = queryService.executeQueryForIdentifiers(req.getQueryId());
							
		FileOutputStream xmlOut = null;

		try{
			// For a query based export the file name ends in 'q' followed by the query id.
			String xmlFile = exportFilePath+"/export"+req.getId()+"-q"+req.getQueryId()+".xml";
			xmlOut = new FileOutputStream(xmlFile);
			
			List<ExportSecurityActionMap> actionMap = req.getActionsMap();			
			List<String> idList = Arrays.asList(ids);
			
			repositoryService.exportToXml(req, idList, actionMap, xmlOut, meta);
		}
		finally {
			if ( null != xmlOut ) xmlOut.close();
		}
		return meta;
	}
		
	private void includePluginFiles(final List<File> outfiles,final Long requestId){
		File exportDir = new File(exportFilePath);
		File[] files = exportDir.listFiles(new FileFilter(){
			public boolean accept(File pathname){
				return pathname.getName().startsWith("export"+requestId) &&
					 pathname.getName().endsWith("csv") && !outfiles.contains(pathname);
			}
		});
		outfiles.addAll(Arrays.asList(files));
	}

	private File[] getXMLFiles(ExportRequest req){
		List<File> files = new ArrayList<File>();
		
		// For a normal export the intermediate xml files end in the group code.
		// For a query based export they end in 'q' followed by the query id.
		if(req.getQueryId()==null){
			for(String group:req.getGroups()){
				files.add(new File(exportFilePath+File.separator+"export"+req.getId()+"-"+group+".xml"));
			}
		}
		else {
			files.add(new File(exportFilePath+File.separator+"export"+req.getId()+"-q"+req.getQueryId()+".xml"));			
		}
		return files.toArray(new File[files.size()]);
	}
	
	private void cleanTemporaryFiles(final Long requestId){
		File exportDir = new File(exportFilePath);
		File[] files = exportDir.listFiles(new FileFilter(){
			public boolean accept(File pathname){
				if ( pathname.getName().startsWith("export"+requestId) &&
					 !pathname.getName().endsWith(".zip") &&
					 !pathname.getName().endsWith(".sha1") &&
					 !pathname.getName().endsWith(".md5")){
					return true;
				}
				return false;
			}
		});
		if(files != null) {
			for ( File f: files){
				LOG.info("Deleting file "+f.getName());
				f.delete();
			}
		}
	}

		
}
