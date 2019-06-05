
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
package org.psygrid.data.dao.hibernate;



import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.psygrid.data.SearchType;
import org.psygrid.data.UserPrivilegeMatrix;
import org.psygrid.data.dao.DataElementDAO;
import org.psygrid.data.dao.ElementAuthorityNotRecognizedException;
import org.psygrid.data.dao.ElementRevisionException;
import org.psygrid.data.dao.ElementStatusChangeException;
import org.psygrid.data.dao.NoSuchElementException;
import org.psygrid.data.dao.UnknownNativeRelationship;
import org.psygrid.data.model.FailedTestException;
import org.psygrid.data.model.RepositoryModel;
import org.psygrid.data.model.RepositoryModelException;
import org.psygrid.data.model.RepositoryObjectType;
import org.psygrid.data.model.RepositoryModel.NodeIdentificationMethod;
import org.psygrid.data.model.RepositoryModel.NodeType;
import org.psygrid.data.model.RepositoryModel.RepositoryObject;
import org.psygrid.data.model.dto.DELQueryObject;
import org.psygrid.data.model.dto.DataElementContainerDTO;
import org.psygrid.data.model.dto.DocumentDTO;
import org.psygrid.data.model.dto.ElementDTO;
import org.psygrid.data.model.dto.ElementStatusContainer;
import org.psygrid.data.model.dto.LSIDDTO;
import org.psygrid.data.model.dto.PersistentDTO;
import org.psygrid.data.model.dto.ValidationRuleDTO;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType;
import org.psygrid.data.model.hibernate.Unit.UnitChainLink;
import org.psygrid.data.model.utils.ElementRelationshipSerializer;
import org.psygrid.data.model.utils.ElementUtility;
import org.psygrid.data.model.utils.ImportHelper;
import org.psygrid.data.model.utils.OptionDependencyRelationship;
import org.psygrid.data.repository.dao.RelationshipReconstitutionException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


public class DataElementDAOHibernate extends HibernateDaoSupport implements DataElementDAO{

	private static final Log sLog = LogFactory.getLog(DataElementDAOHibernate.class);
	final static String revisionNum = "0";
	final boolean retrieveNonRootElements = true;
	private List<LSIDAuthority> authorityList = null;


	protected void initDao() throws Exception{
		//Get the authorities currently in the database.
		LSIDAuthority[] authorities = getLSIDAuthorityList();
		authorityList = new ArrayList<LSIDAuthority>();

		if(authorities != null && authorities.length > 0){
			for(int i = 0; i < authorities.length; i++){
				authorityList.add(authorities[i]);
			}
		}
	}

	/**
	 * Saves the data element and all of its constituents.
	 * If any constituent elements already have lsids, such elements are already in the database and won't be saved again.
	 * 
	 * @param dataElement - the element to be saved to the database
	 * @param info - the admin info, identifying the who, what, where, when info to be used to generate the element meta-data.
	 * @param authority - the lsid authority that the lsid will belong to.
	 * @param autoApprove - should be set to 'true' only when this is being used to import data, in which case the element, and
	 * 		all sub-elements bypass the 'pending' state and go straight to 'approved'.
	 * @return - the LSID of the saved element.
	 * @throws FailedTestException 
	 * @throws ElementAuthorityNotRecognizedException 
	 */
	public org.psygrid.data.model.dto.LSIDDTO saveDataElement(DataElementContainerDTO dataElement, AdminInfo info, String authority, boolean autoApprove) throws HibernateException, UnknownNativeRelationship, UnsupportedEncodingException, LSIDException, FailedTestException, ElementAuthorityNotRecognizedException {

		org.psygrid.data.model.hibernate.LSID lsid = null;

		org.psygrid.data.model.dto.PersistentDTO underlyingElement = 
			DataElementContainer.ElementType.valueOf(dataElement.getEnumElementType()) == DataElementContainer.ElementType.primary ? dataElement.getPrimaryElement() :
				dataElement.getRuleElement();

			PersistentDTO.setDelContext(true);

			org.psygrid.data.model.hibernate.DataElementContainer hibContainer = dataElement.toHibernate();


			ImportHelper.translateNativeElementCollectionToElementRelationships(hibContainer, true, autoApprove ? null : this.authorityList);

			generateLSIDTemplates(hibContainer, authority);

			generateMetaData(hibContainer, info, true);

			//The unique ids have not yet been generated.
			Session session = null; 
			Transaction tx = null;
			try{
				ArrayList<org.psygrid.data.model.hibernate.Persistent> elemList = new ArrayList<org.psygrid.data.model.hibernate.Persistent>();
				session = getHibernateTemplate().getSessionFactory().openSession();
				tx = session.beginTransaction();
				saveElements(hibContainer, true, true, elemList, session, autoApprove);
				tx.commit();
			}catch(HibernateException ex){
				if (tx!=null) tx.rollback();
				throw ex;
			}catch(LSIDException ex){
				if(tx!=null) tx.rollback();
				throw ex;
			}catch(OutOfMemoryError er){
				if(tx!=null) tx.rollback();
				throw er;
			}finally{
				if(session != null)
					session.close();
			}

			if(session != null && session.isOpen() && session.isConnected())
				session.disconnect();

			return hibContainer.getElementLSIDObject().toDTO();
	}


	private void saveBaseUnit(Unit unit, Session session){

		if(unit.getBaseUnit()!= null){
			saveBaseUnit(unit.getBaseUnit(), session);
		}

		session.saveOrUpdate(unit);
	}


	/**
	 * Replace transformer objects in the list with equivalent ones already in the database.
	 * @param units - the list of transformers to be substituted for pre-existing db objects, if possible.
	 * @param session - hibernate session
	 */
	private void replaceTransformersWithDbEquivalents(List<Transformer>transformers, Session session) {

		Criteria crit = session.createCriteria(Transformer.class);
		List<Transformer> el = crit.list();

		int listSize = transformers.size();
		for(int i = 0; i < listSize; i++) {
			for(Transformer dbTransformer: el) {
				if(transformers.get(i).isEquivalentTo(dbTransformer)) {
					transformers.set(i, dbTransformer);
					break;
				}
			}
		}

	}

	/**
	 * Replace transformer objects in the list with equivalent ones already in the database.
	 * @param units - the list of transformers to be substituted for pre-existing db objects, if possible.
	 * @param session - hibernate session
	 */
	private void replaceExternalTransformerWithDbEquivalents(org.psygrid.data.model.hibernate.ExternalDerivedEntry entry, Session session) {
		Transformer transformer = (Transformer)entry.getExternalTransformer();
		Criteria crit = session.createCriteria(Transformer.class);
		List<Transformer> el = crit.list();

		for(Transformer dbTransformer: el) {
			if(transformer.isEquivalentTo(dbTransformer)) {
				entry.setExternalTransformer(dbTransformer);
				break;
			}
		}
	}

	/**
	 * Replaces validation rules (and the element relationships involving them) with an eqiuvalent rule already in the database, if one already exists.
	 * The BasicEntry object passed must have already had its relationships converted to del element relationships.
	 * This method should only really used during an import.
	 * @param rules
	 * @param session
	 */
	private void replaceValidationRulesWithDbEquivalents(org.psygrid.data.model.hibernate.DataElementContainer bEntry, Session session){
		Criteria crit = session.createCriteria(org.psygrid.data.model.hibernate.ValidationRule.class);
		List<org.psygrid.data.model.hibernate.ValidationRule> el = crit.list();

		List<org.psygrid.data.model.hibernate.ElementRelationship> elementRelationships = bEntry.getElementRelationships();

		Map<Integer, org.psygrid.data.model.hibernate.ElementRelationship> replacementIndices = new HashMap<Integer, org.psygrid.data.model.hibernate.ElementRelationship>();

		for(int i = 0; i < elementRelationships.size(); i++){

			org.psygrid.data.model.hibernate.ElementRelationship elRel = elementRelationships.get(i);
			if(elRel.getRelatedElement() != null && elRel.getRelatedElement().getElement() instanceof org.psygrid.data.model.hibernate.ValidationRule){

				org.psygrid.data.model.hibernate.ValidationRule valRule = (org.psygrid.data.model.hibernate.ValidationRule) elRel.getRelatedElement().getElement();

				//try to find a match.
				for(org.psygrid.data.model.hibernate.ValidationRule rule: el){
					if(valRule.isEquivalentTo(rule)){

						replacementIndices.put(i, ElementRelationshipSerializer.initialiseBasicElementRelationship(bEntry, new org.psygrid.data.model.hibernate.DataElementContainer(rule) , null));

					}
				}
			}
		}

		//Now rivise the elementRelationships list according to the replacementIndices mapping.
		Set<Integer> repIndKeySet = replacementIndices.keySet();
		for(Integer index: repIndKeySet){
			int indexInt = index;
			elementRelationships.remove(indexInt);
			elementRelationships.add(index, replacementIndices.get(index));
		}
	}

	/**
	 * Replace unit objects in the list with equivalent ones already in the database.
	 * @param units - the list of units to be substituted for pre-existing db objects, if possible.
	 * @param session - hibernate session
	 */
	private void replaceUnitsWithDbEquivalents(List<Unit>units, Session session){

		Criteria crit = session.createCriteria(Unit.class);
		List<Unit> el = crit.list();

		int listSize = units.size();
		for(int i = 0; i < listSize; i++) {
			for(Unit dbUnit: el) {

				if(units.get(i).isEquivalentTo(dbUnit)) {
					units.set(i, dbUnit);
					break;
				}
			}
		}
	}


	/**
	 * Replaces the authority object of the incoming lsid with a string-equivanent one from the database, if there 
	 * is one.
	 * @param lsid - object whose authority object is to be replaced, if possible.
	 * @param session - hibernate session
	 */
	private void replaceLSIDAuthorityWithDbEquivalent(org.psygrid.data.model.hibernate.LSID lsid, Session session){

		Criteria crit = session.createCriteria(LSIDAuthority.class);
		List<LSIDAuthority> authorities = crit.list();

		int listSize = authorities.size();

		for(int i = 0; i < listSize; i++){

			if(lsid.getAuthorityId().equals(authorities.get(i).getAuthorityID())){
				lsid.setLsidAuthority(authorities.get(i));
				break;
			}
		}
	}

	/**
	 * Replaces the namespace object of the incoming lsid with a string-equivalent one from the database, if there
	 * is one.
	 * 
	 * @param lsid - object whose namespace object is to be replaced, if possible.
	 * @param session - hibernate session.
	 */
	private void replaceLSIDNameSpaceWithDbEquivalent(org.psygrid.data.model.hibernate.LSID lsid, Session session){

		Criteria crit = session.createCriteria(LSIDNameSpace.class);
		List<LSIDNameSpace> nameSpaces = crit.list();

		int listSize = nameSpaces.size();

		for(int i = 0; i < listSize; i++){
			if(lsid.getNamespaceId().equals(nameSpaces.get(i).getNameSpace())){
				lsid.setLsidNameSpace(nameSpaces.get(i));
				break;
			}
		}
	}


	/**
	 * This method helps to get elements ready for saving. 
	 * Removes any unnecessary peripheral objects that aren't part of an element definition.
	 * Determines whether there are already matching (by string) lsid authorities, namespaces w/in the db and uses the
	 * existing db entries where possible to prevent a proliferation of identical namespace/authority objects in the
	 * database.
	 * 
	 * If the element container has units, it checks to see if they are identical to existing db entries, and uses the
	 * existing entries where possible to prevent proliferation of identical units within the database.
	 * 
	 * Does the same thing with transformers.
	 *  
	 * @param elem
	 * @param session
	 * @param isImport - whether this is for an import (some logic is import-specific).
	 */
	private void preprocessElement(org.psygrid.data.model.hibernate.DataElementContainer elem, Session session, boolean isImport){

		if(org.psygrid.data.model.hibernate.Document.class.isAssignableFrom(elem.getElementClass())){

			//Remove consent form groups and document occurrences.
			org.psygrid.data.model.hibernate.Document importDoc = (org.psygrid.data.model.hibernate.Document)elem.getElement();

			List<org.psygrid.data.model.hibernate.ConsentFormGroup> cfgs = new ArrayList<org.psygrid.data.model.hibernate.ConsentFormGroup>();
			importDoc.setConFrmGrps(cfgs);

			int numDocOccurrences = importDoc.numOccurrences();
			for(int count = numDocOccurrences-1; count >= 0; count--){
				importDoc.removeOccurrence(count);
			}
		}

		replaceLSIDAuthorityWithDbEquivalent(elem.getElementLSIDObject(), session);
		replaceLSIDNameSpaceWithDbEquivalent(elem.getElementLSIDObject(), session);

		//Save units first.
		if(org.psygrid.data.model.hibernate.BasicEntry.class.isAssignableFrom(elem.getElementClass()) &&
				((org.psygrid.data.model.hibernate.BasicEntry)elem.getElement()).getUnits() != null &&
				((org.psygrid.data.model.hibernate.BasicEntry)elem.getElement()).getUnits().size() > 0){
			org.psygrid.data.model.hibernate.BasicEntry bE = (org.psygrid.data.model.hibernate.BasicEntry)elem.getElement();

			replaceUnitsWithDbEquivalents(bE.getUnits(), session);

			for(org.psygrid.data.model.hibernate.Unit unit: bE.getUnits()){
				List<UnitChainLink> unitChainInfo = unit.buildUnitChainInfo();

				//Save the end link first. If it has a base unit, remove the reference, and add it back later.
				boolean linkIsRecursive = false;
				boolean foundBeginningOfChain = false;
				Unit recursionUnit = null;
				Unit endUnit = null;
				for(int i = unitChainInfo.size()-1; i >= 0; i--){ //Iterate backwards, because END link is at end of list.
					UnitChainLink link = unitChainInfo.get(i);

					List<Unit.UnitChainDescriptor> descriptorList = link.getUnitChainType();

					for(int j = 0; j < descriptorList.size(); j++){
						if(descriptorList.get(j) == Unit.UnitChainDescriptor.End){

							endUnit = link.getUnit();

							if(link.getUnit().getBaseUnit() != null){
								linkIsRecursive = true;
								recursionUnit = link.getUnit().getBaseUnit();
								endUnit.setBaseUnit(null);

							}
						}

						if(descriptorList.get(j) == Unit.UnitChainDescriptor.Start){
							foundBeginningOfChain = true;
							break;
						}

					}

					session.saveOrUpdate(link.getUnit());

					if(foundBeginningOfChain && linkIsRecursive){ //put the recursion back together, if necessary
						endUnit.setBaseUnit(recursionUnit);
						session.saveOrUpdate(endUnit);
					}

				} //END backwards iteration through unit chain info list.
			}
		}


		if(org.psygrid.data.model.hibernate.BasicEntry.class.isAssignableFrom(elem.getElementClass()) &&
				((org.psygrid.data.model.hibernate.BasicEntry)elem.getElement()).getTransformers() != null &&
				((org.psygrid.data.model.hibernate.BasicEntry)elem.getElement()).getTransformers().size() > 0) {
			org.psygrid.data.model.hibernate.BasicEntry bE = (org.psygrid.data.model.hibernate.BasicEntry)elem.getElement();

			replaceTransformersWithDbEquivalents(bE.getTransformers(), session);
		}

		if(org.psygrid.data.model.hibernate.BasicEntry.class.isAssignableFrom(elem.getElementClass()) &&
				((org.psygrid.data.model.hibernate.BasicEntry)elem.getElement()).getOutputTransformers() != null &&
				((org.psygrid.data.model.hibernate.BasicEntry)elem.getElement()).getOutputTransformers().size() > 0) {
			org.psygrid.data.model.hibernate.BasicEntry bE = (org.psygrid.data.model.hibernate.BasicEntry)elem.getElement();

			replaceTransformersWithDbEquivalents(bE.getOutputTransformers(), session);
		}

		if(org.psygrid.data.model.hibernate.ExternalDerivedEntry.class.isAssignableFrom(elem.getElementClass()) &&
				((org.psygrid.data.model.hibernate.ExternalDerivedEntry)elem.getElement()).getTransformers() != null &&
				((org.psygrid.data.model.hibernate.ExternalDerivedEntry)elem.getElement()).getTransformers().size() > 0) {
			org.psygrid.data.model.hibernate.ExternalDerivedEntry extDE = (org.psygrid.data.model.hibernate.ExternalDerivedEntry)elem.getElement();

			replaceTransformersWithDbEquivalents(extDE.getTransformers(), session);
			List<Transformer> extTransformer = new ArrayList<Transformer>();
			extTransformer.add((Transformer)extDE.getExternalTransformer());
			replaceExternalTransformerWithDbEquivalents(((org.psygrid.data.model.hibernate.ExternalDerivedEntry)elem.getElement()), session);
		}

		//The following clause won't work because by the time this method is called, all of the native element relationships
		//have been supplanted by del element relationships.
		//So what we want to do is to find those element relationships that have a ValidationRule as a relatedElement,
		//and to revise that relationship with an equivalent in the database if one exists.

		if(isImport && org.psygrid.data.model.hibernate.BasicEntry.class.isAssignableFrom(elem.getElementClass())){
			replaceValidationRulesWithDbEquivalents(elem, session);
		}


	}


	/**
	 * Sets the element status.
	 * Also sets the isHeadRevision flag to 'true', and sets the element submission context.
	 * 
	 * @param elem
	 * @param autoApprove
	 */
	private void setSavedElementStatus(org.psygrid.data.model.hibernate.DataElementContainer elem, boolean autoApprove, boolean rootNode){

		//AutoApprove means that this is an import job, and that ALL nodes (root and all) are approved,
		//and that all nodes are set to 'root' submission context immediately.

		//If autoApprove is false, then only the root gets the 'root' submission context.
		//All other elements get set to ***

		DataElementStatus elemStatus = null;

		if(autoApprove){
			elemStatus = DataElementStatus.APPROVED;
		}else{
			elemStatus = DataElementStatus.PENDING;
		}

		ElementSubmissionContext submitContext = null;
		if(autoApprove){
			//Set the submission context to 'root' automatically.
			submitContext = ElementSubmissionContext.ROOT;
		}else{
			if(rootNode){
				//Set the submission context to 'root'
				submitContext = ElementSubmissionContext.ROOT;
			}else{
				//Set the submission context to 
				submitContext = ElementSubmissionContext.SUBORDINATE;
			}
		}


		if(elem.getElementType() == DataElementContainer.ElementType.primary){
			//This is a primary element.

			((org.psygrid.data.model.hibernate.Element)elem.getElement()).setEnumStatus(elemStatus.toString());
			((org.psygrid.data.model.hibernate.Element)elem.getElement()).setHeadRevision(true);
			((org.psygrid.data.model.hibernate.Element)elem.getElement()).setSubmissionContext(submitContext);

		}else{

			((org.psygrid.data.model.hibernate.ValidationRule)elem.getElement()).setEnumStatus(elemStatus.toString());
			((org.psygrid.data.model.hibernate.ValidationRule)elem.getElement()).setHeadRevision(true);
			((org.psygrid.data.model.hibernate.ValidationRule)elem.getElement()).setEnumSubmissionContext(submitContext.toString());
		}
	}



	/**
	 * Saves the element and its immediate constituents. 
	 * @param elem - the element to save
	 * @param rootNode - specifies whether the element passed in is the root node. If so, then its immediate constituents
	 * 					 are automatically saved.
	 * @param recurse - if true, then any element passed in will also have its constituent elements saved
	 * @param elemList - a list of elements already saved via recursive method calls. Offers insurance against
	 * 							redundant saving.
	 * @param session - 	the hibernate session object.
	 * @throws HibernateException
	 * @throws FailedTestException 
	 * @throws LSIDException - this will happen if the element being saved has already-persisted sub-elements that don't match the current library contents.
	 */
	private void saveElements(org.psygrid.data.model.hibernate.DataElementContainer elem, boolean rootNode, boolean recurse, ArrayList<org.psygrid.data.model.hibernate.Persistent> elemList, Session session, boolean autoApprove) throws HibernateException, FailedTestException, LSIDException{

		if (elemList.indexOf(elem.getElement()) != -1){
			return;
		}

		elem.setInstanceLSID(null); //Do not save instance lsids into the element library!

		setSavedElementStatus(elem, autoApprove, rootNode);

		boolean isImport = autoApprove;
		preprocessElement(elem, session, isImport);

		session.saveOrUpdate(elem.getElement()); //Unless we do this, we don't get the hibernate id!
		elem.getElementLSIDObject().completeObjectIdWithDBSpecifics(elem.getId());

		elem.getLatestMetaData().setElementLSID(elem.getElementLSID());

		elemList.add(elem.getElement());

		if(recurse){
			List<org.psygrid.data.model.hibernate.ElementRelationship> elemRelationships = elem.getElementRelationships();

			for(int i = 0; i < elemRelationships.size(); i++){
				org.psygrid.data.model.hibernate.ElementRelationship elemRelationship = elemRelationships.get(i);

				if(elemRelationship instanceof OptionDependentElementRelationship){
					//Handle this here or should the handling be in a separate post-save method?
					//Actually we can just handle it here. Should just be a matter of updating the element
					//relationships with the lsids of their related elements.
					//OptionDependentElementRelationship opDepElemRelationship = (OptionDependentElementRelationship)elemRelationship;
					continue;
				}

				if(!elemRelationship.getRelatedElementIsPersisted()){
					if(elemRelationship.getRelationshipType() != org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.repositoryRelationship){

						saveElements(elemRelationship.getRelatedElement(), false, true, elemList, session, autoApprove);
					}	
				}else{
					//If the related element is alread persisted, it is necessary to ensure that the lsid provided matches the lsid in the database.
					//This is to protect against the possibility that the element could have found its way from a separate library database with the
					//an identical authority name (which is currently possible because the test system).
					ElementUtility.getElement(elemRelationship.getRelatedElementLSID(), session);
				}
			}
		}

	}



	/**
	 * Recursively generates lsid 'templates' for objects. The lsid objects are created here, but must be finalised during the actual saving
	 * of objects, at which point the lsid's object field is appended with the hibernate database id of the object itself.
	 * The method relies on the native element relationships having already been converted to 'del' element relationships.
	 * 
	 * If any elements are encountered that already have an lsid object, the extant object is left intact and 
	 * unchanged.
	 * 
	 * 
	 * @param dataElement - the element for which an lsid is to be generated.
	 * @param authority - the lsid authority to which the element belongs.
	 * @return - the newly-generated lsid template
	 * @throws UnsupportedEncodingException
	 * @throws LSIDException
	 */
	private org.psygrid.data.model.hibernate.LSID generateLSIDTemplates(org.psygrid.data.model.hibernate.DataElementContainer dataElement, String authority) throws UnsupportedEncodingException, LSIDException {
		org.psygrid.data.model.hibernate.LSID lsid = generateLSIDTemplate(dataElement, authority);
		dataElement.setLSID(lsid);

		List<org.psygrid.data.model.hibernate.ElementRelationship> elemRelationships = dataElement.getElementRelationships();

		for(int i = 0; i < elemRelationships.size(); i++){
			org.psygrid.data.model.hibernate.ElementRelationship elemRelationship = elemRelationships.get(i);

			if(elemRelationship.getRelatedElementIsPersisted()){
				//Make sure that the related element lsid's authority belongs to one of the authorities already in the
				//database.

				continue;
			}else{
				org.psygrid.data.model.hibernate.LSID id2 = elemRelationship.getRelatedElement().getElementLSIDObject();

				if(id2 == null){
					this.generateLSIDTemplates(elemRelationship.getRelatedElement(), authority);

				}

			}	
		}

		return lsid;
	}


	/**
	 * Generates a new metadata object for the element, based on the AdminInfo passed in. The method will also generate
	 * metadata for subordinate elements, if 'recurse' is set to true.
	 * @param elem - the element for which ElementMetaData is to be generated, based on the AdminInfo passed in.
	 * @param info - details the who, what, where, why info to be used to generate the element meta-data.
	 * @param recurse - whether or not to generate metadata for subordinate elements as well. If this is set to true, then the
	 * DataElementContainer MUST have had its native element relationships translated to del relationships first.
	 */
	private void generateMetaData(org.psygrid.data.model.hibernate.DataElementContainer elem, AdminInfo info, boolean recurse) {	

		if(elem.getMetaData().size() > 0 && recurse){
			return;
		}

		ElementMetaData theMetaData = new ElementMetaData();
		theMetaData.setActivityDescription(info.getDescription());
		theMetaData.setWho(info.getWho());
		theMetaData.setElementDate(new Date());
		theMetaData.setElementStatus(info.getElementActive() ? ElementMetaData.Status.activated : ElementMetaData.Status.deactivated);
		theMetaData.setRegistrar(info.getRegistrar());
		theMetaData.setReplacedBy(null);
		theMetaData.setTerminologicalRef(info.getTerminologicalReference());
		theMetaData.setAction(DataElementAction.valueOf(info.getActionTaken()));

		elem.addMetaData(theMetaData);

		List<org.psygrid.data.model.hibernate.ElementRelationship> elemRelationships = elem.getElementRelationships();

		if(recurse){
			for(int i = 0; i < elemRelationships.size(); i++){
				org.psygrid.data.model.hibernate.ElementRelationship elemRelationship = elemRelationships.get(i);

				org.psygrid.data.model.hibernate.DataElementContainer relatedElement = elemRelationship.getRelatedElement();
				if(relatedElement != null){
					generateMetaData(relatedElement, info, recurse);
				}
			}
		}
	}

	/**
	 * Generates an lsid 'template' for the element container passed in (the lsid is to be completed later when the
	 * element is assigned a hibernate db id during saving, and this id is appended to the object id).
	 * 
	 * If the container already has an lsid object, then the extant one will be returned.
	 * Otherwise, a new one is generated and returned.
	 * 
	 * @param dataElement - element for which a new lsid is to be assigned.
	 * @param authority - the element-owning authority.
	 * @return - the dataElement's lsid.
	 * @throws UnsupportedEncodingException
	 * @throws LSIDException
	 */
	private org.psygrid.data.model.hibernate.LSID generateLSIDTemplate(org.psygrid.data.model.hibernate.DataElementContainer dataElement, String authority) throws UnsupportedEncodingException, LSIDException {
		final String METHOD_NAME = "generateLSID";

		//Protect against setting the lsid twice...
		if(dataElement.getElementLSIDObject() != null)
			return dataElement.getElementLSIDObject();

		String nameSpace = dataElement.getElement().getClass().getName();

		//Reserve 12 + 12 + hyphen + hyphen = 26 spaces for the lsid and element object id appendix to the lsid object id
		//The entire LSID needs can be, at a maximum, 255.
		//Subtracting the 26, that leaves 229.
		//The URN:LSID: prefix takes 9, leaving 220.
		//Reserve 12 + colon = 13 spaces for the revision bit, leaving 207.
		//So, the space left for the name+display text portion of the lsid is
		//207 - (authority_length + 1) =- (namespace_length +1) 
		//which is 205 - (authority_length + namespace_length)

		org.psygrid.data.model.hibernate.LSID lsid = null;
		String objectId = dataElement.getElementName();

		if(objectId != null && dataElement.getElementName().equals("")){
			objectId = objectId.concat(" ");
		}

		if(dataElement.getElementDescription() != null && dataElement.getElementDescription().length() > 0){
			if(objectId == null)
				objectId = new String();

			objectId = objectId.concat("_" + dataElement.getElementDescription());
		}

		//If we've gotten this far and the objectId is still null or empty, allow the object id to become the
		//last bit of the namespace.
		if(objectId == null || objectId.length() == 0){
			objectId = nameSpace.substring(nameSpace.lastIndexOf(".")+1, nameSpace.length());
		}

		//Need to check if this thing is NSS-compliant. If it isn't then transform it before putting into database.

		if(!org.psygrid.data.model.hibernate.LSID.isNSSCompliant(objectId)){
			try {
				objectId = URLEncoder.encode(objectId, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				//log this.
				throw e;
			}
		}

		int maxObjectIdLength = 205 - (authority.length() + nameSpace.length()); 
		if(objectId.length() > maxObjectIdLength){
			objectId = objectId.substring(0, maxObjectIdLength);
			//There is a possibility that the substring command has chopped an escape sequence in 
			//half and therefore the objectId is no longer valid.

			//remedy this by iteratively removing another character off the end of the string until the
			//string is compliant.

			while(!org.psygrid.data.model.hibernate.LSID.isNSSCompliant(objectId)){
				objectId = objectId.substring(0, objectId.length()-1);
			}
		}


		try {
			lsid = org.psygrid.data.model.hibernate.LSID.valueOf(authority, nameSpace, objectId, DataElementDAOHibernate.revisionNum, true);
		} catch (LSIDException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(), e);
			throw e;
		}
		return lsid;
	}


	/**
	 * Retrieves the head revision lsid if 'omitPending' is false.
	 * If 'omitPending' is true, it returns the latest revision with an 'approved' status.
	 * 
	 * Note: If the lsid passed in is currently pending and 'omitPending' is true then a NoSuitableRevisionFound
	 * exception will be thrown. 
	 * 
	 * @param lsid - the lsid for which the latest appropriate revision is to be found.
	 * @param omitPending - whether to report pending elements, or to omit them.
	 * @return - the latest appropriate revision.
	 */
	public String getCurrentRevisionLevel(final String lsid, final boolean omitPending){

		final String METHOD_NAME = "getCurrentRevisionLevel";

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				Long lsidID = null;
				try {
					lsidID = ElementUtility.getLSIDIdFromLSIDString(lsid);
				} catch (LSIDException e) {
					sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(), e);
					throw new HibernateException(e);
				}

				org.psygrid.data.model.hibernate.LSID lsidObj = null;
				String penultimateRevision = lsid;
				String latestRevision = lsid;


				lsidObj = (org.psygrid.data.model.hibernate.LSID)session.createQuery("from LSID e where e.id=?")
				.setLong(0, lsidID)
				.uniqueResult();

				while(lsidObj.getNextRevision() != null){

					penultimateRevision = lsidObj.toString();  

					String nextLSID = lsidObj.getNextRevision();
					try {
						lsidID = ElementUtility.getLSIDIdFromLSIDString(nextLSID);
					} catch (LSIDException e) {
						sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(), e);
						throw new HibernateException(e);
					}

					lsidObj = (org.psygrid.data.model.hibernate.LSID)session.createQuery("from LSID e where e.id=?")
					.setLong(0, lsidID)
					.uniqueResult();

					latestRevision = lsidObj.toString();
				}

				//Once the head Revision is reached, decide return the head revision if:
				//it is APPROVED. If it is not approved, then return it only if omitPending is 'false'.
				//But if it is pending and omitPending is true then return the previous revision.
				String returnLSID = null;  


				if(!omitPending){
					returnLSID = latestRevision;
				}else{   	
					DataElementStatus latestElementStatus;

					try {
						org.psygrid.data.model.hibernate.DataElementContainer latestElem = ElementUtility.getElement(latestRevision, session);
						latestElementStatus = latestElem.getStatus();
					} catch (LSIDException e) {
						sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(), e);
						throw new HibernateException(e);
					}

					if(latestElementStatus == DataElementStatus.APPROVED){
						returnLSID = latestRevision;
					}else if(latestElementStatus == DataElementStatus.PENDING){

						if(latestRevision.equals(penultimateRevision)){
							//TODO: Throw a NoSuitableRevisionFound exception (needs to be created).
						}

						returnLSID = penultimateRevision;
					}else{
						//TODO: Handle future states, not currently in use.
					}      
				}

				return returnLSID;
			}

		};

		Object obj = getHibernateTemplate().execute(callback);
		return (String)obj;
	}


	/**
	 * Retrieves an element from the database. 
	 * @param lsid - the identifier of the element to be retrieved.
	 * @param retrieveAllConstituents - if true, retrieve all subordinate consitiuents. If false, retrieve
	 * 		only the element of the lsid passed in, ignoring any consitiuents.
	 * @param eraseDBReferences - if true, all hibernate ids will be erased. This option should be used when the
	 * 		calling client is going to deploy the returted element into a dataset db.
	 * @param upm - an object detailing the user's roles - to be used to determine whether the element (and consitiuents) should be
	 * 		set to 'isEditable' for the user.
	 * @return the element
	 */
	public DataElementContainerDTO getElementAndConstituents(final String lsid, final boolean retrieveAllConstituents, final boolean eraseDBReferences, UserPrivilegeMatrix upm) throws HibernateException, NoSuchElementException, RelationshipReconstitutionException, LSIDException{

		DataElementContainerDTO returnElement = null;

		ArrayList<org.psygrid.data.model.hibernate.Element> elemList = new ArrayList<org.psygrid.data.model.hibernate.Element>();
		Session session = getHibernateTemplate().getSessionFactory().openSession();

		if(session == null) {
			session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		}

		session.setFlushMode(org.hibernate.FlushMode.NEVER);

		org.psygrid.data.model.hibernate.DataElementContainer elemContainer = ElementUtility.getElementAndSetIsEditable(lsid, session, upm);

		if(elemContainer == null){
			throw new NoSuchElementException("No element found for lsid " + lsid);
		}

		org.psygrid.data.model.hibernate.LSID theLSID = elemContainer.getElementLSIDObject();

		String instanceNamespace = theLSID.getNamespaceId() + "+Instance";
		elemContainer.setInstanceLSID(org.psygrid.data.model.hibernate.LSID.valueOf(theLSID.getAuthorityId(), instanceNamespace, theLSID.getObjectId(), theLSID.getRevisionId(), true));
		elemContainer.getInstanceLSID().setObjectId(null);

		if(retrieveAllConstituents)
			elemContainer.setIsEditable(true);


		if(retrieveAllConstituents){
			List<org.psygrid.data.model.hibernate.DataElementContainer> elements = new ArrayList<org.psygrid.data.model.hibernate.DataElementContainer>();
			List<org.psygrid.data.model.hibernate.ElementRelationship> elemRelationships = elemContainer.getElementRelationships();
			List<org.psygrid.data.model.hibernate.DataElementContainer> expandedElements = new ArrayList<org.psygrid.data.model.hibernate.DataElementContainer>();

			for(org.psygrid.data.model.hibernate.ElementRelationship relationship:elemRelationships){



				if(relationship instanceof OptionDependentElementRelationship){
					//The option entry will already be in the 'elements' array - so pluck it out of there.
					//Also, the relatedElement will already be in the array - so get this from there as well.

					OptionDependentElementRelationship opDepRel = (OptionDependentElementRelationship) relationship;

					org.psygrid.data.model.hibernate.DataElementContainer optionEntryContainer = null, controlledElementContainer = null;

					boolean optionFound = false, controlledElemFound = false;
					for(org.psygrid.data.model.hibernate.DataElementContainer existingElement: elements){

						if(optionFound && controlledElemFound){
							break;
						}else{

							if(!optionFound){
								if(existingElement.getElementLSID().equals(opDepRel.getOptionElementLSID())){
									optionEntryContainer = existingElement;
									optionFound = true;
								}
							}

							if(!controlledElemFound){
								if(existingElement.getElementLSID().equals(opDepRel.getRelatedElementLSID())){
									controlledElementContainer = existingElement;
									controlledElemFound = true;
								}
							}
						}


					}

					if(optionFound && controlledElemFound){

						opDepRel.reconstituteNativeRelationship(optionEntryContainer, controlledElementContainer);


					}else{
						//Should throw an exception.
					}



				}else{
					org.psygrid.data.model.hibernate.DataElementContainer relatedElementContainer = ElementUtility.getElementAndSetIsEditable(relationship.getRelatedElementLSID(), session, upm);

					if(relatedElementContainer == null){
						throw new NoSuchElementException("No element found for lsid " + relationship.getRelatedElementLSID());
					}

					theLSID = relatedElementContainer.getElementLSIDObject();

					instanceNamespace = theLSID.getNamespaceId() + "+Instance";
					relatedElementContainer.setInstanceLSID(org.psygrid.data.model.hibernate.LSID.valueOf(theLSID.getAuthorityId(), instanceNamespace, theLSID.getObjectId(), theLSID.getRevisionId(), false));
					relatedElementContainer.getInstanceLSID().setObjectId(null);
					relationship.reconstituteNativeRelationship(elemContainer, relatedElementContainer);

					if(retrieveAllConstituents)
						relatedElementContainer.setIsEditable(true);

					//Only reconstitute del relationships - leave out the repository relationships.
					//Do NOT add option entries to the list of elements to expand.
					if(relationship.getRelationshipType() != org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.repositoryRelationship){
						elements.add(relatedElementContainer);
					}
				}
			}

			expandedElements.add(elemContainer); //This element's immediate relationships have been fully expanded.

			do{
				List<org.psygrid.data.model.hibernate.DataElementContainer> theElements = new ArrayList<org.psygrid.data.model.hibernate.DataElementContainer>();
				theElements.addAll(elements); //elements at the next level
				elements.removeAll(theElements);//used to store all the elements at the next level.

				//cycle through the list of related elements
				for(org.psygrid.data.model.hibernate.DataElementContainer el:theElements){

					if(el == null || expandedElements.indexOf(el) != -1){
						//Don't process the element if its immediate relationships have already been expanded.
						continue;
					}

					//In here, we iterate through the element relationships for each element, load the related element,
					//and place this element into the 'elements' list
					List<org.psygrid.data.model.hibernate.ElementRelationship> elemRelationships2 = el.getElementRelationships();

					for(org.psygrid.data.model.hibernate.ElementRelationship relationship:elemRelationships2){

						org.psygrid.data.model.hibernate.DataElementContainer relatedElementContainer = ElementUtility.getElementAndSetIsEditable(relationship.getRelatedElementLSID(), session, upm);


						if(relatedElementContainer == null){
							throw new NoSuchElementException("No element found for lsid " + relationship.getRelatedElementLSID());
						}

						theLSID = relatedElementContainer.getElementLSIDObject();

						instanceNamespace = theLSID.getNamespaceId() + "+Instance";
						relatedElementContainer.setInstanceLSID(org.psygrid.data.model.hibernate.LSID.valueOf(theLSID.getAuthorityId(), instanceNamespace, theLSID.getObjectId(), theLSID.getRevisionId(), false));
						relatedElementContainer.getInstanceLSID().setObjectId(null);



						relationship.reconstituteNativeRelationship(el, relatedElementContainer);

						el.setIsEditable(true);

						//If the related element is not null and the related element possesses del relatioships, then
						//put it in the elements array so that we can expand it further.
						if(relationship.getRelationshipType() == org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.delPrimaryRelationship && relatedElementContainer != null && relatedElementContainer.getElementRelationships() != null){
							boolean elementNeedsExpansion = false;
							for(org.psygrid.data.model.hibernate.ElementRelationship eR:relatedElementContainer.getElementRelationships()){
								if(eR.getRelationshipType() == org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.delPrimaryRelationship ||
										eR.getRelationshipType() == org.psygrid.data.model.hibernate.ElementRelationship.RelationshipType.delSecondaryRelationship){
									elementNeedsExpansion = true;
									break;
								}
							}

							if(elementNeedsExpansion){
								elements.add(relatedElementContainer);
							}
						}		
					}   

					expandedElements.add(el);
				}
			}while(elements.size() > 0);
		}


		if(eraseDBReferences){
			org.psygrid.data.model.hibernate.Persistent.setPrepareElementForNewRevision(true);
		}

		returnElement =  elemContainer.toDTO();
		return returnElement;
	}

	/**
	 * Retrieves the element meta-data for the specified lsid.
	 * The element's change history is made available via a list of history items contained within the returned
	 * metadata object.
	 * 
	 * @param lsid - the element lsid for which element metadata is to be retrieved.
	 * @param retrieveFullHistory - if this is true then the element's entire history is returned. If 'false' then only the
	 * 		most recent meta-data object from each previous element is used to build up the element history, thereby omitting info about
	 * 		pending (and revisions to pending) items.
	 * @return - the most recent ElementMetaData element, populated with element history information.
	 */
	public org.psygrid.data.model.dto.ElementMetaDataDTO getMetaData(String lsid, boolean retrieveFullHistory) throws HibernateException, NoSuchElementException, RelationshipReconstitutionException, LSIDException {

		Session session = null;
		Transaction tx = null;
		ElementMetaData metaData = null;
		org.psygrid.data.model.hibernate.Element element = null;
		try{
			session = getHibernateTemplate().getSessionFactory().openSession();
			tx = session.beginTransaction();

			org.psygrid.data.model.hibernate.DataElementContainer elem = ElementUtility.getElement(lsid, session);
			ElementUtility.populateElementHistory(elem, session, retrieveFullHistory);

			metaData = elem.getLatestMetaData();

		}catch(HibernateException ex){
			if (tx!=null) tx.rollback();
			throw ex;
		}catch(OutOfMemoryError er){
			if (tx!=null) tx.rollback();
			throw er;
		}catch(LSIDException e){
			if (tx!=null) tx.rollback();
			throw e;
		}
		finally{
			if(session != null)
				session.close();
		}

		if(session != null && session.isOpen() && session.isConnected())
			session.disconnect();

		return metaData.toDTO();
	}


	private Class elementTypeToClass(final String elementType){
		Class obj = null;

		if(elementType.equals("Entry")){
			obj = org.psygrid.data.model.hibernate.Entry.class;
		}else if(elementType.equals("DataSet")){
			obj = org.psygrid.data.model.hibernate.DataSet.class;
		}else if(elementType.equals("Document")){
			obj = org.psygrid.data.model.hibernate.Document.class;
		}else if(elementType.equals("BasicEntry")){
			obj = org.psygrid.data.model.hibernate.BasicEntry.class;
		}else if(elementType.equals("BooleanEntry")){
			obj = org.psygrid.data.model.hibernate.BooleanEntry.class;
		}else if(elementType.equals("DateEntry")){
			obj = org.psygrid.data.model.hibernate.DateEntry.class;
		}else if(elementType.equals("DerivedEntry")){
			obj = org.psygrid.data.model.hibernate.DerivedEntry.class;
		}else if(elementType.equals("ExternalDerivedEntry")){
			obj = org.psygrid.data.model.hibernate.ExternalDerivedEntry.class;
		}else if(elementType.equals("IntegerEntry")){
			obj = org.psygrid.data.model.hibernate.IntegerEntry.class;
		}else if(elementType.equals("LongTextEntry")){
			obj = org.psygrid.data.model.hibernate.LongTextEntry.class;
		}else if(elementType.equals("NumericEntry")){
			obj = org.psygrid.data.model.hibernate.NumericEntry.class;
		}else if(elementType.equals("OptionEntry")){
			obj = org.psygrid.data.model.hibernate.OptionEntry.class;
		}else if(elementType.equals("TextEntry")){
			obj = org.psygrid.data.model.hibernate.TextEntry.class;
		}else if(elementType.equals("CompositeEntry")){
			obj = org.psygrid.data.model.hibernate.CompositeEntry.class;
		}else if(elementType.equals("NarrativeEntry")){
			obj = org.psygrid.data.model.hibernate.NarrativeEntry.class;
		}else if(elementType.equals("DateValidationRule")){
			obj = org.psygrid.data.model.hibernate.DateValidationRule.class;
		}else if(elementType.equals("IntegerValidationRule")){
			obj = org.psygrid.data.model.hibernate.IntegerValidationRule.class;
		}else if(elementType.equals("TextValidationRule")){
			obj = org.psygrid.data.model.hibernate.TextValidationRule.class;
		}else if(elementType.equals("NumericValidationRule")){
			obj = org.psygrid.data.model.hibernate.NumericValidationRule.class;
		}else if(elementType.equals("ValidationRule")){
			obj = org.psygrid.data.model.hibernate.ValidationRule.class;
		}

		return obj;
	}

	protected List<Long> getElementSearchSubset(List<String>docLSIDs, Session session) throws LSIDException{

		//if docFilterLSIDs is populated, we want to 
		//1) extract the element object id from the lsids
		//2) we want to retrieve all of the lsids from the primary element relationships
		//3) If authorityFilterLSIDS is not null, we want to retrieve ONLY the lsids that correspond to that authority
		List<Long> subElementsList = new ArrayList<Long>();
		Criteria crit = null;

		//Narrow by document, and perhaps by authority also
		List<Long> docIDList = new ArrayList<Long>();
		for(String docLSID: docLSIDs){
			docIDList.add(ElementUtility.getElementIdFromLSIDString(docLSID));
		}

		crit = session.createCriteria(org.psygrid.data.model.hibernate.ElementRelationship.class);
		crit.add(Restrictions.in("id", docIDList));

		SQLQuery query = session.createSQLQuery("select c_lsid from t_element_relationship " +
				"where c_relationship_id in (:docList)" +
		"and c_relationship_type = 'delPrimaryRelationship'");
		query.setParameterList("docList", docIDList);

		List<Object> resultList = query.list();

		//Now convert these from LSIDs to object ids.
		List<Long> elementIDList = new ArrayList<Long>();
		for(Object currentObj: resultList){
			String lsid = (String) currentObj;
			elementIDList.add(ElementUtility.getElementIdFromLSIDString(lsid));
		}

		return elementIDList;
	}

	public DocumentDTO[] getDocumentsSummaryInfo(final String authority){

		DocumentDTO[] results = null;

		Session session = getHibernateTemplate().getSessionFactory().openSession();

		SQLQuery query = session.createSQLQuery("select c_name, t_lsid.c_object_id, t_lsid_authority.c_authority_id, " +
				"t_lsid_namespace.c_namespace, t_lsid.c_revision_id from t_components " +
				"inner join t_documents on t_components.c_id = t_documents.c_id " +
				"inner join t_lsid on t_components.c_lsid_id = t_lsid.c_lsid_id " +
				"inner join t_lsid_authority on t_lsid.c_auth_id = t_lsid_authority.c_auth_id " +
				"inner join t_lsid_namespace on t_lsid.c_ns_id = t_lsid_namespace.c_ns_id"+
				" where t_lsid_authority.c_authority_id='"+authority+"' ");


		List<Object> resultList = query.list();

		results = new DocumentDTO[resultList.size()];
		int counter = 0;
		for(Object result: resultList){

			Object[] resultArray = (Object[])result;
			String name, objectId, authorityId, namespaceId, revision;
			name = (String)resultArray[0];
			objectId = (String)resultArray[1];
			authorityId = (String)resultArray[2];
			namespaceId = (String)resultArray[3];
			revision = (String)resultArray[4];

			DocumentDTO dtoDoc = new DocumentDTO();
			LSIDDTO docLSID = new LSIDDTO(new org.psygrid.data.model.dto.LSIDAuthorityDTO(authorityId), new org.psygrid.data.model.dto.LSIDNameSpaceDTO(namespaceId), objectId, revision);//LSIDAuthority authorityObj, LSIDNameSpace nsObj, String objectId, String revisionId

			if(new Integer(revision) > 0){
				name = name.concat("(Revision " + revision + ")");
			}

			dtoDoc.setName(name);
			dtoDoc.setLSID(docLSID);

			results[counter] = dtoDoc;
			counter++;
		}

		return results;
	}

	/**
	 * Conducts an element search as specified by the search criteria in the queryManager object passed in.
	 * return - the same query object, with the query results.
	 */
	public DELQueryObject sophisticatedGetElementByTypeAndName(DELQueryObject queryManager) throws HibernateException, NoSuchElementException, RelationshipReconstitutionException, LSIDException {
		Session session = getHibernateTemplate().getSessionFactory().openSession();

		if(queryManager.isNewQuery()) {

			List<Long> searchSubset = new ArrayList<Long>();

			if(queryManager.getDocFilterLSIDs().size() > 0){
				searchSubset = getElementSearchSubset(queryManager.getDocFilterLSIDs(), session);
			}

			final String elementType = queryManager.getElementType();
			final String elementName = queryManager.getSearchCriteria();
			final SearchType searchType = SearchType.valueOf(queryManager.getSearchType());

			session.setFlushMode(org.hibernate.FlushMode.NEVER); 

			String elementSearchString = null;

			if(searchType == SearchType.beginsWith){
				elementSearchString = elementName + "%";
			}else if(searchType == SearchType.contains){
				elementSearchString = "%" + elementName + "%";
			}else if(searchType == SearchType.endsWith){
				elementSearchString = "%" + elementName;
			}else if(searchType == SearchType.exactMatch){
				elementSearchString = elementName;
			}

			//If this element type is a validation rule, get the
			boolean isValidationRule = false;
			if(elementType.contains("ValidationRule")){
				isValidationRule = true;
			}

			Class elementClass = elementTypeToClass(elementType);

			Criteria crit = session.createCriteria(elementClass);
			crit.createAlias("LSID", "lsid");
			//Commented out the line below because validation rules do not have displayText!!
			//crit.add(Restrictions.or(Restrictions.ilike("name", elementSearchString), Restrictions.ilike("displayText", elementSearchString)));
			crit.add(Restrictions.ilike("name", elementSearchString));

			if(!retrieveNonRootElements){
				crit.add(Restrictions.eq("enumSubmissionContext", "ROOT"));
			}

			if(queryManager.getSearchLatestRevisionOnly()){
				crit.add(Restrictions.eq("headRevision", new Boolean(true)));
			}

			if(queryManager.getAuthorityFilterLSIDs().size() > 0){
				crit.createAlias("lsid.lsidAuthority", "authority"); 
				crit.add(Restrictions.in("authority.authorityID", queryManager.getAuthorityFilterLSIDs()));
			}

			if(queryManager.getStatusExclusions().size() > 0){
				crit.add(Restrictions.not(Restrictions.in("enumStatus", queryManager.getStatusExclusions())));

				//When pending documents are being returned, we only want to return those elements that are
				//at the root submission context. The rule is that when elements are approved, that their
				//submission context is changed to 'root'. By doing this, we can simply place a restriction
				//in this query to only return elements of the 'root' submission context.
			}

			if(searchSubset.size() > 0){
				crit.add(Restrictions.in("id", searchSubset));
			}

			crit.addOrder( Order.asc("name"));

			List<String> lsidStrings = populateQueryObjectFromQueryResults(queryManager, crit, isValidationRule);


			queryManager.setNewQuery(false);
			queryManager.setMatchingLSIDs(lsidStrings);
			queryManager.setTotalNumResults(lsidStrings.size() + queryManager.getReturnedElements().size());

		}else{
			//Which is bigger, the granularity, or the remaining number of elements to retrieve?
			int retrievalNumber = queryManager.getGranularity() <= queryManager.getMatchingLSIDs().size() ?  queryManager.getGranularity() : queryManager.getMatchingLSIDs().size();

			//Get *retrievalNumber* of matching lsids, and 
			String[] lsidArray = new String[retrievalNumber];
			for(int i = 0; i < retrievalNumber; i++){
				lsidArray[i] = queryManager.fetchusImmediatusThyneMatchingLSIDAndRemove();
			}

			List<DataElementContainerDTO> elements = getElementSummaries(lsidArray, session);

			for(DataElementContainerDTO can: elements){
				queryManager.addElement(can, false);
			}
		}

		return queryManager;
	}

	/**
	 * Populates the DELQueryObject passed in with the results of the query.
	 * @param obj - the query object to be populated with results.
	 * @param crit - provides access to the query Results (query must have already been specified and results
	 * 	must be ready to obtain by calling crit.list() 
	 * @param isValidationRule - determines which helper method to call in order to get the job done.
	 * @return - a list of results that the obj was NOT populated with because granularity was exceeded.
	 */
	private List<String> populateQueryObjectFromQueryResults(DELQueryObject obj, Criteria crit, boolean isValidationRule){

		if(isValidationRule){
			return populateQueryObjectWithValRules(obj, crit);
		}else{
			return populateQueryObjectWithElements(obj, crit);
		}
	}

	/**
	 * Populates the query object with 'Element' results.
	 * @param queryManager - the object to be populated with results.
	 * @param crit - the query, whose results must be ready to be obtained by simply calling the list() method.
	 * @return - a list of results that weren't added to the queryManager object because the granularity was exceeded.
	 */
	private List<String> populateQueryObjectWithElements(DELQueryObject queryManager, Criteria crit){

		List<org.psygrid.data.model.hibernate.Element> el = crit.list();

		List<String> lsidStrings = new ArrayList<String>(el.size());

		int counter = 1;
		for(org.psygrid.data.model.hibernate.Element obj: el){

			if(counter <= queryManager.getGranularity()){
				//Also, add elements to the query manager, up the the granularity.

				ElementDTO elem = obj.instantiateDTO();
				elem.setName(obj.getName());
				elem.setDescription(obj.getDescription());
				elem.setDisplayText(obj.getDisplayText());
				elem.setElementSubmissionContext(obj.getEnumSubmissionContext());
				org.psygrid.data.model.hibernate.LSID objLSID = obj.getLSID();
				elem.setLSID(new LSIDDTO(new org.psygrid.data.model.dto.LSIDAuthorityDTO(objLSID.getAuthorityId()), new org.psygrid.data.model.dto.LSIDNameSpaceDTO(objLSID.getNamespaceId()), objLSID.getObjectId(), objLSID.getRevisionId()));

				queryManager.addElement(new DataElementContainerDTO(elem), false);
				counter++;
			}else{
				lsidStrings.add(obj.getLSID().toString());
			} 	

		}

		return lsidStrings;
	}

	private List<String> populateQueryObjectWithValRules(DELQueryObject queryManager, Criteria crit){

		List<org.psygrid.data.model.hibernate.ValidationRule> el = crit.list();

		List<String> lsidStrings = new ArrayList<String>(el.size());

		int counter = 1;
		for(org.psygrid.data.model.hibernate.ValidationRule obj: el){

			if(counter <= queryManager.getGranularity()){
				//Also, add elements to the query manager, up the the granularity.

				ValidationRuleDTO rule = obj.instantiateDTO();
				rule.setName(obj.getName());
				rule.setDescription(obj.getDescription());
				rule.setSubmisssionContext(obj.getEnumSubmissionContext());

				org.psygrid.data.model.hibernate.LSID objLSID = obj.getLSID();
				rule.setLSID(new LSIDDTO(new org.psygrid.data.model.dto.LSIDAuthorityDTO(objLSID.getAuthorityId()), new org.psygrid.data.model.dto.LSIDNameSpaceDTO(objLSID.getNamespaceId()), objLSID.getObjectId(), objLSID.getRevisionId()));

				queryManager.addElement(new DataElementContainerDTO(rule), false);
				counter++;
			}else{
				lsidStrings.add(obj.getLSID().toString());
			} 	

		}

		return lsidStrings;

	}

	/**
	 * Retrieves summary data for elements.
	 * @param lsid - an array of summary data for which summary data is required.
	 * @param session - the current hibernate session
	 * @return - a list of DataElementContainer objects that are minimally populated with just the
	 * 		summary info.
	 * @throws HibernateException
	 * @throws NoSuchElementException
	 * @throws RelationshipReconstitutionException
	 * @throws LSIDException
	 */
	private List<DataElementContainerDTO> getElementSummaries(final String[] lsid, Session session) throws HibernateException, NoSuchElementException, RelationshipReconstitutionException, LSIDException{
		List<Long> elementIds = new ArrayList<Long>(0);
		List<Long> valRuleIds = new ArrayList<Long>(0);

		HashMap<Long, String> idToClassTypeMap = new HashMap<Long, String>(); //The key is the object's id, and the value is the class type.
		HashMap<Long, String> idToLSIDMap = new HashMap<Long, String>(); //The key is the object's id, and the value is its corresponding lsid string.

		for(int i = 0; i < lsid.length; i++){
			Long objId = ElementUtility.getElementIdFromLSIDString(lsid[i]);

			DataElementContainer.ElementType elemType = ElementUtility.getElementType(lsid[i]);

			if(elemType == DataElementContainer.ElementType.primary){
				elementIds.add(objId);
			}else if(elemType == DataElementContainer.ElementType.rule){
				valRuleIds.add(objId);
			}

			org.psygrid.data.model.hibernate.LSID lsidObj = org.psygrid.data.model.hibernate.LSID.valueOf(lsid[i]);

			String hibernateNameSpace = lsidObj.getLsidNameSpace().getNameSpace();
			String dtoNameSpace = hibernateNameSpace.replaceFirst("hibernate", "dto");

			idToClassTypeMap.put(objId, dtoNameSpace);
			idToLSIDMap.put(objId, lsid[i]);
		}

		//This is fine for all objects derived from element, but what about Validation Rules?
		//Validation rule does not have displayText, but it has everything else.

		List<DataElementContainerDTO> elementsArray = new ArrayList<DataElementContainerDTO>();

		if(elementIds.size() > 0){
			Query elementQuery = session.createQuery("select elem.id, elem.name, elem.displayText " +
					"from Element elem " +
			"where elem.id in (:groups)")
			.setParameterList("groups", elementIds);

			List queryResults = elementQuery.list();

			for(Object o: queryResults) {

				Object[] itemInfo = (Object[])o;

				//Create the element
				//First, get the id from the results.
				Long id = (Long)itemInfo[0];
				String theLSID = idToLSIDMap.get(id);
				String classType = idToClassTypeMap.get(id);
				String elemName = (String)itemInfo[1];
				String elemDisplayText = (String)itemInfo[2];

				try {
					Class c =Class.forName(classType);
					ElementDTO theElement = (ElementDTO)c.newInstance();
					theElement.setName(elemName);
					theElement.setDisplayText(elemDisplayText);
					org.psygrid.data.model.hibernate.LSID objLSID = org.psygrid.data.model.hibernate.LSID.valueOf(theLSID);
					theElement.setLSID(new LSIDDTO(new org.psygrid.data.model.dto.LSIDAuthorityDTO(objLSID.getAuthorityId()), new org.psygrid.data.model.dto.LSIDNameSpaceDTO(objLSID.getNamespaceId()), objLSID.getObjectId(), objLSID.getRevisionId()));
					elementsArray.add(new DataElementContainerDTO(theElement));	
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


		if(valRuleIds.size() > 0){
			Query validationRuleQuery = session.createQuery("select vr.id, vr.name " +
					"from ValidationRule vr " +
			"where vr.id in (:groups)")
			.setParameterList("groups", valRuleIds);

			List queryResults = validationRuleQuery.list();

			for(Object o: queryResults) {

				Object[] itemInfo = (Object[])o;

				//Create the element
				//First, get the id from the results.
				Long id = (Long)itemInfo[0];
				String theLSID = idToLSIDMap.get(id);
				String classType = idToClassTypeMap.get(id);
				String elemName = (String)itemInfo[1];

				try {
					Class c =Class.forName(classType);
					ValidationRuleDTO theElement = (ValidationRuleDTO)c.newInstance();
					theElement.setName(elemName);
					org.psygrid.data.model.hibernate.LSID objLSID = org.psygrid.data.model.hibernate.LSID.valueOf(theLSID);
					theElement.setLSID(new LSIDDTO(new org.psygrid.data.model.dto.LSIDAuthorityDTO(objLSID.getAuthorityId()), new org.psygrid.data.model.dto.LSIDNameSpaceDTO(objLSID.getNamespaceId()), objLSID.getObjectId(), objLSID.getRevisionId()));
					elementsArray.add(new DataElementContainerDTO(theElement));	
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}


		return elementsArray;
	}

	/**
	 * @deprecated - replaced by 'sophisticatedGetElementByTypeAndName()'
	 */
	@Deprecated
	public ElementDTO[] getElementByTypeAndName(final String elementType, final String elementName, final SearchType searchType) {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				//Flush mode set to NEVER because although the hibernate object must be modified,
				//These mods are for export data only, and are NOT to be reflected back to the database.
				//Oh, but 'NEVER' is now deprecated. So I have to use 'MANUAL'.
				session.setFlushMode(org.hibernate.FlushMode.NEVER); 

				String elementSearchString = null;

				if(searchType == SearchType.beginsWith){
					elementSearchString = elementName + "%";
				}else if(searchType == SearchType.contains){
					elementSearchString = "%" + elementName + "%";
				}else if(searchType == SearchType.endsWith){
					elementSearchString = "%" + elementName;
				}else if(searchType == SearchType.exactMatch){
					elementSearchString = elementName;
				}


				Class elementClass = elementTypeToClass(elementType);

				Criteria crit = session.createCriteria(elementClass);
				crit.add(Restrictions.or(Restrictions.ilike("name", elementSearchString), Restrictions.ilike("displayText", elementSearchString)));
				crit.setMaxResults(50);
				List<org.psygrid.data.model.hibernate.Element> el = crit.list();


				//We want to remove any items that have multiple revisions
				//First, go through and pick out the items with revision numbers higher than zero.
				//Then, go through and weed out all the previous revisions.
				List<org.psygrid.data.model.hibernate.Element> revisedElemList = new ArrayList<org.psygrid.data.model.hibernate.Element>();

				for(org.psygrid.data.model.hibernate.Element elem: el){
					String revisionValueStr = elem.getLSID().getRevisionId();
					int revisionValueInt = Integer.valueOf(revisionValueStr);

					if(revisionValueInt > 0){

						org.psygrid.data.model.hibernate.LSID lsidToRemove = elem.getLSID();
						int numRevisions = Integer.valueOf(lsidToRemove.getRevisionId());

						boolean addElem = false;
						int elemListSize = revisedElemList.size();
						for(int k = elemListSize-1; k >= 0; k--){
							org.psygrid.data.model.hibernate.LSID lsid = el.get(k).getLSID();
							if(lsid.getAuthorityId().equals(lsidToRemove.getAuthorityId()) &&
									lsid.getNamespaceId().equals(lsidToRemove.getNamespaceId()) &&
									lsid.getObjectId().equals(lsidToRemove.getObjectId()) &&
									Integer.valueOf(lsid.getRevisionId()) < numRevisions){

								revisedElemList.remove(k);  
								addElem = true;
							}
						}

						if(addElem || revisedElemList.size() == 0 || revisionValueInt ==1)
							revisedElemList.add(elem);
					}
				}

				for(int i = 0; i < revisedElemList.size(); i++){
					org.psygrid.data.model.hibernate.LSID lsidToRemove = revisedElemList.get(i).getLSID();
					int numRevisions = Integer.valueOf(lsidToRemove.getRevisionId());

					int size = el.size();
					for(int j = size-1; j >= 0; j--){
						org.psygrid.data.model.hibernate.LSID lsid = el.get(j).getLSID();
						if(lsid.getAuthorityId().equals(lsidToRemove.getAuthorityId()) &&
								lsid.getNamespaceId().equals(lsidToRemove.getNamespaceId()) &&
								lsid.getObjectId().equals(lsidToRemove.getObjectId())){

							el.remove(j);
						}
					}
				}

				//Now we can put them all into one list.
				for(int i = 0; i < revisedElemList.size(); i++){
					el.add(revisedElemList.get(i));
				}

				if(null != el){
					ElementDTO[] dtoElements = new ElementDTO[el.size() >= 50? 50 : el.size()];
					for(int i = 0; i < (el.size() >= 50? 50 : el.size()); i++){
						dtoElements[i] = el.get(i).toDTO(); 
						dtoElements[i].setIsEditable(false);
					}

					return dtoElements;
				}

				return new ElementDTO[0];

			}

		};

		ElementDTO[] elements = (ElementDTO[])getHibernateTemplate().execute(callback);
		return elements;
	}

	private void incrementRevisionId(org.psygrid.data.model.hibernate.DataElementContainer element) throws ElementRevisionException{

		org.psygrid.data.model.hibernate.LSID lsid = element.getElementLSIDObject();
		String lastRevisionLSID = lsid.toString();
		Integer revisionNumber = Integer.parseInt(lsid.getRevisionId());
		String latestLSID = this.getCurrentRevisionLevel(lastRevisionLSID, false);
		Integer currentDBRevisionNo = null;
		try {
			org.psygrid.data.model.hibernate.LSID latestLSIDObj = org.psygrid.data.model.hibernate.LSID.valueOf(latestLSID);
			currentDBRevisionNo = Integer.parseInt(latestLSIDObj.getRevisionId());
		} catch (LSIDException e) {
			//Can safely ignore this because the lsid was retrieved from the db and is therefore guaranteed.
		}


		if(revisionNumber != currentDBRevisionNo){
			//Don't revise - it has already been modified!
			throw new ElementRevisionException("Cannot revise element: " + lastRevisionLSID + "." +
					"\rThe item is already at revision level " + currentDBRevisionNo.toString() + 
			" in the element library." );
		}

		revisionNumber += 1;
		lsid.setRevisionId(revisionNumber.toString());

		lsid.removeDBSpecificsFromObjectId();
	}

	/**
	 * Revises a single element. Works on the premise that all sub-elements have already been revised and have had their 
	 * LSIDs updated.
	 * @param repObj - the object being revised
	 * @param isRoot - whether this is the root object (required for setting the submission context correctly)
	 * @param adminInfo - the info (provided mostly client-side) concerning the revision.
	 * @param session - the hibernate session object.
	 * @param opDepRelationships - option dependency information. If the list is not null and the revision element is a 
	 * 								document, these relationships are added to the document before it is saved.
	 * @throws ElementRevisionException
	 * @throws UnknownNativeRelationship
	 * @throws HibernateException
	 * @throws LSIDException
	 * @throws FailedTestException
	 * @throws ElementAuthorityNotRecognizedException
	 */
	private void reviseRepositoryObject(RepositoryModel.RepositoryObject repObj, boolean isRoot, AdminInfo adminInfo, Session session, List<OptionDependencyRelationship> opDepRelationships) throws ElementRevisionException, UnknownNativeRelationship, HibernateException, LSIDException, FailedTestException, ElementAuthorityNotRecognizedException{
		ArrayList<org.psygrid.data.model.hibernate.Persistent> elemList = new ArrayList<org.psygrid.data.model.hibernate.Persistent>();
		String previousRevisionLSID = null;
		org.psygrid.data.model.hibernate.DataElementContainer elemContainer = ElementUtility.getElementContainerForRepositoryObject(repObj);

		previousRevisionLSID = elemContainer.getElementLSID();
		incrementRevisionId(elemContainer);

		ImportHelper.translateNativeElementCollectionToElementRelationships(elemContainer, false, authorityList);
		if(opDepRelationships != null && repObj.getNodeType() == RepositoryObjectType.Document){
			ImportHelper.buildOpDepElementRelationshipsInDocument((org.psygrid.data.model.hibernate.Document)repObj.getRepositoryObject(), opDepRelationships);
		}
		
		elemContainer.setMetaData(new ArrayList<ElementMetaData>()); //We want to generate new meta-data for this element.
		generateMetaData(elemContainer, adminInfo, false);

		saveElements(elemContainer, isRoot, false, elemList, session, false);
		updateElementLinks(previousRevisionLSID, elemContainer.getElementLSID(), session);
	}

	/**
	 * This method is used only when there is a new element embedded within a submitted revision.
	 * The important assumptions are: that the submission context is subordinate (because it is being
	 * submitted as part of a revision) and that the 'save' is non-recursive - i.e. that no sub-elements
	 * need to be saved.
	 * @param repObj - the object to be saved
	 * @param adminInfo - the adminintrative info detailing the who, what, when, why contextual info.
	 * @param authority - the authority to which the newly-saved element belongs.
	 * @param session - the hibernate session object
	 * @throws UnknownNativeRelationship
	 * @throws ElementRevisionException
	 * @throws UnsupportedEncodingException
	 * @throws LSIDException
	 * @throws FailedTestException 
	 * @throws ElementAuthorityNotRecognizedException 
	 */
	private void saveRepositoryObject(RepositoryModel.RepositoryObject repObj, AdminInfo adminInfo, String authority, Session session) throws UnknownNativeRelationship, ElementRevisionException, UnsupportedEncodingException, LSIDException, FailedTestException, ElementAuthorityNotRecognizedException{

		org.psygrid.data.model.hibernate.DataElementContainer elemContainer = null;

		RepositoryObjectType type = repObj.getNodeType();
		switch(type){
		case ValidationRule:
		{
			org.psygrid.data.model.hibernate.ValidationRule valRule = (org.psygrid.data.model.hibernate.ValidationRule)repObj.getRepositoryObject();
			elemContainer = new org.psygrid.data.model.hibernate.DataElementContainer(valRule);
		}
		break;
		case Document:
		case SingleVariableTest:
		case MultipleVariableTest:
		case Entry:
		{
			org.psygrid.data.model.hibernate.Element element = (org.psygrid.data.model.hibernate.Element)repObj.getRepositoryObject();
			elemContainer = new org.psygrid.data.model.hibernate.DataElementContainer(element);
		}
		break;
		}


		ImportHelper.translateNativeElementCollectionToElementRelationships(elemContainer, false, authorityList);
		generateLSIDTemplates(elemContainer, authority);

		generateMetaData(elemContainer, adminInfo, false);

		elemContainer.setInstanceLSID(null); //Do not save instance lsids into the element library!

		setSavedElementStatus(elemContainer, false, false);

		boolean isImport = false;
		preprocessElement(elemContainer, session, isImport);

		session.saveOrUpdate(elemContainer.getElement()); //Unless we do this, we don't get the hibernate id!

		elemContainer.getElementLSIDObject().setObjectId(elemContainer.getElementLSIDObject().getObjectId().concat("-" + elemContainer.getId().toString()));
		session.saveOrUpdate(elemContainer.getElementLSIDObject());
		elemContainer.getElementLSIDObject().setObjectId(elemContainer.getElementLSIDObject().getObjectId().concat("-" + elemContainer.getElementLSIDObject().getId().toString()));
		elemContainer.getLatestMetaData().setElementLSID(elemContainer.getElementLSID());
		session.saveOrUpdate(elemContainer.getLatestMetaData());

	}

	/**
	 * This method revises an element. A revision consitiutes a new row in the element database and a new lsid identifier.
	 * The lsid will be updated to refer to the lsid string of its predecessor.
	 * 
	 * The method allows for the possibility that subordinate elements may have also been revised, and/or that
	 * some of the sub-elements may be new; in these cases, revised sub-elements will also be revised and new sub-elements
	 * will be saved.
	 * 
	 * The method identifies subordinate element revision candidates via the 'isRevisionCandidate' flag, which must have
	 * been set already by the client application.
	 * @throws FailedTestException 
	 * @throws ElementAuthorityNotRecognizedException 
	 * @throws RepositoryModelException 
	 * 
	 * @DataElementContainer elem - the element to revise.
	 * @AdminInfo info - the who, what, when, why info to be used to create the associated element meta-data.
	 * @String saml - the saml assertion identifying a user's privileges.
	 * 
	 */
	public String reviseElement(DataElementContainerDTO elem, AdminInfo adminInfo, String saml) throws UnknownNativeRelationship, LSIDException, ElementRevisionException, UnsupportedEncodingException, FailedTestException, ElementAuthorityNotRecognizedException, RepositoryModelException {


		org.psygrid.data.model.dto.PersistentDTO underlyingElement = 
			DataElementContainer.ElementType.valueOf(elem.getEnumElementType()) == DataElementContainer.ElementType.primary ? elem.getPrimaryElement() :
				elem.getRuleElement();

			PersistentDTO.setDelContext(true);

			org.psygrid.data.model.hibernate.DataElementContainer hibElem = elem.toHibernate();

			//Get the authority of the submitted element. Use this as the authority for any new sub-elements.
			String authority = hibElem.getElementLSIDObject().getAuthorityId();

			//Need to consider what will happen if revised sub-elements are not the of the same authority as the
			//root element. In that case, it will be necessary to know if whether the user is an author in each authority
			//containing a revised element.

			//But for now, revised sub-elements of authorities different from the root will automatically be disallowed from being
			//revised, and therefore the entire revision will get rejected.


			RepositoryModel elemModel = new RepositoryModel(hibElem.getElement(), NodeIdentificationMethod.Binary, true);
			
			
			boolean revisedElementIsADocument = false;
			//If this is a document, then it is necessary to get all of the option entry element relationsips immediately.
			List<RepositoryObject> nodeList = elemModel.getNodesAtSpecifiedDepth(0, NodeType.All);
			if(nodeList.size() != 1){
				throw new RepositoryModelException("RepositoryModel Node retrieval - " + new Integer(nodeList.size()).toString() + " node(s) detected, not 1.");
			}else{
				//Find out if the root node is a document. If it is, then it is necessary to capture all of the option dependent element relationships.
				if(nodeList.get(0).getNodeType() == RepositoryObjectType.Document){
					revisedElementIsADocument = true;
				}
			}
			
			

			//Find the max depth level at which there is at least one new element or one revision candidate.
			int maxChangeDepth = elemModel.getMaxChangeDepth();

			//Get the revision candidates, new elements at the max depth.
			//Propagate the revision candidate flag upwards.

			List<RepositoryModel.RepositoryObject> revisionCandidates = elemModel.getNodesAtSpecifiedDepth(maxChangeDepth, NodeType.RevisionCandidate);
			for(RepositoryModel.RepositoryObject repObj: revisionCandidates){
				repObj.propagateRevisionCandidateFlagToRoot();
			}

			List<RepositoryModel.RepositoryObject> newElements = elemModel.getNodesAtSpecifiedDepth(maxChangeDepth, NodeType.New);
			for(RepositoryModel.RepositoryObject repObj: newElements){
				repObj.propagateRevisionCandidateFlagToRoot();
			}

			Session session = null; 
			Transaction tx = null;
			try{

				session = getHibernateTemplate().getSessionFactory().openSession();
				tx = session.beginTransaction();
				
				List<OptionDependencyRelationship> opDepRelationships = null;
				
				
				if(revisedElementIsADocument){
					RepositoryObject document = elemModel.getNodesAtSpecifiedDepth(0, NodeType.All).get(0);
					opDepRelationships = ImportHelper.getOptionDependencyRelationships((org.psygrid.data.model.hibernate.Document)document.getRepositoryObject());
				}
				
				
				for(int i = maxChangeDepth; i >= 0; i--){	
					revisionCandidates = elemModel.getNodesAtSpecifiedDepth(i, NodeType.RevisionCandidate);
					newElements = elemModel.getNodesAtSpecifiedDepth(i, NodeType.New);

					for(RepositoryModel.RepositoryObject repObj: newElements){
						if(!repObj.getIsElementCandidate() || repObj.getIsProcessed()){
							continue;
						}

						saveRepositoryObject(repObj, adminInfo, authority, session);
						elemModel.setIsProcessed(repObj);
					}

					//Deal with the revision candidates first - increment the lsids.
					for(RepositoryModel.RepositoryObject repObj: revisionCandidates){
						if(!repObj.getIsElementCandidate() || repObj.getIsProcessed()){
							continue;
						}
						//If maxChangeDepth is zero, then this is the root submission,
						//so pass in 'true' for 'rootElement'.
						boolean isRoot = i == 0;

						//if this is not the root and the revised element is from an authority different
						//to that of the root, throw an exception!!
						//In subsequent revisions, allow sub-elements from different revisions to be revised,
						//as per a fully populated user privilege matrix for every authority represented in the
						//entire submission.

						if(!isRoot) {
							org.psygrid.data.model.hibernate.DataElementContainer elemContainer = ElementUtility.getElementContainerForRepositoryObject(repObj);

							String subElementAuthority = elemContainer.getElementLSIDObject().getAuthorityId();

							if(!subElementAuthority.equals(authority)) {
								throw new ElementRevisionException("The submitted revision contains revised sub-elements " +
								"from a different authortiy than that of the root element, which is not permitted.");
							}
						}

						reviseRepositoryObject(repObj, isRoot, adminInfo, session, opDepRelationships);
						elemModel.setIsProcessed(repObj);
					}
				}
				tx.commit();
			}catch(HibernateException ex){
				if (tx!=null) tx.rollback();
				throw ex;
			}catch(OutOfMemoryError er){
				if (tx!=null) tx.rollback();
				throw er;
			} catch (LSIDException e) {
				if (tx!=null) tx.rollback();
				throw e;
			}finally{
				if(session != null)
					session.close();
			}

			if(session != null && session.isOpen() && session.isConnected())
				session.disconnect();
			////////////////////////////////////////////////////////////////////////////

			return hibElem.getElementLSID();
	}

	/**
	 * Responsible for maintaining the lsid refences of previous and next revisions.
	 * Updates the lsid object identified by lastRevisionLSID to refer to currentRevisionLSID as the next revision
	 * in the chain. Updates currentRevisionLSID to refer to lastRevisionLSID as the previous revision.
	 * 
	 * Also ensures that the old element's 'isHeadRevision' flag is set to false.
	 * 
	 * @param lastRevisionLSID - the penultimate revision in the revision chain.
	 * @param currentRevisionLSID - the 'head revision' in the revision chain.
	 * @param session - hibernate session.
	 * @throws HibernateException
	 * @throws LSIDException
	 */
	private void updateElementLinks(String lastRevisionLSID, String currentRevisionLSID, Session session) throws HibernateException, LSIDException{

		org.psygrid.data.model.hibernate.DataElementContainer oldElement = ElementUtility.getElement(lastRevisionLSID, session);

		DataElementContainer.ElementType elemType = oldElement.getElementType();

		switch(elemType){
		case primary:
			org.psygrid.data.model.hibernate.Element oldElem = (org.psygrid.data.model.hibernate.Element)oldElement.getElement();
			oldElem.setHeadRevision(false);
			break;
		case rule:
			org.psygrid.data.model.hibernate.ValidationRule oldValRule = (org.psygrid.data.model.hibernate.ValidationRule)oldElement.getElement();
			oldValRule.setHeadRevision(false);
			break;
		}

		oldElement.getElementLSIDObject().setNextRevision(currentRevisionLSID);
		session.saveOrUpdate(oldElement.getElement());


		org.psygrid.data.model.hibernate.DataElementContainer newElement = ElementUtility.getElement(currentRevisionLSID, session);

		newElement.getElementLSIDObject().setPreviousRevision(lastRevisionLSID);
		session.saveOrUpdate(newElement.getElementLSIDObject());
	}


	public org.psygrid.data.model.hibernate.LSIDAuthority[] getLSIDAuthorityList() {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){


				List<org.psygrid.data.model.hibernate.LSIDAuthority> authList = (List<org.psygrid.data.model.hibernate.LSIDAuthority>)session.createQuery("from LSIDAuthority")
				.list();
				return authList;
			}

		};



		List<org.psygrid.data.model.hibernate.LSIDAuthority> authList = (List<org.psygrid.data.model.hibernate.LSIDAuthority>)getHibernateTemplate().execute(callback);
		org.psygrid.data.model.hibernate.LSIDAuthority[] authorityArray = new org.psygrid.data.model.hibernate.LSIDAuthority[authList.size()];
		for(int i = 0; i < authList.size(); i++) {
			authorityArray[i] = authList.get(i);
		}

		return authorityArray;
	}


	public void insertLSIDAuthority(String lsidAuthority) throws LSIDException {
		Session session = null; 
		Transaction tx = null;
		try{

			if(!ElementUtility.authorityIsInList(lsidAuthority, this.authorityList)){
				session = getHibernateTemplate().getSessionFactory().openSession();
				tx = session.beginTransaction();

				LSIDAuthority authority = new LSIDAuthority(lsidAuthority);
				session.save(authority);

				tx.commit();
				authorityList.add(authority);
			}

		}catch(HibernateException ex){
			if (tx!=null) tx.rollback();
			throw ex;
		}catch(OutOfMemoryError er){
			if (tx!=null) tx.rollback();
			throw er;
		} finally{
			if(session != null)
				session.close();
		}

		if(session != null && session.isOpen() && session.isConnected())
			session.disconnect();
		////////////////////////////////////////////////////////////////////////////
	}

	private void changeElemStatus(DataElementStatus newStatus, org.psygrid.data.model.hibernate.DataElementContainer elemContainer, AdminInfo info, Session session) throws LSIDException, ElementStatusChangeException{

		//TODO: The following line of code will assumes that approval is the only state to which an element can be changed.
		info.setActionTaken(DataElementAction.APPROVE.toString());
		info.setDescription("APPROVE");

		List<String> alreadyChangedList = new ArrayList<String>();

		if(!elemContainer.getHeadRevision()){
			throw new ElementStatusChangeException("The element named " + elemContainer.getElementName() +
			" is not the head-revision element and therefore its status cannot be changed.");
		}

		elemContainer.changeElementStatus(newStatus);
		generateMetaData(elemContainer, info, false);
		elemContainer.getLatestMetaData().setElementLSID(elemContainer.getElementLSID());
		session.update(elemContainer.getElement());
		alreadyChangedList.add(elemContainer.getElementLSIDObject().toString());

		List<org.psygrid.data.model.hibernate.ElementRelationship> elemRelationships = elemContainer.getElementRelationships();
		for(org.psygrid.data.model.hibernate.ElementRelationship relShip: elemRelationships){
			if(relShip.getRelationshipType() == RelationshipType.delPrimaryRelationship || 
					relShip.getRelationshipType() == RelationshipType.delSecondaryRelationship){

				changeElemStatusInner(newStatus, relShip.getRelatedElementLSID(), info, session, alreadyChangedList);

			}
		}
	}

	private boolean listContainsLSID(List<String> lsidsList, String lsid){
		boolean listContainsLSID = false;
		for(String str: lsidsList){
			if(str.equals(lsid)){
				listContainsLSID = true;
				break;
			}
		}

		return listContainsLSID;
	}

	private void changeElemStatusInner(DataElementStatus newStatus, String lsid, AdminInfo info, Session session, List<String> alreadyChangedList) throws LSIDException, ElementStatusChangeException{

		//It is important to ensure that the lsid represents a pending head-revision element. If not, don't update its status.
		org.psygrid.data.model.hibernate.DataElementContainer elemContainer = ElementUtility.getElement(lsid, session);

		if(!elemContainer.getHeadRevision()){
			throw new ElementStatusChangeException("The element named " + elemContainer.getElementName() +
			" is not the head-revision element and therefore its status cannot be changed.");
		}

		DataElementStatus elemStatus = elemContainer.getStatus();

		//I have place a constraint upon chaning element status whic is that when an element is first submitted and is 
		//set to 'pending', the element has a 'submission context' of 'root'.
		//The constraint is that even though there may be many constituent elements also set to 'pending', that they cannot
		//be 'approved' individually. 
		//Only the 'root' element can be 'approved', and this approval is then granted to all constituents at the same time.
		//A fortunate consequence of this is that, if an element is encountered that is not at 'pending', then the reason
		//for this is because when that particular constituent element must already exist in the element database.
		//Since we know this, there is no need to 'drill down' into any element that is not set at pending.

		//Do we need to 'drill down' into an element if it is in the 'alreadyChanged' List? No. If it is in this list then
		//its constituent element relationships have already been expanded elsewhere - no need to do this twice and possibly
		//get into an endless loop.

		if(elemStatus == DataElementStatus.PENDING &&  !listContainsLSID(alreadyChangedList, elemContainer.getElementLSID())){
			//update the element's status
			elemContainer.changeElementStatus(newStatus);
			generateMetaData(elemContainer, info, false);
			elemContainer.getLatestMetaData().setElementLSID(elemContainer.getElementLSID());
			session.update(elemContainer.getElement());
			alreadyChangedList.add(elemContainer.getElementLSIDObject().toString());

			//Change the submission context to 'root' because this only 'root' elements are returned from queries
			//due to restrictions placed on pending elements.
			if(newStatus == DataElementStatus.APPROVED){
				elemContainer.setSubmissionContext(ElementSubmissionContext.ROOT);
			}

			List<org.psygrid.data.model.hibernate.ElementRelationship> elemRelationships = elemContainer.getElementRelationships();
			for(org.psygrid.data.model.hibernate.ElementRelationship relShip: elemRelationships){
				if(relShip.getRelationshipType() == RelationshipType.delPrimaryRelationship || 
						relShip.getRelationshipType() == RelationshipType.delSecondaryRelationship){

					changeElemStatusInner(newStatus, relShip.getRelatedElementLSID(), info, session, alreadyChangedList);

				}
			}

		}else{
			//Do nothing either the element is NOT pending, or has already been dealt with. 
			return;
		}
	}


	public void modifyElementStatus(DataElementStatus newStatus, String lsid, AdminInfo adminInfo) throws LSIDException, ElementStatusChangeException {

		Session session = null;
		Transaction tx = null;
		try{
			session = getHibernateTemplate().getSessionFactory().openSession();
			tx = session.beginTransaction();
			//....

			org.psygrid.data.model.hibernate.DataElementContainer elemContainer = ElementUtility.getElement(lsid, session);

			Class elementClass = elemContainer.getElementClass();

			ElementSubmissionContext submissionContext = null;

			if(org.psygrid.data.model.hibernate.Element.class.isAssignableFrom(elementClass)){
				submissionContext = ((org.psygrid.data.model.hibernate.Element)elemContainer.getElement()).getSubmissionContext();

			}else{
				submissionContext = ((org.psygrid.data.model.hibernate.ValidationRule)elemContainer.getElement()).getSubmissionContext();
			}

			if(submissionContext != ElementSubmissionContext.ROOT){
				//TODO:DEL This should throw an exception probably.
				return;
			}

			//Set this element and all its pending constituents to 'APPROVED'. All sub-elements must already have an lsid.
			//Follow all primary & secondary relationships and approve those elements.

			changeElemStatus(newStatus, elemContainer, adminInfo, session);

			//....
			tx.commit();
		}catch(HibernateException ex){
			if (tx!=null) tx.rollback();
			throw ex;
		}catch(OutOfMemoryError er){
			if(tx!=null) tx.rollback();
			throw er;
		} catch (LSIDException e) {
			if(tx!=null) tx.rollback();
			throw e;
		} catch (ElementStatusChangeException e) {
			if(tx!=null) tx.rollback();
			throw e;
		}finally{
			if(session != null)
				session.close();
		}

		if(session != null && session.isOpen() && session.isConnected())
			session.disconnect();
	}


	private org.psygrid.data.model.hibernate.ElementStatusContainer getCurrentElementStatus(String lsid, Session session) throws LSIDException{

		//Grab the current element lsid, isHeadRevision and elementStatus

		org.psygrid.data.model.hibernate.LSID elemLsid = null;
		boolean isHeadRevision;
		DataElementStatus elemStatus;

		org.psygrid.data.model.hibernate.DataElementContainer element = ElementUtility.getElement(lsid, session);

		elemLsid = element.getElementLSIDObject();
		isHeadRevision = element.getHeadRevision();
		elemStatus = element.getStatus();

		org.psygrid.data.model.hibernate.ElementStatusContainer returnContainer = 
			new org.psygrid.data.model.hibernate.ElementStatusContainer(elemLsid, isHeadRevision, elemStatus);

		return returnContainer;
	}


	/**
	 * 
	 */
	public ElementStatusContainer[] reportElementStatusChanges(ElementStatusContainer[] elementsInQuestion, boolean reportNonHeadRevisionElements) throws LSIDException {

		Session session = null;
		Transaction tx = null;
		List<ElementStatusContainer> inputList = null;
		List<ElementStatusContainer> outputList = null;

		try{
			session = getHibernateTemplate().getSessionFactory().openSession();
			tx = session.beginTransaction();
			//....

			inputList = Arrays.asList(elementsInQuestion);
			outputList = new ArrayList<ElementStatusContainer>();

			for(ElementStatusContainer input: inputList){
				org.psygrid.data.model.hibernate.ElementStatusContainer currentStatusContainer = getCurrentElementStatus(input.getLsid(), session);

				if(!currentStatusContainer.getIsHeadRevision()){
					//Get the head-revision lsid.
					String headRevisionLSIDStr = this.getCurrentRevisionLevel(currentStatusContainer.getLsid().toString(), false);
					//get the status of the head revision object.

					org.psygrid.data.model.hibernate.DataElementContainer headElement = ElementUtility.getElement(headRevisionLSIDStr, session);
					DataElementStatus headRevStatus = headElement.getStatus();
					org.psygrid.data.model.hibernate.LSID headRevLSID = org.psygrid.data.model.hibernate.LSID.valueOf(headRevisionLSIDStr);
					currentStatusContainer.populateHeadRevisionInfo(headRevLSID, headRevStatus);
				}

				if(!input.toHibernate().isEqual(currentStatusContainer)){
					outputList.add(currentStatusContainer.toDTO());
				}else if(reportNonHeadRevisionElements && !currentStatusContainer.getIsHeadRevision()){
					outputList.add(currentStatusContainer.toDTO());
				}
			}

			//....
			tx.commit();
		}catch(HibernateException ex){
			if (tx!=null) tx.rollback();
			throw ex;
		}catch(OutOfMemoryError er){
			if(tx!=null) tx.rollback();
			throw er;
		} catch (LSIDException e) {
			if(tx!=null) tx.rollback();
			throw e;
		}finally{
			if(session != null)
				session.close();
		}

		if(session != null && session.isOpen() && session.isConnected())
			session.disconnect();


		ElementStatusContainer[] returnArray = new ElementStatusContainer[outputList.size()];
		outputList.toArray(returnArray);

		return returnArray;
	}
}
