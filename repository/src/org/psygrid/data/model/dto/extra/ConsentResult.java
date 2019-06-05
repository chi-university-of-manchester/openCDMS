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


package org.psygrid.data.model.dto.extra;

/**
 * Bean to return the result of a query about updated consent for a 
 * single record.
 * 
 * @author Rob Harper
 *
 */
public class ConsentResult {

	private String identifier;
	
	private String primaryIdentifier;
	
	private String secondaryIdentifier;
	
	private String externalId;
	
	private boolean consentGiven;
	
	private Long consentFormId;

	public ConsentResult(){}
	
	public ConsentResult(String identifier, String primaryIdentifier, String secondaryIdentifier, boolean consentGiven, Long consentFormId, String externalId){
		this.identifier = identifier;
		this.primaryIdentifier = primaryIdentifier;
		this.secondaryIdentifier = secondaryIdentifier;
		this.consentGiven = consentGiven;
		this.consentFormId = consentFormId;
		this.externalId = externalId;
	}
	
	public Long getConsentFormId() {
		return consentFormId;
	}

	public void setConsentFormId(Long consentFormId) {
		this.consentFormId = consentFormId;
	}

	public boolean isConsentGiven() {
		return consentGiven;
	}

	public void setConsentGiven(boolean consentGiven) {
		this.consentGiven = consentGiven;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getSecondaryIdentifier() {
		return secondaryIdentifier;
	}

	public void setSecondaryIdentifier(String secondaryIdentifier) {
		this.secondaryIdentifier = secondaryIdentifier;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getPrimaryIdentifier() {
		return primaryIdentifier;
	}

	public void setPrimaryIdentifier(String primaryIdentifier) {
		this.primaryIdentifier = primaryIdentifier;
	}
	
}
