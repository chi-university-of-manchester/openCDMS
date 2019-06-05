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

package org.psygrid.esl.services;



/**
 * Internal interface of the ESL.
 * 
 * This interface extends the web service interface and adds transactional methods
 * that are used internally by the server but which are not exposed as web service methods.
 * 
 * The need for this interface will disappear when we update our web service library and can choose
 * which individual methods to expose as web services using annotations.
 * When this happens we can merge this interface into its parent.
 * 
 * @author Terry Child
 *
 */
public interface EslServiceInternal extends Esl {
        	
	/**
	 * This method should be called when a group is added to the repository.
	 * 
	 * @param projectCode the project
	 * @param newCode the new group code
	 * @param newName the new groups name
	 */
	public void groupAdded(String projectCode,String newCode,String newName);

	/**
	 * This method should be called when a group is updated in the repository.
	 * 
	 * @param projectCode the project
	 * @param groupCode the existing group code
	 * @param newCode the new group code
	 * @param newName the new groups name
	 */
	public void groupUpdated(String projectCode,String groupCode,String newCode,String newName);
	
	/**
	 * This method should be called when a group is deleted from the repository.
	 * 
	 * @param projectCode the project
	 * @param groupCode the group
	 */
	public void groupDeleted(String projectCode,String groupCode);
}
