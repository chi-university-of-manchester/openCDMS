package org.psygrid.meds.events;

public enum StatusChangeEventType {
	
	packageAllocation("package allocated"),
	packageVerification("package verified"),
	packageVerificationUndo("undo package verification"),
	packageDistribution("packgage distributed"),
	packageDistributionUndo("undo package distribution"),
	packageReturn("package returned"),
	packageReturnUndo("undo package return"),
	packageUnusable("package determined unusable"),
	packageUnusableUndo("undo package determined unusable");
	
	private String eventString = null;
	
	StatusChangeEventType(String eventString){
		this.eventString = eventString;
	}
	
	public String getEventString(){
		return eventString;
	}


}
