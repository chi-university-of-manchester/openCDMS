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

package org.psygrid.esl.test.model.hibernate;

import java.util.Date;

import junit.framework.TestCase;

import org.psygrid.esl.dao.DAOException;
import org.psygrid.esl.dao.DuplicateObjectException;
import org.psygrid.esl.dao.EslDAO;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IGroup;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.test.TestHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Lucy Bridges
 *
 */
public class NonUniqueGroupCodesTest extends TestCase {

	protected EslDAO dao;
	protected org.psygrid.esl.model.IFactory factory;
	protected ApplicationContext ctx = null;
	protected TestHelper helper = new TestHelper();
	
	public NonUniqueGroupCodesTest() {
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
	 * Test method for {@link org.psygrid.esl.hibernate.Group.setGroupCode()}.
	 */
	public void testGroupCodes_success() {
		
		//create a project
		IProject project = factory.createProject("NonUniqueGroupCodesTest-"+helper.getUnique());
		project.setProjectCode(TestHelper.getUnique());
		//add groups
		IGroup g1 = factory.createGroup("code 1");
		g1.setGroupCode("1-"+TestHelper.getUnique());
		IGroup g2 = factory.createGroup("code 2");
		g2.setGroupCode("2-"+TestHelper.getUnique());
		IGroup g3 = factory.createGroup("code 3");
		g3.setGroupCode("3-"+TestHelper.getUnique());
		
		project.setGroup(g1);
		project.setGroup(g2);
		project.setGroup(g3);
		
		Long id = null;
		try {
			id = dao.saveProject(project.toDTO(), "NonUniqueGroupCodesTest");
		}
		catch (DAOException e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project has no id", id);
		
	}

	/**
	 * Test method for {@link org.psygrid.esl.hibernate.Group.setGroupCode()}.
	 */
	public void testGroupCodes_name() {

		//create a project
		IProject project = factory.createProject("NonUniqueGroupCodesTest.groupCodes_success");
		project.setProjectCode(TestHelper.getUnique());
		//add groups
		IGroup g1 = factory.createGroup("code 1");
		g1.setGroupCode("1-"+TestHelper.getUnique());
		IGroup g2 = factory.createGroup("code 2");
		g2.setGroupCode("2-"+TestHelper.getUnique());
		IGroup g3 = factory.createGroup("code 3");
		g3.setGroupCode("3-"+TestHelper.getUnique());
		
		//add a group with an existing group name but different code
		IGroup g4 = factory.createGroup("code 3");
		g4.setGroupCode("4-"+TestHelper.getUnique());
		
		project.setGroup(g1);
		project.setGroup(g2);
		project.setGroup(g3);
		project.setGroup(g4);
		
		Long id = null;
		try {
			id = dao.saveProject(project.toDTO(), "NonUniqueGroupCodesTest");
		}
		catch (DAOException e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project has no id", id);
		
	}

	/**
	 * Test method for {@link org.psygrid.esl.hibernate.Group.setGroupCode()}.
	 */
	public void testGroupCodes_fail() {

		//create a project
		IProject project = factory.createProject("NonUniqueGroupCodesTest.groupCodes_fail");
		project.setProjectCode(TestHelper.getUnique());
		//add groups
		IGroup g1 = factory.createGroup("code 1");
		g1.setGroupCode("1");
		IGroup g2 = factory.createGroup("code 2");
		g2.setGroupCode("2");
		IGroup g3 = factory.createGroup("code 3");
		String code3 = "3";
		g3.setGroupCode(code3);
		
		//add a group with an existing code and catch DuplicateObjectException
		IGroup g4 = factory.createGroup("code 4");
		g4.setGroupCode(code3);
		
		project.setGroup(g1);
		project.setGroup(g2);
		project.setGroup(g3);
		project.setGroup(g4);
		
		Long id = null;
		boolean error = false;
		try {
			id = dao.saveProject(project.toDTO(), "NonUniqueGroupCodesTest");
		}
		catch (DuplicateObjectException e) {
			error = true;
		}
		catch (DAOException e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}

		assertTrue("Error should be thrown", error);	
	}

	/*setup several projects and ensure that different groups are saved with same code*/
	
	/**
	 * Test method for {@link org.psygrid.esl.hibernate.Group.setGroupCode()}.
	 */
	public void testGroupCodes_two() {

		//create a project
		IProject project = factory.createProject("NonUniqueGroupCodesTest.groupCodes_two");
		project.setProjectCode(TestHelper.getUnique());
		//add groups
		IGroup g1 = factory.createGroup("code 1");
		g1.setGroupCode("1");
		IGroup g2 = factory.createGroup("code 2");
		g2.setGroupCode("2");
		IGroup g3 = factory.createGroup("code 3");
		String code3 = "3-"+TestHelper.getUnique();
		g3.setGroupCode(code3);
		
		project.setGroup(g1);
		project.setGroup(g2);
		project.setGroup(g3);
				
		Long id = null;
		try {
			id = dao.saveProject(project.toDTO(), "NonUniqueGroupCodesTest");
		}
		catch (DAOException e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project has no id", id);

		
		//create a second project
		IProject p2 = factory.createProject("NonUniqueGroupCodesTest.groupCodes_two");
		p2.setProjectCode(TestHelper.getUnique());
		//add groups
		IGroup g4 = factory.createGroup("code 1");
		g4.setGroupCode("1");		//same code as previous group - should be allowed as it's for different project
		IGroup g5 = factory.createGroup("code 2");
		g5.setGroupCode("2");
		IGroup g6 = factory.createGroup("code 3");
		String code6 = "3-"+TestHelper.getUnique();
		g6.setGroupCode(code6);
		
		p2.setGroup(g1);
		p2.setGroup(g2);
		p2.setGroup(g3);
				
		Long id2 = null;
		try {
			id2 = dao.saveProject(p2.toDTO(), "NonUniqueGroupCodesTest");
		}
		catch (DAOException e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Second project has no id", id2);
	}

	/**
	 * Test method for {@link org.psygrid.esl.hibernate.Group.setGroupCode()}.
	 */
	public void testGroupCodes_two_fail() {

		//create a project
		IProject project = factory.createProject("NonUniqueGroupCodesTest.groupCodes_two_fail");
		project.setProjectCode(TestHelper.getUnique());
		//add groups
		IGroup g1 = factory.createGroup("code 1");
		g1.setGroupCode("1");
		IGroup g2 = factory.createGroup("code 2");
		g2.setGroupCode("2");
		IGroup g3 = factory.createGroup("code 3");
		String code3 = "3-"+TestHelper.getUnique();
		g3.setGroupCode(code3);
		
		project.setGroup(g1);
		project.setGroup(g2);
		project.setGroup(g3);
				
		Long id = null;
		try {
			id = dao.saveProject(project.toDTO(), "NonUniqueGroupCodesTest");
		}
		catch (DAOException e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project has no id", id);

		
		//create a second project
		IProject p2 = factory.createProject("NonUniqueGroupCodesTest.groupCodes_two_fail");
		p2.setProjectCode(TestHelper.getUnique());
		//add groups
		IGroup g4 = factory.createGroup("code 1");
		g4.setGroupCode("1");		//same code as previous group - should be allowed as it's for different project
		IGroup g5 = factory.createGroup("code 2");
		g5.setGroupCode("2");
		IGroup g6 = factory.createGroup("code 3");
		String code6 = "3-"+TestHelper.getUnique();
		g6.setGroupCode(code6);
		
		p2.setGroup(g1);
		p2.setGroup(g2);
		p2.setGroup(g3);
				
		Long id2 = null;
		try {
			id2 = dao.saveProject(p2.toDTO(), "NonUniqueGroupCodesTest");
		}
		catch (DAOException e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Second project has no id", id2);
		
		boolean error = false;
		try {
			IProject p3 = dao.getProject(id).toHibernate();
			IGroup newGroup = factory.createGroup("newgroup");
			newGroup.setGroupCode("1");		//duplicate within the project, so will cause saveProject to fail
			p3.setGroup(newGroup);
			dao.saveProject(p3.toDTO(), "NonUniqueGroupCodesTest");
		}
		catch (DuplicateObjectException e) {
			error = true;
		}
		catch (DAOException e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		
		assertTrue("Duplicate Group has not been caught", error);
	}
	
	
	/**
	 * Test method for {@link org.psygrid.esl.hibernate.Group.setGroupCode()}.
	 */
	public void testGroupCodes_add() {
		
		//create a project
		IProject project = factory.createProject("NonUniqueGroupCodesTest-add");
		project.setProjectCode(TestHelper.getUnique());
		//add groups
		IGroup g1 = factory.createGroup("code 1");
		g1.setGroupCode("1-"+TestHelper.getUnique());
		IGroup g2 = factory.createGroup("code 2");
		g2.setGroupCode("2-"+TestHelper.getUnique());
		IGroup g3 = factory.createGroup("code 3");
		g3.setGroupCode("3-"+TestHelper.getUnique());
		
		project.setGroup(g1);
		project.setGroup(g2);
		project.setGroup(g3);
		
		Long id = null;
		IProject newP = null;
		try {
			id = dao.saveProject(project.toDTO(), "NonUniqueGroupCodesTest");
			newP = dao.getProject(id).toHibernate();
			IGroup g4 = factory.createGroup("new- code 4");
			g4.setGroupCode("4");
			newP.setGroup(g4);

			id = dao.saveProject(newP.toDTO(), "NonUniqueGroupCodesTest");
		}
		catch (DAOException e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project has no id", id);
		
		
		
	/*	try {
			
		}
		catch (DAOException e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		assertNotNull("Project has no id", id);
		*/
	}

}
