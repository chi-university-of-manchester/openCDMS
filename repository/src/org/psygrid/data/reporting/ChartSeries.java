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
 * Class to represent a single group of data in a row of a dataset.
 * 
 * @author Lucy Bridges
 *
 */
public class ChartSeries {

    /**
     * The label for the series.
     */
    private String label;
    
    /**
     * The type of the label.
     * <p>
     * Generally the label will be a string, but for
     * certain types of chart (e.g. X-Y plot) the label 
     * could be a date or a number.
     * 
     * If the label is a date an attempt will be made to
     * convert it to a 'pretty' format. i.e a date 
     * containing integers of the format 'mm yyyy' will
     * be converted to 'MMM yyyy'.
     */
    private String labelType;
    
    /**
     * The values for this row for each series.
     * <p>
     * The number of items here should be equal to the
     * number of seriesLabels in the parent chart.
     */
    private ChartPoint[] points;

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

    public ChartPoint[] getPoints() {
        return points;
    }

    public void setPoints(ChartPoint[] points) {
        this.points = points;
    }
    
}
