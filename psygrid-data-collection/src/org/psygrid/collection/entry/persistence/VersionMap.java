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

public class VersionMap {

    public static final String REPO_NAME = "Repository";
    public static final String ESL_NAME = "ESL";
    public static final String AA_NAME = "AA";
    public static final String PA_NAME = "PA";

    private Map<String, String> versionMap;
    
    public VersionMap(){
        versionMap = new HashMap<String, String>();
    }
    
    public void addVersion(String key, String value){
        versionMap.put(key, value);
    }
    
    public String getVersion(String key){
        return versionMap.get(key);
    }
    
}
