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


package org.psygrid.common.identifier;

/**
 * @author Rob Harper
 *
 */
public class IdentifierHelper {

    /**
     * Character used to divide the project and group sections
     * of the overall identifier string.
     */
    public static final String PROJ_GRP_SEPARATOR = "/";
    
    /**
     * Character used to divide the group and suffix sections
     * of the overall identifier string.
     */
    public static final String GRP_SUFF_SEPARATOR = "-";
    
    public static final String getProjectCodeFromIdentifier(String identifier) throws InvalidIdentifierException {
        checkIdentifier(identifier);
        return identifier.substring(0, identifier.indexOf(PROJ_GRP_SEPARATOR));
    }
    
    public static final String getGroupCodeFromIdentifier(String identifier) throws InvalidIdentifierException {
        checkIdentifier(identifier);
        return identifier.substring(identifier.indexOf(PROJ_GRP_SEPARATOR)+1, identifier.indexOf(GRP_SUFF_SEPARATOR));
    }
    
    public static final void checkIdentifier(String identifier) throws InvalidIdentifierException {
        //find position of the first project/group separator and last group/suffix 
        //separator, and the number of project/group and group/suffix separators
        int projGrpPos = identifier.indexOf(PROJ_GRP_SEPARATOR);
        int grpSuffPos = identifier.lastIndexOf(GRP_SUFF_SEPARATOR);
        int numProjGrpSep = 0;
        int numGrpSuffSep = 0;
        for ( int i=0; i<identifier.length(); i++ ){
            String s = identifier.substring(i,i+1);
            if ( s.equals(PROJ_GRP_SEPARATOR) ){
                numProjGrpSep++;
            }
            if ( s.equals(GRP_SUFF_SEPARATOR) ){
                numGrpSuffSep++;
            }
        }
        
        if ( projGrpPos < 0 || grpSuffPos < 0 || projGrpPos > grpSuffPos || numProjGrpSep != 1 || numGrpSuffSep != 1 ){
            throw new InvalidIdentifierException("The supplied identifier '"+identifier+"' does not have the correct format. "+
            		"The format should be <project code>"+PROJ_GRP_SEPARATOR+"<group code>"+
            		GRP_SUFF_SEPARATOR+"<number>");
        }
    	
    }
    
}
