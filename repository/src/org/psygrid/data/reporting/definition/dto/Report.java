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

package org.psygrid.data.reporting.definition.dto;

import java.util.HashMap;
import java.util.Map;

import org.psygrid.data.model.dto.DataSetDTO;
import org.psygrid.data.model.dto.PersistentDTO;

public abstract class Report extends PersistentDTO {

    private DataSetDTO dataSet;
    
    private String title;
    
    private boolean template = true;
    
    private boolean showHeader = true;
    
    public DataSetDTO getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSetDTO dataSet) {
        this.dataSet = dataSet;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isTemplate() {
		return template;
	}

	public void setTemplate(boolean template) {
		this.template = template;
	}
   
	public boolean isShowHeader() {
		return showHeader;
	}
	
	public void setShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
	}
	
	
    public org.psygrid.data.reporting.definition.hibernate.Report toHibernate(){
        Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
        return toHibernate(hRefs);
    }
    
    @Override
    public abstract org.psygrid.data.reporting.definition.hibernate.Report toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs);

    public void toHibernate(org.psygrid.data.reporting.definition.hibernate.Report hP, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hP, hRefs);
        hP.setTitle(this.title);
        hP.setTemplate(template);
        hP.setShowHeader(showHeader);
        if ( null != dataSet ){
            hP.setDataSet(dataSet.toHibernate(hRefs));
        }
    }
    
}
