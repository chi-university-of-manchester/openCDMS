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
 * Enumeration to 
 * @author Rob Harper
 *
 */
public enum ReminderLevel {

    /**
     * A mild reminder, to be sent (for example) a week or
     * two before the event the reminder is for.
     */
    MILD,
    /**
     * A normal reminder, to be sent (for example) a day or
     * two before the event the reminder is for.
     */
    NORMAL,
    /**
     * A severe reminder, to be sent (for example) after the 
     * event the reminder is for, if the event has not been
     * performed.
     */
    SEVERE
    
}
