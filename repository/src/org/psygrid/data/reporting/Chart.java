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

package org.psygrid.data.reporting;

/**
 * Class to represent a single chart in a report.
 * <p>
 * This class attempts to provide a generic structure
 * for defining the data in any type of chart (e.g. table,
 * bar chart, pie chart, etc.) plus a rendering hint of
 * how the data should be rendered visually.
 * 
 * @author Rob Harper
 *
 */
public class Chart {

    public static final String CHART_TABLE   = "Table";
    public static final String CHART_BAR     = "Bar";
    public static final String CHART_BAR_HZ  = "Bar Horizontal";
    public static final String CHART_PIE     = "Pie";
    public static final String CHART_LINE    = "Line";
    public static final String CHART_LINE_HZ = "Line Horizontal";
    public static final String CHART_STACKED_BAR = "Stacked Bar";
    public static final String CHART_TIME_SERIES = "Time Series";
    public static final String CHART_GANTT   = "Gantt";
    
    /**
     * The types of the chart.
     * <p>
     * Basically a rendering hint as to how the data
     * contained in the Chart object should be displayed. 
     * If more than one type is present then the chart 
     * data should be rendered multiple times, once for each
     * type.
     */
    protected String[] types;
    
    /**
     * The title of the chart.
     */
    protected String title;
    
    protected String rangeAxisLabel = null;
    
    protected boolean usePercentages = false;
    
    /**
     * The rows of data in the chart.
     */
    protected ChartRow[] rows;

    public ChartRow[] getRows() {
        return rows;
    }

    public void setRows(ChartRow[] rows) {
        this.rows = rows;
    }

 
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String getRangeAxisLabel() {
    	return rangeAxisLabel;
    }
    
    public void setRangeAxisLabel(String rangeAxisLabel) {
    	this.rangeAxisLabel = rangeAxisLabel;
    }

    /**
     * Get whether the chart is to use percentage based values
     * 
     * @return usePercentages
     */
	public boolean isUsePercentages() {
		return usePercentages;
	}

	/**
	 * Set whether the chart is to use percentage based values
	 * 
	 * @param usePercentages
	 */
	public void setUsePercentages(boolean usePercentages) {
		this.usePercentages = usePercentages;
	}
}
