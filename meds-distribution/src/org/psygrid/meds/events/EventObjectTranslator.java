package org.psygrid.meds.events;

public class EventObjectTranslator {
	
	public static void populateEventInfoFromEvent(Event event, EventInfo eventInfo){
		eventInfo.setEventDate(event.getEventDate());
		eventInfo.setSystemUser(event.getSystemUser());
	}
	
	public static MedsPackageStatusChangeEventInfo translateMedsPackageStatusChangeEventToMedsPackageStatusChangeEventInfo(MedsPackageStatusChangeEvent e) throws InvalidEventException{
		
		e.validate();
		MedsPackageStatusChangeEventInfo eventInfo = null;
		
		if(e.getAdditionalInfo() != null && e.getAdditionalInfo().length() != 0){
			eventInfo = new MedsPackageStatusChangeEventInfo(e.getSystemUser(), e.getEventDate(), e.getStatusChangeEvent(), e.getAdditionalInfo());
		}else{
			eventInfo = new MedsPackageStatusChangeEventInfo(e.getSystemUser(), e.getEventDate(), e.getStatusChangeEvent());
		}
		
		return eventInfo;
	}
	
	public static PackageViewEventInfo translatePackageViewEventInfoFromPackageViewEvent(PackageViewEvent vE) throws InvalidEventException{
		vE.validate();
		PackageViewEventInfo pckgInfo = new PackageViewEventInfo(vE.getSystemUser(), vE.getEventDate(), vE.getViewedPackage().getPackageId());
		return pckgInfo;
	}

}
