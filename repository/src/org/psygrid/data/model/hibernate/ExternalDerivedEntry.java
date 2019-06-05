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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.psygrid.data.model.ILongTextValue;
import org.psygrid.data.model.ITestable;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.dto.ElementDTO;
import org.psygrid.data.query.IEntryStatement;
import org.psygrid.data.query.QueryOperation;
import org.psygrid.data.query.QueryStatementValue;
import org.psygrid.data.query.hibernate.NumericStatement;

/**
 * Class to represent an entry whose value in a record is
 * derived from the values of other entrys and calculated
 * using an external service.
 * 
 * @author Lucy Bridges
 *
 * @hibernate.joined-subclass table="t_external_derived_entries"
 * 							
 * @hibernate.joined-subclass-key column="c_id"
 */
public class ExternalDerivedEntry extends BasicEntry implements ITestable {

	/**
     * The transformer used to calculate the entry.
     * 
     * This is separate to any transformers normally applied to
     * an entry as it is derived from the specified variables
     * and calculated by pressing a button (rather than upon
     * submission to the repository).
     */
    protected Transformer externalTransformer = null;
    
	/**
     * LinkedHashMap of variables. Key = variable name, Entry = instance of
     * InputableEntry.
     */
    protected LinkedHashMap<String, BasicEntry> variables = new LinkedHashMap<String, BasicEntry>();
	
    /**
     * If True allow the transformer to be called even if one of more of
     * the inputs is a standard code.
     * <p>
     * It is assumed that the transformer will gracefully handle standard codes
     * in its inputs. 
     */
    private boolean transformWithStdCodes;
    
    /**
     * Specify the list of variable names that must have values entered (not
     * standard codes), if transformWithStdCodes is allowed.
     */
    private List<String> transformRequiredVariables = new ArrayList<String>();
    
    private MultipleVariableTest test;
    
	/**
     * Default no-arg constructor, as required by the Hibernate framwework
     * for all persistable classes.
     */
    public ExternalDerivedEntry(){}
    
    /**
     * Constructor that accepts the name of the new external derived entry.
     * 
     * @param name The name of the new external derived entry.
     */
    public ExternalDerivedEntry(String name){
        super(name);
    }
 
    /**
     * Constructor that accepts the name and status of the new 
     * external derived entry.
     * 
     * @param name The name of the new external derived entry.
     * @param entryStatus The status of the new external derived entry.
     */
    public ExternalDerivedEntry(String name, EntryStatus entryStatus){
        super(name, entryStatus);
    }
    
    /**
     * Constructor that accepts the name and display text of the new 
     * derived entry.
     * 
     * @param name The name of the new external derived entry.
     * @param displayText The display text of the new external derived entry.
     */
    public ExternalDerivedEntry(String name, String displayText){
        super(name, displayText);
    }
    
    /**
     * Constructor that accepts the name, display text and status of 
     * the new External derived entry.
     * 
     * @param name The name of the new external derived entry.
     * @param displayText The display text of the new external derived entry.
     * @param entryStatus The status of the new external derived entry.
     */
    public ExternalDerivedEntry(String name, String displayText, EntryStatus entryStatus){
        super(name, displayText, entryStatus);
    }
    
    /**
     * Get the map of input variables.
     * 
     * @return The map of input variables.
     * 
     * @hibernate.map cascade="none" 
     *                table="t_ext_variables" order-by="c_variable_name asc"
     * @hibernate.key column="c_entry_id"
     * @hibernate.map-key column="c_variable_name" type="string"
     * @hibernate.many-to-many column="c_input_id" 
     *                         class="org.psygrid.data.model.hibernate.BasicEntry"
     */
    public LinkedHashMap<String, BasicEntry> getVariables() {
        return variables;
    }

    /**
     * Set the map of input variables.
     * 
     * @param variables The map of input variables.
     */
    protected void setVariables(Map<String, BasicEntry> variables) {
    	for (String key: variables.keySet()) {
    		addVariable(key, variables.get(key));
    	}
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
        BasicEntry ie = variables.get(variableName);
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
    public Set<String> getVariableNames() {
        return variables.keySet();
    }
    
    public String formatValue(IValue value) throws ModelException {
        
        ILongTextValue ltv = (ILongTextValue)value;
        return ltv.getValue(); 
    }
 
    /**
	 * Get the externalTransformer to be used to calculate
	 * the value of this external derived entry.
	 * 
	 * @return externalTransformer
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.Transformer"                     
	 *                        column="c_external_transformer_id"
	 *                        not-null="false"
	 *                        unique="false"
	 *                        cascade="save-update"
	 *                        
	 *                        
     * @return externalTransformer
     */                                 	 
	public Transformer getExternalTransformer() {
		return externalTransformer;
	}

	/**
	 * Set the name of the externalTransformer used to calculate
	 * the value of this external derived entry.
	 * 
	 * @param transformer
	 */
	public void setExternalTransformer(Transformer transformer) {
		this.externalTransformer = (Transformer)transformer;
	}

	/**
     * Retrieve the value of the transform with standard codes flag.
     * <p>
     * If True allow the transformer to be called even if one of more of
     * the inputs is a standard code.
     * <p>
     * It is assumed that the transformer will gracefully handle standard codes
     * in its inputs. 
     * 
     * @return The transform with standard codes flag.
	 * @hibernate.property column="c_trans_w_std_codes"
	 */
	public boolean isTransformWithStdCodes() {
		return transformWithStdCodes;
	}

	/**
	 * Set  the value of the transform with standard codes flag.
     * <p>
     * If True allow the transformer to be called even if one of more of
     * the inputs is a standard code.
     * <p>
     * It is assumed that the transformer will gracefully handle standard codes
     * in its inputs. 
     * 
	 * @param transformWithStdCodes The transform with standard codes flag.
	 */
	public void setTransformWithStdCodes(boolean transformWithStdCodes) {
		this.transformWithStdCodes = transformWithStdCodes;
	}

	/**
	 * Specify the list of variable names that must have values entered (not
     * standard codes) before calculation is permitted, if transformWithStdCodes 
     * is allowed.
     * 
     * @hibernate.list cascade="all"
     * 				   table="t_trans_req_vars"
     * @hibernate.key column="c_ede_id"
     *                not-null="true"
     * @hibernate.element column="c_variable"
     *                    type="string"
     * @hibernate.list-index column="c_index"
	 *
	 * @return transformRequiredVariables
	 */
	public List<String> getTransformRequiredVariables() {
		return transformRequiredVariables;
	}
	
	/**
	 * Add the name of a variable that must have a value entered before
	 * the calculation is permitted, when transformWithStdCodes is allowed.
	 * 
	 * @param variableName
	 */
	public void addTransformRequiredVariable(String variableName) {
		if (transformRequiredVariables == null) {
			transformRequiredVariables = new ArrayList<String>();
		}
		transformRequiredVariables.add(variableName);
	}
	
	/**
	 * Specify the list of variable names that must have values entered (not
     * standard codes), if transformWithStdCodes is allowed.
     * 
	 * @param transformRequiredVariables
	 */
	public void setTransformRequiredVariables(
			List<String> transformRequiredVariables) {
		this.transformRequiredVariables = transformRequiredVariables;
	}

	/**
	 * @return value
	 */
	@Override
	public ILongTextValue generateValue() {
		//generates and returns a new value object of the type longtextvalue
		ILongTextValue value = new LongTextValue();
        if ( this.units.size() > 0 ){
            value.setUnit(this.units.get(0));
        }
		return value;
	}
	
    public Class getValueClass() {
        return LongTextValue.class;
    }
 
    public org.psygrid.data.model.dto.ExternalDerivedEntryDTO toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        //check for an already existing instance of a dto object for this 
        //derived entry in the map of references
        org.psygrid.data.model.dto.ExternalDerivedEntryDTO dtoDE = null;
        if ( dtoRefs.containsKey(this)){
            dtoDE = (org.psygrid.data.model.dto.ExternalDerivedEntryDTO)dtoRefs.get(this);
        }
        if ( null == dtoDE ){
            //an instance of the derived entry has not already
            //been created, so create it, and add it to the 
            //map of references
            dtoDE = new org.psygrid.data.model.dto.ExternalDerivedEntryDTO();
            dtoRefs.put(this, dtoDE);
            toDTO(dtoDE, dtoRefs, depth);
        }
        
        return dtoDE;
    }
    
    public void toDTO(org.psygrid.data.model.dto.ExternalDerivedEntryDTO dtoDE, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
        super.toDTO(dtoDE, dtoRefs, depth);
               
        if ( RetrieveDepth.RS_COMPLETE != depth &&
                RetrieveDepth.RS_NO_BINARY != depth &&
                RetrieveDepth.RS_SUMMARY != depth ){
        	
        	dtoDE.setTransformWithStdCodes(transformWithStdCodes);
        	
            org.psygrid.data.model.dto.BasicEntryDTO[] dtoVars = new org.psygrid.data.model.dto.BasicEntryDTO[this.variables.size()];
            String[] dtoKeys = new String[this.variables.size()];
            int counter = 0;

            for (String entry: variables.keySet()) {
                dtoKeys[counter] = entry;
                dtoVars[counter] = ((BasicEntry)variables.get(entry)).toDTO(dtoRefs, depth);
                counter++;
            }
            dtoDE.setVariableKeys(dtoKeys);
            dtoDE.setVariables(dtoVars);
           
            if (externalTransformer != null ) {
            	dtoDE.setExternalTransformer(externalTransformer.toDTO(dtoRefs, depth));
            }
            
            if(test != null){
            	dtoDE.setTest(test.toDTO(dtoRefs, depth));
            }
            
            String[] dtoReqVars = new String[this.transformRequiredVariables.size()];
            counter = 0;
            for (String variableName: this.transformRequiredVariables) {
            	dtoReqVars[counter] = variableName;
            	counter++;
            }
            dtoDE.setTransformRequiredVariables(dtoReqVars);
        }
    }
	 
    protected boolean checkVariableName(String variableName){
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
		//do nothing - standard codes cannot be applied to an external derived entry in this way
		//(a standard code may only be applied to an external derived entry by the process of 
		//performing the calculation)
	}

	@Override
	public ElementDTO instantiateDTO() {
		return new org.psygrid.data.model.dto.ExternalDerivedEntryDTO();
	}

	public boolean hasTest() {
		return (test != null);
	}

	public boolean runTest() {
		return test.test(this);		
	}
	
	public void resetTest() {
		if(this.test != null){
			test.resetTest();
		}
		
	}
	
	/**
	@DEL_REP_EXTERNAL_DERIVEDENTRY_TESTID_SWITCH@
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
