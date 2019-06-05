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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.model.hibernate.Value;
import org.psygrid.data.reporting.Chart;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.reporting.ChartRow;
import org.psygrid.data.reporting.ChartSeries;
import org.psygrid.data.reporting.definition.IBasicStatisticsChart;
import org.psygrid.data.reporting.stats.StatsHelper;

/**
 * Management chart to display basic statistics on the responses
 * to user-selected entries of a dataset.
 * <p>
 * The entries to generate stats on are selected at runtime, and
 * the data to analyse can also be restricted by group.
 * <p>
 * The statistics that are currently supported are mean, median, 
 * mode, min and max. Note that if multiple entries are selected
 * then it is only possible to generate the same statistic for all
 * i.e. you can't request mean of entry 1, mode of entry 2 etc.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_basic_stats_charts"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class BasicStatisticsChart extends ManagementChart implements IBasicStatisticsChart {

	private static final Log LOG = LogFactory.getLog(BasicStatisticsChart.class);

	public static final String STAT_MEAN = "Mean";
	public static final String STAT_MODE = "Mode";
	public static final String STAT_MEDIAN = "Median";
	public static final String STAT_MIN = "Min";
	public static final String STAT_MAX = "Max";
	
	/**
	 * The groups of the dataset to generate stats for.
	 */
	private List<Group> groups = new ArrayList<Group>();

	/**
	 * The list of entry ids to generate statistics for.
	 */
	private List<Long> entryIds = new ArrayList<Long>();
	
	/**
	 * The list of statistics to generate.
	 */
	private List<String> statistics = new ArrayList<String>();
	
	public BasicStatisticsChart() {
		super();
		setAllowedStates();
	}

	public BasicStatisticsChart(String type, String title) {
		super(type, title);
		setAllowedStates();
	}

	public List<Long> getEntryIds() {
		return entryIds;
	}

	public void setEntryIds(List<Long> entryIds) {
		this.entryIds = entryIds;
	}

	public void addEntryId(Long entryId){
		entryIds.add(entryId);
	}
	
	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<String> getStatistics() {
		return statistics;
	}

	public void setStatistics(List<String> statistics) {
		this.statistics = statistics;
	}
	
	public void addStatistic(String statistic){
		this.statistics.add(statistic);
	}

	@Override
	public Chart generateChart(Session session) {
		
		LOG.info("Generating basic statistics chart...");
		
		//get the relevant groups as specified
		List<String> grps = new ArrayList<String>();		
		for (Group g: getGroups()) {
			grps.add(g.getName());
		}
		//if no groups have been specified retrieve groups for the report
		if (getGroups() == null || getGroups().size() == 0) {
			for (Group g: getReport().getGroups()) {
				grps.add(g.getName());
			}
			//if still no groups add all for the dataset
			if (grps.size() == 0) {
				for (Group g: getReport().getDataSet().getGroups()) {
					grps.add(g.getName());
				}
			}
		}

		LOG.info("Groups: "+grps);
		
		List result = session.createQuery("select r.theValue, r.entry.id, r.entry.displayText from BasicResponse r " +
										  "where r.entry.id in (:entries) " +
										  "and r.record.identifier.groupPrefix in (:groups) " +
										  "and r.record.status.enumGenericState in (:states) " +
										  "order by r.entry.id")
							 .setParameterList("entries", this.entryIds)
							 .setParameterList("groups", grps)
							 .setParameterList("states", getAllowedStates())
							 .list();
		
		//create map of entry text to list of values for that entry
		Map<String, List<Double>> data = new LinkedHashMap<String, List<Double>>();
		for ( Long entryId: entryIds ){
			boolean added = false;
			List<Double> currentData = null;
			for ( Object o: result ){
				Object[] row = (Object[])o;
				if ( ((Long)row[1]).equals(entryId) ){
					if ( !added ){
						currentData = new ArrayList<Double>();
						data.put((String)row[2], currentData);
						added = true;
					}
					currentData.add(((Value)row[0]).getValueForStats());
				}
			}
		}
		
		//create chart
		org.psygrid.data.reporting.Chart chart = new org.psygrid.data.reporting.Chart();
		chart.setTitle(this.title);
		chart.setRangeAxisLabel(rangeAxisLabel);
		chart.setUsePercentages(usePercentages);
		String[] types = new String[this.types.size()];
		for ( int i=0; i<this.types.size(); i++ ){
			types[i] = this.types.get(i);
		}
		chart.setTypes(types);

		chart.setRows(new ChartRow[this.entryIds.size()]);

		int counter = 0;
		for (Entry<String, List<Double>> entry: data.entrySet()){
			//add a row to the chart for each entry
			ChartRow row = new ChartRow();
			chart.getRows()[counter] = row;
			row.setLabel(entry.getKey());
			row.setLabelType(IValue.TYPE_STRING);
			row.setSeries(new ChartSeries[statistics.size()]);
			int statCounter = 0;
			for ( String stat: statistics ){
				//add a series for each type of statistic required
				ChartSeries series = new ChartSeries();
				row.getSeries()[statCounter] = series;
				series.setLabel(stat);
				series.setLabelType(IValue.TYPE_STRING);
				series.setPoints(new ChartPoint[1]);
				ChartPoint point = new ChartPoint();
				series.getPoints()[0] = point;
				point.setValueType(IValue.TYPE_DOUBLE);
				
				if ( stat.equals(STAT_MEAN) ){
					Double mean = StatsHelper.calculateMean(entry.getValue());
					if ( null == mean ){
						point.setValue(null);
					}
					else{
						point.setValue(mean.toString());
					}
				}
				if ( stat.equals(STAT_MEDIAN) ){
					Double median = StatsHelper.calculateMedian(entry.getValue());
					if ( null == median ){
						point.setValue(null);
					}
					else{
						point.setValue(median.toString());
					}
				}
				if ( stat.equals(STAT_MODE) ){
					Double mode = StatsHelper.calculateMode(entry.getValue());
					if ( null == mode ){
						point.setValue(null);
					}
					else{
						point.setValue(mode.toString());
					}
				}
				if ( stat.equals(STAT_MIN) ){
					Double min = StatsHelper.calculateMin(entry.getValue());
					if ( null == min ){
						point.setValue(null);
					}
					else{
						point.setValue(min.toString());
					}
				}
				if ( stat.equals(STAT_MAX) ){
					Double max = StatsHelper.calculateMax(entry.getValue());
					if ( null == max ){
						point.setValue(null);
					}
					else{
						point.setValue(max.toString());
					}
				}
				
				statCounter++;
			}
			
			counter++;
		}
		
		return chart;

	}

	@Override
	protected void setAllowedStates() {
		allowedStates.add(GenericState.ACTIVE.toString());
		allowedStates.add(GenericState.COMPLETED.toString());
		allowedStates.add(GenericState.LEFT.toString());
	}

	@Override
	public org.psygrid.data.reporting.definition.dto.BasicStatisticsChart toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
		//chart in the map of references
		org.psygrid.data.reporting.definition.dto.BasicStatisticsChart dtoMC = null;
		if ( dtoRefs.containsKey(this)){
			dtoMC = (org.psygrid.data.reporting.definition.dto.BasicStatisticsChart)dtoRefs.get(this);
		}
		else {
			//an instance of chart has not already
			//been created, so create it, and add it to the
			//map of references
			dtoMC = new org.psygrid.data.reporting.definition.dto.BasicStatisticsChart();
			dtoRefs.put(this, dtoMC);
			toDTO(dtoMC, dtoRefs, depth);
		}

		return dtoMC;
	}

	public void toDTO(org.psygrid.data.reporting.definition.dto.BasicStatisticsChart dtoC, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoC, dtoRefs, depth);

		org.psygrid.data.model.dto.GroupDTO[] dtoGroups = new org.psygrid.data.model.dto.GroupDTO[this.groups.size()];
		for (int i=0; i<this.groups.size(); i++){
			Group g = groups.get(i);
			dtoGroups[i] = g.toDTO(dtoRefs, depth);
		}        
		dtoC.setGroups(dtoGroups);

		Long[] dtoEntryIds = new Long[this.entryIds.size()];
		for ( int i=0, c=entryIds.size(); i<c; i++ ){
			dtoEntryIds[i] = entryIds.get(i);
		}
		dtoC.setEntryIds(dtoEntryIds);
		
		String[] dtoStats = new String[statistics.size()];
		for ( int i=0, c=statistics.size(); i<c; i++ ){
			dtoStats[i] = statistics.get(i);
		}
		dtoC.setStatistics(dtoStats);
		
	}

}
