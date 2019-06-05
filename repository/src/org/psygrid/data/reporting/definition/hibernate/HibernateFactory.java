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

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.psygrid.data.reporting.definition.Factory;
import org.psygrid.data.reporting.definition.IBasicStatisticsChart;
import org.psygrid.data.reporting.definition.ICollectionDateChart;
import org.psygrid.data.reporting.definition.IDocumentStatusChart;
import org.psygrid.data.reporting.definition.IEslChartItem;
import org.psygrid.data.reporting.definition.IGroupsSummaryChart;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IProjectSummaryChart;
import org.psygrid.data.reporting.definition.IReceivingTreatmentChart;
import org.psygrid.data.reporting.definition.IRecordChart;
import org.psygrid.data.reporting.definition.IRecordReport;
import org.psygrid.data.reporting.definition.IRecordStatusChart;
import org.psygrid.data.reporting.definition.IRecruitmentProgressChart;
import org.psygrid.data.reporting.definition.ISimpleChartItem;
import org.psygrid.data.reporting.definition.ISimpleChartRow;
import org.psygrid.data.reporting.definition.IStdCodeStatusChart;
import org.psygrid.data.reporting.definition.ITrendsChart;
import org.psygrid.data.reporting.definition.ITrendsChartRow;
import org.psygrid.data.reporting.definition.ITrendsGanttChart;
import org.psygrid.data.reporting.definition.ITrendsReport;
import org.psygrid.data.reporting.definition.IUKCRNSummaryChart;
import org.psygrid.data.reporting.definition.IUserSummaryChart;

/**
 * Implementation of report definition factory to create
 * objects in the org.psygrid.data.reporting.definition.hibernate
 * package. 
 * 
 * @author Rob Harper
 *
 */
public class HibernateFactory implements Factory {

    public IRecordReport createRecordReport(DataSet ds, String title) {
        return new RecordReport(ds, title);
    }

    public IManagementReport createManagementReport(DataSet ds, String title) {
        return new ManagementReport(ds, title);
    }

    public ITrendsReport createTrendsReport(DataSet ds, String title) {
    	return new TrendsReport(ds, title);
    }
    
    public IRecordChart createSimpleChart(String type, String title) {
        return new RecordChart(type, title);
    }

    public ISimpleChartRow createSimpleChartRow() {
        return new SimpleChartRow();
    }
    
    public ITrendsChartRow createTrendsChartRow() {
    	return new TrendsChartRow();
    }
    
    public ISimpleChartItem createSimpleChartItem(Entry entry, DocumentOccurrence docOcc, SectionOccurrence secOcc) {
        return new SimpleChartItem((Entry)entry, (DocumentOccurrence)docOcc, (SectionOccurrence)secOcc);
    }

    public ISimpleChartItem createSimpleChartItem(Entry entry, DocumentOccurrence docOcc, SectionOccurrence secOcc, String options) {
        return new SimpleChartItem((Entry)entry, (DocumentOccurrence)docOcc, (SectionOccurrence)secOcc, options);
    }

    public ISimpleChartItem createSimpleChartItem(Entry entry, DocumentOccurrence docOcc, SectionOccurrence secOcc, String options, String labelOptions) {
        return new SimpleChartItem((Entry)entry, (DocumentOccurrence)docOcc, (SectionOccurrence)secOcc, options, labelOptions);
    }

    public IGroupsSummaryChart createGroupsSummaryChart(String type, String title) {
        return new GroupsSummaryChart(type, title);
    }

    public IProjectSummaryChart createProjectSummaryChart(String type, String title) {
        return new ProjectSummaryChart(type, title);
    }

    public IUserSummaryChart createUserSummaryChart(String type, String title) {
        return new UserSummaryChart(type, title);
    }

    public IRecruitmentProgressChart createRecruitmentProgressChart(String type, String title) {
    	return new RecruitmentProgressChart(type, title);
    }
    
    public IUKCRNSummaryChart createUKCRNSummaryChart(String type, String title) {
    	return new UKCRNSummaryChart(type, title);
    }
    
    public ITrendsChart createTrendsChart(String type, String title) {
    	return new TrendsChart(type, title);
    }
    
    public ITrendsGanttChart createTrendsGanttChart(String type, String title) {
    	return new TrendsGanttChart(type, title);
    }
    
    public IReceivingTreatmentChart createReceivingTreatmentChart(String type, String title) {
    	return new ReceivingTreatmentChart(type,title);
    }
    
    public IRecordStatusChart createRecordStatusChart(String type, String title) {
    	return new RecordStatusChart(type, title);
    }
    
    public IDocumentStatusChart createDocumentStatusChart(String type, String title) {
    	return new DocumentStatusChart(type, title);
    }

    public ICollectionDateChart createCollectionDateChart(String type, String title) {
    	return new CollectionDateChart(type, title);
    }
    
    public IStdCodeStatusChart createStdCodeStatusChart(String type, String title) {
    	return new StdCodeStatusChart(type, title);
	}

	public IBasicStatisticsChart createBasicStatisticsChart(String type, String title) {
		return new BasicStatisticsChart(type, title);
	}
	
    public IEslChartItem createEslChartItem(String propertyName) {
        return new EslChartItem(propertyName);
    }


}
