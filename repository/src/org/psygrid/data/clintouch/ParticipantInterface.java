package org.psygrid.data.clintouch;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.IntegerValue;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.psygrid.data.utils.esl.EslException;
import org.psygrid.data.utils.esl.IRemoteClient;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.hibernate.Subject;


/**
 * Used to retrieve information relating to participants, e.g. list of
 * participants for which alarms are active
 * @author MattMachin
 * @version 1.0
 * @created 05-Sep-2011 11:20:01
 */
public class ParticipantInterface {
	private static Log sLog = LogFactory.getLog(ParticipantInterface.class);
	
	private IRemoteClient eslClient;
	
	public void setEslClient(IRemoteClient eslClient) {
		this.eslClient = eslClient;
	}

	public ISubject getParticipantByMobileNumber(String mobileNumber, String projectCode, String saml) {
		Subject subjectToSearchFor = new Subject();
		subjectToSearchFor.setMobilePhone(mobileNumber);
		IProject project;
		List<ISubject> subjectResults = null;
		try {
			project = eslClient.retrieveProjectByCode(projectCode, saml);
			subjectResults = eslClient.findSubjectByExample(project, subjectToSearchFor, saml);
		} catch (EslException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(subjectResults.size() == 0) {
			// No participant is registered with the mobile number. Just log the error
			sLog.info("Received message from participant when not registered on study. Mobile number was " + mobileNumber);
			return null;
		}
		
		// There should only be 1 match as mobile number is checked as unique upon entry to participant register 
		return subjectResults.get(0);
	}
	
	public boolean isParticipantActiveInStudy(Record record, DataSet dataSet) {
		Date startDate = record.getTheRecordData().getScheduleStartDate();
		Date endDate = getEndDate(record, dataSet);
		Date currentDate = new Date();
		
		if(currentDate.after(startDate) && currentDate.before(endDate)) {
			return true;
		}
		
		return false;
	}
	
	public String getMobileNumberForParticipant(Record record, String saml) {
		ISubject subject = null;
		try {
			IProject project = eslClient.retrieveProjectByCode(record.getDataSet().getProjectCode(), saml);
			subject = eslClient.retrieveSubjectByStudyNumber(project, record.getIdentifier().getIdentifier(), saml);
		} catch (EslException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(subject != null) {
			return subject.getMobilePhone();
		}
		
		return null;
	}

	private Date getEndDate(Record record, DataSet dataSet) {
		int duration = getDurationFromRecord(record, dataSet);
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(record.getTheRecordData().getScheduleStartDate());
		endDate.add(Calendar.DATE, duration);
		
		return endDate.getTime();
	}
	
	private int getDurationFromRecord(Record record, DataSet dataSet) {
		Document document = dataSet.getDocument("Setup");
		DocumentOccurrence documentOccurrence = document.getOccurrence(0);
		DocumentInstance documentInstance = record.getDocumentInstance(documentOccurrence);
		Entry entry = document.getEntry("Duration");
		Section section = entry.getSection();
		SectionOccurrence sectionOccurrence = section.getOccurrence(0);
		BasicResponse durationResponse = (BasicResponse)documentInstance.getResponse(entry, sectionOccurrence);
		IntegerValue durationValue = (IntegerValue) durationResponse.getTheValue();
		
		return durationValue.getValue();
	}
}