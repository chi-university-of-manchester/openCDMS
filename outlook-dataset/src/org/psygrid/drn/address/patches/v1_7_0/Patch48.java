package org.psygrid.drn.address.patches.v1_7_0;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch48 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		int numGroups = ds.numGroups();
		Group cenManAndManChildrenHosp = null;
		for(int i = 0; i < numGroups; i++){
			if(ds.getGroup(i).getName().equals("651002")){
				cenManAndManChildrenHosp = ds.getGroup(i);
				break;
			}
		}
		
		if(cenManAndManChildrenHosp == null){
			throw new Exception("Group 651002 not found - aborting patch");
		}

		Site childHospSite = new Site("Royal Manchester Children's Hospital", "N0000246", "M13 9WL", (org.psygrid.data.model.hibernate.Group)cenManAndManChildrenHosp);
		childHospSite.addConsultant("Dr Ian Doughty");
		cenManAndManChildrenHosp.addSite(childHospSite);
		
		
	}

	@Override
	public String getName() { 
		// TODO Auto-generated method stub
		return "Adds Manchester Children's Hospital to 651002 Group";
	}

}
