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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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
import org.psygrid.data.reporting.definition.IUserSummaryChart;
import org.psygrid.data.reporting.definition.ReportException;

/**
 * Class to represent a chart in a management report that displays
 * the number of records with each status by the user who first
 * created the record, for the groups defined.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_user_summ_charts"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class UserSummaryChart extends ManagementChart implements IUserSummaryChart {

    private static final String DIST_NAME_PREFIX = "CN=";
    
    /**
     * The groups in the dataset that will be featured in the chart.
     * <p>
     * This collection should be a subset of the groups associated
     * with the parent report (unless the parent report has no groups 
     * defined, which we take to mean all groups).
     */
    private List<Group> groups = new ArrayList<Group>();

    public UserSummaryChart(){
        super();
    }
    
    public UserSummaryChart(String type, String title) {
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
     *                 table="t_usrsmchrt_groups"
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
		//not currently used as all states are shown
		allowedStates.add(GenericState.ACTIVE.toString());
		allowedStates.add(GenericState.COMPLETED.toString());
		allowedStates.add(GenericState.REFERRED.toString());
		allowedStates.add(GenericState.LEFT.toString());
		allowedStates.add(GenericState.INACTIVE.toString());
		allowedStates.add(GenericState.INVALID.toString());
	}

    
    @Override
    public Chart generateChart(Session session) {

        List<Group> groups = this.getGroups();
        //Build up the HQL
//        StringBuilder hqlBuilder = new StringBuilder();
//        hqlBuilder.append("select r.status.longName, r.createdBy, count(r) from Record r ");
//        hqlBuilder.append("where r.dataSet.id=? ");
//        hqlBuilder.append("and r.deleted=? ");
//        hqlBuilder.append("and r.identifier.groupPrefix in (");
        StringBuilder hqlBuilder = new StringBuilder();
        hqlBuilder.append("select r.status.longName, ch.user, count(r) from Record r, ChangeHistory ch ");
        hqlBuilder.append("where r.dataSet.id=? ");
        hqlBuilder.append("and ch=r.history[0] ");        
        hqlBuilder.append("and r.deleted=? ");
        hqlBuilder.append("and r.identifier.groupPrefix in (");
        for ( int i=0; i<groups.size(); i++ ){
            if ( i > 0 ){
                hqlBuilder.append(", ");
            }
            hqlBuilder.append("'").append(groups.get(i).getName()).append("'");
        }
        hqlBuilder.append(") ");        
        hqlBuilder.append("group by r.status.longName, ch.user order by r.status.longName, ch.user");
        
        //execute the query
        List results = session.createQuery(hqlBuilder.toString())
                              .setLong(0, this.getReport().getDataSet().getId())
                              .setBoolean(1, false)
                              .list();
        
        //map of record status to (map of user who created the record to the number of 
        //records with this status and this creator)
        Map<String, Map<String,Integer>> resultsMap = new HashMap<String, Map<String,Integer>>();
        //set of all users
        Set<String> users = new LinkedHashSet<String>();
        
        //populate the above map with the results of the query
        for ( int i=0; i<results.size(); i++ ){
            Object[] data = (Object[])results.get(i);
            //add the user to the set of all users
            String username = getUserName((String)data[1]);
            users.add(username);
            if ( !resultsMap.containsKey(data[0]) ){
                Map<String, Integer> map = new HashMap<String, Integer>();
                resultsMap.put((String)data[0], map);
                map.put(username, Integer.parseInt(data[2].toString()));
            }
            else{
                Map<String, Integer> map = resultsMap.get(data[0]);
                map.put(username, Integer.parseInt(data[2].toString()));
            }
        }
        
        //finally, convert the data in the map into the a Chart object
        org.psygrid.data.reporting.Chart chart = new org.psygrid.data.reporting.Chart();
        chart.setTitle(this.title);
        chart.setRangeAxisLabel(this.rangeAxisLabel);
        chart.setUsePercentages(usePercentages);
        String[] types = new String[this.types.size()];
        for ( int i=0; i<this.types.size(); i++ ){
            types[i] = this.types.get(i);
        }
        chart.setTypes(types);
        String[] labels = new String[users.size()];
        //chart.setSeriesLabels(labels);
        int counter = 0;
        for ( String user: users){
            labels[counter] = user;
            counter++;
        }
        ChartRow[] rows = new ChartRow[resultsMap.size()];
        chart.setRows(rows);
        counter = 0;
        for ( Entry<String, Map<String, Integer>> entry: resultsMap.entrySet() ){
            String status = entry.getKey();
            Map<String, Integer> map = entry.getValue();
            
            ChartRow row = new ChartRow();
            rows[counter] = row;
            row.setLabel(status);
            row.setLabelType(IValue.TYPE_STRING);
            
            //creates a series/group for each user
            ChartSeries[] series = new ChartSeries[users.size()];
            row.setSeries(series);
            
            int i = 0;
            for (String user: users) {
            	ChartPoint[] points = new ChartPoint[1];
            	points[0] = new ChartPoint();
            	
            	ChartSeries s = new ChartSeries();
                s.setLabel(user);
                s.setPoints(points);
                row.getSeries()[i] = s;
            	
            	//add a point for each user - if no value exists in the map for the
                //user then we just add a zero so as not to leave gaps in the data
            	Integer value = map.get(user);
                if ( null == value ){
                    points[0].setValue("0");
                }
                else{
                    points[0].setValue(value.toString());
                }
            	points[0].setValueType(IValue.TYPE_INTEGER);
            		               
                i++;
            }
            
        }
        
        return chart;
    }
    
    /**
     * Get just the username from an LDAP distinguished name. If the input
     * string does not appear to be a distinguished name then it is just 
     * returned as is.
     * 
     * @param distinguishedName LDAP distinguished name
     * @return User name
     */
    private String getUserName(String distinguishedName){
        if ( distinguishedName.startsWith(DIST_NAME_PREFIX) ){
            //this is an LDAP distinguished name, we want to extract just
            //the username from it
            return distinguishedName.substring(DIST_NAME_PREFIX.length(), distinguishedName.indexOf(","));
        }
        else{
            //not an LDAP distinguished name, just return it as is
            return distinguishedName;
        }
    }
    
    @Override
    public org.psygrid.data.reporting.definition.dto.UserSummaryChart toDTO(
            Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs,
            RetrieveDepth depth) {
        //check for an already existing instance of a dto object for this 
        //management chart in the map of references
        org.psygrid.data.reporting.definition.dto.UserSummaryChart dtoMC = null;
        if ( dtoRefs.containsKey(this)){
            dtoMC = (org.psygrid.data.reporting.definition.dto.UserSummaryChart)dtoRefs.get(this);
        }
        else {
            //an instance of the management chart has not already
            //been created, so create it, and add it to the
            //map of references
            dtoMC = new org.psygrid.data.reporting.definition.dto.UserSummaryChart();
            dtoRefs.put(this, dtoMC);
            toDTO(dtoMC, dtoRefs, depth);
        }
        
        return dtoMC;
    }

    public void toDTO(org.psygrid.data.reporting.definition.dto.UserSummaryChart dtoC, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        super.toDTO(dtoC, dtoRefs, depth);
        org.psygrid.data.model.dto.GroupDTO[] dtoGroups = new org.psygrid.data.model.dto.GroupDTO[this.groups.size()];
        for (int i=0; i<this.groups.size(); i++){
            Group g = groups.get(i);
            dtoGroups[i] = g.toDTO(dtoRefs, depth);
        }        
        dtoC.setGroups(dtoGroups);
    }

}
