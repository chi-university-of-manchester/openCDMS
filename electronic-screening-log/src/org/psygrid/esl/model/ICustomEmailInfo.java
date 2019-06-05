package org.psygrid.esl.model;

import java.util.List;

import org.psygrid.esl.randomise.EmailType;

/**
 * Contains custom email information to be used when creating randomisation emails.
 * If none is specified, then the default email information will be generated.
 * This interface is meant to be expanded as other types of custom information are
 * required.
 * @author williamvance
 *
 */
public interface ICustomEmailInfo {

	/**
	 * Returns the site object, if specified. Otherwise, is null.
	 * @return
	 */
	public ISite getSite();
	
}
