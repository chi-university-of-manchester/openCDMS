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

package org.opencdms.web.modules.export.panels;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.ITreeState;
import org.apache.wicket.model.IModel;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.modules.export.models.ExportDetailsModel;
import org.opencdms.web.modules.export.models.ExportDetailsModel.ExportEntry;
import org.psygrid.data.export.ExportFormat;
import org.psygrid.data.model.hibernate.*;

/**
 * @author Rob Harper
 *
 */
public class RequestExportDetailsPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(RequestExportDetailsPanel.class);

	private final CheckBoxTree entries;
	private final CheckBoxMultipleChoice<Status> docStatuses;
	final DropDownChoice<ExportFormat> format;
	
	private final WebMarkupContainer entriesContainer;
	private final WebMarkupContainer docStatusesContainer;
	private final WebMarkupContainer participantRegisterContainer;
	private final WebMarkupContainer formatContainer;
	private final WebMarkupContainer codesOrValuesContainer;
	
	public RequestExportDetailsPanel(String id, final IModel<ExportDetailsModel> model) {
		super(id, model);

		entriesContainer = new WebMarkupContainer("entriesContainer");
		entriesContainer.setOutputMarkupPlaceholderTag(true);
		entriesContainer.setVisible(false);

		docStatusesContainer = new WebMarkupContainer("docStatusesContainer");
		docStatusesContainer.setOutputMarkupPlaceholderTag(true);
		docStatusesContainer.setVisible(false);
		
		participantRegisterContainer = new WebMarkupContainer("participantRegisterContainer");
		participantRegisterContainer.setOutputMarkupPlaceholderTag(true);
		participantRegisterContainer.setVisible(false);

		formatContainer = new WebMarkupContainer("formatContainer");
		formatContainer.setOutputMarkupPlaceholderTag(true);
		formatContainer.setVisible(false);

		codesOrValuesContainer = new WebMarkupContainer("codesOrValuesContainer");
		codesOrValuesContainer.setOutputMarkupPlaceholderTag(true);
		codesOrValuesContainer.setVisible(false);

		docStatuses = new CheckBoxMultipleChoice<Status>("docStatuses",
				new ArrayList<Status>());
		docStatuses.setChoiceRenderer(new ChoiceRenderer<Status>("longName", "shortName"));
		docStatuses.setOutputMarkupId(true);
		docStatuses.setRequired(true);
		
		format =  new DropDownChoice<ExportFormat>("format", 
				new ArrayList<ExportFormat>());
		format.setOutputMarkupId(true);
		format.setRequired(true);
		
		final DropDownChoice<String> codesOrValues = 
			new DropDownChoice<String>(
				"codesOrValues", 
				new ArrayList<String>());
		codesOrValues.setOutputMarkupId(true);
		codesOrValues.setRequired(false);
		
		codesOrValues.add(new AjaxFormComponentUpdatingBehavior("onchange"){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				endOfPanel(target);
			}
			
		});

		format.add(new AjaxFormComponentUpdatingBehavior("onchange"){
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onUpdate(AjaxRequestTarget target){
				if ( model.getObject().getFormat() == ExportFormat.MULTIPLE_CSV ||
						model.getObject().getFormat() == ExportFormat.SINGLE_CSV ||
						model.getObject().getFormat() == ExportFormat.EXCEL ){
					codesOrValues.setChoices(getCodeChoices());
					codesOrValues.setRequired(true);
					codesOrValuesContainer.setVisible(true);
					target.addComponent(codesOrValuesContainer);
				}
				else{
					endOfPanel(target);
				}
			}
			
			private List<String> getCodeChoices(){
				List<String> valueOrCode = new ArrayList<String>();
				valueOrCode.add("Both");
				valueOrCode.add("Values Only");
				valueOrCode.add("Codes Only");
				return valueOrCode;
			}

		});
		
		entries = new CheckBoxTree("entries", new DefaultTreeModel(new DefaultMutableTreeNode("Tree"))){

			private static final long serialVersionUID = 1L;

			private boolean valid = false;
			
			@Override
			public boolean isValid(){
				return valid;
			}
							
			@Override
			public void validate() {
				//Validation
				if ( isVisibleInHierarchy() && model.getObject().getEntries().isEmpty() ){
					LOG.info("Entries tree is not valid");
					valid = false;
					Session.get().getFeedbackMessages().error(this, "Nothing has been selected to export");
					Session.get().dirty();
				}
			}

			@Override
			protected ITreeState newTreeState() {
				ITreeState ts = super.newTreeState();
				ts.setAllowSelectMultiple(true);
				return ts;
			}

			@Override
			protected void onNodeCheckUpdated(TreeNode node, BaseTree tree,
					AjaxRequestTarget target) {
				if ( tree.getTreeState().isNodeSelected(node) ){
					selectTree( tree, node );
				}
				else{
					deselectTree( tree, node );
				}					
			}
			
			private void deselectTree( BaseTree tree, TreeNode node ) {
				if ( node.isLeaf() ){
					if ( node instanceof DefaultMutableTreeNode ){
						Object obj = ((DefaultMutableTreeNode)node).getUserObject();
						if ( obj instanceof ExportEntry ){
							model.getObject().getEntries().remove((ExportEntry)obj);
						}
					}
				}
				else{
					Enumeration<?> nodeEnum = node.children();
					while ( nodeEnum.hasMoreElements() ) {
						TreeNode child = (TreeNode)nodeEnum.nextElement();
						tree.getTreeState().selectNode( child, false );
						deselectTree( tree, child );
					}
				}
			}
			
			private void selectTree( BaseTree tree, TreeNode node ) {
				if ( node.isLeaf() ){
					if ( node instanceof DefaultMutableTreeNode ){
						Object obj = ((DefaultMutableTreeNode)node).getUserObject();
						if ( obj instanceof ExportEntry ){
							model.getObject().getEntries().add((ExportEntry)obj);
						}
					}
				}
				else{
					Enumeration<?> nodeEnum = node.children();
					while ( nodeEnum.hasMoreElements() ) {
						TreeNode child = (TreeNode)nodeEnum.nextElement();
						tree.getTreeState().selectNode( child, true );
						selectTree( tree, child );
					}
				}
			}
			
			
			
		};
		entries.setRootLess(true);
		entries.setOutputMarkupId(true);
		
		entriesContainer.add(entries);
		entriesContainer.add(new ComponentFeedbackPanel("entriesFeedback", entries));

		docStatusesContainer.add(docStatuses);
		docStatusesContainer.add(new ComponentFeedbackPanel("docStatusesFeedback", docStatuses));
		
		participantRegisterContainer.add(new CheckBox("participantRegister"));

		formatContainer.add(format);
		formatContainer.add(new ComponentFeedbackPanel("formatFeedback", format));

		codesOrValuesContainer.add(codesOrValues);
		codesOrValuesContainer.add(new ComponentFeedbackPanel("codesOrValuesFeedback", codesOrValues));

		add(entriesContainer);
		add(docStatusesContainer);
		add(participantRegisterContainer);
		add(formatContainer);
		add(codesOrValuesContainer);
		
	}
	
	public void endOfPanel(AjaxRequestTarget target){
		//do nothing
	}
	
	public void showErrors(final AjaxRequestTarget target){
		target.addComponent(entriesContainer);
		target.addComponent(docStatusesContainer);
		target.addComponent(formatContainer);
		target.addComponent(codesOrValuesContainer);
	}
	
	public void buildDocumentTree(DataSet dataSet, AjaxRequestTarget target){
		TreeModel model = null;
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(dataSet.getDisplayText());
		for ( int i=0, c=dataSet.numDocumentGroups(); i<c; i++ ){
			DocumentGroup group = dataSet.getDocumentGroup(i);
			LOG.info("Group="+group.getDisplayText());
			DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(group.getDisplayText());
			rootNode.add(groupNode);
			for ( int j=0, d=dataSet.numDocuments(); j<d; j++ ){
				Document doc = dataSet.getDocument(j);
				for ( int k=0, e=doc.numOccurrences(); k<e; k++ ){
					DocumentOccurrence docOcc = doc.getOccurrence(k);
					if ( docOcc.getDocumentGroup().equals(group) ){
						LOG.info("Document="+doc.getDisplayText());
						DefaultMutableTreeNode docOccNode = new DefaultMutableTreeNode(doc.getDisplayText());
						groupNode.add(docOccNode);
						for ( int l=0, f=doc.numSections(); l<f; l++ ){
							Section sec = doc.getSection(l);
							LOG.info("Section="+sec.getDisplayText());
							for ( int m=0, g=sec.numOccurrences(); m<g; m++ ){
								SectionOccurrence secOcc = sec.getOccurrence(m);
								LOG.info("Section Occurrence="+secOcc.getDisplayText());
								DefaultMutableTreeNode secOccNode = new DefaultMutableTreeNode(secOcc.getCombinedDisplayText());
								docOccNode.add(secOccNode);
								for ( int n=0, h=doc.numEntries(); n<h; n++ ){
									Entry entry = doc.getEntry(n);
									if ( entry.getSection().equals(sec) && !(entry instanceof NarrativeEntry) ){
										DefaultMutableTreeNode entryNode = 
											new DefaultMutableTreeNode(
													new ExportEntry(docOcc, secOcc, entry));
										secOccNode.add(entryNode);
									}
								}
							}
						}
					}
				}
			}
		}
		model = new DefaultTreeModel(rootNode);
		entries.setModelObject(model);
		entriesContainer.setVisible(true);
		target.addComponent(entriesContainer);
		
		docStatuses.setChoices(getDocStatuses(dataSet));
		docStatusesContainer.setVisible(true);
		target.addComponent(docStatusesContainer);
		
		if(dataSet.isEslUsed() && canUserExportPRData()) {
			participantRegisterContainer.setVisible(true);
			target.addComponent(participantRegisterContainer);
		} 
		
		format.setChoices(ExportFormat.getFormatList());
		formatContainer.setVisible(true);
		target.addComponent(formatContainer);
		
	}

	private List<Status> getDocStatuses(DataSet dataSet){
		List<Status> statuses = new ArrayList<Status>();
		//Assuming that all datasets have at least one document!
		Document doc = dataSet.getDocument(0);
		for ( int i=0, c=doc.numStatus(); i<c; i++ ){
			LOG.info("Adding status "+doc.getStatus(i).getLongName());
			statuses.add(doc.getStatus(i));
		}
		return statuses;
	}
	
	public void validate(){
		entries.validate();
	}
	
	private boolean canUserExportPRData() {
		OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
		Roles roles = session.getRoles();
		if( roles.contains("ROLE_EXPORTPR") ) {
			return true;
		}
		
		return false;
	}

}
