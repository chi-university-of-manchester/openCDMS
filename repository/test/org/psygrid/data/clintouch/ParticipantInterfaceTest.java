package org.psygrid.data.clintouch;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.clintouch.util.StubCommonClasses;
import org.psygrid.data.model.IIntegerValue;
import org.psygrid.data.model.dto.DocumentInstanceDTO;
import org.psygrid.data.model.dto.IdentifierDTO;
import org.psygrid.data.model.dto.PersistentDTO;
import org.psygrid.data.model.dto.RecordDTO;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.IntegerEntry;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;

/**
 * Tests the ParticipantInterface
 * Note that a different stub of the repository is used for different tests
 * Note also that to get these tests to work without actually hitting the database
 * requires a LOT of stubbing. Unfortunately, the original code wasn't written
 * with testing in mind. 
 * @author MattMachin
 *
 */
public class ParticipantInterfaceTest {
	private static final int STUDY_DURATION_DAYS = 7;
	private static final String MOBILE_PHONE_NUMBER = "012345";
	
	private final ParticipantInterface participantInterface = new ParticipantInterface();
	
	private HibernateFactory factory = new HibernateFactory();
	
/*	@Test
	public void getListOfParticipantsWithNoParticipantsInSystem() {
		participantInterface.setEslClient(StubCommonClasses.stubRemoteClient(MOBILE_PHONE_NUMBER));
		
		assert participantInterface.getListOfParticipantsWithAlarmsDue(new ArrayList<RecordDTO>(), null).isEmpty();
	}*/
	
/*	@Test
	public void getListOfParticipantsWithSomeParticipantsInSystemButNoneWithQuestionsDue() {
		final List<RecordDTO> recordsToSend = new ArrayList<RecordDTO>();
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 2);	// make sure that start date is in the future
		createRecordAndAddToList(recordsToSend, calendar);
		addDurationValueToRecord(recordsToSend.get(0));
		createRecordAndAddToList(recordsToSend, calendar);
		addDurationValueToRecord(recordsToSend.get(1));
		
		participantInterface.setEslClient(StubCommonClasses.stubRemoteClient(MOBILE_PHONE_NUMBER));
		
		assert participantInterface.getListOfParticipantsWithAlarmsDue(recordsToSend, null).isEmpty();
	}*/
	
/*	@Test
	public void getListOfParticipantsOneWithQuestionsDue() {
		final List<RecordDTO> recordsToSend = new ArrayList<RecordDTO>();
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 2);	// first record start date in future
		createRecordAndAddToList(recordsToSend, calendar);
		addDurationValueToRecord(recordsToSend.get(0));
		calendar.add(Calendar.DATE, -3); // second record start date in past
		createRecordAndAddToList(recordsToSend, calendar);
		addDurationValueToRecord(recordsToSend.get(1));
		
		participantInterface.setEslClient(StubCommonClasses.stubRemoteClient(MOBILE_PHONE_NUMBER));
		
		assert participantInterface.getListOfParticipantsWithAlarmsDue(recordsToSend, null).size() == 1;
	}*/
	
/*	@Test
	public void getListOfParticipantsOneWithQuestionsDueOneOutsideStudyDuration() {
		final List<RecordDTO> recordsToSend = getRecordListTwoRecordsOneDueOneNot();
				
		participantInterface.setEslClient(StubCommonClasses.stubRemoteClient(MOBILE_PHONE_NUMBER));
		
		List<ParticipantDetails> participantResults = participantInterface.getListOfParticipantsWithAlarmsDue(recordsToSend, null);
		assert participantResults.size() == 1;
		assert participantResults.get(0).getMobileNumber().equals(MOBILE_PHONE_NUMBER);
	}*/
	
/*	public List<RecordDTO> getRecordListTwoRecordsOneDueOneNot() {
		final List<RecordDTO> recordList = new ArrayList<RecordDTO>();
		final Calendar calendar = Calendar.getInstance();
		
		// first record start date well in past, so has finished study
		calendar.add(Calendar.DATE, -200);	
		createRecordAndAddToList(recordList, calendar);
		addDurationValueToRecord(recordList.get(0));
        
		final Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.DATE, -3); // second record start date in past
		createRecordAndAddToList(recordList, calendar2);
		addDurationValueToRecord(recordList.get(1));
		
		return recordList;
	}*/
	
	/**
	 * Create a new record (partially stubbed).
	 * Set the identifier
	 * Set the schedule start date from the Calendar object passed in
	 * Add the record to the list of results
	 * @param results
	 * @param scheduleStartDate
	 */
	private void createRecordAndAddToList(final List<RecordDTO> results, final Calendar scheduleStartDate) {
		final RecordDTO newRecord = partialStubDtoRecord();
		newRecord.setIdentifier(new IdentifierDTO());
		newRecord.getTheRecordData().setScheduleStartDate(scheduleStartDate.getTime());
		results.add(newRecord);
	}
	
	/**
	 * In order to setup the duration value, we need setup a document, section, occurrences, entry, instance, response
	 * @param record
	 */
	private void addDurationValueToRecord(RecordDTO record) {		
		Document setupDocument = factory.createDocument("Setup");
		Section mainSection = createSectionAndAddToDocument(setupDocument);
		SectionOccurrence mainSectionOccurrence = createSectionOccurrenceAndAddToDocument(mainSection);
        IntegerEntry durationEntry = createIntegerEntry(setupDocument, mainSection);
        
        DocumentOccurrence adminSetupOccurrence = factory.createDocumentOccurrence("Admin");
        setupDocument.addOccurrence(adminSetupOccurrence);
                
        org.psygrid.data.model.hibernate.DocumentInstance documentInstance = (org.psygrid.data.model.hibernate.DocumentInstance) setupDocument.generateInstance(adminSetupOccurrence);
        
        BasicResponse response = createBasicResponse(durationEntry, mainSectionOccurrence);
        documentInstance.addResponse(response);
        
        addDocumentInstanceToRecord(record, documentInstance);
	}
	
	private Section createSectionAndAddToDocument(Document document) {
		Section mainSection = factory.createSection("Main section");
		document.addSection(mainSection);
		
		return mainSection;
	}
	
	private SectionOccurrence createSectionOccurrenceAndAddToDocument(Section section) {
		SectionOccurrence sectionOccurrence = factory.createSectionOccurrence("Main Section Occurrence");
		section.addOccurrence(sectionOccurrence);
		
		return sectionOccurrence;
	}
	
	private IntegerEntry createIntegerEntry(Document document, Section section) {
		IntegerEntry entry = factory.createIntegerEntry("Duration");
		document.addEntry(entry);
        entry.setSection(section);
        
        return entry;
	}
	
	private BasicResponse createBasicResponse(IntegerEntry entry, SectionOccurrence sectionOccurrence) {
		BasicResponse response = entry.generateInstance(sectionOccurrence);
	    IIntegerValue value = entry.generateValue();
	    value.setValue(STUDY_DURATION_DAYS);
	    response.setValue(value);
	    
	    return response;
	}
	
	private void addDocumentInstanceToRecord(RecordDTO record, org.psygrid.data.model.hibernate.DocumentInstance documentInstance) {
		DocumentInstanceDTO[] documentInstanceArray = new DocumentInstanceDTO[1];
        Map<Persistent, PersistentDTO> dtoRefs = new HashMap<Persistent, PersistentDTO>();
        documentInstanceArray[0] = documentInstance.toDTO(dtoRefs, RetrieveDepth.RS_COMPLETE);
        record.setDocInstances(documentInstanceArray);
	}
	
	/**
	 * Perform a partial stubbing of dto.Record. This means that a real object 
	 * exists and only the methods stubbed out below are actually stubs. (Others
	 * remain as real implementations)
	 * @return
	 */
	private RecordDTO partialStubDtoRecord() {
		org.psygrid.data.model.hibernate.Record hibernateRecord = stubHibernateRecord();
		RecordDTO dtoRecord = spy(new RecordDTO());
		when(dtoRecord.toHibernate()).thenReturn(hibernateRecord);
		
		return dtoRecord;
	}
	
	private org.psygrid.data.model.hibernate.Record stubHibernateRecord() {
		org.psygrid.data.model.hibernate.Record hibernateRecord = mock(org.psygrid.data.model.hibernate.Record.class);
		when(hibernateRecord.getDataSet()).thenReturn(new DataSet());
		
		return hibernateRecord;
	}
}
