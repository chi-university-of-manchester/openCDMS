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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.psygrid.data.model.IDateValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.Chart;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.reporting.ChartRow;
import org.psygrid.data.reporting.ChartSeries;
import org.psygrid.data.reporting.definition.ICollectionDateChart;
import org.psygrid.data.reporting.definition.ReportException;

/**
 * A management chart showing the date created of every document
 * for the records in a project.
 * 
 * The chart can be displayed by group, or whole project if no 
 * group is specified.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_collection_date_charts"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class CollectionDateChart extends ManagementChart implements ICollectionDateChart {

	private static final Log LOG = LogFactory.getLog(CollectionDateChart.class);
	
	private static final String DOC_ABSENT = "Absent"; 

	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
	
	private List<Group> groups = new ArrayList<Group>();
	
	private Map<String, Integer> collectionDateEntries = new HashMap<String, Integer>();

	public CollectionDateChart() {
		super();
		setAllowedStates();
	}

	public CollectionDateChart(String type, String title) {
		super(type, title);
		setAllowedStates();
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
	 *                 table="t_colldatechrt_groups"
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


	
    /**
     * Get the map of collection date entries.
     * <p>
     * The key of the map is the document name; the value is the index of
     * the entry for the date of collection.
     * 
     * @return The map of collection date entries.
     * 
     * @hibernate.map cascade="none" 
     *                table="t_coll_date_entries"
     * @hibernate.key column="c_coll_date_chart_id"
     * @hibernate.map-key column="c_document_name" 
     * 					  type="string"
     * @hibernate.element column="c_entry_index" 
     *                    type="integer"
     */
	public Map<String, Integer> getCollectionDateEntries() {
		return collectionDateEntries;
	}

	public void setCollectionDateEntries(Map<String, Integer> collectionDateEntries) {
		this.collectionDateEntries = collectionDateEntries;
	}

	public void addCollectionDateEntry(String docName, Integer entryIndex){
		collectionDateEntries.put(docName, entryIndex);
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public Chart generateChart(Session session) {

		LOG.info("Generating collection date chart...");
		
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

		LOG.info("Groups: "+grps);
		
		Long dsId = getReport().getDataSet().getId();

		/*
		 * Get all document occurrences for all documents in a dataset 
		 */		
		String hql = "from DocumentOccurrence occ " +
					 "where occ.document.id in " +
					 "(select doc.id from Document doc where doc.myDataSet.id=:dsId)";

		List<DocumentOccurrence> docOccs = session.createQuery(hql).setLong("dsId", dsId).list();

		Boolean useExternalID = (Boolean)session.createQuery("select d.useExternalIdAsPrimary from DataSet d where d.id=:id ")
		.setLong("id", getReport().getDataSet().getId())
		.uniqueResult();
		
		String recordhql = "from Record r "+
		"where r.dataSet.id = :id and r.deleted=false "+ 
		"and r.identifier.groupPrefix in (select g.name from Group g where g.id in (:grps))";
		
		// Conditionally add an order by clause
		if(useExternalID){
			recordhql+=" order by r.externalIdentifier";
		}

		/* 
		 * Retrieve the records created in each group 
		 */
		ScrollableResults recordCursor = session.createQuery(recordhql)
		.setLong("id", getReport().getDataSet().getId())
		.setParameterList("grps", grps)
		.setReadOnly(true)
		.scroll();

		//Create a list of Chart Rows
		List<org.psygrid.data.reporting.ChartRow> rows = new ArrayList<org.psygrid.data.reporting.ChartRow>();
			
		while (recordCursor.next()) {

			Record record = (Record)recordCursor.get(0);

			String recordStatus = "";
			if (record.getStatus() != null && record.getStatus().getGenericState() != null) {
				recordStatus = record.getStatus().getGenericState().toString();
			}

			if(this.recordEligible(recordStatus)) {

				Map<String, String> occAndDate = new LinkedHashMap<String, String>();

				for (DocumentOccurrence docOcc: docOccs) {
					//add status to given document occurrence
					//calculate the status of each documentGroup using the relevent documents status
					addCollectionDateForDocumentInstance(occAndDate, docOcc, record);
				}

				org.psygrid.data.reporting.ChartRow row = new ChartRow();
				rows.add(insertRow(row, record, occAndDate));
			}
			session.evict(record);
		}

		recordCursor.close();
					
		/*
		 * Generate the chart
		 */
		org.psygrid.data.reporting.Chart chart = new org.psygrid.data.reporting.Chart();
		chart.setTitle(this.title);
		chart.setRangeAxisLabel(rangeAxisLabel);
		chart.setUsePercentages(usePercentages);
		String[] types = new String[this.types.size()];
		for ( int i=0; i<this.types.size(); i++ ){
			types[i] = this.types.get(i);
		}
		chart.setTypes(types);

		chart.setRows(new ChartRow[rows.size()]);
		for (int i=0; i < rows.size(); i++) {
			chart.getRows()[i] = rows.get(i);
		}

		return chart;
		
	}

	private ChartRow insertRow(ChartRow row, Record record, Map<String, String> occsAndDate) {

		String identifier = record.getIdentifier().getIdentifier();

		String externalID = record.getExternalIdentifier();
		boolean useExternalID = record.getUseExternalIdAsPrimary();

		/* define and populate the report columns */

		row.setSeries(new ChartSeries[occsAndDate.size()+2]);

		//The record/subject identifier 
		org.psygrid.data.reporting.ChartSeries studyId = new ChartSeries();
		row.getSeries()[0] = studyId;
		studyId.setLabel("");
		studyId.setLabelType(IValue.TYPE_STRING);
		studyId.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		studyId.getPoints()[0] = point;
		// conditionally report externalID
		String reportedID = useExternalID?externalID:identifier;
		point.setValue(reportedID);
		point.setValueType(IValue.TYPE_STRING);		
		}

		//The overall record status 
		org.psygrid.data.reporting.ChartSeries recStatus = new ChartSeries();
		row.getSeries()[1] = recStatus;
		if ( null != record.getDataSet().getScheduleStartQuestion() ){
			recStatus.setLabel(record.getDataSet().getScheduleStartQuestion());
		}
		else{
			recStatus.setLabel("Date of entry into study");
		}
		
		recStatus.setLabelType(IValue.TYPE_STRING);
		recStatus.setPoints(new ChartPoint[1]);
		{
			ChartPoint point = new ChartPoint();
			recStatus.getPoints()[0] = point;
			if ( null != record.getDataSet().getScheduleStartQuestion() ){
				point.setValue(dateFormatter.format(record.getScheduleStartDate()));
			}
			else{
				point.setValue(dateFormatter.format(record.getStudyEntryDate()));
			}
			point.setValueType(IValue.TYPE_STRING);
		}

		int i = 2;
		for (String occ: occsAndDate.keySet()) {
			//The document occurrence and status of the document instance 
			org.psygrid.data.reporting.ChartSeries series = new ChartSeries();
			row.getSeries()[i++] = series;
			series.setLabel(occ);
			series.setLabelType(IValue.TYPE_STRING);
			series.setPoints(new ChartPoint[1]);
			{ 
				ChartPoint point = new ChartPoint();
				series.getPoints()[0] = point;
				point.setValue(occsAndDate.get(occ));
				point.setValueType(IValue.TYPE_STRING);		       
			}

		}

		return row;
	}

	@Override
	public org.psygrid.data.reporting.definition.dto.CollectionDateChart toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
		//chart in the map of references
		org.psygrid.data.reporting.definition.dto.CollectionDateChart dtoCDC = null;
		if ( dtoRefs.containsKey(this)){
			dtoCDC = (org.psygrid.data.reporting.definition.dto.CollectionDateChart)dtoRefs.get(this);
		}
		else {
			//an instance of chart has not already
			//been created, so create it, and add it to the
			//map of references
			dtoCDC = new org.psygrid.data.reporting.definition.dto.CollectionDateChart();
			dtoRefs.put(this, dtoCDC);
			toDTO(dtoCDC, dtoRefs, depth);
		}

		return dtoCDC;
	}

	public void toDTO(org.psygrid.data.reporting.definition.dto.CollectionDateChart dtoCDC, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoCDC, dtoRefs, depth);

		org.psygrid.data.model.dto.GroupDTO[] dtoGroups = new org.psygrid.data.model.dto.GroupDTO[this.groups.size()];
		for (int i=0; i<this.groups.size(); i++){
			Group g = groups.get(i);
			dtoGroups[i] = ((Group)g).toDTO(dtoRefs, depth);
		}        
		dtoCDC.setGroups(dtoGroups);
		
        String[] dtoKeys = new String[this.collectionDateEntries.size()];
        Integer[] dtoVars = new Integer[this.collectionDateEntries.size()];
        int counter = 0;
        for ( Map.Entry<String, Integer> entry: collectionDateEntries.entrySet()){
            dtoKeys[counter] = entry.getKey();
            dtoVars[counter] = entry.getValue();
            counter++;
        }
        dtoCDC.setCollDateEntryKeys(dtoKeys);
        dtoCDC.setCollDateEntryValues(dtoVars);

	}
	
	@Override
	protected void setAllowedStates() {
		allowedStates.add(GenericState.ACTIVE.toString());
		allowedStates.add(GenericState.COMPLETED.toString());
		allowedStates.add(GenericState.LEFT.toString());
	}

	/**
	 * Records with a status of inactive are not included 
	 * in this chart.
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
	 * Get the date of collection for the given document instance.
	 * <p>
	 * If the document contains an explicit question asking when the
	 * data was collected we use this, otherwise we just use the date of
	 * creation of the document instance.
	 * 
	 * @param inst The document instance.
	 * @return The date of colelction.
	 */
	private void addCollectionDateForDocumentInstance(Map<String, String> map, DocumentOccurrence docOcc, Record record){
		Document doc = docOcc.getDocument();
		DocumentInstance inst = record.getDocumentInstance(docOcc);
		Integer entryIndex = collectionDateEntries.get(doc.getName());
		String labelString = null;
		String dateString = DOC_ABSENT;
		if ( null == entryIndex ){
			labelString = docOcc.getCombinedName();
			if ( null != inst ){
				Date when  = null;
				if ( inst.getHistoryCount() > 0 ){
					when = inst.getHistory(0).getWhen();
				}
				else{
					when = inst.getCreated();
				}
				dateString = dateFormatter.format(when);
			}
		}
		else{
			//Assume here that the section containing the entry for "date of collection"
			//has just one occurrence - I can't think of a situation where this would
			//not be valid.
			labelString = docOcc.getCombinedName()+" *";
			if ( null != inst ){
				Entry entry = doc.getEntry(entryIndex);
				BasicResponse resp = (BasicResponse)inst.getResponse(entry, entry.getSection().getOccurrence(0));
				IDateValue value = (IDateValue)resp.getValue();
				dateString = dateFormatter.format(value.getValue());
			}
		}
		map.put(labelString, dateString);
	}
	
}
