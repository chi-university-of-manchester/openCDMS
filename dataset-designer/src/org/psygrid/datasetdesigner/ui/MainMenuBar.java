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

import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

import javax.help.CSH;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.datasetdesigner.actions.AboutAction;
import org.psygrid.datasetdesigner.actions.ApproveElementAction;
import org.psygrid.datasetdesigner.actions.ApproveValidationRulesAction;
import org.psygrid.datasetdesigner.actions.CloseStudyAction;
import org.psygrid.datasetdesigner.actions.ConfigureConsentAction;
import org.psygrid.datasetdesigner.actions.ConfigureDatasetPropertiesAction;
import org.psygrid.datasetdesigner.actions.ConfigureDocumentGroupsAction;
import org.psygrid.datasetdesigner.actions.ConfigureRandomiserAction;
import org.psygrid.datasetdesigner.actions.ConfigureRegisterAction;
import org.psygrid.datasetdesigner.actions.ConfigureReportsAction;
import org.psygrid.datasetdesigner.actions.ConfigureRolesAction;
import org.psygrid.datasetdesigner.actions.ConfigureSitesAction;
import org.psygrid.datasetdesigner.actions.ConfigureStatusAndStateTransitionsAction;
import org.psygrid.datasetdesigner.actions.ConfigureTransformersAction;
import org.psygrid.datasetdesigner.actions.ConfigureUnitsAction;
import org.psygrid.datasetdesigner.actions.ConfigureValidationRulesAction;
import org.psygrid.datasetdesigner.actions.DatasetWizardAction;
import org.psygrid.datasetdesigner.actions.DeleteSavedDatasetAction;
import org.psygrid.datasetdesigner.actions.ExitAction;
import org.psygrid.datasetdesigner.actions.NewDocumentAction;
import org.psygrid.datasetdesigner.actions.OpenLibraryViewAction;
import org.psygrid.datasetdesigner.actions.OpenRecentStudyAction;
import org.psygrid.datasetdesigner.actions.OpenStudyAction;
import org.psygrid.datasetdesigner.actions.PatchDatasetAction;
import org.psygrid.datasetdesigner.actions.PreviewDatasetAction;
import org.psygrid.datasetdesigner.actions.PublishStudyAction;
import org.psygrid.datasetdesigner.actions.ReorderDocumentsAction;
import org.psygrid.datasetdesigner.actions.SaveAsStudyAction;
import org.psygrid.datasetdesigner.actions.SaveStudyAction;
import org.psygrid.datasetdesigner.actions.SearchDELEntriesAction;
import org.psygrid.datasetdesigner.actions.SearchPendingDELEntriesAction;
import org.psygrid.datasetdesigner.actions.ShowValidationDialogAction;
import org.psygrid.datasetdesigner.actions.SubmitToElemLibraryAction;
import org.psygrid.datasetdesigner.actions.UpdateLibraryElementsAction;
import org.psygrid.datasetdesigner.actions.UpdatePolicyAction;
import org.psygrid.datasetdesigner.actions.ViewScheduleAction;
import org.psygrid.datasetdesigner.actions.ViewValidationRulesAction;
import org.psygrid.datasetdesigner.actions.SearchDELEntriesAction.SearchType;
import org.psygrid.datasetdesigner.actions.SubmitToElemLibraryAction.SubmissionType;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.controllers.RecentStudiesController;
import org.psygrid.datasetdesigner.model.DELStudySet;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.ui.dataelementfacilities.DELSecurity;
import org.psygrid.datasetdesigner.utils.HelpHelper;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.Utils;

/**
 * Main menu bar for the DSD
 * Contains all the menu items and actions for the main window
 * @author pwhelan
 *
 */
public class MainMenuBar extends JMenuBar {

	/**
	 * The main window of the application
	 */
	private MainFrame frame; 
	
	/**
	 * The <code>JTabbedPane</code> containing the various document design panels
	 */
	private JTabbedPane docPane;

	/**
	 * The menu containing actions for performing on datasets
	 */
	private JMenu datasetMenu;

	/**
	 * The menu containing actions for opening/saving etc. datasets
	 */
	private JMenu fileMenu;
	
	/**
	 * The menu containing actions for opening/saving etc. datasets
	 */
	private JMenuItem saveMenuItem;

	/**
	 * The menu containing actions for opening/saving etc. datasets
	 */
	private JMenuItem newDocItem;

	/**
	 * The menu containing actions for opening/saving etc. datasets
	 */
	private JMenuItem saveAsMenuItem;
	
	/**
	 * The menu containing actions for opening/saving etc. datasets
	 */
	private JMenuItem closeMenuItem;

	/**
	 * The menu containing actions for opening/saving etc. datasets
	 */
	private JMenuItem publishMenuItem;
	
	/**
	 * The menu containing actions for deleting datasets
	 */
	private JMenuItem deleteMenuItem;
	
	/**
	 * The menu containing actions for previewing datasets in CoCoA
	 */
	private JMenuItem patchMenuItem;
	
	/**
	 * The menu containing actions for previewing datasets in CoCoA
	 */
	private JMenuItem previewMenuItem;
	
	/**
	 * The menu containing actions for previewing datasets in CoCoA
	 */
	private JMenuItem propertiesMenuItem;
	
	/**
	 * The menu containing actions for previewing datasets in CoCoA
	 */
	private JMenuItem randomItem;
	
	/**
	 * The menu containings the action for exiting 
	 */
	private JMenuItem exitItem;
	
	/**
	 * The menu containings the action for exiting 
	 */
	private JMenuItem updatePolicyItem;
	
	/**
	 * Indicates whether the data element viewer is currently active 
	 */
	private Boolean delContext;
	
	/**
	 * Checks if a separator has already been added to separate
	 * recent studies from the exit item
	 */
	private boolean separatorAdded = false;

	
	/**
	 * Menu item for opening a wizard
	 */
	private JMenuItem dsWizardItem;
	
	private ArrayList<JMenuItem> recentOpenedStudiesItems = new ArrayList<JMenuItem>();
	
	/**
	 * Constructor - add actions and menu items to the menu bar
	 * @param frame the main window of the application
	 * @param docPane the tabbed pane that contains document panels
	 */
	public MainMenuBar(MainFrame frame, JTabbedPane docPane) {
		this.frame = frame;
		this.docPane = docPane;

		delContext = false;	
		
		if (DatasetController.getInstance().getActiveDs() != null && 
				DatasetController.getInstance().getActiveDs() instanceof DELStudySet) {
			//Only the DEL view is open so set the DEL context.
			delContext = true;
		}

		buildMenu();
	}

	/**
	 * Lay out the menu with their menu items
	 */
	private void buildMenu() {
		createFileMenu();
		add(fileMenu);

		if (delContext) {
			datasetMenu = new JMenu(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.library"));
			createLibraryMenu();
		}
		else {
			datasetMenu = new JMenu(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.dataset"));
			//createDatasetMenu();
			datasetMenu.addMenuListener(new DatasetMenuListener());
		}

		add(datasetMenu);

		JMenu helpMenu = new JMenu(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.help"));
		JMenuItem helpMenuItem = new JMenuItem(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.helpetc"));
		helpMenuItem.addActionListener(
				new CSH.DisplayHelpFromSource(HelpHelper.getInstance().getHelpBroker()) );
		helpMenu.add(helpMenuItem);
		helpMenu.addSeparator();
		helpMenu.add(new JMenuItem(new AboutAction(frame)));
		add(helpMenu);

	}
	
	/**
	 * Create the file menu with open/save options for a dataset
	 */
	private void createFileMenu() {
		fileMenu = new JMenu(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.file"));
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.addMenuListener(new FileMenuListener());
		
		
		dsWizardItem = new JMenuItem(new DatasetWizardAction(frame));
		dsWizardItem.setMnemonic(KeyEvent.VK_N);
		dsWizardItem.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (!frame.isCanCreateDataSet()) {
					dsWizardItem.setEnabled(false);
				}
			}
		});
		
		
		fileMenu.add(dsWizardItem);
		//fileMenu.add(newDocItem);
		
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(new OpenStudyAction(frame)));

		boolean delClientInitialized = true;
		if (docPane != null && ((MainTabbedPane)docPane).getDelInitializer() != null) {
			delClientInitialized = ((MainTabbedPane)docPane).getDelInitializer().isDelConnectionIsInitialised();
		}
		
		final JMenuItem openLibraryItem = new JMenuItem(new OpenLibraryViewAction(frame));
		openLibraryItem.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				//The connection often doesn't get initialised until after the menu is set, so it needs to be checked later
				String[] authorities = null;
				
				if (docPane == null) {
					openLibraryItem.setEnabled(false);
				} else {
					if (((MainTabbedPane)docPane).getDelInitializer() != null) {
						authorities = ((MainTabbedPane)docPane).getDelInitializer().getLSIDAuthorities();
					}
					if (authorities != null && authorities.length > 0
							&& ((MainTabbedPane)docPane).getDelInitializer().isDelConnectionIsInitialised()) {
						openLibraryItem.setEnabled(true);
					}
					else {
						openLibraryItem.setEnabled(false);
					}
				}
			}
		});
		
		openLibraryItem.setEnabled(delClientInitialized);
		fileMenu.add(openLibraryItem);
		fileMenu.addSeparator();
		
		closeMenuItem = new JMenuItem(new CloseStudyAction(frame)); 
		closeMenuItem.setMnemonic(KeyEvent.VK_C);
		fileMenu.add(closeMenuItem);
		fileMenu.addSeparator();
		
		saveMenuItem = new JMenuItem(new SaveStudyAction(frame));
		saveMenuItem.setMnemonic(KeyEvent.VK_S);
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
                java.awt.Event.CTRL_MASK));
		fileMenu.add(saveMenuItem);

		saveAsMenuItem = new JMenuItem(new SaveAsStudyAction(frame));
		fileMenu.add(saveAsMenuItem);
		fileMenu.addSeparator();
		
		//deleteMenuItem = new JMenuItem(new DeleteDatasetAction(frame));
		// this is not really needed anymore, I think because users
		//can resave datasets
		//		fileMenu.add(deleteMenuItem);
		//fileMenu.addSeparator();
		
		deleteMenuItem = new JMenuItem(new DeleteSavedDatasetAction(frame));
		fileMenu.add(deleteMenuItem);
		fileMenu.addSeparator();
		
		//publish
		publishMenuItem = new JMenuItem(new PublishStudyAction(frame));
		publishMenuItem.setMnemonic(KeyEvent.VK_P);
		fileMenu.add(publishMenuItem);
		
		patchMenuItem = new JMenuItem(new PatchDatasetAction(frame));
		patchMenuItem.setMnemonic(KeyEvent.VK_A);
		fileMenu.add(patchMenuItem);
		
		updatePolicyItem = new JMenuItem(new UpdatePolicyAction(frame));
		updatePolicyItem.setMnemonic(KeyEvent.VK_U);
		fileMenu.add(updatePolicyItem);
		
		fileMenu.addSeparator();
		
		previewMenuItem = new JMenuItem(new PreviewDatasetAction(frame));
		publishMenuItem.setMnemonic(KeyEvent.VK_V);
		fileMenu.add(previewMenuItem);
		
		fileMenu.addSeparator();
		
		propertiesMenuItem = new JMenuItem(new ConfigureDatasetPropertiesAction(frame));
		fileMenu.add(propertiesMenuItem);
		fileMenu.addSeparator();
		
		exitItem = new JMenuItem(new ExitAction(frame));
		exitItem.setMnemonic(KeyEvent.VK_X);
		fileMenu.add(exitItem);

	}

	/**
	 * Create the leftmost menu
	 */
	private void createDatasetMenu() {
		datasetMenu.setText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.dataset"));
		newDocItem = new JMenuItem(new NewDocumentAction(frame, false));
		
		StudyDataSet activeDs = DatasetController.getInstance().getActiveDs();
		
		//if no active ds set, disable this menu item 
		if (activeDs == null) {
			datasetMenu.add(newDocItem);
			datasetMenu.addSeparator();
			datasetMenu.add(new JMenuItem(new ConfigureUnitsAction(frame)));
			datasetMenu.add(new JMenuItem(new ConfigureTransformersAction(frame)));
			datasetMenu.add(new JMenuItem(new ConfigureValidationRulesAction(frame)));
			datasetMenu.addSeparator();
			datasetMenu.add(new JMenuItem(new ShowValidationDialogAction(frame)));
			datasetMenu.add(new JMenuItem(new ViewScheduleAction(frame, (MainTabbedPane)docPane)));
			datasetMenu.addSeparator();
			datasetMenu.add(new JMenuItem(new ReorderDocumentsAction(frame, false)));
			datasetMenu.add(new JMenuItem(new ConfigureSitesAction(frame)));
			datasetMenu.add(new JMenuItem(new ConfigureRolesAction(frame)));
			datasetMenu.add(new JMenuItem(new ConfigureConsentAction(frame)));
			datasetMenu.add(new JMenuItem(new ConfigureStatusAndStateTransitionsAction(frame)));
			datasetMenu.add(new JMenuItem(new ConfigureDocumentGroupsAction(frame)));
			datasetMenu.add(new JMenuItem(new ConfigureReportsAction(frame)));
			datasetMenu.add(new JMenuItem(new ConfigureRegisterAction(frame)));
			datasetMenu.add(new ConfigureRandomiserAction(frame));
			if (DELSecurity.getInstance().canSearchLibrary()) {
				datasetMenu.addSeparator();
				final JMenuItem checkForUpdatesItem = new JMenuItem(new UpdateLibraryElementsAction(frame.getDocPane()));
				checkForUpdatesItem.setEnabled(false);
				datasetMenu.add(checkForUpdatesItem);
				checkForUpdatesItem.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent event) {
						//Update the menu item for a current DEL connection
						if (DocTreeModel.getInstance().getCheckedOutLSIDs(false, false).size() > 0) {
							checkForUpdatesItem.setEnabled(((MainTabbedPane)docPane).getDelInitializer().isDelConnectionIsInitialised());
						}
					}
				});		
			}
			
			//ds is null so disable menu items
			enabledDatasetMenu(false);
			
		} else if (activeDs.isReadOnly()) {
			datasetMenu.add(new JMenuItem(new ConfigureUnitsAction(frame, false, true)));
			datasetMenu.add(new JMenuItem(new ConfigureTransformersAction(frame, false, true)));
			datasetMenu.add(new JMenuItem(new ConfigureValidationRulesAction(frame, false, true)));
			datasetMenu.addSeparator();
			datasetMenu.add(new JMenuItem(new ShowValidationDialogAction(frame)));
			datasetMenu.add(new JMenuItem(new ViewScheduleAction(frame, ((MainTabbedPane)docPane), true)));
			datasetMenu.addSeparator();
			//can't reorder docs in read-only mode
			//datasetMenu.add(new JMenuItem(new ReorderDocumentsAction(frame, true)));
			datasetMenu.add(new JMenuItem(new ConfigureSitesAction(frame, false, true)));
			datasetMenu.add(new JMenuItem(new ConfigureRolesAction(frame, true)));
			datasetMenu.add(new JMenuItem(new ConfigureConsentAction(frame, true)));
			datasetMenu.add(new JMenuItem(new ConfigureStatusAndStateTransitionsAction(frame, true)));
			datasetMenu.add(new JMenuItem(new ConfigureDocumentGroupsAction(frame, true)));
			datasetMenu.add(new JMenuItem(new ConfigureReportsAction(frame, true)));
			datasetMenu.add(new JMenuItem(new ConfigureRegisterAction(frame, true)));
			randomItem = new JMenuItem(new ConfigureRandomiserAction(frame, true));
			datasetMenu.add(new JMenuItem(new ConfigureRandomiserAction(frame, true)));

			if (DELSecurity.getInstance().canSearchLibrary()) {
				datasetMenu.addSeparator();
				final JMenuItem checkForUpdatesItem = new JMenuItem(new UpdateLibraryElementsAction(frame.getDocPane()));
				checkForUpdatesItem.setEnabled(false);
				datasetMenu.add(checkForUpdatesItem);
				checkForUpdatesItem.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent event) {
						//Update the menu item for a current DEL connection
						if (DocTreeModel.getInstance().getCheckedOutLSIDs(false, false).size() > 0) {
							checkForUpdatesItem.setEnabled(((MainTabbedPane)docPane).getDelInitializer().isDelConnectionIsInitialised());
						}
					}
				});		
			}
			
			//enable all menu items
			enabledDatasetMenu(true);
			
		} else {
			datasetMenu.add(newDocItem);
			datasetMenu.addSeparator();
			datasetMenu.add(new JMenuItem(new ConfigureUnitsAction(frame)));
			datasetMenu.add(new JMenuItem(new ConfigureTransformersAction(frame)));
			datasetMenu.add(new JMenuItem(new ConfigureValidationRulesAction(frame)));
			datasetMenu.addSeparator();
			datasetMenu.add(new JMenuItem(new ShowValidationDialogAction(frame)));
			datasetMenu.add(new JMenuItem(new ViewScheduleAction(frame, (MainTabbedPane)docPane)));
			datasetMenu.addSeparator();
			datasetMenu.add(new JMenuItem(new ReorderDocumentsAction(frame, false)));
			datasetMenu.add(new JMenuItem(new ConfigureRolesAction(frame)));
			
			if (activeDs.getDs().isPublished()) {
				datasetMenu.add(new JMenuItem(new ConfigureConsentAction(frame, true)));
				randomItem = new JMenuItem(new ConfigureRandomiserAction(frame, true));
				datasetMenu.add(randomItem);
				datasetMenu.add(new JMenuItem(new ConfigureRegisterAction(frame, true)));
				if (activeDs.getDs().isRandomizationRequired()) {
					datasetMenu.add(new JMenuItem(new ConfigureSitesAction(frame, true)));
				} else {
					datasetMenu.add(new JMenuItem(new ConfigureSitesAction(frame)));
				}
			} else {
				datasetMenu.add(new JMenuItem(new ConfigureConsentAction(frame)));
				//use randomItem here so we can disable it properly later (if no randomization set)
				randomItem = new JMenuItem(new ConfigureRandomiserAction(frame));
				datasetMenu.add(randomItem);
				datasetMenu.add(new JMenuItem(new ConfigureRegisterAction(frame)));
				datasetMenu.add(new JMenuItem(new ConfigureSitesAction(frame)));
			}
			
			datasetMenu.add(new JMenuItem(new ConfigureStatusAndStateTransitionsAction(frame)));
			datasetMenu.add(new JMenuItem(new ConfigureDocumentGroupsAction(frame)));
			datasetMenu.add(new JMenuItem(new ConfigureReportsAction(frame)));
			
			if (DELSecurity.getInstance().canSearchLibrary()) {
				datasetMenu.addSeparator();
				final JMenuItem checkForUpdatesItem = new JMenuItem(new UpdateLibraryElementsAction(frame.getDocPane()));
				checkForUpdatesItem.setEnabled(false);
				datasetMenu.add(checkForUpdatesItem);
				checkForUpdatesItem.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent event) {
						//Update the menu item for a current DEL connection
						if (DocTreeModel.getInstance().getCheckedOutLSIDs(false, false).size() > 0) {
							checkForUpdatesItem.setEnabled(((MainTabbedPane)docPane).getDelInitializer().isDelConnectionIsInitialised());
						}
					}
				});		
			}
			//enable all menu items
			enabledDatasetMenu(true);
		}
		
		
	}

	/**
	 * Enable/dislabe the dataset menu items based on flag passed 
	 * as paramet
	 * @param enable true if menu items should be disabled; false if not
	 */
	private void enabledDatasetMenu(boolean enable) {
		for (java.awt.Component comp: datasetMenu.getMenuComponents()) {
			comp.setEnabled(enable);
			
			StudyDataSet activeDs = DatasetController.getInstance().getActiveDs();
			
			//disable randomzier menu if study isn't randomized
			if (comp == randomItem) {
				if (activeDs != null) {
					if (activeDs.getDs().isRandomizationRequired()) {
						randomItem.setEnabled(true);
					} else {
						randomItem.setEnabled(false);
					}
				}
			}
		}
	}
	
	
	/**
	 * Create the menu with DataElementLibrary elements
	 *
	 */
	private void createLibraryMenu() {
		datasetMenu.setText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.library"));

		final boolean delClientInitialized = frame.getDocPane().getDelInitializer().isDelConnectionIsInitialised();

		if (DELSecurity.getInstance().canEditElements()) {
			datasetMenu.add(new JMenuItem(new ConfigureUnitsAction(frame)));
			datasetMenu.add(new JMenuItem(new ConfigureTransformersAction(frame)));
			datasetMenu.add(new JMenuItem(new ConfigureValidationRulesAction(frame, true)));
		}
		else if (DELSecurity.getInstance().canApproveElements()) {
			datasetMenu.add(new JMenuItem(new ConfigureUnitsAction(frame,true)));
			datasetMenu.add(new JMenuItem(new ConfigureTransformersAction(frame, true)));
			datasetMenu.addSeparator();
			final JMenuItem approveRulesItem = new JMenuItem(new ApproveValidationRulesAction(frame));
			datasetMenu.add(approveRulesItem);
			approveRulesItem.setEnabled(delClientInitialized);
			approveRulesItem.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					//Update the menu item for changes to access to the DEL
					approveRulesItem.setEnabled(delClientInitialized && DELSecurity.getInstance().canApproveElements());
				}
			});
		}
		else {
			datasetMenu.add(new JMenuItem(new ConfigureUnitsAction(frame, true)));
			datasetMenu.add(new JMenuItem(new ConfigureTransformersAction(frame, true)));
			datasetMenu.add(new JMenuItem(new ViewValidationRulesAction(frame, true)));
		}

		datasetMenu.addSeparator();

		final JMenuItem searchDELDocumentsItem = new JMenuItem(new SearchDELEntriesAction(frame.getDocPane(), SearchType.Documents));						
		datasetMenu.add(searchDELDocumentsItem);					
		searchDELDocumentsItem.setEnabled(delClientInitialized && DELSecurity.getInstance().canSearchLibrary());
		searchDELDocumentsItem.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				//Update the menu item for changes to access to the DEL
				searchDELDocumentsItem.setEnabled(delClientInitialized && DELSecurity.getInstance().canSearchLibrary());
			}
		});

		final JMenuItem searchDELEntriesItem = new JMenuItem(new SearchDELEntriesAction(frame.getDocPane(), SearchType.Entries));						
		datasetMenu.add(searchDELEntriesItem);					
		searchDELEntriesItem.setEnabled(delClientInitialized);
		searchDELEntriesItem.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				//Update the menu item for changes to access to the DEL
				searchDELEntriesItem.setEnabled(delClientInitialized && DELSecurity.getInstance().canSearchLibrary());
			}
		});

		final JMenuItem searchDELRulesItem = new JMenuItem(new SearchDELEntriesAction(frame.getDocPane(), SearchType.ValidationRules));						
		datasetMenu.add(searchDELRulesItem);					
		searchDELRulesItem.setEnabled(delClientInitialized);
		searchDELRulesItem.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				//Update the menu item for changes to access to the DEL
				searchDELRulesItem.setEnabled(delClientInitialized && DELSecurity.getInstance().canSearchLibrary());
			}
		});

		//User is a curator in at least one authority
		if (DELSecurity.getInstance().canApproveElements()) {
			datasetMenu.addSeparator();
			final JMenuItem searchPendingEntriesItem = new JMenuItem(new SearchPendingDELEntriesAction(frame.getDocPane(), SearchType.All));
			datasetMenu.add(searchPendingEntriesItem);
			searchPendingEntriesItem.setEnabled(delClientInitialized);
			searchPendingEntriesItem.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					//Update the menu item for changes to access to the DEL
					searchPendingEntriesItem.setEnabled(delClientInitialized && DELSecurity.getInstance().canApproveElements());
				}
			});

			final DataSet ds;
			if (DocTreeModel.getInstance().getDELDataset() != null) {
				ds = (DataSet)DocTreeModel.getInstance().getDELDataset().getDs();
			}
			else {
				ds = null;
			}

			final JMenuItem approveDocumentsItem = new JMenuItem(new ApproveElementAction(frame.getDocPane(), ds));
			datasetMenu.add(approveDocumentsItem);
			approveDocumentsItem.setEnabled(delClientInitialized);
			approveDocumentsItem.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					//Update the menu item for changes to access to the DEL
					boolean hasDocs = Utils.hasApprovableDocuments();
					approveDocumentsItem.setEnabled(delClientInitialized && hasDocs && DELSecurity.getInstance().canApproveElements());
				}
			});
		}

		//User is an author in at least one authority
		if (DELSecurity.getInstance().canSubmitElements()) {
			datasetMenu.addSeparator();
			boolean enableSubmitEntriesItem;
			if(delClientInitialized && Utils.hasSubmittableElements()){
				enableSubmitEntriesItem = true;
			}
			else {
				enableSubmitEntriesItem = false;
			}
			final JMenuItem submitEntriesItem = new JMenuItem(new SubmitToElemLibraryAction(frame.getDocPane(), SubmissionType.Documents));
			datasetMenu.add(submitEntriesItem);
			submitEntriesItem.setEnabled(enableSubmitEntriesItem);
			submitEntriesItem.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					//Update the menu item for changes to submittable elements.
					boolean enableSubmitEntriesItem = false;
					if(delClientInitialized && Utils.hasSubmittableElements()){
						enableSubmitEntriesItem = true;
					}
					submitEntriesItem.setEnabled(enableSubmitEntriesItem && DELSecurity.getInstance().canSubmitElements());
				}
			});	

			boolean canSubmitRulesItem;
			if(delClientInitialized && Utils.hasSubmittableValidationRules()){
				canSubmitRulesItem = true;
			}
			else {
				canSubmitRulesItem = false;
			}
			final JMenuItem submitRulesItem = new JMenuItem(new SubmitToElemLibraryAction(frame.getDocPane(), SubmissionType.ValidationRules));
			datasetMenu.add(submitRulesItem);
			submitRulesItem.setEnabled(canSubmitRulesItem);
			submitRulesItem.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					//Update the menu item for changes to submittable elements.
					boolean canSubmitRulesItem = false;
					if(delClientInitialized && Utils.hasSubmittableValidationRules()){
						canSubmitRulesItem = true;
					}
					submitRulesItem.setEnabled(canSubmitRulesItem && DELSecurity.getInstance().canSubmitElements());
				}
			});	
		}

		datasetMenu.addSeparator();
		final JMenuItem checkForUpdatesItem = new JMenuItem(new UpdateLibraryElementsAction(frame.getDocPane()));
		checkForUpdatesItem.setEnabled(delClientInitialized);
		datasetMenu.add(checkForUpdatesItem);
		checkForUpdatesItem.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				//Update the menu item for a current DEL connection
				if (DocTreeModel.getInstance().getCheckedOutLSIDs(false, false).size() > 0) { 
					checkForUpdatesItem.setEnabled(((MainTabbedPane)docPane).getDelInitializer().isDelConnectionIsInitialised());
				}
			}
		});		

	}

	/**
	 * Checks if menu is used in data element library mode
	 * @return true if DEL context is in used; false if not
	 */
	public boolean isDelContext() {
		return delContext;
	}

	/**
	 * Clear existing menu items on recently opened studies
	 * Create the recently-opened studies
	 *
	 */
	private void buildAndAddRecentlyOpenedStudiesItems() {
		
		//remove the last separator before readding it in the correct position
		if (separatorAdded) {
			for (int j=fileMenu.getItemCount()-1; j>0; j--) {
				//remove the last separator
				if (!(fileMenu.getItem(j) instanceof JMenuItem)) {
					fileMenu.remove(j);
					break;
				}
			}
		}
		
		//remove all existing items from the menu
		for (JMenuItem item: recentOpenedStudiesItems) {
			fileMenu.remove(item);
		}
		//remove the exit item/
		fileMenu.remove(exitItem);
		
		for (int i=0; i<RecentStudiesController.getInstance().getStudies().size(); i++) {
			String recentStudy = RecentStudiesController.getInstance().getStudies().get(i);
			//create a new menu item for each one
			JMenuItem recentStudyItem = new JMenuItem(new OpenRecentStudyAction(frame, recentStudy, i+1, formatFileToOpen(recentStudy)));
			recentStudyItem.setToolTipText(recentStudy);
			recentOpenedStudiesItems.add(recentStudyItem);
			fileMenu.add(recentStudyItem);
		}
		
		if (RecentStudiesController.getInstance().getStudies().size() > 0 ) { 
			fileMenu.addSeparator();
			separatorAdded = true;
 		 }
		fileMenu.add(exitItem);
	}
	
	/**
	 * Format the name from this string containing the file to open
	 * @return the formatted name
	 */
	private String formatFileToOpen(String fileToOpen) {
		StringBuffer dsName = new StringBuffer(fileToOpen);
		//remove the **DATABASE** text
		if (fileToOpen.contains(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.repkey"))) {
			dsName.delete(0, dsName.lastIndexOf("*")+1);
			return dsName.toString();
		} else {
			//delete from the start to the end of the last file separator
			if (dsName.lastIndexOf(File.separator) != -1) {
				dsName.delete(0, dsName.lastIndexOf(File.separator)+1);
			}
			return dsName.toString();
		}
	}
	
	/**
	 * Sets the del context 
	 * @param delContext true if the DEL context should be set; false if not
	 */
	public void setDelContext(boolean delContext) {
		boolean oldContext = this.delContext;
		this.delContext = delContext;
		//Reload the menu bar for the new context
		if (oldContext != delContext) {
			if (delContext) {
				//close any open tabs as well
				frame.getDocPane().closeAll();
				datasetMenu.removeAll();
				createLibraryMenu();
			}
			else {
				//close any open tabs as well
				frame.getDocPane().closeAll();
				datasetMenu.removeAll();
				createDatasetMenu();
			}
			
			datasetMenu.revalidate();
		}
	}
	
	private class FileMenuListener implements MenuListener {

		public void menuCanceled(MenuEvent e) {
			//do nothing if menu cancelled
		}

		public void menuDeselected(MenuEvent e) {
			//do nothing if menu deselected
		}

		public void menuSelected(MenuEvent e) {
			StudyDataSet activeDs = DatasetController.getInstance().getActiveDs();
			
			if (!delContext) {
				if (activeDs == null) {
					dsWizardItem.setEnabled(true);
					publishMenuItem.setEnabled(false);
					saveMenuItem.setEnabled(false);
					closeMenuItem.setEnabled(false);
					saveAsMenuItem.setEnabled(false);
					deleteMenuItem.setEnabled(false);
					patchMenuItem.setEnabled(false);
					previewMenuItem.setEnabled(false);
					propertiesMenuItem.setEnabled(false);
					updatePolicyItem.setEnabled(false);
					
				} else {
					//set this active
					dsWizardItem.setEnabled(true);
					closeMenuItem.setEnabled(true);
					saveAsMenuItem.setEnabled(true);
					propertiesMenuItem.setEnabled(true);
					patchMenuItem.setEnabled(false);

					if (activeDs.getLastStoredLocation() == null) {
						saveMenuItem.setEnabled(false);
						deleteMenuItem.setEnabled(false);
					} else {
						//if ds is read only; disable saving and creation of new document
						if (activeDs.isReadOnly()) {
							saveMenuItem.setEnabled(false);
							
						} else {
							
							//can only use save if it has been edited already
							//CHECK THIS BEFORE REPOSITORY PUBLISHED CHECKING
							if (DatasetController.getInstance().getActiveDs().isDirty()) {
								saveMenuItem.setEnabled(true);
							} else {
								saveMenuItem.setEnabled(false);
							}
							
							//indicate clearly on menu item where study will be saved to
							if (activeDs.getLastStoredLocation().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.repkey"))) {
								saveMenuItem.setText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.mainmenubar.menuitem.studysavedtorep"));
								if (DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
									//if it's published and last stored location as the repository
									//then you can't resave it there
									saveMenuItem.setEnabled(false);
									deleteMenuItem.setEnabled(false);
								} else {
									saveMenuItem.setEnabled(true);
									deleteMenuItem.setEnabled(true);
								}
							//it's saved to a file; cannot delete from here
							} else {
								saveMenuItem.setText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.mainmenubar.menuitem.studysavedtofile"));
								deleteMenuItem.setEnabled(false);
							}
						}
					}
					
					//cannot publish if already published or not fully configured
					//can only preview if fully configured
					if (DatasetController.getInstance().getActiveDs().isFullyConfigured() ) {
						previewMenuItem.setEnabled(true);
						if (!DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
							publishMenuItem.setEnabled(true);
							updatePolicyItem.setEnabled(false);
							patchMenuItem.setEnabled(false);
						} else {
							//has been published so we can patch but not publish
							//need to check privileges here - need to assert that the user is
							// 'PM' in this ds and 'Study Patcher' in the SYSTEM project.
							
							boolean canPatch = frame.isCanPatchDataSet();;
							
							patchMenuItem.setEnabled(canPatch); 
							publishMenuItem.setEnabled(false);
							updatePolicyItem.setEnabled(true);
						}
					} else {
						previewMenuItem.setEnabled(false);
						publishMenuItem.setEnabled(false);
						updatePolicyItem.setEnabled(false);
					}
				}
			}else {
				//in DEL case, must use right-click menu
				dsWizardItem.setEnabled(false);
				//make sure close is enabled 
				closeMenuItem.setEnabled(true);
				deleteMenuItem.setEnabled(false);
				patchMenuItem.setEnabled(false);
				publishMenuItem.setEnabled(false);
				updatePolicyItem.setEnabled(false);
				previewMenuItem.setEnabled(false);
				saveAsMenuItem.setEnabled(false);
				saveMenuItem.setEnabled(false);
				propertiesMenuItem.setEnabled(false);
			}
			
			//add the recently opened studies items to the menu
			buildAndAddRecentlyOpenedStudiesItems();
		} 
	}
	
	private class DatasetMenuListener implements MenuListener {

		public void menuCanceled(MenuEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void menuDeselected(MenuEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void menuSelected(MenuEvent e) {
			if (delContext) {
				datasetMenu.setText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.library"));
				datasetMenu.removeAll();
				createLibraryMenu();
			} else {
				datasetMenu.setText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.dataset"));
				datasetMenu.removeAll();
				createDatasetMenu();
				StudyDataSet studySet = DatasetController.getInstance().getActiveDs();

				//checks for new document item
				if (!delContext) {
					if (studySet == null || studySet.isReadOnly()) {
						newDocItem.setEnabled(false);
					} else {
						newDocItem.setEnabled(true);
					}
				}
			}
		}
	}
}
	
