package org.psygrid.data.sampletracking;

import java.util.Date;

/**
 * Holds information about a sample (e.g. blood,plasma,tissue etc.)
 * 
 * @author Terry
 * 
 */
public final class SampleInfo {
	
	private Long ID;
	private String recordID; 
	
	private String user;
	private Date timestamp;

	private String participantID;
	private String sampleID;
	private String status;
	private String sampleType;
	private String tubeType;
	private String trackingID;
	private Date sampleDate;
	private String comment;

	public SampleInfo() {
	}

	// TODO: Call the other constructor
	public SampleInfo(String recordID, String participantID,String sampleID, 
			String status, String sampleType,String tubeType,String trackingID, Date sampleDate,
			String comment) {
		this.ID = null;
		this.recordID = recordID;
		this.user=null;
		this.timestamp = null;
		this.participantID = participantID;
		this.sampleID = sampleID;
		this.status = status;
		this.sampleType=sampleType;
		this.tubeType = tubeType;
		this.trackingID=trackingID;
		this.sampleDate=sampleDate;
		this.comment=comment;
	}
	
	public SampleInfo(Long ID,String recordID, String user,
			Date timestamp, String participantID,String sampleID, String status, String sampleType,String tubeType,String trackingID,
			Date sampleDate, String comment) {
		this.ID = ID;
		this.recordID = recordID;
		this.user = user;
		this.timestamp = timestamp;
		this.participantID = participantID;
		this.sampleID = sampleID;
		this.status = status;
		this.sampleType = sampleType;
		this.tubeType = tubeType;
		this.trackingID=trackingID;
		this.sampleDate=sampleDate;
		this.comment=comment;
	}

	/**
	 * The system generate unique identifier for the sample.
	 * 
	 * @return the sampleID
	 */
	public Long getID() {
		return ID;
	}

	/**
	 * @param sampleID the sampleID to set
	 */
	public void setID(Long ID) {
		this.ID = ID;
	}

	/**
	 * The record identifier associated with this sample.
	 * 
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
	 * The user who saved or updated the sample information.
	 * 
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * The time that this sample information was saved or updated in the database.
	 * 
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * The participant identifier of the participant when the sample was created.
	 * 
	 * The participant identifier may change but the one associated with existing
	 * samples will not change.
	 * 
	 * @return the participantID
	 */
	public String getParticipantID() {
		return participantID;
	}

	/**
	 * @param participantID the participantID to set
	 */
	public void setParticipantID(String participantID) {
		this.participantID = participantID;
	}

	/**
	 * An identifier for the sample - this may be generated from the participant identifier
	 * depending on how sample tracking is configured for a given study.
	 * 
	 * @return the sampleID
	 */
	public String getSampleID() {
		return sampleID;
	}

	/**
	 * @param sampleID the sampleID to set
	 */
	public void setSampleID(String sampleID) {
		this.sampleID = sampleID;
	}

	/**
	 * The sample status e.g. ALLOCATED,DESPATCHED,etc.
	 * 
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * The type of sample e.g. Blood.
	 * 
	 * @return the sampleType
	 */
	public String getSampleType() {
		return sampleType;
	}

	/**
	 * @param sampleType the sampleType to set
	 */
	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	/**
	 * The type of the sample tube e.g. 'Red Topped'.
	 * 
	 * @return the tubeType
	 */
	public String getTubeType() {
		return tubeType;
	}

	/**
	 * @param tubeType the tubeType to set
	 */
	public void setTubeType(String tubeType) {
		this.tubeType = tubeType;
	}

	/**
	 * A parcel tracking identifier used when shipping samples.
	 * @return the trackingID
	 */
	public String getTrackingID() {
		return trackingID;
	}

	/**
	 * @param trackingID the trackingID to set
	 */
	public void setTrackingID(String trackingID) {
		this.trackingID = trackingID;
	}

	/**
	 * The date that the sample was taken.
	 * 
	 * @return the sampleDate
	 */
	public Date getSampleDate() {
		return sampleDate;
	}

	/**
	 * @param sampleDate the sampleDate to set
	 */
	public void setSampleDate(Date sampleDate) {
		this.sampleDate = sampleDate;
	}

	/**
	 * A used entered comment.
	 * 
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
}



