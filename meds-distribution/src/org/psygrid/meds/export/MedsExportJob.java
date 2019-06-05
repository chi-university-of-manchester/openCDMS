package org.psygrid.meds.export;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.meds.actions.notify.AAQCWrapper;
import org.psygrid.meds.medications.MedicationPackage;
import org.psygrid.meds.medications.MedicationPackageDao;
import org.psygrid.meds.utils.MedsCSVExportFormatter;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class MedsExportJob extends QuartzJobBean {
	
	private static final Log LOG = LogFactory.getLog(MedsExportJob.class);
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd-MMM-yyyy");

	   /**
     * The mail sender used to send the email messages
     */
    protected JavaMailSender mailSender;
    
    /**
     * Repository DAO object
     */
    protected MedsExportDao exportDao;
    
    protected AAQCWrapper aaqc;
    
    protected MedicationPackageDao medsDao; 
    
    private String exportFilePath;
    
    private String exportWebUrl;
    
    /**
     * A property to allow whether mails are actually sent, or just recorded
     * in the system logs, to be set declaratively. To aid testing.
     */
    protected boolean sendMails;
    
    /**
     * The email address of the system administrator
     */
    protected String sysAdminEmail;

    public JavaMailSender getMailSender() {
        return mailSender;
    }
    
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public boolean isSendMails() {
        return sendMails;
    }

    public void setSendMails(boolean sendMails) {
        this.sendMails = sendMails;
    }

    public String getSysAdminEmail() {
        return sysAdminEmail;
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

	public MedsExportDao getExportDao() {
		return exportDao;
	}

	public void setExportDao(MedsExportDao exportDao) {
		this.exportDao = exportDao;
	}

	public MedicationPackageDao getMedsDao() {
		return medsDao;
	}

	public void setMedsDao(MedicationPackageDao medsDao) {
		this.medsDao = medsDao;
	}

	public String getExportFilePath() {
		return exportFilePath;
	}

	public void setExportFilePath(String exportFilePath) {
		this.exportFilePath = exportFilePath;
	}

	public String getExportWebUrl() {
		return exportWebUrl;
	}

	public void setExportWebUrl(String exportWebUrl) {
		this.exportWebUrl = exportWebUrl;
	}

	
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {

		ServletContext servletContext=(ServletContext)context.getJobDetail().getJobDataMap().get("servletContext");

		org.psygrid.meds.export.MedsExportRequest r = exportDao.getNextPendingRequest(false);
		if ( null != r ){

			LOG.info("Servicing export request "+r.getId());
			try{
				LOG.info("Setting status to Processing");
				exportDao.updateRequestStatus(r.getId(), MedsExportRequest.STATUS_PROCESSING);

				List<MedicationPackage> packages = medsDao.getMedicationPackagesForProject(r.getProjectCode());

			
				boolean allEmpty = false;
				
				if(packages == null || packages.size() == 0){
					allEmpty = true;
				}

				//If the export is all empty then update the request and return.
				
				if ( allEmpty ){
					//The export request returned no data
					exportDao.updateRequestStatus(r.getId(), MedsExportRequest.STATUS_NO_DATA);
					return;
				}


				//STAGE 3 - format the XML into the final output format
				List<String> files = new ArrayList<String>();
				
				String path = exportFilePath+File.separator+"meds_export_packages"+r.getId()+".csv";
				MedsCSVExportFormatter.medsPackagesToCSV(packages, r, path);
				files.add(path);
				
				String viewEventPath = exportFilePath+File.separator+"meds_export_view_events"+r.getId()+".csv";
				MedsCSVExportFormatter.medsPackageViewInfoToCSV(packages, r, viewEventPath);
				files.add(viewEventPath);
				
				String viewWorkflowEventsPath = exportFilePath+File.separator+"meds_export_workflow_events"+r.getId()+".csv";
				MedsCSVExportFormatter.medsPackageWorkflowEventsToCSV(packages, r, viewWorkflowEventsPath);
				files.add(viewWorkflowEventsPath);
				
				for ( String f:files){
					LOG.info("File: "+f);
				}

				//create a zip file
				String zipPath = exportFilePath+File.separator+"meds_export"+r.getId()+".zip";
				ZipOutputStream zip = null;
				try{
					zip = new ZipOutputStream(new FileOutputStream(zipPath));
					zip.setLevel(Deflater.BEST_COMPRESSION);
					byte[] buffer = new byte[4096];
					int bytesRead;
					for ( String file : files ){
						File f = new File(file);
						zip.putNextEntry(new ZipEntry("export"+r.getId()+File.separator+f.getName()));
						FileInputStream in = null;
						try{
							in = new FileInputStream(file);
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
					finally{
						zip.close();
					}
				

				if ( sendMails ){
					// Failure to send an email should not set the export status to error.
					try {
						//send notification email
						InternetAddress address = aaqc.lookUpEmailAddress(r.getRequestor());
						SimpleMailMessage smm = new SimpleMailMessage();
						smm.setFrom(this.sysAdminEmail);
						smm.setTo(address.getAddress());
						smm.setSentDate(new Date());
						smm.setSubject("openCDMS: Medication database export request complete");
						StringBuilder body = new StringBuilder();
						body.append("Your export request has been completed.\n\n");
						body.append("  ID="+r.getId()+"\n");
						body.append("  Project="+r.getProjectCode()+"\n");
						body.append("  Request Date="+formatter.format(r.getRequestDate())+"\n\n");
						body.append("The data may be downloaded by visiting the PsyGrid Clinical Portal:\n\n");
						body.append(this.exportWebUrl);
						smm.setText(body.toString());
						mailSender.send(smm);
					} catch (Exception e) {
						LOG.error("Unable to send an email following a completed export for "+r.getRequestor(), e);
					}
				}
					
				//delete the temporary files we created along the way
				cleanTemporaryFiles(r.getId());
			}
			catch(Exception ex){
				//an error occurred during the export process
				LOG.error("An error occurred during data export.", ex);
				r.setStatus(MedsExportRequest.STATUS_ERROR);
				exportDao.updateRequestStatus(r.getId(), MedsExportRequest.STATUS_ERROR);
			}
		}
		else{
			LOG.info("No outstanding export requests");
		}
	
	}
	
	private void cleanTemporaryFiles(final Long requestId){
		File exportDir = new File(exportFilePath);
		File[] files = exportDir.listFiles(new FileFilter(){
			public boolean accept(File pathname){
				if ( pathname.getName().startsWith("meds_export"+requestId) &&
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
