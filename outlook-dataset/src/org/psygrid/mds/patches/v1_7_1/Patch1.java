package org.psygrid.mds.patches.v1_7_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch1 extends AbstractPatch {

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		// TODO Auto-generated method stub
		
		int debug = 1;
		//VRS is at index 10
		
		
		HibernateFactory factory = new HibernateFactory();
		
		NumericValidationRule  vr = factory.createNumericValidationRule();
		vr.setLowerLimit(Double.valueOf("0"));
		vr.setUpperLimit(Double.valueOf("3.5"));
		vr.setMessage("Value must be in between 0 and 3.5 inclusive and must be an increment of 0.5");
		
		Document vrs = null;
		boolean docFound = false;
		for(int i = 0; i < ds.numDocuments(); i++){
			if(ds.getDocument(i).getName().equals("VRS")){
				docFound = true;
				vrs = ds.getDocument(i);
				break;
			}
		}
		
		if(!docFound){
			throw new Exception("VRS document not found.");
		}
		
		//get the calculated entry. Remove all variables.
		BasicEntry proratedStaticScoreEntry = null;
		DerivedEntry dE = null;
		boolean dEFound = false;
		boolean proratedEntryFound = false;
		for(int i = 0; i < vrs.numEntries(); i++){
			
			if(vrs.getEntry(i).getName().equals("Prorated static score")){
				proratedStaticScoreEntry = (BasicEntry)vrs.getEntry(i);
				proratedEntryFound = true;
			}
			
			if(vrs.getEntry(i) instanceof DerivedEntry && vrs.getEntry(i).getName().equals("Total  Dynamic Score")){
				dE = (DerivedEntry)vrs.getEntry(i);
				dEFound = true;
				break;
			}
		}
		if(!dEFound || !proratedEntryFound){
			throw new Exception("Derived entry and/or prorated score not found!");
		}
		
		proratedStaticScoreEntry.removeValidationRule(0);
		proratedStaticScoreEntry.setChanged(true);
		
		proratedStaticScoreEntry.setElementPatchingAction(Element.ACCEPT_ALL_EXISTING_ELEMENTS.toString());
		
		AuditableChange auditableChange = factory.createAuditableChange(AuditableChange.ACTION_EDIT,
                "Removed incorrect validation rule.",
                "William Vance");
		
		
		AuditLog auditLog = proratedStaticScoreEntry.getAuditLog();
		if (auditLog == null) {
			auditLog = factory.createAuditLog();
		}
		auditLog.addAuditableChange(auditableChange);
		//update the audit log
		proratedStaticScoreEntry.setAuditLog(auditLog);
		
		
		DerivedEntry rawDE = (DerivedEntry)dE;
		
		OptionEntry opEntry = null;
		Map<String, BasicEntry> variableMap = new HashMap<String, BasicEntry>();
		
		for(int i = 20; i >= 1; i--){
			
			boolean entryFound = false;
			String label = i + "A";
			String name = null;
			String description = null;
			String displayText = null;
			Section section = null;
			Integer index = null;
			
			Entry e = null;
			for(int j = 0; j < vrs.numEntries(); j++){
				e = vrs.getEntry(j);
				String entryLabel = e.getLabel();
				
				if(entryLabel != null && entryLabel.equals(label)){
					entryFound = true;
					index = j;
					name = e.getName() + " ";
					section = e.getSection();
					description = e.getDescription();
					displayText = e.getDisplayText();
					e.setLocked(true);
					
					auditableChange = factory.createAuditableChange(AuditableChange.ACTION_EDIT,
							"Removed entry (to be replaced by a NumericEntry).",
							"William Vance");
					
					
					auditLog = e.getAuditLog();
					if (auditLog == null) {
						auditLog = factory.createAuditLog();
					}
					auditLog.addAuditableChange(auditableChange);
					//update the audit log
					e.setAuditLog(auditLog);
					
					break;
				}
			}
			
			if(!entryFound){
				throw new Exception("Didn't find entry with label " + label + ". Aborting patch.");
			}
			
			NumericEntry numE =factory.createNumericEntry(name, displayText, EntryStatus.MANDATORY);
			numE.setDescription(description);
			numE.addValidationRule(vr);
			numE.setSection(section);
			numE.setLabel(label);
			
			//The label is later going to be used as the name of the variable, which can't start with a number.
			//Remove the last character. And append a 'V' to the front.
			
			String newLabel = label.substring(0, label.length()-1);
			newLabel = "V" + newLabel;
			
			variableMap.put(newLabel, (BasicEntry) numE);
			vrs.insertEntry(numE, index + 1);
	
		}
		
		rawDE.setChanged(true);
		
		auditableChange = factory.createAuditableChange(AuditableChange.ACTION_EDIT,
				"Reconfigured derived entry to use the new NumericEntry items as variables for D1 - D20.",
				"William Vance");
		
		
		auditLog = rawDE.getAuditLog();
		if (auditLog == null) {
			auditLog = factory.createAuditLog();
		}
		auditLog.addAuditableChange(auditableChange);
		//update the audit log
		rawDE.setAuditLog(auditLog);
		
		Set<String> varNames = rawDE.getVariableNames();
		List<String> varNamesList = new ArrayList<String>();
		
		for(String varName : varNames){
			varNamesList.add(varName);
		}
		
		
		for(String varName : varNamesList){
			rawDE.removeVariable(varName);
			rawDE.removeVariableDefault(varName);
		}
		
		StringBuffer formula = new StringBuffer();
		
		Iterator<java.util.Map.Entry<String, BasicEntry>> it = variableMap.entrySet().iterator();
		
		java.util.Map.Entry<String, BasicEntry> entry = it.next();
		formula.append(entry.getKey());
		
		while(it.hasNext()){
			formula.append(" + ");
			entry = it.next();
			formula.append(entry.getKey());
			
		}
		
		for(java.util.Map.Entry<String, BasicEntry> derEntVar: variableMap.entrySet()){
			
			rawDE.addVariable(derEntVar.getKey(), derEntVar.getValue());
			rawDE.addVariableDefault(derEntVar.getKey(), new NumericValue(Double.valueOf("0")));
			
		}
		
		//Need to change the formula...
		rawDE.setFormula(formula.toString());
		
		
		rawDE.setElementPatchingAction(Element.REJECT_ALL_EXISTING_ELEMENTS.toString());
		
		ds.addValidationRule(vr);

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
