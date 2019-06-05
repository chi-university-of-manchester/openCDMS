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


//Created on Oct 17, 2005 by John Ainsworth

package org.psygrid.security.example;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;
import javax.xml.rpc.server.ServletEndpointContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.SAMLAssertion;
import org.opensaml.XML;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.SecureService;
import org.psygrid.security.accesscontrol.AEFGroup;
import org.psygrid.security.accesscontrol.AEFProject;
import org.psygrid.security.accesscontrol.AccessEnforcementFunction;
import org.psygrid.security.accesscontrol.IAccessEnforcementFunction;
import org.psygrid.security.attributeauthority.AttributeAuthorityQueryPortTypeImpl;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.example.service.SecureServiceExamplePortType;
import org.w3c.dom.Document;

/**
 * @author jda
 * 
 */
public class SecureServiceExample extends SecureService implements
		SecureServiceExamplePortType, ServiceLifecycle {

	/** logger */
	private IAccessEnforcementFunction aef = null;
	private static Log sLog = LogFactory
			.getLog(AttributeAuthorityQueryPortTypeImpl.class);

	/**
	 * 
	 */
	public SecureServiceExample() {
		super();
	}

	public String exampleMethod(String project,java.lang.String s)
	throws NotAuthorisedFaultMessage {
		sLog.info("exampleMethod........");
		try{
		InputStream is = new ByteArrayInputStream(
				s.getBytes());
		Document doc = XML.parserPool.parse(is);
		samlToken = new SAMLAssertion(doc.getDocumentElement());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		try {
		if(aef.authoriseUser(s, new AEFGroup(null, "iop", null), RBACAction.ACTION_DR_GENERATE_IDENTIFIERS.toAEFAction(), new AEFProject(null, "fep", false))){
			return "smoking";
		} else {
			return "denied";
		}
		}catch(Exception e){
			sLog.info(e.getMessage());
			e.printStackTrace();
		}
		return "duh";
		
	}

	/* (non-Javadoc)
	 * @see javax.xml.rpc.server.ServiceLifecycle#destroy()
	 */
	public void destroy() {
		super.destroy();
	}
	/* (non-Javadoc)
	 * @see javax.xml.rpc.server.ServiceLifecycle#init(java.lang.Object)
	 */
	public void init(Object o) throws ServiceException {
		super.init(o);
		aef = new AccessEnforcementFunction();
		try{
		aef.initialise((ServletEndpointContext)o);
		} catch (PGSecurityException pgse) {
			throw new ServiceException("failed to initialise AEF");
		}
	}
}
