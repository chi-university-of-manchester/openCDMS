package org.psygrid.meds.events;

import java.util.Date;

import org.psygrid.meds.project.Pharmacy;

/**
 * Holds info about a centre view event 
 * @author Bill
 * @hibernate.class table="t_pharmacy_view_event"
 */
public class PharmacyViewEvent extends Event {
	
	private Pharmacy viewedPharmacy;
	
	protected PharmacyViewEvent(){
		super();
		viewedPharmacy = null;
	}
	
	public PharmacyViewEvent(String sysUser, Date eventDate, Pharmacy p){
		super(sysUser, eventDate);
		this.viewedPharmacy = p;
	}
	
	protected void setViewedPharmacy(Pharmacy p){
		viewedPharmacy = p;
	}

	/**
	 * 
	 * @return - the centre that has been viewed
	 * @hibernate.many-to-one class="org.psygrid.meds.project.Pharmacy"
	 *                        column="c_pharmacy_id"
	 *                        not-null="true"
	 *                        cascade="none"
	 */
	public Pharmacy getViewedPharmacy() {
		return viewedPharmacy;
	}

}
