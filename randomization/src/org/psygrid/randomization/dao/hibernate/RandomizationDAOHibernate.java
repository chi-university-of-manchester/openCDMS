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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.psygrid.randomization.Parameter;
import org.psygrid.randomization.dao.DuplicateRandomizerException;
import org.psygrid.randomization.dao.RandomizationDAO;
import org.psygrid.randomization.dao.RandomizerDAOException;
import org.psygrid.randomization.dao.UnknownRandomizerException;
import org.psygrid.randomization.model.DuplicateSubjectException;
import org.psygrid.randomization.model.RandomizerException;
import org.psygrid.randomization.model.hibernate.PersistableRNG;
import org.psygrid.randomization.model.hibernate.Randomizer;
import org.psygrid.randomization.model.hibernate.RpmrblRandomizer;
import org.psygrid.randomization.model.hibernate.Treatment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class RandomizationDAOHibernate extends HibernateDaoSupport implements RandomizationDAO {

    public PersistableRNG getRng(Long id) {
        return (PersistableRNG)getHibernateTemplate().get(PersistableRNG.class, id);
    }

    public Long saveRng(PersistableRNG rng) {
        getHibernateTemplate().saveOrUpdate(rng);
        return rng.getId();
    }

    public org.psygrid.randomization.model.dto.RpmrblRandomizer getRpmrblRandomizer(final Long id) {
        
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                RpmrblRandomizer r = (RpmrblRandomizer)session.createCriteria(RpmrblRandomizer.class)
                                                              .add(Restrictions.idEq(id))
                                                              .uniqueResult();
                
                org.psygrid.randomization.model.dto.RpmrblRandomizer dtoR = null;
                if ( null != r ){
                    dtoR = r.toDTO();
                }
                return dtoR;
            }
        };
        
        org.psygrid.randomization.model.dto.RpmrblRandomizer r = (org.psygrid.randomization.model.dto.RpmrblRandomizer)getHibernateTemplate().execute(callback);
        return r;
    }

    public Long saveRpmrblRandomizer(org.psygrid.randomization.model.dto.RpmrblRandomizer r) {
        RpmrblRandomizer randomizer = r.toHibernate();
        getHibernateTemplate().saveOrUpdate(randomizer);
        return randomizer.getId();
    }

    
    public String allocate(final String rdmzrName, final String subject, final Parameter[] parameters) 
            throws DuplicateSubjectException, UnknownRandomizerException, RandomizerException, RandomizerDAOException {

        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){

                Randomizer rdmzr = (Randomizer)session.createQuery("from Randomizer r where r.name=?")
                                                      .setString(0, rdmzrName)
                                                      .uniqueResult();
                
                if ( null == rdmzr ){
                    //no randomizer found
                    return new UnknownRandomizerException("No randomizer exists with the name '"+rdmzrName+"'");
                }
                
                rdmzr.initialize(); 
                
                if(parameters != null){
	                for ( Parameter p: parameters ){
	                    rdmzr.setParameter(p.getKey(), p.getValue());
	                }
                }
                
                String treatment = null;
                try{
                    treatment = rdmzr.allocate(subject);
                }
                catch(DuplicateSubjectException ex){
                    return ex;
                }
                catch(RandomizerException ex){
                    return ex;
                }
                
                session.saveOrUpdate(rdmzr);
                
                return treatment;
            }
        };
        
        Object result = getHibernateTemplate().execute(callback);
        if ( result instanceof DuplicateSubjectException ){
            throw (DuplicateSubjectException)result;
        }
        if ( result instanceof UnknownRandomizerException ){
            throw (UnknownRandomizerException)result;
        }
        if ( result instanceof RandomizerException ){
            throw (RandomizerException)result;
        }
        return (String)result;
    }

    public String getAllocation(final String rdmzrName, final String subject) throws UnknownRandomizerException, RandomizerDAOException {
        
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){

                Randomizer rdmzr = (Randomizer)session.createQuery("from Randomizer r where r.name=?")
                                                      .setString(0, rdmzrName)
                                                      .uniqueResult();
                
                if ( null == rdmzr ){
                    return new UnknownRandomizerException("No randomizer exists with the name '"+rdmzrName+"'");
                }
                
                String treatment = null;
                try{
                    treatment = rdmzr.getAllocation(subject);
                }
                catch(RandomizerException ex){
                    throw new RuntimeException(ex);
                }
                
                return treatment;
            }
        };

        Object result = getHibernateTemplate().execute(callback);
        return (String)result;
    }
    
	
	public String[] getRandomisedParticipantsWithinTimeframe(
			final String randomiserName, final Date startBoundaryInclusive,
			final Date endBoundaryDelimiter) throws UnknownRandomizerException {
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){

                Randomizer randomiser = (Randomizer)session.createQuery("from Randomizer r where r.name=?")
                                                      .setString(0, randomiserName)
                                                      .uniqueResult();
                
                if ( null == randomiser ){
                    return new UnknownRandomizerException("No randomizer exists with the name '"+randomiserName+"'");
                }
                
                String[] allocatedParticipants = null;
                
                allocatedParticipants = randomiser.getRandomisedParticipantsWithinTimeframe(startBoundaryInclusive, endBoundaryDelimiter);
                
                return allocatedParticipants;
            }
        };

        Object result = getHibernateTemplate().execute(callback);
        return (String[])result;
	}
    
    public String[] getAllocation(final String rdmzrName, final String subject, final Date date) throws UnknownRandomizerException, RandomizerDAOException {
    	HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){

                Randomizer rdmzr = (Randomizer)session.createQuery("from Randomizer r where r.name=?")
                                                      .setString(0, rdmzrName)
                                                      .uniqueResult();
                
                if ( null == rdmzr ){
                    return new UnknownRandomizerException("No randomizer exists with the name '"+rdmzrName+"'");
                }
                
                Treatment treatment = null;
                try{
                    treatment = rdmzr.getAllocation(subject, date);		//new sql statement?
                }
                catch(RandomizerException ex){
                    throw new RuntimeException(ex);
                }
                
                if (null == treatment) {
                	return new RandomizerDAOException("No treatment exists for "+subject);
                }
                return new String[]{treatment.getCode(), treatment.getName()};
            }
        };

        Object result = getHibernateTemplate().execute(callback);
        if (result instanceof UnknownRandomizerException) {
        	throw (UnknownRandomizerException)result;
        }
        if (result instanceof RandomizerDAOException) {
        	throw (RandomizerDAOException)result;
        }
        return (String[])result;
    }
    
    public org.psygrid.randomization.model.dto.Randomizer getRandomizer(final String name) throws RandomizerDAOException{
        
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                Randomizer r = (Randomizer)session.createQuery("from Randomizer r where r.name=?")
                                                  .setString(0, name)
                                                  .uniqueResult();
                
                org.psygrid.randomization.model.dto.Randomizer dtoR = null;
                if ( null != r ){
                    dtoR = r.toDTO();
                }
                return dtoR;
            }
        };
        
        org.psygrid.randomization.model.dto.Randomizer r = (org.psygrid.randomization.model.dto.Randomizer)getHibernateTemplate().execute(callback);
        return r;
    }

    public Long saveRandomizer(org.psygrid.randomization.model.dto.Randomizer r) throws DuplicateRandomizerException, RandomizerDAOException {
        Randomizer randomizer = r.toHibernate();
        try{
            getHibernateTemplate().saveOrUpdate(randomizer);
            return randomizer.getId();
        }
        catch(DataIntegrityViolationException ex){
            throw new DuplicateRandomizerException("A randomizer with name '"+r.getName()+"' already exists.", ex);
        }
    }

    public Long saveRandomizer(Randomizer r) {
        getHibernateTemplate().saveOrUpdate(r);
        return r.getId();
    }

    public void deleteRandomizer(final String rdmzrName) throws RandomizerDAOException {
    	HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                Randomizer rdmzr = (Randomizer)session.createQuery("from Randomizer r where r.name=?")
                                                      .setString(0, rdmzrName)
                                                      .uniqueResult();
                
                if ( null == rdmzr ){
                    return new UnknownRandomizerException("No randomizer exists with the name '"+rdmzrName+"'");
                }
            
                session.delete(rdmzr);
                
                return null;
            }
        };
        
		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof RandomizerDAOException ){
			throw (RandomizerDAOException)result;
		}

    }

    
    
    public boolean checkIntegrity(final String rdmzrName) throws RandomizerException, UnknownRandomizerException, RandomizerDAOException {
        
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                Randomizer r = (Randomizer)session.createQuery("from Randomizer r where r.name=?")
                                                  .setString(0, rdmzrName)
                                                  .uniqueResult();
                
                if ( null == r ){
                    return new UnknownRandomizerException("No randomizer exists with the name '"+rdmzrName+"'");
                }
                
                try{
                    return new Boolean(r.checkIntegrity());
                }
                catch(RandomizerException ex){
                    return ex;
                }
                
            }
        };
        
        Object result = getHibernateTemplate().execute(callback);
        if ( result instanceof UnknownRandomizerException ){
            throw (UnknownRandomizerException)result;
        }
        if ( result instanceof RandomizerException ){
            throw (RandomizerException)result;
        }
        return ((Boolean)result).booleanValue();
    }

    public String[][] getAllAllocations(final String rdmzrName) throws UnknownRandomizerException, RandomizerDAOException {
        
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                Randomizer r = (Randomizer)session.createQuery("from Randomizer r where r.name=?")
                                                  .setString(0, rdmzrName)
                                                  .uniqueResult();
                
                if ( null == r ){
                    return new UnknownRandomizerException("No randomizer exists with the name '"+rdmzrName+"'");
                }
                
                Map<String, String> result = r.getAllAllocations();
                String[][] array = new String[result.size()][2];
                int counter = 0;
                for ( Entry<String, String> e: result.entrySet() ){
                    array[counter][0] = e.getKey();
                    array[counter][1] = e.getValue();
                    counter++;
                }
                
                return array;
            }
        };
        
        Object result = getHibernateTemplate().execute(callback);
        if ( result instanceof RandomizerDAOException ){
            throw (UnknownRandomizerException)result;
        }
        return (String[][])result;
    }

    public String[][] getRandomizerStatistics(final String rdmzrName) throws UnknownRandomizerException, RandomizerDAOException, RandomizerException {

        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                Randomizer r = (Randomizer)session.createQuery("from Randomizer r where r.name=?")
                                                  .setString(0, rdmzrName)
                                                  .uniqueResult();
                
                if ( null == r ){
                    return new UnknownRandomizerException("No randomizer exists with the name '"+rdmzrName+"'");
                }
                
                Map<String, Long> result = null;
                try{
                    result = r.getRandomizerStatistics();
                }
                catch (RandomizerException ex){
                    return ex;
                }
                
                String[][] array = new String[result.size()][2];
                int counter = 0;
                for ( Entry<String, Long> e: result.entrySet() ){
                    array[counter][0] = e.getKey();
                    array[counter][1] = e.getValue().toString();
                    counter++;
                }
                
                return array;
            }
        };
        
        Object result = getHibernateTemplate().execute(callback);
        if ( result instanceof RandomizerDAOException ){
            throw (UnknownRandomizerException)result;
        }
        else if ( result instanceof RandomizerException ){
            throw (RandomizerException)result;
        }
        return (String[][])result;
    }

    public String[][] getRandomizerStatistics(final String rdmzrName, final Parameter[] parameters) throws UnknownRandomizerException, RandomizerDAOException, RandomizerException {
        HibernateCallback callback = new HibernateCallback(){
            public Object doInHibernate(Session session){
                Randomizer r = (Randomizer)session.createQuery("from Randomizer r where r.name=?")
                                                  .setString(0, rdmzrName)
                                                  .uniqueResult();
                
                if ( null == r ){
                    return new UnknownRandomizerException("No randomizer exists with the name '"+rdmzrName+"'");
                }
                
                Map<String, Long> result = null;
                try{
                    for ( Parameter p: parameters ){
                        r.setParameter(p.getKey(), p.getValue());
                    }
                    result = r.getRandomizerStatistics();
                }
                catch(RandomizerException ex){
                    return ex;
                }
                
                String[][] array = new String[result.size()][2];
                int counter = 0;
                for ( Entry<String, Long> e: result.entrySet() ){
                    array[counter][0] = e.getKey();
                    array[counter][1] = e.getValue().toString();
                    counter++;
                }
                
                return array;
            }
        };
        
        Object result = getHibernateTemplate().execute(callback);
        if ( result instanceof RandomizerDAOException ){
            throw (UnknownRandomizerException)result;
        }
        else if ( result instanceof RandomizerException ){
            throw (RandomizerException)result;
        }
        return (String[][])result;
    }

	public Calendar[] getSubjectRandomizationEvents(final String rdmzrName, final String subjectCode) 
	throws UnknownRandomizerException, RandomizerDAOException {

	HibernateCallback callback = new HibernateCallback(){
		public Object doInHibernate(Session session){

			List dates = session.createQuery("select a.date from Allocation a where a.subject=:subject")
			.setString("subject", subjectCode)
			.list();

			if ( null == dates || dates.size() == 0){
				return null;
			}

			return dates;
		}
	};

	Object obj = getHibernateTemplate().execute(callback);
	if (obj == null) {
		return null;
	}
	
	List results = (List) obj; 
	Calendar[] dates = new Calendar[results.size()];
	for (int i = 0; i < results.size(); i++) {
		dates[i] = Calendar.getInstance();
		dates[i].setTime((Date)results.get(i));
	}

	return dates;
}


}
