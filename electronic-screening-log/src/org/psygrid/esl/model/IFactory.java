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

package org.psygrid.esl.model;

import org.psygrid.common.email.Email;


/**
 * Interface used to create various objects.
 * 
 * @author Lucy Bridges
 *
 */
public interface IFactory {

	/**
	 * Create a new project with the given name.
	 * 
	 * @param name The name of the new project.
	 * @return The new project.
	 */
	public IProject createProject(String name);

	/**
	 * Create a new group with the given name.
	 * 
	 * @param name The name of the new group.
	 * @return The new group.
	 */
	public IGroup createGroup(String name);

	/**
	 * Create a new group with the given name and code.
	 * 
	 * @param name The name of the new group.
	 * @param code The group code
	 * @return The new group.
	 */
	public IGroup createGroup(String name, String code);

	/**
	 * Create a new trial subject with the given studycode.
	 * 
	 * @param studyNumber The study code belonging to the new subject.
	 * @return The new subject.
	 */
	public ISubject createSubject(String studyNumber);
	
	/**
	 * Create a new trial subject.
	 * 
	 * @return The new subject.
	 */
	public ISubject createSubject();

	/**
	 * Create a new address for a subject.
	 * 
	 * @return The new address.
	 */
	public IAddress createAddress();


	/**
	 * Create a new randomisation procedure with the given name.
	 * 
	 * @param name The name of the new randomisation.
	 * @return The new randomisation.
	 */
	public IRandomisation createRandomisation(String name);

	/**
	 * Create a new role with the given name.
	 * 
	 * @param name The name of the new role.
	 * @return The new role.
	 */
	public IRole createRole(String name);

	/**
	 * Create a new randomisation strata with the given name.
	 * 
	 * @param name The name of the new strata.
	 * @return The new strata.
	 */
	public IStrata createStrata(String name);

	/**
	 * Create a new Email, which will be sent during a particular
	 * stage of the randomisation process.
	 * 
	 * @return IEmail
	 */
	public Email createEmail();

	/**
	 * Create a new custom field with the given name.
	 * 
	 * @param name The name
	 * @return
	 */
	public ICustomField createCustomField(String name);
	
	/**
	 * Create a new custom value with the given name and value.
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public ICustomValue createCustomValue(String name, String value);
}
