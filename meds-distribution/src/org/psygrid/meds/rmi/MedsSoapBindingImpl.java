/**
 * MedsSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package org.psygrid.meds.rmi;

import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;

import org.psygrid.common.email.EmailDAO;
import org.psygrid.meds.events.InvalidEventException;
import org.psygrid.meds.export.MedsExportRequest;
import org.psygrid.meds.medications.InvalidMedicationPackageException;
import org.psygrid.meds.medications.MedicationPackageNotFoundException;
import org.psygrid.meds.medications.PackageInfo;
import org.psygrid.meds.medications.PackageInfoView;
import org.psygrid.meds.medications.ParticipantNotFoundException;
import org.psygrid.meds.project.InvalidProjectException;
import org.psygrid.meds.project.PharmacyInfo;
import org.psygrid.services.SecureSoapBindingImpl;
import org.springframework.context.ApplicationContext;




public class MedsSoapBindingImpl extends  SecureSoapBindingImpl implements org.psygrid.meds.rmi.Meds{

	Meds service = null;
	

	
	protected void onInit() throws ServiceException {
		super.onInit();
		ApplicationContext  c = this.getApplicationContext();
		//projectDao = (ProjectDao)c.getBean("myProjectDAO");
		//medsDao = (MedicationPackageDao)c.getBean("myMedsDAO");
		service = (Meds)c.getBean("medsService");
		
	}
	
	
	
    
	public void saveProject(org.psygrid.meds.project.ProjectInfo in0, String saml) throws java.rmi.RemoteException {
    	service.saveProject(in0, saml);
    }

	
	
	public void saveMedication(PackageInfo medInfo, String saml) throws RemoteException, InvalidMedicationPackageException {
		service.saveMedication(medInfo, saml);			
	}


	
	public PackageInfoView viewMedicationPackage(String packageIdentifier,
			String projectCode, String saml) throws RemoteException, InvalidProjectException, InvalidEventException, MedicationPackageNotFoundException{

		return service.viewMedicationPackage(packageIdentifier, projectCode, saml);		
	}
	
	
	public PackageInfoView[] getMedicationPackagesForParticipant(String projectCode, String participantIdentifier, String saml) throws RemoteException, InvalidProjectException, InvalidEventException, ParticipantNotFoundException{
		
		return service.getMedicationPackagesForParticipant(projectCode, participantIdentifier, saml);
	}

	
	
	public String allocateInitialPackage(String projectCode, String pharmacyIdentifier,
			String treatmentCode, String participantId, String saml) throws RemoteException {
		
		return service.allocateInitialPackage(projectCode, pharmacyIdentifier, treatmentCode, participantId, saml);
		
	}
	
	
	public String[] getDistributablePackagesForUser(String projectCode, String participantIdentifier, String saml) throws RemoteException, ParticipantNotFoundException {
		return service.getDistributablePackagesForUser(projectCode, participantIdentifier, saml);
	}
	
	
	public String allocateSubsequentPackage(String projectCode,
			String participantIdentifier, String saml) throws RemoteException, ParticipantNotFoundException{
		
		return service.allocateSubsequentPackage(projectCode, participantIdentifier, saml);
		
	}
	
	

	
	
	public void addPharmacyToProject(String projectCode, PharmacyInfo pharmInfo, String saml) throws RemoteException{
		
		service.addPharmacyToProject(projectCode, pharmInfo, saml);

	}

	
	
	public String distributeMedication(String projectCode, String medicationPackageId, String saml)
			throws RemoteException, MedicationPackageNotFoundException {
		
		return service.distributeMedication(projectCode, medicationPackageId, saml);
	}

	
	
	public boolean undistributeMedication(String projectCode,
			String packageIdentifier, String saml) throws RemoteException {

		return service.undistributeMedication(projectCode, packageIdentifier, saml);
	}

	
	
	public boolean changeMedicationPackageStatus(String projectCode,
			String packageIdentifier, String currentStatus,
			String changedStatus, String additionalInformation, String saml)
			throws RemoteException, MedicationPackageNotFoundException {
		
			return service.changeMedicationPackageStatus(projectCode, packageIdentifier, currentStatus, changedStatus, additionalInformation, saml);
	}

	
	public boolean changeMedicationPackagesStatus(String projectCode,
			String[] packageIdentifiers, String currentStatus,
			String changedStatus, String additionalInformation, String saml) throws RemoteException, MedicationPackageNotFoundException{
	
		return service.changeMedicationPackagesStatus(projectCode, packageIdentifiers, currentStatus, changedStatus, additionalInformation, saml);
	}



	
	public boolean verifyMedicationPackagesStatus(String projectCode,
			String[] packageIdentifiers, String currentStatus,
			String changedStatus, String additionalInformation, String saml)
			throws RemoteException {
		
		return service.verifyMedicationPackagesStatus(projectCode, packageIdentifiers, currentStatus, changedStatus, additionalInformation, saml);
	}



	
	public boolean unverifyMedicationPackagesSatus(String projectCode,
			String[] packageIdentifiers, String currentStatus,
			String changedStatus, String additionalInformation, String saml)
			throws RemoteException {
		return service.unverifyMedicationPackagesSatus(projectCode, packageIdentifiers, currentStatus, changedStatus, additionalInformation, saml);
	}



	
	public PackageInfoView[] getMedicationPackagesForProject(
			String projectCode, String saml) throws RemoteException, InvalidProjectException, InvalidEventException {
		
		return service.getMedicationPackagesForProject(projectCode, saml);
		
	}



	
	public void requestMedsExport(MedsExportRequest request, String saml)
			throws RemoteException {
		service.requestMedsExport(request, saml);
	}



	
	public void saveMedications(PackageInfo[] medInfo, String saml)
			throws RemoteException, InvalidMedicationPackageException {
		service.saveMedications(medInfo, saml);
		
	}





}
