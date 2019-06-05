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
package org.psygrid.data.model.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.psygrid.data.repository.dao.RelationshipReconstitutionException;

/**
 * 
 * @hibernate.joined-subclass table="t_element_relationship"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class ElementRelationship extends Persistent{
	
	public enum RelationshipType{
		delPrimaryRelationship,		//A data element hierarchical relationship
		delSecondaryRelationship, 	//Part of the data element definition, but not a hierarchical relationship
		repositoryRelationship		//Only important when returning a repository template. Not part of the del definition.
	};
	
	/**
	 * This method uses reflection to reconstitute the native repository relationship that exists between
	 * these base element and the related element, based on the information contained within this object.
	 * The relatedElement needs to have already been set.
	 * 
	 * @param baseElement
	 * @throws RelationshipReconstitutionException 
	 */
	public void reconstituteNativeRelationship(DataElementContainer baseElement, DataElementContainer relatedElement) throws RelationshipReconstitutionException{
		Class[] argTypes = new Class[1];
		try {
			argTypes[0] = Class.forName(getElementClass());

			Method mtd = baseElement.getElementClass().getMethod(getRepopulateMethod(), argTypes);
						
			Object[] args = new Object[1];
			args[0] = relatedElement.getElement();
			
			mtd.invoke(baseElement.getElement(), args);					
		} catch (ClassNotFoundException e) {
			
		}catch (SecurityException e) {
			throw new RelationshipReconstitutionException("Basic relationship failed to be reconstituted due to SecurityException", e, baseElement, relatedElement, this);
		} catch (NoSuchMethodException e) {
			throw new RelationshipReconstitutionException("Basic relationship failed to be reconstituted due to NoSuchMethodException", e, baseElement, relatedElement, this);
		} catch (IllegalAccessException e) {
			throw new RelationshipReconstitutionException("Basic relationship failed to be reconstituted due to IllegalAccessException", e, baseElement, relatedElement, this);
		} catch (InvocationTargetException e) {
			throw new RelationshipReconstitutionException("Basic relationship failed to be reconstituted due to InvocationTargetException", e, baseElement, relatedElement, this);
		}
	}

	//NOTE: relatedElement ONLY to be used during an import. This is NOT persisted to the library database.
	private DataElementContainer relatedElement;
	
	private String relatedElementLSID;

	private String repopulateMethod;
	private String elementClass;

	
	private boolean indexedRelationship;
	private int	elementIndex; //To be used for reconstructing lists... (maintaining order that has been defined implicitly by list order)
	
	private RelationshipType relationshipType;
	
	private boolean relatedElementIsPersisted;
	
	public ElementRelationship(){
	}
	
	
	
	public ElementRelationship(String lsid, String elementClass, String repopulateMethod) throws IllegalArgumentException{
		this.relatedElementLSID = lsid;
		this.elementClass = elementClass;
		this.repopulateMethod = repopulateMethod;
		this.indexedRelationship = false;
		this.elementIndex = -1;
		this.relatedElementIsPersisted = true;
	}
	
	
	public ElementRelationship(String lsid, String elementClass, String repopulateMethod, int index) throws IllegalArgumentException{
		this.relatedElementLSID = lsid;
		this.elementClass = elementClass;
		this.repopulateMethod = repopulateMethod;
		this.elementIndex = index;
		this.indexedRelationship = true;
		this.relatedElementIsPersisted = true;
	}
	
	
	/**
	 * Determines whether the related element also needs saving; determined solely by which constructor
	 * is called. 
	 * @return - whether the related element may require saving.
	 */
	public boolean getRelatedElementIsPersisted() {
		return relatedElementIsPersisted;
	}



	public ElementRelationship(DataElementContainer relatedElement, String elementClass, String repopulateMethod) throws IllegalArgumentException{
		this.relatedElement = relatedElement;
		this.elementClass = elementClass;
		this.repopulateMethod = repopulateMethod;
		this.indexedRelationship = false;
		this.elementIndex = -1;
		this.relatedElementLSID = null;
		this.relatedElementIsPersisted = false;
	}
	
	public ElementRelationship(DataElementContainer relatedElement, String elementClass, String repopulateMethod, int index) throws IllegalArgumentException{
		this.relatedElement = relatedElement;
		this.elementClass = elementClass;
		this.repopulateMethod = repopulateMethod;
		this.elementIndex = index;
		this.indexedRelationship = true;
		this.relatedElementLSID = null;
		this.relatedElementIsPersisted = false;
	}
	
	
	
	public void setRelatedElementLSID(String lsid){
		this.relatedElementLSID = lsid;
	}
	
	/**
	 * 
	 * @return - the string-lsid of the related element.
	 * 
	 * @hibernate.property column="c_lsid"
	 */
	public String getRelatedElementLSID(){
		if(relatedElement != null){
			return relatedElement.getElementLSIDObject().toString();
		}else{
			return relatedElementLSID;
		}
	}
	
	/**
	 * @hibernate.property column="c_repopulate_method"
	 */
	public String getRepopulateMethod(){
		return this.repopulateMethod;
	}
	
	public void setRepopulateMethod(String method){
		this.repopulateMethod = method;
	}
	
	/**
	 * @hibernate.property column="c_element_class"
	 */
	public String getElementClass(){
		return this.elementClass;
	}
	
	public void setElementClass(String elementClass){
		this.elementClass = elementClass;
	}
	

	@Override
	public org.psygrid.data.model.dto.PersistentDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		org.psygrid.data.model.dto.ElementRelationshipDTO elemRelationship = null;
		//check to see if we're already in the map.
       if ( dtoRefs.containsKey(this)){
           elemRelationship = (org.psygrid.data.model.dto.ElementRelationshipDTO)dtoRefs.get(this);
       }
       if( null == elemRelationship){
    	   elemRelationship = new org.psygrid.data.model.dto.ElementRelationshipDTO();
    	   dtoRefs.put(this, elemRelationship);
    	   toDTO(elemRelationship, dtoRefs, depth);
       }
		 
		return elemRelationship;
	}
	
   
   public void toDTO(org.psygrid.data.model.dto.ElementRelationshipDTO elementRelationship, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
       super.toDTO(elementRelationship, dtoRefs, depth);
       elementRelationship.setElementLSID(this.getRelatedElementLSID());
       elementRelationship.setRepopulateMethod(this.getRepopulateMethod());
       elementRelationship.setElementClass(this.getElementClass());
       elementRelationship.setElementIndex(getElementIndex());
       elementRelationship.setIndexedRelationship(this.getIndexedRelationship());
   }

	public int getElementIndex() {
		return elementIndex;
	}
	
	
	/**
	 * @hibernate.property column="c_element_index"
	 */
	public void setElementIndex(int elementIndex) {
		this.elementIndex = elementIndex;
	}

	/**
	 * @hibernate.property column="c_indexed_relationship"
	 */
	public boolean getIndexedRelationship() {
		return indexedRelationship;
	}

	public void setIndexedRelationship(boolean indexedRelationship) {
		this.indexedRelationship = indexedRelationship;
	}

	public RelationshipType getRelationshipType() {
		return relationshipType;
	}
	
	/**
	 * @hibernate.property column="c_relationship_type"
	 */
	public String getRelationshipTypeEnum(){
		String returnedRelationshipType = null;
		if(this.relationshipType != null){
			returnedRelationshipType = this.relationshipType.toString();
		}
		
		return returnedRelationshipType;
	}

	public void setRelationshipType(RelationshipType relationshipType) {
		this.relationshipType = relationshipType;
	}
	
	public void setRelationshipTypeEnum(String relationshipType){
		RelationshipType relType = null;
		if(relationshipType != null){
			relType = RelationshipType.valueOf(relationshipType);
		}
		
		setRelationshipType(relType);
	}


	public DataElementContainer getRelatedElement() {
		return relatedElement;
	}


}
