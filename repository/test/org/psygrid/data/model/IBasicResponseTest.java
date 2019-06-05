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

import org.psygrid.data.model.hibernate.*;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;

public class IBasicResponseTest extends ModelTest {

    @Test()
	public void testGetValue(){
        try{
            Section sec = factory.createSection("SEC");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            TextEntry entry = factory.createTextEntry("Entry");
            entry.setSection(sec);
            BasicResponse response = entry.generateInstance(so);
            ITextValue value1 = entry.generateValue();
            value1.setValue("Blah");
            response.setValue(value1);
            ITextValue value2 = entry.generateValue();
            value2.setValue("Blah Blah");
            response.setValue(value2);            
            AssertJUnit.assertEquals("Response has not returned the correct value",value2,response.getValue());
            ITextValue value3 = entry.generateValue();
            value3.setValue("Blah Blah Blah");
            response.setValue(value3);            
            AssertJUnit.assertEquals("Response has not returned the correct value",value3,response.getValue());
        }
        catch(Exception ex){
            ex.printStackTrace();
            Assert.fail("Exception: "+ex.toString());
        }
    }
    
    @Test()
	public void testSetValueValue(){
        try{
            Section sec = factory.createSection("SEC");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            TextEntry entry = factory.createTextEntry("Entry");
            entry.setSection(sec);
            BasicResponse response = entry.generateInstance(so);
            ITextValue value1 = entry.generateValue();
            value1.setValue("Blah");
            response.setValue(value1);
            AssertJUnit.assertEquals("Response has not returned the correct value",value1,response.getValue());
            ITextValue value2 = entry.generateValue();
            value2.setValue("Blah Blah");
            response.setValue(value2);            
            AssertJUnit.assertEquals("Response has not returned the correct value",value2,response.getValue());
        }
        catch(Exception ex){
            ex.printStackTrace();
            Assert.fail("Exception: "+ex.toString());
        }
    }
    
    @Test()
	public void testSetValueValueComment(){
        try{
            Section sec = factory.createSection("SEC");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            TextEntry entry = factory.createTextEntry("Entry");
            entry.setSection(sec);
            BasicResponse response = entry.generateInstance(so);
            ITextValue value1 = entry.generateValue();
            value1.setValue("Blah");
            String comment = "Comment";
            response.setValue(value1, comment);
            AssertJUnit.assertEquals("Response has not returned the correct value",value1,response.getValue());
            List<Provenance> provs = response.getProvenance();
            AssertJUnit.assertEquals("Provenance does not have the correct comment",comment,provs.get(0).getComment());
            ITextValue value2 = entry.generateValue();
            value2.setValue("Blah Blah");
            String comment2 = "Comment 2";
            response.setValue(value2, comment2);            
            AssertJUnit.assertEquals("Response has not returned the correct value",value2,response.getValue());
            provs = response.getProvenance();
            AssertJUnit.assertEquals("Provenance does not have the correct comment",comment2,provs.get(1).getComment());
        }
        catch(Exception ex){
            ex.printStackTrace();
            Assert.fail("Exception: "+ex.toString());
        }
    }
    
    @Test()
	public void testGetStringValue(){
        try{
            Section sec = factory.createSection("SEC");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            NumericEntry entry = factory.createNumericEntry("Entry");
            entry.setSection(sec);
            BasicResponse response = entry.generateInstance(so);
            String value1 = "23.45";
            INumericValue nv1 = entry.generateValue();
            nv1.setValue(Double.parseDouble(value1));
            response.setValue(nv1);
            AssertJUnit.assertEquals("Value of response is not correct",value1,response.getStringValue());
        }
        catch(Exception ex){
            ex.printStackTrace();
            Assert.fail("Exception: "+ex.toString());
        }
    }
    
    @Test()
	public void testSetValue_WrongType(){
        try{
            //TODO should really test all combinations of values and
            //responses-to-entrys but it will take a l-o-n-g time
            //to write all of the tests
            Section sec = factory.createSection("SEC");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            NumericEntry ne = factory.createNumericEntry("Numeric Entry");
            ne.setSection(sec);
            TextEntry te = factory.createTextEntry("Text Entry");
            te.setSection(sec);
            BasicResponse nr = ne.generateInstance(so);
            BasicResponse tr = te.generateInstance(so);
            IValue nv = ne.generateValue();
            IValue tv = te.generateValue();
            try{
                nr.setValue(tv);
                Assert.fail("Exception should have been thrown when adding a text value to a response to a numeric entry");
            }
            catch(ModelException ex){
                //do nothing
            }
            try{
                tr.setValue(nv);
                Assert.fail("Exception should have been thrown when adding a numeric value to a response to a text entry");
            }
            catch(ModelException ex){
                //do nothing
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            Assert.fail("Exception: "+ex.toString());
        }
    }
    
    @Test()
	public void testGetLatestValueComment(){
        try{
            Section sec = factory.createSection("SEC");
            SectionOccurrence so = factory.createSectionOccurrence("SO");
            sec.addOccurrence(so);
            TextEntry entry = factory.createTextEntry("Entry");
            entry.setSection(sec);
            BasicResponse response = entry.generateInstance(so);
            ITextValue value1 = entry.generateValue();
            value1.setValue("Blah");
            String comment = "Comment 1";
            response.setValue(value1, comment);
            
            AssertJUnit.assertEquals("Latest value comment is not correct", comment, response.getLatestValueComment());
            
            ITextValue value2 = entry.generateValue();
            value2.setValue("Foo");
            String comment2 = "Comment 2";
            response.setValue(value2, comment2);
            
            AssertJUnit.assertEquals("Latest value comment is not correct", comment2, response.getLatestValueComment());
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            Assert.fail("Exception: "+ex.toString());
        }
    }
    
}

