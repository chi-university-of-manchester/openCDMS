package org.psygrid.data.sampletracking;

import java.util.List;

import junit.framework.TestCase;

import org.psygrid.data.sampletracking.ConfigInfo;
import org.psygrid.data.sampletracking.SampleInfo;
import org.psygrid.data.sampletracking.SampleTrackingService;
import org.psygrid.data.sampletracking.server.model.Action;
import org.psygrid.data.sampletracking.server.model.SampleTrackingDAO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SampleTrackingDAOTest extends TestCase {

	private final static String DAO_NAME = "sampleTrackingDAOTest";
	
	public void testSampleTrackingDAO() {
	    
        String[] paths = {"applicationContext.xml"};
		ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);
	    SampleTrackingDAO dao = (SampleTrackingDAO) ctx.getBean(DAO_NAME);
	    
	    Action action = new Action("TST","DESPATCHED","EMAIL","terry@smallblueworld.com","Sample Event","Your message here");
	    dao.saveAction(action);
	    List<Action> actions = dao.getActions("TST", "DESPATCHED");
	    assertEquals("should be one sample action",1,actions.size());
	    
	}



}





