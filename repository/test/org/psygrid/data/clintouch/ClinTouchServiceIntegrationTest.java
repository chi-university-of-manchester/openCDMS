package org.psygrid.data.clintouch;

import junit.framework.Assert;

import org.psygrid.common.sms.SMSException;
import org.psygrid.data.clintouch.util.StubCommonClasses;
import org.psygrid.data.repository.RepositoryServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Similar to ClinTouchServiceTest but uses the real classes rather than stubs.
 * @author Matt Machin
 *
 */
//@Test(groups = {"integration-tests"})
public class ClinTouchServiceIntegrationTest {
	private ClinTouchServiceImpl clinTouchService = new ClinTouchServiceImpl();
	private Questionnaire questionnaire = new Questionnaire();
	private MessageHandler messageHandler;
	
	//@BeforeClass
	public void initialise() {
		clinTouchService.setQuestionnaire(questionnaire);
		clinTouchService.setaaqc(StubCommonClasses.stubAAQCWrapper());
			
		MessageHandlerIntegrationTest messageHandlerIntegrationTest = new MessageHandlerIntegrationTest();
		messageHandler = messageHandlerIntegrationTest.setupMessageHandler();
		clinTouchService.setMessageHandler(messageHandler);
		
		ApplicationContext applicationContext = new FileSystemXmlApplicationContext("test\\applicationContext.xml");
		
		RepositoryServiceImpl serviceImpl = (RepositoryServiceImpl) applicationContext.getBean("repositoryServiceImpl");
		serviceImpl.setAccessControl(StubCommonClasses.stubAccessEnforcementFunction());
		serviceImpl.setLogHelper(StubCommonClasses.stubAuditLogger());
		clinTouchService.setRepository(serviceImpl);
	}
	
	//@Test
	public void retrieveQuestionFromDatabaseAndSend() throws SMSException {
		int previousCredit = messageHandler.getPsygridSMSSenderImpl().checkCredit();
		clinTouchService.run();
//		Assert.assertEquals(previousCredit - 1, messageHandler.getPsygridSMSSenderImpl().checkCredit());
	}
}
