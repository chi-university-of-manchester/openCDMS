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
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.model.hibernate.SecOccInstance;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.reporting.definition.ISimpleChartItem;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.utils.esl.IRemoteClient;

/**
 * Class to represent a single item in a chart.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_simple_chart_items" 
 * @hibernate.joined-subclass-key column="c_id"
 */
public class SimpleChartItem extends AbstractChartItem implements ISimpleChartItem {

    /**
     * The entry that the simple chart item references.
     * <p>
     * When generating a report for a given record the value of the response
     * to this entry will be quoted in the report.
     */
    private Entry entry;
    
    /**
     * The document occurrence that the simple chart item references.
     * <p>
     * When generating a report for a given record only responses contained
     * by a document instance referencing this document occurrence will be
     * considered.
     */
    private DocumentOccurrence docOccurrence;
    
    /**
     * The section occurrence that the simple chart item references.
     * <p>
     * When generating a report for a given record only responses referencing 
     * this section occurrence will be considered.
     */
    private SectionOccurrence secOccurrence;
    
    /**
     * The options to be passed to org.psygrid.data.model.hibernate.Value#getReportValueAsString
     * when getting the value to put in the report.
     */
    private String options;
    
    /**
     * The options used when forming the label from the
     * chart item
     */
    private String labelOptions;
    
    public SimpleChartItem(){}
    
    public SimpleChartItem(Entry entry, DocumentOccurrence docOcc, SectionOccurrence secOcc){
        this.entry = entry;
        this.docOccurrence = docOcc;
        this.secOccurrence = secOcc;
    }
    
    public SimpleChartItem(Entry entry, DocumentOccurrence docOcc, SectionOccurrence secOcc, String options){
        this.entry = entry;
        this.docOccurrence = docOcc;
        this.secOccurrence = secOcc;
        this.options = options;
    }
    
    public SimpleChartItem(Entry entry, DocumentOccurrence docOcc, SectionOccurrence secOcc, String options, String labelOptions){
        this.entry = entry;
        this.docOccurrence = docOcc;
        this.secOccurrence = secOcc;
        this.options = options;
        this.labelOptions = labelOptions;
    }
    
    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.DocumentOccurrence"
     *                        column="c_doc_occ_id"
     *                        not-null="true"
     *                        unique="false"
     *                        cascade="none"
     */
    public DocumentOccurrence getDocOccurrence() {
        return docOccurrence;
    }

    public void setDocOccurrence(DocumentOccurrence docOccurrence) {
        this.docOccurrence = docOccurrence;
    }

    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Entry"
     *                        column="c_entry_id"
     *                        not-null="true"
     *                        unique="false"
     *                        cascade="none"
     */
    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.SectionOccurrence"
     *                        column="c_sec_occ_id"
     *                        not-null="true"
     *                        unique="false"
     *                        cascade="none"
     */
    public SectionOccurrence getSecOccurrence() {
        return secOccurrence;
    }

    public void setSecOccurrence(SectionOccurrence secOccurrence) {
        this.secOccurrence = secOccurrence;
    }

    /**
     * @hibernate.property column="c_options"
     */
    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    /**
     * @hibernate.property column="c_label_options"
     */
    public String getLabelOptions() {
        return labelOptions;
    }

    public void setLabelOptions(String labelOptions) {
        this.labelOptions = labelOptions;
    }

    @Override
	public String getLabel() throws ReportException {
    	String seriesLabel = "";
		try {
			if ( ISimpleChartItem.LABEL_LABEL_ONLY.equals(labelOptions)){
				//use the label of the entry as the label for this chart row
				seriesLabel = entry.getLabel();
			}
			else if (ISimpleChartItem.LABEL_LABEL_TEXT.equals(labelOptions)){
				//use the label and the display text of the entry as the label 
				//for this chart row
				seriesLabel = entry.getLabel()+". "+entry.getDisplayText();
			}
			else{
				//use the display text of the entry as the label for this chart row.
				seriesLabel = entry.getDisplayText();		
			}
		}
		catch (Exception e) {
			throw new ReportException("Problem when setting the label for the row", e);
		}
		return seriesLabel;
	}

	@Override
	public ChartPoint getPoint(Session session, IRemoteClient eslClient, Long recordId, String saml) throws ReportException {
		List result = session.createQuery("from Response r where r.record.id=? and r.entry.id=? "+
										  "and r.sectionOccurrence.id=? and r.docInstance.occurrence.id=?")
							 .setLong(0, recordId)
							 .setLong(1, entry.getId())
							 .setLong(2, secOccurrence.getId())
							 .setLong(3, docOccurrence.getId())
							 .list();
		if (result == null) {
			throw new ReportException("No result returned when retrieving data for report.");
		}

		ChartPoint point = new ChartPoint();
		
		//add the value to the series of points for the current row
		if ( result.size() > 1 ){
			throw new ReportException("More than one response for record="+recordId+", entry="+entry.getId()+" ("+result.size()+")");
		}
		else if ( 0 == result.size() ){			
			point.setValue(null);
		}
		else{
			BasicResponse br = (BasicResponse)result.get(0);
			String valueAndType[] = br.getTheValue().getReportValueAsString(options);
			if ( null != valueAndType[0] ){
				point.setValue(valueAndType[0]);
				point.setValueType(valueAndType[1]);
				point.setUnit(valueAndType[2]);
			}
		}
		
		return point;
	}

	public List<ChartPoint> getPoints(Session session, IRemoteClient eslClient, Long recordId, String saml) throws ReportException{
		/*
		HQL query that I wanted to use, that worked in the Hibernate Console, but doesn't
		when you try it for real...
		List secOccInsts = session.createQuery("from SecOccInstance soi "+
				   "where soi.sectionOccurrence.id=? "+
				   "and soi.id in ("+
				   "select inst.secOccInstances.id from DocumentInstance inst "+
				   "where inst.record.id=?" +
				   "and inst.occurrence.id=?)")
				   */

		//get all the SecOccInstances for this record and document instance...
		List secOccInsts = session.createQuery("select inst.secOccInstances from DocumentInstance inst "+
				  							   "where inst.record.id=?" +
				  							   "and inst.occurrence.id=?")
				  				  .setLong(0, recordId)
				  				  .setLong(1, docOccurrence.getId())
				  				  .list();
		
		List<ChartPoint> points = new ArrayList<ChartPoint>();
		
		for ( Object o: secOccInsts ){
		
			SecOccInstance soi = (SecOccInstance)o;
			if ( soi.getSectionOccurrence().equals(secOccurrence) ){
				
				//this SecOccInstance is for the SectionOccurrence referenced by the chart item
				//so get the response for the given Record, Entry, SecOccInstance and DocumentInstance...
				List result = session.createQuery("from Response r where r.record.id=? and r.entry.id=? "+
				  								  "and r.secOccInstance.id=? and r.docInstance.occurrence.id=?")
				  					 .setLong(0, recordId)
				  					 .setLong(1, entry.getId())
				  					 .setLong(2, soi.getId())
				  					 .setLong(3, docOccurrence.getId())
				  					 .list();
				
				if (result == null) {
					throw new ReportException("No result returned when retrieving data for report.");
				}
				
				ChartPoint point = new ChartPoint();
				
				//add the value to the series of points for the current row
				if ( result.size() > 1 ){
					throw new ReportException("More than one response for record="+recordId+", entry="+entry.getId()+" ("+result.size()+")");
				}
				else if ( 0 == result.size() ){			
					point.setValue(null);
				}
				else{
					BasicResponse br = (BasicResponse)result.get(0);
					String valueAndType[] = br.getTheValue().getReportValueAsString(options);
					if ( null != valueAndType[0] ){
						point.setValue(valueAndType[0]);
						point.setValueType(valueAndType[1]);
						point.setUnit(valueAndType[2]);
					}
				}
			
				points.add(point);
			}
		}
			
		return points;
	}
	
	@Override
	public boolean getMultiple() throws ReportException {
		return secOccurrence.isMultipleAllowed();
	}

	@Override
    public org.psygrid.data.reporting.definition.dto.SimpleChartItem toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        //check for an already existing instance of a dto object for this 
        //simple chart item in the map of references
        org.psygrid.data.reporting.definition.dto.SimpleChartItem dtoSCI = null;
        if ( dtoRefs.containsKey(this)){
            dtoSCI = (org.psygrid.data.reporting.definition.dto.SimpleChartItem)dtoRefs.get(this);
        }
        else {
            //an instance of the element has not already
            //been created, so create it, and add it to the
            //map of references
            dtoSCI = new org.psygrid.data.reporting.definition.dto.SimpleChartItem();
            dtoRefs.put(this, dtoSCI);
            toDTO(dtoSCI, dtoRefs, depth);
        }
        
        return dtoSCI;
    }

    public void toDTO(org.psygrid.data.reporting.definition.dto.SimpleChartItem dtoSCI, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        super.toDTO(dtoSCI, dtoRefs, depth);
        if ( null != this.entry ){
            dtoSCI.setEntry(this.entry.toDTO(dtoRefs, depth));
        }
        if ( null != this.docOccurrence ){
            dtoSCI.setDocOccurrence(this.docOccurrence.toDTO(dtoRefs, depth));
        }
        if ( null != this.secOccurrence ){
            dtoSCI.setSecOccurrence(this.secOccurrence.toDTO(dtoRefs, depth));
        }
        dtoSCI.setOptions(this.options);
        dtoSCI.setLabelOptions(this.labelOptions);
    }

}