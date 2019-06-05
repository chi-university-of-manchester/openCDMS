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

public enum DocumentImportFeature{
	MultipleSections(0, "Multiple Sections"),
	FixedLengthCompositeEntry(1, "Composite Entry/entries"),
	VariableLengthCompositeEntry(2, "Variable length composite entry/entries"),
	UncomplexFirstSection(3, "Document's first section is uncomplex");
	
	private DocumentImportFeature(int uniqueId, String featureDescription){
		this.uniqueId = uniqueId;
		this.featureDescription = featureDescription;
	}
	
	public int getUniqueId() { return uniqueId; }
	
	public String getDescription() { return featureDescription; }
	
	final int uniqueId;
	final String featureDescription;
}
