package org.psygrid.meds.medications;

import java.util.List;

import org.psygrid.meds.events.EventObjectTranslator;
import org.psygrid.meds.events.InvalidEventException;
import org.psygrid.meds.events.MedsPackageStatusChangeEvent;
import org.psygrid.meds.events.MedsPackageStatusChangeEventInfo;
import org.psygrid.meds.events.PackageViewEvent;
import org.psygrid.meds.events.PackageViewEventInfo;
import org.psygrid.meds.project.InvalidProjectException;
import org.psygrid.meds.project.Pharmacy;
import org.psygrid.meds.project.PharmacyInfo;
import org.psygrid.meds.project.Project;
import org.psygrid.meds.project.ProjectObjectTranslator;
import org.psygrid.meds.project.Treatment;
import org.psygrid.meds.project.TreatmentInfo;

public class MedicationObjectTranslator {
	
	public static MedicationPackage translatePackageInfoToMedicationPackage(PackageInfo info, Project project) throws InvalidMedicationPackageException{
		
		info.validate();
				
		MedicationPackage p = new MedicationPackage();
		p.setProjectCode(info.getProjectCode());
		p.setPackageId(info.getPackageIdentifier());
		
		p.setBatchNumber(info.getBatchNumber());
		p.setShipmentNumber(info.getShipmentNumber());
		p.setExpiryDate(info.getExpiryDate());
		p.setQpRelease(info.getQpRelease());
		
		Pharmacy pharmacy = MedicationObjectTranslator.getPharmacyFromPharmacyInfo(project, info.getPharmacyInfo());
		if(pharmacy == null){
			throw new InvalidMedicationPackageException("Pharmacy code: " + info.getPharmacyInfo().getPharmacyCode() + " does not match db contents.");
		}
		
		Treatment t = MedicationObjectTranslator.getTreatmentFromTreatmentInfo(project, info.getTreatmentInfo());
		if(t == null){
			throw new InvalidMedicationPackageException("Treatment code: " + info.getTreatmentInfo().getTreatmentCode() + " does not match db contents.");
		}
		
		p.setPharmacy(pharmacy);
		p.setTreatment(t);
		return p;
	}
	
	private static Treatment getTreatmentFromTreatmentInfo(Project p, TreatmentInfo tInfo){
		
		Treatment returnTreatment = null;
		List<Treatment> treatments = p.getTreatments();
		for(Treatment t : treatments){
			if(t.getTreatmentCode().equals(tInfo.getTreatmentCode())){
				returnTreatment = t;
				break;
			}
		}
		
		return returnTreatment;
	}
	
	private static Pharmacy getPharmacyFromPharmacyInfo(Project p, PharmacyInfo pharmInfo){
		Pharmacy returnedPharmacy = null;
		
		//Search through the centres to find the pharmacy id.
		List<Pharmacy> pharmacies = p.getPharmacies();
		for(Pharmacy ph: pharmacies){
			if(ph.getPharmacyCode().equals(pharmInfo.getPharmacyCode())){
				returnedPharmacy = ph;
				break;
			}
		}
		
		return returnedPharmacy;
	}
	
	public static PackageInfoView translateMedicationPackageToPackageInfoView(MedicationPackage medPackage, boolean getViewEvents, boolean getStatusChangeEvents) throws InvalidProjectException, InvalidEventException {
		
		PackageInfo info = new PackageInfo();
		PharmacyInfo pInfo = ProjectObjectTranslator.translatePharmacyToPharmacyInfo(medPackage.getPharmacy());
		info.setPharmacyInfo(pInfo);
		TreatmentInfo tInfo = ProjectObjectTranslator.translateTreatmentToTreatmentInfo(medPackage.getTreatment());
		info.setTreatmentInfo(tInfo);
		
		info.setPackageIdentifier(medPackage.getPackageId());
		info.setProjectCode(medPackage.getProjectCode());
		info.setBatchNumber(medPackage.getBatchNumber());
		info.setShipmentNumber(medPackage.getShipmentNumber());
		info.setExpiryDate(medPackage.getExpiryDate());
		info.setQpRelease(medPackage.getQpRelease());
		
		PackageStatus s = medPackage.getStatusEnum();
		info.setPackageStatus(s.toString());
		
		List<PackageViewEvent> viewEvents = medPackage.getViewEvents();
		List<MedsPackageStatusChangeEvent> statusChangeEvents = medPackage.getStatusChangeEvents();
		
		PackageInfoView pInfoView = new PackageInfoView(info);
		pInfoView.setViewEventsIncluded(getViewEvents);
		pInfoView.setStatusChangeEventsIncluded(getStatusChangeEvents);
		
		int numPackageViewEvents = viewEvents.size();
		int numStatusChangeEvents = statusChangeEvents.size();
		
		if(getStatusChangeEvents){
			MedsPackageStatusChangeEventInfo[] changeEventsArray = new MedsPackageStatusChangeEventInfo[numStatusChangeEvents];
			int counter = 0;
			for(MedsPackageStatusChangeEvent e: statusChangeEvents){
				changeEventsArray[counter] = EventObjectTranslator.translateMedsPackageStatusChangeEventToMedsPackageStatusChangeEventInfo(e);
				counter++;
			}
			
			pInfoView.setStatusChangeEvents(changeEventsArray);
		}

		if(getViewEvents){
			PackageViewEventInfo[] viewEventsArray = new PackageViewEventInfo[numPackageViewEvents];
			int counter = 0;
			for(PackageViewEvent e: viewEvents){
				viewEventsArray[counter] = EventObjectTranslator.translatePackageViewEventInfoFromPackageViewEvent(e);
				counter++;
			}
			
			pInfoView.setViewEvents(viewEventsArray);
		}
		
		return pInfoView;
	}

}
