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

import java.util.HashMap;
import java.util.Map;

/*
 * This class stores a map of hashed user names and the last used version of CoCoA
 * for that user (if known).
 */
public class LastUsedCoCoAVersionMap {
	
    private Map<String, String> versionMap;
    
    public LastUsedCoCoAVersionMap(){
        versionMap = new HashMap<String, String>();
    }
    
    protected LastUsedCoCoAVersionMap(Map<String, String> versionMap){
    	this.versionMap = versionMap;
    }
    
    public void addLastUsedVersionForUser(String key, String value){
        versionMap.put(key, value);
    }
    
    public String getLastUsedVersionForUser(String key){
        return versionMap.get(key);
    }
    
    public boolean userExistsInMap(String key){
    	return versionMap.containsKey(key);
    }
    
    public LastUsedCoCoAVersionMap copy(){
    	return new LastUsedCoCoAVersionMap(new HashMap<String, String>(this.versionMap));
    }

}
