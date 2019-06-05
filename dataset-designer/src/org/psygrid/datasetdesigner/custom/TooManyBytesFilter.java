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
package org.psygrid.datasetdesigner.custom;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Filters by the maximum number of bytes.
 * 
 * @author pwhelan
 */
public class TooManyBytesFilter extends DocumentFilter {

	/**
	 * Default max size for text entries
	 */
	private static final int MAX_CHARS = 256;
	
	/**
	 * The class log
	 */
	private static final Log LOG = LogFactory.getLog(TextFieldWithStatus.class);
	
	/**
	 * The document to which this filter is applied
	 */
	private Document parentDocument;
	
	/**
	 * Max size allowed
	 */
	private int maxSize;
	
	
	/**
	 * Constructor
	 * @param parentDocument the document to which this filter will be added
	 */
	public TooManyBytesFilter(Document parentDocument) {
		this(parentDocument, MAX_CHARS);
	}
	
	public TooManyBytesFilter(Document parentDocument, int maxSize) {
		super();
		this.parentDocument = parentDocument;
		this.maxSize = maxSize;
	}
	
	/**
	 * Validate the string to ensure it fits within the MAX_CHAR range
	 * @param string the String to check
	 * @return false if it does not validate; true if it does
	 */
	private boolean validateString(String string) {
		try{
            if (string.getBytes("UTF-8").length > maxSize){
                JOptionPane.showMessageDialog(null, PropertiesHelper.getStringFor("org.psygrid.dataset.designer.ui.textfieldwithstatus"));
                return false;
            }
    	} catch(UnsupportedEncodingException ex) {
    		//should never happen as encoding is hardcoded! But if ti does, log and
    		//fall back to string length check
    		LOG.error("Invalid charset!", ex);
            if ( string.length() > maxSize){
            	JOptionPane.showMessageDialog(null, PropertiesHelper.getStringFor("org.psygrid.dataset.designer.ui.textfieldwithstatus"));
            	return false;
            }
    	}
    	
    	return true;
	}
	
	
	
	
	/**
	 * Insert the string
     * @param fb FilterBypass that can be used to mutate Document
     * @param offset  the offset into the document to insert the content >= 0.
     *    All positions that track change at or after the given location 
     *    will move.  
     * @param string the string to insert
     * @param attr      the attributes to associate with the inserted
     *   content.  This may be null if there are no attributes.
	 */
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
		if (validateString(string)) {
			super.insertString(fb, offset, string, attr);
		}
	}

	/**
	 * Insert the string
     * @param fb FilterBypass that can be used to mutate Document
     * @param offset the offset from the beginning >= 0
     * @param length the number of characters to remove >= 0
	 */
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		super.remove(fb, offset, length);
	}

	/**
	 * Insert the string
	 * @param fb FilterBypass that can be used to mutate Document
     * @param offset Location in Document
     * @param length Length of text to delete
     * @param text Text to insert, null indicates no text to insert
     * @param attrs AttributeSet indicating attributes of inserted text,
     *              null is legal.
	 */
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		String newString = parentDocument.getText(0, parentDocument.getLength()) + text;
		
		if (validateString(newString)) {
			super.replace(fb, offset, length, text, attrs);
		}
	}
}
