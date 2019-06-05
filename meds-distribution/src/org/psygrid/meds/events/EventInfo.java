package org.psygrid.meds.events;

import java.util.Date;

public abstract class EventInfo {
	
	private String systemUser;
	private Date eventDate;
	
	public EventInfo(){
		systemUser = null;
		setEventDate(null);
	}
	
	public EventInfo(String systemUser, Date eventDate){
		this.systemUser = systemUser;
		this.eventDate = eventDate;
	}
	
	public void setSystemUser(String systemUser) {
		this.systemUser = systemUser;
	}
	public String getSystemUser() {
		return systemUser;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public Date getEventDate() {
		return eventDate;
	}
	
	protected void validate() throws InvalidEventException{
		
		//Make sure there's a system user.
		if(systemUser == null || systemUser.length() == 0){
			throw new InvalidEventException("The allocation event's system user is null or zero-length");
		}
		
		//Make sure there's an event date.
		if(eventDate == null){
			throw new InvalidEventException("The allocation event's date is null.");
		}
		
	}
	
}
