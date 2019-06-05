/*
Copyright (c) 2006-2010, The University of Manchester, UK.

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
 * Bean to return the result of a call to getIdentifiersExtended().
 * 
 * @author Terry Child
 *
 */
public class IdentifierData {

	private String identifier;
		
	private String externalId;

	private boolean useExternalID;
	
	public IdentifierData(){}

	public IdentifierData(String identifier, String externalId, boolean useExternalID){
		this.identifier = identifier;
		this.externalId = externalId;
		this.useExternalID = useExternalID;
	}
		
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public void setUseExternalID(boolean useExternalId) {
		this.useExternalID = useExternalId;
	}

	public boolean getUseExternalID() {
		return useExternalID;
	}
}
