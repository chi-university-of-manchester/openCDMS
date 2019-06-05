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


package org.psygrid.collection.entry.renderer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.json.JSONObject;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.adapter.LongTextValueAdapter;
import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.event.EditEvent;
import org.psygrid.collection.entry.event.EditListener;
import org.psygrid.collection.entry.event.RendererCreatedEvent;
import org.psygrid.collection.entry.event.SectionBuiltEvent;
import org.psygrid.collection.entry.event.SectionBuiltListener;
import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.model.ExternalDerivedValueModel;
import org.psygrid.collection.entry.model.StandardPresModel;
import org.psygrid.collection.entry.model.TextPresModel;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.remote.TransformerFault;
import org.psygrid.collection.entry.ui.EntryLabel;
import org.psygrid.collection.entry.ui.LongTextEntryFieldWithButton;
import org.psygrid.collection.entry.validation.LongTextValidationHandler;
import org.psygrid.data.model.ILongTextValue;
import org.psygrid.data.model.hibernate.*;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationResult;

public class ExternalDerivedRendererSPIDelegate {

	private TextPresModel presModel;
	private RendererData rendererData;
	private ExternalDerivedEntry entry;
	private ExternalDerivedValueModel externalDerivedValueModel;
	private JLabel validationLabel;
	private BuilderHandler builderHandler;

	public ExternalDerivedRendererSPIDelegate(RendererData rendererData) {
		this.rendererData = rendererData;
	}

	public BasicRenderer<TextPresModel> getRenderer() {
		RendererHandler rendererHandler = rendererData.getRendererHandler();
		builderHandler = rendererHandler.getBuilderHandler();

		rendererHandler.addEditListener(new EditListener() {
			public void editOccurred(EditEvent event) {
				rebindToDependentEntry(event.getCurrentPresModel(),
						event.getCopyPresModel());
			}
		});

		builderHandler.addSectionBuiltListener(
				new SectionBuiltListener() {
					public void sectionBuilt(SectionBuiltEvent event) {
						bindToDependentEntries();
						builderHandler.removeSectionBuiltListener(this);
					}
				});
		entry = (ExternalDerivedEntry) rendererData.getModel();
		BasicResponse response = RendererHelper.getInstance().getModelResponse(rendererData);

		LongTextValueAdapter value;
		if (response == null) {
			ILongTextValue valueDelegate = new LongTextValue("");	
			value = new LongTextValueAdapter(valueDelegate);
			if (valueDelegate.getValue() != null) {
				value.setDisplayText(String.valueOf(valueDelegate.getValue()));
			}
			response = RendererHelper.getInstance().processResponse(rendererData, value);
		}
		else {
			ILongTextValue valueDelegate = 
				(ILongTextValue) RendererHelper.getInstance().checkAndGetValue(response);
			value = new LongTextValueAdapter(valueDelegate);
			if (valueDelegate.getValue() != null) {
				value.setDisplayText(String.valueOf(valueDelegate.getValue()));
			}
		}

		presModel = rendererData.getRendererHandler().createTextPresModel(
				this, response, value, rendererData.getValidationPrefix());

		// Safe not to release listener
		presModel.getDisplayTextModel().addValueChangeListener(
				new LongTextValidationHandler(presModel));



		String displayText = RendererHelper.getInstance().concatEntryLabelAndDisplayText(entry);
		JComponent label = new EntryLabel(displayText);

		LongTextEntryFieldWithButton field = new LongTextEntryFieldWithButton (
				presModel.getDisplayTextModel(), 
				presModel.getStandardCodeModel());
		field.setRows(1);

		Status docStatus = rendererData.getDocOccurrenceInstance().getStatus();
		RendererHelper.getInstance().processEntryStatus(label, 
				presModel, field, rendererData.isCopy(), 
				docStatus, rendererData.isEditable());
		field.setEditable(false);
		RendererHelper.getInstance().processDescription(label, entry, field.getTextComponent());		
		validationLabel = new JLabel();	
		BasicRenderer<TextPresModel> renderer = new BasicRenderer<TextPresModel>(label, validationLabel, field, presModel);
		field.getPopupButton().setEnabled(field.isEnabled());
		switch (DocumentStatus.valueOf(docStatus)) {
		case INCOMPLETE:
		case REJECTED:
		case CONTROLLED:
		case COMPLETE:
			field.getPopupButton().setEnabled(true);
			break;
		case APPROVED:
		case PENDING:
		case VIEW_ONLY:
			field.getPopupButton().setEnabled(false);
			break;
		}
		field.getPopupButton().addActionListener(new ButtonPress());
		
		
		if(variablesInOtherSectionsWereChanged() == true){
			defaultValue();
		}
		
		

		/*
		 * Entry cannot be empty, so validate before save
		 */
		RendererHelper.getInstance().processValidation(presModel, validationLabel);

		int rowIndex = rendererData.getRowIndex();
		rendererHandler.putRenderer(entry, rowIndex, renderer);
		rendererHandler.fireRendererCreatedEvent(new RendererCreatedEvent(this, 
				renderer));
		return renderer;
	}
	
	private boolean variablesInOtherSectionsWereChanged(){
		
		List<Entry> changedEntries = RendererHelper.getInstance().getAppModel().getEditedEntries();
		boolean extDerEntryVarWasEdited = false;
		for(Entry e: changedEntries){
			if(extDerEntryVarWasEdited == true){
				break;
			}
			BasicEntry entry = presModel.getEntry();
			ExternalDerivedEntry extEntry = (ExternalDerivedEntry)entry;
			Set<String> varNames = extEntry.getVariableNames();
			for(String varName: varNames){
				BasicEntry e1 = extEntry.getVariable(varName);
				if(e1 == e){
					extDerEntryVarWasEdited = true;
					break;
				}
			}
		}
		
		if(extDerEntryVarWasEdited){
			RendererHelper.getInstance().getAppModel().getEditedEntries().clear();
		}
		
		return extDerEntryVarWasEdited;
		
	}

	private void rebindToDependentEntry(BasicPresModel oldPresModel,
			BasicPresModel newPresModel) {
		Set<String> variableNames = entry.getVariableNames();
		BasicEntry editedEntry = newPresModel.getEntry();
		for (String variableName : variableNames) {
			boolean required = true;
			if (entry.isTransformWithStdCodes()
					&& !entry.getTransformRequiredVariables().contains(variableName)) {
				//A response to this entry is not required (a stdcode can be used instead)
				required = false;
			}
			if (editedEntry.equals(entry.getVariable(variableName))) {
				ExternalDerivedValueModel.PresModel newDerivedPresModel = 
					new ExternalDerivedValueModel.PresModel(newPresModel, variableName, required);
				ExternalDerivedValueModel.PresModel oldDerivedPresModel =
					new ExternalDerivedValueModel.PresModel(oldPresModel, variableName, required);
				externalDerivedValueModel.replacePresModel(oldDerivedPresModel, 
						newDerivedPresModel);
			}
		}
	}

	private void bindToDependentEntries() {
		BuilderHandler builderHandler = 
			rendererData.getRendererHandler().getBuilderHandler();

		Set<String> variableNames = entry.getVariableNames();

		Map<BasicEntry, List<ExternalDerivedValueModel.PresModel>> presModelsMap =
			new LinkedHashMap<BasicEntry, List<ExternalDerivedValueModel.PresModel>>();
		for (String variableName : variableNames) {
			BasicEntry  variable = entry.getVariable(variableName);
			List<ExternalDerivedValueModel.PresModel> derivedPresModels = 
				checkAndGetPresModels(builderHandler, variable, variableName);
			presModelsMap.put(variable, derivedPresModels);
		}

		StandardCode externalDerivedEntryStdCode = getExternalDerivedEntryStdCode(builderHandler);
		externalDerivedValueModel = new ExternalDerivedValueModel(presModelsMap, externalDerivedEntryStdCode);	
		// Safe not to release listener
		externalDerivedValueModel.addValueChangeListener(new Updater());

		if (presModel.getDisplayTextModel().getValue() == null) {
			defaultValue();	
		}

	}

	private StandardCode getExternalDerivedEntryStdCode(BuilderHandler builderHandler) {
		for (StandardCode stdCode : builderHandler.getStandardCodes()) {
			if (stdCode.isUsedForDerivedEntry()) {
				return stdCode;
			}
		}
		return null;
	}

	private List<ExternalDerivedValueModel.PresModel> checkAndGetPresModels(
			BuilderHandler builderHandler, BasicEntry  variable,
			String variableName) {

		List<BasicPresModel> variablePresModels = 
			builderHandler.getBasicPresModels(variable);

		if (variablePresModels.size() == 0) {
			throw new IllegalStateException("The variable: " + variable //$NON-NLS-1$
					+ " is not an IEntry in the IDocument."); //$NON-NLS-1$
		}
		List<ExternalDerivedValueModel.PresModel> derivedPresModels = 
			new ArrayList<ExternalDerivedValueModel.PresModel>(variablePresModels.size());

		for (BasicPresModel basicPresModel : variablePresModels) {
			boolean required = true;
			if (entry.isTransformWithStdCodes()
					&& !entry.getTransformRequiredVariables().contains(variableName)) {
				//A response to this entry is not required (a stdcode can be used instead)
				required = false;
			}
			derivedPresModels.add(new ExternalDerivedValueModel.PresModel(basicPresModel, 
					variableName, required));
		}
		return derivedPresModels;
	}

	private void defaultValue() {
		ValueModel displayTextModel = presModel.getDisplayTextModel();

		LinkedHashMap<String,BasicEntry> vars = entry.getVariables();
		List<Integer> names = new ArrayList<Integer>();
		for (String bob: vars.keySet()) {
			names.add(Integer.parseInt(bob));
		}
		//Get a list of variables that must have a value.
		//If std codes are not allowed then this will be all variables.
		List<BasicEntry> requiredVariables = new ArrayList<BasicEntry>();
		if (entry.isTransformWithStdCodes()) {
			List<String> requiredNames = entry.getTransformRequiredVariables();
			if (requiredNames.size() > 0) {
				for (String variableName: entry.getVariables().keySet()) {
					if (requiredNames.contains(variableName)) {
						requiredVariables.add(entry.getVariable(variableName));
					}
				}
			}
		}
		else {
			//Value is blank by default or if any dependent entries are changed
			presModel.getStandardCodeModel().setValue(null);
			displayTextModel.setValue(null);
			return;
		}

		boolean missingValue = false;
		ValidationResult missing = new ValidationResult();
		int usedStdCodes = 0;
		//Search through the variables to find any that have not yet been given a value
		for (Integer name: names) {
			BasicEntry a  = entry.getVariable(Integer.toString(name));
			Object variableValue = builderHandler.getBasicPresModels(a).get(0).getValueModel().getValue();
			StandardPresModel spm = (StandardPresModel)builderHandler.getBasicPresModels(a).get(0);
			Object value = spm.getStandardCodeModel().getValue();
			if ( null != value ){
				//Standard code used
				usedStdCodes ++;
			}
			if (variableValue == null) {
				if (requiredVariables.contains(a)) {
					missingValue = true;
					//This is a required variable so should not be null
					missing.addWarning(Messages.getString("ExternalDerivedRendererSPIDelegate.requiredVariableMessage_p1") +a.getDisplayText()+ Messages.getString("ExternalDerivedRendererSPIDelegate.requiredVariableMessage_p2"));
				}
			}
		}
		if (usedStdCodes == names.size()) {
			//Only std codes used, so don't display validation warnings
			missing = new ValidationResult();
			presModel.getValidationModel().setResult(missing);
		}
		
		if (missingValue) {
			StandardCode stdCode = getExternalDerivedEntryStdCode(builderHandler);
			displayTextModel.setValue(RendererHelper.getInstance().getStandardCodeText(stdCode));

			Status docStatus = presModel.getDocInstance().getStatus();
			if (docStatus != null && (docStatus.getShortName().equals(Status.DOC_STATUS_PENDING)
					|| docStatus.getShortName().equals(Status.DOC_STATUS_APPROVED))) {
				//Document is not being edited so don't display validation errors!
			}
			else {
				presModel.getStandardCodeModel().setValue(stdCode);
				if (missing.getWarnings().size() > 0) {
					presModel.getValidationModel().setResult(missing);
				}
			}
		}
		else {
			//Value is blank by default or if any dependent entries are changed
			presModel.getStandardCodeModel().setValue(null);
			displayTextModel.setValue(null);
		}
	}

	/**
	 * Returns true if this entry should use all entries in the document 
	 * when calling the external transformer.
	 * For now switch on whether the external transform web service operation is "tyrercuzick".
	 */
	private boolean useAllEntries(){
		BasicEntry  entry = presModel.getEntry();
		ExternalDerivedEntry extEntry = (ExternalDerivedEntry)entry;
		String operation = extEntry.getExternalTransformer().getWsOperation();
		return operation.equals("tyrercuzick"); 
	}

	
	/**
	 * Calculate the value using an external transformer
	 */
	private void calculateValue() {
		if(useAllEntries()){
			calculateValueWhenUsingAllEntries();
		}
		else {
			calculateValueWhenUsingSpecificEntries();
		}
	}
	
	/**
	 * Gathers responses and uses the external transformer to 
	 * calculate the value.
	 * 
	 * The application must be online and stdcodes must not have
	 * been used in the relevant responses, otherwise the
	 * transformation is not attempted.
	 */
	private void calculateValueWhenUsingSpecificEntries() {
		RemoteManager m = RemoteManager.getInstance();
		ValueModel displayTextModel = presModel.getDisplayTextModel();

		if (presModel.getStandardCodeModel().getValue() != null) {
			//No need to recalculate
			return;
		}
		LinkedHashMap<String,BasicEntry> vars = entry.getVariables();
		List<Integer> names = new ArrayList<Integer>();
		for (String bob: vars.keySet()) {
			names.add(Integer.parseInt(bob));
		}
		java.util.Collections.sort(names);

		List<String> stdCodeError = new ArrayList<String>();

		//check for standard codes in the inputs
		boolean useStdCode = false;
		boolean allStdCodes = true;
		StandardCode stdCode = null;
		if ( !entry.isTransformWithStdCodes() ){
			for (Integer name: names) {
				BasicEntry a  = entry.getVariable(Integer.toString(name));
				BasicPresModel bpm = builderHandler.getBasicPresModels(a).get(0);
				if ( bpm instanceof StandardPresModel ){
					StandardPresModel spm = (StandardPresModel)bpm;
					Object value = spm.getStandardCodeModel().getValue();
					if ( null != value ){
						stdCodeError.add(Messages.getString("ExternalDerivedRendererSPIDelegate.requiredVariableMessage_p1")+a.getDisplayText()+Messages.getString("ExternalDerivedRendererSPIDelegate.requiredVariableMessage_p2"));
						if ( null == stdCode && allStdCodes ){
							stdCode = (StandardCode)value;
							useStdCode = true;
						}
						else if ( allStdCodes ){
							if ( !stdCode.equals(value ) ){
								useStdCode = true;
								allStdCodes = false;
								break;
							}
						}
						else{
							useStdCode = true;
							break;
						}

					}
					else{
						allStdCodes = false;
						if ( useStdCode ){
							break;
						}
					}
				}
			}
		}

		//standard code in the inputs - calculation does not proceed and
		//the result is a standard code
		if ( useStdCode ){
			if ( !allStdCodes ){
				stdCode = getExternalDerivedEntryStdCode(rendererData.getRendererHandler().getBuilderHandler());
			}
			displayTextModel.setValue(RendererHelper.getInstance().getStandardCodeText(stdCode));
			presModel.getStandardCodeModel().setValue(stdCode);
			return;
		}
		else if (allStdCodes) {
			BasicEntry a = entry.getVariable(Integer.toString(names.get(0)));
			BasicPresModel bpm = builderHandler.getBasicPresModels(a).get(0);
			StandardPresModel spm = (StandardPresModel)bpm;
			Object value = spm.getStandardCodeModel().getValue();
			if (value != null) {
				stdCode = getExternalDerivedEntryStdCode(rendererData.getRendererHandler().getBuilderHandler());
				displayTextModel.setValue(RendererHelper.getInstance().getStandardCodeText(stdCode));
				presModel.getStandardCodeModel().setValue(stdCode);
				return;
			}	
		}


		if (m.isConnectionAvailable() ) {

			//Connection available

			//Get a list of variables that must have a value.
			//If std codes are not allowed then this will be all variables.
			List<BasicEntry> requiredVariables = new ArrayList<BasicEntry>();
			if (entry.isTransformWithStdCodes()) {
				List<String> requiredNames = entry.getTransformRequiredVariables();
				if (requiredNames.size() > 0) {
					for (String variableName: entry.getVariables().keySet()) {
						if (requiredNames.contains(variableName)) {
							requiredVariables.add(entry.getVariable(variableName));
						}
					}
				}
			}
			else {
				for (String variableName: entry.getVariables().keySet()) {
					requiredVariables.add(entry.getVariable(variableName));
				}
			}

			List<String> errorMsg = new ArrayList<String>();
			List<String> responses = new ArrayList<String>();
			for (Integer name: names) {
				BasicEntry a  = entry.getVariable(Integer.toString(name));

				/*
				 * Retrieve the value for the given entry from its value model and format it appropriately.
				 */
				String value = null;

				Object variableValue = builderHandler.getBasicPresModels(a).get(0).getValueModel().getValue();
				if (variableValue == null) {
					if (requiredVariables.contains(a)) {
						//This is a required variable so should not be null
						errorMsg.add(Messages.getString("ExternalDerivedRendererSPIDelegate.requiredVariableMessage_p1")+a.getDisplayText()+Messages.getString("ExternalDerivedRendererSPIDelegate.requiredVariableMessage_p2"));
					}
					value = "";
				}
				else if (variableValue instanceof String) {
					value = (String) variableValue;
				}
				else if (variableValue instanceof Double) {
					value = Double.toString((Double) variableValue);
				}
				else if (variableValue instanceof Option) {
					Integer code = ((Option) variableValue).getCode();
					if (code == null) {
						value = "";
					}	
					value = code.toString();
				}
				else if (variableValue instanceof Integer) {
					value = Integer.toString(((Integer) variableValue).intValue());
				}
				responses.add(value);
			}

			/*
			 * Display error because required values are missing.
			 */
			if (errorMsg.size() > 0) {
				String message = Messages.getString("ExternalDerivedRendererSPIDelegate.calculationCouldNotBePerformedMessage");
				ValidationResult vResult = new ValidationResult();
				vResult.addError(message);
				for (String msg: errorMsg) {
					vResult.addError(msg);
				}
				presModel.getValidationModel().setResult(vResult);
				return;
			}

			String result = null;
			String error  = null;
			try {
				result = (String)m.transform(entry.getDataSet().getId(), entry.getExternalTransformer(), responses);
				
			}
			catch (RemoteServiceFault e) {
				error = e.getMessage();
			}
			catch (TransformerFault e) {
				error = e.getMessage();
			}
			

			if (result == null) {
				ValidationResult vResult = new ValidationResult();
				vResult.addError("Error: "+error);
				presModel.getValidationModel().setResult(vResult);
			}
			else {
				presModel.getStandardCodeModel().setValue(null);
				displayTextModel.setValue(result);
			}
		}
		else{
			//No connection available
			String message = Messages.getString("ExternalDerivedRendererSPIDelegate.noNetworkConnectionMessage");
			ValidationResult vResult = new ValidationResult();
			vResult.addError(message);
			for (String msg: stdCodeError) {
				vResult.addError(msg);
			}
			presModel.getValidationModel().setResult(vResult);
		}
	}

	
	/**
	 * Gathers responses and uses the external transformer to 
	 * calculate the value.
	 * 
	 * All the values in the document are used to perform the calculation.
	 * 
	 * The application must be online.
	 */
	private void calculateValueWhenUsingAllEntries() {

		if (presModel.getStandardCodeModel().getValue() != null) {
			return; //No need to recalculate
		}

		RemoteManager m = RemoteManager.getInstance();

		ValueModel displayTextModel = presModel.getDisplayTextModel();
		
		if (m.isConnectionAvailable() ) {

			Map<String,Object> responses = getResponseMap();

			String result = null;
			String error  = null;
			try {
				JSONObject job = new JSONObject(responses);
				List<String> temp = new ArrayList<String>();
				temp.add(job.toString());
				result = (String)m.transform(entry.getDataSet().getId(), entry.getExternalTransformer(), temp);
				
			}
			catch (RemoteServiceFault e) {
				error = e.getMessage();
			}
			catch (TransformerFault e) {
				error = e.getMessage();
			}
			
			if (result == null) {
				ValidationResult vResult = new ValidationResult();
				vResult.addError("Error: "+error);
				presModel.getValidationModel().setResult(vResult);
			}
			else {
				presModel.getStandardCodeModel().setValue(null);
				displayTextModel.setValue(result);
			}
		}
		else{
			//No connection available
			String message = Messages.getString("ExternalDerivedRendererSPIDelegate.noNetworkConnectionMessage");
			ValidationResult vResult = new ValidationResult();
			vResult.addError(message);
			presModel.getValidationModel().setResult(vResult);
		}
	}

	
	/**
	 *  Returns a map holding the name-value pairs for all the entries within 
	 *  the document instance which contains this ExternalDerivedEntry.
	 *  Composite entries are returned as lists of nested maps.
	 */
	private  Map<String,Object> getResponseMap() {
	
		Map<String,Object> responses=new HashMap<String,Object>();

		DocumentInstance instance=rendererData.getDocOccurrenceInstance();
		DocumentOccurrence occurrence = instance.getOccurrence();
		Document doc = occurrence.getDocument();

		int numEntries = doc.numEntries();
				
		for(int i=0;i<numEntries;i++){
			Entry entry = doc.getEntry(i);
			if(entry instanceof  BasicEntry){
				mapBasicResponse((BasicEntry)entry, responses);
			}
			else if(entry instanceof CompositeEntry){
				mapCompositeResponse((CompositeEntry)entry,responses);
			}
		}
		return responses;
	}
	
	/**
	 * Adds the current value of the response to a basic entry to the responses map.
	 * @param basic the basic entry for which we want to retrieve the response
	 * @param responses the map from entry names to response values
	 */
	private void mapBasicResponse(BasicEntry  basic,Map<String,Object> responses) {
		Object variableValue = builderHandler.getBasicPresModels(basic).get(0).getValueModel().getValue();
		String value = formatValue(variableValue);
		responses.put(basic.getName(), value);
	}

	/**
	 * Adds the current values of the responses to a composite entry to the responses map.
	 * The rows of the composite response are added as a list with each value in the list
	 * being a map of basic entry names to their response values.
	 * @param composite the composite entry for which we want to retrieve responses
	 * @param responses the map from entry names to response values
	 */
	private void mapCompositeResponse(CompositeEntry composite,Map<String,Object> responses) {
							
		List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
		int numEntries = composite.numEntries();
		for(int i=0;i<numEntries;i++){
			BasicEntry  basic = composite.getEntry(i);
			List<BasicPresModel> list = builderHandler.getBasicPresModels(basic);
			for(int j=0;j<list.size();j++){
				BasicPresModel bpm = list.get(j);
				if(rows.size()<j+1) rows.add(new HashMap<String,Object>());
				Object variableValue = bpm.getValueModel().getValue();	
				String value = formatValue(variableValue);
				rows.get(j).put(basic.getName(), value);					
			}
		}
		responses.put(composite.getName(), rows);
	}
	
	/**
	 * Format the value of an entry as a string.
	 * @param variableValue the value stored in the entry's value model.
	 * @return a string representation of the value
	 */
	private String formatValue(Object variableValue){
		String value = null;
		if (variableValue == null) {
			value = "";
		}
		else if (variableValue instanceof String) {
			value = (String) variableValue;
		}
		else if (variableValue instanceof Double) {
			value = Double.toString((Double) variableValue);
		}
		else if (variableValue instanceof Option) {
			Integer code = ((Option) variableValue).getCode();
			if (code == null) {
				value = "";
			}	
			value = code.toString();
		}
		else if (variableValue instanceof Integer) {
			value = Integer.toString(((Integer) variableValue).intValue());
		}
		return value;
	}
	
	
	private class Updater implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			defaultValue();
		}
	}

	private class ButtonPress implements ActionListener {

		public void actionPerformed(ActionEvent action) {
			calculateValue();
		}
	}
}
