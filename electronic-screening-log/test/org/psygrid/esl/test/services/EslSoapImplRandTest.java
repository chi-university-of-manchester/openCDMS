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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.psygrid.esl.dao.*;
import org.psygrid.esl.model.*;
import org.psygrid.esl.model.hibernate.Randomisation;
import org.psygrid.esl.test.TestHelper;
import org.psygrid.esl.model.hibernate.Strata;
import org.psygrid.esl.services.client.EslClient;

/**
 * Test the remote randomiser and treatment allocation.
 * 
 * @author Lucy Bridges
 *
 */
public class EslSoapImplRandTest extends TestCase {

	protected EslDAO dao;
	protected org.psygrid.esl.model.IFactory factory;
	protected ApplicationContext ctx = null;
	
	public EslSoapImplRandTest() {
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
	 * Test method for {@link org.psygrid.esl.dao.EslSoapBindingImpl#createRandomise(org.psygrid.esl.model.dto.Randomisation random)}.
	 */
	public void testSetupRandomisation() {
		String projectName = "EslSoapBindingImpl-setupRandomisation";

		Long projId = null;
		
		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		try {
			projId = dao.saveProject(dtoProj, null);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		assertNotNull("Project wasn't saved properly", projId);

		Randomisation rand = null;
		
		//use web-service to save the randomisation
		EslClient client = new EslClient();
		IProject p = null;
		try {
			p = client.retrieveProject(projId, null);
			//rand       = client.retrieveRandomisation(p.getRandomisation().getId(), null);
			rand = (Randomisation)p.getRandomisation();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		
		assertNotNull("No randomisation retrieved from the database", rand);
		
		List<IStrata> stratalist  = new ArrayList<IStrata>();
		List<String> stratavalues = new ArrayList<String>();
		stratavalues.add("a");
		stratavalues.add("b");
		List<String> sexvalues = new ArrayList<String>();
		sexvalues.add("female");
		sexvalues.add("male");
		List<String> locationvalues = new ArrayList<String>();
		locationvalues.add("manchester");
		locationvalues.add("liverpool");
		
		stratalist.add(new Strata("firstName", stratavalues));
		stratalist.add(new Strata("sex", sexvalues));
		stratalist.add(new Strata("city", locationvalues));
		rand.setStrata(stratalist);
		
		Map<String,String> treatments = new HashMap<String,String>();
		treatments.put("code", "Treatment name");
		treatments.put("code2", "Treatment name2");
		treatments.put("code3", "Treatment name3");
		rand.setTreatments(treatments);
		
		
		try {
			client.setupRandomisation(p, rand, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
		
	}
	
	
	/**
	 * Test method for {@link org.psygrid.esl.dao.EslSoapBindingImpl#createRandomise(org.psygrid.esl.model.dto.Randomisation random)}.
	 */
	public void testSetupRandomisation_twice() {
		String projectName = "EslSoapBindingImpl-setupRandomisation_twice";

		Long projId = null;
		
		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		try {
			projId = dao.saveProject(dtoProj, null);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		assertNotNull("Project wasn't saved properly", projId);

		Randomisation rand = null;
		
		//use web-service to save the randomisation
		EslClient client = new EslClient();
		IProject p = null;
		try {
			p = client.retrieveProject(projId, null);
			//rand       = client.retrieveRandomisation(p.getRandomisation().getId(), null);
			rand = (Randomisation)p.getRandomisation();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		
		assertNotNull("No randomisation retrieved from the database", rand);
		
		List<IStrata> stratalist  = new ArrayList<IStrata>();
		List<String> stratavalues = new ArrayList<String>();
		stratavalues.add("a");
		stratavalues.add("b");
		List<String> sexvalues = new ArrayList<String>();
		sexvalues.add("female");
		sexvalues.add("male");
	
		
		stratalist.add(new Strata("firstName", stratavalues));
		stratalist.add(new Strata("sex", sexvalues));

		rand.setStrata(stratalist);
		
		Map<String,String> treatments = new HashMap<String,String>();
		treatments.put("code", "Treatment name");
		treatments.put("code2", "Treatment name2");
		treatments.put("code3", "Treatment name3");
		rand.setTreatments(treatments);
		
		
		try {
			client.setupRandomisation(p, rand, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		boolean fail = true;
		try {
			IProject newProject = client.retrieveProject(projId, null);
			IRandomisation newrand = newProject.getRandomisation();
			client.setupRandomisation(newProject, newrand, null);
		}
		catch (org.psygrid.esl.services.RandomisationException e) {
			fail = false;
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
			//fail = false;
		}
		assertFalse("Duplicate randomizer exception should have been thrown.", fail);
	}
	
	

	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#retrieveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testAllocation() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-testAllocation";

		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);
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

		IProject wsProject  = null;
		Long wsSubjectId    = null;
		String subjectName  = projectName+"-NewSubject-"+helper.STUDY_NUMBER;
		IRandomisation rand = null;
		try {
			wsProject = client.retrieveProject(projId, null);
			rand = wsProject.getRandomisation();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't retrieved", wsProject);

		try {
			client.setupRandomisation(wsProject, rand, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
		ISubject wsSubject = factory.createSubject(subjectName);
		
		wsSubject.setGroup(wsProject.getGroups().get(0));
		wsSubject.setStudyNumber("Two - "+helper.STUDY_NUMBER);
		
		//required parameters for randomisation strata
		wsSubject.setSex("male");
		wsSubject.setCentreNumber("abc");
		//wsSubject.setRiskIssues("");
		
		//use web-service to save a new subject
		try {
			wsSubjectId = client.saveSubject(wsSubject, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Subject has no id", wsSubjectId);

		//as subject has been saved, web service can now be used to 
		//allocate a treatment arm to the subject
		try {
			client.randomiseSubject(wsSubject, null, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
		
		//use web-service to get the treatment allocation for a given subject
		String treatment = null;
		try {
			treatment = client.lookupRandomisationResult(wsProject, "Two - "+helper.STUDY_NUMBER, null);
		}
		catch (Exception ex) {
			ex.getStackTrace();
			fail("Exception: "+ex.toString());
		}
System.out.println("Treatment: "+treatment);
		assertNotNull("No treatment has been found", treatment);
	}
	
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#randomiseSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testAllocation_fail() {

		Long projId = null;
		String projectName = "EslSoapImplRand-testAllocation_fail";

		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);
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

		IProject wsProject  = null;
		Long wsSubjectId    = null;
		String subjectName  = projectName+"-NewSubject-"+helper.STUDY_NUMBER;
		IRandomisation rand = null;
		try {
			wsProject = client.retrieveProject(projId, null);
			rand = wsProject.getRandomisation();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't retrieved", wsProject);

		try {
			client.setupRandomisation(wsProject, rand, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
		ISubject wsSubject = factory.createSubject(subjectName);
		
		wsSubject.setGroup(wsProject.getGroups().get(0));
		wsSubject.setStudyNumber("Two - "+helper.STUDY_NUMBER);
		
		//required parameters for randomisation strata
		wsSubject.setSex("male");
		wsSubject.setCentreNumber("abc");
/*
		//use web-service to save a new subject
		try {
			wsSubjectId = client.saveSubject(wsSubject, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Subject has no id", wsSubjectId);
*/
	
		//attempt to allocate a treatment to a subject that hasn't been saved
		try {
			client.randomiseSubject(wsSubject, null, null);
		}
		catch (org.psygrid.esl.services.ESLServiceFault e) {
			//this is supposed to happen, so do nothing
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#randomiseSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testAllocation_fail2() {

		Long projId = null;
		String projectName = "EslSoapImplRand-testAllocation_fail2";

		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);
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

		IProject wsProject  = null;
		Long wsSubjectId    = null;
		String subjectName  = projectName+"-NewSubject-"+helper.STUDY_NUMBER;
		IRandomisation rand = null;
		try {
			wsProject = client.retrieveProject(projId, null);
			rand = wsProject.getRandomisation();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't retrieved", wsProject);

		//remove the randomisation
		wsProject.setRandomisation(null);
		try {
			client.saveProject(wsProject, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
	
		ISubject wsSubject = factory.createSubject(subjectName);
		
		wsSubject.setGroup(wsProject.getGroups().get(0));
		wsSubject.setStudyNumber("Two - "+helper.STUDY_NUMBER);
		
		//required parameters for randomisation strata
		wsSubject.setSex("male");
		wsSubject.setCentreNumber("abc");

		//use web-service to save a new subject
		try {
			wsSubjectId = client.saveSubject(wsSubject, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Subject has no id", wsSubjectId);

	boolean fault=false;
		//attempt to allocate a treatment, when no randomisation has been setup
		try {
			client.randomiseSubject(wsSubject, null, null);
		}
		catch (org.psygrid.esl.services.ESLServiceFault e) {
			//this is supposed to happen, so do nothing
			fault = true;
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
		assertTrue("No fault ESLServiceFault thrown", fault);
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#randomiseSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testAllocation_fail3() {

		Long projId = null;
		String projectName = "EslSoapImplRand-testAllocation_fail3";

		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);
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

		IProject wsProject  = null;
		Long wsSubjectId    = null;
		String subjectName  = projectName+"-NewSubject-"+helper.STUDY_NUMBER;
		IRandomisation rand = null;
		try {
			wsProject = client.retrieveProject(projId, null);
			rand = wsProject.getRandomisation();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't retrieved", wsProject);
		assertNotNull("Randomisation wasn't retrieved", rand);
		
		//don't setup the randomisation (although it's been saved
		//it's not yet been setup with the remote randomiser)
		
	
		ISubject wsSubject = factory.createSubject(subjectName);
		
		wsSubject.setGroup(wsProject.getGroups().get(0));
		wsSubject.setStudyNumber("Two - "+helper.STUDY_NUMBER);
		
		//required parameters for randomisation strata
		wsSubject.setSex("male");
		wsSubject.setCentreNumber("abc");

		//use web-service to save a new subject
		try {
			wsSubjectId = client.saveSubject(wsSubject, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Subject has no id", wsSubjectId);

		boolean fault = false;
		//attempt to allocate a treatment, when the randomisation has not 
		//been setup properly
		try {
			client.randomiseSubject(wsSubject, null, null);
		}
		catch (org.psygrid.esl.services.RandomisationException e) {
			//this is supposed to happen, so do nothing
			fault = true;
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertTrue("No RandomisationException was thrown", fault);
	}
	
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#retrieveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testAllocation_twice() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-testAllocation_twice";

		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);
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
		Long wsSubjectId   = null;
		String subjectName = projectName+"-NewSubject-"+helper.STUDY_NUMBER;
		IRandomisation rand = null;
		try {
			wsProject = client.retrieveProject(projId, null);
			rand = wsProject.getRandomisation();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't retrieved", wsProject);

		try {
			client.setupRandomisation(wsProject, rand, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
		ISubject wsSubject = factory.createSubject(subjectName);
		
		wsSubject.setGroup(wsProject.getGroups().get(0));
		wsSubject.setStudyNumber("Three - "+helper.STUDY_NUMBER);
		
		//required parameters for randomisation strata
		wsSubject.setSex("male");
		wsSubject.setCentreNumber("abc");
		
		//use web-service to save a new subject
		try {
			wsSubjectId = client.saveSubject(wsSubject, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertNotNull("Subject has no id", wsSubjectId);

		
		
		//as subject has been saved, web service can now be used to 
		//allocate a treatment arm to the subject
		try {
			client.randomiseSubject(wsSubject, null, null);
		}
	
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		} 
		//allocating a treatment again should not have an effect
		//and will also return true (as the subject has been randomised)
		try {
			client.randomiseSubject(wsSubject, null, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#retrieveSubject(org.psygrid.esl.model.dto.Subject)}.
	 */
	public void testAllocation_badstrata() {

		Long projId = null;
		String projectName = "EslSoapBindingImpl-testAllocation_badstrata";

		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);
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
		Long wsSubjectId   = null;
		String subjectName = projectName+"-NewSubject-"+helper.STUDY_NUMBER;
		IRandomisation rand = null;
		try {
			wsProject = client.retrieveProject(projId, null);
			rand = wsProject.getRandomisation();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Project wasn't retrieved", wsProject);

		try {
			client.setupRandomisation(wsProject, rand, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
		ISubject wsSubject = factory.createSubject(subjectName);
		
		wsSubject.setGroup(wsProject.getGroups().get(0));
		wsSubject.setStudyNumber("Two - "+helper.STUDY_NUMBER);
		
		//required parameters for randomisation strata
		wsSubject.setSex("male");
		
		/* centreNumber is a required strata for randomisation */
		//wsSubject.setCentreNumber("abc");
		
		boolean fail = true;
		//use web-service to save a new subject
		try {
			wsSubjectId = client.saveSubject(wsSubject, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		//as subject has been saved, web service can now be used to 
		//allocate a treatment arm to the subject
		try {
			client.randomiseSubject(wsSubject, null, null);
		}
		catch (org.psygrid.esl.services.RandomisationException e) {
			fail = false;
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
		assertFalse("Exception was not thown when allocating missing strata values.", fail);
	}
	
	/**
	 * Test method for {@link org.psygrid.esl.services.EslSoapBindingImpl#emergencyBreakIn(org.psygrid.esl.model.dto.Project)}.
	 */
	public void testEmergencyBreakIn() {
		
		String projectName = "EslSoapBindingImpl-emergencyBreakIn";

		Long projId = null;
		
		TestHelper helper = new TestHelper();
		IProject project  = helper.populateProject(projectName, factory);

		org.psygrid.esl.model.dto.Project dtoProj = project.toDTO();
		try {
			projId = dao.saveProject(dtoProj, null);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		assertNotNull("Project wasn't saved properly", projId);
		
		//use web-service to save the randomisation
		EslClient client   = new EslClient();
		IProject wsProject = null;
		try {
			wsProject = client.retrieveProject(projId, null);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		
		assertNotNull("No project retrieved from the database", wsProject);
			
		
		try {
			client.setupRandomisation(wsProject, wsProject.getRandomisation(), null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
		ISubject s = null;
		
		try {
			s = client.retrieveSubjectByStudyNumber(wsProject, helper.STUDY_NUMBER, null);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("No subject retrieved from the database", s);
		s.setSex("female");
		s.setCentreNumber("abc");
		
		Long sId = null;	
		try {
			sId = client.saveSubject(s, null);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		assertNotNull("No subject id returned", sId);
//		as subject has been saved, web service can now be used to 
		//allocate a treatment arm to the subject
		try {
			client.randomiseSubject(s, null, null);
		}
	
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		} 
		
		ISubject wsSubject = factory.createSubject(helper.STUDY_NUMBER+"-1");
		Long wsSubjectId = null;
		
		wsSubject.setGroup(wsProject.getGroups().get(0));
		
		//required parameters for randomisation strata
		wsSubject.setSex("male");
		wsSubject.setCentreNumber("abc");
		
		//use web-service to save a new subject, which will trigger treatment allocation
		try {
			wsSubjectId = client.saveSubject(wsSubject, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Subject has no id", wsSubjectId);
//		as subject has been saved, web service can now be used to 
		//allocate a treatment arm to the subject
		try {
			client.randomiseSubject(wsSubject, null, null);
		}
	
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		} 
		
		ISubject wsSubject2 = factory.createSubject(helper.STUDY_NUMBER+"-2");
		Long wsSubjectId2 = null;
		
		wsSubject2.setGroup(wsProject.getGroups().get(0));
		
		//required parameters for randomisation strata
		wsSubject2.setSex("male");
		wsSubject2.setCentreNumber("abc");
		
		//use web-service to save a new subject, which will trigger treatment allocation
		try {
			wsSubjectId2 = client.saveSubject(wsSubject2, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Subject 2 has no id", wsSubjectId2);
//		as subject has been saved, web service can now be used to 
		//allocate a treatment arm to the subject
		
		try {
			client.randomiseSubject(wsSubject2, null, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		} 
		
		ISubject wsSubject3 = factory.createSubject(helper.STUDY_NUMBER+"-3");
		Long wsSubjectId3 = null;
		
		wsSubject3.setGroup(wsProject.getGroups().get(1));
		
		//required parameters for randomisation strata
		wsSubject3.setSex("male");
		wsSubject3.setCentreNumber("abc");
		
		//use web-service to save a new subject, which will trigger treatment allocation
		try {
			wsSubjectId3 = client.saveSubject(wsSubject3, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Subject 3 has no id", wsSubjectId3);
		
		try {
			client.randomiseSubject(wsSubject3, null, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		} 
		
		Map<String, String> results = null;
		try {
			results = client.emergencyBreakIn(wsProject, null);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		
		
		System.out.println("Treatments: "+results);
		
		
		assertNotNull("No results found", results);
		assertEquals("Wrong number of subjects returned", results.size(), 4);
		assertNotNull("Original subject has no treatment", results.get(helper.STUDY_NUMBER));
		assertNotNull("Subject 1 has no treatment", results.get(helper.STUDY_NUMBER+"-1"));
		assertNotNull("Subject 2 has no treatment", results.get(helper.STUDY_NUMBER+"-2"));
		assertNotNull("Subject 3 has no treatment", results.get(helper.STUDY_NUMBER+"-3"));
	}
}
