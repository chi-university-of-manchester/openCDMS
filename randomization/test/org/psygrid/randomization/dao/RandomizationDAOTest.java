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

package org.psygrid.randomization.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.psygrid.randomization.model.RandomizerException;
import org.psygrid.randomization.model.hibernate.IBRpbrblRandomizer;
import org.psygrid.randomization.model.hibernate.PersistableRNG;
import org.psygrid.randomization.model.hibernate.Randomizer;
import org.psygrid.randomization.model.hibernate.RpmrblRandomizer;
import org.psygrid.randomization.model.hibernate.StratifiedRandomizer;
import org.psygrid.randomization.model.hibernate.Stratum;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RandomizationDAOTest extends TestCase {

    protected ApplicationContext ctx = null;
    
    private RandomizationDAO dao;
    
    public RandomizationDAOTest() {
        String[] paths = {"applicationContext.xml"};
        ctx = new ClassPathXmlApplicationContext(paths);
    }

    
    protected void setUp() throws Exception {
        super.setUp();
        dao = (RandomizationDAO)ctx.getBean("randomizationDAOService");
    }

    
    protected void tearDown() throws Exception {
        super.tearDown();
        dao = null;
    }

    public void testRng(){
        try{
            //create two random number generators with the same seed
            long seed = (new Date()).getTime();
            PersistableRNG rng1 = new PersistableRNG();
            rng1.setSeed(seed);
            rng1.initialize();
            PersistableRNG rng2 = new PersistableRNG();
            rng2.setSeed(seed);
            rng2.initialize();
            
            //generate two random numbers with each RNG
            int rn1 = rng1.nextInt(3);
            int rn2 = rng2.nextInt(3);
            assertEquals("First random numbers are not equal", rn1, rn2);
            rn1 = rng1.nextInt(5);
            rn2 = rng2.nextInt(5);
            assertEquals("Second random numbers are not equal", rn1, rn2);
            
            //persist RNG2 then re-load it
            Long rng2id = dao.saveRng(rng2);
            rng2 = dao.getRng(rng2id);
            rng2.initialize();
            
            //generate a third random number with each RNG
            rn1 = rng1.nextInt(4);
            rn2 = rng2.nextInt(4);
            assertEquals("Third random numbers are not equal", rn1, rn2);
            
            //persist RNG2 then re-load it
            dao.saveRng(rng2);
            rng2 = dao.getRng(rng2id);
            rng2.initialize();
            
            //generate a fourth random number with each RNG
            rn1 = rng1.nextInt(10);
            rn2 = rng2.nextInt(10);
            assertEquals("Fourth random numbers are not equal", rn1, rn2);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testRpbrblRandomizer(){
        
        try{
            //create two identical "Random Permuted Blocks of Random Block Length" randomizers
            long seed = (new Date()).getTime();
            String name1 = (new java.rmi.dgc.VMID()).toString();
            RpmrblRandomizer rnd1 = new RpmrblRandomizer();
            rnd1.setName(name1);
            rnd1.addTreatment("Treatment A", "A");
            rnd1.addTreatment("Treatment B", "B");
            rnd1.createRng(seed);
            
            String name2 = (new java.rmi.dgc.VMID()).toString();
            RpmrblRandomizer rnd2 = new RpmrblRandomizer();
            rnd2.setName(name2);
            rnd2.addTreatment("Treatment A", "A");
            rnd2.addTreatment("Treatment B", "B");
            rnd2.createRng(seed);
            
            //persist the 2nd randomizer then reload it
            dao.saveRandomizer(rnd2.toDTO());
            rnd2 = (RpmrblRandomizer)dao.getRandomizer(name2).toHibernate();
            rnd2.initialize();
            
            //randomize two subjects
            String subject1 = "Subject 1";
            String trt1_1 = rnd1.allocate(subject1);
            String trt1_2 = rnd2.allocate(subject1);
            assertEquals("Randomizers have allocated subject 1 to different treatment arms", trt1_1, trt1_2);
            
            String subject2 = "Subject 2";
            String trt2_1 = rnd1.allocate(subject2);
            String trt2_2 = rnd2.allocate(subject2);
            assertEquals("Randomizers have allocated subject 2 to different treatment arms", trt2_1, trt2_2);
            
            //persist the 2nd randomizer then reload it
            dao.saveRandomizer(rnd2.toDTO());
            rnd2 = (RpmrblRandomizer)dao.getRandomizer(name2).toHibernate();
            rnd2.initialize();
            
            //randomize three subjects
            String subject3 = "Subject 3";
            String trt3_1 = rnd1.allocate(subject3);
            String trt3_2 = rnd2.allocate(subject3);
            assertEquals("Randomizers have allocated subject 3 to different treatment arms", trt3_1, trt3_2);
            
            String subject4 = "Subject 4";
            String trt4_1 = rnd1.allocate(subject4);
            String trt4_2 = rnd2.allocate(subject4);
            assertEquals("Randomizers have allocated subject 4 to different treatment arms", trt4_1, trt4_2);
            
            String subject5 = "Subject 5";
            String trt5_1 = rnd1.allocate(subject5);
            String trt5_2 = rnd2.allocate(subject5);
            assertEquals("Randomizers have allocated subject 5 to different treatment arms", trt5_1, trt5_2);
            
            //persist the 2nd randomizer then reload it
            dao.saveRandomizer(rnd2.toDTO());
            rnd2 = (RpmrblRandomizer)dao.getRandomizer(name2).toHibernate();
            rnd2.initialize();
            
            //randomize three subjects
            String subject6 = "Subject 6";
            String trt6_1 = rnd1.allocate(subject6);
            String trt6_2 = rnd2.allocate(subject6);
            assertEquals("Randomizers have allocated subject 6 to different treatment arms", trt6_1, trt6_2);
            
            String subject7 = "Subject 7";
            String trt7_1 = rnd1.allocate(subject7);
            String trt7_2 = rnd2.allocate(subject7);
            assertEquals("Randomizers have allocated subject 7 to different treatment arms", trt7_1, trt7_2);
            
            String subject8 = "Subject 8";
            String trt8_1 = rnd1.allocate(subject8);
            String trt8_2 = rnd2.allocate(subject8);
            assertEquals("Randomizers have allocated subject 8 to different treatment arms", trt8_1, trt8_2);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testIBRpbrblRandomizer(){
        
        try{
            //create two identical "Random Permuted Blocks of Random Block Length" randomizers
            long seed = (new Date()).getTime();
            String name1 = (new java.rmi.dgc.VMID()).toString();
            IBRpbrblRandomizer rnd1 = new IBRpbrblRandomizer();
            rnd1.setName(name1);
            rnd1.addTreatment("Treatment A", "A");
            rnd1.addTreatment("Treatment B", "B");
            rnd1.createRng(seed);
            rnd1.setMinBlockSize(2);
            rnd1.setMaxBlockSize(4);
            
            String name2 = (new java.rmi.dgc.VMID()).toString();
            IBRpbrblRandomizer rnd2 = new IBRpbrblRandomizer();
            rnd2.setName(name2);
            rnd2.addTreatment("Treatment A", "A");
            rnd2.addTreatment("Treatment B", "B");
            rnd2.createRng(seed);
            rnd2.setMinBlockSize(2);
            rnd2.setMaxBlockSize(4);
            
            //persist the 2nd randomizer then reload it
            dao.saveRandomizer(rnd2.toDTO());
            rnd2 = (IBRpbrblRandomizer)dao.getRandomizer(name2).toHibernate();
            rnd2.initialize();
            
            //randomize two subjects
            String subject1 = "Subject 1";
            String trt1_1 = rnd1.allocate(subject1);
            String trt1_2 = rnd2.allocate(subject1);
            assertEquals("Randomizers have allocated subject 1 to different treatment arms", trt1_1, trt1_2);
            
            String subject2 = "Subject 2";
            String trt2_1 = rnd1.allocate(subject2);
            String trt2_2 = rnd2.allocate(subject2);
            assertEquals("Randomizers have allocated subject 2 to different treatment arms", trt2_1, trt2_2);
            
            //persist the 2nd randomizer then reload it
            dao.saveRandomizer(rnd2.toDTO());
            rnd2 = (IBRpbrblRandomizer)dao.getRandomizer(name2).toHibernate();
            rnd2.initialize();
            
            //randomize three subjects
            String subject3 = "Subject 3";
            String trt3_1 = rnd1.allocate(subject3);
            String trt3_2 = rnd2.allocate(subject3);
            assertEquals("Randomizers have allocated subject 3 to different treatment arms", trt3_1, trt3_2);
            
            String subject4 = "Subject 4";
            String trt4_1 = rnd1.allocate(subject4);
            String trt4_2 = rnd2.allocate(subject4);
            assertEquals("Randomizers have allocated subject 4 to different treatment arms", trt4_1, trt4_2);
            
            String subject5 = "Subject 5";
            String trt5_1 = rnd1.allocate(subject5);
            String trt5_2 = rnd2.allocate(subject5);
            assertEquals("Randomizers have allocated subject 5 to different treatment arms", trt5_1, trt5_2);
            
            //persist the 2nd randomizer then reload it
            dao.saveRandomizer(rnd2.toDTO());
            rnd2 = (IBRpbrblRandomizer)dao.getRandomizer(name2).toHibernate();
            rnd2.initialize();
            
            //randomize three subjects
            String subject6 = "Subject 6";
            String trt6_1 = rnd1.allocate(subject6);
            String trt6_2 = rnd2.allocate(subject6);
            assertEquals("Randomizers have allocated subject 6 to different treatment arms", trt6_1, trt6_2);
            
            String subject7 = "Subject 7";
            String trt7_1 = rnd1.allocate(subject7);
            String trt7_2 = rnd2.allocate(subject7);
            assertEquals("Randomizers have allocated subject 7 to different treatment arms", trt7_1, trt7_2);
            
            String subject8 = "Subject 8";
            String trt8_1 = rnd1.allocate(subject8);
            String trt8_2 = rnd2.allocate(subject8);
            assertEquals("Randomizers have allocated subject 8 to different treatment arms", trt8_1, trt8_2);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testStratifiedRandomizer(){
        try{
            //create two identical stratified randomizers
            long seed = (new Date()).getTime();
            String name1 = (new java.rmi.dgc.VMID()).toString();
            Randomizer rnd1 = createStratifiedRnd(name1, seed);
            String name2 = (new java.rmi.dgc.VMID()).toString();
            Randomizer rnd2 = createStratifiedRnd(name2, seed);
            
            //persist the 2nd randomizer then reload it
            dao.saveRandomizer(rnd2.toDTO());
            rnd2 = dao.getRandomizer(name2).toHibernate();
            rnd2.initialize();
            
            //randomize two subjects
            String subject = "Subject 1";
            String sex = "Male";
            String centre = "North";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            String trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            String trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 2";
            sex = "Male";
            centre = "North";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 3";
            sex = "Male";
            centre = "South";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            //persist the 2nd randomizer then reload it
            dao.saveRandomizer(rnd2.toDTO());
            rnd2 = dao.getRandomizer(name2).toHibernate();
            rnd2.initialize();
            
            subject = "Subject 4";
            sex = "Female";
            centre = "South";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 5";
            sex = "Male";
            centre = "South";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 6";
            sex = "Male";
            centre = "North";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 7";
            sex = "Female";
            centre = "North";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 8";
            sex = "Female";
            centre = "South";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 9";
            sex = "Male";
            centre = "East";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 10";
            sex = "Female";
            centre = "West";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            //persist the 2nd randomizer then reload it
            dao.saveRandomizer(rnd2.toDTO());
            rnd2 = dao.getRandomizer(name2).toHibernate();
            rnd2.initialize();
            
            subject = "Subject 11";
            sex = "Female";
            centre = "East";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 12";
            sex = "Male";
            centre = "North";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 13";
            sex = "Male";
            centre = "North";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 14";
            sex = "Male";
            centre = "East";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 15";
            sex = "Female";
            centre = "East";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 16";
            sex = "Female";
            centre = "East";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 17";
            sex = "Male";
            centre = "West";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 18";
            sex = "Male";
            centre = "North";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 19";
            sex = "Female";
            centre = "South";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
            subject = "Subject 20";
            sex = "Male";
            centre = "South";
            rnd1.setParameter("Sex", sex);
            rnd1.setParameter("Centre", centre);
            trt1 = rnd1.allocate(subject);
            rnd2.setParameter("Sex", sex);
            rnd2.setParameter("Centre", centre);
            trt2 = rnd2.allocate(subject);
            assertEquals("Randomizers have allocated "+subject+" to different treatment arms", trt1, trt2);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }

    public void testCheckIntegrity(){
        try{
            long seed = (new Date()).getTime();
            String name1 = (new java.rmi.dgc.VMID()).toString();
            RpmrblRandomizer rnd = new RpmrblRandomizer();
            rnd.setName(name1);
            rnd.addTreatment("Treatment A", "A");
            rnd.addTreatment("Treatment B", "B");
            rnd.createRng(seed);

            for ( int i=0; i<50; i++ ){
                String subject = "Subject "+Integer.toString(i+1);
                rnd.allocate(subject);
            }
            
            dao.saveRandomizer(rnd.toDTO());
            
            assertTrue("Integrity test returns false", dao.checkIntegrity(name1));

        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testGetRandomizerStatistics(){
        try{
            long seed = (new Date()).getTime();
            String name1 = (new java.rmi.dgc.VMID()).toString();
            RpmrblRandomizer rnd = new RpmrblRandomizer();
            rnd.setName(name1);
            rnd.addTreatment("Treatment A", "A");
            rnd.addTreatment("Treatment B", "B");
            rnd.createRng(seed);

            Map<String, Long> checkMap = new HashMap<String, Long>();
            checkMap.put("A", new Long(0));
            checkMap.put("B", new Long(0));
            for ( int i=0; i<50; i++ ){
                String subject = "Subject "+Integer.toString(i+1);
                String trtmnt = rnd.allocate(subject);
                checkMap.put(trtmnt, new Long(checkMap.get(trtmnt).longValue()+1));
            }
            
            dao.saveRandomizer(rnd.toDTO());
            
            String[][] stats = dao.getRandomizerStatistics(name1);
            assertEquals("Array of statistics has wrong number of elements", 2, stats.length);
            assertEquals("Stats for treatment A are incorrect", checkMap.get("A").longValue(), Long.parseLong(stats[0][1]));
            assertEquals("Stats for treatment B are incorrect", checkMap.get("B").longValue(), Long.parseLong(stats[1][1]));

        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testGetAllAllocations(){
        try{
            long seed = (new Date()).getTime();
            String name1 = (new java.rmi.dgc.VMID()).toString();
            RpmrblRandomizer rnd = new RpmrblRandomizer();
            rnd.setName(name1);
            rnd.addTreatment("Treatment A", "A");
            rnd.addTreatment("Treatment B", "B");
            rnd.createRng(seed);

            for ( int i=0; i<50; i++ ){
                String subject = "Subject "+Integer.toString(i+1);
                rnd.allocate(subject);
            }
            
            dao.saveRandomizer(rnd.toDTO());
            
            assertEquals("Array of all allocations has wrong number of elements", 50, dao.getAllAllocations(name1).length);

        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testSaveRandomizer(){
        try{
            long seed = (new Date()).getTime();
            String name1 = (new java.rmi.dgc.VMID()).toString();
            RpmrblRandomizer rnd1 = new RpmrblRandomizer();
            rnd1.setName(name1);
            rnd1.addTreatment("Treatment A", "A");
            rnd1.addTreatment("Treatment B", "B");
            rnd1.createRng(seed);
            
            //persist the 2nd randomizer then reload it
            dao.saveRandomizer(rnd1.toDTO());
            
            //try to save a second randomizer with the same name
            try{
                dao.saveRandomizer(rnd1.toDTO());
                fail("Exception should have been thrown when trying to save a 2nd randomizer with the same name");
            }
            catch(DuplicateRandomizerException ex){
                //expected behaviour - do nothing
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public static StratifiedRandomizer createStratifiedRnd(String name, long seed) throws RandomizerException {
        StratifiedRandomizer rnd = new StratifiedRandomizer();
        rnd.setName(name);
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
        rnd.addTreatment("Treatment A", "A");
        rnd.addTreatment("Treatment B", "B");
        //seed the RNGs
        long[] seeds = new long[rnd.numCombinations()];
        seeds[0] = seed;
        for ( int i=1; i<seeds.length; i++ ){
            seeds[i] = seeds[0] + i;
        }
        rnd.createRngs(seeds);
        return rnd;
    }
    
}
