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
 * Information about a single group.
 * 
 * Used by the group admin web page to show the list of groups.
 * 
 * @author Terry Child
 */
public class GroupSummary {

	private Long groupID;

	private String groupCode;

	private String groupName;
		
	private String datasetCode;

	private String datasetName;

	public GroupSummary(){}

	/**
	 * @return the groupID
	 */
	public Long getGroupID() {
		return groupID;
	}

	/**
	 * @return the groupCode
	 */
	public String getGroupCode() {
		return groupCode;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @return the datasetCode
	 */
	public String getDatasetCode() {
		return datasetCode;
	}

	/**
	 * @return the datasetName
	 */
	public String getDatasetName() {
		return datasetName;
	}

	
}
