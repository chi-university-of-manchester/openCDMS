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
package org.psygrid.data.model;

import org.psygrid.data.model.hibernate.DataElementContainer;
import org.psygrid.data.model.hibernate.DataElementStatus;

import java.util.List;

public interface IDELQueryObject {
	
	public void populateAdvancedSearchCriteria(List<String> docFilterLSIDs, List<String> authorityFilterLSIDs, List<DataElementStatus> statusExclusions, boolean searchLatestRevisionOnly);
	
	/**
	 * Returns the total number of results that are returned by the query.
	 * @return
	 */
	public int getTotalResults();
	
	/**
	 * Returns the number of Results that have yet to be retrieved from the server.
	 * @return
	 */
	public int getRemainingResults();
	
	/**
	 * Returns a list of element objects populated by the last server request.
	 * Calling this method clears the object's internal array, so any subsequent calls to it
	 * will return an empty array.
	 * @return
	 */
	List<DataElementContainer> getReturnedElements();
	
	/**
	 * Returns the number of element objects returned by the last server request.
	 */
	public int getReturnedElementCount();
	
	public String getElementType(); 

	public String getSearchCriteria();

	public String getSearchType();
}
