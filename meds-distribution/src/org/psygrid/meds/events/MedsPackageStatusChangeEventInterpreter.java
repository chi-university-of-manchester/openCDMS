package org.psygrid.meds.events;

import org.psygrid.meds.medications.PackageStatus;

public class MedsPackageStatusChangeEventInterpreter {
	
	public class AdditionalStatusChangeEventInfo{
		
		private final String additionalInfoDescription;
		private final String additionalInfo;
		
		AdditionalStatusChangeEventInfo(String infoDescription, String info){
			additionalInfoDescription = infoDescription;
			additionalInfo = info;
		}

		public String getAdditionalInfoDescription() {
			return additionalInfoDescription;
		}

		public String getAdditionalInfo() {
			return additionalInfo;
		}
		
		public String getAdditionalInfoSummary(){
			return additionalInfoDescription + ": " + additionalInfo;
		}
	}
	
	private final MedsPackageStatusChangeEvent e;
	
	public MedsPackageStatusChangeEventInterpreter(MedsPackageStatusChangeEvent e){
		this.e = e;
	}
	
	public static StatusChangeEventType assessEventType(PackageStatus status1, PackageStatus status2){
		
		StatusChangeEventType eventType = null;
		
		if(status1.equals(PackageStatus.unverified)){
			if(status2.equals(PackageStatus.available)){
				eventType = StatusChangeEventType.packageVerification;
			}else if(status2.equals(PackageStatus.unusable)){
				eventType = StatusChangeEventType.packageUnusable;
			}
		}else if(status1.equals(PackageStatus.available)){
			if(status2.equals(PackageStatus.allocated)){
				eventType = StatusChangeEventType.packageAllocation;
			}else if(status2.equals(PackageStatus.unverified)){
				eventType = StatusChangeEventType.packageVerificationUndo;
			}
		}else if(status1.equals(PackageStatus.distributed)){
			if(status2.equals(PackageStatus.returned)){
				eventType = StatusChangeEventType.packageReturn;
			}if(status2.equals(PackageStatus.allocated)){
				eventType = StatusChangeEventType.packageDistributionUndo;
			}
		}else if(status1.equals(PackageStatus.unusable)){
			if(status2.equals(PackageStatus.unverified)){
				eventType = StatusChangeEventType.packageUnusableUndo;
			}
		}else if(status1.equals(PackageStatus.returned)){
			if(status2.equals(PackageStatus.distributed)){
				eventType = StatusChangeEventType.packageReturnUndo;
			}
		}else if(status1.equals(PackageStatus.allocated)){
			if(status2.equals(PackageStatus.distributed)){
				eventType = StatusChangeEventType.packageDistribution;
			}
		}
	
		
		return eventType;
	}
	
	
	public AdditionalStatusChangeEventInfo getAdditionalInfo(){
		
		AdditionalStatusChangeEventInfo info = null;
		switch(e.getStatusChangeEventEnum()){
		case packageAllocation:
			info = new AdditionalStatusChangeEventInfo("Participant identifier", e.getAdditionalInfo());
			break;
		case packageReturn:
			info = new AdditionalStatusChangeEventInfo("Number of pills returned", e.getAdditionalInfo());
			break;
		case packageVerificationUndo:
		case packageDistributionUndo:
		case packageReturnUndo:
		case packageUnusableUndo:
			info = new AdditionalStatusChangeEventInfo("Comment", e.getAdditionalInfo());
			break;
		default:
			//Do nothing.
			break;
				
		}
		
		return info;
	}
}
