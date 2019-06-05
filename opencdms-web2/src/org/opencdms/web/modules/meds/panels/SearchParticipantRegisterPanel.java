package org.opencdms.web.modules.meds.panels;

import java.io.Serializable;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.security.SamlHelper;
import org.opencdms.web.modules.meds.models.ParticipantRegisterSearchModel;
import org.psygrid.esl.model.IProject;
import org.psygrid.esl.model.ISubject;
import org.psygrid.esl.model.hibernate.HibernateFactory;
import org.psygrid.esl.services.ESLServiceFault;
import org.psygrid.esl.services.NotAuthorisedFault;
import org.psygrid.esl.services.client.EslClient;

public class SearchParticipantRegisterPanel extends Panel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8512676237660414513L;
	
	private List<ISubject> matchingSubjects = null;
	final private ParticipantSearchListener listener;

	public SearchParticipantRegisterPanel(String id, ParticipantSearchListener p) {
		super(id);
		listener = p;
		add(new SearchParticipantRegisterForm("searchParticipantForm"));
		//Next thing we need is a selectable list view!
		final ListView v = new ListView("matches", matchingSubjects){

			@Override
			protected void populateItem(ListItem item) {
				// TODO Auto-generated method stub
				
			}
        	 
         };
		final RadioGroup radioGroup = new RadioGroup("radioGroup", new Model());
		radioGroup.add(v);
		add(radioGroup);

	}


	public class SearchParticipantRegisterForm extends Form {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1726059235243866340L;
		
		
		public SearchParticipantRegisterForm(String id) {
			super(id, new Model(new ParticipantRegisterSearchModel()));
			
			// TODO Auto-generated constructor stub
			 add(new TextField<String>("firstname", new PropertyModel(getModel().getObject(), "firstName")).setRequired(false));
	         add(new TextField<String>("lastname", new PropertyModel(getModel().getObject(), "lastName")).setRequired(false));
	          
	         }
		
		/**
	     * Called upon form submit. Attempts to authenticate the user.
	     */
	    protected void onSubmit() {
	    	
	    	final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
	    	
	    	ParticipantRegisterSearchModel m = (ParticipantRegisterSearchModel)this.getModel().getObject();
	    	
	    	
	    	EslClient prClient = new EslClient();
	    	HibernateFactory f = new HibernateFactory();
	    	ISubject s = f.createSubject();
	    	s.setLastName(m.getLastName());
	    	s.setFirstName(m.getFirstName());
	    	
	    	try {
	    		IProject project = prClient.retrieveProjectByCode("PRST", SamlHelper.getSaml(session.getUser()));
				List<ISubject> matchingSubjects = prClient.findSubjectByExample(project, s, SamlHelper.getSaml(session.getUser()));
				SearchParticipantRegisterPanel.this.matchingSubjects = matchingSubjects;
				final RadioGroup g = new RadioGroup("radioGroup", new Model()){
					@Override
					protected void onSelectionChanged(Object newSelection){
						int nume = 1;
						String participantId = (String) newSelection;
						if(SearchParticipantRegisterPanel.this.listener != null){
							SearchParticipantRegisterPanel.this.listener.participantSpecified(participantId);
						}
						
					}
					
					protected boolean wantOnSelectionChangedNotifications() {
						return true;
					}
				};
				final ListView v = new ListView("matches", matchingSubjects){

					@Override
					protected void populateItem(ListItem item) {
						ISubject s = (ISubject)item.getModelObject();
						item.add(new Label("participantid", s.getStudyNumber()));
						item.add(new Label("lastname", s.getLastName()));
						item.add(new Label("firstname", s.getFirstName()));
						item.add(new Radio("radio", new Model(s.getStudyNumber())));
						
					}
		        	 
		         };
		        g.add(v);
				SearchParticipantRegisterPanel.this.replace(g);
				
				if(matchingSubjects.size() == 0){
					//TODO - relay no matches
				}else if(matchingSubjects.size() > 1){
					//TODO - relay that there are too many matches
					//Display the matching subjects in a list view?
				}else{
					//Display the matching subject in a list view
				}
				
			} catch (ConnectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SocketTimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ESLServiceFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotAuthorisedFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }



	}


}
