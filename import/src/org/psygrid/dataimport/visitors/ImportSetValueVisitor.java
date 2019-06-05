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

package org.psygrid.dataimport.visitors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.model.hibernate.visitors.SetValueVisitor;
import org.psygrid.data.model.hibernate.visitors.VisitorException;

public class ImportSetValueVisitor extends SetValueVisitor {

    private BasicResponse response;
    
    private List<String> formats;
    
    private String otherText;
    
    @Override
    public void setValue(String value) {
        if ( null != value && 0 == value.length() ){
            this.value = null;
        }
        else{
            this.value = value;
        }
    }

    public List<String> getFormats() {
		return formats;
	}

	public void setFormats(List<String> formats) {
		this.formats = formats;
	}

	public BasicResponse getResponse() {
        return response;
    }

    public void setResponse(BasicResponse response) {
        this.response = response;
    }

    public String getOtherText() {
        return otherText;
    }

    public void setOtherText(String otherText) {
        this.otherText = otherText;
    }

    @Override
    public void visit(DateValue v) throws VisitorException {
    	boolean parsed = false;
    	Date date = null;
        if ( null != this.value ){
        	for ( String format: formats ){
        		try{
        			SimpleDateFormat formatter = new SimpleDateFormat(format);
        			date = formatter.parse(this.value);
        			parsed = true;
        		}
        		catch(ParseException ex){
        			//do nothing - if none of the formats parse successfully
        			//we deal with that later
        		}
        	}
        	if ( !parsed ){
        		throw new VisitorException("Cannot convert string into a date using any of the supplied formats.");
        	}
        }
        v.setValue(date);
    }

    @Override
    public void visit(OptionValue v) throws VisitorException {
        //translate the value from the import source into the approprate
        //option
    	if ( null == this.value ){
    		v.setValue(null);
    	}
    	else{
	        OptionEntry oe = (OptionEntry)response.getEntry();
	        
	        Option o = oe.getOption(Integer.parseInt(this.value));
	        
	        //Need to get the option with the same CODE value as the value.
	        /*
	        IOption o = null;
	        int numberOfOptions = oe.numOptions();
	        Integer currentValue = Integer.valueOf(this.value);
	        for(int i = 0; i < numberOfOptions; i++){
	        	IOption op = oe.getOption(i);
	        	if(op.getCode().compareTo(currentValue) == 0){
	        		o = op;
	        		break;
	        	}
	        }
	        */
	        if(o == null){
	        	throw new VisitorException("No option found matching code.");
	        }
	        v.setValue(o);
    	}
        if ( null != this.otherText ){
            v.setTextValue(otherText);
        }
    }

}
