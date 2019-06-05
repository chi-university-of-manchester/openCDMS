package org.psygrid.data.sampletracking;

import java.util.Date;

/**
 * Holds information about a sample tracking participant.
 * 
 * At the moment this is used to store an identifier for the purposes
 * of sample tracking - which may be different to the participant's record identifier.
 * 
 * @author Terry
 * 
 */
public final class ParticipantInfo {
	
	private String recordID; 
	private String projectCode; 
	private String identifier;


	public ParticipantInfo() {
	}

	/**
	 * @param recordID
	 * @param projectCode
	 * @param identifier
	 */
	public ParticipantInfo(String recordID, String projectCode, String identifier) {
		super();
		this.recordID = recordID;
		this.projectCode = projectCode;
		this.identifier = identifier;
	}

	/**
	 * @return the recordID
	 */
	public String getRecordID() {
		return recordID;
	}


	/**
	 * @param recordID the recordID to set
	 */
	public void setRecordID(String recordID) {
		this.recordID = recordID;
	}


	/**
	 * @return the projectCode
	 */
	public String getProjectCode() {
		return projectCode;
	}


	/**
	 * @param projectCode the projectCode to set
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}


	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}


	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}


	
}



