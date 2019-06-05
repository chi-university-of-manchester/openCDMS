package org.opencdms.web.modules.meds.panels;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.psygrid.meds.medications.PackageInfo;
import org.psygrid.meds.medications.PackageStatus;
import org.psygrid.meds.utils.MedsGenerator;

public class VerifyPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 168909061146131720L;
	
	
	private List<SelectablePackageInfo> thePackages;
	
	public VerifyPanel(String id) {
		super(id);
		//go and get the unverified medication packages from the meds service.
		//display them in a list.
		//Let users select all, none or individually.
		//Let them either verify all selected or Mark all selected as unusable.
		//Offer an 'are you sure' safety mechanism.
		
		//Go do an artificial 'get' of some PackageInfo objects to populate the list with.
		List<PackageInfo> packages = MedsGenerator.generateDummyPackages(50);
		thePackages = new ArrayList<SelectablePackageInfo>();
		for(PackageInfo p : packages){
			thePackages.add(new SelectablePackageInfo(p));
		}
		
		
		

		
		
		//add(unverifiedPackages);
		//add(new PagingNavigator("navigator", unverifiedPackages));
		add(new VerifyPanelForm("selectAndDo"));
		
	}
	
	private class VerifyPanelForm extends Form{

		/**
		 * 
		 */
		private static final long serialVersionUID = -2752701111387730856L;
		
		private final CheckGroup checkGroup;
		private PageableListView unverifiedPackages = null;
		
		public VerifyPanelForm(String id) {
			super(id);
			
			checkGroup = new CheckGroup("checkGroup", new ArrayList());
			add(checkGroup);
			
			unverifiedPackages = new PageableListView("unverifiedPackages", thePackages,10){

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem item) {
					SelectablePackageInfo medsPackage = (SelectablePackageInfo) item.getModelObject();
					//item.add(new CheckBox("selected", new PropertyModel(medsPackage, "selected")));
					
					Check c = new Check("selected", item.getModel());
					c.setVisible(medsPackage.getpInfo().getPackageStatus().equals(PackageStatus.unverified.toString()));
					item.add(c);
					item.add(new Label("packageIdentifier", medsPackage.getpInfo().getPackageIdentifier()));
					item.add(new Label("treatmentInfo.treatmentName", medsPackage.getpInfo().getTreatmentInfo().getTreatmentName()));
					item.add(new Label("shipmentNumber", medsPackage.getpInfo().getShipmentNumber()));
					item.add(new Label("batchNumber", medsPackage.getpInfo().getBatchNumber()));
					item.add(new Label("expiryDate", DateFormat.getInstance().format(medsPackage.getpInfo().getExpiryDate())));
					item.add(new Label("status", medsPackage.getpInfo().getPackageStatus()));
				}
				
			};
			
			checkGroup.add(unverifiedPackages);
			add(new PagingNavigator("navigator", unverifiedPackages));
			
			Button selectAllButton = new Button("selectAllButton"){
				public void onSubmit(){
					//Select all items in list.
					List<SelectablePackageInfo> selectedItems = (List<SelectablePackageInfo>)checkGroup.getModelObject();
					
					for(SelectablePackageInfo s : selectedItems){
						s.getpInfo().setPackageStatus(PackageStatus.available.toString());
					}
					VerifyPanelForm.this.unverifiedPackages.modelChanged();
					
				}
			};
			
			Button deselectAllButton = new Button("deselectAllButton"){
				public void onSubmit(){
					//Deselect all items in list.
				}
			};
			
			Button verifySelectedButton = new Button("verifySelectedButton"){
				public void onSubmit(){
					//verify selected items
					//Are you sure?
				}
			};
		
			Button markUnusableSelectedButton = new Button("markUnusableSelectedButton"){
				public void onSubmit(){
					//mark selected items as unusable
					//Are you sure?
				}
			};
			
			add(selectAllButton);
			add(deselectAllButton);
			add(verifySelectedButton);
			add(markUnusableSelectedButton);
		}
	}
}
