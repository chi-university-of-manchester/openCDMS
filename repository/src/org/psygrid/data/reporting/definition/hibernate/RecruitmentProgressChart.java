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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
import org.psygrid.data.reporting.definition.IRecruitmentProgressChart;
import org.psygrid.data.reporting.definition.Pair;
import org.psygrid.data.reporting.definition.ReportException;


/**
 * A management chart comparing the number of subjects consented
 * into the trial (as a cumulative total) against the targets 
 * set for each month, giving a view of the trial's progress.
 * 
 * The chart can be displayed by group, or whole project if no 
 * group is specified, and is for the provided time period.
 * 
 * @author Lucy Bridges
 *
 * @hibernate.joined-subclass table="t_recruitment_progress_charts"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class RecruitmentProgressChart extends ManagementChart implements IRecruitmentProgressChart {

	/**
	 * The period of time the chart is to cover
	 */
	private Calendar startDate = new GregorianCalendar(0, 0, 0);
	private Calendar endDate   = new GregorianCalendar(0, 0, 0);

	/**
	 * The recruitment targets for each month
	 */
	private Map<Calendar, Integer> targets = new LinkedHashMap<Calendar, Integer>();

	private List<Group> groups = new ArrayList<Group>();

	public RecruitmentProgressChart() {
		super();
	}

	public RecruitmentProgressChart(String type, String title) {
		super(type, title);
	}

	/**
	 * Get the chart's end date
	 * 
	 * @return the endDate
	 * 
	 * @hibernate.property column="c_end_date" type="java.util.Calendar"
	 */
	public Calendar getEndDate() {
		return endDate;
	}

	/**
	 * Set the chart's end date
	 * 
	 * Ignores fields other than month and year
	 * 
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Calendar endDate) {
		if ( endDate == null ){
			this.endDate = new GregorianCalendar(0, 0, 0);
		}
		else {
			endDate.clear(Calendar.MILLISECOND);
			endDate.clear(Calendar.SECOND);
			endDate.clear(Calendar.MINUTE);
			//Set the hour to 3am to avoid daylight savings problems
			endDate.set(Calendar.HOUR_OF_DAY, 3);
			endDate.clear(Calendar.DATE);
			endDate.clear(Calendar.DAY_OF_MONTH);
			endDate.setTimeZone(TimeZone.getTimeZone("GMT"));
			this.endDate = endDate;
		}
	}

	/**
	 * Get the chart's start date
	 * 
	 * @return the startDate
	 * 
	 * @hibernate.property column="c_start_date" type="java.util.Calendar"
	 */
	public Calendar getStartDate() {
		return startDate;
	}

	/**
	 * Set the start date for the chart
	 * 
	 * Fields other than month and year are ignored
	 * 
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Calendar startDate) {
		if ( startDate == null ){
			this.startDate = new GregorianCalendar(0, 0, 0);
		}
		else {
			startDate.clear(Calendar.MILLISECOND);
			startDate.clear(Calendar.SECOND);
			startDate.clear(Calendar.MINUTE);
			//Set the hour to 3am to avoid daylight savings problems
			startDate.set(Calendar.HOUR_OF_DAY, 3);
			startDate.clear(Calendar.DATE);
			startDate.clear(Calendar.DAY_OF_MONTH);
			startDate.setTimeZone(TimeZone.getTimeZone("GMT"));
			this.startDate = startDate;
		}
	}

	/**
	 * Set the period of time the chart is to cover.
	 * 
	 * Note: fields other than month and year are ignored
	 * 
	 * 
	 * @param startDate
	 * @param endDate
	 */
	public void setTimePeriod(Calendar startDate, Calendar endDate) {
		this.setEndDate(endDate);
		this.setStartDate(startDate);
	}

	/**
	 * Get the target figures for recruitment
	 * 
	 * @return the targets
	 * 
	 * @hibernate.map cascade="all" table="t_targets" order-by="c_month asc"
	 * @hibernate.key column="c_target_id" not-null="true"
	 * @hibernate.map-key column="c_month"
	 *                    type="java.util.Calendar"
	 * @hibernate.element column="c_value"
	 *                    type="int"
	 */
	public Map<Calendar, Integer> getTargets() {
		return targets;
	}

	/**
	 * Set the target figures for recruitment for each month,
	 * as a map of Calendar -> Integer
	 * 
	 * Note: the dates should be created as follows for
	 * accurate rendering (with any timezone and additional information
	 * removed):
	 * 
	 * Calendar startDate = new GregorianCalendar(myYear, myMonth, 0);
	 * 
	 * @param targets
	 */
	public void setTargets(Map<Calendar, Integer> targets) {
		this.targets = targets;
	}

	/**
	 * Set a target for a given month
	 * 
	 * Note: Fields other than month and year will be ignored
	 * 
	 * @param target
	 */
	public void addTarget(Calendar month, Integer target) {
		month.clear(Calendar.MILLISECOND);
		month.clear(Calendar.SECOND);
		month.clear(Calendar.MINUTE);
		//Set the hour to 3am to avoid daylight savings problems
		month.set(Calendar.HOUR_OF_DAY, 3);
		month.clear(Calendar.DATE);
		month.clear(Calendar.DAY_OF_MONTH);
		month.setTimeZone(TimeZone.getTimeZone("GMT"));
		targets.put(month, target);
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
	 *                 table="t_recuitmentchrt_groups"
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
		this.groups.add(group);
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
		allowedStates.add(GenericState.LEFT.toString());
	}


	/**
	 * Retrieves the recruitment rates from the database, based on the date
	 * a record was created, to populate a org.psygrid.data.reporting.Chart 
	 * object used in rendering the report.
	 */
	@Override
	public Chart generateChart(Session session) {

		Calendar startDate = getStartDate();
		Calendar endDate   = getEndDate();

		/*
		 * If dates haven't been set or have been set incorrectly, then
		 * use a default value.
		 * (If dates have been set then the Year field will have been
		 * set to zero) 
		 */
		if (startDate == null || endDate == null
				|| startDate.get(Calendar.YEAR) == 0001
				|| endDate.get(Calendar.YEAR) == 0001
				|| startDate.get(Calendar.YEAR) == 0002
				|| endDate.get(Calendar.YEAR) == 0002
				|| endDate.before(startDate)) {
			Calendar[] dates = getSixMonths();
			startDate = dates[0];
			endDate   = dates[1];
		}
				
		//create date for database comparison (add one to month to find dates with the same month)
		Calendar endDate2  = new GregorianCalendar(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH)+1, 0);
		endDate2.setTimeZone(TimeZone.getTimeZone("GMT"));
		//get the relevant groups as specified
		List<Long> grps = new ArrayList<Long>();		
		for (Group g: getGroups()) {
			grps.add(g.getId());
		}
		//if no groups have been specified retrieve groups for the report
		if (getGroups() == null || getGroups().size() == 0) {
			for (Group g: getReport().getGroups()) {
				grps.add(g.getId());
			}
			//if still no groups add all for the dataset
			if (grps.size() == 0) {
				for (Group g: getReport().getDataSet().getGroups()) {
					grps.add(g.getId());
				}
			}
		}
		
		/* 
		 * Get number of new subjects entered each month, based on the date consent was saved
		 * to the database (i.e this would create a new record, so look at when a record was first saved)
		 */

		/* 
		 * Retrieve the number of records created for each month between the start and end dates specified in the chart.
		 * This needs to be done using subqueries, otherwise the HQL returns an incorrect number of records.
		 */
//		List results = session.createQuery("select month(r.created), year(r.created), count(r) from Record r "+
//				" where r.id in (select r.id from Record r "+
//				" where r.dataSet.id = :id and r.deleted=:deleted and r.status.enumGenericState in (:states) )"+
//				// link a record to a group using its identifier
//				" and r.identifier.id in (select i.id from Identifier i where i.groupPrefix in " +
//				" (select g.name from Group g where g.id in (:grps)) )"+
//				" and r.created between :first and :last "+
//				" group by month(r.created), year(r.created)" +
//				" order by year(r.created), month(r.created)")

		List results = session.createQuery("select month(ch.when), year(ch.when), count(r) from Record r, ChangeHistory ch "+
				" where r.id in (select r.id from Record r "+
				" where r.dataSet.id = :id and r.deleted=:deleted and r.status.enumGenericState in (:states) )"+
				// link a record to a group using its identifier
				" and r.identifier.id in (select i.id from Identifier i where i.groupPrefix in " +
				" (select g.name from Group g where g.id in (:grps)) )"+
				" and ch=r.history[0] "+ 
				" and ch.when between :first and :last "+
				" group by month(ch.when), year(ch.when)" +
				" order by year(ch.when), month(ch.when)")

		.setLong("id", getReport().getDataSet().getId())
		.setBoolean("deleted", false)
		.setParameterList("grps", grps)
		.setCalendar("first", startDate)
		.setCalendar("last", endDate2)
		.setParameterList("states", getAllowedStates())
		.list();


		/*
		 * Generate the chart and enter the query results
		 */
		org.psygrid.data.reporting.Chart chart = new org.psygrid.data.reporting.Chart();
		chart.setTitle(this.title);
		chart.setRangeAxisLabel(rangeAxisLabel);
		chart.setUsePercentages(usePercentages);
		String[] types = new String[this.types.size()];
		for ( int i=0; i<this.types.size(); i++ ){
			types[i] = this.types.get(i);
		}
		chart.setTypes(types);


		chart.setRows(new ChartRow[2]);

		//Add the figures retrieved from the database, based on the
		//numbers of newly created records
		ChartRow row = new ChartRow();
		chart.getRows()[0] = row;
		row.setLabel("Consented");
		row.setLabelType(IValue.TYPE_STRING);
		row.setSeries(new ChartSeries[results.size()]);

		int totalSoFar = 0;
		for ( int i=0; i<results.size(); i++ ){

			Object[] data = (Object[])results.get(i);

			ChartSeries s = new ChartSeries();
			s.setLabel(data[0].toString()+" "+data[1].toString());
			s.setLabelType(IValue.TYPE_DATE);
			row.getSeries()[i] = s;

			ChartPoint point = new ChartPoint();
			s.setPoints(new ChartPoint[1]);
			s.getPoints()[0] = point;

			try {
				totalSoFar += Integer.parseInt(data[2].toString());
			}
			catch (Exception e) {
				//Value shouldn't be null, but if it is totalSoFar isn't incremented
			}
			point.setValue(Integer.toString(totalSoFar));
			point.setValueType(IValue.TYPE_INTEGER);
		}

		//Add the relevant target figures to the chart
		ChartRow row2 = new ChartRow();
		chart.getRows()[1] = row2;
		row2.setLabel("Target");
		row2.setLabelType(IValue.TYPE_STRING);

		List<ChartSeries> targetSeries = new ArrayList<ChartSeries>();

		for ( Calendar date: getTargets().keySet() ){

			if ( dateBetween(startDate, endDate, date) ) {
				ChartPoint point = new ChartPoint();
				if (getTargets().get(date) == null) {
					point.setValue(null);
				}
				else {
					point.setValue(getTargets().get(date).toString());
				}
				point.setValueType(IValue.TYPE_INTEGER);

				ChartSeries s = new ChartSeries();
				//add one to month as java.util.Calendar months start at 0
				s.setLabel((date.get(Calendar.MONTH)+1)+" "+date.get(Calendar.YEAR));		
				s.setLabelType(IValue.TYPE_DATE);
				s.setPoints(new ChartPoint[1]);
				s.getPoints()[0] = point;

				targetSeries.add(s);
			}
		}
		//add the series onto the row
		row2.setSeries(new ChartSeries[targetSeries.size()]);
		for (int i = 0; i < targetSeries.size(); i++) {
			row2.getSeries()[i] = targetSeries.get(i);
		}

		return chart;
	}

	//@Override
	public org.psygrid.data.reporting.definition.dto.RecruitmentProgressChart toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
		//chart in the map of references
		org.psygrid.data.reporting.definition.dto.RecruitmentProgressChart dtoMC = null;
		if ( dtoRefs.containsKey(this)){
			dtoMC = (org.psygrid.data.reporting.definition.dto.RecruitmentProgressChart)dtoRefs.get(this);
		}
		else {
			//an instance of chart has not already
			//been created, so create it, and add it to the
			//map of references
			dtoMC = new org.psygrid.data.reporting.definition.dto.RecruitmentProgressChart();
			dtoRefs.put(this, dtoMC);
			toDTO(dtoMC, dtoRefs, depth);
		}

		return dtoMC;
	}

	public void toDTO(org.psygrid.data.reporting.definition.dto.RecruitmentProgressChart dtoC, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoC, dtoRefs, depth);

		dtoC.setEndDate(endDate);
		dtoC.setStartDate(startDate);

		if (targets != null) {
			@SuppressWarnings("unchecked")
			Pair<Calendar,Integer>[] dtoTargets = new Pair[targets.size()];
			int i = 0;
			for (Calendar month: targets.keySet()) {
				if (month != null) {
					dtoTargets[i] = new Pair<Calendar,Integer>(month, targets.get(month));
				}
				i++;
			}
			dtoC.setTargets(dtoTargets);
		}

		org.psygrid.data.model.dto.GroupDTO[] dtoGroups = new org.psygrid.data.model.dto.GroupDTO[this.groups.size()];
		for (int i=0; i<this.groups.size(); i++){
			Group g = groups.get(i);
			dtoGroups[i] = ((Group)g).toDTO(dtoRefs, depth);
		}        
		dtoC.setGroups(dtoGroups);
	}

	/**
	 * Establish whether a date is between (or equal to) two given dates, comparing
	 * only months and years.
	 * 
	 * @param startDate
	 * @param endDate
	 * @param date
	 * @return isBetween
	 */
	private boolean dateBetween(Calendar startDate, Calendar endDate, Calendar newDate) {

		Calendar start = (Calendar)startDate.clone();
		Calendar end   = (Calendar)endDate.clone();
		Calendar date  = (Calendar)newDate.clone();

		//clear everything except month and year
		start.clear(Calendar.MILLISECOND);
		start.clear(Calendar.SECOND);
		start.clear(Calendar.MINUTE);
		start.clear(Calendar.HOUR_OF_DAY);
		start.clear(Calendar.DATE);
		start.clear(Calendar.DAY_OF_MONTH);
		start.setTimeZone(TimeZone.getTimeZone("GMT"));
		end.clear(Calendar.MILLISECOND);
		end.clear(Calendar.SECOND);
		end.clear(Calendar.MINUTE);
		end.clear(Calendar.HOUR_OF_DAY);
		end.clear(Calendar.DATE);
		end.clear(Calendar.DAY_OF_MONTH);
		end.setTimeZone(TimeZone.getTimeZone("GMT"));
		date.clear(Calendar.MILLISECOND);
		date.clear(Calendar.SECOND);
		date.clear(Calendar.MINUTE);
		date.clear(Calendar.HOUR_OF_DAY);
		date.clear(Calendar.DATE);
		date.clear(Calendar.DAY_OF_MONTH);
		date.setTimeZone(TimeZone.getTimeZone("GMT"));
		return (date.after(startDate) || date.equals(startDate)) 
		&& (date.before(endDate) || date.equals(endDate));

	}

	/*
	 * 
	 */
	private Calendar[] getSixMonths() {
		//generate a chart for the previous six months, based on current date
		Calendar curDate = Calendar.getInstance();
		Calendar startDate = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH) - 6, 0);
		Calendar endDate   = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), 0);
		startDate.setTimeZone(TimeZone.getTimeZone("GMT"));
		endDate.setTimeZone(TimeZone.getTimeZone("GMT"));

		return new Calendar[]{startDate, endDate};
	}

}