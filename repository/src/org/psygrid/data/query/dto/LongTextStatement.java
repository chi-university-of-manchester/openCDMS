package org.psygrid.data.query.dto;

import java.util.Map;

import org.psygrid.data.model.dto.PersistentDTO;

public class LongTextStatement extends TextStatement {
	public org.psygrid.data.query.hibernate.LongTextStatement toHibernate(
			Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		//check for an already existing instance of a hibernate object for this 
        //statement in the map of references
    	org.psygrid.data.query.hibernate.LongTextStatement hDS = null;
        if ( hRefs.containsKey(this)){
            hDS = (org.psygrid.data.query.hibernate.LongTextStatement)hRefs.get(this);
        }
        else{
            //an instance of the statement has not already
            //been created, so create it, and add it to the
            //map of references
            hDS = new org.psygrid.data.query.hibernate.LongTextStatement();
            hRefs.put(this, hDS);
            toHibernate(hDS, hRefs);
        }
        
        return hDS;
	}
	
	public void toHibernate(org.psygrid.data.query.hibernate.LongTextStatement hDS, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
    	super.toHibernate(hDS, hRefs);
    }
}
