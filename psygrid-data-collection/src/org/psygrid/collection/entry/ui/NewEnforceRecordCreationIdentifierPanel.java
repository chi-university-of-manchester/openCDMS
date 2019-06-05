package org.psygrid.collection.entry.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.psygrid.data.model.hibernate.DataSet;

public class NewEnforceRecordCreationIdentifierPanel extends NewIdentifierPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8281897360398185758L;


	protected NewEnforceRecordCreationIdentifierPanel(JDialog dialog) {
		super(dialog);
		// TODO Auto-generated constructor stub
	}
	
	public NewEnforceRecordCreationIdentifierPanel(DataSet dataSet, JDialog dialog) throws IdentifierPanelException {
		super(dataSet, dialog);
		init();
		cancelButton.setEnabled(false);
		centresCBox.removeItemListener(groupListener);
		groupListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					//Bring up a message that allows the user to confirm they have selected the
					//correct group. If they cancel, then set the selected index back to -1, and
					//don't do anything else.
					Centre selectedCentre =  (Centre)centresCBox.getSelectedItem();
					
					String confirmMessageP1 = Messages.getString("ChooseIdentifierPanel.confirmCentreMessage_p1");
					String confirmMessageP2 = Messages.getString("ChooseIdentifierPanel.confirmCentreMessage_p2");
					StringBuffer fullMessage = new StringBuffer();
					
					fullMessage.append(confirmMessageP1).append(selectedCentre.getGroup().getLongName()).append(confirmMessageP2);
					
					String confirmTitle = Messages.getString("ChooseIdentifierPanel.confirmCentreTitle");
					int result = JOptionPane.showConfirmDialog(getParent(), fullMessage.toString(),
							confirmTitle, JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						//If they confirm ok then proceed.
						centresCBox.setEnabled(false);
						displaySites((Centre)e.getItem());
					}else{
						centresCBox.setSelectedIndex(-1);
					}
					
				}
			}
		};
		centresCBox.addItemListener(groupListener);
	}
	
}
