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
package org.opencdms.web.core.security.details;

import java.util.List;
import java.util.Map;

import org.acegisecurity.GrantedAuthority;
import org.opensaml.SAMLAssertion;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * Interface to define PsyGrid specific extensions to the
 * standard Acegi UserDetails.
 * 
 * @author Rob Harper
 *
 */
public interface PsygridUserDetails {

	/**
	 * Get the SAMl assertion retrieved from the Attribute
	 * Authority for the user.
	 * <p>
	 * This SAML assertion will be required when calling all
	 * secure PsyGrid web-services.
	 * 
	 * @return The SAML assertion.
	 */
	public SAMLAssertion getSaml();

	/**
	 * Set the SAMl assertion retrieved from the Attribute
	 * Authority for the user.
	 * <p>
	 * This SAML assertion will be required when calling all
	 * secure PsyGrid web-services.
	 * 
	 * @param sa The SAML assertion.
	 */
	public void setSaml(SAMLAssertion sa);

	/**
	 * Get the PsyGrid representation of the users DN (Distinguished
	 * Name).
	 * <p>
	 * This only differs from the native DN in terms of spaces after
	 * punctuation and capitalization, but must be used when querying
	 * the Attribute Authority.
	 * 
	 * @return The PsyGrid distinguished name.
	 */
	public String getPgDn();
	
	/**
	 * Get the list of projects that the users is a member of.
	 * 
	 * @return The list of projects.
	 */
	public List<ProjectType> getProjects();
	
	/**
	 * Get the list of projects that the user is a member of, and has
	 * the necessary privileges to export data from.
	 * 
	 * @return The list of projects that data can be exported from.
	 */
	public List<ProjectType> getExportableProjects();
	
	/**
	 * Get the list of projects that the user is a member of, and has
	 * the necessary privileges to export data from, with the export being 
	 * perfored immediately rather than put in the queue for later execution.
	 * 
	 * @return The list of projects that data can be immediately exported from.
	 */
	public List<ProjectType> getImmediatelyExportableProjects();
	
	/**
	 * Get the map of which groups the user is a member of for each
	 * of their projects.
	 * 
	 * @return The groups map.
	 */
	public Map<ProjectType, List<GroupType>> getGroups();
	
	/**
	 * Get the time difference in milliseconds between the AA server 
	 * and its client.
	 * <p>
	 * A positive value means that the server clock is running faster
	 * than the client clock.
	 * 
	 * @return The server time difference.
	 */
	public long getServerTimeDifference();
	
	/**
	 * Get the active group for the ESLWeb.
	 * 
	 * @return The active project.
	 */
	public ProjectType getActiveProject();
	
	/**
	 * Set the active project for the ESLWeb.
	 * 
	 * @param activeProject The active project.
	 */
	public void setActiveProject(ProjectType activeProject);
	
	/**
	 * Get the active group for the ESLWeb.
	 * 
	 * @return The active group.
	 */
	public GroupType getActiveGroup();
	
	/**
	 * Set the active group for the ESLWeb.
	 * 
	 * @param activeGroup The active group.
	 */
	public void setActiveGroup(GroupType activeGroup);
	
	/**
	 * Get the global authorities for the user.
	 * <p>
	 * These are the authorities that are defined by the authorities
	 * populator when the user is authenticated.
	 * 
	 * @return The global authorities.
	 */
	public GrantedAuthority[] getGlobalAuthorities();
	
	/**
	 * Set the global authorities for the user.
	 * <p>
	 * These are the authorities that are defined by the authorities
	 * populator when the user is authenticated.
	 * 
	 * @param globalAuthorities The global authorities.
	 */
	public void setGlobalAuthorities(GrantedAuthority[] globalAuthorities);
	
	/**
	 * Reset the user's authorities so that they include all the authorities
	 * in the argument, plus all the global authorities.
	 * 
	 * @param authorities The authorities.
	 */
	public void resetAuthorities(GrantedAuthority[] authorities);
	
	/**
	 * Get the list of projects that the user is a member of, and has
	 * the necessary privileges to audit data in.
	 * 
	 * @return The list of projects for which data can be audited.
	 */
	public List<ProjectType> getAuditableProjects();
	

}
