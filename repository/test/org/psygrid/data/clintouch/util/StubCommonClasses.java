package org.psygrid.data.clintouch.util;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.psygrid.data.clintouch.AlarmTimes;
import org.psygrid.data.clintouch.ParticipantInterface;
import org.psygrid.data.model.dto.RecordDTO;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.Identifier;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.RecordData;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.psygrid.data.model.hibernate.TextEntry;
import org.psygrid.data.repository.Repository;
import org.psygrid.data.utils.esl.EslException;
import org.psygrid.data.utils.esl.IRemoteClient;
import org.psygrid.data.utils.wrappers.AAQCWrapper;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.hibernate.Project;
import org.psygrid.esl.model.hibernate.Subject;
import org.psygrid.logging.AuditLogger;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.accesscontrol.AEFAction;
import org.psygrid.security.accesscontrol.AEFGroup;
import org.psygrid.security.accesscontrol.AEFProject;
import org.psygrid.security.accesscontrol.AccessEnforcementFunction;
import org.psygrid.security.accesscontrol.IAccessEnforcementFunction;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;


public class StubCommonClasses {
	private static RecordDTO recordResult;
	
	public static Record getRecordResult() {
		return recordResult.toHibernate();
	}
	
	public static AuditLogger stubAuditLogger() {
		AuditLogger auditLogger = mock(AuditLogger.class);
		doNothing().when(auditLogger).logMethodCall(anyString(), anyString(), anyString(), anyString());
		
		return auditLogger;
	}
	
	public static IAccessEnforcementFunction stubAccessEnforcementFunction() {
		IAccessEnforcementFunction accessControl = mock(AccessEnforcementFunction.class);
		when(accessControl.getCallersIdentity()).thenReturn("testUser");
		try {
			when(accessControl.authoriseUser(anyString(), any(AEFGroup.class), any(AEFAction.class), any(AEFProject.class))).thenReturn(true);
		} catch (PGSecurityException e) {
			e.printStackTrace();
		} catch (PGSecurityInvalidSAMLException e) {
			e.printStackTrace();
		} catch (PGSecuritySAMLVerificationException e) {
			e.printStackTrace();
		}
		
		return accessControl;
	}
	
	public static AAQCWrapper stubAAQCWrapper() {
		AAQCWrapper mockAAQCWrapper = mock(AAQCWrapper.class);
		try {
			when(mockAAQCWrapper.getSAMLAssertion(anyString())).thenReturn(new String());
		} catch (NotAuthorisedFaultMessage e) {
			e.printStackTrace();
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (PGSecuritySAMLVerificationException e) {
			e.printStackTrace();
		} catch (PGSecurityInvalidSAMLException e) {
			e.printStackTrace();
		} catch (PGSecurityException e) {
			e.printStackTrace();
		}
		
		return mockAAQCWrapper;
	}
	
	public static IRemoteClient stubRemoteClient(String mobilePhoneNumber) {
		IRemoteClient eslClient = mock(IRemoteClient.class);
		try {
			when(eslClient.retrieveProjectByCode(anyString(), anyString())).thenReturn(new Project());
			
			Subject subject = new Subject();
			subject.setMobilePhone(mobilePhoneNumber);
			when(eslClient.retrieveSubjectByStudyNumber(any(IProject.class), anyString(), anyString())).thenReturn(subject);
		} catch (EslException e) {
			e.printStackTrace();
		}
		
		return eslClient;
	}
	
	public static DataSet setupDataSet(String firstQuestionText, String secondQuestionText) {
		DataSet dataSet = new DataSet();
		final List<Document> documents = new ArrayList<Document>();
		
		setupDocument(documents, "Admin");
		
		final List<Entry> entries = setupEntries(new String[]{firstQuestionText, secondQuestionText});
		setupDocumentWithEntries(documents, "Question Set One", entries, new DocumentOccurrence());
		
		setupDocument(documents, "Question Set Two");
		
		List<Entry> timingsEntries = setupEntries(new String[]{"timings1"});
		setupDocumentWithEntries(documents, "Timings", timingsEntries, new DocumentOccurrence());
		
		dataSet.setDocuments(documents);
		dataSet.setId((long) 1);
		
		return dataSet;
	}
	
	public static Record setupRecord(DataSet dataSet) {
		Record record = new Record();
		RecordData recordData = new RecordData();
		recordData.setScheduleStartDate(new Date(10));	// Date well in the past so won't be active in study
		record.setTheRecordData(recordData);
		Identifier identifier = new Identifier();
		identifier.setIdentifier("Test");
		record.setIdentifier(identifier);
		record.setDataSet(dataSet);
		
		return record;
	}
	
	/**
	 * Always returns the first event as the current event number
	 */
	public static AlarmTimes stubAlarmTimes() {
		AlarmTimes alarmTimes = mock(AlarmTimes.class);
		when(alarmTimes.getCurrentEventNumber(any(Date.class))).thenReturn(0);
		
		return alarmTimes;
	}
	
	public static ParticipantInterface stubParticipantInterface() {
		ParticipantInterface participantInterface = mock(ParticipantInterface.class);
		
		Subject subject = new Subject("1234");
		when(participantInterface.getParticipantByMobileNumber(anyString(), anyString(), anyString())).thenReturn(subject);
		
		return participantInterface;
	}
	
	/**
	 * Stubs out the Repository class
	 * @param record The record to return when a record is requested
	 * @param dataSet The DataSet to return when a dataSet is requested
	 * @return
	 */
	public static Repository stubRepository(Record record, DataSet dataSet) {
		Repository repository = mock(Repository.class);

		try {
			when(repository.getRecordComplete(anyString(), anyString())).thenReturn(record.toDTO());
			when(repository.saveRecord(any(RecordDTO.class), anyBoolean(), anyString())).thenAnswer(new Answer<Object>(){
				public Object answer(InvocationOnMock invocation) {
					Object[] args = invocation.getArguments();
					recordResult = (RecordDTO) args[0];
					return 1;
				}
			});
			when(repository.getDataSetSummaryWithDocs(anyString(), anyString())).thenReturn(dataSet.toDTO());
			when(repository.getDataSetComplete(anyLong(), anyString())).thenReturn(dataSet.toDTO());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return repository;
	}
	
	private static List<Entry> setupEntries(String[] entryText) {
		List<Entry> entryList = new ArrayList<Entry>();
		
		Section section = new Section();
		section.addOccurrence(new SectionOccurrence());
		
		for(String text : entryText) {
			Entry entry = new TextEntry();
			entry.setDisplayText(text);
			entry.setSection(section);
			entryList.add(entry);
		}

		return entryList;
	}
	
	private static void setupDocument(List<Document> documents, String documentName) {
		final Document newDocument = new Document(documentName);
		documents.add(newDocument);
	}
	
	private static void setupDocumentWithEntries(List<Document> documents, String documentName, List<Entry> entries, DocumentOccurrence occurrence) {
		final Document newDocument = new Document(documentName);
		newDocument.setEntries(entries);
		newDocument.addOccurrence(occurrence);
		documents.add(newDocument);
	}
}
