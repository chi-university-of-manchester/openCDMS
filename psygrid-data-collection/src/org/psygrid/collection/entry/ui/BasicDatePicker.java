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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.SpinnerNumberModel;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener; 

import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.calendar.DateSpan;
import org.jdesktop.swingx.calendar.JXMonthView;
//import org.jdesktop.swingx.event.DateSelectionEvent;
//import org.jdesktop.swingx.event.DateSelectionListener;


public class BasicDatePicker extends JComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private JTextField textField;

    private JXDatePickerPopup popup;

    private JPanel linkPanel;

    private long linkDate;

    private MessageFormat linkFormat;

    private JButton popupButton;

    private int minPopupButtonWidth = 20;

    private JXMonthView monthView;

    private Handler handler;
    
    private DateFormat dateFormat;

    private String actionCommand = "selectionChanged"; //$NON-NLS-1$

    private boolean editable = true;

    private SpinnerChangeListener spinChangeListener;
    
    private MonthViewPickerChanged monthChangeListener;
    
    public BasicDatePicker() {
        this(System.currentTimeMillis());
    }
    
    @Override
    public void addMouseListener(MouseListener listener) {
        textField.addMouseListener(listener);
        popupButton.addMouseListener(listener);
        super.addMouseListener(listener);
    }
    
    @Override
    public void removeMouseListener(MouseListener listener) {
        textField.removeMouseListener(listener);
        popupButton.removeMouseListener(listener);
        super.removeMouseListener(listener);
    }
    
    @Override
    public void addFocusListener(FocusListener listener) {
        textField.addFocusListener(listener);
        popupButton.addFocusListener(listener);
        super.addFocusListener(listener);
    }
    
    @Override
    public void removeFocusListener(FocusListener listener) {
        textField.removeFocusListener(listener);
        popupButton.removeFocusListener(listener);
        super.removeFocusListener(listener);
    }

    public BasicDatePicker(long millis) {
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy"); //$NON-NLS-1$
        dateFormat.setLenient(false);
        monthView = new JXMonthView();
        monthView.setTraversable(true);
        
        textField = createEditor();
        textField.setName("dateField"); //$NON-NLS-1$
        textField.setBorder(null);

        handler = new Handler();
        popupButton = new JButton();
        popupButton.setName("popupButton"); //$NON-NLS-1$
        popupButton.setRolloverEnabled(false);
        
        // Safe not to release listener
        popupButton.addMouseListener(handler);
        
//      Safe not to release listener
        popupButton.addMouseMotionListener(handler);

        // this is a trick to get hold of the client prop which
        // prevents closing of the popup
        JComboBox box = new JComboBox();
        Object preventHide = box.getClientProperty("doNotCancelPopup"); //$NON-NLS-1$
        popupButton.putClientProperty("doNotCancelPopup", preventHide); //$NON-NLS-1$

        KeyStroke spaceKey = KeyStroke
                .getKeyStroke(KeyEvent.VK_SPACE, 0, false);

        InputMap inputMap = popupButton.getInputMap(JComponent.WHEN_FOCUSED);
        inputMap.put(spaceKey, "TOGGLE_POPUP"); //$NON-NLS-1$

        ActionMap actionMap = popupButton.getActionMap();
        actionMap.put("TOGGLE_POPUP", new TogglePopupAction()); //$NON-NLS-1$

        add(textField);
        add(popupButton);

        updateUI();

        linkDate = millis;
        linkPanel = new TodayPanel();
        
    }
    
    protected Border getTextFieldBorder() {
        Border border = UIManager.getBorder("JXDatePicker.border"); //$NON-NLS-1$
        if (border == null) {
            border = BorderFactory.createCompoundBorder(LineBorder
                    .createGrayLineBorder(), BorderFactory.createEmptyBorder(3,
                    3, 3, 3));
        }
        return border;
    }

    /**
     * Resets the UI property to a value from the current look and feel.
     */
    @Override
    public void updateUI() {

        String str = UIManager.getString("JXDatePicker.arrowDown.tooltip"); //$NON-NLS-1$
        if (str == null) {
            str = Messages.getString("BasicDatePicker.toolTipText"); //$NON-NLS-1$
        }
        popupButton.setToolTipText(str);

        Icon icon = UIManager.getIcon("JXDatePicker.arrowDown.image"); //$NON-NLS-1$
        if (icon == null) {
            icon = (Icon) UIManager.get("Tree.expandedIcon"); //$NON-NLS-1$
        }
        popupButton.setIcon(icon);

        textField.setBorder(getTextFieldBorder());

        String formatString = UIManager.getString("JXDatePicker.linkFormat"); //$NON-NLS-1$
        if (formatString == null) {
            formatString = Messages.getString("BasicDatePicker.todayText") + " {0,date, dd MMMM yyyy}";
        }
        linkFormat = new MessageFormat(formatString);
    }

    /**
     * Set the currently selected date.
     *
     * @param date date
     */
    public void setDate(Date date) {
        textField.setText(dateFormat.format(date));
    }

    /**
     * Returns the currently selected date.
     *
     * @return Date
     */
    public Date getDate() {
        try {
            return dateFormat.parse(textField.getText());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Return the <code>JXMonthView</code> used in the popup to
     * select dates from.
     *
     * @return the month view component
     */
    public JXMonthView getMonthView() {
        return monthView;
    }

    public void setMonthView(JXMonthView monthView) {
        this.monthView = monthView;
        popup = null;
    }

    /**
     * Set the date the link will use and the string defining a MessageFormat
     * to format the link.  If no valid date is in the editor when the popup
     * is displayed the popup will focus on the month the linkDate is in.  Calling
     * this method will replace the currently installed linkPanel and install
     * a new one with the requested date and format.
     *
     * @param linkDate Date in milliseconds
     * @param linkFormatString String used to format the link
     * @see java.text.MessageFormat
     */
    public void setLinkDate(long linkDate, String linkFormatString) {
        this.linkDate = linkDate;
        linkFormat = new MessageFormat(linkFormatString);
        setLinkPanel(new TodayPanel());
    }

    /**
     * Return the panel that is used at the bottom of the popup.  The default
     * implementation shows a link that displays the current month.
     *
     * @return The currently installed link panel
     */
    public JPanel getLinkPanel() {
        return linkPanel;
    }

    /**
     * Set the panel that will be used at the bottom of the popup.
     *
     * @param linkPanel The new panel to install in the popup
     */
    public void setLinkPanel(JPanel linkPanel) {
        // If the popup is null we haven't shown it yet.
        if (popup != null) {
            popup.remove(linkPanel);
            popup.add(linkPanel, BorderLayout.SOUTH);
        }
        this.linkPanel = linkPanel;
    }

    /**
     * Returns the text field used to edit the date selection.
     *
     * @return the text field
     */
    public JTextField getEditor() {
        return textField;
    }

    /**
     * Creates the editor used to edit the date selection.  Subclasses should
     * override this method if they want to substitute in their own editor.
     *
     * @return an instance of a JTextField
     */
    protected JTextField createEditor() {
        return new JTextField();
    }
    
    public JButton getPopupButton() {
        return popupButton;
    }

    /**
     * Enables or disables the date picker and all its subcomponents.
     *
     * @param value true to enable, false to disable
     */
    @Override
    public void setEnabled(boolean value) {
        if (isEnabled() == value) {
            return;
        }

        textField.setEnabled(value);
        popupButton.setEnabled(value);
        super.setEnabled(value);
    }
    
    public void setEditable(boolean value) {
        textField.setEditable(value);
        if (!value) {
            popupButton.setEnabled(false);
        }
        else {
            popupButton.setEnabled(isEnabled());
        }
        editable = value;
    }
    
    public boolean isEditable() {
        return editable;
    }

    /**
     * Returns the string currently used to identiy fired ActionEvents.
     *
     * @return String The string used for identifying ActionEvents.
     */
    public String getActionCommand() {
        return actionCommand;
    }

    /**
     * Sets the string used to identify fired ActionEvents.
     *
     * @param actionCommand The string used for identifying ActionEvents.
     */
    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

    /**
     * Adds an ActionListener.
     * <p>
     * The ActionListener will receive an ActionEvent when a selection has
     * been made.
     *
     * @param l The ActionListener that is to be notified
     */
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    /**
     * Removes an ActionListener.
     *
     * @param l The action listener to remove.
     */
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    /**
     * Fires an ActionEvent to all listeners.
     */
    protected void fireActionPerformed() {
        Object[] listeners = listenerList.getListenerList();
        ActionEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                if (e == null) {
                    e = new ActionEvent(BasicDatePicker.this,
                            ActionEvent.ACTION_PERFORMED, actionCommand);
                }
                ((ActionListener) listeners[i + 1]).actionPerformed(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doLayout() {
        int width = getWidth();
        int height = getHeight();

        Insets insets = getInsets();
        int popupButtonWidth = Math.max(popupButton.getPreferredSize().width,
                minPopupButtonWidth);
        textField.setBounds(insets.left, insets.bottom, width
                - popupButtonWidth, height);
        popupButton.setBounds(width - popupButtonWidth + insets.left,
                insets.bottom, popupButtonWidth, height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension dim = textField.getPreferredSize();
        dim.width += Math.max(popupButton.getPreferredSize().width,
                minPopupButtonWidth);
        Insets insets = getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;
        return dim;
    }

    /**
     * Action used to commit the current value in the JFormattedTextField.
     * This action is used by the keyboard bindings.
     */
    private class TogglePopupAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public TogglePopupAction() {
            super("TogglePopup"); //$NON-NLS-1$
        }

        public void actionPerformed(ActionEvent ev) {
            handler.toggleShowPopup();
        }
    }

    private class Handler implements MouseListener, MouseMotionListener {
        private boolean _forwardReleaseEvent = false;

        public void mouseClicked(MouseEvent ev) {
            // Do nothing
        }

        public void mousePressed(MouseEvent ev) {
        	
            if (!popupButton.isEnabled()) {
                return;
            }
            
            toggleShowPopup();
        }

        public void mouseReleased(MouseEvent ev) {
        	
            if (!popupButton.isEnabled()) {
                return;
            }

            // Retarget mouse event to the month view.
            if (_forwardReleaseEvent) {
                ev = SwingUtilities.convertMouseEvent(popupButton, ev,
                        monthView);
                monthView.dispatchEvent(ev);
                _forwardReleaseEvent = false;
            }
        }

        public void mouseEntered(MouseEvent ev) {
            // Do nothing
        }

        public void mouseExited(MouseEvent ev) {
            // Do nothing
        }

        public void mouseDragged(MouseEvent ev) {
            if (!isEnabled()) {
                return;
            }

            _forwardReleaseEvent = true;

            if (null == popup || !popup.isShowing()) {
                return;
            }

            // Retarget mouse event to the month view.
            ev = SwingUtilities.convertMouseEvent(popupButton, ev, monthView);
            monthView.dispatchEvent(ev);
        }

        public void mouseMoved(MouseEvent ev) {
            // Do nothing
        }

        public void toggleShowPopup() {
            if (popup == null) {
                popup = new JXDatePickerPopup();
            }
            if (!popup.isVisible()) {
                Date startDate = null;
                Date endDate = null;
                try {
                    startDate = dateFormat.parse(textField.getText());
                }
                catch(ParseException pe) {
                    startDate = new Date();
                }
                try {
                    endDate = dateFormat.parse(textField.getText());
                } catch (ParseException e) {
                    endDate = new Date();
                }
                DateSpan span = new DateSpan(startDate, endDate);
                monthView.setSelectedDateSpan(span);
                long ensureVisible = 0;
                try {
                    ensureVisible = dateFormat.parse(textField.getText()).getTime();
                } catch (ParseException e) {
                    ensureVisible = System.currentTimeMillis();
                }
                monthView.ensureDateVisible(ensureVisible);
                popup.show(BasicDatePicker.this, 0, BasicDatePicker.this
                                .getHeight());
            } else {
            	
                popup.setVisible(false);
            }
        }
    }

    /**
     * Popup component that shows a JXMonthView component along with controlling
     * buttons to allow traversal of the months.  Upon selection of a date the
     * popup will automatically hide itself and enter the selection into the
     * editable field of the JXDatePicker.
     */
    protected class JXDatePickerPopup extends JPopupMenu implements
            ActionListener {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public JXDatePickerPopup() {
            monthView.setActionCommand("MONTH_VIEW"); //$NON-NLS-1$
            
            // Safe not to release listener
            monthView.addActionListener(this);
            
            setLayout(new BorderLayout());
 
            //create a JSpinner to hold the year selector
            //this sits on top of the month viewer and is linked to it
            JSpinner yearSpinner = new JSpinner();
            JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)yearSpinner.getEditor();
            editor.getTextField().setEditable(false);
            
            spinChangeListener = new SpinnerChangeListener(this); 
            
            //gets the current year in the default time zone and locale
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            
            //set the model, start at 1860, end at current year, incrememt by 1
            SpinnerNumberModel spinNumModel = new SpinnerNumberModel(currentYear, 1900, currentYear+100, 1);
            //connect this to the month view and connect the month view to it - secret service spies
            yearSpinner.setModel(spinNumModel); 
            spinNumModel.addChangeListener(spinChangeListener);
            
            //configure the editor; don't show the separator in the years
            JSpinner.NumberEditor numEditor = new JSpinner.NumberEditor(yearSpinner, "#");
            
            //make it uneditable
            numEditor.getTextField().setEditable(false);
            yearSpinner.setEditor(numEditor);
            
            //month change listener
            monthChangeListener = new MonthViewPickerChanged(yearSpinner);
            monthView.addPropertyChangeListener("firstDisplayedDate", monthChangeListener);
            
            //create panel for the spinner, containig a label explaining select year
            JPanel yearPanel = new JPanel();
            yearPanel.setOpaque(true);
            yearPanel.setBackground(Color.LIGHT_GRAY);
            
            //center the components
            yearPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            JLabel yearLabel = new JLabel(Messages.getString("BasicDatePicker.selectYear"));
            yearLabel.setLabelFor(yearSpinner);
            yearPanel.add(yearLabel);
            yearPanel.add(yearSpinner);

            
            add(yearPanel, BorderLayout.NORTH);

            add(monthView, BorderLayout.CENTER);
            
            if (linkPanel != null) {
                add(linkPanel, BorderLayout.SOUTH);
            }
        }

        public void actionPerformed(ActionEvent ev) {
            String command = ev.getActionCommand();
            if ("MONTH_VIEW".equals(command)) { //$NON-NLS-1$
                DateSpan span = monthView.getSelectedDateSpan();
                textField.setText(dateFormat.format(span.getStartAsDate()));
                popup.setVisible(false);
                fireActionPerformed();
            }
        }
    }

    private final class TodayPanel extends JXPanel {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        TodayPanel() {
            super(new FlowLayout());
            setDrawGradient(true);
            setGradientPaint(new GradientPaint(0, 0, new Color(238, 238, 238),
                    0, 1, Color.WHITE));
            JXHyperlink todayLink = new JXHyperlink(new TodayAction());
            Color textColor = new Color(16, 66, 104);
            todayLink.setUnclickedColor(textColor);
            todayLink.setClickedColor(textColor);
            add(todayLink);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(new Color(187, 187, 187));
            g.drawLine(0, 0, getWidth(), 0);
            g.setColor(new Color(221, 221, 221));
            g.drawLine(0, 1, getWidth(), 1);
        }

        private final class TodayAction extends AbstractAction {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            TodayAction() {
                super(linkFormat.format(new Object[] { new Date(linkDate) }));
            }

            public void actionPerformed(ActionEvent ae) {
                DateSpan span = new DateSpan(linkDate, linkDate);
                monthView.ensureDateVisible(span.getStart());
            }
        }
    }

    public void setFormat(DateFormat format) {
        this.dateFormat = format;
    }
    
    public DateFormat getFormat() {
        return dateFormat;
    }
    
    /**
     * List to changes in the spinner
     * If spinner changes, update month viewer to display months for selected year
     * @author pwhelan
     */
    private class SpinnerChangeListener implements ChangeListener {
    	
    	//the popup; must ensure this is visible when spinner is clicked
    	private JPopupMenu popup;
    	
    	/**
    	 * Create the listener and initialise popup and month viewer 
    	 * @param popup popup that contains the spinner and month viewer
    	 * @param spinMonthView the month veiwer
    	 */
    	private SpinnerChangeListener(JPopupMenu popup) {
    		this.popup = popup;
    	}

    	/**
    	 * Change event has occurred in the year spinner
    	 * Update the month viewer to show months for newly selected year
    	 * Also force popup to stay on screen
    	 * @param e the change event on the spinner
    	 */
		public void stateChanged(ChangeEvent e) {

			//remove listener from month viewer temporarily to prevent looping
			if (monthChangeListener != null) {
				monthChangeListener.setEnabled(false);
			}
			
	    	//ensure popup stays visible; it is usually dismissed on mouse-click
			popup.setVisible(true);
			
			
			//set the calendar to the time in the monthViewer
			Calendar cal = Calendar.getInstance();

			//set date selected in the month viewer
			cal.setTimeInMillis(monthView.getFirstDisplayedDate());
			
            SpinnerNumberModel spin = (SpinnerNumberModel)e.getSource();

            //increment by the year selected in the spinner
            cal.set(Calendar.YEAR, (Integer)spin.getValue());
            
            //set the new time in the month viewer
            monthView.ensureDateVisible(cal.getTimeInMillis());
            
			//remove listener from month viewer temporarily to prevent looping
			if (monthChangeListener != null) {
				monthChangeListener.setEnabled(true);
			}
		}
    }
    
    /**
     * Month View Picker Changed
     * Propert Change Listener listens for changes of month to the month viewer 
     * When detected, it increments the year if a year change has occured
     * @author pwhelan
     */
    private class MonthViewPickerChanged implements PropertyChangeListener {

    	//the spinner that must be changed on a month change
    	private JSpinner spinner;
    	
    	private boolean enabled = true;
    	
    	/**
    	 * Create the Month View Picker Changed and set the spinner
    	 * @param spinner spinner to update on month change
    	 */
    	private MonthViewPickerChanged(JSpinner spinner) {
    		this.spinner = spinner;
    	}
    	
    	public void setEnabled(boolean enabled){
    		this.enabled = enabled;
    	}
    	
    	public boolean isEnabled() {
    		return enabled;
    	}
    	
    	/**
    	 * Property Change has happened
    	 * @param evt a property even has occured on the month viewer
    	 */
		public void propertyChange(PropertyChangeEvent evt) {
			
			if (evt.getPropertyName().equals("firstDisplayedDate")) {
				if (enabled) {
					if (spinChangeListener != null) {
						spinner.getModel().removeChangeListener(spinChangeListener);
					}
					
					//set the calendar to the time in the monthViewer
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(monthView.getFirstDisplayedDate());
					
					//check the year now used in the month viewer
					int newYear = (((Integer)(cal.get(Calendar.YEAR))).intValue());
					
					//if new year and spinner year are different, then update
					if (newYear != ((Integer)spinner.getValue()).intValue()) {
						spinner.getModel().setValue(cal.get(Calendar.YEAR));
					}
					
					if (spinChangeListener != null) {
						spinner.getModel().addChangeListener(spinChangeListener);
					}
				}
			}
		}
    }

    
}
