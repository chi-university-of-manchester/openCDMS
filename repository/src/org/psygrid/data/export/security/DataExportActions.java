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

package org.psygrid.data.export.security;

/**
 * This class represents the range of possible export actions that can be taken. These actions are to be assigned for a particular user role and
 * for a particular ExportSecurityValues value. EXPORT_RESTRICTED is the least permissive action, whilst EXPORT_UNRESTRICTED is the
 * most permissive action.
 * 
 * NOTE: These enumerations (with the same names) are also in org.psygrid.security.RBACAction
 * Before querying the attribute authority, these must be converted to RBAC enumerations.
 * This can be accomplished as follows:
 * 		DataExportActions action = DataExportActions.ACTION_EXPORT_TRANSFORMED;
 *		RBACAction rbac = RBACAction.valueOf(action.toString());
 * 
 * @author Bill Vance
 *
 */

public enum DataExportActions {
	ACTION_EXPORT_RESTRICTED, //before exporting, data must be assigned standard code indicating that it is restricted (raw data not exported)
	ACTION_EXPORT_TRANSFORMED, //before exporting, data must be transformed first
	ACTION_EXPORT_UNRESTRICTED //export data as-is in the database.
}
