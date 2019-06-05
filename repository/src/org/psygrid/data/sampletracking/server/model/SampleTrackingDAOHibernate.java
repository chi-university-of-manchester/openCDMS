package org.psygrid.data.sampletracking.server.model;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

class SampleTrackingDAOHibernate extends HibernateDaoSupport implements SampleTrackingDAO {
	
	public Config getConfig(final String projectCode) {
		Object result = getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				 return session.createQuery("from Config c where c.projectCode = :projectCode")
				 		.setString("projectCode", projectCode)
				 		.uniqueResult();
			}
		}
		);
		return (Config)result;
	}

	public void saveConfig(Config conf) {
		getHibernateTemplate().saveOrUpdate(conf);
	}

	public Participant getParticipant(final String recordID) {
		Object result = getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				 return session.createQuery("from Participant p where p.recordID = :recordID")
				 		.setString("recordID",recordID)
				 		.uniqueResult();
			}
		}
		);
		return (Participant)result;
	}

	public void saveParticipant(Participant participant) {
		getHibernateTemplate().saveOrUpdate(participant);
	}
	
	public Sample getSample(final long sampleID) {
		Object result = getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				 return session.createQuery("from Sample s where s.id = :sampleID")
				 		.setLong("sampleID", sampleID)
				 		.uniqueResult();
			}
		}
		);
		return (Sample)result;	
	}
	
	public void saveSample(Sample sample) {
		getHibernateTemplate().saveOrUpdate(sample);
	}

	public List<Action> getActions(final String projectCode, final String status) {
		Object result = getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				 return session.createQuery("from Action sa where sa.projectCode = :projectCode and sa.status = :status")
				 		.setString("projectCode", projectCode)
				 		.setString("status", status)
				 		.list();
			}
		}
		);
		return (List<Action>)result;
	}

	public void saveAction(Action action) {
		getHibernateTemplate().saveOrUpdate(action);		
	}

}
