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
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.psygrid.esl.dao.EslDAO;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IGroup;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.services.client.EslClient;
import org.psygrid.esl.test.TestHelper;


public class ConcurrencyTests extends TestCase {

	private EslDAO dao;
	private IFactory factory;
	private TestHelper helper = new TestHelper();
	protected ApplicationContext ctx = null;

	private IProject project = null;
	private Long projectId = null;

	public ConcurrencyTests() {
		String[] paths = {"applicationContext.xml"};
		ctx = new ClassPathXmlApplicationContext(paths);
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


	private class SaveSubjects extends TestRunnable {


		private ISubject subject = null;
		private String user = null;

		public SaveSubjects(ISubject subject, String user){

			this.subject = subject;
			this.user = user;
		}

		
		public void runTest() throws Throwable {
			//       System.out.println("Saving object: "+subject.getFirstName());
			EslClient client = new EslClient();

			try{
				synchronized (project) {
					project   = client.retrieveProject(projectId, null);
					subject.setGroup(project.getGroups().get(0));
					// dao.saveSubject(subject, user);
					client.saveSubject(subject, null);
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
				fail("Exception: "+ex);
			}

		}

	}

	private class SaveProject extends TestRunnable {


		private IProject project = null;
		private String user = null;


		public SaveProject(IProject project, String user){

			this.project = project;
			this.user = user;

		}

		
		public void runTest() throws Throwable {
			// System.out.println("Saving object: "+project.getProjectCode());
			EslClient client = new EslClient();
			try{
				client.saveProject(project, null);
			}
			catch(Exception ex){
				ex.printStackTrace();
				fail("Exception: "+ex);
			}

		}

	}


	public void testConcurrentSaveSubjects(){
		try{
			project = helper.populateProject("ConcurrentSaveSubjects", factory);
			projectId 	 = null;

			EslClient client = new EslClient();

			try {
				projectId = client.saveProject(project, null);
				project   = client.retrieveProject(projectId, null);
			}
			catch(Exception ex){
				ex.printStackTrace();
				fail("Exception: "+ex);
			}

			int nThreads = 35;


			//populate the test runner with SaveSubject objects
			TestRunnable[] trs = new TestRunnable[nThreads];
			for ( int i=0; i<nThreads; i++ ){
				String sn = helper.getUnique();
				ISubject s = factory.createSubject("subject "+i);
				s.setFirstName("subject "+i);
				s.setStudyNumber(sn+"-"+Integer.toString(i));

				s.setGroup(project.getGroups().get(0));

				sn = null;

				trs[i] = new SaveSubjects(s, "user "+i);
			}

			MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);

			//kickstarts the MTTR & fires off threads
			mttr.runTestRunnables();

		}
		catch(Throwable ex){
			ex.printStackTrace();
			fail("Exception: "+ex);
		}
	}

	private class SaveGroup extends TestRunnable {


		private IGroup group = null;
		private String user = null;


		public SaveGroup(IGroup group, String user){

			this.group = group;
			this.user = user;

		}

		
		public void runTest() throws Throwable {

			EslClient client = new EslClient();
			try{
				//retrieve the latest version of the project and add the group to it
				synchronized (project) {
					project   = client.retrieveProject(projectId, null);
					project.setGroup(group);
					client.saveProject(project, null);
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
				fail("Exception: "+ex);
			}

		}

	}
	public void testConcurrentSaveGroups(){
		try{
			project = helper.populateProject("ConcurrentSaveGroups", factory);
			projectId 	 = null;

			EslClient client = new EslClient();

			try {
				projectId = client.saveProject(project, null);
				project   = client.retrieveProject(projectId, null);
			}
			catch(Exception ex){
				ex.printStackTrace();
				fail("Exception: "+ex);
			}

			int nThreads = 30;

			//populate the test runner with SaveSubjects
			TestRunnable[] trs = new TestRunnable[nThreads];
			for ( int i=0; i<nThreads; i++ ){
				String sn = helper.getUnique();
				IGroup g = factory.createGroup("group "+i);

				g.setGroupCode(sn);
				g.setGroupName("group "+i);
				sn = null;

				trs[i] = new SaveGroup(g, "user "+i);
			}

			MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);

			//kickstarts the MTTR & fires off threads
			mttr.runTestRunnables();

		}
		catch(Throwable ex){
			ex.printStackTrace();
			fail("Exception: "+ex);
		}
	}
	
	private class AllocateTreatment extends TestRunnable {

		private ISubject subject = null;

		public AllocateTreatment(ISubject subject){
			this.subject = subject;
		}

		
		public void runTest() throws Throwable {

			EslClient client = new EslClient();
			try{
				//retrieve the latest version of the project and add the group to it
					client.randomiseSubject(subject, null, null);
					//System.out.println(client.lookupRandomisationResult(project, subject.getStudyNumber(), null));
			}
			catch(Exception ex){
				ex.printStackTrace();
				fail("Exception: "+ex);
			}

		}

	}
	public void testAllocateTreatments(){
		
		fail("This test needs to be fixed as Subjects are not now sent as part of a Group DTO");

		try{
			project = helper.populateProject("ConcurrentAllocateTreatments", factory);
			projectId 	 = null;

			EslClient client = new EslClient();

			try {
				projectId = client.saveProject(project, null);
				project   = client.retrieveProject(projectId, null);
			}
			catch(Exception ex){
				ex.printStackTrace();
				fail("Exception: "+ex);
			}

			int nThreads = 10;

			IGroup g = project.getGroups().get(0);
			for ( int i=0; i<nThreads; i++ ){
				ISubject s = factory.createSubject("subject "+i);
				s.setStudyNumber(TestHelper.getUnique());
				s.setCentreNumber("abc");
				s.setSex("male");
				g.setSubject(s);
			}
			
			try {
				projectId = client.saveProject(project, null);
				project   = client.retrieveProject(projectId, null);
				client.setupRandomisation(project, project.getRandomisation(), null);
			}
			catch(Exception ex){
				ex.printStackTrace();
				fail("Exception: "+ex);
			}

			g = project.getGroups().get(0);
			//populate the test runner with SaveSubjects
			TestRunnable[] trs = new TestRunnable[nThreads];
			for ( int i=0; i<nThreads; i++ ){
				ISubject s = g.getSubjects().get(i);
				trs[i] = new AllocateTreatment(s);
			}

			MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);

			//kickstarts the MTTR & fires off threads
			mttr.runTestRunnables();

		}
		catch(Throwable ex){
			ex.printStackTrace();
			fail("Exception: "+ex);
		}
	}
}
