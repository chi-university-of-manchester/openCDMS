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

import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.jgoodies.forms.layout.Sizes;
import com.jgoodies.validation.view.ValidationComponentUtils;

public class DualTextField extends AbstractEditable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // Workaround for bug in JTextField. When it calculates the preferred size
    // it doesn't provide enough space for the text. This padding seems to solve
    // the problem
    private static final int LEFT_FIELD_PADDING = 2;

    private JTextField leftField;

    private JTextField rightField;

    private boolean editable;
    
    private int fieldsMargin;

    private String separator;

    private JTextField separatorField;

    private boolean rightFieldActive;

    public DualTextField() {
        leftField = new JTextField();
        leftField.setHorizontalAlignment(SwingConstants.RIGHT);
        leftField.setBorder(null);
        rightField = new JTextField();
        rightField.setBorder(null);
        rightFieldActive = false;
        fieldsMargin = 3;
        add(leftField);
        add(rightField);
    }
    
    @Override
    public void addMouseListener(MouseListener listener) {
        rightField.addMouseListener(listener);
        leftField.addMouseListener(listener);
        separatorField.addMouseListener(listener);
        super.addMouseListener(listener);
    }
    
    @Override
    public void removeMouseListener(MouseListener listener) {
        rightField.removeMouseListener(listener);
        leftField.removeMouseListener(listener);
        separatorField.removeMouseListener(listener);
        super.removeMouseListener(listener);
    }

    public void setFieldsMargin(int newMargin) {
        this.fieldsMargin = newMargin;
    }

    public int getFieldsMargin() {
        return fieldsMargin;
    }

    public void setLeftFieldText(String text) {
        leftField.setText(text);
    }

    public void setRightFieldText(String text) {
        rightField.setText(text);
    }

    /**
     * This forces the activation of the right field even if it's empty and has
     * a preferred size of 0.
     * 
     * @param b
     */
    public void setRightFieldActive(boolean b) {
        rightFieldActive = b;
    }

    public boolean isRightFieldActive() {
        return rightFieldActive;
    }

    @Override
    public void doLayout() {
        Insets insets = getInsets();

        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;

        Dimension leftFieldSize = leftField.getPreferredSize();
        Dimension rightFieldSize = rightField.getPreferredSize();
        int leftFieldWidth = leftFieldSize.width;
        if (leftFieldWidth > 0) {
            leftFieldWidth += LEFT_FIELD_PADDING;
        }
        leftFieldWidth = Math.min(leftFieldWidth, width);

        int x = insets.left;
        leftField.setBounds(x, insets.top, leftFieldWidth, height);
        x += leftFieldWidth;
        int remainingWidth = width - leftFieldWidth;
        if ((leftFieldWidth > 0 && rightFieldSize.width > 0)
                || (leftFieldWidth > 0 && rightFieldActive)) {

            if (separatorField != null) {
                int sepWidth = Math.min(remainingWidth, separatorField
                        .getPreferredSize().width);
                separatorField.setBounds(x, insets.top, sepWidth, height);
                remainingWidth -= sepWidth;
                x += sepWidth;
            }

            int margin = fieldsMargin;
            x += margin;
            remainingWidth -= margin;
        }
        rightField.setBounds(x, insets.top, remainingWidth, height);
    }

    @Override
    public Dimension getPreferredSize() {
        Insets insets = getInsets();
        Dimension leftFieldSize = leftField.getPreferredSize();
        Dimension rightFieldSize = rightField.getPreferredSize();
        int width = leftFieldSize.width + rightFieldSize.width;

        if ((leftFieldSize.width > 0 && rightFieldSize.width > 0)
                || (leftFieldSize.width > 0 && rightFieldActive)) {
            width += fieldsMargin;

            if (separatorField != null) {
                width += separatorField.getPreferredSize().width;
            }

            // extra padding for textfield
            width += LEFT_FIELD_PADDING;
        }

        int height = Math.max(leftFieldSize.height, rightFieldSize.height);
        width = Math.max(Sizes.dialogUnitXAsPixel(50, leftField), width);
        width += insets.left + insets.right;
        height += insets.top + insets.bottom;
        return new Dimension(width, height);
    }

    public String getRightFieldText() {
        return rightField.getText();
    }

    public JTextField getRightField() {
        return rightField;
    }

    public JTextField getLeftField() {
        return leftField;
    }

    public String getLeftFieldText() {
        return leftField.getText();
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
        if (separatorField == null) {
            separatorField = new JTextField();
            separatorField.setBorder(null);
            separatorField.setEditable(false);
            add(separatorField);
        }
        separatorField.setText(separator);
    }
    
    @Override
    public void setEnabled(boolean b, boolean isStandardCode) {
        if (isEnabled() == (b && !isStandardCode) ) {
            return;
        }
        
        rightField.setEnabled(b && !isStandardCode);
        leftField.setEnabled(b && !isStandardCode);
        separatorField.setEnabled(b && !isStandardCode);
        super.setEnabled(b, isStandardCode);
    }
    
    @Override
    public void setEditable(boolean b) {
        if (editable == b) {
            return;
        }
        
        rightField.setEditable(b);
        leftField.setEditable(b);
        separatorField.setEditable(b);
        super.setEditable(b);
    }

	@Override
	public void setMandatory(boolean b) {
		if ( isMandatory() == b ){
			return;
		}
		if ( b ){
			ValidationComponentUtils.setMandatoryBackground(rightField);
			ValidationComponentUtils.setMandatoryBackground(leftField);
			ValidationComponentUtils.setMandatoryBackground(separatorField);
		}
		else{
			rightField.setBackground(DEFAULT_BACKGROUND);
			leftField.setBackground(DEFAULT_BACKGROUND);
			separatorField.setBackground(DEFAULT_BACKGROUND);
		}
		super.setMandatory(b);
	}

	@Override
	public void setBackground(Color bg) {
		rightField.setBackground(bg);
		leftField.setBackground(bg);
		separatorField.setBackground(bg);
		super.setBackground(bg);
	}
    
    
}
