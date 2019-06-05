
package org.psygrid.data.importing.model;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.psygrid.data.export.hibernate.ExportRequest;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

class ImportDAOHibernate extends HibernateDaoSupport implements ImportDAO {

	public ImportRequest getNextImportRequest(){
		Object result = getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				Long count = (Long)session.createQuery(
				"select count(*) from ImportRequest r where r.status=?")
				.setString(0, ImportRequest.STATUS_PROCESSING)
				.uniqueResult();
				if ( count.intValue() > 0 ){
					//there is already an import in progress
					return null;
				}
				return session.createQuery("from ImportRequest r where r.status = :status")
				 		.setString("status", ImportRequest.STATUS_PENDING)
				 		.setMaxResults(1)
				 		.uniqueResult();
			}
		}
		);
		return (ImportRequest)result;		
	}
	
	public ImportRequest getImportRequest(long id){
		return (ImportRequest) getHibernateTemplate().load(ImportRequest.class, id);
	}

	public void saveImportRequest(ImportRequest request){
		getHibernateTemplate().saveOrUpdate(request);		
	}
	
	public List<ImportRequest> getImportRequests(final String projectCode){
		Object result = getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				 return session.createQuery("from ImportRequest r where r.projectCode = :projectCode order by r.id desc")
				 		.setString("projectCode", projectCode)
				 		.list();
			}
		}
		);
		return (List<ImportRequest>)result;		
	}
	
}
