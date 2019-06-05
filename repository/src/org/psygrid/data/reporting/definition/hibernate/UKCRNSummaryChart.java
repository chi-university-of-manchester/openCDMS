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

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.reporting.ChartRow;
import org.psygrid.data.reporting.ChartSeries;
import org.psygrid.data.reporting.definition.IUKCRNSummaryChart;
import org.psygrid.data.utils.esl.EslException;
import org.psygrid.data.utils.esl.IRemoteClient;
import org.psygrid.randomization.NotAuthorisedFault;
import org.psygrid.randomization.UnknownRandomizerFault;
import org.psygrid.randomization.client.RandomizationClient;

/**
 * Class to represent a chart in a management report that displays
 * a UKCRN accural report for a given study
 * 
 * See bug 641 for report requirements.
 * 
 * @author Lucy Bridges
 *
 * @hibernate.joined-subclass table="t_ukcrn_summ_charts"
 * 						proxy="org.psygrid.data.reporting.definition.hibernate.UKCRNSummaryChart"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class UKCRNSummaryChart extends ManagementChart implements IUKCRNSummaryChart {
	
	private enum StudyEntryPoint{
		UNSPECIFIED,
		REGISTRATION,
		RANDOMISATION
	}

	private static Log sLog = LogFactory.getLog(UKCRNSummaryChart.class);
	/**
	 * The period of time the chart is to cover
	 */
	private Calendar startDate = new GregorianCalendar(0, 0, 0);
	private Calendar endDate   = new GregorianCalendar(0, 0, 0);

	private IRemoteClient client = null;
	
	private RandomizationClient randClient = new RandomizationClient();
	
	private String studyEntryPoint = StudyEntryPoint.UNSPECIFIED.toString();

	/**
	 * Default entry to be used for a blank or null result, should
	 * not be required as this is also set in RemoteClient.
	 */
	private static final String ENTRY_DEFAULT = "Unknown";

	/**
	 * Get the client used to interface with the ESL and retrieve
	 * information required by the UKCRN
	 * 
	 * @return the client
	 */
	public IRemoteClient getClient() {
		return client;
	}

	/**
	 * Set the client used to interface with the ESL and retrieve
	 * information required by the UKCRN
	 * 
	 * @param client the client to set
	 */
	public void setClient(IRemoteClient client) {
		this.client = client;
	}

	public UKCRNSummaryChart() {
		super();
	}

	public UKCRNSummaryChart(String type, String title) {
		super(type, title);
	}

	/**
	 * Get the chart's end date
	 * 
	 * @return the endDate
	 * 
	 * @hibernate.property column="c_end_date" type="java.util.Calendar"
	 */
	public Calendar getEndDate() {
		return endDate;
	}

	/**
	 * Set the chart's end date
	 * 
	 * Ignores fields other than month and year
	 * 
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Calendar endDate) {
		if ( endDate == null ){
			this.endDate = new GregorianCalendar(0, 0, 0);
		}
		else {
			endDate.clear(Calendar.MILLISECOND);
			endDate.clear(Calendar.SECOND);
			endDate.clear(Calendar.MINUTE);
			endDate.clear(Calendar.HOUR_OF_DAY);
			endDate.clear(Calendar.DATE);
			endDate.clear(Calendar.DAY_OF_MONTH);
			endDate.setTimeZone(TimeZone.getTimeZone("GMT"));
			this.endDate = endDate;
		}
	}

	/**
	 * Get the chart's start date
	 * 
	 * @return the startDate
	 * 
	 * @hibernate.property column="c_start_date" type="java.util.Calendar"
	 */
	public Calendar getStartDate() {
		return startDate;
	}

	/**
	 * Set the start date for the chart
	 * 
	 * Fields other than month and year are ignored
	 * 
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Calendar startDate) {
		if ( startDate == null ){
			this.startDate = new GregorianCalendar(0, 0, 0);
		}
		else {
			startDate.clear(Calendar.MILLISECOND);
			startDate.clear(Calendar.SECOND);
			startDate.clear(Calendar.MINUTE);
			startDate.clear(Calendar.HOUR_OF_DAY);
			startDate.clear(Calendar.DATE);
			startDate.clear(Calendar.DAY_OF_MONTH);
			startDate.setTimeZone(TimeZone.getTimeZone("GMT"));
			this.startDate = startDate;
		}
	}

	/**
	 * Set the period of time the chart is to cover.
	 * 
	 * Note: fields other than month and year are ignored
	 * 
	 * 
	 * @param startDate
	 * @param endDate
	 */
	public void setTimePeriod(Calendar startDate, Calendar endDate) {
		this.setEndDate(endDate);
		this.setStartDate(startDate);
	}

	
	protected void setAllowedStates() {
		allowedStates.add(GenericState.ACTIVE.toString());
		allowedStates.add(GenericState.COMPLETED.toString());
		//allowedStates.add(GenericState.REFERRED.toString());
		allowedStates.add(GenericState.INACTIVE.toString());
		allowedStates.add(GenericState.LEFT.toString());
	}

	
	public org.psygrid.data.reporting.Chart generateChart(Session session) {
		return generateChart(session, null, null);
	}
	
	private org.psygrid.data.reporting.Chart generateChartForRegistrationStudyEntryPoint(Session session, IRemoteClient client, String saml){
		if (client != null) {
			setClient(client);
		}

		Calendar startDate = getStartDate();
		Calendar endDate   = getEndDate();
		
		/*	
		 * If dates haven't been set or have been set incorrectly, then
		 * use a default value.
		 * (If dates have been set then the Minute field will have been
		 * set to zero) 
		 */
		if (startDate.get(Calendar.YEAR) == 0001
				|| endDate.get(Calendar.YEAR) == 0001
				|| startDate.get(Calendar.YEAR) == 0002
				|| endDate.get(Calendar.YEAR) == 0002
				|| endDate.before(startDate)) {
			Calendar[] dates = getFinancialYear();
			startDate = dates[0];
			endDate   = dates[1];
		}
		
		//Retrieve the dataset and therefore project info
		DataSet ds = this.getReport().getDataSet();

		//Retrieve from the AA
		//We can't use the details stored in the repository as this may not match the UKCRN assigned details
		String[] ukCRNProject = getProjectDetails(ds.getProjectCode(), saml); 
		String ukCRNProjectId   = null; 
		String ukCRNProjectCode = null;

		try {
			ukCRNProjectId   = ukCRNProject[0];
			ukCRNProjectCode = ukCRNProject[1];
		}
		catch (NullPointerException npe) {
			//do nothing
			ukCRNProjectId   = ENTRY_DEFAULT;
			ukCRNProjectCode = ENTRY_DEFAULT;
		}

		//Create date for database comparison (add one to month to find dates within the current month)
		Calendar dbEndDate = (Calendar)endDate.clone();
		dbEndDate.add(Calendar.MONTH, 1);

		Boolean useExternalID = (Boolean)session.createQuery("select d.useExternalIdAsPrimary from DataSet d where d.id=:id ")
		.setLong("id", ds.getId())
		.uniqueResult();

		//Used to populate the report with registration details of all subjects
		//Retrieve the record status long name to ignore all ieligable records
//		String hql = "select r.id, r.identifier, r.identifier.groupPrefix, s.siteId, s.siteName, t.enumGenericState, r.consultant, s.id, rd.studyEntryDate " +
//		", r.externalIdentifier " +
//		"from Record r " +
//		"left join r.site s " + 
//		"left join r.dataSet d " +
//		"left join r.status t " +
//		"left join r.theRecordData rd " +
//		"where d.id=:id " +
//		"and r.created between :start and :end " +
//		"and r.deleted=:deleted";
		
		String hql = "select r.id, r.identifier, r.identifier.groupPrefix, s.siteId, s.siteName, t.enumGenericState, r.consultant, s.id, rd.studyEntryDate " +
		", r.externalIdentifier " +
		"from Record r " +
		"left join r.site s " + 
		"left join r.dataSet d " +
		"left join r.status t " +
		"left join r.theRecordData rd " +
		"where d.id=:id " +
		"and r.history[0].when between :start and :end " +
		"and r.deleted=:deleted";
				
		// Conditionally add an order by clause
		if(useExternalID){
			hql+=" order by r.externalIdentifier";
		}

		//Get all records for dataset
		List results = session.createQuery(hql)
		.setLong("id", ds.getId())
		.setCalendar("start", startDate)
		.setCalendar("end", dbEndDate)
		.setBoolean("deleted", false)
		.list();

		org.psygrid.data.reporting.Chart chart = new org.psygrid.data.reporting.Chart();
		chart.setTitle(this.title);
		chart.setRangeAxisLabel(this.rangeAxisLabel);
		chart.setUsePercentages(usePercentages);
		String[] types = new String[this.types.size()];
		for ( int i=0; i<this.types.size(); i++ ){
			types[i] = this.types.get(i);
		}
		chart.setTypes(types);

		List<ChartRow> rows = new ArrayList<ChartRow>();

		int randEventsTotal = 0;	//total number of randomisations counter

		Map<String,String> groupCodes = new HashMap<String,String>();
		for ( int i=0; i<results.size(); i++ ) {
			Object[] data  = (Object[])results.get(i);
			String groupCode  = (String)data[2];

			if (groupCode != null && !groupCode.equals("")) {
				groupCodes.put(groupCode, "");
			}
		}

		//Retrieve the PI Names for each group
		Map<String,String> piNames = getPINamesForGroups(groupCodes, ds.getProjectCode(), saml);

		//For each result (i.e record, representing the data on an individual subject)
		//populate the table rows
		for ( int i=0; i<results.size(); i++ ) {

			Object[] data  = (Object[])results.get(i);

			String recordStatus = (String)data[5];

			if ( recordEligible(recordStatus)) {

				/* retrieve the subject's study entry date (this can vary depending on the study) */
				Date studyEntryDate = (Date)data[8];
				String created = ENTRY_DEFAULT;
				if ( null != studyEntryDate ){
					created = parseDate(studyEntryDate.toString());
				}
				
				/* parse the results given by the database query */
				String identifier = ((Identifier)data[1]).getIdentifier();
				String groupCode  = (String)data[2];	
				String siteId	  = (String)data[3];
				String siteName   = (String)data[4];

				String consultant = (String)data[6];
				Long siteUniqueId = (Long)data[7];
				
				String externalID = (String)data[9];
				
				if (consultant == null || consultant.equals("")) {
					
					if (siteUniqueId != null && !"".equals(siteId)) {
						
						String getSite = "from Site s where s.id=:id";
						Site s = (Site)session.createQuery(getSite)
						.setLong("id", siteUniqueId).uniqueResult();
						if (s != null) {
							if (s.getConsultants() != null && s.getConsultants().size() == 1) {
								consultant = (String)s.getConsultants().get(0);
							}
						}
					}
					
				}
				String investigatorName = consultant;
				//if the consultant has not been found after checking the site above, then
				//use the principal investigator
				if (investigatorName == null || investigatorName.equals("")) {
					investigatorName = piNames.get(groupCode);
				}

				if (siteId == null || siteId.equals("")) {
					siteId = ENTRY_DEFAULT;
				}
				if (groupCode == null || groupCode.equals("")) {
					groupCode = ENTRY_DEFAULT;
				}
				if (siteName == null || siteName.equals("")) {
					siteName = ENTRY_DEFAULT;
				}
				if (investigatorName == null || investigatorName.equals("")) {
					investigatorName = ENTRY_DEFAULT;
				}


				String entryEvent = "Registration";
				String entryEventNo = "1";
				String recruitType  = "1";


				/* create a new row and all the required series entries */
				ChartRow row = new ChartRow();

				String[] vars = new String[12];	//create array to pass to insertRow
				vars[0] = ukCRNProjectCode;
				vars[1] = ukCRNProjectId;
				vars[2] = investigatorName;
				vars[3] = "";			//investigator id (intentionally blank)
				vars[4] = siteName;
				vars[5] = siteId;

				// conditionally report externalID
				String reportedID = useExternalID?externalID:identifier;
				vars[6] = reportedID;
				
				vars[7] = created;		//study entry date 
				vars[8] = entryEvent;
				vars[9] = entryEventNo;
				vars[10] = recruitType;
				vars[11] = Integer.toString(i+1);	//running total (number of records) 

				row = insertRow(row, vars);
				rows.add(row);

			}	//if eligable
		} //get records

		chart.setRows(new ChartRow[rows.size()]);
		for (int i = 0; i < rows.size(); i++) {
			chart.getRows()[i] = rows.get(i);
		}

		return chart;		
		
	}
	
	private org.psygrid.data.reporting.Chart generateChartForRandomisedStudyEntryPoint(Session session, IRemoteClient client, String saml){
		if (client != null) {
			setClient(client);
		}
		
		Calendar startDate = getStartDate();
		Calendar endDate = getEndDate();
		
		/*	
		 * If dates haven't been set or have been set incorrectly, then
		 * use a default value.
		 * (If dates have been set then the Minute field will have been
		 * set to zero) 
		 */
		if (startDate.get(Calendar.YEAR) == 0001
				|| endDate.get(Calendar.YEAR) == 0001
				|| startDate.get(Calendar.YEAR) == 0002
				|| endDate.get(Calendar.YEAR) == 0002
				|| endDate.before(startDate)) {
			Calendar[] dates = getFinancialYear();
			startDate = dates[0];
			endDate   = dates[1];
		}
		
		//Retrieve the dataset and therefore project info
		DataSet ds = this.getReport().getDataSet();
		
		//Retrieve from the AA
		//We can't use the details stored in the repository as this may not match the UKCRN assigned details
		String[] ukCRNProject = getProjectDetails(ds.getProjectCode(), saml); 
		String ukCRNProjectId   = null; 
		String ukCRNProjectCode = null;

		try {
			ukCRNProjectId   = ukCRNProject[0];
			ukCRNProjectCode = ukCRNProject[1];
		}
		catch (NullPointerException npe) {
			//do nothing
			ukCRNProjectId   = ENTRY_DEFAULT;
			ukCRNProjectCode = ENTRY_DEFAULT;
		}
		
		Boolean useExternalID = (Boolean)session.createQuery("select d.useExternalIdAsPrimary from DataSet d where d.id=:id ")
				.setLong("id", ds.getId())
				.uniqueResult();
		
		//Create date for database comparison (add one to month to find dates within the current month)
		Calendar dbEndDate =(Calendar) endDate.clone();
		dbEndDate.add(Calendar.MONTH, 1);
		
		String projectCode = ds.getProjectCode();
		String [] randomisedParticipants = null;
		boolean retrievalSuccess = true;
		try {
			randomisedParticipants = randClient.getRandomisedParticipantsWithinTimeframe(projectCode, startDate, dbEndDate, saml);
		} catch (ConnectException e) {
			sLog.info("Unable to look up data for project='"+projectCode+" "+e.getMessage());
			retrievalSuccess = false;
		} catch (SocketTimeoutException e) {
			sLog.info("Unable to look up data for project='"+projectCode+" "+e.getMessage());
			retrievalSuccess = false;
		} catch (NotAuthorisedFault e) {
			sLog.info("Unable to look up data for project='"+projectCode+" "+e.getMessage());
			retrievalSuccess = false;
		} catch (UnknownRandomizerFault e) {
			sLog.info("Unable to look up data for project='"+projectCode+" "+e.getMessage());
			retrievalSuccess = false;
		}
		
		if(!retrievalSuccess){
			return null;
		}
		
		List<String> randomisedParticipantsList = Arrays.asList(randomisedParticipants);
		
		String hql = "select r.id, r.identifier, r.identifier.groupPrefix, s.siteId, s.siteName, t.enumGenericState, r.consultant, s.id, rd.studyEntryDate, r.externalIdentifier " +
		"from Record r left join r.site s left join r.dataSet d left join r.status t left join r.theRecordData rd where r.identifier.identifier in (:identifiers) and r.deleted=:deleted";
		
		// Conditionally add an order by clause
		if(useExternalID){
			hql+=" order by r.externalIdentifier";
		}

		//Get all records for dataset
		List results = session.createQuery(hql)
		.setBoolean("deleted", false)
		.setParameterList("identifiers", randomisedParticipantsList)
		.list();
		
		org.psygrid.data.reporting.Chart chart = new org.psygrid.data.reporting.Chart();
		chart.setTitle(this.title);
		chart.setRangeAxisLabel(this.rangeAxisLabel);
		chart.setUsePercentages(usePercentages);
		String[] types = new String[this.types.size()];
		for ( int i=0; i<this.types.size(); i++ ){
			types[i] = this.types.get(i);
		}
		chart.setTypes(types);

		List<ChartRow> rows = new ArrayList<ChartRow>();

		int randEventsTotal = 0;	//total number of randomisations counter

		Map<String,String> groupCodes = new HashMap<String,String>();
		for ( int i=0; i<results.size(); i++ ) {
			Object[] data  = (Object[])results.get(i);
			String groupCode  = (String)data[2];

			if (groupCode != null && !groupCode.equals("")) {
				groupCodes.put(groupCode, "");
			}
		}

		//Retrieve the PI Names for each group
		Map<String,String> piNames = getPINamesForGroups(groupCodes, ds.getProjectCode(), saml);

		
		//For each result (i.e record, representing the data on an individual subject)
		//populate the table rows
		for ( int i=0; i<results.size(); i++ ) {

			Object[] data  = (Object[])results.get(i);

			String recordStatus = (String)data[5];

			if ( recordEligible(recordStatus)) {

				Date[] randEvents = getSubjectRandomisationEvents(ds.getProjectCode(), ((Identifier)data[1]).getIdentifier(), saml);

				if(randEvents == null || randEvents.length == 0){
					//We've had it!
					return null;
				}
				
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				String randomisationDate = format.format(randEvents[0]);
				
				/* parse the results given by the database query */
				String identifier = ((Identifier)data[1]).getIdentifier();
				String groupCode  = (String)data[2];	
				String siteId	  = (String)data[3];
				String siteName   = (String)data[4];

				String consultant = (String)data[6];
				Long siteUniqueId = (Long)data[7];
				
				String externalID = (String)data[9];
				
				if (consultant == null || consultant.equals("")) {
					
					if (siteUniqueId != null && !"".equals(siteId)) {
						
						String getSite = "from Site s where s.id=:id";
						Site s = (Site)session.createQuery(getSite)
						.setLong("id", siteUniqueId).uniqueResult();
						if (s != null) {
							if (s.getConsultants() != null && s.getConsultants().size() == 1) {
								consultant = (String)s.getConsultants().get(0);
							}
						}
					}
					
				}
				String investigatorName = consultant;
				//if the consultant has not been found after checking the site above, then
				//use the principal investigator
				if ((investigatorName == null || investigatorName.equals("")) && piNames != null) {
					investigatorName = piNames.get(groupCode);
				}

				if (siteId == null || siteId.equals("")) {
					siteId = ENTRY_DEFAULT;
				}
				if (groupCode == null || groupCode.equals("")) {
					groupCode = ENTRY_DEFAULT;
				}
				if (siteName == null || siteName.equals("")) {
					siteName = ENTRY_DEFAULT;
				}
				if (investigatorName == null || investigatorName.equals("")) {
					investigatorName = ENTRY_DEFAULT;
				}


				String entryEvent = "Randomisation";
				String entryEventNo = "1";
				String recruitType  = "1";


				/* create a new row and all the required series entries */
				ChartRow row = new ChartRow();

				String[] vars = new String[12];	//create array to pass to insertRow
				vars[0] = ukCRNProjectCode;
				vars[1] = ukCRNProjectId;
				vars[2] = investigatorName;
				vars[3] = "";			//investigator id (intentionally blank)
				vars[4] = siteName;
				vars[5] = siteId;

				// conditionally report externalID
				String reportedID = useExternalID?externalID:identifier;
				vars[6] = reportedID;
				
				vars[7] = randomisationDate;		//date of randomisation
				vars[8] = entryEvent;
				vars[9] = entryEventNo;
				vars[10] = recruitType;
				vars[11] = Integer.toString(i+1);	//running total (number of records) 

				row = insertRow(row, vars);
				rows.add(row);
			}	//if eligable
		} //get records
		
		chart.setRows(new ChartRow[rows.size()]);
		for (int i = 0; i < rows.size(); i++) {
			chart.getRows()[i] = rows.get(i);
		}
		
		return chart;
	}
	
	private org.psygrid.data.reporting.Chart generateChartForUnspecifiedStudyEntryPoint(Session session, IRemoteClient client, String saml){
		if (client != null) {
			setClient(client);
		}

		Calendar startDate = getStartDate();
		Calendar endDate   = getEndDate();
		
		/*	
		 * If dates haven't been set or have been set incorrectly, then
		 * use a default value.
		 * (If dates have been set then the Minute field will have been
		 * set to zero) 
		 */
		if (startDate.get(Calendar.YEAR) == 0001
				|| endDate.get(Calendar.YEAR) == 0001
				|| startDate.get(Calendar.YEAR) == 0002
				|| endDate.get(Calendar.YEAR) == 0002
				|| endDate.before(startDate)) {
			Calendar[] dates = getFinancialYear();
			startDate = dates[0];
			endDate   = dates[1];
		}
		
		//Retrieve the dataset and therefore project info
		DataSet ds = this.getReport().getDataSet();

		//Retrieve from the AA
		//We can't use the details stored in the repository as this may not match the UKCRN assigned details
		String[] ukCRNProject = getProjectDetails(ds.getProjectCode(), saml); 
		String ukCRNProjectId   = null; 
		String ukCRNProjectCode = null;

		try {
			ukCRNProjectId   = ukCRNProject[0];
			ukCRNProjectCode = ukCRNProject[1];
		}
		catch (NullPointerException npe) {
			//do nothing
			ukCRNProjectId   = ENTRY_DEFAULT;
			ukCRNProjectCode = ENTRY_DEFAULT;
		}

		//Whether the project uses randomisation
		boolean isRandomised = isProjectRandomised(ds.getProjectCode(), saml);
		
		//If the project is randomised, it is necessary to determine whether the study entry point has been specified (i.e. randomisation or registration).
		//Must make sure to report on only one or the other if this has been specified.
		//If neither has been specified, just allow the old logic to run.

		//Create date for database comparison (add one to month to find dates within the current month)
		Calendar dbEndDate = (Calendar)endDate.clone();
		dbEndDate.add(Calendar.MONTH, 1);
		
		Boolean useExternalID = (Boolean)session.createQuery("select d.useExternalIdAsPrimary from DataSet d where d.id=:id ")
		.setLong("id", ds.getId())
		.uniqueResult();

		//Used to populate the report with registration details of all subjects
		//Retrieve the record status long name to ignore all ieligable records
//		String hql = "select r.id, r.identifier, r.identifier.groupPrefix, s.siteId, s.siteName, t.enumGenericState, r.consultant, s.id, rd.studyEntryDate " +
//		", r.externalIdentifier " +
//		"from Record r " +
//		"left join r.site s " + 
//		"left join r.dataSet d " +
//		"left join r.status t " +
//		"left join r.theRecordData rd " +
//		"where d.id=:id " +
//		"and r.created between :start and :end " +
//		"and r.deleted=:deleted";
		
		String hql = "select r.id, r.identifier, r.identifier.groupPrefix, s.siteId, s.siteName, t.enumGenericState, r.consultant, s.id, rd.studyEntryDate " +
		", r.externalIdentifier " +
		"from Record r " +
		"left join r.site s " + 
		"left join r.dataSet d " +
		"left join r.status t " +
		"left join r.theRecordData rd " +
		"where d.id=:id " +
		"and r.history[0].when between :start and :end " +
		"and r.deleted=:deleted";
		
				
		// Conditionally add an order by clause
		if(useExternalID){
			hql+=" order by r.externalIdentifier";
		}

		//Get all records for dataset
		List results = session.createQuery(hql)
		.setLong("id", ds.getId())
		.setCalendar("start", startDate)
		.setCalendar("end", dbEndDate)
		.setBoolean("deleted", false)
		.list();

		org.psygrid.data.reporting.Chart chart = new org.psygrid.data.reporting.Chart();
		chart.setTitle(this.title);
		chart.setRangeAxisLabel(this.rangeAxisLabel);
		chart.setUsePercentages(usePercentages);
		String[] types = new String[this.types.size()];
		for ( int i=0; i<this.types.size(); i++ ){
			types[i] = this.types.get(i);
		}
		chart.setTypes(types);

		List<ChartRow> rows = new ArrayList<ChartRow>();

		int randEventsTotal = 0;	//total number of randomisations counter

		Map<String,String> groupCodes = new HashMap<String,String>();
		for ( int i=0; i<results.size(); i++ ) {
			Object[] data  = (Object[])results.get(i);
			String groupCode  = (String)data[2];

			if (groupCode != null && !groupCode.equals("")) {
				groupCodes.put(groupCode, "");
			}
		}

		//Retrieve the PI Names for each group
		Map<String,String> piNames = getPINamesForGroups(groupCodes, ds.getProjectCode(), saml);
		
		int runningTotal = 0;		
		//For each result (i.e record, representing the data on an individual subject)
		//populate the table rows
		for ( int i=0; i<results.size(); i++ ) {

			Object[] data  = (Object[])results.get(i);

			String recordStatus = (String)data[5];

			if ( recordEligible(recordStatus)) {
				runningTotal++;
				
				/* retrieve the subject's study entry date (this can vary depending on the study) */
				Date studyEntryDate = (Date)data[8];
				String created = ENTRY_DEFAULT;
				if ( null != studyEntryDate ){
					created = parseDate(studyEntryDate.toString());
				}
				
				/* parse the results given by the database query */
				String identifier = ((Identifier)data[1]).getIdentifier();
				String groupCode  = (String)data[2];	
				String siteId	  = (String)data[3];
				String siteName   = (String)data[4];

				String consultant = (String)data[6];
				Long siteUniqueId = (Long)data[7];
				
				String externalID = (String)data[9];
				
				if (consultant == null || consultant.equals("")) {
					
					if (siteUniqueId != null && !"".equals(siteId)) {
						
						String getSite = "from Site s where s.id=:id";
						Site s = (Site)session.createQuery(getSite)
						.setLong("id", siteUniqueId).uniqueResult();
						if (s != null) {
							if (s.getConsultants() != null && s.getConsultants().size() == 1) {
								consultant = (String)s.getConsultants().get(0);
							}
						}
					}
					
				}
				String investigatorName = consultant;
				//if the consultant has not been found after checking the site above, then
				//use the principal investigator
				if (investigatorName == null || investigatorName.equals("")) {
					investigatorName = piNames.get(groupCode);
				}

				if (siteId == null || siteId.equals("")) {
					siteId = ENTRY_DEFAULT;
				}
				if (groupCode == null || groupCode.equals("")) {
					groupCode = ENTRY_DEFAULT;
				}
				if (siteName == null || siteName.equals("")) {
					siteName = ENTRY_DEFAULT;
				}
				if (investigatorName == null || investigatorName.equals("")) {
					investigatorName = ENTRY_DEFAULT;
				}


				String entryEvent = "Registration";
				String entryEventNo = "1";
				String recruitType  = "1";


				/* create a new row and all the required series entries */
				ChartRow row = new ChartRow();

				String[] vars = new String[12];	//create array to pass to insertRow
				vars[0] = ukCRNProjectCode;
				vars[1] = ukCRNProjectId;
				vars[2] = investigatorName;
				vars[3] = "";			//investigator id (intentionally blank)
				vars[4] = siteName;
				vars[5] = siteId;

				// conditionally report externalID
				String reportedID = useExternalID?externalID:identifier;
				vars[6] = reportedID;
				
				vars[7] = created;		//study entry date or date of randomisation
				vars[8] = entryEvent;
				vars[9] = entryEventNo;
				vars[10] = recruitType;
				vars[11] = Integer.toString(runningTotal);	//running total (number of records) 

				/*
				 * If the project is randomised, it is assumed that there will
				 * be one row per randomisation event. Otherwise, the subject
				 * will have a single row detailing registration (as above). 
				 */
				if (isRandomised) {

					int randCount = Integer.parseInt(entryEventNo);
					Date[] randEvents = getSubjectRandomisationEvents(ds.getProjectCode(), identifier, saml);

					if (randEvents != null && randEvents.length > 0) {

						randEventsTotal += randEvents.length -1;	//counter for total number of randomisations so far (used to increment number of rows)

						//Insert a new row for each randomisation the subject has had
						for (Date randDate: randEvents) {
							SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
							String formatDate = format.format(randDate);

							String[] randVars = vars.clone();
							//Change the study entry date to the date of this randomisation
							randVars[7] = formatDate.toString();
							//Change the entry event
							randVars[8] = "Randomisation";	
							//Increment the entryEventNo (i.e the number of randomisations (starting at 1))
							randVars[9] = Integer.toString(randCount++); 

							//Add an additional row for subject detailing each randomisation event	
							ChartRow row2 = new ChartRow();
							rows.add(row2);
							row2 = insertRow(row2, randVars);
						}
					}
					else {		//subject has not been randomised
						row = insertRow(row, vars);	
						rows.add(row);
					}
				}
				else {			//project does not use randomisation
					row = insertRow(row, vars);
					rows.add(row);
				}

			}	//if eligable
		} //get records

		chart.setRows(new ChartRow[rows.size()]);
		for (int i = 0; i < rows.size(); i++) {
			chart.getRows()[i] = rows.get(i);
		}

		return chart;		
	}
	public org.psygrid.data.reporting.Chart generateChart(Session session, IRemoteClient client, String saml) {
		
		org.psygrid.data.reporting.Chart chart = null;
		StudyEntryPoint p = this.getStudyEntryPointEnum();
		
		switch(p){
		case UNSPECIFIED:
			chart = generateChartForUnspecifiedStudyEntryPoint(session, client, saml);
		break;
		case REGISTRATION:
			chart = this.generateChartForRegistrationStudyEntryPoint(session, client, saml);
		break;
		case RANDOMISATION:
			chart = this.generateChartForRandomisedStudyEntryPoint(session, client, saml);
		break;
		
			
		}
		
		return chart;
		
	}

	private ChartRow insertRow(ChartRow row, String[] vars) {
		row.setSeries(new ChartSeries[16]);

		/* define and populate the report columns */

		//The project identifier as assigned by UKCRN
		ChartSeries studyId = new ChartSeries();
		row.getSeries()[0] = studyId;
		studyId.setLabel("StudyID");
		studyId.setLabelType(IValue.TYPE_STRING);
		studyId.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		studyId.getPoints()[0] = point;
		point.setValue(vars[0]);
		point.setValueType(IValue.TYPE_STRING);		       
		}

		//Acronym used for the project, so the project code
		ChartSeries acronym = new ChartSeries();
		row.getSeries()[1] = acronym;
		acronym.setLabel("Acronym");
		acronym.setLabelType(IValue.TYPE_STRING);
		acronym.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		acronym.getPoints()[0] = point;
		point.setValue(vars[1]);
		point.setValueType(IValue.TYPE_STRING);		       
		}

		//The username of the person who entered the details (and so who created the record?)
		ChartSeries investigatorName = new ChartSeries();
		row.getSeries()[2] = investigatorName;
		investigatorName.setLabel("InvestigatorName");
		investigatorName.setLabelType(IValue.TYPE_STRING);
		investigatorName.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		investigatorName.getPoints()[0] = point;
		point.setValue(vars[2]);
		point.setValueType(IValue.TYPE_STRING);		       
		}

		//This is currently not defined so always left blank
		ChartSeries investigatorId = new ChartSeries();
		row.getSeries()[3] = investigatorId;
		investigatorId.setLabel("InvestigatorID");
		investigatorId.setLabelType(IValue.TYPE_STRING);
		investigatorId.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		investigatorId.getPoints()[0] = point;
		point.setValue(vars[3]);		
		point.setValueType(IValue.TYPE_STRING);		       
		}

		//The hospital/place taking part in the study and the hub name
		ChartSeries siteName = new ChartSeries();
		row.getSeries()[4] = siteName;
		siteName.setLabel("SiteName");
		siteName.setLabelType(IValue.TYPE_STRING);
		siteName.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		siteName.getPoints()[0] = point;
		point.setValue(vars[4]);
		point.setValueType(IValue.TYPE_STRING);		       
		}

		//The UKCRN assigned identifier for the site
		ChartSeries siteId = new ChartSeries();
		row.getSeries()[5] = siteId;
		siteId.setLabel("SiteID");
		siteId.setLabelType(IValue.TYPE_STRING);
		siteId.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		siteId.getPoints()[0] = point;
		point.setValue(vars[5]);
		point.setValueType(IValue.TYPE_STRING);		       
		}

		//The study number
		ChartSeries studyPatientId = new ChartSeries();
		row.getSeries()[6] = studyPatientId;
		studyPatientId.setLabel("StudyPatientID");
		studyPatientId.setLabelType(IValue.TYPE_STRING);
		studyPatientId.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		studyPatientId.getPoints()[0] = point;
		point.setValue(vars[6]);
		point.setValueType(IValue.TYPE_STRING);		       
		}

		//The date the subject was entered into the study, 
		//or the date of last randomisation
		ChartSeries studyEntryDate = new ChartSeries();
		row.getSeries()[7] = studyEntryDate;
		studyEntryDate.setLabel("StudyEntryDate");
		studyEntryDate.setLabelType(IValue.TYPE_STRING);
		studyEntryDate.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		studyEntryDate.getPoints()[0] = point;
		point.setValue(vars[7]);
		point.setValueType(IValue.TYPE_DATE);
		}

		//Value is either 'Registration' or 'Randomisation'
		ChartSeries entryEvent = new ChartSeries();
		row.getSeries()[8] = entryEvent;
		entryEvent.setLabel("EntryEvent");
		entryEvent.setLabelType(IValue.TYPE_STRING);
		entryEvent.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		entryEvent.getPoints()[0] = point;
		point.setValue(vars[8]);	//assumes no randomisations performed
		point.setValueType(IValue.TYPE_STRING);		       
		}

		//Value is 1+number of randomisations
		ChartSeries entryEventNo = new ChartSeries();
		row.getSeries()[9] = entryEventNo;
		entryEventNo.setLabel("EntryEventNo");
		entryEventNo.setLabelType(IValue.TYPE_STRING);
		entryEventNo.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		entryEventNo.getPoints()[0] = point;
		point.setValue(vars[9]);
		point.setValueType(IValue.TYPE_INTEGER);		       
		}

		//Always '1', except for some control/unaffected subjects
		ChartSeries recruitType = new ChartSeries();
		row.getSeries()[10] = recruitType;
		recruitType.setLabel("RecruitType");
		recruitType.setLabelType(IValue.TYPE_STRING);
		recruitType.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		recruitType.getPoints()[0] = point;
		point.setValue(vars[10]);
		point.setValueType(IValue.TYPE_INTEGER);		       
		}

		//Number of subjects entered for this project so far
		ChartSeries runningTotal = new ChartSeries();
		row.getSeries()[11] = runningTotal;
		runningTotal.setLabel("RunningTotal");
		runningTotal.setLabelType(IValue.TYPE_STRING);
		runningTotal.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		runningTotal.getPoints()[0] = point;
		point.setValue(vars[11]);
		point.setValueType(IValue.TYPE_INTEGER);		       
		}

		/* The remaining rows are optional and so ignored as the information is not stored by the repository */
		ChartSeries gender = new ChartSeries();
		row.getSeries()[12] = gender;
		gender.setLabel("Gender");
		gender.setLabelType(IValue.TYPE_STRING);
		gender.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		gender.getPoints()[0] = point;
		point.setValue("");
		point.setValueType(IValue.TYPE_STRING);		       
		}

		ChartSeries dob = new ChartSeries();
		row.getSeries()[13] = dob;
		dob.setLabel("DOB");
		dob.setLabelType(IValue.TYPE_STRING);
		dob.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		dob.getPoints()[0] = point;
		point.setValue("");
		point.setValueType(IValue.TYPE_STRING);		       
		}

		ChartSeries ethnicity = new ChartSeries();
		row.getSeries()[14] = ethnicity;
		ethnicity.setLabel("Ethnicity");
		ethnicity.setLabelType(IValue.TYPE_STRING);
		ethnicity.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		ethnicity.getPoints()[0] = point;
		point.setValue("");
		point.setValueType(IValue.TYPE_STRING);		       
		}

		ChartSeries postcode = new ChartSeries();
		row.getSeries()[15] = postcode;
		postcode.setLabel("Postcode");
		postcode.setLabelType(IValue.TYPE_STRING);
		postcode.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		postcode.getPoints()[0] = point;
		point.setValue("");
		point.setValueType(IValue.TYPE_STRING);
		}
		//======================================

		return row;
	}

	/**
	 * Query the AA to retrieve the UKCRN approved project
	 * details.
	 * 
	 * @param projectCode
	 * @return project id and code
	 */
	private String[] getProjectDetails(String projectCode, String saml) {
		if (client == null) {
			return null;
		}
		return client.getProjectDetails(projectCode, saml);
	}

	/**
	 * Query the ESL to find out if the given project uses
	 * randomisation.
	 * 
	 * @param projectCode
	 * @return boolean
	 */
	private boolean isProjectRandomised(String projectCode, String saml) {
		if (client == null) {
			return false;
		}
		try {
			return client.isProjectRandomised(projectCode, saml);
		}
		catch (EslException e) {
			if (sLog.isInfoEnabled()) {
				sLog.info("Problem occurred when calling 'isProjectRandomised' "+e.getMessage());
			}
			return false;
		}
	}

	/**
	 * Queries the ESL to retrieve the dates (and therefore number)
	 * of randomisations for a given subject
	 * 
	 * @param subjectCode
	 * @return dates of randomisations
	 */
	private Date[] getSubjectRandomisationEvents(String projectCode, String subjectCode, String saml) {
		if ( client == null ) {
			return null;
		}
		try {
			return client.getSubjectRandomisationEvents(projectCode, subjectCode, saml);
		}
		catch (EslException e) {
			if (sLog.isInfoEnabled()) {
				sLog.info("Problem occurred when calling 'getSubjectRandomisationEvents' "+e.getMessage());
			}
			return null;
		}
	}


	/**
	 * Get the names of the Principal Investigator belonging to the given groups
	 * and project, to provide the InvestigatorNamse in the UKCRN report.
	 * 
	 * Will retrieve the first name listed for each group only.
	 * 
	 * @param groupCodes
	 * @param projectCode
	 * @return groupCode and PI names
	 */
	private Map<String,String> getPINamesForGroups(Map<String,String> groupCodes, String projectCode, String saml) {
		if (client == null || groupCodes == null || projectCode == null) {
			return null;
		}

		for (String groupCode: groupCodes.keySet()) {
			String piName = null;

			try {
				piName = client.getUserInRoleForGroup("PrincipalInvestigator", groupCode, projectCode, saml);
			}
			catch (Exception e){
				if (sLog.isInfoEnabled()) {
					sLog.info("Problem retrieving user name for role 'Principal Investigator' in group "+groupCode, e);
				}
			}
			if (piName == null || piName.equals("")) {
				piName = ENTRY_DEFAULT;
			}

			groupCodes.put(groupCode, piName);
		}
		return groupCodes;
	}

	/**
	 * Retrieve the study entry date for a subject. 
	 * 
	 * @param recordId
	 * @param session
	 * @return entryDate
	 */
	private String getStudyEntryDate(long recordId, Session session) {

		//Retrieve record
		Record record = (Record)session.createCriteria(Record.class)
		.add(Restrictions.idEq(recordId))
		.uniqueResult();

		if ( null != record.getStudyEntryDate() ){
			return parseDate(record.getStudyEntryDate().toString());
		}
		
		//If the date is not present provide a default value instead
		return ENTRY_DEFAULT;
	}

	private String parseDate(String date) {

		//The format req'd by the UKCRN
		SimpleDateFormat ukcrnformat = new SimpleDateFormat("dd/MM/yyyy");

		//Try to format the date string using successive date formats 
		try {
			SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");	//typical date returned by DB2
			Date parsedDate = parser.parse(date);
			return ukcrnformat.format(parsedDate);
		}
		catch (ParseException pex) {

			SimpleDateFormat parser = new SimpleDateFormat("dd-MMM-yyyy");		//typical date returned by MySQL
			try {
				Date parsedDate = parser.parse(date);
				return ukcrnformat.format(parsedDate);
			}
			catch (ParseException pe) {
				//Support for partial dates, although the UKCRN probably won't like this.
				SimpleDateFormat partial = new SimpleDateFormat("MMM-yyyy");
				try {
					Date parsedDate = partial.parse(date);
					SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");
					return sdf2.format(parsedDate);
				}
				catch (ParseException e) {
					return date;			//give up and return original
				}
			}
		}
	}

	/**
	 * Records with a status of ineligable or not consented are not included 
	 * in the UKCRN chart.
	 * 
	 * @param recordStatus
	 * @return eligability
	 */
	private boolean recordEligible(String recordStatus) {

		List<String> allowedStates = this.getAllowedStates();
				
		if (recordStatus != null && allowedStates.contains(recordStatus)) {
			return true;
		}
		return false;
	}

	
	/**
	 * 
	 * @return
	 * @hibernate.property column="c_study_entry_point"
	 */
	public String getStudyEntryPoint() {
		return studyEntryPoint;
	}

	public void setStudyEntryPoint(String studyEntryPoint) {
		
		if(studyEntryPoint == null){
			studyEntryPoint = StudyEntryPoint.UNSPECIFIED.toString();
		}
		
		try{
			StudyEntryPoint.valueOf(studyEntryPoint);
		}catch(IllegalArgumentException e){
			throw e;
		}
		
		this.studyEntryPoint = studyEntryPoint;
	}
	
	protected StudyEntryPoint getStudyEntryPointEnum(){
		return StudyEntryPoint.valueOf(studyEntryPoint);
	}
	

	@Override
	public org.psygrid.data.reporting.definition.dto.UKCRNSummaryChart toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
		//management chart in the map of references
		org.psygrid.data.reporting.definition.dto.UKCRNSummaryChart dtoMC = null;
		if ( dtoRefs.containsKey(this)){
			dtoMC = (org.psygrid.data.reporting.definition.dto.UKCRNSummaryChart)dtoRefs.get(this);
		}
		else {
			//an instance of the management chart has not already
			//been created, so create it, and add it to the
			//map of references
			dtoMC = new org.psygrid.data.reporting.definition.dto.UKCRNSummaryChart();
			dtoRefs.put(this, dtoMC);
			toDTO(dtoMC, dtoRefs, depth);
		}

		return dtoMC;
	}

	public void toDTO(org.psygrid.data.reporting.definition.dto.UKCRNSummaryChart dtoC, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoC, dtoRefs, depth);
		dtoC.setStartDate(startDate);
		dtoC.setEndDate(endDate);
		dtoC.setStudyEntryPoint(studyEntryPoint);
	}

	private Calendar[] getFinancialYear() {
		//generate a chart for the current financial year (assuming may-april)
		Calendar curDate = Calendar.getInstance();
		Calendar startDate;
		Calendar endDate2;
		if (curDate.get(Calendar.MONTH) < Calendar.APRIL) {
			startDate  = new GregorianCalendar(curDate.get(Calendar.YEAR) -1, Calendar.APRIL, 0);
			endDate2   = new GregorianCalendar(curDate.get(Calendar.YEAR), Calendar.MARCH, 0);	
		}
		else {
			startDate  = new GregorianCalendar(curDate.get(Calendar.YEAR), Calendar.APRIL, 0);
			endDate2   = new GregorianCalendar(curDate.get(Calendar.YEAR) +1, Calendar.MARCH, 0);
		}
		return new Calendar[]{startDate, endDate2};
	}
}
