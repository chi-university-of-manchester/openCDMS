
package org.psygrid.data.importing;

import java.rmi.RemoteException;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.sampletracking.ConfigInfo;
import org.psygrid.data.sampletracking.SampleInfo;
import org.psygrid.data.sampletracking.SampleTrackingService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ImportingServiceTest extends TestCase {

	private static Log logger = LogFactory.getLog(ImportingServiceTest.class);

	private final static String SERVICE_NAME = "importService";
	
	public void testImportTypes() throws RemoteException, RepositoryServiceFault {
	    
        String[] paths = {"applicationContext.xml"};
		ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);
	    ImportService service = (ImportService) ctx.getBean(SERVICE_NAME);

	    String saml = null;

	    String[] importTypes = service.getImportTypes("PRC",saml);
	    
	    assertEquals(3, importTypes.length);
	}

	public void testImportRequest() throws RemoteException, RepositoryServiceFault {
	    
        String[] paths = {"applicationContext.xml"};
		ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);
	    ImportService service = (ImportService) ctx.getBean(SERVICE_NAME);
	    
	    String saml = null;

	    ImportData data = new ImportData("PRC","c:\\bing\\bong.csv","1234567890","type1","user1");
	    service.requestImport(data,saml);
	    ImportStatus[] statuses = service.getImportStatuses("PRC",saml);
	    assertEquals(1, statuses.length);
	}

}





