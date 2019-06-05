package org.psygrid.data.clintouch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.clintouch.ClinTouchServiceImpl.DemoQuestionnaire;
import org.psygrid.data.clintouch.QuestionMetadata.BranchType;
import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.ChangeHistory;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.Option;
import org.psygrid.data.model.hibernate.OptionValue;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.Response;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.psygrid.data.model.hibernate.TextValue;
import org.psygrid.data.model.hibernate.Value;


/**
 * Main class that provides an interface to the questions stored in the repository
 * @author Matt Machin
 * @version 1.0
 * @created 17-Aug-2011 15:13:19
 */
public class Questionnaire {
	public static final String INVALID_ANSWER_VALUE = "0";
	private static final String FURTHER_INVALID_ANSWER_VALUE = "-1"; // User has provided a second invalid answer
	
	private static final Integer QUESTION_SET_ONE_DOCUMENT_INDEX = 1;
	private static final Integer QUESTION_SET_TWO_DOCUMENT_INDEX = 2;
	private static final Integer QUESTION_SECTION_OCCURRENCE_INDEX = 0;
	
	private static final int MESSAGE_EXPECTED_LENGTH = 1;
	
	private static final String INITIAL_QUESTION_SENT = "Awaiting response";
	private static final String REMINDER_SENT = "Reminder sent";
	
	private static final String QUESTIONNAIRE_TIMED_OUT = "Timed out";
	
	private static final String SETUP_DOCUMENT = "Setup";	
	private static final String PRIMARY_DELUSION_TEXT = "Primary Delusion";
	private static final String SECONDARY_DELUSION_TEXT = "Secondary Delusion";
	private static final String PRIMARY_DELUSION_ENTRY = "PrimaryDelusion";
	private static final String SECONDARY_DELUSION_ENTRY = "SecondaryDelusion";
	private static final int NO_DELUSION_CODE = 1;
	
	private static final String TIMINGS_DOCUMENT = "Timings";
	private static final Integer TIMINGS_SECTION_OCCURRENCE_INDEX = 0;
	
	private static final Log LOG = LogFactory.getLog(Questionnaire.class);
	
	private AlarmTimes alarmTimes;
	
	private QuestionMetadataCollection questionSetOneMetadata;
	private QuestionMetadataCollection questionSetTwoMetadata;
	private QuestionMetadataCollection activeQuestionSetMetadata;
	
	private static final int QUESTION_REVERSE_VALUE = 8;
	private int currentQuestionNumber; // Used to retrieve isReversed info for a question
	
	private boolean isDemoActive;
	private boolean isDemoQuestionSet2;
	private Calendar timeDemoStarted;
	
	public Questionnaire() {
		setupQuestionMetadata();
	}
	
	public void setAlarmTimes(AlarmTimes alarmTimes) {
		this.alarmTimes = alarmTimes;
	}

	public boolean isDemoActive() {
		return isDemoActive;
	}
	
	/**
	 * Checks whether there is a currently active alarm for the given record. Then checks whether we have already
	 * had any responses yet.
	 * @param dataSet
	 * @param record
	 * @return true if there is a currently active alarm and there have been no responses
	 */
	public boolean isParticipantDueQuestion(final DataSet dataSet, final Record record) {
		int currentEventNumber = alarmTimes.getCurrentEventNumber(record.getScheduleStartDate());
		if(currentEventNumber < 0) {
			return false;
		}
		
		final Document document = getCurrentDocument(dataSet, record, currentEventNumber);
		DocumentInstance instance = getDocumentInstanceCreatingIfDoesntExist(document, record, currentEventNumber);
		if(instance.getResponses().isEmpty()) {
			// We haven't sent a question yet
			return true;
		}
		if(initialQuestionSent(document, instance)) {
			// Have sent initial question, but no response yet so send reminder
			return true;
		}
		
		return false;
	}
	
	public boolean isParticipantDueTimeoutMessage(final DataSet dataSet, final Record record) {
		int currentEventNumber = alarmTimes.getLastEventNumber(record.getScheduleStartDate());
		if(currentEventNumber < 0) {
			return false;
		}
		
		final Document document = getCurrentDocument(dataSet, record, currentEventNumber);
		DocumentInstance documentInstance = getDocumentInstanceCreatingIfDoesntExist(document, record, currentEventNumber);
		
		BasicEntry entry = getNextQuestionEntry(record, dataSet, document, documentInstance);
		if(entry == null) {
			// All questions completed
			return false;
		}
		
		Response response = documentInstance.getResponse(entry, entry.getSection().getOccurrence(QUESTION_SECTION_OCCURRENCE_INDEX));
		if( response != null) {
			if(questionnaireTimedOutSent((BasicResponse)response)) {
				return false;
			}
			if(invalidResponse((BasicResponse)response)) {
				return false;
			}
		}
		
		questionnaireTimedOut(documentInstance, entry, (BasicResponse)response);
		
		return true;
	}
	
	/**
	 * Get the next question in the list.
	 * @param dataSet		The dataset containing the questions
	 * @param record		The record for the participant
	 * @return 	Returns the question string. Returns null if there are no more questions or no alarm active
	 */
	public String getNextQuestion(final DataSet dataSet, final Record record) {
		int currentEventNumber = alarmTimes.getCurrentEventNumber(record.getScheduleStartDate());
		if(currentEventNumber < 0) {
			if(!isDemoActive) {
				LOG.info("Requested question when no alarm is active. Record id = " + record.getIdentifier().getIdentifier());
				return null;
			}
			if(isDemoQuestionSet2) {
				currentEventNumber = alarmTimes.getTotalAlarmCount() + 1;
			} else {
				currentEventNumber = alarmTimes.getTotalAlarmCount();
			}
		}
		
		final Document document = getCurrentDocument(dataSet, record, currentEventNumber);
		
		return getNextQuestion(dataSet, record, currentEventNumber, document);
	}
	
	/**
	 * Sets the answer in the appropriate document
	 * @param dataSet		The DataSet associated with the Record
	 * @param record		The Record to be updated
	 * @param answer		The answer to set
	 * @param clinTouchUser	The user who made the changes
	 * @return				True if the record is updated, false if not
	 */
	public boolean setAnswer(final DataSet dataSet, final Record record, String answer, String clinTouchUser) {
		int currentEventNumber = alarmTimes.getCurrentEventNumber(record.getScheduleStartDate());
		if(currentEventNumber < 0) {
			if(!isDemoActive) {
				LOG.info("Received an answer when no alarm is active. Record id = " + record.getIdentifier().getIdentifier() + " answer = " + answer);
				return false;
			}
			if(isDemoQuestionSet2) {
				currentEventNumber = alarmTimes.getTotalAlarmCount() + 1;
			} else {
				currentEventNumber = alarmTimes.getTotalAlarmCount();
			}
		}
		final Document document = getCurrentDocument(dataSet, record, currentEventNumber);
		DocumentInstance instance = getDocumentInstanceCreatingIfDoesntExist(document, record, currentEventNumber);
		
		if(instance.getResponses().isEmpty() || initialQuestionSent(document, instance) || reminderSent(document, instance)) {
			// First answer for this questionnaire
			if(!isDemoActive) {
				questionnaireStarted(record, dataSet, currentEventNumber);
			}
		}
		
		BasicEntry entry = getNextQuestionEntry(record, dataSet, document, instance);
		if(entry == null) {
			// There was no entry that hadn't already been completed
			LOG.error("Attempting to add an answer to a document that already has answers for all questions. Record id = " + record.getIdentifier().getIdentifier() + " answer = " + answer);
			return false;
		}
		answer = reverseAnswerIfRequired(answer);
		Response currentResponse = instance.getResponse(entry, entry.getSection().getOccurrence(QUESTION_SECTION_OCCURRENCE_INDEX));
		if(currentResponse != null) {
			// Response previously held an invalid answer so update it
			updateResponseWithValue(currentResponse, answer);
		} else {		
			// No existing response so create one and set the value
			BasicResponse response = createBasicResponseWithValue(entry, answer);
			instance.addResponse(response);
		}
		
		// Store changes in history
		ChangeHistory change = instance.addToHistory(clinTouchUser);
		instance.checkForChanges(change);
		
		return true;
	}
	
	/**
	 * Check that answer is in the accepted range 1-7
	 * Removes trailing whitespace, as some participants where sending their answer followed by a space
	 * @param answer 	The message received
	 * @return			The validated answer or null if the answer is invalid
	 */
	public String validateAnswer(final DataSet dataSet, final Record record, String answer, String clinTouchUser) {
		boolean valid = true;
		if(answer == null) {
			valid = false;
		} else {
			answer = answer.trim();
			if(answer.length() != MESSAGE_EXPECTED_LENGTH) {
				valid = false;
			} else {
				if(answer.charAt(0) < '1' || answer.charAt(0) > '7') {
					valid = false;
				}
			}
		}
		
		return valid ? answer : null;
	}
	
	public boolean allQuestionsCompleted(final DataSet dataSet, final Record record) {
		int currentEventNumber = alarmTimes.getCurrentEventNumber(record.getScheduleStartDate());
		if(currentEventNumber < 0 && isDemoActive) {
			if(isDemoQuestionSet2) {
				currentEventNumber = alarmTimes.getTotalAlarmCount() + 1;
			} else {
				currentEventNumber = alarmTimes.getTotalAlarmCount();
			}
		}
		final Document document = getCurrentDocument(dataSet, record, currentEventNumber);
		DocumentInstance instance = getDocumentInstanceCreatingIfDoesntExist(document, record, currentEventNumber);
		BasicEntry entry = getNextQuestionEntry(record, dataSet, document, instance);
		if(entry == null) {
			if(!isDemoActive) {
				questionnaireCompleted(record, dataSet, currentEventNumber);
			} else {
				isDemoActive = false;
			}
			return true;
		}
		
		return false;
	}
	
	/**
	 * If this is the first question, add a response to indicate that we have sent the question. That way we won't keep
	 * resending it
	 * @param dataSet
	 * @param record
	 * @return Return true if we have added a response
	 */
	public boolean recordQuestionSent(final DataSet dataSet, final Record record) {
		int currentEventNumber = alarmTimes.getCurrentEventNumber(record.getScheduleStartDate());
		if(currentEventNumber < 0 && isDemoActive) {
			if(isDemoQuestionSet2) {
				currentEventNumber = alarmTimes.getTotalAlarmCount() + 1;
			} else {
				currentEventNumber = alarmTimes.getTotalAlarmCount();
			}
		}
		final Document document = getCurrentDocument(dataSet, record, currentEventNumber);
		DocumentInstance documentInstance = getDocumentInstanceCreatingIfDoesntExist(document, record, currentEventNumber);
		if(documentInstance.getResponses().isEmpty()) {
			BasicEntry entry = getNextQuestionEntry(record, dataSet, document, documentInstance);
			BasicResponse response = createBasicResponseWithValue(entry, INITIAL_QUESTION_SENT);
			documentInstance.addResponse(response);
			LOG.info("Sent first question");
			
			return true;
		}
		
		return false;
	}
	
	public boolean recordReminderSent(final DataSet dataSet, final Record record) {
		int currentEventNumber = alarmTimes.getCurrentEventNumber(record.getScheduleStartDate());
		if(currentEventNumber < 0 && isDemoActive) {
			if(isDemoQuestionSet2) {
				currentEventNumber = alarmTimes.getTotalAlarmCount() + 1;
			} else {
				currentEventNumber = alarmTimes.getTotalAlarmCount();
			}
		}
		final Document document = getCurrentDocument(dataSet, record, currentEventNumber);
		DocumentInstance documentInstance = getDocumentInstanceCreatingIfDoesntExist(document, record, currentEventNumber);
		if(initialQuestionSent(document, documentInstance)) {
			Entry firstEntry = document.getEntry(0);
			Response firstResponse = documentInstance.getResponses(firstEntry).get(0);
			Value currentAnswer = ((BasicResponse)firstResponse).getTheValue();
			((TextValue)currentAnswer).setValue(REMINDER_SENT);
			LOG.info("Sent reminder");
			
			return true;
		}
		
		return false;
	}
	
	public String startDemo(final DataSet dataSet, final Record record, DemoQuestionnaire demoQuestionnaire) {
		Document document = getDemoDocument(dataSet, record, demoQuestionnaire);
		int currentEventNumber;
		if(isDemoQuestionSet2) {
			currentEventNumber = alarmTimes.getTotalAlarmCount()+1;
		} else {
			currentEventNumber = alarmTimes.getTotalAlarmCount();
		}
		// Force creation of the instance at this point
		getDocumentInstanceCreatingIfDoesntExist(document, record, currentEventNumber);
		isDemoActive = true;
		timeDemoStarted = Calendar.getInstance();
		
		String nextQuestion = getNextQuestion(dataSet, record, currentEventNumber, document);
		if(nextQuestion != null) {
			LOG.info("Starting demo");
		}
		
		return nextQuestion; 
	}
	
	public void checkForDemoTimeout() {
		if(alarmTimes.hasDemoTimedOut(timeDemoStarted)) {
			isDemoActive = false;
		}
	}
	
	private String getNextQuestion(final DataSet dataSet, final Record record, int currentEventNumber, Document document) {		
		DocumentInstance instance = getDocumentInstanceCreatingIfDoesntExist(document, record, currentEventNumber);
		BasicEntry entry = getNextQuestionEntry(record, dataSet, document, instance);
		if(entry == null) {
			// No further questions
			return null;
		}
		if(isCurrentAnswerFurtherInvalidAnswerValue(instance, entry)) {
			// Don't send questions if more than one invalid response
			return null;
		}
		return getQuestionTextIncludingDelusions(entry, record, dataSet);
	}
	
	private Document getCurrentDocument(final DataSet dataSet, final Record record, int currentEventNumber) {
		Document questionSetOne = dataSet.getDocuments().get(QUESTION_SET_ONE_DOCUMENT_INDEX);
		Document questionSetTwo = dataSet.getDocuments().get(QUESTION_SET_TWO_DOCUMENT_INDEX);
		
		if(currentEventNumber == 0) {
			// First event is always question set 1
			activeQuestionSetMetadata = questionSetOneMetadata;
			return questionSetOne;
		}
		
		Document currentlyActiveDocument = getCurrentlyActiveDocumentOrNullIfDoesntExist(questionSetOne, questionSetTwo, record, currentEventNumber);
		if(currentlyActiveDocument != null) {
			return currentlyActiveDocument;
		}
		
		return getMostRecentlyCompletedDocument(questionSetOne, questionSetTwo, record, dataSet, currentEventNumber);
	}
	
	private Document getDemoDocument(final DataSet dataSet, final Record record, DemoQuestionnaire demoQuestionnaire) {
		if(demoQuestionnaire == DemoQuestionnaire.QUESTIONNAIRE_2) {
			activeQuestionSetMetadata = questionSetTwoMetadata;
			isDemoQuestionSet2 = true;
			return dataSet.getDocuments().get(QUESTION_SET_TWO_DOCUMENT_INDEX);
		}
		
		activeQuestionSetMetadata = questionSetOneMetadata;
		isDemoQuestionSet2 = false;
		return dataSet.getDocuments().get(QUESTION_SET_ONE_DOCUMENT_INDEX);
	}
	
	private Document getCurrentlyActiveDocumentOrNullIfDoesntExist(Document questionSetOne, Document questionSetTwo, final Record record, int currentEventNumber) {
		DocumentOccurrence oneOccurrence = questionSetOne.getOccurrence(currentEventNumber);
		DocumentInstance oneInstance = record.getDocumentInstance(oneOccurrence);
		if(oneInstance != null) {
			// Question set one currently active
			activeQuestionSetMetadata = questionSetOneMetadata;
			return questionSetOne;
		}
		
		DocumentOccurrence twoOccurrence = questionSetTwo.getOccurrence(currentEventNumber);
		DocumentInstance twoInstance = record.getDocumentInstance(twoOccurrence);
		if(twoInstance != null) {
			// Question set two currently active
			activeQuestionSetMetadata = questionSetTwoMetadata;
			return questionSetTwo;
		}
		
		return null;
	}
	
	private Document getMostRecentlyCompletedDocument(Document questionSetOne, Document questionSetTwo, final Record record, DataSet dataSet, int currentEventNumber) {
		for(int previousEventCount = 1; previousEventCount <= currentEventNumber; previousEventCount++) {
			DocumentOccurrence oneOccurrence = questionSetOne.getOccurrence(currentEventNumber - previousEventCount);
			DocumentInstance oneInstance = record.getDocumentInstance(oneOccurrence);
			if(oneInstance != null) {
				activeQuestionSetMetadata = questionSetOneMetadata;
				if(getNextQuestionEntry(record, dataSet, questionSetOne, oneInstance) == null) {
					// Question set one was answered last time and all questions completed
					activeQuestionSetMetadata = questionSetTwoMetadata;
					return questionSetTwo;
				} else {
					// Question set one was started but not completed
					activeQuestionSetMetadata = questionSetOneMetadata;
					return questionSetOne;
				}				
			} else {
				DocumentOccurrence twoOccurrence = questionSetTwo.getOccurrence(currentEventNumber - previousEventCount);
				DocumentInstance twoInstance = record.getDocumentInstance(twoOccurrence);
				if(twoInstance != null) {
					activeQuestionSetMetadata = questionSetTwoMetadata;
					if(getNextQuestionEntry(record, dataSet, questionSetTwo, twoInstance) == null) {
						// Question set two was answered last time and all questions completed
						activeQuestionSetMetadata = questionSetOneMetadata;
						return questionSetOne;
					} else {
						// Question set two was started but not completed
						activeQuestionSetMetadata = questionSetTwoMetadata;
						return questionSetTwo;
					}
				}
			}
		}
		
		// If no matches, return questionSetOne as default
		return questionSetOne;
	}
	
	private DocumentInstance getDocumentInstanceCreatingIfDoesntExist(final Document document, final Record record, int currentEventNumber) {
		DocumentOccurrence occurrence = document.getOccurrence(currentEventNumber);
		DocumentInstance instance = record.getDocumentInstance(occurrence);
		if(instance == null) {
			instance = (DocumentInstance)document.generateInstance(occurrence);
			record.addDocumentInstance(instance);
		}
		
		return instance;
	}
	

	/**
	 * Find the first question in the current document that does not have an answer
	 * @param document 	The current document
	 * @return			The entry that contains the first question without an answer or null if all questions have been answered
	 */
	private BasicEntry getNextQuestionEntry(Record record, DataSet dataSet, final Document document, final DocumentInstance documentInstance) {
		List<Entry> entries = document.getEntries();
		BasicEntry basicEntry = null;
		
		List<Integer> previousAnswers = new ArrayList<Integer>();
		
		int entryCount = 0;
		do {
			currentQuestionNumber = entryCount;
			Entry entry = entries.get(entryCount);
			Response response = documentInstance.getResponse(entry, entry.getSection().getOccurrence(QUESTION_SECTION_OCCURRENCE_INDEX));
			if(response == null) {
				if(isNoDelusion(record, dataSet, entry)) {
					// Entry contains "No delusion" so this is the end of the questionnaire.
					return null;
				}
				// We know that this will be a basic entry so just cast
				basicEntry = (BasicEntry)entry;
				break;
			} else {
				Value answer = ((BasicResponse)response).getTheValue();
				String answerString = ((TextValue)answer).getValue();
				if(isInvalidAnswer(answerString) || isAwaitingResponse(answerString) || isQuestionnaireTimedOut(answerString)) {
					// We should update the value
					// We know that this will be a basic entry so just cast
					basicEntry = (BasicEntry)entry;
					break;
				}
				int answerInt = Integer.parseInt(answerString);
				int questionsToSkip = activeQuestionSetMetadata.getQuestionsToSkip(entryCount, answerInt, previousAnswers);
				// Move forward to the next question plus skip any additional questions
				entryCount += 1 + questionsToSkip;
				previousAnswers.add(answerInt);
			}
		} while(entryCount < entries.size());
		
		return basicEntry;
	}
	
	private BasicResponse createBasicResponseWithValue(BasicEntry entry, String answer) {
		Section section = entry.getSection();
		SectionOccurrence sectionOccurrence = section.getOccurrence(QUESTION_SECTION_OCCURRENCE_INDEX); 
		BasicResponse response = (BasicResponse)entry.generateInstance(sectionOccurrence);
		IValue value = entry.generateValue();
		ITextValue textAnswer = (ITextValue)value;
		textAnswer.setValue(answer);
		response.setValue(textAnswer);
		
		return response;
	}
	
	private void updateResponseWithValue(Response response, String answer) {
		Value currentAnswer = ((BasicResponse)response).getTheValue();
		String currentAnswerString = ((TextValue)currentAnswer).getValue();
		if(currentAnswerString.equals(FURTHER_INVALID_ANSWER_VALUE)) {
			// If we have had more than 1 invalid answer, don't store any further responses
			return;
		}
		if(answer.equals(INVALID_ANSWER_VALUE)) {
			((TextValue)currentAnswer).setValue(FURTHER_INVALID_ANSWER_VALUE);
		} else {
			((TextValue)currentAnswer).setValue(answer);
		}
	}
	
	private boolean isCurrentAnswerFurtherInvalidAnswerValue(DocumentInstance documentInstance, Entry entry) {
		Response response = documentInstance.getResponse(entry, entry.getSection().getOccurrence(QUESTION_SECTION_OCCURRENCE_INDEX));
		if(response != null) {
			Value valueAnswer = ((BasicResponse)response).getTheValue();
			if(((TextValue)valueAnswer).getValue().equals(FURTHER_INVALID_ANSWER_VALUE)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Use this to setup the metadata associated with questions. The questions need to align with those in the questionnaire document
	 */
	private void setupQuestionMetadata() {
		setupQuestionSetOneMetadata();
		setupQuestionSetTwoMetadata();
		
		activeQuestionSetMetadata = questionSetOneMetadata;
	}
	
	private void setupQuestionSetOneMetadata() {
		List<QuestionMetadata> questionMetadata = new ArrayList<QuestionMetadata>();
		
		// Hopelessness
		questionMetadata.add(new QuestionMetadata(true,false));
		questionMetadata.add(new QuestionMetadata(false,true,BranchType.ALL_ARE_LESS,3,2,1));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		
		// Depression
		questionMetadata.add(new QuestionMetadata(false,true,BranchType.LESS_THAN,3,4,0));
		questionMetadata.add(new QuestionMetadata(false,true,BranchType.LESS_THAN,3,3,0));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		
		// Hallucinations
		questionMetadata.add(new QuestionMetadata(false,true,BranchType.LESS_THAN,3,3,0));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,true,BranchType.LESS_THAN,3,3,0));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		
		questionSetOneMetadata = new QuestionMetadataCollection(questionMetadata);
	}
	
	private void setupQuestionSetTwoMetadata() {
		List<QuestionMetadata> questionMetadata = new ArrayList<QuestionMetadata>();
		
		// Anxiety
		questionMetadata.add(new QuestionMetadata(false,true,BranchType.LESS_THAN,3,3,0));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		
		// Grandiosity
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,true,BranchType.LESS_THAN,3,1,0));
		questionMetadata.add(new QuestionMetadata(false,false));
		
		// Suspiciousness
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,true,BranchType.ALL_ARE_LESS,3,3,2));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		
		// Delusions
		questionMetadata.add(new QuestionMetadata(false,true,BranchType.LESS_THAN,3,3,0));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,true,BranchType.LESS_THAN,3,3,0));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		questionMetadata.add(new QuestionMetadata(false,false));
		
		questionSetTwoMetadata = new QuestionMetadataCollection(questionMetadata);
	}
	
	private String getQuestionTextIncludingDelusions(BasicEntry entry, Record record, DataSet dataSet) {
		if(entry.getDisplayText().equalsIgnoreCase(PRIMARY_DELUSION_TEXT)) {
			return getDelusionOption(record, dataSet, PRIMARY_DELUSION_ENTRY).getDisplayText();
		}
		
		if(entry.getDisplayText().equalsIgnoreCase(SECONDARY_DELUSION_TEXT)) {
			return getDelusionOption(record, dataSet, SECONDARY_DELUSION_ENTRY).getDisplayText();
		}
		
		return entry.getDisplayText();
	}
	
	private Option getDelusionOption(Record record, DataSet dataSet, String delusionEntry) {
		Document document = dataSet.getDocument(SETUP_DOCUMENT);
		DocumentOccurrence documentOccurrence = document.getOccurrence(0);
		DocumentInstance documentInstance = record.getDocumentInstance(documentOccurrence);
		Entry entry = document.getEntry(delusionEntry);
		Section section = entry.getSection();
		SectionOccurrence sectionOccurrence = section.getOccurrence(0);
		BasicResponse delusionResponse = (BasicResponse)documentInstance.getResponse(entry, sectionOccurrence);
		OptionValue delusionValue = (OptionValue)delusionResponse.getTheValue();
		Option delusionOption = delusionValue.getValue();
		
		return delusionOption;
	}
	
	/**
	 * If the entry contains a delusion question, check whether the associated value is "no delusion".
	 * @param entry
	 * @return True if this is a delusion question and the associated value is "no delusion".
	 */
	private boolean isNoDelusion(Record record, DataSet dataSet, Entry entry) {
		if(entry.getDisplayText().equalsIgnoreCase(PRIMARY_DELUSION_TEXT)) {
			if(getDelusionOption(record, dataSet, PRIMARY_DELUSION_ENTRY).getCode() == NO_DELUSION_CODE) {
				return true;
			}
			return false;
		}
		if(entry.getDisplayText().equalsIgnoreCase(SECONDARY_DELUSION_TEXT)) {
			if(getDelusionOption(record, dataSet, SECONDARY_DELUSION_ENTRY).getCode() == NO_DELUSION_CODE) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	private String reverseAnswerIfRequired(String answer) {
		if(activeQuestionSetMetadata.isReversed(currentQuestionNumber)) {
			return Integer.toString(QUESTION_REVERSE_VALUE - Integer.parseInt(answer));
		}
		
		return answer;
	}
	
	/**
	 * Record the time the questionnaire was started. If a time has previously been recorded then don't update it.
	 * @param record
	 * @param dataSet
	 * @param currentEventNumber
	 */
	private void questionnaireStarted(Record record, DataSet dataSet, int currentEventNumber) {
		Document document = dataSet.getDocument(TIMINGS_DOCUMENT);
		DocumentOccurrence documentOccurrence = document.getOccurrence(0);
		DocumentInstance documentInstance = record.getDocumentInstance(documentOccurrence);
		if(documentInstance == null) {
			documentInstance = (DocumentInstance)document.generateInstance(documentOccurrence);
			record.addDocumentInstance(documentInstance);
		}
		// We know that this will be a basic entry so just cast
		BasicEntry entry = (BasicEntry)document.getEntry(currentEventNumber*2);
		if(documentInstance.getResponse(entry, entry.getSection().getOccurrence(TIMINGS_SECTION_OCCURRENCE_INDEX)) == null) {
			BasicResponse response = createBasicResponseWithValue(entry, new Date().toString());
			documentInstance.addResponse(response);
			LOG.info("Questionnaire started");
		}
	}
	
	private void questionnaireCompleted(Record record, DataSet dataSet, int currentEventNumber) {
		Document document = dataSet.getDocument(TIMINGS_DOCUMENT);
		DocumentOccurrence documentOccurrence = document.getOccurrence(0);
		DocumentInstance documentInstance = record.getDocumentInstance(documentOccurrence);
		if(documentInstance == null) {
			documentInstance = (DocumentInstance)document.generateInstance(documentOccurrence);
			record.addDocumentInstance(documentInstance);
		}
		// We know that this will be a basic entry so just cast
		BasicEntry entry = (BasicEntry)document.getEntry(currentEventNumber*2+1);
		if(documentInstance.getResponse(entry, entry.getSection().getOccurrence(TIMINGS_SECTION_OCCURRENCE_INDEX)) == null) {
			BasicResponse response = createBasicResponseWithValue(entry, new Date().toString());
			documentInstance.addResponse(response);
			LOG.info("Questionnaire completed");
		}
	}
	
	private void questionnaireTimedOut(DocumentInstance documentInstance, BasicEntry entry, BasicResponse response) {
		if(response != null) {
			updateResponseWithValue(response, QUESTIONNAIRE_TIMED_OUT);
		} else {
			response = createBasicResponseWithValue(entry, QUESTIONNAIRE_TIMED_OUT);
			documentInstance.addResponse(response);
		}
		
		LOG.info("Questionnaire timed out");
	}
	
	private boolean isInvalidAnswer(String answer) {
		if(answer.equals(INVALID_ANSWER_VALUE) || answer.equals(FURTHER_INVALID_ANSWER_VALUE)) {
			return true;
		}
		
		return false;
	}
	
	private boolean isAwaitingResponse(String answer) {
		if(answer.equals(INITIAL_QUESTION_SENT) || answer.equals(REMINDER_SENT)) {
			return true;
		}
		
		return false;
	}
	
	private boolean isQuestionnaireTimedOut(String answer) {
		if(answer.equals(QUESTIONNAIRE_TIMED_OUT)) {
			return true;
		}
		
		return false;
	}
	
	private boolean initialQuestionSent(Document document, DocumentInstance documentInstance) {
		if(documentInstance.getResponses().size() == 1) {
			Entry firstEntry = document.getEntry(0);
			Response firstResponse = documentInstance.getResponses(firstEntry).get(0);
			Value answer = ((BasicResponse)firstResponse).getTheValue();
			String answerString = ((TextValue)answer).getValue();
			if(answerString.equals(INITIAL_QUESTION_SENT)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean reminderSent(Document document, DocumentInstance documentInstance) {
		if(documentInstance.getResponses().size() == 1) {
			Entry firstEntry = document.getEntry(0);
			Response firstResponse = documentInstance.getResponses(firstEntry).get(0);
			Value answer = ((BasicResponse)firstResponse).getTheValue();
			String answerString = ((TextValue)answer).getValue();
			if(answerString.equals(REMINDER_SENT)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean questionnaireTimedOutSent(BasicResponse response) {
		Value answer = ((BasicResponse)response).getTheValue();
		String answerString = ((TextValue)answer).getValue();
		if(answerString.equals(QUESTIONNAIRE_TIMED_OUT)) {
			return true;
		}
		
		return false;
	}
	
	private boolean invalidResponse(BasicResponse response) {
		Value answer = ((BasicResponse)response).getTheValue();
		String answerString = ((TextValue)answer).getValue();
		if(answerString.equals(FURTHER_INVALID_ANSWER_VALUE)) {
			return true;
		}
		
		return false;
	}
}