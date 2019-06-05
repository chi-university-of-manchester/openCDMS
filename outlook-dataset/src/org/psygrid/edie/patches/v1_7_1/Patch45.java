package org.psygrid.edie.patches.v1_7_1;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch45 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		int numDocs = ds.numDocuments();
		
		Document mindInTheEye = null;
		
		for(int i = 0; i < numDocs; i++){
			
			Document doc = ds.getDocument(i);
			
			if(doc.getName().equals("Reading the Mind in the Eye Test")){
				mindInTheEye = doc;
				break;
			}
		}
		
		//For this document, set all option entries so that missing codes are enabled.
		//Change the derived entry so that its variables do not have default values.
		//Remember the provenance stuff.
		//And do we need to set any default patching action? Do not think so because:
		//	1) Previously all documents must have been completed entirely. There was no missing code option.
		//	2) Because they had to be completed entirely, the default values in the calculated entry would never have been used.
		
		int numEntries = mindInTheEye.numEntries();
		
		HibernateFactory factory = new HibernateFactory();
		
		for(int i = 0; i < numEntries; i++){
			
			Entry e = mindInTheEye.getEntry(i);
			
			if(e instanceof NarrativeEntry){
				continue;
			}
			
			if(e instanceof OptionEntry){
				((OptionEntry) e).setDisableStandardCodes(false);
				e.setElementPatchingAction(Element.ACCEPT_ALL_EXISTING_ELEMENTS.toString());
				
				AuditableChange auditableChange = factory.createAuditableChange(AuditableChange.ACTION_EDIT,
                        "Enable missing codes.",
                        "William Vance");
				
				
				AuditLog auditLog = e.getAuditLog();
				if (auditLog == null) {
					auditLog = factory.createAuditLog();
				}
				auditLog.addAuditableChange(auditableChange);
				//update the audit log
				e.setAuditLog(auditLog);
				
			}else if(e instanceof DerivedEntry){
				DerivedEntry dE = (DerivedEntry)e;
				dE.setVariableDefaults(null);
				dE.setElementPatchingAction(Element.ACCEPT_ALL_EXISTING_ELEMENTS.toString());
				
				AuditableChange auditableChange = factory.createAuditableChange(AuditableChange.ACTION_EDIT,
                        "Remove default values from calculated entry.",
                        "William Vance");
				
				
				AuditLog auditLog = e.getAuditLog();
				if (auditLog == null) {
					auditLog = factory.createAuditLog();
				}
				auditLog.addAuditableChange(auditableChange);
				//update the audit log
				e.setAuditLog(auditLog);
				
			}
			
			
			
		}


	}

	@Override
	public String getName() {
		String name = "Enables missing codes for all option entries and removes default values for calculated entry varibales.";
		return name;
	}

}
