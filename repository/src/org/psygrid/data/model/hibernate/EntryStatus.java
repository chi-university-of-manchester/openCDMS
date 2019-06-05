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

package org.psygrid.data.model.hibernate;

/**
 * Enumeration to represent the status of an entry.
 * 
 * @author Rob Harper
 *
 */
public enum EntryStatus {
    /**
     * A response can be provided for the entry, but does not
     * have to be.
     */
    OPTIONAL,
    /**
     * The entry is disabled, no response can be provided for the
     * entry.
     */
    DISABLED,
    /**
     * The entry is mandatory, and so a response must be provided.
     */
    MANDATORY
}
