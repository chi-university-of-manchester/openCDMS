/*
Copyright (c) 2006, The University of Manchester, UK.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301, USA.
*/

package org.psygrid.collection.entry.chooser;

/**
 * @author Rob Harper
 *
 */
public enum ChoosableType {
    DATASET,
    DOCUMENT_GROUP,
    DOCUMENT_OCCURRENCE,
    RECORD,
    DOCUMENT_INSTANCE,
    DOCUMENT_STATUS,
    REPORT;
    
    /*
	From DocChoosableType...
    @Override
    public String toString() {
        switch (this) {
        case DATASET:
            return Messages.getString("DocChoosableType.dataset"); //$NON-NLS-1$
        case DOCUMENT_GROUP:
            return Messages.getString("DocChoosableType.documentGroup"); //$NON-NLS-1$
        case DOCUMENT_OCCURRENCE:
            return Messages.getString("DocChoosableType.documentOccurrence"); //$NON-NLS-1$
        }
        return null;
    }
    
    From DocStatusChoosableType
    @Override
    public String toString() {
        switch (this) {
        case DATASET:
            return Messages.getString("DocChoosableType.dataset"); //$NON-NLS-1$
        case DOCUMENT_GROUP:
            return Messages.getString("DocChoosableType.documentGroup"); //$NON-NLS-1$
        case DOCUMENT_OCCURRENCE:
            return Messages.getString("DocChoosableType.documentOccurrence"); //$NON-NLS-1$
        }
        return null;
    }

	*/
}
