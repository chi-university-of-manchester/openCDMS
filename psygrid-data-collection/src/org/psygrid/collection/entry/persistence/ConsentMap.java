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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.psygrid.data.model.hibernate.Consent;
import org.psygrid.data.model.hibernate.Record;

public class ConsentMap {

    private Map<String, Set<Consent>> consentMap;
    
    public ConsentMap() {
        consentMap = new HashMap<String, Set<Consent>>();
    }

    public final Map<String, Set<Consent>> getConsentMap(){
        return Collections.unmodifiableMap(this.consentMap);
    }
    
    public final void addRecord(String identifier, Set<Consent> consent){
        consentMap.put(identifier, consent);
    }
    
    public final boolean addRecordNoOverwrite(String identifier, Set<Consent> consent){
        if ( !consentMap.containsKey(identifier) ){
            consentMap.put(identifier, consent);
            return true;
        }
        else{
            return false;
        }
    }
    
    public final Set<Consent> getConsentForRecord(String identifier){
        Set<Consent> c = this.consentMap.get(identifier);
        if ( null != c ){
            return Collections.unmodifiableSet(c);
        }
        return null;
    }
    
    public final Set<Consent> getConsentToAddToRecord(String identifier){
        Set<Consent> set = new HashSet<Consent>();
        if ( null == consentMap.get(identifier) ){
            return set;
        }
        for ( Consent c: consentMap.get(identifier) ){
            set.add(c.getBasicCopy());
        }
        return set;
    }
    
    public final boolean noConsentForProject(String projectCode){
    	for ( String key: consentMap.keySet() ){
    		if ( key.startsWith(projectCode) ){
    			return false;
    		}
    	}
    	return true;
    }
    
    public final void addConsentFromMapToRecord(Record record){
    	Set<Consent> consents = getConsentToAddToRecord(record.getIdentifier().getIdentifier());
    	for ( Consent c: consents ){
    		record.addConsent(c);
    	}
    }
    
    public final void deleteRecord(String identifier){
    	consentMap.remove(identifier);
    }
    
    public final ConsentMap2 convertToNewFormat(){
    	ConsentMap2 newMap = new ConsentMap2();
    	for ( Map.Entry<String, Set<Consent>> record : consentMap.entrySet() ){
    		newMap.addRecordNoOverwrite(record.getKey(), record.getValue());
    	}
    	return newMap;
    }
    
}
