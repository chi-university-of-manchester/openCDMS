package org.psygrid.data.sampletracking.server.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all samples for a record.
 * 
 * @author Terry Child
 * @hibernate.class table="t_sampletracking_participant"
 */
public class Participant {

	private Long id;
	private String recordID; 
	private String projectCode; 
	private String identifier;
	
	private List<Sample> samples = new ArrayList<Sample>();

	protected Participant() {
	}

	public Participant(String projectCode,String recordID,String identifier) {
		this.projectCode=projectCode;
		this.recordID=recordID;
		this.identifier=identifier;
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
	 * @return the recordID
	 * @hibernate.property column="c_record_id"
     *                     unique="true"
     *                     not-null="true"
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
	 * @hibernate.property column="c_project_code"
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
     * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.data.sampletracking.server.model.Sample" 
     * @hibernate.key column="c_participant_id" not-null="true"
     * @hibernate.list-index column="c_index"
     */
    public List<Sample> getSamples() {
        return samples;
    }    

	public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

}
