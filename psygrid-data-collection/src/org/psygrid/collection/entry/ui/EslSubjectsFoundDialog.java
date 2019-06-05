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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.util.RecordHelper;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.esl.model.ISubject;

import com.jgoodies.forms.factories.ButtonBarFactory;

/**
 * A dialog window that displays a list of subjects returned when
 * searching the ESL (from the EslSearchPanel or from the 
 * EslFullSearchSubjectDialog)
 * 
 * @author Lucy Bridges
 *
 */
public class EslSubjectsFoundDialog extends ApplicationDialog   {

	private static final long serialVersionUID = 1L;

	private JPanel resultsPanel;
	private List<ISubject> subjects;
	private JTable resultsTable;
	private JButton okButton;

	private ApplicationDialog parent;
	private Application application;
	
	private ISubject selectedSubject = null;
	private Record selectedRecord   = null;

	/**
	 * Creates an instance of the dialog box. The dialog box will list the
	 * subjects matching the criteria given to the ESL search
	 * 
	 * @param parent
	 * @param subjects
	 * @param dataset
	 */
	public EslSubjectsFoundDialog(Application application, ApplicationDialog parent, List<ISubject> subjects, DataSet dataset) {
		super(application, true);
		this.subjects = subjects;
		this.parent = parent;
		this.application = application;
		init(application, dataset);
	}

	private void init(Application parent, DataSet dataSet) {
		setTitle(EntryMessages.getString("EslSubjectsFoundDialog.matchingparticipants")); //$NON-NLS-1$
		// Safe not to remove
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		getContentPane().setLayout(new BorderLayout());

		resultsPanel = buildResults();
		getContentPane().add(resultsPanel, BorderLayout.CENTER);
		getContentPane().add(buildButtons(), BorderLayout.SOUTH);
		pack();
		Dimension size = getSize();
		if (size.width < 415) {
			setSize(415, size.height);   
		}
		setModal(true);
		setLocation(WindowUtils.getPointForCentering(this));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	}

	private JPanel buildResults() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

		resultsTable = new JTable();
		resultsTable.setModel(new ResultsTableModel(subjects));
		resultsTable.setPreferredScrollableViewportSize(new Dimension(600, 175));
		resultsTable.setRowSelectionAllowed(true);
		resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Safe not to release listener
		resultsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int row = resultsTable.rowAtPoint(e.getPoint());
				if (row < 0) {
					return;
				}
				if (e.getClickCount() == 1
						&& e.getButton() == MouseEvent.BUTTON1) {
					handleSubjectSelected(row);
				}
				else if (e.getClickCount() == 2
						&& e.getButton() == MouseEvent.BUTTON1) {
					handleOk();
				}
			}
		});		

		JScrollPane scrollPane = new JScrollPane(resultsTable);
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	private JPanel buildButtons() {
		okButton = new JButton(EntryMessages.getString("Entry.ok")); //$NON-NLS-1$
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleOk();
			}
		});
		JButton cancelButton = new JButton(EntryMessages.getString("EslSearch.searchagain")); //$NON-NLS-1$
		cancelButton.setEnabled(true);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		JPanel okButtonPanel = ButtonBarFactory.buildRightAlignedBar(okButton, cancelButton);
		okButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
		
		return okButtonPanel;
	}
	
	private void handleOk() {
		//Retrieve the record based on the study number of the selected subject
		String studyNumber = selectedSubject.getStudyNumber();
		try{
			selectedRecord = RecordHelper.constructRecord(studyNumber);
		}
		catch(InvalidIdentifierException ex){
			ExceptionsHelper.handleFatalException(ex);
		}
		catch(IOException ex){
			ExceptionsHelper.handleIOException(application, ex, false);
		}
		EslReviewSubjectDialog subjectDialog = new EslReviewSubjectDialog(application, selectedRecord, selectedSubject);
		subjectDialog.setVisible(true);
		selectedSubject = null;
		if (subjectDialog.isSubjectOK()) {
			//parent.dispose();
			dispose();
		}
		else if (subjectDialog.isSubjectEdit()) {
			parent.dispose();
			dispose();
		}
	}

	public Record getSelectedRecord() {
		return selectedRecord;
	}
	
	private void handleSubjectSelected(int rowIndex) {
		ResultsTableModel tableModel = (ResultsTableModel) resultsTable.getModel();
		selectedSubject = tableModel.getSubjectAtRow(rowIndex);
		okButton.setEnabled(true);
	}

	private class ResultsTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		
		private String[] columnNames = {
				EntryMessages.getString("EslSubjectsFoundDialog.firstname"), 
				EntryMessages.getString("EslSubjectsFoundDialog.lastname"), 
				EntryMessages.getString("EslSubjectsFoundDialog.participantidentifier"), 
				EntryMessages.getString("EslSubjectsFoundDialog.address")};
		
		private List<ISubject> results;

		public ResultsTableModel(List<ISubject> subjects) {
			results = subjects;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return results.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			switch (col) {
			case 0:
				return results.get(row).getFirstName();
			case 1:
				return results.get(row).getLastName();
			case 2:
				return results.get(row).getStudyNumber();
			case 3:
				if (results.get(row).getAddress() != null) {
					return results.get(row).getAddress().getAddress1();
				}
				return "";
			default:
				return "";
			}
		}

		public Class getColumnClass(int c) {
			return String.class;
		}

		public ISubject getSubjectAtRow(int row) {
			return results.get(row);
		}

		/*
		 * Don't need to implement this method unless your table's
		 * editable.
		 */
		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}

}
