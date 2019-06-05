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


/**
 * Allows the user to navigate a repository model that has been re-organised for
 * maximum intuitiveness and ease of access.
 * 
 * @author williamvance
 *
 */
public interface RepositoryObjectInterface {
	
	/**
	 * This returns the level of depth the object is currently at.
	 * The document itself sits at level 0.
	 * Sections are level 1.
	 * Entries and sub-entries are at level 2.
	 * If there are option entries, its options constitute another level.
	 * For composite entries, its sub-entries constitute another level.
	 * The deepest possible level occurs when the model possesses a composite
	 * with an option entry subordinate. In this case, the max depth will be 4.
	 * 
	 * 
	 * @return - depthLevel
	 */
	public int getDepthLevel();
	
	/**
	 * Returns the children of this node. For example, if the node is of type 'Section' its
	 * children will be a RepositoryObjectlist of type 'Entry'.
	 * 
	 * If the node has no children, an empty list will be returned;
	 * 
	 * @return - the list of children.
	 */
	public RepositoryObjectList getChildren();
	
	/**
	 * Returns the type of object that this node represents (Document, Option, Section, Entry)
	 * @return
	 */
	public RepositoryObjectType getNodeType();
	
	/**
	 * Returns the parents of any secondary relationships (option dependency, dervied entry variables)
	 * this node is involved in. If it has no secondary relationship parents, then a zero-length list
	 * will be returned.
	 * 
	 * @return
	 */
	//public List<RepositoryObject> getSecondaryRelationshipSuperiors();
	
	/**
	 * Returns the children of any secondary relationships (option dependency, derived entry variable)
	 * this node is involved in. If it has no secondary relationship children, then a zero-length list
	 * will be returned.
	 * 
	 * @return
	 */
	//public List<RepositoryObject> getSecondaryRelationshipSubordinates();
}


