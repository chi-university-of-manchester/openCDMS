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

package org.psygrid.data.model.hibernate.visitors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.psygrid.data.model.hibernate.BooleanValue;
import org.psygrid.data.model.hibernate.DateValue;
import org.psygrid.data.model.hibernate.IntegerValue;
import org.psygrid.data.model.hibernate.LongTextValue;
import org.psygrid.data.model.hibernate.NumericValue;
import org.psygrid.data.model.hibernate.OptionValue;
import org.psygrid.data.model.hibernate.TextValue;

public abstract class ValueVisitor {
        
    public abstract void visit(TextValue v) throws VisitorException;
    public abstract void visit(DateValue v) throws VisitorException;
    public abstract void visit(NumericValue v) throws VisitorException;
    public abstract void visit(BooleanValue v) throws VisitorException;
    public abstract void visit(LongTextValue v) throws VisitorException;
    public abstract void visit(OptionValue v) throws VisitorException;
    public abstract void visit(IntegerValue v) throws VisitorException;

}
