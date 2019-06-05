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

import org.psygrid.data.model.hibernate.Record;

/**
 * Abstract class containing common functionality for all
 * Choosable implementations representing Records.
 * 
 * @author Rob Harper
 *
 */
public abstract class AbstractChoosableRecord<T extends Choosable> extends AbstractChoosableWithChildren<T> {

	public AbstractChoosableRecord(Choosable parent){
		super(parent);
	}
	
	public String getDescription() {
		return null;
	}

	public ChoosableType getType() {
		return ChoosableType.RECORD;
	}

	public abstract Record getRecord();
	
	public abstract String getIdentifier();
	
	public abstract String getSysIdentifier();
	
}
