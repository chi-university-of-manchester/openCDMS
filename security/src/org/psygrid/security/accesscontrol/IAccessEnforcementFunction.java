/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as 
published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/


//Created on Dec 15, 2005 by John Ainsworth
package org.psygrid.security.accesscontrol;

import java.util.List;
import java.util.Map;

import javax.xml.rpc.server.ServletEndpointContext;

import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.TargetType;


/**
 * @author jda
 *
 */
public interface IAccessEnforcementFunction {

	/**
	 * @param samlAssertion
	 * @param group
	 * @param action
	 * @param project
	 * @return
	 */
	public abstract boolean authoriseUser(String stringAssertion,
			AEFGroup group, AEFAction action, AEFProject project)
			throws PGSecurityException, PGSecurityInvalidSAMLException,
			PGSecuritySAMLVerificationException;
	
	
	/**
	 * @deprecated
	 * @param samlAssertion
	 * @param group
	 * @param action
	 * @param project
	 * @return
	 */
//	public abstract boolean authoriseUser(String stringAssertion,
//			GroupType group, ActionType action, ProjectType project)
//			throws PGSecurityException, PGSecurityInvalidSAMLException,
//			PGSecuritySAMLVerificationException;

    public void initialise(ServletEndpointContext ctx) throws PGSecurityException;
    
    /**
     * Retrieve the username from a SAML assertion. The assertion will be checked for validity.
     * 
     * @param stringAssertion The SAML assertion.
     * @return The username.
     * @throws PGSecurityException
     */
    public String getUserFromSAML(String stringAssertion) throws PGSecurityException,
	PGSecurityInvalidSAMLException, 
	PGSecuritySAMLVerificationException;

    /**
     * Retrieve the name of the invoker from the secure transport.
     * 
     * @return The username.
     */
    public String getCallersIdentity();
   
    /**
     * Retrieve the username from a SAML assertion. The assertion is unchecked.
     * 
     * @param stringAssertion The SAML assertion.
     * @return The username.
     * @throws PGSecurityException
     */
	public String getUserFromUnverifiedSAML(String stringAssertion)
			throws PGSecurityException;
    /**
     * Retrieve the username from a SAML assertion. The assertion will be checked for validity.
     * 
     * @param stringAssertion The SAML assertion.
     * @return The username.
     * @throws PGSecurityException
     */
	public String getIssuerFromSAML(String stringAssertion)
			throws PGSecurityException, PGSecurityInvalidSAMLException,
			PGSecuritySAMLVerificationException;
    /**
     * Retrieve the username from a SAML assertion. The assertion is unchecked.
     * 
     * @param stringAssertion The SAML assertion.
     * @return The username.
     * @throws PGSecurityException
     */
	public String getIssuerFromUnverifiedSAML(String stringAssertion)
			throws PGSecurityException;
	
    /**
     * Test for the things the supplied SAML assertions entitles the holder
     * to perform
     * 
     * @param request A list of the project and action combinations to be tested
     * @param stringAssertion The SAML assertion.
     * @return The username.
     * @throws PGSecurityException
     */
	public abstract Map<ProjectType, List<Map<ActionType, List<TargetType>>>> checkAuthorisation(Map<ProjectType, List<ActionType>> request, String sa)
	throws PGSecurityException;
}