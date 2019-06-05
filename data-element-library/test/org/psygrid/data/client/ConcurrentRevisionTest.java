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
package org.psygrid.data.client;

import junit.framework.TestCase;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

import org.psygrid.data.dao.DataElementDAO;
import org.psygrid.data.model.IAdminInfo;
import org.psygrid.data.model.hibernate.DataElementAction;
import org.psygrid.data.model.hibernate.DataElementContainer;
import org.psygrid.data.model.hibernate.DateEntry;
import org.psygrid.data.model.hibernate.DateValidationRule;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.HibernateDataElementFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConcurrentRevisionTest extends TestCase {
	
	private DataElementDAO dao;
    protected ApplicationContext ctx = null;

	public ConcurrentRevisionTest() {
        String[] paths = {"applicationContext.xml"};
        ctx = new ClassPathXmlApplicationContext(paths);
	}
	
    protected void setUp() throws Exception {
        super.setUp();
        dao = (DataElementDAO)ctx.getBean("repositoryDAOService");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        dao = null;
     }


    public void testStuff() throws Throwable{

    	String lsid = (String)System.getProperty("lsid.to.revise");
    	DataElementClient client = new DataElementClient();
        client.getLSIDAuthorities(null);
        
        if(lsid == null)
        	lsid = this.createAndSubmitDocument();
        
        DataElementContainer elem1 = (DataElementContainer)client.getCompleteElement(lsid, null, true);
        DataElementContainer elem2 = (DataElementContainer)client.getCompleteElement(lsid, null, true);
        
        this.modifyValidationRuleElement(elem1);
        this.modifyValidationRuleElement(elem2);
        
        TestConcurrentRevision test1 = new TestConcurrentRevision(elem1);
        TestConcurrentRevision test2 = new TestConcurrentRevision(elem2);
        
        TestRunnable[] tests = {test2, test1};
        
        MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(tests);
        
        mttr.runTestRunnables();
    }
    
    /**
     * Creates a document to later be revised.
     * @return the lsid of the document.
     */
    private String createAndSubmitDocument(){
    	
    	 	
    	return null;
    }
    
    /**
     * This method expects a document with a single Date entry that has a 
     * validation rule. It modifies the DateEntry name to 'Sally', and sets the
     * entry to be a revision candidate.
     * @param elemContainer
     */
    private void modifyDateElement(DataElementContainer elemContainer){
    	Document doc = (Document)elemContainer.getElement();
    	DateEntry dE = (DateEntry)doc.getEntry(0);
    	dE.setName("Sally");
    	dE.setIsRevisionCandidate(true);
    }
    
    /**
     * This method expects a document with a single Date entry that has a 
     * validation rule. It modifies the ValidationRule name to 'Harry', and sets the
     * entry to be a revision candidate.
     * @param elemContainer
     */
    private void modifyValidationRuleElement(DataElementContainer elemContainer){
    	Document doc = (Document)elemContainer.getElement();
    	DateValidationRule valRule = (DateValidationRule)((DateEntry)doc.getEntry(0)).getValidationRule(0);
    	valRule.setName("Harry");
    	valRule.setIsRevisionCandidate(true);
    }
    
    
    private class TestConcurrentRevision extends TestRunnable{

    	private DataElementContainer elemContainer;
    	
    	public TestConcurrentRevision(DataElementContainer elemContainer){
    		this.elemContainer = elemContainer;
    	}
    	
    	
		@Override
		public void runTest() throws Throwable {
			DataElementClient client = new DataElementClient();
			String authority = elemContainer.getElementLSIDObject().getAuthorityId();
			
			HibernateDataElementFactory factory = new HibernateDataElementFactory();
			DataElementAction action;
			IAdminInfo info = factory.createAdminInfo(DataElementAction.REVISE, "revised by concurrency test", null, null, true);
			
			String lsid = client.reviseElement(elemContainer, info, authority, null);
			System.out.println(lsid);
		}
    	
    }       

}
