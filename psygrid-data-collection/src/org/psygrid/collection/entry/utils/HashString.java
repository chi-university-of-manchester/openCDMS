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


package org.psygrid.collection.entry.utils;

import org.psygrid.collection.entry.security.SecurityHelper;

/**
 * Command line app to hash a string using the same algorithm as CoCoA
 * uses to hash persisted usernames and passwords.
 * <p>
 * Useful to work out which numbered folder in the psygrid profile 
 * relates to which login.
 * 
 * @author Rob Harper
 *
 */
public class HashString {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if ( args.length != 1 ){
			System.out.println("Usage: HashString <string>");
			return;
		}
		
		System.out.println(SecurityHelper.hash(args[0].toCharArray()));

	}

}
