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

package org.psygrid.transformers.impl.postcode;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.psygrid.transformers.TransformerException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class PostCodeDAOImpl extends HibernateDaoSupport implements PostCodeDAO {

    public String getLowerSoaForPostcode(String postCode) throws TransformerException {
        
        String lowerSoa = null;
        
        if ( null != postCode ){
            
            postCode = postCode.toUpperCase();
    
            //check that the input is a valid postcode - no point
            //in going to the database if it isn't!
            Pattern p = Pattern.compile("(([A-PR-UWYZ](([0-9][0-9A-HJKSTUW]?)|([A-HK-Y][0-9][0-9ABEHMNPRVWXY]?))[ ]{0,2}[0-9][ABD-HJLNP-UW-Z]{2,2})|(GIR[ ]{0,2}0AA))");
            Matcher m = p.matcher(postCode);
            if ( !m.matches() ){
                throw new TransformerException("The supplied postcode is not valid");
            }
            
            //do some manipulation of the postcode string to enhance the chances
            //of getting a match - make sure that there are always 7 characters
            if ( postCode.length() < 7 ){
                //need to pad with spaces before the final three characters
                int finalPartPos = postCode.length() - 3;
                StringBuilder builder = new StringBuilder();
                builder.append(postCode.substring(0,finalPartPos));
                for ( int i=0; i<7-postCode.length(); i++ ){
                    builder.append(" ");
                }
                builder.append(postCode.substring(finalPartPos, postCode.length()));
                postCode = builder.toString();
            }
            else if ( postCode.length() > 7 ){
                //need to remove additional spaces
                int finalPartStart = postCode.length() - 3;
                int firstPartEnd = postCode.indexOf(" ");
                StringBuilder builder = new StringBuilder();
                builder.append(postCode.substring(0, firstPartEnd));
                for ( int i=0; i<(7-3-firstPartEnd); i++ ){
                    builder.append(" ");
                }
                builder.append(postCode.substring(finalPartStart, postCode.length()));
                postCode = builder.toString();
            }
            
            Iterator result = getHibernateTemplate().find("select p.outputArea.lowerSoa.code from PostCode p where p.value=?",
                                                      postCode).iterator();
            while ( result.hasNext() ){
                lowerSoa = (String)result.next();
            }
            
            if ( null == lowerSoa ){
                throw new TransformerException("No lower-layer super output area found for the postcode '"+postCode+"'");
            }
        }
        return lowerSoa;
    }

    public void saveMiddleSoa(MiddleSOA middleSOA) {
        
        getHibernateTemplate().saveOrUpdate(middleSOA);
        
    }

}
