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
import org.psygrid.esl.model.IGroup;
import org.psygrid.esl.model.IAddress;
import org.psygrid.esl.test.TestHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;


public class EslDAOUpdateTest extends TestCase {

	protected EslDAO dao;
	protected org.psygrid.esl.model.IFactory factory;
	protected ApplicationContext ctx = null;

	public EslDAOUpdateTest() {
		String[] paths = {"applicationCtx.xml"};
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
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		dao = null;
		factory = null;
	}       


	/**
	 * Test method for {@link org.psygrid.esl.dao.EslDAO#saveProject(org.psygrid.esl.model.dto.Project)}.
	 */
	public void testSaveProject_update() {

		Long projId = null;
		IProject p  = null;

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject("EslDAOTest-project_update", factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj, "JUnit Test");
			p = dao.getProject(projId).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("projId is null", projId);
		assertNotNull("p id is null", p.getId());

		String projectCode = p.getProjectCode()+"-edited";
		//p.setProjectCode(projectCode);
		p.setProjectName(projectCode);
		p.setRandomisation(null);
		//Save the updated project
		try {
			projId = dao.saveProject(p.toDTO(), "JUnit Test");
			p = dao.getProject(projId).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("projId is null", projId);
		assertNotNull("p id is null", p.getId());
	}

	/**
	 * Test method for {@link org.psygrid.esl.services.EslDAO#saveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testSaveSubject_update() {

		Long projId = null;
		IProject p  = null;

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject("EslDAOTest-Subject_update", factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj, "JUnit Test");
			p = dao.getProject(projId).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("projId is null", projId);
		assertNotNull("p id is null", p.getId());

		ISubject wsSubject = factory.createSubject("Test subject-"+helper.getUnique());
		Long wsSubjectId = null;
		assertNotNull("No Groups found", p.getGroups().get(0));
		assertNotNull("No ID found for group", p.getGroups().get(0).getId());
		wsSubject.setGroup((org.psygrid.esl.model.hibernate.Group)p.getGroups().get(0));
		wsSubject.setFirstName("fred");
		org.psygrid.esl.model.dto.Subject newSubject = null;
		try {
			wsSubjectId = dao.saveSubject(wsSubject, "JUnit Test");
			newSubject = dao.getSubject(wsSubjectId);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("New subject is null", wsSubject);

		newSubject.setFirstName("fred-updated");
		newSubject.setLastName("new lastname");
		Long newSubjectId = null;
		try {
			newSubjectId = dao.saveSubject(newSubject.toHibernate(), "JUnit Test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertEquals("Id of updated subject does not match original", newSubjectId, wsSubjectId);

	}


	/**
	 * Test that ProvenanceLog can hold a list of individual Changes
	 * 
	 * Test method for {@link org.psygrid.esl.services.EslDAO#saveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testSaveSubject_updateTwice() {

		Long projId = null;
		IProject p  = null;

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject("EslDAOTest-Subject_updateTwice", factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj, "JUnit Test");
			p = dao.getProject(projId).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("projId is null", projId);
		assertNotNull("p id is null", p.getId());

		ISubject wsSubject = factory.createSubject("Test subject-"+helper.getUnique());
		Long wsSubjectId = null;
		assertNotNull("No Groups found", p.getGroups().get(0));
		assertNotNull("No ID found for group", p.getGroups().get(0).getId());
		wsSubject.setGroup((org.psygrid.esl.model.hibernate.Group)p.getGroups().get(0));
		wsSubject.setFirstName("fred");
		org.psygrid.esl.model.dto.Subject newSubject = null;
		try {
			wsSubjectId = dao.saveSubject(wsSubject, "JUnit Test");
			newSubject = dao.getSubject(wsSubjectId);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("New subject is null", wsSubject);

		newSubject.setFirstName("fred-updated");
		newSubject.setLastName("new lastname");
		Long newSubjectId = null;
		org.psygrid.esl.model.dto.Subject newerSubject = null;
		try {
			newSubjectId = dao.saveSubject(newSubject.toHibernate(), "JUnit Test");
			newerSubject = dao.getSubject(newSubjectId);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertEquals("Id of updated subject does not match original", newSubjectId, wsSubjectId);

		newerSubject.setFirstName("fred-updated-twice");
		newerSubject.setLastName("newer lastname");
		Long newerSubjectId = null;
		try {
			newerSubjectId = dao.saveSubject(newerSubject.toHibernate(), "JUnit Test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertEquals("Id of second updated subject does not match original", newerSubjectId, newSubjectId);

	}

	/**
	 * Test method for {@link org.psygrid.esl.dao.EslDAO#saveProject(org.psygrid.esl.model.dto.Project)}.
	 */
	public void testAddNewGroup() {

		Long projId = null;
		IProject p  = null;

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject("EslDAOTest-addNewGroup", factory);

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

		int noOfGroups = p.getGroups().size();

		IGroup group = factory.createGroup("group update test");
		group.setGroupCode(TestHelper.getUnique());

		p.setGroup(group);
		IProject p2 = null;
		//Save the updated project
		try {
			projId = dao.saveProject(p.toDTO(), "JUnit Test");
			p2 = dao.getProject(projId).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		int newNoOfGroups = p2.getGroups().size();
		assertNotNull("projId is null", projId);
		assertNotNull("p id is null", p.getId());

		assertEquals("Incorrect number of groups", noOfGroups+1, newNoOfGroups);
	}

	/**
	 * Test method for {@link org.psygrid.esl.dao.EslDAO#saveProject(org.psygrid.esl.model.dto.Project)}.
	 */
	public void testSaveGroup_update() {

		Long projId = null;
		IProject p  = null;

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject("EslDAOTest-saveGroup_update", factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj, "JUnit Test");
			p = dao.getProject(projId).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("projId is null", projId);
		assertNotNull("p id is null", p.getId());

		int noOfGroups = p.getGroups().size();

		IGroup group = p.getGroups().get(0);
		group.setGroupName(helper.GROUP_NAME1+"-updated");
		p.setGroup(group);

		IProject p2 = null;
		IGroup g = null;
		Long pId = null;
		//Save the updated group
		try {
			pId = dao.saveProject(p.toDTO(), "JUnit Test");
			p2 = dao.getProject(pId).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		int newNoOfGroups = p2.getGroups().size();
		assertNotNull("projId is null", projId);
		assertNotNull("p id is null", p.getId());

		assertEquals("Incorrect number of groups", noOfGroups, newNoOfGroups);
	}

	/**
	 * Test method for {@link org.psygrid.esl.services.EslDAO#saveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testSaveAddress_update() {

		Long projId = null;
		IProject p  = null;

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject("EslDAOTest-Address_update", factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();


		ISubject subject = null;
		try {
			projId = dao.saveProject(dtoProj, "JUnit Test");
			p = dao.getProject(projId).toHibernate();
			subject = dao.getSubject(p.toDTO(), helper.STUDY_NUMBER).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("projId is null", projId);
		assertNotNull("p id is null", p.getId());
		assertNotNull("subject is null", subject);

		IAddress address = subject.getAddress();
		address.setAddress1(address.getAddress1()+"-updated");
		address.setCity(address.getCity()+"-updated");

		subject.setAddress(address);
		Long sId = null;
		try {
			sId = dao.saveSubject(subject, "JUnit Test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Subject Id is null", sId);

	}

	/**
	 * Test method for {@link org.psygrid.esl.services.EslDAO#saveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testSaveSubjectRisks_update() {

		Long projId = null;
		IProject p  = null;

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject("EslDAOTest-Subject_update", factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj, "JUnit Test");
			p = dao.getProject(projId).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("projId is null", projId);
		assertNotNull("p id is null", p.getId());

		ISubject wsSubject = factory.createSubject("Test subject-"+helper.getUnique());
		Long wsSubjectId = null;
		assertNotNull("No Groups found", p.getGroups().get(0));
		assertNotNull("No ID found for group", p.getGroups().get(0).getId());
		wsSubject.setGroup((org.psygrid.esl.model.hibernate.Group)p.getGroups().get(0));
		wsSubject.setFirstName("fred");

		wsSubject.setRiskIssues("This subject has a lot of risk issues. Greater than 255 in fact. FillerFillerFillerFillerFillerFiller1 FillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFiller" 
				+ "FillerFillerFillerFillerFiller2"
				+ "FillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFiller3");

		org.psygrid.esl.model.dto.Subject newSubject = null;
		try {
			wsSubjectId = dao.saveSubject(wsSubject, "JUnit Test");
			newSubject = dao.getSubject(wsSubjectId);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("New subject is null", wsSubject);

		newSubject.setFirstName("fred-updated");
		newSubject.setLastName("new lastname");

		//255
		newSubject.setRiskIssues("The risk issues have been updated. "+
				" Blah Blah BlahBlahBlahBlah BlahBlah Blah Blah BlahBlah" +
				"BlahBlah BlahBlah Blah Blah BlahBlahBlahBlah1 BlahBlah Blah Blah BlahBlahBlahBlah BlahBlah Blah Blah " +
		"BlahBlahBlahBlah BlahBlah Blah Blah2 BlahBlahBlahBlah BlahBlah3");

		//255+17
		newSubject.setRiskIssues("The risk issues have been updated. "+
				" Blah Blah BlahBlahBlahBlah BlahBlah Blah Blah BlahBlah" +
				"BlahBlah BlahBlah Blah Blah BlahBlahBlahBlah1 BlahBlah Blah Blah BlahBlahBlahBlah BlahBlah Blah Blah " +
		"BlahBlahBlahBlah BlahBlah Blah Blah2 BlahBlahBlahBlah BlahBlahBlahBlah BlahBlah3");

		Long newSubjectId = null;
		try {
			newSubjectId = dao.saveSubject(newSubject.toHibernate(), "JUnit Test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertEquals("Id of updated subject does not match original", newSubjectId, wsSubjectId);

	}

	/**
	 * Test method for {@link org.psygrid.esl.services.EslDAO#saveSubjectRisks_updateTwice(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testSaveSubjectRisks_updateTwice() {

		Long projId = null;
		IProject p  = null;

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject("EslDAOTest-saveSubjectRisks_updateTwice", factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj, "JUnit Test");
			p = dao.getProject(projId).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("projId is null", projId);
		assertNotNull("p id is null", p.getId());

		ISubject wsSubject = factory.createSubject("Test subject-"+helper.getUnique());
		Long wsSubjectId = null;
		assertNotNull("No Groups found", p.getGroups().get(0));
		assertNotNull("No ID found for group", p.getGroups().get(0).getId());
		wsSubject.setGroup((org.psygrid.esl.model.hibernate.Group)p.getGroups().get(0));
		wsSubject.setFirstName("fred");

		wsSubject.setRiskIssues("This subject has a lot of risk issues. Greater than 255 in fact. FillerFillerFillerFillerFillerFiller1 FillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFiller" 
				+ "FillerFillerFillerFillerFiller2"
				+ "FillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFiller3");

		org.psygrid.esl.model.dto.Subject newSubject = null;
		try {
			wsSubjectId = dao.saveSubject(wsSubject, "JUnit Test");
			newSubject = dao.getSubject(wsSubjectId);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("New subject is null", wsSubject);

		newSubject.setFirstName("fred-updated");
		newSubject.setLastName("new lastname");


		//255+17
		newSubject.setRiskIssues("The risk issues have been updated. "+
				" Blah Blah BlahBlahBlahBlah BlahBlah Blah Blah BlahBlah" +
				"BlahBlah BlahBlah Blah Blah BlahBlahBlahBlah1 BlahBlah Blah Blah BlahBlahBlahBlah BlahBlah Blah Blah " +
		"BlahBlahBlahBlah BlahBlah Blah Blah2 BlahBlahBlahBlah BlahBlahBlahBlah BlahBlah3");

		Long newSubjectId = null;
		try {
			newSubjectId = dao.saveSubject(newSubject.toHibernate(), "JUnit Test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertEquals("Id of updated subject does not match original", newSubjectId, wsSubjectId);

		try {
			newSubject = dao.getSubject(wsSubjectId);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("New subject is null", wsSubject);

		//255
		newSubject.setRiskIssues("The risk issues have been updated. "+
				" Blah Blah BlahBlahBlahBlah BlahBlah Blah Blah BlahBlah" +
				"BlahBlah BlahBlah Blah Blah BlahBlahBlahBlah1 BlahBlah Blah Blah BlahBlahBlahBlah BlahBlah Blah Blah " +
		"BlahBlahBlahBlah BlahBlah Blah Blah2 BlahBlahBlahBlah BlahBlah3");

		newSubjectId = null;
		try {
			newSubjectId = dao.saveSubject(newSubject.toHibernate(), "JUnit Test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertEquals("Id of updated subject does not match original", newSubjectId, wsSubjectId);

	}
}
