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

package org.psygrid.securitymanager.ui;

import javax.swing.JTextField;
import javax.swing.text.*;

/**
 * Enforces first letter of the textfield to be capitalised.
 * Currently unused but we might need this to enforce standard
 * ldap entries.
 * @author pwhelan
 *
 */
public class CustomFirstLetterUpperCaseField extends JTextField {
	
	public CustomFirstLetterUpperCaseField(int cols) {
         super(cols);
     }
 
	protected Document createDefaultModel() {
		return new CustomFirstLetterUpperCaseDocument();
    }
 
    static class CustomFirstLetterUpperCaseDocument extends PlainDocument {
 
    	public void insertString(int offs, String str, AttributeSet a) 
 	    	throws BadLocationException {
 
 	          if (str == null) {
 		      return;
 	          }
 	          char[] upper = str.toCharArray();
 	          
 	          if (offs == 0 && upper.length > 0)
 	          {
 	        	  upper[0] = Character.toUpperCase(upper[0]);
 	        	  
 	          }
 	          
 	          super.insertString(offs, new String(upper), a);
 	      }
     }
 }