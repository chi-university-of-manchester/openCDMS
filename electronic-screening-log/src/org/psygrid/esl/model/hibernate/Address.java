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

package org.psygrid.esl.model.hibernate;

import org.psygrid.esl.model.IAddress;
import org.psygrid.esl.model.IStrata;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.IPersistent;
import org.psygrid.esl.model.StrataAllocationFault;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.lang.reflect.*;

/**
 * A realization of the address for an individual subject.
 * 
 * @author Lucy Bridges
 * 
 * @hibernate.joined-subclass table="t_address"
 * 								proxy="org.psygrid.esl.model.hibernate.Address"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Address extends Auditable implements IAddress {

	private String address1;

	private String address2;

	private String address3;

	private String city;

	private String region;	

	private String country;  

	private String postCode;

	private String homePhone;

	/**
	 * The parent subject having this address
	 */
	private ISubject subject = null;


	public Address() {
	}

	/**
	 * @see org.psygrid.esl.model.hibernate.IAddress#getAddress1()
	 * 
	 * @hibernate.property column="c_address1"
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * @see org.psygrid.esl.model.hibernate.IAddress#setAddress1(java.lang.String)
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	/**
	 * @see org.psygrid.esl.model.hibernate.IAddress#getAddress2()
	 * 
	 * @hibernate.property column="c_address2"
	 */
	public String getAddress2() {
		return address2;
	}

	/**
	 * @see org.psygrid.esl.model.hibernate.IAddress#setAddress2(java.lang.String)
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	/**
	 * @see org.psygrid.esl.model.hibernate.IAddress#getAddress3()
	 * 
	 * @hibernate.property column="c_address3"
	 */
	public String getAddress3() {
		return address3;
	}

	/**
	 * @see org.psygrid.esl.model.hibernate.IAddress#setAddress3(java.lang.String)
	 */
	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	/**
	 * @see org.psygrid.esl.model.hibernate.IAddress#getCity()
	 * 
	 * @hibernate.property column="c_city"
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @see org.psygrid.esl.model.hibernate.IAddress#setCity(java.lang.String)
	 * 
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @see org.psygrid.esl.model.hibernate.IAddress#getRegion()
	 * 
	 * @hibernate.property column="c_region"
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @see org.psygrid.esl.model.hibernate.IAddress#setRegion(java.lang.String)
	 */
	public void setRegion(String region) {
		this.region = region;
	}

	/**
	 * @see org.psygrid.esl.model.hibernate.IAddress#getCountry()
	 * 
	 * @hibernate.property column="c_country"
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @see org.psygrid.esl.model.hibernate.IAddress#setCountry(java.lang.String)
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @see org.psygrid.esl.model.hibernate.IAddress#getPostCode()
	 * 
	 * @hibernate.property column="c_post_code"
	 */
	public String getPostCode() {
		return postCode;
	}

	/**
	 * @see org.psygrid.esl.model.hibernate.IAddress#setPostCode(java.lang.String)
	 */
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	/** 
	 * @see org.psygrid.esl.model.hibernate.IAddress#getHomePhone()
	 * 
	 * @hibernate.property column="c_home_phone"
	 */
	public String getHomePhone() {
		return homePhone;
	}

	/**
	 * @see org.psygrid.esl.model.hibernate.IAddress#setHomePhone(java.lang.String)
	 */
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}


	/**
	 * @hibernate.many-to-one class="org.psygrid.esl.model.hibernate.Subject"
	 *                        column="c_subject_id"
	 *                        not-null="false"
	 *                        insert="false"
	 *                        update="false"
	 *                        unique="true"
	 */
	//was cascade="none"
	public ISubject getSubject() {
		return subject;
	}

	public void setSubject(ISubject subject) {
		this.subject = subject;
	}


	public org.psygrid.esl.model.dto.Address toDTO(){
		Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs = new HashMap<IPersistent, org.psygrid.esl.model.dto.Persistent>();
		org.psygrid.esl.model.dto.Address dtoAddress = toDTO(dtoRefs);
		dtoRefs = null;
		return dtoAddress;
	}

	public org.psygrid.esl.model.dto.Address toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs) {
		org.psygrid.esl.model.dto.Address dtoA = null;
		if ( dtoRefs.containsKey(this)){
			dtoA = (org.psygrid.esl.model.dto.Address)dtoRefs.get(this);
		}
		if ( dtoA == null ){
			dtoA = new org.psygrid.esl.model.dto.Address();
			dtoRefs.put(this, dtoA);
			toDTO(dtoA, dtoRefs);
		}
		return dtoA;
	}

	public void toDTO(org.psygrid.esl.model.dto.Address dtoA, Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs){
		super.toDTO(dtoA, dtoRefs);
		dtoA.setAddress1(address1);
		dtoA.setAddress2(address2);
		dtoA.setAddress3(address3);
		dtoA.setCity(city);
		dtoA.setRegion(region);
		dtoA.setCountry(country);
		dtoA.setPostCode(postCode);
		dtoA.setHomePhone(homePhone);
		if (subject != null) {
			dtoA.setSubject(subject.toDTO(dtoRefs));
		}
	}

	/**
	 * For a given list of Strata, use introspection to see if any of
	 * them match the fields of this class and return its value.
	 * 
	 * @param strata The strata specified for the randomisation to be applied
	 * @return Map of strata names and their values from the address object
	 * @throws StrataAllocationFault
	 */
	public Map<String, String> getStrataValues(List<IStrata> strata) throws StrataAllocationFault {

		Class c = this.getClass();
		Field[] fields = c.getDeclaredFields();

		Map<String,String> values = new HashMap<String,String>();

		for (IStrata s: strata) {
			for (Field f: fields) {
				if (s.getName().equalsIgnoreCase(f.getName())) {
					try {
						String value = (String)f.get(this);

						if (value == null) {
							throw new StrataAllocationFault("No value specified for strata "+s.getName());
						}
						//retrieve a list of the allowed values for this strata
						boolean allowed = false;
						List<String> possibleValues = s.getValues();
						for (String possible: possibleValues) {
							if (value.equals(possible)) {
								allowed = true;
								break;
							}
						}
						if (! allowed) {
							//the value given by the subject does not match any
							//of the strata's permitted values
							throw new StrataAllocationFault("The value given by the address of Subject '"+this.getSubject().getStudyNumber()+"' is not included in the range permitted by the Strata "+s.getName()+". The allowed range is:"+possibleValues);
						}

						values.put(s.getName(),value);
					}
					catch (Exception e) {
						throw new StrataAllocationFault("No value specified for strata "+s.getName() , e);
					}
				}
			}
		}

		return values;
	}
}
