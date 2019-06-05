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

package org.psygrid.security.attributeauthority.dao.hibernate;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.psygrid.security.attributeauthority.dao.DAOException;
import org.psygrid.security.attributeauthority.dao.ObjectOutOfDateException;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;

/**
 * Interceptor to be used when saving objects within a transaction.
 * 
 * @author Rob Harper
 * 
 * @see http://forum.springframework.org/showthread.php?t=9669
 *
 */
public class DAOInterceptor implements MethodInterceptor {

    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            //invoke the underlying method
            Object result = invocation.proceed();
            return result;
        } 
        catch (HibernateOptimisticLockingFailureException ex) {
            //convert the exception into User defined exception then rethrow it
            throw new ObjectOutOfDateException("Cannot save object - the object is out-of-date",ex);
        }
        catch (DataAccessException ex) {
            throw new DAOException("Data Access Exception: "+ex.getMessage());
        }
        catch (Exception e){
        		e.printStackTrace();
        		throw e;
        }
        
    }

}
