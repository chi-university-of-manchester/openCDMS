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
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.reporting.definition.ITrendsChart;
import org.psygrid.data.reporting.definition.ITrendsReport;
import org.psygrid.data.reporting.definition.ReportException;

/**
 * Class to represent a "trends" report.
 * <p>
 * A trends report provides a graphical summary 
 * of data from all documents of specified 
 * types, which have been entered into a data set
 * over a given time period.
 * 
 * @author Lucy Bridges
 *
 * @hibernate.joined-subclass table="t_trends_reports"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class TrendsReport extends Report implements ITrendsReport {

    /**
     * The collection of charts contained by the report.
     */
    private List<ITrendsChart> trendsCharts = new ArrayList<ITrendsChart>();
    
    public TrendsReport() {
    }

    public TrendsReport(DataSet ds, String title) {
        super(ds, title);
    }

    /**
     * 
     * @return charts
     * 
     * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.data.reporting.definition.hibernate.TrendsChart"
     * @hibernate.key column="c_trend_report_id" not-null="true"
     * @hibernate.list-index column="c_trends_index"
     */
    public List<ITrendsChart> getTrendsCharts() {
        return trendsCharts;
    }

    public void setTrendsCharts(List<ITrendsChart> trendsCharts) {
        this.trendsCharts = trendsCharts;
    }

    public void addChart(ITrendsChart chart) throws ReportException {
        if (null == chart){
            throw new ReportException("Cannot add a null chart");
        }
        this.trendsCharts.add(chart);
    }

    public ITrendsChart getChart(int index) throws ReportException {
        try{
            return this.trendsCharts.get(index);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ReportException("No chart exists for index="+index, ex);
        }
    }

    public int numCharts() {
        return this.trendsCharts.size();
    }

	public org.psygrid.data.reporting.definition.dto.TrendsReport toDTO(){
        return toDTO(RetrieveDepth.REP_SAVE);
    }
    
    public org.psygrid.data.reporting.definition.dto.TrendsReport toDTO(RetrieveDepth depth){
        Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
        return toDTO(dtoRefs, depth);
    }
    
    @Override
    public org.psygrid.data.reporting.definition.dto.TrendsReport toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        //check for an already existing instance of a dto object for this 
        //report in the map of references
        org.psygrid.data.reporting.definition.dto.TrendsReport dtoR = null;
        if ( dtoRefs.containsKey(this)){
            dtoR = (org.psygrid.data.reporting.definition.dto.TrendsReport)dtoRefs.get(this);
        }
        if ( null == dtoR ){
            //an instance of the report has not already
            //been created, so create it, and add it to the
            //map of references
            dtoR = new org.psygrid.data.reporting.definition.dto.TrendsReport();
            dtoRefs.put(this, dtoR);
            toDTO(dtoR, dtoRefs, depth);
        }
        
        return dtoR;
    }

    public void toDTO(org.psygrid.data.reporting.definition.dto.TrendsReport dtoR, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        super.toDTO(dtoR, dtoRefs, depth);
        org.psygrid.data.reporting.definition.dto.TrendsChart[] dtoCharts = 
            new org.psygrid.data.reporting.definition.dto.TrendsChart[this.getTrendsCharts().size()];
        for (int i=0; i<this.getTrendsCharts().size(); i++){
        	ITrendsChart c = getTrendsCharts().get(i);
            dtoCharts[i] = c.toDTO(dtoRefs, depth);
        }        
        dtoR.setCharts(dtoCharts);
    }
    
}
