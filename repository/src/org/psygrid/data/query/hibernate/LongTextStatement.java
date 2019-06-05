package org.psygrid.data.query.hibernate;

import java.util.Map;

import org.psygrid.data.model.hibernate.LongTextValue;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;

/**
 * 
 * @hibernate.joined-subclass table="t_long_text_statements"
 * @hibernate.joined-subclass-key column="c_id"
 *
 */
public class LongTextStatement extends TextStatement{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7323607067836028039L;

	public LongTextStatement() {}
	
	public LongTextStatement(String textValue) {
		super(textValue);
	}

	public org.psygrid.data.query.dto.LongTextStatement toDTO(
			Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs,
			RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
        //statement in the map of references
    	org.psygrid.data.query.dto.LongTextStatement dtoDS = null;
        if ( dtoRefs.containsKey(this)){
            dtoDS = (org.psygrid.data.query.dto.LongTextStatement)dtoRefs.get(this);
        }
        else {
            //an instance of the statement has not already
            //been created, so create it, and add it to the
            //map of references
            dtoDS = new org.psygrid.data.query.dto.LongTextStatement();
            dtoRefs.put(this, dtoDS);
            toDTO(dtoDS, dtoRefs, depth);
        }
        
        return dtoDS;
	}

	private void toDTO(org.psygrid.data.query.dto.LongTextStatement dtoS, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		super.toDTO(dtoS, dtoRefs, depth);
	}
	
	public Class<?> getAssociatedValueType() {
		return LongTextValue.class;
	}
}
