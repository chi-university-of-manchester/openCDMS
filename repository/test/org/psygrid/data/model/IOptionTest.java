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

package org.psygrid.data.model;

import org.psygrid.data.model.hibernate.EntryStatus;
import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.data.model.hibernate.Option;
import org.psygrid.data.model.hibernate.OptionDependent;
import org.psygrid.data.model.hibernate.TextEntry;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;


public class IOptionTest extends ModelTest {

    @Test()
	public void testNumOptionDependents(){
            Option option = factory.createOption("option", "option");
            OptionDependent od1 = factory.createOptionDependent();
            String name1 = "te1";
            TextEntry te1 = factory.createTextEntry(name1);
            od1.setDependentEntry(te1);
            od1.setEntryStatus(EntryStatus.DISABLED);
            OptionDependent od2 = factory.createOptionDependent();
            String name2 = "te2";
            TextEntry te2 = factory.createTextEntry(name2);
            od2.setDependentEntry(te2);
            od2.setEntryStatus(EntryStatus.DISABLED);
            OptionDependent od3 = factory.createOptionDependent();
            String name3 = "te3";
            TextEntry te3 = factory.createTextEntry(name3);
            od3.setDependentEntry(te3);
            od3.setEntryStatus(EntryStatus.DISABLED);
            option.addOptionDependent(od1);
            option.addOptionDependent(od2);
            option.addOptionDependent(od3);
            
            AssertJUnit.assertEquals("Option has wrong number of dependents",3,option.numOptionDependents());
    }
    
    @Test()
	public void testAddOptionDependent(){
            Option option = factory.createOption("option", "option");
            OptionDependent od1 = factory.createOptionDependent();
            String name1 = "te1";
            TextEntry te1 = factory.createTextEntry(name1);
            od1.setDependentEntry(te1);
            od1.setEntryStatus(EntryStatus.DISABLED);
            OptionDependent od2 = factory.createOptionDependent();
            String name2 = "te2";
            TextEntry te2 = factory.createTextEntry(name2);
            od2.setDependentEntry(te2);
            od2.setEntryStatus(EntryStatus.DISABLED);
            OptionDependent od3 = factory.createOptionDependent();
            String name3 = "te3";
            TextEntry te3 = factory.createTextEntry(name3);
            od3.setDependentEntry(te3);
            od3.setEntryStatus(EntryStatus.DISABLED);
            option.addOptionDependent(od1);
            AssertJUnit.assertEquals("Option has wrong number of dependents",1,option.numOptionDependents());
            AssertJUnit.assertEquals("Dependent an index 0 has the wrong entry",name1,option.getOptionDependent(0).getDependentEntry().getName());
            
            option.addOptionDependent(od2);
            option.addOptionDependent(od3);
            AssertJUnit.assertEquals("Option has wrong number of dependents",3,option.numOptionDependents());
            AssertJUnit.assertEquals("Dependent an index 0 has the wrong entry",name1,option.getOptionDependent(0).getDependentEntry().getName());
            AssertJUnit.assertEquals("Dependent an index 1 has the wrong entry",name2,option.getOptionDependent(1).getDependentEntry().getName());
            AssertJUnit.assertEquals("Dependent an index 2 has the wrong entry",name3,option.getOptionDependent(2).getDependentEntry().getName());
    }
    
    @Test()
	public void testAddOptionDependent_Null(){
            Option option = factory.createOption("option", "option");

            try{
                option.addOptionDependent(null);
                Assert.fail("Exception should have been thrown when trying to add a null option dependent");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Option has wrong number of dependents",0,option.numOptionDependents());
            }
    }
    
    @Test()
	public void testGetOptionDependent_Success(){
            Option option = factory.createOption("option", "option");
            OptionDependent od1 = factory.createOptionDependent();
            String name1 = "te1";
            TextEntry te1 = factory.createTextEntry(name1);
            od1.setDependentEntry(te1);
            od1.setEntryStatus(EntryStatus.DISABLED);
            OptionDependent od2 = factory.createOptionDependent();
            String name2 = "te2";
            TextEntry te2 = factory.createTextEntry(name2);
            od2.setDependentEntry(te2);
            od2.setEntryStatus(EntryStatus.DISABLED);
            OptionDependent od3 = factory.createOptionDependent();
            String name3 = "te3";
            TextEntry te3 = factory.createTextEntry(name3);
            od3.setDependentEntry(te3);
            od3.setEntryStatus(EntryStatus.DISABLED);
            option.addOptionDependent(od1);
            option.addOptionDependent(od2);
            option.addOptionDependent(od3);
            
            OptionDependent odTest = option.getOptionDependent(1);
            AssertJUnit.assertEquals("Dependent an index 1 has the wrong entry",name2,odTest.getDependentEntry().getName());
            AssertJUnit.assertEquals("Dependent an index 1 has the wrong status",EntryStatus.DISABLED,odTest.getEntryStatus());
    }
    
    @Test()
	public void testGetOptionDependent_InvalidIndex(){
            Option option = factory.createOption("option", "option");
            OptionDependent od1 = factory.createOptionDependent();
            String name1 = "te1";
            TextEntry te1 = factory.createTextEntry(name1);
            od1.setDependentEntry(te1);
            od1.setEntryStatus(EntryStatus.DISABLED);
            OptionDependent od2 = factory.createOptionDependent();
            String name2 = "te2";
            TextEntry te2 = factory.createTextEntry(name2);
            od2.setDependentEntry(te2);
            od2.setEntryStatus(EntryStatus.DISABLED);
            OptionDependent od3 = factory.createOptionDependent();
            String name3 = "te3";
            TextEntry te3 = factory.createTextEntry(name3);
            od3.setDependentEntry(te3);
            od3.setEntryStatus(EntryStatus.DISABLED);
            option.addOptionDependent(od1);
            option.addOptionDependent(od2);
            option.addOptionDependent(od3);
            
            try{
                option.getOptionDependent(-1);
                Assert.fail("Exception should have been thrown when trying to get an option dependent using an invalid index (-1)");
            }
            catch(ModelException ex){
                //do nothing
            }
            
            try{
                option.getOptionDependent(3);
                Assert.fail("Exception should have been thrown when trying to get an option dependent using an invalid index (3)");
            }
            catch(ModelException ex){
                //do nothing
            }
    }

    @Test()
	public void testRemoveOptionDependent_Success(){
            Option option = factory.createOption("option", "option");
            OptionDependent od1 = factory.createOptionDependent();
            String name1 = "te1";
            TextEntry te1 = factory.createTextEntry(name1);
            od1.setDependentEntry(te1);
            od1.setEntryStatus(EntryStatus.DISABLED);
            OptionDependent od2 = factory.createOptionDependent();
            String name2 = "te2";
            TextEntry te2 = factory.createTextEntry(name2);
            od2.setDependentEntry(te2);
            od2.setEntryStatus(EntryStatus.DISABLED);
            OptionDependent od3 = factory.createOptionDependent();
            String name3 = "te3";
            TextEntry te3 = factory.createTextEntry(name3);
            od3.setDependentEntry(te3);
            od3.setEntryStatus(EntryStatus.DISABLED);
            option.addOptionDependent(od1);
            option.addOptionDependent(od2);
            option.addOptionDependent(od3);
            
            option.removeOptionDependent(0);
            AssertJUnit.assertEquals("Option has wrong number of dependents",2,option.numOptionDependents());
            AssertJUnit.assertEquals("Dependent at index 0 has the wrong entry",name2,option.getOptionDependent(0).getDependentEntry().getName());
            AssertJUnit.assertEquals("Dependent at index 1 has the wrong entry",name3,option.getOptionDependent(1).getDependentEntry().getName());
            
            option.removeOptionDependent(1);
            AssertJUnit.assertEquals("Option has wrong number of dependents",1,option.numOptionDependents());
            AssertJUnit.assertEquals("Dependent at index 0 has the wrong entry",name2,option.getOptionDependent(0).getDependentEntry().getName());
    }
    
    @Test()
	public void testRemoveOptionDependent_InvalidIndex(){
            Option option = factory.createOption("option", "option");
            OptionDependent od1 = factory.createOptionDependent();
            String name1 = "te1";
            TextEntry te1 = factory.createTextEntry(name1);
            od1.setDependentEntry(te1);
            od1.setEntryStatus(EntryStatus.DISABLED);
            OptionDependent od2 = factory.createOptionDependent();
            String name2 = "te2";
            TextEntry te2 = factory.createTextEntry(name2);
            od2.setDependentEntry(te2);
            od2.setEntryStatus(EntryStatus.DISABLED);
            OptionDependent od3 = factory.createOptionDependent();
            String name3 = "te3";
            TextEntry te3 = factory.createTextEntry(name3);
            od3.setDependentEntry(te3);
            od3.setEntryStatus(EntryStatus.DISABLED);
            option.addOptionDependent(od1);
            option.addOptionDependent(od2);
            option.addOptionDependent(od3);
            
            try{
                option.removeOptionDependent(-1);
                Assert.fail("Exception should have been thrown when trying to remove an option dependent using an invalid index (-1)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Option has wrong number of dependents",3,option.numOptionDependents());
                AssertJUnit.assertEquals("Dependent an index 0 has the wrong entry",name1,option.getOptionDependent(0).getDependentEntry().getName());
                AssertJUnit.assertEquals("Dependent an index 1 has the wrong entry",name2,option.getOptionDependent(1).getDependentEntry().getName());
                AssertJUnit.assertEquals("Dependent an index 2 has the wrong entry",name3,option.getOptionDependent(2).getDependentEntry().getName());
            }

            try{
                option.removeOptionDependent(3);
                Assert.fail("Exception should have been thrown when trying to remove an option dependent using an invalid index (3)");
            }
            catch(ModelException ex){
                AssertJUnit.assertEquals("Option has wrong number of dependents",3,option.numOptionDependents());
                AssertJUnit.assertEquals("Dependent an index 0 has the wrong entry",name1,option.getOptionDependent(0).getDependentEntry().getName());
                AssertJUnit.assertEquals("Dependent an index 1 has the wrong entry",name2,option.getOptionDependent(1).getDependentEntry().getName());
                AssertJUnit.assertEquals("Dependent an index 2 has the wrong entry",name3,option.getOptionDependent(2).getDependentEntry().getName());
            }
    }
}
