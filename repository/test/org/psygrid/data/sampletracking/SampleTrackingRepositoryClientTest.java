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

package org.psygrid.data.sampletracking;

import junit.framework.TestCase;

import org.psygrid.data.repository.client.RepositoryClient;

public class SampleTrackingRepositoryClientTest extends TestCase {
    
        
    public SampleTrackingRepositoryClientTest() {
    }
        
    public void testGetVersion(){
        try{
            RepositoryClient client = new RepositoryClient();
            System.out.println(client.getVersion());
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
    public void testGetDataSetComplete(){
        try{
            RepositoryClient client = new RepositoryClient();
            
    	    ConfigInfo defaultConfig = client.getSampleConfig("TST0",null);
    	    assertNotNull("default should not be null",defaultConfig);
    	    
    	    ConfigInfo conf = new ConfigInfo("TST",false,false,false,"","",true,"","",":",
    	    		new String[]{"BLOOD","PLASMA","SERUM"},new String[]{"CLEAR","BLUE","RED"},new String[]{"ALLOCATED","DESPATCHED","RECEIVED"},
    	    		144,77,12,false);
    	    client.saveSampleConfig(conf,null);
    	    ConfigInfo loaded = client.getSampleConfig("TST",null);
    	    assertFalse("isTracking differs", loaded.isTracking());
    	    assertFalse("autoPopulate differs", loaded.isAutoParticipantID());
    	    assertFalse("isUsingExternalID differs", loaded.isUsingExternalID());
    	    loaded.setTracking(true);
    	    loaded.setAutoParticipantID(true);
    	    loaded.setUsingExternalID(true);
    	    client.saveSampleConfig(loaded,null);
    		ConfigInfo reloaded = client.getSampleConfig("TST",null);
    	    assertTrue("isTracking differs", reloaded.isTracking());
    	    assertTrue("autoPopulate differs", reloaded.isAutoParticipantID());
    	    assertTrue("isUsingExternalID differs", reloaded.isUsingExternalID());
    	    assertEquals("Status invalid","DESPATCHED",reloaded.getStatuses()[1]);
    	    assertEquals("Tube type invalid","RED",reloaded.getTubeTypes()[2]);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }

}
