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
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;

import org.psygrid.collection.entry.event.SelectAllFocusListener;
import org.psygrid.collection.entry.model.UnitComboBoxModel;
import org.psygrid.collection.entry.renderer.OptionComboBoxRenderer;

import com.jgoodies.binding.value.ValueModel;
import org.psygrid.data.model.hibernate.Unit;

public abstract class AbstractBasicEntryField extends EntryComponent {
    
    private JTextComponent textComponent;

    private EditableComboBox unitsComboBox;

    private ValueModel textModel;

    private ValueModel unitModel;

    private List<Unit> units;
    
    private JComponent decoratedTextComponent;

    public AbstractBasicEntryField(ValueModel textModel, ValueModel unitModel,
            List<Unit> units) {
        super();
        if ((units != null && unitModel == null)
                || (units == null && unitModel != null)) {
            throw new IllegalArgumentException("If unitModel is not null, " //$NON-NLS-1$
                    + "units cannot be null and if units is not null, then" //$NON-NLS-1$
                    + "unitModel cannot be null"); //$NON-NLS-1$
        }
        this.textModel = textModel;
        this.unitModel = unitModel;
        this.units = units;
        initComponents();
    }
    
    @Override
    public void addMouseListener(MouseListener listener) {
        if (unitsComboBox != null) {
            unitsComboBox.addMouseListener(listener);
        }
        super.addMouseListener(listener);
    }
    
    @Override
    public void removeMouseListener(MouseListener listener) {
        unitsComboBox.removeMouseListener(listener);
        super.removeMouseListener(listener);
    }

    public AbstractBasicEntryField(ValueModel textModel) {
        this(textModel, null, null);
    }

    protected ValueModel getTextModel() {
        return textModel;
    }
    
    public void setTextModel(ValueModel textModel) {
        this.textModel = textModel;
    }
    
    public void setUnitModel(ValueModel unitModel) {
        this.unitModel = unitModel;
        if (unitsComboBox == null) {
            return;
        }
        UnitComboBoxModel cbModel = (UnitComboBoxModel) unitsComboBox.getModel();
        cbModel.setUnitModel(unitModel);
    }

    protected abstract JTextComponent createTextComponent();

    @Override
    public JTextComponent getTextComponent() {
        return textComponent;
    }

    private void initComponents() {
        textComponent = createTextComponent();
        textComponent.setName("textField"); //$NON-NLS-1$
        textComponent.addFocusListener(new SelectAllFocusListener(textComponent));
        decoratedTextComponent = decorateTextComponent(textComponent);
        add(decoratedTextComponent);
        if (units != null && units.size() != 0) {
            UnitComboBoxModel cbModel = new UnitComboBoxModel(unitModel, units);
            unitsComboBox = new EditableComboBox(new JComboBox(cbModel));
            unitsComboBox.setEditable(true);
            unitsComboBox.setEditor(new OptionComboBoxRenderer());
            add(unitsComboBox);
        }
        updateUIInternal();
    }
    
    public JComponent getDecoratedTextComponent() {
        return decoratedTextComponent;
    }

    /**
     * Provides subclasses with a way to decorate the textComponent if
     * required (for example a JScrollPane).
     * 
     * The default implementation simply returns <code>textComp</code>.
     * 
     * @param textComp
     */
    protected JComponent decorateTextComponent(JTextComponent textComp) {
        return textComp;
    }

    private void updateUIInternal() {
        Border border = UIManager.getBorder("JXDatePicker.border"); //$NON-NLS-1$
        if (border == null) {
            border = BorderFactory.createCompoundBorder(LineBorder
                    .createGrayLineBorder(), BorderFactory.createEmptyBorder(3,
                    3, 3, 3));
        }
        decoratedTextComponent.setBorder(border);
    }

    @Override
    public void updateUI() {
        updateUIInternal();
    }

    @Override
    public void doLayout() {
        Insets insets = getInsets();
        

        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;

        if (unitsComboBox == null) {
            decoratedTextComponent.setBounds(insets.left, insets.top, width, height);
        } else {
            Dimension cBoxSize = unitsComboBox.getPreferredSize();
            decoratedTextComponent.setBounds(insets.left, insets.top, width
                    - cBoxSize.width, height);
            unitsComboBox.setBounds(width - cBoxSize.width + insets.left,
                    insets.top, cBoxSize.width, height);
        }   
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension dim = decoratedTextComponent.getPreferredSize();
        if (unitsComboBox != null) {
                dim.width += unitsComboBox.getPreferredSize().width;
        }
        Insets insets = getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;
        return dim;
    }

    public void setTextBackground(Color color) {
        decoratedTextComponent.setBackground(color);
    }
    
    public EditableComboBox getUnitsComboBox() {
        return unitsComboBox;
    }
    
    @Override
    public void setEditable(boolean b) {
        if (unitsComboBox != null) {
            unitsComboBox.setEditable(b);
        }
        super.setEditable(b);
    }

    @Override
    public void setEnabled(boolean b, boolean isStandardCode) {
        if (unitsComboBox != null) {
            unitsComboBox.setEnabled(b, isStandardCode);
        }
        super.setEnabled(b, isStandardCode);
    }
}