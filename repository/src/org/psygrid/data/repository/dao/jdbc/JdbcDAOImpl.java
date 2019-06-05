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

package org.psygrid.data.repository.dao.jdbc;

import javax.sql.DataSource;

import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.JdbcDAO;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcDAOImpl implements JdbcDAO {

    private DataSource dataSource;

    private JdbcTemplate jt;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jt = new JdbcTemplate(dataSource);    
    }

    public Integer reserveIdentifierSpace(Long dataSetId, String group,
            int nIdentifiers) throws DAOException {
    
        {
            String sql = "update t_groups set c_max_suffix=c_max_suffix+? "+
                         "where c_dataset_id=? and c_name=?";
            Object[] params = new Object[]{ nIdentifiers, dataSetId, group };
            int x = jt.update(sql, params);
            if ( 0 == x ){
                throw new DAOException("No group exists for dataset="+dataSetId+" and group='"+group+"'");
            }
        }
        
        Integer newMaxSuffix = null;
        {
            String sql = "select c_max_suffix from t_groups "+
                         "where c_dataset_id=? and c_name=?";
            Object[] params = new Object[]{ dataSetId, group };
            newMaxSuffix = jt.queryForInt(sql, params);
        }
        
        return newMaxSuffix;
    }

    public void reserveIdentifier (Long dataSetId, String group, int suffix) throws DAOException {
                
        String sql = "select c_max_suffix from t_groups where c_dataset_id=? and c_name=?";
        Object[] params = new Object[]{ dataSetId, group };
     
        try{
            int currentMax = jt.queryForInt(sql, params);
            if ( suffix > currentMax ){
                sql = "update t_groups set c_max_suffix=? "+
                        "where c_dataset_id=? and c_name=?";
                params = new Object[]{ suffix, dataSetId, group };
                jt.update(sql, params);            
            }
        }
        catch(IncorrectResultSizeDataAccessException ex){
            throw new DAOException("No group exists for dataset="+dataSetId+" and group='"+group+"'", ex);
        }
        
    }
    
}
