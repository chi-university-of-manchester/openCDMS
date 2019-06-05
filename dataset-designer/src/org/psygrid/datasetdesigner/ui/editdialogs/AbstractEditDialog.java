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
package org.psygrid.datasetdesigner.ui.editdialogs;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.help.CSH;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.custom.ExportSecurityComboBox;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.DummyDocument;
import org.psygrid.datasetdesigner.renderer.OptionListCellRenderer;
import org.psygrid.datasetdesigner.ui.DELInfoPanel;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.ui.MultipleVariableTestPanel;
import org.psygrid.datasetdesigner.ui.OptionDependentPanel;
import org.psygrid.datasetdesigner.ui.TransformersPanel;
import org.psygrid.datasetdesigner.ui.UnitsPanel;
import org.psygrid.datasetdesigner.ui.ValidationPanel;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureEntryPermissionsDialog;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ProvenanceDialog;
import org.psygrid.datasetdesigner.utils.HelpHelper;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.security.RBACAction;

/**
 * Abstract class for configuring entries
 * Contains layout methods and action events
 * @author pwhelan
 */
public abstract class AbstractEditDialog extends JDialog 
implements ActionListener {

	//action buttons
	protected JButton okButton;
	private JButton cancelButton;

	//combo box for status of the entry
	private JComboBox entryStatusBox;

	//the entry itself
	private Entry entry;

	//basic text fields for name, display text, help etc.
	private TextFieldWithStatus nameField;
	private TextFieldWithStatus displayTextField;
	private TextFieldWithStatus permissionsField;
	private TextFieldWithStatus helpField;
	private TextFieldWithStatus labelField;

	protected JButton editPermissionsButton;

	private JComboBox units;

	//combo box to store export security setting
	private ExportSecurityComboBox exportSecurity;

	//font style for the narrative entry
	private JComboBox narrativeStyleBox;

	//check box to disable standard codes for an entry
	private JCheckBox disableStandardCodes;

	//check box to disable drop down display
	private JCheckBox optionDropDownBox;
	
	//combo box to handle new entries added to published datasets
	protected JComboBox publishedEntryHandlingBox;

	//panels for controlling option dependencies, units etc. assignments
	private OptionDependentPanel optPanel;
	private UnitsPanel unitsPanel;
	private DELInfoPanel adminPanel;
	private TransformersPanel transformersPanel;
	private ValidationPanel validationPanel;
	private MultipleVariableTestPanel multiVarTestPanel;

	//used in DEL mode
	protected boolean viewOnly = false;

	protected Document entryContext = null;

	//vector of listeners used to listen for ok event
	private Vector okListeners = new Vector();

	//vector of listeners used ot listen for add/remove events
	private Vector removeEntryListeners = new Vector();

	//if option dependencis should be shown for this type of entry
	private boolean showOptions = true;

	//if basic entry options should be shown
	private boolean showBasicEntryOptions = true;

	//return a checkbox for displaying option codes
	private JCheckBox optionCodesBox;

	//if the entry is editable
	protected boolean isEdit = false;

	//if the entry is a composite entry
	private boolean isComposite = false;

	/**
	 * Specifies whether the document is part of the data element library view.
	 */
	protected boolean isDEL;
	//the string to copy/paste
	private String copyString = null;

	//the calling parent frame - used for ownership
	private MainFrame frame;

	/**
	 * Specifies whether changes have actually been made to the properties of 
	 * the entry.
	 * 
	 * Used in the DEL view to accurately mark entries as revision candidates.
	 */
	protected boolean changed = false;

	/** 
	 * Constructor. Cannot be called from a composite entry
	 * 
	 * @param frame the main frame of the application
	 * @param entry the entry to configure
	 * @param title the title of the abstracteditdialog
	 */
	public AbstractEditDialog(MainFrame frame, BasicEntry entry, String title, boolean isDEL, boolean canEdit) {
		super(frame, title);
		if (!(entry.getName() == null || entry.getName().equals(""))) {
			isEdit = true;
		}
		this.isDEL = isDEL;
		this.viewOnly = !canEdit;
		this.frame = frame;
		setModal(true);
		this.entry = entry;
		this.showOptions = true;
		init();
	}

	public AbstractEditDialog(JDialog parent, Entry entry, String title, Document entryContext, boolean isDEL, boolean canEdit) {
		super(parent, title);
		this.entryContext = entryContext;
		this.viewOnly = !canEdit;
		setModal(true);
		this.entry = entry;
		this.showOptions = true;
		this.isDEL = isDEL;
		init();
	}

	/** 
	 * Constructor 
	 * @param frame the main frame of the application
	 * @param entry the entry to configure
	 * @param showOptions if options should be displayed
	 * @param showBasicEntryOptions show the basic options  
	 * @param title the title of the abstracteditdialog
	 * @param isComposite if the entry is part of a composite
	 */
	public AbstractEditDialog(MainFrame frame,
			Entry entry,
			String title,
			boolean showOptions,
			boolean showBasicEntryOptions,
			boolean isComposite,
			boolean isDEL,
			boolean canEdit) {
		super(frame, title);
		if (!(entry.getName() == null || entry.getName().equals(""))) {
			isEdit = true;
		}

		setModal(true);
		this.frame = frame;
		this.entry = entry;
		this.showOptions = showOptions;
		this.showBasicEntryOptions = showBasicEntryOptions;
		this.isDEL = isDEL;
		this.viewOnly = !canEdit;
		this.isComposite = isComposite;
		init();
		addWindowListener(new CloseWindowListener());

	}

	/** 
	 * Constructor 
	 * @param frame the main frame of the application
	 * @param entry the entry to configure
	 * @param title the title of the abstracteditdialog
	 * @param showOptions if options should be displayed
	 * @param showBasicEntryOptions show the basic options  
	 */
	public AbstractEditDialog(MainFrame frame, 
			Entry entry,
			String title, 
			boolean showOptions, 
			boolean showBasicEntryOptions,
			boolean isDEL,
			boolean canEdit) {
		super(frame, title);

		if (!(entry.getName() == null || entry.getName().equals(""))) {
			isEdit = true;
		}

		setModal(true);
		this.frame = frame;
		this.entry = entry;
		this.showOptions = showOptions;
		this.showBasicEntryOptions = showBasicEntryOptions;
		this.isDEL = isDEL;
		this.viewOnly = !canEdit;
		init();
	}

	/** 
	 * Constructor 
	 * @param parentDialog the parent dialog
	 * @param entry the entry to configure
	 * @param title the title of the abstracteditdialog
	 * @param showOptions if options should be displayed
	 * @param showBasicEntryOptions show the basic options  
	 */
	public AbstractEditDialog(JDialog parentDialog, 
			Entry entry,
			String title, 
			boolean showOptions, 
			boolean showBasicEntryOptions,
			boolean isDEL, boolean canEdit) {
		super(parentDialog, title);
		this.entry = entry;
		this.showOptions = showOptions;
		this.showBasicEntryOptions = showBasicEntryOptions;
		this.isDEL = isDEL;
		this.viewOnly = !canEdit;
		init();
	}

	/** 
	 * Constructor 
	 * @param frame the main frame of the application
	 * @param entry the entry to configure
	 * @param title the title of the abstracteditdialog
	 * @param showOptions if options should be displayed
	 * @param showBasicEntryOptions show the basic options  
	 * @param entryContext the document this entry belongs to
	 */
	public AbstractEditDialog(JDialog parentDialog, 
			Entry entry,
			String title, 
			boolean showOptions, 
			boolean showBasicEntryOptions, 
			Document entryContext,
			boolean isDEL, boolean canEdit) {
		super(parentDialog, title);
		this.viewOnly = true;
		this.entryContext = entryContext;
		this.entry = entry;
		this.showOptions = showOptions;
		this.showBasicEntryOptions = showBasicEntryOptions;
		this.isDEL = isDEL;
		this.viewOnly = !canEdit;
		init();
	}

	/** 
	 * Constructor 
	 * @param frame the main frame of the application
	 * @param entry the entry to configure
	 * @param title the title of the abstracteditdialog
	 * @param showOptions if options should be displayed
	 * @param showBasicEntryOptions show the basic options  
	 * @param entryContext the document this entry belongs to
	 */
	public AbstractEditDialog(MainFrame frame, 
			Entry entry,
			String title, 
			boolean showOptions, 
			boolean showBasicEntryOptions, 
			Document entryContext,
			boolean isDEL, boolean canEdit) {
		super(frame, title);
		this.frame = frame;
		this.viewOnly = true;
		this.entryContext = entryContext;
		this.entry = entry;
		this.showOptions = showOptions;
		this.showBasicEntryOptions = showBasicEntryOptions;
		this.isDEL = isDEL;
		this.viewOnly = !canEdit;
		init();
	}

	/**
	 * Initialise the basic layout and size of this dialog
	 * Then call populate to fill appropriate panels
	 */
	private void init() {
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getTabbedPane(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
		addWindowListener(new CloseWindowListener());
		populate();
	}


	/**
	 * Create the JTabbedPane that contains the units, transformers,
	 * validation rules etc. panels
	 * @return the configured JTabbedPane
	 */
	private JTabbedPane getTabbedPane() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.basic"), getGenericPanel());

		if (entry instanceof BasicEntry) {
			if (showBasicEntryOptions) {

				if (! (entry instanceof DateEntry)
						&& ! (entry instanceof BooleanEntry)) {
					tabbedPane.add(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.units"), getUnitsPanel());
				}
				tabbedPane.add(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.transformers"), getTransformersPanel());

				if (! (entry instanceof BooleanEntry)
						&& !(entry instanceof OptionEntry)) {
					tabbedPane.add(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.validationrules"), getValidationPanel());
				}
			}
		}

		if (showOptions) {
			tabbedPane.add(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.optdependencies"), getOptionDependentPanel());
		}

		if (entry instanceof DerivedEntry) {
			tabbedPane.add(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.testcases"), getMultipleVariableTestPanel());
		}

		if(((org.psygrid.data.model.hibernate.Entry)entry).getLSID() != null){
			//create a wrapper to hold it so that the 
			//empty jpanel is stretcedh intead of the admin info panel on resizing 
			JPanel holderPanel = new JPanel();
			holderPanel.setLayout(new BorderLayout());
			holderPanel.add(getAdminInfoPanel(), BorderLayout.NORTH);
			holderPanel.add(new JPanel(), BorderLayout.CENTER);
			tabbedPane.add(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.admininfo"), holderPanel);
		}

		return tabbedPane;
	}

	/**
	 * Create the checkbox that allows selection/deselection of 
	 * standard code display
	 * @return the configured standard code display checkbox
	 */
	public JCheckBox getOptionCodesBox() {
		if (optionCodesBox == null) {
			optionCodesBox = new JCheckBox();
		}

		return optionCodesBox;
	}

	/**
	 * Create the checkbox that allows selection/deselection of 
	 * drop down display (combo box)
	 * @return the configured  display drop down combo box
	 */
	public JCheckBox getOptionDropDownBox() {
		if (optionDropDownBox == null) {
			optionDropDownBox = new JCheckBox();
		}

		return optionDropDownBox;
	}

	/**
	 * Create the narrative style box
	 * option box for selecting font style
	 * @return the configured font style selection combobox
	 */
	public JComboBox getNarrativeStyleBox() {
		if (narrativeStyleBox == null) {
			narrativeStyleBox = new JComboBox();
			DefaultComboBoxModel styleModel = new DefaultComboBoxModel();
			for (NarrativeStyle style : NarrativeStyle.values())
			{
				styleModel.addElement(style);
			}
			narrativeStyleBox.setModel(styleModel);
		}
		return narrativeStyleBox;
	}

	/**
	 * Get the Units Panel
	 * @return a JPanel containing the units selection panel
	 */
	private JPanel getUnitsPanel() {
		unitsPanel = new UnitsPanel(frame, this.entryContext == null ? DatasetController.getInstance().getActiveDocument() : entryContext, (BasicEntry)entry, viewOnly);
		return unitsPanel;
	}

	/**
	 * Get the Admin Info Panel
	 * @return a JPanel containing admin info for DEL
	 */
	private JPanel getAdminInfoPanel() {
		adminPanel = new DELInfoPanel(new DataElementContainer((Entry)entry));
		return adminPanel;
	}

	/**
	 * Get the Transformers Panel
	 * @return a JPanel containing the transformers selection panel
	 */
	private JPanel getTransformersPanel() {
		transformersPanel = new TransformersPanel(frame, this.entryContext == null ? DatasetController.getInstance().getActiveDocument() : entryContext, (BasicEntry)entry, viewOnly, isDEL);
		return transformersPanel;
	}

	/**
	 * Get the Validation Panel
	 * @return a JPanel containing the validation rules panel
	 */
	private JPanel getValidationPanel() {
		validationPanel = new ValidationPanel(frame, this.entryContext == null ? DatasetController.getInstance().getActiveDocument() : entryContext, (BasicEntry)entry, viewOnly, isDEL);
		return validationPanel;
	}

	private JPanel getMultipleVariableTestPanel() {
		multiVarTestPanel = new MultipleVariableTestPanel(this, (BasicEntry)entry, viewOnly, isDEL);
		return multiVarTestPanel;
	}

	/**
	 * Get the name field
	 * @return a text field for name of entry
	 */
	public TextFieldWithStatus getNameField() {
		nameField.addActionListener(new CSH.DisplayHelpAfterTracking(HelpHelper.getInstance().getHelpBroker()));
		return nameField;
	}

	/**
	 * Get the display text field
	 * @return a text field for display text of entry
	 */
	public TextFieldWithStatus getDisplayTextField() {
		return displayTextField;
	}

	/**
	 * Get the permission level for this entry
	 * @return a text field for display text of entry
	 */
	public TextFieldWithStatus getPermissionsField() {
		return permissionsField;
	}

	/**
	 * Get the help text field
	 * @return a text field for help text of entry
	 */
	public TextFieldWithStatus getHelpField() {
		return helpField;
	}

	/**
	 * Get the label field
	 * @return a label field for the label of entry
	 */
	public TextFieldWithStatus getLabelField() {
		return labelField;
	}

	/**
	 * Add a listener - fired when add/remove is hit
	 * @param al the listener to add
	 */
	public void addRemoveListener(ActionListener al) {
		removeEntryListeners.add(al);
	}

	/**
	 * Remove a listener - fired when add/remove is hit
	 * @param al the listener to remove
	 */
	public void removeRemoveListener(ActionListener al) {
		removeEntryListeners.remove(al);
	}

	/**
	 * Add a listener - fired when ok is hit
	 * @param al the listener to add
	 */
	public void addOKListener(ActionListener al) {
		okListeners.add(al);
	}

	/**
	 * Remove a listener - fired when ok is hit
	 * @param al the listener to remove
	 */
	public void removeOKListener(ActionListener al) {
		okListeners.remove(al);
	}

	/**
	 * Fire an event to all remove listeners
	 */
	public void fireRemoveListenerEvent() {
		Iterator removeIt = removeEntryListeners.iterator();
		while(removeIt.hasNext()) {
			ActionListener al = (ActionListener) removeIt.next();
			al.actionPerformed(new ActionEvent(this, 1, ""));
		}
	}

	/**
	 * Fire an event to all ok listeners
	 */
	public void fireActionEvent() {
		Iterator atIt = okListeners.iterator();

		while(atIt.hasNext()) {
			ActionListener al = (ActionListener)atIt.next();
			al.actionPerformed(new ActionEvent(this, 1, ""));
		}
	}

	/**
	 * Return the basic props panel, containing name, display text etc.
	 * @return the configured basic properties panel
	 */
	public JPanel getGenericPanel() {
		JPanel genericPanel = new JPanel();
		genericPanel.setLayout(new BoxLayout(genericPanel, BoxLayout.Y_AXIS));
		JPanel mainPanel = new JPanel(new SpringLayout());

		RightClickMouseAdatper rightClickAdapter = new RightClickMouseAdatper();

		int rows = 3;

		nameField = new TextFieldWithStatus(20, true);
		nameField.addMouseListener(rightClickAdapter);
		displayTextField = new TextFieldWithStatus(20, true);
		displayTextField.addMouseListener(rightClickAdapter);
		labelField = new TextFieldWithStatus(20, false);
		labelField.addMouseListener(rightClickAdapter);
		permissionsField = new TextFieldWithStatus(33, false);
		permissionsField.setEditable(false);
		helpField = new TextFieldWithStatus(40, false);
		helpField.addMouseListener(rightClickAdapter);
		disableStandardCodes = new JCheckBox();

		if(viewOnly){
			nameField.setEditable(false);
			displayTextField.setEditable(false);
			labelField.setEditable(false);
			helpField.setEditable(false);
			//associateTransformer.setEditable(false);
			disableStandardCodes.setEnabled(false);
		}
		
		if (DatasetController.getInstance().getActiveDs() != null) {
			//disable entry name change if study is published; check on entry name as well
			if (DatasetController.getInstance().getActiveDs().getDs().isPublished() 
					&& (entry != null) 
					&& (entry.getId() != null)) {
				nameField.setEditable(false);
			}
		}
 
		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdentryconfigname"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.name")));
		mainPanel.add(nameField);

		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdentryconfigdisplaytext"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.displaytext")));
		mainPanel.add(displayTextField);

		if (!((Document)DatasetController.getInstance().getActiveDocument() instanceof DummyDocument)) {
			//Individual entries do not have labels (they only make sense within the context of a document)

			if (! (entry instanceof NarrativeEntry)) {
				mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdentryconfignumber"));
				mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.label")));
				mainPanel.add(labelField);	
				rows++;
			}

		}

		if (! (entry instanceof NarrativeEntry)) {
			if (!isComposite) {
				mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdentryconfigentrystatus"));
				mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.entrystatus")));
				mainPanel.add(getEntryStatusComboBox());
				rows ++;
			}

			mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdentryconfighelptext"));
			mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.helptext")));
			mainPanel.add(helpField);

			rows ++;
			
			
			if (!isDEL) {
				mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdentryconfigpermissions"));
				mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.permissionstext")));
				JPanel permissionsPanel = new JPanel(new BorderLayout());
				//permissionsPanel.setLayout(new BoxLayout(permissionsPanel, BoxLayout.X_AXIS));
				permissionsPanel.add(permissionsField, BorderLayout.WEST);
				if (viewOnly) {
					editPermissionsButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.view"));
				}
				else {
					editPermissionsButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.viewedit"));	
				}
				permissionsPanel.add(Box.createRigidArea(new Dimension(0,5)), BorderLayout.CENTER);
				permissionsPanel.add(editPermissionsButton, BorderLayout.EAST);
				editPermissionsButton.addActionListener(this);
				if ((entry.getAccessAction() == null || RBACAction.ACTION_DR_DOC_STANDARD.toString().equals(entry.getAccessAction()))
						&& (entry.getEditableAction() == null || RBACAction.ACTION_DR_DOC_STANDARD.toString().equals(entry.getEditableAction()))) {
					permissionsField.setText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.docpermissionsdefault"));
				}
				else {
					permissionsField.setText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.docpermissionscustom"));
				}
				mainPanel.add(permissionsPanel);
				rows++;
			}
		}
		//if (isDEL) {
		//	mainPanel.add(new JLabel(""));
		//	mainPanel.add(new JLabel(""));
		//}
		//else {
		//	mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.exportsecuritylevel")));
		//	mainPanel.add(getExportSecurityBox());
		//}

		if(this.viewOnly){
			getEntryStatusComboBox().setEnabled(false);
			getExportSecurityBox().setEnabled(false);
		}

		if (entry instanceof  BasicEntry) {
			//can't disable std codes on a boolean
			if (entry instanceof BooleanEntry
					|| entry instanceof DerivedEntry
					|| entry instanceof ExternalDerivedEntry) {
				rows--;
			}	else {
				mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdentryconfigstandardcodes"));
				mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.disablestandardcodes")));
				mainPanel.add(disableStandardCodes);
			}

			if (entry instanceof OptionEntry) {
				mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdentryconfigshowoptioncodes"));
				mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.showoptioncodes")));
				mainPanel.add(getOptionCodesBox());

				mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdentryconfigshowoptiondropdown"));
				mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.optiondropdown")));
				mainPanel.add(getOptionDropDownBox());

				if(viewOnly){
					getOptionCodesBox().setEnabled(false);
					getOptionDropDownBox().setEnabled(false);
				}
				rows += 2;
			}
			SpringUtilities.makeCompactGrid(mainPanel,
					rows, 3, //rows, cols
					6, 6,        //initX, initY
					6, 6);       //xPad, yPad
		} else {
			rows--;

			if (entry instanceof NarrativeEntry) {
				mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdentryconfigfontstyle"));
				mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.narrativestyle")));
				mainPanel.add(getNarrativeStyleBox());

				if(viewOnly){
					this.getNarrativeStyleBox().setEnabled(false);
				}
				rows++;
			}

		}
		
		//if it's a new addition or change to existing element to a published dataset,
		//then the user must select between rejecting entries
		if (DatasetController.getInstance().getActiveDs().getDs().isPublished()) {
			if (!isComposite) {
				publishedEntryHandlingBox = new JComboBox();
				DefaultComboBoxModel entryPublishedModel = new DefaultComboBoxModel();
				entryPublishedModel.addElement(Element.REJECT_ALL_EXISTING_ELEMENTS);
				entryPublishedModel.addElement(Element.ACCEPT_ALL_EXISTING_ELEMENTS);
				publishedEntryHandlingBox.setModel(entryPublishedModel);
				
				mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdentryconfigpatchelement"));
				mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.abstracteditdialog.patchelement")));
				mainPanel.add(publishedEntryHandlingBox);
				rows++;
			}
		}

		SpringUtilities.makeCompactGrid(mainPanel,
				rows, 3, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad
		
		genericPanel.add(mainPanel);
		//dummy panel for space!
		genericPanel.add(new JLabel(""));

		JPanel dummy = new JPanel();
		dummy.setLayout(new BorderLayout());
		JPanel dummyFiller = new JPanel();
		dummy.add(dummyFiller, BorderLayout.CENTER);
		genericPanel.add(dummy);

		return genericPanel;
	}

	/**
	 * Return the option dependent containing list of choosable options 
	 * @return configured option dependent panel
	 */
	public JPanel getOptionDependentPanel() {
		optPanel = new OptionDependentPanel(this.entryContext == null ? DatasetController.getInstance().getActiveDocument() : entryContext, entry, isDEL, viewOnly); 
		return optPanel;
	}

	/**
	 * Save the configured option dependencies for this entry
	 */
	public void saveOptionDepencies() {
		optPanel.saveDependencies();
	}

	/**
	 * Save the configured units for this entry
	 */
	public void saveUnits() {
		unitsPanel.saveUnits();
	}

	/**
	 * Save the configured transformers for this entry
	 */
	public void saveTransformers() {
		transformersPanel.saveTransformers();
	}

	/**
	 * Save the configured validation rules for this entry
	 */
	public void saveValidationRules() {
		validationPanel.saveValidationRules();
	}

	public JComboBox getUnitBox() {
		if (units == null) {
			units = new JComboBox();
			units.setRenderer(new OptionListCellRenderer());
			DefaultComboBoxModel unitsModel = new DefaultComboBoxModel();
			units.setModel(unitsModel);
		}
		return units;
	}

	/**
	 * Return the entry status combo box for this entry
	 * @return the configured entry status combo box
	 */
	public JComboBox getEntryStatusComboBox() {
		if (entryStatusBox == null) {
			entryStatusBox = new JComboBox();
			entryStatusBox.addItem(EntryStatus.MANDATORY);
			entryStatusBox.addItem(EntryStatus.DISABLED);
			entryStatusBox.addItem(EntryStatus.OPTIONAL);
		}

		if(viewOnly)
			entryStatusBox.setEditable(false);

		return entryStatusBox;
	}

	/**
	 * Return the export security combo box for this entry
	 * @return the configured export security combo box
	 */
	public ExportSecurityComboBox getExportSecurityBox() {
		if (exportSecurity == null) {
			exportSecurity = new ExportSecurityComboBox(viewOnly);
		}
		return exportSecurity;
	}

	/**
	 * Return the button panel containing ok, cancel etc
	 * @return the configured button panel
	 */
	public JPanel buildButtonPanel(){
		okButton = new JButton("Ok");
		okButton.addActionListener(this);

		if(!viewOnly){
			cancelButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.cancel"));
		}else{
			cancelButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.done"));
		}

		cancelButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		if(!viewOnly){
			buttonPanel.add(okButton);
		}

		buttonPanel.add(cancelButton);
		return buttonPanel;
	}

	/**
	 * Get the ok button
	 * @return the ok button
	 */
	public  JButton getOkButton() {
		return okButton;
	}

	/**
	 * Get the cancel button
	 * @return the cancel button
	 */
	public JButton getCancelButton() {
		return cancelButton;
	}

	/**
	 * Get the disable standard codes checkbox
	 * @return the checkbox for disabling standard codes
	 */
	public JCheckBox getDisableStandardCodes() {
		return disableStandardCodes;
	}

	/**
	 * Populate the entry with existing settings.
	 */
	public void populate() {
		if (entry != null) {
			nameField.setText(entry.getName());
			displayTextField.setText(entry.getDisplayText());

			if (!(entry instanceof NarrativeEntry)) {
				labelField.setText(entry.getLabel());
				if (entryStatusBox != null) {
					entryStatusBox.setSelectedItem(entry.getEntryStatus());
				}
				helpField.setText(entry.getDescription());
			}
			//if (!isDEL) {
			//	exportSecurity.setSelectedSecurityValue(entry.getExportSecurity());
			//}

			if (entry instanceof  BasicEntry) {
				disableStandardCodes.setSelected(((BasicEntry)entry).isDisableStandardCodes());
			}

			if (entry instanceof NarrativeEntry) {
				getNarrativeStyleBox().setSelectedItem(((NarrativeEntry)entry).getStyle());
			}

			if (entry instanceof OptionEntry) {
				getOptionCodesBox().setSelected(((OptionEntry)entry).isOptionCodesDisplayed());
				getOptionDropDownBox().setSelected(((OptionEntry)entry).isDropDownDisplay());
			} 
			
			//only set this if the dataset is published and not part of a composite; otherwise, it is not added to the panel
			if (DatasetController.getInstance().getActiveDs().getDs().isPublished() && !isComposite)
			{
				publishedEntryHandlingBox.setSelectedItem(entry.getElementPatchingAction());
			}

		}
	}

	/**
	 * Get the relevant entry
	 * @return the current entry
	 */
	public Entry getEntry() {
		return entry;
	}

	/**
	 * Must be implemented by subclasses
	 */
	public abstract void ok();

	/**
	 * Validate the current entries
	 * @return true if entry is validated; false if not
	 */
	public abstract boolean validateEntries(); 

	/**
	 * Validate the basic properties for an entry
	 * @return true if entry is validated; false if not
	 */
	public boolean validateAbstractEntry() {
		if (getNameField().getText().equals("")
				|| getDisplayTextField().getText().equals("")) {
			JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.completenamedisplay"));
			return false;
		} 

		Document doc = DatasetController.getInstance().getActiveDocument();
		for (int i=0; i<doc.numEntries(); i++) {
			Entry curEntry = doc.getEntry(i);
			if (!curEntry.equals(entry)) {
				if (curEntry.getName().equalsIgnoreCase(getNameField().getText())) {
					JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.entrynameexists"));
					return false;
				} else if (curEntry.getDisplayText() != null) {
					if (curEntry.getDisplayText().equalsIgnoreCase(getDisplayTextField().getText())) {
						JOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.entrydisplayexists"));
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * Action Performed
	 * @param aet the calling action
	 */
	public void actionPerformed(ActionEvent aet) {

		if (aet.getSource() == okButton) {

			Entry theEntry = (Entry)entry;
			if (validateAbstractEntry()) {
				if (validateEntries()) {
						ok();

					if (showOptions) {
						optPanel.saveDependencies();
					}
					if (showBasicEntryOptions) {
						if (! (entry instanceof DateEntry)
								&& !(entry instanceof BooleanEntry)) {
							unitsPanel.saveUnits();
						}

						if (! (entry instanceof BooleanEntry) && !(entry instanceof OptionEntry)  ) {
							validationPanel.saveValidationRules();
						}
						
						transformersPanel.saveTransformers();
					}

					if (!isComposite) {
						if (!isEdit) {
							DocTreeModel.getInstance().addEntry(entry, this.entryContext == null ? DatasetController.getInstance().getActiveDocument() : entryContext);
						} else {
							DocTreeModel.getInstance().updateEntry(entry, this.entryContext == null ? DatasetController.getInstance().getActiveDocument() : entryContext);
						}
					}

					if (changed) {
						//If changes have been made then this item should be marked as being revised 
						theEntry.setIsRevisionCandidate(true);
						((Document)DatasetController.getInstance().getActiveDocument()).setIsRevisionCandidate(true);
					}

					fireActionEvent();

					//set the dataset to dirty
					DatasetController.getInstance().getActiveDs().setDirty(true);

					boolean canDispose = this.doExtensionSpecificValidation();
					
					//if it's a published study, every change must be put in the provenance log
					if (DatasetController.getInstance().getActiveDs().getDs().isPublished() && !isComposite) {
						//set handler for existing elements
						if (publishedEntryHandlingBox != null) {
							if(publishedEntryHandlingBox.getSelectedItem() == null || ((String)publishedEntryHandlingBox.getSelectedItem()).equals("")){
								canDispose = false;
							}else{
								entry.setElementPatchingAction((String)publishedEntryHandlingBox.getSelectedItem());
							}
						}else{
							canDispose = false;
						}
						new ProvenanceDialog(this, entry, canDispose);
					//if not published, then dispose of the dialog here
					} else {
						// do the extension check here instead
						if (canDispose) {
							this.dispose();
						}
					}
					
				}
			}
		} else if (aet.getSource() == cancelButton) { 
			fireRemoveListenerEvent();
			this.dispose();
		} else if (aet.getSource() == editPermissionsButton) { 
			new ConfigureEntryPermissionsDialog(this, entry, !isEdit);
		}else{
			dispatchEventToSubClass(aet);
		}
	}


	/**
	 * Handle the case where the dialog is just closed by clicking 
	 * 'x'.
	 */
	private class CloseWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			fireRemoveListenerEvent();
			dispose();
		}
	}

	public boolean fieldChanged(Object oldField, Object newField) {
		if (oldField == null) {
			oldField = "";
		}
		if (newField == null) {
			newField = "";
		}

		return !oldField.equals(newField); 
	}

	public boolean isDEL() {
		return isDEL;
	}

	/**
	 * Right Click Mouse Adapter - Custom Class for 
	 * listening to right clicks for copy/pasting
	 */
	private class RightClickMouseAdatper extends MouseAdapter {

		/**
		 * On mouse-click show the copy/paste menu
		 * @param e the calling MouseEvent
		 */
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				TextFieldWithStatus field = (TextFieldWithStatus)e.getComponent();
				JPopupMenu copyPasteMenu = new JPopupMenu();
				copyPasteMenu.add(new JMenuItem(new CopyAction(field.getText())));

				if (copyString != null) {
					copyPasteMenu.add(new JMenuItem(new PasteAction(field)));
				}

				copyPasteMenu.show(field, e.getPoint().x, e.getPoint().y);
			}
		}

	}

	/**
	 * Action to copy the text
	 * @author pwhelan
	 */
	private class CopyAction extends AbstractAction {

		private String localCopyString;

		/**
		 * Set the selected text to the copy string
		 * @param copyString
		 */
		public CopyAction(String copyString) {
			super("Copy");
			localCopyString = copyString;
		}

		/**
		 * Set the variable copy string to the curretnly selected text
		 * @param evt the calling action event
		 */
		public void actionPerformed(ActionEvent evt) {
			copyString = localCopyString;
		}
	}
	
	/**
	 * This is a method that is to be overridden by an exending class, giving the chance to do
	 * any specific validation. If the returned value is 'false', the dialog will not be dismissed.
	 * @return
	 */
	protected boolean doExtensionSpecificValidation(){
		return true;
	}

	/**
	 * Action to paste the text 
	 * @author pwhelan
	 */
	private class PasteAction extends AbstractAction {
		private TextFieldWithStatus fieldToPaste;

		/**
		 * Set the selected text to the paste string
		 * @param fieldToPaste
		 */
		public PasteAction(TextFieldWithStatus fieldToPaste) {
			super("Paste");
			this.fieldToPaste = fieldToPaste;
		}

		/**
		 * paste the string into the selected textfield
		 * @param evt the calling ActionEvent
		 */
		public void actionPerformed(ActionEvent evt) {
			fieldToPaste.setText(copyString);
		}
	}
	
	public void dispatchEventToSubClass(ActionEvent aet){
		
	}
}
