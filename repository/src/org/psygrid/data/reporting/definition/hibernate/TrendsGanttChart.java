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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.hibernate.Query;
import org.hibernate.Session;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.reporting.ChartSeries;
import org.psygrid.data.reporting.definition.ISimpleChartItem;
import org.psygrid.data.reporting.definition.ITrendsGanttChart;
import org.psygrid.data.reporting.definition.ReportException;

/**
 * Class to represent a trends chart for showing treatment usage.
 * Behaves the same as a trends chart but uses a gantt chart to 
 * show the data, with two datapoints per chart item to show the
 * start and end points. 
 *  
 * <p>
 * A trends chart provides a summary of data 
 * from all documents of a particular type 
 * which have been entered into a data set
 * over a given time period.
 * 
 * @author Lucy Bridges
 *
 * @hibernate.joined-subclass table="t_trends_gantt_charts"
 * 							proxy="org.psygrid.data.reporting.definition.hibernate.TrendsGanttChart"
 * @hibernate.joined-subclass-key column="c_id"
 *
 */
public class TrendsGanttChart extends TrendsChart implements ITrendsGanttChart {

	public TrendsGanttChart() {}

	public TrendsGanttChart(String type, String title) {
		super(type, title);
	}


	public org.psygrid.data.reporting.ComplexChart generateChart(Session session, Long datasetId) throws ReportException {

		//Create the reporting.Chart
		org.psygrid.data.reporting.ComplexChart chart = new org.psygrid.data.reporting.ComplexChart();

		chart.setTitle(title);
		chart.setRangeAxisLabel(rangeAxisLabel);
		chart.setUsePercentages(usePercentages);

		String[] types = new String[this.types.size()];
		try {
			for ( int i=0; i<this.types.size(); i++ ){
				types[i] = this.types.get(i);
			}
			chart.setTypes(types);
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

		//create date for database comparison (add one to month to find dates with the same month)
		Calendar dbEndDate  = new GregorianCalendar(getEndDate().get(Calendar.YEAR), getEndDate().get(Calendar.MONTH) + 1, 0);
		dbEndDate.setTimeZone(TimeZone.getTimeZone("GMT"));
		//Retrieve all the records created in this dataset, so that we can get the
		//values for each and calculate a summary
		List groupNames = session.createQuery("select g.name from Group g" +
				" where g.id in (:grps) ")
		.setParameterList("grps", grps)
		.list();

		Query idQuery = session.createQuery("select i.id from Identifier i where i.groupPrefix in (:grps)) ")
		.setParameterList("grps", groupNames);
		idQuery.setReadOnly(true);
		List identifierIds = idQuery.list();

		List<Long> result = new ArrayList<Long>();
		List objresult = new ArrayList();
		//Map<Long,Calendar> result = new HashMap<Long,Calendar>();
		
		if (identifierIds != null || identifierIds.size() > 0) {
			
			
//			Split the query, because DB2 throws an error if the query is too long 
			int searched = identifierIds.size()-1 < MAX_IDS ? identifierIds.size()-1 : MAX_IDS;
			int start = 0;

			while (start < searched) {
				//identifiers are unique across datasets, so not necessary to specify dataset
//				Query query = session.createQuery("select r.id, r.status.enumGenericState from Record r " +
//						" where r.identifier.id in ( :identifiers ) "+
//						"and r.created between :start and :end "+
//						"and r.deleted=:deleted")
				Query query = session.createQuery("select r.id, r.status.enumGenericState from Record r " +
						" where r.identifier.id in ( :identifiers ) "+
						"and r.history[0].when between :start and :end "+
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

				String recordStatus = (String)data[1];
				
				if (recordEligible(recordStatus)) {
					//result.put(id, cal);
					result.add(id);
				}
			}

		}

		//Chart will only have one row, containing many 'series' specified
		//by the SimpleChartRow (and its items)
		org.psygrid.data.reporting.ChartRow row = new org.psygrid.data.reporting.ChartRow();
		chart.setRows(new org.psygrid.data.reporting.ChartRow[1]);
		chart.getRows()[0] = row;



		/*
		 * Each SimpleChartRow must have three SimpleChartItems.
		 * The first specifying the thing to be searched for (which will
		 * form the individual rows of the final chart) and the remaining two 
		 * pointing to the start and end dates for it.
		 * 
		 * Each SimpleChartRow represents a possible answer to the question
		 * specified in the first SimpleChartItem and its label must be
		 * set to this answer. 
		 */


		List<ChartSeries> series = new ArrayList<ChartSeries>();

		boolean found = false;

		//each row specifies a possible answer
		for (int j = 0; j < this.getRows().size(); j++) {

			if (this.getRows().get(j).getSeries().size() == 3) {
				String label = this.getRow(j).getLabel();
				//TODO don't really want to use casts here.
				ISimpleChartItem trendName = (ISimpleChartItem)this.getRows().get(j).getSeries(0);
				ISimpleChartItem sciStart  = (ISimpleChartItem)this.getRows().get(j).getSeries(1);
				ISimpleChartItem sciEnd    = (ISimpleChartItem)this.getRows().get(j).getSeries(2);

				if (result != null) {
					for (Object o: result) {
						Long recordId = (Long)o;

						List recordResult = session.createQuery("from Response r " +
								"where r.id in (select r.id from Response r " +
								"where r.record.id=:id ) " +
								"and r.entry.id=:entry "+
								//"and r.sectionOccurrence.id=:sectionOcc " +
						"and r.docInstance.occurrence.id=:docOcc" )

						.setLong("id", recordId)
						.setLong("entry", trendName.getEntry().getId())
						//.setLong("sectionOcc", sci.getSecOccurrence().getId())
						.setLong("docOcc", trendName.getDocOccurrence().getId())
						.list();

						if (recordResult == null) {
							throw new ReportException("No result returned when retrieving data for report.");
						}

						//for each Reponse entered in a section occurrence 
						for (Object obj: recordResult) {

							BasicResponse br = (BasicResponse)obj;
							String valueAndType[] = br.getTheValue().getReportValueAsString(trendName.getOptions());

							if (valueAndType[0] != null && valueAndType[0].endsWith(label)) {
								found = true;
							}
							else {
								found = false;
							}


							ChartSeries s = new ChartSeries();
							s.setLabel(label);
							s.setLabelType(IValue.TYPE_STRING);
							s.setPoints(new org.psygrid.data.reporting.ChartPoint[2]);


							series.add(s);

							//Get the start and end dates for the specific section occurrence instance found
							if (found) {

								s.getPoints()[0] = new org.psygrid.data.reporting.ChartPoint();
								s.getPoints()[0].setValue(null);
								s.getPoints()[0].setValueType(IValue.TYPE_DATE);
								s.getPoints()[1] = new org.psygrid.data.reporting.ChartPoint();
								s.getPoints()[1].setValue(null);
								s.getPoints()[1].setValueType(IValue.TYPE_DATE);

								Long secOccId = br.getSecOccInstance().getId();

								//Retrieve start date
								Date startDate = new Date();
								Object recordResult1 = session.createQuery("from Response r " +
										"where r.id in (select r.id from Response r " +
										"where r.record.id=:id ) " +
										"and r.entry.id=:entry "+
										"and r.secOccInstance.id=:sectionOcc " +
								"and r.docInstance.occurrence.id=:docOcc" )

								.setLong("id", recordId)
								.setLong("entry", sciStart.getEntry().getId())
								.setLong("sectionOcc", secOccId)
								.setLong("docOcc", sciStart.getDocOccurrence().getId())
								.uniqueResult();

								BasicResponse br1 = (BasicResponse)recordResult1;
								String valueAndTypeStart[] = br1.getTheValue().getReportValueAsString(sciStart.getOptions());

								if (recordResult1 == null) {
									startDate = null;
								}	
								if (valueAndTypeStart[0] != null) {
									DateFormat sfd = new SimpleDateFormat("dd-MMM-yyyy");
									try {
										startDate = sfd.parse(valueAndTypeStart[0]);
									}
									catch (ParseException pe) {
										startDate = null;
									}
								}
								if (startDate == null) {
									s.getPoints()[0].setValue(null);
								}
								else {
									s.getPoints()[0].setValue(startDate.toString());
								}
								s.getPoints()[0].setValueType(IValue.TYPE_DATE);


								//Retrieve end date
								Object recordResult2 = session.createQuery("from Response r " +
										"where r.id in (select r.id from Response r " +
										"where r.record.id=:id ) " +
										"and r.entry.id=:entry "+
										"and r.secOccInstance.id=:sectionOcc " +
								"and r.docInstance.occurrence.id=:docOcc" )

								.setLong("id", recordId)
								.setLong("entry", sciEnd.getEntry().getId())
								.setLong("sectionOcc", secOccId)
								.setLong("docOcc", sciEnd.getDocOccurrence().getId())
								.uniqueResult();

								BasicResponse br2 = (BasicResponse)recordResult2;
								String valueAndTypeEnd[]   = br2.getTheValue().getReportValueAsString(sciEnd.getOptions());

								Date endDate = new Date();
								if (recordResult2 == null) {
									endDate = null;
								}						

								if (valueAndTypeEnd[0] != null) {
									SimpleDateFormat sfd = new SimpleDateFormat("dd-MMM-yyyy");
									try {
										endDate = sfd.parse(valueAndTypeEnd[0]);
									}
									catch (ParseException pe) {
										endDate = null;
									}
								}

								if (endDate == null) {
									s.getPoints()[1].setValue(null);
								}
								else {
									s.getPoints()[1].setValue(endDate.toString());
								}
								s.getPoints()[1].setValueType(IValue.TYPE_DATE);

							} //iffound


						} //end foreach document

					} //end foreach record

				}
			}
		} //foreach row


		row.setSeries(new ChartSeries[series.size()]);
		for (int i = 0; i < series.size(); i++) {
			row.getSeries()[i] = series.get(i);
		}

		return chart;
	}












	@Override
	public org.psygrid.data.reporting.definition.dto.TrendsGanttChart toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
		//trends chart in the map of references
		org.psygrid.data.reporting.definition.dto.TrendsGanttChart dtoTrends = null;
		if ( dtoRefs.containsKey(this)){
			dtoTrends = (org.psygrid.data.reporting.definition.dto.TrendsGanttChart)dtoRefs.get(this);
		}
		else {
			//an instance of the element has not already
			//been created, so create it, and add it to the
			//map of references
			dtoTrends = new org.psygrid.data.reporting.definition.dto.TrendsGanttChart();
			dtoRefs.put(this, dtoTrends);
			toDTO(dtoTrends, dtoRefs, depth);
		}

		return dtoTrends;
	}

	public void toDTO(org.psygrid.data.reporting.definition.dto.TrendsGanttChart dtoTrends, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoTrends, dtoRefs, depth);
	}

}
