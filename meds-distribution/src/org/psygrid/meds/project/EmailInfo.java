package org.psygrid.meds.project;

public class EmailInfo {

	private String subject;
	private String body;
	
	public EmailInfo(){
	}
	
	public EmailInfo(String subject, String body){
		this.subject = subject;
		this.body = body;
	}
	
	public void setSubject(String subject){
		this.subject = subject;
	}
	
	public String getSubject(){
		return subject;
	}
	
	public void setBody(String body){
		this.body = body;
	}
	
	public String getBody(){
		return body;
	}
	
	public void validate() throws InvalidProjectException{
		if(subject == null || subject.length() == 0){
			throw new InvalidProjectException("Email has a null or zero-length subject.");
		}
		
		if(body == null || body.length() == 0){
			throw new InvalidProjectException("Email has a null or zero-length body.");
		}
	}
}
