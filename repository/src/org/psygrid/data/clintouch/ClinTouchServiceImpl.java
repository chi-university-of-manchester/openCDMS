package org.psygrid.data.clintouch;

import java.net.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.sms.SMSException;
import org.psygrid.data.model.dto.RecordDTO;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.repository.Repository;
import org.psygrid.data.repository.RepositoryNoSuchDatasetFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.utils.wrappers.AAQCWrapper;
import org.psygrid.esl.model.ISubject;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;

/**
 * @author Matt Machin
 * @version 1.0
 * @created 23-Aug-2011 15:09:49
 * 
 * Note: When looking at the setup of this service, pay close attention to the transaction configuration in 
 * applicationContext.xml. 
 */
public class ClinTouchServiceImpl implements ClinTouchService {
	private static Log sLog = LogFactory.getLog(ClinTouchServiceImpl.class);
	
	private static final String PROJECT_CODE = "CT";
	
	private static final String QUESTIONNAIRE_COMPLETED_MESSAGE = "That is the end of the questionnaire. Thank you for answering the questions";
	private static final String QUESTIONNAIRE_TIMED_OUT_MESSAGE = "You have run out of time to answer the questions";
	
	private static final String DEMO_QUESTIONNAIRE_1 = "demo1";
	private static final String DEMO_QUESTIONNAIRE_2 = "demo2"; 
	enum DemoQuestionnaire { NO_DEMO, QUESTIONNAIRE_1, QUESTIONNAIRE_2 };

	private MessageHandler messageHandler;
	private Questionnaire questionnaire;
	private ParticipantInterface participantInterface;
	
	/**
	 * Used to retrieve a saml
	 */
	private AAQCWrapper aaqc;
	
	/**
	 * For security authentication
	 */
	private String saml;
	
	/**
	 * User to send / retrieve data to / from the Repository
	 */
	private String clinTouchUser;
	
	/**
	 * The dataset that contains the questions
	 */
	private DataSet dataSet;
	
	/**
	 * To access the repository service
	 */
	private Repository repository;
	
	public void setaaqc(AAQCWrapper aaQueryClientWrapper) {
		this.aaqc = aaQueryClientWrapper;
	}
	
	/**
	 * Set the repository
	 * @param repository
	 */
	public void setRepository(final Repository repository) {
		this.repository = repository;
	}

	
	public void setMessageHandler(final MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

	public void setQuestionnaire(final Questionnaire questionnaire) {
		this.questionnaire = questionnaire;
	}
	
	public void setParticipantInterface(ParticipantInterface participantInterface) {
		this.participantInterface = participantInterface;
	}
	
	public void setClinTouchUser(final String clinTouchUser) {
		this.clinTouchUser = clinTouchUser;
	}

	/**
	 * Run periodically to check whether there are any current alarms. If any alarms
	 * are active, start sending messages to the user.
	 */
	public void run() {
		getSAMLAssertion();
		getDataSet();
		
		if(questionnaire.isDemoActive()) {
			questionnaire.checkForDemoTimeout();
		}
		
		List<Record> records = getRecordsForDataSet();
		for(Record record : records) {
			if(questionnaire.isParticipantDueQuestion(dataSet, record)) {
				String question;
				if((question = questionnaire.getNextQuestion(dataSet, record)) != null) { 
					if(questionnaire.recordQuestionSent(dataSet, record) || questionnaire.recordReminderSent(dataSet, record)) {
						try {
							repository.saveRecord(record.toDTO(), false, saml);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					}
					try {
						String mobileNumber = participantInterface.getMobileNumberForParticipant(record, saml);
						sLog.info("Sending message - " + question + " to participant with number " + mobileNumber);
						messageHandler.send(mobileNumber, question);
					} catch (final SMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if(questionnaire.isParticipantDueTimeoutMessage(dataSet, record)) {
				String mobileNumber = participantInterface.getMobileNumberForParticipant(record, saml);
				sendMessage(mobileNumber, QUESTIONNAIRE_TIMED_OUT_MESSAGE);
				try {
					repository.saveRecord(record.toDTO(), false, saml);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}
	}
	
	public void messageReceived(String mobileNumber, String message) {
		sLog.info("Received message " + message + " from " + mobileNumber);
		getSAMLAssertion();
		getDataSet();
		
		ISubject participant = participantInterface.getParticipantByMobileNumber(mobileNumber, dataSet.getProjectCode(), saml);
		if(participant == null) {
			return;
		}
		String studyNumber = participant.getStudyNumber();
		
		org.psygrid.data.model.hibernate.Record record = null;
		try {
			record = repository.getRecordComplete(studyNumber, saml).toHibernate();
			record.attach((DataSet)dataSet);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryServiceFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DemoQuestionnaire demoQuestionnaire = isDemoQuestionnaire(message);
		if( demoQuestionnaire != DemoQuestionnaire.NO_DEMO) {
			String question = questionnaire.startDemo(dataSet, record, demoQuestionnaire);
			try {
				repository.saveRecord(record.toDTO(), false, saml);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendMessage(mobileNumber, question);
			return;
		}
		
		String answer = questionnaire.validateAnswer(dataSet, record, message, clinTouchUser);
		if(answer == null) {
			sLog.info("Received invalid response");
			questionnaire.setAnswer(dataSet, record, Questionnaire.INVALID_ANSWER_VALUE, clinTouchUser);
			try {
				repository.saveRecord(record.toDTO(), false, saml);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			sendNextQuestion(record, mobileNumber);
			return;
		}
		
		if(participantInterface.isParticipantActiveInStudy(record, dataSet) || questionnaire.isDemoActive()) {
			if( questionnaire.setAnswer(dataSet, record, answer, clinTouchUser) ) {
				if(questionnaire.allQuestionsCompleted(dataSet, record)) {
					sendMessage(mobileNumber, QUESTIONNAIRE_COMPLETED_MESSAGE);
				} else {
					sendNextQuestion(record, mobileNumber);
				}
				try {
					repository.saveRecord(record.toDTO(), false, saml);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			sLog.info("Received message from participant when not active in study. Mobile number was " + mobileNumber);
		}		
	}
	
	private void sendNextQuestion(Record record, String mobileNumber) {
		String question = questionnaire.getNextQuestion(dataSet, record);
		if(question != null) {
			try {
				sLog.info("Sending message - " + question + " to participant with number " + mobileNumber);
				messageHandler.send(mobileNumber, question);
			} catch (final SMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void sendMessage(String mobileNumber, String message) {
		try {
			messageHandler.send(mobileNumber, message);
		} catch (final SMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getSAMLAssertion() {
		try {
			saml = aaqc.getSAMLAssertion(clinTouchUser);
		} catch (NotAuthorisedFaultMessage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PGSecuritySAMLVerificationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PGSecurityInvalidSAMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PGSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getDataSet() {
		try {
			DataSet datasetSummary = repository.getDataSetSummaryWithDocs(PROJECT_CODE, saml).toHibernate();
			dataSet = repository.getDataSetComplete(datasetSummary.getId(), saml).toHibernate();
		} catch (final RepositoryNoSuchDatasetFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final RepositoryServiceFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Note that we need the complete data for each record
	private List<Record> getRecordsForDataSet() {
		List<Record> records = new ArrayList<Record>();
		try {
			RecordDTO[] recordsTemp = repository.getRecords(dataSet.getId(), saml);
			for(RecordDTO recordDTO : recordsTemp) {
				Record record = repository.getRecordComplete(recordDTO.getId(), saml).toHibernate();
				record.attach((DataSet)dataSet);
				records.add(record);
			}
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (RepositoryServiceFault e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return records;
	}
	
	private DemoQuestionnaire isDemoQuestionnaire(String message) {
		if(message.equals(DEMO_QUESTIONNAIRE_1)) {
			return DemoQuestionnaire.QUESTIONNAIRE_1;
		} else if(message.equals(DEMO_QUESTIONNAIRE_2)) {
			return DemoQuestionnaire.QUESTIONNAIRE_2;
		}
		
		return DemoQuestionnaire.NO_DEMO;
	}
}