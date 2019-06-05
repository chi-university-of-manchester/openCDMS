package org.psygrid.drn.address.patches.v1_7_0;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Site;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch45 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
Factory factory = new HibernateFactory();
		Group grp57 = (Group)factory.createGroup("651013");
		grp57.setLongName("Stockport NHS Trust");
		
		/*
		 Name: Stepping Hill Hospital
		 UKCRN code: N0000221
		 Postal Code: SK2 7JE
		 Consultants: Dr Chris Cooper
		 */
		
		Site steppingHillHosp = new Site("Stepping Hill Hospital", "N0000221", "SK2 7JE", grp57);
		steppingHillHosp.addConsultant("Dr Chris Cooper");
		grp57.addSite(steppingHillHosp);
		ds.addGroup(grp57);
		
		Group grp58 = (Group)factory.createGroup("651014");
		grp58.setLongName("Lancashire Teaching Hospitals Trust");
		
		/*
		 Name: Royal Preston Hospital
		 UKCRN code: N0000160
		 Postal Code: PR2 9HT
		 Consultants: Dr Melanie Foster
		 */
		
		Site royalPrestonHosp = new Site("Royal Preston Hospital", "N0000160", "PR2 9HT", grp58);
		royalPrestonHosp.addConsultant("Dr Melanie Foster");
		grp58.addSite(royalPrestonHosp);
		ds.addGroup(grp58);
	}

	@Override
	public String getName() {
		return "Add the following centres: 'Stockport NHS Trust' and 'Lancashire Teaching Hospitals Trust'";
	}

}
