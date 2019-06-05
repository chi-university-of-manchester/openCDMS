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

package org.psygrid.esl.test.randomise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.psygrid.common.email.Email;
import org.psygrid.esl.dao.DAOException;
import org.psygrid.esl.dao.EslDAO;
import org.psygrid.esl.model.IAddress;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.IGroup;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.IRandomisation;
import org.psygrid.esl.model.IStrata;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.hibernate.Randomisation;
import org.psygrid.esl.model.hibernate.Strata;
import org.psygrid.esl.randomise.EmailType;
import org.psygrid.esl.randomise.IRemoteRandomiser;
import org.psygrid.esl.randomise.RandomisationException;
import org.psygrid.esl.randomise.RemoteRandomiser;
import org.psygrid.esl.test.TestHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Lucy Bridges
 *
 */
public class RemoteTest extends TestCase {
	
	private final String RDMZR_NAME = "RemoteTest-"+(TestHelper.getUnique());
	protected org.psygrid.esl.model.IFactory factory;
	protected ApplicationContext ctx = null;
	IRandomisation rand = new Randomisation();
	EslDAO dao = null;
	
	
	public RemoteTest() {
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
		factory = (IFactory) ctx.getBean("factory");
		dao = (EslDAO) ctx.getBean("eslClientDAOService");
	}


	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.psygrid.esl.randomise.RemoteRandomiser#saveRandomisation(org.psygrid.esl.model.IRandomisation)}.
	 */
	public void testSaveRandomisation() {
		fail("This test needs to be fixed as Subjects are not now sent as part of a Group DTO");

		/* create a new randomisation */
		
		rand.setName(RDMZR_NAME);
		List<IStrata> stratalist  = new ArrayList<IStrata>();
		List<String> sexvalues = new ArrayList<String>();
		sexvalues.add("female");
		sexvalues.add("male");
		List<String> locationvalues = new ArrayList<String>();
		locationvalues.add("manchester");
		locationvalues.add("liverpool");
		
		stratalist.add(new Strata("sex", sexvalues));
		stratalist.add(new Strata("city", locationvalues));
		rand.setStrata(stratalist);
		
		Map<String,String> treatments = new HashMap<String,String>();
		treatments.put("code", "Treatment name");
		treatments.put("code2", "Treatment name2");
		treatments.put("code3", "Treatment name3");
		rand.setTreatments(treatments);
		
		Email email = factory.createEmail();
		email.setBody("text here");
		email.setSubject("Remote Test");
		Map<String, Email> emails = new HashMap<String,Email>();
		emails.put(EmailType.DECISION.type(), email);
		rand.setEmails(emails);
		
		/* Setup a new randomisation */
		IRemoteRandomiser rr = null; 
		
		try {
			rr = new RemoteRandomiser();
			rr.saveRandomisation(rand, null);
		}
		catch (RandomisationException rex) {
			rex.getStackTrace();
			fail("Exception: "+rex.toString());
		}
        catch (Exception naf) {
            naf.getStackTrace();
            fail("Exception: "+naf.toString());
        }
		
		IProject project = factory.createProject(TestHelper.getUnique());
		project.setProjectCode(TestHelper.getUnique());
		project.setRandomisation(rand);
		
		IGroup group = factory.createGroup("test");
		group.setGroupCode(TestHelper.getUnique());
		project.setGroup(group);
		
		/* allocate a treatment to a subject */
		String studyNumber = TestHelper.getUnique();
		ISubject subject = factory.createSubject("Test 1");
		subject.setSex("male");
		subject.setFirstName("a");
		subject.setStudyNumber(studyNumber);

		IAddress address = factory.createAddress();
		address.setCity("manchester");
		subject.setAddress(address);
		
		group.setSubject(subject);
		Long projectId = null;
		try {
			projectId = dao.saveProject(project.toDTO(), "Remote Test");
			rr.allocateTreatment(rand, subject, null, null);
		}
		catch (Exception rex) {
			rex.getStackTrace();
			fail("Exception: "+rex.toString());
		}
		//should quietly ignore and not allocate a treatment twice
		try {
			rr.allocateTreatment(rand, subject, null, null);
		}
		catch (Exception rex) {
			rex.getStackTrace();
			fail("Exception: "+rex.toString());
		}
		String treatment = null; 
		
		try {
			project = dao.getProject(projectId).toHibernate();
			subject = dao.getSubject(project.toDTO(), subject.getStudyNumber()).toHibernate();
		}
		catch (DAOException e) {
			e.printStackTrace();
			fail("Exception: "+e.toString());
		}
		try {
			treatment = rr.getAllocation(RDMZR_NAME, subject, null);
		}
		catch (RandomisationException rex) {
			//rex.getStackTrace();
			rex.printStackTrace();
			fail("Exception: "+rex.toString());
		}
        catch (Exception naf) {
            naf.getStackTrace();
            fail("Exception: "+naf.toString());
        }
		assertNotNull("No treatment has been found", treatment);
		System.out.println("Treatment is: "+treatment);
	}

}
