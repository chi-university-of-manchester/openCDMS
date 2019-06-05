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

package org.psygrid.data.utils.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.repository.dao.ObjectOutOfDateException;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;

/**
 * Interceptor to be used to log access to service methods.
 * 
 * @author Rob Harper
 *
 */
public class ServiceInterceptor implements MethodInterceptor {
	
	private static Log log = LogFactory.getLog(ServiceInterceptor.class);

    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            //invoke the underlying method
            Object result = invocation.proceed();
            return result;
        } 
        catch (HibernateOptimisticLockingFailureException ex) {
			log.error(ex.getMessage(),ex);
            throw new ObjectOutOfDateException("Cannot save object - the object is out-of-date",ex);
        }
        catch (DataAccessException ex) {
			log.error(ex.getMessage(),ex);
			throw new RepositoryServiceFault(ex);
        }
		catch(Exception ex){
			// Log all exceptions thrown to the client as errors
			log.error(ex.getMessage(),ex);
			throw ex;
		}
    }

}
