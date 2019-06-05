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

package org.psygrid.data.transformer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.psygrid.data.dao.DAOTest;
import org.psygrid.data.model.IDateValue;
import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.psygrid.data.repository.transformer.InputTransformer;
import org.psygrid.data.repository.transformer.TransformerClient;
import org.psygrid.data.repository.transformer.TransformerException;

public class InputTransformerTest extends DAOTest {

    private RepositoryDAO dao = null;
    private InputTransformer transformer = null;
    private Factory factory = null;
    
    public static final DateFormat fullDateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    protected void setUp() throws Exception {
        super.setUp();
        dao = (RepositoryDAO)ctx.getBean("repositoryDAOService");
        factory = (Factory) ctx.getBean("factory");
        transformer = (InputTransformer) ctx.getBean("myInputTransformer");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        dao = null;
        factory = null;
        transformer = null;
    }
    
    public void testTransform(){
        try{
            //1. Create a dataset that has an entry with a transformer
            String name = "testTransform - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            Transformer t = factory.createTransformer();
            t.setWsUrl("http://localhost:8080/transformers/services/sha1transformer");
            t.setWsNamespace("urn:transformers.psygrid.org");
            t.setWsOperation("encrypt");
            t.setResultClass("org.psygrid.data.model.hibernate.TextValue");
            ds.addTransformer(t);
            Document doc = factory.createDocument("D1");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            Section sec = factory.createSection("Sec");
            SectionOccurrence soc = factory.createSectionOccurrence("Default");
            sec.addOccurrence(soc);
            doc.addSection(sec);
            TextEntry te1 = factory.createTextEntry("TE1");
            te1.addTransformer(t);
            doc.addEntry(te1);
            te1.setSection(sec);
            
            //save the dataset and re-load it
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //create a record from the dataset
            Record rec = ds.generateInstance();
            DocumentInstance di = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            rec.addDocumentInstance(di);
            te1 = (TextEntry)ds.getDocument(0).getEntry(0);
            BasicResponse tr1 = te1.generateInstance(ds.getDocument(0).getSection(0).getOccurrence(0));
            ITextValue tv1 = (ITextValue)te1.generateValue();
            String value = "Foo Bar";
            tv1.setValue(value);
            tr1.setValue(tv1);
            di.addResponse(tr1);
            
            //get the transformer clients
            Map<Long, TransformerClient> clients = dao.getTransformerClients(dsId);
            
            //Run the transformer
            transformer.transform(rec, clients);
            
            //Check that the value has been transformed
            di = rec.getDocumentInstance(ds.getDocument(0).getOccurrence(0));
            tr1 = (BasicResponse)di.getResponse(ds.getDocument(0).getEntry(0), ds.getDocument(0).getSection(0).getOccurrence(0));
            tv1 = (ITextValue)tr1.getValue();
            
            assertTrue("Value is not a text value", tv1 instanceof ITextValue );
            assertTrue("Value does not have transformed flag set to true", tv1.isTransformed());

            List<Provenance> prov = tr1.getProvenance();
            assertEquals("Response contains more than one provenance item", 1, prov.size());
            assertTrue("Provenance items current value is not a text value", prov.get(0).getCurrentValue() instanceof ITextValue );
            assertTrue("Provenance items current value does not have transformed flag set to true", ((ITextValue)prov.get(0).getCurrentValue()).isTransformed());
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testTransform_InvalidWS(){
        try{
            //1. Create a dataset that has entry's with transformers
            String name = "testTransform - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            Transformer t = factory.createTransformer();
            //set up transformer to reference a non-existent operation
            t.setWsUrl("http://localhost:8080/transformers/services/sha1transformer");
            t.setWsNamespace("urn:transformers.psygrid.org");
            t.setWsOperation("invalidoperation");
            t.setResultClass("org.psygrid.data.model.hibernate.TextValue");
            ds.addTransformer(t);
            Document doc = factory.createDocument("D1");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            Section sec = factory.createSection("Sec");
            SectionOccurrence soc = factory.createSectionOccurrence("Default");
            sec.addOccurrence(soc);
            doc.addSection(sec);
            TextEntry te1 = factory.createTextEntry("TE1");
            te1.addTransformer(t);
            doc.addEntry(te1);
            te1.setSection(sec);
            
            //save the dataset and re-load it
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //create a record from the dataset
            Record rec = ds.generateInstance();
            DocumentInstance di = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            rec.addDocumentInstance(di);
            te1 = (TextEntry)ds.getDocument(0).getEntry(0);
            BasicResponse tr1 = te1.generateInstance(ds.getDocument(0).getSection(0).getOccurrence(0));
            ITextValue tv1 = (ITextValue)te1.generateValue();
            String value = "Foo Bar";
            tv1.setValue(value);
            tr1.setValue(tv1);
            di.addResponse(tr1);
            
            //get the transformer clients
            Map<Long, TransformerClient> clients = dao.getTransformerClients(dsId);
            
            //Run the transformer
            try{
                transformer.transform(rec, clients);
                fail("Exception should have been thrown when trying to transform using an invalid web-service");
            }
            catch(TransformerException ex){
                //do nothing
            }
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
    public void testTransform_Date(){
        try{
            //1. Create a dataset that has an entry with a transformer
            String name = "testTransform_Date - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            ds.setProjectCode(guid.toString());
            Transformer t = factory.createTransformer();
            t.setWsUrl("http://localhost:8080/transformers/services/datetransformer");
            t.setWsNamespace("urn:transformers.psygrid.org");
            t.setWsOperation("getMonthAndYear");
            t.setResultClass("org.psygrid.data.model.hibernate.DateValue");
            ds.addTransformer(t);
            Document doc = factory.createDocument("D1");
            DocumentOccurrence occ = factory.createDocumentOccurrence("Occ1");
            doc.addOccurrence(occ);
            ds.addDocument(doc);
            Section sec = factory.createSection("Sec");
            SectionOccurrence soc = factory.createSectionOccurrence("Default");
            sec.addOccurrence(soc);
            doc.addSection(sec);
            DateEntry de1 = factory.createDateEntry("DE1");
            de1.addTransformer(t);
            doc.addEntry(de1);
            de1.setSection(sec);
            
            //save the dataset and re-load it
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            //create a record from the dataset
            Record rec = ds.generateInstance();
            DocumentInstance di = ds.getDocument(0).generateInstance(ds.getDocument(0).getOccurrence(0));
            rec.addDocumentInstance(di);
            de1 = (DateEntry)ds.getDocument(0).getEntry(0);
            BasicResponse tr1 = de1.generateInstance(ds.getDocument(0).getSection(0).getOccurrence(0));
            IDateValue dv1 = de1.generateValue();
            Calendar cal = Calendar.getInstance();
            cal.set(1977,6,20); //20th July 1977 (month is zero-based :-)
            dv1.setValue(cal.getTime());
            tr1.setValue(dv1);
            di.addResponse(tr1);
            
            //get the transformer clients
            Map<Long, TransformerClient> clients = dao.getTransformerClients(dsId);
            
            //Run the transformer
            transformer.transform(rec, clients);
            
            //Check that the value has been transformed
            di = rec.getDocumentInstance(ds.getDocument(0).getOccurrence(0));
            tr1 = (BasicResponse)di.getResponse(ds.getDocument(0).getEntry(0), ds.getDocument(0).getSection(0).getOccurrence(0));
            dv1 = (IDateValue)tr1.getValue();
            
            assertTrue("Value is not a date value", dv1 instanceof IDateValue );
            assertTrue("Value does not have transformed flag set to true", dv1.isTransformed());
            assertNull("Date component of date value is not null", dv1.getValue());
            assertEquals("Month component of date value is not correct", new Integer(7), dv1.getMonth());
            assertEquals("Year component of date value is not correct", new Integer(1977), dv1.getYear());
            
            List<Provenance> prov = tr1.getProvenance();
            assertEquals("Response contains more than one provenance item", 1, prov.size());
            assertTrue("Provenance items current value is not a date value", prov.get(0).getCurrentValue() instanceof IDateValue );
            assertTrue("Provenance items current value does not have transformed flag set to true", ((IDateValue)prov.get(0).getCurrentValue()).isTransformed());
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
}
