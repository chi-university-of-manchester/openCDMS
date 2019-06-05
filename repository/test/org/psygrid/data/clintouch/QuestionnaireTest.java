package org.psygrid.data.clintouch;

import org.psygrid.data.clintouch.util.StubCommonClasses;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Record;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class QuestionnaireTest {
	private final Questionnaire questionnaire = new Questionnaire();
	private DataSet dataSet;
	private Record record;
	
	public static final String FIRST_QUESTION_TEXT = "First question";
	public static final String SECOND_QUESTION_TEXT = "Next question";
	
	public static final Integer QUESTION_SET_ONE_DOCUMENT_INDEX = 1;
	
	@BeforeClass
	public void initialiseClass() {
		questionnaire.setAlarmTimes(StubCommonClasses.stubAlarmTimes());
	}
	
	@BeforeTest
	public void setup() {
		dataSet = StubCommonClasses.setupDataSet(FIRST_QUESTION_TEXT, SECOND_QUESTION_TEXT);
		record = StubCommonClasses.setupRecord(dataSet);
	}
	
	/**
	 * Pass null to validateAnswer. Should return null and no document instance should be created.
	 */
	@Test
	public void validateAnswerNull() {															
		Assert.assertNull(questionnaire.validateAnswer(dataSet, record, null, null));
		assert hasDocumentInstance() == false;
	}
	
	/**
	 * Pass blank string to validateAnswer. Should return null and no document instance should be created.
	 */
	@Test
	public void validateAnswerBlankString() {
		Assert.assertNull(questionnaire.validateAnswer(dataSet, record, "", null));
		assert hasDocumentInstance() == false;
	}
	
	/**
	 * Pass space to validateAnswer. Should return null and no document instance should be created.
	 */
	@Test
	public void validateAnswerSpace() {
		Assert.assertNull(questionnaire.validateAnswer(dataSet, record, " ", null));
		assert hasDocumentInstance() == false;
	}
	
	/**
	 * Pass non numeric value to validateAnswer. Should return null and no document instance should be created.
	 */
	@Test
	public void validateAnswerNonNumeric() {
		Assert.assertNull(questionnaire.validateAnswer(dataSet, record, "x", null));
		assert hasDocumentInstance() == false;
	}
	
	/**
	 * Pass too high number to validateAnswer. Should return null and no document instance should be created.
	 */
	@Test
	public void validateAnswerNumberTooHigh() {
		Assert.assertNull(questionnaire.validateAnswer(dataSet, record, "8", null));
		assert hasDocumentInstance() == false;
	}
	
	/**
	 * Pass too low number to validateAnswer. Should return null and no document instance should be created.
	 */
	@Test
	public void validateAnswerNumberTooLow() {
		Assert.assertNull(questionnaire.validateAnswer(dataSet, record, "0", null));
		assert hasDocumentInstance() == false;
	}
	
	/**
	 * Pass too long number to validateAnswer. Should return null and no document instance should be created.
	 */
	@Test
	public void validateAnswerTooLong() {
		Assert.assertNull(questionnaire.validateAnswer(dataSet, record, "12", null));
		assert hasDocumentInstance() == false;
	}
	
	/**
	 * Pass valid answer to validateAnswer. Should return the correct answer.
	 */
	@Test
	public void validateAnswerValid() {
		assert questionnaire.validateAnswer(dataSet, record, "3", null).equals("3");
	}
	
	private boolean hasDocumentInstance() {
		Document document = dataSet.getDocument(QUESTION_SET_ONE_DOCUMENT_INDEX); 
		if( record.getDocumentInstances(document).size() == 0 ) {
			return false;
		}
		
		return true;
	}
	
/*	@Test
	public void getNextQuestionFirstQuestion() {
		assert FIRST_QUESTION_TEXT.equals(questionnaire.getNextQuestion(dataSet.toHibernate(),1));
	}
	
	@Test
	public void getNextQuestionSecondQuestion() {
		assert SECOND_QUESTION_TEXT.equals(questionnaire.getNextQuestion(dataSet.toHibernate(),2));
	}
	
	@Test
	public void getNextQuestionBeyondLastQuestion() {
		Assert.assertNull(questionnaire.getNextQuestion(dataSet.toHibernate(),3));
	}*/
}
