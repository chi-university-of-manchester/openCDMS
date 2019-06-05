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
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.hibernate.Query;
import org.hibernate.Session;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.model.hibernate.Value;
import org.psygrid.data.reporting.ChartSeries;
import org.psygrid.data.reporting.definition.IAbstractChartItem;
import org.psygrid.data.reporting.definition.ISimpleChartItem;
import org.psygrid.data.reporting.definition.ITrendsChart;
import org.psygrid.data.reporting.definition.ITrendsChartRow;
import org.psygrid.data.reporting.definition.ITrendsReport;
import org.psygrid.data.reporting.definition.ReportException;

/**
 * Class to represent a "trends" chart.
 * <p>
 * A trends chart provides a summary of data 
 * from all documents of a particular type 
 * which have been entered into a data set
 * over a given time period.
 * 
 * @author Lucy Bridges
 *
 * @hibernate.joined-subclass table="t_trends_charts"
 * 							proxy="org.psygrid.data.reporting.definition.hibernate.TrendsChart"
 * @hibernate.joined-subclass-key column="c_id"
 *
 */
public class TrendsChart extends SimpleChart implements ITrendsChart {

	//private static Log sLog = LogFactory.getLog(TrendsChart.class);

	private List<ITrendsChartRow> rows = new ArrayList<ITrendsChartRow>();

	protected static final int MAX_IDS = 1000;
	
	/**
	 * The period of time the chart is to cover
	 */
	private Calendar startDate  = Calendar.getInstance();
	private Calendar endDate    = Calendar.getInstance();

	private List<Group> groups = new ArrayList<Group>();

	private boolean showTotals = false;

	private ITrendsReport report = null; 

	public TrendsChart() {}

	public TrendsChart(String type, String title) {
		super(type, title);
	}


	/**
	 * 
	 * @return rows
	 * 
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.data.reporting.definition.hibernate.TrendsChartRow"
	 * 			entity-name="TrendsChartRows"
	 * @hibernate.key column="c_trends_chart_id" not-null="true"
	 * @hibernate.list-index column="c_index"
	 */ 
	public List<ITrendsChartRow> getRows() {
		return rows;
	}

	public void setRows(List<ITrendsChartRow> rows) {
		this.rows = rows;
	}

	public void addRow(ITrendsChartRow row) throws ReportException {
		if ( null == row ){
			throw new ReportException("Cannot add a null row");
		}
		rows.add(row);
	}

	public ITrendsChartRow getRow(int index) throws ReportException {
		try{
			return this.rows.get(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ReportException("No row exists for index="+index, ex);
		}
	}

	public int numRows() {
		return this.rows.size();
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
		endDate.clear(Calendar.MILLISECOND);
		endDate.clear(Calendar.SECOND);
		endDate.clear(Calendar.MINUTE);
		endDate.clear(Calendar.HOUR_OF_DAY);
		endDate.clear(Calendar.DATE);
		endDate.setTimeZone(TimeZone.getTimeZone("GMT"));
		this.endDate = endDate;
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
		startDate.clear(Calendar.MILLISECOND);
		startDate.clear(Calendar.SECOND);
		startDate.clear(Calendar.MINUTE);
		startDate.clear(Calendar.HOUR_OF_DAY);
		startDate.clear(Calendar.DATE);
		startDate.setTimeZone(TimeZone.getTimeZone("GMT"));
		this.startDate = startDate;
	}

	/**
	 * Set the period of time the chart is to cover.
	 * 
	 * Note: Fields other than month and year are ignored
	 * 
	 * 
	 * @param startDate
	 * @param endDate
	 */
	public void setTimePeriod(Calendar startDate, Calendar endDate) {
		setStartDate(startDate);
		setEndDate(endDate);
	}

	/**
	 * Get the report this chart belongs to.
	 * 
	 * @return the report
	 * 
	 * @hibernate.many-to-one class="org.psygrid.data.reporting.definition.hibernate.TrendsReport"
	 *                        column="c_trend_report_id"
	 *                        not-null="true"
	 *                        update="false"
	 *                        insert="false"
	 */
	public ITrendsReport getReport() {
		return report;
	}

	/**
	 * @param report the report to set
	 */
	public void setReport(ITrendsReport report) {
		this.report = report;
	}

	/**
	 * Get the groups in the dataset that will be featured in the chart.
	 * 
	 * @return The groups.
	 * 
	 * @hibernate.list cascade="none" 
	 *                 table="t_trendschrt_groups"
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

	/**
	 * When set this overlays a bar chart onto the graph
	 * showing the total number of records found per month
	 * 
	 * Note: For timeseries graphs only.
	 * 
	 * @return the showTotals
	 * 
	 * @hibernate.property column="c_show_totals"
	 */
	public boolean isShowTotals() {
		return showTotals;
	}

	/**
	 * When set this overlays a bar chart onto the graph
	 * showing the total number of records found per month
	 * 
	 * Note: For timeseries graphs only.
	 * 
	 * @param showTotals the showTotals to set
	 */
	public void setShowTotals(boolean showTotals) {
		this.showTotals = showTotals;
	}

	public org.psygrid.data.reporting.ComplexChart generateChart(Session session, Long datasetId) throws ReportException {

		//Create the reporting.Chart for rendering, using ComplexChart for a dual dataseries if req'd (when showTotals is set)
		org.psygrid.data.reporting.ComplexChart chart = new org.psygrid.data.reporting.ComplexChart();
		chart.setTitle(title);
		chart.setRangeAxisLabel(rangeAxisLabel);

		//Add the summary type to the axis label, if appropriate 
		if (this.getRow(0) != null && this.getRow(0).getSummaryType() != null
				&& !this.getRow(0).getSummaryType().equals(ITrendsChartRow.SUMMARY_TYPE_COLLATE)
				&& !this.getRow(0).getSummaryType().equals(ITrendsChartRow.SUMMARY_TYPE_HIGH)
				&& !this.getRow(0).getSummaryType().equals(ITrendsChartRow.SUMMARY_TYPE_LOW)) {
			chart.setRangeAxisLabel(rangeAxisLabel+" ( "+this.getRow(0).getSummaryType()+" )");
		}
		chart.setUsePercentages(usePercentages);

		if (showTotals) {
			chart.setSecondaryAxisLabel("Cases");	
		}

		String[] types = new String[this.types.size()];
		try {
			for ( int i=0; i<this.types.size(); i++ ){
				types[i] = this.types.get(i);
			}
			chart.setTypes(types);

			chart.setRows(new org.psygrid.data.reporting.ChartRow[this.getRows().size()]);
			chart.setSecondaryRows(new org.psygrid.data.reporting.ChartRow[1]);		//shows the total number of cases
		}
		catch (Exception e) {
			throw new ReportException("Problem setting up chart types or rows", e);
		}
		//get the relevant groups as specified
		List<Long> grps = new ArrayList<Long>();		
		for (Group g: getGroups()) {
			grps.add(g.getId());
		}
		//if still groups add all for the dataset
		if (grps.size() == 0) {
			for (Group g: ((org.psygrid.data.model.hibernate.DataSet)getReport().getDataSet()).getGroups()) {
				grps.add(g.getId());
			}
		}	

		//session is readonly so flushing is not required (to improve performance slightly)
		//session.setFlushMode(FlushMode.NEVER);	

		//create date for database comparison (add one to month to find dates with the same month)
		Calendar dbEndDate  = new GregorianCalendar(getEndDate().get(Calendar.YEAR), getEndDate().get(Calendar.MONTH)+2, 0);
		dbEndDate.setTimeZone(TimeZone.getTimeZone("GMT"));

		List groupNames = session.createQuery("select g.name from Group g" +
		" where g.id in (:grps) ")
		.setParameterList("grps", grps)
		.list();

		//Will look across all projects for matching groups, but project code is not stored in the dataset object held by the report 
		Query idQuery = session.createQuery("select i.id from Identifier i where i.groupPrefix in (:grps)) ")
		.setParameterList("grps", groupNames);
		List identifierIds = idQuery.list();
		
		//Retrieve all the records created in this dataset, so that we can get the
		//values for each and calculate a summary
		List objresult = new ArrayList();//null;
		Map<Long,Calendar> result = new HashMap<Long,Calendar>();

		if (identifierIds != null && identifierIds.size() > 0) {
			
			//Split the query, because DB2 throws an error if the query is too long 
			int searched = identifierIds.size()-1 < MAX_IDS ? identifierIds.size()-1 : MAX_IDS;
			int start = 0;

			while (start < searched) {
				//identifiers are unique across datasets, so not necessary to specify dataset
//				Query query = session.createQuery("select r.id, r.created, r.status.enumGenericState from Record r " +
//						" where r.identifier.id in ( :identifiers ) "+
//						"and r.created between :start and :end "+
//						"and r.deleted=:deleted")
				Query query = session.createQuery("select r.id, ch.when, r.status.enumGenericState from Record r, ChangeHistory ch " +
						"where r.identifier.id in ( :identifiers ) "+
						"and ch=r.history[0] "+ 
						"and ch.when between :start and :end "+
						"and r.deleted=:deleted")
				.setParameterList("identifiers", identifierIds.subList(start, searched))
				.setCalendar("start", getStartDate())
				.setCalendar("end", dbEndDate)
				.setBoolean("deleted", false);

				objresult.addAll(query.list());

				start = searched;
				searched = identifierIds.size()-1 < searched+MAX_IDS ? identifierIds.size()-1 : searched+MAX_IDS;
			}

			for (Object obj: objresult) {
				Object[] data = (Object[])obj;
				Long id = (Long)data[0];

				Date created = (Date)data[1];
				Calendar cal = Calendar.getInstance();
				cal.setTime(created);

				String recordStatus = (String)data[2];
				
				if (recordEligible(recordStatus)) {
					result.put(id, cal);
				}
			}

		}

		//sort out rows	(options from questions?)
		for ( int i=0; i<this.getRows().size(); i++ ){
			ITrendsChartRow curRow = (ITrendsChartRow)this.getRows().get(i);

			org.psygrid.data.reporting.ChartRow row = new org.psygrid.data.reporting.ChartRow();
			//use the display text of the entry as the label for this chart row.
			String label = this.getRows().get(i).getLabel();
			if ( label == null) {
				label = ""+i;
			}
			row.setLabel(label);
			row.setLabelType(IValue.TYPE_STRING);

			//calculate the number of months to search through
			int months = calcMonthsDifference(getStartDate(), dbEndDate); 

			ChartSeries[] series = new ChartSeries[months];

			org.psygrid.data.reporting.ChartRow secondaryRow = new org.psygrid.data.reporting.ChartRow();
			ChartSeries[] secondarySeries = new ChartSeries[months];


			//for each chart item, look up its value 
			//for each data series in this row..
			for (int j = 0; j < months; j++) {
				String pointType = null;
				series[j] = new ChartSeries();

				//Count the number of occurances of a value such as a string, used 
				//where symmary type is collate, such as in the pathways to care report.
				//Needs to be reset for each row and month	
				Map<String,Integer> collateValues = new HashMap<String,Integer>();
				//assumes one data point per series
				series[j].setPoints(new org.psygrid.data.reporting.ChartPoint[1]);

				//set the label based on the number of months we are past the startDate
				Calendar curDate = new GregorianCalendar(getStartDate().get(Calendar.YEAR), getStartDate().get(Calendar.MONTH)+ 1 + j, 0);;
				//add 1 to the month because Calendar's months start at 0.
				String seriesLabel = (curDate.get(Calendar.MONTH)+1)+" "+curDate.get(Calendar.YEAR);		

				series[j].setLabel(seriesLabel);
				series[j].setLabelType(IValue.TYPE_DATE);

				List<Double> values = new ArrayList<Double>();

				int noOfResults = 0;	//used for calculating the mean and total number of records

				for (IAbstractChartItem aci: this.getRows().get(i).getSeries()) {

					//TODO better way of doing this??
					ISimpleChartItem sci = (ISimpleChartItem)aci;
					
					//go through each Record for the relevant month and retrieve the values from the database for each
					//item in each row, populating the reporting.Chart object as usual.
					for (Long recordId: result.keySet()) {
						Calendar created = result.get(recordId);

						if (created != null && created.get(Calendar.MONTH) == curDate.get(Calendar.MONTH)
								&& created.get(Calendar.YEAR) == curDate.get(Calendar.YEAR)) {
							//retrieve the results for the particular documentOccurence
							//ignoring the section occurrence to find all entries regardless of section 
							//(as section occ causes problems with variable section documents like pathways to care)

//							Query query = session.createQuery("select br.theValue.id from BasicResponse br " +
//									"left join br.record r " +
//									"where r.id=:id " +
//									"and month(r.created)=:createdmonth " +
//									" and year(r.created)=:createdyear" +
//									" and br.entry.id=:entry " +
//							" and br.docInstance.occurrence.id=:docOcc ")

							Query query = session.createQuery("select br.theValue.id from BasicResponse br " +
									"left join br.record r " +
									"where r.id=:id " +
									"and month(r.history[0].when)=:createdmonth " +
									" and year(r.history[0].when)=:createdyear" +
									" and br.entry.id=:entry " +
							" and br.docInstance.occurrence.id=:docOcc ")
							.setLong("entry", sci.getEntry().getId())
							.setLong("docOcc", sci.getDocOccurrence().getId())
							.setInteger("createdmonth", (curDate.get(Calendar.MONTH)+1))
							.setInteger("createdyear", curDate.get(Calendar.YEAR))
							.setLong("id", recordId);
							
							query.setReadOnly(true);
							List recordResult = query.list();
							session.evict(query);
							double point = 0;

							if ( recordResult == null || recordResult.size() == 0 ){
								point = 0;	
							}

							//foreach Reponse entered..
							for (Object obj: recordResult) {
								Long valueId = (Long)obj;

								Value theValue = null;
								if (valueId != null) {

									theValue = (Value)session.createQuery("from Value v " +
									"where v.id=:valueId ")
									.setLong("valueId", valueId)
									.uniqueResult();
								}

								String[] valueAndType = new String[2];
								if (theValue != null) {
									valueAndType = theValue.getReportValueAsString(sci.getOptions());
								}

								/*
								 * Make a note of the number of results for each different response found so far 
								 */
								if (curRow.getSummaryType().equals(ITrendsChartRow.SUMMARY_TYPE_COLLATE)) {

									//Don't need to set point, instead find matching answer and add one to the number found
									// keeping a record of the results.
									if (valueAndType[0] != null) {
										if (collateValues.get(valueAndType[0]) != null) {
											collateValues.put(valueAndType[0], collateValues.get(valueAndType[0]) + 1);
										}
										else {
											collateValues.put(valueAndType[0], 1);
										}
									}
									values.add(point);
								}
								/*
								 * Add the value to the series of points for the current row.
								 * A summary of these points will be calculated later and
								 * added to the row.
								 */
								else{
									try {
										point = Double.valueOf(valueAndType[0]);
										pointType = valueAndType[1];	
										values.add(point);
									}
									catch(Exception e) {
										//exception will occur if point is not a double - it is therefore likely to be a standard
										//code and should be ignored in calculations
									}
								}

								noOfResults++;

							} //end foreach document/response
						}//end if record created this month
					} //end foreach record

				} //end foreach sc item
				//}


				double summary = 0.0;

				//Find the mean of the results for all records
				if (curRow.getSummaryType().equals(ITrendsChartRow.SUMMARY_TYPE_MEAN)) {
					if (values.size() > 0) {
						for (double d: values) {
							summary += d;	
						}
						summary = summary / values.size();
					}
				}
				//Find the median of the results for all records
				else if (curRow.getSummaryType().equals(ITrendsChartRow.SUMMARY_TYPE_MEDIAN)) {
					if (values.size() > 0) {
						Collections.sort(values);
						summary = values.get(values.size()/2);
					}
				}
				else if (curRow.getSummaryType().equals(ITrendsChartRow.SUMMARY_TYPE_COLLATE)) {
					//record the number of occurances of this row for the current month (series) - used in pathways to care
					//the key is a BasicResponse value and so is in format 'x. row.getLabel()' where x is the option number 
					for (String key: collateValues.keySet()) {
						if (key.endsWith(row.getLabel())) {
							summary = collateValues.get(key);
						}
					}
				}	
				else if (curRow.getSummaryType().equals(ITrendsChartRow.SUMMARY_TYPE_TOTAL)) {
					summary = noOfResults;	//number of results found matching the criteria
				}
				else if (curRow.getSummaryType().equals(ITrendsChartRow.SUMMARY_TYPE_SUMMATION)) {
					for (double d: values) {
						summary += d;	
					}
				}
				else if (curRow.getSummaryType().equals(ITrendsChartRow.SUMMARY_TYPE_HIGH)) {
					if (values.size() > 0) {
						summary = Collections.max(values);
					}
				}
				else if (curRow.getSummaryType().equals(ITrendsChartRow.SUMMARY_TYPE_LOW)) {
					if (values.size() > 0) {
						summary = Collections.min(values);
					}
				}

				if (curRow.getSummaryType().equals(ITrendsChartRow.SUMMARY_TYPE_ALL)) {
					if (values.size() > 0) {
						series[j].setPoints(new org.psygrid.data.reporting.ChartPoint[values.size()]);
						int count = 0;
						for (double d: values) {
							org.psygrid.data.reporting.ChartPoint point = new org.psygrid.data.reporting.ChartPoint();
							point.setValue(Double.toString(d));
							point.setValueType(pointType);
							series[j].getPoints()[count] = point;
							count++;
						}
					}
				}
				else {
					org.psygrid.data.reporting.ChartPoint point = new org.psygrid.data.reporting.ChartPoint();
					point.setValue(Double.toString(summary));
					point.setValueType(IValue.TYPE_DOUBLE);

					series[j].getPoints()[0] = point;
				}

				row.setSeries(series);

				//add a secondary dataseries showing the total number of records (displayed as a barchart)
				if (showTotals) {

					secondaryRow.setLabel("Number of Cases");
					secondaryRow.setLabelType(row.getLabelType());

					org.psygrid.data.reporting.ChartPoint p = new org.psygrid.data.reporting.ChartPoint();

					p.setValue(Double.toString(noOfResults));
					p.setValueType(pointType);

					secondarySeries[j] = new ChartSeries();
					secondarySeries[j].setLabel(seriesLabel);
					secondarySeries[j].setLabelType(IValue.TYPE_DATE);

					//assumes one data point per series
					secondarySeries[j].setPoints(new org.psygrid.data.reporting.ChartPoint[1]);
					secondarySeries[j].getPoints()[0] = p;
					secondaryRow.setSeries(secondarySeries);
				}

			}	//end foreach series

			//add this row to the chart
			chart.getRows()[i] = row;

			//add this row to the chart
			chart.getSecondaryRows()[0] = secondaryRow;

		}//end foreach row

		return chart;
	}

	@Override
	public org.psygrid.data.reporting.definition.dto.TrendsChart toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
		//trends chart in the map of references
		org.psygrid.data.reporting.definition.dto.TrendsChart dtoTrends = null;
		if ( dtoRefs.containsKey(this)){
			dtoTrends = (org.psygrid.data.reporting.definition.dto.TrendsChart)dtoRefs.get(this);
		}
		else {
			//an instance of the element has not already
			//been created, so create it, and add it to the
			//map of references
			dtoTrends = new org.psygrid.data.reporting.definition.dto.TrendsChart();
			dtoRefs.put(this, dtoTrends);
			toDTO(dtoTrends, dtoRefs, depth);
		}

		return dtoTrends;
	}

	public void toDTO(org.psygrid.data.reporting.definition.dto.TrendsChart dtoTrends, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoTrends, dtoRefs, depth);

		dtoTrends.setStartDate(this.startDate);
		dtoTrends.setEndDate(this.endDate);
		dtoTrends.setShowTotals(showTotals);

		if (report != null) {
			dtoTrends.setReport(report.toDTO(dtoRefs, depth));
		}

		if ( depth != RetrieveDepth.DS_SUMMARY ){
			org.psygrid.data.reporting.definition.dto.TrendsChartRow[] dtoRows = new org.psygrid.data.reporting.definition.dto.TrendsChartRow[this.getRows().size()];
			for (int i=0; i<this.getRows().size(); i++){
				TrendsChartRow s = (TrendsChartRow)getRows().get(i);
				dtoRows[i] = s.toDTO(dtoRefs, depth);
			}        
			dtoTrends.setRows(dtoRows);

			org.psygrid.data.model.dto.GroupDTO[] dtoGroups = new org.psygrid.data.model.dto.GroupDTO[this.groups.size()];
			for (int i=0; i<this.groups.size(); i++){
				Group g = groups.get(i);
				dtoGroups[i] = ((Group)g).toDTO(dtoRefs, depth);
			}        
			dtoTrends.setGroups(dtoGroups);
		}
	}

	/**
	 * Calculate the number of months difference between two given dates
	 * 
	 * @param startDate
	 * @param endDate
	 * @return number of months
	 */
	protected int calcMonthsDifference(Calendar startDate, Calendar endDate) {

		Calendar start = (Calendar)startDate.clone();
		Calendar end   = (Calendar)endDate.clone();

		start.clear(Calendar.MILLISECOND);
		start.clear(Calendar.SECOND);
		start.clear(Calendar.MINUTE);
		start.clear(Calendar.HOUR_OF_DAY);
		start.clear(Calendar.DATE);
		end.clear(Calendar.MILLISECOND);
		end.clear(Calendar.SECOND);
		end.clear(Calendar.MINUTE);
		end.clear(Calendar.HOUR_OF_DAY);
		end.clear(Calendar.DATE);

		int elapsed = 0;
		while ( start.before(end) ) {
			start.add(Calendar.MONTH, 1);
			elapsed++;
		}
		return elapsed;
	}


}
