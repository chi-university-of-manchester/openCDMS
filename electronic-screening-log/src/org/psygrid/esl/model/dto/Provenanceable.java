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

package org.psygrid.esl.model.dto;

import java.util.Map;

public abstract class Provenanceable extends Persistent {

    public abstract org.psygrid.esl.model.hibernate.Provenanceable toHibernate(Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs);
    
    public void toHibernate(org.psygrid.esl.model.hibernate.Provenanceable hP, Map<Persistent, org.psygrid.esl.model.hibernate.Persistent> hRefs){
        super.toHibernate(hP, hRefs);
    }
    
}
