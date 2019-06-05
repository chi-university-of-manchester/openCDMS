package org.psygrid.data.importing;

/**
 * Holds the data needed for an import.
 * 
 * @author Terry
 *
 */
public final class ImportData {
			
	/**
	 * The project code of the project.
	 */
	private String projectCode;
		
	/**
	 * remoteFilePath
	 */
	private String remoteFilePath;

	/**
	 * The import data.
	 */
	private String data;
	
	/**
	 * The type of data for this import - this is used by
	 * the import plug-in to identify the document(s) to populate.
	 */
	private String dataType;

	/**
	 * The user requesting the import.
	 */
	private String user;
	
	/**
	 * Needs an empty constructor to be a bean.
	 */
	public ImportData(){}

	/**
	 * @param projectCode
	 * @param remoteFilePath
	 * @param data
	 * @param dataType
	 * @param user
	 */
	public ImportData(String projectCode, String remoteFilePath, String data,
			String dataType, String user) {
		super();
		this.projectCode = projectCode;
		this.remoteFilePath = remoteFilePath;
		this.data = data;
		this.dataType = dataType;
		this.user = user;
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
	 * @return the remoteFilePath
	 */
	public String getRemoteFilePath() {
		return remoteFilePath;
	}

	/**
	 * @param remoteFilePath the remoteFilePath to set
	 */
	public void setRemoteFilePath(String remoteFilePath) {
		this.remoteFilePath = remoteFilePath;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
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

		
}

