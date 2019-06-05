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
 * Class to represent an individual option that will appear in the
 * list of options for an OptionEntry
 * 
 * Each option has a textual value and (optionally) a code value.
 * <p>
 * Each option may also have a collection of "depenedent entries",
 * that is entrys whose status is affected in response to the option
 * being selected.
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_options"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Option extends Component {

    /**
     * Code value of the Option
     */
    private Integer code;
    
    /**
     * Boolean flag to define whether it is permitted for additional
     * textual data to be entered if this option is selected.
     */
    private boolean textEntryAllowed;
    
    /**
     * List of option dependents.
     * <p>
     * These are the entrys whose status is modified in response
     * to this option being selected.
     */
    private List<OptionDependent> optionDependents = new ArrayList<OptionDependent>();
    
    /**
     * The option entry that this option is a part of.
     */
    private OptionEntry entry;
    
    /**
     * Default no-arg constructor, as required by the Hibernate framwework
     * for all persistable classes.
     */
    public Option(){}
    
    /**
     * Constructor that accepts the display text of the option
     * 
     * @param displayText The display text for the option.
     */
    public Option(String displayText){
        this.displayText = displayText;
    }
    
    /**
     * Constructor that accepts the display text and code value
     * of the option.
     * 
     * @param displayText The display text for the option.
     * @param code The code value of the option.
     */
    public Option(String displayText, int code){
        this.displayText = displayText;
        this.code = code;
    }

    /**
     * Constructor that accepts the name and display text of the option
     * 
     * @param name The name of the option.
     * @param displayText The display text for the option.
     */
    public Option(String name, String displayText){
        super(name, displayText);
    }
    
    /**
     * Constructor that accepts the name and display text and code value
     * of the option.
     * 
     * @param name The name of the option.
     * @param displayText The display text for the option.
     * @param code The code value of the option.
     */
    public Option(String name, String displayText, int code){
        super(name, displayText);
        this.code = code;
    }
    
    /**
     * Get the code value of the Option
     * 
     * @return The code value
     * @hibernate.property column="c_code"
     *                     not-null="false"
     */
    public Integer getCode() {
        return code;
    }

    /**
     * Set the code value of the Option
     * 
     * @param codeValue The code value
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * Get the value of the boolean flag to define whether it is permitted 
     * for additional textual data to be entered if this option is selected.
     * 
     * @return The additional text allowed boolean flag.
     * @hibernate.property column="c_text_allowed"
     */
    public boolean isTextEntryAllowed() {
        return textEntryAllowed;
    }

    /**
     * Set the value of the boolean flag to define whether it is permitted 
     * for additional textual data to be entered if this option is selected.
     * 
     * @param textEntryAllowed The additional text allowed boolean flag.
     */
    public void setTextEntryAllowed(boolean textEntryAllowed) {
        this.textEntryAllowed = textEntryAllowed;
    }

    /**
     * Get the list of option dependents.
     * <p>
     * These are the entrys whose status is modified in response
     * to this option being selected.
     * 
     * @return The list of option dependents.
     * 
     * @hibernate.list cascade="all" batch-size="100"
     * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.OptionDependent"
     * @hibernate.key column="c_option_id" 
     *                not-null="true"
     * @hibernate.list-index column="c_index"
     */
    public List<OptionDependent> getOptionDependents() {
        return optionDependents;
    }

    /**
     * Set the list of option dependents.
     * 
     * @param optionDependents The list of option dependents.
     */
    protected void setOptionDependents(List<OptionDependent> optionDependents) {
        this.optionDependents = optionDependents;
    }

    /**
     * Add an option dependent to the option's collection of option
     * dependents.
     * 
     * @param optionDependent The option dependent to add.
     * @throws ModelException if the option dependent in the argument
     * is <code>null</code>.
     */
    public void addOptionDependent(OptionDependent optionDependent)
            throws ModelException {
        if ( null == optionDependent ){
            throw new ModelException("Cannot add a null option dependent");
        }
        optionDependents.add((OptionDependent)optionDependent);
    }

    /**
     * Retrieve a single option dependent fron the collection of
     * option dependents.
     * 
     * @param index The index of the option dependent to retrieve.
     * @return The option dependent with the given index.
     * @throws ModelException if no option dependent exists for the
     * given index.
     */
    public OptionDependent getOptionDependent(int index) throws ModelException {
        try{
            return optionDependents.get(index);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No option dependent found for index "+index,ex);
        }
    }

    /**
     * Remove a single option dependent from the collection of
     * option dependents.
     * 
     * @param index The index of the option dependent to remove.
     * @throws ModelException if no option dependent exists for
     * the given index.
     */
    public void removeOptionDependent(int index) throws ModelException {
        try{
            OptionDependent od = optionDependents.remove(index);
            if ( null != od.getId() ){
                try{
                    //option dependent object being removed has already been
                    //persisted so add it to the list of objects to delete
                    this.entry.getDataSet().getDeletedObjects().add(od);
                }
                catch (NullPointerException ex){
                    //do nothing - if the option is not linked to a
                    //dataset then any option dependents that we remove
                    //can just be de-referenced
                }
            }
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No option dependent found for index "+index,ex);
        }
    }

    /**
     * Get the number of option dependentscontained by the option.
     * 
     * @return The number of option dependents.
     */
    public int numOptionDependents() {
        return optionDependents.size();
    }
     
    /**
     * Get the option entry that this option is a part of.
     * 
     * @return The option entry.
     * 
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.OptionEntry"
     *                        column="c_entry_id"
     *                        not-null="true"
     *                        insert="false"
     *                        update="false"
     */
    public OptionEntry getEntry() {
        return entry;
    }

    public void setEntry(OptionEntry entry) {
        this.entry = entry;
    }

    public org.psygrid.data.model.dto.OptionDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //option in the map of references
        org.psygrid.data.model.dto.OptionDTO dtoO = null;
        if ( dtoRefs.containsKey(this)){
            dtoO = (org.psygrid.data.model.dto.OptionDTO)dtoRefs.get(this);
        }
        if ( null == dtoO ){
            //an instance of the option has not already
            //been created, so create it, and add it to the
            //map of references
            dtoO = new org.psygrid.data.model.dto.OptionDTO();
            dtoRefs.put(this, dtoO);
            toDTO(dtoO, dtoRefs, depth);
        }
        
        return dtoO;
    }
    
    public void toDTO(org.psygrid.data.model.dto.OptionDTO dtoO, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoO, dtoRefs, depth);
        dtoO.setCode(this.code);
        dtoO.setTextEntryAllowed(this.textEntryAllowed);
        if ( RetrieveDepth.RS_COMPLETE != depth &&
                RetrieveDepth.RS_NO_BINARY != depth &&
                RetrieveDepth.RS_SUMMARY != depth ){
            
            org.psygrid.data.model.dto.OptionDependentDTO[] dtoODs = new org.psygrid.data.model.dto.OptionDependentDTO[this.optionDependents.size()];
            for (int i=0; i<this.optionDependents.size(); i++){
                OptionDependent od = optionDependents.get(i);
                dtoODs[i] = od.toDTO(dtoRefs, depth);
            }
            dtoO.setOptionDependents(dtoODs);
            if ( null != this.entry ){
                dtoO.setEntry(this.entry.toDTO(dtoRefs, depth));
            }
        }
    }
}
