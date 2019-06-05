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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.psygrid.data.model.hibernate.BooleanValue;
import org.psygrid.data.model.hibernate.DateValue;
import org.psygrid.data.model.hibernate.IntegerValue;
import org.psygrid.data.model.hibernate.LongTextValue;
import org.psygrid.data.model.hibernate.NumericValue;
import org.psygrid.data.model.hibernate.OptionValue;
import org.psygrid.data.model.hibernate.TextValue;

public class SetValueVisitor extends ValueVisitor {

    public static final String SEPARATOR = "/";

    protected String value;
    
    protected String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public void visit(BooleanValue v) throws VisitorException {
        v.setValue(Boolean.parseBoolean(this.getValue()));
    }

    @Override
    public void visit(DateValue v) throws VisitorException {
        //make sure all properties are null
        v.setValue(null);
        v.setMonth(null);
        v.setYear(null);
        
        if ( null == value ){
            return;
        }
        
        Pattern p = Pattern.compile("[0-9]{1,2}/[0-9]{4,}");
        Matcher m = p.matcher(value);
        Pattern p2 = Pattern.compile("[0-9]{4,}");
        Matcher m2 = p2.matcher(value);        
        if ( m.matches() ){
            //the string value of the visitor represents a month
            //and year only
            int slashPos = value.indexOf("/");
            int month = Integer.parseInt(value.substring(0,slashPos));
            //decrement month, as internally month stored as 0=Jan,...,11=Dec
            month--;
            if ( month > 11 || month < 0 ){
                throw new VisitorException("'"+month+"' is not a valid month");
            }
            v.setMonth(new Integer(month));
            v.setYear(Integer.parseInt(value.substring(slashPos+1)));
        }
        else if ( m2.matches() ){
            //the string value of the visitor represents a year only
            v.setYear(Integer.parseInt(value));
        }
        else{
            //the string value of the visitor represents a
            //complete date
            try{
                DateFormat dateFormatter = initDateFormatter("dd"+SEPARATOR+"MM"+SEPARATOR+"yyyy");
                v.setValue(dateFormatter.parse(value));
            }
            catch(ParseException ex){
                throw new VisitorException("Cannot convert string into a date using dd/MM/yyyy format");
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

    @Override
    public void visit(LongTextValue v) throws VisitorException {
        v.setValue(this.getValue());
    }

    @Override
    public void visit(NumericValue v) throws VisitorException {
        if ( null == this.getValue() ){
            v.setValue(null);
        }
        else{
            v.setValue(Double.parseDouble(this.getValue()));
        }
    }

    @Override
    public void visit(OptionValue v) throws VisitorException {
        throw new VisitorException("Cannot set the value of an OptionValue");
    }

    @Override
    public void visit(TextValue v) throws VisitorException {
        v.setValue(this.getValue());
    }

    @Override
    public void visit(IntegerValue v) throws VisitorException {
        if ( null == this.getValue() ){
            v.setValue(null);
        }
        else{
            v.setValue(Integer.parseInt(this.getValue()));
        }
    }

}
