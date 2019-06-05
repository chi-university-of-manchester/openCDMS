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
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.psygrid.collection.entry.event.EntryStatusEvent;
import org.psygrid.collection.entry.event.EntryStatusListener;
import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.collection.entry.validation.InformationMessage;
import org.psygrid.collection.entry.validation.TransformedMessage;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.EntryStatus;
import org.psygrid.data.model.hibernate.ResponseStatus;
import org.psygrid.data.model.hibernate.ValidationRule;
import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationMessage;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.Validator;
import com.jgoodies.validation.util.DefaultValidationResultModel;

public class BasicPresModel extends PresentationModel implements Validator,
ResponsePresModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ValidationResultModel validationModel;

	protected List<ValidationResult> validationResults;

	private ValueModel valueModel;

	private ValueModel unitModel;

	private ValueModel transformedModel;

	private final ValueModel entryStatusModel;

	private final SectionPresModel sectionOccPresModel;

	private final EventListenerList listenerList = new EventListenerList();

	private final BasicResponse response;

	private final String validationPrefix;

	private final DocumentInstance docInstance;

	private final ValueModel responseStatusModel;

	public BasicPresModel(BasicResponse response, IValue bean,
			SectionPresModel sectionOccPresModel, String validationPrefix,
			DocumentInstance docInstance) {
		super(bean);
		if (response == null) {
			throw new IllegalArgumentException("response cannot be null"); //$NON-NLS-1$
		}
		this.docInstance = docInstance;
		this.validationModel = new DefaultValidationResultModel();
		this.response = response;
		this.sectionOccPresModel = sectionOccPresModel;
		this.validationPrefix = validationPrefix;
		this.responseStatusModel = new PropertyAdapter(response, "status", true); //$NON-NLS-1$
		entryStatusModel = new ValueHolder(getEntry().getEntryStatus(), true);
		initEventHandling();
	}

	public final String getValidationPrefix() {
		return validationPrefix;
	}

	public final  BasicResponse  getResponse() {
		return response;
	}

	public final ValueModel getResponseStatusModel() {
		return responseStatusModel;
	}

	public final ValueModel getEntryStatusModel() {
		return entryStatusModel;
	}

	public final void addEntryStatusListener(EntryStatusListener listener) {
		listenerList.add(EntryStatusListener.class, listener);
	}

	public final void removeEntryStatusListener(EntryStatusListener listener) {
		listenerList.remove(EntryStatusListener.class, listener);
	}

	public final EntryStatusListener[] getEntryStatusListeners() {
		return listenerList.getListeners(EntryStatusListener.class);
	}

	protected void fireEntryStatusEvent(EntryStatusEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == EntryStatusListener.class) {
				((EntryStatusListener) listeners[i + 1]).statusChanged(event);
			}
		}
	}

	public final ValueModel getValueModel() {
		if (valueModel == null) {
			valueModel = getModel("value"); //$NON-NLS-1$
		}
		return valueModel;
	}

	public final ValueModel getUnitModel() {
		if (unitModel == null) {
			unitModel = getModel("unit"); //$NON-NLS-1$
		}

		return unitModel;
	}

	public final ValueModel getTransformedModel() {
		if (transformedModel == null) {
			transformedModel = getModel("transformed"); //$NON-NLS-1$
		}
		return transformedModel;
	}

	public final DocumentInstance getDocInstance() {
		return docInstance;
	}

	private void initEventHandling() {
		PropertyChangeListener handler = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				performValidation(false);
			}
		};
		addBeanPropertyChangeListener(handler);
		getBeanChannel().addValueChangeListener(handler);
		// Safe not to remove
		getEntryStatusModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				EntryStatus oldValue = (EntryStatus) evt.getOldValue();
				EntryStatus newValue = (EntryStatus) evt.getNewValue();
				fireEntryStatusEvent(new EntryStatusEvent(BasicPresModel.this,
						oldValue, newValue, sectionOccPresModel));
			}
		});

		getResponseStatusModel().addValueChangeListener(handler);
	}

	public ValidationResultModel getValidationModel() {
		return validationModel;
	}

	public BasicEntry getEntry() {
		return (BasicEntry) response.getEntry();
	}

	public int addValidationResult(ValidationResult result) {
		if (validationResults == null) {
			validationResults = new ArrayList<ValidationResult>();
		}

		validationResults.add(result);
		return validationResults.size() - 1;
	}

	public void removeValidationResult(ValidationResult result) {
		if (validationResults != null) {
			validationResults.remove(result);
		}
	}

	public void removeValidationResult(int index) {
		if (validationResults != null) {
			validationResults.remove(index);
		}
	}

	protected ValidationResult checkResponseStatus() {
		ValidationResult result = new ValidationResult();
		ValueModel responseStatus = getResponseStatusModel();
		if (responseStatus.getValue() == ResponseStatus.FLAGGED_INVALID) {
			result.addWarning(response.getAnnotation());
		}
		else if (responseStatus.getValue() == ResponseStatus.FLAGGED_EDITED){
			String latestComment = response.getLatestValueComment();
			if (latestComment == null) {
				latestComment = response.getAnnotation();
			}
			ValidationMessage message = new InformationMessage(latestComment);
			result.add(message);
		}
		return result;
	}

	protected ValidationResult preValidate() {
		ValidationResult result = checkResponseStatus();

		if (result.hasWarnings()) {
			return result;
		}

		if (getTransformedModel().getValue().equals(Boolean.TRUE)) {
			result.add(new TransformedMessage("This value has been transformed"));
		}
		return result;
	}

	public ValidationResult validate(boolean partial) {
		ValidationResult result = preValidate();

		if (result.hasMessages()) {
			return result;
		}

		if (validationResults != null && validationResults.size() > 0) {
			return validationResults.get(0);
		}

		Object value = getValueModel().getValue();
		EntryStatus entryStatus = RendererHelper.getInstance().getEntryStatus(this);
		if (entryStatus == EntryStatus.DISABLED) {
			return result;
		}
		if (value == null || value.equals("")) { //$NON-NLS-1$
			if (partial || entryStatus == EntryStatus.OPTIONAL)    {
				return result;
			}
			if (entryStatus == EntryStatus.MANDATORY) {
				String message = Messages
				.getString("EntryPresentationModel.mandatoryFieldMessage"); //$NON-NLS-1$
				result.addError(message);
				return result;
			}
		}

		for (int i = 0, c = getEntry().numValidationRules(); i < c; ++i) {
			ValidationRule vRule = getEntry().getValidationRule(i);
			List<String> validationErrors = vRule.validateAll(value);
			if (validationErrors.size() > 0) {
				for ( String msg: validationErrors){
					result.addError(msg);
				}
				return result;
			}
		}

		return result;
	}

	public ValidationResult validate() {
		return validate(false);
	}

	public void clear() {
		//do nothing
		//superseded by reset() which is used to nullify disabled 
		//entries in real time, rather than at the end when a
		//document occurrence is saved
	}

	public void performValidation(boolean partial) {
		ValidationResult result = validate(partial);
		getValidationModel().setResult(result);
	}

	protected final List<ValidationResult> getValidationResults() {
		return validationResults;
	}

	public SectionPresModel getSectionOccPresModel() {
		return sectionOccPresModel;
	}

	public void reset() {
		getValueModel().setValue(null);
	}

	public void touch() {
		getValueModel().setValue(getValueModel().getValue());
		
	}
}
