/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as 
published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.psygrid.datasetdesigner.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.hibernate.*;

/**
 * This class was added to help track down the root cause of bug# 1441 ('Create' can create unopenable or compromised-integrity study xml files)
 * @author Bill
 *
 */
public class DatasetIntegrityChecker {
	
	/**
	 * This method checks for dataset inconsistencies that have been known to occur, but whose root causes have not yet been determined.
	 * Throws a ModelException with explanatory text, detailing the first inconsistency encountered.
	 * @param ds
	 * @throws ModelException
	 */
	public static void checkDatasetIntegrity(DataSet ds) throws ModelException{
		checkNullValidationRules(ds);
		checkNullConsentFormGroup(ds);
		checkForNonIntegratedDocumentGroups(ds);
	}

	/**
	 * This method checks for the following problems:
	 * 	- document occurences with null document groups
	 * 	- document occurrences with document groups not referenced by the dataset
	 *  - redundant document occurrences (multiple document occurrences for the same document mapped to the same document group)
	 *  Throws a Model Exception with explanatory text under the above circumstances.
	 * @param ds
	 * @throws ModelException
	 */
	private static void checkForNonIntegratedDocumentGroups(DataSet ds) throws ModelException{
		List<DocumentGroup> documentGroups = ds.getDocumentGroups();
		Map<DocumentGroup, String> documentGroupMapByObject = new HashMap<DocumentGroup, String>();
		
		for(DocumentGroup dg : documentGroups){
			documentGroupMapByObject.put(dg, dg.getName());
		}
		
		int numDocs = ds.numDocuments();
		for(int i = 0; i < numDocs; i++){

			Document d = ds.getDocument(i);
						
			int numOccurrences = d.numOccurrences();
			Map<DocumentGroup, DocumentOccurrence> occurrencesForDocumentByDocGroup = new HashMap<DocumentGroup, DocumentOccurrence>();
			for(int j = 0; j < numOccurrences; j++){
				DocumentOccurrence occ = d.getOccurrence(j);
				
				// Locally saved datasets may have "Preview" document occurrences - these get stripped when the
				// dataset is saved to the server.
				if(occ.getName().startsWith("Preview")) continue;				

				//Find the document occurrence's document group within the dataset reference.
				
				System.out.println(occ.getName());
				if(occ.getDocumentGroup() == null){
					throw new ModelException("Document with occurrence name: " + occ.getName() + " has null document group.");
				}else{
					if(documentGroupMapByObject.get(occ.getDocumentGroup()) == null){
						throw new ModelException("Document occurrence: " + occ.getName() + " has the following document group, which is not"
								+ " referenced  from the dataset: " + occ.getDocumentGroup().getName());
					}
				}
				
				
				if(occurrencesForDocumentByDocGroup.get(occ.getDocumentGroup()) != null){
					throw new ModelException("Redundant document group detected. There is more than one occurrence of " + d.getName() + " mapped to the following document group: " + occ.getDocumentGroup().getName());
				}
				occurrencesForDocumentByDocGroup.put(occ.getDocumentGroup(), occ);
				
			} //End of document occurrence loop
			
		} //End of document loop
		
	}
	
	/**
	 * This method checks through all entries in the dataset's documents, and looks for associated validation rules that are null.
	 * It also checks within the dataset's 'master' list of validation rules.
	 * If found, the method throws a ModelException with explanatory text.
	 * @param ds
	 * @throws ModelException
	 */
	private static void checkNullValidationRules(DataSet ds) throws ModelException{
		
		int numValidationRules = ds.numValidationRules();
		for(int count = 0; count < numValidationRules; count++){
			ValidationRule r = ds.getValidationRule(count);
			if(r == null){
				throw new ModelException("Dataset has one or more null validation rules.");
			}
			
		}
		
		int numDocuments = ds.numDocuments();
		for(int i = 0; i < numDocuments; i++){
			Document d = ds.getDocument(i);
			int numEntries = d.numEntries();
			for(int j = 0; j < numEntries; j++){
				Entry e = d.getEntry(j);
				if (e instanceof BasicEntry){
					BasicEntry bE = (BasicEntry)e;
					if(basicEntryHasNullValidationRule(bE)){
						throw new ModelException("Document: " + d.getName() + " has entry: " + bE.getName() + " with a null validation rule.");
					}
				}else if(e instanceof CompositeEntry){
					CompositeEntry cE = (CompositeEntry)e;
					int numBasicEntries = cE.numEntries();
					for(int k = 0; k < numBasicEntries; k++){
						if(basicEntryHasNullValidationRule(cE.getEntry(k))){
							throw new ModelException("Document: " + d.getName() + " has entry: " + cE.getEntry(k).getName() + " with a null validation rule.");
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * Checks the basic entry passed in for any null validation rules. Returns true if a null rule is found, and returns false otherwise.
	 * @param bE
	 * @return
	 */
	private static boolean basicEntryHasNullValidationRule(BasicEntry bE){
		int numRules = bE.numValidationRules();
		for(int i = 0; i < numRules; i++){
			ValidationRule r = bE.getValidationRule(i);
			if(r == null){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * This method checks the dataset's consent form groups, and throws a ModelException with explanatory text if any are null.
	 * @param ds
	 * @throws ModelException
	 */
	private static void checkNullConsentFormGroup(DataSet ds) throws ModelException{
		int numConsentFormGroups = ds.numAllConsentFormGroups();
		for(int i = 0; i < numConsentFormGroups; i++){
			ConsentFormGroup gp = ds.getAllConsentFormGroup(i);
			if(gp == null){
				throw new ModelException("Dataset has null consent form group.");
			}
		}
	}
}
