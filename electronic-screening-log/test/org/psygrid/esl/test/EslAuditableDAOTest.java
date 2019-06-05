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

import org.psygrid.esl.dao.EslDAO;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IProvenanceLog;
import org.psygrid.esl.model.IProvenanceChange;
import org.psygrid.esl.test.TestHelper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

import junit.framework.TestCase;

/**
 * @author Lucy Bridges
 *
 */
public class EslAuditableDAOTest extends TestCase {

	protected EslDAO dao;
	protected org.psygrid.esl.model.IFactory factory;
	protected ApplicationContext ctx = null;

	public EslAuditableDAOTest() {
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
	 * Test method for {@link org.psygrid.esl.dao.hibernate.AuditableDAOHibernate#getHistory(org.psygrid.esl.model.IAuditable)}.
	 */
	public final void testGetHistory() {

		IProject updatedProject = setupProject();

		org.psygrid.esl.model.dto.ProvenanceLog log = null;

		try {
			log = dao.getHistory(updatedProject);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Provenance Log not returned", log);

		IProvenanceLog iLog = log.toHibernate();

		System.out.println("Project was created on "+iLog.getCreated()+" by "+iLog.getCreatedBy());
		System.out.println("So far, the following changes have been made: "+ iLog.getProvenanceChange());

	}

	/**
	 * Test method for {@link org.psygrid.esl.dao.hibernate.AuditableDAOHibernate#getChange(org.psygrid.esl.model.IAuditable, java.util.Date)}.
	 */
	public final void testGetChangeIAuditableDate() {

		IProject updatedProject = setupProject();

		org.psygrid.esl.model.dto.ProvenanceLog log = null;

		try {
			log = dao.getHistory(updatedProject);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Provenance Log not returned", log);

		IProvenanceLog iLog = log.toHibernate();

		IProvenanceChange provChange = iLog.getProvenanceChange().get(0);

		Date timestamp = provChange.getTimestamp();

		org.psygrid.esl.model.dto.Change[] changes = null;
		try {
			changes = dao.getChanges(updatedProject, timestamp);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertEquals("Incorrect number of changes made", changes.length, 1);

		System.out.println("Changes made are: ");
		for (org.psygrid.esl.model.dto.Change c : changes) {
			System.out.println(c.getField()+"- from: "+c.getPrevValue()+" to : "+c.getNewValue());
		}

	}

	/**
	 * Test method for {@link org.psygrid.esl.dao.hibernate.AuditableDAOHibernate#getChange(org.psygrid.esl.model.IAuditable, java.util.Date, java.lang.String)}.
	 */
	public final void testGetChangeIAuditableDateString() {

		IProject updatedProject = setupProject();

		org.psygrid.esl.model.dto.ProvenanceLog log = null;

		try {
			log = dao.getHistory(updatedProject);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("Provenance Log not returned", log);

		IProvenanceLog iLog = log.toHibernate();

		IProvenanceChange provChange = iLog.getProvenanceChange().get(0);

		Date timestamp = provChange.getTimestamp();

		org.psygrid.esl.model.dto.Change change = null;
		try {
			change = dao.getChange(updatedProject, timestamp, "projectName");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("No change found", change);
		assertEquals("Field name doesn't match requested field", change.getField(), "projectName");

		System.out.println("Change made is: ");
		System.out.println(change.getField()+"- from: "+change.getPrevValue()+" to : "+change.getNewValue());

	}

	private IProject setupProject() {
		try {
			IProject project = dao.getProject("ED2").toHibernate();
			return project;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		return null;
	}
}
