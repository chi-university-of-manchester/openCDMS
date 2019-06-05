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

package org.psygrid.esl.model.hibernate;

import java.util.Map;

import org.psygrid.esl.model.IProvenanceable;
import org.psygrid.esl.model.IPersistent;

/**
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_provenanceables"
 * 						proxy="org.psygrid.esl.model.hibernate.Provenanceable"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class Provenanceable extends Persistent implements IProvenanceable {

    /**
     * Locks the object so that its properties cannot be edited.
     * <p>
     * Should be overridden on a class by class basis.
     */
    public void lock(){
        //do nothing
    }
    
    public abstract org.psygrid.esl.model.dto.Provenanceable toDTO(Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs);
    
    public void toDTO(org.psygrid.esl.model.dto.Provenanceable dtoP, Map<IPersistent, org.psygrid.esl.model.dto.Persistent> dtoRefs){
        super.toDTO(dtoP, dtoRefs);
    }

}