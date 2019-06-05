import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.psygrid.meds.events.InvalidEventException;
import org.psygrid.meds.medications.MedicationPackageNotFoundException;
import org.psygrid.meds.medications.PackageInfo;
import org.psygrid.meds.medications.PackageInfoView;
import org.psygrid.meds.project.InvalidProjectException;
import org.psygrid.meds.rmi.MedicationClient;


public class ViewPackgeTest {

	/**
	 * @param args
	 * @throws ServiceException 
	 * @throws RemoteException 
	 * @throws InvalidEventException 
	 * @throws InvalidProjectException 
	 * @throws MedicationPackageNotFoundException 
	 */
	public static void main(String[] args) throws RemoteException, ServiceException, InvalidProjectException, InvalidEventException, MedicationPackageNotFoundException {
		// TODO Auto-generated method stub
	
		MedicationClient client = new MedicationClient();
		
		String saml = "testme";
		
		PackageInfoView info = client.viewMedicationPackage(args[0], args[1], saml);

		int debug = 2;
		
	}

}
