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

package org.psygrid.neden;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;

import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.Chart;
import org.psygrid.data.reporting.definition.Factory;
import org.psygrid.data.reporting.definition.IBasicStatisticsChart;
import org.psygrid.data.reporting.definition.ICollectionDateChart;
import org.psygrid.data.reporting.definition.IDocumentStatusChart;
import org.psygrid.data.reporting.definition.IGroupsSummaryChart;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IProjectSummaryChart;
import org.psygrid.data.reporting.definition.IRecordChart;
import org.psygrid.data.reporting.definition.IRecordReport;
import org.psygrid.data.reporting.definition.IRecordStatusChart;
import org.psygrid.data.reporting.definition.IRecruitmentProgressChart;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.reporting.definition.ISimpleChartItem;
import org.psygrid.data.reporting.definition.ISimpleChartRow;
import org.psygrid.data.reporting.definition.IStdCodeStatusChart;
import org.psygrid.data.reporting.definition.ITrendsChart;
import org.psygrid.data.reporting.definition.ITrendsChartRow;
import org.psygrid.data.reporting.definition.ITrendsGanttChart;
import org.psygrid.data.reporting.definition.ITrendsReport;
import org.psygrid.data.reporting.definition.IUKCRNSummaryChart;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.reporting.definition.ReportFrequency;
import org.psygrid.data.reporting.definition.hibernate.HibernateFactory;
import org.psygrid.security.RBACAction;

public class Reports {

	public static Factory factory = new HibernateFactory();

	public static IReport panssReportBaseline(DataSet ds) throws Exception {

		Document panss = ds.getDocument(1);
		//check that we have the right document!
		if ( !panss.getName().equals("PANSS") ){
			throw new Exception("This isn't the PANSS document!!!");
		}
		DocumentOccurrence panssBaseline = panss.getOccurrence(0);

		return panssReport(ds, panssBaseline);
	}

	public static IReport panssReport6Months(DataSet ds) throws Exception {

		Document panss = ds.getDocument(1);
		//check that we have the right document!
		if ( !panss.getName().equals("PANSS") ){
			throw new Exception("This isn't the PANSS document!!!");
		}
		DocumentOccurrence panss6Months = panss.getOccurrence(1);

		return panssReport(ds, panss6Months);
	}

	public static IReport panssReport12Months(DataSet ds) throws Exception {

		Document panss = ds.getDocument(1);
		//check that we have the right document!
		if ( !panss.getName().equals("PANSS") ){
			throw new Exception("This isn't the PANSS document!!!");
		}
		DocumentOccurrence panss12Months = panss.getOccurrence(2);

		return panssReport(ds, panss12Months);
	}

	private static IReport panssReport(DataSet ds, DocumentOccurrence docOcc) throws Exception {

		IRecordReport panssReport =
			factory.createRecordReport(ds, "PANSS - "+docOcc.getDisplayText());

		Document panss = ds.getDocument(1);
		//check that we have the right document!
		if ( !panss.getName().equals("PANSS") ){
			throw new Exception("This isn't the PANSS document!!!");
		}
		Section posSec = panss.getSection(1);
		SectionOccurrence posSecOcc = posSec.getOccurrence(0);
		Section negSec = panss.getSection(2);
		SectionOccurrence negSecOcc = negSec.getOccurrence(0);
		Section genSec = panss.getSection(3);
		SectionOccurrence genSecOcc = genSec.getOccurrence(0);

		//Summary chart
		IRecordChart summary =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
					"Summary");
		panssReport.addChart(summary);

		ISimpleChartItem posTotal = factory.createSimpleChartItem(panss.getEntry(8), docOcc, posSecOcc);
		summary.addItem(posTotal);
		ISimpleChartItem negTotal = factory.createSimpleChartItem(panss.getEntry(16), docOcc, negSecOcc);
		summary.addItem(negTotal);
		ISimpleChartItem compIndex = factory.createSimpleChartItem(panss.getEntry(17), docOcc, negSecOcc);
		summary.addItem(compIndex);
		ISimpleChartItem genTotal = factory.createSimpleChartItem(panss.getEntry(34), docOcc, genSecOcc);
		summary.addItem(genTotal);
		ISimpleChartItem total = factory.createSimpleChartItem(panss.getEntry(35), docOcc, genSecOcc);
		summary.addItem(total);

		//Scores chart
		IRecordChart scores =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_BAR,
			"Scores");
		panssReport.addChart(scores);

		ISimpleChartItem p1 = factory.createSimpleChartItem(panss.getEntry(1), docOcc, posSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(p1);
		ISimpleChartItem p2 = factory.createSimpleChartItem(panss.getEntry(2), docOcc, posSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(p2);
		ISimpleChartItem p3 = factory.createSimpleChartItem(panss.getEntry(3), docOcc, posSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(p3);
		ISimpleChartItem p4 = factory.createSimpleChartItem(panss.getEntry(4), docOcc, posSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(p4);
		ISimpleChartItem p5 = factory.createSimpleChartItem(panss.getEntry(5), docOcc, posSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(p5);
		ISimpleChartItem p6 = factory.createSimpleChartItem(panss.getEntry(6), docOcc, posSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(p6);
		ISimpleChartItem p7 = factory.createSimpleChartItem(panss.getEntry(7), docOcc, posSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(p7);

		ISimpleChartItem n1 = factory.createSimpleChartItem(panss.getEntry(9), docOcc, negSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(n1);
		ISimpleChartItem n2 = factory.createSimpleChartItem(panss.getEntry(10), docOcc, negSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(n2);
		ISimpleChartItem n3 = factory.createSimpleChartItem(panss.getEntry(11), docOcc, negSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(n3);
		ISimpleChartItem n4 = factory.createSimpleChartItem(panss.getEntry(12), docOcc, negSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(n4);
		ISimpleChartItem n5 = factory.createSimpleChartItem(panss.getEntry(13), docOcc, negSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(n5);
		ISimpleChartItem n6 = factory.createSimpleChartItem(panss.getEntry(14), docOcc, negSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(n6);
		ISimpleChartItem n7 = factory.createSimpleChartItem(panss.getEntry(15), docOcc, negSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(n7);

		ISimpleChartItem g1 = factory.createSimpleChartItem(panss.getEntry(18), docOcc, genSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(g1);
		ISimpleChartItem g2 = factory.createSimpleChartItem(panss.getEntry(19), docOcc, genSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(g2);
		ISimpleChartItem g3 = factory.createSimpleChartItem(panss.getEntry(20), docOcc, genSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(g3);
		ISimpleChartItem g4 = factory.createSimpleChartItem(panss.getEntry(21), docOcc, genSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(g4);
		ISimpleChartItem g5 = factory.createSimpleChartItem(panss.getEntry(22), docOcc, genSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(g5);
		ISimpleChartItem g6 = factory.createSimpleChartItem(panss.getEntry(23), docOcc, genSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(g6);
		ISimpleChartItem g7 = factory.createSimpleChartItem(panss.getEntry(24), docOcc, genSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(g7);
		ISimpleChartItem g8 = factory.createSimpleChartItem(panss.getEntry(25), docOcc, genSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(g8);
		ISimpleChartItem g9 = factory.createSimpleChartItem(panss.getEntry(26), docOcc, genSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(g9);
		ISimpleChartItem g10 = factory.createSimpleChartItem(panss.getEntry(27), docOcc, genSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(g10);
		ISimpleChartItem g11 = factory.createSimpleChartItem(panss.getEntry(28), docOcc, genSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(g11);
		ISimpleChartItem g12 = factory.createSimpleChartItem(panss.getEntry(29), docOcc, genSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(g12);
		ISimpleChartItem g13 = factory.createSimpleChartItem(panss.getEntry(30), docOcc, genSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(g13);
		ISimpleChartItem g14 = factory.createSimpleChartItem(panss.getEntry(31), docOcc, genSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(g14);
		ISimpleChartItem g15 = factory.createSimpleChartItem(panss.getEntry(32), docOcc, genSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(g15);
		ISimpleChartItem g16 = factory.createSimpleChartItem(panss.getEntry(33), docOcc, genSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(g16);

		return panssReport;
	}

	public static IReport youngManiaBaseline(DataSet ds) throws Exception {
		Document youngMania = ds.getDocument(2);
		//check that we have the right document!
		if ( !youngMania.getName().equals("Young Mania") ){
			throw new Exception("This isn't the Young Mania document!!!");
		}
		DocumentOccurrence ymBaseline = youngMania.getOccurrence(0);

		return youngManiaReport(ds, ymBaseline);
	}

	public static IReport youngMania6Months(DataSet ds) throws Exception {
		Document youngMania = ds.getDocument(2);
		//check that we have the right document!
		if ( !youngMania.getName().equals("Young Mania") ){
			throw new Exception("This isn't the Young Mania document!!!");
		}
		DocumentOccurrence ym6Months = youngMania.getOccurrence(1);

		return youngManiaReport(ds, ym6Months);
	}

	public static IReport youngMania12Months(DataSet ds) throws Exception {
		Document youngMania = ds.getDocument(2);
		//check that we have the right document!
		if ( !youngMania.getName().equals("Young Mania") ){
			throw new Exception("This isn't the Young Mania document!!!");
		}
		DocumentOccurrence ym12Months = youngMania.getOccurrence(2);

		return youngManiaReport(ds, ym12Months);
	}

	private static IReport youngManiaReport(DataSet ds, DocumentOccurrence docOcc) throws Exception {

		IRecordReport ymReport =
			factory.createRecordReport(ds, "Young Mania - "+docOcc.getDisplayText());

		Document youngMania = ds.getDocument(2);
		//check that we have the right document!
		if ( !youngMania.getName().equals("Young Mania") ){
			throw new Exception("This isn't the Young Mania document!!!");
		}
		Section mainSec = youngMania.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		//Summary chart
		IRecordChart summary =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
					"Summary");
		ymReport.addChart(summary);

		ISimpleChartItem total = factory.createSimpleChartItem(youngMania.getEntry(12), docOcc, mainSecOcc);
		summary.addItem(total);

		//Scores chart
		IRecordChart scores =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_BAR,
					"Scores");
		ymReport.addChart(scores);

		ISimpleChartItem s1 = factory.createSimpleChartItem(youngMania.getEntry(1), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		scores.addItem(s1);
		ISimpleChartItem s2 = factory.createSimpleChartItem(youngMania.getEntry(2), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		scores.addItem(s2);
		ISimpleChartItem s3 = factory.createSimpleChartItem(youngMania.getEntry(3), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		scores.addItem(s3);
		ISimpleChartItem s4 = factory.createSimpleChartItem(youngMania.getEntry(4), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		scores.addItem(s4);
		ISimpleChartItem s5 = factory.createSimpleChartItem(youngMania.getEntry(5), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		scores.addItem(s5);
		ISimpleChartItem s6 = factory.createSimpleChartItem(youngMania.getEntry(6), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		scores.addItem(s6);
		ISimpleChartItem s7 = factory.createSimpleChartItem(youngMania.getEntry(7), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		scores.addItem(s7);
		ISimpleChartItem s8 = factory.createSimpleChartItem(youngMania.getEntry(8), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		scores.addItem(s8);
		ISimpleChartItem s9 = factory.createSimpleChartItem(youngMania.getEntry(9), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		scores.addItem(s9);
		ISimpleChartItem s10 = factory.createSimpleChartItem(youngMania.getEntry(10), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		scores.addItem(s10);
		ISimpleChartItem s11 = factory.createSimpleChartItem(youngMania.getEntry(11), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		scores.addItem(s11);

		return ymReport;
	}

	public static IReport gafReportBaseline(DataSet ds) throws Exception {

		Document gaf = ds.getDocument(7);
		//check that we have the right document!
		if ( !gaf.getName().equals("GAF Data Entry Sheet") ){
			throw new Exception("This isn't the GAF document!!!");
		}
		DocumentOccurrence gafBaseline = gaf.getOccurrence(0);

		return gafReport(ds, gafBaseline);
	}

	public static IReport gafReport12Months(DataSet ds) throws Exception {

		Document gaf = ds.getDocument(7);
		//check that we have the right document!
		if ( !gaf.getName().equals("GAF Data Entry Sheet") ){
			throw new Exception("This isn't the GAF document!!!");
		}
		DocumentOccurrence gaf12Months = gaf.getOccurrence(1);

		return gafReport(ds, gaf12Months);
	}

	private static IReport gafReport(DataSet ds, DocumentOccurrence docOcc) throws Exception {

		IRecordReport gafReport =
			factory.createRecordReport(ds, "GAF - "+docOcc.getDisplayText());

		Document gaf = ds.getDocument(7);
		//check that we have the right document!
		if ( !gaf.getName().equals("GAF Data Entry Sheet") ){
			throw new Exception("This isn't the GAF document!!!");
		}
		Section mainSec = gaf.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		//Summary chart
		IRecordChart summary =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
					"Summary");
		gafReport.addChart(summary);

		ISimpleChartItem s1 = factory.createSimpleChartItem(gaf.getEntry(1), docOcc, mainSecOcc);
		summary.addItem(s1);
		ISimpleChartItem s2 = factory.createSimpleChartItem(gaf.getEntry(2), docOcc, mainSecOcc);
		summary.addItem(s2);
		ISimpleChartItem s3 = factory.createSimpleChartItem(gaf.getEntry(3), docOcc, mainSecOcc);
		summary.addItem(s3);

		return gafReport;
	}

	public static IReport drugCheckReportBaseline(DataSet ds) throws Exception {

		Document drugCheck = ds.getDocument(6);
		//check that we have the right document!
		if ( !"Drug Check".equals(drugCheck.getName().trim()) ){
			throw new Exception("This isn't the Drug Check document, it's "+drugCheck.getName()+"!!!");
		}
		DocumentOccurrence drugCheckBaseline = drugCheck.getOccurrence(0);

		return drugCheckReport(ds, drugCheckBaseline);
	}

	public static IReport drugCheckReport12Months(DataSet ds) throws Exception {

		Document drugCheck = ds.getDocument(6);
		//check that we have the right document!
		if ( !"Drug Check".equals(drugCheck.getName().trim()) ){
			throw new Exception("This isn't the Drug Check document, it's "+drugCheck.getName()+"!!!");
		}
		DocumentOccurrence drugCheck12Months = drugCheck.getOccurrence(1);

		return drugCheckReport(ds, drugCheck12Months);
	}

	private static IReport drugCheckReport(DataSet ds, DocumentOccurrence docOcc) throws Exception {

		IRecordReport drugCheckReport =
			factory.createRecordReport(ds, "Drug Check - "+docOcc.getDisplayText());

		Document drugCheck = ds.getDocument(6);
		//check that we have the right document!
		if ( !"Drug Check".equals(drugCheck.getName().trim()) ){
			throw new Exception("This isn't the Drug Check document, it's "+drugCheck.getName()+"!!!");
		}

		Section generalSec = drugCheck.getSection(0);
		SectionOccurrence generalSecOcc = generalSec.getOccurrence(0);
		Section probListSec = drugCheck.getSection(1);
		SectionOccurrence probListSecOcc = probListSec.getOccurrence(0);
		Section sdsSec = drugCheck.getSection(2);
		SectionOccurrence sdsSecOcc = sdsSec.getOccurrence(0);
		Section readySec = drugCheck.getSection(3);
		SectionOccurrence readySecOcc = readySec.getOccurrence(0);
		Section confidenceSec = drugCheck.getSection(4);
		SectionOccurrence confidenceSecOcc = confidenceSec.getOccurrence(0);
		Section summarySec = drugCheck.getSection(5);
		SectionOccurrence summarySecOcc = summarySec.getOccurrence(0);

		//Summary chart
		IRecordChart summary =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
			"Summary");
		drugCheckReport.addChart(summary);

		for ( int i=56; i<=60; i++ ){
			summary.addItem(factory.createSimpleChartItem(drugCheck.getEntry(i), docOcc, summarySecOcc));
		}

		//Drug use chart
		//TODO add otherDrugs composite
		IRecordChart drugUse =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
					"Drug Use");
		drugCheckReport.addChart(drugUse);

		for ( int i=1; i<=29; i++ ){
			drugUse.addItem(factory.createSimpleChartItem(drugCheck.getEntry(i), docOcc, generalSecOcc));
		}

		//Problem list chart
		IRecordChart problemList =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
					"Problem List");
		drugCheckReport.addChart(problemList);

		for ( int i=32; i<=40; i++ ){
			problemList.addItem(factory.createSimpleChartItem(drugCheck.getEntry(i), docOcc, probListSecOcc));
		}
		//TODO risky or outrageous behaviour composite
		for ( int i=42; i<=47; i++ ){
			problemList.addItem(factory.createSimpleChartItem(drugCheck.getEntry(i), docOcc, probListSecOcc));
		}

		//SDS chart
		IRecordChart sds =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
					"Severity of Dependence Scale");
		drugCheckReport.addChart(sds);

		for ( int i=48; i<=53; i++ ){
			sds.addItem(factory.createSimpleChartItem(drugCheck.getEntry(i), docOcc, sdsSecOcc));
		}

		//Readiness to Change chart
		IRecordChart readiness =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
					"Readiness to Change");
		drugCheckReport.addChart(readiness);

		readiness.addItem(factory.createSimpleChartItem(drugCheck.getEntry(54), docOcc, readySecOcc));

		//Confidence to Change chart
		IRecordChart confidence =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
					"Confidence to Change");
		drugCheckReport.addChart(confidence);

		confidence.addItem(factory.createSimpleChartItem(drugCheck.getEntry(55), docOcc, confidenceSecOcc));

		return drugCheckReport;
	}

	public static IReport cpmMgmtReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National EDEN - Clinical Project Manager Report");
		report.setEmailAction(RBACAction.ACTION_DR_CRM_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(true);
		report.setTemplate(false);

		IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);

		IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
		chrt1.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		report.addChart(chrt1);

		IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Heart of Birmingham - West EIS");
		chrt2.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt2.addGroup(ds.getGroup(0));
		report.addChart(chrt2);

		IGroupsSummaryChart chrt3 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Heart of Birmingham - East EIS");
		chrt3.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt3.addGroup(ds.getGroup(1));
		report.addChart(chrt3);

		IGroupsSummaryChart chrt4 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "East PCT Birmingham");
		chrt4.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt4.addGroup(ds.getGroup(2));
		report.addChart(chrt4);

		IGroupsSummaryChart chrt5 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Lancashire");
		chrt5.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt5.addGroup(ds.getGroup(3));
		chrt5.addGroup(ds.getGroup(4));
		chrt5.addGroup(ds.getGroup(10));
		report.addChart(chrt5);

		IGroupsSummaryChart chrt6 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Norfolk");
		chrt6.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt6.addGroup(ds.getGroup(5));
		report.addChart(chrt6);

		IGroupsSummaryChart chrt7 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Cambridge CAMEO");
		chrt7.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt7.addGroup(ds.getGroup(6));
		report.addChart(chrt7);

		IGroupsSummaryChart chrt8 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Cornwall");
		chrt8.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt8.addGroup(ds.getGroup(7));
		chrt8.addGroup(ds.getGroup(8));
		report.addChart(chrt8);

		IGroupsSummaryChart chrt9 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Birmingham South");
		chrt9.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt9.addGroup(ds.getGroup(9));
		report.addChart(chrt9);

		IGroupsSummaryChart chrt10 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Kings Lynn");
		chrt10.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt10.addGroup(ds.getGroup(11));
		report.addChart(chrt10);

		IGroupsSummaryChart chrt11 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Solihull");
		chrt11.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt11.addGroup(ds.getGroup(12));
		report.addChart(chrt11);

		IGroupsSummaryChart chrt12 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Cheshire & Wirral");
		chrt12.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt12.addGroup(ds.getGroup(13));
		report.addChart(chrt12);

		IGroupsSummaryChart chrt13 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Huntingdon");
		chrt13.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt13.addGroup(ds.getGroup(14));
		report.addChart(chrt13);

		return report;
	}

	public static IReport rmMgmtReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National EDEN - Recruitment Manager Report");
		report.setEmailAction(RBACAction.ACTION_DR_RECRUITMENT_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(true);
		report.setTemplate(false);

		IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);

		IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
		chrt1.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		report.addChart(chrt1);

		IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Heart of Birmingham - West EIS");
		chrt2.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt2.addGroup(ds.getGroup(0));
		report.addChart(chrt2);

		IGroupsSummaryChart chrt3 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Heart of Birmingham - East EIS");
		chrt3.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt3.addGroup(ds.getGroup(1));
		report.addChart(chrt3);

		IGroupsSummaryChart chrt4 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "East PCT Birmingham");
		chrt4.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt4.addGroup(ds.getGroup(2));
		report.addChart(chrt4);

		IGroupsSummaryChart chrt5 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Lancashire");
		chrt5.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt5.addGroup(ds.getGroup(3));
		chrt5.addGroup(ds.getGroup(4));
		chrt5.addGroup(ds.getGroup(10));
		report.addChart(chrt5);

		IGroupsSummaryChart chrt6 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Norfolk");
		chrt6.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt6.addGroup(ds.getGroup(5));
		report.addChart(chrt6);

		IGroupsSummaryChart chrt7 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Cambridge CAMEO");
		chrt7.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt7.addGroup(ds.getGroup(6));
		report.addChart(chrt7);

		IGroupsSummaryChart chrt8 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Cornwall");
		chrt8.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt8.addGroup(ds.getGroup(7));
		chrt8.addGroup(ds.getGroup(8));
		report.addChart(chrt8);

		IGroupsSummaryChart chrt9 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Birmingham South");
		chrt9.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt9.addGroup(ds.getGroup(9));
		report.addChart(chrt9);

		IGroupsSummaryChart chrt10 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Kings Lynn");
		chrt10.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt10.addGroup(ds.getGroup(11));
		report.addChart(chrt10);

		IGroupsSummaryChart chrt11 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Solihull");
		chrt11.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt11.addGroup(ds.getGroup(12));
		report.addChart(chrt11);

		IGroupsSummaryChart chrt12 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Cheshire & Wirral");
		chrt12.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt12.addGroup(ds.getGroup(13));
		report.addChart(chrt12);

		IGroupsSummaryChart chrt13 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Huntingdon");
		chrt13.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt13.addGroup(ds.getGroup(14));
		report.addChart(chrt13);

		IGroupsSummaryChart chrt14 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Peterborough");
		chrt14.addType(org.psygrid.data.reporting.Chart.CHART_BAR);
		chrt14.addGroup(ds.getGroup(15));
		report.addChart(chrt14);

		return report;
	}

	public static IReport ciMgmtReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National EDEN - Chief Investigator Report");
		report.setEmailAction(RBACAction.ACTION_DR_CHIEF_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(false);
		report.setTemplate(true);

		IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);

		IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
		report.addChart(chrt1);
		return report;
	}

	@Deprecated
	public static IReport piHeartBirminghamWestMgmtReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National EDEN - Principal Investigator (Heart of Birmingham - West EIS) Report");
		report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.addGroup(ds.getGroup(0));
		report.setWithRawData(false);
		report.setTemplate(false);

		IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
		IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
		report.addChart(chrt1);

		IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Heart of Birmingham - West EIS");
		chrt2.addGroup(ds.getGroup(0));
		report.addChart(chrt2);

		return report;
	}

	@Deprecated
	public static IReport piHeartBirminghamEastMgmtReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National EDEN - Principal Investigator (Heart of Birmingham - East EIS) Report");
		report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.addGroup(ds.getGroup(1));
		report.setWithRawData(false);
		report.setTemplate(false);

		IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
		IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
		report.addChart(chrt1);

		IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Heart of Birmingham - East EIS");
		chrt2.addGroup(ds.getGroup(1));
		report.addChart(chrt2);

		return report;
	}

	@Deprecated
	public static IReport piPCTBirminghamEastMgmtReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National EDEN - Principal Investigator (Birmingham East PCT) Report");
		report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.addGroup(ds.getGroup(2));
		report.setWithRawData(false);
		report.setTemplate(false);

		IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
		IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
		report.addChart(chrt1);

		IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Birmingham East PCT");
		chrt2.addGroup(ds.getGroup(2));
		report.addChart(chrt2);

		return report;
	}

	@Deprecated
	public static IReport piLancashireMgmtReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National EDEN - Principal Investigator (Lancashire) Report");
		report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.addGroup(ds.getGroup(3));
		report.addGroup(ds.getGroup(4));
		report.addGroup(ds.getGroup(10));
		report.setWithRawData(false);
		report.setTemplate(false);

		IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
		IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
		report.addChart(chrt1);

		IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Lancashire");
		chrt2.addGroup(ds.getGroup(3));
		chrt2.addGroup(ds.getGroup(4));
		chrt2.addGroup(ds.getGroup(10));
		report.addChart(chrt2);


		return report;
	}

	@Deprecated
	public static IReport piNorfolkMgmtReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National EDEN - Principal Investigator (Norfolk) Report");
		report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.addGroup(ds.getGroup(5));
		report.setWithRawData(false);
		report.setTemplate(false);

		IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
		IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
		report.addChart(chrt1);

		IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Norfolk");
		chrt2.addGroup(ds.getGroup(5));
		report.addChart(chrt2);


		return report;
	}

	@Deprecated
	public static IReport piCambridgeMgmtReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National EDEN - Principal Investigator (Cambridge CAMEO) Report");
		report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.addGroup(ds.getGroup(6));
		report.setWithRawData(false);
		report.setTemplate(false);

		IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
		IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
		report.addChart(chrt1);

		IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Cambridge CAMEO");
		chrt2.addGroup(ds.getGroup(6));
		report.addChart(chrt2);

		return report;
	}

	@Deprecated
	public static IReport piCornwallMgmtReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National EDEN - Principal Investigator (Cornwall) Report");
		report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.addGroup(ds.getGroup(7));
		report.addGroup(ds.getGroup(8));
		report.setWithRawData(false);
		report.setTemplate(false);

		IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
		IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
		report.addChart(chrt1);

		IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Cornwall");
		chrt2.addGroup(ds.getGroup(7));
		chrt2.addGroup(ds.getGroup(8));
		report.addChart(chrt2);

		return report;
	}

	@Deprecated
	public static IReport piPCTBirminghamSouthMgmtReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National EDEN - Principal Investigator (Birmingham South PCT) Report");
		report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.addGroup(ds.getGroup(9));
		report.setWithRawData(false);
		report.setTemplate(false);

		IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
		IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
		report.addChart(chrt1);

		IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Birmingham South PCT");
		chrt2.addGroup(ds.getGroup(9));
		report.addChart(chrt2);

		return report;
	}


	public static IReport insightScaleReportBaseline(DataSet ds) throws Exception {

		Document insight = ds.getDocument(3);
		//check that we have the right document!
		if ( !insight.getName().equals("Insight Scale Scoring") ){
			throw new Exception("This isn't the Insight Scale document!!!");
		}
		DocumentOccurrence insightBaseline = insight.getOccurrence(0);

		return insightScaleReport(ds, insightBaseline);
	}

	public static IReport insightScaleReport12Months(DataSet ds) throws Exception {

		Document insight = ds.getDocument(3);
		//check that we have the right document!
		if ( !insight.getName().equals("Insight Scale Scoring") ){
			throw new Exception("This isn't the Insight Scale document!!!");
		}
		DocumentOccurrence insight12Months = insight.getOccurrence(1);

		return insightScaleReport(ds, insight12Months);
	}

	private static IReport insightScaleReport(DataSet ds, DocumentOccurrence docOcc) throws Exception {

		IRecordReport insightScaleReport =
			factory.createRecordReport(ds, "Insight Scale - "+docOcc.getDisplayText());

		Document insight = ds.getDocument(3);
		//check that we have the right document!
		if ( !insight.getName().equals("Insight Scale Scoring") ){
			throw new Exception("This isn't the Insight Scale document!!!");
		}
		Section mainSec = insight.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		//Summary chart
		IRecordChart summary =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
					"Summary");
		insightScaleReport.addChart(summary);

		ISimpleChartItem s1 = factory.createSimpleChartItem(insight.getEntry(9), docOcc, mainSecOcc);
		summary.addItem(s1);
		ISimpleChartItem s2 = factory.createSimpleChartItem(insight.getEntry(10), docOcc, mainSecOcc);
		summary.addItem(s2);
		ISimpleChartItem s3 = factory.createSimpleChartItem(insight.getEntry(11), docOcc, mainSecOcc);
		summary.addItem(s3);
		ISimpleChartItem s4 = factory.createSimpleChartItem(insight.getEntry(13), docOcc, mainSecOcc);
		summary.addItem(s4);

		return insightScaleReport;
	}

	public static IReport DUPReportBaseline(DataSet ds) throws Exception {

		Document dup = ds.getDocument(9);
		//check that we have the right document!
		if ( !dup.getName().equals("DUP") ){
			throw new Exception("This isn't the DUP document!!!");
		}
		DocumentOccurrence dupBaseline = dup.getOccurrence(0);

		return DUPReport(ds, dupBaseline);
	}

	private static IReport DUPReport(DataSet ds, DocumentOccurrence docOcc) throws Exception {

		IRecordReport dupReport =
			factory.createRecordReport(ds, "DUP - "+docOcc.getDisplayText());

		Document dup = ds.getDocument(9);
		//check that we have the right document!
		if ( !dup.getName().equals("DUP") ){
			throw new Exception("This isn't the DUP document!!!");
		}
		Section mainSec = dup.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		//Summary chart
		IRecordChart summary =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
					"Summary");
		dupReport.addChart(summary);

		ISimpleChartItem s1 = factory.createSimpleChartItem(dup.getEntry(6), docOcc, mainSecOcc);
		summary.addItem(s1);
		ISimpleChartItem s2 = factory.createSimpleChartItem(dup.getEntry(7), docOcc, mainSecOcc);
		summary.addItem(s2);
		ISimpleChartItem s3 = factory.createSimpleChartItem(dup.getEntry(8), docOcc, mainSecOcc);
		summary.addItem(s3);
		ISimpleChartItem s4 = factory.createSimpleChartItem(dup.getEntry(9), docOcc, mainSecOcc);
		summary.addItem(s4);
		ISimpleChartItem s5 = factory.createSimpleChartItem(dup.getEntry(10), docOcc, mainSecOcc);
		summary.addItem(s5);
		ISimpleChartItem s6 = factory.createSimpleChartItem(dup.getEntry(11), docOcc, mainSecOcc);
		summary.addItem(s6);
		ISimpleChartItem s7 = factory.createSimpleChartItem(dup.getEntry(12), docOcc, mainSecOcc);
		summary.addItem(s7);
		ISimpleChartItem s8 = factory.createSimpleChartItem(dup.getEntry(13), docOcc, mainSecOcc);
		summary.addItem(s8);

		return dupReport;
	}
	public static IReport calgaryScaleReportBaseline(DataSet ds) throws Exception {

		Document calgaryScale = ds.getDocument(4);
		//check that we have the right document!
		if ( !calgaryScale.getName().equals("Calgary") ){
			throw new Exception("This isn't the Calgary document!!!");
		}
		DocumentOccurrence calgaryScaleBaseline = calgaryScale.getOccurrence(0);

		return calgaryScaleReport(ds, calgaryScaleBaseline);
	}

	public static IReport calgaryScaleReport6Months(DataSet ds) throws Exception {

		Document calgaryScale = ds.getDocument(4);
		//check that we have the right document!
		if ( !calgaryScale.getName().equals("Calgary") ){
			throw new Exception("This isn't the Calgary document!!!");
		}
		DocumentOccurrence calgaryScale6Months = calgaryScale.getOccurrence(1);

		return calgaryScaleReport(ds, calgaryScale6Months);
	}

	public static IReport calgaryScaleReport12Months(DataSet ds) throws Exception {

		Document calgaryScale = ds.getDocument(4);
		//check that we have the right document!
		if ( !calgaryScale.getName().equals("Calgary") ){
			throw new Exception("This isn't the Calgary document!!!");
		}
		DocumentOccurrence calgaryScale12Months = calgaryScale.getOccurrence(2);

		return calgaryScaleReport(ds, calgaryScale12Months);
	}

	private static IReport calgaryScaleReport(DataSet ds, DocumentOccurrence docOcc) throws Exception {

		IRecordReport calgaryScaleReport =
			factory.createRecordReport(ds, "Calgary Depression for Schizophrenia Scale - "+docOcc.getDisplayText());

		Document calgaryScale = ds.getDocument(4);
		//check that we have the right document!
		if ( !calgaryScale.getName().equals("Calgary") ){
			throw new Exception("This isn't the Calgary document!!!");
		}
		Section mainSec = calgaryScale.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		//Summary chart
		IRecordChart summary =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
					"Summary");
		calgaryScaleReport.addChart(summary);

		ISimpleChartItem total = factory.createSimpleChartItem(calgaryScale.getEntry(10), docOcc, mainSecOcc);
		summary.addItem(total);

		//Scores chart
		IRecordChart scores =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_BAR,
					"Scores");
		calgaryScaleReport.addChart(scores);

		ISimpleChartItem p1 = factory.createSimpleChartItem(calgaryScale.getEntry(1), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(p1);
		ISimpleChartItem p2 = factory.createSimpleChartItem(calgaryScale.getEntry(2), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(p2);
		ISimpleChartItem p3 = factory.createSimpleChartItem(calgaryScale.getEntry(3), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(p3);
		ISimpleChartItem p4 = factory.createSimpleChartItem(calgaryScale.getEntry(4), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(p4);
		ISimpleChartItem p5 = factory.createSimpleChartItem(calgaryScale.getEntry(5), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(p5);
		ISimpleChartItem p6 = factory.createSimpleChartItem(calgaryScale.getEntry(6), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(p6);
		ISimpleChartItem p7 = factory.createSimpleChartItem(calgaryScale.getEntry(7), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(p7);
		ISimpleChartItem p8 = factory.createSimpleChartItem(calgaryScale.getEntry(8), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(p8);
		ISimpleChartItem p9 = factory.createSimpleChartItem(calgaryScale.getEntry(9), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		scores.addItem(p9);

		return calgaryScaleReport;
	}

	public static IReport eq5dReportBaseline(DataSet ds) throws Exception {
		Document eq5d = ds.getDocument(8);
		//check that we have the right document!
		if ( !eq5d.getName().equals("EQ5D") ){
			throw new Exception("This isn't the EQ5D document!!!");
		}
		DocumentOccurrence eq5dBaseline = eq5d.getOccurrence(0);
		return eq5dReport(ds, eq5dBaseline);
	}
	public static IReport eq5dReport12Months(DataSet ds) throws Exception {
		Document eq5d = ds.getDocument(8);
		//check that we have the right document!
		if ( !eq5d.getName().equals("EQ5D") ){
			throw new Exception("This isn't the EQ5D document!!!");
		}
		DocumentOccurrence eq5d12Months = eq5d.getOccurrence(1);
		return eq5dReport(ds, eq5d12Months);
	}


	private static IReport eq5dReport(DataSet ds, DocumentOccurrence docOcc) throws Exception {

		IRecordReport eq5dReport =
			factory.createRecordReport(ds, "Health Questionnaire EQ5D - "+docOcc.getDisplayText());

		Document eq5d = ds.getDocument(8);
		//check that we have the right document!
		if ( !eq5d.getName().equals("EQ5D") ){
			throw new Exception("This isn't the EQ5D document!!!");
		}
		Section mainSec = eq5d.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		//Scores chart
		IRecordChart scores =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_BAR,
					"Scores");
		eq5dReport.addChart(scores);

		ISimpleChartItem p1 = factory.createSimpleChartItem(eq5d.getEntry(1), docOcc, mainSecOcc, "Code", ISimpleChartItem.LABEL_TEXT_ONLY);
		scores.addItem(p1);
		ISimpleChartItem p2 = factory.createSimpleChartItem(eq5d.getEntry(2), docOcc, mainSecOcc, "Code", ISimpleChartItem.LABEL_TEXT_ONLY);
		scores.addItem(p2);
		ISimpleChartItem p3 = factory.createSimpleChartItem(eq5d.getEntry(3), docOcc, mainSecOcc, "Code", ISimpleChartItem.LABEL_TEXT_ONLY);
		scores.addItem(p3);
		ISimpleChartItem p4 = factory.createSimpleChartItem(eq5d.getEntry(4), docOcc, mainSecOcc, "Code", ISimpleChartItem.LABEL_TEXT_ONLY);
		scores.addItem(p4);
		ISimpleChartItem p5 = factory.createSimpleChartItem(eq5d.getEntry(5), docOcc, mainSecOcc, "Code", ISimpleChartItem.LABEL_TEXT_ONLY);
		scores.addItem(p5);


		return eq5dReport;
	}

	public static IReport premorbidAdjustmentReport(DataSet ds) throws Exception {

		Document pas = ds.getDocument(11);
		DocumentOccurrence docOcc = pas.getOccurrence(0);

		//create a report detailing the premorbid adjustment scale for an individual
		IRecordReport pasReport =
			factory.createRecordReport(ds, "Premorbid Adjustment Scale - "+docOcc.getDisplayText());

		//check that we have the right document!
		if ( !pas.getName().equals("Premorbid Summary Sheet") ){
			throw new Exception("This isn't the Premorbid document!!!");
		}

		//retrieve the section containing the adjusted scores
		Section posSec = pas.getSection(2);
		SectionOccurrence adjustedSecOcc = posSec.getOccurrence(0);

		//Summary chart (a table containing the totals for each section)
		IRecordChart summary = factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_BAR,
		"Adjusted Scores");
		pasReport.addChart(summary);
		summary.setRangeAxisLabel("Score");		//y-axis label

		ISimpleChartRow t_childhood = factory.createSimpleChartRow();
		t_childhood.setLabel("Childhood");
		ISimpleChartRow t_earlyadolescence = factory.createSimpleChartRow();
		t_earlyadolescence.setLabel("Early Adolescence");
		ISimpleChartRow t_lateadolescence = factory.createSimpleChartRow();
		t_lateadolescence.setLabel("Late Adolescence");
		ISimpleChartRow t_adulthood = factory.createSimpleChartRow();
		t_adulthood.setLabel("Adulthood");

		summary.addRow(t_childhood);
		summary.addRow(t_earlyadolescence);
		summary.addRow(t_lateadolescence);
		summary.addRow(t_adulthood);


		//Group the following:
		// - childhood
		// - early adolescence
		// - late adolescence
		// - adulthood
		//
		//into the categories of:
		// - adaption to school
		// - peer relationships
		// - scholastic performance
		// - socialbility and withdrawal
		// - social sexual aspects of life



		//entry 62 = childhood
		//entry 67 = early adolesence
		//entry 73 = late adolesence
		//entry 79 = adult
		//entry 83 = general(stop)
		try {
			//childhood == 62
			for (int i = 63; i < 67; i++) {
				ISimpleChartItem item = factory.createSimpleChartItem(
						pas.getEntry(i), docOcc, adjustedSecOcc);
				t_childhood.addSeries(item);
			}
			for (int i = 68; i < 73; i++) {
				ISimpleChartItem item = factory.createSimpleChartItem(
						pas.getEntry(i), docOcc, adjustedSecOcc);
				t_earlyadolescence.addSeries(item);
			}
			//get label from entry(64) (i.e late adolesence)
			for (int i = 74; i < 79; i++) {
				ISimpleChartItem item = factory.createSimpleChartItem(
						pas.getEntry(i), docOcc, adjustedSecOcc);
				t_lateadolescence.addSeries(item);
			}
			for (int i = 80; i < 83; i++) {
				ISimpleChartItem item = factory.createSimpleChartItem(
						pas.getEntry(i), docOcc, adjustedSecOcc);
				t_adulthood.addSeries(item);
			}
		}
		catch (Exception e) {
			// if the entry doesn't exist continue anyway
		}

		Section orgSec = pas.getSection(0);
		SectionOccurrence orgSecOcc = orgSec.getOccurrence(0);

		//Individuals chart (a table containing the scores for an individual in each section)
		IRecordChart individual = factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_BAR,
		"Client's Original Scores");
		pasReport.addChart(individual);
		individual.setRangeAxisLabel("Score");		//y-axis label

		ISimpleChartRow t_childhood1 = factory.createSimpleChartRow();
		t_childhood1.setLabel("Childhood");
		ISimpleChartRow t_earlyadolescence1 = factory.createSimpleChartRow();
		t_earlyadolescence1.setLabel("Early Adolescence");
		ISimpleChartRow t_lateadolescence1 = factory.createSimpleChartRow();
		t_lateadolescence1.setLabel("Late Adolescence");
		ISimpleChartRow t_adulthood1 = factory.createSimpleChartRow();
		t_adulthood1.setLabel("Adulthood");

		individual.addRow(t_childhood1);
		individual.addRow(t_earlyadolescence1);
		individual.addRow(t_lateadolescence1);
		individual.addRow(t_adulthood1);

		try {
			for (int i = 1; i < 5; i++) {
				ISimpleChartItem item = factory.createSimpleChartItem(
						pas.getEntry(i), docOcc, orgSecOcc);
				t_childhood1.addSeries(item);
			}
			for (int i = 6; i < 11; i++) {
				ISimpleChartItem item = factory.createSimpleChartItem(
						pas.getEntry(i), docOcc, orgSecOcc);
				t_earlyadolescence1.addSeries(item);
			}
			for (int i = 12; i < 17; i++) {
				ISimpleChartItem item = factory.createSimpleChartItem(
						pas.getEntry(i), docOcc, orgSecOcc);
				t_lateadolescence1.addSeries(item);
			}
			for (int i = 18; i < 21; i++) {
				ISimpleChartItem item = factory.createSimpleChartItem(
						pas.getEntry(i), docOcc, orgSecOcc);
				t_adulthood1.addSeries(item);
			}
		}
		catch (Exception e) {
			// if the entry doesn't exist continue anyway
		}

		return pasReport;
	}

	private static IReport premorbidTrendsReportBaseline(DataSet ds, List<Group> groups, String hub) throws ReportException {
		Document pas = ds.getDocument(11);

		//check that we have the right document!
		if ( !pas.getName().equals("Premorbid Summary Sheet") ){
			throw new ReportException("This isn't the Premorbid document!!!");
		}
		//Retrieve baseline document
		DocumentOccurrence docOcc = pas.getOccurrence(0);

		return premorbidTrendsReport(ds, docOcc, groups, hub);
	}

	private static IReport premorbidTrendsReport(DataSet ds, DocumentOccurrence docOcc, List<Group> groups, String hub) throws ReportException {

		Document pas = ds.getDocument(11);

		//Check that we have the right document!
		if ( !pas.getName().equals("Premorbid Summary Sheet") ){
			throw new ReportException("This isn't the Premorbid document!!!");
		}

		//Get the section containing the adjusted scores
		Section posSec = pas.getSection(2);
		SectionOccurrence adjustedSecOcc = posSec.getOccurrence(0);

		//Retrieve the section containing the original client scores
		Section orgSec = pas.getSection(0);
		SectionOccurrence orgSecOcc = orgSec.getOccurrence(0);

		//Create a new trends report
		ITrendsReport pasReport =
			factory.createTrendsReport(ds, "Premorbid - "+docOcc.getDisplayText() +" Trends");

		//get the mean average of the values in the summary
		String summaryType = ITrendsChartRow.SUMMARY_TYPE_MEAN;


		{
			//Summary chart
			ITrendsChart summary = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
					"Sociability and Withdrawal (Adjusted Scores)");
			summary.setRangeAxisLabel("Score");
			pasReport.addChart(summary);
			summary = setPreviousSixMonths(summary);

			if (groups != null) {
				for (Group g: groups) {
					summary.addGroup(g);
				}
			}

			ITrendsChartRow t_childhood = factory.createTrendsChartRow();
			t_childhood.setLabel("Childhood");
			t_childhood.setSummaryType(summaryType);
			ITrendsChartRow t_earlyadolescence = factory.createTrendsChartRow();
			t_earlyadolescence.setLabel("Early Adolescence");
			t_earlyadolescence.setSummaryType(summaryType);
			ITrendsChartRow t_lateadolescence = factory.createTrendsChartRow();
			t_lateadolescence.setLabel("Late Adolescence");
			t_lateadolescence.setSummaryType(summaryType);
			ITrendsChartRow t_adulthood = factory.createTrendsChartRow();
			t_adulthood.setLabel("Adulthood");
			t_adulthood.setSummaryType(summaryType);

			summary.addRow(t_childhood);
			summary.addRow(t_earlyadolescence);
			summary.addRow(t_lateadolescence);
			summary.addRow(t_adulthood);

			try {
					ISimpleChartItem item = factory.createSimpleChartItem(
							pas.getEntry(63), docOcc, adjustedSecOcc);
					t_childhood.addSeries(item);
					ISimpleChartItem item1 = factory.createSimpleChartItem(
							pas.getEntry(68), docOcc, adjustedSecOcc);
					t_earlyadolescence.addSeries(item1);
					ISimpleChartItem item2 = factory.createSimpleChartItem(
							pas.getEntry(74), docOcc, adjustedSecOcc);
					t_lateadolescence.addSeries(item2);
					ISimpleChartItem item3 = factory.createSimpleChartItem(
							pas.getEntry(80), docOcc, adjustedSecOcc);
					t_adulthood.addSeries(item3);
			}
			catch (Exception e) {
				// if the entry doesn't exist continue anyway
			}
		}

		{
			//Summary chart
			ITrendsChart summary = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
					"Sociability and Withdrawal (Client's Original Scores)");
			summary.setRangeAxisLabel("Score");
			pasReport.addChart(summary);
			summary = setPreviousSixMonths(summary);

			if (groups != null) {
				for (Group g: groups) {
					summary.addGroup(g);
				}
			}

			ITrendsChartRow t_childhood = factory.createTrendsChartRow();
			t_childhood.setLabel("Childhood");
			t_childhood.setSummaryType(summaryType);
			ITrendsChartRow t_earlyadolescence = factory.createTrendsChartRow();
			t_earlyadolescence.setLabel("Early Adolescence");
			t_earlyadolescence.setSummaryType(summaryType);
			ITrendsChartRow t_lateadolescence = factory.createTrendsChartRow();
			t_lateadolescence.setLabel("Late Adolescence");
			t_lateadolescence.setSummaryType(summaryType);
			ITrendsChartRow t_adulthood = factory.createTrendsChartRow();
			t_adulthood.setLabel("Adulthood");
			t_adulthood.setSummaryType(summaryType);

			summary.addRow(t_childhood);
			summary.addRow(t_earlyadolescence);
			summary.addRow(t_lateadolescence);
			summary.addRow(t_adulthood);

			try {
					ISimpleChartItem item = factory.createSimpleChartItem(
							pas.getEntry(1), docOcc, orgSecOcc);
					t_childhood.addSeries(item);
					ISimpleChartItem item1 = factory.createSimpleChartItem(
							pas.getEntry(6), docOcc, orgSecOcc);
					t_earlyadolescence.addSeries(item1);
					ISimpleChartItem item2 = factory.createSimpleChartItem(
							pas.getEntry(12), docOcc, orgSecOcc);
					t_lateadolescence.addSeries(item2);
					ISimpleChartItem item3 = factory.createSimpleChartItem(
							pas.getEntry(18), docOcc, orgSecOcc);
					t_adulthood.addSeries(item3);
			}
			catch (Exception e) {
				// if the entry doesn't exist continue anyway
			}
		}

		{
			//Peer relationships chart
			ITrendsChart peers = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
					"Peer Relationships (Adjusted Scores)");
			peers.setRangeAxisLabel("Score");
			pasReport.addChart(peers);

			peers = setPreviousSixMonths(peers);

			if (groups != null) {
				for (Group g: groups) {
					peers.addGroup(g);
				}
			}

			ITrendsChartRow t_childhood = factory.createTrendsChartRow();
			t_childhood.setLabel("Childhood");
			t_childhood.setSummaryType(summaryType);
			ITrendsChartRow t_earlyadolescence = factory.createTrendsChartRow();
			t_earlyadolescence.setLabel("Early Adolescence");
			t_earlyadolescence.setSummaryType(summaryType);
			ITrendsChartRow t_lateadolescence = factory.createTrendsChartRow();
			t_lateadolescence.setLabel("Late Adolescence");
			t_lateadolescence.setSummaryType(summaryType);
			ITrendsChartRow t_adulthood = factory.createTrendsChartRow();
			t_adulthood.setLabel("Adulthood");
			t_adulthood.setSummaryType(summaryType);

			peers.addRow(t_childhood);
			peers.addRow(t_earlyadolescence);
			peers.addRow(t_lateadolescence);
			peers.addRow(t_adulthood);


			try {
				ISimpleChartItem item = factory.createSimpleChartItem(
						pas.getEntry(64), docOcc, adjustedSecOcc);
				t_childhood.addSeries(item);

				ISimpleChartItem item1 = factory.createSimpleChartItem(
						pas.getEntry(69), docOcc, adjustedSecOcc);
				t_earlyadolescence.addSeries(item1);

				ISimpleChartItem item2 = factory.createSimpleChartItem(
						pas.getEntry(75), docOcc, adjustedSecOcc);
				t_lateadolescence.addSeries(item2);

				ISimpleChartItem item3 = factory.createSimpleChartItem(
						pas.getEntry(81), docOcc, adjustedSecOcc);
				t_adulthood.addSeries(item3);

			}
			catch (Exception e) {
				// if the entry doesn't exist continue anyway
			}
		}

		{
			//Peer relationships chart
			ITrendsChart peers = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
					"Peer Relationships (Client's Original Scores)");
			peers.setRangeAxisLabel("Score");
			pasReport.addChart(peers);

			peers = setPreviousSixMonths(peers);

			if (groups != null) {
				for (Group g: groups) {
					peers.addGroup(g);
				}
			}

			ITrendsChartRow t_childhood = factory.createTrendsChartRow();
			t_childhood.setLabel("Childhood");
			t_childhood.setSummaryType(summaryType);
			ITrendsChartRow t_earlyadolescence = factory.createTrendsChartRow();
			t_earlyadolescence.setLabel("Early Adolescence");
			t_earlyadolescence.setSummaryType(summaryType);
			ITrendsChartRow t_lateadolescence = factory.createTrendsChartRow();
			t_lateadolescence.setLabel("Late Adolescence");
			t_lateadolescence.setSummaryType(summaryType);
			ITrendsChartRow t_adulthood = factory.createTrendsChartRow();
			t_adulthood.setLabel("Adulthood");
			t_adulthood.setSummaryType(summaryType);

			peers.addRow(t_childhood);
			peers.addRow(t_earlyadolescence);
			peers.addRow(t_lateadolescence);
			peers.addRow(t_adulthood);


			try {
				ISimpleChartItem item = factory.createSimpleChartItem(
						pas.getEntry(2), docOcc, orgSecOcc);
				t_childhood.addSeries(item);

				ISimpleChartItem item1 = factory.createSimpleChartItem(
						pas.getEntry(7), docOcc, orgSecOcc);
				t_earlyadolescence.addSeries(item1);

				ISimpleChartItem item2 = factory.createSimpleChartItem(
						pas.getEntry(13), docOcc, orgSecOcc);
				t_lateadolescence.addSeries(item2);

				ISimpleChartItem item3 = factory.createSimpleChartItem(
						pas.getEntry(19), docOcc, orgSecOcc);
				t_adulthood.addSeries(item3);

			}
			catch (Exception e) {
				// if the entry doesn't exist continue anyway
			}
		}

		{
			//Scholastic performance chart
			ITrendsChart scholastic = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
					"Scholastic Performance (Adjusted Scores)");
			scholastic.setRangeAxisLabel("Score");
			pasReport.addChart(scholastic);
			scholastic = setPreviousSixMonths(scholastic);

			if (groups != null) {
				for (Group g: groups) {
					scholastic.addGroup(g);
				}
			}

			ITrendsChartRow t_childhood = factory.createTrendsChartRow();
			t_childhood.setLabel("Childhood");
			t_childhood.setSummaryType(summaryType);
			ITrendsChartRow t_earlyadolescence = factory.createTrendsChartRow();
			t_earlyadolescence.setLabel("Early Adolescence");
			t_earlyadolescence.setSummaryType(summaryType);
			ITrendsChartRow t_lateadolescence = factory.createTrendsChartRow();
			t_lateadolescence.setLabel("Late Adolescence");
			t_lateadolescence.setSummaryType(summaryType);

			scholastic.addRow(t_childhood);
			scholastic.addRow(t_earlyadolescence);
			scholastic.addRow(t_lateadolescence);


			try {
				ISimpleChartItem item = factory.createSimpleChartItem(
						pas.getEntry(65), docOcc, adjustedSecOcc);
				t_childhood.addSeries(item);

				ISimpleChartItem item1 = factory.createSimpleChartItem(
						pas.getEntry(70), docOcc, adjustedSecOcc);
				t_earlyadolescence.addSeries(item1);

				ISimpleChartItem item2 = factory.createSimpleChartItem(
						pas.getEntry(76), docOcc, adjustedSecOcc);
				t_lateadolescence.addSeries(item2);
			}
			catch (Exception e) {
				// if the entry doesn't exist continue anyway
			}
		}

		{
			//Scholastic performance chart
			ITrendsChart scholastic = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
					"Scholastic Performance (Client's Original Scores)");
			scholastic.setRangeAxisLabel("Score");
			pasReport.addChart(scholastic);
			scholastic = setPreviousSixMonths(scholastic);

			if (groups != null) {
				for (Group g: groups) {
					scholastic.addGroup(g);
				}
			}

			ITrendsChartRow t_childhood = factory.createTrendsChartRow();
			t_childhood.setLabel("Childhood");
			t_childhood.setSummaryType(summaryType);
			ITrendsChartRow t_earlyadolescence = factory.createTrendsChartRow();
			t_earlyadolescence.setLabel("Early Adolescence");
			t_earlyadolescence.setSummaryType(summaryType);
			ITrendsChartRow t_lateadolescence = factory.createTrendsChartRow();
			t_lateadolescence.setLabel("Late Adolescence");
			t_lateadolescence.setSummaryType(summaryType);

			scholastic.addRow(t_childhood);
			scholastic.addRow(t_earlyadolescence);
			scholastic.addRow(t_lateadolescence);


			try {
				ISimpleChartItem item = factory.createSimpleChartItem(
						pas.getEntry(3), docOcc, orgSecOcc);
				t_childhood.addSeries(item);

				ISimpleChartItem item1 = factory.createSimpleChartItem(
						pas.getEntry(8), docOcc, orgSecOcc);
				t_earlyadolescence.addSeries(item1);

				ISimpleChartItem item2 = factory.createSimpleChartItem(
						pas.getEntry(14), docOcc, orgSecOcc);
				t_lateadolescence.addSeries(item2);
			}
			catch (Exception e) {
				// if the entry doesn't exist continue anyway
			}
		}

		{
			//Sociability and withdrawal chart
			ITrendsChart social = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
					"Adaption to School (Adjusted Scores)");
			social.setRangeAxisLabel("Score");
			pasReport.addChart(social);
			social = setPreviousSixMonths(social);

			if (groups != null) {
				for (Group g: groups) {
					social.addGroup(g);
				}
			}

			ITrendsChartRow t_childhood = factory.createTrendsChartRow();
			t_childhood.setLabel("Childhood");
			t_childhood.setSummaryType(summaryType);
			ITrendsChartRow t_earlyadolescence = factory.createTrendsChartRow();
			t_earlyadolescence.setLabel("Early Adolescence");
			t_earlyadolescence.setSummaryType(summaryType);
			ITrendsChartRow t_lateadolescence = factory.createTrendsChartRow();
			t_lateadolescence.setLabel("Late Adolescence");
			t_lateadolescence.setSummaryType(summaryType);

			social.addRow(t_childhood);
			social.addRow(t_earlyadolescence);
			social.addRow(t_lateadolescence);

			try {
				ISimpleChartItem item = factory.createSimpleChartItem(
						pas.getEntry(66), docOcc, adjustedSecOcc);
				t_childhood.addSeries(item);

				ISimpleChartItem item1 = factory.createSimpleChartItem(
						pas.getEntry(71), docOcc, adjustedSecOcc);
				t_earlyadolescence.addSeries(item1);

				ISimpleChartItem item2 = factory.createSimpleChartItem(
						pas.getEntry(77), docOcc, adjustedSecOcc);
				t_lateadolescence.addSeries(item2);
			}
			catch (Exception e) {
				// if the entry doesn't exist continue anyway
			}
		}

		{
			//Sociability and withdrawal chart
			ITrendsChart social = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
					"Adaption to School (Client's Original Scores)");
			social.setRangeAxisLabel("Score");
			pasReport.addChart(social);
			social = setPreviousSixMonths(social);

			if (groups != null) {
				for (Group g: groups) {
					social.addGroup(g);
				}
			}

			ITrendsChartRow t_childhood = factory.createTrendsChartRow();
			t_childhood.setLabel("Childhood");
			t_childhood.setSummaryType(summaryType);
			ITrendsChartRow t_earlyadolescence = factory.createTrendsChartRow();
			t_earlyadolescence.setLabel("Early Adolescence");
			t_earlyadolescence.setSummaryType(summaryType);
			ITrendsChartRow t_lateadolescence = factory.createTrendsChartRow();
			t_lateadolescence.setLabel("Late Adolescence");
			t_lateadolescence.setSummaryType(summaryType);

			social.addRow(t_childhood);
			social.addRow(t_earlyadolescence);
			social.addRow(t_lateadolescence);

			try {
				ISimpleChartItem item = factory.createSimpleChartItem(
						pas.getEntry(4), docOcc, orgSecOcc);
				t_childhood.addSeries(item);

				ISimpleChartItem item1 = factory.createSimpleChartItem(
						pas.getEntry(9), docOcc, orgSecOcc);
				t_earlyadolescence.addSeries(item1);

				ISimpleChartItem item2 = factory.createSimpleChartItem(
						pas.getEntry(15), docOcc, orgSecOcc);
				t_lateadolescence.addSeries(item2);
			}
			catch (Exception e) {
				// if the entry doesn't exist continue anyway
			}
		}

		{
			//Social sexual aspects of life chart
			ITrendsChart summary = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
					"Social Sexual Aspects of Life (Adjusted Scores)");
			summary.setRangeAxisLabel("Score");
			pasReport.addChart(summary);

			summary = setPreviousSixMonths(summary);

			if (groups != null) {
				for (Group g: groups) {
					summary.addGroup(g);
				}
			}

			ITrendsChartRow t_earlyadolescence = factory.createTrendsChartRow();
			t_earlyadolescence.setLabel("Early Adolescence");
			t_earlyadolescence.setSummaryType(summaryType);
			ITrendsChartRow t_lateadolescence = factory.createTrendsChartRow();
			t_lateadolescence.setLabel("Late Adolescence");
			t_lateadolescence.setSummaryType(summaryType);
			ITrendsChartRow t_adulthood = factory.createTrendsChartRow();
			t_adulthood.setLabel("Adulthood");
			t_adulthood.setSummaryType(summaryType);

			summary.addRow(t_earlyadolescence);
			summary.addRow(t_lateadolescence);
			summary.addRow(t_adulthood);


			try {

				ISimpleChartItem item = factory.createSimpleChartItem(
						pas.getEntry(72), docOcc, adjustedSecOcc);
				t_earlyadolescence.addSeries(item);
				ISimpleChartItem item1 = factory.createSimpleChartItem(
						pas.getEntry(78), docOcc, adjustedSecOcc);
				t_lateadolescence.addSeries(item1);

				ISimpleChartItem item2 = factory.createSimpleChartItem(
						pas.getEntry(82), docOcc, adjustedSecOcc);
				t_adulthood.addSeries(item2);

			}
			catch (Exception e) {
				// if the entry doesn't exist continue anyway
			}
		}

		{
			//Social sexual aspects of life chart
			ITrendsChart summary = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
					"Social Sexual Aspects of Life (Client's Original Scores)");
			summary.setRangeAxisLabel("Score");
			pasReport.addChart(summary);

			summary = setPreviousSixMonths(summary);

			if (groups != null) {
				for (Group g: groups) {
					summary.addGroup(g);
				}
			}

			ITrendsChartRow t_earlyadolescence = factory.createTrendsChartRow();
			t_earlyadolescence.setLabel("Early Adolescence");
			t_earlyadolescence.setSummaryType(summaryType);
			ITrendsChartRow t_lateadolescence = factory.createTrendsChartRow();
			t_lateadolescence.setLabel("Late Adolescence");
			t_lateadolescence.setSummaryType(summaryType);
			ITrendsChartRow t_adulthood = factory.createTrendsChartRow();
			t_adulthood.setLabel("Adulthood");
			t_adulthood.setSummaryType(summaryType);

			summary.addRow(t_earlyadolescence);
			summary.addRow(t_lateadolescence);
			summary.addRow(t_adulthood);

			try {

				ISimpleChartItem item = factory.createSimpleChartItem(
						pas.getEntry(10), docOcc, orgSecOcc);
				t_earlyadolescence.addSeries(item);
				ISimpleChartItem item1 = factory.createSimpleChartItem(
						pas.getEntry(16), docOcc, orgSecOcc);
				t_lateadolescence.addSeries(item1);

				ISimpleChartItem item2 = factory.createSimpleChartItem(
						pas.getEntry(20), docOcc, orgSecOcc);
				t_adulthood.addSeries(item2);

			}
			catch (Exception e) {
				// if the entry doesn't exist continue anyway
			}
		}



		return pasReport;
	}


	public static IReport premorbidTrendsReport(DataSet ds) throws ReportException {
		return premorbidTrendsReportBaseline(ds, null, "Overview");
	}

	public static IReport ukCRNReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National EDEN - UKCRN Report");
		report.setEmailAction(RBACAction.ACTION_DR_UKCRN_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(true);
		report.setFrequency(ReportFrequency.MONTHLY);
		report.setShowHeader(false);

		IUKCRNSummaryChart chart = factory.createUKCRNSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "National EDEN");
		report.addChart(chart);

		//This will be set automatically when the report is generated to create
		//a for the current financial year (assuming may-april)
		chart.setTimePeriod(null, null);

		return report;
	}

	/**
	 * Create a management report comparing the number of new subjects consented
	 * into the trial against targets set for each month, giving a view of the
	 * trial's progress.
	 *
	 * @param ds the dataset
	 * @param groups to create the report for
	 * @param hub name of the NHS hub the report is for
	 * @return report
	 * @throws ReportException
	 */
	private static IReport recruitmentReport(DataSet ds, List<Group> groups, String hub) throws ReportException {
		IManagementReport report = recruitmentReport(ds, groups, hub, "PrincipalInvestigator");
		report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		return report;
	}

	private static IManagementReport recruitmentReport(DataSet ds, List<Group> groups, String hub, String role) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National EDEN - Recruitment Progress ("+hub+") Report");
		//report.setRole(role);
		report.setWithRawData(true);
		report.setFrequency(ReportFrequency.MONTHLY);

		/*
		 * Create a timeseries chart showing the number of subjects consented
		 * into the trial against the targets set for each month, giving
		 * a view of the trial's progress.
		 */
		IRecruitmentProgressChart chart = factory.createRecruitmentProgressChart(Chart.CHART_TIME_SERIES, "National EDEN - Recruitment Progress ("+hub+")");

		//Set this automatically when the chart is generated (will show previous 6 months by default)
		chart.setTimePeriod(null, null);

		chart.setRangeAxisLabel("Number of Clients");	//y-axis label

		//check
		if (groups != null) {
			for (Group g: groups) {
				chart.addGroup(g);
			}
		}
		report.addManagementChart(chart);

		return report;
	}

	@Deprecated
	public static IReport recruitmentInHeartBirminghamWestReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(0));
		IReport report = recruitmentReport(ds, groups, "Heart of Birmingham - West EIS");
		report.setTemplate(false);
		return report;
	}

	@Deprecated
	public static IReport recruitmentInHeartBirminghamEastReport(DataSet ds) throws ReportException {
		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(1));

		IReport report = recruitmentReport(ds, groups, "Heart of Birmingham - East EIS");
		report.setTemplate(false);
		return report;
	}

	@Deprecated
	public static IReport recruitmentInPCTBirminghamEastReport(DataSet ds) throws ReportException {

		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(2));

		IReport report = recruitmentReport(ds, groups, "Birmingham East PCT");
		report.setTemplate(false);
		return report;
	}

	public static IReport recruitmentInLancashireReport(DataSet ds) throws ReportException {

		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(3));
		groups.add(ds.getGroup(4));
		groups.add(ds.getGroup(10));

		IReport report = recruitmentReport(ds, groups, "Lancashire");
		report.setTemplate(false);

		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		chart.addTarget(new GregorianCalendar(2005, 7, 0), 0);		//August 2005
		chart.addTarget(new GregorianCalendar(2005, 8, 0), 0);
		chart.addTarget(new GregorianCalendar(2005, 9, 0), 0);
		chart.addTarget(new GregorianCalendar(2005, 10, 0), 0);
		chart.addTarget(new GregorianCalendar(2005, 11, 0), 0);
		chart.addTarget(new GregorianCalendar(2006, 0, 0), 0);
		chart.addTarget(new GregorianCalendar(2006, 1, 0), 0);
		chart.addTarget(new GregorianCalendar(2006, 2, 0), 0);
		chart.addTarget(new GregorianCalendar(2006, 3, 0), 0);
		chart.addTarget(new GregorianCalendar(2006, 4, 0), 7);
		chart.addTarget(new GregorianCalendar(2006, 5, 0), 11);
		chart.addTarget(new GregorianCalendar(2006, 6, 0), 11);
		chart.addTarget(new GregorianCalendar(2006, 7, 0), 27);
		chart.addTarget(new GregorianCalendar(2006, 8, 0), 34);
		chart.addTarget(new GregorianCalendar(2006, 9, 0), 37);
		chart.addTarget(new GregorianCalendar(2006, 10, 0), 40);
		chart.addTarget(new GregorianCalendar(2006, 11, 0), 46);
		chart.addTarget(new GregorianCalendar(2007, 0, 0), 47);
		chart.addTarget(new GregorianCalendar(2007, 1, 0), 59);
		chart.addTarget(new GregorianCalendar(2007, 2, 0), 71);
		chart.addTarget(new GregorianCalendar(2007, 3, 0), 83);
		chart.addTarget(new GregorianCalendar(2007, 4, 0), 95);
		chart.addTarget(new GregorianCalendar(2007, 5, 0), 107);
		chart.addTarget(new GregorianCalendar(2007, 6, 0), 119);
		chart.addTarget(new GregorianCalendar(2007, 7, 0), 131);
		chart.addTarget(new GregorianCalendar(2007, 8, 0), 143);
		chart.addTarget(new GregorianCalendar(2007, 9, 0), 155);
		chart.addTarget(new GregorianCalendar(2007, 10, 0), 167);
		chart.addTarget(new GregorianCalendar(2007, 11, 0), 179);
		chart.addTarget(new GregorianCalendar(2008, 0, 0), 191);
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 203);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 215);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 227);

		return report;
	}

	public static IReport recruitmentInNorfolkReport(DataSet ds) throws ReportException {

		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(5));

		IReport report = recruitmentReport(ds, groups, "Norfolk");
		report.setTemplate(false);

		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		chart.addTarget(new GregorianCalendar(2005, 7, 0), 1);		//August 2005
		chart.addTarget(new GregorianCalendar(2005, 8, 0), 6);
		chart.addTarget(new GregorianCalendar(2005, 9, 0), 8);
		chart.addTarget(new GregorianCalendar(2005, 10, 0), 13);
		chart.addTarget(new GregorianCalendar(2005, 11, 0), 15);
		chart.addTarget(new GregorianCalendar(2006, 0, 0), 20);
		chart.addTarget(new GregorianCalendar(2006, 1, 0), 21);
		chart.addTarget(new GregorianCalendar(2006, 2, 0), 21);
		chart.addTarget(new GregorianCalendar(2006, 3, 0), 22);
		chart.addTarget(new GregorianCalendar(2006, 4, 0), 22);
		chart.addTarget(new GregorianCalendar(2006, 5, 0), 28);
		chart.addTarget(new GregorianCalendar(2006, 6, 0), 31);
		chart.addTarget(new GregorianCalendar(2006, 7, 0), 37);
		chart.addTarget(new GregorianCalendar(2006, 8, 0), 39);
		chart.addTarget(new GregorianCalendar(2006, 9, 0), 42);
		chart.addTarget(new GregorianCalendar(2006, 10, 0), 42);
		chart.addTarget(new GregorianCalendar(2006, 11, 0), 42);
		chart.addTarget(new GregorianCalendar(2007, 0, 0), 42);
		chart.addTarget(new GregorianCalendar(2007, 1, 0), 47);
		chart.addTarget(new GregorianCalendar(2007, 2, 0), 56);
		chart.addTarget(new GregorianCalendar(2007, 3, 0), 65);
		chart.addTarget(new GregorianCalendar(2007, 4, 0), 74);
		chart.addTarget(new GregorianCalendar(2007, 5, 0), 83);
		chart.addTarget(new GregorianCalendar(2007, 6, 0), 92);
		chart.addTarget(new GregorianCalendar(2007, 7, 0), 101);
		chart.addTarget(new GregorianCalendar(2007, 8, 0), 110);
		chart.addTarget(new GregorianCalendar(2007, 9, 0), 119);
		chart.addTarget(new GregorianCalendar(2007, 10, 0), 128);
		chart.addTarget(new GregorianCalendar(2007, 11, 0), 137);
		chart.addTarget(new GregorianCalendar(2008, 0, 0), 146);
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 155);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 164);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 173);

		return report;
	}

	public static IReport recruitmentInCambridgeReport(DataSet ds) throws ReportException {

		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(6));
		groups.add(ds.getGroup(14));

		IReport report =  recruitmentReport(ds, groups, "Cambridge and Huntingdon");
		report.setTemplate(false);

		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		chart.addTarget(new GregorianCalendar(2005, 7, 0), 2);		//August 2005
		chart.addTarget(new GregorianCalendar(2005, 8, 0), 5);
		chart.addTarget(new GregorianCalendar(2005, 9, 0), 7);
		chart.addTarget(new GregorianCalendar(2005, 10, 0), 11);
		chart.addTarget(new GregorianCalendar(2005, 11, 0), 13);
		chart.addTarget(new GregorianCalendar(2006, 0, 0), 15);
		chart.addTarget(new GregorianCalendar(2006, 1, 0), 17);
		chart.addTarget(new GregorianCalendar(2006, 2, 0), 20);
		chart.addTarget(new GregorianCalendar(2006, 3, 0), 23);
		chart.addTarget(new GregorianCalendar(2006, 4, 0), 25);
		chart.addTarget(new GregorianCalendar(2006, 5, 0), 25);
		chart.addTarget(new GregorianCalendar(2006, 6, 0), 25);
		chart.addTarget(new GregorianCalendar(2006, 7, 0), 25);
		chart.addTarget(new GregorianCalendar(2006, 8, 0), 38);
		chart.addTarget(new GregorianCalendar(2006, 9, 0), 43);
		chart.addTarget(new GregorianCalendar(2006, 10, 0), 44);
		chart.addTarget(new GregorianCalendar(2006, 11, 0), 44);
		chart.addTarget(new GregorianCalendar(2007, 0, 0), 45);
		chart.addTarget(new GregorianCalendar(2007, 1, 0), 50);
		chart.addTarget(new GregorianCalendar(2007, 2, 0), 57);
		chart.addTarget(new GregorianCalendar(2007, 3, 0), 64);
		chart.addTarget(new GregorianCalendar(2007, 4, 0), 71);
		chart.addTarget(new GregorianCalendar(2007, 5, 0), 78);
		chart.addTarget(new GregorianCalendar(2007, 6, 0), 85);
		chart.addTarget(new GregorianCalendar(2007, 7, 0), 88);
		chart.addTarget(new GregorianCalendar(2007, 8, 0), 93);
		chart.addTarget(new GregorianCalendar(2007, 9, 0), 98);
		chart.addTarget(new GregorianCalendar(2007, 10, 0), 102);
		chart.addTarget(new GregorianCalendar(2007, 11, 0), 108);
		chart.addTarget(new GregorianCalendar(2008, 0, 0), 113);
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 118);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 123);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 128);

		return report;
	}
	public static IReport recruitmentInPeterboroughReport(DataSet ds) throws ReportException {

		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(15));

		IReport report =  recruitmentReport(ds, groups, "Peterborough");
		report.setTemplate(false);

		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		//TODO get targets

		return report;
	}



	public static IReport recruitmentInCornwallReport(DataSet ds) throws ReportException {

		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(7));
		groups.add(ds.getGroup(8));

		IReport report = recruitmentReport(ds, groups, "Cornwall");
		report.setTemplate(false);

		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		chart.addTarget(new GregorianCalendar(2005, 7, 0), 0);		//August 2005
		chart.addTarget(new GregorianCalendar(2005, 8, 0), 0);
		chart.addTarget(new GregorianCalendar(2005, 9, 0), 0);
		chart.addTarget(new GregorianCalendar(2005, 10, 0), 0);
		chart.addTarget(new GregorianCalendar(2005, 11, 0), 0);
		chart.addTarget(new GregorianCalendar(2006, 0, 0), 0);
		chart.addTarget(new GregorianCalendar(2006, 1, 0), 6);
		chart.addTarget(new GregorianCalendar(2006, 2, 0), 9);
		chart.addTarget(new GregorianCalendar(2006, 3, 0), 12);
		chart.addTarget(new GregorianCalendar(2006, 4, 0), 14);
		chart.addTarget(new GregorianCalendar(2006, 5, 0), 14);
		chart.addTarget(new GregorianCalendar(2006, 6, 0), 15);
		chart.addTarget(new GregorianCalendar(2006, 7, 0), 32);
		chart.addTarget(new GregorianCalendar(2006, 10, 0), 35);
		chart.addTarget(new GregorianCalendar(2006, 11, 0), 42);
		chart.addTarget(new GregorianCalendar(2006, 12, 0), 47);
		chart.addTarget(new GregorianCalendar(2006, 13, 0), 49);
		chart.addTarget(new GregorianCalendar(2007, 0, 0), 51);
		chart.addTarget(new GregorianCalendar(2007, 1, 0), 60);
		chart.addTarget(new GregorianCalendar(2007, 2, 0), 69);
		chart.addTarget(new GregorianCalendar(2007, 3, 0), 78);
		chart.addTarget(new GregorianCalendar(2007, 4, 0), 87);
		chart.addTarget(new GregorianCalendar(2007, 5, 0), 96);
		chart.addTarget(new GregorianCalendar(2007, 6, 0), 105);
		chart.addTarget(new GregorianCalendar(2007, 7, 0), 114);
		chart.addTarget(new GregorianCalendar(2007, 8, 0), 123);
		chart.addTarget(new GregorianCalendar(2007, 9, 0), 132);
		chart.addTarget(new GregorianCalendar(2007, 10, 0), 141);
		chart.addTarget(new GregorianCalendar(2007, 11, 0), 150);
		chart.addTarget(new GregorianCalendar(2008, 0, 0), 159);
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 168);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 177);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 186);

		return report;
	}

	@Deprecated
	public static IReport recruitmentInPCTBirminghamSouthReport(DataSet ds) throws ReportException {

		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(9));

		IReport report =  recruitmentReport(ds, groups, "Birmingham South PCT");
		report.setTemplate(false);
		return report;
	}

	public static IReport recruitmentInBirminghamReport(DataSet ds) throws ReportException {

		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(9));
		groups.add(ds.getGroup(2));
		groups.add(ds.getGroup(1));
		groups.add(ds.getGroup(0));

		IReport report =  recruitmentReport(ds, groups, "Birmingham");
		report.setTemplate(false);

		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		chart.addTarget(new GregorianCalendar(2005, 7, 0), 0);		//August 2005
		chart.addTarget(new GregorianCalendar(2005, 8, 0), 0);
		chart.addTarget(new GregorianCalendar(2005, 9, 0), 1);
		chart.addTarget(new GregorianCalendar(2005, 10, 0), 5);
		chart.addTarget(new GregorianCalendar(2005, 11, 0), 6);
		chart.addTarget(new GregorianCalendar(2006, 0, 0), 6);
		chart.addTarget(new GregorianCalendar(2006, 1, 0), 7);
		chart.addTarget(new GregorianCalendar(2006, 2, 0), 8);
		chart.addTarget(new GregorianCalendar(2006, 3, 0), 14);
		chart.addTarget(new GregorianCalendar(2006, 4, 0), 15);
		chart.addTarget(new GregorianCalendar(2006, 5, 0), 19);
		chart.addTarget(new GregorianCalendar(2006, 6, 0), 26);
		chart.addTarget(new GregorianCalendar(2006, 7, 0), 29);
		chart.addTarget(new GregorianCalendar(2006, 8, 0), 32);
		chart.addTarget(new GregorianCalendar(2006, 9, 0), 33);
		chart.addTarget(new GregorianCalendar(2006, 10, 0), 42);
		chart.addTarget(new GregorianCalendar(2006, 11, 0), 44);
		chart.addTarget(new GregorianCalendar(2007, 0, 0), 49);
		chart.addTarget(new GregorianCalendar(2007, 1, 0), 61);
		chart.addTarget(new GregorianCalendar(2007, 2, 0), 73);
		chart.addTarget(new GregorianCalendar(2007, 3, 0), 85);
		chart.addTarget(new GregorianCalendar(2007, 4, 0), 97);
		chart.addTarget(new GregorianCalendar(2007, 5, 0), 109);
		chart.addTarget(new GregorianCalendar(2007, 6, 0), 121);
		chart.addTarget(new GregorianCalendar(2007, 7, 0), 133);
		chart.addTarget(new GregorianCalendar(2007, 8, 0), 145);
		chart.addTarget(new GregorianCalendar(2007, 9, 0), 157);
		chart.addTarget(new GregorianCalendar(2007, 10, 0), 169);
		chart.addTarget(new GregorianCalendar(2007, 11, 0), 181);
		chart.addTarget(new GregorianCalendar(2008, 0, 0), 193);
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 205);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 217);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 229);

		return report;
	}

	public static IReport recruitmentInKingsLynnReport(DataSet ds) throws ReportException {

		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(11));

		IReport report =  recruitmentReport(ds, groups, "Kings Lynn");
		report.setTemplate(false);

		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		chart.addTarget(new GregorianCalendar(2007, 8, 0), 1); //Sept 2007
		chart.addTarget(new GregorianCalendar(2007, 9, 0), 2);
		chart.addTarget(new GregorianCalendar(2007, 10, 0), 3);
		chart.addTarget(new GregorianCalendar(2007, 11, 0), 4);
		chart.addTarget(new GregorianCalendar(2008, 0, 0), 5);
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 6);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 7);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 8);

		return report;
	}

	public static IReport recruitmentInSolihullReport(DataSet ds) throws ReportException {

		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(12));

		IReport report =  recruitmentReport(ds, groups, "Solihull");
		report.setTemplate(false);

		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		chart.addTarget(new GregorianCalendar(2007, 7, 0), 1); //August 2007
		chart.addTarget(new GregorianCalendar(2007, 8, 0), 2);
		chart.addTarget(new GregorianCalendar(2007, 9, 0), 3);
		chart.addTarget(new GregorianCalendar(2007, 10, 0), 4);
		chart.addTarget(new GregorianCalendar(2007, 11, 0), 5);
		chart.addTarget(new GregorianCalendar(2008, 0, 0), 6);
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 7);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 8);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 9);

		return report;
	}

	public static IReport recruitmentInCheshireWirralReport(DataSet ds) throws ReportException {

		List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(13));

		IReport report =  recruitmentReport(ds, groups, "Cheshire & Wirral");
		report.setTemplate(false);

		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		chart.addTarget(new GregorianCalendar(2007, 5, 0), 0);
		chart.addTarget(new GregorianCalendar(2007, 6, 0), 0);
		chart.addTarget(new GregorianCalendar(2007, 7, 0), 0);
		chart.addTarget(new GregorianCalendar(2007, 8, 0), 5);
		chart.addTarget(new GregorianCalendar(2007, 9, 0), 10);
		chart.addTarget(new GregorianCalendar(2007, 10, 0), 15);
		chart.addTarget(new GregorianCalendar(2007, 11, 0), 20);
		chart.addTarget(new GregorianCalendar(2008, 0, 0), 25);
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 30);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 35);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 40);


		return report;
	}

	public static IReport cpmRecruitmentReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National EDEN - Recruitment Progress Report");
		report.setEmailAction(RBACAction.ACTION_DR_CRM_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(false);
		report.setTemplate(false);

		//add charts from the reports for each of the hubs
		report.addManagementChart(((IManagementReport)recruitmentInCambridgeReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInPeterboroughReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInCornwallReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInLancashireReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInNorfolkReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInBirminghamReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInKingsLynnReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInSolihullReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInCheshireWirralReport(ds)).getChart(0));

		return report;
	}

	public static IReport ciRecruitmentReport(DataSet ds) throws ReportException {
		IManagementReport report = recruitmentReport(ds, null, "Overview", "Chief Investigator");
		report.setEmailAction(RBACAction.ACTION_DR_CHIEF_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setTemplate(true);


		IRecruitmentProgressChart chart = (IRecruitmentProgressChart)((IManagementReport)report).getChart(0);
		chart.setTargets(new LinkedHashMap<Calendar, Integer>());

		/*
		 * Create the total recruitment targets for the whole NEDEN project
		 */
		chart.addTarget(new GregorianCalendar(2005, 7, 0), 3);		//August 2005
		chart.addTarget(new GregorianCalendar(2005, 8, 0), 11);
		chart.addTarget(new GregorianCalendar(2005, 9, 0), 16);
		chart.addTarget(new GregorianCalendar(2005, 10, 0), 29);
		chart.addTarget(new GregorianCalendar(2005, 11, 0), 34);
		chart.addTarget(new GregorianCalendar(2006, 0, 0), 41);
		chart.addTarget(new GregorianCalendar(2006, 1, 0), 51);
		chart.addTarget(new GregorianCalendar(2006, 2, 0), 58);
		chart.addTarget(new GregorianCalendar(2006, 3, 0), 71);
		chart.addTarget(new GregorianCalendar(2006, 4, 0), 84);
		chart.addTarget(new GregorianCalendar(2006, 5, 0), 97);
		chart.addTarget(new GregorianCalendar(2006, 6, 0), 109);
		chart.addTarget(new GregorianCalendar(2006, 7, 0), 114);
		chart.addTarget(new GregorianCalendar(2006, 8, 0), 161);
		chart.addTarget(new GregorianCalendar(2006, 9, 0), 172);
		chart.addTarget(new GregorianCalendar(2006, 10, 0), 196);
		chart.addTarget(new GregorianCalendar(2006, 11, 0), 219);
		chart.addTarget(new GregorianCalendar(2007, 0, 0), 226);
		chart.addTarget(new GregorianCalendar(2007, 1, 0), 277);
		chart.addTarget(new GregorianCalendar(2007, 2, 0), 326);
		chart.addTarget(new GregorianCalendar(2007, 3, 0), 375);
		chart.addTarget(new GregorianCalendar(2007, 4, 0), 424);
		chart.addTarget(new GregorianCalendar(2007, 5, 0), 473);
		chart.addTarget(new GregorianCalendar(2007, 6, 0), 522);
		chart.addTarget(new GregorianCalendar(2007, 7, 0), 569);
		chart.addTarget(new GregorianCalendar(2007, 8, 0), 616);
		chart.addTarget(new GregorianCalendar(2007, 9, 0), 663);
		chart.addTarget(new GregorianCalendar(2007, 10, 0), 710);
		chart.addTarget(new GregorianCalendar(2007, 11, 0), 757);
		chart.addTarget(new GregorianCalendar(2008, 0, 0), 804);
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 851);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 898);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 943);

		return report;
	}

	public static IReport dupTrendsReportBaseline(DataSet ds) throws ReportException {
		return DUPTrendsReportBaseline(ds, null, "Overview");
	}

	private static IReport DUPTrendsReportBaseline(DataSet ds, List<Group> groups, String hub) throws ReportException {

		Document dup = ds.getDocument(9);
		//check that we have the right document!
		if ( !dup.getName().equals("DUP") ){
			throw new ReportException("This isn't the DUP document!!!");
		}
		DocumentOccurrence dupBaseline = dup.getOccurrence(0);

		return DUPTrendsReport(ds, dupBaseline, groups, hub);
	}

	private static IReport DUPTrendsReport(DataSet ds, DocumentOccurrence docOcc, List<Group> groups, String hub) throws ReportException {

		ITrendsReport dupReport =
			factory.createTrendsReport(ds, "Duration of Untreated Psychosis (DUP) - "+docOcc.getDisplayText()+" - Trends");

		Document dupDoc = ds.getDocument(9);
		//check that we have the right document!
		if ( !dupDoc.getName().equals("DUP") ){
			throw new ReportException("This isn't the DUP document!!!");
		}
		Section mainSec = dupDoc.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		//Summary chart
		ITrendsChart summary = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
		"DUP Trends ("+hub+")");
		summary.setRangeAxisLabel("Days");			//y-axis (will show average number of days for each series type (e.g average referral time))
		dupReport.addChart(summary);

		//display a bar chart with the total cases per month on the same graph
		summary.setShowTotals(true);

		summary = setPreviousSixMonths(summary);
		//get the mean average of the values in the summary
		String summaryType = ITrendsChartRow.SUMMARY_TYPE_MEAN;

		if (groups != null) {
			for (Group g: groups) {
				summary.addGroup(g);
			}
		}

		ITrendsChartRow dup = factory.createTrendsChartRow();
		dup.setLabel("Duration of Untreated Psychosis");
		dup.setSummaryType(summaryType);
		ITrendsChartRow helpSeeking = factory.createTrendsChartRow();
		helpSeeking.setLabel("Help Seeking");
		helpSeeking.setSummaryType(summaryType);
		ITrendsChartRow referral = factory.createTrendsChartRow();
		referral.setLabel("Referral");
		referral.setSummaryType(summaryType);

		summary.addRow(dup);
		summary.addRow(helpSeeking);
		summary.addRow(referral);

		//delay in help-seeking
		ISimpleChartItem s3 = factory.createSimpleChartItem(dupDoc.getEntry(9), docOcc, mainSecOcc);	//8
		helpSeeking.addSeries(s3);

		//delay in help-seeking pathway
		//ISimpleChartItem s4 = factory.createSimpleChartItem(dupDoc.getEntry(10), docOcc, mainSecOcc);
		//average.addSeries(s4);

		//delay within MHS
		ISimpleChartItem s5 = factory.createSimpleChartItem(dupDoc.getEntry(11), docOcc, mainSecOcc);
		referral.addSeries(s5);		//add to s6?

		//delay in reaching EIS
		//ISimpleChartItem s6 = factory.createSimpleChartItem(dupDoc.getEntry(12), docOcc, mainSecOcc);
		//average.addSeries(s6);

		//duration of untreated psychosis
		ISimpleChartItem s7 = factory.createSimpleChartItem(dupDoc.getEntry(13), docOcc, mainSecOcc);
		dup.addSeries(s7);

		return dupReport;
	}


	public static IReport dupHighLowTrendsReport(DataSet ds) throws ReportException {
		return DUPHighLowTrendsReportBaseline(ds, null, "Overview");
	}

	private static IReport DUPHighLowTrendsReportBaseline(DataSet ds, List<Group> groups, String hub) throws ReportException {

		Document dup = ds.getDocument(9);
		//check that we have the right document!
		if ( !dup.getName().equals("DUP") ){
			throw new ReportException("This isn't the DUP document!!!");
		}
		DocumentOccurrence dupBaseline = dup.getOccurrence(0);

		return DUPHighLowTrendsReport(ds, dupBaseline, groups, hub);
	}

	private static IReport DUPHighLowTrendsReport(DataSet ds, DocumentOccurrence docOcc, List<Group> groups, String hub) throws ReportException {

		ITrendsReport dupReport =
			factory.createTrendsReport(ds, "Longest and Shortest Durations of Untreated Psychosis - "+docOcc.getDisplayText()+"");

		Document dupDoc = ds.getDocument(9);
		//check that we have the right document!
		if ( !dupDoc.getName().equals("DUP") ){
			throw new ReportException("This isn't the DUP document!!!");
		}
		Section mainSec = dupDoc.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		//Summary chart
		ITrendsChart summary = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
		"Longest and Shortest DUP ("+hub+")");
		summary.setRangeAxisLabel("Days");			//y-axis (will show average number of days for each series type (e.g average referral time))
		dupReport.addChart(summary);

		summary = setPreviousSixMonths(summary);

		if (groups != null) {
			for (Group g: groups) {
				summary.addGroup(g);
			}
		}

		//show the highest and lowest values in the summary
		ITrendsChartRow duphigh = factory.createTrendsChartRow();
		duphigh.setLabel("Longest DUP");
		duphigh.setSummaryType(ITrendsChartRow.SUMMARY_TYPE_HIGH);
		summary.addRow(duphigh);
		ITrendsChartRow duplow = factory.createTrendsChartRow();
		duplow.setLabel("Shortest DUP");
		duplow.setSummaryType(ITrendsChartRow.SUMMARY_TYPE_LOW);
		summary.addRow(duplow);

		//duration of untreated psychosis
		ISimpleChartItem s1 = factory.createSimpleChartItem(dupDoc.getEntry(13), docOcc, mainSecOcc);
		ISimpleChartItem s2 = factory.createSimpleChartItem(dupDoc.getEntry(13), docOcc, mainSecOcc);

		duphigh.addSeries(s1);
		duplow.addSeries(s2);

		return dupReport;
	}


	public static IReport panssTrendsReportBaseline(DataSet ds) throws ReportException {
		return panssTrendsReportBaseline(ds, null, "Overview");
	}

	public static IReport panssTrendsReport6Months(DataSet ds) throws ReportException {
		return panssTrendsReport6Months(ds, null, "Overview");
	}

	public static IReport panssTrendsReport12Months(DataSet ds) throws ReportException {
		return panssTrendsReport12Months(ds, null, "Overview");
	}

	private static IReport panssTrendsReportBaseline(DataSet ds, List<Group> groups, String hub) throws ReportException {
		return panssTrendsReport(ds, 0, groups, hub);
	}

	private static IReport panssTrendsReport6Months(DataSet ds, List<Group> groups, String hub) throws ReportException {
		return panssTrendsReport(ds, 1, groups, hub);
	}

	private static IReport panssTrendsReport12Months(DataSet ds, List<Group> groups, String hub) throws ReportException {
		return panssTrendsReport(ds, 2, groups, hub);
	}
	private static IReport panssTrendsReport(DataSet ds, int docOccNo, List<Group> groups, String hub) throws ReportException {

		Document panss = ds.getDocument(1);
		//Check that we have the right document!
		if ( !panss.getName().equals("PANSS") ){
			throw new ReportException("This isn't the PANSS document!!!");
		}

		Section posSec = panss.getSection(1);
		SectionOccurrence posSecOcc = posSec.getOccurrence(0);
		Section negSec = panss.getSection(2);
		SectionOccurrence negSecOcc = negSec.getOccurrence(0);
		Section genSec = panss.getSection(3);
		SectionOccurrence genSecOcc = genSec.getOccurrence(0);

		DocumentOccurrence docOcc = panss.getOccurrence(docOccNo);

		//Create a new trends report
		ITrendsReport panssReport =
			factory.createTrendsReport(ds, "PANSS - "+docOcc.getDisplayText() +" Trends");

		//Summary chart
		ITrendsChart summary =
			factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
			"PANSS Summary ("+hub+")");

		summary.setRangeAxisLabel("Total Scores");
		panssReport.addChart(summary);

		//Set chart to display the previous six months
		summary = setPreviousSixMonths(summary);

		//display a bar chart with the total cases per month on the same graph
		//summary.setShowTotals(true);

		//Get the mean average of the values in the summary
		String summaryType = ITrendsChartRow.SUMMARY_TYPE_MEAN;

		if (groups != null) {
			for (Group g: groups) {
				summary.addGroup(g);
			}
		}

		ITrendsChartRow posRow = factory.createTrendsChartRow();
		posRow.setLabel("Positive");
		posRow.setSummaryType(summaryType);
		ITrendsChartRow negRow = factory.createTrendsChartRow();
		negRow.setLabel("Negative");
		negRow.setSummaryType(summaryType);
		ITrendsChartRow genRow = factory.createTrendsChartRow();
		genRow.setLabel("General");
		genRow.setSummaryType(summaryType);

		summary.addRow(posRow);
		summary.addRow(negRow);
		summary.addRow(genRow);

		ISimpleChartItem posTotal = factory.createSimpleChartItem(panss.getEntry(8), docOcc, posSecOcc);
		posRow.addSeries(posTotal);
		ISimpleChartItem negTotal = factory.createSimpleChartItem(panss.getEntry(16), docOcc, negSecOcc);
		negRow.addSeries(negTotal);
		ISimpleChartItem genTotal = factory.createSimpleChartItem(panss.getEntry(34), docOcc, genSecOcc);
		genRow.addSeries(genTotal);

		//Additional chart showing various courses of treatments entered for same records
		ITrendsReport treatmentsReport = (ITrendsReport)panssTreatmentsReport(ds, 0, groups, "");
		panssReport.addChart(treatmentsReport.getChart(0));

		return panssReport;
	}

	private static IReport panssTreatmentsReport(DataSet ds, int docOccNo, List<Group> groups, String title) throws ReportException {

		//Create a treatment chart to sit underneath the panss summary
		Document treatmentDoc = ds.getDocument(22);
		//check that we have the right document!
		if ( !treatmentDoc.getName().equals("TreatmentDocumentation") ){
			throw new ReportException("This isn't the treatment documentation document!!! It's the "+treatmentDoc.getName());
		}

		//Assuming first document only is to be used
		DocumentOccurrence treatmentDocOcc = treatmentDoc.getOccurrence(docOccNo);
		Section treatmentSec = treatmentDoc.getSection(1);
		SectionOccurrence treatmentSecOcc = treatmentSec.getOccurrence(0);

		//Create a new report
		ITrendsReport treatmentReport =
			factory.createTrendsReport(ds, "Treatment types - "+treatmentDocOcc.getDisplayText() +" Report");

		ITrendsGanttChart treatment =
			factory.createTrendsGanttChart(org.psygrid.data.reporting.Chart.CHART_GANTT,
			"Treatments");

		treatmentReport.addChart(treatment);

		//Set chart to the same time period as the panss summary chart
		Calendar curDate = Calendar.getInstance();
		//Generate the dates for the previous six months, based on current date
		Calendar startDate = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH) - 6, 0);
		Calendar endDate   = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), 0);
		treatment.setTimePeriod(startDate, endDate);

		//String treatmentSummaryType = ITrendsGanttChart.SUMMARY_TYPE_GANTT;

		if (groups != null) {
			for (Group g: groups) {
				treatment.addGroup(g);
			}
		}

		//the selection of possible types of treatment (in entry 1)
		List<String> treatmentTypes = new ArrayList<String>();
		OptionEntry optionEntry = (OptionEntry)treatmentDoc.getEntry(1);

		for (int i=0; i < optionEntry.numOptions(); i++) {
			treatmentTypes.add(optionEntry.getOption(i).getDisplayText());
		}

		/*
		 * Each TrendsChartRow must have three SimpleChartItems.
		 * The first specifying the thing to be searched for (which will
		 * form the individual rows of the final chart) and the remaining two
		 * pointing to the start and end dates for it.
		 *
		 * Each TrendsChartRow represents a possible answer to the question
		 * specified in the first SimpleChartItem and its label must be
		 * set to this answer.
		 */
		for (String answer: treatmentTypes) {
			ITrendsChartRow pRow = factory.createTrendsChartRow();
			treatment.addRow(pRow);
			pRow.setLabel(answer);

			ISimpleChartItem type = factory.createSimpleChartItem(treatmentDoc.getEntry(1), treatmentDocOcc, treatmentSecOcc);
			pRow.addSeries(type);

			ISimpleChartItem start = factory.createSimpleChartItem(treatmentDoc.getEntry(4), treatmentDocOcc, treatmentSecOcc);
			pRow.addSeries(start);

			ISimpleChartItem end = factory.createSimpleChartItem(treatmentDoc.getEntry(5), treatmentDocOcc, treatmentSecOcc);
			pRow.addSeries(end);
		}

		return treatmentReport;
	}


	public static IReport pathwaysTrendsReport(DataSet ds) throws ReportException {
		return pathwaysTrendsReport(ds, null, "Overview");
	}

	private static ITrendsReport pathwaysTrendsReport(DataSet ds, List<Group> groups, String hub) throws ReportException {

		Document pathways = ds.getDocument(10);
		//check that we have the right document!
		if ( !pathways.getName().equals("PathwaysToCare") ){
			throw new ReportException("This isn't the pathways to care document!!!");
		}

		//Assuming baseline document only is to be used
		DocumentOccurrence docOcc = pathways.getOccurrence(0);

		Section posSec = pathways.getSection(1);
		SectionOccurrence posSecOcc = posSec.getOccurrence(0);


		//Create a new trends report
		ITrendsReport pathwaysReport =
			factory.createTrendsReport(ds, "Collated Pathways to Care Report");

		//Summary chart
		ITrendsChart summary =
			factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_STACKED_BAR,
			"Collated Pathways to Care ("+hub+")");

		summary.setRangeAxisLabel("People");
		summary.setUsePercentages(true);
		pathwaysReport.addChart(summary);


		//Set chart to display the previous six months
		summary = setPreviousSixMonths(summary);

		//get the total numbers of people taking each pathway
		String summaryType = ITrendsChartRow.SUMMARY_TYPE_COLLATE;

		if (groups != null) {
			for (Group g: groups) {
				summary.addGroup(g);
			}
		}

		//the selection of possible answers to the question (entry 4) 'Who was contacted?'
		List<String> posAnswers = new ArrayList<String>();
		OptionEntry optionEntry = (OptionEntry)pathways.getEntry(4);

		for (int i=0; i < optionEntry.numOptions(); i++) {
			posAnswers.add(optionEntry.getOption(i).getDisplayText());
		}

		for (String answer: posAnswers) {
			ITrendsChartRow pRow = factory.createTrendsChartRow();
			summary.addRow(pRow);
			pRow.setLabel(answer);
			pRow.setSummaryType(summaryType);

			ISimpleChartItem pathwayType = factory.createSimpleChartItem(pathways.getEntry(4), docOcc, posSecOcc);
			pRow.addSeries(pathwayType);
		}

		return pathwaysReport;
	}


	public static IReport gafTrendsReportBaseline(DataSet ds) throws ReportException {
		return gafTrendsReportBaseline(ds, null, "Overview");
	}

	public static IReport gafTrendsReport12Months(DataSet ds) throws ReportException {
		return gafTrendsReport12Months(ds, null, "Overview");
	}

	private static IReport gafTrendsReportBaseline(DataSet ds, List<Group> groups, String hub) throws ReportException {

		Document gaf = ds.getDocument(7);
		//check that we have the right document!
		if ( !gaf.getName().equals("GAF Data Entry Sheet") ){
			throw new ReportException("This isn't the GAF document!!!");
		}
		DocumentOccurrence gafBaseline = gaf.getOccurrence(0);

		return gafTrendsReport(ds, gafBaseline, groups, hub);
	}

	private static IReport gafTrendsReport12Months(DataSet ds, List<Group> groups, String hub) throws ReportException {

		Document gaf = ds.getDocument(7);
		//check that we have the right document!
		if ( !gaf.getName().equals("GAF Data Entry Sheet") ){
			throw new ReportException("This isn't the GAF document!!!");
		}
		DocumentOccurrence gafBaseline = gaf.getOccurrence(1);

		return gafTrendsReport(ds, gafBaseline, groups, hub);
	}

	private static IReport gafTrendsReport(DataSet ds, DocumentOccurrence docOcc, List<Group> groups, String hub) throws ReportException {

		ITrendsReport gafReport =
			factory.createTrendsReport(ds, "GAF - "+docOcc.getDisplayText()+" - Trends");

		Document gaf = ds.getDocument(7);
		//check that we have the right document!
		if ( !gaf.getName().equals("GAF Data Entry Sheet") ){
			throw new ReportException("This isn't the GAF document!!!");
		}
		Section mainSec = gaf.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		//Summary chart
		ITrendsChart summary = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
		"Gaf Trends ("+hub+")");
		summary.setRangeAxisLabel("Scores");			//y-axis (will show average number of days for each series type (e.g average referral time))
		gafReport.addChart(summary);

		//display a bar chart with the total cases per month on the same graph
		summary.setShowTotals(true);

		summary = setPreviousSixMonths(summary);
		//get the mean average of the values in the summary
		String summaryType = ITrendsChartRow.SUMMARY_TYPE_MEAN;

		if (groups != null) {
			for (Group g: groups) {
				summary.addGroup(g);
			}
		}

		ITrendsChartRow total = factory.createTrendsChartRow();
		total.setLabel("Total");
		total.setSummaryType(summaryType);

		ITrendsChartRow symptoms = factory.createTrendsChartRow();
		symptoms.setLabel("Symptoms");
		symptoms.setSummaryType(summaryType);

		ITrendsChartRow disability = factory.createTrendsChartRow();
		disability.setLabel("Disability");
		disability.setSummaryType(summaryType);

		summary.addRow(total);
		summary.addRow(symptoms);
		summary.addRow(disability);

		ISimpleChartItem s1 = factory.createSimpleChartItem(gaf.getEntry(1), docOcc, mainSecOcc);
		total.addSeries(s1);
		ISimpleChartItem s2 = factory.createSimpleChartItem(gaf.getEntry(2), docOcc, mainSecOcc);
		symptoms.addSeries(s2);
		ISimpleChartItem s3 = factory.createSimpleChartItem(gaf.getEntry(3), docOcc, mainSecOcc);
		disability.addSeries(s3);

		return gafReport;
	}



	public static IReport eq5dTrendsReportBaseline(DataSet ds) throws ReportException {
		return eq5dTrendsReportBaseline(ds, null, "Overview");
	}

	public static IReport eq5dTrendsReport12Months(DataSet ds) throws ReportException {
		return eq5dTrendsReport12Months(ds, null, "Overview");
	}

	private static IReport eq5dTrendsReportBaseline(DataSet ds, List<Group> groups, String hub) throws ReportException {
		Document eq5d = ds.getDocument(8);
		//check that we have the right document!
		if ( !eq5d.getName().equals("EQ5D") ){
			throw new ReportException("This isn't the EQ5D document!!!");
		}
		DocumentOccurrence eq5dBaseline = eq5d.getOccurrence(0);
		return eq5dTrendsReport(ds, eq5dBaseline, groups, hub);
	}

	private static IReport eq5dTrendsReport12Months(DataSet ds, List<Group> groups, String hub) throws ReportException {
		Document eq5d = ds.getDocument(8);
		//check that we have the right document!
		if ( !eq5d.getName().equals("EQ5D") ){
			throw new ReportException("This isn't the EQ5D document!!!");
		}
		DocumentOccurrence eq5dBaseline = eq5d.getOccurrence(1);
		return eq5dTrendsReport(ds, eq5dBaseline, groups, hub);
	}

	private static IReport eq5dTrendsReport(DataSet ds, DocumentOccurrence docOcc, List<Group> groups, String hub) throws ReportException {

		ITrendsReport eq5dReport =
			factory.createTrendsReport(ds, "Health Questionnaire EQ5D Trends - "+docOcc.getDisplayText());

		Document eq5d = ds.getDocument(8);
		//check that we have the right document!
		if ( !eq5d.getName().equals("EQ5D") ){
			throw new ReportException("This isn't the EQ5D document!!!");
		}
		Section mainSec = eq5d.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		//Summary chart
		ITrendsChart summary = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
		"Trends for EQ5D ("+hub+")");
		summary.setRangeAxisLabel("Scores");
		eq5dReport.addChart(summary);

		//display a bar chart with the total cases per month on the same graph
		summary.setShowTotals(true);

		summary = setPreviousSixMonths(summary);
		//get the mean average of the values in the summary
		String summaryType = ITrendsChartRow.SUMMARY_TYPE_MEAN;

		if (groups != null) {
			for (Group g: groups) {
				summary.addGroup(g);
			}
		}

		ITrendsChartRow a = factory.createTrendsChartRow();
		a.setLabel("Mobility");
		ITrendsChartRow b = factory.createTrendsChartRow();
		b.setLabel("Self-care");
		ITrendsChartRow c = factory.createTrendsChartRow();
		c.setLabel("Usual Activities");
		ITrendsChartRow d = factory.createTrendsChartRow();
		d.setLabel("Pain/Discomfort");
		ITrendsChartRow e = factory.createTrendsChartRow();
		e.setLabel("Anxiety/Depression");

		summary.addRow(a);
		summary.addRow(b);
		summary.addRow(c);
		summary.addRow(d);
		summary.addRow(e);

		for (int i = 0; i<summary.numRows(); i++) {
			summary.getRow(i).setSummaryType(summaryType);
		}

		ISimpleChartItem p1 = factory.createSimpleChartItem(eq5d.getEntry(1), docOcc, mainSecOcc, "Code", ISimpleChartItem.LABEL_TEXT_ONLY);
		a.addSeries(p1);
		ISimpleChartItem p2 = factory.createSimpleChartItem(eq5d.getEntry(2), docOcc, mainSecOcc, "Code", ISimpleChartItem.LABEL_TEXT_ONLY);
		b.addSeries(p2);
		ISimpleChartItem p3 = factory.createSimpleChartItem(eq5d.getEntry(3), docOcc, mainSecOcc, "Code", ISimpleChartItem.LABEL_TEXT_ONLY);
		c.addSeries(p3);
		ISimpleChartItem p4 = factory.createSimpleChartItem(eq5d.getEntry(4), docOcc, mainSecOcc, "Code", ISimpleChartItem.LABEL_TEXT_ONLY);
		d.addSeries(p4);
		ISimpleChartItem p5 = factory.createSimpleChartItem(eq5d.getEntry(5), docOcc, mainSecOcc, "Code", ISimpleChartItem.LABEL_TEXT_ONLY);
		e.addSeries(p5);

		return eq5dReport;
	}



	public static IReport calgaryScaleTrendsReportBaseline(DataSet ds) throws ReportException {
		return calgaryScaleTrendsReportBaseline(ds, null, "Overview");
	}
	public static IReport calgaryScaleTrendsReport6Months(DataSet ds) throws ReportException {
		return calgaryScaleTrendsReport6Months(ds, null, "Overview");
	}
	public static IReport calgaryScaleTrendsReport12Months(DataSet ds) throws ReportException {
		return calgaryScaleTrendsReport12Months(ds, null, "Overview");
	}

	private static IReport calgaryScaleTrendsReportBaseline(DataSet ds, List<Group> groups, String hub) throws ReportException {

		Document calgaryScale = ds.getDocument(4);
		//check that we have the right document!
		if ( !calgaryScale.getName().equals("Calgary") ){
			throw new ReportException("This isn't the Calgary document!!!");
		}
		DocumentOccurrence calgaryScaleBaseline = calgaryScale.getOccurrence(0);

		return calgaryScaleTrendsReport(ds, calgaryScaleBaseline, groups, hub);
	}

	private static IReport calgaryScaleTrendsReport6Months(DataSet ds, List<Group> groups, String hub) throws ReportException {

		Document calgaryScale = ds.getDocument(4);
		//check that we have the right document!
		if ( !calgaryScale.getName().equals("Calgary") ){
			throw new ReportException("This isn't the Calgary document!!!");
		}
		DocumentOccurrence calgaryScaleBaseline = calgaryScale.getOccurrence(1);

		return calgaryScaleTrendsReport(ds, calgaryScaleBaseline, groups, hub);
	}

	private static IReport calgaryScaleTrendsReport12Months(DataSet ds, List<Group> groups, String hub) throws ReportException {

		Document calgaryScale = ds.getDocument(4);
		//check that we have the right document!
		if ( !calgaryScale.getName().equals("Calgary") ){
			throw new ReportException("This isn't the Calgary document!!!");
		}
		DocumentOccurrence calgaryScaleBaseline = calgaryScale.getOccurrence(2);

		return calgaryScaleTrendsReport(ds, calgaryScaleBaseline, groups, hub);
	}

	private static IReport calgaryScaleTrendsReport(DataSet ds, DocumentOccurrence docOcc, List<Group> groups, String hub) throws ReportException {

		ITrendsReport calgaryScaleReport =
			factory.createTrendsReport(ds, "Calgary Depression for Schizophrenia Scale Trends - "+docOcc.getDisplayText());

		Document calgaryScale = ds.getDocument(4);
		//check that we have the right document!
		if ( !calgaryScale.getName().equals("Calgary") ){
			throw new ReportException("This isn't the Calgary document!!!");
		}
		Section mainSec = calgaryScale.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		//Summary chart
		ITrendsChart summary = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
		"Calgary Depression for Schizophrenia Scale Trends ("+hub+")");
		summary.setRangeAxisLabel("Scores");
		calgaryScaleReport.addChart(summary);

		//display a bar chart with the total number of records per month on the same graph
		summary.setShowTotals(true);

		summary = setPreviousSixMonths(summary);
		//get the mean average of the values in the summary
		String summaryType = ITrendsChartRow.SUMMARY_TYPE_MEAN;

		if (groups != null) {
			for (Group g: groups) {
				summary.addGroup(g);
			}
		}
		ITrendsChartRow a = factory.createTrendsChartRow();
		a.setLabel("1");
		ITrendsChartRow b = factory.createTrendsChartRow();
		b.setLabel("2");
		ITrendsChartRow c = factory.createTrendsChartRow();
		c.setLabel("3");
		ITrendsChartRow d = factory.createTrendsChartRow();
		d.setLabel("4");
		ITrendsChartRow e = factory.createTrendsChartRow();
		e.setLabel("5");
		ITrendsChartRow f = factory.createTrendsChartRow();
		f.setLabel("6");
		ITrendsChartRow g = factory.createTrendsChartRow();
		g.setLabel("7");
		ITrendsChartRow h = factory.createTrendsChartRow();
		h.setLabel("8");
		ITrendsChartRow i = factory.createTrendsChartRow();
		i.setLabel("9");

		summary.addRow(a);
		summary.addRow(b);
		summary.addRow(c);
		summary.addRow(d);
		summary.addRow(e);
		summary.addRow(f);
		summary.addRow(g);
		summary.addRow(h);
		summary.addRow(i);

		for (int row = 0; row<summary.numRows(); row++) {
			summary.getRow(row).setSummaryType(summaryType);
		}

		ISimpleChartItem p1 = factory.createSimpleChartItem(calgaryScale.getEntry(1), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		a.addSeries(p1);
		ISimpleChartItem p2 = factory.createSimpleChartItem(calgaryScale.getEntry(2), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		b.addSeries(p2);
		ISimpleChartItem p3 = factory.createSimpleChartItem(calgaryScale.getEntry(3), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		c.addSeries(p3);
		ISimpleChartItem p4 = factory.createSimpleChartItem(calgaryScale.getEntry(4), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		d.addSeries(p4);
		ISimpleChartItem p5 = factory.createSimpleChartItem(calgaryScale.getEntry(5), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		e.addSeries(p5);
		ISimpleChartItem p6 = factory.createSimpleChartItem(calgaryScale.getEntry(6), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		f.addSeries(p6);
		ISimpleChartItem p7 = factory.createSimpleChartItem(calgaryScale.getEntry(7), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		g.addSeries(p7);
		ISimpleChartItem p8 = factory.createSimpleChartItem(calgaryScale.getEntry(8), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		h.addSeries(p8);
		ISimpleChartItem p9 = factory.createSimpleChartItem(calgaryScale.getEntry(9), docOcc, mainSecOcc, null, ISimpleChartItem.LABEL_LABEL_ONLY);
		i.addSeries(p9);


		//Show the average total scores across the months
		ITrendsChart total = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
		"Total Scores Summary ("+hub+")");
		total.setRangeAxisLabel("Mean Scores");
		calgaryScaleReport.addChart(total);

		total = setPreviousSixMonths(total);
		//get the mean average of the values in the summary
		String totalSummaryType = ITrendsChartRow.SUMMARY_TYPE_MEAN;

		if (groups != null) {
			for (Group grp: groups) {
				total.addGroup(grp);
			}
		}

		ITrendsChartRow row = factory.createTrendsChartRow();
		row.setLabel("Total Score");
		row.setSummaryType(totalSummaryType);
		total.addRow(row);
		ISimpleChartItem item = factory.createSimpleChartItem(calgaryScale.getEntry(10), docOcc, mainSecOcc);
		row.addSeries(item);

		return calgaryScaleReport;
	}


	public static IReport youngManiaTrendsReportBaseline(DataSet ds) throws ReportException {
		return youngManiaTrendsReportBaseline(ds, null, "Overview");
	}

	public static IReport youngManiaTrendsReport6Months(DataSet ds) throws ReportException {
		return youngManiaTrendsReport6Months(ds, null, "Overview");
	}

	public static IReport youngManiaTrendsReport12Months(DataSet ds) throws ReportException {
		return youngManiaTrendsReport12Months(ds, null, "Overview");
	}

	private static IReport youngManiaTrendsReportBaseline(DataSet ds, List<Group> groups, String hub) throws ReportException {
		Document youngMania = ds.getDocument(2);
		//check that we have the right document!
		if ( !youngMania.getName().equals("Young Mania") ){
			throw new ReportException("This isn't the Young Mania document!!!");
		}
		DocumentOccurrence ymBaseline = youngMania.getOccurrence(0);

		return youngManiaTrendsReport(ds, ymBaseline, groups, hub);
	}

	private static IReport youngManiaTrendsReport6Months(DataSet ds, List<Group> groups, String hub) throws ReportException {
		Document youngMania = ds.getDocument(2);
		//check that we have the right document!
		if ( !youngMania.getName().equals("Young Mania") ){
			throw new ReportException("This isn't the Young Mania document!!!");
		}
		DocumentOccurrence ymBaseline = youngMania.getOccurrence(1);

		return youngManiaTrendsReport(ds, ymBaseline, groups, hub);
	}

	private static IReport youngManiaTrendsReport12Months(DataSet ds, List<Group> groups, String hub) throws ReportException {
		Document youngMania = ds.getDocument(2);
		//check that we have the right document!
		if ( !youngMania.getName().equals("Young Mania") ){
			throw new ReportException("This isn't the Young Mania document!!!");
		}
		DocumentOccurrence ymBaseline = youngMania.getOccurrence(2);

		return youngManiaTrendsReport(ds, ymBaseline, groups, hub);
	}

	private static IReport youngManiaTrendsReport(DataSet ds, DocumentOccurrence docOcc, List<Group> groups, String hub) throws ReportException {

		ITrendsReport ymReport =
			factory.createTrendsReport(ds, "Young Mania Trends - "+docOcc.getDisplayText());

		Document youngMania = ds.getDocument(2);
		//check that we have the right document!
		if ( !youngMania.getName().equals("Young Mania") ){
			throw new ReportException("This isn't the Young Mania document!!!");
		}
		Section mainSec = youngMania.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		//Summary chart
		ITrendsChart scores = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
		"Young Mania Trends Scores ("+hub+")");
		scores.setRangeAxisLabel("Scores");
		ymReport.addChart(scores);

		//display a bar chart with the total number of records per month on the same graph
		scores.setShowTotals(true);

		scores = setPreviousSixMonths(scores);
		//get the mean average of the values in the summary
		String summaryType = ITrendsChartRow.SUMMARY_TYPE_MEAN;

		if (groups != null) {
			for (Group g: groups) {
				scores.addGroup(g);
			}
		}

		ITrendsChartRow a = factory.createTrendsChartRow();
		a.setLabel("Elevated Mood");
		ITrendsChartRow b = factory.createTrendsChartRow();
		b.setLabel("Increased Motor Activity");
		ITrendsChartRow c = factory.createTrendsChartRow();
		c.setLabel("Sexual Interest");
		ITrendsChartRow d = factory.createTrendsChartRow();
		d.setLabel("Sleep");
		ITrendsChartRow e = factory.createTrendsChartRow();
		e.setLabel("Irritability");
		ITrendsChartRow f = factory.createTrendsChartRow();
		f.setLabel("Speech (Rate and Amount)");
		ITrendsChartRow g = factory.createTrendsChartRow();
		g.setLabel("Language - Thought Disorder");
		ITrendsChartRow h = factory.createTrendsChartRow();
		h.setLabel("Content");
		ITrendsChartRow i = factory.createTrendsChartRow();
		i.setLabel("Disruptive Aggressive");
		ITrendsChartRow j = factory.createTrendsChartRow();
		j.setLabel("Appearance");
		ITrendsChartRow k = factory.createTrendsChartRow();
		k.setLabel("Insight");

		scores.addRow(a);
		scores.addRow(b);
		scores.addRow(c);
		scores.addRow(d);
		scores.addRow(e);
		scores.addRow(f);
		scores.addRow(g);
		scores.addRow(h);
		scores.addRow(i);
		scores.addRow(j);
		scores.addRow(k);

		for (int row = 0; row < scores.numRows(); row++) {
			scores.getRow(row).setSummaryType(summaryType);
		}

		ISimpleChartItem s1 = factory.createSimpleChartItem(youngMania.getEntry(1), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		a.addSeries(s1);
		ISimpleChartItem s2 = factory.createSimpleChartItem(youngMania.getEntry(2), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		b.addSeries(s2);
		ISimpleChartItem s3 = factory.createSimpleChartItem(youngMania.getEntry(3), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		c.addSeries(s3);
		ISimpleChartItem s4 = factory.createSimpleChartItem(youngMania.getEntry(4), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		d.addSeries(s4);
		ISimpleChartItem s5 = factory.createSimpleChartItem(youngMania.getEntry(5), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		e.addSeries(s5);
		ISimpleChartItem s6 = factory.createSimpleChartItem(youngMania.getEntry(6), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		f.addSeries(s6);
		ISimpleChartItem s7 = factory.createSimpleChartItem(youngMania.getEntry(7), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		g.addSeries(s7);
		ISimpleChartItem s8 = factory.createSimpleChartItem(youngMania.getEntry(8), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		h.addSeries(s8);
		ISimpleChartItem s9 = factory.createSimpleChartItem(youngMania.getEntry(9), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		i.addSeries(s9);
		ISimpleChartItem s10 = factory.createSimpleChartItem(youngMania.getEntry(10), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		j.addSeries(s10);
		ISimpleChartItem s11 = factory.createSimpleChartItem(youngMania.getEntry(11), docOcc, mainSecOcc, IOptionValue.OPTION_CODE);
		k.addSeries(s11);



		//Show average total scores across the months
		ITrendsChart summary = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
		"Total Scores Summary ("+hub+")");
		summary.setRangeAxisLabel("Mean Scores");
		ymReport.addChart(summary);

		summary = setPreviousSixMonths(summary);
		//get the mean average of the values in the summary
		String totalSummaryType = ITrendsChartRow.SUMMARY_TYPE_MEAN;

		if (groups != null) {
			for (Group grp: groups) {
				summary.addGroup(grp);
			}
		}

		ITrendsChartRow row = factory.createTrendsChartRow();
		row.setLabel("Total Score");
		row.setSummaryType(totalSummaryType);
		summary.addRow(row);

		ISimpleChartItem total = factory.createSimpleChartItem(youngMania.getEntry(12), docOcc, mainSecOcc);
		row.addSeries(total);

		return ymReport;
	}


	public static IReport insightScaleTrendsReportBaseline(DataSet ds) throws ReportException {
		return insightScaleTrendsReportBaseline(ds, null, "Overview");
	}

	public static IReport insightScaleTrendsReport12Months(DataSet ds) throws ReportException {
		return insightScaleTrendsReport12Months(ds, null, "Overview");
	}

	private static IReport insightScaleTrendsReportBaseline(DataSet ds, List<Group> groups, String hub) throws ReportException {

		Document insight = ds.getDocument(3);
		//check that we have the right document!
		if ( !insight.getName().equals("Insight Scale Scoring") ){
			throw new ReportException("This isn't the Insight Scale document!!!");
		}
		DocumentOccurrence insightBaseline = insight.getOccurrence(0);

		return insightScaleTrendsReport(ds, insightBaseline, groups, hub);
	}

	private static IReport insightScaleTrendsReport12Months(DataSet ds, List<Group> groups, String hub) throws ReportException {

		Document insight = ds.getDocument(3);
		//check that we have the right document!
		if ( !insight.getName().equals("Insight Scale Scoring") ){
			throw new ReportException("This isn't the Insight Scale document!!!");
		}
		DocumentOccurrence insightBaseline = insight.getOccurrence(1);

		return insightScaleTrendsReport(ds, insightBaseline, groups, hub);
	}

	private static IReport insightScaleTrendsReport(DataSet ds, DocumentOccurrence docOcc, List<Group> groups, String hub) throws ReportException {

		ITrendsReport insightScaleReport =
			factory.createTrendsReport(ds, "Insight Scale Trends - "+docOcc.getDisplayText());

		Document insight = ds.getDocument(3);
		//check that we have the right document!
		if ( !insight.getName().equals("Insight Scale Scoring") ){
			throw new ReportException("This isn't the Insight Scale document!!!");
		}
		Section mainSec = insight.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		//Summary chart
		ITrendsChart scores = factory.createTrendsChart(org.psygrid.data.reporting.Chart.CHART_TIME_SERIES,
		"Insight Scale Trends Scores ("+hub+")");
		scores.setRangeAxisLabel("Scores");
		insightScaleReport.addChart(scores);

		//display a bar chart with the total number of records per month on the same graph
		scores.setShowTotals(true);

		scores = setPreviousSixMonths(scores);
		//get the mean average of the values in the summary
		String summaryType = ITrendsChartRow.SUMMARY_TYPE_MEAN;

		if (groups != null) {
			for (Group g: groups) {
				scores.addGroup(g);
			}
		}

		ITrendsChartRow a = factory.createTrendsChartRow();
		a.setLabel("Awareness of symptoms");
		ITrendsChartRow b = factory.createTrendsChartRow();
		b.setLabel("Awareness of illness");
		ITrendsChartRow c = factory.createTrendsChartRow();
		c.setLabel("Need for treatment");
		ITrendsChartRow d = factory.createTrendsChartRow();
		d.setLabel("Total");

		scores.addRow(a);
		scores.addRow(b);
		scores.addRow(c);
		scores.addRow(d);

		for (int row = 0; row < scores.numRows(); row++) {
			scores.getRow(row).setSummaryType(summaryType);
		}

		ISimpleChartItem s1 = factory.createSimpleChartItem(insight.getEntry(9), docOcc, mainSecOcc);
		a.addSeries(s1);
		ISimpleChartItem s2 = factory.createSimpleChartItem(insight.getEntry(10), docOcc, mainSecOcc);
		b.addSeries(s2);
		ISimpleChartItem s3 = factory.createSimpleChartItem(insight.getEntry(11), docOcc, mainSecOcc);
		c.addSeries(s3);
		ISimpleChartItem s4 = factory.createSimpleChartItem(insight.getEntry(13), docOcc, mainSecOcc);
		d.addSeries(s4);

		return insightScaleReport;
	}

	/**
	 * Create a record completion status report showing the status of each study point
	 * in a dataset for all records in the selected groups.
	 *
	 * Gives an overview of the status of each record and therefore of a trial's progress.
	 *
	 * @param ds the dataset
	 * @param groups to create the report for
	 * @param hub name of the NHS hub the report is for
	 * @return report
	 * @throws ReportException
	 */
	public static IReport recordStatusReport(DataSet ds, List<Group> groups, String hub) throws ReportException {
		IManagementReport report = recordStatusReport(ds, groups, hub, "PrincipalInvestigator");
		report.setEmailAction(RBACAction.ACTION_DR_CRM_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_STATUS_REPORT);
		return report;
	}

	private static IManagementReport recordStatusReport(DataSet ds, List<Group> groups, String hub, String role) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National Eden - Record Status Report");
		//report.setRole(role);
		report.setWithRawData(true);
		report.setTemplate(true);
		report.setFrequency(ReportFrequency.NEVER);

		/*
		 * Create a tabular chart showing the records in the groups provided and their current status
		 */
		IRecordStatusChart chart = factory.createRecordStatusChart(Chart.CHART_TABLE, "Records "+hub);
		chart.setRangeAxisLabel("");	//y-axis label

		//check
		if (groups != null) {
			for (Group g: groups) {
				chart.addGroup(g);
			}
		}
		report.addManagementChart(chart);

		return report;
	}

	/**
	 * Create a document completion status report showing the status of each document instance
	 * in a dataset for all records in the selected groups.
	 *
	 * Gives an overview of the status of each record and therefore of a trial's progress.
	 *
	 * @param ds the dataset
	 * @param groups to create the report for
	 * @param hub name of the NHS hub the report is for
	 * @return report
	 * @throws ReportException
	 */
	public static IReport documentStatusReport(DataSet ds, List<Group> groups, String hub) throws ReportException {
		IManagementReport report = documentStatusReport(ds, groups, hub, "PrincipalInvestigator");
		report.setEmailAction(RBACAction.ACTION_DR_CRM_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_STATUS_REPORT);
		return report;
	}

	private static IManagementReport documentStatusReport(DataSet ds, List<Group> groups, String hub, String role) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National Eden - Document Status Report");
		//report.setRole(role);
		report.setWithRawData(true);
		report.setTemplate(true);
		report.setFrequency(ReportFrequency.NEVER);

		/*
		 * Create a tabular chart showing the records in the groups provided and their current status
		 */
		IDocumentStatusChart chart = factory.createDocumentStatusChart(Chart.CHART_TABLE, "Documents "+hub);
		chart.setRangeAxisLabel("");	//y-axis label

		//check
		if (groups != null) {
			for (Group g: groups) {
				chart.addGroup(g);
			}
		}
		report.addManagementChart(chart);

		return report;
	}

	/**
	 * Create a document collection date report showing the date of data collection for each
	 * document instance in a dataset for all records in the selected groups.
	 *
	 * @param ds the dataset
	 * @param groups to create the report for
	 * @param hub name of the NHS hub the report is for
	 * @return report
	 * @throws ReportException
	 */
	public static IReport collectionDateReport(DataSet ds, List<Group> groups, String hub) throws ReportException {
		return collectionDateReport(ds, groups, hub, "PrincipalInvestigator");
	}

	private static IReport collectionDateReport(DataSet ds, List<Group> groups, String hub, String role) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National Eden - Document Collection Date Report");
		//report.setRole(role);
		report.setWithRawData(true);
		report.setTemplate(true);
		report.setFrequency(ReportFrequency.NEVER);

		/*
		 * Create a tabular chart showing the records in the groups provided and their current status
		 */
		ICollectionDateChart chart = factory.createCollectionDateChart(Chart.CHART_TABLE, "Documents "+hub);
		chart.setRangeAxisLabel("");	//y-axis label

		//check
		if (groups != null) {
			for (Group g: groups) {
				chart.addGroup(g);
			}
		}

		//add the documents/entries where date of collection is specified explicitly
		Document panss = ds.getDocument(1);
		if ( !"PANSS".equals(panss.getName()) ){
			throw new RuntimeException("This is not the PANSS document, it is "+panss.getName());
		}
		Entry panssDate = panss.getEntry(36);
		if ( !"Date of assesssment".equals(panssDate.getName()) ){
			throw new RuntimeException("This is not the Date of assesssment entry, it is "+panssDate.getName());
		}
		chart.addCollectionDateEntry(panss.getName(), new Integer(36));

		Document ym = ds.getDocument(2);
		if ( !"Young Mania".equals(ym.getName()) ){
			throw new RuntimeException("This is not the Young Mania document, it is "+ym.getName());
		}
		Entry ymDate = ym.getEntry(13);
		if ( !"Date of assesssment".equals(ymDate.getName()) ){
			throw new RuntimeException("This is not the Date of assesssment entry, it is "+ymDate.getName());
		}
		chart.addCollectionDateEntry(ym.getName(), new Integer(13));

		Document insight = ds.getDocument(3);
		if ( !"Insight Scale Scoring".equals(insight.getName()) ){
			throw new RuntimeException("This is not the Insight Scale Scoring document, it is "+insight.getName());
		}
		Entry insightDate = insight.getEntry(14);
		if ( !"Date of assesssment".equals(insightDate.getName()) ){
			throw new RuntimeException("This is not the Date of assesssment entry, it is "+insightDate.getName());
		}
		chart.addCollectionDateEntry(insight.getName(), new Integer(14));

		Document calgary = ds.getDocument(4);
		if ( !"Calgary".equals(calgary.getName()) ){
			throw new RuntimeException("This is not the Calgary document, it is "+calgary.getName());
		}
		Entry calgaryDate = calgary.getEntry(11);
		if ( !"Date of assesssment".equals(calgaryDate.getName()) ){
			throw new RuntimeException("This is not the Date of assesssment entry, it is "+calgaryDate.getName());
		}
		chart.addCollectionDateEntry(calgary.getName(), new Integer(11));

		Document gaf = ds.getDocument(7);
		if ( !"GAF Data Entry Sheet".equals(gaf.getName()) ){
			throw new RuntimeException("This is not the GAF Data Entry Sheet document, it is "+gaf.getName());
		}
		Entry gafDate = gaf.getEntry(4);
		if ( !"Date of assesssment".equals(gafDate.getName()) ){
			throw new RuntimeException("This is not the Date of assesssment entry, it is "+gafDate.getName());
		}
		chart.addCollectionDateEntry(gaf.getName(), new Integer(4));

		report.addManagementChart(chart);

		return report;
	}

	public static IReport stdCodeStatusReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, "National Eden - Standard Codes Usage Report");
		report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(true);
		report.setTemplate(true);
		report.setFrequency(ReportFrequency.NEVER);

		/*
		 * Create a chart showing percentage occurrence of std codes for entries in the documents specified for the records in the
		 * groups provided
		 */
		IStdCodeStatusChart pansschart2 = factory.createStdCodeStatusChart(Chart.CHART_BAR,
				"Document Percentage Usage");
		pansschart2.setUsePercentages(true);
		pansschart2.setPerDocument(true);
		pansschart2.setRangeAxisLabel("");	//y-axis label

		report.addManagementChart(pansschart2);


		/*
		 * Create a chart showing percentage occurrence of std codes for entries in the documents specified for the records in the
		 * groups provided
		 */
		IStdCodeStatusChart pansschart = factory.createStdCodeStatusChart(Chart.CHART_TABLE,
				"Question Percentage Usage");
		pansschart.setUsePercentages(true);
		pansschart.setPerEntry(true);
		pansschart.setRangeAxisLabel("");	//y-axis label

		report.addManagementChart(pansschart);


		/*
		 * Create a chart showing usage of std codes for entries in the documents specified for the records in the
		 * groups provided
		 */
		IStdCodeStatusChart pansschart1 = factory.createStdCodeStatusChart(Chart.CHART_TABLE,
				"Usage Per Patient");
		pansschart1.setUsePercentages(false);
		pansschart1.setRangeAxisLabel("");	//y-axis label

		report.addManagementChart(pansschart1);



		return report;
	}

	public static IReport basicStatisticsReport(DataSet ds) throws ReportException{

		IManagementReport report = factory.createManagementReport(ds, "National Eden - Basic Statistics Report");
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(false);
		report.setTemplate(true);
		report.setFrequency(ReportFrequency.NEVER);

		/*
		 * Create a chart showing percentage occurrence of std codes for entries in the documents specified for the records in the
		 * groups provided
		 */
		IBasicStatisticsChart basicStatsChart = factory.createBasicStatisticsChart(Chart.CHART_TABLE,
				"Statistics");
		basicStatsChart.setUsePercentages(false);
		basicStatsChart.setRangeAxisLabel("");	//y-axis label

		report.addChart(basicStatsChart);

		return report;
	}

	/**
	 * Add a time period to a given trends chart, for the previous six
	 * months based on the current date.
	 *
	 * @param chart
	 * @return chart
	 */
	private static ITrendsChart setPreviousSixMonths(ITrendsChart chart) {

		//generate the dates for the previous six months, based on current date
		Calendar curDate = Calendar.getInstance();
		//+1 to the months because Calendar's months start at 0
		Calendar startDate = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH) - 5, 0);
		Calendar endDate   = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), 0);

		chart.setTimePeriod(startDate, endDate);
		return chart;
	}
}