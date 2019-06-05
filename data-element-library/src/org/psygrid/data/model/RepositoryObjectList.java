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

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.Persistent;


public class RepositoryObjectList  {

	final int level;
	final RepositoryModel.RepositoryObject parent;
	
	List<RepositoryModel.RepositoryObject> objectList = new ArrayList<RepositoryModel.RepositoryObject>();
	
	public RepositoryObjectList(RepositoryModel.RepositoryObject parent, final int thisLevel){
		this.level = thisLevel;
		this.parent = parent;
	}
	
	/**
	 * This adds the object the object list, creating a new RepositoryObject wrapper for it. 
	 * If the added child object also has a child,
	 * this child is added as well... until the entire model tree is populated. Additionally, every object that
	 * is the parent of a secondary relationship has its 'isParentOfSecondaryRelationship' set to true,
	 * to facilitate searching for these objects later.
	 *  
	 * @param repObj
	 */ 
	public void addRepositoryObject(RepositoryModel.RepositoryObject repObj){
		objectList.add(repObj);
	}
	
	/**
	 * Searches the list for the object passed in.
	 * If not found, it then calls the list objects to do a search of its chidren, and so on.
	 * Therefore, the whole tree is searched.
	 * 
	 * @param obj
	 * @return
	 */
	public void searchForRepositoryObject(Persistent obj, List<RepositoryModel.RepositoryObject> matchList){
		
		for(RepositoryModel.RepositoryObject wrapper: objectList){
			wrapper.searchForRepositoryObject(obj, matchList);
		}
	}
	
	public List<RepositoryModel.RepositoryObject> getRepositoryObjects(){
		return objectList;
	}
	

	public int getDepthLevel() {
		return level;
	}

	public List<RepositoryModel.RepositoryObject> getLeafNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<RepositoryModel.RepositoryObject> getObjectList() {
		return objectList;
	}
	
}
