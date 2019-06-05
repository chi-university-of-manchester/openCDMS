package org.psygrid.meds.project;


import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ProjectDao extends HibernateDaoSupport {
	
	public void saveProject(Project p) throws HibernateException{
		getHibernateTemplate().saveOrUpdate(p);
	}
	
	
	/*
	public void addPharmacyToCentre(final String projectCode, String centreCode, PharmacyInfo pharmacyInfo) throws InvalidParameter{
		Project p = getProject(projectCode);
		
		if(p == null){
			throw new InvalidParameter("The specified project code returned no results.");
		}
		
		Centre c = getCentre(projectCode, centreCode);
		
		if(c == null){
			throw new InvalidParameter("The specified centre code returned no results.");
		}
		
		Pharmacy ph = ProjectObjectTranslator.translatePharmacyInfoToPharmacy(pharmacyInfo);
		
		c.addPharmacy(ph);
		ph.setCentre(c);
		
		this.getHibernateTemplate().saveOrUpdate(c);
	}
	*/
	
	public void addPharmacyToProject(final String projectCode, PharmacyInfo info) throws HibernateException {
		Project p = getProject(projectCode);
		Pharmacy pharm = ProjectObjectTranslator.translatePharmacyInfoToPharmacy(info);
		p.addPharmacy(pharm);;
		getHibernateTemplate().saveOrUpdate(p);
	}
	
	public Project getProject(final String projectCode){
		
		Project p = (Project)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				
				Query q = session.createQuery("select p from Project p where p.projectCode = :projCode")
				.setText("projCode", projectCode);
				
				return q.uniqueResult();
			}
		});
		
		return p;
	}
	
	/*
	protected Centre getCentre(final String projectCode, final String centreCode){
		
		Centre c = (Centre)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session){
				
				Query q = session.createQuery("select c from Project p join p.centres c where p.projectCode = :projCode and c.centreCode = :centreCode")
				.setText("projCode", projectCode).
				setText("centreCode", centreCode);
				
				return q.uniqueResult();
			}
		});
		
		return c;
	}
	*/
}
