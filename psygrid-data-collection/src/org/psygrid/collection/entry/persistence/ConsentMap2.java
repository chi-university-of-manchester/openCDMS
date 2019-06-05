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


package org.psygrid.collection.entry.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.dto.extra.ConsentResult;
import org.psygrid.data.model.dto.extra.ConsentStatusResult;
import org.psygrid.data.model.hibernate.*;

/**
 * @author Rob Harper
 *
 */
public class ConsentMap2 {

	private Map<String, Set<BasicConsent>> consentMap;
	
    public ConsentMap2() {
        consentMap = new HashMap<String, Set<BasicConsent>>();
    }

    public final boolean addRecordNoOverwrite(String identifier, Set<Consent> consent){
        if ( !consentMap.containsKey(identifier) ){
        	Set<BasicConsent> set = new HashSet<BasicConsent>();
        	for ( Consent c: consent ){
        		set.add(new BasicConsent(c.isConsentGiven(), c.getConsentForm().getId()));
        	}
        	consentMap.put(identifier, set);
            return true;
        }
        else{
            return false;
        }
    }
    
    public final boolean consentExists(String identifier){
    	return consentMap.containsKey(identifier);
    }
    
    public final boolean checkConsent(String identifier, ConsentForm cf){
    	Set<BasicConsent> bcs = consentMap.get(identifier);
    	if ( null == bcs ){
    		return false;
    	}
    	for ( BasicConsent bc: bcs){
    		if ( bc.getConsentFormId() == cf.getId().longValue() ){
    			return bc.isConsentGiven();
    		}
    	}
    	return false;
    }

    public Set<String> getIdentifiers(){
    	return consentMap.keySet();
    }
    
    public final void addConsentFromMapToRecord(Record record){
    	DataSet ds = record.getDataSet();
    	Set<BasicConsent> consents = consentMap.get(record.getIdentifier().getIdentifier());
    	if ( null != consents ){
    		for ( BasicConsent bc: consents ){
    			ConsentForm cf = findConsentFormById(ds, bc.getConsentFormId());
    			if ( null != cf ){
    				Consent c = cf.generateConsent();
    				c.setConsentGiven(bc.isConsentGiven());
    				record.addConsent(c);
    			}
    		}
    	}
    }

    private ConsentForm findConsentFormById(DataSet ds, Long consentFormId){
    	for ( int i=0, c=ds.numAllConsentFormGroups(); i<c; i++ ){
    		ConsentFormGroup cfg = ds.getAllConsentFormGroup(i);
    		for ( int j=0, d=cfg.numConsentForms(); j<d; j++ ){
    			PrimaryConsentForm pcf = cfg.getConsentForm(j);
    			if ( pcf.getId().equals(consentFormId) ){
    				return pcf;
    			}
    			for ( int k=0, e=pcf.numAssociatedConsentForms(); k<e; k++ ){
    				AssociatedConsentForm acf = pcf.getAssociatedConsentForm(k);
    				if ( acf.getId().equals(consentFormId) ){
    					return acf;
    				}
    			}
    		}
    	}
    	return null;
    }
    
    public final boolean noConsentForProject(String projectCode){
    	for ( String key: consentMap.keySet() ){
    		if ( key.startsWith(projectCode) ){
    			return false;
    		}
    	}
    	return true;
    }
    
    public final void deleteRecord(String identifier){
    	consentMap.remove(identifier);
    }

    public final void addFromConsentStatusResult(ConsentStatusResult result){
    	String lastIdentifier = null;
    	Set<BasicConsent> bcset = null;
    	for ( ConsentResult cr: result.getConsentResults() ){
    		if ( !cr.getIdentifier().equals(lastIdentifier) ){
    			bcset = new HashSet<BasicConsent>();
    			consentMap.put(cr.getIdentifier(), bcset);
    		}
    		if ( null != cr.getConsentFormId() ){
    			bcset.add(new BasicConsent(cr.isConsentGiven(), cr.getConsentFormId()));
    		}
    		lastIdentifier = cr.getIdentifier();
    	}
    }
    
    public final void synchronizeWithGroups(DatedProjectType project, List<String> groups) throws InvalidIdentifierException {
    	String projectCode = project.getIdCode();
    	List<String> toDelete = new ArrayList<String>();
    	for ( String identifier: consentMap.keySet() ){
    		if ( IdentifierHelper.getProjectCodeFromIdentifier(identifier).equals(projectCode) ){
    			if ( !groups.contains(IdentifierHelper.getGroupCodeFromIdentifier(identifier)) ){
    				toDelete.add(identifier);
    			}
    		}
    	}
    	for ( String identifier: toDelete ){
    		consentMap.remove(identifier);
    	}
    }
    
    public void removeForProject(String projectCode){
    	Iterator<Map.Entry<String, Set<BasicConsent>>> conIt = consentMap.entrySet().iterator();
    	while ( conIt.hasNext() ){
    		Map.Entry<String, Set<BasicConsent>> e = conIt.next();
    		try{
	    		if ( projectCode.equals(IdentifierHelper.getProjectCodeFromIdentifier(e.getKey())) ){
	    			conIt.remove();
	    		}
    		}
    		catch(InvalidIdentifierException ex){
    			//do nothing - should never happen
    		}
    	}
    }
    
	public class BasicConsent{
		private boolean consentGiven;
		private long consentFormId;
		public BasicConsent(boolean consentGiven, long consentFormId){
			this.consentGiven = consentGiven;
			this.consentFormId = consentFormId;
		}
		public long getConsentFormId() {
			return consentFormId;
		}
		public void setConsentFormId(long consentFormId) {
			this.consentFormId = consentFormId;
		}
		public boolean isConsentGiven() {
			return consentGiven;
		}
		public void setConsentGiven(boolean consentGiven) {
			this.consentGiven = consentGiven;
		}
		
	}
	
}
