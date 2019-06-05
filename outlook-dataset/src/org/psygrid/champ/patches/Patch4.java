package org.psygrid.champ.patches;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.EslCustomField;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch4 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		EslCustomField cf = ds.getEslCustomField(0);
		cf.addValue("Neurology");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Add new custom field value to the CHAMP dataset";
	}

}
