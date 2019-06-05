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

package org.psygrid.data.model.dto;

import java.util.Map;

/**
 * Class to represent an entrys whose value in a record is
 * derived from the values of other entrys, rather than being
 * explicitly entered.
 * 
 * @author Rob Harper
 */
public class DerivedEntryDTO extends BasicEntryDTO {

	/**
	 * Determines whether disabled variables should have their default values applied(if any).
	 * If default values are not applied (or don't exist), the entry cannot be calculated.
	 */
	boolean useDefaultValuesForDisabledEntriesInCalculation;
	
	/**
	 * The formula used to calculate the value of the derived entry.
	 */
	private String formula;

	/**
	 * Array of variables that are inputs into the calculation.
	 */
	private BasicEntryDTO[] variables = new BasicEntryDTO[0];

	/**
	 * Array of variable keys that represent how the variables
	 * are referenced in a formula.
	 */
	private String[] variableKeys= new String[0];

	/**
	 * The composite entry that is the parent of the basic entry that
	 * features in the map of variables.
	 * <p>
	 * This need only be non-<code>null</code> where the derived entry
	 * is calculated by performing some kind of aggregation over the
	 * responses that make up the different "rows" of a composite entry.
	 * In this case, the aggregateOperator property must also be 
	 * non-<code>null</code>.
	 */
	private CompositeEntryDTO composite;

	/**
	 * Aggregate operator used when the derived entry is calculated
	 * by performing an aggregation over the
	 * responses that make up the different "rows" of a composite entry.
	 * <p>
	 * If the composite property is <code>null</code> this property will
	 * not be used. If the composite property is non-<code>null</code>
	 * then this property must be non-<code>null</code> too.
	 */
	private String aggregateOperator;

	/**
	 * An array of variable keys that have default values assigned.
	 * 
	 * If a variable featured in the list of variables is not in this list, 
	 * then it's safe to assume that a value is required for it!
	 */
	private String[] variableDefaultKeys = new String[0];

	/**
	 * An array of default values for the variables listed in the 
	 * variableDefaultKeys array. Default values are used for 
	 * missing/null variables when calculating the value of this derived entry.
	 * 
	 * If a variable featured in the list of variables is not in this list, 
	 * then it's safe to assume that a value is required for it!
	 */
	private NumericValueDTO[] variableDefaultValues = new NumericValueDTO[0];

	private MultipleVariableTestDTO test;

	/**
	 * Default no-arg constructor, as required by the Hibernate framwework
	 * for all persistable classes.
	 */
	public DerivedEntryDTO(){}

	public String getFormula() {
		return this.formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	/**
	 * Get the array of input variables.
	 * 
	 * @return The array of input variables.
	 */
	public BasicEntryDTO[] getVariables() {
		return variables;
	}

	/**
	 * Set the array of input variables.
	 * 
	 * @param variables The array of input variables.
	 */
	public void setVariables(BasicEntryDTO[] variables) {
		this.variables = variables;
	}

	/**
	 * Get the array of variable keys.
	 * 
	 * @return The array of variable keys.
	 */
	public String[] getVariableKeys() {
		return variableKeys;
	}

	/**
	 * Set the array of variable keys.
	 * 
	 * @param variableKeys The array of variable keys.
	 */
	public void setVariableKeys(String[] variableKeys) {
		this.variableKeys = variableKeys;
	}

	public String getAggregateOperator() {
		return aggregateOperator;
	}

	public void setAggregateOperator(String aggregateOperator) {
		this.aggregateOperator = aggregateOperator;
	}

	public CompositeEntryDTO getComposite() {
		return composite;
	}

	public void setComposite(CompositeEntryDTO composite) {
		this.composite = composite;
	}

	public String[] getVariableDefaultKeys() {
		return variableDefaultKeys;
	}

	public void setVariableDefaultKeys(String[] variableDefaultKeys) {
		this.variableDefaultKeys = variableDefaultKeys;
	}

	public NumericValueDTO[] getVariableDefaultValues() {
		return variableDefaultValues;
	}

	public void setVariableDefaultValues(NumericValueDTO[] variableDefaultValues) {
		this.variableDefaultValues = variableDefaultValues;
	}
	
	/**
     * 
     * @return - whether to use default values for entries that are disabled when doing the calculation.
     * 
     */
    public boolean getUseDefaultValuesForDisabledEntriesInCalculation() {
		return useDefaultValuesForDisabledEntriesInCalculation;
	}

    /**
     * Sets whether to use default values for entries that are disabled when doing the calculation.
     * @param useDefaultValuesForDisabledEntriesInCalculation
     */
	public void setUseDefaultValuesForDisabledEntriesInCalculation(
			boolean useDefaultValuesForDisabledEntriesInCalculation) {
		this.useDefaultValuesForDisabledEntriesInCalculation = useDefaultValuesForDisabledEntriesInCalculation;
	}

	public org.psygrid.data.model.hibernate.DerivedEntry toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		//check for an already existing instance of a hibernate object for this 
		//derived entry in the map of references
		org.psygrid.data.model.hibernate.DerivedEntry hDE = null;
		if ( hRefs.containsKey(this)){
			hDE = (org.psygrid.data.model.hibernate.DerivedEntry)hRefs.get(this);
		}
		if ( null == hDE ){
			//an instance of the derived entry has not already
			//been created, so create it, and add it to the 
			//map of references
			hDE = new org.psygrid.data.model.hibernate.DerivedEntry();
			hRefs.put(this, hDE);
			toHibernate(hDE, hRefs);
		}

		return hDE;
	}

	@SuppressWarnings("unchecked")
	public void toHibernate(org.psygrid.data.model.hibernate.DerivedEntry hDE, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		super.toHibernate(hDE, hRefs);
		hDE.setFormula(this.formula);
		hDE.setAggregateOperator(this.aggregateOperator);

		Map hVars = hDE.getVariables();
		for ( int i=0; i<this.variableKeys.length; i++){
			String key = this.variableKeys[i];
			BasicEntryDTO value = this.variables[i];
			if ( null != key && null != value ){
				hVars.put(key, value.toHibernate(hRefs));
			}
		}
		
		hDE.setUseDefaultValuesForDisabledEntriesInCalculation(useDefaultValuesForDisabledEntriesInCalculation);

		if (null != this.test){
			hDE.setTest(test.toHibernate(hRefs));
		}

		if ( null != this.composite ){
			hDE.setComposite(this.composite.toHibernate(hRefs));
		}

		if (null != this.variableDefaultKeys) {
			Map hDefaults = hDE.getVariableDefaults();
			for ( int i=0; i<this.variableDefaultKeys.length; i++){
				String key = this.variableDefaultKeys[i];
				NumericValueDTO value = this.variableDefaultValues[i];
				if ( null != key && null != value ){
					hDefaults.put(key, value.toHibernate(hRefs));
				}
			}
		}
	}

	public MultipleVariableTestDTO getTest() {
		return test;
	}

	public void setTest(MultipleVariableTestDTO test) {
		this.test = test;
	}


}
