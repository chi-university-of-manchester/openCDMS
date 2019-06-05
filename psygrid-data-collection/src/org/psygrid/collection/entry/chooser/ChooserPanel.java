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


package org.psygrid.collection.entry.chooser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXTable;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.Icons;
import org.psygrid.collection.entry.SwingWorkerExecutor;
import org.psygrid.collection.entry.ui.EslSearchPanel;
import org.psygrid.collection.entry.ui.ProgressPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.psygrid.data.model.hibernate.DataSet;

public abstract class ChooserPanel extends ProgressPanel    {

	private static final Log LOG = LogFactory.getLog(ChooserPanel.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JXTable table;

	protected ChooserModel model;

	private JToolBar toolBar;

	private JScrollPane tableScrollPane;

	private Action upAction;

	private Action backAction;

	private Action forwardAction;

	private JTextField locationField;

	private JPanel informationPanel;

	private JTextArea descriptionArea;

	private JPanel eslSearchPanel;

	protected Application application;
	protected ChooserDialog dialog;

	protected DataSet dataset = null;

	public ChooserPanel(Application application, ChooserDialog dialog) {
		this.application = application;
		this.dialog = dialog;
	}

	public void addSelectionListener(ChooserSelectionListener listener) {
		listenerList.add(ChooserSelectionListener.class, listener);
	}

	public void removeSelectionListener(ChooserSelectionListener listener) {
		listenerList.remove(ChooserSelectionListener.class, listener);
	}

	protected boolean fireSelected(ChooserSelectionEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChooserSelectionListener.class)
				if (!((ChooserSelectionListener) listeners[i + 1]).selected(event))
					return false;
		}
		return true;
	}

	protected void processSelectionAction(int row) {
		if (row < 0) {
			return;
		}
		Choosable selectedValue = model.getCurrentTableModel().getValueAtRow(
				row);
		if (fireSelected(new ChooserSelectionEvent(this, selectedValue)))
			processSelectionAction(row, selectedValue);
	}

	protected abstract void processSelectionAction(int row,
			Choosable selectedValue);

	protected void loadChoosable(final int row) {
		SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() throws ChoosableException {
				fireJobStartedEvent();
				model.loadChoosable(row);
				return null;
			}

			@Override
			protected void done() {
				try {
					synchronized (model) {
						get();
					}
				} catch (InterruptedException e) {
					if (LOG.isWarnEnabled()) {
						LOG.warn(e.getMessage(), e);
					}
				} catch (ExecutionException e) {
					Throwable cause = e.getCause();
					if (cause instanceof ChoosableException) {
						String title = Messages.getString("ChooserPanel.loadingDocErrorTitle");
						String message = Messages.getString("ChooserPanel.loadingDocErrorMessage");
						LOG.error(message, cause);
						JOptionPane.showMessageDialog(getParent(), message,
								title, JOptionPane.ERROR_MESSAGE);
						return;
					}
					ExceptionsHelper.handleFatalException(cause);
				} finally {
					fireJobFinishedEvent();
				}
			}
		};
		SwingWorkerExecutor.getInstance().execute(worker);
	}

	private void initTable() {
		table.setRowSelectionAllowed(true);
		table.setShowGrid(false);
		table.setColumnMargin(0);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		List<?> columns = table.getColumns();
		for (int i = 0, c = columns.size(); i < c; ++i) {
			TableColumn column = (TableColumn) columns.get(i);
			column.setCellRenderer(getTableCellRenderer());
		}
	}

	protected abstract TableCellRenderer getTableCellRenderer();

	protected void resetTableModel() {
		table.setModel(model.getCurrentTableModel());
		initTable();
		table.revalidate();
		table.repaint();
	}

	private void setParentTable() {
		SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() throws ChoosableException {
				model.setParentTableModel();
				return null;
			}

			@Override
			protected void done() {
				try {
					synchronized (model) {
						get();
					}
				} catch (InterruptedException e) {
					ExceptionsHelper.handleInterruptedException(e);
				} catch (ExecutionException e) {
					Throwable cause = e.getCause();
					if (cause instanceof ChoosableException) {
						String title = Messages.getString("ChooserPanel.loadingParentErrorTitle");
						String message = Messages.getString("ChooserPanel.loadingParentErrorMessage");
						JOptionPane.showMessageDialog(getParent(), message,
								title, JOptionPane.ERROR_MESSAGE);
					} else {
						ExceptionsHelper.handleFatalException(cause);
					}
				}
			}
		};
		SwingWorkerExecutor.getInstance().execute(worker);
	}

	private JPanel createToolBarPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(toolBar, BorderLayout.NORTH);
		panel.add(createStatusSelectionPanel(), BorderLayout.CENTER);
		return panel;
	}

	private void createToolBar() {
		JPanel locationPanel = new JPanel(new BorderLayout());
		locationField = new JTextField();
		locationField.setEditable(false);
		locationPanel.add(locationField);
		locationPanel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		resetLocationField();
		toolBar = new JToolBar();
		toolBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		toolBar.setFloatable(false);
		upAction = new AbstractAction(
				Messages.getString("ChooserPanel.up"), Icons.getInstance().getIcon("up")) { //$NON-NLS-1$//$NON-NLS-2$

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				setParentTable();
				resetInformationField();
				resetStatusSelectionPanel();
			}
		};
		upAction.setEnabled(false);

		backAction = new AbstractAction(
				Messages.getString("ChooserPanel.back"), //$NON-NLS-1$
				Icons.getInstance().getIcon("back")) { //$NON-NLS-1$

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				model.setPreviousTableModel();
				resetInformationField();
				resetStatusSelectionPanel();
			}
		};
		backAction.setEnabled(false);

		forwardAction = new AbstractAction(Messages
				.getString("ChooserPanel.forward"), //$NON-NLS-1$
				Icons.getInstance().getIcon("forward")) { //$NON-NLS-1$

			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				model.setNextTableModel();
				resetInformationField();
				resetStatusSelectionPanel();
			}
		};
		forwardAction.setEnabled(false);

		toolBar.add(backAction);
		toolBar.add(forwardAction);
		toolBar.add(upAction);
		toolBar.add(locationPanel);
	}

	private void resetActions() {
		if (model.hasNext()) {
			forwardAction.setEnabled(true);
		}

		else {
			forwardAction.setEnabled(false);
		}

		if (model.hasPrevious()) {
			backAction.setEnabled(true);
		}

		else {
			backAction.setEnabled(false);
		}

		if (model.hasParent()) {
			upAction.setEnabled(true);
		}

		else {
			upAction.setEnabled(false);
		}
	}

	private void resetLocationField() {
		List<String> locations = new ArrayList<String>();
		ChooserTableModel tm = model.getCurrentTableModel();
		Choosable choosable = tm.getParent();
		while (choosable != null) {
			locations.add(choosable.getDisplayText());
			choosable = choosable.getParent();
		}

		StringBuilder sb = new StringBuilder(50);

		for (int i = locations.size() - 1; i >= 0; --i) {
			String location = locations.get(i);
			if (location != null) {
				sb.append(location);
			}
			sb.append("/"); //$NON-NLS-1$
		}

		locationField.setText(sb.toString());
	}

	private void resetInformationField() {
		Choosable p = model.getCurrentTableModel().parent;
		if (p instanceof ChoosableDataSet
				&& model.getCurrentTableModel().choosables != null
				&& model.getCurrentTableModel().choosables.size() > 0
				&& model.getCurrentTableModel().choosables.get(0) instanceof AbstractChoosableRecord) {
			//We must be looking at identifiers, so update the selected dataset
			dataset = ((ChoosableDataSet)p).getDataSet();	
		}
		else {
			dataset = null;
		}
		this.remove(informationPanel);
		createInformationPanel();
		add(informationPanel, BorderLayout.SOUTH);
		this.revalidate();
		this.repaint();
		this.updateUI();
	}

	protected void setModel(ChooserModel model) {
		this.model = model;
	}

	public ChooserModel getModel() {
		return model;
	}

	public void init(final ChoosableList choosableList) {
		init(choosableList, null);
	}

	public void init(final ChoosableList choosableList, final ChooserModel chooserModel) {
		setLayout(new BorderLayout());

		SwingWorker<ChooserModel, Object> createChooserModelWorker = 
			new SwingWorker<ChooserModel, Object>() {
			@Override
			protected ChooserModel doInBackground() throws ChoosableException   {
				if (chooserModel == null) {
					return new ChooserModel(choosableList);
				}
				return chooserModel;
			}

			@Override
			protected void done() {
				try {
					ChooserModel newModel = get();
					synchronized (newModel) {
						setModel(newModel);
					}
					// This listener does not need to be removed since the
					// lifetime of the model is the same as of this object
					model.addChoosableLoadedListener(new ChooserModelListener() {
						public void choosableLoaded(ChooserModelEvent event) {
							resetTableModel();
							resetActions();
							resetLocationField();
							resetInformationField();
						}
					});


					setPreferredSize(new Dimension(649, 490));

					createToolBar();
					table = new JXTable(model.getCurrentTableModel());
					table.setSortable(false);
					initTable();
					addListeners();
					createInformationPanel();
					tableScrollPane = new JScrollPane(table);
					Border currentBorder = tableScrollPane.getBorder();
					tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createEmptyBorder(2, 2, 2, 2), currentBorder));
					add(createToolBarPanel(), BorderLayout.NORTH);
					add(tableScrollPane);
					add(informationPanel, BorderLayout.SOUTH);

					ChooserPanel.this.fireFinishedLoadingEvent(
							new FinishedLoadingEvent(this));
				} 
				catch (ExecutionException e) {
					Throwable cause = e.getCause();
					if (cause instanceof ChoosableException) {
						ChooserPanel.this.fireFinishedLoadingEvent(
								new FinishedLoadingEvent(this, 
										(ChoosableException) cause));
					}
					else {
						ExceptionsHelper.handleFatalException(cause);
					}
				}
				catch (InterruptedException e) {
					if (LOG.isWarnEnabled()) {
						LOG.warn(e.getMessage(), e);
					}
				}
			}

		};
		SwingWorkerExecutor.getInstance().execute(createChooserModelWorker);
	}

	private void createInformationPanel() {
		informationPanel = new JPanel();
		informationPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 5, 2));
		String layoutSpec = "default,4dlu,pref:grow"; //$NON-NLS-1$
		FormLayout layout = new FormLayout(layoutSpec);
		DefaultFormBuilder builder = new DefaultFormBuilder(layout,
				informationPanel);

		descriptionArea = new JTextArea();
		descriptionArea.setRows(1);
		descriptionArea.setEditable(false);
		JLabel descriptionLabel = new JLabel(Messages
				.getString("ChooserPanel.description")); //$NON-NLS-1$
		builder.append(descriptionLabel);
		builder.nextLine();
		builder.append(descriptionArea, builder.getColumnCount());
		builder.nextLine();

		JButton okButton = new JButton(EntryMessages.getString("Entry.ok")); //$NON-NLS-1$

		// Safe not to release listener
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleOkAction();
			}
		});
		JButton cancelButton = new JButton(EntryMessages
				.getString("Entry.cancel")); //$NON-NLS-1$

		// Safe not to release listener
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleCancelAction();
			}
		});
		JPanel buttonsPanel = ButtonBarFactory.buildOKCancelBar(okButton,
				cancelButton);
		builder.append(buttonsPanel, builder.getColumnCount());
		builder.nextLine();

		//display search panel when chosing identifiers in a project using the esl
		eslSearchPanel = createEslSearchPanel();
		builder.append(eslSearchPanel);		
	}

	/**
	 * This panel displays the document statuses available for filtering the
	 * list of records, based on whether the record has any documents in that
	 * status.
	 * 
	 * Not implemented by default.
	 * 
	 * @return
	 */
	protected JPanel createStatusSelectionPanel() {
		//to be implemented by the DocInstanceChooserPanel and LoadDocumentChooserPanel
		return new JPanel();
	}

	/**
	 * Used when going Back or Up to reset the status selection panel when
	 * required
	 */
	protected void resetStatusSelectionPanel() {
		//to be implemented by the DocInstanceChooserPanel and LoadDocumentChooserPane
	}

	protected JPanel createEslSearchPanel() {
		if (dataset != null && dataset.isEslUsed()) {
			JButton searchButton = new JButton(Messages.getString("ChooserPanel.search"));
			final JPanel buttonPanel = ButtonBarFactory.buildLeftAlignedBar(searchButton);
			eslSearchPanel = new JPanel(new BorderLayout());
			eslSearchPanel.add(buttonPanel, BorderLayout.NORTH);

			searchButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					eslSearchPanel.remove(buttonPanel);
					JButton hideSearch = new JButton(Messages.getString("ChooserPanel.hidesearch"));
					eslSearchPanel.add(ButtonBarFactory.buildLeftAlignedBar(hideSearch), BorderLayout.NORTH);
					eslSearchPanel.add(new EslSearchPanel(application, dialog, dataset), BorderLayout.CENTER);
					eslSearchPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 2));
					eslSearchPanel.revalidate();
					informationPanel.revalidate();
					ChooserPanel.this.revalidate();

					hideSearch.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							eslSearchPanel.removeAll();
							eslSearchPanel.add(buttonPanel, BorderLayout.NORTH);
							eslSearchPanel.revalidate();
						}
					});
				}
			});

			return eslSearchPanel;
		}
		return new JPanel();
	}

	private void handleCancelAction() {
		fireCancelAction(new CancelActionEvent(this));
	}

	private void handleOkAction() {
		processSelectionAction(table.getSelectedRow());
	}

	public void addCancelActionListener(CancelActionListener listener) {
		listenerList.add(CancelActionListener.class, listener);
	}

	public void removeCancelActionListener(CancelActionListener listener) {
		listenerList.remove(CancelActionListener.class, listener);
	}

	protected void fireCancelAction(CancelActionEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CancelActionListener.class) {
				((CancelActionListener) listeners[i + 1]).cancelAction(event);
			}
		}
	}

	public void addFinishedLoadingListener(FinishedLoadingListener listener) {
		listenerList.add(FinishedLoadingListener.class, listener);
	}

	public void removeFinishedLoadingListener(FinishedLoadingListener listener) {
		listenerList.remove(FinishedLoadingListener.class, listener);
	}

	protected void fireFinishedLoadingEvent(FinishedLoadingEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == FinishedLoadingListener.class) {
				((FinishedLoadingListener) listeners[i + 1])
				.finishedLoading(event);
			}
		}
	}

	private void addListeners() {
		// Safe not to release listener
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				if (row == -1) {
					return;
				}
				String description = model.getCurrentTableModel()
				.getValueAtRow(row).getDescription();

				if (description == null || description.equals("")) { //$NON-NLS-1$
					description = Messages
					.getString("ChooserPanel.noDescription"); //$NON-NLS-1$
				}
				descriptionArea.setText(description);
				if (e.getClickCount() == 2
						&& e.getButton() == MouseEvent.BUTTON1) {
					processSelectionAction(row);
				}
			}

		});
	}

	public static class FinishedLoadingEvent extends EventObject {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private final ChoosableException choosableException;

		public FinishedLoadingEvent(Object source) {
			super(source);
			choosableException = null;
		}

		public FinishedLoadingEvent(Object source, ChoosableException exception) {
			super(source);
			this.choosableException = exception;
		}

		public final ChoosableException getChoosableException() {
			return choosableException;
		}
	}

	public static interface FinishedLoadingListener extends EventListener {
		public void finishedLoading(FinishedLoadingEvent event);
	}

	public static interface CancelActionListener extends EventListener {
		public void cancelAction(CancelActionEvent event);
	}

	public static class CancelActionEvent extends EventObject {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CancelActionEvent(Object source) {
			super(source);
		}

	}

	protected DataSet getDataset() {
		return dataset;
	}

	protected void setDataset(DataSet dataset) {
		this.dataset = dataset;
	}
}