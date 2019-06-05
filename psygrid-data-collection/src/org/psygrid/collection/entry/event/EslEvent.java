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

package org.psygrid.collection.entry.event;

import java.util.EventObject;

import org.psygrid.esl.model.ISubject;

public class EslEvent extends EventObject    {

    private static final long serialVersionUID = -8068025583305805088L;

    private ISubject eslSubject;
    
    private boolean saveRequired;
    
    public EslEvent(Object source, ISubject eslSubject, boolean saveRequired) {
        super(source);
        this.eslSubject = eslSubject;
        this.saveRequired = saveRequired;
    }

    public ISubject getEslSubject() {
        return eslSubject;
    }

	public boolean isSaveRequired() {
		return saveRequired;
	}

}
