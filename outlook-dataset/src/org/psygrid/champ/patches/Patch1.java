package org.psygrid.champ.patches;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch1 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		int numberOfDocuments = ds.numDocuments();
		Document adSUSDoc = null;
		for(int docCounter = 0; docCounter < numberOfDocuments; docCounter++){
			Document doc = ds.getDocument(docCounter);
			if (doc.getName().contains("AD-SUS")){
				adSUSDoc = doc; 
				break;
			}
		}
		
		//Need to get the entry.
		int numEntries = adSUSDoc.numEntries();
		Entry diagnosticDetailsEntry = null;
		for(int entryCounter = 0; entryCounter < numEntries; entryCounter++){
			Entry entry = adSUSDoc.getEntry(entryCounter);
			if(entry.getName().equals("AD-SUS Diagnostic test details")){
				diagnosticDetailsEntry = entry;
				break;
			}
		}
		
		//This entry is a table. Need to grab the 'AD-SUS -hospital names' child text entry.
		CompositeEntry compEntry = (CompositeEntry)diagnosticDetailsEntry;
		BasicEntry hospitalNamesEntry = null;
		
		int numberOfChildEntries = compEntry.numEntries();
		for(int childEntryCounter = 0; childEntryCounter < numberOfChildEntries; childEntryCounter++){
			BasicEntry childEntry = compEntry.getEntry(childEntryCounter);
			if(childEntry.getName().equals("AD-SUS -hospital names")){
				hospitalNamesEntry = childEntry;
				break;
			}
		}
		
		OptionEntry hospitalNamesOptionEntry = (OptionEntry) hospitalNamesEntry;
		
		Option otherHospitalOption = null;
		
		int numOptions = hospitalNamesOptionEntry.numOptions();
		
		for(int optionCounter = 0; optionCounter < numOptions; optionCounter++){
			Option option = hospitalNamesOptionEntry.getOption(optionCounter);
			if(option.getName().equals("Other - please specify")){
				
				otherHospitalOption = option;
				break;
				
			}
		}
		
		otherHospitalOption.setCode(new Integer(13));
		otherHospitalOption.setTextEntryAllowed(true);
		
	}

	@Override
	public String getName() {
		return "Fixes C04 of AD-SUS so that 'Other' hospital can be specified";
	}

}
