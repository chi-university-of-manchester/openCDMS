package org.psygrid.meds.events;

import java.util.Date;

/**
 * Holds 'who' and 'when' info about an event
 * @author Bill
 *@hibernate.class table="t_event"
 */
public abstract class Event {
	
	private Long id; //hibernate identifier
	private String systemUser;
	private Date eventDate;
	
	public Event(){
		systemUser = null;
		eventDate = null;
	}
	
	public Event(String sysUser, Date eventDate){
		systemUser = sysUser;
		this.eventDate = eventDate;
	}
	
	public void setSystemUser(String systemUser) {
		this.systemUser = systemUser;
	}
	
    /**
     * @hibernate.property column="c_sys_user"
	 * 									not-null="true"
     */
	public String getSystemUser() {
		return systemUser;
	}
	
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	
    /**
     * @hibernate.property column="c_event_date"
	 * 									not-null="true"
     */
	public Date getEventDate() {
		return eventDate;
	}
	

	public void setId(Long id) {
		this.id = id;
	}
	
	

	/**
	 * @return the id
	 * @hibernate.id column = "c_id" generator-class="native"
	 */
	public Long getId() {
		return id;
	}
	
	protected void validate() throws InvalidEventException{
		
		if(eventDate == null){
			throw new InvalidEventException("Event date cannot be null.");
		}
		
		if(systemUser == null || systemUser.length() == 0){
			throw new InvalidEventException("Event's system user name cannot be null or zero in length.");
		}
		
	}


}
