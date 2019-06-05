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

package org.psygrid.randomization.model.hibernate;

import java.util.Date;
import java.util.Map;

import junit.framework.TestCase;

public class RpmrblRandomizerTest extends TestCase {

    public void testTwoArm(){
        try{
            RpmrblRandomizer r = new RpmrblRandomizer();
            r.createRng((new Date()).getTime());
            r.addTreatment("Treatment A", "A");
            r.addTreatment("Treatment B", "B");
            
            for ( int i=0; i<50; i++ ){
                String subject = "Subject "+Integer.toString(i+1);
                String arm = r.allocate(subject);
                System.out.println(subject+" -> "+arm);
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testThreeArm(){
        try{
            RpmrblRandomizer r = new RpmrblRandomizer();
            r.createRng((new Date()).getTime());
            r.addTreatment("Treatment A", "A");
            r.addTreatment("Treatment B", "B");
            r.addTreatment("Treatment C", "C");
            
            for ( int i=0; i<20; i++ ){
                String subject = "Subject "+Integer.toString(i+1);
                String arm = r.allocate(subject);
                System.out.println(subject+" -> "+arm);
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }

    public void testCheckIntegrity(){
        try{
            RpmrblRandomizer r = new RpmrblRandomizer();
            r.createRng((new Date()).getTime());
            r.addTreatment("Treatment A", "A");
            r.addTreatment("Treatment B", "B");            
            for ( int i=0; i<50; i++ ){
                String subject = "Subject "+Integer.toString(i+1);
                r.allocate(subject);
            }
            
            assertTrue("Integrity test returns false", r.checkIntegrity());
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testGetAllAllocations(){
        try{
            RpmrblRandomizer r = new RpmrblRandomizer();
            r.createRng((new Date()).getTime());
            r.addTreatment("Treatment A", "A");
            r.addTreatment("Treatment B", "B");            
            for ( int i=0; i<50; i++ ){
                String subject = "Subject "+Integer.toString(i+1);
                r.allocate(subject);
            }
            Map<String, String> map = r.getAllAllocations();
            assertEquals("Map of all allocations has wrong number of elements", 50, map.size());
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testGetRandomizerStatistics(){
        try{
            RpmrblRandomizer r = new RpmrblRandomizer();
            r.createRng((new Date()).getTime());
            r.addTreatment("Treatment A", "A");
            r.addTreatment("Treatment B", "B");            
            for ( int i=0; i<50; i++ ){
                String subject = "Subject "+Integer.toString(i+1);
                r.allocate(subject);
            }
            Map<String, Long> map = r.getRandomizerStatistics();
            assertEquals("Map of statistics has wrong number of elements", 2, map.size());
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
}
