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

package org.psygrid.esl.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.psygrid.esl.model.IChange;
import org.psygrid.esl.model.IProvenanceChange;
import org.psygrid.esl.model.IProvenanceLog;
import org.psygrid.esl.model.hibernate.Auditable;
import org.psygrid.esl.model.hibernate.Change;
import org.psygrid.esl.model.hibernate.ProvenanceChange;
import org.psygrid.esl.model.hibernate.ProvenanceLog;

/**
 * Hibernate entity interceptor for the ESL.
 * 
 * Used to perform some pre-database-commit alterations to persistent
 * objects.
 * 
 * @author Lucy Bridges
 *
 */
public class EntityInterceptor extends EmptyInterceptor {

	
	static final long serialVersionUID = -627854012730649842L;

	/**
	 * General purpose logger
	 */
	//private static Log sLog = LogFactory.getLog(EntityInterceptor.class);

	private static final ThreadLocal<String> user = new ThreadLocal<String>();
	
	// Store a thread local mapping of Auditable object IDs to a list of changes made to those objects.
	// These changes will be written to the database after a session is flushed.
	private static final ThreadLocal<Map<Serializable,List<Change>>> changeMap = new ThreadLocal<Map<Serializable,List<Change>>>(); 

	private SessionFactory sessionFactory;
	
	private static final String UPDATE = "update";
	//private static final String INSERT = "insert";
	//private static final String DELETE = "delete";	

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public static String getUserName() {
		return user.get();
	}

	public static void setUserName(String userName) {
		if ( userName != null && getUserName() != null){
			//if the username is not null then thread-safety has been
			//violated!
			throw new RuntimeException("Thread safety violated in EntityInterceptor!");
		}
		user.set(userName);
	}

	
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) throws CallbackException {

		boolean changed = false;
		
		// When ProvenanceLog objects are first inserted into the database
		// set their username and timestamp to the current user and time
		if ( entity instanceof ProvenanceLog){
            for ( int i=0; i<propertyNames.length; i++ ) {
                if (propertyNames[i].equals("createdBy")) {
                    state[i] = getUserName();
                    changed = true;
                }
                else if (propertyNames[i].equals("created")) {
                    state[i] = new Date();
                    changed = true;
                }
            }
		}
		
		return changed;
	}

	
	public boolean onFlushDirty(Object entity, Serializable ID, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {

		// This is a thread safe version of : http://www.hibernate.org/318.html

		if ( entity instanceof Auditable ){

			    // Open a session with a new connection to grab the old value
			    // of the Auditable object.
				Session tmpsession = sessionFactory.openSession();

				Class objectClass = entity.getClass();

				try {
					// Use the id and class to get the pre-update state of the 
					// object from the database.
					Serializable persistedObjectId = getObjectId(entity);
					Object preUpdateState = tmpsession.get(objectClass,  persistedObjectId);

					// Generate a list of changes for the object being updated.
					List<Change> changes = getChanges(entity, preUpdateState, UPDATE);
					
					// Put the changes in the changes map to be saved in the postFlush handler.
					if(changes.size()>0){
						// Lazily initialize the change map.
						if(changeMap.get()==null){
							changeMap.set(new HashMap<Serializable,List<Change>>());
						}
						changeMap.get().put(persistedObjectId, changes);
					}
				} 
				catch (Exception e) {
					throw new RuntimeException("Could not log changes: ", e);
				}
				finally {
					tmpsession.close();
				}
		}

		return false;
	}


	public void postFlush(Iterator arg0) throws CallbackException {

		// This code is messy because we need to fit in with the previous 
		// implementation which was was wrongly adding objects to the object graph
		// in the onSave() and onFlushDirty() handlers.
		// Note - this implementation is still unsatisfactory because it uses
		// a temporary session to add ProvenanceChange objects to the ProvenanceLogs
		// of the Auditable objects that have just been flushed but are still held in
		// the current session. This is okay as long as the objects in the current session 
		// are not changed again after it has been flushed.
		//
		// Basically - Audit logging should be external to the domain model and should
		// NOT be trying to attach the change logs directly to the domain objects.
		//
		// Using a temporary session to save the audit log is the accepted solution 
		// - see Java Persistence With Hibernate pg 546.
		// This code is a compromise so we don't have to change the domain model and
		// all the Change reporting code.
		
		// Grab the map of changes
		Map<Serializable,List<Change>> entityChangeMap = changeMap.get();
		
		if(entityChangeMap!=null){

			// Clear the threadlocal change map so that calling flush on
			// the temporary session below does not try to re-enter this code.
			changeMap.set(null);
			
			// Open a temporary session with the underlying connection of the current 
			// thread-bound session.
			// This ensures that the changes we are about to save are saved
			// in the same transaction as their changed objects.
			Session tmpsession = sessionFactory.openSession( sessionFactory.getCurrentSession().connection());
	        			
	        try {
	        	// For each Auditable entity that was updated, grab its ProvenanceLog
	        	// and add a ProvenanceChange object to this, then add a set of Change
	        	// objects pointing back to the new ProvenanceChange.
	        	for(Serializable id:entityChangeMap.keySet()){
	        	 Auditable entity = (Auditable)tmpsession.get(Auditable.class, id);
	        	 IProvenanceLog log = entity.getLog();
	        	 IProvenanceChange provChange = new ProvenanceChange();
	        	 provChange.setUser(user.get());
	        	 provChange.setTimestamp(new Date());
	        	 log.addProvenanceChange(provChange);
	        	 provChange.setProvenanceLog(log);
	        	 tmpsession.saveOrUpdate(provChange);
	        	 tmpsession.saveOrUpdate(log);
	        	 for(Change change:entityChangeMap.get(id)){
	        		 change.setProvenance(provChange);
		             tmpsession.save(change);	        		 
	        	 }
	            }
	            tmpsession.flush();
	        } catch (Exception e) {
				throw new RuntimeException("Could not log changes: ", e);
	        } finally {
	            tmpsession.close();
	        }
		}
	}
	

	public void afterTransactionCompletion(Transaction tx) {
		user.set(null);
		changeMap.set(null);
	}




	/**
	 * Gets the id of the persisted object
	 * @param obj the object to get the id from
	 * @return object Id
	 */
	private Serializable getObjectId(Object obj) throws Exception {

		Class objectClass = obj.getClass();
		Method[] methods = objectClass.getMethods();

		Serializable persistedObjectId = null;
		for (int ii = 0; ii < methods.length; ii++) {
			// If the method name equals 'getId' then invoke it to get the id of the object.
			if (methods[ii].getName().equals("getId")) {
				try {
					persistedObjectId = (Serializable)methods[ii].invoke(obj, null);
					break;      
				} catch (Exception e) {
					throw new Exception("Audit Log Failed - Could not get persisted object id: " + e.getMessage());
				}
			}
		}
		return persistedObjectId;

	}

	private List<Change> getChanges(Object newObject, Object existingObject, String event) { 

		Class c = newObject.getClass();      
		Field[] fields = c.getDeclaredFields();  //excludes inherited fields

		List<Change> changes = new ArrayList<Change>();


		fieldIteration:	for (Field f: fields) {

			//make any private fields accessible so we can access their values
			f.setAccessible(true);

			//if the current field is static, transient or final then don't log it as 
			//these modifiers are very unlikely to be part of the data model.
			if(Modifier.isTransient(f.getModifiers())
					|| Modifier.isFinal(f.getModifiers())
					|| Modifier.isStatic(f.getModifiers())) {
				continue fieldIteration;
			}

			if(event.equals(UPDATE)) {

				try {

					String fieldName = f.getName();
					if(! fieldName.equals("id")) {
						Class interfaces[] = f.getType().getInterfaces();

						for (int i = 0; i < interfaces.length; i++) {

							if (interfaces[i].getName().equals("java.util.Collection")) {
								continue fieldIteration;					
							}
							//if the field is a class that is to be audited, don't log any changes as these will be done automatically later on
							else if(interfaces[i].getName().equals("org.psygrid.esl.model.IAuditable")){
								continue fieldIteration;
							}
						} 
					}

					String propertyNewState;
					String propertyPreUpdateState;
					boolean isDate = false;

					//get new field values
					try {
						Object objPropNewState = f.get(newObject);
						if (objPropNewState != null) {
							propertyNewState = objPropNewState.toString();

							if (objPropNewState instanceof java.util.Date) {
								isDate = true;
							}
						} 
						else {
							propertyNewState = "";
						}
					} 
					catch (Exception e) {
						propertyNewState = "";
					}

					try {
						Object objPreUpdateState = f.get(existingObject);
						if (objPreUpdateState != null) {
							propertyPreUpdateState = objPreUpdateState.toString();
						} 
						else {
							propertyPreUpdateState = "";
						}
					} catch (Exception e) {
						propertyPreUpdateState = "";
					}

					//now we have the two property values - compare them
					if (propertyNewState.equals(propertyPreUpdateState)) {
						continue fieldIteration; // Values haven't changed so loop to next property
					} 
					else  {

						if (isDate) {
							Date newState = new Date();
							Date oldState = new Date();
							try {
								Date tmp1 = (Date)f.get(newObject);
								Date tmp2 = (Date)f.get(existingObject);
								//read into new objects to ensure formating is the same (otherwise compareTo will not work as expected)
								newState.setTime(tmp1.getTime());	
								oldState.setTime(tmp2.getTime());

								if (newState.compareTo(oldState) == 0) {
									//Dates are equal, so values haven't changed and we can continue iteration
									continue fieldIteration;
								}
							}
							catch(Exception e) {
								//continue below
							}
						}

						//find out if the value of the field has changed and log if so
						Change change = new Change();
						change.setField(f.getName());

						//hibernate doesn't like plain objects, so they've been converted to strings
						change.setPrevValue(propertyPreUpdateState);
						change.setNewValue(propertyNewState);

						changes.add(change);

					}
				}
				catch(Exception e) {
					throw new RuntimeException("problem accessing or logging field values", e);
				}

			}
		}

		return changes;

	}

}
