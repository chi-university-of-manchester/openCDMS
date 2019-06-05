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
package org.psygrid.datasetdesigner.ui.chooser;

import org.jdesktop.swingx.JXTable;
import org.psygrid.collection.entry.security.SecurityHelper;

import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.custom.BoxButton;

import org.psygrid.datasetdesigner.renderer.EntryTableCellRenderer;

import org.psygrid.datasetdesigner.utils.XMLFileFilter;

import org.psygrid.datasetdesigner.model.ProjectChooserTableModel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableColumn;
import org.jdesktop.swingworker.SwingWorker;

import org.psygrid.www.xml.security.core.types.ProjectType;


/**
 * A chooser to select datasets from the file system
 * or from the repository.
 *
 * @author pwhelan
 */
public class StudyChooser extends JFileChooser implements ActionListener {
    /**
     * Show the file selecor view
     */
    public final static String CHOOSER_VIEW = "ChooserView";

    /**
     * Show the view of the repository
     */
    public final static String REPOSITORY_VIEW = "RepositoryView";

    /**
     * The main window of the application to act as parent
     */
    private JFrame mainFrame;

    /**
     * Button to hold the action to switch to the repository view
     */
    private BoxButton repositoryButton;

    /**
     * Button to hold the action to switch to the repository view
     */
    private BoxButton fileChooserButton;

    /**
     * CardLayout used to control display switching between repository
     * and file system view
     */
    private CardLayout cards;

    /**
     * Specified mode; either repository or file chooser;
     * default is chooser view
     */
    private String mode = CHOOSER_VIEW;

    /**
     * Main panel of the chooser holding either chooser or the
     * repository view
     */
    private JPanel mainPanel;

    /**
     * Main panel of the chooser holding either chooser or the
     * repository view
     */
    private JPanel repositoryPanel;
    
    /**
     * Open button for rep view
     */
    private JButton openButton;
    
    /**
     * Cancel button for rep view
     */
    private JButton cancelButton;

    /**
     * Open button for rep view
     */
    private JButton saveButton;

    /**
     * Open button for rep view
     */
    private JXTable table;

    /**
     * Open button for rep view
     */
    private ProjectType selectedProject;
    
    /**
     * TextField to hold the selected repository project
     */
    private JTextField selectedRepositoryField;
    
    /**
     * TextField to hold the selected repository project
     */
    private JComboBox repositorySwitcher;
    
    /**
     * Constructor; create the chooser
     * @param mainFrame the main window of the application
     */
    public StudyChooser(JFrame mainFrame) {
        super();
        this.mainFrame = mainFrame;
	    XMLFileFilter ff = new XMLFileFilter();
        setFileFilter(ff);
        setAcceptAllFileFilterUsed(false);
        addChoosableFileFilter(ff);
    }

    /**
     * Override the createDialog method to use
     * CardLayout to switch between Repository and File System views
     * @param parent the owner of the dialog
     * @return the configured JDialog
     * @throws Headless Exception
     */
    protected JDialog createDialog(Component parent) throws HeadlessException {
        String title = getUI().getDialogTitle(this);
        getAccessibleContext().setAccessibleDescription(title);

        JDialog dialog = new JDialog(mainFrame, title, true);
        dialog.setComponentOrientation(this.getComponentOrientation());

        Container contentPane = dialog.getContentPane();
        cards = new CardLayout();
        contentPane.setLayout(new BorderLayout());

        mainPanel = new JPanel();
        mainPanel.setLayout(cards);
        mainPanel.add(this, CHOOSER_VIEW);
        
        mainPanel.add(buildRepositoryPanel(), REPOSITORY_VIEW);

        JPanel westPanel = new JPanel();
        westPanel.setBorder(BorderFactory.createEmptyBorder(9, 5, 30, 0));
        westPanel.setLayout(new BorderLayout());
        
        JPanel comboPanel = new JPanel();
        comboPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        repositorySwitcher = new JComboBox();
        DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
        comboModel.addElement("File System");
        comboModel.addElement("Database");
        repositorySwitcher.setModel(comboModel);
        repositorySwitcher.addActionListener(this);

        
        if (getDialogType() == JFileChooser.OPEN_DIALOG) {
            comboPanel.add(new JLabel("From:"));
        } else if (getDialogType() == JFileChooser.SAVE_DIALOG) {
        	dialog.setTitle("Save As");
        	comboPanel.add(new JLabel("To:"));
        }
         
        comboPanel.add(repositorySwitcher);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(Box.createVerticalStrut(20));
        
        JLabel repLabel = new JLabel("Database");
        repLabel.setAlignmentX(0.5f);
        JLabel fileSystemLabel = new JLabel("FileSystem");
        fileSystemLabel.setAlignmentX(0.5f);
        
		repositoryButton = new BoxButton("Database", "repository.png");
        repositoryButton.addActionListener(this);
        repositoryButton.setAlignmentX(0.5f);
        fileChooserButton = new BoxButton("fileChooserButton", "filesystemview.png");
        fileChooserButton.addActionListener(this);
        fileChooserButton.setAlignmentX(0.5f);
        buttonPanel.add(repLabel);
        buttonPanel.add(Box.createVerticalStrut(2));
        buttonPanel.add(repositoryButton);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(fileSystemLabel);
        buttonPanel.add(Box.createVerticalStrut(2));
        buttonPanel.add(fileChooserButton);
        
        westPanel.add(comboPanel, BorderLayout.NORTH);
        westPanel.add(buttonPanel, BorderLayout.CENTER);

        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(westPanel, BorderLayout.WEST);

        if (JDialog.isDefaultLookAndFeelDecorated()) {
            boolean supportsWindowDecorations = UIManager.getLookAndFeel()
                                                         .getSupportsWindowDecorations();

            if (supportsWindowDecorations) {
                dialog.getRootPane()
                      .setWindowDecorationStyle(JRootPane.FILE_CHOOSER_DIALOG);
            }
        }

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        
        setRepPanelEnabled(false);
        UpdateTableThread thread = new UpdateTableThread();
        thread.start();
        
        return dialog;
    }

    /**
     *
     *
     * @return 
     */
    public JPanel buildRepositoryPanel() {
    	JPanel repAndHolderPanel = new JPanel();
    	repAndHolderPanel.setLayout(new BorderLayout());
    	repAndHolderPanel.setBorder(BorderFactory.createEmptyBorder(9, 5, 0, 0));
    	
        repositoryPanel = new JPanel();
        repositoryPanel.setLayout(new BorderLayout());
        
        try {
        	table = new JXTable();
            
            if (getDialogType() == JFileChooser.OPEN_DIALOG) {
                table.addMouseListener(new MouseAdapter() {
                	public void mouseClicked(MouseEvent e) {
                		if (e.getClickCount() == 2) {
                			selectedProject = (((ProjectChooserTableModel)table.getModel()).getValueAtRow(table.getSelectedRow()));
                			//selectedProject = (ProjectType)table.getModel().getAt(table.getSelectedRow(), 0);
                			approveSelection();
                		}
                	}
                });
                table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            		public void valueChanged(ListSelectionEvent e) {
                        // If cell selection is enabled, both row and column change events are fired
                        if (e.getSource() == table.getSelectionModel()
                              && table.getRowSelectionAllowed()) {
                        	if (table.getSelectedRow() == -1) {
                        		selectedProject = null;
                        		selectedRepositoryField.setText("");
                        	} else {
                                selectedProject = (((ProjectChooserTableModel)table.getModel()).getValueAtRow(e.getLastIndex()));
                                selectedRepositoryField.setText(selectedProject.getName());
                        	}
                        } 
                } });
            }
        } catch (Exception ex) {
        	table = new JXTable();
        	ex.printStackTrace();
        }
        
        initTable();

        JScrollPane tableScrollPane = new JScrollPane(table);
        Border currentBorder = tableScrollPane.getBorder();
        tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 4, 2, 4), currentBorder));
        repositoryPanel.add(tableScrollPane, BorderLayout.CENTER);
        repositoryPanel.add(createToolBar(), BorderLayout.SOUTH);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        if (getDialogType() == JFileChooser.OPEN_DIALOG) {
        	openButton = new JButton("Open");
        	openButton.addActionListener(this);
        	cancelButton = new JButton("Cancel");
        	cancelButton.addActionListener(this);
        	buttonPanel.add(openButton);
        	buttonPanel.add(cancelButton);
        } else {
        	saveButton = new JButton("Save");
        	saveButton.addActionListener(this);
        	cancelButton = new JButton("Cancel");
        	cancelButton.addActionListener(this);
        	buttonPanel.add(saveButton);
        	buttonPanel.add(cancelButton);
        }
        
        repAndHolderPanel.add(repositoryPanel, BorderLayout.CENTER);
        repAndHolderPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return repAndHolderPanel;
    }
    
    private void setRepPanelEnabled(boolean enabled) {
    	repositoryPanel.setEnabled(enabled);
    	if(enabled) {
    		selectedRepositoryField.setText("");
    	} else {
    		selectedRepositoryField.setText("Loading studies...please wait");
    	}
    	selectedRepositoryField.setEnabled(enabled);
    }
    
    private void updateTableInBackground() {
    	try {
        	SwingWorker worker = new SwingWorker() {
          	   public ProjectChooserTableModel doInBackground() {
          		   try {
                      	ArrayList<ProjectType> pTypes = new ArrayList<ProjectType>(SecurityHelper.getAAQueryClient().getMyProjects());
                        return new ProjectChooserTableModel(pTypes);
          		   } catch (Exception ex) {
          			   ex.printStackTrace();
          			   return null;
          		   }
          	   }
          	   
          	   protected void done() {
          		   try {
              		   if (table == null) {
              			   table = new JXTable();
              			   initTable();
              		   }
              		   table.setModel((ProjectChooserTableModel)get());
              		   initTable();
              		   setRepPanelEnabled(true);
              		   
              		   //if the dialog type is a save dialog, then dispaly the dataset name
              		   //in the textfield
              		   if (getDialogType() == JFileChooser.SAVE_DIALOG) {
                  		   selectedRepositoryField.setText(DatasetController.getInstance().getActiveDs().getDs().getName());
              		   }
          		   } catch (Exception ex) {
          			   ex.printStackTrace();
          		   }
          	   }
          	};
          	worker.run();
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }
    
    private JToolBar createToolBar() {
    	JToolBar toolBar = new JToolBar();
		toolBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		toolBar.setFloatable(false);
		JPanel locationPanel = new JPanel(new BorderLayout());
		selectedRepositoryField = new JTextField();
		if (getDialogType() == JFileChooser.SAVE_DIALOG) {
			locationPanel.add(new JLabel("Study Name: "), BorderLayout.WEST);
			selectedRepositoryField.setText(DatasetController.getInstance().getActiveDs().getDs().getName());
		} else {
			locationPanel.add(new JLabel("Open Study: "), BorderLayout.WEST);
		
		}
		selectedRepositoryField.setEditable(false);
		
		locationPanel.add(selectedRepositoryField, BorderLayout.CENTER);
		locationPanel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		toolBar.add(locationPanel);
		return toolBar;
    }

    /**
     *
     *
     * @return 
     */
    public String getMode() {
        return mode;
    }

    /**
     *
     *
     * @param e 
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == repositoryButton) {
            cards.show(mainPanel, REPOSITORY_VIEW);
            mode = REPOSITORY_VIEW;
            repositorySwitcher.setSelectedItem("Database");
        } else if (e.getSource() == fileChooserButton) {
            cards.show(mainPanel, CHOOSER_VIEW);
            mode = CHOOSER_VIEW;
            repositorySwitcher.setSelectedItem("File System");
        } else if (e.getSource() == openButton || e.getSource() == saveButton){
        	approveSelection();
        } else if (e.getSource() == cancelButton) {
        	cancelSelection();
        } else if (e.getSource() == repositorySwitcher) {
        	if(repositorySwitcher.getSelectedItem().equals("Database")) {
                cards.show(mainPanel, REPOSITORY_VIEW);
                mode = REPOSITORY_VIEW;
        	} else {
                cards.show(mainPanel, CHOOSER_VIEW);
                mode = CHOOSER_VIEW;
        	}
        }
    }

    
	private void initTable() {
		table.setPreferredScrollableViewportSize(new Dimension(500, 280));
		
		if (getDialogType() == JFileChooser.OPEN_DIALOG) {
			table.setRowSelectionAllowed(true);
			table.setFocusable(true);
			table.setCellSelectionEnabled(true);
		} else {
			table.setRowSelectionAllowed(false);
			// Do this only if there's no need for the component to have the focus
		    table.setFocusable(false);
		    // Partially disables selections (see description above)
		    table.setCellSelectionEnabled(false);
		}
		table.setShowGrid(false);
		table.setColumnMargin(0);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		List<?> columns = table.getColumns();
		for (int i = 0, c = columns.size(); i < c; ++i) {
			TableColumn column = (TableColumn) columns.get(i);
			column.setCellRenderer(new EntryTableCellRenderer());
		}
	}
    
	
	public ProjectType getSelectedProject() {
		return selectedProject;
	}
    
    @Override
	public File getSelectedFile() {
    	if (mode == REPOSITORY_VIEW) {
    		return null;
    	}
    	return super.getSelectedFile();
	}

    // This class extends Thread
    class UpdateTableThread extends Thread {
        // This method is called when the thread runs
        public void run() {
        	updateTableInBackground();
        }
    }

}
