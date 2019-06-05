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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.event.EslEvent;
import org.psygrid.collection.entry.event.EslListener;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.esl.model.IAddress;
import org.psygrid.esl.model.IFactory;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.hibernate.HibernateFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Main ESL panel, used for adding a participant to the register.
 * 
 * @author Rob Harper
 *
 */
public class EslPanel extends AbstractEslPersonPanel {

	private static final long serialVersionUID = 6968483316190927863L;

	private Record record;
	protected DefaultFormBuilder builder;
	private JButton okButton;
	private JButton cancelButton;

	private ISubject subject = null;

	private JTextField studyNumberText;
	private JTextField centreNumberText;

	public EslPanel(
			Record record, ISubject subject) {
		this(record, subject, false);
	}

	public EslPanel(Record record, ISubject subject, boolean readOnly) {
		super(readOnly, (DataSet) record.getDataSet());
		this.record = record;
		this.subject = subject;
		init(subject, record.getDataSet());
	}

	/**
	 * When no subject or record is provided, the dialog
	 * is opened in 'search' mode.
	 * 
	 * @param parent
	 */
	public EslPanel(DataSet dataSet) {
		super(false, (DataSet) dataSet);
		this.record = null;
		init(null, dataSet);
	}

	private void init(ISubject subject, DataSet dataSet) {
		builder = new DefaultFormBuilder(new FormLayout("default"), this); //$NON-NLS-1$
		builder.setDefaultDialogBorder();

		addInstructions();
		addPanels(subject, dataSet);

		okButton = createOkButton();
		okButton.setEnabled(true);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveEslSubject();
			}
		});

		cancelButton = new JButton(EntryMessages.getString("Entry.cancel"));
		cancelButton.setEnabled(true);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireEslEvent(new EslEvent(this, null, false));
			}
		});

		builder.appendUnrelatedComponentsGapRow();
		builder.nextLine(2);

		addEditButton();

		JPanel okButtonPanel = ButtonBarFactory.buildOKCancelBar(okButton, cancelButton);
		builder.append(okButtonPanel, builder.getColumnCount());        
	}

	public void addEslListener(EslListener listener) {
		listenerList.add(EslListener.class, listener);
	}

	public void removeEslListener(EslListener listener) {
		listenerList.remove(EslListener.class, listener);
	}

	protected void fireEslEvent(EslEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == EslListener.class) {
				((EslListener) listeners[i + 1]).eslCompleted(event);
			}
		}
	}

	protected void saveEslSubject(){
		if ( !validateForm() ){
			return;
		}
		ISubject eslSubject = null;
		IAddress eslAddress = null;
		if ( null == subject ){
			//TODO is there any way around hard-coding the factory??
			IFactory factory = new HibernateFactory();
			eslSubject = factory.createSubject();
			if ( null != studyNumberText ){
				eslSubject.setStudyNumber(studyNumberText.getText());
			}
			eslAddress = factory.createAddress();
			eslSubject.setAddress(eslAddress);
		}
		else{
			eslSubject = subject;
			eslAddress = subject.getAddress();
		}
		if ( getTitleText().getText().length() > 0 ){
			eslSubject.setTitle(getTitleText().getText());
		}
		if ( getForenameText().getText().length() > 0 ){
			eslSubject.setFirstName(getForenameText().getText());
		}
		if ( getSurnameText().getText().length() > 0 ){
			eslSubject.setLastName(getSurnameText().getText());
		}
		eslSubject.setSex((String)getSexCombo().getSelectedItem());
		eslSubject.setDateOfBirth(getDobPicker().getDate());
		if ( getAddress1Text().getText().length() > 0 ){
			eslAddress.setAddress1(getAddress1Text().getText());
		}
		if ( getAddress2Text().getText().length() > 0 ){
			eslAddress.setAddress2(getAddress2Text().getText());
		}
		if ( getAddress3Text().getText().length() > 0 ){
			eslAddress.setAddress3(getAddress3Text().getText());
		}
		if ( getCityText().getText().length() > 0 ){
			eslAddress.setCity(getCityText().getText());
		}
		if ( getRegionText().getText().length() > 0 ){
			eslAddress.setRegion(getRegionText().getText());
		}
		if ( getCountryText().getText().length() > 0 ){
			eslAddress.setCountry(getCountryText().getText());
		}
		if ( getPostcodeText().getText().length() > 0 ){
			eslAddress.setPostCode(getPostcodeText().getText());
		}
		if ( getHomePhoneText().getText().length() > 0 ){
			eslAddress.setHomePhone(getHomePhoneText().getText());
		}
		if ( getEmailAddress().getText().length() > 0 ){
			eslSubject.setEmailAddress(getEmailAddress().getText());
		}
		if ( getWorkPhoneText().getText().length() > 0 ){
			eslSubject.setWorkPhone(getWorkPhoneText().getText());
		}
		if ( getMobilePhoneText().getText().length() > 0 ){
			eslSubject.setMobilePhone(getMobilePhoneText().getText());
		}
		if ( getNhsNumberText().getText().length() > 0 ){
			eslSubject.setNhsNumber(getNhsNumberText().getText());
		}
		if ( getHospitalNumberText().getText().length() > 0 ){
			eslSubject.setHospitalNumber(getHospitalNumberText().getText());
		}
		if ( null != centreNumberText && centreNumberText.getText().length() > 0 ){
			eslSubject.setCentreNumber(centreNumberText.getText());
		}
		if ( getRiskIssuesText().getText().length() > 0 ){
			eslSubject.setRiskIssues(getRiskIssuesText().getText());
		}
		for ( Map.Entry<String, JComboBox> entry: getCustomCombos().entrySet() ){
			eslSubject.addOrUpdateCustomValue(entry.getKey(), (String)entry.getValue().getSelectedItem());
		}
		
		fireEslEvent(new EslEvent(this, eslSubject, isSaveRequired()));
	}

	private JPanel initStudyNumberPanel(String studyNumber, String centreNumber){
		JPanel studyNumberPanel = new JPanel();
		DefaultFormBuilder myBuilder = new DefaultFormBuilder(new FormLayout("60dlu, 2dlu, 75dlu, 10dlu, 60dlu, 2dlu, 75dlu, 2dlu"), studyNumberPanel);
		myBuilder.setBorder(new TitledBorder(EntryMessages.getString("EslPanel.studynumber")));

		//StudyNumber
		JLabel studyNumberLabel = new JLabel(EntryMessages.getString("EslPanel.number"));
		studyNumberText = new JTextField(studyNumber);
		studyNumberText.setEditable(false);
		myBuilder.append(studyNumberLabel);
		myBuilder.append(studyNumberText);

		//Centre Number
		JLabel centreNumberLabel = new JLabel(EntryMessages.getString("EslPanel.centrenumber"));
		centreNumberText = new JTextField(centreNumber);
		centreNumberText.setEditable(false);
		myBuilder.append(centreNumberLabel);
		myBuilder.append(centreNumberText);

		myBuilder.appendUnrelatedComponentsGapRow();

		return studyNumberPanel;
	}

	private JPanel initStudyNumberPanel(){
		String studyNumber = ""; 

		String centreNumber = "";

		if (record != null) {
			studyNumber  = record.getIdentifier().getIdentifier();
			centreNumber = record.getIdentifier().getGroupPrefix();
		}
		return initStudyNumberPanel(studyNumber, centreNumber);
	}

	protected void addInstructions(){
		//default implementation - do nothing
	}
	
	protected JButton createOkButton(){
		return new JButton(EntryMessages.getString("Entry.save"));
	}
	
	protected void addPanels(ISubject subject, DataSet dataSet){
		JPanel studyNumberPanel = null;
		JPanel personalDetailsPanel = null;
		JPanel sexDobPanel = null;
		JPanel addressPanel = null;
		JPanel phoneEmailPanel = null;
		JPanel healthDetailsPanel = null;
		JPanel riskIssuesPanel = null;
		JPanel customPanel = null;

		if ( null == subject ){
			studyNumberPanel = initStudyNumberPanel();
			personalDetailsPanel = initPersonalDetailsPanel();
			sexDobPanel = initSexDobPanel();
			addressPanel = initAddressPanel();
			phoneEmailPanel = initPhoneEmailPanel();
			healthDetailsPanel = initHealthDetailsPanel();
			riskIssuesPanel = initRiskIssuesPanel();
			customPanel = initCustomPanel(dataSet);
		}
		else{
			studyNumberPanel = initStudyNumberPanel(
					subject.getStudyNumber(), subject.getCentreNumber());
			personalDetailsPanel = initPersonalDetailsPanel(
					subject.getTitle(), subject.getFirstName(), subject.getLastName());
			sexDobPanel = initSexDobPanel(
					subject.getSex(), subject.getDateOfBirth());
			IAddress address = subject.getAddress();
			addressPanel = initAddressPanel(
					address.getAddress1(), address.getAddress2(), address.getAddress3(),
					address.getCity(), address.getRegion(), address.getCountry(), address.getPostCode());
			phoneEmailPanel = initPhoneEmailPanel(
					subject.getEmailAddress(), address.getHomePhone(), subject.getWorkPhone(), 
					subject.getMobilePhone());
			healthDetailsPanel = initHealthDetailsPanel(
					subject.getNhsNumber(), subject.getHospitalNumber());
			riskIssuesPanel = initRiskIssuesPanel(
					subject.getRiskIssues());
			customPanel = initCustomPanel(dataSet, subject);
		}

		builder.append(studyNumberPanel);
		builder.append(personalDetailsPanel);
		builder.append(sexDobPanel);
		builder.append(addressPanel);
		builder.append(phoneEmailPanel);
		builder.append(healthDetailsPanel);
		builder.append(riskIssuesPanel);
		if ( null != customPanel ){
			builder.append(customPanel);
		}
	}
	
	protected void addEditButton(){
		//default implementation - do nothing
	}
	
	protected boolean isSaveRequired(){
		return true;
	}
	
	protected ISubject getSubject() {
		return subject;
	}
}
