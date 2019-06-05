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

package org.psygrid.dataimport.identifier;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.Identifier;

public abstract class AbstractParser {
	
	/**
	 * Defines the type of information that can be carried in a participant identifier from an external
	 * data source.
	 * @author williamvance
	 *
	 */
	public enum InfoType{
		UNIQUE_STUDY_ID,
		CENTRE_INFO,
		PROJECT
	}
	

	protected List<InfoType> infoTypes = null;
		
    protected List<String> projectCode = new ArrayList<String>();
    
    protected int suffixSize;
    
    /**
     * Constructor that accepts the project code that will feature
     * in the generated identifiers, and the suffix size (the number
     * of characters in the numeric suffix of the overall identifier).
     * 
     * @param projectCode
     */
    public AbstractParser(String projectCode, int suffixSize){
        this.projectCode.add(projectCode);
        this.suffixSize = suffixSize;
    }
    
    /**
     * Constructor that accepts more than one project code.
     * @param projectCodes
     * @param suffixSize
     */
    public AbstractParser(List<String> projectCodes, int suffixSize){
    	this.projectCode = projectCodes;
    	this.suffixSize = suffixSize;
    	
    }
    
    /**
     * Set the type(s) of information that is carried by the external participant identifier
     * @param infoTypes
     */
    public void setInfoTypes(List<InfoType> infoTypes){
    	this.infoTypes = infoTypes;
    }
    
     
    /**
     * Create a PsyGrid identifier from an input that is the native
     * identifier of the system where the data is being imported from.
     * <p>
     * Note that the identifier is not allocated or reserved in the 
     * PsyGrid system, that must be done by the caller.
     * 
     * @param input Identifier from the data source.
     * @return PsyGrid identifier object.
     * @throws DelimeterNotFoundException 
     * @throws TranslationException 
     */
    public abstract Identifier createIdentifier(String input) throws ParserException, DelimeterNotFoundException, TranslationException;
    
}
