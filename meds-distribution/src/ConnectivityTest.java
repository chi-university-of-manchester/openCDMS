/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/


import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.Options;
import org.opensaml.SAMLAssertion;
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
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.model.IProject;
import org.psygrid.security.authentication.client.LoginClient;
import org.psygrid.security.authentication.service.LoginServicePortType;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.utils.PropertyUtilities;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * Class for testing access to the DataElementClient with security
 * enabled.
 * 
 * @author Lucy Bridges
 *
 */
public class ConnectivityTest {
	
	private static MedicationClient client = new MedicationClient();
	private static LoginServicePortType aa1 = null;
	
	
	public static void main(String[] args){
		
		SAMLAssertion sa = null;
		IProject project = null;
        try{
        	sa = login(args);
        	String saml = sa.toString();
        	
        	System.out.println("Do stuff for: "+client.getUrl());
        	
        	//Should work always
        	System.out.println("Getting element types");
        	
        	ConnectivityTest test = new ConnectivityTest();
    		test.saveNewProject(saml);
    		/*
    		for(int i = 1000; i < 1005; i++){
    			String pkg = "A99_" + Integer.valueOf(i).toString();
    			test.saveMedicationPackage(pkg);
    			String pkg2 = "B99_" + Integer.valueOf(i).toString();
    			test.saveMedicationPackage2(pkg2);
    		}
    		*/

        	
        	        	
        	
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        
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

	protected void saveNewProject(String saml) throws InvalidProjectException{
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
		
		//String saml = "testme";
		
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
	
	private static SAMLAssertion login(String[] args) throws Exception {
		
		System.setProperty("axis.socketSecureFactory",
		"org.psygrid.security.components.net.PsyGridClientSocketFactory");
		Options opts = new Options(args);
		Properties properties = PropertyUtilities.getProperties("test.properties");
		System.out.println(properties.getProperty("org.psygrid.security.authentication.client.trustStoreLocation"));
		LoginClient tc = null;

		try {
			tc = new LoginClient("test.properties");
			aa1 = tc.getPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		char[] password = opts.getPassword().toCharArray();
		short[] pwd = new short[password.length];
		for (int i = 0; i < pwd.length; i++) {
			pwd[i] = (short) password[i];
		}System.out.println("about to call login via my proxy");
		String credential = tc.getPort().login(opts.getUser(), pwd);
		if (credential != null) {
			byte[] ks = Base64.decode(credential);
			FileOutputStream fos = new FileOutputStream(properties
					.getProperty("org.psygrid.security.authentication.client.keyStoreLocation"));
			fos.write(ks);
			fos.flush(); 
			fos.close();
		}
		System.out.println("loggedin");
		System.setProperty("javax.net.ssl.keyStorePassword", new String(password));
		PsyGridClientSocketFactory.reinit();
		AAQueryClient qc = new AAQueryClient("test.properties");
		System.out.println("getAssertion");
		SAMLAssertion sa = qc.getSAMLAssertion();

		return sa;
	}
}
