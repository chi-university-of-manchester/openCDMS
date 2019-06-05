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


//Created on Feb 9, 2006 by John Ainsworth

package org.psygrid.security.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLAttribute;
import org.opensaml.SAMLAttributeStatement;
import org.opensaml.SAMLException;
import org.opensaml.SAMLNameIdentifier;
import org.opensaml.XML;
import org.psygrid.security.PGSecurityConstants;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.policyauthority.service.ProcessingFaultMessage;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.w3c.dom.Document;

/**
 * @author jda
 * 
 */
public class SAMLUtilities {
	/** logger */
	public static Log sLog = LogFactory.getLog(SAMLUtilities.class);

	static public String getUserFromSAML(String stringAssertion)
			throws PGSecurityException, PGSecurityInvalidSAMLException,
			PGSecuritySAMLVerificationException {
		if (stringAssertion != null) {
			SAMLAssertion sa = null;
			sa = retrieveAndVerifySAMLAssertion(stringAssertion);
			if (sa == null) {
				throw new PGSecurityException(
						"Failed to retrieve SAML Assertion");
			}
			Iterator it = sa.getStatements();
			while (it.hasNext()) {
				Object o = it.next();
				if (o instanceof SAMLAttributeStatement && null != o) {
					SAMLAttributeStatement stat = (SAMLAttributeStatement) o;
					SAMLNameIdentifier sni = stat.getSubject().getName();
					return sni.getName();
				}
			}
		}
		throw new PGSecurityException(
				"User name could not be retrieved from SAML Assertion");
	}

	static public boolean isSystemLevelAssertion(String stringAssertion) throws ProcessingFaultMessage{
		
		SAMLAssertion samlAssert;
		try {
			samlAssert = SAMLUtilities.retrieveSAMLAssertion(stringAssertion);
		} catch (PGSecurityException e1) {
			throw new ProcessingFaultMessage(e1.getMessage());
		}
		
		ProjectType systemProject = new ProjectType("SYSTEM", "-1", null, null, false);
		PrivilegeType[] userPrivileges = SAMLUtilities.getUsersPrivilegesInProjectFromST(samlAssert, systemProject);
		
		for(int i = 0; i < userPrivileges.length; i++){
			PrivilegeType t = userPrivileges[i];
			if(t.getRole() != null && t.getRole().getName().equals("System")){
				return true;
			}
		}
		
		return false;
	}
	
	static public SAMLAssertion retrieveAndVerifySAMLAssertion(
			String stringAssertion) throws PGSecurityException,
			PGSecurityInvalidSAMLException, PGSecuritySAMLVerificationException {
		SAMLAssertion sa = null;
		if (stringAssertion != null) {
			sa =retrieveSAMLAssertion(stringAssertion);
		}
		if (sa == null) {
			throw new PGSecurityException("Failed to retrieve SAML Assertion");
		}
		verifySAMLAssertion(sa);
		return sa;
	}
	
	static public void verifySAMLAssertion(SAMLAssertion sa) 
			throws PGSecurityException, PGSecurityInvalidSAMLException, PGSecuritySAMLVerificationException {
		verifySAMLAssertion(sa, new Date());
	}
	
	static public void verifySAMLAssertion(SAMLAssertion sa, long timeDifference)
		throws PGSecurityException, PGSecurityInvalidSAMLException, PGSecuritySAMLVerificationException {
		Date current = new Date();
		current = new Date(current.getTime()+timeDifference);
		verifySAMLAssertion(sa, current);
	}
	
	static public void verifySAMLAssertion(SAMLAssertion sa, Date current) 
		throws PGSecurityException, PGSecurityInvalidSAMLException, PGSecuritySAMLVerificationException {
		if(sa.getNotBefore().after(current)){
			throw new PGSecurityInvalidSAMLException("SAML Assertion is not yet valid");
		}
		if(sa.getNotOnOrAfter().before(current)){
			throw new PGSecurityInvalidSAMLException("SAML Assertion has expired");
		}
		try {
			sa.checkValidity();
		} catch (SAMLException se) {
			throw new PGSecurityInvalidSAMLException("SAML Assertion not valid"
					+ se.getMessage());
		}
		try {
			sa.verify();
		} catch (SAMLException se) {
			throw new PGSecuritySAMLVerificationException(
					"SAML Assertion can not be verified" + se.getMessage());
		}
	}

	static public void verfiyIssuerOfSAMLAssertion(SAMLAssertion sa, KeyStore ks)
			throws PGSecurityException, PGSecurityInvalidSAMLException,
			PGSecuritySAMLVerificationException {
		boolean issuerTrusted = false;
		try {
			Enumeration<String> aliases = ks.aliases();
			while(aliases.hasMoreElements()){
				String alias = aliases.nextElement();
				X509Certificate c = (X509Certificate)ks.getCertificate(alias);
				c.checkValidity();
				X509Certificate issuer = (X509Certificate)sa.getX509Certificates().next();
				issuer.checkValidity();
				sLog.debug(issuer.getSubjectDN().getName());
				sLog.debug(c.getSubjectDN().getName());
				if(issuer.equals(c)){
					issuerTrusted = true;
					break;
				}
			}
		} catch (KeyStoreException kse){
			throw new PGSecuritySAMLVerificationException("SAML Assertion can not be verified " + kse.getMessage());
		} catch (SAMLException se){
			throw new PGSecuritySAMLVerificationException("SAML Assertion can not be verified " + se.getMessage());		
		}catch (CertificateNotYetValidException nve){
			throw new PGSecuritySAMLVerificationException("SAML Assertion can not be verified " + nve.getMessage());		
		}catch (CertificateExpiredException cee){
			throw new PGSecuritySAMLVerificationException("SAML Assertion can not be verified " + cee.getMessage());		
		}
		if(!issuerTrusted){
			sLog.debug("issuer not trusted");
			throw new PGSecuritySAMLVerificationException("SAML Assertion issuer not trusted");				
		}
	}
	

	static public String getUserFromUnverifiedSAML(String stringAssertion)
			throws PGSecurityException {
		if (stringAssertion != null) {
			SAMLAssertion sa = null;
			sa = retrieveSAMLAssertion(stringAssertion);
			if (sa == null) {
				throw new PGSecurityException(
						"Failed to retrieve SAML Assertion");
			}
			Iterator it = sa.getStatements();
			while (it.hasNext()) {
				Object o = it.next();
				if (o instanceof SAMLAttributeStatement && null != o) {
					SAMLAttributeStatement stat = (SAMLAttributeStatement) o;
					SAMLNameIdentifier sni = stat.getSubject().getName();
					return sni.getName();
				}
			}
		}
		throw new PGSecurityException(
				"User name could not be retrieved from SAML Assertion");
	}
	
	static public String getIssuerFromUnverifiedSAML(String stringAssertion)
			throws PGSecurityException {
		if (stringAssertion != null) {
			SAMLAssertion sa = null;
			sa = retrieveSAMLAssertion(stringAssertion);
			if (sa == null) {
				throw new PGSecurityException(
						"Failed to retrieve SAML Assertion");
			}
			return sa.getIssuer();
		}
		throw new PGSecurityException(
				"User name could not be retrieved from SAML Assertion");
	}
	
	static public String getIssuerFromSAML(String stringAssertion)
			throws PGSecurityException, PGSecurityInvalidSAMLException,
			PGSecuritySAMLVerificationException {
		if (stringAssertion != null) {
			SAMLAssertion sa = null;
			sa = retrieveAndVerifySAMLAssertion(stringAssertion);
			if (sa == null) {
				throw new PGSecurityException(
						"Failed to retrieve SAML Assertion");
			}
			return sa.getIssuer();
		}
		throw new PGSecurityException(
				"User name could not be retrieved from SAML Assertion");
	}


	static public SAMLAssertion retrieveSAMLAssertion(String stringAssertion)
			throws PGSecurityException {
		SAMLAssertion sa = null;
		if (stringAssertion != null) {

			InputStream is = new ByteArrayInputStream(stringAssertion
					.getBytes());
			try {
				Document doc = XML.parserPool.parse(is);
				sa = new SAMLAssertion(doc.getDocumentElement());
			} catch (Exception e) {
				sLog.info(e.getMessage());
				throw new PGSecurityException(
						"failed to recover SAML Assertion");
			}
		}
		if (sa == null) {
			throw new PGSecurityException("Failed to retrieve SAML Assertion");
		}
		return sa;
	}
	
	public static PrivilegeType[] getUsersPrivilegesInProjectFromST(SAMLAssertion sa, ProjectType project) {
		Iterator<SAMLAttributeStatement> it = sa.getStatements();
		boolean groupFound = false;
		SAMLAttributeStatement st = null;
		SAMLAttribute attribute = null;
		List<PrivilegeType> ptl = new ArrayList<PrivilegeType>();
		while (it.hasNext()&&!groupFound) {
			st = it.next();
			Iterator<SAMLAttribute> it2 = st.getAttributes();
			while (it2.hasNext()) {
				attribute = it2.next();
				if (attribute.getName().equals(
						PGSecurityConstants.SAML_ATTRIBUTE_MEMBERSHIP)) {
					Iterator<String> it3 = attribute.getValues();
					while (it3.hasNext()) {
						String g = it3.next();
						if(g.equals(project.getName())){
							groupFound=true;
						}
						String id = it3.next();
						if(id.equals(project.getIdCode())){
							groupFound=true;
						}
					}		
					if(groupFound){
						while(it2.hasNext()){
							attribute = it2.next();
							if (attribute.getName().equals(
									PGSecurityConstants.SAML_ATTRIBUTE_ROLE)) {
								it3 = attribute.getValues();
								while (it3.hasNext()) {
									String r = it3.next();
									ptl.add(new PrivilegeType(new RoleType(r, null), null));
								}
							}
							if (attribute.getName().equals(
									PGSecurityConstants.SAML_ATTRIBUTE_GROUP)) {
								it3 = attribute.getValues();
								while (it3.hasNext()) {
									String r = it3.next(); //group name
									String s = it3.next(); //group code
									ptl.add(new PrivilegeType(null, new GroupType(r, s, null)));
	
								}
							}
						}
					}
				}
			}
		}
		return ptl.toArray(new PrivilegeType[ptl.size()]);
	}
}
