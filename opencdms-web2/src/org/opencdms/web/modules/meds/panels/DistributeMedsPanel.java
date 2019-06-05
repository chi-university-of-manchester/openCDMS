package org.opencdms.web.modules.meds.panels;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.security.SamlHelper;
import org.psygrid.common.identifier.IdentifierHelper;
import org.psygrid.esl.model.ISubject;
import org.psygrid.meds.medications.ParticipantNotFoundException;
import org.psygrid.meds.rmi.MedicationClient;

public class DistributeMedsPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String participantId;
	private List<String> distributablePackages;
	private boolean distributablePackagesRetrieved;
	
	public DistributeMedsPanel(String id, String participantId){
		super(id);
		this.participantId = participantId;
		
		add(new Label("pID", participantId));
		add(new FeedbackPanel("feedback"));
		
		final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
    	String saml = SamlHelper.getSaml(session.getUser());
		
		MedicationClient c = new MedicationClient();
		
		String projectCode = IdentifierHelper.getProjectCodeFromIdentifier(participantId);
		try {
			try {
				distributablePackages = c.getDistributablePackagesForUser(projectCode, participantId, saml);
			} catch (ParticipantNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			distributablePackagesRetrieved = true;
		} catch (RemoteException e) {
			distributablePackages = new ArrayList<String>(); //empty list
			distributablePackagesRetrieved = false;
			
		}
		
		if(distributablePackagesRetrieved == false){
			error("Could not retrieve distributable packages.");
		}
		
		add(new DistributeMedsForm("distributePackagesForm"));
		
		
	}
	
	private class DistributeMedsForm extends Form {


		/**
		 * 
		 */
		private static final long serialVersionUID = 4037436717290656734L;
		
		public DistributeMedsForm(String id) {
			super(id);
			// TODO Auto-generated constructor stub
			ListView v = new ListView("packages", DistributeMedsPanel.this.distributablePackages){

				@Override
				protected void populateItem(ListItem item) {
					String packageId = (String)item.getModelObject();
					item.add(new Label("packageId", packageId));
					
				}
				
			};
			
			add(v);
		}

		
	}

}
