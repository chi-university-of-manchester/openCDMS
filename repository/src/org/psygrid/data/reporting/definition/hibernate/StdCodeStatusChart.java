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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.Chart;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.reporting.ChartRow;
import org.psygrid.data.reporting.ChartSeries;
import org.psygrid.data.reporting.definition.IStdCodeStatusChart;
import org.psygrid.data.reporting.definition.ReportException;


/**
 * A management chart showing the percentage of standard codes
 * used broken down by field for the given document occurrences 
 * and sites in a project (identified by record).
 *  
 * This provides an overview of data quality in a project (too high
 * a percentage of std codes in use indicates that assessments are
 * not being filled in!)
 * 
 * The chart can be displayed by group, or whole project if no 
 * group is specified.
 * 
 * @author Lucy Bridges
 *
 * @hibernate.joined-subclass table="t_stdcode_status_charts"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class StdCodeStatusChart extends ManagementChart implements IStdCodeStatusChart {

	private static final Log LOG = LogFactory.getLog(StdCodeStatusChart.class);


	private List<Group> groups = new ArrayList<Group>();

	private List<Long> docOccs = new ArrayList<Long>(); 

	private static final String DOC_ABSENT    = "Document Absent";
	private static final String ENTRY_ABSENT  = "Entry Absent"; 
	private static final String ENTRY_PRESENT = "Entry Present";

	private static final String DATA_NOT_KNOWN    = "Data not known";
	private static final String NOT_APPLICABLE    = "Not applicable";
	private static final String REFUSED_TO_ANSWER = "Refused to answer";
	private static final String UNABLE_TO_CAPTURE = "Data unable to be captured";

	private static final String FIELD_SEPARATOR = "*";

	/**
	 * Specify that the chart should summarise the percentage
	 * occurrences of each value per entry (default is per record)
	 */
	private boolean perEntry = false;

	/**
	 * Specify that the chart should summarise the percentage
	 * occurrences of each value per document (default is per record)
	 */
	private boolean perDocument = false;


	public StdCodeStatusChart() {
		super();
		setAllowedStates();
	}

	public StdCodeStatusChart(String type, String title) {
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
	 *                 table="t_stdcodestatuschrt_groups"
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

	public List<Long> getDocOccs() {
		return docOccs;
	}

	public void addDocOcc(Long docOcc) {
		this.docOccs.add(docOcc);
	}

	public void setDocOccs(List<Long> docOccs) {
		this.docOccs = docOccs;
	}

	/**
	 * @hibernate.property column="c_per_document" 
	 */
	public boolean isPerDocument() {
		return perDocument;
	}

	public void setPerDocument(boolean perDocument) {
		this.perDocument = perDocument;
	}

	/**
	 * @hibernate.property column="c_per_entry"
	 */
	public boolean isPerEntry() {
		return perEntry;
	}

	public void setPerEntry(boolean perEntry) {
		this.perEntry = perEntry;
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

		Boolean useExternalID = (Boolean)session.createQuery("select d.useExternalIdAsPrimary from DataSet d where d.id=:id ")
		.setLong("id", getReport().getDataSet().getId())
		.uniqueResult();
		
		String hql = "from Record r "+
		"where r.dataSet.id = :id and r.deleted=false "+ 
		"and r.identifier.groupPrefix in (select g.name from Group g where g.id in (:grps))";

		
		// Conditionally add an order by clause
		if(useExternalID){
			hql+=" order by r.externalIdentifier";
		}
		else {
			hql+=" order by r.identifier.groupPrefix, r.identifier.suffix";			
		}

		
		ScrollableResults recordCursor  = session.createQuery(hql)
		.setLong("id", getReport().getDataSet().getId())
		.setParameterList("grps", grps)
		.setReadOnly(true)
		.setFetchSize(100)
		.scroll();
		
		List<DocumentOccurrence> docOccs = session.createQuery("from DocumentOccurrence o where o.id in (:docOccIds) ")
		.setParameterList("docOccIds", this.docOccs)
		.list();


		/*
		 * Store each field and the occurrences of each value within that field
		 * e.g the numbers of ENTRY_ABSENT, ENTRY_PRESENT and each std code.
		 */
		Map<String,Map<String,Integer>> fieldOcc = new LinkedHashMap<String,Map<String,Integer>>();

		int eligibleRecords = 0;

		while (recordCursor.next()) {

			Record record = (Record)recordCursor.get(0);

			String recordStatus = "";
			if (record.getStatus() != null && record.getStatus().getGenericState() != null) {
				recordStatus = record.getStatus().getGenericState().toString();
			}

			if(recordEligible(recordStatus) && docOccs != null) {
				eligibleRecords++;

				Map<String,String> occFieldAndStatus = new LinkedHashMap<String,String>();

				for (DocumentOccurrence docOcc: docOccs) {
					//For each field in each document get the value - whether stdcode, present or absent
					fetchValuesForFields(occFieldAndStatus, docOcc, record);
				}

				//Get the value for each field for the current record and add it to the total percentages..
				for (String field: occFieldAndStatus.keySet()) {
					Map<String,Integer> valueOcc = fieldOcc.get(field);	//Value and occurrences so far

					if (valueOcc == null) {
						fieldOcc.put(field, new LinkedHashMap<String,Integer>());
					}

					String fieldStatus = occFieldAndStatus.get(field);
					Integer value = fieldOcc.get(field).get(fieldStatus);

					if (value == null) {
						fieldOcc.get(field).put(fieldStatus, 1);
					}
					else {
						fieldOcc.get(field).put(fieldStatus, value+1);
					}
				}

				if (!perEntry && !perDocument) {
					/*
					 * By default the chart will show all values entered per field per
					 * record.
					 * 
					 * PerEntry will summarise the occurrences of each value as a percentage
					 * by field
					 * 
					 * PerDocument will summarise the occurrences of each value as a percentage
					 * by document
					 */
					org.psygrid.data.reporting.ChartRow row = new ChartRow();
					rows.add(insertRow(row, record, occFieldAndStatus));
				}
			}
			
			session.evict(record);
		}
		
		// Close the result set
        recordCursor.close();

		if (!fieldOcc.isEmpty()) {
			
			/*
			 * Calculate the percentages the occurrences of each value in a field 
			 */
			if (perEntry) {
				for (String field: fieldOcc.keySet()) {

					double size = eligibleRecords;

					Map<String,Double> valueAndPercentage = new LinkedHashMap<String,Double>();

					for (String value: fieldOcc.get(field).keySet()) {
						Integer occurrence = fieldOcc.get(field).get(value);
						valueAndPercentage.put(value, (occurrence/size)*100);
					}
					org.psygrid.data.reporting.ChartRow row = new ChartRow();
					rows.add(insertPercentageRow(row, field, valueAndPercentage));
				}
			}

			/*
			 * Calculate the percentages the occurrences of each value in a document 
			 */
			if (perDocument) {
				Map<String,Map<String,Map<String,Integer>>> docValueAndPercentage = new LinkedHashMap<String,Map<String,Map<String,Integer>>>();

				//Sort the existing list of fields, values and occurrences into documents
				for (String field: fieldOcc.keySet()) {
					String docName = field.split("\\"+FIELD_SEPARATOR)[0];

					if (docValueAndPercentage.containsKey(docName)) {
						docValueAndPercentage.get(docName).put(field, fieldOcc.get(field));
					}
					else {
						Map<String,Map<String,Integer>> bob = new LinkedHashMap<String,Map<String,Integer>>();
						bob.put(field, fieldOcc.get(field));
						docValueAndPercentage.put(docName, bob);
					}
				}

				//Sum the occurrences of the values used in each field, per document
				for (String document: docValueAndPercentage.keySet()) {

					double size = eligibleRecords;
					double fieldNo = docValueAndPercentage.get(document).size();

					Map<String,Double> valueAndPercentage = new LinkedHashMap<String,Double>();

					for (String field: docValueAndPercentage.get(document).keySet()) {

						for (String value: docValueAndPercentage.get(document).get(field).keySet()) {
							double occurrence = fieldOcc.get(field).get(value);

							if (valueAndPercentage.containsKey(value)) {
								occurrence += valueAndPercentage.get(value);
							}
							valueAndPercentage.put(value, occurrence);
						}
					}

					//Now we have the total number of occurences for each value in the complete document
					//calculate the percentage, based on the number of records??!
					for (String value: valueAndPercentage.keySet()) {
						double total = valueAndPercentage.get(value);
						valueAndPercentage.put(value, ((total/fieldNo)*100)/size);
					}

					//Add a row for this document showing the percentage occurrence of each value
					org.psygrid.data.reporting.ChartRow row = new ChartRow();
					rows.add(insertPercentageRow(row, document, valueAndPercentage));

				}
			}

		}

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


	private ChartRow insertRow(ChartRow row, Record record, Map<String,String> occsFieldAndStatus) {

		String identifier = record.getIdentifier().getIdentifier();

		String externalID = record.getExternalIdentifier();
		boolean useExternalID = record.getUseExternalIdAsPrimary();

		/* define and populate the report columns */

		row.setSeries(new ChartSeries[occsFieldAndStatus.size()+2]);	//fixme

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
		for (String occ: occsFieldAndStatus.keySet()) {
			//The status of each field of the document instance 
			org.psygrid.data.reporting.ChartSeries series = new ChartSeries();
			row.getSeries()[i++] = series;
			series.setLabel(occ);
			series.setLabelType(IValue.TYPE_STRING);
			series.setPoints(new ChartPoint[1]);
			{ ChartPoint point = new ChartPoint();
			series.getPoints()[0] = point;
			point.setValue(occsFieldAndStatus.get(occ));
			point.setValueType(IValue.TYPE_STRING);	
			}

		}

		return row;
	}

	/**
	 * Insert a row for a field in a document, with the values used for that field and the
	 * percentages of occurrences 
	 * 
	 * @param row
	 * @param field
	 * @param valueAndPercentage
	 * @return chartRow
	 */
	private ChartRow insertPercentageRow(ChartRow row, String field, Map<String,Double> valueAndPercentage) {

		/* define and populate the report columns */

		row.setSeries(new ChartSeries[8]);	//fixme

		//The field in a given document 
		org.psygrid.data.reporting.ChartSeries studyId = new ChartSeries();
		row.getSeries()[0] = studyId;
		studyId.setLabel("");
		studyId.setLabelType(IValue.TYPE_STRING);
		studyId.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		studyId.getPoints()[0] = point;
		point.setValue(field);
		point.setValueType(IValue.TYPE_STRING);		
		}

		//Enforce ordering of columns and ensure that an entry is always present to guarentee consistent layout
		if (valueAndPercentage.containsKey(DOC_ABSENT)) {
			org.psygrid.data.reporting.ChartSeries series = insertSeries(DOC_ABSENT, valueAndPercentage.get(DOC_ABSENT));
			row.getSeries()[1] = series;
		}
		else {
			org.psygrid.data.reporting.ChartSeries series = insertSeries(DOC_ABSENT, 0D);
			row.getSeries()[1] = series;
		}

		if (valueAndPercentage.containsKey(ENTRY_ABSENT)) {
			org.psygrid.data.reporting.ChartSeries series = insertSeries(ENTRY_ABSENT, valueAndPercentage.get(ENTRY_ABSENT));
			row.getSeries()[2] = series;
		}
		else {
			org.psygrid.data.reporting.ChartSeries series = insertSeries(ENTRY_ABSENT, 0D);
			row.getSeries()[2] = series;
		}

		if (valueAndPercentage.containsKey(ENTRY_PRESENT)) {
			org.psygrid.data.reporting.ChartSeries series = insertSeries(ENTRY_PRESENT, valueAndPercentage.get(ENTRY_PRESENT));
			row.getSeries()[3] = series;
		}
		else {
			org.psygrid.data.reporting.ChartSeries series = insertSeries(ENTRY_PRESENT, 0D);
			row.getSeries()[3] = series;
		}

		if (valueAndPercentage.containsKey(DATA_NOT_KNOWN)) {
			org.psygrid.data.reporting.ChartSeries series = insertSeries(DATA_NOT_KNOWN, valueAndPercentage.get(DATA_NOT_KNOWN));
			row.getSeries()[4] = series;
		}
		else {
			org.psygrid.data.reporting.ChartSeries series = insertSeries(DATA_NOT_KNOWN, 0D);
			row.getSeries()[4] = series;
		}

		if (valueAndPercentage.containsKey(NOT_APPLICABLE)) {
			org.psygrid.data.reporting.ChartSeries series = insertSeries(NOT_APPLICABLE, valueAndPercentage.get(NOT_APPLICABLE));
			row.getSeries()[5] = series;
		}
		else {
			org.psygrid.data.reporting.ChartSeries series = insertSeries(NOT_APPLICABLE, 0D);
			row.getSeries()[5] = series;
		}

		if (valueAndPercentage.containsKey(REFUSED_TO_ANSWER)) {
			org.psygrid.data.reporting.ChartSeries series = insertSeries(REFUSED_TO_ANSWER, valueAndPercentage.get(REFUSED_TO_ANSWER));
			row.getSeries()[6] = series;
		}
		else {
			org.psygrid.data.reporting.ChartSeries series = insertSeries(REFUSED_TO_ANSWER, 0D);
			row.getSeries()[6] = series;
		}

		if (valueAndPercentage.containsKey(UNABLE_TO_CAPTURE)) {
			org.psygrid.data.reporting.ChartSeries series = insertSeries(UNABLE_TO_CAPTURE, valueAndPercentage.get(UNABLE_TO_CAPTURE));
			row.getSeries()[7] = series;
		}
		else {
			org.psygrid.data.reporting.ChartSeries series = insertSeries(UNABLE_TO_CAPTURE, 0D);
			row.getSeries()[7] = series;
		}

		return row;
	}

	public ChartSeries insertSeries(String label, Double value) {
		org.psygrid.data.reporting.ChartSeries series = new ChartSeries();
		series.setLabel(label);
		series.setLabelType(IValue.TYPE_STRING);
		series.setPoints(new ChartPoint[1]);
		{ ChartPoint point = new ChartPoint();
		series.getPoints()[0] = point;
		point.setValue(Double.toString(value));
		point.setValueType(IValue.TYPE_DOUBLE);		       
		}
		return series;
	}

	@Override
	public org.psygrid.data.reporting.definition.dto.StdCodeStatusChart toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
		//chart in the map of references
		org.psygrid.data.reporting.definition.dto.StdCodeStatusChart dtoMC = null;
		if ( dtoRefs.containsKey(this)){
			dtoMC = (org.psygrid.data.reporting.definition.dto.StdCodeStatusChart)dtoRefs.get(this);
		}
		else {
			//an instance of chart has not already
			//been created, so create it, and add it to the
			//map of references
			dtoMC = new org.psygrid.data.reporting.definition.dto.StdCodeStatusChart();
			dtoRefs.put(this, dtoMC);
			toDTO(dtoMC, dtoRefs, depth);
		}

		return dtoMC;
	}

	public void toDTO(org.psygrid.data.reporting.definition.dto.StdCodeStatusChart dtoC, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoC, dtoRefs, depth);
		dtoC.setPerDocument(perDocument);
		dtoC.setPerEntry(perEntry);

		org.psygrid.data.model.dto.GroupDTO[] dtoGroups = new org.psygrid.data.model.dto.GroupDTO[this.groups.size()];
		for (int i=0; i<this.groups.size(); i++){
			Group g = groups.get(i);
			dtoGroups[i] = ((Group)g).toDTO(dtoRefs, depth);
		}        
		dtoC.setGroups(dtoGroups);

		if (docOccs != null) {
			Long[] dtoOcc = new Long[this.docOccs.size()];
			for (int i=0, c=this.docOccs.size(); i<c; i++){
				dtoOcc[i] = docOccs.get(i);
			}        
			dtoC.setDocumentOccurrences(dtoOcc);
		}
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
	 * Get whether a value is present for each field in a document occurrence for a
	 * record and whether a std code is used. - Updates the hashmap provided
	 * 
	 * @param map
	 * @param docOcc
	 * @param record
	 */
	private void fetchValuesForFields(Map<String, String> map, DocumentOccurrence docOcc, Record record) {
		Document doc = docOcc.getDocument();
		DocumentInstance inst = (DocumentInstance)record.getDocumentInstance(docOcc);


		String docName = docOcc.getCombinedName();

		for (Entry entry: doc.getEntries()) {


			if (entry instanceof NarrativeEntry || entry instanceof CompositeEntry) {
				continue;	
			}

			for (SectionOccurrence sectionOcc: entry.getSection().getOccurrences()) {


				String labelString = docName+" "+FIELD_SEPARATOR+" "+sectionOcc.getCombinedDisplayText()+" "+FIELD_SEPARATOR+" "+entry.getName();

				if (inst == null) {
					map.put(labelString, DOC_ABSENT);
				}
				else  {
					map.put(labelString, ENTRY_ABSENT);	//will default to this if nothing has been entered

					if (inst.getSecOccInstances(sectionOcc).size() == 0) {			

						BasicResponse resp = (BasicResponse)inst.getResponse(entry, sectionOcc);
						if (resp != null) {
							IValue value = resp.getValue();

							if (value != null) {
								StandardCode stdCode = resp.getValue().getStandardCode();
								if (stdCode != null) {
									//Match stdCode with enum
									String code = stdCode(stdCode.getCode());
									if (code == null) {
										LOG.error("A standard code was found but not recognised. Code was: "+stdCode.getCode());
									}
									else {
										map.put(labelString, code);
									}
								}
								else {
									map.put(labelString, ENTRY_PRESENT);
								}
							}
						}
					}
					else {

						//should just have the compulsory sections - otherwise optional ones will be counted as missing for otherwise complete docs!
						boolean firstSection = true;

						for (SecOccInstance secOccInst: inst.getSecOccInstances(sectionOcc)) {

							if (firstSection) {
								firstSection = false;

								BasicResponse resp = (BasicResponse)inst.getResponse(entry, secOccInst);

								if (resp != null) {
									IValue value = resp.getValue();

									if (value != null) {
										StandardCode stdCode = resp.getValue().getStandardCode();
										if (stdCode != null) {
											//Match stdCode with enum
											String code = stdCode(stdCode.getCode());
											if (code == null) {
												LOG.error("A standard code was found but not recognised. Code was: "+stdCode.getCode());
											}
											else {
												map.put(labelString, code);
											}
										}
										else {
											map.put(labelString, ENTRY_PRESENT);
										}
									}

								}
							}
						}
					}
				}
			}
		}

	}


	/**
	 * Retrieve the name of a standard code for a 
	 * given integer value.
	 * 
	 * @param code
	 * @return stdCode
	 */
	private String stdCode(int code) {
		if (code == 960) {
			return DATA_NOT_KNOWN;
		}
		if (code == 970) {
			return NOT_APPLICABLE;
		}
		if (code == 980) {
			return REFUSED_TO_ANSWER;
		}
		if (code == 999) {
			return UNABLE_TO_CAPTURE;
		}
		return null;
	}
}