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
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.reporting.Chart;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.reporting.ChartRow;
import org.psygrid.data.reporting.ChartSeries;
import org.psygrid.data.reporting.definition.IGroupsSummaryChart;
import org.psygrid.data.reporting.definition.ReportException;

/**
 * Class to represent a chart in a management report that displays
 * the number of records with each status for the groups defined.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_grp_summ_charts"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class GroupsSummaryChart extends ManagementChart implements IGroupsSummaryChart {

	private static Log sLog = LogFactory.getLog(GroupsSummaryChart.class);
	
    /**
     * The groups in the dataset that will be featured in the chart.
     * <p>
     * This collection should be a subset of the groups associated
     * with the parent report (unless the parent report has no groups 
     * defined, which we take to mean all groups).
     */
    private List<Group> groups = new ArrayList<Group>();

    public GroupsSummaryChart() {
        super();
    }

    public GroupsSummaryChart(String type, String title) {
        super(type, title);
    }

    /**
     * Get the groups in the dataset that will be featured in the chart.
     * <p>
     * This collection should be a subset of the groups associated
     * with the parent report (unless the parent report has no groups 
     * defined, which we take to mean all groups).
     * 
     * @return The groups.
     * 
     * @hibernate.list cascade="none" 
     *                 table="t_grpsmchrt_groups"
     * @hibernate.key column="c_chart_id"
     * @hibernate.many-to-many class="org.psygrid.data.model.hibernate.Group"
     *                         column="c_group_id"
     * @hibernate.list-index column="c_index"
     */
    public List<Group> getGroups() {
        return groups;
    }

    /**
     * Set the groups in the dataset that will be featured in the chart.
     * <p>
     * This collection should be a subset of the groups associated
     * with the parent report (unless the parent report has no groups 
     * defined, which we take to mean all groups).
     * 
     * @param groups The groups.
     */
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public int numGroups() {
        return groups.size();
    }

    public void addGroup(Group group) throws ReportException {
        if (null == group){
            throw new ReportException("Cannot add a null group");
        }
        this.groups.add((Group)group);
    }

    public Group getGroup(int index) throws ReportException {
        try{
            return this.groups.get(index);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ReportException("No group exists for index="+index, ex);
        }
    }

	protected void setAllowedStates() {
		allowedStates.add(GenericState.ACTIVE.toString());
		allowedStates.add(GenericState.COMPLETED.toString());
		allowedStates.add(GenericState.REFERRED.toString());
		allowedStates.add(GenericState.LEFT.toString());
	}

    
    @Override
    public Chart generateChart(Session session) {


        //create string for "where r.identifier.groupPrefix in" clause
        List<Group> groups = this.getGroups();
        
        if ( 0 == groups.size() ){
        	//Hibernate will throw an exception for the query below if there are no groups
        	//so just log the error here and return null
        	sLog.error("Cannot generate chart - no groups have been defined (chart id = "+this.getId()+")");
        	return null;
        }
        
        StringBuilder whereInClause = new StringBuilder();
        whereInClause.append("(");
        for ( int i=0; i<groups.size(); i++ ){
            if ( i > 0 ){
                whereInClause.append(", ");
            }
            whereInClause.append("'").append(groups.get(i).getName()).append("'");
        }
        whereInClause.append(") ");
        
        String hql = 
            "select r.status.longName, count(r) from Record r "+
            "where r.dataSet.id=? "+
			"and r.deleted=? "+
			"and r.status.enumGenericState in (:states) "+
            "and r.identifier.groupPrefix in "+whereInClause.toString()+
            "group by r.status.longName order by r.status.longName";
        
        //get counts for statuses of records
        List results = session.createQuery(hql)
                              .setLong(0, this.getReport().getDataSet().getId())
                              .setBoolean(1, false)
					  		  .setParameterList("states", getAllowedStates())
                              .list();
        
        org.psygrid.data.reporting.Chart chart = new org.psygrid.data.reporting.Chart();
        chart.setTitle(this.title);
        chart.setRangeAxisLabel(this.rangeAxisLabel);
        chart.setUsePercentages(usePercentages);
        String[] types = new String[this.types.size()];
        for ( int i=0; i<this.types.size(); i++ ){
            types[i] = this.types.get(i);
        }
        chart.setTypes(types);

        chart.setRows(new ChartRow[1]);
        ChartRow row = new ChartRow();
        chart.getRows()[0] = row;
        row.setLabel("Clients");
        row.setLabelType(IValue.TYPE_STRING);
        row.setSeries(new ChartSeries[results.size()]);
        
        for ( int i=0; i<results.size(); i++ ){            
            Object[] data = (Object[])results.get(i);
            
            ChartSeries s = new ChartSeries();
            row.getSeries()[i] = s;
            s.setLabel((String)data[0]);
            s.setLabelType(IValue.TYPE_STRING);
            
            ChartPoint point = new ChartPoint();
            s.setPoints(new ChartPoint[1]);
            s.getPoints()[0] = point;
            point.setValue(data[1].toString());
            point.setValueType(IValue.TYPE_INTEGER);
        }
        
        
        return chart;
    }
    
    @Override
    public org.psygrid.data.reporting.definition.dto.GroupsSummaryChart toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        //check for an already existing instance of a dto object for this 
        //management chart in the map of references
        org.psygrid.data.reporting.definition.dto.GroupsSummaryChart dtoMC = null;
        if ( dtoRefs.containsKey(this)){
            dtoMC = (org.psygrid.data.reporting.definition.dto.GroupsSummaryChart)dtoRefs.get(this);
        }
        else {
            //an instance of the management chart has not already
            //been created, so create it, and add it to the
            //map of references
            dtoMC = new org.psygrid.data.reporting.definition.dto.GroupsSummaryChart();
            dtoRefs.put(this, dtoMC);
            toDTO(dtoMC, dtoRefs, depth);
        }
        
        return dtoMC;
    }

    public void toDTO(org.psygrid.data.reporting.definition.dto.GroupsSummaryChart dtoC, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        super.toDTO(dtoC, dtoRefs, depth);
        org.psygrid.data.model.dto.GroupDTO[] dtoGroups = new org.psygrid.data.model.dto.GroupDTO[this.groups.size()];
        for (int i=0; i<this.groups.size(); i++){
            Group g = groups.get(i);
            dtoGroups[i] = g.toDTO(dtoRefs, depth);
        }        
        dtoC.setGroups(dtoGroups);
    }

}
