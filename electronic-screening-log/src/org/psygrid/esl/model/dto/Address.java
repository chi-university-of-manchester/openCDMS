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

package org.psygrid.esl.model.dto;

import java.util.HashMap;
import java.util.Map;


/**
 * Class representing the address details belonging to a subject
 * 
 * @author Lucy Bridges
 *
 */
public class Address extends Auditable {

	private String address1;

	private String address2;

	private String address3;

	private String city;

	private String region;	

	private String country;  

	private String postCode;

	private String homePhone;

	private Subject subject = null;
	
	
	/**
	 * @return the address1
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * @param address1 the address1 to set
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	/**
	 * @return the address2
	 */
	public String getAddress2() {
		return address2;
	}

	/**
	 * @param address2 the address2 to set
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	/**
	 * @return the address3
	 */
	public String getAddress3() {
		return address3;
	}

	/**
	 * @param address3 the address3 to set
	 */
	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the homePhone
	 */
	public String getHomePhone() {
		return homePhone;
	}

	/**
	 * @param homePhone the homePhone to set
	 */
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	/**
	 * @return the postCode
	 */
	public String getPostCode() {
		return postCode;
	}

	/**
	 * @param postCode the postCode to set
	 */
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}
	
	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	
	public org.psygrid.esl.model.hibernate.Address toHibernate(){
		//create list to hold references to objects in the project's
		//object graph which have multiple references to them within
		//the object graph. This is used so that each object instance
		//is copied to its hibernate equivalent once and once only
		Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> dtoRefs = new HashMap<Persistent, org.psygrid.esl.model.hibernate.Persistent>();
		org.psygrid.esl.model.hibernate.Address hAddress = toHibernate(dtoRefs);
		dtoRefs = null;
		return hAddress;
	}
	
	public org.psygrid.esl.model.hibernate.Address toHibernate(Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		
		org.psygrid.esl.model.hibernate.Address hA = null;
        if ( hRefs.containsKey(this)){
            hA = (org.psygrid.esl.model.hibernate.Address)hRefs.get(this);
        }
        if ( null == hA ){
            hA = new org.psygrid.esl.model.hibernate.Address();
            hRefs.put(this, hA);
            toHibernate(hA, hRefs);
        }
        return hA;
	}

	public void toHibernate(org.psygrid.esl.model.hibernate.Address hA, Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
		super.toHibernate(hA, hRefs);
		hA.setAddress1(address1);
		hA.setAddress2(address2);
		hA.setAddress3(address3);
		hA.setCity(city);
		hA.setRegion(region);
		hA.setCountry(country);
		hA.setPostCode(postCode);
		hA.setHomePhone(homePhone);
		
		if (subject != null) {
			hA.setSubject(subject.toHibernate(hRefs));
		}
	}
	
}
