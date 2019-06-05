package org.psygrid.meds.export;

import java.util.List;

import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class MedsExportDao extends HibernateDaoSupport {
	
	public MedsExportRequest getNextPendingRequest(final boolean immediate){
		
		MedsExportRequest r = (MedsExportRequest) this.getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				
				Long count = (Long)session.createQuery(
						"select count(*) from MedsExportRequest er where er.status=?")
						.setString(0, MedsExportRequest.STATUS_PROCESSING)
						.uniqueResult();
						if ( count.intValue() > 0 ){
							//there is already an export in progress
							return null;
						}
						List result = session.createQuery(
						"from MedsExportRequest er where er.status=? and er.immediate=? order by er.requestDate asc")
						.setString(0, MedsExportRequest.STATUS_PENDING)
						.setBoolean(1, immediate)
						.list();
						MedsExportRequest req = null;
						for ( int i=0; i<result.size(); i++ ){
							//get the first export request in the list. A copy is taken, just
							//to make sure that all fields have been initialized from
							//the database
							req = (MedsExportRequest)result.get(i);
							break;
						}
						if ( null == req ){
							return null;
						}
				return req;

			}
		});
		
		return r;
	}
	
	public void saveExportRequest(final MedsExportRequest r){
		getSession().save(r);
	}
	
	public void updateRequestStatus(final Long requestId, final String newStatus)  {
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				MedsExportRequest req = (MedsExportRequest)session.createQuery("from MedsExportRequest er where er.id=?")
				.setLong(0, requestId)
				.uniqueResult();
				if ( null == req ){
					return null;
				}
				req.setStatus(newStatus);
				session.saveOrUpdate(req);
				return null;
			}
		};
		getHibernateTemplate().execute(callback);
		
	}

}
