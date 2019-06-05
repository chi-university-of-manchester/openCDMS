import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.psygrid.meds.rmi.MedicationClient;


public class AllocatePackageTest {

	/**
	 * @param args
	 * @throws ServiceException 
	 * @throws RemoteException 
	 */
	public static void main(String[] args) throws RemoteException, ServiceException {
		// TODO Auto-generated method stub

		String projectCode = args[0];
		String pharmacyCode = args[1];
		String treatmentCode = args[2];
		String participantId = args[3];
		
		String saml = "testme";
		
		MedicationClient client = new MedicationClient();
		
		String allocatedPackageId = client.allocateInitialMedicationPackage(projectCode, pharmacyCode, treatmentCode, participantId, saml);
		
		System.out.println("The allocated package id is: " + allocatedPackageId);
		
		
	}

}
