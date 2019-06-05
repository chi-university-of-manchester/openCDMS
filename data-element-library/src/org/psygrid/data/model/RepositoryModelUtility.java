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

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.MultipleVariableTest;
import org.psygrid.data.model.hibernate.Option;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.data.model.hibernate.SingleVariableTest;
import org.psygrid.data.model.hibernate.ValidationRule;


public class RepositoryModelUtility {

	static RepositoryObjectType getObjectType(Object obj){

		RepositoryObjectType objectType = null;
		
		if(obj instanceof Section){
			objectType = RepositoryObjectType.Section;
		}else if(obj instanceof Document){
			objectType = RepositoryObjectType.Document;
		}else if(obj instanceof Option){
			objectType = RepositoryObjectType.Option;
		}else if(obj instanceof Entry){
			objectType = RepositoryObjectType.Entry;
		}else if(obj instanceof ValidationRule){
			objectType = RepositoryObjectType.ValidationRule;
		}else if(obj instanceof SingleVariableTest){
			objectType = RepositoryObjectType.SingleVariableTest;
		}else if(obj instanceof MultipleVariableTest){
			objectType = RepositoryObjectType.MultipleVariableTest;
		}else{
			objectType = RepositoryObjectType.Unknown;
		}

		return objectType;
		
	}
	
}
