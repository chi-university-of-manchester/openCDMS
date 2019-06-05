package org.psygrid.drn.address.patches.v1_6_8;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;
import org.psygrid.data.model.hibernate.HibernateFactory;

public class Patch38 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Factory factory = new HibernateFactory();
		
		//This will be group 51 when accessed by index.
		Group grp52 = (Group)factory.createGroup("640007");
		grp52.setLongName("Durham and Darlington NHS Foundation Trust");
		Site site52a = new Site("Darlington Memorial Hospital", "N0000068", "?", grp52);
		//Does this site have a consultant?
		grp52.addSite(site52a);
		
		//This will be group 52 when accessed by index.
		Group grp53 = (Group)factory.createGroup("654006");
		grp53.setLongName("The Queen Elizabeth Hospital King's Lynn");
		Site site53a = new Site("The Queen Elizabeth Hospital King's Lynn NHS Foundation Trust", "N0000163", "?", grp53);
		//Does this site have a consultant?
		grp53.addSite(site53a);
		
		ds.addGroup(grp52);
		ds.addGroup(grp53);
		
		Group durham = ds.getGroup(51);
		Group qeh = ds.getGroup(52);
		if(!durham.getName().equals("640007") ||
			!qeh.getName().equals("654006")	){
			throw new Exception("The index values of one of the new groups does not correspond with the values used in the modification to Reports.java.");
		}
	}

	@Override
	public String getName() {
		return new String("Add \"Durham and Darlington NHS Foundation Trust\" and \"The Queen Elizabeth Hospital King's Lynn\" to the ADDRESS database.");
	}

}
