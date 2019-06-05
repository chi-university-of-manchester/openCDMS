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


package org.psygrid.collection.entry.chooser;

import org.psygrid.data.model.hibernate.DocumentOccurrence;

/**
 * Choosable implementation to represent a Document Occurrence.
 * <p>
 * Is always at the bottom level of a Choosable hierarchy, so
 * cannot have children.
 * 
 * @author Rob Harper
 *
 */
public class ChoosableDocOccurrence extends AbstractChoosable  {

	/**
	 * The Document Occurrence that the Choosable represents.
	 */
	private final DocumentOccurrence documentOccurrence;
	
	/**
	 * If True a Document Instance exists for the Document Occurrence (and
	 * previously selected Record) that has been committed to the central
	 * data repository.
	 * <p>
	 * If False, either no Document Instance exists or is local and has
	 * not been committed to the central data repository.
	 */
	private final boolean remote;
	
	/**
	 * The status of the Document Instance for the Document Occurrence (and
	 * previously selected Record), or <code>null</code> if no Document
	 * Instance exists.
	 */
	private final String status;

	/**
	 * If True then the Document Occurrence is in a "secondary" dataset
	 * and is intended to be completed via data replication from the
	 * equivalent Document Occurrence in the "primary" dataset.
	 */
	private final boolean secondary; 

	public ChoosableDocOccurrence(DocumentOccurrence documentOccurrence,
			ChoosableDocGroup parent, String status, boolean isRemote, boolean isSecondary) {
		super(parent);
		this.documentOccurrence = documentOccurrence;
		this.remote = isRemote;
		this.status = status;
		this.secondary = isSecondary;
		parent.addChild(this);
	}

	public ChoosableDocOccurrence(DocumentOccurrence documentOccurrence, ChoosableTemplateDocGroup parent){
		super(parent);
		this.documentOccurrence = documentOccurrence;
		this.remote = false;
		this.status = null;
		this.secondary = false;
		parent.addChild(this);
	}
	
	public String getDisplayText() {
		return ChooserHelper.getDocumentDisplayText(documentOccurrence);
	}

	public String getDescription() {
		return documentOccurrence.getDescription();
	}

	public ChoosableType getType() {
		return ChoosableType.DOCUMENT_OCCURRENCE;
	}

	/**
	 * Returns whether this Document Occurrence has been locked
	 * 
	 * @return boolean
	 */
	public boolean isLocked() {
		//If the DocumentGroup has been locked then this document should also be locked
		if (getParent().isLocked()) {
			return true;
		}
		return documentOccurrence.isLocked();
	}

	public DocumentOccurrence getDocumentOccurrence() {
		return documentOccurrence;
	}

	/**
	 * Returns whether the document is held locally or in the repository
	 * 
	 * @return isRemote
	 */
	public boolean isRemote() {
		return remote;
	}

	/**
	 * Returns whether this document is a linked DDE document that will be
	 * filled in automatically by the 'primary' document 
	 * 
	 * @return isSecondary
	 */
	public boolean isSecondary() {
		return secondary;
	}

	public String getStatus() {
		return status;
	}

}
