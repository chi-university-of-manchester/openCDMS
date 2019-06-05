package org.psygrid.data.sampletracking;

/**
 * Holds the sample tracking configuration for a dataset.
 * 
 * @author Terry
 *
 */
public final class ConfigInfo {
	
	private String projectCode;
	private boolean tracking;
	private boolean autoParticipantID;
	private boolean usingExternalID;
	private String participantRegex;
	private String participantRegexDescription;

	private boolean autoSampleID;
	private String sampleRegex;
	private String sampleRegexDescription;
	private String separator;
	
	private String[] sampleTypes = new String[]{"Blood","Plasma","Serum","Tissue"};
	private String[] tubeTypes = new String[]{"Clear","Red","Green","Blue"};
	private String[] statuses = new String[]{"Allocated","Despatched","Received"};

	private int labelWidth;
	private int labelHeight;
	private int labelFontSize;
	private boolean printBarcodes;
	
	/**
	 * Needs an empty constructor to be a bean.
	 */
	public ConfigInfo(){};
		
	/**
	 * @param projectCode
	 * @param tracking
	 * @param autoParticipantID
	 * @param usingExternalID
	 * @param participantRegex
	 * @param participantRegexDescription
	 * @param autoSampleID
	 * @param sampleRegex
	 * @param sampleRegexDescription
	 * @param separator
	 * @param sampleTypes
	 * @param tubeTypes
	 * @param statuses
	 * @param labelWidth
	 * @param labelHeight
	 * @param labelFontSize
	 * @param printBarcodes
	 */
	public ConfigInfo(String projectCode, boolean tracking,
			boolean autoParticipantID, boolean usingExternalID,
			String participantRegex, String participantRegexDescription,
			boolean autoSampleID, String sampleRegex,
			String sampleRegexDescription, String separator,
			String[] sampleTypes, String[] tubeTypes, String[] statuses,
			int labelWidth, int labelHeight, int labelFontSize,
			boolean printBarcodes) {
		super();
		this.projectCode = projectCode;
		this.tracking = tracking;
		this.autoParticipantID = autoParticipantID;
		this.usingExternalID = usingExternalID;
		this.participantRegex = participantRegex;
		this.participantRegexDescription = participantRegexDescription;
		this.autoSampleID = autoSampleID;
		this.sampleRegex = sampleRegex;
		this.sampleRegexDescription = sampleRegexDescription;
		this.separator = separator;
		this.sampleTypes = sampleTypes;
		this.tubeTypes = tubeTypes;
		this.statuses = statuses;
		this.labelWidth = labelWidth;
		this.labelHeight = labelHeight;
		this.labelFontSize = labelFontSize;
		this.printBarcodes = printBarcodes;
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
	 * @return the tracking
	 */
	public boolean isTracking() {
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
	 * @return the participantRegex
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
	 * @return the participantRegexDescription
	 */
	public String getParticipantRegexDescription() {
		return participantRegexDescription;
	}

	/**
	 * @param participantRegexDescription the participantRegexDescription to set
	 */
	public void setParticipantRegexDescription(String participantRegexDescription) {
		this.participantRegexDescription = participantRegexDescription;
	}

	/**
	 * @return the autoSampleID
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

	/**
	 * @return the separator
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
	 * @return the sampleTypes
	 */
	public String[] getSampleTypes() {
		return sampleTypes;
	}

	/**
	 * @param sampleTypes the sampleTypes to set
	 */
	public void setSampleTypes(String[] sampleTypes) {
		this.sampleTypes = sampleTypes;
	}

	/**
	 * @return the tubeTypes
	 */
	public String[] getTubeTypes() {
		return tubeTypes;
	}

	/**
	 * @param tubeTypes the tubeTypes to set
	 */
	public void setTubeTypes(String[] tubeTypes) {
		this.tubeTypes = tubeTypes;
	}

	/**
	 * @return the statuses
	 */
	public String[] getStatuses() {
		return statuses;
	}

	/**
	 * @param statuses the statuses to set
	 */
	public void setStatuses(String[] statuses) {
		this.statuses = statuses;
	}

	/**
	 * @return the labelWidth
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

		
	
}

