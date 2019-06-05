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

package org.psygrid.data.reporting.definition.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.reporting.definition.IRecordReport;
import org.psygrid.data.reporting.definition.IRecordChart;
import org.psygrid.data.reporting.definition.ReportException;

/**
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_rec_reports"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class RecordReport extends Report implements IRecordReport {

    /**
     * The collection of charts contained by the report.
     */
    private List<RecordChart> charts = new ArrayList<RecordChart>();
    
    private Record record = null;
    
    public RecordReport() {
        super();
    }

    public RecordReport(DataSet ds, String title) {
        super(ds, title);
    }

    /**
     * 
     * @return
     * 
     * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.data.reporting.definition.hibernate.RecordChart"
     * @hibernate.key column="c_report_id" not-null="true"
     * @hibernate.list-index column="c_index"
     */
    public List<RecordChart> getCharts() {
        return charts;
    }

    public void setCharts(List<RecordChart> charts) {
        this.charts = charts;
    }

    public void addChart(IRecordChart chart) throws ReportException {
        if (null == chart){
            throw new ReportException("Cannot add a null chart");
        }
        this.charts.add((RecordChart)chart);
    }

    public IRecordChart getChart(int index) throws ReportException {
        try{
            return this.charts.get(index);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ReportException("No chart exists for index="+index, ex);
        }
    }

    public int numCharts() {
        return this.charts.size();
    }

    /**
	 * @return the record
	 * 
	 * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Record"
	 *                        column="c_record_id"
	 *                        not-null="false"
	 *                        update="false"
	 *                        insert="false"
	 */
	public Record getRecord() {
		return record;
	}

	/**
	 * @param record the record to set
	 */
	public void setRecord(Record record) {
		this.record = record;
	}

	public org.psygrid.data.reporting.definition.dto.RecordReport toDTO(){
        return toDTO(RetrieveDepth.REP_SAVE);
    }
    
    public org.psygrid.data.reporting.definition.dto.RecordReport toDTO(RetrieveDepth depth){
        Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
        return toDTO(dtoRefs, depth);
    }
	
	@Override
    public org.psygrid.data.reporting.definition.dto.RecordReport toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        //check for an already existing instance of a dto object for this 
        //report in the map of references
        org.psygrid.data.reporting.definition.dto.RecordReport dtoR = null;
        if ( dtoRefs.containsKey(this)){
            dtoR = (org.psygrid.data.reporting.definition.dto.RecordReport)dtoRefs.get(this);
        }
        if ( null == dtoR ){
            //an instance of the report has not already
            //been created, so create it, and add it to the
            //map of references
            dtoR = new org.psygrid.data.reporting.definition.dto.RecordReport();
            dtoRefs.put(this, dtoR);
            toDTO(dtoR, dtoRefs, depth);
        }
        
        return dtoR;
    }

    public void toDTO(org.psygrid.data.reporting.definition.dto.RecordReport dtoR, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        super.toDTO(dtoR, dtoRefs, depth);
        org.psygrid.data.reporting.definition.dto.RecordChart[] dtoCharts = 
            new org.psygrid.data.reporting.definition.dto.RecordChart[this.charts.size()];
        for (int i=0; i<this.charts.size(); i++){
            RecordChart c = charts.get(i);
            dtoCharts[i] = c.toDTO(dtoRefs, depth);
        }        
        dtoR.setCharts(dtoCharts);
        
        if (getRecord() != null) {
        	dtoR.setRecord(((Record)getRecord()).toDTO(dtoRefs, depth));
        }
    }
    
}
