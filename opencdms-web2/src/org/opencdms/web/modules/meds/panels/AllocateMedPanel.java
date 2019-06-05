package org.opencdms.web.modules.meds.panels;

import java.rmi.RemoteException;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.security.SamlHelper;
import org.opencdms.web.modules.meds.models.ParticipantRegisterSearchModel;
import org.psygrid.esl.model.ISubject;
import org.psygrid.meds.medications.ParticipantNotFoundException;
import org.psygrid.meds.rmi.MedicationClient;

public class AllocateMedPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3336054254197827879L;
	
	private final String participantIdentifier;
	
	public AllocateMedPanel(String id, String participantIdentifier) {
		super(id);
		this.participantIdentifier = participantIdentifier;
		//In here we want: 
		// - a label to display the identifier
		// - a button to allow the previous packages for this participant to be viewed
		// - list of previous packages if above button is pressed (this will not be visible at first).
		// - a button to go ahead and allocate
		
		add(new Label("pID", new Model(participantIdentifier)));
		add(new AllocateMedsPackageForm("viewPackagesForm"));
	}
	
	//WE NEED A FORM BECAUSE WE'RE GOING TO HAVE A SUBMIT BUTTON
	private class AllocateMedsPackageForm extends Form{


		/**
		 * 
		 */
		private static final long serialVersionUID = 4954005690272140862L;
		
		public AllocateMedsPackageForm(String id) {
			super(id);
		}
		
		 protected void onSubmit() {
		    	
		    	final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
		    	String saml = SamlHelper.getSaml(session.getUser());
		    	
		    	String identifier = AllocateMedPanel.this.participantIdentifier;
		    	int projectCodeDelimiterIndex = identifier.indexOf('/');
		    	String projectCode = identifier.substring(0,projectCodeDelimiterIndex);
		    	
		    	MedicationClient medsClient = new MedicationClient();
		    	String allocatedPackage = null;
		    	try {
					try {
						allocatedPackage = medsClient.allocateSubsequentMedicationPackage(projectCode, identifier, saml);
					} catch (ParticipantNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//Tell the user that there was a problem.
				}
				
				//Feed back the allocated package id to the user.
		    	
		 }

		
	}
	
	



}
