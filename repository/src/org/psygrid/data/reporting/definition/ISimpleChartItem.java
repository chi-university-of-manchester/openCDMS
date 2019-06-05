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

import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.SectionOccurrence;

/**
 * Interface to represent a single item in a simple chart.
 * 
 * @author Rob Harper
 *
 */
public interface ISimpleChartItem extends IAbstractChartItem {

    public static final String LABEL_LABEL_ONLY = "Label";
    public static final String LABEL_TEXT_ONLY  = "Text";
    public static final String LABEL_LABEL_TEXT = "LabelText";
    
    /**
     * Get the document occurrence that the simple chart item references.
     * <p>
     * When generating a report for a given record only responses contained
     * by a document instance referencing this document occurrence will be
     * considered.
     * 
     * @return The document occurrence.
     */
    public DocumentOccurrence getDocOccurrence() ;

    /**
     * Set the document occurrence that the simple chart item references.
     * <p>
     * When generating a report for a given record only responses contained
     * by a document instance referencing this document occurrence will be
     * considered.
     * 
     * @param docOccurrence The document occurrence.
     */
    public void setDocOccurrence(DocumentOccurrence docOccurrence);

    /**
     * Get the entry that the simple chart item references.
     * <p>
     * When generating a report for a given record the value of the response
     * to this entry will be quoted in the report.
     * 
     * @return The entry.
     */
    public Entry getEntry();

    /**
     * Set the entry that the simple chart item references.
     * <p>
     * When generating a report for a given record the value of the response
     * to this entry will be quoted in the report.
     * 
     * @param entry
     */
    public void setEntry(Entry entry);

    /**
     * Get the section occurrence that the simple chart item references.
     * <p>
     * When generating a report for a given record only responses referencing 
     * this section occurrence will be considered.
     * 
     * @return The section occurrence.
     */
    public SectionOccurrence getSecOccurrence();

    /**
     * Set the section occurrence that the simple chart item references.
     * <p>
     * When generating a report for a given record only responses referencing 
     * this section occurrence will be considered.
     * 
     * @param secOccurrence The section occurrence.
     */
    public void setSecOccurrence(SectionOccurrence secOccurrence);

    /**
     * Get the options to be passed to org.psygrid.data.model.hibernate.Value#getReportValueAsString
     * when getting the value to put in the report.
     * 
     * @return The options.
     */
    public String getOptions();

    /**
     * Set the options to be passed to org.psygrid.data.model.hibernate.Value#getReportValueAsString
     * when getting the value to put in the report.
     * 
     * @param options
     */
    public void setOptions(String options);

    /**
     * Get the options used when forming the label from the
     * chart item.
     *
     * @return The label options.
     */
    public String getLabelOptions();

    /**
     * Set the options used when forming the label from the
     * chart item.
     *
     * @param labelOptions The label options.
     */
    public void setLabelOptions(String labelOptions);
}
