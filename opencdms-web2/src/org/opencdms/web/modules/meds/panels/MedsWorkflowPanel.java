package org.opencdms.web.modules.meds.panels;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.security.SamlHelper;
import org.opencdms.web.modules.meds.models.AllocateMedsModel;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.data.repository.RepositoryNoSuchDatasetFault;
import org.psygrid.data.repository.RepositoryServiceFault;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

public class MedsWorkflowPanel extends Panel implements ParticipantSearchListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String participantIdentifier = null;
	
	public MedsWorkflowPanel(String id){
		super(id);
		add(new SearchParticipantRegisterPanel("searchPanel", this));
		List tabs = new ArrayList();
		
        tabs.add(new AbstractTab(new Model("ALLOCATE")) {
            
			public Panel getPanel(String panelId)
            {
            //return new TabPanel1(panelId);
				return new AllocateMedPanel(panelId, participantIdentifier);
            }
            
            });
        
        tabs.add(new AbstractTab(new Model("DISTRIBUTE")) {
            
			public Panel getPanel(String panelId)
            {
            //return new TabPanel1(panelId);
				return new DistributeMedsPanel(panelId, participantIdentifier);
            }
            
            });

           
         add(new TabbedPanel("tabs", tabs));
		
		//add(new AllocateMedsForm("form"));
	}
	
	
    private class AllocateMedsForm extends Form<AllocateMedsModel> {

		private static final long serialVersionUID = 1L;
		
		DropDownChoice<ProjectType> study = null;
    	DropDownChoice<GroupType> centres = null;
    	DropDownChoice<String> participantIds = null;
    	
    	public AllocateMedsForm(String id) {
    		super(id,new CompoundPropertyModel<AllocateMedsModel>(new AllocateMedsModel()));
    		    	        
    		final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
    		    		
    		study = new DropDownChoice<ProjectType>("study");
    		study.setChoices(session.getUser().getProjects());
    		study.setChoiceRenderer(new ChoiceRenderer<ProjectType>("name", "idCode"));
    		study.setRequired(true);
    		

    		
    		study.add(new AjaxFormComponentUpdatingBehavior("onchange"){
    			protected void onUpdate(AjaxRequestTarget target) {
    				// update the doc occurrence list
        			String idcode = study.getModelObject().getIdCode();
		
        			Map<ProjectType, List<GroupType>> centreMap = session.getUser().getGroups();
        			List<GroupType> centresForStudy = centreMap.get(study.getModelObject());
        			
        			centres.setChoices(centresForStudy);
        			centres.add(new AjaxFormComponentUpdatingBehavior("onchange"){
        				protected void onUpdate(AjaxRequestTarget target){
        					
        					
        					String idCode = study.getModelObject().getIdCode();
        					String saml = SamlHelper.getSaml(session.getUser());
        					
        					RepositoryClient client = new RepositoryClient();
        					try {
        						DataSet ds = client.getDataSetSummary(idCode, new Date(0), saml);
								List<String> identifiers = client.getIdentifiers(ds.getId(), saml);
								participantIds.setChoices(identifiers);
							} catch (ConnectException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (SocketTimeoutException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (RepositoryServiceFault e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NotAuthorisedFault e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (RepositoryNoSuchDatasetFault e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							//Here's where we'd add participants...
	        				target.addComponent(participantIds);
        				}
        				
        				
        				
        			});
        			
        			target.addComponent(centres);
        		
    			}	
    		});
    		add(study);
    		
    		centres = new DropDownChoice<GroupType>("centre");
    		centres.setOutputMarkupId(true);
    		centres.setChoiceRenderer(new ChoiceRenderer<GroupType>("name", "idCode"));
    		centres.setRequired(true);
    		add(centres);
    		
    		participantIds = new DropDownChoice<String>("participant");
    		participantIds.setOutputMarkupId(true);
    		participantIds.setRequired(true);
    		add(participantIds);

    		
 
    		    		
    	}

        protected void onSubmit(){}   
    }


	public void participantSpecified(String participant) {
		this.participantIdentifier = participant;
		
	}

}
