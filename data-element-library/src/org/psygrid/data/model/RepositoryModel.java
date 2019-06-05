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
package org.psygrid.data.model;

import java.util.ArrayList;
import java.util.List;
import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.DerivedEntry;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Element;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.ExternalDerivedEntry;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.data.model.hibernate.ValidationRule;
import org.psygrid.data.model.RepositoryObjectList;

/**
 * This class represents the top-level 'node' of a repository model
 * It could represent a dataset, a document, or an entry
 * 
 * @author williamvance
 *
 */
public class RepositoryModel {
	
	public enum NodeIdentificationMethod{
		Binary,
		LSID,
		nodeName
	}
	
	public enum NodeType{
		RevisionCandidate,
		New,
		All
	}
	
	private final int depthLevel = 0;
	private final RepositoryObject repObject;
	private final RepositoryObjectType modelType;
	private static boolean checkForModelRecursion;
	
	/**
	 * This model needs to be relatable to a native document. Therefore, it provides
	 * a means of querying for nodes by passing in a native document node. This property
	 * identifies the means by which nodes will be matched.
	 */
	private static  NodeIdentificationMethod nodeIdentificationMethod;

	public RepositoryModel(Persistent repositoryObject, NodeIdentificationMethod idMethod, boolean checkForModelRecursion) throws RepositoryModelException{
		this.checkForModelRecursion = checkForModelRecursion;
		nodeIdentificationMethod = idMethod;
		RepositoryObject docParent = null;
		modelType = RepositoryModelUtility.getObjectType(repositoryObject);
		repObject = new RepositoryObject(this, docParent, repositoryObject, depthLevel);
		
		repObject.expand();
		
	}
	
	
	/**
	 * Returns all leaf nodes within the model.
	 * @return
	 */
	public List<RepositoryObject> getLeafNodes(){
		List<RepositoryObject> leafNodes = new ArrayList<RepositoryObject>();
		repObject.getLeafNodes(leafNodes);
		return leafNodes;
	}
	
	
	public int getMaxDepth(){
		return this.getRepositoryObject().getMaxDepth();
	}
	
	/**
	 * Returns the maximum depth at which either a revision candidate or a new element
	 * has been introduced.
	 * @return
	 */
	public int getMaxChangeDepth(){
		return this.getRepositoryObject().getMaxChangeDepth();
	}
	
	
	/**
	 * Returns a list of nodes, of a specified type, at the specified depth.
	 * @param depth - node depth (zero is root)
	 * @param type - the type of node to return
	 * @return - a list of nodes at specified depth and type
	 */
	public List<RepositoryObject> getNodesAtSpecifiedDepth(int depth, NodeType type) throws IndexOutOfBoundsException{
		List<RepositoryObject> objects = new ArrayList<RepositoryObject>();
		repObject.getNodesAtSpecifiedDepth(depth, objects, type);
		return objects;
		
	}
	
	public void setIsProcessed(RepositoryObject repositoryObject) {
		
		//Find duplicates ant set them as well.
		List<RepositoryObject> matchingObjects = searchForRepositoryObject(repositoryObject.getRepositoryObject());
		for(RepositoryObject obj: matchingObjects){
			obj.setProcessed();
		}
	}
	
	/**
	 * Returns a list of RepositoryObject nodes within the model that represent the
	 * object passed in. Returns an empty list if no matches are found.
	 * @param repositoryObject
	 * @return - a list of RepositoryObject nodes that encapsulate the object.
	 */
	private List<RepositoryObject> searchForRepositoryObject(Persistent repositoryObject) {
		
		List<RepositoryObject> matchedObjects = new ArrayList<RepositoryObject>();
		
		repObject.searchForRepositoryObject(repositoryObject, matchedObjects);
		
		return matchedObjects;
	}
	
	/**
	 * Returns the root RepositoryObject represented by the model.
	 * @return - the root repository object.
	 */
	private RepositoryObject getRepositoryObject(){
		return repObject;
	}
	

	public static NodeIdentificationMethod getNodeIdentificationMethod() {
		return nodeIdentificationMethod;
	}
	
	public class RepositoryObject implements RepositoryObjectInterface {

		final private RepositoryObjectType objectType;
		final private Persistent repositoryObject;
		final private RepositoryObject parent;
		private RepositoryModel repModel = null;
		final private int treeDepth;  //This is the structural depth.
		private RepositoryObjectList children;
		private boolean isLeafNode = true;
		boolean isProcessed; //Set this as soon as you create a new lsid; so that it does not accidentally get bumped up again.
		
		
		public RepositoryObject(RepositoryModel model, RepositoryObject parent, Persistent repObj, int depthLevel){
			
			repModel = model;
			this.parent = parent;
			objectType = RepositoryModelUtility.getObjectType(repObj);
			repositoryObject = repObj;
			treeDepth = depthLevel;
			
			children = new RepositoryObjectList(this, treeDepth+1);
		}
		
		protected RepositoryObject(RepositoryObject parent, Persistent repObj, int depthLevel) throws RepositoryModelException{
			
			this.parent = parent;
			
			if(RepositoryModel.isCheckForModelRecursion() && this.isDuplicatedInParentLineage()) {
				throw new RepositoryModelException("The element contains a recursive relationship.");
			}
			
			objectType = RepositoryModelUtility.getObjectType(repObj);
			repositoryObject = repObj;
			treeDepth = depthLevel;
			
			children = new RepositoryObjectList(this, treeDepth+1);
		}
		
		
		/**
		 * Add the object passed in as a child. As a result of calling this method, any of the children's children
		 * will also be added to the model, and any objects that are the parent of a secondary relationship will have
		 * their 'isParentOfSecondaryRelationship' to facilitate model searches for these types of objects.
		 * 
		 * Also, this child may have sub-nodes that are not part of the primary tree, such as Validation rules,
		 * Multi-variable test objects and Single-variable test objects. In these cases, such sub-objects will be added
		 * to the model's 'unattatched object' list.
		 * 
		 * @param child
		 * @throws RepositoryModelException 
		 */
		private void addChild(Persistent child) throws RepositoryModelException{
			
			RepositoryObject wrappedObject = new RepositoryObject(this, child, getChildren().getDepthLevel());
					
			this.getChildren().addRepositoryObject(wrappedObject);
			
			wrappedObject.expand();

			isLeafNode = false;
		}
		
		
		/**
		 * Returns whether the repository object is a library element candidate.
		 * Some aren't, such as options and sections.
		 */
		public boolean getIsElementCandidate(){
			
			boolean returnValue = true;
			
			switch(objectType){
			case Section:
			case Option:
			case Unknown:
				returnValue = false;
				break;
			default:
				break;
			}
			
			return returnValue;
		}
		
		/**
		 * This method 
		 * @param obj
		 * @throws RepositoryModelException 
		 */
		private void expand() throws RepositoryModelException{
		
			Persistent repObj = getRepositoryObject();
			
			if(repObj instanceof Document){
				Document doc = (Document) repObj;
				
				List<Section> sections = doc.getSections();
				for(Section sec: sections){
					addChild(sec);
				}
				
				//Add the right entries to the right sections
				List<Entry> entries = doc.getEntries();
				for(Entry e: entries){
					List<RepositoryObject> sections1 = getRepModel().searchForRepositoryObject(e.getSection());
					for(RepositoryObject sec: sections1){
						sec.addChild(e);
					}
				}
			}
			
			if(repObj instanceof BasicEntry){
				//Deal with validation rules.
				BasicEntry bE = (BasicEntry) repObj;
				if(bE.getValidationRules() != null && bE.getValidationRules().size() > 0){
					for(ValidationRule vR: bE.getValidationRules()){
						addChild(vR);
					}
				}
			}
			
			if(repObj instanceof CompositeEntry){
				for(Entry e: ((CompositeEntry)repObj).getEntries()){
					addChild(e);
				}
			}else if(repObj instanceof DerivedEntry){
				
				if(((DerivedEntry)repObj).getTest() != null){
					addChild(((DerivedEntry)repObj).getTest());
				}
				
				
				DerivedEntry dE = (DerivedEntry)repObj;		
				for(String varName : dE.getVariableNames()){
					addChild((Persistent)dE.getVariable(varName));
				}
				
				
			}else if(repObj instanceof ExternalDerivedEntry){
				
				if(((ExternalDerivedEntry)repObj).getTest() != null){
					addChild(((ExternalDerivedEntry)repObj).getTest());
				}
				
				ExternalDerivedEntry edE = (ExternalDerivedEntry)repObj;
				for(String varName : edE.getVariableNames()){
					addChild((Persistent)edE.getVariable(varName));
				}
				
			}else if(repObj instanceof ValidationRule){
				if(((ValidationRule)repObj).getTest() != null){
					addChild(((ValidationRule)repObj).getTest());
				}
			}
			
			
		}
		
		public RepositoryObjectType getObjectType() {
			return objectType;
		}

		public Persistent getRepositoryObject() {
			return repositoryObject;
		}
		

	
		/**
		 * This allows an exhaustive search for an native object in the model.
		 * This method returns the RepositoryObject that subsumes the native object,
		 * or null if no match was found.
		 * @param obj
		 * @return
		 */
		public void searchForRepositoryObject(Persistent repositoryObject, List<RepositoryObject> matchList) {
				
			if(ModelUtils.equals(repositoryObject, getRepositoryObject(), RepositoryModel.getNodeIdentificationMethod())){
				matchList.add(this);
			}
			
			if(children != null){
				children.searchForRepositoryObject(repositoryObject, matchList);
			}
		}
		
		
		/**
		 * Returns whether this object is already represented in its parent lineage. If it is, then the repository
		 * models being represented is recursive - and the RepositoryModel can't currently handle this.
		 * @return - whether this's underlying object is currently represented in its parent lineage.
		 */
		private boolean isDuplicatedInParentLineage() {
			
			if(getParent() != null) {
				return getParent().isDuplicatedInParentLineage(this);
			}else {
				//This is the root, and therefore it cannot be duplicated, so return false.
				return false;
			}
		}
		
		/**
		 * This method passes an object up the parent chain, comparing it with each ancestor. If 
		 * a match is found, the method returns 'true'. The comparison is carried out on the repository object
		 * that the RepositoryObject encapsulates.
		 * @param obj - the comparison object.
		 * @return - whether the object's encapsulated repository object has already represented within the
		 * object's parental lineage.
		 */
		protected boolean isDuplicatedInParentLineage(RepositoryObject obj) {
			
			if(ModelUtils.equals(obj.getRepositoryObject(), this.getRepositoryObject(), RepositoryModel.getNodeIdentificationMethod())){
				return true;
			}else if(getParent() != null) {
				return getParent().isDuplicatedInParentLineage(obj);
			}else {
				return false;
			}
		}
		

		public RepositoryObjectList getChildren() {
			return children;
		}

		public int getDepthLevel() {
			return treeDepth;
		}

		public RepositoryObjectType getNodeType() {
			return objectType;
		}

	
		/**
		 * Return the leaf nodes for the model. If this is not a leaf node then the node will ask 
		 * its children.
		 * 
		 * @param leafNodes
		 */
		public void getLeafNodes(List<RepositoryObject> leafNodes){
			
			if(this.isLeafNode){
				leafNodes.add(this);
			}else if (children != null){
				//Ask its children.
				for(RepositoryObject child : children.getObjectList()){
					child.getLeafNodes(leafNodes);
				}
			}
		}
		
		public int getMaxDepth(){
			
			int maxDepth = -1;
			
			if(this.isLeafNode){
				maxDepth = this.treeDepth;
				
			}else if (children != null){
				//Return the max depth of its children.
				for(RepositoryObject child : children.getObjectList()){
					maxDepth = Math.max(maxDepth, child.getMaxDepth());
				}
			}
			
			return maxDepth;
		}
		
		
		/**
		 * Returns the maximum 'change depth' - the max depth at which there is a 
		 * revision candidate or a new element has been introduced.
		 * Returns -1 in the event that there is no change.
		 * @return
		 */
		public int getMaxChangeDepth(){
			//TODO: - Logic not finished!
			int maxDepth = -1;
			
			//If this node hasn't changed then its depth is -1.
			//Otherwise it's 'treeDepth'.
			
			//Report back the greater of this node's value and the max
			//value of that of it's children.
			
			if(this.getIsNewElement() || this.getIsRevisionCandidate()){
				maxDepth = this.treeDepth;
			}
			
		
			if (children != null){
				//Return the max depth of its children.
				for(RepositoryObject child : children.getObjectList()){
					maxDepth = Math.max(maxDepth, child.getMaxChangeDepth());
				}
			}
			
			return maxDepth;
		}
		
		
		
		public void getNodesAtSpecifiedDepth(int depth, List<RepositoryObject> list, NodeType type){
			//Just follow the primary relationships; this should naturally avoid any duplicates.
			
			if(this.treeDepth == depth){
				//only add it if it hasn't already been added.
				boolean alreadyAdded = false;
				for(RepositoryObject listObj: list){
					if(listObj == this){
						alreadyAdded = true;
						break;
					}
				}
				
				if(!alreadyAdded){
					
					boolean addNode = false;
					
					switch(type){
						case All:
							addNode = true;
							break;
						case RevisionCandidate:
						{
							addNode = this.getIsRevisionCandidate();
						}
						break;
						case New:
						{
							addNode = this.getIsNewElement();
						}
						break;
					}
					
					if(addNode){
						list.add(this);
					}
				}

			}else if(children != null && depth > treeDepth){
				for(RepositoryObject child : children.getObjectList()){
					child.getNodesAtSpecifiedDepth(depth, list, type);
				}
			}
			
		}
		
		
		/**
		 * 
		 * @param valueToPropagate
		 */
		private void propagateRevisionCandidateFlag(boolean valueToPropagate){
			//Propagate to the parents and to secondaryRelationship superiors
			
			if(valueToPropagate){
				this.setIsRevisionCandidate(valueToPropagate);
			}else{
				//Don't do anything because we don't propagate 'false' upwards, only 'true'.
			}
			
			if(this.parent != null){
				parent.propagateRevisionCandidateFlag(valueToPropagate);
			}
			
		}
		
		/**
		 * Propagate the revision candidate flag upwards.
		 *
		 */
		public void propagateRevisionCandidateFlagToRoot(){
			//boolean isRevisionCandidate = this.getIsRevisionCandidate();
			this.propagateRevisionCandidateFlag(true);
		}
		
		private void setIsRevisionCandidate(boolean revCandidateFlag){
			boolean returnValue = false;
			
			switch(this.objectType){
			case Document:
				((org.psygrid.data.model.hibernate.Element)repositoryObject).setIsRevisionCandidate(revCandidateFlag);
				break;
			case Entry:
				((org.psygrid.data.model.hibernate.Element)repositoryObject).setIsRevisionCandidate(revCandidateFlag);
				break;
			case Section:
				((org.psygrid.data.model.hibernate.Element)parent.getRepositoryObject()).setIsRevisionCandidate(revCandidateFlag);
				break;
			case Option:
				((org.psygrid.data.model.hibernate.Element)parent.getRepositoryObject()).setIsRevisionCandidate(revCandidateFlag);
				break;
			case ValidationRule:
				((org.psygrid.data.model.hibernate.ValidationRule)repositoryObject).setIsRevisionCandidate(revCandidateFlag);
				break;
			case SingleVariableTest:
				((org.psygrid.data.model.hibernate.SingleVariableTest)repositoryObject).setIsRevisionCandidate(revCandidateFlag);
				break;
			case MultipleVariableTest:
				((org.psygrid.data.model.hibernate.MultipleVariableTest)repositoryObject).setIsRevisionCandidate(revCandidateFlag);
				break;
			}
			
		}
		
		public boolean getIsNewElement(){
			boolean isNewElement = false;
			
			switch (objectType){
				case Document:
				case Entry:
				case SingleVariableTest:
				case MultipleVariableTest:
				{
					isNewElement = ((Element)repositoryObject).getLSID() == null;
				}
				break;
				case Option:
				case Section:
				{
					isNewElement = ((Element)this.parent.getRepositoryObject()).getLSID() == null;
					
				}
				break;
				case ValidationRule:
				{
					isNewElement = ((ValidationRule)repositoryObject).getLSID() == null;
				}
				break;
			}
			
			return isNewElement;
		}
		
		public boolean getIsRevisionCandidate(){
			
			boolean returnValue = false;
			
			switch(this.objectType){
			case Document:
				returnValue = ((org.psygrid.data.model.hibernate.Element)repositoryObject).isRevisionCandidate();
				break;
			case Entry:
				returnValue = ((org.psygrid.data.model.hibernate.Element)repositoryObject).isRevisionCandidate();
				break;
			case Section:
				returnValue = ((org.psygrid.data.model.hibernate.Element)parent.getRepositoryObject()).isRevisionCandidate();
				break;
			case Option:
				returnValue = ((org.psygrid.data.model.hibernate.Element)parent.getRepositoryObject()).isRevisionCandidate();
				break;
			case ValidationRule:
				returnValue = ((org.psygrid.data.model.hibernate.ValidationRule)repositoryObject).getIsRevisionCandidate();
				break;
			case SingleVariableTest:
				returnValue = ((org.psygrid.data.model.hibernate.SingleVariableTest)repositoryObject).getIsRevisionCandidate();
				break;
			case MultipleVariableTest:
				returnValue = ((org.psygrid.data.model.hibernate.MultipleVariableTest)repositoryObject).getIsRevisionCandidate();
				break;
			}
			
			return returnValue;
		}

		public boolean getIsProcessed() {
			return isProcessed;
		}

		protected void setProcessed(){
			this.isProcessed = true;
		}
		


		protected RepositoryModel getRepModel() {
			return repModel != null ? repModel : getParent().getRepModel();
		}

		public RepositoryObject getParent() {
			return parent;
		}

	}

	protected static boolean isCheckForModelRecursion() {
		return checkForModelRecursion;
	}


}