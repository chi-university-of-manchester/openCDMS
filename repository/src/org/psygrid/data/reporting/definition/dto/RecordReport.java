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
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.dto.PersistentDTO;
import org.psygrid.data.model.dto.RecordDTO;

public class RecordReport extends Report {

    private RecordChart[] charts = new RecordChart[0];
    
    private RecordDTO record = new RecordDTO();
    
    public RecordChart[] getCharts() {
        return charts;
    }

    public void setCharts(RecordChart[] charts) {
        this.charts = charts;
    }

    /**
	 * @return the record
	 */
	public RecordDTO getRecord() {
		return record;
	}

	/**
	 * @param record the record to set
	 */
	public void setRecord(RecordDTO record) {
		this.record = record;
	}
    
	  @Override
	   public org.psygrid.data.reporting.definition.hibernate.RecordReport toHibernate(){
	        Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
	        return toHibernate(hRefs);
	    }
	
    @Override
    public org.psygrid.data.reporting.definition.hibernate.RecordReport toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        //check for an already existing instance of a hibernate object for this 
        //report in the map of references
        org.psygrid.data.reporting.definition.hibernate.RecordReport hR = null;
        if ( hRefs.containsKey(this)){
            hR = (org.psygrid.data.reporting.definition.hibernate.RecordReport)hRefs.get(this);
        }
        else{
            //an instance of the report has not already
            //been created, so create it, and add it to the
            //map of references
            hR = new org.psygrid.data.reporting.definition.hibernate.RecordReport();
            hRefs.put(this, hR);
            toHibernate(hR, hRefs);
        }
        
        return hR;
    }

    public void toHibernate(org.psygrid.data.reporting.definition.hibernate.RecordReport hP, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        super.toHibernate(hP, hRefs);
        List<org.psygrid.data.reporting.definition.hibernate.RecordChart> hCharts = hP.getCharts();
        for (RecordChart c: charts){
            if ( null != c ){
                hCharts.add(c.toHibernate(hRefs));
            }
        }
        if (getRecord() != null) {
        	hP.setRecord(getRecord().toHibernate(hRefs));
        }
        
    }

}
