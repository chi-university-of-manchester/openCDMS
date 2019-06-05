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

package org.psygrid.esl.model.hibernate;

import org.psygrid.common.email.Email;
import org.psygrid.esl.model.*;

/**
 * Uses factory pattern to provide accessor methods to create the 
 * various classes used for database interaction/data storage. 
 * 
 * @author Lucy Bridges
 *
 */
public class HibernateFactory implements IFactory {

	public IProject createProject(String code) {
        return new Project(code);
    }

	public IGroup createGroup(String name){
		return new Group(name);
	}
	public IGroup createGroup(String name, String code){
		return new Group(name, code);
	}
   
	public ISubject createSubject() {
		return new Subject();
	}
   
	public ISubject createSubject(String studyNumber) {
		return new Subject(studyNumber);
	}
   
	public IAddress createAddress() {
		return new Address();
	}
   
	public IRandomisation createRandomisation(String name) {
		return new Randomisation(name);
	}
   
	public IRole createRole(String name) {
		return new Role(name);   
	}
   
	public IStrata createStrata(String name) {
		return new Strata(name);
	}
   
	public Email createEmail() {
		return new Email();
	}

	public ICustomField createCustomField(String name) {
		return new CustomField(name);
	}

	public ICustomValue createCustomValue(String name, String value) {
		return new CustomValue(name, value);
	}

}
