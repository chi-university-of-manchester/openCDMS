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

import org.psygrid.data.model.hibernate.Option;
import org.psygrid.data.model.hibernate.OptionValue;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.query.IOptionStatement;

/**
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_opt_statements"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class OptionStatement extends EntryStatement implements IOptionStatement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1077773749045842891L;
	
	private Option value;
	
	public OptionStatement(){
		super();
	}
	
	public OptionStatement(Option value){
		super();
		this.value = (Option)value;
	}
	
    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Option"
     *                        column="c_option_id"
     *                        not-null="false"
     *                        cascade="none"
     */			
	public Option getValue() {
		return value;
	}
	
	public Object getTheValue(){
		return value;
	}

	public void setValue(Option value) {
		this.value = (Option)value;
	}

    public org.psygrid.data.query.dto.OptionStatement toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        //check for an already existing instance of a dto object for this 
        //statement in the map of references
    	org.psygrid.data.query.dto.OptionStatement dtoDS = null;
        if ( dtoRefs.containsKey(this)){
            dtoDS = (org.psygrid.data.query.dto.OptionStatement)dtoRefs.get(this);
        }
        else {
            //an instance of the statement has not already
            //been created, so create it, and add it to the
            //map of references
            dtoDS = new org.psygrid.data.query.dto.OptionStatement();
            dtoRefs.put(this, dtoDS);
            toDTO(dtoDS, dtoRefs, depth);
        }
        
        return dtoDS;
    }
    
	public void toDTO(org.psygrid.data.query.dto.OptionStatement dtoS, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		super.toDTO(dtoS, dtoRefs, depth);
		if ( null != this.value ){
			dtoS.setValue(this.value.toDTO(dtoRefs, depth));
		}
	}

	public Class<?> getAssociatedValueType() {
		return OptionValue.class;
	}
}
