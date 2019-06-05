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


package org.psygrid.common;

/**
 * @author Rob Harper
 *
 */
public class ValidationRulesWrapper {

	   /**
	    * A reference to a possibly alternate factory.
	    */
	   static private IValidationRuleFactory _factory = null;

	   /**
	    * A reference to the current instance.
	    */
	   static private ValidationRules _instance = null;


	   /**
	    * This is the default factory method.
	    * It is called to create a new Singleton when
	    * a new instance is needed and _factory is null.
	    */
	   static private ValidationRules makeInstance() {
	      return new ValidationRules();
	   }

	   /**
	    * This is the accessor for the Singleton.
	    */
	   static public synchronized ValidationRules instance() {
	      if(null == _instance) {
	         _instance = (null == _factory) ? makeInstance() : _factory.makeInstance();
	      }
	      return _instance;
	   }

	   /**
	    * Sets the factory method used to create new instances.
	    * You can set the factory method to null to use the default method.
	    * @param factory The Singleton factory
	    */
	   static public synchronized void setFactory(IValidationRuleFactory factory) {
	      _factory = factory;
	   }

	   /**
	    * Sets the current Singleton instance.
	    * You can set this to null to force a new instance to be created the
	    * next time instance() is called.
	    * @param instance The Singleton instance to use.
	    */
	   static public synchronized void setInstance(ValidationRules instance) {
	      _instance = instance;
	   }
}
