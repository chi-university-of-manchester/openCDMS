package org.psygrid.data.sampletracking.server;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.sampletracking.SampleInfo;
import org.psygrid.data.sampletracking.server.model.Action;
import org.psygrid.data.sampletracking.server.model.SampleTrackingDAO;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Handle event from the sample tracking service - ideally this should be done using OSGi events.
 * @author Terry
 *
 */
class SampleTrackingEventHandler {

	private static Log logger = LogFactory.getLog(SampleTrackingEventHandler.class);

	// Don't necessarily have to store the actions in the sample tracking model
	// This should be in a generic notifications module.
	private SampleTrackingDAO dao;
	
	private boolean sendMails;

	private JavaMailSender mailSender;

	private String sysAdminEmail;
	
	/**
	 * @param sampleTrackingDAO the sampleTrackingDAO to set - injected
	 */
	public void setSampleTrackingDAO(SampleTrackingDAO sampleTrackingDAO) {
		this.dao = sampleTrackingDAO;
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

	public void handleEvent(String projectCode,SampleInfo info){
		List<Action> actions = dao.getActions(projectCode, info.getStatus());
		for(Action action: actions){
			logger.info("Sample action '"+action.getAction()+"' for sample identifier '"+info.getSampleID()+"' new status='"+info.getStatus()+"' sendMails="+sendMails );
			if(mailSender!=null && sendMails && action.getAction().equals("EMAIL")){
				String subject = action.getSubject()+" "+info.getParticipantID()+" "+info.getSampleID();
				String message = action.getMessage()+"\n\nSample ID:"+info.getSampleID()+" for participant"+info.getParticipantID()+" status changed to "+info.getStatus()+".";
				if(info.getTrackingID()!=null && info.getTrackingID().length()!=0){
					message+="\nTracking ID='"+info.getTrackingID()+"'";
				}
				sendEmail(subject,message,action.getTargets());
			}
		}
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
				logger.info("Email: Body="+message.getText());
			}
			catch(Exception ex){
				logger.error("Exception from mailSender", ex);
			}
	}

}




