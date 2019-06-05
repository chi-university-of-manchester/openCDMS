package org.psygrid.meds.rmi;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import javax.xml.rpc.ServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.AbstractClient;
import org.psygrid.meds.events.InvalidEventException;
import org.psygrid.meds.medications.InvalidMedicationPackageException;
import org.psygrid.meds.medications.MedicationPackageNotFoundException;
import org.psygrid.meds.medications.PackageInfo;
import org.psygrid.meds.medications.PackageInfoView;
import org.psygrid.meds.medications.PackageStatus;
import org.psygrid.meds.medications.ParticipantNotFoundException;
import org.psygrid.meds.project.InvalidProjectException;
import org.psygrid.meds.project.PharmacyInfo;
import org.psygrid.meds.project.ProjectInfo;


public class MedicationClient extends AbstractClient {

	private final static Log LOG = LogFactory.getLog(MedicationClient.class);
	
	private Meds getService() {
		MedsServiceLocator locator = new MedsServiceLocator();
		Meds service = null;
		
		try{
			if ( null == this.url ){
				service = locator.getmeds();
			}
			else{
				service = locator.getmeds(url);
			}
		}catch(ServiceException ex){
			//this can only happen if the repository was built with
			//an incorrect URL
			throw new RuntimeException("Repository URL is invalid!", ex);
		}
		if ( this.timeout >= 0 ){
			MedsSoapBindingStub stub  = (MedsSoapBindingStub)service;
			stub.setTimeout(this.timeout);
		}
		return service;
	}
	
	public void verifyMedsPackages(List<String> packageIdentifiers, String projectCode, String saml) throws RemoteException, MedicationPackageNotFoundException {
		
		int numPackageIdentifiers = packageIdentifiers.size();
		String[] packageIdentifiersArray = new String[numPackageIdentifiers];
		packageIdentifiersArray =packageIdentifiers.toArray(packageIdentifiersArray);
		
		Meds m = this.getService();
		try {
			m.changeMedicationPackagesStatus(projectCode, packageIdentifiersArray, PackageStatus.unverified.toString(), PackageStatus.available.toString(), null, saml);
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}

	}
	
	
	
	public void markMedsPackagesAsUnusable(List<String> packageIdentifiers, String projectCode, String saml) throws RemoteException, MedicationPackageNotFoundException {
		int numPackageIdentifiers = packageIdentifiers.size();
		String[] packageIdentifiersArray = new String[numPackageIdentifiers];
		packageIdentifiersArray =packageIdentifiers.toArray(packageIdentifiersArray);
		
		Meds m = this.getService();
		try {
			m.changeMedicationPackagesStatus(projectCode, packageIdentifiersArray, PackageStatus.unverified.toString(), PackageStatus.unusable.toString(), null, saml);
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}
	}
	
	public void returnMedicationPackage(String packageIdentifier, String projectCode, int numPillsReturned, String saml) throws RemoteException, MedicationPackageNotFoundException{
		Meds m = this.getService();
		try{
			m.changeMedicationPackageStatus(projectCode, packageIdentifier, PackageStatus.distributed.toString(), PackageStatus.returned.toString(), Integer.toString(numPillsReturned), saml);
		}catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}
		
	}
	
	public void undoMedicationPackageReturn(String projectCode, String packageIdentifier, String saml) throws RemoteException, MedicationPackageNotFoundException{
		
		Meds m = this.getService();
		try {
			m.changeMedicationPackageStatus(projectCode, packageIdentifier, PackageStatus.returned.toString(), PackageStatus.distributed.toString(), null, saml);
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}
		
	}
	
	public PackageInfoView viewMedicationPackage(String packageIdentifier, String projectCode, String saml) throws RemoteException, InvalidProjectException, InvalidEventException, MedicationPackageNotFoundException{
		
		PackageInfoView pInfo = null;
		
		Meds m = this.getService();
		try{
			pInfo = m.viewMedicationPackage(packageIdentifier, projectCode, saml);
		}catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}
		
		return pInfo;
		
	}
	
	public List<PackageInfoView> viewMedicationPackagesForParticipant(String projectCode, String participantIdentifier, String saml) throws InvalidProjectException, InvalidEventException, RemoteException, ParticipantNotFoundException{
		
		List<PackageInfoView> packages = null;
		PackageInfoView[] packagesArray = null;
		Meds m = this.getService();
		packagesArray = m.getMedicationPackagesForParticipant(projectCode, participantIdentifier, saml);
		
		return Arrays.asList(packagesArray);
		
	}
	
	public List<PackageInfoView> viewMedicationPackagesForProject(String projectCode, String saml) throws RemoteException, InvalidProjectException, InvalidEventException{
		
		List<PackageInfoView> packages = null;
		PackageInfoView[] packagesArray = null;
		Meds m = this.getService();
		packagesArray = m.getMedicationPackagesForProject(projectCode, saml);
 		
		return Arrays.asList(packagesArray);
	}
	
	public String allocateInitialMedicationPackage(String projectCode, String pharmacyIdentifier, String treatmentCode, String participantId, String saml) throws RemoteException{
		
		String allocatedPackageId = null;
		
		Meds m = this.getService();
		try {
			allocatedPackageId = m.allocateInitialPackage(projectCode, pharmacyIdentifier, treatmentCode, participantId, saml);
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}
		
		return allocatedPackageId;
	}
	
	public String allocateSubsequentMedicationPackage(String projectCode, String participantId, String saml) throws RemoteException, ParticipantNotFoundException{
		
		String allocatedPackageId = null;
		
		Meds m = this.getService();
		try {
			allocatedPackageId = m.allocateSubsequentPackage(projectCode, participantId, saml);
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}
		
		return allocatedPackageId;
		
	}
	
	public List<String> getDistributablePackagesForUser(String projectCode, String participantId, String saml) throws RemoteException, ParticipantNotFoundException{
		Meds m = this.getService();
		String[] packages = null;
		try{
			packages = m.getDistributablePackagesForUser(projectCode, participantId, saml);
		}catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}
		
		return Arrays.asList(packages);
	}
	
	public String distributeMedicationPackage(String projectCode, String medicationPackageId, String saml) throws RemoteException, MedicationPackageNotFoundException {
		
		String allocatedPackageId = null;
		
		Meds m = this.getService();
		try {
			allocatedPackageId = m.distributeMedication(projectCode, medicationPackageId, saml);
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}
		
		return allocatedPackageId;
		
	}
	
	public void undoMedicationPackageDistribution(String projectCode, String packageIdentifier, String saml) throws RemoteException{
		Meds m = this.getService();
		try {
			m.undistributeMedication(projectCode, packageIdentifier, saml);
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}
		
	}
	
	public void saveMedicationProject(ProjectInfo info, String saml) throws RemoteException{
		
		Meds m = this.getService();
		try {
			m.saveProject(info, saml);
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}
		
	}
	
	public void addPharmacyToProject(String projectCode, PharmacyInfo pharmInfo, String saml) throws RemoteException{
		Meds m = this.getService();
		try {
			m.addPharmacyToProject(projectCode, pharmInfo, saml);
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		}
	}
	
	public void saveMedicationPackage(PackageInfo p, String saml) throws  RemoteException, InvalidMedicationPackageException{
		
		Meds m = this.getService();
		try {
			m.saveMedication(p, saml);
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		} catch (InvalidMedicationPackageException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw ex;
		}
		
	}
	
	public void saveMedicationPackages(List<PackageInfo> packages, String saml) throws  RemoteException, InvalidMedicationPackageException{
		
		Meds m = this.getService();
		try {
			PackageInfo[] packagesArray = new PackageInfo[packages.size()];
			packagesArray = packages.toArray(packagesArray);
			m.saveMedications(packagesArray, saml);
		} catch (RemoteException e) {
			LOG.fatal(e.getMessage(), e);
			throw e;
		} catch (InvalidMedicationPackageException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw ex;
		}
		
	}
	


	
	
}
