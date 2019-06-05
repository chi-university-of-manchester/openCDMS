package org.psygrid.data.clintouch;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClinTouchService extends Remote {
	public void messageReceived(String mobileNumber, String message) throws RemoteException;
	
	public void run() throws RemoteException;
}
