package org.psygrid.data.sampletracking.server.model;

import java.util.Date;

/**
 * Holds the information for a single revision of a Sample.
 * @see Sample
 * @author Terry
 * @hibernate.class table="t_sampletracking_sample_revision"
 */
public class SampleRevision {

	private Long id;
	
	Sample sample;

	private String user;
	private Date timestamp;
	private String participantIdentifier;
	private String identifier;
	private String status;
	private String sampleType;	
	private String tubeType;
	private String trackingID;
	private Date sampleDate;
	private String comment;
	
	protected SampleRevision(){	
	}
	
	
	/**
	 * @param user
	 * @param timestamp
	 * @param label
	 * @param status
	 * @param tube
	 */
	public SampleRevision(String user, Date timestamp, String participantIdentifier,String identifier,
			String status, String sampleType,String tubeType,String trackingID,Date sampleDate,
			String comment) {
		this.user = user;
		this.timestamp = timestamp;
		this.participantIdentifier = participantIdentifier;
		this.identifier = identifier;
		this.status = status;
		this.sampleType=sampleType;
		this.tubeType = tubeType;
		this.trackingID=trackingID;
		this.sampleDate=sampleDate;
		this.comment=comment;
	}


	/**
	 * @return the id
	 * @hibernate.id column = "c_id" generator-class="native"
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	protected void setId(Long id) {
		this.id = id;
	}

	
	/**
	 * @return the sample
	 * @hibernate.many-to-one class="org.psygrid.data.sampletracking.server.model.Sample"
     *                        column="c_sample_id"
     *                        not-null="true"
     *                        insert="false"
     *                        update="false"
	 */
	public Sample getSample() {
		return sample;
	}

	/**
	 * @param sample the sample to set
	 */
	public void setSample(Sample sample) {
		this.sample = sample;
	}

	/**
	 * @return the user
	 * @hibernate.property column="c_user"
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
	 * @return the participantID
	 * @hibernate.property column="c_participant_identifier"
	 */
	public String getParticipantIdentifier() {
		return participantIdentifier;
	}


	/**
	 * @param participantID the participantID to set
	 */
	public void setParticipantIdentifier(String participantIdentifier) {
		this.participantIdentifier = participantIdentifier;
	}


	/**
	 * @return the identifier
	 * @hibernate.property column="c_identifier"
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


	/**
	 * @return the timestamp
	 * @hibernate.property column="c_timestamp"
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
	 * @return the status
	 * @hibernate.property column="c_status"
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
	 * @return the sampleType
	 * @hibernate.property column="c_sampletype"
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
	 * @return the tube
	 * @hibernate.property column="c_tubetype"
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
	 * @return the trackingID
	 * @hibernate.property column="c_tracking_id"
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
	 * @return the sampleDate
	 * @hibernate.property column="c_sampledate"
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
	 * @return the comment
	 * @hibernate.property column="c_comment"
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




