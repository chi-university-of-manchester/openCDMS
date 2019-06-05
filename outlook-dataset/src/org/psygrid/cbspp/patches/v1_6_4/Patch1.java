package org.psygrid.cbspp.patches.v1_6_4;

import org.psygrid.data.model.hibernate.ConsentFormGroup;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.PrimaryConsentForm;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch1 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		//Note: Repository code must be altered before running this patch. Otherwise a model exception will occur.
		//It is necessary to comment out one line of code in org.psygrid.data.model.hibernate.ConsentFormGroup.removeConsentForm().
		//Comment out the line that adds the removed ConsentForm object to the dataset's list of deleted objects.
		//We don't actually want it deleted, since we are just moving moving it to another ConsentFormGroup.
		
		int numConsentFormGroups = ds.numAllConsentFormGroups();
		
		if(numConsentFormGroups != 1){
			throw new RuntimeException("The dataset has more than one consent form group. It is therefore not the expected configuration. Aborting patch.");
		}
		
		ConsentFormGroup cfg = ds.getAllConsentFormGroup(0);
		
		int numPrimaryConsentForms = cfg.numConsentForms();
		
		if(numPrimaryConsentForms != 3){
			throw new RuntimeException("The dataset's consent form group does not have 3 primary consent forms. It is therefore not the expected configuration. Aborting patch.");
		}
		
		PrimaryConsentForm p1 = cfg.getConsentForm(1);
		PrimaryConsentForm p2 = cfg.getConsentForm(2);
		
		//Okay, check and make sure that the consent form's text is correct.
		
		Factory factory = new HibernateFactory();
		ConsentFormGroup g1 = factory.createConsentFormGroup();
		g1.setDescription("Additional Consent Form 1");

		ConsentFormGroup g2 = factory.createConsentFormGroup();
		g2.setDescription("Additional Consent Form 2");
		
		//Now, remove p1 and p2 from the original consent form group and add it to the second consent form group.
		
		cfg.removeConsentForm(2);
		cfg.removeConsentForm(1);
		

		g1.addConsentForm(p1);
		g2.addConsentForm(p2);
		
		ds.addAllConsentFormGroup(g1);
		ds.addAllConsentFormGroup(g2);
		
	}

	@Override
	public String getName() {
		return "Separate the final two primary consent items contained in the original consent form group into" +
			" two newly added ConsentFormGroups.";
	}

}
