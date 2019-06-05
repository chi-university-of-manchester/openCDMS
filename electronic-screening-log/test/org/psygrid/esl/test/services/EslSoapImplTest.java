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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.psygrid.common.email.Email;
import org.psygrid.esl.dao.EslDAO;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IGroup;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IRandomisation;
import org.psygrid.esl.model.IRole;
import org.psygrid.esl.model.IStrata;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.services.ESLDuplicateObjectFault;
import org.psygrid.esl.services.ESLServiceFault;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.esl.test.TestHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Test EslClient and therefore the web services end points.
 * See EslSoapImplRandTest.java for randomisation specific tests.
 * 
 * @author Lucy Bridges
 *
 */
public class EslSoapImplTest extends TestCase {

	protected EslDAO dao;
	protected org.psygrid.esl.model.IFactory factory;
	protected ApplicationContext ctx = null;

	public EslSoapImplTest() {
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
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#retrieveProject(org.psygrid.esl.model.dto.Project)}.
	 */
	public void testRetrieveProject() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-retrieveProject";

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj, null);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't saved properly", projId);

		//use web-service to get the project
		EslClient client = new EslClient();
		IProject wsProject = null;
		try {
			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertEquals("Project name is wrong", projectName, wsProject.getProjectName());

		IRandomisation r1 = (IRandomisation)wsProject.getRandomisation();
		assertNotNull("randomisation doesn't exist", r1.getId());

		List<IRole> listofroles = r1.getRolesToNotify();
		IRole role1 = listofroles.get(0);
		assertNotNull("role1 doesn't exist", role1);

		List<IGroup> groups = wsProject.getGroups();
		assertNotNull("No groups exist for the project", groups);
	}

	/**
	 * Test method for {@link org.psygrid.esl.dao.EslSoapBindingImpl#getProject(String)}.
	 */
	public void testRetrieveProjectByProjectCode() {

		Long projId = null;

		TestHelper helper = new TestHelper();
		IProject project = helper.populateProject("EslSoapImplTest-RetrieveProjectByProjectCode", factory);

		String projectCode = project.getProjectCode();

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj, null);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		//use web-service to get the project
		EslClient client = new EslClient();
		IProject wsProject = null;
		try {
			wsProject = client.retrieveProjectByCode(projectCode, null);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("projId is null", projId);
		assertEquals("The project codes don't match", wsProject.getProjectCode(), projectCode);
	}

	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#retrieveAllProjects()}.
	 */
	/*public void testRetrieveAllProjects() {

		Long projId1 = null;
		Long projId2 = null;
		String projectName1 = "EslSoapBindingImpl-retrieve1";
		String projectName2 = "EslSoapBindingImpl-retrieve2";

		TestHelper helper = new TestHelper();
		IProject project1 = helper.populateProject(projectName1, factory);
		TestHelper helper2 = new TestHelper();
		IProject project2 = helper2.populateProject(projectName2, factory);

		org.psygrid.esl.model.dto.Project dtoProj1 = project1.toDTO();
		org.psygrid.esl.model.dto.Project dtoProj2 = project2.toDTO();

		try {
			projId1 = dao.saveProject(dtoProj1, null);
			projId2 = dao.saveProject(dtoProj2, null);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project 1 wasn't saved properly", projId1);
		assertNotNull("Project 2 wasn't saved properly", projId2);

		//use web-service to get the project
		EslClient client = new EslClient();
		List<IProject> wsProjects = null;
		String saml = "";
		try {
			wsProjects = client.retrieveAllProjects(saml);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("No projects were found", wsProjects);
		assertTrue("No projects returned",wsProjects.size() > 0);	
		//assertEquals("Project 1 id is wrong", wsProjects.get(0).getId(), projId1);
		//assertEquals("Project 2 id is wrong", wsProjects.get(1).getId(), projId2);
	}*/



	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveProject(org.psygrid.esl.model.dto.Project)}.
	 */
	public void testSaveProject() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-SaveProject";

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		//use web-service to save and retrieve the project
		EslClient client = new EslClient();
		IProject wsProject = null;
		try {
			projId = client.saveProject(project, null);  	//project or dtoProj?
			assertNotNull("Project wasn't saved properly", projId);

			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertEquals("Project name is wrong", projectName, wsProject.getProjectName());

	}

	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveProject(org.psygrid.esl.model.dto.Project)}.
	 */
	public void testSaveProject_duplicate() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-SaveProject_duplicate";

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject(projectName, factory);

		//use web-service to save and retrieve the project
		EslClient client = new EslClient();
		IProject wsProject = null;
		try {
			projId = client.saveProject(project, null);  	
			assertNotNull("Project wasn't saved properly", projId);

			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertEquals("Project name is wrong", projectName, wsProject.getProjectName());
		
		boolean fault = false;
		//attempt to save the same object a second time
		try {
			projId = client.saveProject(project, null);  
			
		}
		catch (ESLServiceFault e) {
			fault = true;
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertTrue("No fault was thrown", fault);
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#retrieveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testRetrieveSubject() {

		fail("This test needs to be fixed as Subjects are not now sent as part of a Group DTO");

		Long projId = null;
		String projectName = "EslSoapBindingImpl-retrieveSubject";

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj, null);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't saved properly", projId);

		//use web-service to get the project
		EslClient client = new EslClient();
		ISubject wsSubject = null;
		IProject wsProject = null;
		long wsSubjectId;
		String subjectName = projectName+"-Subject";

		try {
			wsProject = client.retrieveProject(projId, null);

		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		List<IGroup> groups = wsProject.getGroups();
		List<ISubject> subjects = groups.get(0).getSubjects();
		ISubject subject = subjects.get(0);
		assertNotNull("Existing subject wasn't retrieved", subject);

		try {
			wsSubject = client.retrieveSubject(subject.getId(), null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("New subject wasn't retrieved", wsSubject);
		assertEquals("Subject ids don't match", subject.getId(), wsSubject.getId());
	}


	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#retrieveSubjectByStudy(org.psygrid.esl.model.dto.Project, java.lang.String)}.
	 */
	public void testRetrieveSubjectForStudyNumber() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-retrieveSubjectForStudyNumber";
		TestHelper helper = new TestHelper();

		IProject newproject = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = newproject.toDTO();

		try {
			projId = dao.saveProject(dtoProj, null);

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

		try {
			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project wasn't retrieved properly", wsProject);

		try {
			wsSubject = client.retrieveSubjectByStudyNumber(wsProject, helper.STUDY_NUMBER, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Subject wasn't retrieved", wsSubject);
		assertEquals("Study numbers don't match", helper.STUDY_NUMBER, wsSubject.getStudyNumber());
	}


	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#lookupStudyNumber(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testLookupStudyNumber() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-lookupStudyNumber";

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj, null);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't saved properly", projId);

		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;

		try {
			wsProject = client.retrieveProject(projId, null);

		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}


		assertNotNull("Existing project wasn't retrieved", wsProject);

		ISubject exampleSubject = factory.createSubject(helper.STUDY_NUMBER);
		exampleSubject.setFirstName(helper.SUBJECT_FIRSTNAME);
		exampleSubject.setLastName(helper.SUBJECT_LASTNAME);
		exampleSubject.setSex("female");
		//exampleSubject.setVersionNo(1);
		
		
		String studyNumber = null;

		try {
			studyNumber = client.lookupStudyNumber(wsProject, exampleSubject, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("No study number found", studyNumber);

	}

	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#...(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testGetStrata() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-getStrata";

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj, null);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't saved properly", projId);

		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;

		try {
			wsProject = client.retrieveProject(projId, null);

		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}


		assertNotNull("Existing project wasn't retrieved", wsProject);

		ISubject exampleSubject = factory.createSubject(helper.STUDY_NUMBER);
		exampleSubject.setFirstName(helper.SUBJECT_FIRSTNAME);
		exampleSubject.setLastName(helper.SUBJECT_LASTNAME);
		exampleSubject.setSex("female");
		//exampleSubject.setVersionNo(1);
		
		

		//testing strata retrieval
		IStrata stratum = factory.createStrata("sex");
		stratum.setValue("male");
		stratum.setValue("female");
		IStrata stratum2 = factory.createStrata("lastName");
		stratum2.setValue("smith");
		stratum2.setValue("clark");
		stratum2.setValue(helper.SUBJECT_LASTNAME);
		List<IStrata> strata = new ArrayList<IStrata>();
		strata.add(stratum);
		
		try {
			exampleSubject.getStrataValues(strata);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
	}

	
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testSaveSubject() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-saveSubject";

		TestHelper helper = new TestHelper();
		IProject project = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		
		//As we aren't testing randomisation/treatment allocation in this class
		//remove randomisation (it hasn't been saved to remote randomiser so will
		//generate an error otherwise).
		dtoProj.setRandomisation(null);
		try {
			projId = dao.saveProject(dtoProj, null);
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
			wsSubjectId = client.saveSubject(wsSubject, null);
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
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#saveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testSaveSubject_duplicate() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-saveSubject_duplicate";

		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		
		//As we aren't testing randomisation/treatment allocation in this class
		//remove randomisation (it hasn't been saved to remote randomiser so will
		//generate an error later on otherwise).
		dtoProj.setRandomisation(null);
		try {
			projId = dao.saveProject(dtoProj, null);
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
			wsSubjectId = client.saveSubject(wsSubject, null);
			
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Subject has no id", wsSubjectId);

		//try to save the same subject again - this should 
		// cause a duplicate subject fault.
		Long wsSubjectId2 = null;
		try {

			wsSubjectId2 = client.saveSubject(wsSubject, null);
		}
		catch (ESLDuplicateObjectFault e) {
			//this is correct, so do nothing
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNull("Duplicate subject has been given an id", wsSubjectId2);
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.dao.EslSoapBindingImpl#findSubjectBy(IProject, java.util.Map)}.
	 */
	/*public void testSubjectFindBy_Success() {

		Long projId = null;
		String projectName = "Test-SubjectFindBy_Success";

		TestHelper helper = new TestHelper();
		IProject project = helper.populateProject(projectName, factory);
		String studyNumber = helper.STUDY_NUMBER;

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't saved properly", projId);

		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;
		try {
			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project wasn't retrieved properly", wsProject);

		List<ISubject> subjects = null;

		Map<String,String> mycriteria = new HashMap<String,String>();
		mycriteria.put("lastName", helper.SUBJECT_LASTNAME);
		mycriteria.put("firstName", helper.SUBJECT_FIRSTNAME);
		mycriteria.put("studyNumber", helper.STUDY_NUMBER);

		try {
			subjects = client.findSubjectBy(wsProject, mycriteria, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("No subjects retrieved", subjects);
		assertFalse("No subjects found", subjects.isEmpty());
	}
*/
	/**
	 * Test method for {@link org.psygrid.esl.dao.EslSoapBindingImpl#findSubjectBy(IProject, java.util.Map)}.
	 */
/*	public void testSubjectFindBy_Fail() {

		Long projId = null;
		String projectName = "Test-SubjectFindBy_Fail";

		TestHelper helper = new TestHelper();
		IProject project = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't saved properly", projId);

		//use web-service to get the project
		EslClient client = new EslClient();

		IProject wsProject = null;
		try {
			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project wasn't retrieved properly", wsProject);

		List<ISubject> subjects = null;

		Map<String,String> mycriteria = new HashMap<String,String>();
		mycriteria.put("lastName", "made up name");
		//fails on this line because of quote
		//mycriteria.put("firstName", "doesn't exist");
		//mycriteria.put("studyNumber", helper.STUDY_NUMBER);
		try {
			subjects = client.findSubjectBy(wsProject, mycriteria, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("No subjects retrieved", subjects);
		assertTrue("No subjects found", subjects.isEmpty());
	}
*/
	/**
	 * Test method for {@link org.psygrid.esl.dao.EslSoapBindingImpl#findSubjectByExample(org.psygrid.esl.model.dto.Project project, org.psygrid.esl.model.dto.Subject exampleSubject)}.
	 */
	public void testFindSubjectByExample() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-findSubjectByExample";

		TestHelper helper = new TestHelper();
		IProject project = helper.populateProject(projectName, factory);
		String studyNumber = helper.STUDY_NUMBER;

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		try {
			projId = dao.saveProject(dtoProj, null);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		assertNotNull("Project wasn't saved properly", projId);


		//use web-service to get the project
		EslClient client = new EslClient();
		IProject wsProject = null;
		try {
			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project wasn't retrieved properly", wsProject);

		//Create an example subject to use
		ISubject exampleSubject = factory.createSubject("A Test 1");
		exampleSubject.setFirstName(helper.SUBJECT_FIRSTNAME);
		exampleSubject.setStudyNumber(studyNumber);

		List<ISubject> wsResults = null;
		try {
			wsResults = client.findSubjectByExample(wsProject, exampleSubject, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("No subjects found", wsResults);
		assertFalse("No subjects were found", wsResults.size() == 0);	
	}


	/**
	 * Test method for {@link org.psygrid.esl.dao.EslSoapBindingImpl#findSubjectByExample(org.psygrid.esl.model.dto.Project project, org.psygrid.esl.model.dto.Subject exampleSubject)}.
	 */
	public void testFindSubjectByExample_False() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-findSubjectByExample_False";

		TestHelper helper = new TestHelper();
		IProject project = helper.populateProject(projectName, factory);
		String studyNumber = helper.STUDY_NUMBER;

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		try {
			projId = dao.saveProject(dtoProj, null);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		assertNotNull("Project wasn't saved properly", projId);


		//use web-service to get the project
		EslClient client = new EslClient();
		IProject wsProject = null;
		try {
			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project wasn't retrieved properly", wsProject);

		//Create an example subject to use
		ISubject exampleSubject = factory.createSubject("A Test 2");
		exampleSubject.setFirstName("A made up name");
		exampleSubject.setStudyNumber("doesn't exist");

		List<ISubject> wsResults = null;
		try {
			wsResults = client.findSubjectByExample(wsProject, exampleSubject, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Results were null", wsResults);
		assertEquals("Results were returned", wsResults.size(), 0);	
	}

	/**
	 * Test method for {@link org.psygrid.esl.dao.EslSoapBindingImpl#findSubjectByExample(org.psygrid.esl.model.dto.Project project, org.psygrid.esl.model.dto.Subject exampleSubject)}.
	 */
	public void testFindSubjectByExample_empty() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-findSubjectByExample_Empty";

		TestHelper helper = new TestHelper();
		IProject project = helper.populateProject(projectName, factory);
		project.setRandomisation(null);
		
		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		try {
			projId = dao.saveProject(dtoProj, null);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		assertNotNull("Project wasn't saved properly", projId);


		//use web-service to get the project
		EslClient client = new EslClient();
		IProject wsProject = null;
		try {
			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project wasn't retrieved properly", wsProject);

		
		
		IGroup wsGroup = wsProject.getGroups().get(0);
		assertNotNull("Groups not found", wsGroup);
		Long wsSubjectId = null;
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
			wsSubjectId = client.saveSubject(wsSubject, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Subject has no id", wsSubjectId);

		
		//Create an empty example subject to use to search by
		ISubject exampleSubject = factory.createSubject(null);

		List<ISubject> wsResults = null;
		try {
			wsResults = client.findSubjectByExample(wsProject, exampleSubject, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		//All subjects in the database for this project should be returned
		assertNotNull("Results were null", wsResults);
		assertEquals("Results were returned", 1, wsResults.size());	
	}

	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#retrieveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testRetrieveRandomisation() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-retrieveRandomisation";

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj, null);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't saved properly", projId);

		//use web-service to get the project
		EslClient client   = new EslClient();
		IProject wsProject = null;

		try {
			wsProject = client.retrieveProject(projId, null);

		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		IRandomisation rand = wsProject.getRandomisation();

		Map<String,Email> emails = rand.getEmails();
		assertNotNull("Ack, no emails found", emails);

		assertEquals("Email's subject is wrong", emails.get(helper.EMAIL_NAME).getSubject(), helper.EMAIL_SUBJECT);

		assertEquals("Randomisation has the wrong name", rand.getName(), helper.RANDOMISATION_NAME);

		Map<String,String> treatments = rand.getTreatments();
		assertNotNull("No treatments found", treatments.get(helper.TREATMENT_NAME));
		assertEquals("Treatment is incorrect", treatments.get(helper.TREATMENT_NAME), helper.TREATMENT_VALUE);

		List<IStrata> strata = rand.getStrata();

		assertNotNull("No strata found", strata.get(0));
		assertEquals("Strata name is incorrect", strata.get(0).getName(), helper.STRATA_NAME);

		List<IRole> roles = rand.getRolesToNotify();
		assertNotNull("No roles found", roles);
		assertEquals("Role name is incorrect", roles.get(0).getName(), helper.ROLE_NAME);

	}
	
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#exists(org.psygrid.esl.model.dto.Project, String)}.
	 */
	public void testExists() {
		
		Long projId = null;
		String projectName = "EslSoapBindingImpl-exists";

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj, null);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't saved properly", projId);

		//use web-service to get the project
		EslClient client   = new EslClient();
		IProject wsProject = null;

		try {
			wsProject = client.retrieveProject(projId, null);

		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
		try {
			assertTrue(client.exists(wsProject, helper.STUDY_NUMBER, null));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
		try {
			assertFalse(client.exists(wsProject, "madeupnumber", null));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
	
	}

}
