package org.psygrid.data.clintouch;

import org.psygrid.common.sms.SMSException;

import org.psygrid.common.sms.SMSMessage;
import org.psygrid.data.clintouch.util.CreateSMSSettings;
import org.psygrid.data.clintouch.util.PsyGridSMSSenderImplStub;
import org.psygrid.data.clintouch.util.StubCommonClasses;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.Record;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ClinTouchServiceTest {
	private ClinTouchServiceImpl clinTouchService = new ClinTouchServiceImpl();
	private Questionnaire questionnaire = new Questionnaire();
	private MessageHandler messageHandler;
	private PsyGridSMSSenderImplStub smsSenderStub;
	private DataSet dataSet;
	private Record record;
	
	private static final String PHONE_NUMBER = "1";
	
	// The first question in question set one is reversed. Refer to the Questionnaire class for details.
	private final String EXPECTED_RESULT_INVALID_ANSWER = "8";
	
	@BeforeClass
	public void initialise() {
		questionnaire.setAlarmTimes(StubCommonClasses.stubAlarmTimes());
		clinTouchService.setQuestionnaire(questionnaire);
		
		MessageHandlerTest messageHandlerTest = new MessageHandlerTest();
		messageHandler = messageHandlerTest.getMessageHandlerWithStubs();
		clinTouchService.setMessageHandler(messageHandler);
		
		smsSenderStub = messageHandlerTest.getSMSSenderStub();
		smsSenderStub.setSettings(CreateSMSSettings.getNewSMSSettings());
		
		clinTouchService.setaaqc(StubCommonClasses.stubAAQCWrapper());
		
		clinTouchService.setParticipantInterface(StubCommonClasses.stubParticipantInterface());
	}
	
	@BeforeTest
	public void setup() {
		dataSet = StubCommonClasses.setupDataSet(QuestionnaireTest.FIRST_QUESTION_TEXT, QuestionnaireTest.SECOND_QUESTION_TEXT);
		record = StubCommonClasses.setupRecord(dataSet);
		clinTouchService.setRepository(StubCommonClasses.stubRepository(record, dataSet));
	}
	
	@Test
	public void testInvalidAnswer() {
		clinTouchService.messageReceived(PHONE_NUMBER, " ");
		BasicResponse response = getResponse();
		assert response.getValue().getValueAsString().equals(EXPECTED_RESULT_INVALID_ANSWER);
	}
	
/*	@Test
	public void retrieveQuestionFromDatabaseAndSend() throws SMSException {
		clinTouchService.run();
		smsSenderStub.verifyExpectedMessage(createExpectedMessage(PHONE_NUMBER, QuestionnaireTest.FIRST_QUESTION_TEXT));
		smsSenderStub.verifyExpectedMessage(createExpectedMessage(PHONE_NUMBER, QuestionnaireTest.SECOND_QUESTION_TEXT));		
	}*/
	
	private SMSMessage createExpectedMessage(String phoneNumber, String message) throws SMSException {
		SMSMessage expectedMessage = new SMSMessage();
		expectedMessage.setRecipientNumber(phoneNumber);
		expectedMessage.setMessage(message);
		
		return expectedMessage;
	}
	
	private BasicResponse getResponse() {
		Document document = dataSet.getDocument(QuestionnaireTest.QUESTION_SET_ONE_DOCUMENT_INDEX); 
		Record recordResult = StubCommonClasses.getRecordResult();
		recordResult.attach(dataSet);
		DocumentInstance documentInstance = recordResult.getDocumentInstances(document).get(0);
		BasicResponse response = (BasicResponse)documentInstance.getResponses(document.getEntry(0)).get(0);
		
		return response;
	}
	
/*	private Repository stubRepository() {
		final Repository mockRepository = mock(Repository.class);
		final DataSetDTO dataSet = StubCommonClasses.setupDataSet(QuestionnaireTest.FIRST_QUESTION_TEXT, QuestionnaireTest.SECOND_QUESTION_TEXT);
		ParticipantInterfaceTest participantInterfaceTest = new ParticipantInterfaceTest();
		RecordDTO[] records = new RecordDTO[2];
		records = participantInterfaceTest.getRecordListTwoRecordsOneDueOneNot().toArray(records);
		
		try {
			when(mockRepository.getDataSetSummaryWithDocs(anyString(), anyString())).thenReturn(dataSet);
			when(mockRepository.getRecords(anyLong(), anyString())).thenReturn(records);
		} catch (final RemoteException e) {
			e.printStackTrace();
		} catch (final RepositoryServiceFault e) {
			e.printStackTrace();
		} catch (final RepositoryNoSuchDatasetFault e) {
			e.printStackTrace();
		}
		
		return mockRepository;
	}*/
}
