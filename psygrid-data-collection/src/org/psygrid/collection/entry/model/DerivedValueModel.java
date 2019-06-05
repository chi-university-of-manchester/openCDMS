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


package org.psygrid.collection.entry.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.EventListenerList;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.event.EntryStatusListener;
import org.psygrid.collection.entry.jeval.If;
import org.psygrid.collection.entry.jeval.JEvalHelper;
import org.psygrid.data.model.hibernate.*;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;

public class DerivedValueModel {

	private static final Log LOG = LogFactory.getLog(DerivedValueModel.class);

	public static final class PresModel implements EntryPresModel {
		private final String variableName;

		private final NumericValue variableDefaultValue;

		private final BasicPresModel basicPresModel;

		public PresModel(BasicPresModel basicPresModel, String variableName, NumericValue variableDefaultValue) {
			if (basicPresModel == null) {
				throw new IllegalArgumentException(
				"basicPresModel cannot be null"); //$NON-NLS-1$
			}
			if (variableName == null) {
				throw new IllegalArgumentException(
				"variableName cannot be null"); //$NON-NLS-1$
			}

			this.basicPresModel = basicPresModel;
			this.variableName = variableName;
			this.variableDefaultValue = variableDefaultValue;
		}

		public final BasicPresModel getPresModelDelegate() {
			return basicPresModel;
		}

		public final String getVariableName() {
			return variableName;
		}

		public final BasicEntry getEntry() {
			return basicPresModel.getEntry();
		}

		public final ValueModel getEntryStatusModel() {
			return basicPresModel.getEntryStatusModel();
		}

		public final ValueModel getValueModel() {
			return basicPresModel.getValueModel();
		}

		public final SectionPresModel getSectionOccPresModel() {
			return basicPresModel.getSectionOccPresModel();
		}

		public final void addEntryStatusListener(EntryStatusListener listener) {
			basicPresModel.addEntryStatusListener(listener);
		}

		public final void removeEntryStatusListener(EntryStatusListener listener) {
			basicPresModel.removeEntryStatusListener(listener);
		}

		public final Response getResponse() {
			return basicPresModel.getResponse();
		}

		public final EntryStatusListener[] getEntryStatusListeners() {
			return basicPresModel.getEntryStatusListeners();
		}

		public final NumericValue getVariableDefaultValue() {
			return variableDefaultValue;
		}

		@Override
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + variableName.hashCode();
			result = PRIME * result + basicPresModel.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final PresModel other = (PresModel) obj;

			return variableName.equals(other.variableName)
			&& basicPresModel.equals(other.basicPresModel);
		}

		public final DocumentInstance getDocInstance() {
			return basicPresModel.getDocInstance();
		}

		public final ValueModel getResponseStatusModel() {
			return basicPresModel.getResponseStatusModel();
		}

		public final ValidationResult validate(boolean partial) {
			return basicPresModel.validate(partial);
		}

		public final void clear() {
			basicPresModel.clear();
		}

		public final ValidationResultModel getValidationModel() {
			return basicPresModel.getValidationModel();
		}

		public final void performValidation(boolean partial) {
			basicPresModel.performValidation(partial);
		}

		public void reset() {
			//do nothing
		}

		public void touch() {
		}
	}

	private static BigDecimal NUM_MS_IN_DAY = BigDecimal
	.valueOf(1000 * 60 * 60 * 24);

	private PropertyChangeListener valueModelsListener;

	private String operator;

	private Map<BasicEntry, List<PresModel>> presModelsMap;

	private Double value;

	private EventListenerList listenerList = new EventListenerList();

	private String formula;

	private String jevalFormula;

	private StandardCode standardCode;

	private StandardCode derivedEntryStdCode;
	
	private final boolean useDefaultValuesForDisabledEntries;

	public DerivedValueModel(
			Map<BasicEntry, List<DerivedValueModel.PresModel>> presModelsMap,
			CompositePresModel compositePresModel, String operator,
			String formula, StandardCode derivedEntryStdCode, boolean useDefaultValuesForDisabledEntries) {
		this.presModelsMap = presModelsMap;
		this.operator = operator;
		this.formula = formula;
		this.derivedEntryStdCode = derivedEntryStdCode;
		this.useDefaultValuesForDisabledEntries = useDefaultValuesForDisabledEntries;

		initEventHandling();
		init();
		if (operator != null) {
			initComplex(compositePresModel);
		}

		calculateValue();
	}

	private void initEventHandling() {
		valueModelsListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				Double oldValue = value;
				calculateValue();
				fireValueChanged(new PropertyChangeEvent(
						DerivedValueModel.this, "value", //$NON-NLS-1$
						oldValue, value));
			}
		};
	}

	private void init() {
		for (BasicEntry variable : presModelsMap.keySet()) {
			initValueModel(variable);
		}
	}

	private void initValueModel(BasicEntry variable) {
		List<PresModel> presModels = presModelsMap.get(variable);
		for (PresModel presModel : presModels) {
			ValueModel valueModel = presModel.getValueModel();
			valueModel.addValueChangeListener(valueModelsListener);
			addListenerToStdCodeModel(presModel.getPresModelDelegate());
			addListenerToDatePresModel(presModel.getPresModelDelegate());
		}
	}

	private void addListenerToStdCodeModel(BasicPresModel basicPresModel) {
		if (basicPresModel instanceof StandardPresModel) {
			StandardPresModel stdPresModel = (StandardPresModel) basicPresModel;
			stdPresModel.getStandardCodeModel().addValueChangeListener(valueModelsListener);
		}
	}

	private void addListenerToDatePresModel(BasicPresModel presModel) {
		if (presModel instanceof DatePresModel){
			DatePresModel dpm = (DatePresModel)presModel;
			dpm.getYearModel().addValueChangeListener(valueModelsListener);
			dpm.getMonthModel().addValueChangeListener(valueModelsListener);
		}
	}

	private void initComplex(CompositePresModel compositePresModel) {

		compositePresModel.addChildModelListener(new ChildModelListener() {
			public void childModelsChanged(ChildModelEvent event) {
				processPresModel(event);
			}
		});
	}

	private void processPresModel(ChildModelEvent event) {
		for (BasicPresModel eventPresModel : event.getChildModels()) {

			Entry entry = eventPresModel.getEntry();
			List<PresModel> presModels = presModelsMap.get(entry);
			if (presModels == null) {
				return;
			}
			if (event.getType() == ChildModelEvent.Type.ADD) {
				PresModel presModel = presModels.get(0);
				presModels.add(new PresModel(eventPresModel, presModel
						.getVariableName(), presModel.getVariableDefaultValue()));
				eventPresModel.getValueModel().addValueChangeListener(
						valueModelsListener);
				addListenerToStdCodeModel(eventPresModel);
				addListenerToDatePresModel(eventPresModel);
			} else {
				presModels.remove(event.getRowIndex());
				eventPresModel.getValueModel().removeValueChangeListener(
						valueModelsListener);
				if (eventPresModel instanceof StandardPresModel) {
					StandardPresModel stdPresModel = (StandardPresModel) eventPresModel;
					stdPresModel.getStandardCodeModel().removeValueChangeListener(valueModelsListener);
				}
				if ( eventPresModel instanceof DatePresModel ){
					DatePresModel dpm = (DatePresModel)eventPresModel;
					dpm.getYearModel().removeValueChangeListener(valueModelsListener);
					dpm.getMonthModel().removeValueChangeListener(valueModelsListener);
				}
			}
		}
	}

	protected void fireValueChanged(PropertyChangeEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PropertyChangeListener.class) {
				((PropertyChangeListener) listeners[i + 1])
				.propertyChange(event);
			}
		}
	}

	public String getVariable(int index) {

		if (index < 1) {
			throw new IllegalArgumentException("index must be bigger than 0"); //$NON-NLS-1$
		}

		return "a" + index; //$NON-NLS-1$

	}

	public StandardCode getStandardCode() {
		return standardCode;
	}

	public Double getValue() {
		return value;
	}

	private Evaluator createEvaluator() {
		Evaluator evaluator = new Evaluator();
		evaluator.putFunction(new If());
		return evaluator;
	}

	private Object calculateRow(Set<BasicEntry> entries, int presModelIndex) {
		Evaluator myEvaluator = createEvaluator();
		StandardCode firstStdCode = null;
		boolean allStdCodes = true;
		boolean someStdCodes = false;
		boolean allMissingCodedEntriesHaveDefaultValues = true;
		List<String> variables = new ArrayList<String>();
		for (BasicEntry entry : entries) {
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
	
	/**
	 * Works out what the assigned value for the variable should be, given that its initial value is null.
	 * In this case, the method works out when to apply a default value, for example.
	 * @param stdCode - the associated entry's stdCode
	 * @param presModel - the associated entry's presence model
	 * @param defaultValue - the associated entry's default value.
	 * @return - the assigned default value. Returns null if no value can be assigned.
	 */
	private Double getVariableValueIfInitiallyNull(StandardCode stdCode, PresModel presModel, NumericValue defaultValue){
		
		Double returnValue = null;
		
		if(!useDefaultValuesForDisabledEntries){
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
			
		}
		
		return returnValue;
		
	}

	private void computeValue(Set<BasicEntry> entries, int presModelIndex) {
		Object result = calculateRow(entries, presModelIndex);
		if (result instanceof StandardCode) {
			value = null;
			standardCode = (StandardCode) result;
		}
		else if ( null == result ){
			standardCode = null;
			value = null;
		}
		else {
			standardCode = null;
			value = (Double) result;
		}
	}

	private void calculateValue() {
		if (operator == null) {
			computeValue(presModelsMap.keySet(), 0);
			return;
		}

		//The following code will only ever be run in the event that the calculated entry 
		//is summing the rows of a composite entry.
		Set<BasicEntry> entries = presModelsMap.keySet();
		BasicEntry firstEntry = entries.iterator().next();
		int size = presModelsMap.get(firstEntry).size();
		if (size == 1) {
			computeValue(presModelsMap.keySet(), 0);
			return;
		}

		Evaluator myEvaluator = createEvaluator();
		StringBuilder jevalColumnFormula = new StringBuilder();
		StandardCode firstStdCode = null;
		boolean someStdCodes = false;
		boolean allStdCodes = true;
		for (int i = 0; i < size; ++i) {
			String variable = getVariable(i + 1);
			Object variableValue = calculateRow(entries, i);
			if (variableValue instanceof StandardCode) {
				StandardCode stdCode = (StandardCode)variableValue;
				someStdCodes = true;
				if (allStdCodes){
					if(firstStdCode==null){
						firstStdCode = stdCode;
					} else {
						if(stdCode.getCode()!=firstStdCode.getCode()){
							allStdCodes = false;
						}
					}
				} 
			}
			else if ( null == variableValue ){
				value = null;				
				standardCode = null;
				return;
			}
			else {
				allStdCodes = false;
			}
			//JEval
			myEvaluator.putVariable(variable, variableValue.toString());
			jevalColumnFormula.append(JEvalHelper.escapeVariable(variable));
			if (i < (size - 1)) {
				jevalColumnFormula.append(operator);
			}
		}
		if(allStdCodes){
			value = null;
			standardCode = firstStdCode;
			return;
		}
		if(someStdCodes){
			value = null;
			standardCode = this.derivedEntryStdCode;
			return;
		}
		standardCode = null;
		value = null;
		try{
			value = new Double(myEvaluator.getNumberResult(jevalColumnFormula.toString()));
		}
		catch(EvaluationException ex){
			ExceptionsHelper.handleFatalException(ex);
		}
	}

	private Double getValueAsDouble(PresModel presModel) {

		if (presModel.getPresModelDelegate() instanceof DatePresModel) {
			return getDateAsNumberOfDays((DatePresModel) presModel
					.getPresModelDelegate());
		}

		Object variableValue = presModel.getValueModel().getValue();
		if (variableValue == null) {
			return null;
		}
		if (variableValue instanceof Double) {
			return (Double) variableValue;
		}
		if (variableValue instanceof Option) {
			Integer code = ((Option) variableValue).getCode();
			if (code == null) {
				return Double.valueOf(0);
			}
			return Double.valueOf(code.intValue());

		}
		if (variableValue instanceof Integer) {
			return Double.valueOf(((Integer) variableValue).intValue());
		}

		throw new IllegalStateException("Variable of wrong value type stored: " //$NON-NLS-1$
				+ variableValue.getClass());
	}

	private Double getDateAsNumberOfDays(DatePresModel datePresModel) {
		Date date = (Date) datePresModel.getValueModel().getValue();
		if (date == null) {
			Integer yearValue = (Integer) datePresModel.getYearModel().getValue();
			if (yearValue == null) {
				return null;
			}
			//set date to 1st July of the selected year (roughly the middle of the year).
			//If a month has been selected this default day/month will be overwritten
			Calendar cal = Calendar.getInstance();
			cal.clear();
			cal.set(Calendar.YEAR, yearValue.intValue());
			cal.set(Calendar.MONTH, 6);
			cal.set(Calendar.DAY_OF_MONTH, 1);

			Integer monthValue = (Integer) datePresModel.getMonthModel().getValue();
			if (monthValue != null) {
				//set month to that selected
				cal.set(Calendar.MONTH, monthValue.intValue());
				//set day to 15th (roughly the middle of the month)
				cal.set(Calendar.DAY_OF_MONTH, 15);
			}
			date = cal.getTime();
		}
		BigDecimal dateValue = BigDecimal.valueOf(date.getTime());
		BigDecimal numOfDays = dateValue.divide(NUM_MS_IN_DAY, 1,
				RoundingMode.HALF_DOWN);
		return Double.valueOf(numOfDays.doubleValue());
	}

	public void addValueChangeListener(PropertyChangeListener listener) {
		listenerList.add(PropertyChangeListener.class, listener);
	}

	public void removeValueChangeListener(PropertyChangeListener listener) {
		listenerList.remove(PropertyChangeListener.class, listener);
	}

	private int getOldPresModelIndex(List<PresModel> presModels,
			PresModel oldPresModel) {

		String errorMessage = "oldPresModel is not an internal presModel"; //$NON-NLS-1$
		if (presModels == null) {
			throw new IllegalArgumentException(errorMessage);
		}

		int index = presModels.indexOf(oldPresModel);

		if (index == -1) {
			throw new IllegalArgumentException(errorMessage);
		}
		return index;
	}

	/**
	 * Replaces the internal presModel that is equal to
	 * <code>oldPresModel</code> with <code>newPresModel</code>.
	 * 
	 * @throws IllegalArgumentException
	 *             if no presModel is found internally that matches
	 *             oldPresModel.
	 */
	public void replacePresModel(PresModel oldPresModel, PresModel newPresModel) {
		List<PresModel> presModels = presModelsMap.get(oldPresModel.getEntry());

		int index = getOldPresModelIndex(presModels, oldPresModel);
		newPresModel.getValueModel()
		.addValueChangeListener(valueModelsListener);
		PresModel removedPresModel = presModels.set(index, newPresModel);
		removedPresModel.getValueModel().removeValueChangeListener(
				valueModelsListener);
		calculateValue();

	}

}
