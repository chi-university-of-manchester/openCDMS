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
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JComboBox.KeySelectionManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.psygrid.collection.entry.event.HasKeyListener;

import com.jgoodies.validation.view.ValidationComponentUtils;

public class EditableComboBox extends AbstractEditable {
    private class MyKeySelectionManager extends KeyAdapter 
            implements KeySelectionManager  {
        
        /* Time before the buffer is cleared */
        private static final long DELAY = 2000L;
        private Timer timer = new Timer();
        private TimerTask currentTask = null;
        private final StringBuilder textBuffer = new StringBuilder();
        
        private TimerTask createTimerTask() {
            return new TimerTask() {
                @Override
                public void run() {
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            textBuffer.setLength(0);
                        }
                    });
                }
            };
        }
        
        @Override
        public void keyTyped(KeyEvent event) {
            int index = selectionForKey(event.getKeyChar(), comboBox.getModel());
            if (index > -1) {
                comboBox.setSelectedIndex(index);
            }
        }
        
        public final int selectionForKey(char key, ComboBoxModel model) {
            if (key == '\u001b') {
                textBuffer.setLength(0);
                cancelTask();
                return -1;
            }
            if (key == '\b') {
                if (textBuffer.length() > 0) {
                    textBuffer.deleteCharAt(textBuffer.length() - 1);
                }
            }
            else if (Character.isLetterOrDigit(key)) {
                textBuffer.append(Character.toLowerCase(key));
            }
            else {
                return -1;
            }
            int index = -1;
            if (textBuffer.length() > 0) {
                for (int i = 0, c = model.getSize(); i < c; ++i) {
                    String itemText = model.getElementAt(i).toString()
                            .toLowerCase().trim();
                    if (itemText.startsWith(textBuffer.toString())) {
                        index = i;
                        break;
                    }
                }
            }
            cancelTask();
            currentTask = createTimerTask();
            timer.schedule(currentTask, DELAY);
            return index;
        }
        private void cancelTask() {
            if (currentTask != null) {
                currentTask.cancel();
            }
        }

    }

    //  Workaround for bug in JTextField. When it calculates the preferred size
    // it doesn't provide enough space for the text. This padding seems to solve
    // the problem
    private static final int TEXT_FIELD_PADDING = 3;
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JComboBox comboBox;
    private JTextField textField;

    private ComboBoxTextConverter textConverter;
    
    public EditableComboBox(JComboBox comboBox) {
        this.comboBox = comboBox;
        this.textField = new JTextField();
        textField.setEditable(false);
        initEventHandling();
        add(comboBox);
    }
    
    public void setTextConverter(ComboBoxTextConverter textConverter) {
        this.textConverter = textConverter;
    }
    
    public ComboBoxTextConverter getTextConverter() {
        return textConverter;
    }
    
    private void initEventHandling() {
        comboBox.getModel().addListDataListener(new ListDataListener() {
            public void contentsChanged(ListDataEvent e) {
                textField.setText(getComboBoxText());
            }

            public void intervalAdded(ListDataEvent e) {
                // Don't do anything
            }

            public void intervalRemoved(ListDataEvent e) {
                // Don't do anything
            }
        });
        
        /* 
         * For some reason if we set the KeySelectionManager, it never
         * gets called. As a workaround, we have to add ourselves as a listener 
         * to the combo box editor directly.
         */
//        comboBox.setKeySelectionManager(new MyKeySelectionManager());
        if (comboBox.getEditor() instanceof HasKeyListener) {
            ((HasKeyListener) comboBox.getEditor()).addKeyListener(new MyKeySelectionManager());
        }
    }

    public final JComponent getActiveComponent() {
        if (isEditable()) {
            return comboBox;
        }
        return textField;
    }
    
    @Override
    public void addMouseListener(MouseListener listener) {
        comboBox.addMouseListener(listener);
        for (int i=0; i<comboBox.getComponentCount(); i++) {
        	comboBox.getComponent(i).addMouseListener(listener);
        }
        textField.addMouseListener(listener);
        super.addMouseListener(listener);
    }
    
    @Override
    public void removeMouseListener(MouseListener listener) {
        comboBox.removeMouseListener(listener);
        for (int i=0; i<comboBox.getComponentCount(); i++) {
        	comboBox.getComponent(i).removeMouseListener(listener);
        }
        textField.removeMouseListener(listener);
        super.removeMouseListener(listener);
    }
    
    public ComboBoxModel getModel() {
        return comboBox.getModel();
    }
    
    public Object getSelectedItem() {
        return comboBox.getSelectedItem();
    }
    
    @Override
    public void doLayout() {
        Insets insets = getInsets();
        
        int width = getWidth() - insets.left - insets.right;
        int height = getHeight() - insets.top - insets.bottom;

        getActiveComponent().setBounds(insets.left, insets.top, width, height);
    }
    
    @Override
    public Dimension getPreferredSize() {
        
        Dimension dim;
        if (isEditable()) {
            dim = getActiveComponent().getPreferredSize();
        }
        else {
            dim = getActiveComponent().getPreferredSize();
            dim.width += TEXT_FIELD_PADDING;
        }
        Insets insets = getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;
        return dim;
    }
    
    @Override
    public void setEditable(boolean editable) {
        if (editable == isEditable()) {
            return;
        }
        remove(0);
        if (editable) {
            add(comboBox);
        }
        else {
            String cBoxText = getComboBoxText();
            textField.setText(cBoxText);
            add(textField);
        }
        revalidate();
        repaint();
        super.setEditable(editable); 
    }

    private String getComboBoxText() {
        String cBoxText = null;
        if (textConverter == null) {
            Object cBoxItem = comboBox.getSelectedItem();
            if (cBoxItem != null) {
                cBoxText = cBoxItem.toString();
            }
        }
        else {
            cBoxText = textConverter.getSelectedItemText(comboBox);
        }
        if (cBoxText == null) {
            cBoxText = ""; //$NON-NLS-1$
        }
        return cBoxText;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        comboBox.setEnabled(enabled);
        textField.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    @Override
    public void setEnabled(boolean enabled, boolean isStandardCode) {
		//Ignore whether a std code has been used, as the dropdown should be editable anyway (Bug #902)
    	boolean b = enabled;
        comboBox.setEnabled(b);
        textField.setEnabled(b);
        super.setEnabled(enabled, isStandardCode);
    }

    @Override
	public void setMandatory(boolean b) {
		if ( b ){
			comboBox.setBackground(ValidationComponentUtils.getMandatoryBackground());
			comboBox.getEditor().getEditorComponent().setBackground(ValidationComponentUtils.getMandatoryBackground());
			ValidationComponentUtils.setMandatoryBackground(textField);
		}
		else{
			comboBox.setBackground(DEFAULT_BACKGROUND);
			comboBox.getEditor().getEditorComponent().setBackground(DEFAULT_BACKGROUND);
			textField.setBackground(DEFAULT_BACKGROUND);
		}
		super.setMandatory(b);
	}

	public void setSelectedIndex(int selectedIndex) {
        comboBox.setSelectedIndex(selectedIndex);
    }

    public void setSelectedItem(Object selectedItem) {
        comboBox.setSelectedItem(selectedItem);
    }

    public ComboBoxEditor getEditor() {
        return comboBox.getEditor();
    }

    public void configureEditor(ComboBoxEditor editor, Object item) {
        comboBox.configureEditor(editor, item);        
    }
    
    public void setEditor(ComboBoxEditor editor) {
        comboBox.setEditor(editor);
    }
}
