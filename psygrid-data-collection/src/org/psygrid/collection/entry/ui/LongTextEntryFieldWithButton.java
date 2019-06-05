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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueModel;

/**
 * Class to be used for ExternalDerivedEntries
 * 
 * As it is initially expected to be an uneditable
 * long text entry field it has not implemented the
 * unit model.
 * 
 * @author Lucy Bridges
 *
 */
public class LongTextEntryFieldWithButton extends AbstractEntryWithButton {

	private static final long serialVersionUID = 1L;

	private JTextComponent textComponent;
	private EditableComboBox unitsComboBox;

	private ValueModel textModel;
	private ValueModel stdCodeModel;

	private final static int BUTTON_WIDTH = 20;


	public LongTextEntryFieldWithButton(ValueModel displayTextModel) {
		super();

		this.textModel = displayTextModel;
		
		initComponents();

		JTextArea textComp = (JTextArea) getTextComponent();
		textComp.setLineWrap(true);
		textComp.setWrapStyleWord(true);

		initEventHandling();

	}

	public LongTextEntryFieldWithButton(ValueModel displayTextModel, ValueModel stdCodeModel) {
		this(displayTextModel);
		this.stdCodeModel = stdCodeModel;
	}

	@Override
	public JTextComponent getTextComponent() {
		return textComponent;
	}

	private void initComponents() {
		
		textComponent = createTextComponent();
		textComponent.setName("textField"); //$NON-NLS-1$
		//textComponent.addFocusListener(new SelectAllFocusListener(textComponent));
		Dimension buttonDim = getPopupButton().getPreferredSize();
		buttonDim.setSize(buttonDim.getWidth()+60, buttonDim.getHeight());
		getPopupButton().setSize(buttonDim);
		getPopupButton().setText("Calculate");
		
		//getPopupButton().setRolloverEnabled(false);
		add(getPopupButton());
		add(textComponent);

		updateUIInternal();
	}

	private void initEventHandling() {

	}
	
	@Override
	public void updateUI() {
		//super.updateUI();
		updateUIInternal();
	}

	@Override
	public void doLayout() {
		super.doLayout();
		Insets insets = getInsets();

		int popupButtonWidth = getPopupButton().getWidth();

		//Increase the width of the container by the button width, so that there's room to add it after the text field
		Point p = this.getLocation();
		this.setBounds(p.x, p.y, this.getWidth()+popupButtonWidth, this.getHeight());
		
		int width = getWidth() - insets.left - insets.right - popupButtonWidth;
		int height = getHeight() - insets.top - insets.bottom;

		if (unitsComboBox == null) {
			textComponent.setBounds(insets.left, insets.top, width, height);
		} else {
			Dimension cBoxSize = unitsComboBox.getPreferredSize();
			textComponent.setBounds(insets.left, insets.top, width
					- cBoxSize.width, height);
			unitsComboBox.setBounds(width - cBoxSize.width + insets.left,
					insets.top, cBoxSize.width, height);
		}  
		
		int popupButtonX = width + insets.left;
		int popupButtonHeight = BUTTON_WIDTH;
		getPopupButton().setBounds(popupButtonX, insets.top, popupButtonWidth, 
				popupButtonHeight);
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dim = textComponent.getPreferredSize();
		if (unitsComboBox != null) {
			dim.width += unitsComboBox.getPreferredSize().width;
		}
		Insets insets = getInsets();
		dim.width += insets.left + insets.right;
		dim.height += insets.top + insets.bottom;
		return dim;
	}

	public void setTextBackground(Color color) {
		textComponent.setBackground(color);
	}

	private void updateUIInternal() {

 		Border border = UIManager.getBorder("JXDatePicker.border"); //$NON-NLS-1$
		if (border == null) {
			border = BorderFactory.createCompoundBorder(LineBorder
					.createGrayLineBorder(), BorderFactory.createEmptyBorder(3,
							3, 3, 3));
		}
		getTextComponent().setBorder(border);
		
		getPopupButton().setToolTipText(getPopupButton().getText());
		getPopupButton().setIcon(null);
	}

	protected JTextComponent createTextComponent() {
		return BasicComponentFactory.createTextArea(getTextModel(), false);
	}

	public void setTextModel(ValueModel textModel) {
		this.textModel = textModel;
		Bindings.bind((JTextArea) getTextComponent(), textModel, false);

		add(getPopupButton());
	}

	protected ValueModel getTextModel() {
		return textModel;
	}

	public void setColumns(int columns) {
		((JTextArea) getTextComponent()).setColumns(columns);
	}

	public void setRows(int rows) {
		((JTextArea) getTextComponent()).setRows(rows);
	}

	public int getColumns() {
		return ((JTextArea) getTextComponent()).getColumns();
	}

	public int getRows() {
		return ((JTextArea) getTextComponent()).getRows();
	}

	/**
     * Enables or disables the text field and its button.
     *
     * @param value true to enable, false to disable
     */
    @Override
    public void setEnabled(boolean value) {
        if (isEnabled() == value) {
            return;
        }

        getTextComponent().setEnabled(value);
        getPopupButton().setEnabled(value);
        super.setEnabled(value);
    }
    
    @Override
    public void setEditable(boolean editable) {
    	getTextComponent().setEditable(editable);
        //Button shouldn't change
    }

    public ValueModel getStandardCodeModel() {
        return stdCodeModel;
    }
    public void setStandardCodeModel(ValueModel standardCodeModel) {
        this.stdCodeModel = standardCodeModel;
    }
}
