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

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.SectionOccurrence;

public interface Factory {

    /**
     * Create a new report with the given dataset and title.
     * 
     * @param ds The dataset that the report is associated with.
     * @param title The title of the report.
     * @return The new report.
     */
    public IRecordReport createRecordReport(DataSet ds, String title);
    
    /**
     * Create a new management report with the given dataset 
     * and title.
     * 
     * @param ds The dataset that the report is associated with.
     * @param title The title of the report.
     * @return The new report.
     */
    public IManagementReport createManagementReport(DataSet ds, String title);
    
    /**
     * Create a new trends report with the given dataset and title.
     * 
     * This type of report is used to show a summary of data entered
     * across a dataset, highlighting any trends.
     * 
     * @param ds
     * @param title
     * @return The new report
     */
    public ITrendsReport createTrendsReport(DataSet ds, String title);
    
    /**
     * Create a new simple chart with the given type and title.
     * 
     * @param type The type of the chart.
     * @param title The title of the chart.
     * @return The new simple chart.
     */
    public IRecordChart createSimpleChart(String type, String title);
      
    /**
     * Create a new simple chart item for the given entry, 
     * document occurrence and section occurrence.
     * 
     * @param entry The entry.
     * @param docOcc The document occurrence.
     * @param secOcc The section occurrence.
     * @return The new simple chart.
     */
    public ISimpleChartItem createSimpleChartItem(Entry entry, DocumentOccurrence docOcc, SectionOccurrence secOcc);

    /**
     * Create a new simple chart row.
     * 
     * @return The new simple chart row
     */
    public ISimpleChartRow createSimpleChartRow();
    
    /**
     * Create a new trends chart row.
     * 
     * @return The new trends chart row
     */
    public ITrendsChartRow createTrendsChartRow();
   
    /**
     * Create a new simple chart item for the given entry, 
     * document occurrence and section occurrence, with the given options.
     * 
     * @param entry The entry.
     * @param docOcc The document occurrence.
     * @param secOcc The section occurrence.
     * @param options The options.
     * @return The new simple chart.
     */
    public ISimpleChartItem createSimpleChartItem(Entry entry, DocumentOccurrence docOcc, SectionOccurrence secOcc, String options);

    /**
     * Create a new simple chart item for the given entry, 
     * document occurrence and section occurrence, with the given options
     * and label options.
     * 
     * @param entry The entry.
     * @param docOcc The document occurrence.
     * @param secOcc The section occurrence.
     * @param options The options.
     * @param labelOptions The label options.
     * @return The new simple chart.
     */
    public ISimpleChartItem createSimpleChartItem(Entry entry, DocumentOccurrence docOcc, SectionOccurrence secOcc, String options, String labelOptions);

    /**
     * Create a new project summary chart with the given type and
     * title.
     * 
     * @param type The type of the chart.
     * @param title The title of the chart.
     * @return The new project summary chart.
     */
    public IProjectSummaryChart createProjectSummaryChart(String type, String title);
    
    /**
     * Create a new groups summary chart with the given type and
     * title.
     * 
     * @param type The type of the chart.
     * @param title The title of the chart.
     * @return The new groups summary chart.
     */
    public IGroupsSummaryChart createGroupsSummaryChart(String type, String title);
    
    /**
     * Create a new user summary chart with the given type and
     * title.
     * 
     * @param type The type of the chart.
     * @param title The title of the chart.
     * @return The new user summary chart.
     */
    public IUserSummaryChart createUserSummaryChart(String type, String title);
    
    /**
     * Create a new UKCRN summary chart with the given type and title
     * 
     * @param type
     * @param title
     * @return THe new UKCRN summary chart
     */
    public IUKCRNSummaryChart createUKCRNSummaryChart(String type, String title);
    
    /**
     * Create a new recruitment progress chart with the given type and
     * title.
     * 
     * @param type The type of the chart.
     * @param title The title of the chart.
     * @return The new user summary chart.
     */
    public IRecruitmentProgressChart createRecruitmentProgressChart(String type, String title);
    
    /**
     * Create a new trends chart with the given type and title.
     * 
     * @param type
     * @param title
     * @return The new trends chart
     */
    public ITrendsChart createTrendsChart(String type, String title);
    
    /**
     * Create a new trends chart with the given type and title.
     * 
     * @param type
     * @param title
     * @return The new trends chart
     */
    public ITrendsGanttChart createTrendsGanttChart(String type, String title);
    
    /**
     * Create a new receiving treatment chart with the given type and title.
     * @param type
     * @param title
     * @return new receivingTreatmentChart
     */
    public IReceivingTreatmentChart createReceivingTreatmentChart(String type, String title);
    
    /**
     * Create a new record status chart with the given type and title.
     * 
     * @param type
     * @param title
     * @return new recordStatusChart
     */
    public IRecordStatusChart createRecordStatusChart(String type, String title);
    
    /**
     * Create a new document status chart with the given type and title.
     * 
     * @param type
     * @param title
     * @return new documentStatusChart
     */
    public IDocumentStatusChart createDocumentStatusChart(String type, String title);

    /**
     * Create a new collection date chart with the given type and title.
     * 
     * @param type
     * @param title
     * @return new collectionDateChart
     */
    public ICollectionDateChart createCollectionDateChart(String type, String title);
    
    /**
     * Create a new standard code status chart with the given type and title.
     * 
     * @param type
     * @param title
     * @return new stdCodeStatusChart
     */
    public IStdCodeStatusChart createStdCodeStatusChart(String type, String title);

    /**
     * Create a new basic statistics chart with the given type and title.
     * 
     * @param type
     * @param title
     * @return new stdCodeStatusChart
     */
    public IBasicStatisticsChart createBasicStatisticsChart(String type, String title);
    
    /**
     * Create a new ESL chart item with the given ESL property name.
     * 
     * @param propertyName
     * @return new EslChartItem
     */
    public IEslChartItem createEslChartItem(String propertyName);
}
