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
package org.psygrid.datasetdesigner.ui.configurationdialogs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import org.jdesktop.swingx.JXDatePicker;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.NumericValidationRule;
import org.psygrid.data.model.hibernate.ValidationRule;
import org.psygrid.data.model.hibernate.DataElementContainer;
import org.psygrid.data.model.hibernate.DateValidationRule;
import org.psygrid.data.model.hibernate.IntegerValidationRule;
import org.psygrid.data.model.hibernate.TextValidationRule;
import org.psygrid.data.model.hibernate.TimeUnits;
import org.psygrid.datasetdesigner.custom.TextFieldWithStatus;
import org.psygrid.datasetdesigner.ui.DELInfoPanel;
import org.psygrid.datasetdesigner.ui.configurationdialogs.ConfigureValidationRuleDialog.Location;
import org.psygrid.datasetdesigner.utils.ElementUtility;
import org.psygrid.datasetdesigner.utils.HelpHelper;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;

/**
 * Add/Edit a validation rule dialog
 * @author pwhelan
 */
public class AddValidationRuleDialog extends JDialog implements ActionListener, 
ItemListener {

		//action buttons
	private JButton okButton;
	private JButton cancelButton;

	//basic fields for description and message
	private TextFieldWithStatus descriptionField;
	private TextFieldWithStatus messageField;

	//selecting the type of validation rules
	private JComboBox typeBox;

	//specific for text, numeric, integer etc.
	private TextFieldWithStatus lowerLimitField;
	private TextFieldWithStatus upperLimitField;
	private TextFieldWithStatus patternField;
	private TextFieldWithStatus patternDetailsField;

	//the fields for holding minimum and maximum numbers
	private TextFieldWithStatus lowerNumberLimitField;
	private TextFieldWithStatus upperNumberLimitField;

	//the fields for holding minimum and maximum integers
	private TextFieldWithStatus lowerIntegerLimitField;
	private TextFieldWithStatus upperIntegerLimitField;

	//specific for date fields
	private JXDatePicker absLowerLimitPicker;
	private JXDatePicker absUpperLimitPicker;

	//the fields for holding minimum and maximum dates
	private TextFieldWithStatus relativeLowerLimitField;
	private TextFieldWithStatus relativeUpperLimitField;
	private JComboBox relativeUpperLimitUnitsBox;
	private JComboBox relativeLowerLimitUnitsBox;

	//the panel to hold the card layout switching for types of validation rules
	private JPanel cards;

	//map for holding list of configured validation rules
	private  Map<ValidationRule,Location> validationMap;

	//the validation rule to edit
	private ValidationRule validationRule;

	private boolean edit;


	/**
	 * Constructor
	 * @param parentDialog the parent dialog
	 * @param datasetBox the combo box to select datasets
	 * @param validationList the list of existing validations rules
	 * @param validationMap map of datasets with validation rules
	 */
	public AddValidationRuleDialog(JDialog parentDialog, Map<ValidationRule,Location> validationMap) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.addvalidationrule"));
		this.validationMap = validationMap;
		this.edit = true;
		build();
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);
	}

	/**
	 * Constructor
	 * @param parentDialog the parent dialog
	 * @param datasetBox the combo box to select datasets
	 * @param validationList the list of existing validations rules
	 * @param validationMap map of datasets with validation rules
	 * @param validationrule the validation rule to edit
	 */
	public AddValidationRuleDialog(JDialog parentDialog, ValidationRule validationRule, Map<ValidationRule,Location> validationMap) {
		super(parentDialog, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.editvalidationrule"));
		this.validationMap = validationMap;
		this.validationRule = validationRule;
		this.edit = ((ValidationRule)validationRule).getIsEditable();
		build();
		init(validationRule);
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);
	}

	public AddValidationRuleDialog(JDialog parentDialog, Map<ValidationRule,Location> validationMap, ValidationRule validationRule, boolean edit) {
		super(parentDialog, "Validation Rule Properties");
		this.validationMap = validationMap;
		this.validationRule = validationRule;
		this.edit = edit && ((ValidationRule)validationRule).getIsEditable();
		build();
		init(validationRule);
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);
	}

	public AddValidationRuleDialog(JDialog parentDialog, ValidationRule validationRule) {
		super(parentDialog, "Validation Rule Properties");
		this.validationRule = validationRule;
		this.edit = false;
		build();
		init(validationRule);
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);
	}

	private void build() {
		setModal(true);
		//Add a tabbed pane if the validation rule exists and is from the DEL to display the DELInfoPanel
		getContentPane().setLayout(new BorderLayout());

		if (this.validationRule != null 
				&& ((ValidationRule)this.validationRule).getLatestMetaData() != null) {
			//Only DEL objects will have metadata, so display
			JPanel panel = buildCenterPanel();
			DELInfoPanel delPanel = new DELInfoPanel(new DataElementContainer((ValidationRule)validationRule));
			JTabbedPane pane = new JTabbedPane();
			pane.add(panel, "Basic");
			pane.add(delPanel, "Library Info");
			getContentPane().add(pane, BorderLayout.CENTER);
		}
		else {
			getContentPane().add(buildCenterPanel(), BorderLayout.CENTER);
		}

		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
	}

	private void init(ValidationRule validationRule) {

		if (validationRule.getDescription() != null) {
			descriptionField.setText(validationRule.getDescription());
		}

		if (validationRule.getMessage() != null) {
			messageField.setText(validationRule.getMessage());
		}

		//build in some checks for this!
		if (validationRule instanceof TextValidationRule) {

			typeBox.setSelectedItem(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.textvalidationrule"));

			TextValidationRule textValidationRule = (TextValidationRule)validationRule;

			((CardLayout)cards.getLayout()).show(cards, "text");
			typeBox.setSelectedItem(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.textrule"));

			if (textValidationRule.getLowerLimit() != null) {
				lowerLimitField.setText(Integer.toString(textValidationRule.getLowerLimit()));
			}

			if (textValidationRule.getUpperLimit() != null) {
				upperLimitField.setText(Integer.toString(textValidationRule.getUpperLimit()));
			}

			if (textValidationRule.getPattern() != null) {
				patternField.setText(textValidationRule.getPattern());
			}

			if (textValidationRule.getPatternDetails() !=null) {
				patternDetailsField.setText(textValidationRule.getPatternDetails());
			}

		} else if (validationRule instanceof NumericValidationRule) {
			NumericValidationRule numericValidationRule = (NumericValidationRule)validationRule;

			((CardLayout)cards.getLayout()).show(cards, "numeric");
			typeBox.setSelectedItem(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.numericrule"));

			if (numericValidationRule.getLowerLimit() != null) {
				lowerNumberLimitField.setText((Double.toString(numericValidationRule.getLowerLimit())));
			}

			if (numericValidationRule.getUpperLimit() != null) {
				upperNumberLimitField.setText((Double.toString(numericValidationRule.getUpperLimit())));
			}

		} else if (validationRule instanceof IntegerValidationRule) {
			IntegerValidationRule integerRule = (IntegerValidationRule)validationRule;

			((CardLayout)cards.getLayout()).show(cards, "integer");
			typeBox.setSelectedItem(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.integerrule"));

			if (integerRule.getLowerLimit() != null) {
				lowerIntegerLimitField.setText(Integer.toString(integerRule.getLowerLimit()));
			}

			if (integerRule.getUpperLimit() != null) {
				upperIntegerLimitField.setText(Integer.toString(integerRule.getUpperLimit()));
			}

		} else if (validationRule instanceof DateValidationRule) {
			DateValidationRule dateRule = (DateValidationRule)validationRule;
			((CardLayout)cards.getLayout()).show(cards, "date");
			typeBox.setSelectedItem(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.daterule"));

			if (dateRule.getAbsLowerLimit() != null) {
				absLowerLimitPicker.setDate(dateRule.getAbsLowerLimit());
			}

			if (dateRule.getAbsUpperLimit() != null) {
				absUpperLimitPicker.setDate(dateRule.getAbsUpperLimit());
			}

			if (dateRule.getRelLowerLimit() != null) {
				relativeLowerLimitField.setText(dateRule.getRelLowerLimit().toString());
			}

			if (dateRule.getRelUpperLimit() != null) {
				relativeUpperLimitField.setText(dateRule.getRelUpperLimit().toString());
			}

			if (dateRule.getRelLowerLimitUnits() != null) {
				relativeLowerLimitUnitsBox.setSelectedItem(dateRule.getRelLowerLimitUnits());
			}

			if (dateRule.getRelUpperLimitUnits() != null) {
				relativeUpperLimitUnitsBox.setSelectedItem(dateRule.getRelUpperLimitUnits());
			}
		}

		typeBox.setEnabled(false);

		descriptionField.setEditable(edit);
		messageField.setEditable(edit);
		typeBox.setEditable(edit);
		lowerLimitField.setEditable(edit);
		upperLimitField.setEditable(edit);
		patternField.setEditable(edit);
		patternDetailsField.setEditable(edit);
		lowerNumberLimitField.setEditable(edit);
		upperNumberLimitField.setEditable(edit);
		lowerIntegerLimitField.setEditable(edit);
		upperIntegerLimitField.setEditable(edit);
		absLowerLimitPicker.setEditable(edit);
		absUpperLimitPicker.setEditable(edit);
		absLowerLimitPicker.setEnabled(edit);
		absUpperLimitPicker.setEnabled(edit);
		relativeLowerLimitField.setEditable(edit);
		relativeUpperLimitField.setEditable(edit);
		relativeUpperLimitUnitsBox.setEnabled(edit);
		relativeLowerLimitUnitsBox.setEnabled(edit);
	}

	/**
	 * Layout the main panel
	 * @return the configured main panel
	 */
	private JPanel buildCenterPanel() {
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(buildDatasetBoxPanel(), BorderLayout.NORTH);
		centerPanel.add(buildMainPanel(), BorderLayout.CENTER);
		centerPanel.add(buildCardPanel(), BorderLayout.SOUTH);
		return centerPanel;
	}

	private JPanel buildDatasetBoxPanel() {
		JPanel datasetBoxPanel = new JPanel();

		return datasetBoxPanel;
	}

	/**
	 * The main panel containing descriptions, message etc.
	 * @return the configured main panel
	 */
	private JPanel buildMainPanel() {
		JPanel fullPanel = new JPanel();
		fullPanel.setLayout(new BoxLayout(fullPanel, BoxLayout.Y_AXIS));

		JPanel mainPanel = new JPanel(new SpringLayout());

		descriptionField = new TextFieldWithStatus(40, true);
		messageField = new TextFieldWithStatus(20, true);

		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalruledescription"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.description")));
		mainPanel.add(descriptionField);
		mainPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalrulemessage"));
		mainPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.message")));
		mainPanel.add(messageField);

		SpringUtilities.makeCompactGrid(mainPanel,
				2, 3, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		fullPanel.add(mainPanel);
		return fullPanel;
	}

	/**
	 * The card panel - select type of validation rule
	 * @return the configured card panel
	 */
	private JPanel buildCardPanel() {
		JPanel cardPanel = new JPanel();
		cardPanel.setLayout(new BorderLayout());

		JPanel comboPanel = new  JPanel();
		comboPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		typeBox = new JComboBox();
		DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
		comboModel.addElement(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.textrule"));
		comboModel.addElement(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.numericrule"));
		comboModel.addElement(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.integerrule"));
		comboModel.addElement(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.daterule"));
		typeBox.setModel(comboModel);
		comboPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalruletype"));
		comboPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.selectrule")));
		comboPanel.add(typeBox);
		typeBox.addItemListener(this);

		cards = new JPanel(new CardLayout());
		cards.add("text", buildTextPanel());
		cards.add("numeric", buildNumericPanel());
		cards.add("integer", buildIntegerPanel());
		cards.add("date", buildDatePanel());

		cardPanel.add(comboPanel, BorderLayout.NORTH);
		cardPanel.add(cards, BorderLayout.CENTER);

		return cardPanel;
	}

	/**
	 * Build panel to configure the text validation rule
	 * @return the configured text validation rule panel
	 */
	private JPanel buildTextPanel() {
		JPanel textPanel = new JPanel(new SpringLayout());

		lowerLimitField = new TextFieldWithStatus(20, false);
		upperLimitField = new TextFieldWithStatus(20, false);
		patternField = new TextFieldWithStatus(50, false);
		patternDetailsField = new TextFieldWithStatus(50, false);

		textPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalruletextlowerlimit"));
		textPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.lowerlimit")));
		textPanel.add(lowerLimitField);
		textPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalruletextupperlimit"));
		textPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.upperlimit")));
		textPanel.add(upperLimitField);
		textPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalruletextpattern"));
		textPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.pattern")));
		textPanel.add(patternField);
		textPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalruletextpatterndetails"));
		textPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.patterndetails")));
		textPanel.add(patternDetailsField);

		JLabel dummyLabelA = new JLabel(" ");
		dummyLabelA.setPreferredSize(lowerLimitField.getPreferredSize());
		JLabel dummyLabelB = new JLabel(" ");
		dummyLabelB.setPreferredSize(upperLimitField.getPreferredSize());
		textPanel.add(new JLabel(" "));
		textPanel.add(new JLabel(" "));
		textPanel.add(dummyLabelA);
		textPanel.add(new JLabel(" "));
		textPanel.add(new JLabel(" "));
		textPanel.add(dummyLabelB);

		SpringUtilities.makeCompactGrid(textPanel,
				6, 3, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		return textPanel;
	}

	/**
	 * Configure the panel to configure a numeric validation rule
	 * @return the configured numeric validation rule edit panel
	 */
	private JPanel buildNumericPanel() {
		JPanel numericPanel = new JPanel(new SpringLayout());

		lowerNumberLimitField = new TextFieldWithStatus(20, false);
		upperNumberLimitField = new TextFieldWithStatus(20, false);

		numericPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalrulenumberlowerlimit"));
		numericPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.lowerlimit")));
		numericPanel.add(lowerNumberLimitField);
		numericPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalrulenumberupperlimit"));
		numericPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.upperlimit")));
		numericPanel.add(upperNumberLimitField);

		JLabel dummyLabelA = new JLabel(" ");
		dummyLabelA.setPreferredSize(lowerNumberLimitField.getPreferredSize());
		JLabel dummyLabelB = new JLabel(" ");
		dummyLabelB.setPreferredSize(upperNumberLimitField.getPreferredSize());

		numericPanel.add(new JLabel(" "));
		numericPanel.add(new JLabel(" "));
		numericPanel.add(dummyLabelA);
		numericPanel.add(new JLabel(" "));
		numericPanel.add(new JLabel(" "));
		numericPanel.add(dummyLabelB);

		numericPanel.add(new JLabel(""));
		numericPanel.add(new JLabel(" "));
		JLabel dummyLabelC = new JLabel(" ");
		dummyLabelC.setPreferredSize(lowerNumberLimitField.getPreferredSize());
		numericPanel.add(dummyLabelC);

		numericPanel.add(new JLabel(""));
		numericPanel.add(new JLabel(" "));
		JLabel dummyLabelD = new JLabel(" ");
		dummyLabelD.setPreferredSize(lowerNumberLimitField.getPreferredSize());
		numericPanel.add(dummyLabelD);

		SpringUtilities.makeCompactGrid(numericPanel,
				6, 3, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		return numericPanel;
	}

	/**
	 * Configure the panel to configure an integer validation rule
	 * @return the configured integer validation rule edit panel
	 */
	private JPanel buildIntegerPanel() {
		JPanel integerPanel = new JPanel(new SpringLayout());

		lowerIntegerLimitField = new TextFieldWithStatus(20, false);
		upperIntegerLimitField = new TextFieldWithStatus(20, false);
		integerPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalruleintegerlowerlimit"));
		integerPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.lowerlimit")));
		integerPanel.add(lowerIntegerLimitField);
		integerPanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalruleintegerupperlimit"));
		integerPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.upperlimit")));
		integerPanel.add(upperIntegerLimitField);


		JLabel dummyLabelA = new JLabel(" ");
		dummyLabelA.setPreferredSize(lowerIntegerLimitField.getPreferredSize());
		JLabel dummyLabelB = new JLabel(" ");
		dummyLabelB.setPreferredSize(upperIntegerLimitField.getPreferredSize());

		integerPanel.add(new JLabel(" "));
		integerPanel.add(new JLabel(" "));
		integerPanel.add(dummyLabelA);
		integerPanel.add(new JLabel(" "));
		integerPanel.add(new JLabel(" "));
		integerPanel.add(dummyLabelB);

		integerPanel.add(new JLabel(""));
		integerPanel.add(new JLabel(""));
		JLabel dummyLabelC = new JLabel(" ");
		dummyLabelC.setPreferredSize(lowerIntegerLimitField.getPreferredSize());
		integerPanel.add(dummyLabelC);

		integerPanel.add(new JLabel(""));
		integerPanel.add(new JLabel(""));
		JLabel dummyLabelD = new JLabel(" ");
		dummyLabelD.setPreferredSize(lowerIntegerLimitField.getPreferredSize());
		integerPanel.add(dummyLabelD);


		SpringUtilities.makeCompactGrid(integerPanel,
				6, 3, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		return integerPanel;
	}

	/**
	 * Configure the panel to configure a date validation rule
	 * @return the configured date validation rule edit panel
	 */
	private JPanel buildDatePanel() {
		JPanel datePanel = new JPanel(new SpringLayout());

		absLowerLimitPicker = new JXDatePicker();

		java.text.DateFormat[] formats = new java.text.DateFormat[1];
		formats[0] = SimpleDateFormat.getDateInstance(java.text.DateFormat.SHORT); 
		absLowerLimitPicker.setFormats(formats);
		absLowerLimitPicker.setDate(null);

		absUpperLimitPicker = new JXDatePicker();
		absUpperLimitPicker.setFormats(formats);
		absUpperLimitPicker.setDate(null);

		relativeUpperLimitField = new TextFieldWithStatus(20, false);
		relativeLowerLimitField = new TextFieldWithStatus(20, false);

		relativeLowerLimitUnitsBox = new JComboBox();

		relativeUpperLimitUnitsBox = new JComboBox();

		DefaultComboBoxModel timeModel = new DefaultComboBoxModel();
		for (TimeUnits unit: TimeUnits.values()) {
			timeModel.addElement(unit);
		}
		relativeLowerLimitUnitsBox.setModel(timeModel);

		DefaultComboBoxModel unitsModel = new DefaultComboBoxModel();
		for (TimeUnits unit: TimeUnits.values()) {
			unitsModel.addElement(unit);
		}
		relativeUpperLimitUnitsBox.setModel(unitsModel);

		datePanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalruledateabslowerlimit"));
		datePanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.abslowerlimit")));
		datePanel.add(absLowerLimitPicker);

		datePanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalruleabsupperlimit"));
		datePanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.absupperlimit")));
		datePanel.add(absUpperLimitPicker);

		datePanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalruledaterellowerlimit"));
		datePanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.rellowerlimit")));
		datePanel.add(relativeLowerLimitField);

		datePanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalruledaterellowerlimitunits"));
		datePanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.rellowerlimitunits")));
		datePanel.add(relativeLowerLimitUnitsBox);

		datePanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalruledaterelupperlimit"));
		datePanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.relupperlimit")));
		datePanel.add(relativeUpperLimitField);

		datePanel.add(HelpHelper.getInstance().getHelpButtonWithID("dsdvalruledaterelupperlimitunits"));
		datePanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.relupperlimitunits")));
		datePanel.add(relativeUpperLimitUnitsBox);

		SpringUtilities.makeCompactGrid(datePanel,
				6, 3, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		return datePanel;
	}

	/**
	 * Util method to layout a panel with a button and list
	 * @param labelString the label header
	 * @param button the button
	 * @param list the list
	 * @return the configured jpanel
	 */
	public JComponent createSubPanel(String labelString, JButton button, JComponent list)
	{
		JPanel subPanel = new JPanel();
		subPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		subPanel.setLayout(new BorderLayout());
		subPanel.add(createLabelPanel(labelString, list, button), BorderLayout.NORTH);
		JScrollPane scroller = new JScrollPane(list);
		scroller.setPreferredSize(new Dimension(250, 200));
		subPanel.add(scroller, BorderLayout.CENTER);
		return subPanel;
	}

	/**
	 * Create the panel containing the arrows for assigning/removing options
	 * @param rightButton
	 * @param leftButton
	 * @return the configured JPanel
	 */
	public JPanel createArrowPanel(JButton rightButton, JButton leftButton)
	{
		JPanel arrowPanel = new JPanel();
		arrowPanel.setLayout(new BoxLayout(arrowPanel, BoxLayout.Y_AXIS));
		arrowPanel.add(leftButton);
		arrowPanel.add(Box.createVerticalStrut(6));
		arrowPanel.add(rightButton);
		return arrowPanel;
	}

	/**
	 * Creates the header panel for the listbox seen in multiple wizard components.
	 * @param labelString
	 * @param list
	 * @param assignButton
	 * @return the correctly layed out JPanel
	 */
	public JPanel createLabelPanel(String labelString, JComponent list, JButton assignButton)
	{
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BorderLayout());
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		labelPanel.add(new JLabel(labelString), BorderLayout.WEST);
		return labelPanel;
	}

	/**
	 * The ok/cancel button panel
	 * @return the configured ok/cancel button Panel
	 */
	public JPanel buildButtonPanel(){
		if (edit) {
			okButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ok"));
			okButton.addActionListener(this);
			cancelButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.cancel"));
		}
		else {
			cancelButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.close"));	
		}
		cancelButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		if (edit) {
			buttonPanel.add(okButton);
		}
		buttonPanel.add(cancelButton);
		return buttonPanel;
	}

	/**
	 * Validation method
	 * @return true if responses are validated; false if not
	 */
	public boolean validateEntries() {
		if (descriptionField.getText() == null || descriptionField.getText().equals("")) {
			WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.descriptionnotempty"));
			return false;
		}

		if (typeBox.getSelectedItem().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.textrule"))) {

			boolean foundLower = false;
			Integer lower = null;
			if (!lowerLimitField.getText().equals("")) {
				try {
					lower = new Integer(lowerLimitField.getText());
					foundLower = true;
				} catch (NumberFormatException nex) {
					WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.lowerlimitvalidinteger"));
					return false;
				}
			}

			boolean foundUpper = false;
			Integer upper = null;
			if (!upperLimitField.getText().equals("")) {
				try {
					upper = new Integer(upperLimitField.getText());
					foundUpper = true;
				} catch (NumberFormatException ne) {
					WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.upperlimitvalidinteger"));
					return false;
				}
			}

			if (foundLower && foundUpper) {
				if (upper < lower) {
					WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.higherupperlimit"));										
					return false;
				}
			}

		} else if (typeBox.getSelectedItem().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.numericrule"))) {

			boolean foundLower = false;
			Double lower = null;
			if (!lowerNumberLimitField.getText().equals("")) {
				try {
					lower = new Double(lowerNumberLimitField.getText());
					foundLower = true;
				} catch (NumberFormatException nex) {
					WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.lowerlimitvaliddouble"));
					return false;
				}
			}

			boolean foundUpper = false;
			Double upper = null;
			if (!upperNumberLimitField.getText().equals("")) {
				try {
					upper = new Double(upperNumberLimitField.getText());
					foundUpper = true;
				} catch (NumberFormatException ne) {
					WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.upperlimitvaliddouble"));
					return false;
				}
			}

			if (foundLower && foundUpper) {
				if (upper < lower) {
					WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.higheruppernumericlimit"));									
					return false;
				}
			}

		} else if (typeBox.getSelectedItem().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.daterule"))) {

			boolean foundLower = false;
			Integer lowerInt = null;
			if (!(relativeLowerLimitField.getText().equals("")) ) {
				try {
					lowerInt = new Integer(relativeLowerLimitField.getText());
					foundLower = true;
				} catch (NumberFormatException ne) {
					WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.rellowerlimitvaliddouble"));									
					return false;
				}
			}

			boolean foundUpper = false;
			Integer upperInt = null;
			if (!relativeUpperLimitField.getText().equals("")) {
				try {
					upperInt = new Integer(relativeUpperLimitField.getText());
					foundUpper = true;
				} catch (NumberFormatException ne) {
					WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.relupperlimitvalidinteger"));									
					return false;
				}
			}

			if (foundLower && foundUpper) {
				if (upperInt < lowerInt) {
					WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.higheruppernumericlimit"));										
					return false;
				}
			}

			Date lowerDate = null;
			boolean foundLowerAbs = false;
			if (absLowerLimitPicker.getDate() != null) {
				lowerDate = absLowerLimitPicker.getDate();
				foundLowerAbs = true;
			}

			Date upperDate = null;
			boolean foundUpperAbs = false;
			if (absUpperLimitPicker.getDate() != null) {
				upperDate = absUpperLimitPicker.getDate();
				foundUpperAbs = true;
			}

			if (foundLowerAbs && foundUpperAbs) {
				if (upperDate.before(lowerDate)) {
					WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.higherupperdatelimit"));									
					return false;
				}
			}

		} else if (typeBox.getSelectedItem().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.integerrule"))) {
			boolean foundLower = false;
			Integer lower = null;
			if (!(lowerIntegerLimitField.getText().equals("")) ) {
				try {
					lower = new Integer(lowerIntegerLimitField.getText());
					foundLower = true;
				} catch (NumberFormatException ne) {
					WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.lowerlimitvalidinteger"));									
					return false;
				}
			}

			boolean foundUpper = false;
			Integer upper = null;
			if (!(upperIntegerLimitField.getText().equals("")) ) {
				try {
					upper = new Integer(upperIntegerLimitField.getText());
					foundUpper = true;
				} catch (NumberFormatException ne) {
					WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.upperlimitvalidinteger"));									
					return false;
				}
			}

			if (foundLower && foundUpper) {
				if (upper < lower) {
					WrappedJOptionPane.showMessageDialog(this, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.higherupperintegerlimit"));									
					return false;
				}
			}

		}

		return true;
	}

	private boolean fieldChanged(Object one, Object two) {
		if (one == null && two == null) {
			return false;
		}
		if ((one == null && two != null)
				|| (one != null && two == null)
				|| !(one.equals(two))) {
			return true;
		}
		return false;
	}

	/**
	 * ActionEvent 
	 * @param aet the calling event
	 */
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			if (validateEntries()) {
				boolean changed = false;
				try {
					if (fieldChanged(validationRule.getMessage(),messageField.getText())) {
						changed = true;
						validationRule.setMessage(messageField.getText());	
					}

					if (fieldChanged(validationRule.getDescription(),descriptionField.getText())) {
						changed = true;
						validationRule.setDescription(descriptionField.getText());	
					}


					if (validationRule instanceof TextValidationRule) {
						Integer lowerLimit = null;
						Integer upperLimit = null;

						try {
							lowerLimit = new Integer(lowerLimitField.getText());
						} catch (NumberFormatException nex) {
							//maybe do something with this later
						}

						try {
							upperLimit = new Integer(upperLimitField.getText());
						} catch (NumberFormatException ne) {
							//maybe do something with this later					
						}

						if (fieldChanged(lowerLimit, ((TextValidationRule)validationRule).getLowerLimit())) {
							changed = true;
							((TextValidationRule)validationRule).setLowerLimit(lowerLimit);	
						}
						if (fieldChanged(upperLimit, ((TextValidationRule)validationRule).getUpperLimit())) {
							changed = true;
							((TextValidationRule)validationRule).setUpperLimit(upperLimit);	
						}

						if (fieldChanged(patternField.getText(), ((TextValidationRule)validationRule).getPattern())) {
							changed = true;
							((TextValidationRule)validationRule).setPattern(patternField.getText());	
						}

						if (fieldChanged(patternDetailsField.getText(), ((TextValidationRule)validationRule).getPatternDetails())) {
							changed = true;
							((TextValidationRule)validationRule).setPatternDetails(patternDetailsField.getText());	
						}

					} else if (validationRule instanceof NumericValidationRule)  {
						Double lowerLimitDouble = null;
						Double upperLimitDouble = null;

						try {
							lowerLimitDouble = new Double(lowerNumberLimitField.getText());
						} catch (NumberFormatException nex) {
							//maybe do something with this later
						}

						try {
							upperLimitDouble = new Double(upperNumberLimitField.getText());
						} catch (NumberFormatException ne) {
							//maybe do something with this later					
						}

						if (fieldChanged(lowerLimitDouble,((NumericValidationRule)validationRule).getLowerLimit())) {
							changed = true;
							((NumericValidationRule)validationRule).setLowerLimit(lowerLimitDouble);	
						}
						if (fieldChanged(upperLimitDouble,((NumericValidationRule)validationRule).getUpperLimit())) {
							changed = true;
							((NumericValidationRule)validationRule).setUpperLimit(upperLimitDouble);	
						}

					} else if (validationRule instanceof IntegerValidationRule) {
						Integer lowerIntLimit = null;
						Integer upperIntLimit = null;

						try {
							lowerIntLimit = new Integer(lowerIntegerLimitField.getText());
						} catch (NumberFormatException nex) {
						}

						try {
							upperIntLimit = new Integer(upperIntegerLimitField.getText());
						} catch (NumberFormatException ne) {
						}

						if (fieldChanged(lowerIntLimit, ((IntegerValidationRule)validationRule).getLowerLimit())) {
							changed = true;
							((IntegerValidationRule)validationRule).setLowerLimit(lowerIntLimit);	
						}
						if (fieldChanged(upperIntLimit, ((IntegerValidationRule)validationRule).getUpperLimit())) {
							changed = true;
							((IntegerValidationRule)validationRule).setUpperLimit(upperIntLimit);	
						}

					} else if (validationRule instanceof DateValidationRule) {
						Date absLowerLimit = absLowerLimitPicker.getDate();
						Date absUpperLimit = absUpperLimitPicker.getDate();

						Integer relativeLowerLimit = null;
						Integer relativeUpperLimit = null;

						TimeUnits relativeUpperLimitUnits = (TimeUnits)relativeUpperLimitUnitsBox.getSelectedItem();
						TimeUnits relativeLowerLimitUnits = (TimeUnits)relativeLowerLimitUnitsBox.getSelectedItem();

						try {
							relativeLowerLimit = new Integer(relativeLowerLimitField.getText());
						} catch (NumberFormatException ne) {
							//maybe do something with this later					
						}

						try {
							relativeUpperLimit = new Integer(relativeUpperLimitField.getText());
						} catch (NumberFormatException ne) {
							//maybe do something with this later					
						}

						if (fieldChanged(absLowerLimit, ((DateValidationRule)validationRule).getAbsLowerLimit())) {
							changed = true;
							((DateValidationRule)validationRule).setAbsLowerLimit(absLowerLimit);	
						}
						if (fieldChanged(absUpperLimit, ((DateValidationRule)validationRule).getAbsUpperLimit())) {
							changed = true;
							((DateValidationRule)validationRule).setAbsUpperLimit(absUpperLimit);
						}


						if (fieldChanged(relativeLowerLimit, ((DateValidationRule)validationRule).getRelLowerLimit())) {
							changed = true;
							if (relativeLowerLimitField.getText().equals("")) {
								((DateValidationRule)validationRule).setRelLowerLimit(null);
							}
							else {
								((DateValidationRule)validationRule).setRelLowerLimit(relativeLowerLimit);
							}
						}
						if (fieldChanged(relativeLowerLimitUnits, ((DateValidationRule)validationRule).getRelLowerLimitUnits())) {
							changed = true;
							((DateValidationRule)validationRule).setRelLowerLimitUnits(relativeLowerLimitUnits);
						}

						if (fieldChanged(relativeUpperLimit, ((DateValidationRule)validationRule).getRelUpperLimit())) {
							changed = true;
							if (relativeUpperLimitField.getText().equals("")) {
								((DateValidationRule)validationRule).setRelUpperLimit(null);
							}
							else {
								((DateValidationRule)validationRule).setRelUpperLimit(relativeUpperLimit);
							}
						}
						if (fieldChanged(relativeUpperLimitUnits, ((DateValidationRule)validationRule).getRelUpperLimitUnits())) {
							changed = true;
							((DateValidationRule)validationRule).setRelUpperLimitUnits(relativeUpperLimitUnits);
						}
					}

					if (!changed) {
						this.dispose();
						return;
					}

					//Readd the validation rule to reflect updated status
					validationMap.put(validationRule, Location.Edited);

				} catch (Exception ex) {
					//Exception encountered, must be a new rule..
					Integer lowerLimit = null;
					Integer upperLimit = null;

					try {
						lowerLimit = new Integer(lowerLimitField.getText());
					} catch (NumberFormatException nex) {
						//maybe do something with this later
					}

					try {
						upperLimit = new Integer(upperLimitField.getText());
					} catch (NumberFormatException ne) {
						//maybe do something with this later					
					}

					if (typeBox.getSelectedItem().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.textrule"))) {
						TextValidationRule rule = ElementUtility.createITextValidationRule(messageField.getText(),
								descriptionField.getText(),
								lowerLimit,
								upperLimit,
								patternField.getText(),
								patternDetailsField.getText());

						validationMap.put(rule, Location.New);
						((ValidationRule)rule).setIsRevisionCandidate(true);
					} else if (typeBox.getSelectedItem().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.numericrule"))) {

						Double lowerLimitDouble = null;
						Double upperLimitDouble = null;

						try {
							lowerLimitDouble = new Double(lowerNumberLimitField.getText());
						} catch (NumberFormatException nex) {
							//maybe do something with this later
						}

						try {
							upperLimitDouble = new Double(upperNumberLimitField.getText());
						} catch (NumberFormatException ne) {
							//maybe do something with this later					
						}


						NumericValidationRule rule = ElementUtility.createINumericValidationRule(messageField.getText(), 
								descriptionField.getText(), 
								lowerLimitDouble,
								upperLimitDouble);
						validationMap.put(rule, Location.New);
						((ValidationRule)rule).setIsRevisionCandidate(true);
					} else if (typeBox.getSelectedItem().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.integerrule"))) {
						Integer lowerIntLimit = null;
						Integer upperIntLimit = null;
						try {
							lowerIntLimit = new Integer(lowerIntegerLimitField.getText());
						} catch (NumberFormatException nex) {
							//maybe do something with this later
						}

						try {
							upperIntLimit = new Integer(upperIntegerLimitField.getText());
						} catch (NumberFormatException ne) {
							//maybe do something with this later					
						}


						IntegerValidationRule rule = ElementUtility.createIIntegerValidationRule(messageField.getText(), 
								descriptionField.getText(), 
								lowerIntLimit,
								upperIntLimit);
						validationMap.put(rule, Location.New);
						((ValidationRule)rule).setIsRevisionCandidate(true);
					} else if (typeBox.getSelectedItem().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.daterule"))) {
						Date absLowerLimit = absLowerLimitPicker.getDate();
						Date absUpperLimit = absUpperLimitPicker.getDate();

						Integer relativeLowerLimit = null;
						Integer relativeUpperLimit = null;

						TimeUnits relativeUpperLimitUnits = (TimeUnits)relativeUpperLimitUnitsBox.getSelectedItem();
						TimeUnits relativeLowerLimitUnits = (TimeUnits)relativeLowerLimitUnitsBox.getSelectedItem();

						absLowerLimit = absLowerLimitPicker.getDate();
						absUpperLimit = absUpperLimitPicker.getDate();

						try {
							relativeLowerLimit = new Integer(relativeLowerLimitField.getText());
						} catch (NumberFormatException ne) {
							//maybe do something with this later					
						}

						try {
							relativeUpperLimit = new Integer(relativeUpperLimitField.getText());
						} catch (NumberFormatException ne) {
							//maybe do something with this later					
						}

						DateValidationRule rule = ElementUtility.createIDateValidationRule(messageField.getText(), 
								descriptionField.getText(),
								absLowerLimit,
								absUpperLimit,
								relativeLowerLimit,
								relativeUpperLimit,
								relativeLowerLimitUnits,
								relativeUpperLimitUnits
						);
						validationMap.put(rule, Location.New);
						((ValidationRule)rule).setIsRevisionCandidate(true);
					}
				}

				/*
				 * Update the status of the edited validation rule
				 */
				if (validationRule != null) {
					((ValidationRule)validationRule).setIsRevisionCandidate(true);

				}

				((ConfigureValidationRuleDialog)getParent()).refreshTable();
				this.dispose();
			}
		}
		else if (aet.getSource() == cancelButton) {
			this.dispose();
		}
	}

	/**
	 * Switch the panels based of type of validation rule selected
	 * @param e the calling event
	 */
	public void itemStateChanged(ItemEvent e) {
		if (e.getItem().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.textrule"))) {
			((CardLayout)cards.getLayout()).show(cards, "text");
		}
		if (e.getItem().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.numericrule"))) {
			((CardLayout)cards.getLayout()).show(cards, "numeric");
		}
		if (e.getItem().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.integerrule"))) {
			((CardLayout)cards.getLayout()).show(cards, "integer");
		}

		if (e.getItem().equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.daterule"))) {
			((CardLayout)cards.getLayout()).show(cards, "date");
		}
	}

}