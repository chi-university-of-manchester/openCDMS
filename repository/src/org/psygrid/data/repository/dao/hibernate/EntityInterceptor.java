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

package org.psygrid.data.repository.dao.hibernate;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.psygrid.data.model.hibernate.ChangeHistory;
import org.psygrid.data.model.hibernate.Provenance;
import org.psygrid.data.model.hibernate.StatusedInstance;

/**
 * Hibernate interceptor for the repository.
 * <p>
 * Used to perform some pre-database-commit alterations to persistent
 * objects, namely (1) set the username property of Provenance and
 * ChangeHistory objects, (2) set the edited date of StatusedInstance
 * objects, (3) set the parentId of ChangeHistory objects.
 * 
 * @author Rob Harper
 *
 */
public class EntityInterceptor extends EmptyInterceptor {
    
    static final long serialVersionUID = -627873012730649842L;
    
    /**
     * The username of the user who is performing this transaction.
     * <p>
     * A ThreadLocal variable is used to ensure thread-safety.
     */
    private static final ThreadLocal<String> user = new ThreadLocal<String>();
    
    /**
     * The ChangeHistory object that is considered to be the parent
     * of all other ChangeHistory objects in this transaction.
     * <p>
     * Typically this will be the latest ChangeHistory object 
     * associated with the Record being saved, this being the parent
     * of ChangeHistory objects associated with DocumentInstances
     * in the Record.
     * <p>
     * A ThreadLocal variable is used to ensure thread-safety.
     */
    private static final ThreadLocal<ChangeHistory> parentHistory = new ThreadLocal<ChangeHistory>();
    
    public static void setUserName(String userName){
        if ( null != userName && null != getUserName() ){
            //if the username is not null then thread-safety has been
            //violated!
            throw new RuntimeException("Thread safety violated in EntityInterceptor (username)!");
        }
    	user.set(userName);
    }
    
    public static String getUserName(){
    	return (String)user.get();
    }
    
    public static void setParentHistory(ChangeHistory parent){
        if ( null != parent && null != getParentHistory() ){
            //if the parent history is not null then thread-safety has been
            //violated!
            throw new RuntimeException("Thread safety violated in EntityInterceptor (parent history)!");
        }
    	parentHistory.set(parent);
    }
    
    public static ChangeHistory getParentHistory(){
    	return (ChangeHistory)parentHistory.get();
    }
    
    public boolean onSave(Object entity, Serializable id, Object[] state,
            String[] propertyNames, Type[] types) throws CallbackException {
        if ( entity instanceof Provenance ){
            for ( int i=0; i<propertyNames.length; i++ ) {
                if ( "user".equals( propertyNames[i] ) ) {
                    state[i] = getUserName();
                    return true;
                }
            }
        }
        if ( entity instanceof StatusedInstance ){
            for ( int i=0; i<propertyNames.length; i++ ) {
                if ( "edited".equals( propertyNames[i] ) ) {
                    state[i] = new Date();
                    return true;
                }
            }
        }
        if ( entity instanceof ChangeHistory ){
        	boolean changed = false;
            for ( int i=0; i<propertyNames.length; i++ ) {
                if ( "user".equals( propertyNames[i] ) ) {
                    state[i] = getUserName();
                    changed = true;
                }
                if ( "parentId".equals( propertyNames[i] ) ){
                	if ( null != getParentHistory() ){
                		state[i] = getParentHistory().getId();
                		changed = true;
                	}
                }
            }
            return changed;
        }
        return false;
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        if ( entity instanceof StatusedInstance ){
            for ( int i=0; i<propertyNames.length; i++ ) {
                if ( "edited".equals( propertyNames[i] ) ) {
                	currentState[i] = new Date();
                	return true;
                }
            }
        }
        return false;        
    }

    @Override
    public void afterTransactionCompletion(Transaction tx) {
        super.afterTransactionCompletion(tx);
        setUserName(null);
        setParentHistory(null);
    }

}
