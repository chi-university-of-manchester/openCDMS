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


package org.psygrid.drn.address;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.Chart;
import org.psygrid.data.reporting.definition.Factory;
import org.psygrid.data.reporting.definition.IBasicStatisticsChart;
import org.psygrid.data.reporting.definition.ICollectionDateChart;
import org.psygrid.data.reporting.definition.IDocumentStatusChart;
import org.psygrid.data.reporting.definition.IEslChartItem;
import org.psygrid.data.reporting.definition.IGroupsSummaryChart;
import org.psygrid.data.reporting.definition.IManagementReport;
import org.psygrid.data.reporting.definition.IProjectSummaryChart;
import org.psygrid.data.reporting.definition.IRecordChart;
import org.psygrid.data.reporting.definition.IRecordReport;
import org.psygrid.data.reporting.definition.IRecordStatusChart;
import org.psygrid.data.reporting.definition.IRecruitmentProgressChart;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.reporting.definition.ISimpleChartItem;
import org.psygrid.data.reporting.definition.IUKCRNSummaryChart;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.reporting.definition.ReportFrequency;
import org.psygrid.data.reporting.definition.hibernate.HibernateFactory;
import org.psygrid.security.RBACAction;

/**
 * @author Rob Harper
 *
 */
public class Reports {

	public static Factory factory = new HibernateFactory();

	public static IReport baselineReport(DataSet ds) throws Exception {

		IRecordReport report =
			factory.createRecordReport(ds, "Baseline");

		//Summary chart
		IRecordChart chart =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
			"");
		chart.setRangeAxisLabel("Value");
		report.addChart(chart);


		Document demographics = ds.getDocument(0);
		//check that we have the right document!
		if ( !"Demographics".equals(demographics.getName()) ){
			throw new Exception("This isn't the Demographics document, it is "+demographics.getName());
		}
		DocumentOccurrence demoOcc = demographics.getOccurrence(0);
		SectionOccurrence demoSecOcc = demographics.getSection(0).getOccurrence(0);

		Document diabetes = ds.getDocument(1);
		//check that we have the right document!
		if ( !"Diabetes and Medical Details".equals(diabetes.getName()) ){
			throw new Exception("This isn't the Diabetes and Medical Details document, it is "+diabetes.getName());
		}
		DocumentOccurrence diabOcc = diabetes.getOccurrence(0);
		SectionOccurrence diabGenSecOcc = diabetes.getSection(0).getOccurrence(0);
		SectionOccurrence diabClassSecOcc = diabetes.getSection(1).getOccurrence(0);
		SectionOccurrence diabHistSecOcc = diabetes.getSection(2).getOccurrence(0);
		SectionOccurrence diabMedSecOcc = diabetes.getSection(3).getOccurrence(0);
		SectionOccurrence diabCommSecOcc = diabetes.getSection(4).getOccurrence(0);

		Document familyHistory = ds.getDocument(2);
		//check that we have the right document!
		if ( !"Family History".equals(familyHistory.getName()) ){
			throw new Exception("This isn't the Family History document, it is "+familyHistory.getName());
		}
		DocumentOccurrence histOcc = familyHistory.getOccurrence(0);
		SectionOccurrence histSecOcc = familyHistory.getSection(0).getOccurrence(0);

		Document clinicalMeas = ds.getDocument(3);
		//check that we have the right document!
		if ( !"Clinical Measurements".equals(clinicalMeas.getName()) ){
			throw new Exception("This isn't the Clinical Measurements document, it is "+clinicalMeas.getName());
		}
		DocumentOccurrence clinOcc = clinicalMeas.getOccurrence(0);
		SectionOccurrence clinMeasSecOcc = clinicalMeas.getSection(0).getOccurrence(0);

		Document biochemistry = ds.getDocument(4);
		//check that we have the right document!
		if ( !"Biochemistry and Urinalysis".equals(biochemistry.getName()) ){
			throw new Exception("This isn't the Biochemistry and Urinalysis document, it is "+biochemistry.getName());
		}
		DocumentOccurrence bioOcc = biochemistry.getOccurrence(0);
		SectionOccurrence bioAntiSecOcc = biochemistry.getSection(0).getOccurrence(0);
		SectionOccurrence bioElecSecOcc = biochemistry.getSection(1).getOccurrence(0);
		SectionOccurrence bioLipidSecOcc = biochemistry.getSection(2).getOccurrence(0);
		SectionOccurrence bioHbSecOcc = biochemistry.getSection(3).getOccurrence(0);
		SectionOccurrence bioLiverSecOcc = biochemistry.getSection(4).getOccurrence(0);
		SectionOccurrence bioGlucSecOcc = biochemistry.getSection(5).getOccurrence(0);
		SectionOccurrence bioThySecOcc = biochemistry.getSection(6).getOccurrence(0);
		SectionOccurrence bioAlbSecOcc = biochemistry.getSection(7).getOccurrence(0);

		chart.addItem(factory.createSimpleChartItem(demographics.getEntry(2), demoOcc, demoSecOcc));
		chart.addItem(factory.createSimpleChartItem(demographics.getEntry(3), demoOcc, demoSecOcc));
		chart.addItem(factory.createSimpleChartItem(demographics.getEntry(4), demoOcc, demoSecOcc));
		chart.addItem(factory.createSimpleChartItem(demographics.getEntry(5), demoOcc, demoSecOcc));
		chart.addItem(factory.createSimpleChartItem(demographics.getEntry(1), demoOcc, demoSecOcc));

		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(0), diabOcc, diabGenSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(1), diabOcc, diabGenSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(2), diabOcc, diabClassSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(3), diabOcc, diabClassSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(4), diabOcc, diabHistSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(5), diabOcc, diabHistSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(6), diabOcc, diabHistSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(7), diabOcc, diabHistSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(8), diabOcc, diabMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(9), diabOcc, diabMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(10), diabOcc, diabMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(11), diabOcc, diabMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(12), diabOcc, diabMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(13), diabOcc, diabMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(14), diabOcc, diabMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(15), diabOcc, diabMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(16), diabOcc, diabMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(17), diabOcc, diabMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(18), diabOcc, diabMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(19), diabOcc, diabCommSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(20), diabOcc, diabCommSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabetes.getEntry(21), diabOcc, diabCommSecOcc));

		chart.addItem(factory.createSimpleChartItem(familyHistory.getEntry(0), histOcc, histSecOcc));
		chart.addItem(factory.createSimpleChartItem(familyHistory.getEntry(1), histOcc, histSecOcc));
		chart.addItem(factory.createSimpleChartItem(familyHistory.getEntry(2), histOcc, histSecOcc));
		chart.addItem(factory.createSimpleChartItem(familyHistory.getEntry(3), histOcc, histSecOcc));
		chart.addItem(factory.createSimpleChartItem(familyHistory.getEntry(4), histOcc, histSecOcc));
		chart.addItem(factory.createSimpleChartItem(familyHistory.getEntry(5), histOcc, histSecOcc));
		chart.addItem(factory.createSimpleChartItem(familyHistory.getEntry(6), histOcc, histSecOcc));

		chart.addItem(factory.createSimpleChartItem(clinicalMeas.getEntry(1), clinOcc, clinMeasSecOcc));
		chart.addItem(factory.createSimpleChartItem(clinicalMeas.getEntry(2), clinOcc, clinMeasSecOcc));
		chart.addItem(factory.createSimpleChartItem(clinicalMeas.getEntry(4), clinOcc, clinMeasSecOcc));
		chart.addItem(factory.createSimpleChartItem(clinicalMeas.getEntry(6), clinOcc, clinMeasSecOcc));
		chart.addItem(factory.createSimpleChartItem(clinicalMeas.getEntry(7), clinOcc, clinMeasSecOcc));
		chart.addItem(factory.createSimpleChartItem(clinicalMeas.getEntry(9), clinOcc, clinMeasSecOcc));
		chart.addItem(factory.createSimpleChartItem(clinicalMeas.getEntry(11), clinOcc, clinMeasSecOcc));
		chart.addItem(factory.createSimpleChartItem(clinicalMeas.getEntry(12), clinOcc, clinMeasSecOcc));

		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(1), bioOcc, bioAntiSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(2), bioOcc, bioAntiSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(3), bioOcc, bioAntiSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(4), bioOcc, bioAntiSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(5), bioOcc, bioAntiSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(6), bioOcc, bioAntiSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(7), bioOcc, bioAntiSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(8), bioOcc, bioAntiSecOcc));

		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(10), bioOcc, bioElecSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(11), bioOcc, bioElecSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(12), bioOcc, bioElecSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(13), bioOcc, bioElecSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(14), bioOcc, bioElecSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(15), bioOcc, bioElecSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(16), bioOcc, bioElecSecOcc));

		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(18), bioOcc, bioLipidSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(19), bioOcc, bioLipidSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(20), bioOcc, bioLipidSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(21), bioOcc, bioLipidSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(22), bioOcc, bioLipidSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(23), bioOcc, bioLipidSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(24), bioOcc, bioLipidSecOcc));

		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(26), bioOcc, bioHbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(27), bioOcc, bioHbSecOcc));

		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(29), bioOcc, bioLiverSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(30), bioOcc, bioLiverSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(31), bioOcc, bioLiverSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(32), bioOcc, bioLiverSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(33), bioOcc, bioLiverSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(34), bioOcc, bioLiverSecOcc));

		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(36), bioOcc, bioGlucSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(37), bioOcc, bioGlucSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(38), bioOcc, bioGlucSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(39), bioOcc, bioGlucSecOcc));

		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(41), bioOcc, bioThySecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(42), bioOcc, bioThySecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(43), bioOcc, bioThySecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(44), bioOcc, bioThySecOcc));

		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(81), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(48), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(49), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(51), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(52), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(53), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(55), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(56), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(57), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(58), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(59), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(60), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(61), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(63), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(64), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(65), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(66), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(67), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(68), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(70), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(71), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(72), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(73), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(74), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(75), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(76), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(78), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(79), bioOcc, bioAlbSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(80), bioOcc, bioAlbSecOcc));

		return report;
	}


	public static IReport sixMonthFollowUp(DataSet ds) throws Exception {

		IRecordReport report =
			factory.createRecordReport(ds, "6 Month Follow Up");

		createFollowUpReport(ds, report, 0);

		return report;
	}

	public static IReport oneYearFollowUp(DataSet ds) throws Exception {

		IRecordReport report =
			factory.createRecordReport(ds, "1 Year Follow Up");

		createFollowUpReport(ds, report, 1);

		return report;
	}

	public static IReport twoYearFollowUp(DataSet ds) throws Exception {

		IRecordReport report =
			factory.createRecordReport(ds, "2 Year Follow Up");

		createFollowUpReport(ds, report, 2);

		return report;
	}

	public static IReport threeYearFollowUp(DataSet ds) throws Exception {

		IRecordReport report =
			factory.createRecordReport(ds, "3 Year Follow Up");

		createFollowUpReport(ds, report, 3);

		return report;
	}

	public static IReport fourYearFollowUp(DataSet ds) throws Exception {

		IRecordReport report =
			factory.createRecordReport(ds, "4 Year Follow Up");

		createFollowUpReport(ds, report, 4);

		return report;
	}

	public static IReport fiveYearFollowUp(DataSet ds) throws Exception {

		IRecordReport report =
			factory.createRecordReport(ds, "5 Year Follow Up");

		createFollowUpReport(ds, report, 5);

		return report;
	}

	private static void createFollowUpReport(DataSet ds, IRecordReport report, int docOccIndex) throws Exception {

		IRecordChart chart =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
			"");
		chart.setRangeAxisLabel("Value");
		report.addChart(chart);

		Document medClinMeas = ds.getDocument(5);
		//check that we have the right document!
		if ( !"Medication and Clinical Measurements".equals(medClinMeas.getName()) ){
			throw new Exception("This isn't the Medication and Clinical Measurements document, it is "+medClinMeas.getName());
		}
		DocumentOccurrence medClinMeasOcc = medClinMeas.getOccurrence(docOccIndex);
		SectionOccurrence mcmGenSecOcc = medClinMeas.getSection(0).getOccurrence(0);
		SectionOccurrence mcmDiabSecOcc = medClinMeas.getSection(1).getOccurrence(0);
		SectionOccurrence mcmMedSecOcc = medClinMeas.getSection(2).getOccurrence(0);
		SectionOccurrence mcmNonDiabetesMedSecOcc = medClinMeas.getSection(3).getOccurrence(0);
		SectionOccurrence mcmClinSecOcc = medClinMeas.getSection(4).getOccurrence(0);

		Document biochemistry = ds.getDocument(6);
		//check that we have the right document!
		if ( !"Biochemistry".equals(biochemistry.getName()) ){
			throw new Exception("This isn't the Biochemistry document, it is "+biochemistry.getName());
		}
		DocumentOccurrence biochemOcc = biochemistry.getOccurrence(docOccIndex);
		SectionOccurrence bioElecSecOcc = biochemistry.getSection(0).getOccurrence(0);
		SectionOccurrence bioLipidSecOcc = biochemistry.getSection(1).getOccurrence(0);
		SectionOccurrence bioHbaSecOcc = biochemistry.getSection(2).getOccurrence(0);
		SectionOccurrence bioLiverSecOcc = biochemistry.getSection(3).getOccurrence(0);
		SectionOccurrence bioGlucSecOcc = biochemistry.getSection(4).getOccurrence(0);
		SectionOccurrence bioThySecOcc = biochemistry.getSection(5).getOccurrence(0);

		Document diabComp = ds.getDocument(7);
		//check that we have the right document!
		if ( !"Diabetes complications".equals(diabComp.getName()) ){
			throw new Exception("This isn't the Diabetes complications document, it is "+diabComp.getName());
		}
		DocumentOccurrence diabCompOcc = diabComp.getOccurrence(docOccIndex);
		SectionOccurrence diabCompRetSecOcc = diabComp.getSection(0).getOccurrence(0);
		SectionOccurrence diabCompNephSecOcc = diabComp.getSection(1).getOccurrence(0);
		SectionOccurrence diabCompNeurSecOcc = diabComp.getSection(2).getOccurrence(0);

		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(0), medClinMeasOcc, mcmGenSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(1), medClinMeasOcc, mcmGenSecOcc));

		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(2), medClinMeasOcc, mcmDiabSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(3), medClinMeasOcc, mcmDiabSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(4), medClinMeasOcc, mcmDiabSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(5), medClinMeasOcc, mcmDiabSecOcc));

		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(6), medClinMeasOcc, mcmMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(7), medClinMeasOcc, mcmMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(8), medClinMeasOcc, mcmMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(9), medClinMeasOcc, mcmMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(10), medClinMeasOcc, mcmMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(11), medClinMeasOcc, mcmMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(12), medClinMeasOcc, mcmMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(13), medClinMeasOcc, mcmMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(14), medClinMeasOcc, mcmMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(15), medClinMeasOcc, mcmMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(16), medClinMeasOcc, mcmMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(17), medClinMeasOcc, mcmMedSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(18), medClinMeasOcc, mcmMedSecOcc));

		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(34), medClinMeasOcc, mcmNonDiabetesMedSecOcc));

		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(21), medClinMeasOcc, mcmClinSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(22), medClinMeasOcc, mcmClinSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(24), medClinMeasOcc, mcmClinSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(26), medClinMeasOcc, mcmClinSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(27), medClinMeasOcc, mcmClinSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(29), medClinMeasOcc, mcmClinSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(31), medClinMeasOcc, mcmClinSecOcc));
		chart.addItem(factory.createSimpleChartItem(medClinMeas.getEntry(32), medClinMeasOcc, mcmClinSecOcc));

		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(1), biochemOcc, bioElecSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(2), biochemOcc, bioElecSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(3), biochemOcc, bioElecSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(4), biochemOcc, bioElecSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(5), biochemOcc, bioElecSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(6), biochemOcc, bioElecSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(7), biochemOcc, bioElecSecOcc));

		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(9), biochemOcc, bioLipidSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(10), biochemOcc, bioLipidSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(11), biochemOcc, bioLipidSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(12), biochemOcc, bioLipidSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(13), biochemOcc, bioLipidSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(15), biochemOcc, bioLipidSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(14), biochemOcc, bioLipidSecOcc));

		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(17), biochemOcc, bioHbaSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(18), biochemOcc, bioHbaSecOcc));

		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(20), biochemOcc, bioLiverSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(21), biochemOcc, bioLiverSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(22), biochemOcc, bioLiverSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(23), biochemOcc, bioLiverSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(24), biochemOcc, bioLiverSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(25), biochemOcc, bioLiverSecOcc));

		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(27), biochemOcc, bioGlucSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(28), biochemOcc, bioGlucSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(29), biochemOcc, bioGlucSecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(30), biochemOcc, bioGlucSecOcc));

		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(32), biochemOcc, bioThySecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(33), biochemOcc, bioThySecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(34), biochemOcc, bioThySecOcc));
		chart.addItem(factory.createSimpleChartItem(biochemistry.getEntry(35), biochemOcc, bioThySecOcc));

		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(0), diabCompOcc, diabCompRetSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(1), diabCompOcc, diabCompRetSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(2), diabCompOcc, diabCompRetSecOcc));

		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(39), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(6), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(7), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(9), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(10), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(11), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(13), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(14), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(15), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(16), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(17), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(18), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(19), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(21), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(22), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(23), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(24), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(25), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(26), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(28), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(29), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(30), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(31), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(32), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(33), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(34), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(36), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(37), diabCompOcc, diabCompNephSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(38), diabCompOcc, diabCompNephSecOcc));

		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(41), diabCompOcc, diabCompNeurSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(42), diabCompOcc, diabCompNeurSecOcc));
		chart.addItem(factory.createSimpleChartItem(diabComp.getEntry(43), diabCompOcc, diabCompNeurSecOcc));

	}

	public static IReport contactInfo(DataSet ds) throws Exception {

		IRecordReport report =
			factory.createRecordReport(ds, "Participant Contact Information");

		Document demographics = ds.getDocument(0);
		//check that we have the right document!
		if ( !"Demographics".equals(demographics.getName()) ){
			throw new Exception("This isn't the Demographics document, it is "+demographics.getName());
		}

		DocumentOccurrence baseline = demographics.getOccurrence(0);

		Section mainSec = demographics.getSection(0);
		SectionOccurrence mainSecOcc = mainSec.getOccurrence(0);

		//Summary chart
		IRecordChart chart =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
			"Participant Contact Information");
		chart.setRangeAxisLabel("Value");
		report.addChart(chart);

		IEslChartItem studyNumber = factory.createEslChartItem("studyNumber");
		chart.addItem(studyNumber);

		IEslChartItem centreNumber = factory.createEslChartItem("centreNumber");
		chart.addItem(centreNumber);

		IEslChartItem title = factory.createEslChartItem("title");
		chart.addItem(title);

		IEslChartItem firstName = factory.createEslChartItem("firstName");
		chart.addItem(firstName);

		IEslChartItem lastName = factory.createEslChartItem("lastName");
		chart.addItem(lastName);

		IEslChartItem sex = factory.createEslChartItem("sex");
		chart.addItem(sex);

		IEslChartItem dob = factory.createEslChartItem("dateOfBirth");
		chart.addItem(dob);

		IEslChartItem address1 = factory.createEslChartItem("address1");
		chart.addItem(address1);

		IEslChartItem address2 = factory.createEslChartItem("address2");
		chart.addItem(address2);

		IEslChartItem address3 = factory.createEslChartItem("address3");
		chart.addItem(address3);

		IEslChartItem town = factory.createEslChartItem("city");
		chart.addItem(town);

		IEslChartItem county = factory.createEslChartItem("region");
		chart.addItem(county);

		IEslChartItem country = factory.createEslChartItem("country");
		chart.addItem(country);

		IEslChartItem postCode = factory.createEslChartItem("postCode");
		chart.addItem(postCode);

		IEslChartItem emailAddress = factory.createEslChartItem("emailAddress");
		chart.addItem(emailAddress);

		IEslChartItem telNo = factory.createEslChartItem("homePhone");
		chart.addItem(telNo);

		IEslChartItem workPhone = factory.createEslChartItem("workPhone");
		chart.addItem(workPhone);

		IEslChartItem mobilePhone = factory.createEslChartItem("mobilePhone");
		chart.addItem(mobilePhone);

		IEslChartItem nhsNo = factory.createEslChartItem("nhsNumber");
		chart.addItem(nhsNo);

		IEslChartItem hospitalNumber = factory.createEslChartItem("hospitalNumber");
		chart.addItem(hospitalNumber);

		IEslChartItem riskIssues = factory.createEslChartItem("riskIssues");
		chart.addItem(riskIssues);

		return report;

	}

	public static IReport gpDetails(DataSet ds) throws Exception {

		IRecordReport report =
			factory.createRecordReport(ds, "GP Information");

		Document demographics = ds.getDocument(0);
		//check that we have the right document!
		if ( !"Demographics".equals(demographics.getName()) ){
			throw new Exception("This isn't the Demographics document, it is "+demographics.getName());
		}
		DocumentOccurrence blDemographics = demographics.getOccurrence(0);
		Section demoMainSec = demographics.getSection(0);
		SectionOccurrence demoMainSecOcc = demoMainSec.getOccurrence(0);

		Document gpDetails = ds.getDocument(8);
		//check that we have the right document!
		if ( !"GP Details".equals(gpDetails.getName()) ){
			throw new Exception("This isn't the GP Details document, it is "+gpDetails.getName());
		}
		DocumentOccurrence blGpDetails = gpDetails.getOccurrence(0);
		Section gpMainSec = gpDetails.getSection(0);
		SectionOccurrence gpMainSecOcc = gpMainSec.getOccurrence(0);

		//Summary chart
		IRecordChart chart =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
			"GP Information");
		chart.setRangeAxisLabel("Value");
		report.addChart(chart);

		ISimpleChartItem dob = factory.createSimpleChartItem(gpDetails.getEntry(0), blGpDetails, gpMainSecOcc);
		chart.addItem(dob);

		IEslChartItem title = factory.createEslChartItem("title");
		chart.addItem(title);

		IEslChartItem firstName = factory.createEslChartItem("firstName");
		chart.addItem(firstName);

		IEslChartItem lastName = factory.createEslChartItem("lastName");
		chart.addItem(lastName);

		IEslChartItem nhsNo = factory.createEslChartItem("nhsNumber");
		chart.addItem(nhsNo);

		ISimpleChartItem gender = factory.createSimpleChartItem(demographics.getEntry(2), blDemographics, demoMainSecOcc);
		chart.addItem(gender);

		ISimpleChartItem dob2 = factory.createSimpleChartItem(demographics.getEntry(3), blDemographics, demoMainSecOcc);
		chart.addItem(dob2);

		ISimpleChartItem dateAddEntered = factory.createSimpleChartItem(gpDetails.getEntry(1), blGpDetails, gpMainSecOcc);
		chart.addItem(dateAddEntered);

		ISimpleChartItem gpName = factory.createSimpleChartItem(gpDetails.getEntry(2), blGpDetails, gpMainSecOcc);
		chart.addItem(gpName);

		ISimpleChartItem gpAddress1 = factory.createSimpleChartItem(gpDetails.getEntry(3), blGpDetails, gpMainSecOcc);
		chart.addItem(gpAddress1);

		ISimpleChartItem gpAddress2 = factory.createSimpleChartItem(gpDetails.getEntry(4), blGpDetails, gpMainSecOcc);
		chart.addItem(gpAddress2);

		ISimpleChartItem gpAddress3 = factory.createSimpleChartItem(gpDetails.getEntry(5), blGpDetails, gpMainSecOcc);
		chart.addItem(gpAddress3);

		ISimpleChartItem gpTown = factory.createSimpleChartItem(gpDetails.getEntry(6), blGpDetails, gpMainSecOcc);
		chart.addItem(gpTown);

		ISimpleChartItem gpCounty = factory.createSimpleChartItem(gpDetails.getEntry(7), blGpDetails, gpMainSecOcc);
		chart.addItem(gpCounty);

		ISimpleChartItem gpPostcode = factory.createSimpleChartItem(gpDetails.getEntry(8), blGpDetails, gpMainSecOcc);
		chart.addItem(gpPostcode);

		ISimpleChartItem gpTelNo = factory.createSimpleChartItem(gpDetails.getEntry(9), blGpDetails, gpMainSecOcc);
		chart.addItem(gpTelNo);

		ISimpleChartItem gpFaxNo = factory.createSimpleChartItem(gpDetails.getEntry(10), blGpDetails, gpMainSecOcc);
		chart.addItem(gpFaxNo);

		ISimpleChartItem pracNurse = factory.createSimpleChartItem(gpDetails.getEntry(11), blGpDetails, gpMainSecOcc);
		chart.addItem(pracNurse);

		return report;

	}

	public static IReport transfers(DataSet ds) throws Exception {

		IRecordReport report =
			factory.createRecordReport(ds, "Site Transfers");

		Document transfer = ds.getDocument(10);
		//check that we have the right document!
		if ( !"Site Transfer".equals(transfer.getName()) ){
			throw new Exception("This isn't the Site Transfer document, it is "+transfer.getName());
		}
		DocumentOccurrence blTransfer = transfer.getOccurrence(0);
		Section transferMainSec = transfer.getSection(0);
		SectionOccurrence transferMainSecOcc = transferMainSec.getOccurrence(0);

		IRecordChart chart =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
			"Site Transfers");
		chart.setRangeAxisLabel("Value");
		report.addChart(chart);

		ISimpleChartItem dob = factory.createSimpleChartItem(transfer.getEntry(0), blTransfer, transferMainSecOcc);
		chart.addItem(dob);

		ISimpleChartItem prevSiteNumber = factory.createSimpleChartItem(transfer.getEntry(1), blTransfer, transferMainSecOcc);
		chart.addItem(prevSiteNumber);

		ISimpleChartItem newSiteNumber = factory.createSimpleChartItem(transfer.getEntry(2), blTransfer, transferMainSecOcc);
		chart.addItem(newSiteNumber);

		ISimpleChartItem dateOfTransfer = factory.createSimpleChartItem(transfer.getEntry(3), blTransfer, transferMainSecOcc);
		chart.addItem(dateOfTransfer);

		return report;
	}

	public static IReport withdrawals(DataSet ds) throws Exception {

		IRecordReport report =
			factory.createRecordReport(ds, "Withdrawals");

		Document termination = ds.getDocument(11);
		//check that we have the right document!
		if ( !"Termination".equals(termination.getName()) ){
			throw new Exception("This isn't the Termination document, it is "+termination.getName());
		}
		DocumentOccurrence blTermination = termination.getOccurrence(0);
		Section terminationMainSec = termination.getSection(0);
		SectionOccurrence terminationMainSecOcc = terminationMainSec.getOccurrence(0);


		IRecordChart chart =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
			"Withdrawals");
		chart.setRangeAxisLabel("Value");
		report.addChart(chart);

		ISimpleChartItem dob = factory.createSimpleChartItem(termination.getEntry(0), blTermination, terminationMainSecOcc);
		chart.addItem(dob);

		ISimpleChartItem dateTermination = factory.createSimpleChartItem(termination.getEntry(1), blTermination, terminationMainSecOcc);
		chart.addItem(dateTermination);

		ISimpleChartItem reasonTermination = factory.createSimpleChartItem(termination.getEntry(2), blTermination, terminationMainSecOcc);
		chart.addItem(reasonTermination);

		ISimpleChartItem commentsTermination = factory.createSimpleChartItem(termination.getEntry(3), blTermination, terminationMainSecOcc);
		chart.addItem(commentsTermination);

		ISimpleChartItem requestAnonDataRemoved = factory.createSimpleChartItem(termination.getEntry(4), blTermination, terminationMainSecOcc);
		chart.addItem(requestAnonDataRemoved);

		return report;
	}

	public static IReport participantsContacted(DataSet ds) throws Exception {

		IRecordReport report =
			factory.createRecordReport(ds, "Participants Contacted");

		Document participantsContacted = ds.getDocument(9);
		//check that we have the right document!
		if ( !"Participant Contacted".equals(participantsContacted.getName()) ){
			throw new Exception("This isn't the Participant Contacted document, it is "+participantsContacted.getName());
		}
		DocumentOccurrence blPartCont = participantsContacted.getOccurrence(0);
		Section contactSec = participantsContacted.getSection(0);
		SectionOccurrence contactSecOcc = contactSec.getOccurrence(0);

		IRecordChart chart =
			factory.createSimpleChart(org.psygrid.data.reporting.Chart.CHART_TABLE,
			"Participants Contacted");
		chart.setRangeAxisLabel("Value");
		report.addChart(chart);

		ISimpleChartItem dob = factory.createSimpleChartItem(participantsContacted.getEntry(0), blPartCont, contactSecOcc);
		chart.addItem(dob);

		ISimpleChartItem study = factory.createSimpleChartItem(participantsContacted.getEntry(1), blPartCont, contactSecOcc);
		chart.addItem(study);

		ISimpleChartItem date = factory.createSimpleChartItem(participantsContacted.getEntry(2), blPartCont, contactSecOcc);
		chart.addItem(date);

		ISimpleChartItem response = factory.createSimpleChartItem(participantsContacted.getEntry(3), blPartCont, contactSecOcc);
		chart.addItem(response);

		ISimpleChartItem recruited = factory.createSimpleChartItem(participantsContacted.getEntry(4), blPartCont, contactSecOcc);
		chart.addItem(recruited);

		return report;
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

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Record Status Report");
		//report.setRole(role);
		report.setWithRawData(true);
		report.setTemplate(true);
		report.setFrequency(ReportFrequency.NEVER);

		/*
		 * Create a tabular chart showing the records in the groups provided and their current status
		 */
		IRecordStatusChart chart = factory.createRecordStatusChart(Chart.CHART_TABLE, "Record "+hub);

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

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Document Status Report");
		//report.setRole(role);
		report.setWithRawData(true);
		report.setTemplate(true);
		report.setFrequency(ReportFrequency.NEVER);

		/*
		 * Create a tabular chart showing the records in the groups provided and their current status
		 */
		IDocumentStatusChart chart = factory.createDocumentStatusChart(Chart.CHART_TABLE, "Document "+hub);

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

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Document Collection Date Report");
		report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
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

		report.addManagementChart(chart);

		return report;
	}

	public static IReport basicStatisticsReport(DataSet ds) throws ReportException{

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Basic Statistics Report");
		//report.setAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
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


   public static IReport cpmMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Recruitment Manager Report");
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

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "North East and Cumbria");
        chrt2.addGroup(ds.getGroup(0));
        chrt2.addGroup(ds.getGroup(1));
        chrt2.addGroup(ds.getGroup(2));
        chrt2.addGroup(ds.getGroup(3));
        chrt2.addGroup(ds.getGroup(4));
        chrt2.addGroup(ds.getGroup(44));
        chrt2.addGroup(ds.getGroup(45));
        chrt2.addGroup(ds.getGroup(46));
        chrt2.addGroup(ds.getGroup(47));
        chrt2.addGroup(ds.getGroup(48));
        chrt2.addGroup(ds.getGroup(51));
        chrt2.addGroup(ds.getGroup(61));
        chrt2.addGroup(ds.getGroup(64));
        chrt2.addGroup(ds.getGroup(65));
        chrt2.addGroup(ds.getGroup(66));
        chrt2.addGroup(ds.getGroup(67));
        chrt2.addGroup(ds.getGroup(68));

        report.addChart(chrt2);

        IGroupsSummaryChart chrt3 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "North West England");
        chrt3.addGroup(ds.getGroup(5));
        chrt3.addGroup(ds.getGroup(24));
        chrt3.addGroup(ds.getGroup(29));
        chrt3.addGroup(ds.getGroup(33));
        chrt3.addGroup(ds.getGroup(35));
        chrt3.addGroup(ds.getGroup(36));
        chrt3.addGroup(ds.getGroup(37));
        chrt3.addGroup(ds.getGroup(42));
        chrt3.addGroup(ds.getGroup(50));
        chrt3.addGroup(ds.getGroup(55));
        chrt3.addGroup(ds.getGroup(56));
        chrt3.addGroup(ds.getGroup(57));
        chrt3.addGroup(ds.getGroup(59));
        chrt3.addGroup(ds.getGroup(60));
        chrt3.addGroup(ds.getGroup(62));
        chrt3.addGroup(ds.getGroup(63));
        chrt3.addGroup(ds.getGroup(73));
        chrt3.addGroup(ds.getGroup(74));
        chrt3.addGroup(ds.getGroup(75));
        report.addChart(chrt3);

        IGroupsSummaryChart chrt4 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "North West London");
        chrt4.addGroup(ds.getGroup(6));
        chrt4.addGroup(ds.getGroup(7));
        chrt4.addGroup(ds.getGroup(19));
        chrt4.addGroup(ds.getGroup(20));
        chrt4.addGroup(ds.getGroup(21));
        chrt4.addGroup(ds.getGroup(22));
        chrt4.addGroup(ds.getGroup(23));
        chrt4.addGroup(ds.getGroup(38));
        report.addChart(chrt4);

        IGroupsSummaryChart chrt5 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "South West Peninsula");
        chrt5.addGroup(ds.getGroup(8));
        chrt5.addGroup(ds.getGroup(9));
        chrt5.addGroup(ds.getGroup(10));
        chrt5.addGroup(ds.getGroup(11));
        chrt5.addGroup(ds.getGroup(76));
        report.addChart(chrt5);

        IGroupsSummaryChart chrt6 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Thames Valley");
        chrt6.addGroup(ds.getGroup(12));
        chrt6.addGroup(ds.getGroup(25));
        chrt6.addGroup(ds.getGroup(26));
        chrt6.addGroup(ds.getGroup(31));
        chrt6.addGroup(ds.getGroup(39));
        chrt6.addGroup(ds.getGroup(40));
        chrt6.addGroup(ds.getGroup(41));
        chrt6.addGroup(ds.getGroup(49));
        chrt6.addGroup(ds.getGroup(53)); //This should be 'University Hospitals Coventry'
        report.addChart(chrt6);

        IGroupsSummaryChart chrt7 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Eastern England");
        chrt7.addGroup(ds.getGroup(13));
        chrt7.addGroup(ds.getGroup(32));
        chrt7.addGroup(ds.getGroup(52));
        chrt7.addGroup(ds.getGroup(58));
        report.addChart(chrt7);

        IGroupsSummaryChart chrt8 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "North East London");
        chrt8.addGroup(ds.getGroup(14));
        chrt8.addGroup(ds.getGroup(15));
        chrt8.addGroup(ds.getGroup(16));
        chrt8.addGroup(ds.getGroup(17));
        chrt8.addGroup(ds.getGroup(27));
        chrt8.addGroup(ds.getGroup(28));
        chrt8.addGroup(ds.getGroup(54)); //Should be 'Barking, Havering and Redbridge Hospitals NHS Trust'
        chrt8.addGroup(ds.getGroup(70));
        chrt8.addGroup(ds.getGroup(72));
        report.addChart(chrt8);

        IGroupsSummaryChart chrt9 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "South East Midlands");
        chrt9.addGroup(ds.getGroup(18));
        chrt9.addGroup(ds.getGroup(43));
        chrt9.addGroup(ds.getGroup(71));
        report.addChart(chrt9);

        //because of deprecated groups we need to explicitly set the groups on this report;
		//otherwise it will assume that the deprecated groups should be included
		int numGroups = ds.numGroups();
		for ( int i=0; i<numGroups; i++ ){
			Group curGroup = ds.getGroup(i);
			if (!(curGroup.getLongName().equals("DEPRECATED"))) {
				report.addGroup(curGroup);
			}
		}

        return report;
    }

    public static IReport ciMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Chief Investigator Report");
        report.setEmailAction(RBACAction.ACTION_DR_CHIEF_INVESTIGATOR_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.setWithRawData(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);

		IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
		report.addChart(chrt1);
		return report;
    }

    public static IReport piNorthEastCumbriaMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Principal Investigator (North East and Cumbria) Report");
        report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.addGroup(ds.getGroup(0));
        report.addGroup(ds.getGroup(1));
        report.addGroup(ds.getGroup(2));
        report.addGroup(ds.getGroup(3));
        report.addGroup(ds.getGroup(4));
        report.addGroup(ds.getGroup(44));
        report.addGroup(ds.getGroup(45));
        report.addGroup(ds.getGroup(46));
        report.addGroup(ds.getGroup(47));
        report.addGroup(ds.getGroup(48));
        report.addGroup(ds.getGroup(51));
        report.addGroup(ds.getGroup(61));
        report.addGroup(ds.getGroup(64));
        report.addGroup(ds.getGroup(65));
        report.addGroup(ds.getGroup(66));
        report.addGroup(ds.getGroup(67));
        report.addGroup(ds.getGroup(68));

        report.setWithRawData(false);
        report.setTemplate(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
        IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        report.addChart(chrt1);

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "North East and Cumbria");
        chrt2.addGroup(ds.getGroup(0));
        chrt2.addGroup(ds.getGroup(1));
        chrt2.addGroup(ds.getGroup(2));
        chrt2.addGroup(ds.getGroup(3));
        chrt2.addGroup(ds.getGroup(4));
        chrt2.addGroup(ds.getGroup(44));
        chrt2.addGroup(ds.getGroup(45));
        chrt2.addGroup(ds.getGroup(44));
        chrt2.addGroup(ds.getGroup(47));
        chrt2.addGroup(ds.getGroup(48));
        chrt2.addGroup(ds.getGroup(51));
        chrt2.addGroup(ds.getGroup(61));
        chrt2.addGroup(ds.getGroup(64));
        chrt2.addGroup(ds.getGroup(65));
        chrt2.addGroup(ds.getGroup(66));
        chrt2.addGroup(ds.getGroup(67));
        chrt2.addGroup(ds.getGroup(68));
        report.addChart(chrt2);

        return report;
    }


    public static IReport piNorthWestMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Principal Investigator (North West England) Report");
        report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.addGroup(ds.getGroup(5));
        report.addGroup(ds.getGroup(24));
        report.addGroup(ds.getGroup(29));
        report.addGroup(ds.getGroup(33));
        report.addGroup(ds.getGroup(35));
        report.addGroup(ds.getGroup(36));
        report.addGroup(ds.getGroup(37));
        report.addGroup(ds.getGroup(42));
        report.addGroup(ds.getGroup(50));
        report.addGroup(ds.getGroup(55));
        report.addGroup(ds.getGroup(56));
        report.addGroup(ds.getGroup(57));
        report.addGroup(ds.getGroup(59));
        report.addGroup(ds.getGroup(60));
        report.addGroup(ds.getGroup(62));
        report.addGroup(ds.getGroup(63));
        report.addGroup(ds.getGroup(73));
        report.addGroup(ds.getGroup(74));
        report.addGroup(ds.getGroup(75));
        report.setWithRawData(false);
        report.setTemplate(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
        IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        report.addChart(chrt1);

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "North West England");
        chrt2.addGroup(ds.getGroup(5));
        chrt2.addGroup(ds.getGroup(24));
        chrt2.addGroup(ds.getGroup(29));
        chrt2.addGroup(ds.getGroup(33));
        chrt2.addGroup(ds.getGroup(35));
        chrt2.addGroup(ds.getGroup(36));
        chrt2.addGroup(ds.getGroup(37));
        chrt2.addGroup(ds.getGroup(42));
        chrt2.addGroup(ds.getGroup(50));
        chrt2.addGroup(ds.getGroup(55));
        chrt2.addGroup(ds.getGroup(56));
        chrt2.addGroup(ds.getGroup(57));
        chrt2.addGroup(ds.getGroup(59));
        chrt2.addGroup(ds.getGroup(60));
        chrt2.addGroup(ds.getGroup(62));
        chrt2.addGroup(ds.getGroup(63));
        chrt2.addGroup(ds.getGroup(73));
        chrt2.addGroup(ds.getGroup(74));
        chrt2.addGroup(ds.getGroup(75));
        report.addChart(chrt2);

        return report;
    }


    public static IReport piNorthWestLondonMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Principal Investigator (North West London) Report");
        report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.addGroup(ds.getGroup(6));
        report.addGroup(ds.getGroup(7));
        report.addGroup(ds.getGroup(19));
        report.addGroup(ds.getGroup(20));
        report.addGroup(ds.getGroup(21));
        report.addGroup(ds.getGroup(22));
        report.addGroup(ds.getGroup(23));
        report.addGroup(ds.getGroup(38));
        report.setWithRawData(false);
        report.setTemplate(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
        IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        report.addChart(chrt1);

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "North West London");
        chrt2.addGroup(ds.getGroup(6));
        chrt2.addGroup(ds.getGroup(7));
        chrt2.addGroup(ds.getGroup(19));
        chrt2.addGroup(ds.getGroup(20));
        chrt2.addGroup(ds.getGroup(21));
        chrt2.addGroup(ds.getGroup(22));
        chrt2.addGroup(ds.getGroup(23));
        chrt2.addGroup(ds.getGroup(38));
        report.addChart(chrt2);

        return report;
    }


    public static IReport piSouthWestMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Principal Investigator (South West Peninsula) Report");
        report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.addGroup(ds.getGroup(8));
        report.addGroup(ds.getGroup(9));
        report.addGroup(ds.getGroup(10));
        report.addGroup(ds.getGroup(11));
        report.addGroup(ds.getGroup(76));
        report.setWithRawData(false);
        report.setTemplate(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
        IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        report.addChart(chrt1);

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "South West Peninsula");
        chrt2.addGroup(ds.getGroup(8));
        chrt2.addGroup(ds.getGroup(9));
        chrt2.addGroup(ds.getGroup(10));
        chrt2.addGroup(ds.getGroup(11));
        chrt2.addGroup(ds.getGroup(76));
        report.addChart(chrt2);

        return report;
    }


    public static IReport piThamesValleyMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Principal Investigator (Thames Valley) Report");
        report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.addGroup(ds.getGroup(12));
        report.addGroup(ds.getGroup(25));
        report.addGroup(ds.getGroup(26));
        report.addGroup(ds.getGroup(31));
        report.addGroup(ds.getGroup(39));
        report.addGroup(ds.getGroup(40));
        report.addGroup(ds.getGroup(41));
        report.addGroup(ds.getGroup(49));
        report.addGroup(ds.getGroup(53)); //This should be 'University Hospitals Coventry'
        report.setWithRawData(false);
        report.setTemplate(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
        IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        report.addChart(chrt1);

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Thames Valley");
        chrt2.addGroup(ds.getGroup(12));
        chrt2.addGroup(ds.getGroup(25));
        chrt2.addGroup(ds.getGroup(26));
        chrt2.addGroup(ds.getGroup(31));
        chrt2.addGroup(ds.getGroup(39));
        chrt2.addGroup(ds.getGroup(40));
        chrt2.addGroup(ds.getGroup(41));
        chrt2.addGroup(ds.getGroup(49));
        chrt2.addGroup(ds.getGroup(53)); //This should be 'University Hospitals Coventry'
        report.addChart(chrt2);

        return report;
    }


    public static IReport piEasternEnglandMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Principal Investigator (Eastern England) Report");
        report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.addGroup(ds.getGroup(13));
        report.addGroup(ds.getGroup(32));
        report.addGroup(ds.getGroup(52));
        report.addGroup(ds.getGroup(58));
        report.setWithRawData(false);
        report.setTemplate(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
        IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        report.addChart(chrt1);

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Eastern England");
        chrt2.addGroup(ds.getGroup(13));
        chrt2.addGroup(ds.getGroup(32));
        chrt2.addGroup(ds.getGroup(52));
        chrt2.addGroup(ds.getGroup(58));
        report.addChart(chrt2);

        return report;
    }


    public static IReport piNorthEastLondonMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Principal Investigator (North East London) Report");
        report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.addGroup(ds.getGroup(14));
        report.addGroup(ds.getGroup(15));
        report.addGroup(ds.getGroup(16));
        report.addGroup(ds.getGroup(17));
        report.addGroup(ds.getGroup(27));
        report.addGroup(ds.getGroup(28));
        report.addGroup(ds.getGroup(54)); //Should be 'Barking, Havering and Redbridge Hospitals NHS Trust'
        report.addGroup(ds.getGroup(70));
        report.addGroup(ds.getGroup(72));
        report.setWithRawData(false);
        report.setTemplate(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
        IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        report.addChart(chrt1);

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "North East London");
        chrt2.addGroup(ds.getGroup(14));
        chrt2.addGroup(ds.getGroup(15));
        chrt2.addGroup(ds.getGroup(16));
        chrt2.addGroup(ds.getGroup(17));
        chrt2.addGroup(ds.getGroup(27));
        chrt2.addGroup(ds.getGroup(28));
        chrt2.addGroup(ds.getGroup(54)); //Should be 'Barking, Havering and Redbridge Hospitals NHS Trust'
        chrt2.addGroup(ds.getGroup(70));
        chrt2.addGroup(ds.getGroup(72));
        report.addChart(chrt2);

        return report;
    }

    public static IReport piSouthEastMidlandsMgmtReport(DataSet ds) throws ReportException {

        IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Principal Investigator (South East Midlands) Report");
        report.setEmailAction(RBACAction.ACTION_DR_INVESTIGATOR_REPORT);
        report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
        report.addGroup(ds.getGroup(18));
        report.addGroup(ds.getGroup(43));
        report.addGroup(ds.getGroup(71));
        report.setWithRawData(false);
        report.setTemplate(false);

        IProjectSummaryChart total = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, "Total Clients");
		total.setShowTotal(true);
		report.addChart(total);
        IProjectSummaryChart chrt1 = factory.createProjectSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "Whole Project");
        report.addChart(chrt1);

        IGroupsSummaryChart chrt2 = factory.createGroupsSummaryChart(org.psygrid.data.reporting.Chart.CHART_PIE, "South East Midlands");
        chrt2.addGroup(ds.getGroup(18));
        chrt2.addGroup(ds.getGroup(43));
        chrt2.addGroup(ds.getGroup(71));
        report.addChart(chrt2);

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
		 * Create the total recruitment targets for the whole project
		 */
		chart.addTarget(new GregorianCalendar(2008, 0, 0), 0);	//Jan 2008
		chart.addTarget(new GregorianCalendar(2008, 1, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 2, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 3, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 4, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 5, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 6, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 7, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 8, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 9, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 10, 0), 0);
		chart.addTarget(new GregorianCalendar(2008, 11, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 0, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 1, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 2, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 3, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 4, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 5, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 6, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 7, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 8, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 9, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 10, 0), 0);
		chart.addTarget(new GregorianCalendar(2009, 11, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 0, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 1, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 2, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 3, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 4, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 5, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 6, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 7, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 8, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 9, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 10, 0), 0);
		chart.addTarget(new GregorianCalendar(2010, 11, 0), 0);

		return report;
	}

	public static IReport cpmRecruitmentReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Recruitment Progress Report");
		report.setEmailAction(RBACAction.ACTION_DR_CRM_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(false);
		report.setTemplate(false);

		//Whole project
		IRecruitmentProgressChart chart = factory.createRecruitmentProgressChart(Chart.CHART_TIME_SERIES, ds.getName()+" - Recruitment Progress (Whole Project)");
		chart.setTimePeriod(null, null);
		chart.setRangeAxisLabel("Number of Clients");
		for ( int i=0, c=ds.numGroups(); i<c; i++ ){
			chart.addGroup(ds.getGroup(i));
		}
		report.addManagementChart(chart);

		//add charts from the reports for each of the hubs
		report.addManagementChart(((IManagementReport)recruitmentInNorthEastAndCumbriaReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInNorthWestReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInNorthWestLondonReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInSouthWestReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInThamesValleyReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInEasternEnglandReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInNorthEastLondonReport(ds)).getChart(0));
		report.addManagementChart(((IManagementReport)recruitmentInSouthEastMidlandsReport(ds)).getChart(0));

		//because of deprecated groups we need to explicitly set the groups on this report;
		//otherwise it will assume that the deprecated groups should be included
		int numGroups = ds.numGroups();
		for ( int i=0; i<numGroups; i++ ){
			Group curGroup = ds.getGroup(i);
			if (!(curGroup.getLongName().equals("DEPRECATED"))) {
				report.addGroup(curGroup);
			}
		}

		return report;

	}

	public static IReport recruitmentInNorthEastAndCumbriaReport(DataSet ds) throws ReportException {
      	List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(0));
		groups.add(ds.getGroup(1));
		groups.add(ds.getGroup(2));
		groups.add(ds.getGroup(3));
		groups.add(ds.getGroup(4));
		groups.add(ds.getGroup(44));
		groups.add(ds.getGroup(45));
		groups.add(ds.getGroup(46));
		groups.add(ds.getGroup(47));
		groups.add(ds.getGroup(48));
		groups.add(ds.getGroup(51));
		groups.add(ds.getGroup(61));
		groups.add(ds.getGroup(61));
		groups.add(ds.getGroup(64));
		groups.add(ds.getGroup(65));
		groups.add(ds.getGroup(66));
		groups.add(ds.getGroup(67));
		groups.add(ds.getGroup(68));

		IReport report = recruitmentReport(ds, groups, "North East and Cumbria");
		report.setTemplate(false);

		return report;
    }

	public static IReport recruitmentInNorthWestReport(DataSet ds) throws ReportException {
      	List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(5));
		groups.add(ds.getGroup(24));
		groups.add(ds.getGroup(29));
		groups.add(ds.getGroup(33));
		groups.add(ds.getGroup(35));
		groups.add(ds.getGroup(36));
		groups.add(ds.getGroup(37));
		groups.add(ds.getGroup(42));
		groups.add(ds.getGroup(50));
		groups.add(ds.getGroup(55));
		groups.add(ds.getGroup(56));
		groups.add(ds.getGroup(57));
		groups.add(ds.getGroup(59));
		groups.add(ds.getGroup(60));
		groups.add(ds.getGroup(62));
		groups.add(ds.getGroup(63));
		groups.add(ds.getGroup(73));
		groups.add(ds.getGroup(74));
		groups.add(ds.getGroup(75));

		IReport report = recruitmentReport(ds, groups, "North West England");
		report.setTemplate(false);

		return report;
    }

	public static IReport recruitmentInNorthWestLondonReport(DataSet ds) throws ReportException {
      	List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(6));
		groups.add(ds.getGroup(7));
		groups.add(ds.getGroup(19));
		groups.add(ds.getGroup(20));
		groups.add(ds.getGroup(21));
		groups.add(ds.getGroup(22));
		groups.add(ds.getGroup(23));
		groups.add(ds.getGroup(38));

		IReport report = recruitmentReport(ds, groups, "North West London");
		report.setTemplate(false);

		return report;
    }

	public static IReport recruitmentInSouthWestReport(DataSet ds) throws ReportException {
      	List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(8));
		groups.add(ds.getGroup(9));
		groups.add(ds.getGroup(10));
		groups.add(ds.getGroup(11));
		groups.add(ds.getGroup(76));

		IReport report = recruitmentReport(ds, groups, "South West Peninsula");
		report.setTemplate(false);

		return report;
    }

	public static IReport recruitmentInThamesValleyReport(DataSet ds) throws ReportException {
      	List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(12));
		groups.add(ds.getGroup(25));
		groups.add(ds.getGroup(26));
		groups.add(ds.getGroup(31));
		groups.add(ds.getGroup(39));
		groups.add(ds.getGroup(40));
		groups.add(ds.getGroup(41));
		groups.add(ds.getGroup(49));
		groups.add(ds.getGroup(53)); //This should be 'University Hospitals Coventry'

		IReport report = recruitmentReport(ds, groups, "Thames Valley");
		report.setTemplate(false);

		return report;
    }

	public static IReport recruitmentInEasternEnglandReport(DataSet ds) throws ReportException {
      	List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(13));
		groups.add(ds.getGroup(32));
		groups.add(ds.getGroup(52));
		groups.add(ds.getGroup(58));

		IReport report = recruitmentReport(ds, groups, "Eastern England");
		report.setTemplate(false);

		return report;
    }

	public static IReport recruitmentInNorthEastLondonReport(DataSet ds) throws ReportException {
      	List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(14));
		groups.add(ds.getGroup(15));
		groups.add(ds.getGroup(16));
		groups.add(ds.getGroup(17));
		groups.add(ds.getGroup(27));
		groups.add(ds.getGroup(28));
		groups.add(ds.getGroup(54)); //Should be 'Barking, Havering and Redbridge Hospitals NHS Trust'
		groups.add(ds.getGroup(70));
		groups.add(ds.getGroup(72));

		IReport report = recruitmentReport(ds, groups, "North East London");
		report.setTemplate(false);

		return report;
    }

	public static IReport recruitmentInSouthEastMidlandsReport(DataSet ds) throws ReportException {
      	List<Group> groups = new ArrayList<Group>();
		groups.add(ds.getGroup(18));
		groups.add(ds.getGroup(43));
		groups.add(ds.getGroup(71));

		IReport report = recruitmentReport(ds, groups, "South East Midlands");
		report.setTemplate(false);

		return report;
    }



	public static IReport recruitmentReport(DataSet ds) throws ReportException {
		IManagementReport report = recruitmentReport(ds, null, "Overview", "Chief Investigator");
		report.setEmailAction(RBACAction.ACTION_DR_CHIEF_INVESTIGATOR_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
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

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - Recruitment Progress Report("+hub+")");
		report.setWithRawData(true);
		report.setFrequency(ReportFrequency.MONTHLY);

		/*
		 * Create a timeseries chart showing the number of subjects consented
		 * into the trial against the targets set for each month, giving
		 * a view of the trial's progress.
		 */
		IRecruitmentProgressChart chart = factory.createRecruitmentProgressChart(Chart.CHART_TIME_SERIES, ds.getName()+" - Recruitment Progress ("+hub+")");

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

	public static IReport ukCRNReport(DataSet ds) throws ReportException {

		IManagementReport report = factory.createManagementReport(ds, ds.getName()+" - UKCRN Report");
		report.setEmailAction(RBACAction.ACTION_DR_UKCRN_REPORT);
		report.setViewAction(RBACAction.ACTION_DR_VIEW_MGMT_REPORT);
		report.setWithRawData(true);
		report.setFrequency(ReportFrequency.MONTHLY);
		report.setShowHeader(false);

		IUKCRNSummaryChart chart = factory.createUKCRNSummaryChart(org.psygrid.data.reporting.Chart.CHART_TABLE, ds.getName());
		report.addChart(chart);

		//This will be set automatically when the report is generated to create
		//a for the current financial year (assuming may-april)
		chart.setTimePeriod(null, null);

		return report;
	}

}
