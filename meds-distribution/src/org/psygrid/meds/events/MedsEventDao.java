package org.psygrid.meds.events;

import java.util.Date;
import java.util.List;

import org.psygrid.meds.medications.MedicationPackage;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class MedsEventDao extends HibernateDaoSupport {
	
	public void registerViewEvent(MedicationPackage p, String viewer){
		PackageViewEvent e = new PackageViewEvent(viewer, new Date(), p);
		getHibernateTemplate().save(e);
	}
	
	public void registerViewEvents(List<MedicationPackage> packageList, String viewer){
		
		for(MedicationPackage p : packageList){
			PackageViewEvent e = new PackageViewEvent(viewer, new Date(), p);
			getHibernateTemplate().save(e);
		}
		
	}
	
}
