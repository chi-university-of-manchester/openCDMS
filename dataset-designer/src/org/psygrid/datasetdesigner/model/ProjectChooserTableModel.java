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
package org.psygrid.datasetdesigner.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.security.SecurityHelper;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.www.xml.security.core.types.ProjectType;

import org.psygrid.data.repository.client.RepositoryClient;

/**
 * Table Model to support selection of projects 
 * 
 * @author pwhelan
 */
public class ProjectChooserTableModel  extends AbstractTableModel {

	/**
	 * The logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(ProjectChooserTableModel.class);
	
    private static final long serialVersionUID = 1L;
    
    protected ArrayList<ProjectType> projects;
    
    protected ArrayList<DataSet> dSets = null;
    
    public ProjectChooserTableModel(List<ProjectType> projects) { 
        this.projects = new ArrayList<ProjectType>(projects);
    	dSets = new ArrayList<DataSet>();

    	try {
        	RepositoryClient client = new RepositoryClient();
    		String saml = SecurityHelper.getAAQueryClient().getSAMLAssertion().toString();
    		for (ProjectType p: projects) {
        		//get the summary
    			try {
    				DataSet  ds;
    				if (p.getIdCode().equals("-1")) {
    					ds= null;
    				} else {
    					ds = client.getDataSetSummary(p.getIdCode(), new Date(0), saml);
    				}
        			if (ds == null) {
            			//get the dataset
                		this.projects.remove(p);
        			} else {
                		dSets.add(ds);
        			}

    			//if exception happens, remove it from the project list to keep this synched
    			} catch (Exception ex) {
    				LOG.error("PCTM : project without dataset", ex);
    				this.projects.remove(p);
    			}
    		
    		}
    	} catch (Exception ex) {
    		LOG.error("ProjectChooserTableModel : error getting datasets from projects", ex);
    	}
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.studychooser.projects"); //$NON-NLS-1$

        default:
            throw new IllegalStateException("Number of columns is fixed at 1"); //$NON-NLS-1$
        }
    }
    
    public int getRowCount() {
        return projects.size();
    }

    public int getColumnCount() {
        return 1;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
    	if (rowIndex < dSets.size()) {
            return dSets.get(rowIndex);
    	} 

    	LOG.error("PCTM : asking for a row value that does not exist : " + rowIndex);
    	return null;
    }

    public ProjectType getValueAtRow(int row) {
        return projects.get(row);
    }

}
