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

package org.psygrid.randomization.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.psygrid.randomization.DuplicateRandomizerFault;
import org.psygrid.randomization.DuplicateSubjectFault;
import org.psygrid.randomization.Parameter;
import org.psygrid.randomization.UnknownRandomizerFault;
import org.psygrid.randomization.dao.RandomizationDAOTest;
import org.psygrid.randomization.model.hibernate.Randomizer;
import org.psygrid.randomization.model.hibernate.RpmrblRandomizer;

public class RandomizationClientTest extends TestCase {

    public void testSaveRandomizer(){
        try{

            long seed = (new Date()).getTime();
            String name = (new java.rmi.dgc.VMID()).toString();
            Randomizer rdmzr = RandomizationDAOTest.createStratifiedRnd(name, seed);
            
            RandomizationClient client = new RandomizationClient();
            client.saveRandomizer(rdmzr, null);
            
            Parameter[] params = new Parameter[2];
            params[0] = new Parameter("Sex", "Male");
            params[1] = new Parameter("Centre", "North");
            String subject = "Subject 1";
            client.allocate(name, subject, params, null);
            
            String treatment = client.getAllocation(name, subject, null);
            assertNotNull("Allocation for '"+subject+"' is null", treatment);
            
            try{
                client.saveRandomizer(rdmzr, null);
                fail("Exception should have been thrown when trying to save the randomizer again");
            }
            catch(DuplicateRandomizerFault ex){
                //expected behaviour
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testAllocate(){
        try{
            long seed = (new Date()).getTime();
            String name = (new java.rmi.dgc.VMID()).toString();
            RpmrblRandomizer rnd = new RpmrblRandomizer();
            rnd.setName(name);
            rnd.createRng(seed);
            rnd.addTreatment("Treatment 1", "1");
            rnd.addTreatment("Treatment 2", "2");
            
            RandomizationClient client = new RandomizationClient();
            client.saveRandomizer(rnd, null);
            
            String subject = "Subject 1";
            String treatment = client.allocate(name, subject, new Parameter[0], null);
            
            assertTrue("Allocated treatment is not either '1' or '2'", treatment.equals("1") || treatment.equals("2"));
        
            //try to re-allocate the same subject - expect exception
            try{
                client.allocate(name, subject, new Parameter[0], null);
                fail("Exception should have been thrown when trying to allocate a duplicate subject");
            }
            catch(DuplicateSubjectFault ex){
                //do nothing - expected behaviour
            }
            
            //try to allocate with non-existent randomizer - expect exception
            try{
                client.allocate(name+"-foo", subject, new Parameter[0], null);
                fail("Exception should have been thrown when trying to allocate with an unknown randomizer");
            }
            catch(UnknownRandomizerFault ex){
                //do nothing - expected behaviour
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testCheckIntegrity(){
        try{
            long seed = (new Date()).getTime();
            String name = (new java.rmi.dgc.VMID()).toString();
            RpmrblRandomizer rnd = new RpmrblRandomizer();
            rnd.setName(name);
            rnd.createRng(seed);
            rnd.addTreatment("Treatment 1", "1");
            rnd.addTreatment("Treatment 2", "2");
            
            RandomizationClient client = new RandomizationClient();
            client.saveRandomizer(rnd, null);
            
            //Do some allocations
            for ( int i=0; i<50; i++ ){
                String subject = "Subject "+Integer.toString(i+1);
                client.allocate(name, subject, new Parameter[0], null);
            }
            
            assertTrue("Integrity test returns false", client.checkIntegrity(name, null));
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testGetAllAllocations(){
        try{
            long seed = (new Date()).getTime();
            String name = (new java.rmi.dgc.VMID()).toString();
            RpmrblRandomizer rnd = new RpmrblRandomizer();
            rnd.setName(name);
            rnd.createRng(seed);
            rnd.addTreatment("Treatment 1", "1");
            rnd.addTreatment("Treatment 2", "2");
            
            RandomizationClient client = new RandomizationClient();
            client.saveRandomizer(rnd, null);
            
            //Do some allocations
            for ( int i=0; i<50; i++ ){
                String subject = "Subject "+Integer.toString(i+1);
                client.allocate(name, subject, new Parameter[0], null);
            }
            
            String[][] all = client.getAllAllocations(name, null);
            assertEquals("Array of all allocations has wrong number of elements", 50, all.length);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testGetRandomizerStatistics(){
        try{
            long seed = (new Date()).getTime();
            String name = (new java.rmi.dgc.VMID()).toString();
            RpmrblRandomizer rnd = new RpmrblRandomizer();
            rnd.setName(name);
            rnd.createRng(seed);
            rnd.addTreatment("Treatment 1", "1");
            rnd.addTreatment("Treatment 2", "2");
            
            RandomizationClient client = new RandomizationClient();
            client.saveRandomizer(rnd, null);
            
            //Do some allocations
            Map<String, Long> checkMap = new HashMap<String, Long>();
            checkMap.put("1", new Long(0));
            checkMap.put("2", new Long(0));
            for ( int i=0; i<50; i++ ){
                String subject = "Subject "+Integer.toString(i+1);
                String trtmnt = client.allocate(name, subject, new Parameter[0], null);
                checkMap.put(trtmnt, new Long(checkMap.get(trtmnt).longValue()+1));
            }
            
            String[][] stats = client.getRandomizerStatistics(name, null);
            assertEquals("Array of statistics has wrong number of elements", 2, stats.length);
            assertEquals("Stats for treatment 1 are incorrect", checkMap.get("1").longValue(), Long.parseLong(stats[0][1]));
            assertEquals("Stats for treatment 2 are incorrect", checkMap.get("2").longValue(), Long.parseLong(stats[1][1]));

        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
}
