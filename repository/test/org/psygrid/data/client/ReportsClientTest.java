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

package org.psygrid.data.client;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.psygrid.data.dao.DAOTestHelper;
import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IRecordChart;
import org.psygrid.data.reporting.definition.IRecordReport;
import org.psygrid.data.reporting.definition.ISimpleChartItem;
import org.psygrid.data.repository.dao.RepositoryDAO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ReportsClientTest extends TestCase {
    
    private RepositoryDAO dao;
    private org.psygrid.data.model.hibernate.Factory factory;
    private org.psygrid.data.reporting.definition.Factory reportFactory;
    
    protected ApplicationContext ctx = null;
    
    public ReportsClientTest() {
        String[] paths = {"applicationContext.xml"};
        ctx = new ClassPathXmlApplicationContext(paths);
    }
        
    protected void setUp() throws Exception {
        super.setUp();
        dao = (RepositoryDAO)ctx.getBean("repositoryDAOService");
        factory = (org.psygrid.data.model.hibernate.Factory) ctx.getBean("factory");
        reportFactory = (org.psygrid.data.reporting.definition.Factory) ctx.getBean("reportFactory");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        dao = null;
        factory = null;
    }
    
    public void testPanssReport(){
        try{
            
            /*
             * Create a dataset that contains a stripped down version of the
             * actual PANSS document
             */
            String name = "testPanssReport - "+(new Date()).toString();
            DataSet ds = factory.createDataset(name);
            //generate unique project code
            java.rmi.dgc.VMID guid = new java.rmi.dgc.VMID();
            String projectCode = DAOTestHelper.checkProjectCode(guid.toString());            
            ds.setProjectCode(projectCode);
            
            Group g1 = factory.createGroup("G1");
            ds.addGroup(g1);
            
            Document panss = factory.createDocument("PANSS",
                    "Positive and Negative Syndrome Scale for Schizophrenia (PANSS)");
    
            //positive scale section
            Section positiveScale = factory.createSection("Positive Scale", "Positive Scale");
            panss.addSection(positiveScale);
            positiveScale.setDisplayText("Positive Scale");
            SectionOccurrence positiveScaleOcc = factory.createSectionOccurrence("Positive Scale Occ");
            positiveScale.addOccurrence(positiveScaleOcc);
            
            String delText = "Delusions";
            String delLabel = "P1";
            NumericEntry delusions = factory.createNumericEntry("Delusions", delText);
            panss.addEntry(delusions);
            delusions.setSection(positiveScale);
            delusions.setLabel(delLabel);
        
            String cncDisText = "Conceptual disorganization";
            String cncDisLabel = "P2";
            NumericEntry conceptualDisorganization = factory.createNumericEntry("Conceptual disorganization", cncDisText);
            panss.addEntry(conceptualDisorganization);
            conceptualDisorganization.setSection(positiveScale);
            conceptualDisorganization.setLabel(cncDisLabel);
                            
            String subPosText = "Subtotal (positive syndrome)";
            DerivedEntry subtotalPositive = factory.createDerivedEntry("Positive subtotal", subPosText, EntryStatus.DISABLED);
            panss.addEntry(subtotalPositive);
            subtotalPositive.setSection(positiveScale);
            subtotalPositive.setDescription("Sum of P1 through P7");
            subtotalPositive.setFormula("d+cd");
            subtotalPositive.addVariable("d", delusions);
            subtotalPositive.addVariable("cd", conceptualDisorganization);
            
            
            //negative scale section
            Section negativeScale = factory.createSection("Negative Scale", "Negative Scale");
            panss.addSection(negativeScale);
            negativeScale.setDisplayText("Negative Scale");
            SectionOccurrence negativeScaleOcc = factory.createSectionOccurrence("Negative Scale Occurrence");
            negativeScale.addOccurrence(negativeScaleOcc);
            
            String blAffText = "Blunted affect";
            String blAffLabel = "N1";
            NumericEntry bluntedAffect = factory.createNumericEntry("Blunted affect", blAffText);
            panss.addEntry(bluntedAffect);
            bluntedAffect.setSection(negativeScale);
            bluntedAffect.setLabel(blAffLabel);
        
            String emWithText = "Emotional withdrawal";
            String emWithLabel = "N2";
            NumericEntry emotionalWithdrawl = factory.createNumericEntry("Emotional withdrawal", emWithText);
            panss.addEntry(emotionalWithdrawl);
            emotionalWithdrawl.setSection(negativeScale);
            emotionalWithdrawl.setLabel(emWithLabel);
        
            String subNegText = "Subtotal (negative syndrome)";
            DerivedEntry subtotalNegative = factory.createDerivedEntry("Negative subtotal", subNegText, EntryStatus.DISABLED);
            panss.addEntry(subtotalNegative);
            subtotalNegative.setSection(negativeScale);
            subtotalNegative.setDescription("sum of N1 through N7");
            subtotalNegative.setFormula("ba+ew");
            subtotalNegative.addVariable("ba", bluntedAffect);
            subtotalNegative.addVariable("ew", emotionalWithdrawl);
            
            String compIndText = "Composite Index";
            DerivedEntry compositeIndex = factory.createDerivedEntry("Composite Index", compIndText, EntryStatus.DISABLED);
            panss.addEntry(compositeIndex);
            compositeIndex.setSection(negativeScale);
            compositeIndex.setDescription("positive syndrome minus negative syndrome");
            compositeIndex.setFormula("ps-ns");
            compositeIndex.addVariable("ps", subtotalPositive);
            compositeIndex.addVariable("ns", subtotalNegative);
            
            
            //general scale section
            Section generalScale = factory.createSection("General scale", "General Psychopathology Scale");
            panss.addSection(generalScale);
            generalScale.setDisplayText("General Scale");
            SectionOccurrence generalScaleOcc = factory.createSectionOccurrence("General scale occurrence");
            generalScale.addOccurrence(generalScaleOcc);
            
            String somConcText = "Somatic concern";
            String somConcLabel = "G1";
            NumericEntry somaticConcern = factory.createNumericEntry("Somatic concern", somConcText);
            panss.addEntry(somaticConcern);
            somaticConcern.setSection(generalScale);
            somaticConcern.setLabel(somConcLabel);
        
            String anxText = "Anxiety";
            String anxLabel = "G2";
            NumericEntry anxiety = factory.createNumericEntry("Anxiety", anxText);
            panss.addEntry(anxiety);
            anxiety.setSection(generalScale);
            anxiety.setLabel(anxLabel);
        
            String subGenText = "Subtotal (general psychopathology)";
            DerivedEntry subtotalGeneral = factory.createDerivedEntry("General subtotal", subGenText, EntryStatus.DISABLED);
            panss.addEntry(subtotalGeneral);
            subtotalGeneral.setSection(generalScale);
            subtotalGeneral.setDescription("sum of G1 through G16");
            subtotalGeneral.setFormula("sc+a");
            subtotalGeneral.addVariable("sc", somaticConcern);
            subtotalGeneral.addVariable("a", anxiety);

            String totText = "Total PANSS score";
            DerivedEntry total = factory.createDerivedEntry("Total score", totText, EntryStatus.DISABLED);
            panss.addEntry(total);
            total.setSection(generalScale);
            total.setDescription("this is the sum of the 3 subtotals");
            total.setFormula("p+n+g");
            total.addVariable("p", subtotalPositive);
            total.addVariable("n", subtotalNegative);
            total.addVariable("g", subtotalGeneral);
            
            DocumentOccurrence panssOcc = factory.createDocumentOccurrence("Panss");
            panss.addOccurrence(panssOcc);
            
            ds.addDocument(panss);
            
            DocumentGroup group = factory.createDocumentGroup("Group");
            ds.addDocumentGroup(group);
            panssOcc.setDocumentGroup(group);
            
            Long dsId = dao.saveDataSet(ds.toDTO());
            dao.publishDataSet(dsId);
            ds = dao.getDataSet(dsId).toHibernate();
            
            panss = ds.getDocument(0);
            panssOcc = ds.getDocument(0).getOccurrence(0);
            positiveScale = panss.getSection(0);
            positiveScaleOcc = positiveScale.getOccurrence(0);
            delusions = (NumericEntry)panss.getEntry(0);
            conceptualDisorganization = (NumericEntry)panss.getEntry(1);
            subtotalPositive = (DerivedEntry)panss.getEntry(2);
            negativeScale = panss.getSection(1);
            negativeScaleOcc = negativeScale.getOccurrence(0);
            bluntedAffect = (NumericEntry)panss.getEntry(3);
            emotionalWithdrawl = (NumericEntry)panss.getEntry(4);
            subtotalNegative = (DerivedEntry)panss.getEntry(5);
            compositeIndex = (DerivedEntry)panss.getEntry(6);
            generalScale = panss.getSection(2);
            generalScaleOcc = generalScale.getOccurrence(0);
            somaticConcern = (NumericEntry)panss.getEntry(7);
            anxiety = (NumericEntry)panss.getEntry(8);
            subtotalGeneral = (DerivedEntry)panss.getEntry(9);
            total = (DerivedEntry)panss.getEntry(10);
            
            /*
             * Add the report
             */
            String repTitle = "PANSS Report";
            IRecordReport panssRep = reportFactory.createRecordReport(ds, repTitle);
            
            String summaryTitle = "Summary";
            String summaryType = org.psygrid.data.reporting.Chart.CHART_TABLE;
            IRecordChart panssSummary = reportFactory.createSimpleChart(summaryType, summaryTitle);
            panssSummary.addItem(reportFactory.createSimpleChartItem(subtotalPositive, panssOcc, positiveScaleOcc));
            panssSummary.addItem(reportFactory.createSimpleChartItem(subtotalNegative, panssOcc, negativeScaleOcc));
            panssSummary.addItem(reportFactory.createSimpleChartItem(subtotalGeneral, panssOcc, generalScaleOcc));
            panssSummary.addItem(reportFactory.createSimpleChartItem(compositeIndex, panssOcc, negativeScaleOcc));
            panssSummary.addItem(reportFactory.createSimpleChartItem(total, panssOcc, generalScaleOcc));
            panssRep.addChart(panssSummary);
            
            String scoresTitle = "PANSS Scores";
            String scoresType = org.psygrid.data.reporting.Chart.CHART_BAR;
            IRecordChart panssScores = reportFactory.createSimpleChart(scoresType, scoresTitle);
            panssScores.addItem(reportFactory.createSimpleChartItem(delusions, panssOcc, positiveScaleOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY));
            panssScores.addItem(reportFactory.createSimpleChartItem(conceptualDisorganization, panssOcc, positiveScaleOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY));
            panssScores.addItem(reportFactory.createSimpleChartItem(bluntedAffect, panssOcc, negativeScaleOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY));
            panssScores.addItem(reportFactory.createSimpleChartItem(emotionalWithdrawl, panssOcc, negativeScaleOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY));
            panssScores.addItem(reportFactory.createSimpleChartItem(somaticConcern, panssOcc, generalScaleOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY));
            panssScores.addItem(reportFactory.createSimpleChartItem(anxiety, panssOcc, generalScaleOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY));
            panssRep.addChart(panssScores);
            
            ReportsClient client = new ReportsClient();
            Long repId = client.saveReport(panssRep, null);
            
            List<org.psygrid.data.reporting.definition.IReport> reports = 
                client.getReportsByDataSet(dsId, null);
            
            assertEquals("List of reports for the dataset contains the wrong number of items", 1, reports.size());
            assertEquals("Report at index 0 has the wrong title", repTitle, reports.get(0).getTitle());
            
            Identifier[] ids = DAOTestHelper.getIdentifiers(dao, projectCode, dsId, 1, "G1");
            
            Record r = ds.generateInstance();
            r.setIdentifier(ids[0]);
            
            DocumentInstance di = panss.generateInstance(panssOcc);
            r.addDocumentInstance(di);
            
            BasicResponse delResp = delusions.generateInstance(positiveScaleOcc);
            INumericValue delVal = delusions.generateValue();
            delVal.setValue(new Double(2.0));
            delResp.setValue(delVal);
            di.addResponse(delResp);
            
            BasicResponse conDisResp = conceptualDisorganization.generateInstance(positiveScaleOcc);
            INumericValue conDisVal = conceptualDisorganization.generateValue();
            conDisVal.setValue(new Double(3.0));
            conDisResp.setValue(conDisVal);
            di.addResponse(conDisResp);
            
            BasicResponse totPosResp = subtotalPositive.generateInstance(positiveScaleOcc);
            INumericValue totPosVal = subtotalPositive.generateValue();
            totPosVal.setValue(new Double(5.0));
            totPosResp.setValue(totPosVal);
            di.addResponse(totPosResp);
            
            BasicResponse bluntResp = bluntedAffect.generateInstance(negativeScaleOcc);
            INumericValue bluntVal = bluntedAffect.generateValue();
            bluntVal.setValue(new Double(5.0));
            bluntResp.setValue(bluntVal);
            di.addResponse(bluntResp);
            
            BasicResponse emWithResp = emotionalWithdrawl.generateInstance(negativeScaleOcc);
            INumericValue emWithVal = emotionalWithdrawl.generateValue();
            emWithVal.setValue(new Double(1.0));
            emWithResp.setValue(emWithVal);
            di.addResponse(emWithResp);
            
            BasicResponse totNegResp = subtotalNegative.generateInstance(negativeScaleOcc);
            INumericValue totNegVal = subtotalNegative.generateValue();
            totNegVal.setValue(new Double(6.0));
            totNegResp.setValue(totNegVal);
            di.addResponse(totNegResp);
            
            BasicResponse compIndResp = compositeIndex.generateInstance(negativeScaleOcc);
            INumericValue compIndVal = compositeIndex.generateValue();
            compIndVal.setValue(new Double(-1.0));
            compIndResp.setValue(compIndVal);
            di.addResponse(compIndResp);
            
            
            BasicResponse somConResp = somaticConcern.generateInstance(generalScaleOcc);
            INumericValue somConVal = somaticConcern.generateValue();
            somConVal.setValue(new Double(1.0));
            somConResp.setValue(somConVal);
            di.addResponse(somConResp);
            
            BasicResponse anxResp = anxiety.generateInstance(generalScaleOcc);
            INumericValue anxVal = anxiety.generateValue();
            anxVal.setValue(new Double(2.0));
            anxResp.setValue(anxVal);
            di.addResponse(anxResp);
            
            BasicResponse subGenResp = subtotalGeneral.generateInstance(generalScaleOcc);
            INumericValue subGenVal = subtotalGeneral.generateValue();
            subGenVal.setValue(new Double(3.0));
            subGenResp.setValue(subGenVal);
            di.addResponse(subGenResp);
            
            BasicResponse totalResp = total.generateInstance(generalScaleOcc);
            INumericValue totalVal = total.generateValue();
            totalVal.setValue(new Double(14.0));
            totalResp.setValue(totalVal);
            di.addResponse(totalResp);
            
            Long rId = dao.saveRecord(r.toDTO(), true, null, "NoUser");
            
            /*
             * generate the report
             */
            String requestor = "NoUser";
            org.psygrid.data.reporting.RecordReport report = client.generateReport(repId, rId, null);
            
            assertNotNull("Null report", report);
            assertEquals("Incorrect report title", repTitle, report.getTitle());
            assertEquals("Incorrect report subject", ids[0].getIdentifier(), report.getSubject());
            assertEquals("Incorrect report requestor", requestor, report.getRequestor());
            
            assertEquals("Report has the wrong number of charts", 2, report.getCharts().length);
            
            org.psygrid.data.reporting.Chart chart1 = report.getCharts()[0];
            assertNotNull("Summary chart is null", chart1);
            assertEquals("Summary chart has the wrong title", summaryTitle, chart1.getTitle());
            assertEquals("Summary chart has the wrong type", summaryType, chart1.getTypes()[0]);
            assertEquals("Summary chart has the wrong number of rows", 5, chart1.getRows().length);
            assertEquals("Summary chart row 1 has the wrong label", subPosText, chart1.getRows()[0].getLabel());
            assertEquals("Summary chart row 1 has the wrong number of series", 1, chart1.getRows()[0].getSeries().length);
            assertEquals("Summary chart row 1 has the wrong value", totPosVal.getValueAsString(), chart1.getRows()[0].getSeries()[0].getPoints()[0].getValue());
            assertEquals("Summary chart row 2 has the wrong label", subNegText, chart1.getRows()[1].getLabel());
            assertEquals("Summary chart row 2 has the wrong number of series", 1, chart1.getRows()[1].getSeries().length);
            assertEquals("Summary chart row 2 has the wrong value", totNegVal.getValueAsString(), chart1.getRows()[1].getSeries()[0].getPoints()[0].getValue());
            assertEquals("Summary chart row 3 has the wrong label", subGenText, chart1.getRows()[2].getLabel());
            assertEquals("Summary chart row 3 has the wrong number of series", 1, chart1.getRows()[2].getSeries().length);
            assertEquals("Summary chart row 3 has the wrong value", subGenVal.getValueAsString(), chart1.getRows()[2].getSeries()[0].getPoints()[0].getValue());
            assertEquals("Summary chart row 4 has the wrong label", compIndText, chart1.getRows()[3].getLabel());
            assertEquals("Summary chart row 4 has the wrong number of series", 1, chart1.getRows()[3].getSeries().length);
            assertEquals("Summary chart row 4 has the wrong value", compIndVal.getValueAsString(), chart1.getRows()[3].getSeries()[0].getPoints()[0].getValue());
            assertEquals("Summary chart row 5 has the wrong label", totText, chart1.getRows()[4].getLabel());
            assertEquals("Summary chart row 5 has the wrong number of series", 1, chart1.getRows()[4].getSeries().length);
            assertEquals("Summary chart row 5 has the wrong value", totalVal.getValueAsString(), chart1.getRows()[4].getSeries()[0].getPoints()[0].getValue());
            
            org.psygrid.data.reporting.Chart chart2 = report.getCharts()[1];
            assertNotNull("Scores chart is null", chart2);
            assertEquals("Scores chart has the wrong title", scoresTitle, chart2.getTitle());
            assertEquals("Scores chart has the wrong type", scoresType, chart2.getTypes()[0]);
            assertEquals("Scores chart has the wrong number of rows", 6, chart2.getRows().length);
            assertEquals("Scores chart row 1 has the wrong label", delLabel, chart2.getRows()[0].getLabel());
            assertEquals("Scores chart row 1 has the wrong number of series", 1, chart2.getRows()[0].getSeries().length);
            assertEquals("Scores chart row 1 has the wrong value", delVal.getValueAsString(), chart2.getRows()[0].getSeries()[0].getPoints()[0].getValue());
            assertEquals("Scores chart row 2 has the wrong label", cncDisLabel, chart2.getRows()[1].getLabel());
            assertEquals("Scores chart row 2 has the wrong number of series", 1, chart2.getRows()[1].getSeries().length);
            assertEquals("Scores chart row 2 has the wrong value", conDisVal.getValueAsString(), chart2.getRows()[1].getSeries()[0].getPoints()[0].getValue());
            assertEquals("Scores chart row 3 has the wrong label", blAffLabel, chart2.getRows()[2].getLabel());
            assertEquals("Scores chart row 3 has the wrong number of series", 1, chart2.getRows()[2].getSeries().length);
            assertEquals("Scores chart row 3 has the wrong value", bluntVal.getValueAsString(), chart2.getRows()[2].getSeries()[0].getPoints()[0].getValue());
            assertEquals("Scores chart row 4 has the wrong label", emWithLabel, chart2.getRows()[3].getLabel());
            assertEquals("Scores chart row 4 has the wrong number of series", 1, chart2.getRows()[3].getSeries().length);
            assertEquals("Scores chart row 4 has the wrong value", emWithVal.getValueAsString(), chart2.getRows()[3].getSeries()[0].getPoints()[0].getValue());
            assertEquals("Scores chart row 5 has the wrong label", somConcLabel, chart2.getRows()[4].getLabel());
            assertEquals("Scores chart row 5 has the wrong number of series", 1, chart2.getRows()[4].getSeries().length);
            assertEquals("Scores chart row 5 has the wrong value", somConVal.getValueAsString(), chart2.getRows()[4].getSeries()[0].getPoints()[0].getValue());
            assertEquals("Scores chart row 6 has the wrong label", anxLabel, chart2.getRows()[5].getLabel());
            assertEquals("Scores chart row 6 has the wrong number of series", 1, chart2.getRows()[5].getSeries().length);
            assertEquals("Scores chart row 6 has the wrong value", anxVal.getValueAsString(), chart2.getRows()[5].getSeries()[0].getPoints()[0].getValue());
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex);
        }
    }
    
}
