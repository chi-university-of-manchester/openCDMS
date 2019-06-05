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

package org.psygrid.common.lsid;

import java.util.Random;

/**
 * Utility class, used when dealing with LSIDs, containing commonly
 * used functions.
 * 
 * @author Lucy Bridges
 *
 */
public class LSIDUtils {

	/**
	 * Create a 10 digit unique number to use as part of the identifier
	 * in an LSID.
	 * 
	 * @return unique number
	 */
	public static String createUniqueId() {
		//TODO:DEL - for now just use the myGrid generation mechanism.
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			int n = r.nextInt();
			n = (n < 0) ? -n : n;
			sb.append("1234567890".charAt(n % 10));
		}

		return sb.toString();
	}

	/**
	 * Returns an LSID object populated with the elements from the lsid
	 * string provided. 
	 * 
	 * @param lsid
	 * @param newLSID - the object to populate
	 * @return ILSID
	 * @throws LSIDFormatException
	 */
	public static ILSID getLSIDFromString(String lsid, ILSID newLSID) throws LSIDFormatException {
		ILSID lsidObj = newLSID;

		String[] elements = lsid.split(":");
		//e.g urn:lsid:org.psygrid:org.psygrid.storage:OLK_123[:1]
		if (elements.length < 5) {
			throw new LSIDFormatException("lsid doesn't have enough elements. lsid was: "+lsid);
		}
		lsidObj.setAuthority(elements[2]);
		lsidObj.setNamespace(elements[3]);

		if (elements.length > 5) {
			lsidObj.setRevision(elements[5]);
		}

		String id = elements[4];
		if (id != null && id.contains(ILSID.separator)) {
			try {
				lsidObj.setProjectCode(id.split(ILSID.separator)[0]);
				lsidObj.setUniqueId(id.split(ILSID.separator)[1]);
			}
			catch (ArrayIndexOutOfBoundsException e) {
				try {
					//Just set the unique id if the project code is not present
					lsidObj.setUniqueId(elements[4]);
				}
				catch (ArrayIndexOutOfBoundsException ex) {
					throw new LSIDFormatException("LSID identifier format is incorrect. Identifier should be in the format 'projectCode_uniqueId'. LSID was: "+lsid);
				}
			}
		}
		else {
			throw new LSIDFormatException("LSID identifier format is incorrect. Identifier should be in the format 'projectCode_uniqueId'. LSID was: "+lsid);
		}

		return lsidObj;
	}
	
	public static String getProjectCode(String lsid) throws LSIDFormatException {
		String projectCode = null;
		
		String[] elements = lsid.split(":");
		//e.g urn:lsid:org.psygrid:org.psygrid.storage:OLK_123[:1]
		if (elements.length < 5) {
			throw new LSIDFormatException("lsid doesn't have enough elements. lsid was: "+lsid);
		}

		String id = elements[4];
		if (id != null && id.contains(ILSID.separator)) {
			try {
				projectCode = id.split(ILSID.separator)[0];
			}
			catch (ArrayIndexOutOfBoundsException e) {
				throw new LSIDFormatException("LSID identifier format is incorrect. Identifier should be in the format 'projectCode_uniqueId'. LSID was: "+lsid);
			}
		}
		else {
			throw new LSIDFormatException("LSID identifier format is incorrect. Identifier should be in the format 'projectCode_uniqueId'. LSID was: "+lsid);
		}

		return projectCode;
	}
}
