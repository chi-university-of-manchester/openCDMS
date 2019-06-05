import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.psygrid.common.email.Email;
import org.psygrid.common.simplemap.Pair;
import org.psygrid.meds.actions.notify.EmailType;
import org.psygrid.meds.medications.InvalidMedicationPackageException;
import org.psygrid.meds.medications.PackageInfo;
import org.psygrid.meds.project.EmailInfo;
import org.psygrid.meds.project.InvalidProjectException;
import org.psygrid.meds.project.PharmacyInfo;
import org.psygrid.meds.project.ProjectInfo;
import org.psygrid.meds.project.TreatmentInfo;
import org.psygrid.meds.rmi.MedicationClient;


public class BasicClientTest {

	public BasicClientTest(){
		
	}
	
	/**
	 * @param args
	 * @throws InvalidProjectException 
	 */
	public static void main(String[] args) throws InvalidProjectException {

		BasicClientTest test = new BasicClientTest();
		test.saveNewProject();
		/*
		for(int i = 1000; i < 1005; i++){
			String pkg = "A99_" + Integer.valueOf(i).toString();
			test.saveMedicationPackage(pkg);
			String pkg2 = "B99_" + Integer.valueOf(i).toString();
			test.saveMedicationPackage2(pkg2);
		}
		*/


	}
	
	protected void saveMedicationPackage(String packageIdentifier) throws InvalidProjectException{
		
		MedicationClient c = new MedicationClient();
		
		PackageInfo p =  new PackageInfo();
		p.setProjectCode("AAA1");
		p.setPackageIdentifier(packageIdentifier);
		PharmacyInfo pInfo = new PharmacyInfo("Pharmacy1", "001_1");
		TreatmentInfo tInfo = new TreatmentInfo("Control", "001");
		
		p.setPharmacyInfo(pInfo);
		p.setTreatmentInfo(tInfo);
		
		String saml = "testme";
		
		try {
			c.saveMedicationPackage(p, saml);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(InvalidMedicationPackageException e){
			e.printStackTrace();
		}
		
		
	}
	
protected void saveMedicationPackage2(String packageIdentifier) throws InvalidProjectException{
		
		MedicationClient c = new MedicationClient();
		
		PackageInfo p =  new PackageInfo();
		p.setProjectCode("AAA1");
		p.setPackageIdentifier(packageIdentifier);
		PharmacyInfo pInfo = new PharmacyInfo("Pharmacy2_name", "Pharmacy2_code");
		TreatmentInfo tInfo = new TreatmentInfo("Pain", "002");
		
		p.setPharmacyInfo(pInfo);
		p.setTreatmentInfo(tInfo);
		
		String saml = "testme";
		
		try {
			c.saveMedicationPackage(p, saml);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidMedicationPackageException e){
			e.printStackTrace();
		}
		
		
	}

	protected void saveNewProject() throws InvalidProjectException{
		// TODO Auto-generated method stub
		String projectCode = "AAA1";
		String projectName = "AAA1";
	
		MedicationClient client = new MedicationClient();
		
		PharmacyInfo pharmacy1 = new PharmacyInfo("Pharmacy1", "001_1");
		PharmacyInfo pharmacy2 = new PharmacyInfo("Pharmacy2", "001_2");
		PharmacyInfo[] pharmacies = new PharmacyInfo[]{pharmacy1, pharmacy2};
		
		TreatmentInfo t1 = new TreatmentInfo("Control", "001");
		TreatmentInfo t2 =new TreatmentInfo("Pain", "002");
		
		TreatmentInfo[] treatments = new TreatmentInfo[] {t1, t2};
		
		String allocationNoticeSubject = "Medication Package allocation notice";
		String allocationNoticeBody = "A medication package has been allocated to the participant '%participantId%'.\n\n" +
				"The package id is '%packageId%', which has been allocated from the following pharmacy: %pharmacyId%.";
		EmailInfo allocationNotice = new EmailInfo(allocationNoticeSubject, allocationNoticeBody);
		Pair<String, EmailInfo>[] emailsArray = new Pair[1];
		emailsArray[0] = new Pair<String, EmailInfo>(EmailType.NOTIFY_MEDS_ALLOCATION.toString(), allocationNotice);
		
		ProjectInfo proj = new ProjectInfo(projectName, projectCode, pharmacies, treatments, emailsArray);
		
		String saml = "testme";
		
		try {
			client.saveMedicationProject(proj, saml);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void saveNewProject2() throws InvalidProjectException{
		// TODO Auto-generated method stub
		String projectCode = "PCD2";
		String projectName = "Project Name_2";
	
		MedicationClient client = new MedicationClient();
		
		PharmacyInfo pharmacy1 = new PharmacyInfo("Pharmacy1", "C1_1");
		PharmacyInfo pharmacy2 = new PharmacyInfo("Pharmacy1", "C2_1");
		PharmacyInfo[] pharmacyArray = new PharmacyInfo[] {pharmacy1, pharmacy2};
				
		TreatmentInfo t1 = new TreatmentInfo("Drugs", "001");
		TreatmentInfo t2 =new TreatmentInfo("Placebo", "002");
		
		TreatmentInfo[] treatments = new TreatmentInfo[] {t1, t2};
		
		String allocationNoticeSubject = "Medication Package allocation notice";
		String allocationNoticeBody = "A medication package has been allocated to the participant '%participantId%'.\n\n" +
				"The package id is '%packageId%', which has been allocated from the following pharmacy: %pharmacyId%.";
		EmailInfo allocationNotice = new EmailInfo(allocationNoticeSubject, allocationNoticeBody);
		Pair<String, EmailInfo>[] emailsArray = new Pair[1];
		emailsArray[0] = new Pair<String, EmailInfo>(EmailType.NOTIFY_MEDS_ALLOCATION.toString(), allocationNotice);
		
		ProjectInfo proj = new ProjectInfo(projectName, projectCode, pharmacyArray, treatments, emailsArray);
		
		String saml = "tesme";
		
		try {
			client.saveMedicationProject(proj, saml);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
