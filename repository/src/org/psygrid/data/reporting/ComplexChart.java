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
 * A complex chart allows a secondary data series to be held
 * allowing overlayed charts to be created.
 * 
 * This is also used to hold a secondary dataseries when
 * 'showtotals' is enabled on trends charts.
 * 
 * @author Lucy Bridges
 *
 */
public class ComplexChart extends Chart {
	
	public static final String CHART_OVERLAYED_BAR = "Overlayed Bar";
	
    /**
     * The rows of data in the chart.
     * containing multiple ChartRows
     */
	private ChartRow[] secondaryRows;

    protected String secondaryAxisLabel = null;
    
    public ChartRow[] getSecondaryRows() {
        return secondaryRows;
    }

    public void setSecondaryRows(ChartRow[] secondaryRows) {
        this.secondaryRows = secondaryRows;
    }

	/**
	 * @return the secondaryAxisLabel
	 */
	public String getSecondaryAxisLabel() {
		return secondaryAxisLabel;
	}

	/**
	 * @param secondaryAxisLabel the secondaryAxisLabel to set
	 */
	public void setSecondaryAxisLabel(String secondaryAxisLabel) {
		this.secondaryAxisLabel = secondaryAxisLabel;
	}

}
