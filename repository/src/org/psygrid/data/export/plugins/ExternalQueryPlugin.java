package org.psygrid.data.export.plugins;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.export.ExportServiceInternal;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.export.hibernate.ExternalQuery;
import org.psygrid.data.model.hibernate.*;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Implementation of the ExportPlugin interface for running queries against external databases.
 * 
 * @author "Terry Child"
 *
 */
public class ExternalQueryPlugin implements ExportPlugin {

	private static final Log logger = LogFactory.getLog(ExternalQueryPlugin.class);

	private static final String[] COLUMNS = {};
	
	
	private ExportServiceInternal exportService = null;

	private String exportFilePath = null;
		
	/**
	 * @param exportFilePath the exportFilePath to set
	 */
	public void setExportFilePath(String exportFilePath) {
		this.exportFilePath = exportFilePath;
	}

	/**
	 * Wired in the application context.
	 */
	public void setExportService(ExportServiceInternal exportService) {
		this.exportService = exportService;
	}

	public boolean isApplicable(ExportRequest request) {
//		ExternalQuery[] queries = exportService.getExternalQueries(request.getProjectCode());
//		return queries.length>0;
		return false;
	}

	public String[] getColumnNames(){
		return COLUMNS;
	}


	public Map<Long, Properties> getResults(List<Record> records,ExportRequest request) {
		// Generate a csv from an external query using the supplied query
		runExternalQuery(records, request);
		return new HashMap<Long,Properties>();
	}
	
	/**
	 * Run an query against an external data source.
	 */
	private void runExternalQuery(List<Record> records,ExportRequest request) {
		try {
			ExternalQuery[] queries = exportService.getExternalQueries(request.getProjectCode());
			if(queries.length>0){
				
				ExternalQuery query = queries[0];
								
				CSVWriter writer = null;
				try{
					// CSV output file for query results.
					File file = new File(exportFilePath+File.separator+"export"+request.getId()+"-"+query.getName()+".csv");
					writer = new CSVWriter(new FileWriter(file));
				
					Connection conn = null;
				
					try {
					       String userName = query.getUser();
					       String password = query.getPassword();
					       String url = query.getUrl();
					       Class.forName ("com.mysql.jdbc.Driver").newInstance ();
					       conn = DriverManager.getConnection (url, userName, password);
					       PreparedStatement s = conn.prepareStatement (query.getQuery());

					       boolean writeHeader = true;
					       for(Record record: records){
						       Long param = getQueryParameter(record, query);
						       if(param!=null){
							       s.setLong(1, param);
							       ResultSet resultSet = s.executeQuery();
								   writer.writeAll(resultSet, writeHeader);
								   resultSet.close();			
								   writeHeader = false;
							   }
					       }
					       
						   s.close();
					}
					finally {
					  if (conn != null) {
					    try {
					               conn.close ();
					    } catch (Exception e) { /* ignore close errors */ }
					  }
					}
				}
				finally{
						if (writer != null){
							writer.close();
						}
				}
			}
			
		} catch (Exception e) {
			logger.warn("Problem running external query",e);
		}
	}

	/**
	 * Returns a value from a response matching the document and
	 * entry names specified in the external query.
	 * @param record
	 * @param query
	 * @return
	 */
	Long getQueryParameter(Record record, ExternalQuery query){
		
		Long result = null;
		DataSet dataset = record.getDataSet();
		Document document = dataset.getDocument(query.getDocumentName());
		// Assume one occurrence of the document containing the entry
		DocumentOccurrence occurrence = document.getOccurrence(0);
		DocumentInstance docinst = record.getDocumentInstance(occurrence);
		BasicEntry entry = (BasicEntry)docinst.getOccurrence().getDocument().getEntry(query.getEntryName());
		List<Response> responses = docinst.getResponses(entry);
		
		BasicResponse response = null;
		
		// Create a response if non exists
		if(responses.size()>0){
			response = (BasicResponse)responses.get(0);
			Value value = (Value)response.getValue();
			String text = value.getValueAsString();
			try {
				result = Long.parseLong(text);
			}
			catch(NumberFormatException ex){
				logger.warn("Unable to parse response entry='"+query.getEntryName()+"', document='"+query.getDocumentName()+"', record='"+record.getIdentifier().getIdentifier()+"'");
			}
		}
		
		return result;
	}

	public Properties getResults(Record record, ExportRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
	
//	Long[] getQueryParameter(List<Record> records, ExternalQuery query){
//		List<Long> result = new ArrayList<Long>(records.size());
//		for(Record record: records){
//		}
//		return result.toArray(new Long[result.size()]);
//	}

}
