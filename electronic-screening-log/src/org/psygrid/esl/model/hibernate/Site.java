package org.psygrid.esl.model.hibernate;

import java.util.Map;

import org.psygrid.esl.model.IPersistent;
import org.psygrid.esl.model.ISite;

public class Site /*implements ISite*/ {

	private String siteId = null;
	private String siteName = null;
	private String geographicCode = null;
	
	public Site(){
		
	}
	
	public String getGeographicCode() {
		return geographicCode;
	}

	public String getSiteId() {
		return siteId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setGeographicCode(String geographicCode) {
		this.geographicCode = geographicCode;

	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;

	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

}
