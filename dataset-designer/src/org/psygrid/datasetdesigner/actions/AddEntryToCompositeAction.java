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
package org.psygrid.datasetdesigner.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.ui.editdialogs.CompositeEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.CompositeFixedLabelDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.BooleanEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.DateEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.NumericEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.OptionEditDialog;
import org.psygrid.datasetdesigner.ui.editdialogs.TextEditDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Action to add a basic entry to a composite entry
 * @author pwhelan
 */
public class AddEntryToCompositeAction extends AbstractAction {

	//drop down selector for type of entry to add
	public JComboBox entryBox;

	//the composite parent
	private CompositeEntry parentEntry;

	//the list of listeners to listen for change events
	private Vector<ActionListener> entriesChangedListeners;

	private CompositeEditDialog parentDialog;

	//true if entry is being edited; false if not
	private boolean edit = false;

	//list of entries
	private JList list;

	//main frame of application
	private MainFrame frame;

	/**
	 * Constructor
	 * @param frame mainframe of teh application
	 * @param parentDialog the composite owner
	 * @param list the list of entries
	 * @param entryBox the type of entry drop-down
	 * @param parentEntry the owning parent entry
	 * @param edit true if entry is being edited; false if not
	 */
	public AddEntryToCompositeAction(MainFrame frame,
			CompositeEditDialog parentDialog,
			JList list,
			JComboBox entryBox,
			CompositeEntry parentEntry,
			boolean edit) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.edit"));
		this.entryBox = entryBox;
		this.list = list;
		this.parentEntry = parentEntry;
		this.parentDialog = parentDialog;
		this.edit = edit;
		this.frame = frame;
		entriesChangedListeners = new Vector();
	}

	/**
	 * Constructor
	 * @param frame mainframe of teh application
	 * @param parentDialog the composite owner
	 * @param list the list of entries
	 * @param entryBox the type of entry drop-down
	 * @param parentEntry the owning parent entry
	 */
	public AddEntryToCompositeAction(MainFrame frame,
			CompositeEditDialog parentDialog, 
			JComboBox entryBox, 
			CompositeEntry parentEntry,
			JList list) {
		super(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.add"));
		this.entryBox = entryBox;
		this.parentEntry = parentEntry;
		this.parentDialog = parentDialog;
		this.list = list;
		this.frame = frame;
		entriesChangedListeners = new Vector();
	}

	/**
	 * Action event handling
	 * @param e the calling action event
	 */
	public void actionPerformed(ActionEvent e) {
		String selectedItem = (String)entryBox.getSelectedItem();
		HibernateFactory factory = new HibernateFactory();

		/*
		 * Retrieve whether or not the DEL view is currently
		 * in use.
		 */
		boolean isDEL = parentDialog.isDEL();
		
		if (edit) {
			if (list.getSelectedValue() instanceof TextEntry) {
				if (((TextEntry)list.getSelectedValue()).getName().equals("Fixed Label")) {
					CompositeFixedLabelDialog editDialog = new CompositeFixedLabelDialog(frame, parentEntry);
					editDialog.addOKListener(new OKListener());
					editDialog.setVisible(true);
				} else {
					TextEditDialog editDialog = new TextEditDialog(frame, ((TextEntry)list.getSelectedValue()), parentEntry, isDEL, true);
					editDialog.addOKListener(new OKListener());
					editDialog.setVisible(true);
				}
			} else if (list.getSelectedValue() instanceof NumericEntry) {
				NumericEditDialog editDialog = new NumericEditDialog(frame, ((NumericEntry)list.getSelectedValue()), parentEntry, isDEL, true);
				editDialog.addOKListener(new OKListener());
				editDialog.setVisible(true);
			} else if (list.getSelectedValue() instanceof OptionEntry) {
				OptionEditDialog editDialog = new OptionEditDialog(frame, ((OptionEntry)list.getSelectedValue()), parentEntry, true, isDEL, true);
				editDialog.addOKListener(new OKListener());
				editDialog.setVisible(true);
			} else if (list.getSelectedValue() instanceof DateEntry) {
				DateEditDialog editDialog = new DateEditDialog(frame, ((DateEntry)list.getSelectedValue()), parentEntry, isDEL, true);
				editDialog.addOKListener(new OKListener());
				editDialog.setVisible(true);
			} else if (list.getSelectedValue() instanceof BooleanEntry) {
				BooleanEditDialog editDialog = new BooleanEditDialog(frame, ((BooleanEntry)list.getSelectedValue()), parentEntry, isDEL, true);
				editDialog.addOKListener(new OKListener());
				editDialog.setVisible(true);
			}

		} else {
			if (selectedItem.equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.textentry"))) {
				TextEditDialog editDialog = new TextEditDialog(frame, factory.createTextEntry(""), parentEntry, isDEL, true);
				editDialog.addOKListener(new OKListener());
				editDialog.setVisible(true);
			} else if (selectedItem.equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.numericentry"))) {
				NumericEditDialog editDialog = new NumericEditDialog(frame, factory.createNumericEntry(""), parentEntry, isDEL, true);
				editDialog.addOKListener(new OKListener());
				editDialog.setVisible(true);
			} else if (selectedItem.equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.optionentry"))) {
				OptionEditDialog editDialog = new OptionEditDialog(frame, factory.createOptionEntry(""), parentEntry, true, isDEL, true);
				editDialog.addOKListener(new OKListener());
				editDialog.setVisible(true);
			} else if (selectedItem.equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.dateentry"))) {
				DateEditDialog editDialog = new DateEditDialog(frame, factory.createDateEntry(""), parentEntry, isDEL, true);
				editDialog.addOKListener(new OKListener());
				editDialog.setVisible(true);
			} else if (selectedItem.equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.booleanentry"))) {
				BooleanEditDialog editDialog = new BooleanEditDialog(frame, factory.createBooleanEntry(""), parentEntry, isDEL, true);
				editDialog.addOKListener(new OKListener());
				editDialog.setVisible(true);
			} else if (selectedItem.equals("FixedLabels")) {
				if (!((DefaultListModel)list.getModel()).contains("Fixed Label")) {
					CompositeFixedLabelDialog editDialog = new CompositeFixedLabelDialog(frame, parentEntry);
					editDialog.addOKListener(new OKListener());
					editDialog.setVisible(true);
				}
			}
		}
	}

	/**
	 * Add a listener to the list of listeners
	 * @param listener the listener to add
	 */
	public void addActionListener(ActionListener listener) {
		entriesChangedListeners.add(listener);
	}

	/**
	 * Remove a listener to the list of listeners
	 * @param listener the listener to remove
	 */
	public void removeActionListener(ActionListener listener) {
		entriesChangedListeners.remove(listener);
	}

	/**
	 * Notify all listeners that an event has occurred.
	 */
	public void fireActionEvent() {
		Iterator atIt = entriesChangedListeners.iterator();
		while(atIt.hasNext()) {
			ActionListener al = (ActionListener)atIt.next();
			al.actionPerformed(new ActionEvent(this, 1, ""));
		}
	}

	/**
	 * An listener for 'ok' events 
	 * @author pwhelan
	 */
	private class OKListener implements ActionListener {

		/** 
		 * notify all listeners on the composite that an event has occurred
		 */
		public void actionPerformed(ActionEvent aet) {
			fireActionEvent();
		}
	}


}