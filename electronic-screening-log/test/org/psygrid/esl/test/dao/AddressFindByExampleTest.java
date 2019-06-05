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

package org.psygrid.esl.test.dao;

import org.psygrid.esl.dao.EslDAO;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.IAddress;
import org.psygrid.esl.test.TestHelper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

import junit.framework.TestCase;

/**
 * Ensure that the searchByExample works correctly for Subjects, including their
 * addresses.
 * 
* @author Lucy Bridges
*
*/
public class AddressFindByExampleTest extends TestCase {

	protected EslDAO dao;
	protected org.psygrid.esl.model.IFactory factory;
	protected ApplicationContext ctx = null;
	protected TestHelper helper;
	
	public AddressFindByExampleTest() {
		String[] paths = {"applicationContext.xml"};
		try {
			ctx = new ClassPathXmlApplicationContext(paths);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

	}

	protected void setUp() throws Exception {
		super.setUp();
		dao = (EslDAO)ctx.getBean("eslClientDAOService");
		factory = (IFactory) ctx.getBean("factory");
		helper = new TestHelper();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		dao = null;
		factory = null;
		helper = null;
	}       

	/**
	 * Test method for {@link org.psygrid.esl.dao.hibernate...}.
	 */
	public final void testFindAddress() {
		
		IProject project = setupProject();

		ISubject exampleSubject = factory.createSubject(helper.STUDY_NUMBER);
		exampleSubject.setStudyNumber(helper.STUDY_NUMBER);
		exampleSubject.setCentreNumber(helper.CENTRE_NUMBER);
		IAddress address = factory.createAddress();
		address.setAddress1(helper.ADDRESS_LINE_1);
		
		exampleSubject.setAddress(address);
		
		
		
		org.psygrid.esl.model.dto.Subject[] subject = null;
		try {
			String[] groups = new String[2];
			groups[0] = project.getGroups().get(0).getGroupCode();
			groups[1] = project.getGroups().get(0).getGroupCode();
			
			subject = dao.findSubjectByExample(project.toDTO(), exampleSubject.toDTO(), groups);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		assertEquals("Wrong number of subjects found", 1, subject.length);
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.dao.hibernate...}.
	 */
	public final void testFindAddress_toUpperCase() {
		
		IProject project = setupProject();

		ISubject exampleSubject = factory.createSubject(helper.STUDY_NUMBER);
		exampleSubject.setStudyNumber(helper.STUDY_NUMBER.toUpperCase());
		exampleSubject.setCentreNumber(helper.CENTRE_NUMBER);
		IAddress address = factory.createAddress();
		address.setAddress1(helper.ADDRESS_LINE_1);
		
		exampleSubject.setAddress(address);
		
		
		org.psygrid.esl.model.dto.Subject[] subject = null;
		try {
			String[] groups = new String[2];
			groups[0] = project.getGroups().get(0).getGroupCode(); 
			groups[1] = project.getGroups().get(1).getGroupCode();
			
			subject = dao.findSubjectByExample(project.toDTO(), exampleSubject.toDTO(), groups);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		assertEquals("Wrong number of subjects found", 1, subject.length);
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.dao.hibernate...}.
	 */
	public final void testFindAddress_fail() {
		
		IProject project = setupProject();

		ISubject exampleSubject = factory.createSubject(helper.STUDY_NUMBER);
		exampleSubject.setStudyNumber(helper.STUDY_NUMBER);
		IAddress address = factory.createAddress();
		address.setAddress1(helper.ADDRESS_LINE_1+"adgasdad");
		
		exampleSubject.setAddress(address);
		
		
		org.psygrid.esl.model.dto.Subject[] subject = null;
		try {
			String[] groups = new String[2];
			groups[0] = project.getGroups().get(0).getGroupCode();
			groups[1] = project.getGroups().get(0).getGroupCode();
			subject = dao.findSubjectByExample(project.toDTO(), exampleSubject.toDTO(), groups);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		assertEquals("Wrong number of subjects found", 0, subject.length);
	}
	
	private IProject setupProject() {

		Long projId = null;
		IProject p  = null;

		IProject project = helper.populateProject("AddressFindByExampleTest", factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj, null);
			p = dao.getProject(projId).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("projId is null", projId);
		assertNotNull("p id is null", p.getId());

		return p;
	}
	
}
