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

package org.psygrid.data.utils.old;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;
import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.data.model.hibernate.Provenance;
import org.psygrid.data.model.hibernate.Response;
import org.psygrid.data.model.hibernate.TextValue;
import org.psygrid.data.model.hibernate.Value;

public class TestInterceptor extends EmptyInterceptor {

	private SessionFactory sf;
	private Map<Long,Provenance> provItems = new HashMap<Long,Provenance>();
	private Map<Long,TextValue> tvItems = new HashMap<Long,TextValue>();
	
	public TestInterceptor(){
		super();
	}

	public TestInterceptor(SessionFactory sf){
		this.sf = sf;
	}
	
	@Override
	public boolean onSave(Object arg0, Serializable arg1, Object[] arg2, String[] arg3, Type[] arg4) {
		if ( arg0 instanceof Value ){
			Value val = (Value)arg0;
            try{
                Provenance prov = new Provenance(null,val);
                //val.getResponse().getProvItems().add(prov);
            }
            catch(ModelException ex){
                //do nothing - this is a demo only - not to be used
                //in a production system!!
            }
		}
		return true;
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
		if ( entity instanceof TextValue ){
			TextValue tv = (TextValue)entity;			
			//Response resp = tv.getResponse();
			Response resp = null;
            
			//Create a new TextValue object containing the modified state
			int valueIndex = 0;
			int deprecatedIndex = 0;
			for ( int i=0 ; i<propertyNames.length; i++){
				if ( propertyNames[i].equals("value"))
					valueIndex = i;
				else if ( propertyNames[i].equals("deprecated"))
					deprecatedIndex = i;
				
			}
            TextValue newTv = null;
            try{
                newTv = new TextValue((String)currentState[valueIndex]);
            }
            catch(ModelException ex){
                //do nothing - this is a demo only - not to be used 
                //in a production system!
            }
            tvItems.put(resp.getId(), newTv);    
			
			//roll back the currentState to the previousState, and set
			//deprecated = True
			currentState[valueIndex] = previousState[valueIndex];
			currentState[deprecatedIndex] = Boolean.valueOf(true);
			
			//create provenance object to attach to the response
            try{
                Provenance prov = new Provenance(tv, newTv);
                provItems.put(resp.getId(), prov);
            }
            catch(ModelException ex){
                //do nothing - this is a demo only - not to be used
                //in a production system!!
            }
			System.out.println("onFlushDirty: Response object");
		}
		return true;
	}

	@Override
	public void postFlush(Iterator entities) {
		Session session = sf.openSession( HibernateUtil.currentSession().connection() );
		
		for (Entry<Long, TextValue> e: tvItems.entrySet()){
			Response tr = (Response)session.createQuery("from BasicResponse as br where br.id = ?").setLong(0, e.getKey()).uniqueResult();
			TextValue tv = e.getValue();
			//tv.setResponse(tr);
			session.save(tv);
		}
		
		for ( Entry<Long, Provenance> e: provItems.entrySet()){
			Response resp = (Response)session.createQuery("from BasicResponse as r where r.id = ?").setLong(0,e.getKey()).uniqueResult();
			resp.getProvItems().add(e.getValue());
			session.save(resp);
		}
		
		provItems.clear();
		tvItems.clear();
		session.flush();
		session.close();
			
	}
	
	
}
