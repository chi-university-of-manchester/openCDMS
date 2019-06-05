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
 * A Choosable to represent a Record, all details of which are
 * known at the time of construction i.e. the record is stored
 * locally and does not need to be retrieved from the remote
 * repository on selection.
 * 
 * @author Rob Harper
 *
 */
public class ChoosableRecord extends AbstractChoosableRecord<Choosable> {

	/**
	 * The Record
	 */
	protected final Record record;
	
	public ChoosableRecord(Record record){
		super(null);
		this.record = record;
	}
	
	public String getDisplayText() {
		if(record.getUseExternalIdAsPrimary() != true)
			return record.getIdentifier().getIdentifier();
		else{
			//return record.getExternalIdentifier();
			return "cr ext id";
		}
			
	}

	public Record getRecord(){
		return record;
	}

	@Override
	public String getIdentifier() {
		return record.getIdentifier().getIdentifier();
	}

	@Override
	public String getSysIdentifier() {
		// TODO Auto-generated method stub
		return record.getIdentifier().getIdentifier();
	}
}
