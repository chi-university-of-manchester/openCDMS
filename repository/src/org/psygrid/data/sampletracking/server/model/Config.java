package org.psygrid.data.sampletracking.server.model;

import java.util.ArrayList;
import java.util.List;


/**
 * Holds the sample tracking configuration for a dataset.
 * 
 * @author Terry
 * @hibernate.class table="t_sampletracking_config"
 */
public class Config {

	private Long id;
	private String projectCode;
	private boolean tracking;
	private boolean autoParticipantID;
	private boolean usingExternalID;
	private String participantRegex;
	private String participantRegexDescription;

	private boolean autoSampleID;
	private String sampleRegex;
	private String sampleRegexDescription;
	private long sampleCounter = 0;	
	private String separator;
	
	private int labelWidth;
	private int labelHeight;
	private int labelFontSize;
	private boolean printBarcodes;
	
	private List<String> sampleTypes = new ArrayList<String>();
	private List<String> tubeTypes = new ArrayList<String>();
	private List<String> statuses = new ArrayList<String>();

	public Config() {}

	/**
	 * @param datasetID
	 * @param tracking
	 * @param tubeTypes
	 * @param statuses
	 */

	public void setAll(String projectCode, boolean tracking, boolean autoParticipantID,boolean usingExternalID,boolean autoSampleID,
			List<String> sampleTypes,List<String> tubeTypes, List<String> statuses,String separator,
			int labelWidth,int labelHeight,int labelFontSize,boolean printBarcodes,String participantRegex,String sampleRegex,
			String participantRegexDescription,String sampleRegexDescription) {
		this.projectCode = projectCode;
		this.tracking = tracking;
		this.autoParticipantID=autoParticipantID;
		this.usingExternalID=usingExternalID;
		this.autoSampleID=autoSampleID;
		this.sampleTypes=sampleTypes;
		this.tubeTypes = tubeTypes;
		this.statuses = statuses;
		this.separator=separator;
		this.labelWidth=labelWidth;
		this.labelHeight=labelHeight;
		this.labelFontSize=labelFontSize;
		this.printBarcodes=printBarcodes;
		this.participantRegex=participantRegex;
		this.sampleRegex=sampleRegex;
		this.participantRegexDescription=participantRegexDescription;
		this.sampleRegexDescription=sampleRegexDescription;
	}
	
	
	/**
	 * @param datasetID
	 * @param tracking
	 */
	
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
	 * @return the tracking
	 * @hibernate.property column="c_tracking"
	 */
	public boolean getTracking() {
		return tracking;
	}

	/**
	 * @param tracking the tracking to set
	 */
	public void setTracking(boolean tracking) {
		this.tracking = tracking;
	}

	
	/**
	 * @return the autoParticipantID
	 * @hibernate.property column="c_auto_participant_id"
	 */
	public boolean isAutoParticipantID() {
		return autoParticipantID;
	}

	/**
	 * @param autoParticipantID the autoParticipantID to set
	 */
	public void setAutoParticipantID(boolean autoParticipantID) {
		this.autoParticipantID = autoParticipantID;
	}

	/**
	 * @return the usingExternalID
	 * @hibernate.property column="c_using_externalid"
	 */
	public boolean isUsingExternalID() {
		return usingExternalID;
	}

	/**
	 * @param usingExternalID the usingExternalID to set
	 */
	public void setUsingExternalID(boolean usingExternalID) {
		this.usingExternalID = usingExternalID;
	}

	/**
	 * @hibernate.list cascade="all" table="t_sampletracking_sampletypes"
     * @hibernate.key column="c_config_id" not-null="true"
     * @hibernate.element column="c_sampletype" type="string"
     * @hibernate.list-index column="c_index"
	 * @return the sampleTypes
	 */
	public List<String> getSampleTypes() {
		return sampleTypes;
	}

	/**
	 * @param tubeTypes the tubeTypes to set
	 */
	public void setSampleTypes(List<String> sampleTypes) {
		this.sampleTypes = sampleTypes;
	}
	
	/**
	 * @hibernate.list cascade="all" table="t_sampletracking_tubetypes"
     * @hibernate.key column="c_config_id" not-null="true"
     * @hibernate.element column="c_tubetype" type="string"
     * @hibernate.list-index column="c_index"
	 * @return the tubeTypes
	 */
	public List<String> getTubeTypes() {
		return tubeTypes;
	}

	/**
	 * @param tubeTypes the tubeTypes to set
	 */
	public void setTubeTypes(List<String> tubeTypes) {
		this.tubeTypes = tubeTypes;
	}

	/**
	 * @hibernate.list cascade="all" table="t_sampletracking_statuses"
     * @hibernate.key column="c_config_id" not-null="true"
     * @hibernate.element column="c_status" type="string"
     * @hibernate.list-index column="c_index"
	 * @return the statuses
	 */
	public List<String> getStatuses() {
		return statuses;
	}

	/**
	 * @param statuses the statuses to set
	 */
	public void setStatuses(List<String> statuses) {
		this.statuses = statuses;
	}

	/**
	 * @return the sampleCounter
	 * @hibernate.property column="c_sample_counter"
	 */
	public long getSampleCounter() {
		return sampleCounter;
	}

	/**
	 * @param sampleCounter the sampleCounter to set
	 */
	public void setSampleCounter(long sampleCounter) {
		this.sampleCounter = sampleCounter;
	}

	/**
	 * @return the separator
	 * @hibernate.property column="c_separator"
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * @param separator the separator to set
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * @return the labelWidth
	 * @hibernate.property column="c_label_width"
	 */
	public int getLabelWidth() {
		return labelWidth;
	}

	/**
	 * @param labelWidth the labelWidth to set
	 */
	public void setLabelWidth(int labelWidth) {
		this.labelWidth = labelWidth;
	}

	/**
	 * @return the labelHeight
	 * @hibernate.property column="c_label_height"
	 */
	public int getLabelHeight() {
		return labelHeight;
	}

	/**
	 * @param labelHeight the labelHeight to set
	 */
	public void setLabelHeight(int labelHeight) {
		this.labelHeight = labelHeight;
	}

	/**
	 * @return the labelFontSize
	 * @hibernate.property column="c_font_size"
	 */
	public int getLabelFontSize() {
		return labelFontSize;
	}

	/**
	 * @param labelFontSize the labelFontSize to set
	 */
	public void setLabelFontSize(int labelFontSize) {
		this.labelFontSize = labelFontSize;
	}

	/**
	 * @return the printBarcodes
	 * @hibernate.property column="c_print_barcodes"
	 */
	public boolean isPrintBarcodes() {
		return printBarcodes;
	}

	/**
	 * @param printBarcodes the printBarcodes to set
	 */
	public void setPrintBarcodes(boolean printBarcodes) {
		this.printBarcodes = printBarcodes;
	}

	/**
	 * @return the participantRegex
	 * @hibernate.property column="c_participant_regex"
	 */
	public String getParticipantRegex() {
		return participantRegex;
	}

	/**
	 * @param participantRegex the participantRegex to set
	 */
	public void setParticipantRegex(String participantRegex) {
		this.participantRegex = participantRegex;
	}

	/**
	 * @return the participant Regex Description
	 * @hibernate.property column="c_participant_regex_desc"
	 */
	public String getParticipantRegexDescription() {
		return participantRegexDescription;
	}

	/**
	 * 
	 * @param participantRegexDescription
	 */
	public void setParticipantRegexDescription(String participantRegexDescription) {
		this.participantRegexDescription = participantRegexDescription;
	}

	/**
	 * @return the autoSampleID
	 * @hibernate.property column="c_auto_sample_id"
	 */
	public boolean isAutoSampleID() {
		return autoSampleID;
	}

	/**
	 * @param autoSampleID the autoSampleID to set
	 */
	public void setAutoSampleID(boolean autoSampleID) {
		this.autoSampleID = autoSampleID;
	}

	/**
	 * @return the sampleRegex
	 * @hibernate.property column="c_sample_regex"
	 */
	public String getSampleRegex() {
		return sampleRegex;
	}

	/**
	 * @param sampleRegex the sampleRegex to set
	 */
	public void setSampleRegex(String sampleRegex) {
		this.sampleRegex = sampleRegex;
	}

	/**
	 * @return the sampleRegexDescription
	 * @hibernate.property column="c_sample_regex_description"
	 */
	public String getSampleRegexDescription() {
		return sampleRegexDescription;
	}

	/**
	 * @param sampleRegexDescription the sampleRegexDescription to set
	 */
	public void setSampleRegexDescription(String sampleRegexDescription) {
		this.sampleRegexDescription = sampleRegexDescription;
	}

	
	
}


