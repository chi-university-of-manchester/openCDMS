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


package org.psygrid.collection.entry.ui;

import java.util.Calendar;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.jgoodies.validation.view.ValidationResultViewFactory;

/**
 * @author Rob Harper
 *
 */
public class DateValidationHelper {

	/**
	 * Check that the date is in the allowable range. For some reason, it checked 
	 * that the date is not in the future or more than 10 years in the past.
	 * This seems somewhat arbitrary.
	 * For ClinTouch, dates in the future are required, so I removed this check.
	 * @param date
	 * @param label
	 * @param parent
	 * @return
	 */
    public static boolean validateDate(Date date, JLabel label, JComponent parent){
    	String message = null;
    	boolean failed = false;
        if ( null == date ){
        	message = Messages.getString("DateValidationHelper.invalidOrNoDateMessage");
        	failed = true;
        }
        
        if ( !failed ){
            //check that the date is not more than 10 years ago in the past
            Date now = removeTimeComponent(new Date());
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.YEAR, -10);
            if ( date.before(cal.getTime()) ){
                message = Messages.getString("DateValidationHelper.oldDateMessage");
                failed = true;
            }
        }            
            
        if ( failed ){
            label.setIcon(ValidationResultViewFactory.getErrorIcon());
            label.setToolTipText(message);
            JOptionPane.showMessageDialog(parent, 
                    message, 
                    Messages.getString("DateValidationHelper.invalidDateTitle"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        label.setIcon(null);
        label.setToolTipText(null);
        return true;
    }
    
    private static Date removeTimeComponent(Date date){
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	cal.clear(Calendar.MILLISECOND);
    	cal.clear(Calendar.SECOND);
    	cal.clear(Calendar.MINUTE);
    	cal.clear(Calendar.HOUR);
    	cal.clear(Calendar.HOUR_OF_DAY);
    	cal.clear(Calendar.AM_PM);
    	return cal.getTime();
    }
    
}
