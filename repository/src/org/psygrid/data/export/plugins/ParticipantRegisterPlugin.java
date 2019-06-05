package org.psygrid.data.export.plugins;

import java.net.ConnectException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.utils.esl.EslException;
import org.psygrid.data.utils.esl.IRemoteClient;
import org.psygrid.data.utils.wrappers.AAQCWrapper;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.ISubject;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.utils.SAMLUtilities;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

public class ParticipantRegisterPlugin implements ExportPlugin {
		
	/*
	 * Whether the user has the necessary permission to export the PR
	 */
	private boolean hasPermission; 
	
	private static final Log LOG = LogFactory.getLog(ParticipantRegisterPlugin.class);
	
	private static final String PARTICIPANT_IDENTIFIER_COLUMN = "Participant Identifier";
	private static final String CENTRE_NUMBER_COLUMN = "Centre Number";
	private static final String TITLE_COLUMN = "Title";
	private static final String FORENAME_COLUMN = "Forename";
	private static final String SURNAME_COLUMN = "Surname";
	private static final String SEX_COLUMN = "Sex";
	private static final String DATE_OF_BIRTH_COLUMN = "Date of Birth";
	private static final String ADDRESS_LINE1_COLUMN = "Address Line 1";
	private static final String ADDRESS_LINE2_COLUMN = "Address Line 2";
	private static final String ADDRESS_LINE3_COLUMN = "Address Line 3";
	private static final String CITY_COLUMN = "City";
	private static final String REGION_COLUMN = "Region";
	private static final String COUNTRY_COLUMN = "Country";
	private static final String POSTCODE_COLUMN = "Postcode";
	private static final String EMAIL_ADDRESS_COLUMN = "E-mail Address";
	private static final String HOME_PHONE_COLUMN = "Home Phone";
	private static final String WORK_PHONE_COLUMN = "Work Phone";
	private static final String MOBILE_PHONE_COLUMN = "Mobile Phone";
	private static final String NHS_NUMBER_COLUMN = "NHS Number";
	private static final String HOSPITAL_NUMBER_COLUMN = "Hospital Number";
	private static final String RISK_NUMBER_COLUMN = "Risk Number";
	
	private static final String[] COLUMNS = {
		PARTICIPANT_IDENTIFIER_COLUMN,
		CENTRE_NUMBER_COLUMN,
		TITLE_COLUMN,
		FORENAME_COLUMN,
		SURNAME_COLUMN,
		SEX_COLUMN,
		DATE_OF_BIRTH_COLUMN,
		ADDRESS_LINE1_COLUMN,
		ADDRESS_LINE2_COLUMN,
		ADDRESS_LINE3_COLUMN,
		CITY_COLUMN,
		REGION_COLUMN,
		COUNTRY_COLUMN,
		POSTCODE_COLUMN,
		EMAIL_ADDRESS_COLUMN,
		HOME_PHONE_COLUMN,
		WORK_PHONE_COLUMN,
		MOBILE_PHONE_COLUMN,
		NHS_NUMBER_COLUMN,
		HOSPITAL_NUMBER_COLUMN,
		RISK_NUMBER_COLUMN
	};
	
	private AAQCWrapper aaqc;

	private IRemoteClient eslClient;

	/**
	 * Wired in application context.
	 * @param aaqc the aaqc to set
	 */
	public void setAaqc(AAQCWrapper aaqc) {
		this.aaqc = aaqc;
	}

	/**
	 * Wired in application context.
	 * @param eslClient the eslClient to set
	 */
	public void setEslClient(IRemoteClient eslClient) {
		this.eslClient = eslClient;
	}

	public boolean isApplicable(ExportRequest request) {
		return request.isParticipantRegister();
	}
	
	/**
	 * If the user doesn't have permission to export the PR, don't return the column names either
	 * @see org.psygrid.data.export.plugins.ExportPlugin#getColumnNames()
	 */
	public String[] getColumnNames() {
		if(hasPermission) {
			return COLUMNS;
		}
		return new String[]{};
	}
	
	public Properties getResults(Record record,ExportRequest request) {
		Properties results = new Properties();
		ISubject subject = null;
		try {
			String saml = aaqc.getSAMLAssertion(request.getRequestor());
			hasPermission = false;
			if(checkAuthorisation(record, saml)) {
				hasPermission = true;
				IProject project = eslClient.retrieveProjectByCode(record.getDataSet().getProjectCode(), saml);
				subject = eslClient.retrieveSubjectByStudyNumber(project,record.getIdentifier().getIdentifier(), saml);
				if(isRecordInCorrectStateForExport(record)) {
					exportParticipantRegister(results, subject);
				}
			}
		} catch (NotAuthorisedFaultMessage e) {
			LOG.error("User not authorised problem when attempting to export PR data for export request"+request.getId(),e);
		} catch (ConnectException e) {
			LOG.error("Connection problem when attempting to export PR data for export request"+request.getId(),e);
		} catch (PGSecurityException e) {
			LOG.error("Security problem when attempting to export PR data for export request"+request.getId(),e);
		} catch (PGSecurityInvalidSAMLException e) {
			LOG.error("User not authorised problem when attempting to export PR data for export request"+request.getId(),e);
		} catch (PGSecuritySAMLVerificationException e) {
			LOG.error("User not authorised problem when attempting to export PR data for export request"+request.getId(),e);
		} catch (EslException e) {
			LOG.error("ESL problem when attempting to export PR data for export request"+request.getId(),e);
		}
		
		return results;
	}

	private void exportParticipantRegister(Properties results, ISubject subject) {
		results.put(PARTICIPANT_IDENTIFIER_COLUMN, getBlankStringIfNull(subject.getStudyNumber()));
		results.put(CENTRE_NUMBER_COLUMN, getBlankStringIfNull(subject.getCentreNumber()));
		results.put(TITLE_COLUMN, getBlankStringIfNull(subject.getTitle()));
		results.put(FORENAME_COLUMN, getBlankStringIfNull(subject.getFirstName()));
		results.put(SURNAME_COLUMN, getBlankStringIfNull(subject.getLastName()));
		results.put(SEX_COLUMN, getBlankStringIfNull(subject.getSex()));
		results.put(DATE_OF_BIRTH_COLUMN, getDateAsStringBlankIfNull(subject.getDateOfBirth()));
		results.put(ADDRESS_LINE1_COLUMN, getBlankStringIfNull(subject.getAddress().getAddress1()));
		results.put(ADDRESS_LINE2_COLUMN, getBlankStringIfNull(subject.getAddress().getAddress2()));
		results.put(ADDRESS_LINE3_COLUMN, getBlankStringIfNull(subject.getAddress().getAddress3()));
		results.put(CITY_COLUMN, getBlankStringIfNull(subject.getAddress().getCity()));
		results.put(REGION_COLUMN, getBlankStringIfNull(subject.getAddress().getRegion()));
		results.put(COUNTRY_COLUMN, getBlankStringIfNull(subject.getAddress().getCountry()));
		results.put(POSTCODE_COLUMN, getBlankStringIfNull(subject.getAddress().getPostCode()));
		results.put(EMAIL_ADDRESS_COLUMN, getBlankStringIfNull(subject.getEmailAddress()));
		results.put(HOME_PHONE_COLUMN, getBlankStringIfNull(subject.getAddress().getHomePhone()));
		results.put(WORK_PHONE_COLUMN, getBlankStringIfNull(subject.getWorkPhone()));
		results.put(MOBILE_PHONE_COLUMN, getBlankStringIfNull(subject.getMobilePhone()));
		results.put(NHS_NUMBER_COLUMN, getBlankStringIfNull(subject.getNhsNumber()));
		results.put(HOSPITAL_NUMBER_COLUMN, getBlankStringIfNull(subject.getHospitalNumber()));
		results.put(RISK_NUMBER_COLUMN, getBlankStringIfNull(subject.getRiskIssues()));
	}
	
	private String getBlankStringIfNull(String data) {
		if(data == null) {
			return "";
		}
		return data;
	}
	
	private String getDateAsStringBlankIfNull(Date data) {
		if(data == null) {
			return "";
		}
		return data.toString();
	}
	
	private boolean checkAuthorisation(Record record, String saml) {
		try {
			aaqc.authoriseUser(SAMLUtilities.getUserFromSAML(saml), RBACAction.ACTION_ESL_EXPORT.toAEFAction(), new ProjectType(null, record.getDataSet().getProjectCode(), null, null, false), new GroupType(null, record.getIdentifier().getGroupPrefix(), null), saml);
		} catch (Exception ex) {
			// Any exception means user is not authorised
			LOG.error("User attempted to export PR when not authorised to do so", ex);
			return false;
		}
		return true;
	}
	
	/**
	 * Don't export records that are in the LEFT generic state. 
	 * @param record 	The record that is due to be exported
	 * @return 			true if he record should be exported, false if not
	 */
	private boolean isRecordInCorrectStateForExport(Record record) {
		if(record.getStatus().getGenericState() == GenericState.LEFT ) {
			return false;
		}
		return true;
	}

}
