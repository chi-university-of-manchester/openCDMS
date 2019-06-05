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
 * This class represents the possible range of security values that can be applied to data elements.
 * The values are used by the export logic. EXPORT_LEVEL_0 is the most pemissive level of security that can be applied,
 * and EXPORT_LEVEL_16 is the most restrictive.
 * 
 * @author Bill Vance
 *
 */

public enum ExportSecurityValues {
	EXPORT_LEVEL_0, //MOST PERMISSIVE LEVEL OF SECURITY
	EXPORT_LEVEL_1,
	EXPORT_LEVEL_2,
	EXPORT_LEVEL_3,
	EXPORT_LEVEL_4,
	EXPORT_LEVEL_5,
	EXPORT_LEVEL_6,
	EXPORT_LEVEL_7,
	EXPORT_LEVEL_8,
	EXPORT_LEVEL_9,
	EXPORT_LEVEL_10,
	EXPORT_LEVEL_11,
	EXPORT_LEVEL_12,
	EXPORT_LEVEL_13,
	EXPORT_LEVEL_14,
	EXPORT_LEVEL_15; //MOST RESTRICTIVE LEVEL OF SECURITY
}
