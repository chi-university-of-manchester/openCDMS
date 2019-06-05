package org.psygrid.meds.events;

import java.util.Date;

public class MedsPackageStatusChangeEventInfo {

	private String systemUser;
	private Date eventDate;
	private String eventType = null;
	private String additionalInfo = null;
	
	public MedsPackageStatusChangeEventInfo(){
		
	}
	
	public MedsPackageStatusChangeEventInfo(String sysUser, Date eventDate, String eventType, String additionalInfo){
		this.systemUser = sysUser;
		this.eventDate = eventDate;
		this.eventType = eventType;
		this.additionalInfo = additionalInfo;
	}
	
	public MedsPackageStatusChangeEventInfo(String sysUser, Date eventDate, String eventType){
		this.systemUser = sysUser;
		this.eventDate = eventDate;
		this.eventType = eventType;
	}

	public String getEventType() {
		return eventType;
	}

	/*
	public StatusChangeEventType getEventTypeEnum(){
		return StatusChangeEventType.valueOf(eventType);
	}
	*/
	
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getSystemUser() {
		return systemUser;
	}

	public void setSystemUser(String systemUser) {
		this.systemUser = systemUser;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	
	
}
