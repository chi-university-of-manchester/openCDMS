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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.reporting.Chart;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.reporting.ChartRow;
import org.psygrid.data.reporting.ChartSeries;
import org.psygrid.data.reporting.definition.IDocumentStatusChart;
import org.psygrid.data.reporting.definition.ReportException;


/**
 * A management chart showing the current status of every document
 * for the records in a project. This provides an overview of an 
 * individual record's progress.
 * 
 * The chart can be displayed by group, or whole project if no 
 * group is specified.
 * 
 * @author Lucy Bridges
 *
 * @hibernate.joined-subclass table="t_document_status_charts"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class DocumentStatusChart extends ManagementChart implements IDocumentStatusChart {


	private List<Group> groups = new ArrayList<Group>();

	private static final String DOC_ABSENT = "Absent"; 

	public DocumentStatusChart() {
		super();
		setAllowedStates();
	}

	public DocumentStatusChart(String type, String title) {
		super(type, title);
		setAllowedStates();
	}

	protected void setAllowedStates() {
		allowedStates.add(GenericState.ACTIVE.toString());
		allowedStates.add(GenericState.COMPLETED.toString());
		allowedStates.add(GenericState.LEFT.toString());
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
	 *                 table="t_documentstatuschrt_groups"
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
	 * Shows the current status of study points for all records, based on document groups.
	 * 
	 *  
	 *  to populate a org.psygrid.data.reporting.Chart 
	 * object used in rendering the report.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Chart generateChart(Session session) {


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

		//Create a list of Chart Rows
		List<org.psygrid.data.reporting.ChartRow> rows = new ArrayList<org.psygrid.data.reporting.ChartRow>();


		Long dsId = getReport().getDataSet().getId();

		/*
		 * Get all document occurrences for all documents in the dataset 
		 */
		List<DocumentOccurrence> docOccs = session.createQuery("from DocumentOccurrence occ where occ.document.myDataSet.id=:dsId)")
		.setLong("dsId", dsId)
		.setReadOnly(true)
		.list();

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
		else {
			recordhql+=" order by r.identifier.groupPrefix, r.identifier.suffix";			
		}


		/* 
		 * Retrieve the records created in each group 
		 */
		ScrollableResults recordCursor = session.createQuery(recordhql)
		.setLong("id", getReport().getDataSet().getId())
		.setParameterList("grps", grps)
		.setReadOnly(true)
		.scroll();

		while (recordCursor.next()) {

			Record record = (Record)recordCursor.get(0);

			String recordStatus = "";
			if (record.getStatus() != null) {
				recordStatus = record.getStatus().getGenericState().toString();
			}

			if(this.recordEligible(recordStatus)) {

				Map<String,String> occAndStatus = new LinkedHashMap<String,String>();

				for (DocumentOccurrence docOcc: docOccs) {
					occAndStatus.put(docOcc.getCombinedName(), DOC_ABSENT);	//will be replaced if a doc instance is found

					for (DocumentInstance inst: record.getDocInstances()) {
						//get the instance of the current document occurrence belonging to this record
						if (inst.getOccurrence().getId().compareTo(docOcc.getId()) == 0) {
							//add status to given document occurrence
							//calculate the status of each documentGroup using the relevant documents status
							occAndStatus.put(docOcc.getCombinedName(), inst.getStatus().getLongName());
						}
					}

				}

				org.psygrid.data.reporting.ChartRow row = new ChartRow();
				rows.add(insertRow(row, record, occAndStatus));
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


	private ChartRow insertRow(ChartRow row, Record record, Map<String,String> occsAndStatus) {



		String identifier = record.getIdentifier().getIdentifier();

		String externalID = record.getExternalIdentifier();
		boolean useExternalID = record.getUseExternalIdAsPrimary();

		/* define and populate the report columns */

		row.setSeries(new ChartSeries[occsAndStatus.size()+2]);

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
		recStatus.setLabel("Current Record Status");
		recStatus.setLabelType(IValue.TYPE_STRING);
		recStatus.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		recStatus.getPoints()[0] = point;
		point.setValue(record.getStatus().getLongName());
		point.setValueType(IValue.TYPE_STRING);		
		}

		int i = 2;
		for (String occ: occsAndStatus.keySet()) {
			//The document occurrence and status of the document instance 
			org.psygrid.data.reporting.ChartSeries series = new ChartSeries();
			row.getSeries()[i++] = series;
			series.setLabel(occ);
			series.setLabelType(IValue.TYPE_STRING);
			series.setPoints(new ChartPoint[1]);
			{ ChartPoint point = new ChartPoint();
			series.getPoints()[0] = point;
			point.setValue(occsAndStatus.get(occ));
			point.setValueType(IValue.TYPE_STRING);		       
			}

		}

		return row;
	}
	@Override
	public org.psygrid.data.reporting.definition.dto.DocumentStatusChart toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
		//chart in the map of references
		org.psygrid.data.reporting.definition.dto.DocumentStatusChart dtoMC = null;
		if ( dtoRefs.containsKey(this)){
			dtoMC = (org.psygrid.data.reporting.definition.dto.DocumentStatusChart)dtoRefs.get(this);
		}
		else {
			//an instance of chart has not already
			//been created, so create it, and add it to the
			//map of references
			dtoMC = new org.psygrid.data.reporting.definition.dto.DocumentStatusChart();
			dtoRefs.put(this, dtoMC);
			toDTO(dtoMC, dtoRefs, depth);
		}

		return dtoMC;
	}

	public void toDTO(org.psygrid.data.reporting.definition.dto.DocumentStatusChart dtoC, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoC, dtoRefs, depth);

		org.psygrid.data.model.dto.GroupDTO[] dtoGroups = new org.psygrid.data.model.dto.GroupDTO[this.groups.size()];
		for (int i=0; i<this.groups.size(); i++){
			Group g = groups.get(i);
			dtoGroups[i] = ((Group)g).toDTO(dtoRefs, depth);
		}        
		dtoC.setGroups(dtoGroups);
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
}