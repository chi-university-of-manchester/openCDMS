package org.psygrid.data.query.hibernate;

import java.util.Map;

import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.model.hibernate.TextValue;
import org.psygrid.data.query.ITextStatement;

/**
 * 
 * @hibernate.joined-subclass table="t_text_statements"
 * @hibernate.joined-subclass-key column="c_id"
 *
 */
public class TextStatement extends EntryStatement implements ITextStatement {

	private static final long serialVersionUID = 3365855431793316978L;
	
	private String value;
	
	public TextStatement() {}
	
	public TextStatement(String value) {
		this.value = value;
	}
	
	/**
	 * @hibernate.property column="c_value"
	 */
	public String getValue() {
		return value;
	}
	
	public Object getTheValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	public org.psygrid.data.query.dto.TextStatement toDTO(
			Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs,
			RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
        //statement in the map of references
    	org.psygrid.data.query.dto.TextStatement dtoDS = null;
        if ( dtoRefs.containsKey(this)){
            dtoDS = (org.psygrid.data.query.dto.TextStatement)dtoRefs.get(this);
        }
        else {
            //an instance of the statement has not already
            //been created, so create it, and add it to the
            //map of references
            dtoDS = new org.psygrid.data.query.dto.TextStatement();
            dtoRefs.put(this, dtoDS);
            toDTO(dtoDS, dtoRefs, depth);
        }
        
        return dtoDS;
	}

	protected void toDTO(org.psygrid.data.query.dto.TextStatement dtoS, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		super.toDTO(dtoS, dtoRefs, depth);
		dtoS.setValue(this.value);
	}

	public Class<?> getAssociatedValueType() {
		return TextValue.class;
	}
}
