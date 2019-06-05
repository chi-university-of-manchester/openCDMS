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


package org.psygrid.esl.test.services;

import junit.framework.TestCase;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.psygrid.esl.services.client.EslClient;
import org.psygrid.esl.model.*;
import org.psygrid.esl.dao.*;
import org.psygrid.esl.test.TestHelper;

/**
 * @author Lucy Bridges
 *
 */
public class EslSoapImplUpdatesTest extends TestCase {
	
	
	protected EslDAO dao;
	protected org.psygrid.esl.model.IFactory factory;
	protected ApplicationContext ctx = null;

	private static final Long projectId = new Long(2); 
	
	public EslSoapImplUpdatesTest() {
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
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		dao = null;
		factory = null;
	}       

	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testSaveSubject_update() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-saveSubject_update";

		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		
		//As we aren't testing randomisation/treatment allocation in this class
		//remove randomisation (it hasn't been saved to remote randomiser so will
		//generate an error otherwise).
		dtoProj.setRandomisation(null);
		try {
			projId = dao.saveProject(dtoProj, "test");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't saved properly", projId);

		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;
		Long wsSubjectId   = null;

		try {
			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}


		IGroup wsGroup = wsProject.getGroups().get(0);

		assertNotNull("Project not retrieved", wsProject);
		assertNotNull("Groups not found", wsGroup);
		
		String studyNum = "fred2 "+helper.getUnique();
		ISubject wsSubject = factory.createSubject(studyNum);
		wsSubject.setLastName("Test 1 - SaveSubject");
		wsSubject.setSex("male");
		wsSubject.setFirstName("fred");
		wsSubject.setCentreNumber("123");
		wsSubject.setStudyNumber(studyNum);
		
		wsSubject.setGroup(wsGroup);

		//use web-service to save a new subject
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Subject has no id", wsSubjectId);

		try {
			wsSubject = client.retrieveSubject(wsSubjectId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("New subject wasn't retrieved", wsSubject);
		assertNotNull("New subject has no name", wsSubject.getLastName());
		
		wsSubject.setFirstName("new");
		wsSubject.setLastName("Test 1 - SaveSubject - updated");
		
		//attempt to update the subject.
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Subject has no id", wsSubjectId);
	}
	
	/**
	 * Ensures that ProvenanceLog stores a list of all changes made to an object
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testSaveSubject_update_two() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-saveSubject_update_two";

		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		
		//As we aren't testing randomisation/treatment allocation in this class
		//remove randomisation (it hasn't been saved to remote randomiser so will
		//generate an error otherwise).
		dtoProj.setRandomisation(null);
		try {
			projId = dao.saveProject(dtoProj, "test");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't saved properly", projId);

		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;
		Long wsSubjectId   = null;

		try {
			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}


		IGroup wsGroup = wsProject.getGroups().get(0);

		assertNotNull("Project not retrieved", wsProject);
		assertNotNull("Groups not found", wsGroup);
		
		String studyNum = "fred2 "+helper.getUnique();
		ISubject wsSubject = factory.createSubject(studyNum);
		wsSubject.setLastName("Test 1 - SaveSubject");
		wsSubject.setSex("male");
		wsSubject.setFirstName("fred");
		wsSubject.setCentreNumber("123");
		wsSubject.setStudyNumber(studyNum);
		
		wsSubject.setGroup(wsGroup);

		//use web-service to save a new subject
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Subject has no id", wsSubjectId);

		try {
			wsSubject = client.retrieveSubject(wsSubjectId, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("New subject wasn't retrieved", wsSubject);
		assertNotNull("New subject has no name", wsSubject.getLastName());
		
		wsSubject.setFirstName("new");
		wsSubject.setLastName("Test 1 - SaveSubject - updated");
		
		//attempt to update the subject.
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Subject has no id", wsSubjectId);
		
		//retrieve and update a second time
		try {
			wsSubject = client.retrieveSubject(wsSubjectId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("New subject wasn't retrieved", wsSubject);
		assertNotNull("New subject has no name", wsSubject.getLastName());
		
		wsSubject.setFirstName("new-two");
		wsSubject.setLastName("Test 1 - SaveSubject - updated-two");
		
		//attempt to update the subject.
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Subject has no id", wsSubjectId);
	}
	
	
	
	/**
	 * Ensures that ProvenanceLog stores a list of all changes made to an object
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testSaveSubject_update_dob() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-saveSubject_update_dob";

		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		
		//As we aren't testing randomisation/treatment allocation in this class
		//remove randomisation (it hasn't been saved to remote randomiser so will
		//generate an error otherwise).
		dtoProj.setRandomisation(null);
		try {
			projId = dao.saveProject(dtoProj, "test");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't saved properly", projId);

		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;
		Long wsSubjectId   = null;

		try {
			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}


		IGroup wsGroup = wsProject.getGroups().get(0);

		assertNotNull("Project not retrieved", wsProject);
		assertNotNull("Groups not found", wsGroup);
		
		String studyNum = "fred2 "+helper.getUnique();
		ISubject wsSubject = factory.createSubject(studyNum);
		wsSubject.setLastName("Test 1 - SaveSubject");
		wsSubject.setSex("male");
		wsSubject.setFirstName("fred");
		wsSubject.setCentreNumber("123");
		wsSubject.setStudyNumber(studyNum);
		wsSubject.setDateOfBirth(new Date());
		
		wsSubject.setGroup(wsGroup);

		//use web-service to save a new subject
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Subject has no id", wsSubjectId);

		try {
			wsSubject = client.retrieveSubject(wsSubjectId, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("New subject wasn't retrieved", wsSubject);
		assertNotNull("New subject has no name", wsSubject.getLastName());
		
		wsSubject.setFirstName("new");
		wsSubject.setLastName("Test 1 - SaveSubject - updated");
		
		//attempt to update the subject.
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Subject has no id", wsSubjectId);
		
		//retrieve and update the date of birth
		try {
			wsSubject = client.retrieveSubject(wsSubjectId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("New subject wasn't retrieved", wsSubject);
		
		wsSubject.setFirstName(wsSubject.getFirstName()+"-dob updated");
		//Date dob = new Date("12-12-12");
		Calendar dob = new GregorianCalendar();
		dob.clear();
		dob.set(1982, Calendar.AUGUST, 1);
		wsSubject.setDateOfBirth(dob.getTime());
		
//		attempt to update the subject.
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Subject has no id", wsSubjectId);
		
		//retrieve and update the date of birth
		try {
			wsSubject = client.retrieveSubject(wsSubjectId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
		assertNotNull("Updated subject wasn't retrieved", wsSubject);
		//assertEquals("Subject's date of birth does not match", dob, wsSubject.getDateOfBirth());
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveProject(org.psygrid.esl.model.dto.Project)}.
	 */
	public void testSaveProject_update() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-saveProject_update";

		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		
		//As we aren't testing randomisation/treatment allocation in this class
		//remove randomisation (it hasn't been saved to remote randomiser so will
		//generate an error otherwise).
		dtoProj.setRandomisation(null);
		try {
			projId = dao.saveProject(dtoProj, "test");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't saved properly", projId);

		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;
		Long wsProjectId   = null;

		try {
			wsProject = client.retrieveProject(projId, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Project not retrieved", wsProject);
		
		wsProject.setProjectCode(wsProject.getProjectCode()+"-Edited");
		IGroup g = factory.createGroup("new group");
		g.setGroupCode(helper.getUnique());
		wsProject.setGroup(g);
		//save changes to the project
		try {
			wsProjectId = client.saveProject(wsProject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
		assertNotNull("Project not saved", wsProjectId);
	}
	
	
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveGroup(org.psygrid.esl.model.dto.Group)}.
	 */
	public void testSaveGroup_update() {
		
		Long projId = null;
		String projectName = "EslSoapBindingImpl-saveSubject_update";

		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		
		//As we aren't testing randomisation/treatment allocation in this class
		//remove randomisation (it hasn't been saved to remote randomiser so will
		//generate an error otherwise).
		dtoProj.setRandomisation(null);
		try {
			projId = dao.saveProject(dtoProj, "test");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't saved properly", projId);

		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;
		Long wsGroupId     = null;

		try {
			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}


		IGroup wsGroup = wsProject.getGroups().get(0);

		assertNotNull("Project not retrieved", wsProject);
		assertNotNull("Groups not found", wsGroup);
		
		wsGroup.setGroupName(wsGroup.getGroupName()+"-edited");
		wsProject.setGroup(wsGroup);
		
		try {
			wsGroupId = client.saveProject(wsProject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Group not saved correctly", wsGroupId);
	}

	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testSaveAddress_update() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-saveAddress_update";

		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		
		//As we aren't testing randomisation/treatment allocation in this class
		//remove randomisation (it hasn't been saved to remote randomiser so will
		//generate an error otherwise).
		dtoProj.setRandomisation(null);
		try {
			projId = dao.saveProject(dtoProj, "test");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't saved properly", projId);

		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;
		ISubject wsSubject = null;
		Long wsSubjectId   = null;

		try {
			wsProject = client.retrieveProject(projId, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
		assertNotNull("Project wasn't retrieved", wsProject);

		try {
			wsSubject = client.retrieveSubjectByStudyNumber(wsProject, helper.STUDY_NUMBER, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("New subject wasn't retrieved", wsSubject);
		assertNotNull("New subject has no address", wsSubject.getAddress());
		
		IAddress address = wsSubject.getAddress();
		address.setAddress1(address.getAddress1()+"-edited");
		wsSubject.setAddress(address);
		
		//attempt to update the subject and therefore address.
		try {
			wsSubjectId = client.saveSubject(wsSubject,"test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Subject has no id", wsSubjectId);
	}
	
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testSaveAddress_update_two() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-saveAddress_update_two";

		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		
		//As we aren't testing randomisation/treatment allocation in this class
		//remove randomisation (it hasn't been saved to remote randomiser so will
		//generate an error otherwise).
		dtoProj.setRandomisation(null);
		try {
			projId = dao.saveProject(dtoProj, "test");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't saved properly", projId);

		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;
		ISubject wsSubject = null;
		Long wsSubjectId   = null;

		try {
			wsProject = client.retrieveProject(projId, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
		assertNotNull("Project wasn't retrieved", wsProject);

		try {
			wsSubject = client.retrieveSubjectByStudyNumber(wsProject, helper.STUDY_NUMBER, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("New subject wasn't retrieved", wsSubject);
		assertNotNull("New subject has no address", wsSubject.getAddress());
		
		IAddress address = wsSubject.getAddress();
		address.setAddress1(address.getAddress1()+"-edited");
		wsSubject.setAddress(address);
		
		//attempt to update the subject and therefore address.
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Subject has no id", wsSubjectId);
		
		ISubject newSubject = null;
		//retrieve subject again and attempt to update details a second time
		try {
			newSubject = client.retrieveSubjectByStudyNumber(wsProject, wsSubject.getStudyNumber(), null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Updated subject wasn't retrieved", newSubject);
		assertNotNull("Updated subject has no address", newSubject.getAddress());
		
		IAddress newAddress = newSubject.getAddress();
		newAddress.setAddress1(newAddress.getAddress1()+"-edited2");
		newSubject.setAddress(newAddress);
		newSubject.setHospitalNumber("321");
		
		Long newSubjectId = null;
		//attempt to update the subject and therefore address.
		try {
			newSubjectId = client.saveSubject(newSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Updated Subject has no id", newSubjectId);
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveProject_update_ED2(org.psygrid.esl.model.dto.Project)}.
	 */
	public void testSaveProject_update_ED2() {

		Long projId = projectId;

		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;
		Long wsProjectId   = null;

		try {
			wsProject = client.retrieveProject(projId, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Project not retrieved", wsProject);

		wsProject.setProjectCode(wsProject.getProjectCode()+"-Edited");
		IGroup g = factory.createGroup("new group");
		g.setGroupCode(TestHelper.getUnique());
		wsProject.setGroup(g);
		//save changes to the project
		try {
			wsProjectId = client.saveProject(wsProject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Project not saved", wsProjectId);
	}

	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveGroup_update_ED2(org.psygrid.esl.model.dto.Group)}.
	 */
	public void testSaveGroup_update_ED2() {

		Long projId = projectId;

		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;
		Long wsGroupId     = null;

		try {
			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}


		IGroup wsGroup = wsProject.getGroups().get(0);

		assertNotNull("Project not retrieved", wsProject);
		assertNotNull("Groups not found", wsGroup);

		wsGroup.setGroupName(wsGroup.getGroupName()+"-edited");
		wsProject.setGroup(wsGroup);

		try {
			wsGroupId = client.saveProject(wsProject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Group not saved correctly", wsGroupId);
	}


	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveSubject_update_ED2(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testSaveSubject_update_ED2() {

		Long projId = projectId;
		
		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;
		Long wsSubjectId   = null;

		try {
			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}


		IGroup wsGroup = wsProject.getGroups().get(0);

		assertNotNull("Project not retrieved", wsProject);
		assertNotNull("Groups not found", wsGroup);

		String studyNum = "fred2 "+TestHelper.getUnique();
		ISubject wsSubject = factory.createSubject(studyNum);
		wsSubject.setLastName("Test 1 - SaveSubject");
		wsSubject.setSex("male");
		wsSubject.setFirstName("fred");
		wsSubject.setCentreNumber("123");
		wsSubject.setStudyNumber(studyNum);

		wsSubject.setGroup(wsGroup);

		//use web-service to save a new subject
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Subject has no id", wsSubjectId);

		try {
			wsSubject = client.retrieveSubject(wsSubjectId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("New subject wasn't retrieved", wsSubject);
		assertNotNull("New subject has no name", wsSubject.getLastName());

		wsSubject.setFirstName("new");
		wsSubject.setLastName("Test 1 - SaveSubject - updated");

		//attempt to update the subject.
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Subject has no id", wsSubjectId);
	}

	/**
	 * Ensures that ProvenanceLog stores a list of all changes made to an object
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveSubject_update_dob_ED2(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testSaveSubject_update_dob_ED2() {

		Long projId = projectId;

		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;
		Long wsSubjectId   = null;

		try {
			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}


		IGroup wsGroup = wsProject.getGroups().get(0);

		assertNotNull("Project not retrieved", wsProject);
		assertNotNull("Groups not found", wsGroup);

		String studyNum = "fred2 "+TestHelper.getUnique();
		ISubject wsSubject = factory.createSubject(studyNum);
		wsSubject.setLastName("Test 1 - SaveSubject");
		wsSubject.setSex("male");
		wsSubject.setFirstName("fred");
		wsSubject.setCentreNumber("123");
		wsSubject.setStudyNumber(studyNum);
		wsSubject.setDateOfBirth(new Date());

		wsSubject.setGroup(wsGroup);

		//use web-service to save a new subject
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Subject has no id", wsSubjectId);

		try {
			wsSubject = client.retrieveSubject(wsSubjectId, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("New subject wasn't retrieved", wsSubject);
		assertNotNull("New subject has no name", wsSubject.getLastName());

		wsSubject.setFirstName("new");
		wsSubject.setLastName("Test 1 - SaveSubject - updated");

		//attempt to update the subject.
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Subject has no id", wsSubjectId);

		//retrieve and update the date of birth
		try {
			wsSubject = client.retrieveSubject(wsSubjectId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("New subject wasn't retrieved", wsSubject);

		wsSubject.setFirstName(wsSubject.getFirstName()+"-dob updated");
		//Date dob = new Date("12-12-12");
		Calendar dob = new GregorianCalendar();
		dob.clear();
		dob.set(1982, Calendar.AUGUST, 1);
		wsSubject.setDateOfBirth(dob.getTime());

//		attempt to update the subject.
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Subject has no id", wsSubjectId);

		//retrieve and update the date of birth
		try {
			wsSubject = client.retrieveSubject(wsSubjectId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Updated subject wasn't retrieved", wsSubject);
		//assertEquals("Subject's date of birth does not match", dob, wsSubject.getDateOfBirth());
	}

	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveSubject_update_ED2(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testSaveAddress_update_ED2() {

		Long projId = projectId;

		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;
		Long wsSubjectId   = null;

		try {
			wsProject = client.retrieveProject(projId, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Project wasn't retrieved", wsProject);

		String studyNum = "fred3 "+TestHelper.getUnique();
		ISubject wsSubject = factory.createSubject(studyNum);
		wsSubject.setLastName("Test 3 - SaveSubject");
		wsSubject.setSex("male");
		wsSubject.setFirstName("fred3");
		wsSubject.setCentreNumber("123");
		wsSubject.setStudyNumber(studyNum);
		wsSubject.setDateOfBirth(new Date());

		wsSubject.setGroup(wsProject.getGroups().get(0));

		IAddress a1 = factory.createAddress();
		a1.setAddress1("ADDRESS_LINE_1");
		a1.setAddress2("ADDRESS_LINE_2");
		a1.setAddress3("ADDRESS_LINE_3");
		a1.setCity("ADDRESS_CITY");
		a1.setPostCode("ABC 123");
		a1.setRegion("region");
		a1.setCountry("Britain");
		a1.setHomePhone("123123");

		wsSubject.setAddress(a1);


		//use web-service to save a new subject
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Subject has no id", wsSubjectId);

		try {
			wsSubject = client.retrieveSubject(wsSubjectId, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}



		assertNotNull("New subject wasn't retrieved", wsSubject);
		assertNotNull("New subject has no address", wsSubject.getAddress());

		IAddress address = wsSubject.getAddress();
		address.setAddress1(address.getAddress1()+"-edited");
		wsSubject.setAddress(address);

		//attempt to update the subject and therefore address.
		try {
			wsSubjectId = client.saveSubject(wsSubject,"test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Subject has no id", wsSubjectId);
	}


	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveAddress_update_withSubject_ED2(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testSaveAddress_update_withSubject_ED2() {

		Long projId = projectId;
		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;

		Long wsSubjectId   = null;

		try {
			wsProject = client.retrieveProject(projId, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Project wasn't retrieved", wsProject);

		String studyNum = "fred4 "+TestHelper.getUnique();
		ISubject wsSubject = factory.createSubject(studyNum);
		wsSubject.setLastName("Test 4 - SaveSubject");
		wsSubject.setSex("male");
		wsSubject.setFirstName("fred4");
		wsSubject.setCentreNumber("123");
		wsSubject.setStudyNumber(studyNum);
		wsSubject.setDateOfBirth(new Date());

		wsSubject.setGroup(wsProject.getGroups().get(0));

		IAddress a1 = factory.createAddress();
		a1.setAddress1("ADDRESS_LINE_1");
		a1.setAddress2("ADDRESS_LINE_2");
		a1.setAddress3("ADDRESS_LINE_3");
		a1.setCity("ADDRESS_CITY");
		a1.setPostCode("ABC 123");
		a1.setRegion("region");
		a1.setCountry("Britain");
		a1.setHomePhone("123123");

		wsSubject.setAddress(a1);

		//use web-service to save a new subject
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Subject has no id", wsSubjectId);

		try {
			wsSubject = client.retrieveSubject(wsSubjectId, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}


		assertNotNull("New subject wasn't retrieved", wsSubject);
		assertNotNull("New subject has no address", wsSubject.getAddress());

		/*IAddress address = wsSubject.getAddress();
		address.setAddress1(address.getAddress1()+"-edited");
		wsSubject.setAddress(address);

		//attempt to update the subject and therefore address.
		try {
			wsSubjectId = client.saveSubject(wsSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Subject has no id", wsSubjectId);
*/
		
		ISubject newSubject = null;
		//retrieve subject again and attempt to update both subject and address details
		try {
			newSubject = client.retrieveSubjectByStudyNumber(wsProject, wsSubject.getStudyNumber(), null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Updated subject wasn't retrieved", newSubject);
		assertNotNull("Updated subject has no address", newSubject.getAddress());

		IAddress newAddress = newSubject.getAddress();
		newAddress.setAddress1(newAddress.getAddress1()+"-edited2");
		newSubject.setAddress(newAddress);
		newSubject.setHospitalNumber("new-321");

		Long newSubjectId = null;
		//attempt to update the subject and therefore address.
		try {
			newSubjectId = client.saveSubject(newSubject, "test");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Updated Subject has no id", newSubjectId);
	}
}
