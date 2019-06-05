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

import org.psygrid.data.model.hibernate.Identifier;

public class NedenParser extends AbstractParser {

    public NedenParser(String projectCode, int suffixSize) {
        super(projectCode, suffixSize);
    }

    public Identifier createIdentifier(String input) throws ParserException {
        /*
         * The format of the NEden identifiers is AAA/BB, where:
         * + BB is a two-digit number that represents the group/hub
         * + AAA is a three-digit number that represents the patient
         * number within the hub.
         * 
         * Additionally, the patient number is divided into ranges
         * to indicate sub-divisions within a group.
         */
        
        //get the group and subject
        Integer nedenGroup = Integer.parseInt(input.substring(input.indexOf("/")+1));
        Integer nedenSubject = Integer.parseInt(input.substring(0, input.indexOf("/")));
        
        String psygridGroup = null;
        Integer psygridSubject = null;
        
        switch (nedenGroup){
        case 1:
            psygridGroup = "001001";
            psygridSubject = nedenSubject;
            break;
        case 2:
            psygridGroup = "002001";
            psygridSubject = nedenSubject;
            break;
        case 3:
            psygridGroup = "003001";
            psygridSubject = nedenSubject;
            break;
        case 4:
            if ( nedenSubject.intValue() <= 400 ){
                psygridGroup = "004001";
            }
            else{
                psygridGroup = "004002";
            }
            psygridSubject = nedenSubject;
            break;
        case 5:
            psygridGroup = "005001";
            psygridSubject = nedenSubject;
            break;
        case 6:
            psygridGroup = "006001";
            psygridSubject = nedenSubject;
            break;
        case 7:
            if ( nedenSubject.intValue() <= 500 ){
                psygridGroup = "007001";
            }
            else{
                psygridGroup = "007002";
            }
            psygridSubject = nedenSubject;
            break;
        case 8:
            psygridGroup = "008001";
            psygridSubject = nedenSubject;
            break;
        default:
            throw new ParserException("Unknown National Eden group");
        }
        
        Identifier psygridIdentifier = new Identifier();
        psygridIdentifier.initialize(this.projectCode.get(0), psygridGroup, psygridSubject.intValue(), this.suffixSize);
        
        return psygridIdentifier;
    }

}
