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
public class SecondaryIdentifierMap {

	private Map<String, String> map;
	
	public SecondaryIdentifierMap(){
		map = new HashMap<String, String>();
	}
	
	public void add(String primaryIdentifier, String secondaryIdentifier){
		map.put(primaryIdentifier, secondaryIdentifier);
	}
	
	public String get(String primaryIdentifier){
		return map.get(primaryIdentifier);
	}
	
	public boolean isIdentifierPrimary(String identifier){
		return map.containsKey(identifier);
	}
	
	public boolean isIdentifierSecondary(String identifier){
		return map.containsValue(identifier);
	}
	
	public void remove(String primaryIdentifier){
		map.remove(primaryIdentifier);
	}
	
	public String getPrimary(String secondaryIdentifier){
		for ( Map.Entry<String, String> entry: map.entrySet()){
			if ( entry.getValue().equals(secondaryIdentifier) ){
				return entry.getKey();
			}
		}
		return null;
	}
	
	public void addFromConsentStatusResult(ConsentStatusResult result){
    	String lastIdentifier = null;
    	for ( ConsentResult cr: result.getConsentResults() ){
    		if ( !cr.getIdentifier().equals(lastIdentifier) ){
    			if ( null != cr.getSecondaryIdentifier() ){
    				map.put(cr.getIdentifier(), cr.getSecondaryIdentifier());
    			}
    			//this second case is required for users who are only members of
    			//a secondary project; here we know the primary identifier for
    			//a secondary so put them intot he map "backwards"
    			if ( null != cr.getPrimaryIdentifier() ){
    				map.put(cr.getPrimaryIdentifier(), cr.getIdentifier());
    			}
    		}
    		lastIdentifier = cr.getIdentifier();
    	}
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
