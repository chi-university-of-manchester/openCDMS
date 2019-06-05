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

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.psygrid.data.model.IValue;
import org.psygrid.data.reporting.Chart;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.reporting.ChartRow;
import org.psygrid.data.reporting.ChartSeries;
import org.psygrid.data.reporting.RecordReport;
import org.psygrid.data.reporting.Report;

/**
 * Class to render a generated report as an Excel workbook.
 * <p>
 * The data from each chart will be written into a separate
 * worksheet in the workbook.
 * 
 * @author Rob Harper
 *
 */
public class ExcelRenderer extends AbstractTextRenderer {

	/**
	 * Render the report as an Excel workbook.
	 * 
	 * @param report The report to render.
	 * @param os OutputStream to send the workbook to.
	 */
	public void render(Report report, OutputStream os) throws IOException, RendererException {

		try{

			WritableWorkbook workbook = Workbook.createWorkbook(os);

			int currentRow = 0;
			if (report.isShowHeader()) {
				//create the general sheet, with the report details
				WritableSheet generalSheet = workbook.createSheet("General", workbook.getNumberOfSheets());

				if ( null != report.getTitle() ){
					generalSheet.addCell(new Label(0, currentRow, "Title:"));
					generalSheet.addCell(new Label(1, currentRow, report.getTitle()));
					currentRow++;
				}
				if ( report instanceof RecordReport ){
					if ( null != ((RecordReport)report).getSubject() ){
						generalSheet.addCell(new Label(0, currentRow, "Subject:"));
						generalSheet.addCell(new Label(1, currentRow, ((RecordReport)report).getSubject()));
						currentRow++;
					}
					if ( null != ((RecordReport)report).getRequestor() ){
						generalSheet.addCell(new Label(0, currentRow, "Requestor:"));
						generalSheet.addCell(new Label(1, currentRow, ((RecordReport)report).getRequestor()));
						currentRow++;
					}
				}
				if ( null != report.getRequestDate() ){
					generalSheet.addCell(new Label(0, currentRow, "Date:"));
					generalSheet.addCell(new Label(1, currentRow, report.getRequestDate().toString()));
					currentRow++;
				}
				if (null != report.getStartDate() && null != report.getEndDate()) {
					generalSheet.addCell(new Label(0, currentRow, "Time period covered:"));
					SimpleDateFormat newformat = new SimpleDateFormat("MMMM yyyy");
					SimpleDateFormat oldformat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
					try {
						Date startdate = oldformat.parse(report.getStartDate().toString());
						Date enddate = oldformat.parse(report.getEndDate().toString());
						generalSheet.addCell(new Label(1, currentRow, newformat.format(startdate) +" until end of "+newformat.format(enddate)));
						currentRow++;
					}
					catch(Exception e) {
						//do nothing
					}
				}
				if ( null != report.getSummaryType() ){
					generalSheet.addCell(new Label(0, currentRow, "Data summary used:"));
					generalSheet.addCell(new Label(1, currentRow, report.getSummaryType()));
					currentRow++;
				}
				if ( null != report.getGroups() && report.getGroups().size() > 0 ){
					generalSheet.addCell(new Label(0, currentRow, "Groups included:"));
					StringBuilder groups = new StringBuilder();
					boolean firstGroup = true;
					for (String group: report.getGroups()) {
						if (firstGroup) {
							groups.append(group);
							firstGroup = false;
						}
						else {
							groups.append(", ").append(group);
						}
					}
					generalSheet.addCell(new Label(1, currentRow, groups.toString()));
					currentRow++;
				}
			}

			for ( Chart c: report.getCharts() ){
				//create a worksheet for this chart
				WritableSheet sheet = workbook.createSheet(c.getTitle(), workbook.getNumberOfSheets());
				currentRow = 0;

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
				if (rowHasLabel) {
					i++;
				}
				List<String> labelPositions = new ArrayList<String>();
				for (String label: labels.keySet()) {
					sheet.addCell(new Label(i, currentRow, label));
					labelPositions.add(label);
					i++;
				}


				currentRow++;
				//write each row of the data
				for ( ChartRow row: c.getRows() ){
					int currentCol = 0;

					if (rowHasLabel) {
						sheet.addCell(new Label(currentCol++, currentRow, row.getLabel()));
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
								sheet.addCell(new Label(currentCol++, currentRow, ""));
							}
						}
						else {
							while ( currentCol < labelPositions.size() && labelPositions.size() > 0
									&& !seriesLabel.equals(labelPositions.get(currentCol))) {
								sheet.addCell(new Label(currentCol++, currentRow, ""));
							}	
						}

						for ( ChartPoint pt: series.getPoints() ){
							if (pt != null) {
								WritableCell cell = null;

								if ( IValue.TYPE_DOUBLE.equals(pt.getValueType()) ||
										IValue.TYPE_INTEGER.equals(pt.getValueType()) ){
									try {
										cell = new Number(currentCol++, currentRow, Double.parseDouble(pt.getValue()));
									} 
									catch(Exception nfe) {
										//the valueType has been set incorrectly or value is null
										cell = new Label(currentCol++, currentRow, pt.getValue());
									}
								}
								else if (IValue.TYPE_DATE.equals(pt.getValueType())) {
									SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
									try {
										Date date = format.parse(pt.getValue());
										Date empty = new Date();
										empty.setTime(0);
										if (format.format(date).equals(format.format(empty))) {
											cell = new Label(currentCol++, currentRow, "");
										}
										else {
											cell = new Label(currentCol++, currentRow, format.format(date));
										}
									}
									catch(Exception e) {
										//the valueType has been set incorrectly or value is null
										cell = new Label(currentCol++, currentRow, pt.getValue());
									}
								}
								else {
									//TODO what about dates?
									cell = new Label(currentCol++, currentRow, pt.getValue());
								}
								sheet.addCell(cell);
							}
						} 
					}
					currentRow++;
				}
			}
			workbook.write();
			workbook.close();

		}
		catch(WriteException ex){
			throw new RendererException("Unable to render the report '"+report.getTitle()+"' as an Excel spreadsheet", ex);
		}
	}



}
