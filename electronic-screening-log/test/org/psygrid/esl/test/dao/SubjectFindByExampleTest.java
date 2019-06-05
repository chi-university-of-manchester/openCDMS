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

import java.util.List;
import junit.framework.TestCase;

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
public class SubjectFindByExampleTest extends TestCase {

	protected EslDAO dao;
	protected org.psygrid.esl.model.IFactory factory;
	protected ApplicationContext ctx = null;

	public SubjectFindByExampleTest () {
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
	 * Test method for {@link org.psygrid.esl.dao.EslDAO#findSubjectByExample(org.psygrid.esl.model.dto.Project project, org.psygrid.esl.model.dto.Subject exampleSubject)}.
	 */
	public void testFindSubjectByExample() {

		Long projId = null;
		String projectName = "EslDAO-findSubjectByExample";

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
		//use dao to retrieve the subject
		org.psygrid.esl.model.dto.Project dtoProject = null;
		try {
			dtoProject = dao.getProject(projId);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project wasn't retrieved properly", dtoProject);
		
		//Create an example subject to use
		ISubject exampleSubject = factory.createSubject();
		exampleSubject.setFirstName(helper.SUBJECT_FIRSTNAME);
		exampleSubject.setLastName(helper.SUBJECT_LASTNAME);
		exampleSubject.setStudyNumber(studyNumber);
		
		
		org.psygrid.esl.model.dto.Subject[] results = null;
		
		try {
			String[] groups = new String[2];
			groups[0] = dtoProject.getGroups()[0].getCode(); 
			groups[1] = dtoProject.getGroups()[1].getCode();
			
			results = dao.findSubjectByExample(dtoProject, exampleSubject.toDTO(), groups);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("No subjects found", results);
		assertFalse("No subjects were found", results.length == 0);	
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.dao.EslDAO#findSubjectByExample(org.psygrid.esl.model.dto.Project project, org.psygrid.esl.model.dto.Subject exampleSubject)}.
	 */
	public void testFindSubjectByExample_Group() {

		Long projId = null;
		String projectName = "EslDAO-findSubjectByExample_Group";

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
		//use dao to retrieve the subject
		org.psygrid.esl.model.dto.Project dtoProject = null;
		try {
			dtoProject = dao.getProject(projId);
			//same as existing subject but belonging to a different group
			ISubject subject = factory.createSubject();
			subject.setFirstName(helper.SUBJECT_FIRSTNAME);
			subject.setLastName(helper.SUBJECT_LASTNAME);
			subject.setStudyNumber(helper.getUnique());
			subject.setGroup(dtoProject.getGroups()[1].toHibernate());
			Long id = dao.saveSubject(subject, "findSubjectByExample_Group");
			
			dtoProject = dao.getProject(projId);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project wasn't retrieved properly", dtoProject);
		
		//Create an example subject to use
		ISubject exampleSubject = factory.createSubject();
		exampleSubject.setFirstName(helper.SUBJECT_FIRSTNAME);
		exampleSubject.setLastName(helper.SUBJECT_LASTNAME);

		//only one subject with the above criteria should be in this group
		exampleSubject.setGroup(dtoProject.getGroups()[0].toHibernate());
		
		
		org.psygrid.esl.model.dto.Subject[] results = null;
		
		try {
			String[] groups = new String[2];
			groups[0] = dtoProject.getGroups()[0].getCode(); 
			groups[1] = dtoProject.getGroups()[1].getCode();
			
			results = dao.findSubjectByExample(dtoProject, exampleSubject.toDTO(), groups);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("No subjects found", results);
		assertFalse("No subjects were found", results.length == 0);
		assertEquals("Incorrect number of subjects found", 1, results.length);	
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.dao.EslDAO#findSubjectByExample(org.psygrid.esl.model.dto.Project project, org.psygrid.esl.model.dto.Subject exampleSubject)}.
	 */
	public void testFindSubjectByExample_GroupFail() {

		Long projId = null;
		String projectName = "EslDAO-findSubjectByExample_GroupFail";

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
		//use dao to retrieve the subject
		org.psygrid.esl.model.dto.Project dtoProject = null;
		try {
			dtoProject = dao.getProject(projId);
			//same as existing subject but belonging to a different group
			ISubject subject = factory.createSubject();
			subject.setFirstName(helper.SUBJECT_FIRSTNAME);
			subject.setLastName(helper.SUBJECT_LASTNAME);
			subject.setStudyNumber(helper.getUnique());
			subject.setGroup(dtoProject.getGroups()[1].toHibernate());
			Long id = dao.saveSubject(subject, "findSubjectByExample_GroupFail");
			
			dtoProject = dao.getProject(projId);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project wasn't retrieved properly", dtoProject);
		
		//Create an example subject to use
		ISubject exampleSubject = factory.createSubject();
		exampleSubject.setFirstName(helper.SUBJECT_FIRSTNAME);
		exampleSubject.setLastName(helper.SUBJECT_LASTNAME);
		exampleSubject.setStudyNumber(studyNumber);
		//no subjects with the above criteria should be in this group (different study number)
		exampleSubject.setGroup(dtoProject.getGroups()[1].toHibernate());
		
		
		org.psygrid.esl.model.dto.Subject[] results = null;
		
		try {
			String[] groups = new String[2];
			groups[0] = dtoProject.getGroups()[0].getCode(); 
			groups[1] = dtoProject.getGroups()[1].getCode();
			
			results = dao.findSubjectByExample(dtoProject, exampleSubject.toDTO(), groups);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("No subjects returned", results);
		assertEquals("Subjects were found", 0, results.length);	
		
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.dao.EslDAO#findSubjectByExample(org.psygrid.esl.model.dto.Project project, org.psygrid.esl.model.dto.Subject exampleSubject)}.
	 */
	public void testFindSubjectByExample_False() {

		Long projId = null;
		String projectName = "EslDAO-findSubjectByExample_False";

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
		//use dao to retrieve the subject
		org.psygrid.esl.model.dto.Project dtoProject = null;
		try {
			dtoProject = dao.getProject(projId);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project wasn't retrieved properly", dtoProject);
		
		//Create an example subject to use
		ISubject exampleSubject = factory.createSubject("Doesn't exist");
		exampleSubject.setFirstName("My made up example");
		exampleSubject.setStudyNumber("Number that doesn't exist 2");
		
		org.psygrid.esl.model.dto.Subject[] results = null;
		
		try {
			String[] groups = new String[2];
			groups[0] = dtoProject.getGroups()[0].getCode(); 
			groups[1] = dtoProject.getGroups()[1].getCode();
			results = dao.findSubjectByExample(dtoProject, exampleSubject.toDTO(), groups);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertEquals("Subjects were found", 0, results.length);

	}
	
	/**
	 * Test method for {@link org.psygrid.esl.dao.EslDAO#findSubjectByExample(org.psygrid.esl.model.dto.Project project, org.psygrid.esl.model.dto.Subject exampleSubject)}.
	 */
	public void testFindSubjectByExample_empty() {
		fail("This test needs to be fixed as Subjects are not now sent as part of a Group DTO");

		Long projId = null;
		String projectName = "EslDAO-findSubjectByExample_empty";

		TestHelper helper = new TestHelper();
		IProject project = helper.populateProject(projectName, factory);
		String studyNumber = helper.STUDY_NUMBER;
		ISubject s = factory.createSubject(helper.getUnique());
		s.setFirstName("test 2");
		project.getGroups().get(0).setSubject(s);
		
		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		try {
			projId = dao.saveProject(dtoProj, null);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		assertNotNull("Project wasn't saved properly", projId);
		//use dao to retrieve the subject
		org.psygrid.esl.model.dto.Project dtoProject = null;
		try {
			dtoProject = dao.getProject(projId);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project wasn't retrieved properly", dtoProject);
		
		//Create an empty subject to use
		ISubject exampleSubject = factory.createSubject();
		//exampleSubject.setFirstName(helper.SUBJECT_FIRSTNAME);
		///exampleSubject.setLastName(helper.SUBJECT_LASTNAME);
		//exampleSubject.setStudyNumber(studyNumber);
		
		
		org.psygrid.esl.model.dto.Subject[] results = null;
		
		try {
			String[] groups = new String[2];
			groups[0] = dtoProject.getGroups()[0].getCode(); 
			groups[1] = dtoProject.getGroups()[1].getCode();
			results = dao.findSubjectByExample(dtoProject, exampleSubject.toDTO(), groups);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}


		//this returns all subjects in a project for the specified groups
		//as no restrictions have been specified in the exampleSubject
		assertEquals("Wrong number of Subjects found", 2, results.length);

	}
}