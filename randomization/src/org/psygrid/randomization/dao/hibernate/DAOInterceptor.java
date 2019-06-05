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

package org.psygrid.randomization.dao.hibernate;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.psygrid.randomization.dao.DuplicateRandomizerException;
import org.psygrid.randomization.dao.RandomizerDAOException;
import org.psygrid.randomization.dao.UnknownRandomizerException;
import org.psygrid.randomization.model.DuplicateSubjectException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

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
        catch (DuplicateRandomizerException ex){
            throw ex;
        }
        catch (DuplicateSubjectException ex){
            throw ex;
        }
        catch (UnknownRandomizerException ex){
            throw ex;
        }
        catch (DataIntegrityViolationException ex) {
            //convert the exception into User defined exception then rethrow it
            throw new DuplicateRandomizerException("Cannot save randomizer - a randomizer already exists with the same name.",ex);
        }
        catch (DataAccessException ex) {
            throw new RandomizerDAOException("Data Access Exception: "+ex.getMessage());
        }
        catch (Exception ex) {
            throw new RandomizerDAOException("Exception: "+ex.getClass().getName()+"-" +ex.getMessage());
        }
        
    }

}
