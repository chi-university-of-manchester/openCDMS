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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.reporting.ChartRow;
import org.psygrid.data.reporting.ChartSeries;
import org.psygrid.data.reporting.definition.IReceivingTreatmentChart;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.utils.esl.EslException;
import org.psygrid.data.utils.esl.IRemoteClient;

/**
 * Class to represent a chart in a management report that displays
 * a list of study participants receiving treatments.
 * 
 * For projects using the psygrid randomisation service.
 * 
 * See bug 662 for report requirements.
 * 
 * @author Lucy Bridges
 * 
 * @hibernate.joined-subclass table="t_treatment_charts"
 * 						proxy="org.psygrid.data.reporting.definition.hibernate.ReceivingTreatmentChart"
 * @hibernate.joined-subclass-key column="c_id"
 *
 */
public class ReceivingTreatmentChart extends ManagementChart implements IReceivingTreatmentChart {
	private static Log sLog = LogFactory.getLog(ReceivingTreatmentChart.class);
	/**
	 * The period of time the chart is to cover
	 */
	private Calendar startDate = new GregorianCalendar(0, 0, 0);
	private Calendar endDate   = new GregorianCalendar(0, 0, 0);

	private IRemoteClient client = null;

	private List<Group> groups = new ArrayList<Group>();

	/**
	 * Default entry to be used for a blank or null result, should
	 * not be required as this is also set in RemoteClient.
	 */
	private static final String ENTRY_DEFAULT = "Unknown";

	private static final String TREATMENT_DEFAULT = "Not Randomised";

	/**
	 * Get the client used to interface with the ESL and retrieve
	 * information required by this chart
	 * 
	 * @return the client
	 */
	public IRemoteClient getClient() {
		return client;
	}

	/**
	 * Set the client used to interface with the ESL and retrieve
	 * information required by this chart
	 * 
	 * @param client the client to set
	 */
	public void setClient(IRemoteClient client) {
		this.client = client;
	}

	public ReceivingTreatmentChart() {
		super();
	}

	public ReceivingTreatmentChart(String type, String title) {
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

	/**
	 * Get the groups in the dataset that will be featured in the chart.
	 * <p>
	 * This collection should be a subset of the groups associated
	 * with the parent report (unless the parent report has no groups 
	 * defined, which we take to mean all groups).
	 * 
	 * @return The groups.
	 * 
	 * @hibernate.list cascade="none" 
	 *                 table="t_treatmentchrt_groups"
	 * @hibernate.key column="c_chart_id"
	 * @hibernate.many-to-many class="org.psygrid.data.model.hibernate.Group"
	 *                         column="c_group_id"
	 * @hibernate.list-index column="c_index"
	 */
	public List<Group> getGroups() {
		return groups;
	}

	/**
	 * Set the groups in the dataset that will be featured in the chart.
	 * <p>
	 * This collection should be a subset of the groups associated
	 * with the parent report (unless the parent report has no groups 
	 * defined, which we take to mean all groups).
	 * 
	 * @param groups The groups.
	 */
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public int numGroups() {
		return groups.size();
	}

	public void addGroup(Group group) throws ReportException {
		if (null == group){
			throw new ReportException("Cannot add a null group");
		}
		this.groups.add(group);
	}

	public Group getGroup(int index) throws ReportException {
		try{
			return this.groups.get(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ReportException("No group exists for index="+index, ex);
		}
	}

	protected void setAllowedStates() {
		allowedStates.add(GenericState.ACTIVE.toString());
		allowedStates.add(GenericState.COMPLETED.toString());
		allowedStates.add(GenericState.REFERRED.toString());
	}


	public org.psygrid.data.reporting.Chart generateChart(Session session) {
		return generateChart(session, null, null)[0];
	}
	public org.psygrid.data.reporting.Chart[] generateChart(Session session, IRemoteClient client, String saml) {

		if (client != null) {
			setClient(client);
		}

		//Retrieve the dataset and therefore project info
		DataSet ds = this.getReport().getDataSet();
		String projectCode = ds.getProjectCode();

		//Whether the project uses randomisation
		boolean isRandomised = isProjectRandomised(projectCode, saml);

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
			Calendar[] dates = getSixMonths();
			startDate = dates[0];
			endDate   = dates[1];
		}

		//get the relevant groups as specified
		List<Long> grps = new ArrayList<Long>();		
		for (Group g: getGroups()) {
			grps.add(g.getId());
		}
		//if no groups have been specified retrieve groups for the report
		if (getGroups() == null || getGroups().size() == 0) {
			for (Group g: getReport().getGroups()) {
				grps.add(g.getId());
			}
			//if still no groups add all for the dataset
			if (grps.size() == 0) {
				for (Group g: getReport().getDataSet().getGroups()) {
					grps.add(g.getId());
				}
			}
		}

		if (isRandomised) {

			//Create date for database comparison (add one to month to find dates within the current month)
			Calendar dbEndDate  = new GregorianCalendar(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH)+1, 0);
			dbEndDate.setTimeZone(TimeZone.getTimeZone("GMT"));
			
			Boolean useExternalID = (Boolean)session.createQuery("select d.useExternalIdAsPrimary from DataSet d where d.id=:id ")
			.setLong("id", getReport().getDataSet().getId())
			.uniqueResult();
			
			/* 
			 * Retrieve the subject details from records created for each month between the start and end dates specified in the chart.
			 * This needs to be done using subqueries/joins, otherwise the HQL returns an incorrect number of records.
			 */
//			String hql = "Select r.created, r.identifier, r.identifier.groupPrefix, s.siteId, s.siteName " +
//			", r.externalIdentifier " +
//			"from Record r " +
//			"left join r.site s " + 
//			"left join r.dataSet d " +
//			"where d.id=:id " +
//			"and r.created between :start and :end " +
//			"and r.deleted=:deleted " +
//			"and r.status.enumGenericState in (:states) "+
//			//link a record to a group using its identifier
//			" and r.identifier.id in (select i.id from Identifier i where i.groupPrefix in " +
//			" (select g.name from Group g where g.id in (:grps)) )";

			String hql = "Select ch.when, r.identifier, r.identifier.groupPrefix, s.siteId, s.siteName " +
			", r.externalIdentifier " +
			"from Record r " +
			"left join r.site s " + 
			"left join r.dataSet d , ChangeHistory ch " +
			"where d.id=:id " +
			"and ch=r.history[0] "+ 
			"and ch.when between :start and :end " +
			"and r.deleted=:deleted " +
			"and r.status.enumGenericState in (:states) "+
			//link a record to a group using its identifier
			" and r.identifier.id in (select i.id from Identifier i where i.groupPrefix in " +
			" (select g.name from Group g where g.id in (:grps)) )";
			
			
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
			.setParameterList("grps", grps)
			.setParameterList("states", getAllowedStates())
			.list();


			List<ReportSubject> subjects = new ArrayList<ReportSubject>();

			/*
			 * Record the different treatment types used.
			 * One chart will be created per treatment. 
			 */
			Set<String> treatmentTypes  = new HashSet<String>();

			for ( int i=0; i<results.size(); i++ ) {

				Object[] data  = (Object[])results.get(i);

				/* parse the results given by the database query */
				//Date date = (Date)data[0];
				//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				//String created = sdf.format(date);

				String identifier = ((Identifier)data[1]).getIdentifier();
				String groupCode  = (String)data[2];	
				String siteId	  = (String)data[3];
				String siteName   = (String)data[4];
				String externalID = (String)data[5];

				if (siteId == null || siteId.equals("")) {
					siteId = ENTRY_DEFAULT;
				}
				if (groupCode == null || groupCode.equals("")) {
					groupCode = ENTRY_DEFAULT;
				}
				if (siteName == null || siteName.equals("")) {
					siteName = ENTRY_DEFAULT;
				}

				ReportSubject subject = new ReportSubject(identifier, groupCode, siteId, siteName,externalID,useExternalID);
				subjects.add(subject);

				Date[] events = getSubjectRandomisationEvents(projectCode, identifier, saml);
				if (events != null) {
					for (Date event: events) {
						//get results of randomisation for each event
						String[] treatment = getRandomisationResult(projectCode, identifier, event, saml);
						if (treatment != null) {
							subject.addRandomisation(event, new Treatment(treatment[0], treatment[1]));
							treatmentTypes.add(treatment[0]);	//record the treatment type used
						}
					}
				}
				else {
					treatmentTypes.add(TREATMENT_DEFAULT);
					Date empty = new Date();
					empty.setTime(0);
					subject.addRandomisation(empty, new Treatment(TREATMENT_DEFAULT,TREATMENT_DEFAULT));
				}
			}

			//add the nhs numbers to the subject objects.
			getNhsNumbers(projectCode, subjects, saml);


			/* Create one chart per treatment */
			org.psygrid.data.reporting.Chart[] charts = new org.psygrid.data.reporting.Chart[treatmentTypes.size()];
			int chartCount = 0;
			for (String treatment: treatmentTypes) {
				org.psygrid.data.reporting.Chart chart = createChart(treatment);
				charts[chartCount++] = chart;
				List<org.psygrid.data.reporting.ChartRow> rows = new ArrayList<ChartRow>();
				for (ReportSubject subject: subjects) {
					for (Date event: subject.getRandomisations().keySet()) {
						if (treatment.equals(subject.getRandomisations().get(event).getTreatmentId())) {
							/* subject has been randomised to this particular treatment, so add to this chart */
							org.psygrid.data.reporting.ChartRow row = new ChartRow();
							rows.add(insertRow(row, subject, event));
						}
					}
				}

				chart.setRows(new ChartRow[rows.size()]);
				for (int i=0; i < rows.size(); i++) {
					chart.getRows()[i] = rows.get(i);
				}
			}
			return charts;
		}
		else {
			//return a plain old list of subjects without randomisation data??
			return new org.psygrid.data.reporting.Chart[0]; //throw error?
		}
	}

	/**
	 * Create a chart to be rendered
	 * @return
	 */
	private org.psygrid.data.reporting.Chart createChart(String treatment) {
		org.psygrid.data.reporting.Chart chart = new org.psygrid.data.reporting.Chart();
		chart.setTitle(this.title+" "+treatment);
		chart.setRangeAxisLabel(this.rangeAxisLabel);
		String[] types = new String[this.types.size()];
		for ( int i=0; i<this.types.size(); i++ ){
			types[i] = this.types.get(i);
		}
		chart.setTypes(types);

		return chart;
	}

	private ChartRow insertRow(ChartRow row, ReportSubject subject, Date date) {
		row.setSeries(new ChartSeries[8]);

		String identifier = subject.getIdentifier();
		String externalID = subject.getExternalIdentifier();
		boolean useExternalID = subject.getUseExternalIdAsPrimary();
		String groupCode  = subject.getGroupCode();
		String siteId     = subject.getSiteId();
		String siteName   = subject.getSiteName();
		String nhsNumber  = subject.getNhsNumber();
		String treatmentName = subject.getRandomisations().get(date).getTreatmentName();
		String treatmentId   = subject.getRandomisations().get(date).getTreatmentId();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String randomisationEventDate = sdf.format(date);

		/* define and populate the report columns */

		//The subject identifier 
		org.psygrid.data.reporting.ChartSeries studyId = new ChartSeries();
		row.getSeries()[0] = studyId;
		studyId.setLabel("Study Number");
		studyId.setLabelType(IValue.TYPE_STRING);
		studyId.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		studyId.getPoints()[0] = point;
		// conditionally report externalID
		String reportedID = useExternalID?externalID:identifier;
		point.setValue(reportedID);
		point.setValueType(IValue.TYPE_STRING);		       
		}

		//The subject's nhs number 
		org.psygrid.data.reporting.ChartSeries number = new ChartSeries();
		row.getSeries()[1] = number;
		number.setLabel("NHS Number");
		number.setLabelType(IValue.TYPE_STRING);
		number.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		number.getPoints()[0] = point;
		point.setValue(nhsNumber);
		point.setValueType(IValue.TYPE_STRING);		       
		}

		//The date the subject was randomised to the current treatment
		ChartSeries randDate = new ChartSeries();
		row.getSeries()[2] = randDate;
		randDate.setLabel("Date Treatment Assigned");
		randDate.setLabelType(IValue.TYPE_STRING);
		randDate.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		randDate.getPoints()[0] = point;
		point.setValue(randomisationEventDate);
		point.setValueType(IValue.TYPE_DATE);
		}

		//The UKCRN assigned identifier for the site
		ChartSeries tId = new ChartSeries();
		row.getSeries()[3] = tId;
		tId.setLabel("Treatment");
		tId.setLabelType(IValue.TYPE_STRING);
		tId.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		tId.getPoints()[0] = point;
		point.setValue(treatmentId);
		point.setValueType(IValue.TYPE_STRING);		       
		}

		//The UKCRN assigned identifier for the site
		ChartSeries treatment = new ChartSeries();
		row.getSeries()[4] = treatment;
		treatment.setLabel("Treatment Name");
		treatment.setLabelType(IValue.TYPE_STRING);
		treatment.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		treatment.getPoints()[0] = point;
		point.setValue(treatmentName);
		point.setValueType(IValue.TYPE_STRING);		       
		}

		//The UKCRN assigned identifier for the site
		ChartSeries sId = new ChartSeries();
		row.getSeries()[5] = sId;
		sId.setLabel("Site ID");
		sId.setLabelType(IValue.TYPE_STRING);
		sId.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		sId.getPoints()[0] = point;
		point.setValue(siteId);
		point.setValueType(IValue.TYPE_STRING);		       
		}

		//The hospital/place taking part in the study and the hub name
		ChartSeries sName = new ChartSeries();
		row.getSeries()[6] = sName;
		sName.setLabel("Site Name");
		sName.setLabelType(IValue.TYPE_STRING);
		sName.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		sName.getPoints()[0] = point;
		point.setValue(siteName);
		point.setValueType(IValue.TYPE_STRING);		       
		}

		//The group code
		ChartSeries gCode = new ChartSeries();
		row.getSeries()[7] = gCode;
		gCode.setLabel("Group Code");
		gCode.setLabelType(IValue.TYPE_STRING);
		gCode.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		gCode.getPoints()[0] = point;
		point.setValue(groupCode);
		point.setValueType(IValue.TYPE_STRING);		       
		}

		return row;
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
				sLog.info("Problem occurred when calling 'getSubjectRandomisationEvents' "+ e.getMessage());
			}
			return null;
		}
	}

	private void getNhsNumbers(String projectCode, List<ReportSubject> subjects, String saml) {
		if ( client == null ) {
			return;
		}

		//Send as a list to reduce the number of ws calls
		List<String> identifiers = new ArrayList<String>();
		for (ReportSubject subject: subjects) {
			identifiers.add(subject.getIdentifier());
		}
		Map<String,String> numbers = new HashMap<String,String>();
		try {
			numbers = client.getNhsNumbers(projectCode, identifiers, saml);
		}
		catch (EslException e) {
			if (sLog.isInfoEnabled()) {
				sLog.info("Problem occurred when calling 'getNhsNumbers' "+e.getMessage());
			}
			return;
		}

		for (String id: numbers.keySet()) {
			for (ReportSubject subject: subjects) {
				if (subject.getIdentifier().equals(id)) {
					String num = numbers.get(id);
					if (num == null || num.equals("")) {
						num = ENTRY_DEFAULT;
					}
					subject.setNhsNumber(num);
					break;
				}
			}
		}
	}

	private String[] getRandomisationResult(String projectCode, String identifier, Date date, String saml) {
		if ( client == null ) {
			return new String[2];
		}
		try {
			return client.getRandomisationResult(projectCode, identifier, date, saml);
		}
		catch (EslException e) {
			if (sLog.isInfoEnabled()) {
				sLog.info("Problem occurred when calling 'getRandomisationResult' "+e.getMessage());
			}
			return null;
		}
	}

	@Override
	public org.psygrid.data.reporting.definition.dto.ReceivingTreatmentChart toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
		//management chart in the map of references
		org.psygrid.data.reporting.definition.dto.ReceivingTreatmentChart dtoMC = null;
		if ( dtoRefs.containsKey(this)){
			dtoMC = (org.psygrid.data.reporting.definition.dto.ReceivingTreatmentChart)dtoRefs.get(this);
		}
		else {
			//an instance of the management chart has not already
			//been created, so create it, and add it to the
			//map of references
			dtoMC = new org.psygrid.data.reporting.definition.dto.ReceivingTreatmentChart();
			dtoRefs.put(this, dtoMC);
			toDTO(dtoMC, dtoRefs, depth);
		}

		return dtoMC;
	}

	public void toDTO(org.psygrid.data.reporting.definition.dto.ReceivingTreatmentChart dtoC, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoC, dtoRefs, depth);
		dtoC.setStartDate(startDate);
		dtoC.setEndDate(endDate);

		org.psygrid.data.model.dto.GroupDTO[] dtoGroups = new org.psygrid.data.model.dto.GroupDTO[this.groups.size()];
		for (int i=0; i<this.groups.size(); i++){
			Group g = groups.get(i);
			dtoGroups[i] = ((Group)g).toDTO(dtoRefs, depth);
		}        
		dtoC.setGroups(dtoGroups);
	}


	/**
	 * A study subject used to store the information to make up one row of the chart
	 * 
	 * @author Lucy Bridges
	 */
	private class ReportSubject {
		String identifier;
		String groupCode;	
		String siteId;  
		String siteName;
		String nhsNumber;
		String externalID;
		boolean useExternalID;

		public ReportSubject(String identifier) {
			this.identifier = identifier;
		}

		public ReportSubject(String identifier, String groupCode, String siteId, String siteName, String externalID, boolean useExternalID) {
			this.identifier = identifier;
			this.groupCode  = groupCode;
			this.siteId     = siteId;
			this.siteName	= siteName;
			this.externalID = externalID;
			this.useExternalID = useExternalID;
		}

		Map<Date,Treatment> randomisations = new HashMap<Date,Treatment>();

		public String getGroupCode() {
			return groupCode;
		}

		public void setGroupCode(String groupCode) {
			this.groupCode = groupCode;
		}

		public String getIdentifier() {
			return identifier;
		}

		public void setIdentifier(String identifier) {
			this.identifier = identifier;
		}

		public Map<Date, Treatment> getRandomisations() {
			return randomisations;
		}

		public void setRandomisations(Map<Date, Treatment> randomisations) {
			this.randomisations = randomisations;
		}

		public void addRandomisation(Date date, Treatment treatment) {
			this.randomisations.put(date, treatment);
		}

		public String getSiteId() {
			return siteId;
		}

		public void setSiteId(String siteId) {
			this.siteId = siteId;
		}

		public String getSiteName() {
			return siteName;
		}

		public void setSiteName(String siteName) {
			this.siteName = siteName;
		}

		public String getNhsNumber() {
			return nhsNumber;
		}

		public void setNhsNumber(String nhsNumber) {
			this.nhsNumber = nhsNumber;
		}

		public String getExternalIdentifier() {
			return externalID;
		}

		public boolean getUseExternalIdAsPrimary() {
			return useExternalID;
		}


	}

	private class Treatment {
		private String treatmentId;
		private String treatmentName;

		public Treatment(String treatmentId, String treatmentName) {
			this.treatmentId = treatmentId;
			this.treatmentName = treatmentName;
		}

		public String getTreatmentId() {
			return treatmentId;
		}

		public void setTreatmentId(String treatmentId) {
			this.treatmentId = treatmentId;
		}

		public String getTreatmentName() {
			return treatmentName;
		}

		public void setTreatmentName(String treatmentName) {
			this.treatmentName = treatmentName;
		}
	}

	private Calendar[] getSixMonths() {
		//generate a chart for the previous six months, based on current date
		Calendar curDate = Calendar.getInstance();
		Calendar startDate = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH) - 6, 0);
		Calendar endDate   = new GregorianCalendar(curDate.get(Calendar.YEAR), curDate.get(Calendar.MONTH), 0);
		startDate.setTimeZone(TimeZone.getTimeZone("GMT"));
		endDate.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		return new Calendar[]{startDate, endDate};
	}
}
