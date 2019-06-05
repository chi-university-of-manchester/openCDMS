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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.opensaml.SAMLAssertion;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.dto.PersistentDTO;
import org.psygrid.data.model.dto.extra.MinimalEntry;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.definition.IAbstractChartItem;
import org.psygrid.data.reporting.definition.IReceivingTreatmentChart;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.reporting.definition.ISimpleChartItem;
import org.psygrid.data.reporting.definition.ITrendsChart;
import org.psygrid.data.reporting.definition.IUKCRNSummaryChart;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.reporting.definition.ReportFrequency;
import org.psygrid.data.reporting.definition.hibernate.GroupsSummaryChart;
import org.psygrid.data.reporting.definition.hibernate.ManagementChart;
import org.psygrid.data.reporting.definition.hibernate.ManagementReport;
import org.psygrid.data.reporting.definition.hibernate.ReceivingTreatmentChart;
import org.psygrid.data.reporting.definition.hibernate.RecordChart;
import org.psygrid.data.reporting.definition.hibernate.RecordReport;
import org.psygrid.data.reporting.definition.hibernate.RecruitmentProgressChart;
import org.psygrid.data.reporting.definition.hibernate.SimpleChartRow;
import org.psygrid.data.reporting.definition.hibernate.TrendsGanttChart;
import org.psygrid.data.reporting.definition.hibernate.TrendsReport;
import org.psygrid.data.reporting.definition.hibernate.UKCRNSummaryChart;
import org.psygrid.data.reporting.old.Report;
import org.psygrid.data.reporting.old.ReportRow;
import org.psygrid.data.reporting.renderer.AbstractTextRenderer;
import org.psygrid.data.reporting.renderer.CSVRenderer;
import org.psygrid.data.reporting.renderer.ExcelRenderer;
import org.psygrid.data.reporting.renderer.PdfRenderer;
import org.psygrid.data.reporting.renderer.RendererException;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.utils.esl.IRemoteClient;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.data.utils.wrappers.AAQCWrapper;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.www.xml.security.core.types.ActionType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Implementation of the ReportingDAO interface that uses Hibernate
 * to manipulate persistent objects.
 * 
 * @author Rob Harper
 *
 */
public class ReportingDAOHibernate extends HibernateDaoSupport implements ReportingDAO {

	private static Log sLog = LogFactory.getLog(ReportingDAOHibernate.class);

	private IRemoteClient client;

	/**
	 * Attribute authority query client
	 */
	private AAQCWrapper aaqc;

	/**
	 * Policy authority query client
	 */
	//private PAQCWrapper paqc;

	public AAQCWrapper getAaqc() {
		return aaqc;
	}

	public void setAaqc(AAQCWrapper aaqc) {
		this.aaqc = aaqc;
	}

	/*
	public PAQCWrapper getPaqc() {
		return paqc;
	}

	public void setPaqc(PAQCWrapper paqc) {
		this.paqc = paqc;
	}
	*/

	public IRemoteClient getClient() {
		return client;
	}

	public void setClient(IRemoteClient client) {
		this.client = client;
	}

	public Report reportByStatus(Long dataSetId) throws DAOException {

		//get all statuses for the dataset
		List statuses = getHibernateTemplate().find("select d.statuses.longName from DataSet d where d.id=?", dataSetId);
		if ( 0 == statuses.size() ){
			throw new DAOException("Either no dataset exists for the given ID, or a dataset exists but has no defined statuses");
		}

		//get counts for statuses of records
		Object[] params = new Object[]{dataSetId, Boolean.FALSE};
		List results = getHibernateTemplate().find("select r.status.longName, count(r) from Record r where r.dataSet.id=? and r.deleted=? group by r.status.longName", params);

		Report r = new Report();
		r.setTitle("Number of participants by status");
		r.setEntity("Status");
		r.setColumns(new String[]{"Number"});        
		ReportRow[] rows = new ReportRow[statuses.size()];
		r.setRows(rows);

		for ( int i=0; i<statuses.size(); i++){
			String status = (String)statuses.get(i);
			ReportRow row = new ReportRow();
			row.setHeading(status);
			Integer count = new Integer(0);
			//try to find a row in the results list for this status
			for ( int j=0; j<results.size(); j++ ){
				Object[] data = (Object[])results.get(j);
				if ( status.equals((String)data[0])){
					count = Integer.parseInt(data[1].toString());
					break;
				}
			}
			row.setValues(new Double[]{new Double(count.doubleValue())});
			rows[i] = row;
		}

		return r;
	}

	public Long saveReport(org.psygrid.data.reporting.definition.dto.Report report){
		org.psygrid.data.reporting.definition.hibernate.Report r = report.toHibernate();
		getHibernateTemplate().saveOrUpdate(r);
		return r.getId();
	}

	public org.psygrid.data.reporting.RecordReport generateReport(final Long reportId, final Long recordId, final String user, final String saml) throws DAOException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				org.psygrid.data.reporting.definition.hibernate.RecordReport r = 
					(org.psygrid.data.reporting.definition.hibernate.RecordReport)session.createQuery("from RecordReport r where r.id=?")
					.setLong(0, reportId)
					.uniqueResult();

				String identifier = (String)session.createQuery("select r.identifier.identifier from Record r where r.id=?")
				.setLong(0, recordId)
				.uniqueResult();

				if ( r == null) {
					return new DAOException("No record report found for id "+reportId);
				}

				org.psygrid.data.reporting.RecordReport report = new org.psygrid.data.reporting.RecordReport();
				report.setTitle(r.getTitle());
				report.setRequestor(user);
				report.setRequestDate(new Date());
				report.setSubject(identifier);
				report.setShowHeader(r.isShowHeader());

				report.setCharts(new org.psygrid.data.reporting.Chart[r.getCharts().size()]);
				for ( int i=0; i<r.getCharts().size(); i++){
					try{
						report.getCharts()[i] = r.getCharts().get(i).generateChart(session, client, recordId, saml);
					}
					catch(ReportException ex){
						return new DAOException("Problem generating report", ex);
					}
					catch (NullPointerException e) {
						return new DAOException("Problem generating report "+i, e);
					}
				}

				return report;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
		return (org.psygrid.data.reporting.RecordReport)result;
	}


	public org.psygrid.data.reporting.Report generateTrendReport(final Long reportId, final Long datasetId, final List<String> groups) 
	throws DAOException, GroupsNotAllowedException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				org.psygrid.data.reporting.definition.hibernate.TrendsReport r = 
					(org.psygrid.data.reporting.definition.hibernate.TrendsReport)session.createQuery("from TrendsReport r where r.id=?")
					.setLong(0, reportId)
					.uniqueResult();


				if ( r == null) {
					return new DAOException("No trend reports found for dataset id "+datasetId);
				}

				org.psygrid.data.reporting.Report report = new org.psygrid.data.reporting.Report();
				report.setTitle(r.getTitle());
				report.setRequestDate(new Date());
				report.setShowHeader(r.isShowHeader());

				report.setCharts(new org.psygrid.data.reporting.Chart[r.getTrendsCharts().size()]);
				for ( int i=0; i<r.getTrendsCharts().size(); i++){
					try{
						//Check that the requested charts are only for groups included in the list of groups.
						//This list should be all groups the user is authorised to access according to their saml.
						if (! checkGroups(r.getTrendsCharts().get(i).getGroups(), groups) ) {
							return new GroupsNotAllowedException("Not authorised to view report containing the groups: "+r.getTrendsCharts().get(i).getGroups()+". Groups allowed are "+groups);
						}
						try {
							report.getCharts()[i] = ((TrendsGanttChart)r.getTrendsCharts().get(i)).generateChart(session, datasetId);
						}
						catch (ClassCastException ex) {
							report.getCharts()[i] = r.getTrendsCharts().get(i).generateChart(session, datasetId);
						}
					}
					catch(ReportException ex){
						return new DAOException("Problem generating report", ex);
					}
					catch (NullPointerException e) {
						return new DAOException("Problem generating report "+i, e);
					}
				}

				return report;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
		if ( result instanceof GroupsNotAllowedException ){
			throw (GroupsNotAllowedException)result;
		}
		return (org.psygrid.data.reporting.Report)result;
	}


	public byte[] generateDynamicRecordReport(final org.psygrid.data.reporting.definition.dto.RecordReport newreport, final String identifier, final String formatType, final List<String> groups, final String saml, final String user) 
	throws DAOException, ReportRenderingException, GroupsNotAllowedException, NotAuthorisedFault {
		//set record
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				org.psygrid.data.model.hibernate.Record record = 
					(org.psygrid.data.model.hibernate.Record)session.createQuery("from Record r where r.identifier.identifier=? and r.deleted=?")
					.setString(0, identifier)
					.setBoolean(1, false)
					.uniqueResult();

				if ( record == null) {
					return new DAOException("No record found for study number "+identifier);
				}
				return record;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
		org.psygrid.data.model.hibernate.Record r = (org.psygrid.data.model.hibernate.Record)result;
		newreport.setRecord(r.toDTO());

		return generateDynamicReport(newreport, formatType, groups, saml, user);
	}

	public byte[] generateDynamicReport(final org.psygrid.data.reporting.definition.dto.Report newreport, final String formatType, final List<String> groups, final String saml, final String user) 
	throws DAOException, ReportRenderingException, GroupsNotAllowedException, NotAuthorisedFault {

		IReport report = newreport.toHibernate();

		org.psygrid.data.reporting.Report rr = null;

		if ( report instanceof RecordReport ) {
			rr = generateReport(report, groups, user, saml);		
		}
		else if ( report instanceof TrendsReport ) {
			rr = generateTrendReport(report, groups, user);	
		}
		else if ( report instanceof ManagementReport ) {
			rr = generateManagementReport(report, groups, saml, user);
		}
		else {
			throw new DAOException("Report type not recognised");
		}

		rr.setShowHeader(report.isShowHeader());

		byte[] reportStream = null;
		if (formatType.equalsIgnoreCase("pdf")) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PdfRenderer renderer = new PdfRenderer();

			try {
				renderer.render(rr, os);
			}
			catch (IOException ioe) {
				throw new ReportRenderingException("Unable to render report as "+formatType+". Problem was: "+ioe.getMessage());
			}
			catch (RendererException re) {
				throw new ReportRenderingException("Unable to render report as "+formatType+". Problem was: "+re.getMessage());
			}

			reportStream = os.toByteArray();
		}
		else  {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			AbstractTextRenderer renderer = null;

			if (formatType.equalsIgnoreCase("csv")) {
				renderer = new CSVRenderer();
			}
			else if (formatType.equalsIgnoreCase("xls")) {
				renderer = new ExcelRenderer();
			}
			else {
				throw new DAOException("Unable to render report as format "+formatType+": Unknown format. ");
			}


			try {
				renderer.render(rr, os);
			}
			catch (IOException ioe) {
				throw new ReportRenderingException("Unable to render report as "+formatType+". Problem was: "+ioe.getMessage());
			}
			catch (RendererException re) {
				throw new ReportRenderingException("Unable to render report as "+formatType+". Problem was: "+re.getMessage());
			}

			reportStream = os.toByteArray();
		}


		return reportStream;
	}

	/**
	 * Generates a reporting.Report (ready to be rendered) from 
	 * a non-persistent Management Report object.
	 * 
	 * NB. This report does not retrieve a list of email recipients.
	 * 
	 * @param newreport
	 * @param groups list of groups the user is restricted to
	 * @param saml the saml assertion required to get additional information for UKCRN Summary reports only
	 * @param user
	 * @return report
	 * @throws DAOException
	 */
	private org.psygrid.data.reporting.Report generateManagementReport(IReport newreport, List<String> groups, String saml, String user) 
	throws DAOException, GroupsNotAllowedException, NotAuthorisedFault {

		final ManagementReport report = (ManagementReport)newreport;

		final Long dsId = report.getDataSet().getId();
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				DataSet ds = (DataSet)session.createQuery("from DataSet d where d.id=:id")
				.setLong("id", dsId)
				.uniqueResult();

				return ds;
			}
		};
		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
		DataSet ds = (DataSet)result;

		org.psygrid.data.reporting.ManagementReport rr = new org.psygrid.data.reporting.ManagementReport();

		rr.setTitle(report.getTitle());
		rr.setRequestDate(new Date());
		rr.setWithRawData(report.isWithRawData());
		rr.setShowHeader(true);

		List<org.psygrid.data.reporting.Chart> charts = new ArrayList<org.psygrid.data.reporting.Chart>();

		for ( int i=0; i<report.getCharts().size(); i++){
			try{		
				//explicitly add the relevant groups for the chart/report otherwise an 
				//error is thrown because the chart/report hasn't been persisted
				if (report.getCharts().get(i) instanceof RecruitmentProgressChart) {
					RecruitmentProgressChart c = (RecruitmentProgressChart)report.getCharts().get(i);

					if (c.getStartDate() != null && c.getEndDate() != null) {
						rr.setStartDate(c.getStartDate().getTime());
						rr.setEndDate(c.getEndDate().getTime());
					}

					if (c.numGroups() == 0) {
						if (report.numGroups() == 0) {
							for (Group g: ds.getGroups()) {
								report.addGroup(g);
							}
						}
						else {
							for (Group g: report.getGroups()) {
								c.addGroup(g);
							}
						}
					}
					else {
						for (Group g: c.getGroups()) {
							report.addGroup(g);
						}
					}
					//Check that the requested charts are only for groups included in the list of groups.
					//This list should be all groups the user is authorised to access according to their saml.
					List<Group> g = new ArrayList<Group>();
					for (Group grp: report.getGroups()) {
						g.add((Group)grp);
					}
					if (! checkGroups(g, groups) ) {
						throw new GroupsNotAllowedException("Not authorised to view report containing the groups: "+g);
					}

					//Add the group names to the report to render
					List<String> displayGroups = new ArrayList<String>();
					for (Group grp: g) {
						displayGroups.add(grp.getLongName());
					}
					rr.setGroups(displayGroups);

					c.setReport(report);
					org.psygrid.data.reporting.Chart chart = report.getCharts().get(i).generateChart(getSession());
					if ( null != chart ){
						charts.add(chart);
					}
				}
				else if (report.getCharts().get(i) instanceof GroupsSummaryChart) {
					GroupsSummaryChart c = (GroupsSummaryChart)report.getCharts().get(i);
					if (c.numGroups() == 0) {
						if (report.numGroups() == 0) {
							for (Group g: ds.getGroups()) {
								report.addGroup(g);
							}
						}
						else {
							for (Group g: report.getGroups()) {
								c.addGroup(g);
							}
						}
					}

					//Check that the requested charts are only for groups included in the list of groups.
					//This list should be all groups the user is authorised to access according to their saml.
					for (Group grp: c.getGroups()) {
						report.addGroup(grp);
					}
					List<Group> g = new ArrayList<Group>();
					for (Group grp: report.getGroups()) {
						g.add((Group)grp);
					}
					if (! checkGroups(g, groups) ) {
						throw new GroupsNotAllowedException("Not authorised to view report containing the groups: "+g+". Groups allowed are "+groups);
					}

					//Add the group names to the report to render
					List<String> displayGroups = new ArrayList<String>();
					for (Group grp: g) {
						displayGroups.add(grp.getLongName());
					}
					rr.setGroups(displayGroups);

					c.setReport(report);
					charts.add(report.getCharts().get(i).generateChart(getHibernateTemplate().getSessionFactory().getCurrentSession()));
				}
				else if (report.getCharts().get(i) instanceof UKCRNSummaryChart) {
					UKCRNSummaryChart c = (UKCRNSummaryChart)report.getCharts().get(i);

					if (c.getStartDate() != null && c.getEndDate() != null) {
						rr.setStartDate(c.getStartDate().getTime());
						rr.setEndDate(c.getEndDate().getTime());
					}
					if (report.numGroups() == 0) {
						for (Group g: ds.getGroups()) {
							report.addGroup(g);
						}
					}	

					List<Group> g = new ArrayList<Group>();
					for (Group grp: report.getGroups()) {
						g.add((Group)grp);
					}
					if (! checkGroups(g, groups) ) {
						throw new GroupsNotAllowedException("Not authorised to view report containing the groups: "+g+". Groups allowed are "+groups);
					}

					//Add the group names to the report to render
					List<String> displayGroups = new ArrayList<String>();
					for (Group grp: g) {
						displayGroups.add(grp.getLongName());
					}
					rr.setGroups(displayGroups);


					c.setReport(report);

					report.setDataSet(ds);

					charts.add(((IUKCRNSummaryChart)c).generateChart(getHibernateTemplate().getSessionFactory().getCurrentSession(), client, saml));
				}
				else if (report.getCharts().get(i) instanceof ReceivingTreatmentChart) {
					ReceivingTreatmentChart c = (ReceivingTreatmentChart)report.getCharts().get(i);

					if (c.getStartDate() != null && c.getEndDate() != null ) {
						rr.setStartDate(c.getStartDate().getTime());
						rr.setEndDate(c.getEndDate().getTime());
					}

					if (report.numGroups() == 0) {
						for (Group g: ds.getGroups()) {
							report.addGroup(g);
						}
					}	

					List<Group> g = new ArrayList<Group>();
					for (Group grp: report.getGroups()) {
						g.add((Group)grp);
					}
					if (! checkGroups(g, groups) ) {
						throw new GroupsNotAllowedException("Not authorised to view report containing the groups: "+g+". Groups allowed are "+groups);
					}

					//Add the group names to the report to render
					List<String> displayGroups = new ArrayList<String>();
					for (Group grp: g) {
						displayGroups.add(grp.getLongName());
					}
					rr.setGroups(displayGroups);

					c.setReport(report);

					report.setDataSet(ds);

					//The receiving treatment chart returns one chart per treatment, so that they can be displayed in a separate excel worksheet
					org.psygrid.data.reporting.Chart[] tcharts = ((IReceivingTreatmentChart)c).generateChart(getHibernateTemplate().getSessionFactory().getCurrentSession(), client, saml);
					for (int j = 0; j < tcharts.length; j++) {
						charts.add(tcharts[j]);
					}

				}
				else {
					ManagementChart c = report.getCharts().get(i);

					if (report.numGroups() == 0) {
						for (Group g: ds.getGroups()) {
							report.addGroup(g);
						}
					}	

					List<Group> g = new ArrayList<Group>();
					for (Group grp: report.getGroups()) {
						g.add((Group)grp);
					}
					if (! checkGroups(g, groups) ) {
						throw new GroupsNotAllowedException("Not authorised to view report containing the groups: "+g+". Groups allowed are "+groups);
					}

					//Add the group names to the report to render
					List<String> displayGroups = new ArrayList<String>();
					for (Group grp: g) {
						displayGroups.add(grp.getLongName());
					}
					rr.setGroups(displayGroups);

					c.setReport(report);
					charts.add(c.generateChart(getHibernateTemplate().getSessionFactory().getCurrentSession()));
				}

				rr.setCharts(new org.psygrid.data.reporting.Chart[charts.size()]);
				for (int k = 0; k < charts.size(); k++) {
					rr.getCharts()[k] = charts.get(k); 
				}
			}
			catch(ReportException ex){
				throw new DAOException("Problem generating report", ex);
			}
			catch (NullPointerException e) {
				throw new DAOException("Problem generating report "+i, e);
			}

		}


		return rr;
	}

	/**
	 * Generates a reporting.Report (ready to be rendered) from 
	 * a non-persistent Trends Report object.
	 * 
	 * @param newreport
	 * @param groups list of groups the user is restricted to
	 * @param user
	 * @return report
	 * @throws DAOException
	 */
	private org.psygrid.data.reporting.Report generateTrendReport(IReport newreport, List<String> groups, String user) 
	throws DAOException, GroupsNotAllowedException {

		final TrendsReport report = (TrendsReport)newreport;

		final Long dsId = report.getDataSet().getId();

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				DataSet ds = (DataSet)session.createQuery("from DataSet d where d.id=:id")
				.setLong("id", dsId)
				.uniqueResult();

				return ds;
			}
		};
		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
		DataSet ds = (DataSet)result;

		List<String> totalGroups = new ArrayList<String>();
		org.psygrid.data.reporting.Report rr = new org.psygrid.data.reporting.Report();

		rr.setTitle(report.getTitle());
		rr.setRequestDate(new Date());

		rr.setCharts(new org.psygrid.data.reporting.Chart[report.getTrendsCharts().size()]);

		for ( int i=0; i<report.getTrendsCharts().size(); i++){
			try{		

				ITrendsChart c = report.getTrendsCharts().get(i);

				if (c.getStartDate() != null && c.getEndDate() != null) {
					rr.setStartDate(c.getStartDate().getTime());
					rr.setEndDate(c.getEndDate().getTime());
				}

				//explicitly add the relevant groups for the chart otherwise an 
				//error is thrown because the chart/report hasn't been persisted
				if (c.numGroups() == 0) {
					for (Group g: ds.getGroups()) {
						c.addGroup(g);
					}
				}

				//Check that the requested charts are only for groups included in the list of groups.
				//This list should be all groups the user is authorised to access according to their saml.
				if (! checkGroups(c.getGroups(), groups) ) {
					throw new GroupsNotAllowedException("Not authorised to view report containing the groups: "+c.getGroups()+". Groups allowed are "+groups);
				}

				c.setReport(report);

				for (Group g: c.getGroups()) {
					if (!totalGroups.contains(g.getLongName())) {
						totalGroups.add(g.getLongName());
					}
				}

				rr.getCharts()[i] = c.generateChart(getHibernateTemplate().getSessionFactory().getCurrentSession(), dsId);
			}
			catch(ReportException ex){
				throw new DAOException("Problem generating report", ex);
			}
			catch (NullPointerException e) {
				throw new DAOException("Problem generating report "+i, e);
			}
		}

		rr.setShowHeader(true);
		rr.setGroups(totalGroups);

		return rr;
	}


	/**
	 * Generates a reporting.Report (ready to be rendered) from 
	 * a non-persistent Report object.
	 *  
	 * @param newreport
	 * @param groups list of groups the user is restricted to
	 * @param user the requestor of the report
	 * @return report
	 * @throws DAOException
	 */
	public org.psygrid.data.reporting.Report generateReport(IReport newreport, List<String> groups, String user, String saml) 
	throws DAOException, GroupsNotAllowedException {
		RecordReport report = (RecordReport)newreport;
		org.psygrid.data.reporting.RecordReport rr = new org.psygrid.data.reporting.RecordReport();
		rr.setTitle(report.getTitle());
		rr.setRequestor(user);
		rr.setRequestDate(new Date());

		// Conditionally report the external identifier
		String identifier = report.getRecord().getIdentifier().getIdentifier();
		String externalID = report.getRecord().getExternalIdentifier();
		boolean useExternalID = report.getRecord().getUseExternalIdAsPrimary();
		rr.setSubject(useExternalID?externalID:identifier);

		rr.setShowHeader(newreport.isShowHeader());

		final Record record = report.getRecord();

		//Check that the record's group is in the list of allowed groups
		boolean found = false;
		if (groups == null || groups.size() == 0) {
			found = true;	//the groups haven't been restricted, so all are allowed
		}
		if (groups != null) {
			for (String groupCode: groups) {
				if (groupCode.equals(record.getIdentifier().getGroupPrefix())) {
					found = true;
				}
			}
		}
		if (! found ) {
			throw new GroupsNotAllowedException("Not authorised to view report containing the group "+record.getIdentifier().getGroupPrefix());
		}

		//The subject that the record belongs too, which is a unique id
		final String subjectId = record.getIdentifier().getIdentifier();
		//rr.setSubject(subjectId);

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				Long recordId = (Long)session.createQuery("select r.id from Record r where r.identifier.identifier=:id")
				.setString("id", subjectId)
				.uniqueResult();


				if ( recordId == null) {
					return new DAOException("No record report found for id "+subjectId);
				}
				return recordId;
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
		Long recordId = (Long)result;

		rr.setCharts(new org.psygrid.data.reporting.Chart[report.getCharts().size()]);
		for ( int i=0; i<report.getCharts().size(); i++){

			RecordChart c = report.getCharts().get(i);
			//Retrieve every item associated with a chart and reassign the entry (rather than just the entry id)
			for (SimpleChartRow row: c.getRows()) {
				for (IAbstractChartItem item: row.getSeries()) {
					//TODO do this without casting
					if ( item instanceof ISimpleChartItem ){
						ISimpleChartItem sci = (ISimpleChartItem)item;
						Long entryId = sci.getEntry().getId();
						//Retrieve the full entry from the database
						Entry entry = (Entry)getHibernateTemplate().get(Entry.class, entryId);
						sci.setEntry(entry);
					}
				}
			}
			try{
				rr.getCharts()[i] = report.getCharts().get(i).generateChart(getHibernateTemplate().getSessionFactory().getCurrentSession(), client, recordId, saml);
			}
			catch(ReportException ex){
				throw new DAOException("Problem generating report", ex);
			}
			catch (NullPointerException e) {
				throw new DAOException("Problem generating report "+i, e);
			}
		}
		return rr;
	}

	public org.psygrid.data.reporting.definition.dto.Report[] getReportsByDataSet(final Long dataSetId) {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				List result = session.createQuery("from RecordReport r where r.dataSet.id=?")
				.setLong(0, dataSetId)
				.list();

				org.psygrid.data.reporting.definition.dto.Report[] reports = new org.psygrid.data.reporting.definition.dto.Report[result.size()];

				for ( int i=0; i<result.size(); i++ ){
					org.psygrid.data.reporting.definition.hibernate.RecordReport r = 
						(org.psygrid.data.reporting.definition.hibernate.RecordReport)result.get(i);
					reports[i] = r.toDTO(RetrieveDepth.DS_SUMMARY);
				}

				return reports;
			}
		};

		return (org.psygrid.data.reporting.definition.dto.Report[])getHibernateTemplate().execute(callback);
	}


	public org.psygrid.data.reporting.definition.dto.Report[] getReportsOfType(final String dataSetCode, final String type)
	throws NoSuchReportException {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				String sql = "";

				if (type.equalsIgnoreCase("record")) {
					sql = "from RecordReport r where r.dataSet.projectCode=?";
				}
				else if (type.equalsIgnoreCase("trends")) {
					sql = "from TrendsReport r where r.dataSet.projectCode=?";
				}
				else if (type.equalsIgnoreCase("management")) {
					sql = "from ManagementReport r where r.dataSet.projectCode=?";
				}
				else {
					return new NoSuchReportException("Unknown report type '"+type+"' specified; must be either record, trends or management.");
				}

				List result = session.createQuery(sql)
				.setString(0, dataSetCode)
				.list();

				org.psygrid.data.reporting.definition.dto.Report[] reports = new org.psygrid.data.reporting.definition.dto.Report[result.size()];

				for ( int i=0; i<result.size(); i++ ){
					org.psygrid.data.reporting.definition.hibernate.Report r = 
						(org.psygrid.data.reporting.definition.hibernate.Report)result.get(i);
					reports[i] = r.toDTO(RetrieveDepth.DS_SUMMARY);
				}

				return reports;
			}
		};

		Object result = getHibernateTemplate().execute(callback);

		if (result instanceof NoSuchReportException) {
			throw (NoSuchReportException)result;
		}

		return (org.psygrid.data.reporting.definition.dto.Report[]) result;
	}

	public org.psygrid.data.reporting.definition.dto.Report[] getAllReportsByDataSet(final Long dataSetId) {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				List result = session.createQuery("from Report r where r.dataSet.id=?")
				.setLong(0, dataSetId)
				.list();

				org.psygrid.data.reporting.definition.dto.Report[] reports = new org.psygrid.data.reporting.definition.dto.Report[result.size()];

				for ( int i=0; i<result.size(); i++ ){
					org.psygrid.data.reporting.definition.hibernate.Report r = 
						(org.psygrid.data.reporting.definition.hibernate.Report)result.get(i);
					reports[i] = r.toDTO(RetrieveDepth.DS_SUMMARY);
				}

				return reports;
			}
		};

		return (org.psygrid.data.reporting.definition.dto.Report[])getHibernateTemplate().execute(callback);
	}

	public org.psygrid.data.reporting.definition.dto.Report getReport(final Long reportId) {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				Object result = session.createQuery("from Report r where r.id=?")
				.setLong(0, reportId)
				.uniqueResult();

				org.psygrid.data.reporting.definition.hibernate.Report r = 
					(org.psygrid.data.reporting.definition.hibernate.Report)result;
				org.psygrid.data.reporting.definition.dto.Report report = r.toDTO(RetrieveDepth.REP_SAVE);

				return report;
			}
		};

		return (org.psygrid.data.reporting.definition.dto.Report)getHibernateTemplate().execute(callback);
	}

	public org.psygrid.data.reporting.definition.dto.RecordReport getRecordReport(final Long reportId) {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				Object result = session.createQuery("from RecordReport r where r.id=?")
				.setLong(0, reportId)
				.uniqueResult();

				org.psygrid.data.reporting.definition.hibernate.RecordReport r = 
					(org.psygrid.data.reporting.definition.hibernate.RecordReport)result;
				org.psygrid.data.reporting.definition.dto.RecordReport report = r.toDTO(RetrieveDepth.REP_SAVE);

				return report;
			}
		};

		return (org.psygrid.data.reporting.definition.dto.RecordReport)getHibernateTemplate().execute(callback);
	}

	public org.psygrid.data.reporting.definition.dto.ManagementReport getManagementReport(final Long reportId) {

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				Object result = session.createQuery("from ManagementReport r where r.id=?")
				.setLong(0, reportId)
				.uniqueResult();

				org.psygrid.data.reporting.definition.hibernate.ManagementReport r = 
					(org.psygrid.data.reporting.definition.hibernate.ManagementReport)result;
				Map<org.psygrid.data.model.hibernate.Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<org.psygrid.data.model.hibernate.Persistent, PersistentDTO>();
				org.psygrid.data.reporting.definition.dto.ManagementReport report = r.toDTO(dtoRefs, RetrieveDepth.REP_SAVE);

				return report;
			}
		};

		return (org.psygrid.data.reporting.definition.dto.ManagementReport)getHibernateTemplate().execute(callback);
	}
	public org.psygrid.data.reporting.Report numbersByStatus(String projectCode) throws DAOException {

		//get counts for statuses of records
		Object[] params = new Object[]{projectCode, Boolean.FALSE};
		List results = getHibernateTemplate().find(
				"select r.status.longName, count(r) from Record r "+
				"where r.dataSet.projectCode=? "+
				"and r.deleted=? "+
				"group by r.status.longName order by r.status.id", params);

		org.psygrid.data.reporting.Report report = new org.psygrid.data.reporting.Report();
		report.setCharts(new org.psygrid.data.reporting.Chart[1]);
		org.psygrid.data.reporting.Chart chart = new org.psygrid.data.reporting.Chart();
		report.getCharts()[0] = chart;

		chart.setRows(new ChartRow[results.size()]);
		for ( int i=0; i<results.size(); i++ ){
			ChartRow row = new ChartRow();
			chart.getRows()[i] = row;
			Object[] data = (Object[])results.get(i);
			row.setLabel((String)data[0]);
			row.setLabelType(IValue.TYPE_STRING);
			// row.setSeries(new ChartPoint[1]);
			row.setSeries(new ChartSeries[1]);
			row.getSeries()[0] = new ChartSeries();
			row.getSeries()[0].setLabel((String)data[0]);

			ChartPoint point = new ChartPoint();
			//row.getSeries()[0] = point;
			row.getSeries()[0].setPoints(new ChartPoint[1]);
			row.getSeries()[0].getPoints()[0] = point;
			point.setValue(data[1].toString());
			point.setValueType(IValue.TYPE_INTEGER);
		}

		return report;
	}

	public org.psygrid.data.reporting.Report numbersByStatusForGroups(String projectCode, String[] groups) throws DAOException {

		//create string for "where r.identifier.groupPrefix in" clause
		StringBuilder whereInClause = new StringBuilder();
		whereInClause.append("(");
		for ( int i=0; i<groups.length; i++ ){
			if ( i > 0 ){
				whereInClause.append(", ");
			}
			whereInClause.append("'").append(groups[i]).append("'");
		}
		whereInClause.append(") ");

		//get counts for statuses of records
		Object[] params = new Object[]{projectCode, Boolean.FALSE};
		List results = getHibernateTemplate().find(
				"select r.status.longName, count(r) from Record r "+
				"where r.dataSet.projectCode=? "+
				"and r.deleted=? "+
				"and r.identifier.groupPrefix in "+whereInClause.toString()+
				"group by r.status.longName order by r.status.id", params);

		org.psygrid.data.reporting.Report report = new org.psygrid.data.reporting.Report();
		report.setCharts(new org.psygrid.data.reporting.Chart[1]);
		org.psygrid.data.reporting.Chart chart = new org.psygrid.data.reporting.Chart();
		report.getCharts()[0] = chart;

		chart.setRows(new ChartRow[results.size()]);
		for ( int i=0; i<results.size(); i++ ){
			ChartRow row = new ChartRow();
			chart.getRows()[i] = row;
			Object[] data = (Object[])results.get(i);
			row.setLabel((String)data[0]);
			row.setLabelType(IValue.TYPE_STRING);
			//row.setSeries(new ChartPoint[1]);
			row.setSeries(new ChartSeries[1]);
			row.getSeries()[0] = new ChartSeries();
			row.getSeries()[0].setLabel((String)data[0]);

			ChartPoint point = new ChartPoint();
			//row.getSeries()[0] = point;
			row.getSeries()[0].setPoints(new ChartPoint[1]);
			row.getSeries()[0].getPoints()[0] = point;

			point.setValue(data[1].toString());
			point.setValueType(IValue.TYPE_INTEGER);
		}

		return report;
	}


	public List<org.psygrid.data.reporting.ManagementReport> generateMgmtReportsForProject(String user, Date date, String project) 
	throws ConnectException {
		List<org.psygrid.data.reporting.ManagementReport> reports = new ArrayList<org.psygrid.data.reporting.ManagementReport>();
		Iterator dataSets = getHibernateTemplate().find("from DataSet ds where ds.projectCode=?", project)
		.iterator();
		while ( dataSets.hasNext() ){

			SAMLAssertion saml = null;
			try {
				if (aaqc != null) {
					saml = aaqc.getFullSAMLAssertion(user);
				}
			}
			catch (PGSecurityException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (PGSecurityInvalidSAMLException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (ConnectException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (PGSecuritySAMLVerificationException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (NotAuthorisedFaultMessage ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}

			DataSet ds = (DataSet)dataSets.next();
			reports.addAll(generateMgmtReportsForDataSet(ds.getId(), date, user, saml));
		}
		return reports;
	}

	public List<org.psygrid.data.reporting.ManagementReport> generateAllMgmtReports(String user, Date date) 
	throws ConnectException {

		List<org.psygrid.data.reporting.ManagementReport> reports = new ArrayList<org.psygrid.data.reporting.ManagementReport>();
		Iterator dataSets = getHibernateTemplate().find("from DataSet").iterator();
		while ( dataSets.hasNext() ){

			SAMLAssertion saml = null;
			try {
				if (aaqc != null) {
					saml = aaqc.getFullSAMLAssertion(user);
				}
			}
			catch (PGSecurityException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (PGSecurityInvalidSAMLException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (ConnectException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (PGSecuritySAMLVerificationException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (NotAuthorisedFaultMessage ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}

			DataSet ds = (DataSet)dataSets.next();
			reports.addAll(generateMgmtReportsForDataSet(ds.getId(), date, user, saml));
		}
		return reports;
	}

	public List<org.psygrid.data.reporting.ManagementReport> getReportRecipientsForProject(String user, Date date, String project) 
	throws ConnectException {
		List<org.psygrid.data.reporting.ManagementReport> reports = new ArrayList<org.psygrid.data.reporting.ManagementReport>();
		Iterator dataSets = getHibernateTemplate().find("from DataSet ds where ds.projectCode=?", project)
		.iterator();
		while ( dataSets.hasNext() ){

			String saml = null;
			try {
				if (aaqc != null) {
					saml = aaqc.getSAMLAssertion(user);
				}
			}
			catch (PGSecurityException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (PGSecurityInvalidSAMLException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (ConnectException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (PGSecuritySAMLVerificationException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (NotAuthorisedFaultMessage ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}

			DataSet ds = (DataSet)dataSets.next();
			reports.addAll(getReportRecipientsForDataSet(ds.getId(), date, saml));
		}
		return reports;
	}

	public org.psygrid.data.reporting.ManagementReport getRecipientsForReport(String user, Date date, long reportId) 
	throws ConnectException {
		String saml = null;
		try {
			if (aaqc != null) {
				saml = aaqc.getSAMLAssertion(user);
			}
		}
		catch (PGSecurityException ex) {
			sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
		}
		catch (PGSecurityInvalidSAMLException ex) {
			sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
		}
		catch (ConnectException ex) {
			sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
		}
		catch (PGSecuritySAMLVerificationException ex) {
			sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
		}
		catch (NotAuthorisedFaultMessage ex) {
			sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
		}

		return getRecipientsForReport(reportId, date, saml);
	}

	@SuppressWarnings("unchecked")
	public List<org.psygrid.data.reporting.ManagementReport> generateMgmtReportsForDataSet(final Long dataSetId, final Date date, final String user, SAMLAssertion saml) 
	throws ConnectException {

		final SAMLAssertion s;

		/*
		 * When this method is called from ReportsJob, the saml assertion 
		 * given is null, so needs to be retrieved from the aaqc.
		 */
		if (aaqc == null) {
			sLog.info("AAQC is null");
		}
		if (saml == null && aaqc != null) {
			SAMLAssertion localSA = null;
			try {
				sLog.info("Retrieving saml as none was provided");
				localSA = aaqc.getFullSAMLAssertion(user);
			}
			catch (PGSecurityException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (PGSecurityInvalidSAMLException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (ConnectException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (PGSecuritySAMLVerificationException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (NotAuthorisedFaultMessage ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}

			s = localSA;
		}
		else {
			s = saml;
		}

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				try{
					SAMLAssertion localSA = s;
					String localsaml = null;
					if (s != null) {
						localsaml = s.toString();
					}

					Calendar cal = Calendar.getInstance();
					cal.setTime(date);

					Iterator results = session.createQuery("from ManagementReport r where r.dataSet.id=?")
					.setLong(0, dataSetId)
					.iterate();

					List<org.psygrid.data.reporting.Report> reports = new ArrayList<org.psygrid.data.reporting.Report>();

					while ( results.hasNext() ){

						org.psygrid.data.reporting.definition.hibernate.ManagementReport r = null;
						try{

							r = (org.psygrid.data.reporting.definition.hibernate.ManagementReport)results.next();

							//see if we need to render this report, based upon its frequency
							//Monthly reports are only rendered if the day-of-month is between 1 and 7
							//Quarterly reports are only rendered if the day-of-month is between 1 and 7
							//AND the month-of-year is Jan, Apr, Jul or Oct
							boolean render = false;
							if ( null == r.getFrequency() || r.getFrequency().equals(ReportFrequency.WEEKLY) ){
								//weekly report, always render (assuming the scheduler is set up to run weekly!
								render = true;
							}
							else if ( r.getFrequency().equals(ReportFrequency.MONTHLY) ){
								if ( cal.get(Calendar.DAY_OF_MONTH) < 8 ){
									render = true;
								}
							}
							else if ( r.getFrequency().equals(ReportFrequency.QUARTERLY) ){
								if ( cal.get(Calendar.DAY_OF_MONTH) < 8 && 0 == cal.get(Calendar.MONTH) % 3 ){
									render = true;
								}
							}
							
							if(r.getEmailAction() == null || r.getEmailAction().length() == 0){
								render = false;
							}

							if ( render ){
								/*
								 * The reports take sometime to generate, so the saml may need to be refreshed.
								 */
								if (localSA == null) {
									localSA = aaqc.getFullSAMLAssertion(user);
								}
								Date expiry = localSA.getNotOnOrAfter();
								Date now = new Date();
								long thirtySeconds = 30 * 1000;
								now.setTime(now.getTime() + thirtySeconds);
								if (expiry.getTime() < now.getTime()) {
									//SAML has expired
									try {
										if (aaqc != null) {
											sLog.info("Retrieving saml as the one provided is now invalid");
											localSA = aaqc.getFullSAMLAssertion(user);
											if (localSA != null) {
												localsaml = localSA.toString();
											}
										}
									}
									catch (PGSecurityException ex) {
										sLog.error("Problem occurred when trying to refresh saml in generateMgmtReportsForDataSet", ex);
									}
									catch (PGSecurityInvalidSAMLException ex) {
										sLog.error("Problem occurred when trying to refresh saml in generateMgmtReportsForDataSet", ex);
									}
									catch (ConnectException ex) {
										sLog.error("Problem occurred when trying to refresh saml in generateMgmtReportsForDataSet", ex);
									}
									catch (PGSecuritySAMLVerificationException ex) {
										sLog.error("Problem occurred when trying to refresh saml in generateMgmtReportsForDataSet", ex);
									}
									catch (NotAuthorisedFaultMessage ex) {
										sLog.error("Problem occurred when trying to refresh saml in generateMgmtReportsForDataSet", ex);
									}
								}
								/*
								 * create the email list for the report
								 */
								ProjectType project = new ProjectType(null, r.getDataSet().getProjectCode(), null, null, false);
								List<String> emails = retrieveRecipients(r, project, localsaml);

								if ( !emails.isEmpty() ){

									org.psygrid.data.reporting.ManagementReport report = new org.psygrid.data.reporting.ManagementReport();
									report.setTitle(r.getTitle());
									report.setRequestDate(new Date());
									report.setWithRawData(r.isWithRawData());
									report.setShowHeader(r.isShowHeader());

									report.setRecipients(new String[emails.size()]);
									for ( int i=0; i<emails.size(); i++ ){
										report.getRecipients()[i] = emails.get(i);
									}

									List<org.psygrid.data.reporting.Chart> charts = new ArrayList<org.psygrid.data.reporting.Chart>();
									for ( int i=0; i<r.getCharts().size(); i++){
										if (r.getCharts().get(i) instanceof UKCRNSummaryChart) {
											UKCRNSummaryChart c = (UKCRNSummaryChart)r.getCharts().get(i);
											if (c.getStartDate() != null && c.getEndDate() != null) {
												report.setStartDate(c.getStartDate().getTime());
												report.setEndDate(c.getEndDate().getTime());
											}
											charts.add(((IUKCRNSummaryChart)r.getCharts().get(i)).generateChart(session, client, localsaml));
										}
										else if (r.getCharts().get(i) instanceof ReceivingTreatmentChart) {
											ReceivingTreatmentChart c = (ReceivingTreatmentChart)r.getCharts().get(i);
											if (c.getStartDate() != null && c.getEndDate() != null) {
												report.setStartDate(c.getStartDate().getTime());
												report.setEndDate(c.getEndDate().getTime());
											}
											for (org.psygrid.data.reporting.Chart chart: ((IReceivingTreatmentChart)r.getCharts().get(i)).generateChart(session, client, localsaml)) {
												charts.add(chart);
											}
										}
										else {
											org.psygrid.data.reporting.Chart c = r.getCharts().get(i).generateChart(session);
											if ( null != c ){
												charts.add(c);
											}
										}
									}

									report.setCharts(new org.psygrid.data.reporting.Chart[charts.size()]);
									for (int j=0; j < charts.size(); j++) {
										report.getCharts()[j] = charts.get(j);
									}

									reports.add(report);

								}
							}
						}
						catch(DAOException ex){
							String message = "Problem retrieving recipients for report";
							if ( null != r ){
								message = message + " '"+r.getTitle()+"'";
							}
							sLog.error(message, ex);
						}
						catch(ConnectException ex){
							//Re-throw - if we can't connect to the AA or PA for this report it's
							//a fair assumption that we won't be able to connect for any reports
							throw ex;
						}
						catch(Exception ex){
							String message = "Exception when generating report";
							if ( null != r ){
								message = message + " '"+r.getTitle()+"'";
							}
							sLog.error(message, ex);
							// If we are catching something as general as Exception we should re-throw it.
							throw new RuntimeException(message,ex);
						}

					}

					return reports;

				}
				catch (ConnectException ex){
					return ex;
				}
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof ConnectException ){
			throw (ConnectException)result;
		}
		return (List<org.psygrid.data.reporting.ManagementReport>)result;
	}

	@SuppressWarnings("unchecked")
	public List<org.psygrid.data.reporting.ManagementReport> getReportRecipientsForDataSet(final Long dataSetId, final Date date, String saml) 
	throws ConnectException {

		final String sa;

		/*
		 * When this method is called from ReportsJob, the saml assertion 
		 * given is null, so needs to be retrieved from the aaqc.
		 */
		if (aaqc == null) {
			sLog.info("AAQC is null");
		}
		if (saml == null && aaqc != null) {
			SAMLAssertion s = null;
			try {
				sLog.info("Retrieving saml as none was provided");
				s = aaqc.getSAMLAssertion();
			}
			catch (PGSecurityException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (PGSecurityInvalidSAMLException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (ConnectException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (PGSecuritySAMLVerificationException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (NotAuthorisedFaultMessage ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}

			if (s != null) {
				sa = s.toString();
			}
			else {
				sa = saml;
			}
		}
		else {
			sa = saml;
		}

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				try{

					Iterator results = session.createQuery("from ManagementReport r where r.dataSet.id=?")
					.setLong(0, dataSetId)
					.iterate();

					List<org.psygrid.data.reporting.Report> reports = new ArrayList<org.psygrid.data.reporting.Report>();

					while ( results.hasNext() ){

						org.psygrid.data.reporting.definition.hibernate.ManagementReport r = null;
						try{

							r = (org.psygrid.data.reporting.definition.hibernate.ManagementReport)results.next();

							org.psygrid.data.reporting.ManagementReport report = new org.psygrid.data.reporting.ManagementReport();
							report.setTitle(r.getTitle());
							/*
							 * create the email list for the report
							 */
							ProjectType project = new ProjectType(null, r.getDataSet().getProjectCode(), null, null, false);
							List<String> emails = retrieveRecipients(r, project, sa);
							report.setRecipients(emails.toArray(new String[emails.size()]));
							reports.add(report);
						}
						catch(DAOException ex){
							String message = "Problem retrieving recipients for report";
							if ( null != r ){
								message = message + " '"+r.getTitle()+"'";
							}
							sLog.error(message, ex);
						}
						catch(ConnectException ex){
							//Re-throw - if we can't connect to the AA or PA for this report it's
							//a fair assumption that we won't be able to connect for any reports
							throw ex;
						}
						catch(Exception ex){
							String message = "Exception when generating report";
							if ( null != r ){
								message = message + " '"+r.getTitle()+"'";
							}
							sLog.error(message, ex);
						}

					}

					return reports;

				}
				catch (ConnectException ex){
					return ex;
				}
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof ConnectException ){
			throw (ConnectException)result;
		}
		return (List<org.psygrid.data.reporting.ManagementReport>)result;
	}

	@SuppressWarnings("unchecked")
	private org.psygrid.data.reporting.ManagementReport getRecipientsForReport(final Long reportId, final Date date, String saml) 
	throws ConnectException {

		final String sa;

		/*
		 * When this method is called from ReportsJob, the saml assertion 
		 * given is null, so needs to be retrieved from the aaqc.
		 */
		if (aaqc == null) {
			sLog.info("AAQC is null");
		}
		if (saml == null && aaqc != null) {
			SAMLAssertion s = null;
			try {
				sLog.info("Retrieving saml as none was provided");
				s = aaqc.getSAMLAssertion();
			}
			catch (PGSecurityException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (PGSecurityInvalidSAMLException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (ConnectException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (PGSecuritySAMLVerificationException ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}
			catch (NotAuthorisedFaultMessage ex) {
				sLog.error("Problem occurred when trying to retrieve saml in generateMgmtReportsForDataSet", ex);
			}

			if (s != null) {
				sa = s.toString();
			}
			else {
				sa = saml;
			}
		}
		else {
			sa = saml;
		}

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				try{

					ManagementReport r = null;
					org.psygrid.data.reporting.ManagementReport report = null;

					try{				

						r = (ManagementReport)session.createQuery("from ManagementReport r where r.id=?")
						.setLong(0, reportId)
						.uniqueResult();

						if ( null == r ){
							throw new Exception("No report found for id "+reportId);
						}

						report = new org.psygrid.data.reporting.ManagementReport();
						report.setTitle(r.getTitle());
						/*
						 * create the email list for the report
						 */
						ProjectType project = new ProjectType(null, r.getDataSet().getProjectCode(), null, null, false);
						List<String> emails = retrieveRecipients(r, project, sa);
						report.setRecipients(emails.toArray(new String[emails.size()]));
					}
					catch(DAOException ex){
						String message = "Problem retrieving recipients for report";
						if ( null != r ){
							message = message + " '"+r.getTitle()+"'";
						}
						sLog.error(message, ex);
					}
					catch(ConnectException ex){
						//Re-throw - if we can't connect to the AA or PA for this report it's
						//a fair assumption that we won't be able to connect for any reports
						throw ex;
					}
					catch(Exception ex){
						String message = "Exception when generating report";
						if ( null != r ){
							message = message + " '"+r.getTitle()+"'";
						}
						sLog.error(message, ex);
					}


					return report;

				}
				catch (ConnectException ex){
					return ex;
				}
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof ConnectException ){
			throw (ConnectException)result;
		}
		return (org.psygrid.data.reporting.ManagementReport)result;
	}

	public void removeReports(Long dataSetId, String projectCode) throws DAOException {
		//Delete all Reports for a given DataSet
		try {
			removeFrom(dataSetId, projectCode, "Report");
		}
		catch(DAOException ex){
			throw new DAOException("Could not remove records",ex);
		} 

	}

	public List<org.psygrid.data.model.dto.GroupDTO> getGroupsForCodes(long dsId, List<String> groupCodes) throws DAOException {
		List<org.psygrid.data.model.dto.GroupDTO> groups = new ArrayList<org.psygrid.data.model.dto.GroupDTO>();

		for (String code: groupCodes) {
			List result = getHibernateTemplate().find(
					"from Group g "+
					"where g.name=? and g.dataSet.id=?", new Object[]{code, dsId});

			if (result.size() != 1) {
				throw new DAOException("No group found in Repository for the group code "+code);
			}
			if ( result instanceof DAOException){
				throw (DAOException)result;
			}
			org.psygrid.data.model.hibernate.Group group = (org.psygrid.data.model.hibernate.Group)result.get(0);
			Map<org.psygrid.data.model.hibernate.Persistent, PersistentDTO> dtoRefs = new HashMap<org.psygrid.data.model.hibernate.Persistent, PersistentDTO>();
			groups.add(group.toDTO(dtoRefs, RetrieveDepth.DS_SUMMARY));
		}

		return groups;
	}

	private void removeFrom(Long dataSetId, String projectCode, String tablename) throws DAOException{

		final Long dataSetIdentifier = dataSetId;
		final String dataSetProjectCode = projectCode;
		final String table = tablename;
		Object result;
		List records;

		if (dataSetId == null || projectCode == null) {
			throw new DAOException("Data Set identifier or project code is null.");
		}

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				Object record = (Object)session.createQuery("from "+ table + " t where t.dataSet.id=? and t.dataSet.projectCode=?")
				.setLong(0, dataSetIdentifier)
				.setString(1, dataSetProjectCode)
				.list();

				return record;
			}
		};

		try{
			result = (Object)getHibernateTemplate().execute(callback);
			if ( result instanceof DAOException){
				throw (DAOException)result;
			}
		}
		catch(DAOException ex){
			throw new DAOException("Could not retrieve records",ex);
		} 

		records = (List) result;

		if (! records.isEmpty()) {
			getHibernateTemplate().deleteAll(records);
		}
	}

	public void deleteReport(Long dataSetId, Long reportId) throws DAOException {
		final Long dataSetIdentifier = dataSetId;
		final Long reportIdentifier = reportId;
		Object result;
		List records;

		if (dataSetId == null) {
			throw new DAOException(
			"Data Set identifier is null.");
		}

		HibernateCallback callback = new HibernateCallback() {
			public Object doInHibernate(Session session) {
				Object record = (Object) session
				.createQuery(
				"from Report t where t.dataSet.id=? and t.id=?")
				.setLong(0, dataSetIdentifier).setLong(1,
						reportIdentifier).list();
				return record;
			}
		};

		try {
			result = (Object) getHibernateTemplate().execute(callback);
			if (result instanceof DAOException) {
				throw (DAOException) result;
			}
		} catch (DAOException ex) {
			throw new DAOException("Could not retrieve records", ex);
		}

		records = (List) result;

		if (!records.isEmpty()) {
			getHibernateTemplate().deleteAll(records);
		}

	}


	/**
	 * Check that each group in the list of requested groups is in the list of allowed groups.
	 * 
	 * @param requestedGroups
	 * @param allowedGroupCodes
	 * @return true
	 * @throws DAOException
	 */
	private boolean checkGroups(List<Group> requestedGroups, List<String> allowedGroupCodes) {

		if (requestedGroups == null || requestedGroups.size() == 0) {
			return true;
		}
		if (allowedGroupCodes == null || allowedGroupCodes.size() == 0) {
			return false;
		}

		for (Group g: requestedGroups) {
			Group group = (Group)g;
			boolean found = false;
			for (String groupCode: allowedGroupCodes) {
				if (group.getName().equals(groupCode)) {
					found = true;
				}
			}
			if (! found) {
				sLog.info("A chart containg group "+group.getName()+" was requested but did not feature in the list of allowed groups provided.");
				return false;
			}
		}
		//all groups must have been found if we get to this point
		return true;
	}

	/**
	 * Fetch a list of email recipients for the given management report.
	 * 
	 * @param report
	 * @param project
	 * @param saml
	 * @return
	 * @throws ConnectException
	 * @throws NotAuthorisedFault
	 * @throws DAOException
	 */
	private List<String> retrieveRecipients(ManagementReport report, ProjectType project, String saml)
	throws ConnectException, DAOException {

		List<String> emailList = new ArrayList<String>();

		List<Group> groups = report.getGroups();
		if (report.getGroups() == null || report.getGroups().isEmpty() ) {
			groups = report.getDataSet().getGroups();
		}

		//Get all the users for this project.
		try{
			List<String> users = Arrays.asList(aaqc.getUsersInProject(project));
			
			Map<String, String> userMap = new HashMap<String, String>();
			
			for(String user: users){
				String samlAssert = aaqc.getSAMLAssertion(user, project);
				userMap.put(user, samlAssert);
			}
			
			//Cycle through the users list. First, check the user to see if they can perform this action for ANY target. If they can, then add them to the emails list.
			//If not, then check to see that they can perform this action for EACH and EVERY group. If they can, then add them to the emails list.
			
			String reportAction = report.getEmailAction();
			RBACAction rbacAction = RBACAction.valueOf(reportAction);
			ActionType action = rbacAction.toActionType();
			
			Set<String> userMapKeys = userMap.keySet();
			for(String user : userMapKeys){
				
				boolean approved = true;
				
				try{
					aaqc.authoriseUser(user, rbacAction.toAEFAction(), project, new GroupType(), userMap.get(user));
				}catch (NotAuthorisedFault e){
					//Do nothing - just continue
					approved = false;
				}
				
				if(!approved){
					//Go check for authorisation for each and every group.
					boolean allApproved = true;
					for(Group g: groups){
						try{
							String centreCode = g.getName();
							aaqc.authoriseUser(user, rbacAction.toAEFAction(), project, new GroupType(null, centreCode, null), userMap.get(user));
						}
						catch(NotAuthorisedFault e){
							allApproved = false;
							break;
						}
						
					}
					
					if(allApproved){
						//Add the user's email address to the list
						try{
							InternetAddress email = aaqc.lookUpEmailAddress(user);
							if ( null != email ){
								emailList.add(email.getAddress());
							}
							else{
								sLog.info("generateMgmtReportsForDataSet: no email address for user '"+user+"'");
							}
						}
						catch(PGSecurityException ex){
							sLog.error("Unable to look up email address for user='"+user+"'",ex);
						}
					}
					
				}else{
					//Add the user's email address to the list.
					try{
						InternetAddress email = aaqc.lookUpEmailAddress(user);
						if ( null != email ){
							emailList.add(email.getAddress());
						}
						else{
							sLog.info("generateMgmtReportsForDataSet: no email address for user '"+user+"'");
						}
					}
					catch(PGSecurityException ex){
						sLog.error("Unable to look up email address for user='"+user+"'",ex);
					}
				}
				
			}
		}catch (RemoteException e) {
			sLog.error("Problem when communicating with the PA ", e);
		}
		catch (PGSecurityException e) {
			sLog.error("Problem retrieving email addresses for report ", e);
		}
		catch (PGSecuritySAMLVerificationException e) {
			sLog.error("Problem retrieving email addresses for report ", e);
		}
		catch (PGSecurityInvalidSAMLException e) {
			sLog.error("Problem retrieving email address for report ", e);
		}
		
		return emailList;
	
		
		//The users able to recieve this report, according to its RBACAction
		/*

		List<String> users = new ArrayList<String>();
		Map<String,String> emails = new HashMap<String,String>();

		if ( null == aaqc ){
			sLog.info("Attribute authority query client has not been initialised.");
		}
		else{
			try {
				String reportAction = report.getEmailAction();
				RBACAction rbacAction = RBACAction.valueOf(reportAction);
				ActionType action = rbacAction.toActionType();
				//get the rules applicable for the action belonging to this report
				CompositeRuleType[] rules = null; //paqc.getRulesForAction(project, action, saml);

				if (rules == null || rules.length == 0) {
					sLog.error("Report action does not have any rules specified in the PA");
					throw new DAOException("Report action does not have any rules specified in the PA");
				}

				//Retrieve users with the permission to excute the rules
				for (CompositeRuleType crt: rules) {
					String[] usersForRule = aaqc.getUsersInProjectWithPermission(project, crt);
					if (usersForRule!= null) {
						for (String user : usersForRule) {
							users.add(user);
						}
					}

					if (usersForRule == null || usersForRule.length == 0) {
						sLog.warn("No users found in "+project.getIdCode()+" for the rule "+reportAction+" ("+crt.toString()+")" +
								" in the report "+report.getTitle());
					}
				}

				//Retrieve users with the permission to excute the rules for specific groups
				Map<String,Integer> groupUsers = new HashMap<String,Integer>();
				for (Group g: groups) {
					if (g != null && g.getName() != null) {
						GroupType group = new GroupType(null, g.getName(), null);
						CompositeRuleType[] rulesGroup = null; //paqc.getRulesForAction(project, group, action, saml);

						for (CompositeRuleType crt: rulesGroup) {
							String[] usersForRule = aaqc.getUsersInProjectWithPermission(project, crt);
							if (usersForRule!= null) {
								for (String user : usersForRule) {
									int groupTotal = 1;
									if (groupUsers.containsKey(user)) {
										groupTotal += groupUsers.get(user);
									}
									groupUsers.put(user, new Integer(groupTotal));									
								}
							}

							if (usersForRule == null || usersForRule.length == 0) {
								sLog.warn("No users found in "+project.getIdCode()+" for the rule "+reportAction+" ("+crt.toString()+")" +
										" in the report "+report.getTitle());
							}
						}
					}
				}

				// any users found who don't have access to ALL of the report's groups
				for (String user: groupUsers.keySet()) {
					if (groupUsers.get(user) == groups.size()) {
						users.add(user);
					}
				}

				//If user is allowed to access all groups in the report then retrieve their email address
				for ( String user: users){
					try{
						InternetAddress email = aaqc.lookUpEmailAddress(user);
						if ( null != email ){
							emails.put(email.getAddress(), "");
						}
						else{
							sLog.info("generateMgmtReportsForDataSet: no email address for user '"+user+"'");
						}
					}
					catch(PGSecurityException ex){
						sLog.error("Unable to look up email address for user='"+user+"'");
					}
				}
			}
			catch (RemoteException e) {
				sLog.error("Problem when communicating with the PA ", e);
			}
			catch (PGSecurityException e) {
				sLog.error("Problem retrieving email addresses for report ", e);
			}
			catch (PGSecuritySAMLVerificationException e) {
				sLog.error("Problem retrieving email addresses for report ", e);
			}
			catch (PGSecurityInvalidSAMLException e) {
				sLog.error("Problem retrieving email address for report ", e);
			}
		}

		for (String email: emails.keySet()) {
			emailList.add(email);
		}

		return emailList;
		*/
	}

	public MinimalEntry[] getEntriesForBasicStatsChart(final long documentId) throws DAOException{

		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){
				//find the document
				Document doc = (Document)session.createQuery("from Document d where id=?").setLong(0, documentId).uniqueResult();
				if ( null == doc ){
					return new DAOException("No document exists for the given id ("+documentId+")");
				}

				List<MinimalEntry> entries = new ArrayList<MinimalEntry>();
				for ( Entry e: doc.getEntries() ){
					if ( e.isForBasicStatistics() ){
						entries.add(new MinimalEntry(e.getId(), e.getDisplayText(), e.getAccessAction()));
					}
				}

				return entries.toArray(new MinimalEntry[entries.size()]);
			}
		};

		Object result = getHibernateTemplate().execute(callback);
		if ( result instanceof DAOException ){
			throw (DAOException)result;
		}
		return (MinimalEntry[])result;
	}

	public org.psygrid.data.reporting.ManagementReport generateMgmtReportById(final long id, final String user){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session){

				org.psygrid.data.reporting.definition.hibernate.ManagementReport r = 
					(org.psygrid.data.reporting.definition.hibernate.ManagementReport)
						session.createQuery("from ManagementReport r where r.id=?")
							   .setLong(0, id)
							   .uniqueResult();
				
				org.psygrid.data.reporting.ManagementReport report = new org.psygrid.data.reporting.ManagementReport();
				report.setTitle(r.getTitle());
				report.setRequestDate(new Date());
				report.setWithRawData(r.isWithRawData());
				report.setShowHeader(r.isShowHeader());

				String localsaml = null;
				try {
					if (aaqc != null) {
						localsaml = aaqc.getFullSAMLAssertion(user).toString();
					}
				}
				catch (PGSecurityException ex) {
					sLog.error("Problem occurred when trying to refresh saml in generateMgmtReportsForDataSet", ex);
				}
				catch (PGSecurityInvalidSAMLException ex) {
					sLog.error("Problem occurred when trying to refresh saml in generateMgmtReportsForDataSet", ex);
				}
				catch (ConnectException ex) {
					sLog.error("Problem occurred when trying to refresh saml in generateMgmtReportsForDataSet", ex);
				}
				catch (PGSecuritySAMLVerificationException ex) {
					sLog.error("Problem occurred when trying to refresh saml in generateMgmtReportsForDataSet", ex);
				}
				catch (NotAuthorisedFaultMessage ex) {
					sLog.error("Problem occurred when trying to refresh saml in generateMgmtReportsForDataSet", ex);
				}
				
				
				List<org.psygrid.data.reporting.Chart> charts = new ArrayList<org.psygrid.data.reporting.Chart>();
				for ( int i=0; i<r.getCharts().size(); i++){
					if (r.getCharts().get(i) instanceof UKCRNSummaryChart) {
						UKCRNSummaryChart c = (UKCRNSummaryChart)r.getCharts().get(i);
						if (c.getStartDate() != null && c.getEndDate() != null) {
							report.setStartDate(c.getStartDate().getTime());
							report.setEndDate(c.getEndDate().getTime());
						}
						charts.add(((IUKCRNSummaryChart)r.getCharts().get(i)).generateChart(session, client, localsaml));
					}
					else if (r.getCharts().get(i) instanceof ReceivingTreatmentChart) {
						ReceivingTreatmentChart c = (ReceivingTreatmentChart)r.getCharts().get(i);
						if (c.getStartDate() != null && c.getEndDate() != null) {
							report.setStartDate(c.getStartDate().getTime());
							report.setEndDate(c.getEndDate().getTime());
						}
						for (org.psygrid.data.reporting.Chart chart: ((IReceivingTreatmentChart)r.getCharts().get(i)).generateChart(session, client, localsaml)) {
							charts.add(chart);
						}
					}
					else {
						org.psygrid.data.reporting.Chart c = r.getCharts().get(i).generateChart(session);
						if ( null != c ){
							charts.add(c);
						}
					}
				}

				report.setCharts(new org.psygrid.data.reporting.Chart[charts.size()]);
				for (int j=0; j < charts.size(); j++) {
					report.getCharts()[j] = charts.get(j);
				}

				return report;
				
			}
		};
		
		return (org.psygrid.data.reporting.ManagementReport)getHibernateTemplate().execute(callback);

	}
	
}
