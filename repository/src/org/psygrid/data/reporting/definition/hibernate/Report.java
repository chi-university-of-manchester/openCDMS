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

import java.util.HashMap;
import java.util.Map;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.reporting.definition.IReport;

/**
 * Class to represent the definition of a single report.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_reports"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class Report extends Persistent implements IReport {

    /**
     * The dataset which this report is related to.
     */
    private DataSet dataSet;
    
    /**
     * The title of the report
     */
    private String title;
    
    private boolean template = true;
    
    private boolean showHeader = true;
    
    public Report(){};
    
    public Report(DataSet ds, String title){
        this.dataSet = (DataSet)ds;
        this.title = title;
    }
    
    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.DataSet"
     *                        column="c_dataset_id"
     *                        not-null="true"
     *                        unique="false"
     *                        cascade="none"
     */
    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = (DataSet)dataSet;
    }

    /**
     * @hibernate.property column="c_title"
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    /**
     * @return template
     * 
     * @hibernate.property column="c_template"
     */
    public boolean isTemplate() {
		return template;
	}

	/**
	 * Get whether a header is to be displayed on the generated report.
	 * 
	 * For example, this would be set to false for UKCRN reports, but 
	 * is true by default.
	 * 
	 * @return showHeader
	 * 
	 * @hibernate.property column="c_show_header"
	 */
	public boolean isShowHeader() {
		return showHeader;
	}
	
	/**
	 * Set whether a header is to be displayed on the generated report.
	 * 
	 * For example, this would be set to false for UKCRN reports.
	 * 
	 * @param showHeader
	 */
	public void setShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
	}
    
    /**
     * 
     * @param template
     */
	public void setTemplate(boolean template) {
		this.template = template;
	}

	public org.psygrid.data.reporting.definition.dto.Report toDTO(){
        return toDTO(RetrieveDepth.REP_SAVE);
    }
    
    public org.psygrid.data.reporting.definition.dto.Report toDTO(RetrieveDepth depth){
        Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
        return toDTO(dtoRefs, depth);
    }
    
    @Override
    public abstract org.psygrid.data.reporting.definition.dto.Report toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);

    public void toDTO(org.psygrid.data.reporting.definition.dto.Report dtoR, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        super.toDTO(dtoR, dtoRefs, depth);
        dtoR.setTitle(this.title);
        dtoR.setTemplate(template);
        dtoR.setShowHeader(showHeader);
        if ( null != this.dataSet ){
            dtoR.setDataSet(this.dataSet.toDTO(dtoRefs, depth));
        }
    }

}
