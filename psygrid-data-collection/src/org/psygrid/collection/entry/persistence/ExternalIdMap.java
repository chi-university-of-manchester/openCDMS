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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.dto.extra.ConsentResult;
import org.psygrid.data.model.dto.extra.ConsentStatusResult;

/**
 * @author Rob Harper
 *
 */
public class ExternalIdMap {

	private Map<String, String> map;
	
	public ExternalIdMap(){
		map = new HashMap<String, String>();
	}

	public void add(String identifier, String externalId){
		map.put(identifier, externalId);
	}
	
	public String get(String identifier){
		return map.get(identifier);
	}
	
	public void addFromConsentStatusResult(ConsentStatusResult result){
    	String lastIdentifier = null;
    	for ( ConsentResult cr: result.getConsentResults() ){
    		if ( !cr.getIdentifier().equals(lastIdentifier) ){
    			if ( null != cr.getExternalId() ){
    				map.put(cr.getIdentifier(), cr.getExternalId());
    			}
    		}
    		lastIdentifier = cr.getIdentifier();
    	}
	}
	
    public final boolean addNoOverwrite(String identifier, String externalId){
        if ( !map.containsKey(identifier) ){
        	map.put(identifier, externalId);
            return true;
        }
        else{
            return false;
        }
    }
    
    public final void remove(String identifier){
    	map.remove(identifier);
    }
    
    public final void synchronizeWithGroups(DatedProjectType project, List<String> groups) throws InvalidIdentifierException {
    	String projectCode = project.getIdCode();
    	List<String> toDelete = new ArrayList<String>();
    	for ( String identifier: map.keySet() ){
    		if ( IdentifierHelper.getProjectCodeFromIdentifier(identifier).equals(projectCode) ){
    			if ( !groups.contains(IdentifierHelper.getGroupCodeFromIdentifier(identifier)) ){
    				toDelete.add(identifier);
    			}
    		}
    	}
    	for ( String identifier: toDelete ){
    		map.remove(identifier);
    	}
    }
    
    public void removeForProject(String projectCode){
    	Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
    	while ( it.hasNext() ){
    		Map.Entry<String, String> e = it.next();
			try{
				if ( projectCode.equals(IdentifierHelper.getProjectCodeFromIdentifier(e.getKey()))){
					it.remove();
				}
			}
			catch(InvalidIdentifierException ex){
				//do nothing - should never happen
			}
    	}
    }

}
