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

package org.psygrid.transformers.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.psygrid.transformers.TransformerException;

public class DateTransformerImpl {

    public static final DateFormat fullDateFormatter = initDateFormatter("dd/MM/yyyy");
    public static final DateFormat monthAndYearFormatter = initDateFormatter("MM/yyyy");
    
    public static String getMonthAndYear(String inputDate) throws TransformerException {
        
        String result = null;
        if ( null != inputDate ){
            
            Pattern p1 = Pattern.compile("[0-9]{1,2}/[0-9]{1,2}/[0-9]{4,}");
            Matcher m1 = p1.matcher(inputDate);
            Pattern p2 = Pattern.compile("[0-9]{1,2}/[0-9]{4,}");
            Matcher m2 = p2.matcher(inputDate);
            Pattern p3 = Pattern.compile("[0-9]{4,}");
            Matcher m3 = p3.matcher(inputDate);
            if ( m1.matches() ){
                //input is in dd/mm/yyyy format
                try{
                    Date date = fullDateFormatter.parse(inputDate);
                    result = monthAndYearFormatter.format(date);
                }
                catch(ParseException ex){
                    throw new TransformerException(ex);
                }
            }
            else if ( m2.matches() ){
                //input is in mm/yyyy format
                result = inputDate;
            }
            else if ( m3.matches() ){
                //input is in yyyy format
                result = inputDate;
            }
            else{
                //input is in an unknown format
                throw new TransformerException("Input '"+inputDate+"' is not in a known date format");
            }
        }
        return result;
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
}
