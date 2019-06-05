package org.psygrid.esl.model;

public interface ISite {
	
	/**
	 * Get the siteName
	 * 
	 * @return The siteName.
	 */
	public String getSiteName();

	/**
	 * Set the siteName
	 * 
	 * @param siteName The group name.
	 */
	public void setSiteName(String siteName);

	/**
	 * Get the siteId
	 * 
	 * @return The siteId.
	 */
	public String getSiteId();

	/**
	 * Set the siteId
	 * 
	 * @param siteId The group name.
	 */
	public void setSiteId(String siteId);


	/**
	 * Get the geographicCode
	 * 
	 * @return The geographicCode.
	 */
	public String getGeographicCode();

	/**
	 * Set the geographicCode
	 * 
	 * @param geographicCode The group name.
	 */
	public void setGeographicCode(String geographicCode);
	
}
