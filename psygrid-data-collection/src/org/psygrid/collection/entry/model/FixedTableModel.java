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


package org.psygrid.collection.entry.model;

import java.util.List;

import org.psygrid.collection.entry.renderer.RendererData.EditableStatus;
import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.CompositeResponse;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.StandardCode;

public class FixedTableModel extends EntryTableModel    {

    public FixedTableModel(CompositeEntry entry, CompositeResponse response,
            SectionPresModel sectionOccPresModel, boolean copy,
            DocumentInstance docInstance, List<StandardCode> standardCodes) {
        super(entry, response, sectionOccPresModel, copy, docInstance,
                standardCodes);
        if (entry.numRowLabels() < 1) {
            throw new IllegalArgumentException("entry must have have 1 or more " + //$NON-NLS-1$
                    "row labels"); //$NON-NLS-1$
        }
    }
    
    @Override
    public void init() {
        int totalRows = entry.numRowLabels();
        
        super.init();
        
        for (int i = 0; i < totalRows; ++i) {
            addRow(i, null, EditableStatus.DEFAULT);
        }
        
        if (totalRows == 0) {
            addRow(null, EditableStatus.DEFAULT);
        }
    }

    @Override
    protected int getStartColumn() {
        //Assume here that it is the first column that contains the fixed
        //values defined in the composite's list of row labels.
        //So, we don't apply a standard code to the first column.
        return 1;
    }

	public void reset() {
		int col = 0;
		for ( List<BasicPresModel> presModels: childPresModels){
			if ( col > 0 ){
				//we skip the first column as this is the "fixed" column
				for ( BasicPresModel presModel: presModels ){
					presModel.reset();
				}
			}
			col++;
		}
	}

	public void touch() {
		// TODO Auto-generated method stub
		
	}

    
}
