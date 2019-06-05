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

import org.psygrid.data.model.hibernate.DocumentGroup;

/**
 * Abstract class containing common functionality for all
 * Choosable implementations that represent Document Groups.
 * 
 * @author Rob Harper
 *
 * @param <T> The type of the children of the Choosable.
 */
public abstract class AbstractChoosableDocGroup<T extends Choosable> extends AbstractChoosableWithChildren<T> {

	/**
	 * The Document Group that the Choosable represents.
	 */
	private final DocumentGroup documentGroup;
	
	public AbstractChoosableDocGroup(DocumentGroup documentGroup, AbstractChoosableWithChildren<Choosable> parent) {
		super(parent);
		this.documentGroup = documentGroup;
	}

	public String getDisplayText() {
		return documentGroup.getDisplayText();
	}

	public String getDescription() {
		return documentGroup.getDescription();
	}

	public ChoosableType getType() {
		return ChoosableType.DOCUMENT_GROUP;
	}

	public DocumentGroup getDocumentGroup() {
		return documentGroup;
	}

}
