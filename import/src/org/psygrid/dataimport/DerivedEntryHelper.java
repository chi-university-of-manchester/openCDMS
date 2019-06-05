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

package org.psygrid.dataimport;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.jeval.If;
import org.psygrid.collection.entry.jeval.JEvalHelper;
import org.psygrid.data.model.IDateValue;
import org.psygrid.data.model.IIntegerValue;
import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.model.IValue;

/**
 * Class to perform derived entry calculations during an import process.
 * <p>
 * The nuts and bolts of this is essentially the same code as in 
 * org.psygrid.collection.entry.model.DerivedValueModel, reworked to
 * use the basic repository objects.
 * 
 * @author Rob Harper
 *
 */
public class DerivedEntryHelper {

    /**
     * Number of milliseconds in a day
     */
    private static BigDecimal NUM_MS_IN_DAY = 
        BigDecimal.valueOf(1000 * 60 * 60 * 24);

    /**
     * The derived entry we are calculating a value for
     */
    private DerivedEntry de;
    
    /**
     * The document instance that the response to the derived entry
     * will exist in, and where we get the responses that are inputs
     * to the calculation from.
     */
    private DocumentInstance docInst;
    
    /**
     * The section occurrence the derived entry is contained in, and 
     * where we get input responses from.
     */
    private SectionOccurrence secOcc;
    
    /**
     * The standard code used if the calculation cannot be performed, due to
     * the value of one of the inputs itself being a standard code.
     */
    private StandardCode defaultStdCode;
    
    public DerivedEntryHelper(DerivedEntry de, DocumentInstance docInst, SectionOccurrence secOcc, StandardCode defaultStdCode){
        this.de = de;
        this.docInst = docInst;
        this.secOcc = secOcc;
        this.defaultStdCode = defaultStdCode;
    }

    public IValue calculateValue() throws Exception {
        if (de.getAggregateOperator() == null) {
            return computeValue(null);
        }

        CompositeResponse cr = (CompositeResponse)docInst.getResponse(de.getComposite(), secOcc);
        int size = cr.numCompositeRows();        
        if (size == 1) {
            return computeValue(cr.getCompositeRow(0));
        }

        INumericValue val = de.generateValue();
        Evaluator myEvaluator = createEvaluator();
        StringBuilder columnFormula = new StringBuilder();
        for (int i = 0; i < size; ++i) {
            
            String variable = getVariable(i + 1);
            Object variableValue = calculateRow(cr.getCompositeRow(i));
            if (variableValue instanceof StandardCode) {
                val.setStandardCode(defaultStdCode);
                break;
            }
            myEvaluator.putVariable(variable, variableValue.toString());
            columnFormula.append(variable);
            if (i < (size - 1)) {
                columnFormula.append(de.getAggregateOperator());
            }
        }
        val.setValue(new Double(myEvaluator.getNumberResult(columnFormula.toString())));
        
        return val;
    }

    private IValue computeValue(CompositeRow row) throws EvaluationException {
        Object result = calculateRow(row);
        INumericValue val = de.generateValue(); 
        if (result instanceof StandardCode) {
            val.setStandardCode((StandardCode) result);
        }
        else {
            val.setValue((Double) result);
        }
        return val;
    }

    private Object calculateRow(CompositeRow row) throws EvaluationException {
    	Evaluator myEvaluator = createEvaluator();
        List<String> variables = new ArrayList<String>();
		StandardCode firstStdCode = null;
		boolean allStdCodes = true;
		boolean someStdCodes = false;
		boolean allMissingCodedEntriesHaveDefaultValues = true;
		
		Map<String, NumericValue> defaults = ((org.psygrid.data.model.hibernate.DerivedEntry)de).getVariableDefaults();
		
        for (String var: de.getVariableNames()) {
        	variables.add(var);
            BasicResponse br = null;
            NumericValue defaultValue = defaults.get(var);
            if ( null == row ){
                BasicEntry be = de.getVariable(var);
                br = (BasicResponse)docInst.getResponse(be, be.getSection().getOccurrence(0));
            }
            else{
                br = row.getResponse(de.getVariable(var));
            }
            IValue val = br.getValue();
            
            if ( null != val.getStandardCode() ){
					//Only count the stdCode has being used if no default value has been given for this entry
					if (defaultValue == null) {
						allMissingCodedEntriesHaveDefaultValues = false;
						someStdCodes = true;
						if ( allStdCodes ){
							if(firstStdCode==null){
								firstStdCode = val.getStandardCode();
							} else {
								if(val.getStandardCode().getCode()!=firstStdCode.getCode()){
									allStdCodes = false;
								}
							}
						}
					}    	
            }else{
            	allStdCodes = false;
            }
            
            Double variableValue = getValueAsDouble(val);
            
            if(null == variableValue){
				variableValue = this.getVariableValueIfInitiallyNull((org.psygrid.data.model.hibernate.DerivedEntry)de, val.getStandardCode(), defaultValue);
				
				if(variableValue == null){
					return null;
				}
			}
            
            myEvaluator.putVariable(var, variableValue.toString());
        }
        
		
		if(allStdCodes && !allMissingCodedEntriesHaveDefaultValues){
			return firstStdCode;
		}
		if (someStdCodes){
			return defaultStdCode;
		}

        
		Object result = null;
		try{
			String jevalFormula = JEvalHelper.escapeVariablesInFormula(de.getFormula(), variables);
			
			result = new Double(myEvaluator.getNumberResult(jevalFormula));
			
			Double resultAsDouble = (Double)result;
			
			if(Double.isInfinite(resultAsDouble) || Double.isNaN(resultAsDouble)){
				result = this.defaultStdCode;
			}
	
		}
		catch(EvaluationException ex){
			ExceptionsHelper.handleFatalException(ex);
		}
		return result;
    }
    
    /**
	 * Works out what the assigned value for the variable should be, given that its initial value is null.
	 * In this case, the method works out when to apply a default value, for example.
	 * @param stdCode - the associated entry's stdCode
	 * @param presModel - the associated entry's presence model
	 * @param defaultValue - the associated entry's default value.
	 * @return - the assigned default value. Returns null if no value can be assigned.
     * @throws ImportException 
	 */
	private Double getVariableValueIfInitiallyNull(DerivedEntry de, StandardCode stdCode, NumericValue defaultValue) throws EvaluationException{
		
		Double returnValue = null;
		
		if(!de.getUseDefaultValuesForDisabledEntriesInCalculation()){
			//Apply the original logic
			if (null == stdCode){
				returnValue = null;
			}else{
				returnValue = getDefaultValue(defaultValue, true);
				//So if there is a standard code, a default value is applied.
				//But this seems irrelevant, because 'someStdCodes' will be true
				//in this case, which causes a missing code to be returned.
			}
		}else{
			throw new EvaluationException("Presently, Import cannot handle calc entries for studies that use default values for disabled entries.");
			/*
			//If the standard code is null (and it is implicitly true that the inital value is null,
			if(null == stdCode){
				if(presModel.getEntryStatusModel().getValue() == EntryStatus.DISABLED){
					 returnValue = getDefaultValue(defaultValue, false);
				}else{
					returnValue = null;
				}
				
			}else{
				returnValue = getDefaultValue(defaultValue, true);
				//So if there is a standard code, a default value is applied.
				//But this seems irrelevant, because 'someStdCodes' will be true
				//in this case, which causes a missing code to be returned.
			}
			*/
			
		}
		
		return returnValue;
		
	}
	
	/**
	 * Returns the defaultValue passed in as a double.
	 * If the input parameter is null and the applyDefault is 'true', it will return a default value of Double(0).
	 * Otherwise, if applyDefault is 'false' and the input defaultValue is null, it will return null;
	 * @param defaultValue
	 * @param whether to generate a default value if the input 'defaultValue' is null.
	 * @return
	 */
	private Double getDefaultValue(NumericValue defaultValue, boolean applyDefault){
		
		Double returnValue = null;
		
		if (defaultValue == null) {
			//Entry has not had a default value specified
			if(applyDefault)
				returnValue = new Double(0);
			else
				returnValue = null;
		}
		else {
			returnValue = defaultValue.getValue();
		}
		
		return returnValue;
	}


    
    /*
     	private Object calculateRow(Set<IBasicEntry> entries, int presModelIndex) {
		Evaluator myEvaluator = createEvaluator();
		StandardCode firstStdCode = null;
		boolean allStdCodes = true;
		boolean someStdCodes = false;
		boolean allMissingCodedEntriesHaveDefaultValues = true;
		List<String> variables = new ArrayList<String>();
		for (IBasicEntry entry : entries) {
			PresModel presModel = presModelsMap.get(entry).get(presModelIndex);
			String name = presModel.getVariableName();
			NumericValue defaultValue = presModel.getVariableDefaultValue();
			variables.add(name);
			BasicPresModel presModelDelegate = presModel.getPresModelDelegate();
			StandardCode stdCode = null;
			if (presModelDelegate instanceof StandardPresModel) {
				StandardPresModel stdPresModel = (StandardPresModel) presModelDelegate;
				stdCode = (StandardCode) stdPresModel.getStandardCodeModel().getValue();
				if ( stdCode != null ){
					//Only count the stdCode has being used if no default value has been given for this entry
					if (defaultValue == null) {
						allMissingCodedEntriesHaveDefaultValues = false;
						someStdCodes = true;
						if ( allStdCodes ){
							if(firstStdCode==null){
								firstStdCode = stdCode;
							} else {
								if(stdCode.getCode()!=firstStdCode.getCode()){
									allStdCodes = false;
								}
							}
						}
					}
				} else {
					allStdCodes = false;
				}
			}
			else{
				allStdCodes = false;
			}
			Double variableValue = getValueAsDouble(presModel);

			//We need a method that returns the required variable value, or it returns null.
			//If the value is null then we need to exit from this method.
			
			
			
			if(null == variableValue){
				variableValue = this.getVariableValueIfInitiallyNull(stdCode, presModel, defaultValue);
				
				if(variableValue == null){
					return null;
				}
			}
			
			myEvaluator.putVariable(name, variableValue.toString());
		}
		
		if(entries.size() == 0){
			//If there were no entries at all then just return null.
			return null;
		}
		
		if(allStdCodes && !allMissingCodedEntriesHaveDefaultValues){
			return firstStdCode;
		}
		if (someStdCodes){
			return this.derivedEntryStdCode;
		}
		Object result = null;
		try{
			if ( null == jevalFormula ){
				jevalFormula = JEvalHelper.escapeVariablesInFormula(formula, variables);
			}
			result = new Double(myEvaluator.getNumberResult(jevalFormula));
			
			Double resultAsDouble = (Double)result;
			
			if(Double.isInfinite(resultAsDouble) || Double.isNaN(resultAsDouble)){
				result = this.derivedEntryStdCode;
			}
	
		}
		catch(EvaluationException ex){
			ExceptionsHelper.handleFatalException(ex);
		}
		return result;
	}
      
     * 
     */
    
    private Evaluator createEvaluator() {
    	Evaluator evaluator = new Evaluator();
    	evaluator.putFunction(new If());
    	return evaluator;
    }
    
    private Double getValueAsDouble(IValue val) {

        if (val instanceof IDateValue) {
            return getDateAsNumberOfDays((IDateValue)val);
        }

        Double doubleVal = null;
        if (val instanceof INumericValue) {
            doubleVal = ((INumericValue)val).getValue();
        }
        else if (val instanceof IOptionValue) {
            
            Integer code = ((IOptionValue)val).getValue().getCode();
            if (code == null) {
                doubleVal = Double.valueOf(0);
            }
            else{
                doubleVal = Double.valueOf(code.intValue());
            }
        }
        else if (val instanceof IIntegerValue) {
            doubleVal = Double.valueOf(((IIntegerValue)val).getValue().intValue());
        }
        else{
            //invalid type of value for use as an input into a 
            //derived entry calculation
            throw new RuntimeException("A value of type "+val.getClass().getName()+" cannot be used as an input into the calculation of a derived entry ("+de.getDisplayText()+")");
        }
        
        return doubleVal;
    }

    private Double getDateAsNumberOfDays(IDateValue val) {
        Date date = val.getValue();
        if (date == null) {
            Integer yearValue = val.getYear();
            if (yearValue == null) {
                return Double.valueOf(0);
            }
            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set(Calendar.YEAR, yearValue.intValue());
            
            Integer monthValue = val.getMonth();
            if (monthValue != null) {
                cal.set(Calendar.MONTH, monthValue.intValue());
            }
            date = cal.getTime();
        }
        BigDecimal dateValue = BigDecimal.valueOf(date.getTime());
        BigDecimal numOfDays = dateValue.divide(NUM_MS_IN_DAY, 1,
                RoundingMode.HALF_DOWN);
        return Double.valueOf(numOfDays.doubleValue());
    }

    private String getVariable(int index) {

        if (index < 1) {
            throw new IllegalArgumentException("index must be bigger than 0"); //$NON-NLS-1$
        }

        return "a" + index; //$NON-NLS-1$

    }

}
