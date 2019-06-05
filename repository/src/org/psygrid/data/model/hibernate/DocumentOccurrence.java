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

package org.psygrid.data.model.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Class to represent an occurrence of a document.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_doc_occs"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class DocumentOccurrence extends Component {

    /**
     * The label for the occurrence.
     * <p>
     * The label is intended to be used for displaying the occurrence number 
     * of the document within its dataset.
     */
    protected String label;
    
    /**
     * The document that the occurrence is an occurrence of.
     */
    private Document document;
    
    /**
     * The document group that the occurrence belongs to.
     */
    private DocumentGroup documentGroup;
    
    /**
     * The collection of reminders for the schedulable object.
     */
    private List<Reminder> reminders = new ArrayList<Reminder>();
    
    protected Integer scheduleTime;
    
    protected TimeUnits scheduleUnits;
    
    /**
     * If True, and the parent Dataset has the randomization required
     * flag set to True, the completion of an instance of this document
     * occurrence is the trigger for performing randomization via the
     * Electronic Screening Log.
     */
    protected boolean randomizationTrigger;
    
    /**
     * If True then the document occurrence has been locked, and creation
     * of instances of it should not be permitted.
     */
    protected boolean locked;
    
    /**
     * If this occurrence is involved in dual data entry as part of the secondary
     * dataset, the index of the occurrence of a document in the primary dataset 
     * that it is associated with. Otherwise <code>null</code>.
     */
    private Long primaryOccIndex;
    
    /**
     * If this occurrence is involved in dual data entry as part of the primary
     * dataset, the index of the occurrence of a document in the primary dataset 
     * that it is associated with. Otherwise <code>null</code>.
     */
    private Long secondaryOccIndex;
    
    /**
     * Default no-arg constructor.
     */
    public DocumentOccurrence(){};
    
    /**
     * Constructor that accepts the name of the occurrence as an
     * argument.
     * 
     * @param name The name of the occurrence.
     */
    public DocumentOccurrence(String name){
        this.name = name;
    }
    
    /**
     * Get the label for the occurrence.
     * <p>
     * The label is intended to be used for displaying the occurrence number 
     * of the document within its dataset.
     * 
     * @return The label.
     * @hibernate.property column="c_label"
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the label for the occurrence.
     * <p>
     * The label is intended to be used for displaying the occurrence number 
     * of the document within its dataset.
     * 
     * @param label The label.
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * Get the document that this occurrence is an occurrence of.
     * 
     * @return The document.
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Document"
     *                        column="c_doc_id"
     *                        not-null="@DEL_DOCOCC_TO_DOC_SWITCH@"
     *                        insert="false"
     *                        update="false"
     */
    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * Get the document group that the occurrence is a part of.
     * 
     * @return The document group.
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.DocumentGroup"
     *                        column="c_doc_grp_id"
     *                        not-null="false"
     *                        cascade="none"
     */
    public DocumentGroup getDocumentGroup() {
        return documentGroup;
    }

    /**
     * Set the document group that the occurrence is a part of.
     * 
     * @param documentGroup The document group.
     */
    public void setDocumentGroup(DocumentGroup documentGroup) {
        this.documentGroup = (DocumentGroup)documentGroup;
    }

    protected DataSet findDataSet() {
        return this.document.getDataSet();
    }

    /**
     * Get the value of the time when an instance of the schedulable
     * is to be created, relative to the time of creation of the
     * Record that the instance will be a part of.
     * <p>
     * If <code>null</code> then this schedulable is not scheduled.
     * 
     * @return The schedule time value.
     * @hibernate.property column="c_time"
     */
    public Integer getScheduleTime() {
        return this.scheduleTime;
    }

    /**
     * Set the value of the time when an instance of the schedulable
     * is to be created, relative to the time of creation of the
     * Record that the instance will be a part of.
     * <p>
     * If <code>null</code> then this schedulable is not scheduled.
     * 
     * @param scheduleTime The schedule time value.
     */
    public void setScheduleTime(Integer scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    /**
     * Get the units of the schedule time.
     * 
     * @return The units of the schedule time.
     */
    public TimeUnits getScheduleUnits() {
        return this.scheduleUnits;
    }

    /**
     * Set the units of the schedule time.
     * 
     * @param units The units of the schedule time.
     */
    public void setScheduleUnits(TimeUnits scheduleUnits) {
        this.scheduleUnits = scheduleUnits;
    }

    /**
     * Get the string value of the enumerated schedule units.
     * <p>
     * Only used by Hibernate to persist the string value of the 
     * enumerated schedule units.
     * 
     * @return The string value of the enumerated schedule units.
     * 
     * @hibernate.property column="c_units"
     */
    protected String getEnumUnits() {
        if ( null == this.scheduleUnits ){
            return null;
        }
        else{
            return this.scheduleUnits.toString();
        }
    }

    /**
     * Set the string value of the enumerated schedule units.
     * <p>
     * Only used by Hibernate to un-persist the string value of 
     * the enumerated schedule units.
     * 
     * @param enumType The string value of the enumerated 
     * schedule units
     */
    protected void setEnumUnits(String enumUnits) {
        if ( null == enumUnits ){
            setScheduleUnits(null);
        }
        else{
            setScheduleUnits(TimeUnits.valueOf(enumUnits));
        }
    }
    
    /**
     * Get the value of the randomization trigger flag.
     * <p>
     * If True, and the parent Dataset has the randomization required
     * flag set to True, the completion of an instance of this document
     * occurrence is the trigger for performing randomization via the
     * Electronic Screening Log.
     * 
     * @return The randomization trigger flag.
     * @hibernate.property column="c_rnd_trigger"
     */
    public boolean isRandomizationTrigger() {
        return randomizationTrigger;
    }

    /**
     * Set the value of the randomization trigger flag.
     * <p>
     * If True, and the parent Dataset has the randomization required
     * flag set to True, the completion of an instance of this document
     * occurrence is the trigger for performing randomization via the
     * Electronic Screening Log.
     * 
     * @param randomizationTrigger The randomization trigger flag.
     */
    public void setRandomizationTrigger(boolean randomizationTrigger) {
        this.randomizationTrigger = randomizationTrigger;
    }

    /**
     * Get the value of the locked flag.
     * <p>
     * If True then the document occurrence has been locked, and creation
     * of instances of it should not be permitted.
     * 
     * @return The value of the locked flag.
     * @hibernate.property column="c_locked"
     */
    public boolean isLocked() {
		return locked;
	}

    /**
     * Set the value of the locked flag.
     * <p>
     * If True then the document occurrence has been locked, and creation
     * of instances of it should not be permitted.
     * 
     * @param locked The value of the locked flag.
     */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
     * Get the collection of reminders for the schedulable object.
     * 
     * @return The collection of reminders.
     * 
     * @hibernate.list cascade="all" batch-size="100"
     * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.Reminder"
     * @hibernate.key column="c_schedulable_id"
     *                not-null="true"
     * @hibernate.list-index column="c_index"
     */
    public List<Reminder> getReminders() {
        return reminders;
    }

    /**
     * Set the collection of reminders for the schedulable object.
     * 
     * @param reminders The collection of reminders.
     */
    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }
    
    /**
     * Retrieve the number of reminders associated with this 
     * schedulable object.
     * 
     * @return The number of reminders.
     */
    public int numReminders(){
        return this.reminders.size();
    }
    
    /**
     * Add a single reminder to this schedulable objects collection
     * of reminders.
     * 
     * @param reminder The reminder to add.
     * @throws ModelException if a <code>null</code> reminder is added.
     */
    public void addReminder(Reminder reminder) throws ModelException{
        if ( null == reminder ){
            throw new ModelException("Cannot add a null reminder");
        }
        this.reminders.add((Reminder)reminder);
    }

    /**
     * Retrieve a single reminder from the schedulable objects collection
     * of reminders.
     * 
     * @param index The index of the reminder to retrieve.
     * @return The reminder at the given index.
     * @throws ModelException if no reminder exists for the given index.
     */
    public Reminder getReminder(int index) throws ModelException{
        try{
            return reminders.get(index);
        }
        catch (IndexOutOfBoundsException ex){
            throw new ModelException("No reminder found for index "+index, ex);
        }
    }
    
    /**
     * Remove a single reminder from the schedulbale objects collection
     * of reminders.
     * 
     * @param index The index of the reminder to retrieve.
     * @throws ModelException if no reminder exists for the given index.
     */
    public void removeReminder(int index) throws ModelException{
        try{
            Reminder r = reminders.remove(index);
            if ( null != r.getId() ){
                //the object being removed has previously been persisted
                //store it in the collection of deleted objects so that 
                //it may be manually deleted when the dataset is next saved
                findDataSet().getDeletedObjects().add(r);
            }
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No reminder found for index "+index, ex);
        }
    }
    
    /**
	 * Get the primary document occurrence index.
	 * <p>
     * If this occurrence is involved in dual data entry as part of the secondary
     * dataset, the index of the occurrence of a document in the primary dataset 
     * that it is associated with. Otherwise <code>null</code>.
     * 
	 * @return The primary document occurrence index.
     * @hibernate.property column="c_prim_occ_index"
     */
    public Long getPrimaryOccIndex() {
		return primaryOccIndex;
	}

    /**
     * Set the primary document occurrence index.
	 * <p>
     * If this occurrence is involved in dual data entry as part of the secondary
     * dataset, the index of the occurrence of a document in the primary dataset 
     * that it is associated with. Otherwise <code>null</code>.
     * 
     * @param primaryOccIndex The primary document occurrence index.
     */
	public void setPrimaryOccIndex(Long primaryOccIndex) {
		this.primaryOccIndex = primaryOccIndex;
	}

	/**
	 * Get the secondary document occurrence index.
	 * <p>
     * If this occurrence is involved in dual data entry as part of the primary
     * dataset, the index of the occurrence of a document in the primary dataset 
     * that it is associated with. Otherwise <code>null</code>.
	 * 
	 * @return The secondary document occurrence index.
	 * @hibernate.property column="c_sec_occ_index"
	 */
	public Long getSecondaryOccIndex() {
		return secondaryOccIndex;
	}

	/**
	 * Set the secondary document occurrence index.
	 * <p>
     * If this occurrence is involved in dual data entry as part of the primary
     * dataset, the index of the occurrence of a document in the primary dataset 
     * that it is associated with. Otherwise <code>null</code>.
	 * 
	 * @param secondaryOccIndex The secondary document occurrence index.
	 */
	public void setSecondaryOccIndex(Long secondaryOccIndex) {
		this.secondaryOccIndex = secondaryOccIndex;
	}

    /**
     * Get a string that is the combination of the document's
     * display text and the occurrence's display text, in the form
     * &lt;document display text&gt; - &lt;doc occurrence display text&gt;.
     *  
     * @return Combined document and occurrence display text.
     */
	public String getCombinedDisplayText(){
        StringBuilder builder = new StringBuilder();
        if ( null != this.document && null != this.document.getDisplayText() ){
            builder.append(this.document.getDisplayText());
            if ( null != this.displayText ){
                builder.append(" - ");
            }
        }
        if ( null != this.displayText ){
            builder.append(this.displayText);
        }
        return builder.toString();
    }
    
    /**
     * Get a string that is the combination of the document's
     * name and the occurrence's name, in the form
     * &lt;document name&gt; (&lt;doc occurrence name&gt;).
     *  
     * @return Combined document and occurrence name.
     */
    public String getCombinedName() {
        StringBuilder builder = new StringBuilder();
        if ( null == this.document || null == this.document.getName() ){
            builder.append("Unknown");
        }
        else{
            builder.append(this.document.getName());
        }
        builder.append(" (");
        if ( null == this.name ){
            builder.append("Unknown");
        }
        else{
            builder.append(this.name);
        }
        builder.append(")");
        return builder.toString();
    }

    public org.psygrid.data.model.dto.DocumentOccurrenceDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //occurrence in the map of references
        org.psygrid.data.model.dto.DocumentOccurrenceDTO dtoO = null;
        if ( dtoRefs.containsKey(this)){
            dtoO = (org.psygrid.data.model.dto.DocumentOccurrenceDTO)dtoRefs.get(this);
        }
        if ( null == dtoO ){
            //an instance of the unit has not already
            //been created, so create it, and add it to the map 
            //of references
            dtoO = new org.psygrid.data.model.dto.DocumentOccurrenceDTO();
            dtoRefs.put(this, dtoO);
            toDTO(dtoO, dtoRefs, depth);
        }

        return dtoO;
    }
    
    public void toDTO(org.psygrid.data.model.dto.DocumentOccurrenceDTO dtoO, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoO, dtoRefs, depth);
        if ( depth != RetrieveDepth.REP_SAVE ){
            dtoO.setLabel(this.label);
            dtoO.setLocked(this.locked);
            dtoO.setPrimaryOccIndex(this.primaryOccIndex);
            dtoO.setSecondaryOccIndex(this.secondaryOccIndex);
            if ( depth != RetrieveDepth.DS_WITH_DOCS ){
	            dtoO.setRandomizationTrigger(this.randomizationTrigger);
	            dtoO.setScheduleTime(this.scheduleTime);
	            if ( null != this.scheduleUnits ){
	                dtoO.setScheduleUnits(this.scheduleUnits.toString());
	            }
	            org.psygrid.data.model.dto.ReminderDTO[] dtoReminders = 
	                new org.psygrid.data.model.dto.ReminderDTO[this.reminders.size()];
	            for (int i=0; i<this.reminders.size(); i++){
	                Reminder r = reminders.get(i);
	                dtoReminders[i] = r.toDTO(dtoRefs, depth);
	            }
	            dtoO.setReminders(dtoReminders);
            }
            if ( null != this.document ){
                dtoO.setDocument(this.document.toDTO(dtoRefs, depth));
            }
            if ( null != this.documentGroup ){
                dtoO.setDocumentGroup(this.documentGroup.toDTO(dtoRefs, depth));
            }
        }
    }
    
}
