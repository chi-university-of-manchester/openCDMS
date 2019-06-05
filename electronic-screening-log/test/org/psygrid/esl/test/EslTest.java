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


package org.psygrid.esl.test;

import java.io.FileOutputStream;
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
import org.psygrid.common.email.Email;
import org.psygrid.esl.model.IAddress;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IGroup;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IRandomisation;
import org.psygrid.esl.model.IRole;
import org.psygrid.esl.model.IStrata;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.hibernate.Address;
import org.psygrid.esl.model.hibernate.Project;
import org.psygrid.esl.model.hibernate.Randomisation;
import org.psygrid.esl.model.hibernate.Role;
import org.psygrid.esl.model.hibernate.Strata;
import org.psygrid.esl.model.hibernate.Subject;
import org.psygrid.esl.randomise.EmailType;
import org.psygrid.esl.services.ESLDuplicateObjectFault;
import org.psygrid.esl.services.ESLOutOfDateFault;
import org.psygrid.esl.services.ESLServiceFault;
import org.psygrid.esl.services.ESLSubjectExistsException;
import org.psygrid.esl.services.Esl;
import org.psygrid.esl.services.NotAuthorisedFault;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.esl.util.EmailUtil;
import org.psygrid.logging.AuditLogger;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.accesscontrol.AEFAction;
import org.psygrid.security.accesscontrol.AEFGroup;
import org.psygrid.security.accesscontrol.AEFProject;
import org.psygrid.security.accesscontrol.IAccessEnforcementFunction;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

import static org.mockito.Mockito.*;

/**
 * Populates the database with an example project to test the web services
 * 
 * 		fail("This test needs to be fixed as Subjects are not now sent as part of a Group DTO");
 * 
 * @author Lucy Bridges
 *
 */
/**
 * @author Lucy Bridges
 *
 */
@Test(groups = {"integration-tests"})
public class EslTest {
	private static final String STUDY_NUMBER_START = "TST/001-";
	private static int participantIdNumber = 1;
	private static final String PROJECT_CODE = "TST";
	private static Long currentProjId; 
	

	private static EslClient client;

	private static String studyNumber = "studyNumber-2";
	
	private String saml;
	private ApplicationContext ctx = null;
	private TestHelper helper;
	private Esl esl;
	private org.psygrid.esl.model.IFactory factory;
	
	private IAccessEnforcementFunction accessControl;
	private AuditLogger auditLogger;
	private EmailUtil emailUtil;
	
	@BeforeClass
	public void initialise() throws Exception {
		saml = "";
		String[] paths = {"applicationContext.xml"};
		try {
			ctx = new ClassPathXmlApplicationContext(paths);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Exception: "+ex.toString());
		}
		helper = new TestHelper();
		esl = (Esl)ctx.getBean("eslService");
		factory = (IFactory) ctx.getBean("factory");
		accessControl = (IAccessEnforcementFunction) ctx.getBean("accessController");
		auditLogger = (AuditLogger) ctx.getBean("eslAuditLogger");
		emailUtil = (EmailUtil) ctx.getBean("emailUtil");
		mockAccessControl();
		mockAuditLogger();
		mockEmailUtil();
	}
	
	@AfterMethod
	protected void tearDown() throws Exception {
		deleteProject(currentProjId, PROJECT_CODE);
	} 
	
	@Test
	public void testSaveProject() {
//		Assert.fail("This test needs to be fixed as Subjects are not now sent as part of a Group DTO");

		Long projId = null;
		IProject p  = null;

		IProject project = helper.populateProject("EslTest", factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = esl.saveProject(dtoProj, saml);
			currentProjId = projId;
			p = esl.retrieveProject(projId, saml).toHibernate();
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
		Long projId = createProjectSaveAndCheck("EslTest");
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
			projId2 = esl.saveProject(p2.toDTO(), null);
			esl.retrieveProject(projId2, null).toHibernate();
		}
		catch (ESLDuplicateObjectFault doe) {
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

		IProject project = helper.populateProject("EslTest", factory);
		
		String projectCode = project.getProjectCode();
		
		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();

		try {
			projId = esl.saveProject(dtoProj, null);
			currentProjId = projId;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Exception: "+ex.toString());
		}

		try {
			p = esl.retrieveProjectByCode(projectCode, null);
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
		Long projId = createProjectSaveAndCheck("EslTest");
		IProject p = getProjectFromId(projId);

		ISubject wsSubject = createSubjectAndCheck(p);
		
		saveSubjectAndCheck(wsSubject);		
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslDAO#saveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	@Test
	public void testSaveSubject_duplicate() {
		Long projId = createProjectSaveAndCheck("EslTest-saveSubject_duplicate");
		IProject p = getProjectFromId(projId);
		
		ISubject wsSubject = createSubjectAndCheck(p);
		
		saveSubjectAndCheck(wsSubject);
		org.psygrid.esl.model.dto.Subject dtoSubject = wsSubject.toDTO();

		Long wsSubjectId2 = null;
		
		//attempt to save the same object a second time, which
		//should produce an error.
		try {
			wsSubjectId2 = esl.saveSubject(dtoSubject, null);
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
		Long projId = createProjectSaveAndCheck("EslTest-saveSubject_fail");
		IProject p = getProjectFromId(projId);
		AssertJUnit.assertNotNull("No Groups found", p.getGroups().get(0));
		AssertJUnit.assertNotNull("No ID found for group", p.getGroups().get(0).getId());

		ISubject wsSubject = factory.createSubject("Test subject-"+TestHelper.getUnique());
		
		Long wsSubjectId = null;
		wsSubject.setFirstName("fred");
		org.psygrid.esl.model.dto.Subject dtoSubject = wsSubject.toDTO();
		
		//Not setting the group should produce an error
		//wsSubject.setGroup((org.psygrid.esl.model.hibernate.Group)p.getGroups().get(0));
		boolean error = false;
		try {
			wsSubjectId = esl.saveSubject(dtoSubject, null);
		}
		catch (ESLServiceFault e) {
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
		Long projId = createProjectSaveAndCheck("EslTest-retrieveSubject");
		IProject p = getProjectFromId(projId);

		ISubject wsSubject = createSubjectAndCheck(p);
		Long wsSubjectId = saveSubjectAndCheck(wsSubject);

		ISubject newSubject = null;
		try {
			newSubject = esl.retrieveSubject(wsSubjectId, null).toHibernate();
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
		String studyNumber = STUDY_NUMBER_START+participantIdNumber;
		
		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		try {
			projId = esl.saveProject(dtoProj, null);
			currentProjId = projId;
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
			wsSubject = esl.retrieveSubjectByStudyNumber(dtoProject, studyNumber, null).toHibernate();
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
		Long projId = createProjectSaveAndCheck("EslTest-deleteSubject");
		IProject p = getProjectFromId(projId);
		
		ISubject wsSubject = createSubjectAndCheck(p);
		
		Long wsSubjectId = saveSubjectAndCheck(wsSubject);
		
		try {
			esl.deleteSubject(STUDY_NUMBER_START+participantIdNumber, null);
		} catch (RemoteException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception: "+e.toString());
		} catch (ESLServiceFault e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception: "+e.toString());
		} catch (NotAuthorisedFault e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception: "+e.toString());
		}
		
		boolean subjectDeleted = false;
		try {
			esl.retrieveSubject(wsSubjectId, null).toHibernate();
		}
		catch (ESLServiceFault e) {
			subjectDeleted = true;
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		}
		
		AssertJUnit.assertTrue("No NoResultsFoundException was thrown", subjectDeleted);
	}
	
	@Test
	public void testDeleteSubjectWithChanges() {
		Long projId = createProjectSaveAndCheck("EslTest-deleteSubjectWithChanges");
		IProject p = getProjectFromId(projId);
		
		ISubject wsSubject = createSubjectAndCheck(p);
		
		Long wsSubjectId = saveSubjectAndCheck(wsSubject);
		
		org.psygrid.esl.model.dto.Subject subject;		
		try {
			subject = esl.retrieveSubject(wsSubjectId, null);
			subject.setCentreNumber("12345");
			esl.saveSubject(subject, null);
		} catch (RemoteException e1) {
			e1.printStackTrace();
			Assert.fail("Unexpected exception: "+e1.toString());
		} catch (ESLDuplicateObjectFault e1) {
			e1.printStackTrace();
			Assert.fail("Unexpected exception: "+e1.toString());
		} catch (ESLOutOfDateFault e1) {
			e1.printStackTrace();
			Assert.fail("Unexpected exception: "+e1.toString());
		} catch (ESLServiceFault e1) {
			e1.printStackTrace();
			Assert.fail("Unexpected exception: "+e1.toString());
		} catch (ESLSubjectExistsException e1) {
			e1.printStackTrace();
			Assert.fail("Unexpected exception: "+e1.toString());
		} catch (NotAuthorisedFault e1) {
			e1.printStackTrace();
			Assert.fail("Unexpected exception: "+e1.toString());
		}
		
		try {
			esl.deleteSubject(STUDY_NUMBER_START+participantIdNumber, null);
		} catch (RemoteException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception: "+e.toString());
		} catch (ESLServiceFault e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception: "+e.toString());
		} catch (NotAuthorisedFault e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception: "+e.toString());
		}
		
		boolean subjectDeleted = false;
		try {
			esl.retrieveSubject(wsSubjectId, null).toHibernate();
		}
		catch (ESLServiceFault e) {
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
		Long projId = createProjectSaveAndCheck("EslTest-testDeleteSubjectDoesntExist");
		
		boolean exceptionThrown = false;
		try {
			esl.deleteSubject(helper.STUDY_NUMBER, null);
		}
		catch (ESLServiceFault e) {
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
		long projId = 0;
		try {
			projId = esl.saveProject(project.toDTO(), "testIsProjectRandomised");
			currentProjId = projId;
			testRandomised = esl.isProjectRandomised(project.getProjectCode(), null);
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
		long projId = 0;
		try {
			project.setRandomisation(null);
			projId = esl.saveProject(project.toDTO(), "testIsProjectRandomised");
			currentProjId = projId;
			testRandomised = esl.isProjectRandomised(project.getProjectCode(), null);
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
			currentProjId = esl.saveProject(project.toDTO(), "testIsEslProject");
			testProject = esl.isEslProject(project.getProjectCode(), null);
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
			currentProjId = esl.saveProject(project.toDTO(), "testIsEslProject_fail");
			testProject = esl.isEslProject("a project that doesn't exist", null);
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
			projId = esl.saveProject(dtoProj, null);
			currentProjId = projId;
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
			p = esl.retrieveProject(projId, null).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Exception: "+ex.toString());
		}

		AssertJUnit.assertNotNull("p id is null", p.getId());
		
		return p;
	}
	
	private void deleteProject(long projectId, String projectCode) {
		try {
			esl.deleteProject(projectId, projectCode, null);
		} catch (RemoteException e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		} catch (ESLServiceFault e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		} catch (NotAuthorisedFault e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		}
	}
	
	private ISubject createSubjectAndCheck(IProject p) {
		ISubject wsSubject = factory.createSubject(STUDY_NUMBER_START+(++participantIdNumber));
		AssertJUnit.assertNotNull("No Groups found", p.getGroups().get(0));
		AssertJUnit.assertNotNull("No ID found for group", p.getGroups().get(0).getId());
		wsSubject.setGroup((org.psygrid.esl.model.hibernate.Group)p.getGroups().get(0));
		wsSubject.setFirstName("fred");
		
		return wsSubject;
	}
	
	private long saveSubjectAndCheck(ISubject wsSubject) {
		org.psygrid.esl.model.dto.Subject dtoSubject = wsSubject.toDTO();
		Long wsSubjectId = null;
		try {
			wsSubjectId = esl.saveSubject(dtoSubject, null);
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
			dtoProject = esl.retrieveProject(projId, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception: "+e.toString());
		}
		AssertJUnit.assertNotNull("Project wasn't retrieved properly", dtoProject);
		
		return dtoProject;
	}
	

	/**
	 * @param args the username and password required for authentication with the ws
	 */
/*	public static void main(String[] args) throws Exception {

		SAMLAssertion saml = login(args);

		client = new EslClient();

		System.out.println("Esl version is: "+ client.getVersion());

		//setup project
		IProject project = new Project();
		project.setProjectCode("ED2");
		project.setProjectName("EDIE 2");
		client.saveProject(project, saml);
		

		IProject project = client.retrieveProjectByCode("ED2", saml);
	
		System.out.println("Project retrieved. Id is:" + project.getId() );

	//editSubjects(project, saml);
		
		//create subjects
			populateSubjects(project, saml);
		//editSubjects(project, saml);
		
	//	project = client.retrieveProjectByCode("ED2", saml);
		
		//setupRandomsation
		//setupRandomisation(project, saml);

		//project = client.retrieveProjectByCode("ED2", saml);
		
		//setupEmails(project, saml);
		
		//project = client.retrieveProjectByCode("ED2", saml);
		
		//allocate treatment arms to subjects and lookup the results
		//allocateTreatments(project, saml);

		//lookupRandomisationResults(project, saml);

		//retrieveProject
		IProject p1 = client.retrieveProject(project.getId(), saml);
		System.out.println("Project retrieved. Studycode is:" + p1.getProjectCode() );

		//retrieveAllProjects
		//System.out.println("Retrieve all projects: "+client.retrieveAllProjects(saml));

		project = client.retrieveProjectByCode("ED2", saml);
		//subject searches
	//	exampleSubjectSearches(project, saml);

		//exists
		if (client.exists(project, studyNumber, saml)) {
			System.out.println("The subject '"+studyNumber+"' exists");
		}
		else {
			System.out.println("The subject '"+studyNumber+"' doesn't exist!");
		}

		//emergencyBreakIn
	//	emergencyBreakIn(project, saml);

		//save individual subject
		//ISubject subject = saveSubject(project.getGroups().get(0), saml);
		//System.out.println("Saved subject's Id is: "+ subject.getId());
		
		//update subject
		//updateSubject(project, saml);
		
		//update project
	//	IProject updateProj = client.retrieveProject(project.getId(), saml);
		//updateProj.setProjectName("EDIE 2");
		//client.saveProject(updateProj, saml);

	} */

	private static SAMLAssertion login(String[] args) throws Exception {

		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		Options opts = new Options(args);
		Properties properties = PropertyUtilities.getProperties("test.properties");
		System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
		LoginClient tc = null;

		try {
			tc = new LoginClient("test.properties");
			tc.getPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		char[] password = opts.getPassword().toCharArray();
		short[] pwd = new short[password.length];
		for (int i = 0; i < pwd.length; i++) {
			pwd[i] = (short) password[i];
		}
		String credential = tc.getPort().login(opts.getUser(), pwd);
		if (credential != null) {
			byte[] ks = Base64.decode(credential);
			FileOutputStream fos = new FileOutputStream(properties
					.getProperty("org.psygrid.security.authentication.client.keyStoreLocation"));
			fos.write(ks);
			fos.flush(); 
			fos.close();
		}
		System.out.println("loggedin");
		System.setProperty("javax.net.ssl.keyStorePassword", new String(password));
		PsyGridClientSocketFactory.reinit();
		AAQueryClient qc = new AAQueryClient("test.properties");
		System.out.println("getAssertion");
		SAMLAssertion sa = qc.getSAMLAssertion(new ProjectType("LocalTrial2", "zyx", null, null, false));

		return sa;
	}

	private static void editSubjects(IProject project, String saml) throws Exception {
		IGroup grp1 = project.getGroups().get(0);
		
		for (int i = 0; i < 10; i++) {
			ISubject s = client.retrieveSubjectByStudyNumber(project, "studyNumber-"+i, saml);
			s.setCentreNumber("001001");
			client.saveSubject(s, saml);
		}
	}
	
	private static void editTreatments(IProject project, String saml) throws Exception {
		IRandomisation random = project.getRandomisation();
		
		Map<String, String> treatments = new HashMap<String,String>();
		treatments.put("ED2-000", "EDIE 2 Control");
        treatments.put("ED2-001", "EDIE 2 CognitiveBehaviourTherapy");
        
		random.setTreatments(treatments);
		client.saveProject(project, saml);
	}
	
	
	private static void populateSubjects(IProject project, String saml) throws Exception {
		IGroup grp1 = project.getGroups().get(0);
		
		for (int i = 63; i < 65; i++) {
			ISubject s1 = new Subject();
			s1.setCentreNumber("001001");
			s1.setStudyNumber("studyNumber-"+i);
			s1.setFirstName("firstName-"+i);
			s1.setDateOfBirth(new Date());
			s1.setSex("Female");
			IAddress a1 = new Address();
			a1.setAddress1("address1-"+i);
			a1.setAddress2("address2-"+i);
			a1.setPostCode("postCode-"+i);
			a1.setCity("city-"+i);

			s1.setAddress(a1);
			grp1.setSubject(s1);
		}

		project.setGroup(grp1);

		client.saveProject(project, saml);
	}

	private static ISubject saveSubject(IGroup group, String saml) throws Exception {

		ISubject s1 = new Subject();
		s1.setCentreNumber("centreNumber");
		s1.setStudyNumber("studyNumber");
		s1.setFirstName("firstName");
		s1.setDateOfBirth(new Date());
		s1.setSex("female");
		IAddress a1 = new Address();
		a1.setAddress1("address1");
		a1.setAddress2("address2");
		a1.setPostCode("postCode");
		a1.setCity("city");

		s1.setAddress(a1);
		s1.setGroup(group);

		client.saveSubject(s1, saml);
		
		return client.retrieveSubjectByStudyNumber(group.getProject(), "studyNumber", saml);
	}


	private static void allocateTreatments(IProject project, String saml) throws Exception {

		ISubject subject = null;
		for (int i = 0; i < 10; i++) {
			subject = client.retrieveSubjectByStudyNumber(project, "studyNumber-"+i, saml);
			if (subject != null) {
				client.randomiseSubject(subject, null, saml);
			}
		}
	}

	private static void setupRandomisation(IProject project, String saml) throws Exception {

		IRandomisation r = null;

		r = project.getRandomisation();

		if ( r == null) {
		r = new Randomisation();

		List<IStrata> strata = new ArrayList<IStrata>();
		IStrata stratum = new Strata();
		stratum.setName("sex");
		List<String> values = new ArrayList<String>();
		values.add("male");
		values.add("female");
		stratum.setValues(values);
		strata.add(stratum);

		Map<String,String> treatments = new HashMap<String,String>();
		treatments.put("treatment1", "treatment name 1");
		treatments.put("treatment2", "treatment name 2");

		List<IRole> roles = new ArrayList<IRole>();
		IRole r1 = new Role();
		r1.setName("CRM");
		r1.setNotifyOfRSDecision(true);
		r1.setNotifyOfRSInvocation(true);
		r1.setNotifyOfRSTreatment(true);
		roles.add(r1);

		IRole r2 = new Role();
		r2.setName("CRO");
		r2.setNotifyOfRSDecision(true);
		r2.setNotifyOfRSInvocation(false);
		r2.setNotifyOfRSTreatment(false);
		roles.add(r2);

		IRole r3 = new Role();
		r3.setName("Therapist");
		r3.setNotifyOfRSDecision(true);
		r3.setNotifyOfRSInvocation(false);
		r3.setNotifyOfRSTreatment(true);
		roles.add(r3);

		Map<String,Email> emails = new HashMap<String,Email>();
		Email e1 = new Email();
		e1.setSubject("Notification of Invocation");
		e1.setBody("Notification of Invocation");
		Email e2 = new Email();
		e2.setSubject("Notification of Decision");
		e2.setBody("A treatment arm has been allocated to the subject '%subjectCode%'.");
		Email e3 = new Email();
		e3.setSubject("Notification of Treatment");
		e3.setBody("The subject '%subjectCode%' has been allocated the treatment %treatment%.");
		emails.put(EmailType.INVOCATION.type(), e1);
		emails.put(EmailType.DECISION.type(), e2);
		emails.put(EmailType.TREATMENT.type(), e3);

		r.setName("randomisation-"+TestHelper.getUnique());
		r.setTreatments(treatments);
		r.setStrata(strata);
		r.setRolesToNotify(roles);
		r.setEmails(emails);
		project.setRandomisation(r);
		}
		client.setupRandomisation(project, r, saml);

	}

	private static void lookupRandomisationResults(IProject project, String saml) throws Exception {

		String result;
		for (int i = 0; i < 10; i++) {
			result = client.lookupRandomisationResult(project, "studyNumber-"+i, saml);
			System.out.println("Subject "+"studyNumber-"+i+" has been allocated treatment "+result);
		}

	}

	private static void exampleSubjectSearches(IProject project, String saml) throws Exception {

		ISubject exampleSubject = new Subject();
		exampleSubject.setFirstName("firstName-12");
		exampleSubject.setStudyNumber(studyNumber);

		//lookupStudyNumber
		//String sn = client.lookupStudyNumber(project, exampleSubject, saml);
		//System.out.println("Example Subject's study number is: " + sn);

		//findSubjectByExample
		List<ISubject> list = client.findSubjectByExample(project, exampleSubject, saml);
		System.out.println("FindSubjectByExample results list: "+list);

		ISubject exampleSubject2 = new Subject();
		exampleSubject2.setStudyNumber(studyNumber);
		IAddress address = new Address();
		address.setAddress1("nothing");
		exampleSubject2.setAddress(address);
		List<ISubject> emptylist = client.findSubjectByExample(project, exampleSubject2, saml);
		System.out.println("FindSubjectByExample results list (should be empty): "+emptylist);

		
		Long subjectId = list.get(0).getId();
		//retrieveSubject
		ISubject s = client.retrieveSubject(subjectId, saml);
		System.out.println("retrieved subject: "+s.getFirstName()+" "+s.getLastName());
	}

	private static void emergencyBreakIn(IProject project, String saml) throws Exception {
		Map<String, String> results = client.emergencyBreakIn(project, saml);
		System.out.println("EmergencyBreakIn results are: " +results);
	}
	
	private static void updateSubject(IProject project, String saml) throws Exception {
		Long subjectId = client.retrieveSubjectByStudyNumber(project, studyNumber, saml).getId();
		ISubject s = client.retrieveSubject(subjectId, saml);
		System.out.println("The subject "+s.getStudyNumber()+" has been retrieved");
		s.setFirstName(s.getFirstName()+"-updated");
		s.setHospitalNumber("1234");
		Long sId = client.saveSubject(s, saml);
		System.out.println("The subject "+s.getStudyNumber()+" has been saved with id: "+sId);
	}
	
	private static void setupEmails(IProject project, String saml) throws Exception {
		IRandomisation r = project.getRandomisation();
		
		Map<String,Email> emails = new HashMap<String,Email>();
		Email e1 = new Email();
		e1.setSubject("Notification of Invocation");
		e1.setBody("Notification of Invocation");
		Email e2 =  new Email();
		e2.setSubject("Notification of Decision");
		e2.setBody("A treatment arm has been allocated to the subject '%subjectCode%'.");
		Email e3 = new Email();
		e3.setSubject("Notification of Treatment");
		e3.setBody("The subject '%subjectCode%' has been allocated the treatment %treatment%.\n	" +
				"The subject has the following risk issues:" +
				"\n\n" +
				"%riskIssues%");
					
		emails.put(EmailType.INVOCATION.type(), e1);
		emails.put(EmailType.DECISION.type(), e2);
		emails.put(EmailType.TREATMENT.type(), e3);
		r.setEmails(emails);
		project.setRandomisation(r);
		client.saveProject(project, saml);
		
	}
	
	private void mockAccessControl() {
		try {
			when(accessControl.authoriseUser(anyString(),any(AEFGroup.class),any(AEFAction.class),any(AEFProject.class))).thenReturn(true);
			when(accessControl.getUserFromSAML(anyString())).thenReturn("testuser");
		} catch (PGSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PGSecurityInvalidSAMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PGSecuritySAMLVerificationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		when(accessControl.getCallersIdentity()).thenReturn("testuser");
	}
	
	private void mockAuditLogger() {
		doNothing().when(auditLogger).logMethodCall(anyString(), anyString(), anyString(), anyString());
	}
	
	private void mockEmailUtil() {
		try {
			when(emailUtil.getEmailRecipients(any(ISubject.class), anyListOf(String.class))).thenReturn(null);
		} catch (NotAuthorisedFaultMessage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
