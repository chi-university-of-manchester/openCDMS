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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.Month;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.util.TableOrder;
import org.psygrid.data.model.IValue;
import org.psygrid.data.reporting.Chart;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.reporting.ChartRow;
import org.psygrid.data.reporting.ComplexChart;
import org.psygrid.data.reporting.RecordReport;
import org.psygrid.data.reporting.Report;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Class to render a generated report as a PDF document, suitable
 * for emailing to the intended recipients of the report.
 * 
 * @author Rob Harper
 *
 */
public class PdfRenderer {

	/**
	 * Render the report as a PDF document.
	 * 
	 * @param report The report to render.
	 * @param os OutputStream to send the PDF to.
	 */
	public void render(Report report, OutputStream os) throws IOException, RendererException {

		Document document = new Document();

		try{
			PdfWriter writer = PdfWriter.getInstance(document, os);
			writer.setStrictImageSequence(true);
			document.open();

			if ( report instanceof RecordReport ){
				//Add the "Date of interview" and "Named clinician" textboxes.
				PdfPTable table = new PdfPTable(5);
				table.setSpacingAfter(15F);
				table.setHorizontalAlignment(Element.ALIGN_LEFT);
				table.setTotalWidth(500f);
				table.setLockedWidth(true);
				table.setWidths(new float[]{0.15f, 0.325f, 0.05f, 0.15f, 0.325f});
				PdfPCell date = new PdfPCell(new Paragraph("Date of interview:"));
				date.setMinimumHeight(30f);
				date.setBorder(0);
				table.addCell(date);
				table.addCell(new PdfPCell());
				PdfPCell spacer = new PdfPCell();
				spacer.setBorder(0);
				table.addCell(spacer);
				PdfPCell clinician = new PdfPCell(new Paragraph("Named clinician:"));
				clinician.setBorder(0);
				table.addCell(clinician);
				table.addCell(new PdfPCell());
				document.add(table);

				RecordReport rr = (RecordReport)report;
				if ( null != rr.getSubject() ){
					Paragraph subject = new Paragraph("Subject: "+rr.getSubject());
					document.add(subject);
				}

				if ( null != rr.getRequestor() ){
					Paragraph requestor = new Paragraph("Requestor: "+rr.getRequestor());
					document.add(requestor);
				}
			}

			if ( null != report.getRequestDate() ){
				Paragraph requestDate = new Paragraph("Date: "+report.getRequestDate().toString());
				document.add(requestDate);
			}

			
			if (report.isShowHeader()) {
				Paragraph spacer = new Paragraph(" ");
				document.add(spacer);

				//Show the time period that the report has been generated for.
				if (null != report.getStartDate() && null != report.getEndDate()) {
					SimpleDateFormat newformat = new SimpleDateFormat("MMMM yyyy");
					SimpleDateFormat oldformat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

					String start ="", end ="";
					try {
						Date startdate = oldformat.parse(report.getStartDate().toString());
						Date enddate = oldformat.parse(report.getEndDate().toString());
						start = newformat.format(startdate);
						end = newformat.format(enddate);
					}
					catch(Exception e) {
						//do nothing
					}
					Paragraph timePeriod = new Paragraph(
							"Time Period: "+start+" until end of "+end);
					document.add(timePeriod);
				}

				//Show the type of summary used in the trends report
				if (null != report.getSummaryType()) {
					Paragraph summary = new Paragraph(
							"Data summary used: "+report.getSummaryType());
					document.add(summary);
				}
				
				//Show the groups used in generating the report
				if (null != report.getGroups() && report.getGroups().size() > 0) {
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
					Paragraph g = new Paragraph(
							"Groups included: "+groups.toString());
					document.add(g);
				}
				
				Paragraph spacer2 = new Paragraph(" ");
				document.add(spacer2);
			}
			
			if ( null != report.getTitle() ){
				Paragraph title = new Paragraph(
						report.getTitle(),
						FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20));
				title.setAlignment(Element.ALIGN_CENTER);
				document.add(title);
			}

			for ( Chart c: report.getCharts() ){
				if ( c != null ) {
					for ( String type: c.getTypes() ){
						//Add some padding above each chart to make it look nicer
						Paragraph padding = new Paragraph(" ");
						document.add(padding);

						if ( Chart.CHART_TABLE.equals(type) ){
							renderTable(document, c);
						}
						else if ( Chart.CHART_BAR.equals(type) ){
							renderBarChart(document, c, writer, PlotOrientation.VERTICAL);
						}
						else if ( Chart.CHART_BAR_HZ.equals(type) ) {
							renderBarChart(document, c, writer, PlotOrientation.HORIZONTAL);
						}
						else if ( Chart.CHART_PIE.equals(type) ){
							renderPieChart(document, c, writer);
						}
						else if ( Chart.CHART_LINE.equals(type) ){
							renderLineChart(document, c, writer, PlotOrientation.VERTICAL);
						}
						else if ( Chart.CHART_LINE_HZ.equals(type) ) {
							renderLineChart(document, c, writer, PlotOrientation.HORIZONTAL);
						}
						else if ( Chart.CHART_STACKED_BAR.equals(type) ) {
							renderStackedBarChart(document, c, writer);
						}
						else if ( Chart.CHART_TIME_SERIES.equals(type) ) {
							renderTimeSeriesChart(document, c, writer);
						}
						else if ( Chart.CHART_GANTT.equals(type) ) {
							renderGanttChart(document, c, writer);
						}
						else if ( ComplexChart.CHART_OVERLAYED_BAR.equals(type) ) {
							renderOverlayedChart(document, (ComplexChart)c, writer);
						}
					}
				}
			}

		} 
		catch (DocumentException ex) {
			throw new RendererException("Unable to render the report '"+report.getTitle()+"' as a PDF", ex);
		} 

		document.close();
	}

	/**
	 * Render a report chart as a table.
	 * 
	 * @param document The PDF document to add the table to.
	 * @param chart The chart to render as a table.
	 * @throws DocumentException
	 */
	private void renderTable(Document document, Chart chart) throws DocumentException {

		renderChartHeader(document, chart);

		PdfPTable table = new PdfPTable(2);	//two cols only (i.e 1+noOfDataSets)
		table.setSpacingBefore(15F);
		table.setSpacingAfter(15F);
		table.setHeaderRows(1);
		//header row
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.LIGHT_GRAY);
		table.addCell(cell);

		//set the title to be the same as the range axis label (typically the y-axis) or the chart title if it's not been set
		String celltitle = chart.getRangeAxisLabel();

		if (chart.getRangeAxisLabel() == null) {
			celltitle = chart.getTitle();
		}

		PdfPCell c = new PdfPCell(new Paragraph(celltitle));
		c.setBackgroundColor(Color.LIGHT_GRAY);
		table.addCell(c);

		for ( ChartRow row: chart.getRows() ){
			if (row.getLabel() != null && !row.getLabel().equals("")) {
				table.addCell(row.getLabel());	
				table.addCell(new PdfPCell());
			}

			//TODO if one series treat differently to multiple series..
			for (int i = 0; i < row.getSeries().length; i++) {
				ChartPoint p = row.getSeries()[i].getPoints()[0];		//assuming a single datapoint

				String seriesLabel = row.getSeries()[i].getLabel();

				if (seriesLabel == null) {
					seriesLabel = "";
				}

				//this will attempt to convert a date label on a series specified as 
				//having date labels containing 
				//integers as "mm yyyy", into a properly formated date
				//if (IValue.TYPE_DATE.equals(p.getValueType())) {
				if (IValue.TYPE_DATE.equals(row.getSeries()[i].getLabelType())) {
					seriesLabel = formatDate(seriesLabel);
				}


				PdfPCell d = new PdfPCell(new Paragraph(seriesLabel));
				d.setIndent(new Float(15));
				table.addCell(d);	//series label
				String value = null;
				if ( null != p.getUnit() ){
					value = p.getValue() + " " + p.getUnit();
				}
				else{
					value = p.getValue();
				}
				table.addCell(value);
			}
		}

		document.add(table);

	}

	/**
	 * Render a report chart as a bar chart.
	 * <p>
	 * JFreeChart is used to create the chart itself, which is then
	 * saved to a byte array as a PNG, before being embedded into the
	 * PDF document.
	 * 
	 * @param document The PDF document to add the chart to.
	 * @param chart The chart to render as a bar chart.
	 * @param writer The PdfWriter used to retrieve the image from
	 * @param orientation The orientation of the bars
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */   
	private void renderBarChart(Document document, Chart chart, PdfWriter writer, PlotOrientation orientation) throws DocumentException, IOException {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		//Charts containing one series can be displayed differently to charts with multiple series
		//e.g the legend is unecessary with a single data series.
		boolean singleSeries = false;
		if (chart.getRows().length == 1) {
			singleSeries = true;
		}

		for ( ChartRow row: chart.getRows()){

			for (int i = 0; i < row.getSeries().length; i++) {
				ChartPoint point = row.getSeries()[i].getPoints()[0];		//assuming a single datapoint

				//set the labels for series and rows to be added to the dataset for each datapoint (or provide an empty string to look pretty)
				String rowLabel, seriesLabel;
				seriesLabel = row.getSeries()[i].getLabel();
				if (singleSeries) {
					rowLabel = chart.getTitle();
				}
				else {
					rowLabel = row.getLabel();
				}
				if (rowLabel == null) {
					rowLabel = "";
				}
				if (seriesLabel == null) {
					seriesLabel = "";
				}	

				try{ 
					if (IValue.TYPE_DATE.equals(row.getSeries()[i].getLabelType())) {
						seriesLabel = formatDate(row.getSeries()[i].getLabel());
					}

					if ( null == point.getValue() || 
							IValue.TYPE_STRING.equals(point.getValueType()) ||
							IValue.TYPE_DATE.equals(point.getValueType())) {
						dataset.setValue(0d, rowLabel, seriesLabel);
					}
					else{
						dataset.setValue(Double.parseDouble(point.getValue()),rowLabel, seriesLabel);
					}

				}
				catch (NullPointerException npe) {
					npe.printStackTrace();
					throw new IOException("Unable to create graph. "+npe.getCause());
				}
			}
		}

		String rangeAxisLabel = chart.getRangeAxisLabel();

		boolean legend = true;		//include a legend by default

		if (singleSeries) { legend = false; }

		JFreeChart barChart = ChartFactory.createBarChart(
				chart.getTitle(),
				null, 
				rangeAxisLabel, 
				dataset, 
				orientation,
				legend, 
				false, 
				false);

		CategoryPlot plot = barChart.getCategoryPlot();
		CategoryAxis axis = plot.getDomainAxis();
		plot.mapDatasetToRangeAxis(1, 1);
		if (orientation.equals(PlotOrientation.VERTICAL)) {
			axis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2.0));

		}
		else {
			axis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
		}

		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());	//ensure y-axis uses integer units

		Image img = getImageFromChart(barChart, writer, 500, 500);
		document.add(img);

	}


	/**
	 * Render a report chart as a pie chart.
	 * <p>
	 * JFreeChart is used to create the chart itself, which is then
	 * saved to a byte array as a PNG, before being embedded into the
	 * PDF document.
	 * 
	 * @param document The PDF document to add the chart to.
	 * @param chart The chart to render as a pie chart.
	 */
	private void renderPieChart(Document document, Chart chart, PdfWriter writer) throws DocumentException, IOException {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for ( ChartRow row: chart.getRows()){

			for (int i = 0; i < row.getSeries().length; i++) {
				ChartPoint point = row.getSeries()[i].getPoints()[0];		//assuming a single datapoint

				String rowLabel, seriesLabel;
				rowLabel = row.getSeries()[i].getLabel();
				seriesLabel = row.getLabel();

				if (rowLabel == null) {
					rowLabel = "";
				}
				if (seriesLabel == null) {
					seriesLabel = "";
				}

				try{
					//rows and series are swapped on the pie chart
					if (IValue.TYPE_DATE.equals(row.getSeries()[i].getLabelType())) {
						rowLabel = formatDate(rowLabel);
					}

					if ( null == point.getValue() || 
							IValue.TYPE_STRING.equals(point.getValueType()) ||
							IValue.TYPE_DATE.equals(point.getValueType())) {
						dataset.setValue(0d, rowLabel, seriesLabel);
					}
					else{
						dataset.setValue(Double.parseDouble(point.getValue()), rowLabel, seriesLabel);
					}

				}
				catch (NullPointerException npe) {
					npe.printStackTrace();
					throw new IOException("Unable to create graph. "+npe.getCause());
				}
			}
		}

		JFreeChart pieChart = ChartFactory.createMultiplePieChart(
				chart.getTitle(),
				dataset,
				TableOrder.BY_COLUMN,
				true,
				false,
				false);

		MultiplePiePlot mpp = (MultiplePiePlot)pieChart.getPlot();
		PiePlot pp = (PiePlot)mpp.getPieChart().getPlot();
		pp.setLabelGenerator(new StandardPieSectionLabelGenerator("{1}")); //show only numbers - no names or '%'s.
		//don't display null/empty values
		pp.setIgnoreNullValues(true);
		pp.setIgnoreZeroValues(true);

		Image img = getImageFromChart(pieChart, writer, 500, 500);
		document.add(img);

	}


	/**
	 * Render a report chart as a line graph.
	 * <p>
	 * JFreeChart is used to create the chart itself, which is then
	 * saved to a byte array as a PNG, before being embedded into the
	 * PDF document.
	 * 
	 * @param document The PDF document to add the chart to.
	 * @param chart The chart to render as a line graph.
	 * @param writer The PdfWriter used to retrieve the image from
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void renderLineChart(Document document, Chart chart, PdfWriter writer, PlotOrientation orientation) throws DocumentException, IOException {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		//charts containing one series can be displayed differently to charts with multiple series
		//e.g the legend is unecessary with a single data series.
		boolean singleSeries = false;

		if (chart.getRows().length == 1) {
			singleSeries = true;
		}

		for ( ChartRow row: chart.getRows()){

			for (int i = 0; i < row.getSeries().length; i++) {
				ChartPoint point = row.getSeries()[i].getPoints()[0];		//assuming a single datapoint

				//set the correct labels for series and rows (or provide an empty string to look pretty)
				String rowLabel, seriesLabel;
				seriesLabel = row.getSeries()[i].getLabel();
				if (singleSeries) {
					rowLabel = chart.getTitle();
				}
				else {
					rowLabel = row.getLabel();
				}
				if (rowLabel == null) {
					rowLabel = "";
				}
				if (seriesLabel == null) {
					seriesLabel = "";
				}

				try{
					if (IValue.TYPE_DATE.equals(row.getSeries()[i].getLabelType())) {
						seriesLabel = formatDate(row.getSeries()[i].getLabel());
					}

					if ( null == point.getValue() || 
							IValue.TYPE_STRING.equals(point.getValueType()) ||
							IValue.TYPE_DATE.equals(point.getValueType())) {
						dataset.setValue(0d, rowLabel, seriesLabel);
					}
					else{
						dataset.setValue(Double.parseDouble(point.getValue()), rowLabel, seriesLabel);
					}

				}
				catch (NullPointerException npe) {
					npe.printStackTrace();
					throw new IOException("Unable to create graph. "+npe.getCause());
				}
			}
		}

		String rangeAxisLabel = chart.getRangeAxisLabel();

		boolean legend = true;		//include a legend by default

		if (singleSeries) { legend = false; }	

		JFreeChart lineGraph = ChartFactory.createLineChart(
				chart.getTitle(),
				null, 
				rangeAxisLabel, 
				dataset, 
				orientation,
				legend, 
				false, 
				false);

		CategoryPlot plot = lineGraph.getCategoryPlot();
		CategoryAxis axis = plot.getDomainAxis();

		if (plot.equals(PlotOrientation.VERTICAL)) {
			axis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2.0));
		}
		else {
			axis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
		}

		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());	//ensure y-axis uses integer units

		Image img = getImageFromChart(lineGraph, writer, 500, 500);
		document.add(img);

	}

	/**
	 * Render a report chart as a stacked bar chart.
	 * <p>
	 * JFreeChart is used to create the chart itself, which is then
	 * saved to a byte array as a PNG, before being embedded into the
	 * PDF document.
	 * 
	 * @param document The PDF document to add the chart to.
	 * @param chart The chart to render as a bar chart.
	 * @param writer The PdfWriter used to retrieve the image from.
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void renderStackedBarChart(Document document, Chart chart, PdfWriter writer) throws DocumentException, IOException {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for ( ChartRow row: chart.getRows()){

			for (int i = 0; i < row.getSeries().length; i++) {
				ChartPoint point = row.getSeries()[i].getPoints()[0];		//assuming a single datapoint

				//set the correct labels for series and rows (or provide an empty string to look pretty)
				String rowLabel, seriesLabel;
				seriesLabel = row.getSeries()[i].getLabel();

				rowLabel = row.getLabel();

				if (rowLabel == null) {
					rowLabel = "";
				}
				if (seriesLabel == null) {
					seriesLabel = "";
				}

				try{
					if (IValue.TYPE_DATE.equals(row.getSeries()[i].getLabelType())) {
						seriesLabel = formatDate(row.getSeries()[i].getLabel());
					}

					if ( null == point.getValue() || 
							IValue.TYPE_STRING.equals(point.getValueType()) ||
							IValue.TYPE_DATE.equals(point.getValueType())) {
						dataset.setValue(0d, rowLabel, seriesLabel);						
					}
					else{
						dataset.setValue(Double.parseDouble(point.getValue()), rowLabel, seriesLabel);
					}

				}
				catch (NullPointerException npe) {
					npe.printStackTrace();
					throw new IOException("Unable to create graph. "+npe.getCause());
				}
			}
		}

		String rangeAxisLabel = chart.getRangeAxisLabel();

		JFreeChart barChart = ChartFactory.createStackedBarChart(
				chart.getTitle(),
				null, 
				rangeAxisLabel, 
				dataset, 
				PlotOrientation.VERTICAL,
				true, 
				false, 
				false);

		CategoryPlot plot = barChart.getCategoryPlot();
		CategoryAxis axis = plot.getDomainAxis();
		axis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2.0));

		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());	//ensure y-axis uses integer units

		if (chart.isUsePercentages()) {
			StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
			renderer.setRenderAsPercentages(true);

			StandardCategoryItemLabelGenerator percentLabels = new StandardCategoryItemLabelGenerator("{3}", NumberFormat.getPercentInstance());
			renderer.setItemLabelGenerator(percentLabels);
			renderer.setItemLabelsVisible(true);

			plot.setRenderer(renderer);
			rangeAxis.setTickLabelsVisible(false);	//values are displayed on the individual bars so not required on the axis 
		}

		Image img = getImageFromChart(barChart, writer, 500, 500);
		document.add(img);

	}

	/**
	 * Render a report chart as a line graph with a time series along one axis
	 * <p>
	 * JFreeChart is used to create the chart itself, which is then
	 * saved to a byte array as a PNG, before being embedded into the
	 * PDF document.
	 * 
	 * @param document The PDF document to add the chart to.
	 * @param chart The chart to render as a line graph.
	 * @param writer The PdfWriter used to retrieve the image from
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void renderTimeSeriesChart(Document document, Chart chart, PdfWriter writer) throws DocumentException, IOException {

		//TODO chart currently only displays one line if several have the same values over time.

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		//one time series == a single chart row

		//charts containing one series can be displayed differently to charts with multiple series
		//e.g the legend is unecessary with a single data series.
		boolean singleSeries = false;

		if (chart.getRows().length == 1) {
			singleSeries = true;
		}

		for ( ChartRow row: chart.getRows()){

			TimeSeries s = new TimeSeries(row.getLabel(), Month.class);

			for (int i = 0; i < row.getSeries().length; i++) {
				ChartPoint point = row.getSeries()[i].getPoints()[0];		//assuming a single datapoint

				//format the date labels to retrieve the month and year, as expected by a TimeSeries
				String date = row.getSeries()[i].getLabel();

				int month = 0, year = 0;

				if (date != null) {
					try {
						String[] tmp = date.split(" ", 2);
						month = Integer.parseInt(tmp[0]);
						year  = Integer.parseInt(tmp[1]);
					}
					catch (Exception e) {
						//do nothing
					}
				}

				try{
					if ( null == point.getValue() || 
							IValue.TYPE_DATE.equals(point.getValueType()) ||
							IValue.TYPE_STRING.equals(point.getValueType())){
						s.add(new Month(month, year), 0d);
					}
					else{
						s.add(new Month(month, year), Double.parseDouble(point.getValue()));
					}

				}
				catch (NullPointerException npe) {
					npe.printStackTrace();
					throw new IOException("Unable to create graph: "+npe.getCause());
				}
			}
			dataset.addSeries(s);
		}


		String rangeAxisLabel = chart.getRangeAxisLabel();

		boolean legend = true;		//include a legend by default

		if (singleSeries) { legend = false; }

		JFreeChart graph = ChartFactory.createTimeSeriesChart(
				chart.getTitle(),
				null, 				//x-axis label
				rangeAxisLabel, 	//y-axis label (null if not set)
				dataset, 
				legend, 
				false, 
				false);


		XYPlot plot = graph.getXYPlot();
		DateAxis axis = (DateAxis) plot.getDomainAxis();
		ValueAxis rangeAxis = plot.getRangeAxis();

		//Ensure that the y-axis always shows integer numbers
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		//Show plot points 
		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setShapesFilled(true);
			renderer.setShapesVisible(true);
		}

		//Specifies that the smallest tick units should be displayed in months
		//at least. Otherwise, with a small number of months, several ticks are
		//shown for each month and JFreeChart doesn't position the chart points correctly.
		final TickUnits standardUnits = new TickUnits();
		standardUnits.add(
				new DateTickUnit(DateTickUnit.MONTH, 1, new SimpleDateFormat("MMM-yyyy"))
		);
		axis.setStandardTickUnits(standardUnits); 

		int monthsDiff = dateDifference(axis.getMinimumDate(), axis.getMaximumDate());
		if ( dataset.getItemCount(0) > 8 || monthsDiff > 8) {
			//turn the date axis labels on their side if there are enough to make them overlap
			axis.setVerticalTickLabels(true);
		}


		/* creates a secondary dataseries showing the totals for each month */
		if (chart instanceof ComplexChart) {
			ComplexChart chart2 = (ComplexChart) chart;

			final TimeSeriesCollection dataset2 = new TimeSeriesCollection();

			for ( ChartRow row: chart2.getSecondaryRows() ){

				if (row != null && row.getSeries() != null) {
					TimeSeries s = new TimeSeries(row.getLabel(), Month.class);
					for (int i = 0; i < row.getSeries().length; i++) {
						ChartPoint point = row.getSeries()[i].getPoints()[0];		//assuming a single datapoint

						//format the date labels to retrieve the month and year, as expected by a TimeSeries
						String date = row.getSeries()[i].getLabel();

						int month = 0, year = 0;

						if (date != null) {
							try {
								String[] tmp = date.split(" ", 2);
								month = Integer.parseInt(tmp[0]);
								year  = Integer.parseInt(tmp[1]);
							}
							catch (Exception e) {
								//do nothing
							}
						}

						try{
							if ( null == point.getValue() || 
									IValue.TYPE_DATE.equals(point.getValueType()) ||
									IValue.TYPE_STRING.equals(point.getValueType())){
								s.add(new Month(month, year), 0d);
							}
							else{
								s.add(new Month(month, year), Double.parseDouble(point.getValue()));
							}

						}
						catch (NullPointerException npe) {
							npe.printStackTrace();
							throw new IOException("Unable to create graph: "+npe.getCause());
						}
					}
					dataset2.addSeries(s);
				}


				//Only add the range axis if chart2 has been populated with series
				if (chart2.getSecondaryRows()[0] != null && chart2.getSecondaryRows()[0].getSeries() != null ) {
					//add a second dataset and renderer, for a bar chart 
					final XYItemRenderer barRenderer = new XYBarRenderer(0.35);
					barRenderer.setBasePaint(Color.gray);
					barRenderer.setPaint(Color.lightGray);
					//plot the secondary vertical axis
					final ValueAxis axis2 = new NumberAxis(chart2.getSecondaryAxisLabel());
					axis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
					plot.setRangeAxis(2, axis2);
					plot.setDataset(2, dataset2);
					plot.setRenderer(2, barRenderer);
					plot.mapDatasetToRangeAxis(2, 2);
					plot.mapDatasetToDomainAxis(1, 2);
					//ensure the bar chart is shown behind the line charts
					plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);

				}
			}
		}


		Image img = getImageFromChart(graph, writer, 500, 500);
		document.add(img);

	}

	/**
	 * Render a report chart as a gantt chart.
	 * <p>
	 * JFreeChart is used to create the chart itself, which is then
	 * saved to a byte array as a PNG, before being embedded into the
	 * PDF document.
	 * 
	 * @param document The PDF document to add the chart to.
	 * @param chart The chart to render as a bar chart.
	 * @param writer The PdfWriter used to retrieve the image from.
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void renderGanttChart(Document document, Chart chart, PdfWriter writer) throws DocumentException, IOException {

		final TaskSeriesCollection dataset = new TaskSeriesCollection();
		Map<String,Task> tasks = new LinkedHashMap<String,Task>();

		for ( ChartRow row: chart.getRows()){
			String rowLabel;
			rowLabel = row.getLabel();

			if (rowLabel == null) {
				rowLabel = "";
			}

			final TaskSeries s1 = new TaskSeries(rowLabel);

			for (int i = 0; i < row.getSeries().length; i++) {
				//assumes points have been added for both start and end
				ChartPoint startDate = row.getSeries()[i].getPoints()[0];
				ChartPoint endDate   = row.getSeries()[i].getPoints()[1];

				//set the correct labels for series and rows (or provide an empty string to look pretty)
				String seriesLabel;
				seriesLabel = row.getSeries()[i].getLabel();

				if (seriesLabel == null) {
					seriesLabel = "";
				}

				try{
					Task task;

					if (startDate != null && endDate != null) {

						SimpleDateFormat sfd = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

						Date start = new Date();
						try {
							if (startDate.getValue() != null) {
								start = sfd.parse(startDate.getValue());
							}
							else {
								start = null;
							}
						}
						catch (ParseException pe) {
							start = null;
						}


						Date end = new Date();
						try {
							if (endDate.getValue() != null) {
								end = sfd.parse(endDate.getValue());
							}
							else {
								end = null;
							}
						}
						catch (ParseException pe) {
							end = null;
						}

						if (start != null && end != null) {
							if (start.after(end)) {
								//data is incorrect, so don't include in report
								task = new Task(seriesLabel, null);
							}
							else {
								task = new Task(seriesLabel,
										new SimpleTimePeriod( start, end ));
							}
						}
						else {
							task = new Task(seriesLabel, null);
						}
					}
					else {
						task = new Task(seriesLabel, null);
					}

					//find out if the series label has been used before - if so, it will be added as a subtask of the previous row 
					if (tasks.containsKey(seriesLabel) && tasks.get(seriesLabel).getDuration() != null) {
						if (task.getDuration() != null) {

							//if a task is to have subtasks then the 'parent' task has to act as a container
							//for those tasks (so has to exist for the earliest start date and the last end date)

							List<Task> existing = new ArrayList<Task>();
							//add the new task
							existing.add(task);
							//move the existing task to a subtask (assuming it's a 'real' task)
							if (tasks.get(seriesLabel).getSubtaskCount() == 0) {
								existing.add(tasks.get(seriesLabel));
							}
							//retrieve all current subtasks
							for (int j = 0; j < tasks.get(seriesLabel).getSubtaskCount(); j++) {
								existing.add(tasks.get(seriesLabel).getSubtask(j));
							}

							//calc start and end dates for the new 'parent' task
							Date start, end;
							start = existing.get(0).getDuration().getStart();
							end = existing.get(0).getDuration().getEnd();

							for (Task t: existing) {
								if (t.getDuration().getStart().before(start)) {
									start = t.getDuration().getStart();
								}
								if (t.getDuration().getEnd().after(end)) {
									end = t.getDuration().getEnd();
								}
							}

							//finally add the (sub)tasks
							Task parent = new Task(seriesLabel, new SimpleTimePeriod( start, end ));
							tasks.put(seriesLabel, parent);

							for (Task t: existing) {
								parent.addSubtask(t);
							}

						}
					}
					else {
						tasks.put(seriesLabel, task);
					}

				}
				catch (NullPointerException npe) {
					npe.printStackTrace();
					throw new IOException("Unable to create graph. "+npe.getCause());
				}
			}

			for (String s: tasks.keySet()) {
				s1.add(tasks.get(s));
			}

			dataset.add(s1);

		}

		String rangeAxisLabel = chart.getRangeAxisLabel();

		final JFreeChart ganttChart = ChartFactory.createGanttChart(
				chart.getTitle(),  // chart title
				null,              // domain axis label
				rangeAxisLabel,    // range axis label
				dataset,           // data
				false,             // include legend
				false,             // tooltips
				false              // urls
		);    

		Image img = getImageFromChart(ganttChart, writer, 500, 500);
		document.add(img);

	}

	/**
	 * (Untested!) Render a chart containing dual series of data as a bar chart
	 * with an overlayed line graph.
	 * <p>
	 * JFreeChart is used to create the chart itself, which is then
	 * saved to a byte array as a PNG, before being embedded into the
	 * PDF document.
	 * 
	 * @param document The PDF document to add the chart to.
	 * @param chart The ComplexChart to render.
	 * @param writer The PdfWriter used to retrieve the image from.
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void renderOverlayedChart(Document document, ComplexChart chart, PdfWriter writer) throws DocumentException, IOException {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for ( ChartRow row: chart.getRows()){

			for (int i = 0; i < row.getSeries().length; i++) {
				ChartPoint point = row.getSeries()[i].getPoints()[0];		//assuming a single datapoint

				try{
					if ( null == point.getValue() || 
							IValue.TYPE_DATE.equals(point.getValueType()) ||
							IValue.TYPE_STRING.equals(point.getValueType())){
						dataset.setValue(0d, row.getSeries()[i].getLabel(), row.getLabel());
					}
					else{
						dataset.setValue(Double.parseDouble(point.getValue()), row.getSeries()[i].getLabel(), row.getLabel());
					}
				}
				catch (NullPointerException npe) {
					npe.printStackTrace();
					throw new IOException("Unable to create graph. "+npe.getCause());
				}
			}
		}

		JFreeChart barChart = ChartFactory.createBarChart(
				chart.getTitle(),
				null, 
				null, 
				dataset, 
				PlotOrientation.VERTICAL,
				true, 
				false, 
				false);

		CategoryPlot plot = barChart.getCategoryPlot();

		//Create second dataset
		final DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();
		for ( ChartRow row: chart.getRows()){
			for (int i = 0; i < row.getSeries().length; i++) {
				ChartPoint point = row.getSeries()[i].getPoints()[0];		//assuming a single datapoint

				try{
					if ( null == point.getValue() || 
							IValue.TYPE_DATE.equals(point.getValueType()) ||
							IValue.TYPE_STRING.equals(point.getValueType())){
						dataset2.setValue(0d, null, row.getLabel());
					}
					else{
						dataset2.setValue(Double.parseDouble(point.getValue()), null, row.getLabel());
					}
				}
				catch (NullPointerException npe) {
					npe.printStackTrace();
					throw new IOException("Unable to create graph. "+npe.getCause());
				}
			}
		}

		plot.setDataset(1, dataset2);
		plot.mapDatasetToRangeAxis(1, 1);

		CategoryAxis axis = plot.getDomainAxis();
		axis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2.0));

		final ValueAxis axis2 = new NumberAxis("Secondary");
		axis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		plot.setRangeAxis(1, axis2);

		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());	//ensure y-axis uses integer units

		Image img = getImageFromChart(barChart, writer, 500, 500);
		document.add(img);

	}


	private void renderChartHeader(Document document, Chart chart) throws DocumentException {
		Paragraph title = new Paragraph(
				chart.getTitle(),
				FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
		title.setAlignment(Element.ALIGN_CENTER);
		document.add(title);        
	}

	private Image getImageFromChart(JFreeChart chart, PdfWriter writer, int width, int height) throws DocumentException {
		PdfContentByte cb = writer.getDirectContent();
		PdfTemplate tp = cb.createTemplate(width, height);
		Graphics2D g2d = tp.createGraphics(width, height, new DefaultFontMapper());
		Rectangle2D r2d = new Rectangle2D.Double(0, 0, width, height);
		chart.draw(g2d, r2d);
		g2d.dispose();
		return Image.getInstance(tp);
	}

	/**
	 * Converts a String containing integers formated as "mm yyyy" into a nicely formated date for use 
	 * on chart labels.
	 * 
	 * @param label
	 * @return formatedLabel
	 */
	private String formatDate(String label) {

		if (label == null) {
			return null;
		}

		String[] date = null;
		boolean badDateFormat = false;

		String niceDate = label;	//this won't change unless the date formating is successful

		try {
			date = label.split(" ", 2);
		}
		catch (Exception e) {
			badDateFormat = true;
		}

		if (date.length < 2) {
			badDateFormat = true;
		}

		if (badDateFormat) {
			//give up trying to format date nicely as there was an error earlier
			return niceDate;
		}

		try {
			SimpleDateFormat format = new SimpleDateFormat("MMM-yyyy");
			Calendar cal = new GregorianCalendar(Integer.parseInt(date[1]), Integer.parseInt(date[0]), 0);
			Date date1 = cal.getTime();

			niceDate = format.format(date1);
		}
		catch (Exception e) {
			//do nothing to the date
		}

		return niceDate;
	}

	private int dateDifference(Date startDate, Date endDate) {
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);

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

		int months = 0;

		while (start.before(end)) {
			start.add(Calendar.MONTH, +1);
			months++;
		}
		return months;
	}
}
