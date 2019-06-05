package org.psygrid.drn.address.patches.v1_6_9;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch42 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		Group group51 = ds.getGroup(50);
		if(!group51.getName().equals("651011")){
			throw new RuntimeException("The is not the Pennine Acute Hospital NHS Trust group.");
		}
		
		Site royalOldham = new Site("Royal Oldham Hospital", "N0000488", "OL1 2JH");
		royalOldham.addConsultant("Dr Egware Odeka");
		group51.addSite(royalOldham);

		Site rochdaleInfirmary = new Site("Rochdale Infirmary", "N0000527", "OL12 0NB");
		rochdaleInfirmary.addConsultant("Dr Indra Ariyawansa");
		group51.addSite(rochdaleInfirmary);
		
	}

	@Override
	public String getName() {
		return new String("Add Sites to Pennine Acute Hospitals NHS Trust");
	}

}
