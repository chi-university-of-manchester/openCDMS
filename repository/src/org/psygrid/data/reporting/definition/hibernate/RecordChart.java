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
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.reporting.ChartSeries;
import org.psygrid.data.reporting.definition.IAbstractChartItem;
import org.psygrid.data.reporting.definition.IRecordChart;
import org.psygrid.data.reporting.definition.ISimpleChartRow;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.utils.esl.IRemoteClient;

/**
 * Class to represent a chart of an individual record.
 * <p>
 * A record chart will provide just one dataset with
 * multiple series of data for (e.g.) a table with 
 * just one value column or a chart with one type
 * of data (e.g bars and lines can be displayed for 
 * multiple series, but not both together).
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_record_charts"
 * 					proxy="org.psygrid.data.reporting.definition.hibernate.RecordChart"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class RecordChart extends SimpleChart implements IRecordChart {

	private static Log sLog = LogFactory.getLog(RecordChart.class);
	
	private List<SimpleChartRow> rows = new ArrayList<SimpleChartRow>();
	
	public RecordChart(){}

	public RecordChart(String type, String title){
		super(type, title);
	}

	/**
	 * 
	 * @return rows
	 * 
	 * @hibernate.list cascade="all"
	 * @hibernate.one-to-many class="org.psygrid.data.reporting.definition.hibernate.SimpleChartRow"
	 * @hibernate.key column="c_rec_chart_id" not-null="false"
	 * @hibernate.list-index column="c_index"
	 */
	public List<SimpleChartRow> getRows() {
		return rows;
	}

	public void setRows(List<SimpleChartRow> rows) {
		this.rows = rows;
	}

	public void addRow(ISimpleChartRow row) throws ReportException {
		if ( null == row ){
			throw new ReportException("Cannot add a null row");
		}
		rows.add((SimpleChartRow)row);
	}

	public ISimpleChartRow getRow(int index) throws ReportException {
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
	
	public void addItem(IAbstractChartItem item) throws ReportException {
		if ( null == item ){
			throw new ReportException("Cannot add a null item");
		}
		//add the chart item to the zeroth row, which is created if it doesn't exist
		if ( null == rows ){
			rows = new ArrayList<SimpleChartRow>();
		}
		if ( 0 == rows.size() ){
			rows.add(new SimpleChartRow());			
		}
		rows.get(0).addSeries(item);
	}

	public IAbstractChartItem getItem(int index) throws ReportException {
		try{
			//return this.items.get(index);
			return this.getRows().get(0).getSeries(index);	//assuming a single row if this method is used
		}
		catch(IndexOutOfBoundsException ex){
			throw new ReportException("No item exists for index="+index, ex);
		}
		catch (Exception e) {
			throw new ReportException("No item exists for index="+index, e);
		}
	}

	//new version to take into account addition of simplechartrow
	public org.psygrid.data.reporting.Chart generateChart(Session session, IRemoteClient eslClient, Long recordId, String saml) throws ReportException {

		org.psygrid.data.reporting.Chart chart = new org.psygrid.data.reporting.Chart();
		chart.setTitle(this.title);
		chart.setRangeAxisLabel(this.rangeAxisLabel);
		chart.setUsePercentages(usePercentages);
		String[] types = new String[this.types.size()];

		try {
			for ( int i=0; i<this.types.size(); i++ ){
				types[i] = this.types.get(i);
			}
			chart.setTypes(types);

			chart.setRows(new org.psygrid.data.reporting.ChartRow[this.getRows().size()]);
		}
		catch (Exception e) {
			throw new ReportException("Problem setting types or labels", e);
		}

		//sort out rows
		for ( int i=0; i<this.getRows().size(); i++ ){

			org.psygrid.data.reporting.ChartRow row = new org.psygrid.data.reporting.ChartRow();

			String label = this.getRows().get(i).getLabel();
			if ( label == null) {
				label = ""+i;
			}
			row.setLabel(label);
			row.setLabelType(IValue.TYPE_STRING);

			int numItems = 0; 
			try {
				numItems = this.getRows().get(i).getSeries().size();
			}
			catch (Exception e) {
				//do nothing, numItems will be 0
				sLog.info("Exception occured when fetching numItems for record chart: "+e.getMessage());
			}
			
			List<ChartSeries> series = new ArrayList<ChartSeries>();
			//for each data series in this row
			for (int j = 0; j < numItems; j++) {
				
				//adds a single chart point to this row
				IAbstractChartItem aci = null;						
				try {
					aci = this.getRows().get(i).getSeries(j);
				}
				catch (Exception e) {
					throw new ReportException("Problem getting item "+j+" for row "+i, e);
				}

				if ( aci.getMultiple() ){
					//the chart item can have multiple points
					List<ChartPoint> points = aci.getPoints(session, eslClient, recordId, saml);
					int counter = 1;
					for ( ChartPoint point: points){
						ChartSeries cs = new ChartSeries();
						cs.setPoints(new org.psygrid.data.reporting.ChartPoint[1]);
						cs.setLabel(aci.getLabel()+" ("+counter+")");
						cs.setLabelType(IValue.TYPE_STRING);
						cs.getPoints()[0] = point;
						series.add(cs);
						counter++;
					}
				}
				else{
					//the chart item will only result in a single chart point
					ChartSeries cs = new ChartSeries();
					cs.setPoints(new org.psygrid.data.reporting.ChartPoint[1]);
					cs.setLabel(aci.getLabel());
					cs.setLabelType(IValue.TYPE_STRING);
					cs.getPoints()[0] = aci.getPoint(session, eslClient, recordId, saml);
					series.add(cs);
				}
			}
			
			row.setSeries(series.toArray(new ChartSeries[series.size()]));

			//add this row to the chart
			chart.getRows()[i] = row;
		}
		return chart;
	}
	
	@Override
	public org.psygrid.data.reporting.definition.dto.RecordChart toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
		//simple chart in the map of references
		org.psygrid.data.reporting.definition.dto.RecordChart dtoSC = null;
		if ( dtoRefs.containsKey(this)){
			dtoSC = (org.psygrid.data.reporting.definition.dto.RecordChart)dtoRefs.get(this);
		}
		else {
			//an instance of the element has not already
			//been created, so create it, and add it to the
			//map of references
			dtoSC = new org.psygrid.data.reporting.definition.dto.RecordChart();
			dtoRefs.put(this, dtoSC);
			toDTO(dtoSC, dtoRefs, depth);
		}

		return dtoSC;
	}

	public void toDTO(org.psygrid.data.reporting.definition.dto.RecordChart dtoC, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoC, dtoRefs, depth);
		if ( depth != RetrieveDepth.DS_SUMMARY ){

			org.psygrid.data.reporting.definition.dto.SimpleChartRow[] dtoRows = new org.psygrid.data.reporting.definition.dto.SimpleChartRow[this.getRows().size()];
			for (int i=0; i<this.getRows().size(); i++){
				SimpleChartRow s = getRows().get(i);
				dtoRows[i] = s.toDTO(dtoRefs, depth);
			}        
			dtoC.setRows(dtoRows);

		}
	}

}
