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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jeval.EvaluationException;

import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.ITestable;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.dto.ElementDTO;
import org.psygrid.data.query.IEntryStatement;
import org.psygrid.data.query.QueryOperation;
import org.psygrid.data.query.QueryStatementValue;
import org.psygrid.data.query.hibernate.NumericStatement;


/**
 * Class to represent an entrys whose value in a record is
 * derived from the values of other entrys, rather than being
 * explicitly entered.
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_derived_entrys"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class DerivedEntry extends BasicEntry implements ITestable {

	/**
	 * Determines whether disabled variables should have their default values applied(if any).
	 * If default values are not applied (or don't exist), the entry cannot be calculated.
	 */
	boolean useDefaultValuesForDisabledEntriesInCalculation = false;
	
	
	/**
     * The formula used to calculate the value of the derived entry.
     */
    private String formula;
    
    /**
     * HashMap of variables. Key = variable name, Entry = instance of
     * InputableEntry.
     * <p>
     * Note that the Map cannot use parameterized types as this is not
     * supported by the qdox parser used by xdoclet.
     */
    private Map variables = new HashMap();

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
    private CompositeEntry composite;
    
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
     * The defaults used for missing/null variables when calculating the
     * value of this derived entry.
     * 
     * If a variable featured in the list of variables is not in this list, 
     * then it's safe to assume that a value is required for it!
     */
    private Map<String,NumericValue> variableDefaults = new HashMap<String,NumericValue>();
    
    private MultipleVariableTest test;
    

    
    /**
     * Default no-arg constructor, as required by the Hibernate framwework
     * for all persistable classes.
     */
    public DerivedEntry(){}
    
    /**
     * Constructor that accepts the name of the new derived entry.
     * 
     * @param name The name of the new derived entry.
     */
    public DerivedEntry(String name){
        super(name);
    }
 
    /**
     * Constructor that accepts the name and status of the new 
     * derived entry.
     * 
     * @param name The name of the new derived entry.
     * @param entryStatus The status of the new derived entry.
     */
    public DerivedEntry(String name, EntryStatus entryStatus){
        super(name, entryStatus);
    }
    
    /**
     * Constructor that accepts the name and display text of the new 
     * derived entry.
     * 
     * @param name The name of the new derived entry.
     * @param displayText The display text of the new derived entry.
     */
    public DerivedEntry(String name, String displayText){
        super(name, displayText);
    }
    
    /**
     * Constructor that accepts the name, display text and status of 
     * the new derived entry.
     * 
     * @param name The name of the new derived entry.
     * @param displayText The display text of the new derived entry.
     * @param entryStatus The status of the new derived entry.
     */
    public DerivedEntry(String name, String displayText, EntryStatus entryStatus){
        super(name, displayText, entryStatus);
    }
    
    /**
     * Get the formula used to calculate the value of the derived entry.
     * <p>
     * For instance (x+y+z)/3 is a valid formula requiring the input
     * of three variables.
     * 
     * @return The formula.
     * @hibernate.property column="c_formula"
     *                     type="text"
     *                     length="4000"
     */
    public String getFormula() {
        return this.formula;
    }

    /**
     * Set the formula used to calculate the value of the derived entry.
     * <p>
     * For instance (x+y+z)/3 is a valid formula requiring the input
     * of three variables.
     * 
     * @param formula The formula.
     */
    public void setFormula(String formula) {
        this.formula = formula;
    }

    /**
     * Get the map of input variables.
     * 
     * @return The map of input variables.
     * 
     * @hibernate.map cascade="none" 
     *                table="t_variables" batch-size="100"
     * @hibernate.key column="c_entry_id"
     * @hibernate.map-key column="c_variable_name" type="string"
     * @hibernate.many-to-many column="c_input_id" 
     *                         class="org.psygrid.data.model.hibernate.BasicEntry"
     */
    public Map getVariables() {
        return variables;
    }

    /**
     * Set the map of input variables.
     * 
     * @param variables The map of input variables.
     */
    protected void setVariables(Map variables) {
        this.variables = variables;
    }
    
    
        
    /**
     * 
     * @return - whether to use default values for entries that are disabled when doing the calculation.
     * 
     * @hibernate.property column="c_def_vals_for_disabled_vars"
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


    /**
     * Add a single variable to the derived entry's collection of
     * variables.
     * 
     * @param variableName The name of the variable.
     * @param entry The entry whose value will be used as the input
     * for the variable.
     * @throws ModelException if the name of the variable is null, or 
     * if the entry that will be used as the input is null.
     */
    @SuppressWarnings("unchecked")
    public void addVariable(String variableName, BasicEntry entry)
            throws ModelException {
        if ( null == variableName ){
            throw new ModelException("It is not possible to add a variable with a null variable name");
        }
        if ( !checkVariableName(variableName) ){
            throw new ModelException("Variable name contains illegal characters; only characters A-Z, a-z, 0-9 and _ are permitted");
        }
        if ( null == entry ){
            throw new ModelException("It is not possible to add a variable with a null entry");
        }
        variables.put(variableName, entry);
    }

    /**
     * Retrieve a single variable from the derived entry's collection
     * of variables.
     * 
     * @param variableName The name of the variable to retrieve.
     * @return The entry for the given variable name.
     * @throws ModelException if no variable exists for the given name.
     */
    public BasicEntry getVariable(String variableName)
            throws ModelException {
        BasicEntry ie = (BasicEntry)variables.get(variableName);
        if ( null == ie ){
            throw new ModelException("No variable exists with name '"+variableName+"'");
        }
        return ie;
    }

    /**
     * Remove a single variable from the derived entry's collection of
     * variable.
     * 
     * @param variableName The name of the variable to remove.
     * @throws ModelException If no variable exists for the given name.
     */
    public void removeVariable(String variableName) throws ModelException {
        Object removed = variables.remove(variableName);
        if ( null == removed ){
            throw new ModelException("No variable exists with name '"+variableName+"'");
        }
    }

    /**
     * Retrieve a set containing all of the variable names for the
     * derived entry's collection of variables.
     * 
     * @return Set of variable names.
     */
    @SuppressWarnings("unchecked")
    public Set<String> getVariableNames() {
        return variables.keySet();
    }

    /**
     * Get the aggregate operator used when the derived entry is calculated
     * by performing an aggregation over the
     * responses that make up the different "rows" of a composite entry.
     * <p>
     * If the composite property is <code>null</code> this property will
     * not be used. If the composite property is non-<code>null</code>
     * then this property must be non-<code>null</code> too.
     * 
     * @return The aggregate operator.
     * @hibernate.property column="c_agg_opertr" length="3"
     */
    public String getAggregateOperator() {
        return aggregateOperator;
    }

    /**
     * Set the aggregate operator used when the derived entry is calculated
     * by performing an aggregation over the
     * responses that make up the different "rows" of a composite entry.
     * <p>
     * If the composite property is <code>null</code> this property will
     * not be used. If the composite property is non-<code>null</code>
     * then this property must be non-<code>null</code> too.
     * 
     * @param aggregateOperator The aggregate operator.
     */
    public void setAggregateOperator(String aggregateOperator) {
        this.aggregateOperator = aggregateOperator;
    }

    /**
     * Get the composite entry that is the parent of the basic entry that
     * features in the map of variables.
     * <p>
     * This need only be non-<code>null</code> where the derived entry
     * is calculated by performing some kind of aggregation over the
     * responses that make up the different "rows" of a composite entry.
     * In this case, the aggregateOperator property must also be 
     * non-<code>null</code>.
     * 
     * @return The composite entry.
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.CompositeEntry"
     *                        column="c_composite_id"
     *                        unique="false"
     *                        not-null="false"
     *                        cascade="none"
     *                        fetch="join"
     */
    public CompositeEntry getComposite() {
        return composite;
    }

    /**
     * Set the composite entry that is the parent of the basic entry that
     * features in the map of variables.
     * <p>
     * This need only be non-<code>null</code> where the derived entry
     * is calculated by performing some kind of aggregation over the
     * responses that make up the different "rows" of a composite entry.
     * In this case, the aggregateOperator property must also be 
     * non-<code>null</code>.
     * 
     * @param composite The composite entry.
     */
    public void setComposite(CompositeEntry composite) {
        this.composite = (CompositeEntry)composite;
    }

    /**
     * Get the defaults used for missing/null variables when calculating the
     * value of this derived entry.
     * 
     * If a variable featured in the list of variables is not in this list, 
     * then it's safe to assume that a value is required for it!
     * 
     * @return variableDefaults
     * 
     * @hibernate.map cascade="all" 
     *                table="t_variable_defaults" batch-size="100"
     * @hibernate.key column="c_default_id"
     * @hibernate.map-key column="c_variable_name" type="string"
     * @hibernate.many-to-many column="c_default_value_id" 
     *                         class="org.psygrid.data.model.hibernate.NumericValue"
     */
    public Map<String, NumericValue> getVariableDefaults() {
		return variableDefaults;
	}

    /**
     * Set the defaults used for missing/null variables when calculating the
     * value of this derived entry.
     * 
     * If a variable featured in the list of variables is not in this list, 
     * then it's safe to assume that a value is required for it!
     * 
     * @param variableDefaults
     */
	public void setVariableDefaults(Map<String, NumericValue> variableDefaults) throws ModelException {
		this.variableDefaults = variableDefaults;
	}
	
	/**
     * Add a default value for the given variable name.
     * 
     * @param variableName
     * @param defaultValue
     * @throws ModelException
     */
	public void addVariableDefault(String variableName, NumericValue defaultValue) throws ModelException {
		if (!variables.containsKey(variableName)) {
			throw new ModelException("The variable '"+variableName+"' must be added to the list of variables before a default value can be assigned.");
		}
		if (variableDefaults == null) {
			variableDefaults = new HashMap<String,NumericValue>();
		}
		variableDefaults.put(variableName, defaultValue);
	}

	/**
     * Remove a default value given for a particular variable.
     *  
     * @param variableName
     * @thorws ModelException
     */
	public void removeVariableDefault(String variableName) throws ModelException {
		if (!variableDefaults.containsKey(variableName)) {
			throw new ModelException("The variable '"+variableName+"' does not have a default value assigned and so cannot be removed.");
		}
		variableDefaults.remove(variableName);
	}
	
	public String formatValue(IValue value) throws ModelException {
        NumericValue nv = (NumericValue)value;
        return Double.toString(nv.getValue());
    }

    public INumericValue generateValue() {
        NumericValue nv = new NumericValue();
        if ( this.units.size() > 0 ){
            nv.setUnit(this.units.get(0));
        }
        return nv;
    }

    public Class getValueClass() {
        return NumericValue.class;
    }
    
    public org.psygrid.data.model.dto.DerivedEntryDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //derived entry in the map of references
        org.psygrid.data.model.dto.DerivedEntryDTO dtoDE = null;
        if ( dtoRefs.containsKey(this)){
            dtoDE = (org.psygrid.data.model.dto.DerivedEntryDTO)dtoRefs.get(this);
        }
        if ( null == dtoDE ){
            //an instance of the derived entry has not already
            //been created, so create it, and add it to the 
            //map of references
            dtoDE = new org.psygrid.data.model.dto.DerivedEntryDTO();
            dtoRefs.put(this, dtoDE);
            toDTO(dtoDE, dtoRefs, depth);
        }
        
        return dtoDE;
    }
    
    public void toDTO(org.psygrid.data.model.dto.DerivedEntryDTO dtoDE, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoDE, dtoRefs, depth);
        if ( depth != RetrieveDepth.REP_SAVE ){
            dtoDE.setFormula(this.formula);
            dtoDE.setAggregateOperator(this.aggregateOperator);
            if ( RetrieveDepth.RS_COMPLETE != depth &&
                    RetrieveDepth.RS_NO_BINARY != depth &&
                    RetrieveDepth.RS_SUMMARY != depth ){
            	
                org.psygrid.data.model.dto.BasicEntryDTO[] dtoVars = new org.psygrid.data.model.dto.BasicEntryDTO[this.variables.size()];
                String[] dtoKeys = new String[this.variables.size()];
                int counter = 0;
                Iterator it = this.variables.entrySet().iterator();
                while ( it.hasNext() ){
                    Map.Entry e = (Map.Entry)it.next();
                    dtoKeys[counter] = (String)e.getKey();
                    dtoVars[counter] = ((BasicEntry)e.getValue()).toDTO(dtoRefs, depth);
                    counter++;
                }
                dtoDE.setVariableKeys(dtoKeys);
                dtoDE.setVariables(dtoVars);
                
                dtoDE.setUseDefaultValuesForDisabledEntriesInCalculation(useDefaultValuesForDisabledEntriesInCalculation);
                
                if (null != this.variableDefaults) {
                	String[] dtoDefaultKeys = new String[this.variableDefaults.size()];
                	org.psygrid.data.model.dto.NumericValueDTO[] dtoDefaultValues = new org.psygrid.data.model.dto.NumericValueDTO[this.variableDefaults.size()];
                	counter = 0;
                	for (String variableName: variableDefaults.keySet()) {
                		dtoDefaultKeys[counter] = variableName;
                		dtoDefaultValues[counter] = variableDefaults.get(variableName).toDTO(dtoRefs, depth);
                		counter++;
                	}
                	dtoDE.setVariableDefaultKeys(dtoDefaultKeys);
                	dtoDE.setVariableDefaultValues(dtoDefaultValues);
                }
                
                if ( null != this.composite ){
                    dtoDE.setComposite(this.composite.toDTO(dtoRefs, depth));
                }
                
                if (null != this.test){
                	dtoDE.setTest(test.toDTO(dtoRefs, depth));
                }
            }
        }
    }
    
    private boolean checkVariableName(String variableName){
        Pattern p = Pattern.compile("[A-Za-z0-9_]+");
        Matcher m = p.matcher(variableName);
        return m.matches();
    }

	@Override
	public boolean isForBasicStatistics() {
		return true;
	}
    
	@Override
	public void applyStandardCode(DocumentInstance docInst, SectionOccurrence secOcc, SecOccInstance secOccInst, StandardCode stdCode) {
		//do nothing - standard codes cannot be applied to a derived entry in this way
		//(a standard code may only be applied to a derived entry by the process of 
		//performing the calculation)
	}

	@Override
	public ElementDTO instantiateDTO() {
		return new org.psygrid.data.model.dto.DerivedEntryDTO();
	}



	public boolean hasTest() {
		return (test != null); 
	}

	public boolean runTest() throws EvaluationException {
		return test.test(this);
	}
	
	public void resetTest() {
		if(test != null){
			test.resetTest();
		}
	}

	/**
	@DEL_REP_DERIVEDENTRY_TESTID_SWITCH@
	*/
	public MultipleVariableTest getTest() {
		return test;
	}

	public void setTest(MultipleVariableTest test) {
		this.test = test;
	}

	public List<QueryOperation> getQueryOperations() {
		return QueryOperation.getOperatorsForNumericEntry();
	}

	public IEntryStatement createStatement(QueryStatementValue queryStatementValue) {
		return new NumericStatement(queryStatementValue.getDoubleValue());
	}

}
