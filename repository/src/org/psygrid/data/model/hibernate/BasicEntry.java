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

import org.psygrid.data.export.security.DataExportActions;
import org.psygrid.data.model.IValue;

/**
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_basic_entrys"
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class BasicEntry extends Entry {

	/**
	 * The collection of validation rules associated with the entry.
	 */
	protected List<ValidationRule> validationRules = new ArrayList<ValidationRule>();
    
    /**
     * The export action to be take for the entry. Note - this is not persisted information.
     * The method is used at export time.
     */
    protected DataExportActions exportAction = null;
    
	/**
	 * The collection of transformers associated with the entry. (Note: these are for transorming upon inputting into the database).
	 */
	protected List<Transformer> transformers = new ArrayList<Transformer>();
	
	/**
	 * The collection of output transformers associated with the entry. These are for transforming upon exporting data
	 * from the database.
	 */
	protected List<Transformer> outputTransformers = new ArrayList<Transformer>();

	/**
	 * The collection of units that may be selected from for responses
	 * to the entry.
	 */
	protected List<Unit> units = new ArrayList<Unit>();

	/**
	 * If True then it is not permitted to select a standard
	 * code as the response to this entry.
	 */
	protected boolean disableStandardCodes;
	
	public BasicEntry() {
    }
	
	public BasicEntry(String name, EntryStatus entryStatus) {
		super(name, entryStatus);
	}
	
	public BasicEntry(String name, String displayText, EntryStatus entryStatus) {
		super(name, displayText, entryStatus);
	}

	public BasicEntry(String name, String displayText) {
		super(name, displayText);
	}

	public BasicEntry(String name) {
		super(name);
	}

    /**
     * 
     * @return the action associated with this entry
     */
    public DataExportActions getExportAction(){
    	return exportAction;
    }
    
    /**
     * 
     * @param action - the action to be applied to the entry.
     * NOTE - this data is NOT persisted, and the get/set methods are deployed
     * ONLY by the export logic.
     */
    public void setExportAction(DataExportActions action){
    	exportAction = action;
    }

	/**
	 * Get the collection of validation rules associated
	 * with the entry.
	 * 
	 * @return The collection of validation rules.
	 * 
	 * @hibernate.list cascade="save-update" 
	 *                 table="t_entry_rules" batch-size="100"
	 * @hibernate.key column="c_entry_id"
	 * @hibernate.many-to-many class="org.psygrid.data.model.hibernate.ValidationRule"
	 *                         column="c_rule_id"
	 * @hibernate.list-index column="c_index"
	 */
	public List<ValidationRule> getValidationRules() {
		return this.validationRules;
	}

	/**
	 * Set the collection of validation rules associated
	 * with the entry.
	 * 
	 * @param rules The collection of validation rules.
	 */
	protected void setValidationRules(List<ValidationRule> validationRules) {
		this.validationRules = validationRules;
	}

    /**
     * Add a single validation rule to the collection of validation
     * rules.
     * 
     * @param rule The validation rule to add.
     * @throws ModelException if the validation rule in the argument is
     * <code>null</code>.
     */
	public void addValidationRule(ValidationRule rule) throws ModelException {
		if ( null == rule ){
			throw new ModelException("Cannot add a null validation rule");
		}
		validationRules.add(rule);
	}

    /**
     * Retrieve a single validation rule  from the entry's collection of
     * validation rules.
     * 
     * @param index The index in the entry's collection of
     * validation rules to retrieve the unit for.
     * @return The validation rule at the given index.
     * @throws ModelException if no validation rule exists at the given index.
     */
	public ValidationRule getValidationRule(int index) throws ModelException {
		try{
			return validationRules.get(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No validation rule exists for index "+index, ex);
		}
	}

    /**
     * Insert a new validation rule at the specified index in the entry's
     * collection of validation rules.
     * 
     * @param rule The new validation rule to insert.
     * @param index The index to insert the validation rule at.
     * @throws ModelException if the index is not valid, or if the validation
     * rule in the argument is <code>null</code>.
     */
	public void insertValidationRule(ValidationRule rule, int index) throws ModelException {
		if ( null == rule ){
			throw new ModelException("Cannot insert a null validation rule.");
		}
		try{
			validationRules.add(index, (ValidationRule)rule);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("Cannot insert validation rule at index "+index+" - invalid index", ex);
		}
	}

    /**
     * Move an existing validation rule in the entry's collection of
     * valdiation rules from its current index to a new index.
     * @param currentIndex The current index of the validation rule to move.
     * @param newIndex The new index to move the validation rule to.
     * @throws ModelException if no validation rule exists for the specified 
     * current index, or if the new index is not valid.
     */
	public void moveValidationRule(int currentIndex, int newIndex) throws ModelException {
		ValidationRule v = null;
		try{
			v = validationRules.remove(currentIndex);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No validation rule found for index "+currentIndex, ex);
		}
		try{
			validationRules.add(newIndex, v);
		}
		catch(IndexOutOfBoundsException ex){
			//roll back - re-insert child element to its old position
			validationRules.add(currentIndex, v);
			throw new ModelException("Cannot move validation rule to index "+newIndex+" - invalid index", ex);
		}
	}

    /**
     * Get the number of validation rule associated with the entry.
     * 
     * @return The number of validation rules.
     */
	public int numValidationRules() {
		return validationRules.size();
	}

    /**
     * Remove a single validation rule  from the entry's collection of
     * validation rules.
     * 
     * @param index The index in the entry's collection of
     * validation rules to remove the validation rule for.
     * @throws ModelException if no validation rule exists at the given index.
     */
	public void removeValidationRule(int index) throws ModelException {
		try{
			validationRules.remove(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No validation rule exists for index "+index, ex);
		}
	}

	/**
	 * Get the collection of (input) transformers associated with the entry.
	 * 
	 * @return The collection of transformers.
	 * 
	 * @hibernate.list cascade="save-update" 
	 *                 table="t_entry_transformers" batch-size="100"
     * @hibernate.key column="c_entry_id"
	 * @hibernate.many-to-many class="org.psygrid.data.model.hibernate.Transformer"
	 *                         column="c_transformer_id"                         
	 * @hibernate.list-index column="c_index"
	 */
	public List<Transformer> getTransformers() {
		return transformers;
	}

	/**
	 * Set the collection of (input) transformers associated with the entry.
	 * 
	 * @param transformers The collection of transformers.
	 */
	public void setTransformers(List<Transformer> transformers) {
		this.transformers = transformers;
	}
	
	/**
	 * Get the collection of output transformers associated with the entry.
	 * 
	 * @return The collection of transformers.
	 * 
	 * @hibernate.list cascade="save-update" 
	 *                 table="t_entry_output_transformers" batch-size="100"
     * @hibernate.key column="c_entry_id"
	 * @hibernate.many-to-many class="org.psygrid.data.model.hibernate.Transformer"
	 *                         column="c_transformer_id"                         
	 * @hibernate.list-index column="c_index"
	 */
	public List<Transformer> getOutputTransformers() {
		return outputTransformers;
	}
	
	/**
	 * Set the collection of transformers associated with the entry.
	 * 
	 * @param transformers The collection of transformers.
	 */
	public void setOutputTransformers(List<Transformer> outputTransformers) {
		this.outputTransformers = outputTransformers;
	}
	
	

    /**
     * Get the number of output transformers associated with the entry.
     * 
     * @return The number of output transformers.
     */
    public int numOutputTransformers(){
    	return this.outputTransformers.size();
    }
    
    
    /**
     * Add a single output transformer to the collection of transformers.
     * 
     * @param transformer - The output transformer to add.
     * @throws ModelException if the transformer in the argument is
     * <code>null</code>.
     */
    public void addOutputTransformer(Transformer transformer) throws ModelException {
		if ( null == transformer ){
			throw new ModelException("Cannot add a null output transformer");
		}
		outputTransformers.add((Transformer)transformer);
    }
    
    /**
     * Retrieve a single output transformer from the entry's collection of
     * transformers.
     * 
     * @param index The index in the entry's collection of
     * output transformers to retrieve the transformer for.
     * @return The output transformer at the given index.
     * @throws ModelException if no transformer exists at the given index.
     */
    public Transformer getOutputTransformer(int index) throws ModelException {
		try{
			return outputTransformers.get(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No output transformer exists for index "+index, ex);
		}
    }
    
    /**
     * Remove a single output transformer from the entry's collection of
     * transformers.
     * 
     * @param index The index in the entry's collection of
     * output transformers to remove the transformer for.
     * @throws ModelException if no transformer exists at the given index.
     */
    public void removeOutputTransformer(int index) throws ModelException {
		try{
			outputTransformers.remove(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No output transformer exists for index "+index, ex);
		}
    }
    
    /**
     * Insert a new output transformer at the specified index in the entry's
     * collection of transformers.
     * 
     * @param transformer The new output transformer to insert.
     * @param index The index to insert the output transformer at.
     * @throws ModelException if the index is not valid, or if the 
     * transformer in the argument is <code>null</code>.
     */
    public void insertOutputTransformer(Transformer transformer, int index) throws ModelException {
		if ( null == transformer ){
			throw new ModelException("Cannot insert a null output transformer.");
		}
		try{
			outputTransformers.add(index, (Transformer)transformer);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("Cannot insert output transformer at index "+index+" - invalid index", ex);
		}
    }
    
    /**
     * Move an existing output transformer in the entry's collection of
     * transformers from its current index to a new index.
     * 
     * @param currentIndex The current index of the output transformer to move.
     * @param newIndex The new index to move the transformer to.
     * @throws ModelException if no transformer exists for the specified 
     * current index, or if the new index is not valid.
     */
    public void moveOutputTransformer(int currentIndex, int newIndex) throws ModelException {
		Transformer t = null;
		try{
			t = outputTransformers.remove(currentIndex);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No output transformer found for index "+currentIndex, ex);
		}
		try{
			outputTransformers.add(newIndex, t);
		}
		catch(IndexOutOfBoundsException ex){
			//roll back - re-insert child element to its old position
			outputTransformers.add(currentIndex, t);
			throw new ModelException("Cannot move output transformer to index "+newIndex+" - invalid index", ex);
		}
    }
 	

    /**
     * Add a single transformer to the collection of transformers.
     * 
     * @param transformer The transformer to add.
     * @throws ModelException if the transformer in the argument is
     * <code>null</code>.
     */
	public void addTransformer(Transformer transformer) throws ModelException {
		if ( null == transformer ){
			throw new ModelException("Cannot add a null transformer");
		}
		transformers.add((Transformer)transformer);
	}

    /**
     * Retrieve a single transformer from the entry's collection of
     * transformers.
     * 
     * @param index The index in the entry's collection of
     * transformers to retrieve the transformer for.
     * @return The transformer at the given index.
     * @throws ModelException if no transformer exists at the given index.
     */
	public Transformer getTransformer(int index) throws ModelException {
		try{
			return transformers.get(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No transformer exists for index "+index, ex);
		}
	}

    /**
     * Insert a new transformer at the specified index in the entry's
     * collection of transformers.
     * 
     * @param transformer The new transformer to insert.
     * @param index The index to insert the transformer at.
     * @throws ModelException if the index is not valid, or if the 
     * transformer in the argument is <code>null</code>.
     */
	public void insertTransformer(Transformer transformer, int index) throws ModelException {
		if ( null == transformer ){
			throw new ModelException("Cannot insert a null transformer.");
		}
		try{
			transformers.add(index, (Transformer)transformer);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("Cannot insert transformer at index "+index+" - invalid index", ex);
		}
	}

    /**
     * Move an existing transformer in the entry's collection of
     * transformers from its current index to a new index.
     * 
     * @param currentIndex The current index of the transformer to move.
     * @param newIndex The new index to move the transformer to.
     * @throws ModelException if no transformer exists for the specified 
     * current index, or if the new index is not valid.
     */
	public void moveTransformer(int currentIndex, int newIndex) throws ModelException {
		Transformer t = null;
		try{
			t = transformers.remove(currentIndex);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No transformer found for index "+currentIndex, ex);
		}
		try{
			transformers.add(newIndex, t);
		}
		catch(IndexOutOfBoundsException ex){
			//roll back - re-insert child element to its old position
			transformers.add(currentIndex, t);
			throw new ModelException("Cannot move transformer to index "+newIndex+" - invalid index", ex);
		}
	}

    /**
     * Get the number of transformers associated with the entry.
     * 
     * @return The number of validation rules.
     */
	public int numTransformers() {
		return this.transformers.size();
	}

    /**
     * Remove a single transformer from the entry's collection of
     * transformers.
     * 
     * @param index The index in the entry's collection of
     * transformers to remove the transformer for.
     * @throws ModelException if no transformer exists at the given index.
     */
	public void removeTransformer(int index) throws ModelException {
		try{
			transformers.remove(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No transformer exists for index "+index, ex);
		}
	}

	/**
	 * Get the set of Units that may be selected from for Responses to
	 * this Entry.
	 * 
	 * @return The set of Units
	 * 
	 * @hibernate.list cascade="none" 
	 *                 table="t_entry_units" batch-size="100"
	 * @hibernate.key column="c_entry_id"
	 * @hibernate.many-to-many class="org.psygrid.data.model.hibernate.Unit"
	 *                         column="c_unit_id"
	 * @hibernate.list-index column="c_index"
	 */
	public List<Unit> getUnits() {
		return units;
	}

	/**
	 * Set the set of Units that may be selected from for Responses to
	 * this Entry.
	 * 
	 * @param units The set of Units
	 */
	protected void setUnits(List<Unit> units) {
		this.units = units;
	}

    /**
     * Add a single unit to the collection of units.
     * 
     * @param unit The unit to add.
     * @throws ModelException if the unit in the argument is
     * <code>null</code>.
     */
	public void addUnit(Unit unit) throws ModelException{
		if ( null == unit ){
			throw new ModelException("Cannot add a null unit");
		}
		units.add((Unit)unit);
	}

    /**
     * Retrieve a single unit from the entry's collection of
     * units.
     * 
     * @param index The index in the entry's collection of
     * units to retrieve the unit for. 
     * @return The unit at the given index.
     * @throws ModelException if no unit exists at the given index.
     */
	public Unit getUnit(int index) throws ModelException {
		try{
			return units.get(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No unit exists for index "+index, ex);
		}
	}

    /**
     * Get the number of units associated with the entry.
     * 
     * @return The number of units.
     */
	public int numUnits() {
		return units.size();
	}

    /**
     * Remove a single unit from the entry's collection of units.
     * 
     * @param index The index in the entry's collection of
     * units to remove the unit for.
     * @throws ModelException if no unit exists at the given index.
     */
	public void removeUnit(int index) throws ModelException {
		try{
			units.remove(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No unit exists for index "+index, ex);
		}
	}

    /**
     * Insert a new unit at the specified index in the entry's
     * collection of units.
     * 
     * @param unit The new unit to insert.
     * @param index The index to insert the unit at.
     * @throws ModelException if the index is not valid, or if the unit
     * in the argument is <code>null</code>.
     */
	public void insertUnit(Unit unit, int index) throws ModelException {
		if ( null == unit ){
			throw new ModelException("Cannot insert a null unit.");
		}
		try{
			units.add(index, (Unit)unit );
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("Cannot insert unit at index "+index+" - invalid index", ex);
		}
	}

    /**
     * Move an existing unit in the entry's collection of units
     * from its current index to a new index.
     * 
     * @param currentIndex The current index of the unit to move.
     * @param newIndex The new index to move the unit to.
     * @throws ModelException if no unit exists for the specified current
     * index, or if the new index is not valid.
     */
	public void moveUnit(int currentIndex, int newIndex) throws ModelException {
		Unit u = null;
		try{
			u = units.remove(currentIndex);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No unit found for index "+currentIndex, ex);
		}
		try{
			units.add(newIndex, u);
		}
		catch(IndexOutOfBoundsException ex){
			//roll back - re-insert child element to its old position
			units.add(currentIndex, u);
			throw new ModelException("Cannot move unit to index "+newIndex+" - invalid index", ex);
		}
	}
	
	@Override
	protected void addChildTasks(DataSet ds) {
		//do nothing - a BasicEntry can't have children.
	}

    /**
     * Generate an IValue object suitable for a response to a
     * specific entry.
     * <p>
     * The actual value of the IValue object
     * will not be set, and will need setting independently after
     * the object has been generated.
     * 
     * @return The IValue object
     */
	public abstract IValue generateValue();

    /**
     * Convert the value of an IValue object that is a value of
     * a response to the entry into a string representation.
     * 
     * @param value The IValue object.
     * @return String representation of the value of the IValue
     * object.
     * @throws ModelException if the value of the IValue object
     * cannot be converted into a string.
     */
	public abstract String formatValue(IValue value) throws ModelException;

    /**
     * Generate a new instance of the basic entry - a basic response -
     * for the given section occurrence.
     * <p>
     * Note that for an entry for which one or more statuses are defined
     * the status of the generated response will be set by default to the 
     * status at index 0 in the list, thus ensuring that the response 
     * has a status for the its entire lifetime.
     * 
     * @param occurrence The section occurrence.
     * @return The new basic response.
     * @throws ModelException if the response cannot be created.
     */
	public BasicResponse generateInstance(SectionOccurrence occurrence) throws ModelException {
		checkSectionOccurrence(occurrence);
		BasicResponse response = new BasicResponse();
		response.setEntry(this);
		response.setSectionOccurrence((SectionOccurrence)occurrence);
		response.setStatus(ResponseStatus.NORMAL);
		return response;
	}

    /**
     * Generate a new response to the basic entry - a basic response -
     * for the given section occurrence instance.
     * <p>
     * Note that for an entry for which one or more statuses are defined
     * the status of the generated response will be set by default to the 
     * status at index 0 in the list, thus ensuring that the response 
     * has a status for the its entire lifetime.
     * 
     * @param secOccInst The section occurrence instance.
     * @return The new basic response.
     * @throws ModelException if the response cannot be created.
     */
	public BasicResponse generateInstance(SecOccInstance secOccInst) throws ModelException {
		checkSecOccInstance(secOccInst);
		BasicResponse response = new BasicResponse();
		response.setEntry(this);
		response.setSecOccInstance((SecOccInstance)secOccInst);
		response.setStatus(ResponseStatus.NORMAL);
		return response;
	}

	/**
     * Get the value of the disable standard codes flag.
     * <p>
     * If True then it is not permitted to select a standard
	 * code as the response to this entry.
     * 
     * @return The disable standard codes flag.
	 * @hibernate.property column="c_dis_std_code"
	 */
	public boolean isDisableStandardCodes() {
		return disableStandardCodes;
	}

    /**
     * Set the value of the disable standard codes flag.
     * <p>
     * If True then it is not permitted to select a standard
	 * code as the response to this entry.
	 * 
     * @param disableStandardCodes The disable standard codes flag.
     */
	public void setDisableStandardCodes(boolean disableStandardCodes) {
		this.disableStandardCodes = disableStandardCodes;
	}

	/**
	 * Get the class of value object that is applicable to be
	 * added to responses to the entry.
	 * 
	 * @return The class of value object.
	 */
	public abstract Class getValueClass();

	public void applyStandardCode(DocumentInstance docInst, SectionOccurrence secOcc, SecOccInstance secOccInst, StandardCode stdCode) {
		if ( !entryStatus.equals(EntryStatus.DISABLED) && !disableStandardCodes ){
			BasicResponse br = null;
			if ( null != secOccInst ){
				br = (BasicResponse)docInst.getResponse(this, secOccInst);
				if ( null == br ){
					br = generateInstance(secOccInst);
					docInst.addResponse(br);
				}
			}
			else {
				br = (BasicResponse)docInst.getResponse(this, secOcc);
				if ( null == br ){
					br = generateInstance(secOcc);
					docInst.addResponse(br);
				}
			}
			IValue v = generateValue();
			v.setStandardCode(stdCode);
			br.setValue(v);
		}
	}

	public abstract org.psygrid.data.model.dto.BasicEntryDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);

	public void toDTO(org.psygrid.data.model.dto.BasicEntryDTO dtoE, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		super.toDTO(dtoE, dtoRefs, depth);
		if ( depth != RetrieveDepth.REP_SAVE ){
			
			dtoE.setDisableStandardCodes(this.disableStandardCodes);
			
			//note that transformers for the entry are retrieved even
			//when toDTO is being called for a Record, to avoid
			//having to hit the database in the transformer processor
			org.psygrid.data.model.dto.TransformerDTO[] dtoTransformers = new org.psygrid.data.model.dto.TransformerDTO[this.transformers.size()];
			for (int i=0; i<this.transformers.size(); i++){
				Transformer t = transformers.get(i);
				dtoTransformers[i] = t.toDTO(dtoRefs, depth);
			}        
			dtoE.setTransformers(dtoTransformers);
			
			org.psygrid.data.model.dto.TransformerDTO[] dtoOutputTransformers = new org.psygrid.data.model.dto.TransformerDTO[this.outputTransformers.size()];
			for (int i=0; i<this.outputTransformers.size(); i++){
				Transformer t = outputTransformers.get(i);
				dtoOutputTransformers[i] = t.toDTO(dtoRefs, depth);
			}
			dtoE.setOutputTransformers(dtoOutputTransformers);

			if ( RetrieveDepth.RS_COMPLETE != depth &&
					RetrieveDepth.RS_NO_BINARY != depth &&
					RetrieveDepth.RS_SUMMARY != depth ){

				org.psygrid.data.model.dto.UnitDTO[] dtoUnits = new org.psygrid.data.model.dto.UnitDTO[this.units.size()];
				for (int i=0; i<this.units.size(); i++){
					Unit unit = units.get(i);
					dtoUnits[i] = unit.toDTO(dtoRefs, depth);
				}        
				dtoE.setUnits(dtoUnits);

				org.psygrid.data.model.dto.ValidationRuleDTO[] dtoRules = new org.psygrid.data.model.dto.ValidationRuleDTO[this.validationRules.size()];
				for (int i=0; i<this.validationRules.size(); i++){
					ValidationRule rule = validationRules.get(i);
					dtoRules[i] = rule.toDTO(dtoRefs, depth);
				}        
				dtoE.setValidationRules(dtoRules);
			}
		}
	}
}
