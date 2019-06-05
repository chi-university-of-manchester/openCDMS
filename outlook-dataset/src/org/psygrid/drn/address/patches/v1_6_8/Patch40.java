package org.psygrid.drn.address.patches.v1_6_8;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch40 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();
		
		//This will be group 53 when accessed by index.
		Group grp54 = (Group)factory.createGroup("649004");
		grp54.setLongName("University Hospitals Coventry");
		Site site54a = new Site("University Hospitals Coventry and Warwickshire NHS Trust", "N0000511", "?", grp54);
		site54a.addConsultant("Dr. Sailesh Sankar");
		grp54.addSite(site54a);
		
		ds.addGroup(grp54);
		
		Group coventry = ds.getGroup(53);
		if(!coventry.getName().equals("649004") ){
			throw new Exception("The index values of one of the new groups does not correspond with the values used in the modification to Reports.java.");
		}
		
		//This will be group 54 when accessed by index.
		Group grp55 = (Group)factory.createGroup("653007");
		grp55.setLongName("Barking, Havering and Redbridge Hospitals NHS Trust");
		Site site55a = new Site("Barking Hospital", "N0008765", "?", grp55);
		site55a.addConsultant("Dr. Kash Nikookam");
		grp55.addSite(site55a);
		
		ds.addGroup(grp55);
		
		Group barking = ds.getGroup(54);
		if(!barking.getName().equals("653007")){
			throw new Exception("The index values of one of the new groups does not correspond with the values used in the modification to Reports.java.");
		}
		
	}

	@Override
	public String getName() {
		return new String("Add \"University Hospitals Coventry\" to the ADDRESS database.");
	}

}
