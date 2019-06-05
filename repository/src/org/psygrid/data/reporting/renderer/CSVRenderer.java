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

package org.psygrid.data.reporting.renderer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.IValue;
import org.psygrid.data.reporting.Chart;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.reporting.ChartRow;
import org.psygrid.data.reporting.ChartSeries;
import org.psygrid.data.reporting.RecordReport;
import org.psygrid.data.reporting.Report;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Class to render a generated report as a CSV document
 * <p>
 * The data from each chart will be written into a separate
 * worksheet in the workbook
 * 
 * @author Lucy Bridges
 *
 */
public class CSVRenderer extends AbstractTextRenderer {

	private CSVWriter csvw;
	
	/**
	 * Render the report as a CSV document
	 * 
	 * @param report The report to render.
	 * @param os OutputStream to send the workbook to.
	 */
	public void render(Report report, OutputStream os) throws IOException, RendererException {

		try{
			BufferedWriter writer = null;
			writer = new BufferedWriter(new OutputStreamWriter(os));
			csvw = new CSVWriter(writer);
			
			//Start writing document
			int currentRow = 0;
			if (report.isShowHeader()) {

				if ( null != report.getTitle() ){
					write(new Object[]{"Title:", report.getTitle()});
					currentRow++;
				}
				if ( report instanceof RecordReport ){
					if ( null != ((RecordReport)report).getSubject() ){
						write(new Object[]{"Subject:", ((RecordReport)report).getSubject()});
						currentRow++;
					}
					if ( null != ((RecordReport)report).getRequestor() ){
						write(new Object[]{"Requestor:", ((RecordReport)report).getRequestor()});
						currentRow++;
					}
				}
				if ( null != report.getRequestDate() ){
					write(new Object[]{"Date:", report.getRequestDate().toString()});
					currentRow++;
				}
				if (null != report.getStartDate() && null != report.getEndDate()) {
					
					SimpleDateFormat newformat = new SimpleDateFormat("MMMM yyyy");
					SimpleDateFormat oldformat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
					try {
						Date startdate = oldformat.parse(report.getStartDate().toString());
						Date enddate = oldformat.parse(report.getEndDate().toString());
						write(new Object[]{"Time period covered:", newformat.format(startdate) +" until "+newformat.format(enddate)});
					}
					catch(Exception e) {
						//do nothing
					}
				}
				write(new Object[]{""});	//blank line to separate header slightly
			}

			writeChart(report.getCharts());
			
			csvw.close();
		}
		catch(Exception ex){
			throw new RendererException("Unable to render the report '"+report.getTitle()+"' as a CSV document", ex);
		}
	}

	private void writeChart(Chart[] charts) {
		int currentRow = 0;
		for ( Chart c: charts ){

			boolean rowHasLabel = false;	//used to determine where the row is to start based on whether or not a column of labels is present

			//add the series labels
			List<String> labelList = new ArrayList<String>();

			for ( ChartRow row: c.getRows()){
				int i = 0;
				for (ChartSeries series: row.getSeries()) {
					String seriesLabel = series.getLabel();

					boolean found = false;
					for (String label: labelList) {
						if ( label.equals(seriesLabel) ) {
							found = true;
						}
					}
					if (!found) {
						labelList.add(seriesLabel);		
					}

					i++;
				}

				if (row.getLabel() != null && !row.getLabel().equals("")) {
					rowHasLabel = true;
				}
			}

			//Ensure that the labels are ordered correctly (for sensible display of dates, etc)
			List<String> newLabelList = sortDates(labelList);


			Map<String,String> labels = new LinkedHashMap<String,String>();
			for (String label: newLabelList) {
				//format date now that it's been ordered and add
				labels.put(formatDate(label), "");
			}

			//Offset the start of rows that have labels
			int i = 0;
			List<String> labelPositions = new ArrayList<String>();
			if (rowHasLabel) {
				i++;labelPositions.add("");
			}
			
			for (String label: labels.keySet()) {
				labelPositions.add(label);
				i++;
			}
			write(labelPositions.toArray());
			if (rowHasLabel) {
				labelPositions.remove(0);	//remove the blank rowlabel
			}
			
			currentRow++;
			//write each row of the data
			
			for ( ChartRow row: c.getRows() ){
				List<String> rows = new ArrayList<String>();
				int currentCol = 0;

				if (rowHasLabel) {
					rows.add(row.getLabel());
					currentCol++;
				}

				for (ChartSeries series: row.getSeries()) {
					String seriesLabel = "";
					if (IValue.TYPE_DATE.equals(series.getLabelType())) {
						seriesLabel = formatDate(series.getLabel());
					}
					else {
						seriesLabel = series.getLabel();
					}

					//If this is not the correct column for the current series put in an empty cell
					//and repeat until the correct column is reached.
					if (rowHasLabel) {
						while ( currentCol < labelPositions.size() && labelPositions.size() > 0
								&& !seriesLabel.equals(labelPositions.get(currentCol-1))) {
							rows.add("");currentCol++;
						}
					}
					else {
						while ( currentCol < labelPositions.size() && labelPositions.size() > 0
								&& !seriesLabel.equals(labelPositions.get(currentCol))) {
							rows.add("");currentCol++;
						}	
					}

					//Add the chart points to a cell.
					String point = "";
					for ( ChartPoint pt: series.getPoints() ){
						
						if (pt != null) {
							if ( IValue.TYPE_DOUBLE.equals(pt.getValueType()) ||
									IValue.TYPE_INTEGER.equals(pt.getValueType()) ){
								try {
									point += pt.getValue();
								} 
								catch(Exception nfe) {
									//the valueType has been set incorrectly or value is null
									point += pt.getValue();
								}
							}
							else if (IValue.TYPE_DATE.equals(pt.getValueType())) {
								SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
								try {
									Date date = format.parse(pt.getValue());
									Date empty = new Date();
									empty.setTime(0);
									if (format.format(date).equals(format.format(empty))) {
										//do nothing
									}
									else {
										point += format.format(date);
									}
								}
								catch(Exception e) {
									//the valueType has been set incorrectly or value is null
									point += pt.getValue();
								}
							}
							else {
								point += pt.getValue();
							}
						}
						
					} 
					rows.add(point);currentCol++;
				}
				Object[] line = new Object[rows.size()];
				
				rows.toArray(line);
				write(line);
				currentRow++;
			}
			write(new Object[]{""});
		}

	}
	
	private void write(Object[] objects) {
		String[] stringy = new String[objects.length];
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] == null) {
				stringy[i] = "";
			}
			else {
				stringy[i] = objects[i].toString();
			}
		}
		csvw.writeNext(stringy);
	}
}
