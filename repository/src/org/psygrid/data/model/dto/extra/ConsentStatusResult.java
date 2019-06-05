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
 * Bean used to return the result of query for details of
 * updated record status and consent.
 * 
 * @author Rob Harper
 *
 */
public class ConsentStatusResult {

	private ConsentResult[] consentResults;
	
	private StatusResult[] statusResults;

	public ConsentStatusResult(){}
	
	public ConsentStatusResult(ConsentResult[] consentResults, StatusResult[] statusResults){
		this.consentResults = consentResults;
		this.statusResults = statusResults;
	}
	
	public ConsentResult[] getConsentResults() {
		return consentResults;
	}

	public void setConsentResults(ConsentResult[] consentResults) {
		this.consentResults = consentResults;
	}

	public StatusResult[] getStatusResults() {
		return statusResults;
	}

	public void setStatusResults(StatusResult[] statusResults) {
		this.statusResults = statusResults;
	}
	
}
