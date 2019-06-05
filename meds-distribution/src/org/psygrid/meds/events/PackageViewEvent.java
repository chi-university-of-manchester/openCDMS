package org.psygrid.meds.events;

import java.util.Date;

import org.psygrid.meds.medications.MedicationPackage;

/**
 * Holds info about a meds package view event
 * @author Bill
 * @hibernate.class table="t_package_view_event"
 */
public class PackageViewEvent extends Event {

	private MedicationPackage viewedPackage;
	
	protected PackageViewEvent(){
		super();
		viewedPackage = null;
	}
	
	public PackageViewEvent(String sysUser, Date eventDate, MedicationPackage p) {
		super(sysUser, eventDate);
		viewedPackage = p;
	}

	/**
	 * 
	 * @return - the allocation event
	 * @hibernate.many-to-one class="org.psygrid.meds.medications.MedicationPackage"
	 *                        column="c_package_id"
	 *                        not-null="true"
	 *                        cascade="none"
	 */
	public MedicationPackage getViewedPackage() {
		return viewedPackage;
	}
	
	protected void setViewedPackage(MedicationPackage p){
		viewedPackage = p;
	}

}
