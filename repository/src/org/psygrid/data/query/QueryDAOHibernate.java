/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.psygrid.data.query;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.query.hibernate.EntryStatement;
import org.psygrid.data.query.hibernate.Query;
import org.psygrid.data.query.hibernate.Statement;
import org.psygrid.data.repository.dao.DAOException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author Rob Harper
 *
 */
public class QueryDAOHibernate extends HibernateDaoSupport implements QueryDAO {

	public Long saveQuery(org.psygrid.data.query.dto.Query query, String userName) {
		Query q = query.toHibernate();
		q.setOwner(userName);
		getHibernateTemplate().saveOrUpdate(q);
		getHibernateTemplate().deleteAll(q.getDeletedStatements());
		return q.getId();
	}

	public org.psygrid.data.query.dto.Query[] getMyQueries(final String project, final String userName){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				List queries = 
				session.createQuery("select q.id, q.name, q.description from Query q "+
									"where q.owner=:owner " +
									"and q.dataSet.projectCode=:project")
					   .setString("owner", userName)
					   .setString("project", project)
					   .list();
				
				org.psygrid.data.query.dto.Query[] result = new org.psygrid.data.query.dto.Query[queries.size()];
				for ( int i=0, c=queries.size(); i<c; i++ ){
					Object[] q = (Object[])queries.get(i);
					org.psygrid.data.query.dto.Query query = new org.psygrid.data.query.dto.Query();
					query.setId((Long)q[0]);
					query.setName((String)q[1]);
					query.setDescription((String)q[2]);
					result[i] = query;
				}
			
				return result;
			}
		};
		
		Object result = getHibernateTemplate().execute(callback);
		return (org.psygrid.data.query.dto.Query[])result;
	}
	
	public org.psygrid.data.query.dto.Query getQuery(final Long queryId) throws DAOException {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				Query q = (Query)
						session.createQuery("from Query q where q.id=:id")
					   .setLong("id", queryId)
					   .uniqueResult();
				
				if ( null == q ){
					return new DAOException("No query exists for id="+queryId);
				}
				
				return q.toDTO();
			}
		};
		
		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
		return (org.psygrid.data.query.dto.Query)result;
	}
	
	public String getProjectCodeForQuery(Long queryId) throws DAOException {
		List result = getHibernateTemplate().find("select q.dataSet.projectCode from Query q where q.id=?", queryId);
		if ( result.isEmpty() ){
			throw new DAOException("No query exists for id="+queryId);
		}
		return (String)result.get(0);
		
	}
	
	public String[] getGroupsForQuery(Long queryId) throws DAOException {
		List result = getHibernateTemplate().find("select g.name from Query q left join q.groups g where q.id=?", queryId);
		if ( result.isEmpty() ){
			throw new DAOException("No query exists for id="+queryId);
		}
		String[] groups = new String[result.size()];
		for ( int i=0, c=result.size(); i<c; i++ ){
			groups[i] = (String)result.get(i);
		}
		return groups;
	}

	public String[] executeQueryForIdentifiers(final Long queryId)
			throws DAOException {
		HibernateCallback callback = new HibernateCallback() {
			public Object doInHibernate(Session session) {
				try {
					Criteria crit = buildQuery(session, queryId);
					crit.setProjection(Projections.property("i.identifier"));
					List result = crit.list();
					String[] ids = new String[result.size()];
					for (int i = 0, c = result.size(); i < c; i++) {
						ids[i] = (String) result.get(i);
					}
					return ids;
				} catch (DAOException ex) {
					return ex;
				}
			}
		};
		Object result = getHibernateTemplate().execute(callback);
		if (result instanceof DAOException) {
			throw (DAOException) result;
		}
		return (String[]) result;
	}

	public String[] executeQueryForExternalIdentifiers(final Long queryId)
			throws DAOException {
		HibernateCallback callback = new HibernateCallback() {
			public Object doInHibernate(Session session) {
				try {
					Criteria crit = buildQuery(session, queryId);
					crit.setProjection(Projections
							.property("externalIdentifier"));
					crit.addOrder(Order.asc("externalIdentifier"));
					List result = crit.list();
					String[] ids = new String[result.size()];
					for (int i = 0, c = result.size(); i < c; i++) {
						ids[i] = (String) result.get(i);
					}
					return ids;
				} catch (DAOException ex) {
					return ex;
				}
			}
		};
		Object result = getHibernateTemplate().execute(callback);
		if (result instanceof DAOException) {
			throw (DAOException) result;
		}
		return (String[]) result;
	}

	public Long executeQueryForCount(final Long queryId) throws DAOException {
		HibernateCallback callback = new HibernateCallback() {
			public Object doInHibernate(Session session) {
				try {
					Criteria crit = buildQuery(session, queryId);
					crit.setProjection(Projections.rowCount());
					List result = crit.list();
					return (Long) result.get(0);
				} catch (DAOException ex) {
					return ex;
				}
			}
		};
		Object result = getHibernateTemplate().execute(callback);
		if (result instanceof DAOException) {
			throw (DAOException) result;
		}
		return (Long) result;
	}	
	
	
	/**
	 * Build the basis of a criteria query to execute a saved query.
	 * <p>
	 * Run as returned, the criteria query will provide a list of
	 * matching records. Projections can be added to the query to (for
	 * instance) just return the number of matching records.
	 * 
	 * @param session Hibernate session.
	 * @param queryId ID of query to execute
	 * @return List of matching records
	 */
	private Criteria buildQuery(Session session, Long queryId) throws DAOException {

		//retrieve the query to execute
		org.psygrid.data.query.hibernate.Query q 
			= (org.psygrid.data.query.hibernate.Query)session.createQuery("from Query q where q.id=:id")
			   					.setLong("id", queryId)
			   					.uniqueResult();
		
		if ( null == q ){
			throw new DAOException("No query exists for id="+queryId);
		}
		
		String projectCode = q.getDataSet().getProjectCode();
		List<String> groups = new ArrayList<String>();
		for ( Group g: q.getGroups() ){
			groups.add(g.getName());
		}
		
		//Form the basis of the main record query, restricting the records
		//returned by project and groups, and removing records in states that
		//ahould be ignored
		Criteria c = session.createCriteria(Record.class);
		c.createAlias("identifier", "i");
		c.createAlias("status", "s");
		c.add( Restrictions.eq("i.projectPrefix", projectCode));
		c.add( Restrictions.in("i.groupPrefix", groups));
		c.add( Restrictions.eq("deleted", Boolean.FALSE));
		c.add( Restrictions.ne("s.enumGenericState", GenericState.INVALID.toString()));
		c.add( Restrictions.ne("s.enumGenericState", GenericState.INACTIVE.toString()));
		
		//Initialise the junction that criteria queries for each query statements 
		//will be added to 
		Junction j = null;
		if ( q.getOperator().equals("AND") ){
			j = Restrictions.conjunction();
		}
		else{
			j = Restrictions.disjunction();
		}
		c.add( j );

		for ( Statement s: q.getStatements() ){
			
			if ( s instanceof EntryStatement ){
				EntryStatement entryStatement = (EntryStatement)s;
				//For each statement we need a sub-query that returns a list of matching
				//record ids. This is done by working "backwards". First a query to find
				//matching Value objects is defined. This is then used to find the responses
				//that match these values. Finally, we have a query to find the Records 
				//matching these responses. This query which returns a list of records
				//ids is added to the junction created above so we end up with
				//Records that are in <List of Records 1> AND/OR in <List of Records 2> etc.
				
				//1a create value criteria depending upon expected Value subclass.
				DetachedCriteria valueCriteria = createValueCriteria(entryStatement);
				//1b add restriction depending upon the statements operator
				addRestrictionBasedOnOperator(valueCriteria, entryStatement);
				
				//1c set projection to just return value ids
				valueCriteria.setProjection(Projections.id());
				
				//2a create response criteria, restricting to the entry specified
				//in the statement and whose value is in the sub-query of matching 
				//values from 1
				DetachedCriteria respC = DetachedCriteria.forClass(BasicResponse.class);
				respC.add( Restrictions.eq("entry", entryStatement.getEntry() ) );
				respC.add( Subqueries.propertyIn("theValue.id", valueCriteria));
				respC.setProjection(Projections.id());
	
				//3a create record criteria of the records that have a response
				//in the sub-query of matching responses from 2
				DetachedCriteria rC = DetachedCriteria.forClass(Record.class);
				rC.createCriteria("docInstances")
					.createCriteria("responses")
						.add( Subqueries.propertyIn("id", respC));
				rC.setProjection(Projections.id());
				
				//Add record query 3 to the statements junction
				switch(entryStatement.getOperator()){
				case IS_NULL:
					//* for an "is null" statement the list of responses contains
					//all those that ARE NOT null, hence to get the list of records
					//for which the response IS null we need to do a "not in" subquery
					j.add( Subqueries.propertyNotIn("id", rC) );
					break;
				default:
					j.add( Subqueries.propertyIn("id", rC) );
				}
			}
			//TODO document and record level queries
		}
		
		return c;
	}
	
	/** 
	 * Create the appropriate value criteria based on the statement type.
	 * @param entryStatement 	The statement
	 * @return					The value criteria
	 */
	private DetachedCriteria createValueCriteria(EntryStatement entryStatement) {
		return DetachedCriteria.forClass(entryStatement.getAssociatedValueType());
	}
	
	private void addRestrictionBasedOnOperator(DetachedCriteria valueCriteria, EntryStatement entryStatement) {
		switch (entryStatement.getOperator()){
		case EQUALS:
			valueCriteria.add(Restrictions.eq("value", entryStatement.getTheValue()));
			break;
		case NOT_EQUALS:
			valueCriteria.add(Restrictions.ne("value", entryStatement.getTheValue()));
			break;
		case GREATER_THAN:
		case IS_AFTER:
			valueCriteria.add(Restrictions.gt("value", entryStatement.getTheValue()));
			break;
		case GREATER_THAN_EQUALS:
			valueCriteria.add(Restrictions.ge("value", entryStatement.getTheValue()));
			break;
		case LESS_THAN:
		case IS_BEFORE:
			valueCriteria.add(Restrictions.lt("value", entryStatement.getTheValue()));
			break;
		case LESS_THAN_EQUALS:
			valueCriteria.add(Restrictions.le("value", entryStatement.getTheValue()));
			break;
		case IS_MISSING:
			valueCriteria.add(Restrictions.isNotNull("standardCode"));
			break;
		case IS_NOT_MISSING:
			valueCriteria.add(Restrictions.isNull("standardCode"));
			break;
		case IS_NULL:
		case IS_NOT_NULL:
			//Note that is null and is not null use the same value restrictions
			//to return the set of values that are not null - see * later
			Junction nnc = Restrictions.disjunction();
			nnc.add(Restrictions.isNotNull("standardCode"));
			nnc.add(Restrictions.isNotNull("value"));
			valueCriteria.add(nnc);
			break;
		case STARTS_WITH:
			valueCriteria.add(Restrictions.like("value", entryStatement.getTheValue().toString(), MatchMode.START));
			break;
		default:
			throw new RuntimeException("Operator not yet implemented");
		}
	}
}
