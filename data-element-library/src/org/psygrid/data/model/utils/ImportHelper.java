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
package org.psygrid.data.model.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.psygrid.data.dao.ElementAuthorityNotRecognizedException;
import org.psygrid.data.dao.ElementRevisionException;
import org.psygrid.data.dao.UnknownNativeRelationship;
import org.psygrid.data.model.FailedTestException;
import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.DataElementContainer;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DerivedEntry;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Element;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.ExternalDerivedEntry;
import org.psygrid.data.model.hibernate.LSIDAuthority;
import org.psygrid.data.model.hibernate.LSIDException;
import org.psygrid.data.model.hibernate.Option;
import org.psygrid.data.model.hibernate.OptionDependent;
import org.psygrid.data.model.hibernate.OptionEntry;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.ValidationRule;
public class ImportHelper {
	
	private enum ElementType{
		Element,
		ValidationRule
	}
	
	/**
	 * Call this method to translate native element relationships into del element relationships.
	 * For example, a document object will have a new 'element relationship' generated for each of its entries, and the
	 * entry will then be removed from the document's entry list. The document can then be stored retrieved and revised
	 * atomically, using hibernate. This is because 'del' element relationships constitute soft links to other elements,
	 * referencing them only by their lsid string.
	 * 
	 * @param elem - the element for which 'del' element relationships are to be generated.
	 * @param recurse - whether to generate 'del' element relationships for subordinate elements as well.
	 * @throws UnknownNativeRelationship
	 * @throws FailedTestException 
	 * @throws LSIDException 
	 * @throws ElementAuthorityNotRecognizedException 
	 * @throws ElementRevisionException
	 */
	public static void translateNativeElementCollectionToElementRelationships(DataElementContainer elem, boolean recurse, List<LSIDAuthority> allowedAuthorities) throws UnknownNativeRelationship, FailedTestException, LSIDException, ElementAuthorityNotRecognizedException{
		
		Persistent theElement = elem.getElement();
		
		if(theElement instanceof DataSet){
			translateNativeElementCollectionToElementRelationshipsInner(theElement, theElement, recurse, true, allowedAuthorities);
		}else{
			translateNativeElementCollectionToElementRelationshipsInner(theElement, theElement, recurse, false, allowedAuthorities);
		}
		
		
	}
	
	private static ElementType getElementType(Persistent element){
		ElementType type = null;
		if(element instanceof org.psygrid.data.model.hibernate.Element){
			type = ElementType.Element;
		}else if(element instanceof org.psygrid.data.model.hibernate.ValidationRule){
			type = ElementType.ValidationRule;
		}
		
		return type;
	}
	
	/**
	 * Retrieves the option dependency relationships within a document. The objects in involved in the relationship
	 * are stored by reference.
	 * 
	 * @param doc - the document with the option dependencies.
	 * @return - Returns a list of OptionDependencyRelationship objects
	 */
	public static List<OptionDependencyRelationship> getOptionDependencyRelationships(org.psygrid.data.model.hibernate.Document doc){
		
		List<OptionDependencyRelationship> opDepRelationships = new ArrayList<OptionDependencyRelationship>();
		
		
		List<Entry> entries = doc.getEntries();
		List<Integer> optionEntries = new ArrayList<Integer>();
		
		for(int i = 0; i < entries.size(); i++){
			
			Entry entry = entries.get(i);
			
			if(entry instanceof OptionEntry){
				optionEntries.add(i);
			}
		}
		
		//Now go through the list of option entries and determine which ones have option dependencies.
		for(Integer opEntryIndex: optionEntries){
			
			OptionEntry opEntry = (OptionEntry)doc.getEntry(opEntryIndex);
			
			List<Option> options = opEntry.getOptions();
			for(int i = 0; i < options.size(); i++){
				Option op = options.get(i);
				
				List<OptionDependent> opDependents = op.getOptionDependents();
				
				for(int j = 0; j < opDependents.size(); j++){
					OptionDependent opDep = opDependents.get(j);
					
					//Add the option dependent information to the returned array.
					OptionDependencyRelationship relationship = new OptionDependencyRelationship(opEntry, i, (Entry)opDep.getDependentEntry(), opDep.getEntryStatus());
					opDepRelationships.add(relationship);
				}
				
			}
			
		}
		
		return opDepRelationships;
	}
	
	/**
	 * Builds OptionDependentElementRelationship objects from the incoming list of OptionDependencyRelationship objects and
	 * adds them to the doc.
	 * @param doc
	 * @param opDepRelationships
	 */
	public static void buildOpDepElementRelationshipsInDocument(Document doc, List<OptionDependencyRelationship> opDepRelationships){
		if(opDepRelationships == null){
			return;
		}
		for(OptionDependencyRelationship opDepRel: opDepRelationships){
			doc.addRelatedElement(ElementRelationshipSerializer.initialiseOptionDependentElementRelationship(opDepRel.getOptionEntry(), opDepRel.getDependentEntry(), opDepRel.getStatus(), opDepRel.getOptionIndex()));
		}
	}
	
	/**
	 * This method removes the native element relationships from the element and its constituents, and
	 * replaces them with ElementRelationship objects. These objects relationships are maintained by relating element
	 * lsids, and some information about how to 'reconstruct' the relationships when required.
	 * Note that the method is recursive, and if given an element with nested subordinates (e.g. a dataset), it will
	 * not only create ElementRelationship objects for its immediate subordinates, but also for its subordinates'
	 * subordinates, and so on.
	 * 
	 * This method works in unison with ElementRelationshipSerializer.initialiseElementRelationship. The meat of 
	 * translateNativeElementCollectionToElementRelationships is navigational (and to remove the native, db-coupled element relationship)
	 * but is initialiseElementRelationship that constructs the new ElementRelationship object.
	 * 
	 * It is possible that some subordinate elements are already in the database. In this case, the subordinate element will
	 * already have an lsid. In this case, the logic is to construct the ElemenRelationship with this lsid, and a NULL 
	 * reference to relatedElement. This state indicates to later functions that a new lsid does not need to be generated.
	 * 
	 * @param elem - The element whose element relationships are to be constructed
	 * @throws UnknownNativeRelationship 
	 * @throws FailedTestException 
	 * @throws LSIDException 
	 * @throws ElementAuthorityNotRecognizedException 
	 * @throws ElementRevisionException 
	 */
	private static void translateNativeElementCollectionToElementRelationshipsInner(Persistent rootElement, Persistent element, boolean recurse, boolean rootIsDataSet, List<LSIDAuthority> allowedAuthorities) throws UnknownNativeRelationship, FailedTestException, LSIDException, ElementAuthorityNotRecognizedException{
		
		System.out.println(element.getClass().getName());
		
		//We need to find out what type of underlying element has been passed in.
		ElementType type = getElementType(element);
		
		if(type == ElementType.Element){
			//remove the element-to-dataset relationship...
			Element elem = (Element)element;		
			
			if(allowedAuthorities != null && elem.getLSID() != null &&  !ElementUtility.elementAuthorityIsInList(elem.getLSID().toString(), allowedAuthorities)){
				throw new ElementAuthorityNotRecognizedException("Element named '" + elem.getName() + "' has an lsid authority of " +
						elem.getLSID().getAuthorityId() + ", which is not recognized in the library database.");
			}

			if(elem.getMyDataSet() != null){
			
				if(rootIsDataSet){
					elem.addRelatedElement(ElementRelationshipSerializer.initialiseBasicElementRelationship(new DataElementContainer(elem), new DataElementContainer(elem.getMyDataSet()), null));
				}
				
				//Get rid of the native relationship.
				elem.setMyDataSet(null);
			}
			
			
			if(elem instanceof BasicEntry){
				//need to deal with validation rules.
				BasicEntry basicElem = (BasicEntry) elem;
				
				for(int i = 0; i < basicElem.getValidationRules().size(); i++){
					ValidationRule rule = (ValidationRule)basicElem.getValidationRule(i);
					basicElem.addRelatedElement(ElementRelationshipSerializer.initialiseBasicElementRelationship(new DataElementContainer(basicElem), new DataElementContainer(rule), null));
					
					if(recurse)
						translateNativeElementCollectionToElementRelationshipsInner(rootElement, rule, recurse, rootIsDataSet, allowedAuthorities);
				}
				
				//Remove the native validation rule relationships now.
				int numberOfRulesToRemove = basicElem.getValidationRules().size();
				for(int i = numberOfRulesToRemove -1; i >= 0; i--){
					basicElem.removeValidationRule(i);
				}
				
				if (elem instanceof DerivedEntry){
					DerivedEntry derivedEntry = (DerivedEntry)elem;

					if(derivedEntry.getTest() != null){
						
						DataElementContainer derivedEntryContainer = new DataElementContainer(derivedEntry);
						if(!ElementUtility.runTest(derivedEntryContainer)){
							//If this element has a test and the test fails, then throw the FailedTestException.

							String elementName = derivedEntryContainer.getElementName();
							String failureMessage = "Test failed for " + elementName;
							throw new FailedTestException(failureMessage);
						}
						
						derivedEntry.addRelatedElement(ElementRelationshipSerializer.initialiseBasicElementRelationship(derivedEntryContainer, new DataElementContainer(derivedEntry.getTest()), null));
						derivedEntry.setTest(null);
					}
					
					if(derivedEntry.getComposite() != null) {
						derivedEntry.addRelatedElement(ElementRelationshipSerializer.initialiseBasicElementRelationship(new DataElementContainer(derivedEntry), new DataElementContainer((org.psygrid.data.model.hibernate.CompositeEntry)derivedEntry.getComposite()), null));
						derivedEntry.setComposite(null);
					}
					
					Set<String> variables = derivedEntry.getVariableNames();
					int variableCount = variables.size();
					
			        Map hVars = derivedEntry.getVariables();
			        List<String> variableNames = new ArrayList<String>();
			        for ( int i=0; i< variableCount; i++){
			        	String variable = (String)variables.toArray()[i];
			        	variableNames.add(variable);
			        	Entry formulaEntry = (Entry)hVars.get(variable);
			        	derivedEntry.addRelatedElement(ElementRelationshipSerializer.initialiseDerivedEntryElementRelationship(derivedEntry, formulaEntry, variable));
			            }
			        
			        //Remove all of the variable-to-entry mappings. This is no longer required.
			        for ( int i = 0; i < variableCount; i++){
			        	derivedEntry.removeVariable(variableNames.get(i));
			        }
		        }
				
				if(elem instanceof ExternalDerivedEntry) {
		        	
		        	ExternalDerivedEntry extDerEnt = (ExternalDerivedEntry) elem;
		        	
		        	if(extDerEnt.getTest() != null){
		        		
		        		DataElementContainer extDEContainer = new DataElementContainer(extDerEnt);
						if(!ElementUtility.runTest(extDEContainer)){
							//If this element has a test and the test fails, then throw the FailedTestException.

							String elementName = extDEContainer.getElementName();
							String failureMessage = "Test failed for " + elementName;
							throw new FailedTestException(failureMessage);
						}
		        		
		        		extDerEnt.addRelatedElement(ElementRelationshipSerializer.initialiseBasicElementRelationship(extDEContainer, new DataElementContainer(extDerEnt.getTest()), null));
		        		extDerEnt.setTest(null);
		        	}
		        	
					Set<String> variables = extDerEnt.getVariableNames();
					int variableCount = variables.size();
					
			        Map hVars = extDerEnt.getVariables();
			        List<String> variableNames = new ArrayList<String>();
			        for ( int i=0; i< variableCount; i++){
			        	String variable = (String)variables.toArray()[i];
			        	variableNames.add(variable);
			        	Entry formulaEntry = (Entry)hVars.get(variable);
			        	extDerEnt.addRelatedElement(ElementRelationshipSerializer.initialiseExternalDerivedEntryElementRelationship(extDerEnt, formulaEntry, variable));
			            }
			        
			        //Remove all of the variable-to-entry mappings. This is no longer required.
			        for ( int i = 0; i < variableCount; i++){
			        	extDerEnt.removeVariable(variableNames.get(i));
			        }
				}
				
				if(elem instanceof OptionEntry && !(rootElement instanceof Document)) {
					
					OptionEntry opEntry = (OptionEntry) elem;
					List<Option> options = opEntry.getOptions();
					
					
					for(Option op: options) {
						List<OptionDependent> opDependents = op.getOptionDependents();
						//We want to remove all of the option dependents.
						//Should be able to do this strictly from the opDependents list.
						java.util.Iterator<OptionDependent> opDepIter = opDependents.iterator();
						while(opDepIter.hasNext()){
							opDepIter.next();
							opDepIter.remove();
						}
					}	
				}
				
			}else if(elem instanceof DataSet){
				
				DataSet ds = (DataSet)elem;
				List<Document> docs = ds.getDocuments();
				for(int i = 0; i < docs.size(); i++){
					Document doc = docs.get(i);
					
					if(recurse)
						translateNativeElementCollectionToElementRelationshipsInner(rootElement, doc, recurse, rootIsDataSet, allowedAuthorities);
					
					ds.addRelatedElement(ElementRelationshipSerializer.initialiseBasicElementRelationship(new DataElementContainer(ds), new DataElementContainer(doc), i));
				}
				
				//remove the native document collection now.
				int size = docs.size();
				for(int i = 0; i < size; i++){
					ds.removeDocument(0);
				}
				
			}else if (elem instanceof CompositeEntry){
				
				CompositeEntry compEntry = (CompositeEntry)elem;
				List<BasicEntry> entries = compEntry.getEntries();
				for(int i = 0; i < entries.size(); i++){
					Entry entry = entries.get(i);
					
					if(recurse)
						translateNativeElementCollectionToElementRelationshipsInner(rootElement, entry, recurse, rootIsDataSet, allowedAuthorities);
					
					compEntry.addRelatedElement(ElementRelationshipSerializer.initialiseCompositeElementrelationship(compEntry, entry, i, entry.getEntryStatus()));
				}
				
				//remove the native entry collection now.
				int size = entries.size();
				
				for(int i = 0; i < size; i++){
					entries.remove(0);
				}
				
			}else if (elem instanceof Document){
				
				Document doc = (Document)elem;
				List<Entry> entries = doc.getEntries();
				List<OptionEntry> optionEntries = new ArrayList<OptionEntry>();
				
				for(int i = 0; i < entries.size(); i++){
					
					Entry entry = entries.get(i);
					
					if(entry instanceof OptionEntry){
						optionEntries.add((OptionEntry)entry);
					}

					if(recurse)
						translateNativeElementCollectionToElementRelationshipsInner(rootElement, entry, recurse, rootIsDataSet, allowedAuthorities);
					
					doc.addRelatedElement(ElementRelationshipSerializer.initialiseDocumentEntryElementRelationship(doc, entry, i));
					
					entry.setEntryStatus(null);
				}
				
				//Find all the option entries within the document. Create element relationships for them.
				//Need to know 
				// - What the option entry lsid is.
				// - What the controlled entry lsid is.
				// - Which option (integer) controls the element.
				// - What the controlled element status is.
				
				//Unfortunately, it is necessary to introduce order dependency here - the option entry relationships must be
				//stored AFTER all of the 'normal' relationships
				
				for(OptionEntry opEntry : optionEntries){
					List<Option> options = opEntry.getOptions();
					for(Option op: options){
						List<OptionDependent> opDependents = op.getOptionDependents();
						for(OptionDependent opDep: opDependents){
							((Document)elem).addRelatedElement(ElementRelationshipSerializer.initialiseOptionDependentElementRelationship(opEntry, opDep.getMyDependentEntry(), opDep.getEntryStatus(), options.indexOf(op)));
						}
					}
					
					//remove the optionDependents now.
					for(Option op: options){
						List<OptionDependent> opDependents = op.getOptionDependents();
						//We want to remove all of the option dependents.
						//Should be able to do this strictly from the opDependents list.
						java.util.Iterator<OptionDependent> opDepIter = opDependents.iterator();
						while(opDepIter.hasNext()){
							opDepIter.next();
							opDepIter.remove();
						}
					}
				}
				
				
				//remove the native entry collection now.
				int size = entries.size();
				for(int i = 0; i < size; i++){
					doc.removeEntry(0);
				}
			}
		}else if (type == ElementType.ValidationRule){

			ValidationRule rule = (ValidationRule) element;
			
			if(allowedAuthorities != null && rule.getLSID() != null &&  !ElementUtility.elementAuthorityIsInList(rule.getLSID().toString(), allowedAuthorities)){
				throw new ElementAuthorityNotRecognizedException("Element named '" + rule.getName() + "' has an lsid authority of " +
						rule.getLSID().getAuthorityId() + ", which is not recognized in the library database.");
			}
			
			if(rule.getTest() != null){
				
				DataElementContainer ruleContainer = new DataElementContainer(rule);
				if(!ElementUtility.runTest(ruleContainer)){
					//If this element has a test and the test fails, then throw the FailedTestException.

					String elementName = ruleContainer.getElementName();
					String failureMessage = "Test failed for " + elementName;
					throw new FailedTestException(failureMessage);
				}
				
				rule.addRelatedElement(ElementRelationshipSerializer.initialiseBasicElementRelationship(ruleContainer, new DataElementContainer(rule.getTest()), null));
				rule.setTest(null);
			}
		}
		

	}		
}

