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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import org.psygrid.collection.entry.event.EntryStatusListener;
import org.psygrid.data.model.hibernate.*;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;

public class ExternalDerivedValueModel {

	public static final class PresModel implements EntryPresModel {
		private final String variableName;

		private final BasicPresModel basicPresModel;

		/**
		 * Variable must have value present, if std codes are allowed.
		 */
		private final boolean required;

		public PresModel(BasicPresModel basicPresModel, String variableName, boolean required) {
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
			this.required = required;
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

		/**
		 * Get whether a value for this variable is required (only
		 * if standard codes are allowed for this EDE).
		 * 
		 * @return required
		 */
		public final boolean isRequired() {
			return required;
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
			// TODO Auto-generated method stub
			
		}
	}

	private PropertyChangeListener valueModelsListener;

	private Map<BasicEntry, List<PresModel>> presModelsMap;

	private String value;

	private EventListenerList listenerList = new EventListenerList();

	private StandardCode standardCode;

	private StandardCode externalDerivedEntryStdCode;
	
	private static BigDecimal NUM_MS_IN_DAY = BigDecimal
	.valueOf(1000 * 60 * 60 * 24);
	
	public ExternalDerivedValueModel(
			Map<BasicEntry, List<ExternalDerivedValueModel.PresModel>> presModelsMap, StandardCode externalDerivedEntryStdCode) {
		this.presModelsMap = presModelsMap;
		this.externalDerivedEntryStdCode = externalDerivedEntryStdCode;

		initEventHandling();
		init();

		calculateValue();
	}

	private void initEventHandling() {
		valueModelsListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				String oldValue = value;
				calculateValue();
				fireValueChanged(new PropertyChangeEvent(
						ExternalDerivedValueModel.this, "value", //$NON-NLS-1$
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

	protected void fireValueChanged(PropertyChangeEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PropertyChangeListener.class) {
				((PropertyChangeListener) listeners[i + 1])
				.propertyChange(event);
			}
		}
	}

	public StandardCode getStandardCode() {
		return standardCode;
	}

	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	public void setStandardCode(StandardCode stdCode) {
		this.standardCode = stdCode;
	}

	private void calculateValue() {
		value = null;
		standardCode = null;
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
}
