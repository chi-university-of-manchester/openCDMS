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


package org.psygrid.collection.entry.ui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.Editable;
import org.psygrid.collection.entry.Icons;
import org.psygrid.collection.entry.event.ApplyStdCodeToRowEvent;
import org.psygrid.collection.entry.event.ApplyStdCodeToRowListener;
import org.psygrid.collection.entry.event.EntryTableModelEvent;
import org.psygrid.collection.entry.event.EntryTableModelListener;
import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.model.EntryTableModel;
import org.psygrid.collection.entry.renderer.BasicRenderer;
import org.psygrid.collection.entry.renderer.Renderer;
import org.psygrid.collection.entry.renderer.RendererHandler;
import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.collection.entry.renderer.RendererData.EditableStatus;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.EditAction;
import org.psygrid.data.model.hibernate.StandardCode;

import com.jgoodies.forms.layout.Sizes;

public class EntryTable extends AbstractEditable  {
	private static final Log LOG = LogFactory.getLog(EntryTable.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected List<Map<JComponent,Boolean>> components;
	private int minFieldWidth = -1;
	private List<JLabel> headerLabels;
	private int[] preferredColumnWidths;
	private int[] columnWidths;

	protected EntryTableModel model;
	protected final RendererHandler rendererHandler;
	private List<ValidationLabelsGroup> validationLabelsGroups;
	protected List<JButton> applyStdCodeToRowButtons;

	private JLabel validationLabel;

	public EntryTable(EntryTableModel model, RendererHandler rendererHandler, JLabel validationLabel) {
		this.model = model;
		this.rendererHandler = rendererHandler;
		this.validationLabel = validationLabel;
	}

	public void init() {
		initComponents();
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
		initEventHandling();
		model.init();
	}

	public EntryTableModel getModel() {
		return model;
	}

	private void initEventHandling() {
		// Safe not to release event
		model.addEntryTableModelListener(new EntryTableModelListener() {
			public void tableChanged(EntryTableModelEvent event) {
				if (event.getType() == EntryTableModelEvent.Type.INSERT) {
					insertRow(event.getRowIndex(), event.isEditable());
				}
				else if (event.getType() == EntryTableModelEvent.Type.DELETE) {
					deleteRow(event.getRowIndex());
				}
				revalidate();
				repaint();
			}
		});
		getModel().addApplyStdCodeToRowListener(new ApplyStdCodeToRowListener() {
			public void applyStdCode(ApplyStdCodeToRowEvent event) {
				handleApplyStdCodeToRowEvent(event);
			}
		});
	}

	protected void deleteRow(int rowIndex) {

		JButton applyStdCodeToRowButton = applyStdCodeToRowButtons.remove(rowIndex);
		remove(applyStdCodeToRowButton);

		Map<JComponent,Boolean> rowComponents = components.remove(rowIndex);

		for (JComponent comp : rowComponents.keySet()) {
			remove(comp);
		}

		CompositeEntry compEntry = model.getEntry();
		for (int i = 0, c = compEntry.numEntries(); i < c; ++i) {
			org.psygrid.data.model.hibernate.Entry entry  = compEntry.getEntry(i);
			rendererHandler.removeRenderer(entry, rowIndex);
		}

		remove(validationLabelsGroups.get(rowIndex));
		revalidate();
		repaint();
	}

	public List<JLabel> getHeaderLabels() {
		return Collections.unmodifiableList(headerLabels);
	}

	private JLabel getHeaderLabel(final String displayText, final String description) {
		JLabel headerLabel = new JLabel(displayText, SwingConstants.CENTER);

		//Set icon for entries where editing is not permitted (will override help icon)
		for (int i = 0; i < model.getEntry().numEntries(); i++) {
			org.psygrid.data.model.hibernate.Entry entry = model.getEntry().getEntry(i);
			if (displayText != null && displayText.equals(entry.getDisplayText())) {
				if ( EditAction.DENY.equals(entry.getEditingPermitted())) {
					headerLabel.setIcon(Icons.getInstance().getIcon("entry_locked"));	
					headerLabel.setToolTipText(Messages.getString("Entry.denied"));
				}
				else if (EditAction.READONLY.equals(entry.getEditingPermitted())){
					headerLabel.setIcon(Icons.getInstance().getIcon("entry_locked"));
					headerLabel.setToolTipText(Messages.getString("Entry.readonly"));
				}
				break;
			}
		}
		if ( EditAction.DENY.equals(model.getEntry().getEditingPermitted())) {
			headerLabel.setIcon(Icons.getInstance().getIcon("entry_locked"));	
			headerLabel.setToolTipText(Messages.getString("Entry.denied"));
		}
		else if (EditAction.READONLY.equals(model.getEntry().getEditingPermitted())){
			headerLabel.setIcon(Icons.getInstance().getIcon("entry_locked"));
			headerLabel.setToolTipText(Messages.getString("Entry.readonly"));
		}
		else if ( null != description && !(description.equals(""))){
			headerLabel.setIcon(Icons.getInstance().getIcon("help")); //$NON-NLS-1$
			headerLabel.setToolTipText(Messages.getString("EntryTable.toolTipText"));
			headerLabel.addMouseListener(
					new MouseAdapter(){
						@Override
						public void mouseClicked(MouseEvent e) {
							WrappedJOptionPane.showWrappedMessageDialog(
									RendererHelper.getInstance().findJFrame(EntryTable.this), 
									description,
									Messages.getString("EntryTable.helpMessageTitle"),
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
			);
		}

		headerLabel.setBackground(UIManager.getColor("TableHeader.background")); //$NON-NLS-1$
		headerLabel.setForeground(UIManager.getColor("TableHeader.foreground")); //$NON-NLS-1$
		headerLabel.setFont(UIManager.getFont("TableHeader.font")); //$NON-NLS-1$
		headerLabel.setOpaque(true);
		headerLabel.setBorder(BorderFactory.createCompoundBorder(LineBorder
				.createGrayLineBorder(), BorderFactory.createEmptyBorder(3,
						3, 3, 3)));
		return headerLabel;
	}

	private void createHeaders() {
		headerLabels = new ArrayList<JLabel>();
		for (Entry<String, String> e : model.getHeadings().entrySet() ) {
			JLabel headerLabel = getHeaderLabel(e.getKey(), e.getValue());
			add(headerLabel);
			headerLabels.add(headerLabel);
		}
	}

	protected void insertRow(int rowIndex, EditableStatus editable) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Inserting row: " + rowIndex); //$NON-NLS-1$
		}
		JButton applyStdCodeToRowButton = 
			new JButton(getModel().getApplyStdCodeToRowAction(rowIndex));
		applyStdCodeToRowButtons.add(rowIndex, applyStdCodeToRowButton);
		add(applyStdCodeToRowButton);

		Map<JComponent,Boolean> row = new LinkedHashMap<JComponent,Boolean>();
		ValidationLabelsGroup validationLabelsGroup = new ValidationLabelsGroup();
		List<BasicPresModel> childModels = new ArrayList<BasicPresModel>();
		for (int i = 0, c = model.numColumns(); i < c; ++i) {

			Renderer renderer = getRenderer(rowIndex, i, editable);

			BasicRenderer<?> basicRenderer = BasicRenderer.class.cast(renderer);
			childModels.add(basicRenderer.getPresModel());
			JComponent field = basicRenderer.getField();

			/*
			 * Check the individual entry status and record whether it 
			 * should be enabled.
			 */
			 org.psygrid.data.model.hibernate.Entry entry = basicRenderer.getPresModel().getEntry();
			 EditAction isEnabled = entry.getEditingPermitted();
			 boolean entryEnabled = true;
			 if (EditAction.DENY.equals(isEnabled)
					 || EditAction.READONLY.equals(isEnabled)) {
				 entryEnabled = false;	//Entry should not be enabled
			 }

			 if (LOG.isDebugEnabled()) {
				 BasicPresModel presModel = basicRenderer.getPresModel();
				 LOG.debug("-----------------" //$NON-NLS-1$
						 + "\nEntryPresentationModel for row, column: [" //$NON-NLS-1$
						 + rowIndex + ", " + i + "]" + "\nValue: " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						 + presModel.getValueModel().getValue() + "\nEntry: " //$NON-NLS-1$
						 + presModel.getEntry() + ", " //$NON-NLS-1$
						 + presModel.getEntry().getDisplayText()
						 + "\n-------------------"); //$NON-NLS-1$
			 }

			 if (field != null) {
				 row.put(field, entryEnabled);
				 add(field);
			 }

			 JLabel validationLabel = basicRenderer.getValidationLabel();
			 if (validationLabel != null) {
				 validationLabelsGroup.addLabel(validationLabel);
			 }

			 if (minFieldWidth == -1 && field instanceof EntryComponent) {
				 minFieldWidth = Sizes.dialogUnitXAsPixel(100, field);
			 }
		}
		add(validationLabelsGroup);
		validationLabelsGroups.add(rowIndex, validationLabelsGroup);

		components.add(rowIndex, row);
		model.addPresModelsRow(childModels);
	}

	protected Renderer getRenderer(int rowIndex, int columnIndex, 
			EditableStatus editable) {
		CompositeEntry compositeEntry = model.getEntry();
		BasicEntry basicEntry = compositeEntry.getEntry(columnIndex);

		String basicEntryText = 
			RendererHelper.getInstance().concatEntryLabelAndDisplayText(basicEntry);
		String validationPrefix = basicEntryText + ": ";  //$NON-NLS-1$

		return rendererHandler.getRenderer(basicEntry,
				model.getResponse(), rowIndex, validationPrefix, model.isCopy(),
				editable);
	}

	protected void populateComponents() {
		components = new ArrayList<Map<JComponent,Boolean>>();
		applyStdCodeToRowButtons = new ArrayList<JButton>();
	}

	protected void initComponents() {
		add(validationLabel);
		validationLabelsGroups = new ArrayList<ValidationLabelsGroup>();
		createHeaders();
		populateComponents();
	}

	private int getRowHeight(Collection<? extends JComponent> row) {
		int height = 0;
		Iterator it = row.iterator();
		for (int j = 0, d = row.size(); j < d; ++j) {
			JComponent comp = (JComponent)it.next();
			Dimension prefSize = comp.getPreferredSize();

			height = Math.max(height, prefSize.height);
		}

		return height;
	}

	protected int getRightMargin() {
		return 0;
	}

	protected JComponent getRightComponent(int row) {
		return null;
	}

	protected int getFarRightMargin() {
		return 0;
	}

	protected JComponent getFarRightComponent(int row) {
		return null;
	}

	@Override
	public void doLayout() {
		Insets insets = getInsets();

		int totalWidth = getWidth() - insets.left - insets.right;
		int totalHeight = getHeight() - insets.top - insets.bottom;

		computeColumnWidths(totalWidth);

		int y = insets.top;
		int height = getRowHeight(headerLabels);

		int x = insets.left;

		int vLabelsWidth = getValidationLabelsColumnWidth();
		validationLabel.setBounds(x, y, vLabelsWidth, height);
		x += vLabelsWidth;

		for (int i = 0, c = headerLabels.size(); i < c; ++i) {

			JLabel label = headerLabels.get(i);
			int width = columnWidths[i];

			label.setBounds(x, y, width, height);

			x += width;
		}

		if (y > totalHeight) {
			return;
		}

		y += height;

		for (int i = 0, c = components.size(); i < c; ++i) {
			x = insets.left;

			Map<JComponent,Boolean> comps = components.get(i);
			height = getRowHeight(comps.keySet());

			ValidationLabelsGroup validationLabel = validationLabelsGroups.get(i);
			validationLabel.setBounds(x, y, vLabelsWidth, height);
			x += vLabelsWidth;

			int j = 0;
			for (JComponent comp: comps.keySet()) {                
				int width = columnWidths[j];

				comp.setBounds(x, y, width, height);
				x += width;
				j++;
			}
			int rightMargin = getRightMargin();
			JComponent rightComponent = getRightComponent(i);
			if (rightComponent != null) {
				rightComponent.setBounds(x, y, rightMargin, height);
				x += rightMargin;
			}
			int farRightMargin = getFarRightMargin();
			JComponent farRightComponent = getFarRightComponent(i);
			if (farRightComponent != null) {
				farRightComponent.setBounds(x, y, farRightMargin, height);
			}

			y += height;
			if (y >= totalHeight) {
				break;
			}
		}

		JComponent bottomComponent = getBottomComponent();

		if (bottomComponent != null) {
			y += 5;
			int centre = totalWidth / 2;
			Dimension bottomCompSize = bottomComponent.getPreferredSize();
			int bottomCompX = centre - (bottomCompSize.width / 2);
			bottomComponent.setBounds(bottomCompX, y, bottomCompSize.width,
					bottomCompSize.height);
		}

	}

	protected JComponent getBottomComponent() {
		return null;
	}

	@Override
	public Dimension getPreferredSize() {
		computePreferredColumnWidths();
		int width = 0;
		for (int i = 0; i < preferredColumnWidths.length; ++i) {
			width += preferredColumnWidths[i];
		}

		width += getNonColumnWidth();

		int height = 0;

		height += getRowHeight(headerLabels);

		for (int i = 0, c = components.size(); i < c; ++i) {
			Map<JComponent,Boolean> row = components.get(i);
			height += getRowHeight(row.keySet());
		}

		Insets insets = getInsets();
		width += insets.left + insets.right;
		height += insets.top + insets.bottom;

		return new Dimension(width, height);
	}

	public ValidationLabelsGroup getValidationLabel(int rowIndex) {
		return validationLabelsGroups.get(rowIndex);
	}

	private int getValidationLabelsColumnWidth() {
		int width = 0;
		for (ValidationLabelsGroup labelsGroup : validationLabelsGroups) {
			width = Math.max(width, labelsGroup.getPreferredSize().width);
		}
		return width;
	}

	List<Map<JComponent,Boolean>> getComps() {
		return components;
	}

	private int[] getColumnWidths(Collection<? extends JComponent> row) {
		int[] widths = new int[row.size()];
		Iterator it = row.iterator();
		for (int i = 0, c = row.size(); i < c; ++i) {
			JComponent comp = (JComponent)it.next();
			Dimension prefSize = comp.getPreferredSize();

			if (comp instanceof EntryComponent) {
				widths[i] = Math.max(minFieldWidth, prefSize.width);
			}

			else {
				widths[i] = prefSize.width;
			}
		}

		return widths;
	}

	private int getNonColumnWidth() {
		int width = getValidationLabelsColumnWidth();
		width += getRightMargin();
		width += getFarRightMargin();
		return width;
	}

	protected void computeColumnWidths(int totalWidth) {
		int arrayLength = preferredColumnWidths.length;
		int[] maxColumnWidths = new int[arrayLength];

		int totalColumnsWidth = 0;

		for (int i = 0; i < arrayLength; ++i) {
			totalColumnsWidth += preferredColumnWidths[i];
		}

		totalWidth = totalWidth - getNonColumnWidth();
		int totalExtraWidth = totalWidth - totalColumnsWidth;
		int colExtraWidth;
		int lastColExtraWidth;
		if (totalExtraWidth <= 0) {
			colExtraWidth = 0;
			lastColExtraWidth = 0;
		}
		else {
			colExtraWidth = totalExtraWidth / maxColumnWidths.length;
			lastColExtraWidth = colExtraWidth
			+ (totalExtraWidth % maxColumnWidths.length);
		}
		for (int i = 0, c = arrayLength - 1; i < c; ++i) {
			maxColumnWidths[i] = preferredColumnWidths[i] + colExtraWidth;
		}

		maxColumnWidths[arrayLength - 1] = preferredColumnWidths[arrayLength - 1]
		                                                         + lastColExtraWidth;

		columnWidths = maxColumnWidths;
	}

	private void computePreferredColumnWidths() {
		int[] maxColumnWidths = getColumnWidths(headerLabels);

		for (int i = 0, c = components.size(); i < c; ++i) {
			Map<JComponent,Boolean> row = components.get(i);

			int[] tempColumnWidths = getColumnWidths(row.keySet());

			for (int j = 0; j < tempColumnWidths.length; ++j) {
				maxColumnWidths[j] = Math.max(maxColumnWidths[j],
						tempColumnWidths[j]);
			}
			preferredColumnWidths = maxColumnWidths;
		}
	}

	@Override
	public void setEnabled(boolean b, boolean isStandardCode) {
		if (isEnabled() == b) {
			return;
		}
		for (Action applyStdCodeToRowAction : getModel().getApplyStdCodeToRowActions()) {
			applyStdCodeToRowAction.setEnabled(b);
		}
		if (b) {
			getModel().processApplyStdCodeActionStatus();
		}

		for (Map<JComponent,Boolean> row : components) {
			for (JComponent comp : row.keySet()) {
				//The field should be enabled if it is
				//writable, assuming the table itself
				//is enabled.
				Boolean fieldEnabled = row.get(comp);
				if (!b) {
					fieldEnabled = false;
				}
				if (comp instanceof Editable) {
					((Editable) comp).setEnabled(fieldEnabled, isStandardCode);
				}
				else {
					comp.setEnabled(fieldEnabled);
				}
			}
		}

		for (ValidationLabelsGroup labelsGroup : validationLabelsGroups) {
			labelsGroup.setEnabled(b);
		}

		for (JLabel header : headerLabels) {
			header.setEnabled(b);
		}
		super.setEnabled(b);
	}

	@Override
	public void setMandatory(boolean b) {
		if (isEnabled() == b) {
			return;
		}
		for (Map<JComponent,Boolean> row : components) {
			for (JComponent comp : row.keySet()) {
				Boolean fieldEnabled = row.get(comp);
				if (!b) {
					fieldEnabled = false;
				}
				if (comp instanceof Editable && fieldEnabled) {
					((Editable) comp).setMandatory(b);
				}
			}
		}
		super.setMandatory(b);
	}

	public JButton getApplyStdCodeToRowButton(int row) {
		return applyStdCodeToRowButtons.get(row);
	}

	protected void handleApplyStdCodeToRowEvent(ApplyStdCodeToRowEvent event) {
		List<StandardCode> stdCodes = getModel().getStandardCodes();
		JFrame window = RendererHelper.getInstance().findJFrame(this);
		ChooseStdCodeDialog dialog = new ChooseStdCodeDialog(
				window,
				Messages.getString("EntryTable.applyStdCodeToRowMessage"),
				stdCodes);
		dialog.setVisible(true);
		StandardCode code = dialog.getStdCode();
		if (null != code) {
			getModel().applyStdCodeToRow(event.getRowIndex(), code);
		}
	}

	@Override
	public void setEditable(boolean b) {
		boolean value;
		if (b) {
			value = isEnabled();
		}
		else {
			value = false;
		}
		for (Action applyStdCodeToRowAction : getModel().getApplyStdCodeToRowActions()) {
			applyStdCodeToRowAction.setEnabled(value);
		}
		super.setEditable(b);
	}


}
