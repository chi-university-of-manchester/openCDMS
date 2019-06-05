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

import java.util.List;
import java.util.Map;

import org.psygrid.data.model.dto.PersistentDTO;

public abstract class Chart extends PersistentDTO {

    private String[] types = new String[0];
    
    private String title;
    
    private String rangeAxisLabel = null;
    
    protected boolean usePercentages = false;
    
    
    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

	public String getRangeAxisLabel() {
		return rangeAxisLabel;
	}
	
	public void setRangeAxisLabel(String axisLabel) {
		this.rangeAxisLabel = axisLabel;
	}
    
    public boolean isUsePercentages() {
    	return usePercentages;
    }

    public void setUsePercentages(boolean usePercentages) {
    	this.usePercentages = usePercentages;
    }
	
    @Override
    public abstract org.psygrid.data.reporting.definition.hibernate.Chart toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs);
    
    public void toHibernate(org.psygrid.data.reporting.definition.hibernate.Chart hC, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hC, hRefs);
        List<String> hTypes = hC.getTypes();
        for ( int i=0; i<this.types.length; i++ ){
            hTypes.add(this.types[i]);
        }
        hC.setTitle(this.title);
        hC.setRangeAxisLabel(rangeAxisLabel);
        hC.setUsePercentages(usePercentages);
    }
    
}
