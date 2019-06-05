package org.psygrid.meds.events;

import java.util.Date;

public class PackageViewEventInfo {
	
	private String systemUser;
	private Date eventDate;
	private String packageId;
	
	public PackageViewEventInfo(){
		systemUser = null;
		eventDate = null;
		packageId = null;
	}
	
	public PackageViewEventInfo(String systemUser, Date eventDate, String packageId){
		this.systemUser = systemUser;
		this.eventDate = eventDate;
		this.packageId = packageId;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
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

	public void validate() throws InvalidEventException{

		if(this.eventDate == null){
			throw new InvalidEventException("View event cannot have a null event date");
		}
		
		if(this.systemUser == null || systemUser.length() == 0){
			throw new InvalidEventException("View event cannot have a null or zero-length system user.");
		}
		
		if(packageId == null || packageId.length() == 0){
			throw new InvalidEventException("View event's package id cannot be null or zero length.");
		}
	}
}
