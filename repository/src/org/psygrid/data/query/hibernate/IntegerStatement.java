/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.psygrid.data.query.hibernate;

import java.util.Map;

import org.psygrid.data.model.hibernate.IntegerValue;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.query.IIntegerStatement;

/**
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_int_statements"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class IntegerStatement extends EntryStatement implements IIntegerStatement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1567310717258464819L;
	
	private Integer value;
	
	public IntegerStatement(){
		super();
	}
	
	public IntegerStatement(Integer value){
		super();
		this.value = value;
	}
	
	/**
	 * @hibernate.property column="c_value"
	 */
	public Integer getValue() {
		return value;
	}
	
	public Object getTheValue(){
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

    public org.psygrid.data.query.dto.IntegerStatement toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        //check for an already existing instance of a dto object for this 
        //statement in the map of references
    	org.psygrid.data.query.dto.IntegerStatement dtoDS = null;
        if ( dtoRefs.containsKey(this)){
            dtoDS = (org.psygrid.data.query.dto.IntegerStatement)dtoRefs.get(this);
        }
        else {
            //an instance of the statement has not already
            //been created, so create it, and add it to the
            //map of references
            dtoDS = new org.psygrid.data.query.dto.IntegerStatement();
            dtoRefs.put(this, dtoDS);
            toDTO(dtoDS, dtoRefs, depth);
        }
        
        return dtoDS;
    }
    
	public void toDTO(org.psygrid.data.query.dto.IntegerStatement dtoS, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		super.toDTO(dtoS, dtoRefs, depth);
		dtoS.setValue(this.value);
	}

	public Class<?> getAssociatedValueType() {
		return IntegerValue.class;
	}
}
