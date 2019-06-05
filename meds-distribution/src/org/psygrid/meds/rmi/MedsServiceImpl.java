package org.psygrid.meds.rmi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.logging.AuditLogger;
import org.psygrid.meds.utils.security.NotAuthorisedFault;
import org.psygrid.meds.actions.notify.EmailUtility;
import org.psygrid.meds.actions.notify.NotificationType;
import org.psygrid.meds.events.InvalidEventException;
import org.psygrid.meds.events.MedsPackageStatusChangeEventInterpreter;
import org.psygrid.meds.events.StatusChangeEventType;
import org.psygrid.meds.export.MedsExportRequest;
import org.psygrid.meds.medications.InvalidMedicationPackageException;
import org.psygrid.meds.medications.MedicationObjectTranslator;
import org.psygrid.meds.medications.MedicationPackage;
import org.psygrid.meds.medications.MedicationPackageNotFoundException;
import org.psygrid.meds.medications.PackageInfo;
import org.psygrid.meds.medications.PackageInfoView;
import org.psygrid.meds.medications.PackageStatus;
import org.psygrid.meds.medications.ParticipantNotFoundException;
import org.psygrid.meds.project.InvalidProjectException;
import org.psygrid.meds.project.Pharmacy;
import org.psygrid.meds.project.PharmacyInfo;
import org.psygrid.meds.project.PharmacyInfoCodeOnly;
import org.psygrid.meds.project.Project;
import org.psygrid.meds.project.ProjectInfo;
import org.psygrid.meds.project.ProjectObjectTranslator;
import org.psygrid.meds.project.Treatment;
import org.psygrid.meds.project.TreatmentInfo;
import org.psygrid.meds.project.TreatmentInfoCodeOnly;
import org.psygrid.meds.utils.AbstractServiceImpl;
import org.psygrid.meds.utils.LdapParser;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.RBACAction;
import org.psygrid.security.RBACRole;
import org.psygrid.security.accesscontrol.AEFGroup;
import org.psygrid.security.accesscontrol.AEFProject;
import org.psygrid.security.accesscontrol.IAccessEnforcementFunction;
import org.psygrid.security.attributeauthority.service.InputFaultMessage;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.attributeauthority.service.ProcessingFaultMessage;
import org.psygrid.www.xml.security.core.types.GroupAttributeType;
import org.psygrid.www.xml.security.core.types.RoleType;

public class MedsServiceImpl extends AbstractServiceImpl implements Meds{
	
	
	
	
	/**
	 * General purpose logger
	 */
	private static Log sLog = LogFactory.getLog(MedsServiceImpl.class);
	
    
	/**
	 * Audit logger
	 */
	private static AuditLogger logHelper = new AuditLogger(MedsServiceImpl.class);
	
	/**
	 * Gets the user name from the supplied saml assertion.
	 * @param saml
	 * @return the user name
	 */
	protected String findUserName(String saml){
        
        //find invoker's username
        String userName = null;
        try{
            userName = accessControl.getUserFromUnverifiedSAML(saml);
        }
        catch(PGSecurityException ex){
            userName = "Unknown";
        }
        return userName;
    }


	
	protected String getComponentName() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void saveProject(ProjectInfo p, String saml) throws RemoteException {
		
		//You need to be a System Administrator in the SYSTEM project in order to do this.
		
    	String userName = findUserName(saml);
    	
    	Project proj = ProjectObjectTranslator.translateProjectInfoToProject(p);
    	String projectCode = p.getProjectCode();
    	
    	proj.setCreationDate(new Date());
    	
    	this.projectDao.saveProject(proj);
		
	}

	
	public void saveMedication(PackageInfo medInfo, String saml)
			throws RemoteException, InvalidMedicationPackageException {
		
		this.checkPermissionsByProject(saml, "saveMedication", RBACAction.ACTION_DR_IMPORT_DATA, medInfo.getProjectCode());
		
		String projectCode = medInfo.getProjectCode();
		Project project = projectDao.getProject(projectCode);
		
		if(project == null){
			throw new InvalidMedicationPackageException("Medication package's project code: " + projectCode + " does not match any projects currently in the database.");
		}
		
		MedicationPackage p = MedicationObjectTranslator.translatePackageInfoToMedicationPackage(medInfo, project);
		medsDao.saveMedicationPackage(p);
		
	}
	
	
	public void saveMedications(PackageInfo[] medInfo, String saml)
			throws RemoteException, InvalidMedicationPackageException {
		//Make sure that every package has the same project code.
		String projectCode = medInfo[0].getProjectCode();
		
		List<PackageInfo> medInfoList = Arrays.asList(medInfo);
		
		for(PackageInfo info : medInfoList){
			String thisProjectCode = info.getProjectCode();
			if(thisProjectCode.equals(projectCode) == false){
				throw new InvalidMedicationPackageException("Not all medication packages in the input array have the same project code.");
			}
		}
		
		this.checkPermissionsByProject(saml, "saveMedication", RBACAction.ACTION_DR_IMPORT_DATA, projectCode);
		
		Project project = projectDao.getProject(projectCode);
		
		if(project == null){
			throw new InvalidMedicationPackageException("Medication package's project code: " + projectCode + " does not match any projects currently in the database.");
		}
		
		for(PackageInfo p : medInfoList){
			PharmacyInfo pH = p.getPharmacyInfo();
			PharmacyInfoCodeOnly pHCodeOnly = new PharmacyInfoCodeOnly(pH.getPharmacyCode());
			p.setPharmacyInfo(pHCodeOnly);
			
			TreatmentInfo tI = p.getTreatmentInfo();
			TreatmentInfoCodeOnly tICodeOnly = new TreatmentInfoCodeOnly(tI.getTreatmentCode());
			p.setTreatmentInfo(tICodeOnly);
		}
		
		for(PackageInfo info : medInfoList){
			MedicationPackage p = MedicationObjectTranslator.translatePackageInfoToMedicationPackage(info, project);
			medsDao.saveMedicationPackage(p);
		}
		
	}

	
	/**
	 * Returns view information about a medication package. It does NOT return view events or status change events.
	 * The caller must either be a CRO belonging to the centre that the package is in
	 * or they must be a Pharmacist belonging to the same pharmacy the package is in.
	 */
	public PackageInfoView viewMedicationPackage(String packageIdentifier,
			String projectCode, String saml) throws RemoteException,
			InvalidProjectException, InvalidEventException, MedicationPackageNotFoundException {
		
		
		String userName = this.findUserName(saml);
		
		Pharmacy ph = medsDao.getPharmacyOfMedicationPackage(packageIdentifier);
		String pharmacyCode = ph.getPharmacyCode();
		
		if(this.userIsAPharmacist(projectCode, userName)){
			this.checkPermissionsByPharmacy(saml, "viewMedicationPackage", RBACAction.ACTION_MD_VIEW_MEDS_PACKAGE, projectCode, pharmacyCode);
		}
		
		MedicationPackage p = medsDao.getMedicationPackage(packageIdentifier, projectCode);
		PackageInfoView pInfoView = null;
		try {
			pInfoView = MedicationObjectTranslator.translateMedicationPackageToPackageInfoView(p, false, false);
		} catch (InvalidProjectException e) {
			// TODO Auto-generated catch block
			throw e;
		} catch (InvalidEventException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		
		eventDao.registerViewEvent(p, userName);
		
		return pInfoView;

	}

	
	/**
	 * Retrieves all packages for a given participant. Includes status change events but not view events.
	 * User must be either CRO, Pharmacist, or PM.
	 * If a CRO then they must be in the same centre that the partcipant's pharmacy is in.
	 * If a Pharmacist then they must be in the same pharmacy as the participant. 
	 * If a PM then there are no restrictions.
	 */
	public PackageInfoView[] getMedicationPackagesForParticipant(
			String projectCode, String participantIdentifier, String saml)
			throws RemoteException, InvalidProjectException,
			InvalidEventException, ParticipantNotFoundException {
		
		
		String user = this.findUserName(saml);
		
		boolean  isAPharmacist = this.userIsAPharmacist(projectCode, user);
		
		MedicationPackage pack = medsDao.getLastAllocatedPackageForUser(participantIdentifier);
		String pharmacyCode = pack.getPharmacy().getPharmacyCode();
		
		if(isAPharmacist){
			this.checkPermissionsByPharmacy(saml, "getMedicationPackagesForParticipant", RBACAction.ACTION_MD_VIEW_MEDS_PACKAGE, projectCode, pharmacyCode);
		}else{
			this.checkPermissionsByIdentifierToGroupLevel(saml, "getMedicationPackagesForParticipant", RBACAction.ACTION_MD_VIEW_MEDS_PACKAGE, participantIdentifier);
		}
		
		List<MedicationPackage> packages = medsDao.getMedicationPackagesForParticipant(projectCode, participantIdentifier);
		List<PackageInfoView> packageInfoList = new ArrayList<PackageInfoView>();
		for(MedicationPackage p: packages){
			try {
				packageInfoList.add(MedicationObjectTranslator.translateMedicationPackageToPackageInfoView(p, false, true));
			} catch (InvalidProjectException e) {
				// TODO Auto-generated catch block
				throw e;
			} catch (InvalidEventException e) {
				// TODO Auto-generated catch block
				throw e;
			}
		}
		
		eventDao.registerViewEvents(packages, user);
		
		PackageInfoView[] pivArray = new PackageInfoView[packageInfoList.size()];
		return packageInfoList.toArray(pivArray);
	}

	
	/**
	 * This is a break-in method, allowing all package information, including all view and workflow events,
	 * to be retrieved.
	 * The user must be Project Manager.
	 */
	public PackageInfoView[] getMedicationPackagesForProject(
			String projectCode, String saml) throws RemoteException,
			InvalidProjectException, InvalidEventException {
		
		String userName = findUserName(saml);
		
		checkPermissionsByProject(saml, "getMedicationPackagesForProject", RBACAction.ACTION_MD_VIEW_PROJECT_PACKAGES, projectCode);
		
		List<MedicationPackage> packages = medsDao.getMedicationPackagesForProject(projectCode);
		List<PackageInfoView> packageViewList = new ArrayList<PackageInfoView>();
		for(MedicationPackage p : packages){
			packageViewList.add(MedicationObjectTranslator.translateMedicationPackageToPackageInfoView(p, true, true));
		}
		
		eventDao.registerViewEvents(packages, userName);
		
		PackageInfoView[] pInfoViewArray = new PackageInfoView[packageViewList.size()];
		return packageViewList.toArray(pInfoViewArray);
	}

	
	/**
	 * Allocates an initial medication package to a participant.
	 * The user must be a CRO and must belong to the centre that includes the pharmacy.
	 */
	public String allocateInitialPackage(String projectCode,
			String pharmacyIdentifier, String treatmentCode,
			String participantId, String saml) throws RemoteException {
		
		this.checkPermissionsByGroupSpecifiedByPharmacy(saml, "allocateInitialPackage", RBACAction.ACTION_MD_ALLOCATE_MEDS, projectCode, pharmacyIdentifier);
		
		String userName = findUserName(saml);
		String allocatedPackageId = medsDao.allocateMedicationPackage(projectCode, pharmacyIdentifier, treatmentCode, participantId, userName);
		
		Map<String,String> emailParams = new HashMap<String,String>();
		emailParams.put("%participantIdentifier%", participantId);
		emailParams.put("%pharmacyCode%", pharmacyIdentifier);
		emailParams.put("%packageIdentifier%", allocatedPackageId);
		
		getEmailUtility().createEmail(projectCode, pharmacyIdentifier, emailParams, NotificationType.medsAllocation);
		
		return allocatedPackageId;

	}

	
	/**
	 * Allocate more medication to a user.
	 * Caller must be a CRO and must belong to the centre that includes the pharmacy.
	 */
	public String allocateSubsequentPackage(String projectcode,
			String participantIdentifier, String saml) throws RemoteException, ParticipantNotFoundException {
		
		MedicationPackage p = medsDao.getLastAllocatedPackageForUser(participantIdentifier);
		Pharmacy ph = p.getPharmacy();
		Treatment t = p.getTreatment();
		
		String userName = findUserName(saml);
		
		this.checkPermissionsByGroupSpecifiedByPharmacy(saml, "allocateSubsequentPackage", RBACAction.ACTION_MD_ALLOCATE_MEDS, projectcode, ph.getPharmacyCode());
		
		String packageIdentifier = medsDao.allocateMedicationPackage(projectcode, ph.getPharmacyCode(), t.getTreatmentCode(), participantIdentifier, userName);
		
		Map<String,String> emailParams = new HashMap<String,String>();
		emailParams.put("%participantIdentifier%", participantIdentifier);
		emailParams.put("%pharmacyCode%", ph.getPharmacyCode());
		emailParams.put("%packageIdentifier%", packageIdentifier);
		
		getEmailUtility().createEmail(projectcode, ph.getPharmacyCode(), emailParams, NotificationType.medsAllocation);
		
		return packageIdentifier;
	}

	
	/**
	 * Must be a Project Manager in order to do this.
	 */
	public void addPharmacyToProject(String projectCode,
			PharmacyInfo pharmInfo, String saml) throws RemoteException {
		
		this.checkPermissionsByProject(saml, "addPharmacyToProject", RBACAction.ACTION_MD_ADD_PHARMACY_TO_MEDS_PROJECT, projectCode);
		
		projectDao.addPharmacyToProject(projectCode, pharmInfo);
	}

	
	public String[] getDistributablePackagesForUser(String projectCode,
			String participantIdentifier, String saml) throws RemoteException, ParticipantNotFoundException {
		
		//If this is a pharmacist then the privilege must be constrained to pharmacy level.
		//Otherwise, constrain to group level.
		
		String user = this.findUserName(saml);
		
		if(this.userIsAPharmacist(projectCode, user)){
			this.checkPermissionsByIdentifierToPharmacyLevel(saml, "getDistributablePackagesForUser", RBACAction.ACTION_MD_VIEW_MEDS_PACKAGE, participantIdentifier);
		}else{
			this.checkPermissionsByIdentifierToGroupLevel(saml, "getDistributedPackagesForUser", RBACAction.ACTION_MD_VIEW_MEDS_PACKAGE, participantIdentifier);
		}
		
		MedicationPackage p = medsDao.getLastAllocatedPackageForUser(participantIdentifier);
		String[] array = new String[]{p.getPackageId()};
		
		//Register a view event on the package.
		eventDao.registerViewEvent(p, user);
		
		return array;
	}

	
	/**
	 * Must be a pharmacist and you must be affiliated to the pharmacy to which the medication package is affiliated..
	 */
	public String distributeMedication(String projectCode,
			String medicationPackageId, String saml) throws RemoteException, MedicationPackageNotFoundException {
		String userName = findUserName(saml);
		
		Pharmacy ph = medsDao.getPharmacyOfMedicationPackage(medicationPackageId);
		String pharmacyCode = ph.getPharmacyCode();
		
		checkPermissionsByPharmacy(saml, "distributeMedication", RBACAction.ACTION_MD_MEDS_PHARMACY_WORKFLOW, projectCode, pharmacyCode);
		String distributedPackageId = this.medsDao.distributeMedication(projectCode, medicationPackageId, userName);
		
		return distributedPackageId;
	}

	
	public boolean undistributeMedication(String projectCode,
			String packageIdentifier, String saml) throws RemoteException {
		String userName = findUserName(saml);
		boolean  success = this.medsDao.undistributeMedication(projectCode, packageIdentifier, userName);
		
		return success;

	}

	
	public boolean changeMedicationPackageStatus(String projectCode,
			String packageIdentifier, String currentStatus,
			String changedStatus, String additionalInformation, String saml)
			throws RemoteException, MedicationPackageNotFoundException {
				
		String userName = findUserName(saml);
		
		StatusChangeEventType eventType = MedsPackageStatusChangeEventInterpreter.assessEventType(PackageStatus.valueOf(currentStatus), PackageStatus.valueOf(changedStatus));
		String eventTypeString = eventType.toString();
		
		boolean changeIsACorrection = false;
		if(eventTypeString.endsWith("Undo")){
			changeIsACorrection = true;
		}
		
		if(changeIsACorrection){
			this.checkPermissionsByProject(saml, "changeMedicationPackageStatus", RBACAction.ACTION_MD_MEDS_WORKFLOW_CORRECTION, projectCode);
		}else{
			//This is a pharmacist, so permission has to be down to the pharmacy level.
			Pharmacy p = medsDao.getPharmacyOfMedicationPackage(packageIdentifier);
			String pharmacyCode = p.getPharmacyCode();
			this.checkPermissionsByPharmacy(saml, "changeMedicationPackageStatus", RBACAction.ACTION_MD_MEDS_PHARMACY_WORKFLOW, projectCode, pharmacyCode);
		}
		
		List<String> packageIdentifiers = new ArrayList<String>();
		packageIdentifiers.add(packageIdentifier);
		
		medsDao.changeMedicationPackagesStatus(projectCode, packageIdentifiers, currentStatus, changedStatus, additionalInformation, userName);
		return true;
	}

	
	public boolean changeMedicationPackagesStatus(String projectCode,
			String[] packageIdentifiers, String currentStatus,
			String changedStatus, String additionalInformation, String saml)
			throws RemoteException, MedicationPackageNotFoundException {
		

		String userName = findUserName(saml);
		
		MedicationPackage p = medsDao.getMedicationPackage(packageIdentifiers[0], projectCode);
		if(p == null){
			throw new MedicationPackageNotFoundException("Medication package " + packageIdentifiers[0] + " not found in database.");
		}
		
		String pharmacyCode = p.getPharmacy().getPharmacyCode();
		
		checkPermissionsByPharmacy(saml, "changeMedicationPackagesStatus", RBACAction.ACTION_MD_MEDS_PHARMACY_WORKFLOW, projectCode, pharmacyCode);
		
		List<String> packageIds = Arrays.asList(packageIdentifiers);
		medsDao.changeMedicationPackagesStatus(projectCode, packageIds, currentStatus, changedStatus, additionalInformation, userName);
		return true;
	}

	
	public boolean verifyMedicationPackagesStatus(String projectCode,
			String[] packageIdentifiers, String currentStatus,
			String changedStatus, String additionalInformation, String saml)
			throws RemoteException {

		//Change the medication packages status.
		//Then change the QP release flag to true.
		List<String> packageIds = Arrays.asList(packageIdentifiers);
		medsDao.changeMedicationPackagesStatus(projectCode, packageIds, currentStatus, changedStatus, additionalInformation, "sample user");
		medsDao.setMedicationPackageQPReleaseFlag(projectCode, packageIds, true, "sample user");
		return false;
	}

	
	public boolean unverifyMedicationPackagesSatus(String projectCode,
			String[] packageIdentifiers, String currentStatus,
			String changedStatus, String additionalInformation, String saml)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void requestMedsExport(MedsExportRequest request, String saml)
			throws RemoteException {
		
		this.checkPermissionsByProject(saml, "requestMedsExport", RBACAction.ACTION_MD_REQUEST_MEDS_EXPORT_FOR_PROJECT, request.getProjectCode());
		
		this.getMedsExportDao().saveExportRequest(request);
	}



	
}
