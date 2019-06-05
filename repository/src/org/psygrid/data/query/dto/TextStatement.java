package org.psygrid.data.query.dto;

import java.util.Map;

import org.psygrid.data.model.dto.PersistentDTO;

public class TextStatement extends EntryStatement {
	private String value;
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public org.psygrid.data.query.hibernate.TextStatement toHibernate(
			Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		//check for an already existing instance of a hibernate object for this 
        //statement in the map of references
    	org.psygrid.data.query.hibernate.TextStatement hDS = null;
        if ( hRefs.containsKey(this)){
            hDS = (org.psygrid.data.query.hibernate.TextStatement)hRefs.get(this);
        }
        else{
            //an instance of the statement has not already
            //been created, so create it, and add it to the
            //map of references
            hDS = new org.psygrid.data.query.hibernate.TextStatement();
            hRefs.put(this, hDS);
            toHibernate(hDS, hRefs);
        }
        
        return hDS;
	}
	
	public void toHibernate(org.psygrid.data.query.hibernate.TextStatement hDS, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
    	super.toHibernate(hDS, hRefs);
    	hDS.setValue(this.value);
    }
}
