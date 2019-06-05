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

public class GetValueVisitor extends ValueVisitor {

    public static final String SEPARATOR = "/";

    private String value;
    
    public String getValue() {
        return value;
    }
    
    protected void setValue(String value) {
        this.value = value;
    }
    
    public void visit(TextValue v) throws VisitorException {
        this.setValue(v.getValue());
    }

    public void visit(DateValue v) throws VisitorException {
        if ( null != v.getValue() ){
            DateFormat dateFormatter = initDateFormatter("dd"+SEPARATOR+"MM"+SEPARATOR+"yyyy");
            this.setValue(dateFormatter.format(v.getValue()));
        }
        else{
            if ( null != v.getMonth() ){
                //assume that if there is a month there is always a year
                this.setValue(v.getMonthString()+SEPARATOR+v.getYear());
            }
            else if ( null != v.getYear() ){
                this.setValue(v.getYear().toString());
            }
            else{
                this.setValue(null);
            }
        }
    }

    /**
     * Create a new SimpleDateFormat object with the specified format
     * and lenient parsing disabled.
     * 
     * @param format The format for the SimpleDateFormat object.
     * @return The new SimpleDateFormat object.
     */
    private static DateFormat initDateFormatter(String format){
        DateFormat formatter = new SimpleDateFormat(format);
        formatter.setLenient(false);
        return formatter;
    }

    public void visit(NumericValue v) throws VisitorException {
        if ( null == v.getValue() ){
            this.setValue(null);
        }
        else{
            this.setValue(v.getValue().toString());
        }
    }

    public void visit(BooleanValue v) throws VisitorException {
        this.setValue(Boolean.toString(v.getValue()));
    }

    public void visit(LongTextValue v) throws VisitorException {
        this.setValue(v.getValue());
    }

    public void visit(OptionValue v) throws VisitorException {
        if ( null == v.getValue() ){
            this.setValue(null);
        }
        else{
            this.setValue(v.getValue().getDisplayText());
        }
    }

    public void visit(IntegerValue v) throws VisitorException {
        if ( null == v.getValue() ){
            this.setValue(null);
        }
        else{
            this.setValue(v.getValue().toString());
        }
    }

}
