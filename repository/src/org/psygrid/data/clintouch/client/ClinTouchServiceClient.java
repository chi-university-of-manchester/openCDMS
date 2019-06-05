package org.psygrid.data.clintouch.client;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.common.AbstractClient;
import org.psygrid.data.clintouch.ClinTouchService;
import org.psygrid.data.clintouch.ClinTouchServiceServiceLocator;

public class ClinTouchServiceClient extends AbstractClient {
	private static final Log LOG = LogFactory.getLog(ClinTouchServiceClient.class);
	
	private final ClinTouchService service;
	
	public ClinTouchServiceClient() {
		ClinTouchServiceServiceLocator serviceLocator = new ClinTouchServiceServiceLocator();
		try {
			if ( url == null ){
				service = serviceLocator.getclintouchservice();
			}
			else{
				service = serviceLocator.getclintouchservice(url);
			}
		} catch(ServiceException ex) {
			throw new RuntimeException("clintouchservice URL is invalid!", ex);
		}
	}
	
	public void messageReceived(String mobileNumber, String message) {
		try {
			service.messageReceived(mobileNumber, message);
		} catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}  
	}
	
	public void run() {
		try {
			service.run();
		} catch(RemoteException ex){
			LOG.fatal(ex.getMessage(), ex);
			throw new RuntimeException(ex.getCause());
		}  
	}
}
