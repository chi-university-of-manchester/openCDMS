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


package org.psygrid.esl.model;

import java.util.List;
import java.util.Map;

/**
 * Interface to represent the definition of an Address belonging to a Subject
 * 
 * @author Lucy Bridges
 *
 */
public interface IAddress extends IAuditable {

	/**
	 * The first line of the address
	 * 
	 * @return String
	 */
	public abstract String getAddress1();

	/**
	 * Set the first line of the address
	 * 
	 * @param address1 the first line of the address to set
	 */
	public abstract void setAddress1(String address1);

	/**
	 * Get the second line of the adress
	 * 
	 * @return String
	 */
	public abstract String getAddress2();

	/**
	 * Set the second line of the address
	 * 
	 * @param address2 the second line of the address to set
	 */
	public abstract void setAddress2(String address2);

	/**
	 * Get the third line of the address
	 *  
	 * @return String
	 */
	public abstract String getAddress3();

	/**
	 * Set the third line of the address
	 * 
	 * @param address3 the third line of the address to set
	 */
	public abstract void setAddress3(String address3);

	/**
	 * Get the city
	 * 
	 * @return String
	 */
	public abstract String getCity();

	/**
	 * Set the city
	 * 
	 * @param city the city to set
	 */
	public abstract void setCity(String city);

	/**
	 * Get the region
	 * 
	 * @return String
	 */
	public abstract String getRegion();

	/**
	 * Set the region
	 * 
	 * @param region the region to set
	 */
	public abstract void setRegion(String region);

	/**
	 * Get the country name
	 * 
	 * @return String
	 */
	public abstract String getCountry();

	/**
	 * Set the country name
	 * 
	 * @param country the country to set
	 */
	public abstract void setCountry(String country);

	/**
	 * Get the post code
	 * 
	 * @return String
	 */
	public abstract String getPostCode();

	/**
	 * Set the postcode
	 * 
	 * @param postCode the postCode to set
	 */
	public abstract void setPostCode(String postCode);

	/**
	 * Get the home telephone number at this Address
	 * 
	 * @return String
	 */
	public abstract String getHomePhone();

	/**
	 * Set the home telephone number at this Address
	 * 
	 * @param homePhone
	 */
	public abstract void setHomePhone(String homePhone);

	/**
	 * Get the Subject this Address belongs to
	 * 
	 * @return ISubject
	 */
	public ISubject getSubject();
	
	/**
	 * Set the Subject having this Address
	 * 
	 * @param subject
	 */
	public void setSubject(ISubject subject);
	
	/**
	 * Retrieve the values for a given set of strata, used by a stratified
	 * randomiser to determine the treatment to be allocated to the Subject.
	 * 
	 * @param strata
	 * @return a Map of strata names and values
	 * @throws StrataAllocationFault
	 */
	public Map<String, String> getStrataValues(List<IStrata> strata) throws StrataAllocationFault;
	
	
	
    public org.psygrid.esl.model.dto.Address toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs);
}