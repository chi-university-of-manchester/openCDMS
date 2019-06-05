package org.psygrid.esl.model.hibernate;

import java.util.Map;

import org.psygrid.esl.model.ICustomEmailInfo;
import org.psygrid.esl.model.IPersistent;

/**
 * Contains custom email info. This is meant to be expanded as
 * different types of custom information are required.
 * @author williamvance
 *
 */
public class CustomEmailInfo  /*implements ICustomEmailInfo*/ {

	private Site site = null;
	
	public CustomEmailInfo(){
		
	}
	
	/**
	 * Return the site object. The return value may be null.
	 */
	public Site getSite() {
		return site;
	}

	/**
	 * Set the site object
	 * @param site
	 */
	public void setSite(Site site) {
		this.site = site;
	}

}
