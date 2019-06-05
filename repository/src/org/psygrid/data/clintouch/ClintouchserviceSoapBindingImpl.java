package org.psygrid.data.clintouch;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.psygrid.services.SecureSoapBindingImpl;
import org.springframework.context.ApplicationContext;

public class ClintouchserviceSoapBindingImpl extends SecureSoapBindingImpl implements ClinTouchService {
	
	private ClinTouchService service;
	
	@Override
	protected void onInit() throws ServiceException {
		super.onInit();
		ApplicationContext context = getWebApplicationContext();
		service = (ClinTouchService)context.getBean("clinTouchService");
	}

	public void messageReceived(String mobileNumber, String message)
			throws RemoteException {
		service.messageReceived(mobileNumber, message);
	}

	public void run() throws RemoteException {
		service.run();
	}
}
