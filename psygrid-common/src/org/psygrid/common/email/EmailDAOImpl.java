package org.psygrid.common.email;

import java.util.List;

import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class EmailDAOImpl extends HibernateDaoSupport implements EmailDAO {

	public List<QueuedEmail> getQueuedEmails() {
		HibernateCallback callback = new HibernateCallback(){

			public Object doInHibernate(Session session){
				List emails = session.createCriteria(QueuedEmail.class)
				.list();
				return emails;
			}
		};

		List<org.psygrid.common.email.QueuedEmail> emails = (List<org.psygrid.common.email.QueuedEmail>)getHibernateTemplate().execute(callback);

		return emails;
	}


	public void removeQueuedEmail(QueuedEmail email) {
		if (email != null) {
			getHibernateTemplate().delete(email);
		}
	}

	public Long saveEmail(QueuedEmail email) {
		getHibernateTemplate().saveOrUpdate(email);
		return email.getId();
	}

}
