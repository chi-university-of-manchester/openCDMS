

package org.psygrid.data.importing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.importing.model.ImportDAO;
import org.psygrid.data.importing.model.ImportRequest;
import org.psygrid.data.importing.plugins.ImportPlugin;
import org.psygrid.data.utils.service.AbstractServiceImpl;
import org.psygrid.data.utils.wrappers.AAQCWrapper;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class ImportServiceImpl extends AbstractServiceImpl implements ImportServiceInternal {

	private static Log logger = LogFactory.getLog(ImportServiceImpl.class);

	private ImportDAO dao;
	
	private Map<String,ImportPlugin> plugins;

	String importFilePath;
	
	private boolean sendMails;

	private JavaMailSender mailSender;

	private String sysAdminEmail;

	/**
	 * Attribute authority query client
	 */
	private AAQCWrapper aaqc;
	
	/**
	 * @param plugins the plugins to set
	 */
	public void setPlugins(Map<String, ImportPlugin> plugins) {
		this.plugins = plugins;
	}

	protected String getComponentName() {
		return "ImportService";
	}
	
	/**
	 * @param importDAO - injected
	 */
	public void setImportDAO(ImportDAO importDAO) {
		this.dao = importDAO;
	}
	
	/**
	 * @param importFilePath the importFilePath to set
	 */
	public void setImportFilePath(String importFilePath) {
		this.importFilePath = importFilePath;
	}

	/**
	 * @param sendMails the sendMails to set
	 */
	public void setSendMails(boolean sendMails) {
		this.sendMails = sendMails;
	}
	
	/**
	 * @param mailSender the mailSender to set
	 */
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	public void setSysAdminEmail(String sysAdminEmail) {
		this.sysAdminEmail = sysAdminEmail;
	}

	public AAQCWrapper getAaqc() {
		return aaqc;
	}

	public void setAaqc(AAQCWrapper aaqc) {
		this.aaqc = aaqc;
	}
	
	public String[] getImportTypes(String projectCode, String saml){
		checkPermissionsByProject(saml,"getImportTypes",RBACAction.ACTION_DR_IMPORT_DATA,projectCode);
		String[] importTypes = new String[]{};
		ImportPlugin plugin = getImportPlugin(projectCode);
		if(plugin!=null){
			importTypes = plugin.getImportTypes();
		}
		return importTypes;
	}

	public void requestImport(ImportData data, String saml){
		checkPermissionsByProject(saml,"requestImport",RBACAction.ACTION_DR_IMPORT_DATA,data.getProjectCode());
		data.setUser(findUserName(saml)) ;
		String filePath = writeDataToFile(data.getData());
		ImportRequest request = new ImportRequest(data.getProjectCode(),data.getUser(),data.getRemoteFilePath(),
				filePath,data.getDataType(),true);
		dao.saveImportRequest(request);
	}
	
	public ImportStatus[] getImportStatuses(String projectCode, String saml){
		checkPermissionsByProject(saml,"getImportStatuses",RBACAction.ACTION_DR_IMPORT_DATA,projectCode);
		List<ImportStatus> statuses = new ArrayList<ImportStatus>();
		List<ImportRequest> requests = dao.getImportRequests(projectCode);
		for(ImportRequest r: requests){
			ImportStatus s = new ImportStatus(r.getId(),r.getProjectCode(),r.getUser(),r.getRequestDate(),r.getRemoteFilePath(),
				r.isImmediate(),r.getStatus(),r.getCurrentLine(),r.getCompletedDate());
			statuses.add(s);
		}
		return statuses.toArray(new ImportStatus[]{});
	}

	
	/**
	 * Gets the next 'Pending' import request and changes its status to 'Processing'.
	 * 
	 * If a request is already 'Processing' this method returns null.
	 * 
	 * This method is not named 'getNextImportRequest' because getters are read only.
	 * 
	 */	
	public ImportRequest nextImportRequest(){

		ImportRequest request = dao.getNextImportRequest();
		if(request == null ){
			logger.info("No outstanding import requests.");
			return null;
		}
		
		request.setStatus(ImportRequest.STATUS_PROCESSING);
		dao.saveImportRequest(request);
		return request;
	}
	
	/**
	 * This method is non-transactional so that partial imports may run.
	 * @param request
	 */
	public void runImport(ImportRequest request) {
		
		logger.info("Processing import request: id='"+request.getId()+"' project='"+request.getProjectCode()+
				"' request date='"+request.getRequestDate()+
				"' remote file='"+request.getRemoteFilePath()+"' import type='"+request.getDataType()+"'");			

		ImportPlugin plugin = getImportPlugin(request.getProjectCode());

		// Log the import to a file
		String logFilePath = importFilePath+File.separator+"import"+request.getId()+".log";

		PrintStream log = null;
		try {
			log = new PrintStream(new FileOutputStream(logFilePath));
		} catch (IOException e) {
			logger.error("Problem opening import log file",e);
			return;
		}
		
		// Run the import
		try {
			plugin.run(request.getProjectCode(),request.getDataType(),request.getFilePath(),request.getUser(),aaqc,log);
			request.setStatus(ImportRequest.STATUS_COMPLETE);
			request.setCompletedDate(new Date());
		}
		catch (Exception e) {
			logger.error("Problem running import",e);
			request.setStatus(ImportRequest.STATUS_ERROR);
		}
		
		log.close();
		
		logger.info("Finished Import id='"+request.getId()+"'");
	}
	
	/**
	 * Updates the database with the import request and sends any emails.
	 * 
	 * @param request
	 */
	public void updateImportRequest(ImportRequest request){

		dao.saveImportRequest(request);		
		// Send an email
		if(sendMails){
			try {
				InternetAddress address = aaqc.lookUpEmailAddress(request.getUser());
				String email = "Import Details:\n\n"
					+" Project code: "+request.getProjectCode()+"\n"
					+" User: "+request.getUser()+"\n"
					+" File: "+request.getRemoteFilePath()+"\n"
					+" Data type: "+request.getDataType()+"\n"
					+" Request date: "+request.getRequestDate()+"\n"
					+" Import Status: "+request.getStatus()+"\n"
					+" Completed date: "+request.getCompletedDate()+"\n"
					+"\n\n\n Import log:\n\n"
					+getImportLog(request.getId());
				sendEmail("OpenCDMS Import completed [ID:"+request.getId()+"]",email,address.toString());
			} catch (Exception ex) {
				logger.error("Problem occurred when trying to send import email.", ex);
			} 
		}
	}
		
	/**
	 * Get the import plugin for the dataset.
	 * @param projectCode the project code
	 * @return
	 */
	private ImportPlugin getImportPlugin(String projectCode) {

		// Chop off study names at the first '_'.
		// This allows plugins to be tested with various versions of a dataset
		// e.g. MYSTUDY_1, MYSTUDY_2 etc.
		int underscore = projectCode.indexOf('_');
		if (underscore != -1) {
			projectCode = projectCode.substring(0, underscore);
		}

		ImportPlugin plugin = plugins.get(projectCode);
		
		logger.info("Using import plugin:'" + plugin + "'");
			
		return plugin;
	}

	
	public String getImportLog(long id){
		String logFilePath = importFilePath+File.separator+"import"+id+".log";
		// Read to log file into a string buffer
		StringBuffer buf = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(logFilePath));
			String message = null;
			while((message = reader.readLine()) != null){
				buf.append(message+"\n");
			}
		} catch (Exception e) {
			logger.error("Problem reading import log",e);
		}
		finally{
			try {
				if(reader!=null) reader.close();
			} catch (IOException e) {
				logger.error("Problem closing import log",e);
			}			
		}
		return buf.toString();
	}

	private String writeDataToFile(String data){
		UUID uuid = UUID.randomUUID();
		String filePath = importFilePath+File.separator+uuid+".csv";
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
		    out.write(data);
		    out.close();
		}
		catch (IOException ex){
			logger.error("Problem writing import data to file: filePath='"+filePath+"'",ex);
		} 
		return filePath;
	}
	
	private void sendEmail(String subject,String body,String recipients){

		SimpleMailMessage message = new SimpleMailMessage();
		String[] addresses = recipients.split(",");
		message.setTo(addresses);
		message.setFrom(sysAdminEmail);
		message.setSentDate(new Date());
		message.setSubject(subject);
		message.setText(body.toString());
		try{
			mailSender.send(message);
			StringBuilder emails = new StringBuilder();
			for ( int i=0; i<message.getTo().length; i++ ){
				if ( i > 0 ){
					emails.append("; ");
				}
				emails.append(message.getTo()[i]);
			}
			logger.info("Email: To="+emails.toString());
			logger.info("Email: Subject="+message.getSubject());
		}
		catch(Exception ex){
			logger.error("Exception from mailSender when sending import email", ex);
		}
	}

}




