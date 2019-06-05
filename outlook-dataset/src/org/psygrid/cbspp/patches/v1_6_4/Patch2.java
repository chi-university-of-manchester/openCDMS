package org.psygrid.cbspp.patches.v1_6_4;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DocumentGroup;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch2 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		// This is a workaround for bug #1352. There is nothing wrong with the
		// CBSPp study - but must be altered becuase of problems in Collect,
		// and because of the urgent need to correct the situation (currently,
		// users cannot enter any more data because all study stages after randomization
		// are locked.
		
		//To fix the problem: the following changes must be made:
		// #1 - add 'Randomisation completed' to the 'allowed study stages' for
		// randomisation.
		// #2 - add '4 months completed to the 'allowed study stages' for
		// '4 months'

		int numDocumentGroups = ds.numDocumentGroups();
		
		DocumentGroup randomisation = null, fourMonths = null;
		
		for(int i = 0; i < numDocumentGroups; i++){
			DocumentGroup dg = ds.getDocumentGroup(i);
			if(dg.getName().equals("4 Month Follow up")){
				fourMonths = dg;
			}
			if(dg.getName().equals("Randomisation")){
				randomisation = dg;
			}
		}
		
		if(fourMonths == null || randomisation == null){
			throw new Exception("Variables did not initialise properly.");
		}

		Status randCompleted = null, fourMosCompleted = null;
		
		int numStatuses = ds.numStatus();
		
		for(int i = 0; i < numStatuses; i++){
			Status s = ds.getStatus(i);
			if(s.getShortName().equals("Randomisation Completed")){
				randCompleted = s;
			}
			
			if(s.getShortName().equals("4 Months Completed")){
				fourMosCompleted = s;
			}
		}
		
		if(randCompleted == null || fourMosCompleted == null){
			throw new Exception("Variables did not initialise properly");
		}
		
		randomisation.addAllowedRecordStatus(randCompleted);
		randomisation.addAllowedRecordStatus(fourMosCompleted);
		fourMonths.addAllowedRecordStatus(fourMosCompleted);
		
	}
	

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Modify allowed record statuses as workaround for #1352";
	}

}
