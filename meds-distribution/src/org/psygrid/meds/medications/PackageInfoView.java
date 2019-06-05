package org.psygrid.meds.medications;

import org.psygrid.meds.events.MedsPackageStatusChangeEventInfo;
import org.psygrid.meds.events.PackageViewEventInfo;

public class PackageInfoView {
	
	private PackageInfo packageInfo;
	private boolean viewEventsIncluded;
	private boolean statusChangeEventsIncluded;
	
	private PackageViewEventInfo[] viewEvents = null;
	private MedsPackageStatusChangeEventInfo[] statusChangeEvents = null;
	
	public PackageInfoView(){
		packageInfo = null;
		viewEventsIncluded = false;
		statusChangeEventsIncluded = false;
	}
	
	
	public PackageInfoView(PackageInfo pI, PackageViewEventInfo[] viewEvents, MedsPackageStatusChangeEventInfo[] statusChangeEvents){ 
		packageInfo = pI;
		viewEventsIncluded = true;
		statusChangeEventsIncluded = true;
		
		this.viewEvents = viewEvents;
		this.statusChangeEvents = statusChangeEvents;
	}
	
	public PackageInfoView(PackageInfo pI, PackageViewEventInfo[] viewEvents){
		packageInfo = pI;
		viewEventsIncluded = true;
		statusChangeEventsIncluded = false;
		
		this.viewEvents = viewEvents;
	}
	
	public PackageInfoView(PackageInfo pI, MedsPackageStatusChangeEventInfo[] statusChangeEvents){
		packageInfo = pI;
		viewEventsIncluded = false;
		statusChangeEventsIncluded = true;
		
		this.statusChangeEvents = statusChangeEvents;
	}
	
	public PackageInfoView(PackageInfo pI){
		packageInfo = pI;
		viewEventsIncluded = false;
		statusChangeEventsIncluded = false;
	}


	public PackageInfo getPackageInfo() {
		return packageInfo;
	}


	public void setPackageInfo(PackageInfo packageInfo) {
		this.packageInfo = packageInfo;
	}


	public boolean getViewEventsIncluded() {
		return viewEventsIncluded;
	}


	public void setViewEventsIncluded(boolean viewEventsIncluded) {
		this.viewEventsIncluded = viewEventsIncluded;
	}


	public boolean getStatusChangeEventsIncluded() {
		return statusChangeEventsIncluded;
	}


	public void setStatusChangeEventsIncluded(boolean statusChangeEventsIncluded) {
		this.statusChangeEventsIncluded = statusChangeEventsIncluded;
	}


	public PackageViewEventInfo[] getViewEvents() {
		return viewEvents;
	}


	public void setViewEvents(PackageViewEventInfo[] viewEvents) {
		this.viewEvents = viewEvents;
	}


	public MedsPackageStatusChangeEventInfo[] getStatusChangeEvents() {
		return statusChangeEvents;
	}


	public void setStatusChangeEvents(
			MedsPackageStatusChangeEventInfo[] statusChangeEvents) {
		this.statusChangeEvents = statusChangeEvents;
	}

}
