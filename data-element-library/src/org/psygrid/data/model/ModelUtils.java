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

import org.psygrid.data.model.hibernate.Persistent;



public class ModelUtils {

	/**
	 * Returns whether these objects are representatively the same.
	 * The return method depends on the node identification method.
	 * 
	 * 
	 * @param repObject - some kind of repositoryObject (doc, entry, option, section)
	 * @param repObject - some kind of repositoryObject (doc, entry, option, section)
	 * @return
	 */
	public static boolean equals(Persistent obj1, Persistent obj2, RepositoryModel.NodeIdentificationMethod idMethod){
		
		//But options don't have lsids!
		//But sections don't have lsids!
		boolean returnValue = false;
		
		if(idMethod == RepositoryModel.NodeIdentificationMethod.Binary){
			if(obj1 == obj2)
				returnValue = true;
			else
				returnValue = false;
			
		}else if(idMethod == RepositoryModel.NodeIdentificationMethod.LSID){
			
		}else if(idMethod == RepositoryModel.NodeIdentificationMethod.nodeName){
			
		}
		
		return returnValue;
	}
	
}
