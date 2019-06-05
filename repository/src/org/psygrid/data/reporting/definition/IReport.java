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

package org.psygrid.data.reporting.definition;

import org.psygrid.data.model.IPersistent;
import org.psygrid.data.model.hibernate.DataSet;

/**
 * Interface to represent the definition of a single report .
 * 
 * @author Rob Harper
 *
 */
public interface IReport extends IPersistent {

    /**
     * Get the dataset which this report is related to.
     * 
     * @return The dataset.
     */
    public DataSet getDataSet();
    
    public void setDataSet(DataSet ds);
    
    /**
     * Get the title of the report.
     * 
     * @return The title.
     * 
     */
    public String getTitle();

    /**
     * Set the title of the report.
     * 
     * @param name The title.
     */
    public void setTitle(String name);
    
    /**
     * Get whether the report can be used as a template for
     * the web based reports interface (meaning it will be 
     * viewable via psygrid-web). This is true by default as
     * most reports, except for some management reports, can
     * automatically be used.
     * 
     * @return template
     */
    public boolean isTemplate();

    /**
     * Set whether the report can be used as a template for
     * the web based reports interface (meaning it will be viewable
     * via psygrid-web). This is true by default as most reports, 
     * except for some management reports, can automatically be used.
     * 
     * @param template
     */
	public void setTemplate(boolean template);
	
	/**
	 * Get whether a header is to be displayed on the generated report.
	 * 
	 * For example, this would be set to false for UKCRN reports.
	 * 
	 * @return showHeader
	 */
	public boolean isShowHeader();
	
	/**
	 * Set whether a header is to be displayed on the generated report.
	 * 
	 * For example, this would be set to false for UKCRN reports.
	 * 
	 * @param showHeader
	 */
	public void setShowHeader(boolean showHeader);
    
    public org.psygrid.data.reporting.definition.dto.Report toDTO();
}