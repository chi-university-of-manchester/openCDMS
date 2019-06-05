package org.psygrid.meds.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.psygrid.meds.events.InvalidEventException;
import org.psygrid.meds.export.MedsExportRequest;
import org.psygrid.meds.medications.InvalidMedicationPackageException;
import org.psygrid.meds.medications.MedicationPackageNotFoundException;
import org.psygrid.meds.medications.PackageInfo;
import org.psygrid.meds.medications.PackageInfoView;
import org.psygrid.meds.medications.ParticipantNotFoundException;
import org.psygrid.meds.project.InvalidProjectException;
import org.psygrid.meds.project.PharmacyInfo;
import org.psygrid.meds.project.ProjectInfo;


public interface Meds extends Remote {

	public void saveProject(ProjectInfo p, String saml) throws RemoteException;
	
	public void saveMedication(PackageInfo medInfo, String saml) throws RemoteException, InvalidMedicationPackageException;
	
	public void saveMedications(PackageInfo[] medInfo, String saml) throws RemoteException, InvalidMedicationPackageException;
	
	public PackageInfoView viewMedicationPackage(String packageIdentifier, String projectCode, String saml) throws RemoteException, InvalidProjectException, InvalidEventException, MedicationPackageNotFoundException;
	
	public PackageInfoView[] getMedicationPackagesForParticipant(String projectCode, String participantIdentifier, String saml) throws RemoteException, InvalidProjectException, InvalidEventException, ParticipantNotFoundException;
	
	public PackageInfoView[] getMedicationPackagesForProject(String projectCode, String saml) throws RemoteException, InvalidProjectException, InvalidEventException;
	
	public String allocateInitialPackage(String projectCode, String pharmacyIdentifier, String treatmentCode, String participantId, String saml) throws RemoteException;
	
	public String allocateSubsequentPackage(String projectcode, String participantIdentifier, String saml)  throws RemoteException, ParticipantNotFoundException;
	
	public void addPharmacyToProject(String projectCode, PharmacyInfo pharmInfo, String saml) throws RemoteException;
	
	public String[] getDistributablePackagesForUser(String projectCode, String participantIdentifier, String saml) throws RemoteException, ParticipantNotFoundException;
	
	public String distributeMedication(String projectCode, String medicationPackageId, String saml) throws RemoteException, MedicationPackageNotFoundException;
	
	public boolean undistributeMedication(String projectCode, String packageIdentifier, String saml) throws RemoteException;
	
	public boolean changeMedicationPackageStatus(String projectCode, String packageIdentifier, String currentStatus, String changedStatus, String additionalInformation, String saml) throws RemoteException, MedicationPackageNotFoundException;
	
	public boolean changeMedicationPackagesStatus(String projectCode, String[] packageIdentifiers, String currentStatus, String changedStatus, String additionalInformation, String saml)throws RemoteException, MedicationPackageNotFoundException;
	
	public boolean verifyMedicationPackagesStatus(String projectCode, String[] packageIdentifiers, String currentStatus, String changedStatus, String additionalInformation, String saml) throws RemoteException;
	
	public boolean unverifyMedicationPackagesSatus(String projectCode, String[] packageIdentifiers, String currentStatus, String changedStatus, String additionalInformation, String saml) throws RemoteException;
	
	public void requestMedsExport(MedsExportRequest request, String saml) throws RemoteException;
	
}
