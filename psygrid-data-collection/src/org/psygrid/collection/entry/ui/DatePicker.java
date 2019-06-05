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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.psygrid.collection.entry.event.SelectAllFocusListener;
import org.psygrid.collection.entry.model.MonthsComboBoxModel;
import org.psygrid.collection.entry.renderer.OptionComboBoxRenderer;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueModel;

public class DatePicker extends AbstractEntryWithButton {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private BasicDatePicker basicDatePicker;
    
    private EditableComboBox monthsComboBox;
    
    private JTextField yearTextField;
    
    private boolean partialDateMode;
    
    private boolean comboMode = false;

    private ValueModel displayTextModel;
    private ValueModel monthValueModel;
    private ValueModel yearValueModel;
    
    private EditableComboBox dayCalComboBox;
    private EditableComboBox monthCalComboBox;
    private EditableComboBox yearCalComboBox;

    public DatePicker(ValueModel displayTextModel, ValueModel monthValueModel,
            ValueModel yearValueModel) {
        super();
        this.displayTextModel = displayTextModel;
        this.monthValueModel = monthValueModel;
        this.yearValueModel = yearValueModel;
        initComponents();
        updatePartialModel();
        initEventHandling();
    }
    
    private void initEventHandling() {
        PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updatePartialModel();
            }
        };
        displayTextModel.addValueChangeListener(listener);
        monthValueModel.addValueChangeListener(listener);
        yearValueModel.addValueChangeListener(listener);
        
    }
    private void updatePartialModel() {
        if (monthValueModel.getValue() != null || 
                yearValueModel.getValue() != null) {
            setPartialDateMode(true);
        }
        else {
            setPartialDateMode(false);
        }
    }

    @Override
    public void addMouseListener(MouseListener listener) {
        // We only need to add the mouse listener to the popup because we set
        // its editor to be textComponent and that gets taken care for us
        // by EntryComponent
        basicDatePicker.getPopupButton().addMouseListener(listener);
        monthsComboBox.addMouseListener(listener);
        yearTextField.addMouseListener(listener);
        super.addMouseListener(listener);
    }
    
    @Override
    public void removeMouseListener(MouseListener listener) {
        basicDatePicker.removeMouseListener(listener);
        monthsComboBox.removeMouseListener(listener);
        yearTextField.removeMouseListener(listener);
        super.removeMouseListener(listener);
    }

    public void setFormat(DateFormat format) {
        basicDatePicker.setFormat(format);
    }

    private void initComponents() {
        basicDatePicker = new BasicDatePicker();
        basicDatePicker.getEditor().addFocusListener(new SelectAllFocusListener(
                basicDatePicker.getEditor()));
        monthsComboBox = createMonthsComboBox();
        yearTextField = BasicComponentFactory.createTextField(yearValueModel, 
                false);
        yearTextField.setColumns(4);
        yearTextField.setAlignmentX(Component.RIGHT_ALIGNMENT);
        yearTextField.setBorder(basicDatePicker.getTextFieldBorder());
        Bindings.bind(basicDatePicker.getEditor(), displayTextModel, false);
        add(basicDatePicker);
        add(getPopupButton());
    
        //added to provide drop-down options
        dayCalComboBox = new EditableComboBox(new JComboBox());
        dayCalComboBox.setEditable(true);
        monthCalComboBox = new EditableComboBox(new JComboBox());
        monthCalComboBox.setEditable(true);
        yearCalComboBox = new EditableComboBox(new JComboBox());
        yearCalComboBox.setEditable(true);
    }

    private EditableComboBox createMonthsComboBox() {
        JComboBox cBox = new JComboBox(new MonthsComboBoxModel(monthValueModel));
        cBox.setEditor(new OptionComboBoxRenderer());
        cBox.setEditable(true);
        return new EditableComboBox(cBox);
    }

    @Override
    public void doLayout() {
        Insets insets = getInsets();

        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;
        
        int popupButtonWidth = getPopupButtonWidth();
        
        if (partialDateMode) {
            Dimension cBoxSize = monthsComboBox.getPreferredSize();
            
            // One third of available space
            int oneThird = (width - popupButtonWidth) / 3;
            int yearFieldWidth = yearTextField.getPreferredSize().width;
            if ((cBoxSize.width <= (oneThird * 2)) &&  yearFieldWidth < oneThird) {
                cBoxSize.width = oneThird * 2;
            }
            monthsComboBox.setBounds(insets.left, insets.top, cBoxSize.width,
                    height);
            yearTextField.setBounds(insets.left + cBoxSize.width, insets.top,
                    width - popupButtonWidth - cBoxSize.width, height);
        }
        else if (comboMode){
//            Dimension cBoxSize = dayCalComboBox.getPreferredSize();
//            // One third of available space
//            int oneThird = (width - popupButtonWidth) / 3;
//            int yearFieldWidth = yearTextField.getPreferredSize().width;
//            if ((cBoxSize.width <= (oneThird * 2)) &&  yearFieldWidth < oneThird) {
//                cBoxSize.width = oneThird * 2;
//            }
//            monthsComboBox.setBounds(insets.left, insets.top, cBoxSize.width,
//                    height);
//            yearTextField.setBounds(insets.left + cBoxSize.width, insets.top,
//                    width - popupButtonWidth - cBoxSize.width, height);
        
        } else {
            basicDatePicker.setSize(width - popupButtonWidth, height);
            basicDatePicker.doLayout();
        }
        int popupButtonX = width - popupButtonWidth + insets.left;
        
        getPopupButton().setBounds(popupButtonX, insets.top, popupButtonWidth, 
                height);
    }
    
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        
        int popupButtonWidth = getPopupButtonWidth();
        Dimension dim;
        
        if (partialDateMode) {
            dim = monthsComboBox.getPreferredSize();
            Dimension yearFieldSize = yearTextField.getPreferredSize();
            dim.width += yearFieldSize.width;
            dim.height = Math.max(yearFieldSize.height, dim.height);
            
        }
        else {
            dim = basicDatePicker.getPreferredSize();
        }
        dim.width += popupButtonWidth;
        Insets insets = getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;
        return dim;
    }

    @Override
    public JTextComponent getTextComponent() {
        return basicDatePicker.getEditor();
    }
    
    public BasicDatePicker getBasicDatePicker() {
        return basicDatePicker;
    }
    
    @Override
    public void setEnabled(boolean b, boolean isStandardCode) {
        basicDatePicker.setEnabled(b && !isStandardCode);
        monthsComboBox.setEnabled(b && !isStandardCode);
        yearTextField.setEnabled(b && !isStandardCode);
        
        dayCalComboBox.setEnabled(b && !isStandardCode);
        monthCalComboBox.setEnabled(b && !isStandardCode);
        yearCalComboBox.setEnabled(b && !isStandardCode);
        
        super.setEnabled(b, isStandardCode);
    }
    
    public boolean isPartialDateMode() {
        return partialDateMode;
    }
    
    public void setPartialDateMode(boolean partialDateMode) {
        if (this.partialDateMode == partialDateMode) {
            return;
        }
        this.partialDateMode = partialDateMode;
        if (partialDateMode) {
            remove(basicDatePicker);
            add(monthsComboBox, 0);
            add(yearTextField, 1);
        }
        else {
            remove(yearTextField);
            remove(monthsComboBox);
            add(basicDatePicker, 0);
        }
    }
    
    public boolean isComboMode() {
    	return comboMode;
    }
    
    public void setComboMode(boolean comboMode) {
        if (this.comboMode == comboMode) {
            return;
        }
        this.comboMode = comboMode;
        if (partialDateMode) {
        	return;
        }
        else {
        	if (comboMode) {
        		remove(basicDatePicker);
        		add(dayCalComboBox, 0);
        		add(monthCalComboBox, 1);
        		add(yearCalComboBox, 2);
        	} else {
        		remove(dayCalComboBox);
        		remove(monthCalComboBox);
                remove(yearCalComboBox);
                add(basicDatePicker, 0);
        	}
        }
    }

    @Override
    public void setEditable(boolean b) {
        basicDatePicker.setEditable(b);
        monthsComboBox.setEditable(b);
        yearTextField.setEditable(b);
        
        dayCalComboBox.setEditable(b);
        monthCalComboBox.setEditable(b);
        yearCalComboBox.setEditable(b);
        
        super.setEditable(b);
    }

    public EditableComboBox getMonthsComboBox() {
        return monthsComboBox;
    }

    public JTextField getYearTextField() {
        return yearTextField;
    }
}
