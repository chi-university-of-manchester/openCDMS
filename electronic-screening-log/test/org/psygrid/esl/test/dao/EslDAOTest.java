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

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.psygrid.esl.model.*;
import org.psygrid.esl.test.TestHelper;
import org.psygrid.esl.dao.*;
import org.psygrid.esl.model.hibernate.Project;

/**
 * @author Lucy Bridges
 *
 */
@Test(groups = {"integration-tests"})
public class EslDAOTest {
	private TestHelper helper;

	protected EslDAO dao;
	protected org.psygrid.esl.model.IFactory factory;
	protected ApplicationContext ctx = null;

	public EslDAOTest () {
		String[] paths = {"applicationContext.xml"};
		try {
			ctx = new ClassPathXmlApplicationContext(paths);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Exception: "+ex.toString());
		}

	}

	@BeforeMethod
	protected void setUp() throws Exception {
		dao = (EslDAO)ctx.getBean("eslDAO");
		factory = (IFactory) ctx.getBean("factory");
		helper = new TestHelper();
	}

	@AfterMethod
	protected void tearDown() throws Exception {
		dao = null;
		factory = null;
	}       

	/**
	 * Test method for {@link org.psygrid.esl.dao.EslDAO#saveProject(org.psygrid.esl.model.dto.Project)}.
	 */
	@Test
	public void testSaveProject() {
//		Assert.fail("This test needs to be fixed as Subjects are not now sent as part of a Group DTO");

		Long projId = null;
		IProject p  = null;

		IProject project = helper.populateProject("EslDAOTest", factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj, null);
			p = dao.getProject(projId).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Exception: "+ex.toString());
		}

		AssertJUnit.assertNotNull("projId is null", projId);
		AssertJUnit.assertNotNull("p id is null", p.getId());
		IRandomisation r1 = p.getRandomisation();


		List<IRole> listofroles = r1.getRolesToNotify();
		IRole role1 = listofroles.get(0);
		AssertJUnit.assertNotNull("No roles found", role1);
		AssertJUnit.assertTrue("notifyofdecision is false", role1.isNotifyOfRSDecision());
		AssertJUnit.assertTrue("notifyofinvocation is false", role1.isNotifyOfRSInvocation());
		AssertJUnit.assertTrue("notifyoftreatment is false", role1.isNotifyOfRSTreatment());
		AssertJUnit.assertNotNull("Role has no name", role1.getName());
				
		AssertJUnit.assertNotNull("No email has been saved", r1.getEmails());
		AssertJUnit.assertNotNull("No email has been saved", r1.getEmails().get(helper.EMAIL_NAME));
		AssertJUnit.assertEquals("Email subject is wrong", helper.EMAIL_SUBJECT, r1.getEmails().get(helper.EMAIL_NAME).getSubject());
		
		AssertJUnit.assertNotNull("No treatments found", r1.getTreatments());
		AssertJUnit.assertNotNull("No treatment found by name", r1.getTreatments().get(helper.TREATMENT_NAME));
		AssertJUnit.assertEquals("Values don't match", helper.TREATMENT_VALUE, r1.getTreatments().get(helper.TREATMENT_NAME));
		
		AssertJUnit.assertNotNull("No strata found", r1.getStrata());
		AssertJUnit.assertEquals("Strata name is wrong", helper.STRATA_NAME, r1.getStrata().get(0).getName());
		
		AssertJUnit.assertNotNull("Project has no groups", p.getGroups());
		AssertJUnit.assertEquals("Project has incorrect number of groups", 2, p.getGroups().size());
		AssertJUnit.assertEquals("Group name is incorrect", helper.GROUP_NAME1, p.getGroups().get(0).getGroupName());
		AssertJUnit.assertEquals("Group name is incorrect", helper.GROUP_NAME2, p.getGroups().get(1).getGroupName());
		
/*		AssertJUnit.assertNotNull("Group has no subjects", p.getGroups().get(0).getSubjects());
		ISubject s = p.getGroups().get(0).getSubjects().get(0);
		AssertJUnit.assertNotNull("Subject has no last name", s.getLastName());
		AssertJUnit.assertNotNull("Subject has no first name", s.getFirstName());
		AssertJUnit.assertNotNull("Subject has no centre number", s.getCentreNumber());
		AssertJUnit.assertNotNull("Subject has no date of birth", s.getDateOfBirth());
		AssertJUnit.assertNotNull("Subject has no hospital number", s.getHospitalNumber());
		AssertJUnit.assertNotNull("Subject has no mobile number", s.getMobilePhone());
		AssertJUnit.assertNotNull("Subject has no nhs number", s.getNhsNumber());
		AssertJUnit.assertNotNull("Subject has email address", s.getEmailAddress());
		AssertJUnit.assertNotNull("Subject has no title", s.getTitle());
		AssertJUnit.assertNotNull("Subject has no work phone number", s.getWorkPhone());
		AssertJUnit.assertNotNull("Subject has no risk issues", s.getRiskIssues());
		AssertJUnit.assertNotNull("Subject has no address", s.getAddress());*/
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.dao.EslDAO#saveProject(org.psygrid.esl.model.dto.Project)}.
	 */
	@Test
	public void testSaveProject_duplicate() {
		Long projId = createProjectSaveAndCheck("EslDAOTest");
		IProject p = getProjectFromId(projId);
		IRandomisation r1 = p.getRandomisation();

		AssertJUnit.assertNotNull("project contains groups", p.getGroups());

		List<IRole> listofroles = r1.getRolesToNotify();
		IRole role1 = listofroles.get(0);
		AssertJUnit.assertNotNull("No roles found", role1);
		
		//create a project with the same details to produce a duplicate object error.
		IProject p2 = new Project();
		p2.setProjectCode(p.getProjectCode());
		p2.setProjectName(p.getProjectName());
		
		Long projId2 = null;
		boolean duplicate = false;
		try {
			projId2 = dao.saveProject(p2.toDTO(), null);
			dao.getProject(projId2).toHibernate();
		}
		catch (DuplicateObjectException doe) {
			duplicate = true;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Exception: "+ex.toString());
		}
		
		AssertJUnit.assertTrue("Not duplicate object exception thrown when saving a duplicate project", duplicate);
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.dao.EslDAO#getProject(String)}.
	 */
	@Test
	public void testGetProjectByProjectCode() {

		Long projId = null;
		org.psygrid.esl.model.dto.Project p  = null;

		IProject project = helper.populateProject("EslDAOTest", factory);
		
		String projectCode = project.getProjectCode();
		
		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = dao.saveProject(dtoProj, null);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Exception: "+ex.toString());
		}

		try {
			p = dao.getProject(projectCode);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Exception: "+ex.toString());
		}
		
		AssertJUnit.assertNotNull("projId is null", projId);
		AssertJUnit.assertNotNull("p id is null", p.getId());

		AssertJUnit.assertEquals("The project codes don't match", p.getProjectCode(), projectCode);

	}
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslDAO#saveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	@Test
	public void testSaveSubject() {
		Long projId = createProjectSaveAndCheck("EslDAOTest");
		IProject p = getProjectFromId(projId);

		ISubject wsSubject = createSubjectAndCheck(p);
		
		saveSubjectAndCheck(wsSubject);		
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslDAO#saveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	@Test
	public void testSaveSubject_duplicate() {
		Long projId = createProjectSaveAndCheck("EslDAOTest-saveSubject_duplicate");
		IProject p = getProjectFromId(projId);
		
		ISubject wsSubject = createSubjectAndCheck(p);
		
		saveSubjectAndCheck(wsSubject);		

		Long wsSubjectId2 = null;
		
		//attempt to save the same object a second time, which
		//should produce an error.
		try {
			wsSubjectId2 = dao.saveSubject(wsSubject, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		}
		
		AssertJUnit.assertNotNull("Duplicate subject's Id is null", wsSubjectId2);
	}
	
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslDAO#saveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	@Test
	public void testSaveSubject_fail() {
		Long projId = createProjectSaveAndCheck("EslDAOTest-saveSubject_fail");
		IProject p = getProjectFromId(projId);

		ISubject wsSubject = factory.createSubject("Test subject-"+TestHelper.getUnique());
		Long wsSubjectId = null;
		AssertJUnit.assertNotNull("No Groups found", p.getGroups().get(0));
		AssertJUnit.assertNotNull("No ID found for group", p.getGroups().get(0).getId());
		wsSubject.setFirstName("fred");
		
		//Not setting the group should produce an error
		//wsSubject.setGroup((org.psygrid.esl.model.hibernate.Group)p.getGroups().get(0));
		boolean error = false;
		try {
			wsSubjectId = dao.saveSubject(wsSubject, null);
		}
		catch (DAOException e) {
			//this should happen, so do nothing
			error = true;
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		}
		AssertJUnit.assertTrue("No DAOException was thrown", error);
		AssertJUnit.assertNull("New subject's Id is not null", wsSubjectId);
	}
	
	@Test
	public void testRetrieveSubject() {
		Long projId = createProjectSaveAndCheck("EslDAOTest-retrieveSubject");
		IProject p = getProjectFromId(projId);

		ISubject wsSubject = createSubjectAndCheck(p);
		Long wsSubjectId = saveSubjectAndCheck(wsSubject);

		ISubject newSubject = null;
		try {
			newSubject = dao.getSubject(wsSubjectId).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		}
		AssertJUnit.assertNotNull("New subject is null", newSubject);
		AssertJUnit.assertNotNull("New subject's Group is null", newSubject.getGroup());
		
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.dao.EslDAO#retrieveSubjectByStudy(org.psygrid.esl.model.dto.Project, java.lang.String)}.
	 */
	@Test
	public void testRetrieveSubjectForStudyNumber() {

		Long projId = null;
		String projectName = "EslDAO-retrieveSubjectForStudyNumber";

		IProject project = helper.populateProject(projectName, factory);
		String studyNumber = "Test subject-"+helper.STUDY_NUMBER;
		
		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		try {
			projId = dao.saveProject(dtoProj, null);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Exception: "+ex.toString());
		}

		AssertJUnit.assertNotNull("Project wasn't saved properly", projId);

		//use dao to retrieve the subject
		org.psygrid.esl.model.dto.Project dtoProject = retrieveDtoProjectAndCheck(projId);
		ISubject wsSubject = null;
		
		try {
			wsSubject = dao.getSubject(dtoProject, studyNumber).toHibernate();
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		}

		AssertJUnit.assertNotNull("Subject wasn't retrieved", wsSubject);
		AssertJUnit.assertEquals("Study numbers don't match", studyNumber, wsSubject.getStudyNumber());
	}
	
	@Test
	public void testDeleteSubject() {
		Long projId = createProjectSaveAndCheck("EslDAOTest-deleteSubject");
		IProject p = getProjectFromId(projId);
		
		ISubject wsSubject = createSubjectAndCheck(p);
		
		Long wsSubjectId = saveSubjectAndCheck(wsSubject);
		
		try {
			dao.deleteSubject("Test subject-"+helper.STUDY_NUMBER);
		} catch (NoResultsFoundException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception: "+e.toString());
		}
		
		boolean subjectDeleted = false;
		try {
			dao.getSubject(wsSubjectId).toHibernate();
		}
		catch (NoResultsFoundException e) {
			subjectDeleted = true;
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		}
		
		AssertJUnit.assertTrue("No NoResultsFoundException was thrown", subjectDeleted);
	}
	
	@Test
	public void testDeleteSubjectDoesntExist() {
		createProjectSaveAndCheck("EslDAOTest-testDeleteSubjectDoesntExist");
		
		boolean exceptionThrown = false;
		try {
			dao.deleteSubject(helper.STUDY_NUMBER);
		}
		catch (NoResultsFoundException e) {
			exceptionThrown = true;
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		}
		
		AssertJUnit.assertTrue("No NoResultsFoundException was thrown", exceptionThrown);
	}

	@Test
	public void testIsRandomised() {
		String projectName = "EslDAO-isRandomised";

		IProject project = helper.populateProject(projectName, factory);
		boolean randomised = false;
		if (project.getRandomisation() != null) {
			randomised = true;
			System.out.println("project is randomised");	
		}
		else {
			System.out.println("project is not randomised");
		}
		
		boolean testRandomised = false;
		try {
			dao.saveProject(project.toDTO(), "testIsProjectRandomised");
			testRandomised = dao.isRandomised(project.getProjectCode());
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		}
		AssertJUnit.assertEquals("Test failed: ", randomised, testRandomised);
	}

	@Test
	public void testIsRandomised_fail() {
		String projectName = "EslDAO-isRandomised_fail";

		IProject project = helper.populateProject(projectName, factory);
		
		boolean testRandomised = false;
		try {
			project.setRandomisation(null);
			dao.saveProject(project.toDTO(), "testIsProjectRandomised");
			testRandomised = dao.isRandomised(project.getProjectCode());
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		}
		AssertJUnit.assertFalse("Test failed: ", testRandomised);
	}
	
	@Test
	public void testIsEslProject() {
		String projectName = "EslDAO-isEslProject";

		IProject project = helper.populateProject(projectName, factory);
				
		boolean testProject = false;
		try {
			dao.saveProject(project.toDTO(), "testIsEslProject");
			testProject = dao.isEslProject(project.getProjectCode());
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		}
		AssertJUnit.assertTrue("Test failed: ", testProject);
	}
	
	@Test
	public void testIsEslProject_fail() {
		String projectName = "EslDAO-isEslProjectFail";

		IProject project = helper.populateProject(projectName, factory);
				
		boolean testProject = false;
		try {
			dao.saveProject(project.toDTO(), "testIsEslProject_fail");
			testProject = dao.isEslProject("a project that doesn't exist");
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		}
		AssertJUnit.assertFalse("Test failed: ", testProject);
	}
	
	private Long createProjectSaveAndCheck(String projectName) {
		Long projId = null;
		
		IProject project = helper.populateProject(projectName, factory);
		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		
		try {
			projId = dao.saveProject(dtoProj, null);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Exception: "+ex.toString());
		}
		
		AssertJUnit.assertNotNull("projId is null", projId);
		
		return projId;
	}
	
	private IProject getProjectFromId(Long projId) {
		IProject p  = null;

		try {
			p = dao.getProject(projId).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Exception: "+ex.toString());
		}

		AssertJUnit.assertNotNull("p id is null", p.getId());
		
		return p;
	}
	
	private ISubject createSubjectAndCheck(IProject p) {
		ISubject wsSubject = factory.createSubject("Test subject-"+helper.STUDY_NUMBER);
		AssertJUnit.assertNotNull("No Groups found", p.getGroups().get(0));
		AssertJUnit.assertNotNull("No ID found for group", p.getGroups().get(0).getId());
		wsSubject.setGroup((org.psygrid.esl.model.hibernate.Group)p.getGroups().get(0));
		wsSubject.setFirstName("fred");
		
		return wsSubject;
	}
	
	private long saveSubjectAndCheck(ISubject wsSubject) {
		Long wsSubjectId = null;
		try {
			wsSubjectId = dao.saveSubject(wsSubject, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		}

		AssertJUnit.assertNotNull("New subject's Id is null", wsSubjectId);
		
		return wsSubjectId;
	}
	
	org.psygrid.esl.model.dto.Project retrieveDtoProjectAndCheck(Long projId) {
		org.psygrid.esl.model.dto.Project dtoProject = null;
		
		try {
			dtoProject = dao.getProject(projId);
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		}
		AssertJUnit.assertNotNull("Project wasn't retrieved properly", dtoProject);
		
		return dtoProject;
	}
}
