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
package org.psygrid.collection.entry.model;

import org.psygrid.data.model.IValue;

import com.jgoodies.validation.ValidationResult;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.DocumentInstance;

public class DerivedPresModel extends TextPresModel {

	private static final long serialVersionUID = 2232731223060362312L;

	public DerivedPresModel(BasicResponse response, IValue bean,
            SectionPresModel sectionOccPresModel, String validationPrefix,
            DocumentInstance docInstance) {
        super(response, bean, sectionOccPresModel, validationPrefix, docInstance);
    }

	@Override
	public ValidationResult validate(boolean partial) {
	
		if(validationResults != null && validationResults.size() > 0){
			return validationResults.get(0);
		}
		
		return new ValidationResult();
	}
    
}
