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

package org.psygrid.data.reporting.scheduling;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.reporting.ManagementReport;
import org.psygrid.data.reporting.ReportingDAO;
import org.psygrid.data.reporting.Reports;
import org.psygrid.data.reporting.renderer.ExcelRenderer;
import org.psygrid.data.reporting.renderer.PdfRenderer;
import org.psygrid.data.reporting.renderer.RendererException;
import org.psygrid.data.utils.scheduling.DefaultJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Quartz job to schedule the generation of management
 * reports.
 * 
 * @author Rob Harper
 *
 */
public class ReportsJob extends DefaultJob {

    private static Log sLog = LogFactory.getLog(ReportsJob.class);
    
    protected String reportUser;

	private Reports reportsService = null;	

    public String getReportUser() {
		return reportUser;
	}

	public void setReportUser(String reportUser) {
		this.reportUser = reportUser;
	}

	public Reports getReportsService() {
		return reportsService;
	}

	public void setReportsService(Reports reportsService) {
		this.reportsService = reportsService;
	}

	@Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {
    	
        try{
            ManagementReport[] reports = reportsService.generateAllMgmtReports(reportUser, Calendar.getInstance());
            sLog.info("Number of reports generated = "+reports.length);
            PdfRenderer pdfRenderer = new PdfRenderer();
            ExcelRenderer xlRenderer = new ExcelRenderer();
            for ( ManagementReport report: reports ){
            	try{
	            	if ( null != report ){
	            		sLog.info("Processing report "+report.getTitle());
	            	}
	            	else{
	            		sLog.info("Report is null!!");
	            	}
	                if ( 0 == report.getRecipients().length ){
	                    sLog.info("No recipients for report '"+report.getTitle()+"'");
	                }
	                else{
	                	String subject = "PSYGRID: Report - "+report.getTitle();
	                	String body = "<html><body><p>Please find attached the PsyGrid report '"+report.getTitle()+
	                    "', generated by the system at "+report.getRequestDate()+"</p>"+
	                    "<p>Regards,<br/>PsyGrid Admin</p></body></html>";
	                	sLog.info("Sending email for report "+report.getTitle());
	                	sLog.info("Recipients = "+Arrays.toString(report.getRecipients()));
	                	sLog.info("Subject = "+subject);
	                	sLog.info("Text = "+body);
	                    //form the email
	                    MimeMessage message = mailSender.createMimeMessage();
	                    MimeMessageHelper helper = new MimeMessageHelper(message, true);
	                    helper.setTo(report.getRecipients());
	                    helper.setFrom(sysAdminEmail);
	                    helper.setSubject(subject);
	                    helper.setSentDate(new Date());
	                    helper.setText(body, true);
	    
	                    
	                    //create the PDF and attach it
	                    sLog.info("Generating pdf...");
	                    ByteArrayOutputStream pdfOs = new ByteArrayOutputStream();
	                    pdfRenderer.render(report, pdfOs);
	                    InputStreamSource pdfSrc = new ByteArrayResource(pdfOs.toByteArray());
	                    helper.addAttachment("report.pdf", pdfSrc);
	                    sLog.info("Pdf generated and attached");
	                    
	                    //if raw data is to be sent also create the XLS and attach it
	                    if ( report.isWithRawData() ){
	                        //extra try-catch block just for rendering as Excel, so if this fails
	                        //then the PDF report will still be emailed
	                        try{
	                        	sLog.info("Generating raw data...");
	                            ByteArrayOutputStream xlOs = new ByteArrayOutputStream();
	                            xlRenderer.render(report, xlOs);
	                            InputStreamSource xlSrc = new ByteArrayResource(xlOs.toByteArray());
	                            helper.addAttachment("report.xls", xlSrc);
	                            sLog.info("Raw data generated and attached");
	                        }
	                        catch(RendererException ex){
	                            sLog.error("executeInternal"+": "+ex.getClass().getSimpleName(),ex);
	                        }
	                        catch(IOException ex){
	                            sLog.error("executeInternal"+": "+ex.getClass().getSimpleName(),ex);
	                        }
	                    }
	                    
	                    if ( sendMails ){
	                    	sLog.info("Sending the email...");
	                        mailSender.send(message);
	                        sLog.info("Mail sent.");
	                    }
	                    
	                    StringBuilder builder = new StringBuilder();
	                    builder.append("Report '"+report.getTitle()+"' sent to ");
	                    String[] recipients = report.getRecipients();
	                    for ( int i=0; i<recipients.length; i++ ){
	                        if ( i > 0 ){
	                            builder.append(", ");
	                        }
	                        builder.append(recipients[i]);
	                    }
	                    sLog.info(builder.toString());
	                }
            	}
            	catch (Exception ex){
            		sLog.error("Error processing report '"+report.getTitle()+"'",ex);
            	}
            }
        }
        catch(Exception ex){
            sLog.error("executeInternal"+": "+ex.getClass().getSimpleName(),ex);
        }
    }

}