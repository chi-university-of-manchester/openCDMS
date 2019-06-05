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

import net.sourceforge.jeval.EvaluationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.psygrid.data.UserPrivilegeMatrix;
import org.psygrid.data.dao.hibernate.DataElementDAOHibernate;
import org.psygrid.data.model.ITestable;
import org.psygrid.data.model.RepositoryModel;
import org.psygrid.data.model.RepositoryObjectType;
import org.psygrid.data.model.hibernate.DataElementContainer;
import org.psygrid.data.model.hibernate.Element;
import org.psygrid.data.model.hibernate.ElementHistoryItem;
import org.psygrid.data.model.hibernate.LSID;
import org.psygrid.data.model.hibernate.LSIDAuthority;
import org.psygrid.data.model.hibernate.LSIDException;
import org.psygrid.data.model.hibernate.ValidationRule;

public class ElementUtility {
	
	private static final Log sLog = LogFactory.getLog(DataElementDAOHibernate.class);
	
    /**
     * Extracts the hibernate lsid identifier from an lsid string.
     * @param lsid - the lsid from which to extract the hibernate lsid object identifier.
     * @return - the lsid db identifier
     * @throws LSIDException
     */
    public static Long getLSIDIdFromLSIDString(String lsid) throws LSIDException{
    	
    	final String METHOD_NAME = "getLSIDFromLSIDString";
    	
    	org.psygrid.data.model.hibernate.LSID lsidObj = null;
		try {
			lsidObj = org.psygrid.data.model.hibernate.LSID.valueOf(lsid);
		} catch (LSIDException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(), e);
			throw e;
		}
    	Long objectId = null;
    	if(lsidObj.getObjectId().lastIndexOf(new Character('-')) != -1){
    		int hyphenIndex = lsidObj.getObjectId().lastIndexOf(new Character('-'));
    		objectId = new Long(lsidObj.getObjectId().substring(hyphenIndex+1));
    	}else{
    		objectId = new Long(lsidObj.getObjectId().toString());
    	}
    	
    	return objectId;
    }
    
    /**
     * Returns whether the authority of the lsid passed in is contained within the provided lsid authority array.
     * @param lsid
     * @param elementAuthorities
     * @return
     * @throws LSIDException
     */
    public static boolean elementAuthorityIsInList(String lsid, List<LSIDAuthority> elementAuthorities) throws LSIDException{
    	String authority = LSID.valueOf(lsid).getAuthorityId();
    	for(LSIDAuthority dbAuthority: elementAuthorities){
    		if(authority.equals(dbAuthority.getAuthorityID())){
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    /**
     * Returns whether the authority passed in is already contained within the provided lsid authority array.
     * @param authority string
     * @param elementAuthorities - the list of authorities to check against.
     * @return - whether the authority is represented within the provided list.
     * @throws LSIDException
     */
    public static boolean authorityIsInList(String authority, List<LSIDAuthority> elementAuthorities) {
    	
    	for(LSIDAuthority dbAuthority: elementAuthorities){
    		if(authority.equals(dbAuthority.getAuthorityID())){
    			return true;
    		}
    	}
    	return false;
    }
    
    public static DataElementContainer getElementContainerForRepositoryObject(RepositoryModel.RepositoryObject obj) {
    	
		org.psygrid.data.model.hibernate.DataElementContainer elemContainer = null;
		
		RepositoryObjectType type = obj.getNodeType();
		switch(type){
			case ValidationRule:
			{
			org.psygrid.data.model.hibernate.ValidationRule valRule = (org.psygrid.data.model.hibernate.ValidationRule)obj.getRepositoryObject();
			elemContainer = new org.psygrid.data.model.hibernate.DataElementContainer(valRule);
			}
			break;
			case Document:
			case SingleVariableTest:
			case MultipleVariableTest:
			case Entry:
			{
			org.psygrid.data.model.hibernate.Element element = (org.psygrid.data.model.hibernate.Element)obj.getRepositoryObject();
			elemContainer = new org.psygrid.data.model.hibernate.DataElementContainer(element);
			}
			break;
		}
    	
    	return elemContainer;
    }
    
    /**
     * Runs the element's test, if it has one. If the element doesn't have a test, then it returns true.
     * If it does have a test, the return value is pass or fail. The method also 'resets' the test.
     * @return pass or fail, or automatic 'pass' if the element does not have a test.
     */
    public static boolean runTest(DataElementContainer elem){
    	
    	boolean result = false;
    	
    	Class elemClass = elem.getElementClass();
    	if(ITestable.class.isAssignableFrom(elemClass)){
   
    		ITestable testableElement = (ITestable)elem.getElement();
    		if(testableElement.hasTest()){
    			try {
					result = testableElement.runTest();
					testableElement.resetTest();
				} catch (EvaluationException e) {
					//Treat this as a failure.
					result = false;
				}
    		}else{
    			//It is a testable element, but doesn't have a test, so return true.
    			result = true;
    		}
    	}else{
    		//Not a testable element - so return true.
    		result = true;
    	}
    	
    	return result;
    }
    
    /**
     * Extracts the hibernate lsid identifier from an lsid string.
     * @param lsid - the lsid from which to extract the hibernate lsid object identifier.
     * @return - the lsid db identifier
     * @throws LSIDException
     */
    public final static Long getElementIdFromLSIDString(String lsid) throws LSIDException{
    	
    	final String METHOD_NAME = "getElementIdFromLSIDString";
    	
    	org.psygrid.data.model.hibernate.LSID lsidObj = null;
		try {
			lsidObj = org.psygrid.data.model.hibernate.LSID.valueOf(lsid);
		} catch (LSIDException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(), e);
			throw e;
		}
    	Long objectId = null;
    	if(lsidObj.getObjectId().lastIndexOf(new Character('-')) != -1){
    		int lastHyphenIndex = lsidObj.getObjectId().lastIndexOf(new Character('-'));
    		int penultimateHyphenIndex = lsidObj.getObjectId().lastIndexOf(new Character('-'), lastHyphenIndex-1);
    		objectId = new Long(lsidObj.getObjectId().substring(penultimateHyphenIndex+1, lastHyphenIndex));
    	}else{
    		objectId = new Long(lsidObj.getObjectId().toString());
    	}
    	
    	return objectId;
    }
    
    public static final DataElementContainer.ElementType getElementType(String lsid) throws LSIDException{
    	
    	final String METHOD_NAME = "getElementType";
    	
    	org.psygrid.data.model.hibernate.LSID lsidObj = org.psygrid.data.model.hibernate.LSID.valueOf(lsid);
    	String nameSpace = lsidObj.getNamespaceId();
    	
    	if(nameSpace.contains("ValidationRule")){
    		return DataElementContainer.ElementType.rule;
    	}else
    		return DataElementContainer.ElementType.primary;
    	
    }
    
    /**
     * 
     * 
     * @param metaData
     * @return
     * @throws LSIDException
     */
	public static ElementHistoryItem populateElementHistoryItem(org.psygrid.data.model.hibernate.ElementMetaData metaData) throws LSIDException{
		
		final String METHOD_NAME = "populateElementHistoryItem";
		
		ElementHistoryItem hI = new ElementHistoryItem();
		
		//get the lsid for the metaData
		org.psygrid.data.model.hibernate.LSID elemLSID = null;
		try {
			elemLSID = org.psygrid.data.model.hibernate.LSID.valueOf(metaData.getElementLSID());
		} catch (LSIDException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(), e);
			throw e;
		}
		
		//TODO:DEL We need to populate each element history item with the action taken.
		hI.setActiontaken(metaData.getAction());
		hI.setDescription(metaData.getActivityDescription());
		hI.setHistoryEventDate(metaData.getElementDate());
		hI.setLsid(metaData.getElementLSID());
		hI.setWho(metaData.getWho());
		
		return hI;
	}
	
	public static  org.psygrid.data.model.hibernate.DataElementContainer getElement(String lsid, Session session) throws LSIDException{
	
		DataElementContainer.ElementType type = getElementType(lsid);
		Long objectId = getElementIdFromLSIDString(lsid);

		org.psygrid.data.model.hibernate.DataElementContainer returnElement = null;
		
		switch(type){
		case primary:
		{
           org.psygrid.data.model.hibernate.Element elem = (org.psygrid.data.model.hibernate.Element)session.createQuery("from Element e where e.id=?")
            .setLong(0, objectId)
            .uniqueResult();
           
           returnElement = new org.psygrid.data.model.hibernate.DataElementContainer(elem);
		}
		break;
		case rule:
		{
			org.psygrid.data.model.hibernate.ValidationRule rule = (org.psygrid.data.model.hibernate.ValidationRule)session.createQuery("from ValidationRule v where v.id=?")
            .setLong(0, objectId)
            .uniqueResult();
           
           returnElement = new org.psygrid.data.model.hibernate.DataElementContainer(rule);
		}
			break;
		default:
			break;
		}
		
		String errorString = null;
		//It is possible at this point that the element was not found. If this is the case, returnElement will have a null
		//element.
		if(returnElement.getElement() == null){
			errorString = new String("Element: " + lsid + " not found in database.");
		}else{
			//It is also possible that an element was found, but it does not match the lsid provided. Need to ensure that
			//the lsids match and that the elements are of the same kind, and also that their names match.
			DataElementContainer.ElementType elemType = returnElement.getElementType();
			switch(elemType){
			case primary:
				Element retrievedElement = (Element)returnElement.getElement();
				if(!retrievedElement.getLSID().toString().equals(lsid)){
					errorString = new String("Element: " + lsid + " not found in database.");
				}
				break;
			case rule:
				ValidationRule rule = (ValidationRule)returnElement.getElement();
				if(!rule.getLSID().toString().equals(lsid)){
					errorString = new String("Element: " + lsid + " not found in database.");
				}
				break;
			default:
				break;
			}
		}
		
		if(errorString != null){
			throw new LSIDException(errorString);
		}
		
		return returnElement;
	}
	
	public static org.psygrid.data.model.hibernate.DataElementContainer getElementAndSetIsEditable(String lsid, Session session, UserPrivilegeMatrix upm) throws LSIDException{
		DataElementContainer.ElementType type = getElementType(lsid);
		Long objectId = getElementIdFromLSIDString(lsid);

		org.psygrid.data.model.hibernate.DataElementContainer returnElement = null;
		
		String elementAuthority;
		
		switch(type){
		case primary:
		{
           org.psygrid.data.model.hibernate.Element elem = (org.psygrid.data.model.hibernate.Element)session.createQuery("from Element e where e.id=?")
            .setLong(0, objectId)
            .uniqueResult();
           
           elementAuthority = elem.getLSID().getAuthorityId();
           boolean isAuthorInElementAuthority = upm.getIsAuthorForAuthority(elementAuthority);
           boolean isEditable = isAuthorInElementAuthority && elem.getHeadRevision();
           elem.setIsEditable(isEditable);
           
           returnElement = new org.psygrid.data.model.hibernate.DataElementContainer(elem);
		}
		break;
		case rule:
		{
			org.psygrid.data.model.hibernate.ValidationRule rule = (org.psygrid.data.model.hibernate.ValidationRule)session.createQuery("from ValidationRule v where v.id=?")
            .setLong(0, objectId)
            .uniqueResult();
			
			elementAuthority = rule.getLSID().getAuthorityId();
			boolean isAuthorInElementAuthority = upm.getIsAuthorForAuthority(elementAuthority);
			boolean isEditable = isAuthorInElementAuthority && rule.getHeadRevision();
			rule.setIsEditable(isEditable);
           
           returnElement = new org.psygrid.data.model.hibernate.DataElementContainer(rule);
		}
			break;
		default:
			break;
		}
		
		
		
		return returnElement;
	}
	
	public static void populateElementHistory(org.psygrid.data.model.hibernate.DataElementContainer elem, Session session, boolean retrieveFullHistory) throws LSIDException{
		
		final String METHOD_NAME = "populateElementHistory";
		
		List<ElementHistoryItem> elemHistItemList = new ArrayList<ElementHistoryItem>();
			
		org.psygrid.data.model.hibernate.LSID lsid = null;
		try {
			lsid = org.psygrid.data.model.hibernate.LSID.valueOf(elem.getElementLSID());
		} catch (LSIDException e) {
			sLog.error(METHOD_NAME+": "+e.getClass().getSimpleName(), e);
			throw e;
		}
		
		
		org.psygrid.data.model.hibernate.DataElementContainer prevElem = elem;
		String previousRevisionString = prevElem.getElementLSIDObject().getPreviousRevision();
		while(previousRevisionString != null) {
			Long lsidID = getLSIDIdFromLSIDString(previousRevisionString);
			
			//Now retrieve the element.
			prevElem = getElement(previousRevisionString, session);
			
			//If retrieving full element history then we need to add info about the pending stages as well.
			//We want to work backwards through the list, from newest to oldest.
			List<org.psygrid.data.model.hibernate.ElementMetaData> metaData = prevElem.getMetaData();
			
			if(!retrieveFullHistory){
				elemHistItemList.add(populateElementHistoryItem(prevElem.getLatestMetaData()));
			}else{
				for(int i = metaData.size()-1; i >=0; i--){
					elemHistItemList.add(populateElementHistoryItem(prevElem.getMetaData().get(i)));
				}
			}
			
 			previousRevisionString = prevElem.getElementLSIDObject().getPreviousRevision();
		}
		
		//Turn revision_0 metadata into a history item to facilitate easy display in the UI
		List<org.psygrid.data.model.hibernate.ElementMetaData> metaData = elem.getMetaData();
		if(!retrieveFullHistory){
			elemHistItemList.add(populateElementHistoryItem(elem.getLatestMetaData()));
		}else{
			for(int i = metaData.size()-1; i >=0; i--){
				elemHistItemList.add(populateElementHistoryItem(elem.getMetaData().get(i)));
			}
		}
		elem.getLatestMetaData().setHistoryList(elemHistItemList);
	}


}
