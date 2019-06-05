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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.EslCustomField;
import org.psygrid.esl.model.ICustomValue;
import org.psygrid.esl.model.ISubject;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Rob Harper
 *
 */
public abstract class AbstractEslPersonPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public class JWideComboBox extends JComboBox{
		 /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public JWideComboBox() { 
		    } 
		 
		    public JWideComboBox(final Object items[]){ 
		        super(items); 
		    } 
		 
		    public JWideComboBox(Vector items) { 
		        super(items); 
		    } 
		 
		    public JWideComboBox(ComboBoxModel aModel) { 
		        super(aModel); 
		    } 
		 
		    private boolean layingOut = false; 
		 
		    public void doLayout(){ 
		        try{ 
		            layingOut = true; 
		            super.doLayout(); 
		        }finally{ 
		            layingOut = false; 
		        } 
		    } 
		 
		    public Dimension getSize(){ 
		        Dimension dim = super.getSize(); 
		        if(!layingOut) 
		            dim.width = Math.max(dim.width, getPreferredSize().width); 
		        return dim; 
		    } 
		
	}

	private boolean readOnly;
	private DataSet dataSet;
	private static final String ADDRESS2_PROJECT_CODE = "ADD2";
	
    private JTextField titleText;
    private JTextField forenameText;
    private JTextField surnameText;
    private JComboBox sexCombo;
    private BasicDatePicker dobPicker;
    private JTextField address1Text;
    private JTextField address2Text;
    private JTextField address3Text;
    private JTextField cityText;
    private JTextField regionText;
    private JTextField countryText;
    private JTextField postcodeText;
    private JTextField emailAddress;
    private JTextField homePhoneText;
    private JTextField workPhoneText;
    private JTextField mobilePhoneText;
    private JTextField nhsNumberText;
    private JTextField hospitalNumberText;
    private JTextArea riskIssuesText;
    private Map<String, JComboBox> customCombos = new LinkedHashMap<String, JComboBox>();

    public AbstractEslPersonPanel(boolean readOnly, DataSet dataSet){
    	this.readOnly = readOnly;
    	this.dataSet = dataSet;
    }
    
    public JTextField getAddress1Text() {
		return address1Text;
	}

	public JTextField getAddress2Text() {
		return address2Text;
	}

	public JTextField getAddress3Text() {
		return address3Text;
	}

	public JTextField getCityText() {
		return cityText;
	}

	public JTextField getCountryText() {
		return countryText;
	}

	public BasicDatePicker getDobPicker() {
		return dobPicker;
	}

	public JTextField getEmailAddress() {
		return emailAddress;
	}

	public JTextField getForenameText() {
		return forenameText;
	}

	public JTextField getHomePhoneText() {
		return homePhoneText;
	}

	public JTextField getHospitalNumberText() {
		return hospitalNumberText;
	}

	public JTextField getMobilePhoneText() {
		return mobilePhoneText;
	}

	public JTextField getNhsNumberText() {
		return nhsNumberText;
	}

	public JTextField getPostcodeText() {
		return postcodeText;
	}

	public JTextField getRegionText() {
		return regionText;
	}

	public JTextArea getRiskIssuesText() {
		return riskIssuesText;
	}

	public JComboBox getSexCombo() {
		return sexCombo;
	}

	public JTextField getSurnameText() {
		return surnameText;
	}

	public JTextField getTitleText() {
		return titleText;
	}

	public JTextField getWorkPhoneText() {
		return workPhoneText;
	}

	public Map<String, JComboBox> getCustomCombos() {
		return customCombos;
	}

	protected JPanel initPersonalDetailsPanel(){
    	return initPersonalDetailsPanel(null, null, null);
    }

    protected JPanel initPersonalDetailsPanel(String title, String firstName, String lastName){
    	JPanel personalDetailsPanel = new JPanel();
    	DefaultFormBuilder myBuilder = new DefaultFormBuilder(
    			new FormLayout("60dlu, 2dlu, 75dlu, 10dlu, 60dlu, 2dlu, 75dlu, 10dlu, 60dlu, 2dlu, 75dlu, 2dlu"), personalDetailsPanel);
    	myBuilder.setBorder(new TitledBorder(Messages.getString("AbstractEslPersonalPanel.personalDetailsTitle")));
    	
        //Title
        JLabel titleLabel = new JLabel(Messages.getString("AbstractEslPersonPanel.titleLabel"));
        titleText = new JTextField(title);
        titleText.setEditable(!readOnly);
        myBuilder.append(titleLabel);
        myBuilder.append(titleText);
        
        //Forename
        JLabel forenameLabel = new JLabel(Messages.getString("AbstractEslPersonPanel.forenameLabel"));
        forenameText = new JTextField(firstName);
        forenameText.setEditable(!readOnly);
        myBuilder.append(forenameLabel);
        myBuilder.append(forenameText);
        
        //Surname
        JLabel surnameLabel = new JLabel(Messages.getString("AbstractEslPersonPanel.surnameLabel"));
        surnameText = new JTextField(lastName);
        surnameText.setEditable(!readOnly);
        myBuilder.append(surnameLabel);
        myBuilder.append(surnameText);
    	
        myBuilder.appendUnrelatedComponentsGapRow();
        
    	return personalDetailsPanel;
    }
    
    protected JPanel initSexDobPanel(){
    	return initSexDobPanel(null, null);
    }
    
    protected JPanel initSexDobPanel(String sex, Date dob){
    	JPanel sexDobPanel = new JPanel();
    	DefaultFormBuilder myBuilder = new DefaultFormBuilder(new FormLayout("60dlu, 2dlu, 75dlu, 10dlu, 60dlu, 2dlu, 75dlu, 2dlu"), sexDobPanel);
    	myBuilder.setBorder(new TitledBorder(Messages.getString("AbstractEslPersonalPanel.sexDOBTitle")));
    	
        //Sex
        JLabel sexLabel = new JLabel(Messages.getString("AbstractEslPersonPanel.sexLabel"));
        String[] comboOptions = new String[]{Messages.getString("AbstractEslPersonPanel.sexOption1"), Messages.getString("AbstractEslPersonPanel.sexOption2")};
        sexCombo = new JComboBox(comboOptions);
        sexCombo.setSelectedIndex(-1);
        sexCombo.setSelectedItem(sex);
        sexCombo.setEnabled(!readOnly);
        myBuilder.append(sexLabel);
        myBuilder.append(sexCombo);
        
        //DOB
        JLabel dobLabel = new JLabel(Messages.getString("AbstractEslPersonPanel.dobLabel"));
        if ( null != dob ){
        	dobPicker = new BasicDatePicker(dob.getTime());
        	dobPicker.setDate(dob);
        }
        else{
        	dobPicker = new BasicDatePicker();
        }
        dobPicker.setEditable(!readOnly);
        myBuilder.append(dobLabel);
        myBuilder.append(dobPicker);
            	
        myBuilder.appendUnrelatedComponentsGapRow();
        
    	return sexDobPanel;
    }
    
    protected JPanel initAddressPanel(){
    	return initAddressPanel(null, null, null, null, null, null, null);
    }
    
    protected JPanel initAddressPanel(String address1, String address2, String address3, String city, String region, String country, String postCode){
    	JPanel addressPanel = new JPanel();
    	DefaultFormBuilder myBuilder = new DefaultFormBuilder(
    			new FormLayout("60dlu, 2dlu, 75dlu, 10dlu, 60dlu, 2dlu, 75dlu, 10dlu, 60dlu, 2dlu, 75dlu, 2dlu"), addressPanel);
    	myBuilder.setBorder(new TitledBorder(Messages.getString("AbstractEslPersonPanel.AddressTitle")));
    	
        //Address 1
        JLabel address1Label = new JLabel(Messages.getString("AbstractEslPersonPanel.AddressLabel1"));
        address1Text = new JTextField(address1);
        address1Text.setEditable(!readOnly);
        myBuilder.append(address1Label);
        myBuilder.append(address1Text);
        
        //Address 2
        JLabel address2Label = new JLabel(Messages.getString("AbstractEslPersonPanel.AddressLabel2"));
        address2Text = new JTextField(address2);
        address2Text.setEditable(!readOnly);
        myBuilder.append(address2Label);
        myBuilder.append(address2Text);
        
        //Address 3
        JLabel address3Label = new JLabel(Messages.getString("AbstractEslPersonPanel.AddressLabel3"));
        address3Text = new JTextField(address3);
        address3Text.setEditable(!readOnly);
        myBuilder.append(address3Label);
        myBuilder.append(address3Text);
       
        //City
        JLabel cityLabel = new JLabel(Messages.getString("AbstractEslPersonPanel.cityLabel"));
        cityText = new JTextField(city);
        cityText.setEditable(!readOnly);
        myBuilder.append(cityLabel);
        myBuilder.append(cityText);
        
        //Region
        JLabel regionLabel = new JLabel(Messages.getString("AbstractEslPersonPanel.regionLabel"));
        regionText = new JTextField(region);
        regionText.setEditable(!readOnly);
        myBuilder.append(regionLabel);
        myBuilder.append(regionText);
        
        //Country
        JLabel countryLabel = new JLabel(Messages.getString("AbstractEslPersonPanel.countryLabel"));
        countryText = new JTextField(country);
        countryText.setEditable(!readOnly);
        myBuilder.append(countryLabel);
        myBuilder.append(countryText);
        
        //Postcode 
        JLabel postcodeLabel = new JLabel(Messages.getString("AbstractEslPersonPanel.postcodeLabel"));
        postcodeText = new JTextField(postCode);
        postcodeText.setEditable(!readOnly);
        myBuilder.append(postcodeLabel);
        myBuilder.append(postcodeText);
                    	
        myBuilder.appendUnrelatedComponentsGapRow();
        
    	return addressPanel;
    }
    
    protected JPanel initPhoneEmailPanel(){
    	return initPhoneEmailPanel(null, null, null, null);
    }
    
    protected JPanel initPhoneEmailPanel(String email, String homePhone, String workPhone, String mobPhone){
    	JPanel phoneEmailPanel = new JPanel();
    	DefaultFormBuilder myBuilder = new DefaultFormBuilder(new FormLayout("60dlu, 2dlu, 75dlu, 10dlu, 60dlu, 2dlu, 75dlu, 2dlu"), phoneEmailPanel);
    	myBuilder.setBorder(new TitledBorder(Messages.getString("AbstractEslPersonPanel.phoneEmailTitle")));
    	
        //Email address 
        JLabel emailLabel = new JLabel(Messages.getString("AbstractEslPersonPanel.emailLabel"));
        emailAddress = new JTextField(email);
        emailAddress.setEditable(!readOnly);
        myBuilder.append(emailLabel);
        myBuilder.append(emailAddress);
        
        //Home Phone
        JLabel homePhoneLabel = new JLabel(Messages.getString("AbstractEslPersonPanel.homePhoneLabel"));
        homePhoneText = new JTextField(homePhone);
        homePhoneText.setEditable(!readOnly);
        myBuilder.append(homePhoneLabel);
        myBuilder.append(homePhoneText);
        
        //Work Phone
        JLabel workPhoneLabel = new JLabel(Messages.getString("AbstractEslPersonPanel.workPhoneLabel"));
        workPhoneText = new JTextField(workPhone);
        workPhoneText.setEditable(!readOnly);
        myBuilder.append(workPhoneLabel);
        myBuilder.append(workPhoneText);
        
        //Mobile Phone
        JLabel mobilePhoneLabel = new JLabel(Messages.getString("AbstractEslPersonPanel.mobilePhoneLabel"));
        mobilePhoneText = new JTextField(mobPhone);
        mobilePhoneText.setEditable(!readOnly);
        myBuilder.append(mobilePhoneLabel);
        myBuilder.append(mobilePhoneText);

        myBuilder.appendUnrelatedComponentsGapRow();
        
        return phoneEmailPanel;
    }
    
    protected JPanel initHealthDetailsPanel(){
    	return initHealthDetailsPanel(null, null);
    }
    
    protected JPanel initHealthDetailsPanel(String nhsNumber, String hospNumber){
    	JPanel healthDetailsPanel = new JPanel();
    	DefaultFormBuilder myBuilder = new DefaultFormBuilder(new FormLayout("60dlu, 2dlu, 75dlu, 10dlu, 60dlu, 2dlu, 75dlu, 2dlu"), healthDetailsPanel);
    	myBuilder.setBorder(new TitledBorder(Messages.getString("AbstractEslPersonPanel.healthDetailsTitle")));
    	
        //NHS Number
        JLabel nhsNumberLabel = new JLabel(Messages.getString("AbstractEslPersonPanel.nhsNumberLabel"));
        nhsNumberText = new JTextField(nhsNumber);
        nhsNumberText.setEditable(!readOnly);
        myBuilder.append(nhsNumberLabel);
        myBuilder.append(nhsNumberText);
        
        //Hospital Number
        JLabel hospitalNumberLabel = new JLabel(Messages.getString("AbstractEslPersonPanel.hospitalNumberLabel"));
        hospitalNumberText = new JTextField(hospNumber);
        hospitalNumberText.setEditable(!readOnly);
        myBuilder.append(hospitalNumberLabel);
        myBuilder.append(hospitalNumberText);
            	
        myBuilder.appendUnrelatedComponentsGapRow();
        
    	return healthDetailsPanel;
    }
    
    protected JPanel initRiskIssuesPanel(){
    	return initRiskIssuesPanel(null);
    }
    
    protected JPanel initRiskIssuesPanel(String riskIssues){
    	JPanel riskIssuesPanel = new JPanel();
    	DefaultFormBuilder myBuilder = new DefaultFormBuilder(new FormLayout("60dlu, 2dlu, 222dlu, 2dlu"), riskIssuesPanel);
    	myBuilder.setBorder(new TitledBorder(Messages.getString("AbstractEslPersonPanel.riskIssuesTitle")));
    	
        JLabel riskIssuesLabel = new JLabel(Messages.getString("AbstractEslPersonPanel.riskIssuesLabel"));
        riskIssuesText = new JTextArea(riskIssues);
        riskIssuesText.setRows(6);
        riskIssuesText.setLineWrap(true);
        riskIssuesText.setWrapStyleWord(true);
        riskIssuesText.setEditable(!readOnly);
        JScrollPane riskIssuesScroll = new JScrollPane(riskIssuesText, 
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        myBuilder.append(riskIssuesLabel);
        myBuilder.append(riskIssuesScroll);
    	
        myBuilder.appendUnrelatedComponentsGapRow();
            	
    	return riskIssuesPanel;
    }

    protected boolean validateForm(){
    	if ( !doValidation() ){
    		return true;
    	}
    	if(!validateTextField(forenameText, Messages.getString("AbstractEslPersonPanel.subjectForenameRequiredMessage"))) {
    		return false;
    	}
    	if(!validateTextField(surnameText, Messages.getString("AbstractEslPersonPanel.subjectSurnameRequiredMessage"))) {
    		return false;
    	}
    	if ( getSexCombo().getSelectedIndex() < 0 ){
    		String message = Messages.getString("AbstractEslPersonPanel.subjectSexRequiredMessage");
    		WrappedJOptionPane.showWrappedMessageDialog(this, message, Messages.getString("AbstractEslPersonPanel.error"), WrappedJOptionPane.ERROR_MESSAGE);
    		return false;
    	}
    	
    	for ( Map.Entry<String, JComboBox> entry: customCombos.entrySet() ){
    		if ( entry.getValue().getSelectedIndex() < 0 ){
        		String message = Messages.getString("AbstractEslPersonPanel.subjectCustomRequiredMessage")+entry.getKey();
        		WrappedJOptionPane.showWrappedMessageDialog(this, message, Messages.getString("AbstractEslPersonPanel.error"), WrappedJOptionPane.ERROR_MESSAGE);
        		return false;
    		}
    	}
    	
    	if(dataSet.getProjectCode().equals(ADDRESS2_PROJECT_CODE)) {
    		if(!validateAddress2Only()) {
    			return false;
    		}
    	}
    	
    	return true;
    }
    
	protected boolean doValidation(){
		return true;
	}
	
    protected JPanel initCustomPanel(DataSet dataSet){
    	return initCustomPanel(dataSet, null);
    }
    
    protected JPanel initCustomPanel(DataSet dataSet, ISubject subject){
    	if ( 0 == dataSet.getEslCustomFieldCount() ){
    		return null;
    	}
    	JPanel customPanel = new JPanel();
    	DefaultFormBuilder myBuilder = new DefaultFormBuilder(
    			new FormLayout("60dlu, 2dlu, 75dlu, default:grow"), customPanel);
    	myBuilder.setBorder(new TitledBorder(Messages.getString("AbstractEslPersonalPanel.customTitle")));

    	for ( int i=0, c=dataSet.getEslCustomFieldCount(); i<c; i++ ){
    		EslCustomField field = dataSet.getEslCustomField(i);
            JLabel label = new JLabel(field.getName());
            String[] comboOptions = new String[field.getValueCount()];
            for ( int j=0, d=field.getValueCount(); j<d; j++ ){
            	comboOptions[j] = field.getValue(j);
            }
            final JComboBox combo = new JWideComboBox(comboOptions);
            combo.addItemListener(new ItemListener() {
            	public void itemStateChanged(ItemEvent itemEvent){
            		if(itemEvent.getStateChange() == ItemEvent.SELECTED){
            			combo.setToolTipText(itemEvent.getItem().toString());
            		}
            	}
            });
            
            customCombos.put(field.getName(), combo);
            combo.setSelectedIndex(-1);
            //see if there is a value for the field
            if ( null != subject ){
            	for ( int j=0, d=subject.getCustomValueCount(); j<d; j++ ){
            		ICustomValue value = subject.getCustomValue(j);
            		if ( value.getName().equals(field.getName())){
            			combo.setSelectedItem(value.getValue());
            			combo.setToolTipText(value.getValue());
            			break;
            		}
            	}
            }
            combo.setEnabled(!readOnly);
            myBuilder.append(label);
            myBuilder.append(combo);

    	}
    	
    	return customPanel;
    }
    
    protected void makeEditable(){
        titleText.setEditable(true);
        forenameText.setEditable(true);
        surnameText.setEditable(true);
        sexCombo.setEnabled(true);
        dobPicker.setEditable(true);
        address1Text.setEditable(true);
        address2Text.setEditable(true);
        address3Text.setEditable(true);
        cityText.setEditable(true);
        regionText.setEditable(true);
        countryText.setEditable(true);
        postcodeText.setEditable(true);
        emailAddress.setEditable(true);
        homePhoneText.setEditable(true);
        workPhoneText.setEditable(true);
        mobilePhoneText.setEditable(true);
        nhsNumberText.setEditable(true);
        hospitalNumberText.setEditable(true);
        riskIssuesText.setEditable(true);
        for ( JComboBox combo: customCombos.values() ){
        	combo.setEnabled(true);
        }
    }
    
    private boolean validateAddress2Only() {
    	if(!validateTextField(address1Text, Messages.getString("AbstractEslPersonPanel.address1RequiredMessage"))) {
    		return false;
    	}
    	
    	if(!validateTextField(cityText, Messages.getString("AbstractEslPersonPanel.cityRequiredMessage"))) {
    		return false;
    	}
    	
    	if(!validateTextField(postcodeText, Messages.getString("AbstractEslPersonPanel.postcodeRequiredMessage"))) {
    		return false;
    	}
    	
    	if(!validateTextField(homePhoneText, Messages.getString("AbstractEslPersonPanel.homePhoneRequiredMessage"))) {
    		return false;
    	}
    	
    	if(dobPicker.getDate() == null) {
    		String message = Messages.getString("AbstractEslPersonPanel.DOBRequiredMessage");
    		WrappedJOptionPane.showWrappedMessageDialog(this, message, Messages.getString("AbstractEslPersonPanel.error"), WrappedJOptionPane.ERROR_MESSAGE);
    		return false;
    	}
    	
    	return true;
    }
    
    private boolean validateTextField(JTextField toValidate, String errorMessage) {
    	if (toValidate.getText().equals("") ){
    		WrappedJOptionPane.showWrappedMessageDialog(this, errorMessage, Messages.getString("AbstractEslPersonPanel.error"), WrappedJOptionPane.ERROR_MESSAGE);
    		return false;
    	}
    	
    	return true;
    }
    
}
