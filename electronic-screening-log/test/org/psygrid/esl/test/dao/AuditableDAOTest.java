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

import java.util.Date;

import junit.framework.TestCase;

import org.psygrid.esl.dao.EslDAO;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IProvenanceChange;
import org.psygrid.esl.model.IProvenanceLog;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.hibernate.Subject;
import org.psygrid.esl.test.TestHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Lucy Bridges
 *
 */
public class AuditableDAOTest extends TestCase {

	protected EslDAO dao;
	protected org.psygrid.esl.model.IFactory factory;
	protected ApplicationContext ctx = null;

	public AuditableDAOTest() {
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

	/**
	 * Test method for {@link org.psygrid.esl.dao.hibernate.AuditableDAOHibernate#getChange(org.psygrid.esl.model.IAuditable, java.util.Date, java.lang.String)}.
	 */
	public final void testGetChangeIAuditableVeryLongString() {
		
		IProject project = setupProject();
		ISubject updatedSubject = setupSubject(project);

		org.psygrid.esl.model.dto.ProvenanceLog log = null;

		try {
			log = dao.getHistory(updatedSubject);
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
			change = dao.getChange(updatedSubject, timestamp, "riskIssues");
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("No change found", change);
		assertEquals("Field name doesn't match requested field", change.getField(), "riskIssues");
		
		System.out.println("Change made is: ");
		System.out.println(change.getField()+"- from: "+change.getPrevValue()+" to : "+change.getNewValue());

	}
	
	private IProject setupProject() {

		Long projId = null;
		IProject p  = null;

		TestHelper helper = new TestHelper();

		IProject project = helper.populateProject("AuditableDAOTest", factory);

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

		//Make an update to an auditable object
		p.setProjectName(p.getProjectName()+"-updated");

		Long newprojId = null;
		IProject updatedProject = null;
		try {
			newprojId = dao.saveProject(p.toDTO(), null);
			updatedProject = dao.getProject(newprojId).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}

		assertNotNull("newprojId is null", newprojId);
		assertNotNull("updatedProject id is null", updatedProject.getId());

		return updatedProject;
	}
	
	private ISubject setupSubject(IProject project) {
		Long subjectId = null;
		ISubject s     = new Subject();
		TestHelper helper = new TestHelper();
		
		s.setFirstName("Bob");
		s.setRiskIssues("This subject has a lot of risk issues. Greater than 255 in fact. " +
				"Filler FillerFillerFillerFillerFillerFiller1 FillerFillerFillerFillerFillerFiller" +
				"Filler FillerFillerFillerFillerFillerFillerFillerFillerFillerFiller2" +
				"FillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFillerFiller3");
		s.setStudyNumber(helper.STUDY_NUMBER);
		s.setGroup(project.getGroups().get(0));
		
		try {
			subjectId = dao.saveSubject(s, null);
			s = dao.getSubject(subjectId).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		assertNotNull("subjectId is null", subjectId);
		assertNotNull("subject id is null", s.getId());
		
		s.setRiskIssues("The risk issues have been updated. "+
				" Blah Blah BlahBlahBlahBlah BlahBlah Blah Blah BlahBlah" +
				"BlahBlah BlahBlah Blah Blah BlahBlahBlahBlah1 BlahBlah Blah Blah BlahBlahBlahBlah BlahBlah Blah Blah " +
		"BlahBlahBlahBlah BlahBlah Blah Blah2 BlahBlahBlahBlah BlahBlahBlahBlah BlahBlah3");

		ISubject updatedSubject = null;
		try {
			subjectId = dao.saveSubject(s, null);
			updatedSubject = dao.getSubject(subjectId).toHibernate();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception: "+ex.toString());
		}
		assertNotNull("subjectId is null", subjectId);
		assertNotNull("subject id is null", updatedSubject.getId());
		
		return updatedSubject;
	}
}
