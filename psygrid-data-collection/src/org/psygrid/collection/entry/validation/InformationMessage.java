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


package org.psygrid.collection.entry.validation;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationMessage;

public final class InformationMessage implements ValidationMessage {

    private final String text;

    public InformationMessage(String text) {
        if (text == null) {
        	this.text = "";
         //   throw new IllegalArgumentException("text cannot be null"); //$NON-NLS-1$
        }
        else {
        	this.text = text;
        }
    }

    public final Severity severity() {
        return Severity.OK;
    }

    public final String formattedText() {
        return text;
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InformationMessage)) {
            return false;
        }
        
        final InformationMessage other = (InformationMessage) obj;
        
        return this.text.equals(other.text);
    }

    public final Object key() {
        return null;
    }
}
