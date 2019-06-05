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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import junit.framework.TestCase;

public class StratifiedRandomizerTest extends TestCase {

    public void testGenerateCombinations(){
        try{
            {
                StratifiedRandomizer rnd = new StratifiedRandomizer();
                Stratum s1 = new Stratum();
                s1.setName("Sex");
                s1.getValues().add("Male");
                s1.getValues().add("Female");
                rnd.addStratum(s1);
                
                rnd.generateCombinations("org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer");
                assertEquals("Number of combinations wrong for one stratum",2,rnd.numCombinations());
            }

            {
                StratifiedRandomizer rnd = new StratifiedRandomizer();
                Stratum s1 = new Stratum();
                s1.setName("Sex");
                s1.getValues().add("Male");
                s1.getValues().add("Female");
                rnd.addStratum(s1);
                Stratum s2 = new Stratum();
                s2.setName("Centre");
                s2.getValues().add("North");
                s2.getValues().add("South");
                s2.getValues().add("East");
                s2.getValues().add("West");
                rnd.addStratum(s2);
                
                rnd.generateCombinations("org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer");
                assertEquals("Number of combinations wrong for two strata",8,rnd.numCombinations());
            }

            {
                StratifiedRandomizer rnd = new StratifiedRandomizer();
                Stratum s1 = new Stratum();
                s1.setName("Sex");
                s1.getValues().add("Male");
                s1.getValues().add("Female");
                rnd.addStratum(s1);
                Stratum s2 = new Stratum();
                s2.setName("Centre");
                s2.getValues().add("North");
                s2.getValues().add("South");
                s2.getValues().add("East");
                s2.getValues().add("West");
                rnd.addStratum(s2);
                Stratum s3 = new Stratum();
                s3.setName("Centre2");
                s3.getValues().add("North");
                s3.getValues().add("South");
                s3.getValues().add("East");
                s3.getValues().add("West");
                rnd.addStratum(s3);
                
                rnd.generateCombinations("org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer");
                assertEquals("Number of combinations wrong for three strata",32,rnd.numCombinations());
            }

        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testAllocate(){
        try{
            StratifiedRandomizer rnd = new StratifiedRandomizer();

            //set up the strata
            Stratum s1 = new Stratum();
            s1.setName("Sex");
            s1.getValues().add("Male");
            s1.getValues().add("Female");
            rnd.addStratum(s1);
            Stratum s2 = new Stratum();
            s2.setName("Centre");
            s2.getValues().add("North");
            s2.getValues().add("South");
            s2.getValues().add("East");
            s2.getValues().add("West");
            rnd.addStratum(s2);

            //generate the combinations
            rnd.generateCombinations("org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer", 2, 4);
            
            //add the treatments
            rnd.addTreatment("Treatment A", "A");
            rnd.addTreatment("Treatment B", "B");
            
            //seed the RNGs
            long[] seeds = new long[rnd.numCombinations()];
            seeds[0] = (new Date()).getTime();
            for ( int i=1; i<seeds.length; i++ ){
                seeds[i] = seeds[0] + i;
            }
            rnd.createRngs(seeds);
            
            //create a batch of other randomizers to check the results of
            //the stratified randomizer with
            List<BlockRandomizer> testRnds = new ArrayList<BlockRandomizer>();
            for ( int i=0; i<seeds.length; i++ ){
                IBRpbrblRandomizer r = new IBRpbrblRandomizer();
                r.setMinBlockSize(2);
                r.setMaxBlockSize(4);
                r.addTreatment("Treatment A", "A");
                r.addTreatment("Treatment B", "B");
                r.createRng(seeds[i]);
                testRnds.add(r);
            }
            
            //test some allocations
            //1. Male, North
            rnd.setParameter("Sex", "Male");
            rnd.setParameter("Centre", "North");
            String subject = "Subject 1";
            String treatment = rnd.allocate(subject);
            assertEquals("Allocation for subject 1 is not correct", testRnds.get(0).allocate(subject), treatment);

            //2. Male, North
            rnd.setParameter("Sex", "Male");
            rnd.setParameter("Centre", "North");
            subject = "Subject 2";
            treatment = rnd.allocate(subject);
            assertEquals("Allocation for subject 2 is not correct", testRnds.get(0).allocate(subject), treatment);
        
            //3. Female, North
            rnd.setParameter("Sex", "Female");
            rnd.setParameter("Centre", "North");
            subject = "Subject 3";
            treatment = rnd.allocate(subject);
            assertEquals("Allocation for subject 3 is not correct", testRnds.get(4).allocate(subject), treatment);
        
            //4. Female, West
            rnd.setParameter("Sex", "Female");
            rnd.setParameter("Centre", "West");
            subject = "Subject 4";
            treatment = rnd.allocate(subject);
            assertEquals("Allocation for subject 4 is not correct", testRnds.get(7).allocate(subject), treatment);
        
            //5. Male, South
            rnd.setParameter("Sex", "Male");
            rnd.setParameter("Centre", "South");
            subject = "Subject 5";
            treatment = rnd.allocate(subject);
            assertEquals("Allocation for subject 5 is not correct", testRnds.get(1).allocate(subject), treatment);
        
            //6. Female, East
            rnd.setParameter("Sex", "Female");
            rnd.setParameter("Centre", "East");
            subject = "Subject 6";
            treatment = rnd.allocate(subject);
            assertEquals("Allocation for subject 6 is not correct", testRnds.get(6).allocate(subject), treatment);
        
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testCheckIntegrity(){
        try{
            StratifiedRandomizer rnd = new StratifiedRandomizer();

            //set up the strata
            Stratum s1 = new Stratum();
            s1.setName("Sex");
            s1.getValues().add("Male");
            s1.getValues().add("Female");
            rnd.addStratum(s1);
            Stratum s2 = new Stratum();
            s2.setName("Centre");
            s2.getValues().add("North");
            s2.getValues().add("South");
            s2.getValues().add("East");
            s2.getValues().add("West");
            rnd.addStratum(s2);

            //generate the combinations
            rnd.generateCombinations("org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer");
            
            //add the treatments
            rnd.addTreatment("Treatment A", "A");
            rnd.addTreatment("Treatment B", "B");
            
            //seed the RNGs
            long[] seeds = new long[rnd.numCombinations()];
            seeds[0] = (new Date()).getTime();
            for ( int i=1; i<seeds.length; i++ ){
                seeds[i] = seeds[0] + i;
            }
            rnd.createRngs(seeds);

            String[][] params = { 
                    {"Male", "North"},
                    {"Male", "South"},
                    {"Female", "East"},
                    {"Male", "North"},
                    {"Female", "South"},
                    {"Male", "North"},
                    {"Male", "East"},
                    {"Male", "North"},
                    {"Female", "East"},
                    {"Female", "North"},
                    {"Male", "South"},
                    {"Male", "North"},
                    {"Female", "East"},
                    {"Male", "West"},
                    {"Male", "East"},
                    {"Female", "North"},
                    {"Male", "East"},
                    {"Male", "North"},
                    {"Female", "South"}
            };
            
            for ( int i=0; i<params.length; i++ ){
                rnd.setParameter("Sex", params[i][0]);
                rnd.setParameter("Centre", params[i][1]);
                rnd.allocate("Subject "+(i+1));
            }

            assertTrue("Integrity test returns false", rnd.checkIntegrity());
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testGetAllAllocations(){
        try{
            StratifiedRandomizer rnd = new StratifiedRandomizer();

            //set up the strata
            Stratum s1 = new Stratum();
            s1.setName("Sex");
            s1.getValues().add("Male");
            s1.getValues().add("Female");
            rnd.addStratum(s1);
            Stratum s2 = new Stratum();
            s2.setName("Centre");
            s2.getValues().add("North");
            s2.getValues().add("South");
            s2.getValues().add("East");
            s2.getValues().add("West");
            rnd.addStratum(s2);

            //generate the combinations
            rnd.generateCombinations("org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer");
            
            //add the treatments
            rnd.addTreatment("Treatment A", "A");
            rnd.addTreatment("Treatment B", "B");
            
            //seed the RNGs
            long[] seeds = new long[rnd.numCombinations()];
            seeds[0] = (new Date()).getTime();
            for ( int i=1; i<seeds.length; i++ ){
                seeds[i] = seeds[0] + i;
            }
            rnd.createRngs(seeds);

            String[][] params = { 
                    {"Male", "North"},
                    {"Male", "South"},
                    {"Female", "East"},
                    {"Male", "North"},
                    {"Female", "South"},
                    {"Male", "North"},
                    {"Male", "East"},
                    {"Male", "North"},
                    {"Female", "East"},
                    {"Female", "North"},
                    {"Male", "South"},
                    {"Male", "North"},
                    {"Female", "East"},
                    {"Male", "West"},
                    {"Male", "East"},
                    {"Female", "North"},
                    {"Male", "East"},
                    {"Male", "North"},
                    {"Female", "North"},
                    {"Female", "South"}
            };
            
            Set<String> subjects = new TreeSet<String>();
            for ( int i=0; i<params.length; i++ ){
                rnd.setParameter("Sex", params[i][0]);
                rnd.setParameter("Centre", params[i][1]);
                rnd.allocate("Subject "+(i+1));
                subjects.add("Subject "+(i+1));
            }

            Map<String, String> map = rnd.getAllAllocations();
            assertEquals("Map of all allocations has wrong number of elements", 20, map.size());
            List<String> subjectList = new ArrayList<String>();
            for ( String subject: subjects ){
                subjectList.add(subject);
            }
            int counter = 0;
            for (Entry<String, String> entry: map.entrySet() ){
                assertEquals("Key (subject) is not correct",subjectList.get(counter),entry.getKey());
                System.out.println(entry.getKey());
                counter++;
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testGetRandomizerStatistics(){
        try{
            StratifiedRandomizer rnd = new StratifiedRandomizer();

            //set up the strata
            Stratum s1 = new Stratum();
            s1.setName("Sex");
            s1.getValues().add("Male");
            s1.getValues().add("Female");
            rnd.addStratum(s1);
            Stratum s2 = new Stratum();
            s2.setName("Centre");
            s2.getValues().add("North");
            s2.getValues().add("South");
            s2.getValues().add("East");
            s2.getValues().add("West");
            rnd.addStratum(s2);

            //generate the combinations
            rnd.generateCombinations("org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer");
            
            //add the treatments
            rnd.addTreatment("Treatment A", "A");
            rnd.addTreatment("Treatment B", "B");
            
            //seed the RNGs
            long[] seeds = new long[rnd.numCombinations()];
            seeds[0] = (new Date()).getTime();
            for ( int i=1; i<seeds.length; i++ ){
                seeds[i] = seeds[0] + i;
            }
            rnd.createRngs(seeds);

            String[][] params = { 
                    {"Male", "North"},
                    {"Male", "South"},
                    {"Female", "East"},
                    {"Male", "North"},
                    {"Female", "South"},
                    {"Male", "North"},
                    {"Male", "East"},
                    {"Male", "North"},
                    {"Female", "East"},
                    {"Female", "North"},
                    {"Male", "South"},
                    {"Male", "North"},
                    {"Female", "East"},
                    {"Male", "West"},
                    {"Male", "East"},
                    {"Female", "North"},
                    {"Male", "East"},
                    {"Male", "North"},
                    {"Female", "South"}
            };
            
            for ( int i=0; i<params.length; i++ ){
                rnd.setParameter("Sex", params[i][0]);
                rnd.setParameter("Centre", params[i][1]);
                rnd.allocate("Subject "+(i+1));
            }

            Map<String, Long> map = rnd.getRandomizerStatistics();
            assertEquals("Map of all allocations has wrong number of elements", 2, map.size());
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
}
