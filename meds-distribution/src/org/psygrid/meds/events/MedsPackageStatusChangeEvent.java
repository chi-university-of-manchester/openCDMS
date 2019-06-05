package org.psygrid.meds.events;

import java.util.Date;

import org.psygrid.meds.medications.MedicationPackage;
import org.psygrid.meds.medications.PackageStatus;

/**
 * Holds info about an allocation
 * @author Bill
 *@hibernate.class table="t_package_status_change_event"
 */
public class MedsPackageStatusChangeEvent extends Event {

	private MedicationPackage eventObject;
	private StatusChangeEventType statusChangeEvent = null;
	private String additionalInfo = null;
	
	protected MedsPackageStatusChangeEvent(){
		super();
	}
	
	public MedsPackageStatusChangeEvent(String sysUser, Date eventDate, StatusChangeEventType eventType, String additionalInfo, MedicationPackage eventObject) {
		super(sysUser, eventDate);
		this.statusChangeEvent = eventType;
		this.additionalInfo = additionalInfo;
		this.eventObject = eventObject;
	}
	
	public MedsPackageStatusChangeEvent(String sysUser, Date eventDate, StatusChangeEventType eventType, MedicationPackage eventObject) {
		super(sysUser, eventDate);
		this.statusChangeEvent = eventType;
		this.eventObject = eventObject;
	}

	/**
	 * 
	 * @return
	 * @hibernate.property column="c_additional_info"
	 */
	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public StatusChangeEventType getStatusChangeEventEnum() {
		return statusChangeEvent;
	}

	public void setStatusChangeEventEnum(StatusChangeEventType statusChangeEvent) {
		this.statusChangeEvent = statusChangeEvent;
	}
	
	/**
	 * 
	 * @return
	 * @hibernate.property column="c_event_type"
	 */
	public String getStatusChangeEvent() {
		return statusChangeEvent.toString();
	}

	public void setStatusChangeEvent(String statusChangeEvent) {
		this.statusChangeEvent = StatusChangeEventType.valueOf(statusChangeEvent);
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	/**
	 * 
	 * @return - the allocation event
	 * @hibernate.many-to-one class="org.psygrid.meds.medications.MedicationPackage"
	 *                        column="c_package_id"
	 *                        not-null="true"
	 *                        cascade="none"
	 */
	public MedicationPackage getEventObject() {
		return eventObject;
	}

	public void setEventObject(MedicationPackage eventObject) {
		this.eventObject = eventObject;
	}

	public void setStatusChangeEvent(StatusChangeEventType statusChangeEvent) {
		this.statusChangeEvent = statusChangeEvent;
	}

	protected void validate() throws InvalidEventException{
		((Event)(this)).validate();
		
		if(eventObject == null){
			throw new InvalidEventException("Event must have an event object.");
		}
		
		if(this.statusChangeEvent == null){
			throw new InvalidEventException("MedsPackageStatusChangeEvent cannot have a null event type.");
		}
		
		MedsPackageStatusChangeEventInterpreter interpreter = new MedsPackageStatusChangeEventInterpreter(this);
		
		if(interpreter.getAdditionalInfo() != null && (additionalInfo == null || additionalInfo.length() == 0)){
			throw new InvalidEventException("This event requires additional info but none has been provided");
		}
			
	}

}
