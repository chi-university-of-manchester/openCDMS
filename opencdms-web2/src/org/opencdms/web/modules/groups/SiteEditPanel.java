/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.opencdms.web.modules.groups;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.Site;

/**
 * A panel for editing a site.
 * 
 * @author Terry Child
 */
public class SiteEditPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	Panel previous = null;

	TextField<String> newConsultantName;

	Button addConsultant;
	
	/**
	 * A Panel for editing a site.
	 * 
	 * @param id the component id
	 * @param isNew - true if this is a new site
	 * @param group the group containing the site
	 * @param site the site being edited.
	 * @param caller the calling panel
	 */
	@SuppressWarnings("serial")
	public SiteEditPanel(String id, boolean isNew, final Group group, final Site site,Panel caller) {
		super(id);

		this.previous=caller;
		
		add(new Label("panelTitle",isNew?"New Site":"Edit Site"));

        add(new FeedbackPanel("feedback"));

		// Use a separate list of consultants and copy it into the site list
		// when the site is submitted.
		final List<String> consultants =  new ArrayList<String>(site.getConsultants());
								        
		Form<Site> form = new Form<Site>("form",new CompoundPropertyModel<Site>(site)){
			protected void onSubmit() {
				// Check name/id clashes and then save the group - should do this with a validator
				// save or update
				if(!site.getConsultants().equals(consultants)){
					site.setConsultants(consultants);
				}
				if(!group.getSites().contains(site)){
					group.addSite(site);
					site.setParent(group);
				}
				info("Please save the centre when you are finished.");
				SiteEditPanel.this.replaceWith(previous);
			}
		};
		
		add(form);

		form.add(new Label("groupName",group.getLongName()+"("+group.getName()+")"));
		form.add(new TextField<String>("siteName").setRequired(true)
				.add(StringValidator.maximumLength(255))
				.add(new SiteNameValidator(group,site)));
		form.add(new TextField<String>("siteId").setRequired(true)
				.add(StringValidator.maximumLength(255))
				.add(new SiteIDValidator(group,site)));
		form.add(new TextField<String>("geographicCode")
				.add(StringValidator.maximumLength(255)));
		
		// Add a list of consultants with 'remove' links.
		form.add(new PropertyListView<String>("consultants",consultants){
			protected void populateItem(ListItem<String> item) {
				item.add(new Label("consultant",item.getModelObject()));
				item.add(new Link<String>("delete",item.getModel()){
					public void onClick() {
						String consultant = getModelObject();
						consultants.remove(consultant);
					}
				});
			}
		});
		
		// Add a consultant name field which is only required if the form is being submitted
		// via the 'addConsultant' button.
		newConsultantName = new TextField<String>("newConsultantName",new Model<String>()){
		    public boolean isRequired() {
		        Form<?> form = (Form<?>) findParent(Form.class);
		        return form.getRootForm().findSubmittingButton() == addConsultant;
		    }			
		};
		newConsultantName.add(StringValidator.maximumLength(255));
		form.add(newConsultantName);

		// Add a submit button for adding new consultant - this button does not perform
		// default form processing - so it will not submit the whole form.
		addConsultant = new Button("addConsultant"){
			public void onSubmit() {
				newConsultantName.validate();
				if(newConsultantName.isValid()){
					String name = newConsultantName.getConvertedInput();
					if(!consultants.contains(name)){
						consultants.add(name);
						newConsultantName.clearInput();
					}
					else {
						error("A consultant with this name already exists.");
					}
				}
			}
		};
		addConsultant.setDefaultFormProcessing(false);
		form.add(addConsultant);
		
		Button delete  = new Button("delete"){
			public void onSubmit() {
				group.getSites().remove(site);
				info("Please save the centre when you are finished.");
				SiteEditPanel.this.replaceWith(previous);
			}
		};
		delete.setDefaultFormProcessing(false);
		delete.setVisible((site.getId()==null) && group.getSites().contains(site));
		delete.add(new SimpleAttributeModifier("onclick", "if (!confirm('Are you sure you want to delete this site?')) return false;"));
		form.add(delete);		

		Button cancel = new Button("cancel"){
			public void onSubmit() {
				SiteEditPanel.this.replaceWith(previous);
			}
		};
		cancel.setDefaultFormProcessing(false);
		form.add(cancel);
	}
            	
	/**
	 * Validates that the siteID is unique within this group.
	 */
	private static final class SiteIDValidator extends AbstractValidator<String> {

		private static final long serialVersionUID = 1L;

		private Group group;
		private Site site;

		public SiteIDValidator(Group group,Site site) {
			super();
			this.group = group;
			this.site=site;
		}

		@Override
		protected void onValidate(IValidatable<String> validatable) {
			for(Site s:group.getSites()){
				if(s!=site && s.getSiteId().equals(validatable.getValue())){
					error(validatable);
				}
			}
		}		
	}

	/**
	 * Validates that the site Name is unique within this group.
	 */
	private static final class SiteNameValidator extends AbstractValidator<String> {

		private static final long serialVersionUID = 1L;

		private Group group;
		private Site site;

		public SiteNameValidator(Group group,Site site) {
			super();
			this.group = group;
			this.site=site;
		}

		@Override
		protected void onValidate(IValidatable<String> validatable) {
			for(Site s:group.getSites()){
				if(s!=site && s.getSiteName() .equals(validatable.getValue())){
					error(validatable);
				}
			}
		}		
	}
	
}
