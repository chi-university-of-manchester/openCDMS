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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.persistence.DataSetSummary;
import org.psygrid.data.model.hibernate.DataSet;

/**
 * Choosable implementation to represent a Dataset.
 * 
 * @author Rob Harper
 *
 */
public class ChoosableDataSet extends AbstractChoosableWithChildren<Choosable>    {

    protected DataSetSummary dataSet;
    
    public ChoosableDataSet(DataSetSummary dataSet, Choosable parent) {
        super(parent);
        this.dataSet = dataSet;
    }
    
    public String getDisplayText() {
    	return dataSet.getDisplayText();
    }

    public ChoosableType getType() {
    	return ChoosableType.DATASET;
    }
    
    public String getDescription() {
    	return dataSet.getDescription();
    }

    public DataSet getDataSet() {
    	try {
    		return dataSet.getCompleteDataSet();
    	}
    	catch (IOException ioe) {
    		ExceptionsHelper.handleIOException(null, ioe, true);
    	}
    	return null;
    }
    
	public boolean isLocked() {
		if ( null != getParent() && getParent().isLocked() ) {
			return true;
		}
		return false;
	}

	public List<Choosable> getChildren(DocumentStatus status) throws ChoosableException {
		if (children != null) {
			List<Choosable> choosables = new ArrayList<Choosable>();
			for (Choosable c: children) {
				if (c instanceof RemoteChoosableRecord) {
					RemoteChoosableRecord r = (RemoteChoosableRecord)c;
					if (r.documentStatus != null && r.documentStatus.equals(status)) {
						choosables.add(r);
					}
				}
			}
			return choosables;
		}
		return null;
	}
}
