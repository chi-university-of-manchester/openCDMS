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


//Created on Nov 10, 2005 by John Ainsworth



package org.psygrid.security;

/**
 * @author jda
 *
 */
public class PGSecurityConstants {

	//SAML Attribute statements
	public static final String SAML_ATTRIBUTE_NS = "http://www.psygrid.org/xml/security/core/types";
	public static final String SAML_ATTRIBUTE_MEMBERSHIP = "Membership";
	public static final String SAML_ATTRIBUTE_ROLE = "Role";
	public static final String SAML_ATTRIBUTE_GROUP = "Group";
	public static final String ANY = "ANY";
	public static final String GROUP_SPECIFIC = "GROUP_SPECIFIC";
	
	public static final String SYSTEM_PROJECT = "SYSTEM";
	public static final String SYSTEM_PROJECT_ID = "-1";
}
