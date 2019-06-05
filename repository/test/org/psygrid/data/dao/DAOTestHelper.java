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

package org.psygrid.data.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.data.model.hibernate.Identifier;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.StandardCode;
import org.psygrid.data.repository.dao.RepositoryDAO;

public class DAOTestHelper {

    public static Identifier[] getIdentifiers(RepositoryDAO dao, String projectCode, Long dsId, int number, String group) throws Exception {
        if ( null == group ){
            group = "FOO";
        }
        int maxSuffix = dao.reserveIdentifierSpace(dsId, group, number);
        org.psygrid.data.model.dto.IdentifierDTO[] dtoIds = dao.generateIdentifiers(projectCode, group, number, maxSuffix, "NoUser");
        Identifier[] ids = new Identifier[dtoIds.length];
        Map<org.psygrid.data.model.dto.PersistentDTO, Persistent> refs = new HashMap<org.psygrid.data.model.dto.PersistentDTO, Persistent>();
        for ( int i=0; i<dtoIds.length; i++ ){
            ids[i] = dtoIds[i].toHibernate(refs);
        }
        return ids;
    }
    
    public static String checkProjectCode(String projectCode){
        StringBuilder builder = new StringBuilder();
        for ( int i=0; i<projectCode.length(); i++ ){
            String s = projectCode.substring(i, i+1);
            if ( s.equals(IdentifierHelper.PROJ_GRP_SEPARATOR) ||
                    s.equals(IdentifierHelper.GRP_SUFF_SEPARATOR) ){
                builder.append("?");
            }
            else{
                builder.append(s);
            }
        }
        return builder.toString();
    }
    
    public static List<StandardCode> getStandardCodes(RepositoryDAO dao) throws Exception {
        
    	org.psygrid.data.model.dto.StandardCodeDTO[] dtoCodes = dao.getStandardCodes();
        List<StandardCode> codes = new ArrayList<StandardCode>();
        for ( org.psygrid.data.model.dto.StandardCodeDTO code : dtoCodes ){
            codes.add(code.toHibernate());
        }
        return codes;
    }
}
