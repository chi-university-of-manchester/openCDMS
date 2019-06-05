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

import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.dto.ElementDTO;
import org.psygrid.data.query.IEntryStatement;
import org.psygrid.data.query.QueryOperation;
import org.psygrid.data.query.QueryStatementValue;
import org.psygrid.data.query.hibernate.OptionStatement;

/**
 * Class to represent an Entry whose value is selected from a defined 
 * list of Options.
 * 
 * @author Rob Harper
 * 
 * @hibernate.joined-subclass table="t_option_entrys"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class OptionEntry extends BasicEntry {

    /**
     * The collection of options from which a value may 
     * be selected.
     */
    private List<Option> options = new ArrayList<Option>();
    
    /**
     * The option that is the default value for the option entry.
     */
    private Option defaultValue;
    
    /**
     * Flag to indicate whether a client should display option codes 
     * or not.
     * <p>
     * This is set to True by default.
     */
    private boolean optionCodesDisplayed;
    
    /**
     * Flag to indicate whether a client should display option codes 
     * or not.
     * <p>
     * This is set to False by default.
     */
    private boolean dropDownDisplay;
    
    /**
     * Default no-arg constructor, as required by the Hibernate framework
     * for all persistable classes.
     */
    public OptionEntry(){};
    
    /**
     * Constructor that accepts the name of the new option
     * entry.
     * 
     * @param name The name of the new option entry.
     */
    public OptionEntry(String name){
        super(name);
        this.optionCodesDisplayed = true;
        this.dropDownDisplay = false;
    }    
    
    /**
     * Constructor that accepts the name and status of the 
     * new option entry.
     * 
     * @param name The name of the new option entry.
     * @param entryStatus The status of the new option entry.
     */
    public OptionEntry(String name, EntryStatus entryStatus){
        super(name, entryStatus);
        this.optionCodesDisplayed = true;
        this.dropDownDisplay = false;
    }    
    
    /**
     * Constructor that accepts the name and display text of the 
     * new option entry.
     * 
     * @param name The name of the new option entry.
     * @param displayText The display text of the new option entry.
     */
    public OptionEntry(String name, String displayText){
        super(name, displayText);
        this.optionCodesDisplayed = true;
        this.dropDownDisplay = false;
    }    
    
    /**
     * Constructor that accepts the name, display text and status
     * of the new option entry.
     * 
     * @param name The name of the new option entry.
     * @param displayText The display text of the new option entry.
     * @param entryStatus The status of the new option entry.
     */
    public OptionEntry(String name, String displayText, EntryStatus entryStatus){
        super(name, displayText, entryStatus);
        this.optionCodesDisplayed = true;
        this.dropDownDisplay = false;
    }    
    
    /**
     * Get the option that is the default value for the option entry.
     * 
     * @return The default option.
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Option"
     *                        column="c_default_id"
     *                        unique="true"
     *                        not-null="false"
     *                        fetch="join"
     */
    public Option getDefaultValue() {
        return defaultValue;
    }

    /**
     * Set the option that is the default value for the option
     * entry.
     * 
     * @param defaultValue The default option.
     */
    public void setDefaultValue(Option defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Get the collection of options from which a value may be 
     * selected.
     * 
     * @return The collection of options.
     * 
     * @hibernate.list cascade="all" batch-size="100"
     * @hibernate.one-to-many class="org.psygrid.data.model.hibernate.Option"
     * @hibernate.key column="c_entry_id" 
     *                not-null="true"
     * @hibernate.list-index column="c_index"
     */
    public List<Option> getOptions() {
        return options;
    }

    /**
     * Set the collection of options from which a value may be 
     * selected.
     * 
     * @param options The collection of options.
     */
    protected void setOptions(List<Option> options) {
        this.options = options;
    }

    /**
     * Add a single option to the option entry's collection of options.
     * 
     * @param option The option to add.
     * @throws ModelException if the option in the argument is 
     * <code>null</code>.
     */
    public void addOption(Option option) throws ModelException {
        if ( null == option ){
            throw new ModelException("Cannot add a null option");
        }
        Option o = (Option)option;
        o.setEntry(this);
        this.options.add(o);
    }

    /**
     * Retrieve a single option from the option entry's collection of
     * options.
     * 
     * @param index The index of the option to retrieve.
     * @return The option with the given index.
     * @throws ModelException if no option exists for the specified index.
     */
    public Option getOption(int index) throws ModelException {
        try{
            return options.get(index);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No option found for index "+index, ex);
        }
    }

    /**
     * Insert a new option into the option entry's collection of options
     * at the specified index.
     * 
     * @param option The option to insert.
     * @param index The index of the position to insert the option at.
     * @throws ModelException if the index to insert the option at is
     * not valid, or if the option in the argument is <code>null</code>.
     */
    public void insertOption(Option option, int index) throws ModelException {
        if ( null == option ){
            throw new ModelException("Cannot insert a null option");
        }
        try{
            Option o = (Option)option;
            o.setEntry(this);
            options.add(index, o);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("Cannot insert option at index "+index+" - invalid index", ex);
        }
    }

    /**
     * Move an existing option from its current index to a new index.
     * 
     * @param currentIndex The current index of the option to move.
     * @param newIndex The new index to move the option to.
     * @throws ModelException if no option exists for the current index, or
     * if the new index is not valid.
     */
    public void moveOption(int currentIndex, int newIndex) throws ModelException {
        Option o = null;
        try{
            o = options.remove(currentIndex);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No option found for index "+currentIndex, ex);
        }
        try{
            options.add(newIndex, o);
        }
        catch(IndexOutOfBoundsException ex){
            //roll back - re-insert option to its old position
            options.add(currentIndex, o);
            throw new ModelException("Cannot move option to index "+newIndex+" - invalid index", ex);
        }
    }

    /**
     * Get the number of options in the option entry's collection
     * of options.
     * 
     * @return The number of options.
     */
    public int numOptions() {
        return options.size();
    }

    /**
     * Remove a single option from the option entry's collection of
     * options.
     * 
     * @param index The index of the option to remove.
     * @throws ModelException if no option exists for the specified index.
     */
    public void removeOption(int index) throws ModelException {
        try{
            Option o = options.remove(index);
            if ( null != o.getId() ){
                //option object being removed has already been
                //persisted so add it to the list of objects to delete
                this.getDataSet().getDeletedObjects().add(o);
            }
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No option found for index "+index, ex);
        }
    }

    /**
     * Get the flag to indicate whether a client should display option codes 
     * or not.
     * 
     * @return The display option codes flag.
     * @hibernate.property column="c_disp_codes"
     */
    public boolean isOptionCodesDisplayed() {
        return optionCodesDisplayed;
    }

    /**
     * Set the flag to indicate whether a client should display option codes 
     * or not.
     * 
     * @param displayOptionCodes The display option codes flag.
     */
    public void setOptionCodesDisplayed(boolean displayOptionCodes) {
        this.optionCodesDisplayed = displayOptionCodes;
    }

    /**
     * Get the flag to indicate whether a client should display options
     * as as drop down list
     * 
     * @return The drop down display.
     * @hibernate.property column="c_dropdown"
     */
    public boolean isDropDownDisplay() {
        return dropDownDisplay;
    }

    /**
     * Set the flag to indicate whether a client should display options as a
     * drop down list; the default alternative is to display as a list of radio 
     * buttons.
     * 
     * @param dropDownDisplay The drop down display flag.
     */
    public void setDropDownDisplay(boolean dropDownDisplay) {
    	this.dropDownDisplay = dropDownDisplay;
    }
    
    public String formatValue(IValue value) throws ModelException {
        //Use the index of the selected option
        OptionValue ov = (OptionValue)value;        
        return Integer.toString(options.indexOf(ov.getValue()));
    }

    public IValue generateValue(String value) throws ModelException {
        try{
            IOptionValue ov = generateValue();
            int index = Integer.parseInt(value);
            //check for an option with the same code as the value
            for ( Option o: options ){
            	if ( index == o.getCode().intValue() ){
            		ov.setValue(o);
            		return ov;
            	}
            }
            //if this point is reached then no option was found with the 
            //relevant code - so pick option by index instead.
            Option o = options.get(index);
            ov.setValue(o);
            return ov;
        }
        catch(NumberFormatException ex){
            throw new ModelException("Supplied string value '"+value+"' could not be converted to an integer index", ex);
        }
        catch(IndexOutOfBoundsException ex){
            throw new ModelException("No option exists for the given index "+value, ex);
        }
    }

    public IOptionValue generateValue() {
        OptionValue ov = new OptionValue();
        if ( this.units.size() > 0 ){
            ov.setUnit(this.units.get(0));
        }
        return ov;
    }

    public Class getValueClass() {
        return OptionValue.class;
    }
    
    public org.psygrid.data.model.dto.OptionEntryDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //option entry in the map of references
        org.psygrid.data.model.dto.OptionEntryDTO dtoOE = null;
        if ( dtoRefs.containsKey(this)){
            dtoOE = (org.psygrid.data.model.dto.OptionEntryDTO)dtoRefs.get(this);
        }
        if ( null == dtoOE ){
            //an instance of the option entry has not already
            //been created, so create it, and add it to the 
            //map of references
            dtoOE = new org.psygrid.data.model.dto.OptionEntryDTO();
            dtoRefs.put(this, dtoOE);
            toDTO(dtoOE, dtoRefs, depth);
        }
        
        return dtoOE;
    }
    
    public void toDTO(org.psygrid.data.model.dto.OptionEntryDTO dtoOE, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoOE, dtoRefs, depth);
        if ( depth != RetrieveDepth.REP_SAVE ){
            
            if ( RetrieveDepth.RS_COMPLETE != depth &&
                    RetrieveDepth.RS_NO_BINARY != depth &&
                    RetrieveDepth.RS_SUMMARY != depth ){
                
                if ( null != this.defaultValue ){
                    dtoOE.setDefaultValue(this.defaultValue.toDTO(dtoRefs, depth));
                }
                
                org.psygrid.data.model.dto.OptionDTO[] dtoOptions = new org.psygrid.data.model.dto.OptionDTO[this.options.size()];
                for (int i=0; i<this.options.size(); i++){
                    Option o = options.get(i);
                    dtoOptions[i] = o.toDTO(dtoRefs, depth);
                }
                dtoOE.setOptions(dtoOptions);
                
                dtoOE.setOptionCodesDisplayed(this.optionCodesDisplayed);
                dtoOE.setDropDownDisplay(this.dropDownDisplay);
            }
        }
    }

	@Override
	public boolean isForBasicStatistics() {
		//TODO would really like to be able to distinguish here
		//between option entries where all options have a numeric 
		//code (and so are suitable for stats) and those where this 
		//is not the case...
		return true;
	}

	@Override
	public ElementDTO instantiateDTO() {
		return new org.psygrid.data.model.dto.OptionEntryDTO();
	}

	public List<QueryOperation> getQueryOperations() {
		return QueryOperation.getOperatorsForOptionEntry();
	}

	public IEntryStatement createStatement(QueryStatementValue queryStatementValue) {
		return new OptionStatement(queryStatementValue.getOptionValue());
	}

}
