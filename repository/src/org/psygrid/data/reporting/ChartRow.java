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
 * Class to represent a single row of data in a Chart.
 * 
 * @author Rob Harper
 *
 */
public class ChartRow {

    /**
     * The label for the row.
     */
    private String label;
    
    /**
     * The type of the label.
     * <p>
     * Generally the label will be a string, but for
     * certain types of chart (e.g. X-Y plot) the label 
     * could be a date or a number.
     */
    private String labelType;
    
    /**
     * The values for this row for each series.
     * <p>
     * The number of items here should be equal to the
     * number of seriesLabels in the parent chart.
     */
    private ChartSeries[] series;
    
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabelType() {
        return labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }

    public ChartSeries[] getSeries() {
        return series;
    }

    public void setSeries(ChartSeries[] series) {
        this.series = series;
    }

    
}
