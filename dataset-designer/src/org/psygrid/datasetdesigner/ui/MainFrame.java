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

package org.psygrid.datasetdesigner.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.event.ProgressEvent;
import org.psygrid.collection.entry.Launcher;
import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.security.SecurityManager;
import org.psygrid.common.security.LoginInterfaceFrame;
import org.psygrid.datasetdesigner.actions.ExitAction;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.model.DELStudySet;
import org.psygrid.datasetdesigner.ui.dataelementfacilities.DataElementClientInitializer;
import org.psygrid.datasetdesigner.utils.IconsHelper;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

/**
 * Main Window of the DSD
 * Holds the tree, entry button panel and the document panel screens
 * 
 * @author pwhelan
 *
 */
public class MainFrame extends LoginInterfaceFrame implements WindowListener{

	/**
	 * The logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(MainFrame.class);

	/**
	 * The <code>MainFrame</code> instance of this class
	 */
	private MainFrame mainFrame;

	/**
	 * The four main components of the DSD; tree, doc panel, 
	 * tabbed pane and entry panel
	 */ 
	private MainEntryPanel mainEntryPanel;

	/**
	 * The main tree of the application
	 */
	private MainTree mainTree;

	/**
	 * The document panel of the application
	 */
	private DocumentPanel documentPanel;

	/**
	 * The tabbed pane containing the document designers
	 */
	private MainTabbedPane docPane;

	/**
	 * The left <code>JSplitPane</code> for dividing up the main panels
	 */
	private JSplitPane leftPane;

	/**
	 * The main menubar of the application
	 */
	private MainMenuBar mainMenuBar;

	/**
	 * The DEL client initializer
	 */
	private DataElementClientInitializer delInitializer;

	/**
	 * Determines whether a dataset can be created or not
	 */
	private boolean canCreateDataSet;
	
	public boolean isCanPatchDataSet() {
		return canPatchDataSet;
	}

	public void setCanPatchDataSet(boolean canPatchDataSet) {
		this.canPatchDataSet = canPatchDataSet;
	}

	private boolean canPatchDataSet = false;

	/**
	 * The connectivity indicator; grey if not started; green if connected; red if not
	 * initialised here to grey
	 */
	private JLabel indicatorLabel = new JLabel(IconsHelper.getInstance().getImageIcon("indicator-grey.gif"));

	/**
	 * A <code>JXstatus</code> bar to show progess on saving, loading etc.
	 */
	private JXStatusBar statusBar;

	/**
	 * Contains the connectivity status of the application; initalised to false
	 * updated by the update status thread that monitors connectivity 
	 * using calls to the RemoteManager
	 */
	private boolean online = false;

	/**
	 * Flag to indicate if the connectivity monitor is conductings its first connectivity 
	 * checks
	 */
	private boolean firstStatusUpdate = true;

	/**
	 * Panel holding the south panel
	 */
	private JPanel southPanel;


	/**
	 * Constructor
	 * Sets the basic size and lays out the menu bar, document panel,
	 * tree and status bar
	 */
	public MainFrame() {
		String title = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.title"); 
		String clientSystem = "";
		try {
			clientSystem = Launcher.getClientProperties().getProperty("system.shortname");
		}
		catch (IOException ioe) {
			LOG.warn("Unable to retrieve client system name.", ioe);
		}
		title += " - "+clientSystem;
		setTitle(title);
		
		setIconImage(IconsHelper.getInstance().getImageIcon("psygrid.jpg").getImage());
		setSize(new Dimension(1000, 600));
		setLocationRelativeTo(null);  
		mainFrame = this;
		canCreateDataSet = false;
		mainMenuBar = new MainMenuBar(this, docPane);

		//layout and add components
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainMenuBar, BorderLayout.NORTH);
		getContentPane().add(new JPanel(), BorderLayout.CENTER);
		getContentPane().add(createStatusBar(), BorderLayout.SOUTH);
		setVisible(true);
		setMenuEnabled(false);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
	}

	/**
	 * Create a panel containing the status bar 
	 * and a connectivity indicator
	 * @return a configured JPanel containing the status bar and the connectivity 
	 * indicator
	 */
	private JPanel createStatusBar() {
		if (southPanel == null) {
			statusBar = new JXStatusBar();
			southPanel = new JPanel();
			southPanel.setLayout(new BorderLayout());
			southPanel.add(statusBar, BorderLayout.CENTER);
			southPanel.add(indicatorLabel, BorderLayout.EAST);
		}
		return southPanel;
	}

	/**
	 * Initialise the gui building and the DEL Client
	 */
	public void init() {
		setStarted(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.mainframe.loading"));
		setMenuEnabled(false);

		SwingWorker worker = new SwingWorker() {
			public Object doInBackground() {
				delInitializer = new DataElementClientInitializer(mainFrame);
				try {
					//Needs to be done after the del is initialised.
					canCreateDataSet = SecurityManager.getInstance().canSaveDataSet();
				} catch (Exception ex) {
					LOG.error("Can create dataset failed" , ex);
				}
				return null;
			}

			public void done() {
				try {
					get();
					doGUIBuilding();
					updateStatusBar();
					//clear text
					setTitle(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.title") + " - " + SecurityManager.getInstance().getUserName());
					setFinished(PropertiesHelper.getStringFor(" "));
					setMenuEnabled(true);
				} catch (Exception ex) {
					LOG.error("Exception initialising main frame" , ex);
				}
			}
		};
		worker.execute();
	}

	/**
	 * Initialise all panels and set the preferred sizes
	 *
	 */
	public void doGUIBuilding() {
		try {
			getContentPane().removeAll();
			setIconImage(IconsHelper.getInstance().getImageIcon("psygrid.jpg").getImage());
			//no need to set the size or location here as it was already set in constructor
			//setSize(new Dimension(1000, 800));
			//setLocationRelativeTo(null);

			//initialise all components 
			docPane = new MainTabbedPane(this, delInitializer);
			mainTree = new MainTree(docPane);

			mainMenuBar = new MainMenuBar(this, docPane);


			mainEntryPanel = new MainEntryPanel();

			//layout and add components
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(mainMenuBar, BorderLayout.NORTH);
			setMenuEnabled(false);

			JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.add(new JScrollPane(mainTree), 
					PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.treeview.design"));

			if (DatasetController.getInstance().getActiveDs() == null
					|| !(DatasetController.getInstance().getActiveDs() instanceof DELStudySet)) {
				tabbedPane.add(new JScrollPane(new ViewScheduleTree(mainTree.getModel())), 
						PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.treeview.schedule"));
			}

			leftPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane, docPane);
			leftPane.addComponentListener(new ResizeAdapter());
			getContentPane().add(leftPane, BorderLayout.CENTER);

			JScrollPane entryScrollPane = new JScrollPane(mainEntryPanel);
			entryScrollPane.setPreferredSize(new Dimension(140, entryScrollPane.getPreferredSize().height-100));


			getContentPane().add(entryScrollPane, BorderLayout.EAST);
			getContentPane().add(createStatusBar(), BorderLayout.SOUTH);
			addComponentListener(new ResizeAdapter());

			setVisible(true);
			leftPane.setDividerLocation(.2);
			leftPane.setResizeWeight(.2);

			docPane.setPreferredSizeForCenterComp(new Dimension(400, 680));
			mainEntryPanel.setPreferredSize(new Dimension(120, 780));
			mainEntryPanel.setMaximumSize(new Dimension(120, (int)mainEntryPanel.getPreferredSize().getHeight()-40));
			mainEntryPanel.setMinimumSize(new Dimension(120, (int)mainEntryPanel.getPreferredSize().getHeight()-40));

			tabbedPane.setMinimumSize(new Dimension(200, (int)mainTree.getMinimumSize().getHeight()));
			tabbedPane.setPreferredSize(new Dimension(200, (int)mainTree.getMinimumSize().getHeight()));
			tabbedPane.setMaximumSize(new Dimension(200, (int)mainTree.getMinimumSize().getHeight()));

		}
		catch (Exception e) {
			LOG.error("MainFrame: error building the main frame", e);
			e.printStackTrace();
		}
	}

	/**
	 * Start the status bar and set the cursort to wait
	 *
	 */
	public void setStarted() {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				statusBar.progressStarted(new ProgressEvent(this));
			}
		});
	}

	/**
	 * Start the status bar and set the cursort to wait
	 *
	 */
	public void setStarted(final String progressText) {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				statusBar.setLeadingMessage(progressText);
				statusBar.progressStarted(new ProgressEvent(this));
			}
		});
	}


	/**
	 * Stop the status bar and set the cursor back to default
	 *
	 */
	public void setFinished() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				statusBar.progressEnded(new ProgressEvent(this));
			}
		});
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * Stop the status bar and set the cursor back to default
	 *
	 */
	public void setFinished(final String progressText) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				statusBar.setLeadingMessage(progressText);
				statusBar.progressEnded(new ProgressEvent(this));
			}
		});
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * Set the status bar text to the String passed
	 * @param text the text to display in the status bar
	 */
	public void setStatusBarText(final String text) {
		statusBar.setLeadingMessage(text);
	}

	/**
	 * return the document panel
	 * @return the document panel
	 */
	public MainTabbedPane getDocPane() {
		return docPane;
	}

	/**
	 * Get the Tree containing documents and entires
	 * @return the main tree of the DSD
	 */
	public MainTree getTree() {
		return mainTree;
	}

	public MainMenuBar getMainMenuBar() {
		return mainMenuBar;
	}

	public MainEntryPanel getMainEntryPanel() {
		return mainEntryPanel;
	}

	/**
	 * Listen to a resize event on the main frame
	 * should resize the document panel manually here
	 * @author pwhelan
	 *
	 */
	private class ResizeAdapter extends ComponentAdapter {
		/**
		 * resize the document panel based on the new size of the window
		 */
		public void componentResized(ComponentEvent e) {
			updatePanel();
		}

		/**
		 * resize the document panel based on the new size of the window
		 */
		public void componentMoved(ComponentEvent e) {
			updatePanel();
		}

		/**
		 * resize the document panel based on the new size of the window
		 */
		public void updatePanel() {
			documentPanel = docPane.getCurrentPanel();
			if (documentPanel != null) {
				int prefWidth = leftPane.getWidth() - leftPane.getDividerLocation();
				prefWidth -= 250;
				docPane.setPreferredSizeForCenterComp(new Dimension(prefWidth, leftPane.getHeight()-40));
			}
		}
	}

	/**
	 * Required to implement to use interface - do nothing
	 */
	public void windowActivated(WindowEvent e) {
	}

	/**
	 * Exit the application when this window is closed
	 */
	public void windowClosed(WindowEvent e) {
		System.exit(0);
	}
	/**
	 * Exit the application when this window is closed
	 */
	public void windowClosing(WindowEvent e) {
		ExitAction exitAction = new ExitAction(this);
		exitAction.actionPerformed(new ActionEvent("", 1, ""));
	}

	/**
	 * Required to implement to use interface - do nothing
	 */
	public void windowDeactivated(WindowEvent e) {
	}

	/**
	 * Required to implement to use interface - do nothing
	 */
	public void windowDeiconified(WindowEvent e) {
	}

	/**
	 * Required to implement to use interface - do nothing
	 */
	public void windowIconified(WindowEvent e) {
	}

	/**
	 * Required to implement to use interface - do nothing
	 */
	public void windowOpened(WindowEvent e) {
	}

	/**
	 * Get the main tree of the application
	 * @return the main application tree
	 */
	public MainTree getMainTree() {
		return mainTree;
	}

	/**
	 * Get the DEL client initializer 
	 * @return the DEL client initializer
	 */
	public DataElementClientInitializer getDelInitializer() {
		return delInitializer;
	}

	/**
	 * Set the DEL client initializer 
	 * @param the DEL client initializer
	 */
	public void setDelInitializer(DataElementClientInitializer delInitializer) {
		this.delInitializer = delInitializer;
	}

	/**
	 * Flag to indicate if user has permission to create a dataset
	 * @return true if user has permission to create a dataset; false if not
	 */
	public boolean isCanCreateDataSet() {
		return canCreateDataSet;
	}

	/**
	 * Set the flag to indicate if the user has permission to create a 
	 * dataset or not
	 * @param canCreateDataSet flag to indicate if the user can 
	 * create a dataset
	 */
	public void setCanCreateDataSet(boolean canCreateDataSet) {
		this.canCreateDataSet = canCreateDataSet;
	}

	/**
	 * Refresh the menu bar; remove it from the panel and recreate it
	 *
	 */
	public void refreshMainMenuBar() {
		try {
			getContentPane().remove(0);
		}
		catch (ArrayIndexOutOfBoundsException ex) {
			//DataElementClientInitializer hasn't initialised properly. 
			//Ignore the error
			LOG.error("MF : refresh main menu bar: error logged " + ex.getMessage());
		}
		getContentPane().invalidate();
		mainMenuBar = new MainMenuBar(this, docPane);
		getContentPane().add(mainMenuBar, BorderLayout.NORTH);
		getContentPane().validate();
		getContentPane().repaint();
	}

	/**
	 * Method to check for connection; if different to previous state, 
	 * update the indicator.
	 */
	public void updateStatus()
	{
		try
		{
			boolean oldOnline = online;
			online = RemoteManager.getInstance().isConnectionAvailable(false);
			//avoid needless refreshing, only update if the status has changed
			if ( firstStatusUpdate ){
				updateIndicatorLabel(online);
				firstStatusUpdate = false;
			}
			else if ( oldOnline != online )
			{
				updateIndicatorLabel(online);
			}
		} 
		catch (Exception ex)
		{
			//assuming exception thrown here means no connection
			online = false;
			updateIndicatorLabel(online);
		}
	}

	/**
	 * Invokes thread to update status bar indicator.
	 */
	public void updateStatusBar()
	{
		UpdateStatus updateStatus = new UpdateStatus();
		updateStatus.start();
	}

	private void setMenuEnabled(boolean enabled) {
		if (mainMenuBar != null) {
			for ( int i = 0; i < mainMenuBar.getMenuCount(); i++ )
			{
				if (mainMenuBar.getMenu(i) != null) {
					mainMenuBar.getMenu(i).setEnabled(enabled);
				}
			}
		}
	}

	/**
	 * Method to update network connectivity icon in the status bar
	 * @param connected Whether connection is available or not
	 */
	public void updateIndicatorLabel(boolean connected)
	{

		if (connected) {
			if ( LOG.isInfoEnabled() ){
				LOG.info("Application has gone online.");
			}
			indicatorLabel.setIcon(IconsHelper.getInstance().getImageIcon("indicator-bright-green.gif"));
			indicatorLabel.setToolTipText("Online");
		} else {
			if ( LOG.isInfoEnabled() ){
				LOG.info("Application has gone offline.");
			}
			indicatorLabel.setIcon(IconsHelper.getInstance().getImageIcon("indicator-red.gif"));
			indicatorLabel.setToolTipText("Offline");
		}
		indicatorLabel.revalidate();
		indicatorLabel.repaint();
	}

	/**
	 * Thread to update the status indicator at bottom right of status bar.
	 * Polls for connection every 10 seconds
	 */
	private class UpdateStatus extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				for (;;)
				{
					Thread.sleep(10000);
					updateStatus();
				}
			} catch (Exception ex)
			{
				//on exception do nothing
				if (LOG.isErrorEnabled()) {
					LOG.error(ex);
				}
			}
		}
	}

}