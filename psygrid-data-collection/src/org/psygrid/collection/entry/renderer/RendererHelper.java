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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;

import org.psygrid.collection.entry.ApplicationModel;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.collection.entry.Editable;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.Icons;
import org.psygrid.collection.entry.TogglePopupTrigger;
import org.psygrid.collection.entry.action.DateEnableEditingAction;
import org.psygrid.collection.entry.action.DateNoAnswerAction;
import org.psygrid.collection.entry.action.EnableEditingAction;
import org.psygrid.collection.entry.action.MarkResponseInvalidAction;
import org.psygrid.collection.entry.action.MarkResponseValidAction;
import org.psygrid.collection.entry.action.NoAnswerAction;
import org.psygrid.collection.entry.action.PartialDateModeAction;
import org.psygrid.collection.entry.action.ViewProvenanceAction;
import org.psygrid.collection.entry.adapter.ValueAdapter;
import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.event.EntryStatusEvent;
import org.psygrid.collection.entry.event.EntryStatusListener;
import org.psygrid.collection.entry.event.PostEditDialogBuiltListener;
import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.model.CompositePresModel;
import org.psygrid.collection.entry.model.DatePresModel;
import org.psygrid.collection.entry.model.EntryPresModel;
import org.psygrid.collection.entry.model.FixedTableModel;
import org.psygrid.collection.entry.model.OptionPresModel;
import org.psygrid.collection.entry.model.ResponsePresModel;
import org.psygrid.collection.entry.model.SectionPresModel;
import org.psygrid.collection.entry.model.StandardPresModel;
import org.psygrid.collection.entry.model.TextPresModel;
import org.psygrid.collection.entry.renderer.RendererData.EditableStatus;
import org.psygrid.collection.entry.ui.DatePicker;
import org.psygrid.collection.entry.ui.EntryComponent;
import org.psygrid.collection.entry.ui.EntryWithButton;
import org.psygrid.collection.entry.ui.InfiniteProgressPanel;
import org.psygrid.collection.entry.validation.InformationMessage;
import org.psygrid.collection.entry.validation.ValidationHandler;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.*;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationMessage;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.view.ValidationComponentUtils;

public class RendererHelper {

	public static final Color MANDATORY_FOREGROUND = new Color(70, 70, 210);

	public static final Color MANDATORY_BACKGROUND = new Color(235, 235, 255);

	public static final Color ERROR_BACKGROUND = new Color(255, 215, 215);

	public static final Color WARNING_BACKGROUND = new Color(255, 235, 205);
	
	private static ApplicationModel model;
	
	private static RendererHelper helper = new RendererHelper();

	public RendererHelper() {
		// Utility class. There's no point in creating an instance of it.
	}
	
	public static void initializeRendererHelper(ApplicationModel mod){
		helper.model = mod;
	}
	
	public static RendererHelper getInstance(){
		return helper;
	}
	
	public ApplicationModel getAppModel(){
		return model;
	}

	public static void bindToOptionDependents(RendererData rendererData,
			OptionPresModel presModel) {
		addOptionRendererValueChangeListener(rendererData, presModel);
		OptionEntry optionEntry = (OptionEntry) presModel.getEntry();
		Option defaultOption = optionEntry.getDefaultValue();
		Option currentOption = (Option) presModel.getValueModel().getValue();
		presModel.setIgnoreDocInstanceStatus(true);
		if (currentOption != null) {
			if (currentOption.equals(defaultOption) == false) {
				updateAllDependentsStatus(rendererData, presModel,
						defaultOption, currentOption);
			} else {
				updateAllDependentsStatus(rendererData, presModel, null,
						currentOption);
			}
		}
		presModel.setIgnoreDocInstanceStatus(false);
	}

	public static Response getResponse(DocumentInstance docInstance,
                                       Entry entry, SectionPresModel sectionPresModel) {
		SectionOccurrence sectionOcc = sectionPresModel.getSectionOccurrence();
		SecOccInstance secOccInstance = sectionPresModel.getSecOccInstance();
		if (secOccInstance == null) {
			return docInstance.getResponse(entry, sectionOcc);
		}
		return docInstance.getResponse(entry, secOccInstance);
	}

	public static Response generateInstance(Entry entry,
                                            SectionPresModel sectionPresModel) {
		SectionOccurrence sectionOcc = sectionPresModel.getSectionOccurrence();
		SecOccInstance sectionOccInstance = sectionPresModel
				.getSecOccInstance();

		if (sectionOccInstance == null) {
			return entry.generateInstance(sectionOcc);
		}
		return entry.generateInstance(sectionOccInstance);
	}

	/**
	 * Updates the options to either their initial status or to the status of
	 * the IOptionDependent.
	 * 
	 * @param value
	 * @param initialStatus
	 */
	private static void updateDependentsStatus(RendererData rendererData,
			OptionPresModel presModel, Option value, boolean initialStatus) {
		RendererHandler rendererHandler = rendererData.getRendererHandler();
		OptionEntry optionEntry = (OptionEntry) rendererData.getModel();

		BuilderHandler builderHandler = rendererHandler.getBuilderHandler();
		List<SectionPresModel> sectionPresModels = builderHandler
				.getSectionPresModels();

		for (int i = 0, c = optionEntry.numOptions(); i < c; ++i) {
			Option option = optionEntry.getOption(i);
			if (option.equals(value)) {
				for (int j = 0, d = option.numOptionDependents(); j < d; ++j) {
					OptionDependent optDependent = option
							.getOptionDependent(j);
					Entry depEntry = optDependent.getDependentEntry();

					SectionOccurrence depSecOcc = optDependent
							.getDependentSecOcc();
					if (depEntry == null && depSecOcc == null) {
						throw new IllegalStateException(
								"IOptionDependent cannot " + //$NON-NLS-1$
										"have null dependentEntry and null dependentSecOcc."); //$NON-NLS-1$
					}

					if (depEntry != null) {
						List<Renderer> depRenderers = rendererHandler
								.getExistingRenderers(depEntry);

						for (Renderer depRenderer : depRenderers) {
							EntryPresModel depPresModel = ((PresModelRenderer<?>) depRenderer)
									.getPresModel();
							updateDependentRendererStatus(presModel,
									initialStatus, optDependent, depPresModel);
						}
					} else {
						EntryStatus status;
						if (initialStatus) {
							status = depSecOcc.getEntryStatus();
						} else {
							status = optDependent.getEntryStatus();
						}
						boolean foundSecOcc = false;
						for (SectionPresModel secPresModel : sectionPresModels) {
							if (secPresModel.getSectionOccurrence().equals(
									depSecOcc)) {
								foundSecOcc = true;
								secPresModel.getEntryStatusValueModel()
										.setValue(status);
							}
						}

						if (!foundSecOcc) {
							throw new IllegalStateException(
									"IOptionDependent " + //$NON-NLS-1$
											"has a dependentSecOcc that could not be "
											+ //$NON-NLS-1$
											"found in the document"); //$NON-NLS-1$
						}
					}

				}
				break;
			}
		}
	}

	private static void updateDependentRendererStatus(
			OptionPresModel presModel, boolean initialStatus,
			OptionDependent optDependent, EntryPresModel depPresModel) {

		EntryStatus status;
		if (initialStatus) {
			status = depPresModel.getEntry().getEntryStatus();
		} else {
			status = optDependent.getEntryStatus();
		}

		if (status == null) {
			throw new IllegalStateException("IOptionDependent cannot" //$NON-NLS-1$
					+ " have null entryStatus"); //$NON-NLS-1$
		}

		depPresModel.getEntryStatusModel().setValue(status);
		if (depPresModel instanceof ResponsePresModel) {
			processDocStatus(presModel, RendererHelper
					.getEntryStatus(depPresModel),
					(ResponsePresModel) depPresModel);
		}
	}

	private static void processDocStatus(OptionPresModel presModel,
			EntryStatus status, ResponsePresModel depPresModel) {
		
		if (depPresModel instanceof CompositePresModel) {
			
			int startIndex = 0;
			
			if(depPresModel instanceof FixedTableModel){
				//Bug#1422 - need to differentiate between a FixedTabelModel and a CompositePresModel.
				//If it's a fixed table model we do NOT want to do anything to the fixed label entry.
				startIndex = 1;
			}
			
			CompositePresModel cpm = (CompositePresModel) depPresModel;
			for (int i = startIndex; i < cpm.getEntry().numEntries(); i++) {
				BasicEntry entry = cpm.getEntry().getEntry(i);
				for (BasicPresModel basicPresModel : cpm
						.getPresModelsForEntry(entry)) {
					processDocStatus(presModel, status, basicPresModel);
				}
			}
			return;
		}
		if (!presModel.ignoreDocInstanceStatus()) {
			DocumentStatus docStatus = DocumentStatus.valueOf(presModel
					.getDocInstance().getStatus());
			if (docStatus == DocumentStatus.REJECTED
					|| docStatus == DocumentStatus.CONTROLLED) {
				ResponseStatus responseStatus;
				String message;
				if (status != EntryStatus.MANDATORY) {
					message = Messages
							.getString("RendererHelper.valueStatusDisabledMessage");
					responseStatus = ResponseStatus.FLAGGED_EDITED;
				} else {
					message = Messages
							.getString("RendererHelper.valueDisabledMessage");
					responseStatus = ResponseStatus.FLAGGED_INVALID;
				}
				depPresModel.getResponse().setAnnotation(message);
				depPresModel.getResponseStatusModel().setValue(responseStatus);
			}
		}
	}

	private static void updateAllDependentsStatus(RendererData rendererData,
			OptionPresModel presModel, Option oldValue, Option newValue) {
		updateDependentsStatus(rendererData, presModel, oldValue, true);
		updateDependentsStatus(rendererData, presModel, newValue, false);
	}

	private static void addOptionRendererValueChangeListener(
			final RendererData rendererData, final OptionPresModel presModel) {
		PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				Option oldValue = (Option) evt.getOldValue();
				Option newValue = (Option) evt.getNewValue();
				updateAllDependentsStatus(rendererData, presModel, oldValue,
						newValue);
			}
		};
		ValueModel valueModel = presModel.getValueModel();
		// Safe not to release listener
		valueModel.addValueChangeListener(listener);
	}

	public static void addAbstractRendererValueChangeListener(
			EntryWithButton entryField, TextPresModel presModel) {
		ValueModel displayTextModel = presModel.getDisplayTextModel();

		// Safe not to remove
		AbstractRendererValueChangeListener listener = new AbstractRendererValueChangeListener(
				entryField, presModel.getStandardCodeModel());
		displayTextModel.addValueChangeListener(listener);
	}

	public static void processResponseStatus(BasicPresModel presModel) {
		BasicResponse response = presModel.getResponse();
		switch (response.getStatus()) {
		case FLAGGED_EDITED:
			ValidationResult vResult = new ValidationResult();
			ValidationMessage message = new InformationMessage(response
					.getLatestValueComment());
			vResult.add(message);
			presModel.getValidationModel().setResult(vResult);
			break;
		case FLAGGED_INVALID:
			String annotation = response.getAnnotation();
			vResult = new ValidationResult();
			vResult.addWarning(annotation);
			presModel.getValidationModel().setResult(vResult);
			break;
		case NORMAL:
			break;
		}
	}

	public static BasicResponse processResponse(RendererData data, IValue value) {
		if (data.getModel() instanceof BasicEntry == false) {
			throw new IllegalArgumentException("data#getModel must return a " + //$NON-NLS-1$
					"IBasicEntry, but instead returns a:" + //$NON-NLS-1$
					data.getModel().getClass());
		}

		BasicEntry basicEntry = (BasicEntry) data.getModel();
		SectionPresModel secPresModel = data.getSectionOccPresModel();
		return processResponse(basicEntry, data.getModelParentResponse(), data
				.getDocOccurrenceInstance(), secPresModel, data.getRowIndex(),
				value);

	}

	public static BasicResponse processResponse(BasicEntry basicEntry,
                                                CompositeResponse parentResponse, DocumentInstance docInstance,
                                                SectionPresModel sectionPresModel, int rowIndex, IValue value) {

		CompositeRow compRow = null;
		if (parentResponse != null) {
			int numCompRows = parentResponse.numCompositeRows();
			if (numCompRows > rowIndex) {
				compRow = parentResponse.getCompositeRow(rowIndex);
			} else {
				compRow = parentResponse.createCompositeRow();
			}
		}

		BasicResponse response = (BasicResponse) generateInstance(basicEntry,
				sectionPresModel);

		setResponseValue(response, value);
		if (compRow != null) {
			compRow.addResponse(response);
			return response;
		}

		docInstance.addResponse(response);

		return response;
	}

	public static String getNoAnswerButtonInitialMessage() {
		return EntryMessages
				.getString("ToggleNoAnswerPopupAction.noAnswerMessage0"); //$NON-NLS-1$
	}

	public static String getNoAnswerButtonOtherMessage() {
		return EntryMessages
				.getString("ToggleNoAnswerPopupAction.noAnswerMessage1"); //$NON-NLS-1$
	}

	private static String concatCodeValueAndText(Integer codeValue,
			String textValue) {
		String codeValueString = null;
		if (codeValue != null) {
			codeValueString = codeValue.toString();
		}

		if (codeValueString != null) {
			StringBuilder sb = new StringBuilder(codeValueString.length()
					+ textValue.length() + 2);
			sb.append(codeValueString).append(". ").append(textValue); //$NON-NLS-1$
			return sb.toString();
		}
		return textValue;
	}

	public static String getStandardCodeText(StandardCode standardCode) {
		return concatCodeValueAndText(Integer.valueOf(standardCode.getCode()),
				standardCode.getDescription());
	}

	public static String getOptionText(OptionEntry optionEntry, Option option) {
		Integer code = null;
		if (optionEntry.isOptionCodesDisplayed()) {
			code = option.getCode();
		}
		return concatCodeValueAndText(code, option.getDisplayText());
	}

	public static JButton getNoAnswerButton() {
		String toolTipText = getNoAnswerButtonInitialMessage();
		JButton button = new JButton();
		button.setIcon(Icons.getInstance().getIcon("down")); //$NON-NLS-1$
		button.setToolTipText(toolTipText);
		button.setName("popupButton"); //$NON-NLS-1$
		return button;
	}

	static List<NoAnswerAction> getNoAnswerActions(
			List<StandardCode> stdCodes, ValueModel displayTextModel,
			ValueModel stdCodeModel, Action enableEditingAction) {
		List<NoAnswerAction> actions = new ArrayList<NoAnswerAction>(stdCodes
				.size());
		for (StandardCode stdCode : stdCodes) {
			NoAnswerAction action = new NoAnswerAction(stdCode,
					displayTextModel, stdCodeModel, enableEditingAction);
			actions.add(action);
		}
		return actions;
	}

	public static JPopupMenu getNoAnswerJPopupMenu(
			List<StandardCode> stdCodes, TextPresModel presModel,
			boolean disableStandardCodes) {
		ValueModel displayTextModel = presModel.getDisplayTextModel();
		ValueModel stdCodeModel = presModel.getStandardCodeModel();
		Action enableEditingAction = new EnableEditingAction(displayTextModel,
				stdCodeModel);
		List<NoAnswerAction> stdCodeActions = getNoAnswerActions(stdCodes,
				displayTextModel, stdCodeModel, enableEditingAction);

		JPopupMenu popup = createJPopupMenu();

		if (!disableStandardCodes) {
			for (NoAnswerAction action : stdCodeActions) {
				presModel.addAction(action);
				action.setEnabled(!disableStandardCodes);
				popup.add(action);
			}
			popup.addSeparator();
		}

		enableEditingAction.setEnabled(false);
		presModel.addAction(enableEditingAction);
		popup.add(enableEditingAction);
		return popup;
	}

	static JPopupMenu createJPopupMenu() {
		return new JPopupMenu(EntryMessages.getString("RendererHelper.select")); //$NON-NLS-1$
	}

	public static void processDescription(JComponent label, org.psygrid.data.model.hibernate.Component entry,
			JComponent field) {
		String description = entry.getDescription();
		ValidationComponentUtils.setInputHint(field, description);
	}

	private static void processDocumentStatus(Editable field, boolean copy,
			Status status, EditableStatus editable) {
		if (copy || editable == EditableStatus.TRUE) {
			return;
		}
		if (editable == EditableStatus.FALSE) {
			field.setEditable(false);
			return;
		}
		DocumentStatus docStatus = null;
		if (status == null) {
			docStatus = DocumentStatus.VIEW_ONLY;
		} else {
			docStatus = DocumentStatus.valueOf(status);
		}
		switch (docStatus) {
		case INCOMPLETE:
		case COMPLETE:
			break;
		case APPROVED:
		case PENDING:
		case VIEW_ONLY:
		case REJECTED:
		case CONTROLLED:
			field.setEditable(false);
			break;
		}
	}

	private static void processDocumentStatus(EntryComponent field,
			boolean copy, Status status, boolean transformed,
			EditableStatus editable) {
		if (copy) {
			return;
		}
		if (transformed) {
			field.setEditable(false);
			return;
		}
		processDocumentStatus(field, copy, status, editable);
	}

	public static MouseListener getChangeResponseStatusListener(
			PresModelRenderer<? extends ResponsePresModel> renderer,
			JComponent field, DocumentStatus docStatus) {
		/* Fix for Bug #1147 - Unable to properly mark composites as edited */
		if (renderer instanceof CompositeRenderer) {
			return null;
		}
		MouseListener togglePopupTrigger = new TogglePopupTrigger(
				createRightClickMenu(renderer, docStatus), field);
		return togglePopupTrigger;
	}

	public static JPopupMenu createRightClickMenu(
			PresModelRenderer<? extends ResponsePresModel> renderer,
			DocumentStatus docStatus) {
		JPopupMenu popupMenu = createJPopupMenu();
		Response response = renderer.getPresModel().getResponse();
		MarkResponseInvalidAction invalidAction = new MarkResponseInvalidAction(
				renderer);
		MarkResponseValidAction validAction = new MarkResponseValidAction(
				renderer);
		invalidAction.setValidAction(validAction);
		validAction.setInvalidAction(invalidAction);
		switch (docStatus) {
		case PENDING:
			switch (response.getStatus()) {
			case NORMAL:
				validAction.setEnabled(false);
				break;
			case FLAGGED_INVALID:
				invalidAction.setEnabled(false);
				break;
			}
			popupMenu.add(invalidAction);
			popupMenu.add(validAction);
			// allow fall-through
		default:
			popupMenu.add(new ViewProvenanceAction(renderer));
		}
		return popupMenu;
	}

	public static MouseListener getMouseListener(
			final RendererData rendererData, final BasicRenderer<?> renderer,
			final JComponent field,
			PostEditDialogBuiltListener postBuiltListener) {
		Status status = rendererData.getDocOccurrenceInstance().getStatus();
		if (status == null) {
			return null; // probably opening a VIEW_ONLY document as soon as
							// CoCoA is started
			/*
			 * throw new IllegalArgumentException(
			 * "documentInstance status cannot " + //$NON-NLS-1$ "be null");
			 * //$NON-NLS-1$
			 */
		}
		if (rendererData.isCopy()
				|| rendererData.isEditable() == EditableStatus.TRUE) {
			return null;
		}
		DocumentStatus docStatus = DocumentStatus.valueOf(status);
		switch (docStatus) {
		case REJECTED:
		case CONTROLLED:
			MouseListener editDialogListener = new EditDialogLauncher(
					rendererData, renderer, postBuiltListener, docStatus);
			return editDialogListener;
		case DATASET_DESIGNER:
			return null;
		default:
			return getChangeResponseStatusListener(renderer, field, docStatus);
		}
	}

	public static void addMouseListeners(final RendererData rendererData,
			JComponent label, final BasicRenderer<?> renderer,
			final JComponent field,
			PostEditDialogBuiltListener postBuiltListener) {

		MouseListener listener = getMouseListener(rendererData, renderer,
				field, postBuiltListener);

		if (listener == null) {
			return;
		}
		if (label != null) {
			label.addMouseListener(listener);
		}
		if (field != null) {
			field.addMouseListener(listener);
		}
	}

	/**
	 * Convenience method that calls <code>processEntryStatus</code>, <code>
	 * processValidation</code>
	 * and <code>processDescription</code> with the provided parameters.
	 * 
	 * @param label
	 * @param presModel
	 * @param entryComponent
	 *            The whole component used for entry (including items like the
	 *            standardCodes button).
	 * @param validationLabel
	 * @param textComponent
	 *            Only the component that is used for input.
	 */
	public static void processAll(JComponent label, BasicPresModel presModel,
			Editable entryComponent, JLabel validationLabel,
			JComponent textComponent, boolean copy, Status docStatus,
			EditableStatus editable) {

		processEntryStatus(label, presModel, entryComponent, copy, docStatus,
				editable);
		processValidation(presModel, validationLabel);
		processDescription(label, presModel.getEntry(), textComponent);
		processResponseStatus(presModel);
	}

	public static EntryStatus getEntryStatus(EntryPresModel presModel) {
		ValueModel secOccStatusValueModel = presModel.getSectionOccPresModel()
				.getEntryStatusValueModel();
		EntryStatus secOccStatus = (EntryStatus) secOccStatusValueModel
				.getValue();

		if (secOccStatus != null
				&& (secOccStatus == EntryStatus.DISABLED || secOccStatus == EntryStatus.OPTIONAL)) {
			return secOccStatus;
		}

		return (EntryStatus) presModel.getEntryStatusModel().getValue();
	}

	public static void processEntryStatus(final JComponent label,
			final EntryPresModel presModel, final Editable field,
			final boolean copy, final Status docStatus,
			final EditableStatus editable) {

		if (copy) {
			return;
		}

		EntryStatus status = getEntryStatus(presModel);
		updateEntryStatus(label, status, field, presModel, copy, docStatus,
				editable);

		// Safe not to release listener
		presModel.addEntryStatusListener(new EntryStatusListener() {
			public void statusChanged(EntryStatusEvent event) {
				EntryPresModel source = (EntryPresModel) event.getSource();
				EntryStatus newStatus = getEntryStatus(source);
				// if the entry status has changed and the status is now
				// DISABLED
				// then reset the presModel - has the effect of clearing the
				// entry and reverting it back to the state it was originally
				// in before the entry was enabled
				if (!newStatus.equals(event.getOldStatus())
						&& newStatus.equals(EntryStatus.DISABLED)) {
					presModel.reset();
				}
				
				if(!newStatus.equals(event.getOldStatus())
					&& newStatus.equals(EntryStatus.MANDATORY) || newStatus.equals(EntryStatus.OPTIONAL)){
					presModel.touch();
				}

				updateEntryStatus(label, newStatus, field, presModel, copy,
						docStatus, editable);
				source.performValidation(true);
			}
		});
	}

	private static void processDocumentStatus(EntryPresModel presModel,
			Editable field, boolean copy, Status docStatus,
			EditableStatus editable) {
		if (presModel instanceof BasicPresModel
				&& field instanceof EntryComponent) {
			EntryComponent entryComp = (EntryComponent) field;
			BasicPresModel bPresModel = (BasicPresModel) presModel;
			boolean transformed = ((Boolean) bPresModel.getTransformedModel()
					.getValue()).booleanValue();
			processDocumentStatus(entryComp, copy, docStatus, transformed,
					editable);
		} else {
			processDocumentStatus(field, copy, docStatus, editable);
		}
	}

	static void updateEntryStatus(JComponent label, EntryStatus status,
			Editable field, EntryPresModel presModel, boolean copy,
			Status docStatus, EditableStatus editable) {
		boolean isStandardCode = isStandardCode(presModel);
		switch (status) {
		case DISABLED:
			if (label != null) {
				label.setEnabled(false);
			}
			field.setEnabled(false, isStandardCode);
			field.setMandatory(false);
			break;
		case MANDATORY:
			if (label != null) {
				label.setEnabled(true);
			}
			field.setEnabled(true, isStandardCode);
			field.setMandatory(true);
			break;
		case OPTIONAL:
			if (label != null) {
				label.setEnabled(true);
			}
			field.setEnabled(true, isStandardCode);
			field.setMandatory(false);
		}
		processDocumentStatus(presModel, field, copy, docStatus, editable);
	}

	private static boolean isStandardCode(EntryPresModel presModel) {
		if (presModel instanceof StandardPresModel) {
			StandardPresModel spm = (StandardPresModel) presModel;
			if (null != spm.getStandardCodeModel().getValue()) {
				return true;
			}
		}
		return false;
	}

	public static List<Unit> getUnitsAsList(BasicEntry entry) {
		List<Unit> units = new ArrayList<Unit>(entry.numUnits());
		for (int i = 0, c = entry.numUnits(); i < c; ++i) {
			units.add(entry.getUnit(i));
		}
		return units;
	}

	static void setResponseValue(BasicResponse response, IValue value) {
		setResponseValue(response, value, null);
	}

	public static void setResponseValue(BasicResponse response, IValue value,
			String comment) {
		// Necessary because hibernate model objects need to cast to the
		// concrete
		// type
		if (value instanceof ValueAdapter) {
			response.setValue(((ValueAdapter) value).getValueDelegate(),
					comment);
		} else {
			response.setValue(value, comment);
		}
	}

	public static String getDoubleAsStringWithoutTrailingZero(Double d) {
		if (d == null) {
			return null;
		}
		String string = d.toString();
		if (string.endsWith(".0")) { //$NON-NLS-1$
			return string.substring(0, string.length() - 2);
		}
		return string;
	}

	public static DateFormat getDateFormat() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		sdf.setLenient(false);
		return sdf; //$NON-NLS-1$
	}

	public static String concatEntryLabelAndDisplayText(Entry entry) {
		String displayText = entry.getDisplayText();
		if (entry.getLabel() == null || entry.getLabel().equals("")) {
			return (displayText == null ? "" : displayText); //$NON-NLS-1$
		}
		int displayTextSize = displayText == null ? 0 : displayText.length();
		String label = entry.getLabel();
		StringBuilder text = new StringBuilder(displayTextSize + label.length()
				+ 2);
		text.append(label);
		text.append(") "); //$NON-NLS-1$
		if (displayText != null) {
			text.append(displayText);
		}
		return text.toString();
	}

	static BasicResponse getModelResponse(RendererData rendererData) {
		BasicEntry entry = (BasicEntry) rendererData.getModel();
		Response parentResponse = rendererData.getModelParentResponse();
		SectionPresModel sectionPresModel = rendererData
				.getSectionOccPresModel();
		DocumentInstance docInstance = rendererData.getDocOccurrenceInstance();
		return getModelResponse(entry, parentResponse, docInstance,
				sectionPresModel, rendererData.getRowIndex());
	}

	public static BasicResponse getModelResponse(BasicEntry entry,
                                                 Response parentResponse, DocumentInstance docInstance,
                                                 SectionPresModel sectionPresModel, int rowIndex) {

		if (parentResponse == null) {
			Response response = getResponse(docInstance, entry,
                    sectionPresModel);

			return (BasicResponse) response;
		} else if (parentResponse instanceof CompositeResponse) {
			CompositeResponse compResponse = (CompositeResponse) parentResponse;
			int numCompRows = compResponse.numCompositeRows();
			if (numCompRows > rowIndex) {
				return getBasicResponse(compResponse, entry, rowIndex);
			}
			return null;
		} else if (parentResponse instanceof BasicResponse) {
			throw new IllegalArgumentException("parentResponse cannot be of " + //$NON-NLS-1$
					"type IBasicResponse"); //$NON-NLS-1$
		}
		return null;
	}

	static BasicResponse getBasicResponse(CompositeResponse parentResponse,
                                          BasicEntry entry, int rowIndex) {
		int numCompRows = parentResponse.numCompositeRows();
		if (numCompRows > rowIndex) {
			CompositeRow compRow = parentResponse.getCompositeRow(rowIndex);
			BasicResponse response = compRow.getResponse(entry);
			return response;
		}
		return null;
	}

	public static IValue checkAndGetValue(BasicResponse response) {
		if (response == null) {
			throw new IllegalArgumentException("response cannot be null"); //$NON-NLS-1$
		}
		IValue value = response.getValue();
		if (value == null) {
			throw new IllegalStateException("An entry cannot have a response " + //$NON-NLS-1$
					"with a null value. Entry details follow:\n" + //$NON-NLS-1$
					response.getEntry().toString());
		}
		return value;
	}

	public static void processValidation(BasicPresModel presModel,
			JLabel validationLabel) {
		processValidation(presModel, validationLabel, presModel
				.getValidationPrefix());
	}

	public static void processValidation(ResponsePresModel presModel,
			JLabel validationLabel, String validationPrefix) {
		ValidationHandler validationHandler = new ValidationHandler(
				validationLabel, validationPrefix);

		ValidationResultModel validationModel = presModel.getValidationModel();

		String resultProperty = ValidationResultModel.PROPERTYNAME_RESULT;
		// Safe not to release
		validationModel.addPropertyChangeListener(resultProperty,
				validationHandler);
		ValidationResult vResult = validationModel.getResult();
		if (vResult.isEmpty()) {
			return;
		}
		// If the vResult is not empty, we want to cause a property change event
		// so that the validation handler will catch the value of the current
		// ValidationResult
		validationModel.setResult(ValidationResult.EMPTY);
		validationModel.setResult(vResult);
	}

	public static JPopupMenu getDatePopupMenu(List<StandardCode> stdCodes,
			DatePresModel datePresModel, DatePicker datePicker,
			boolean disableStandardCodes) {

		JPopupMenu popup = RendererHelper.getInstance().createJPopupMenu();

		DateEntry de = (DateEntry) datePresModel.getEntry();
		PartialDateModeAction enablePartial = new PartialDateModeAction(
				datePresModel, datePicker, de.isDisablePartialDate());
		datePresModel.addAction(enablePartial);
		popup.add(enablePartial);
		popup.addSeparator();

		Action enableEditingAction = new DateEnableEditingAction(datePresModel,
				enablePartial);

		List<Action> stdCodeActions = getDateNoAnswerActions(stdCodes,
				datePresModel, enableEditingAction, datePicker, enablePartial);

		for (Action action : stdCodeActions) {
			datePresModel.addAction(action);
			action.setEnabled(!disableStandardCodes);
			popup.add(action);
		}

		popup.addSeparator();
		enableEditingAction.setEnabled(false);
		datePresModel.addAction(enableEditingAction);
		popup.add(enableEditingAction);

		return popup;
	}

	private static List<Action> getDateNoAnswerActions(
			List<StandardCode> stdCodes, DatePresModel presModel,
			Action enableEditingAction, DatePicker datePicker,
			PartialDateModeAction enablePartial) {
		List<Action> actions = new ArrayList<Action>(stdCodes.size());

		for (StandardCode stdCode : stdCodes) {
			Action action = new DateNoAnswerAction(stdCode, presModel,
					enableEditingAction, datePicker, enablePartial);
			actions.add(action);
		}

		return actions;
	}

	public static JFrame findJFrame(Component comp) {
		if (comp == null) {
			return null;
		} else if (comp instanceof JFrame) {
			return (JFrame) comp;
		} else {
			return findJFrame(comp.getParent());
		}
	}

	public static InfiniteProgressPanel createInfiniteProgressPanel() {
		return new InfiniteProgressPanel("Please wait...");
		// InfiniteProgressPanel.DEFAULT_BARS_COUNT,
		// InfiniteProgressPanel.DEFAULT_SHIELD, DEF, 2000);
	}

	public static JFrame findJFrameFromRenderer(PresModelRenderer<?> renderer) {
		for (Component comp : renderer.getComponents()) {
			JFrame frame = findJFrame(comp);
			if (frame != null) {
				return frame;
			}
		}
		return null;
	}
}
