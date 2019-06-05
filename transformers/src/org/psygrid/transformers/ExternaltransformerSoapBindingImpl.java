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

package org.psygrid.transformers;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.transformers.impl.external.ExternalServiceTransformer;
import org.psygrid.transformers.impl.external.TyrerCuzickTransformer;
import org.springframework.context.ApplicationContext;
import org.springframework.remoting.jaxrpc.ServletEndpointSupport;

public class ExternaltransformerSoapBindingImpl extends ServletEndpointSupport implements ExternalTransformer {

	private static Log sLog = LogFactory.getLog(ExternaltransformerSoapBindingImpl.class);
	private ApplicationContext ctx = getWebApplicationContext();

	@Override
	protected void onInit() throws ServiceException {
		ctx = getWebApplicationContext();
	}

	public String opcrit(String input) throws RemoteException {
		final String METHOD_NAME = "opcrit";
		System.err.println("Opcrit Transformer");
		ExternalServiceTransformer opcrit = (ExternalServiceTransformer) ctx.getBean("opcritTransformer");
		System.err.println("Opcrit is: "+opcrit);

		String result = null;
		try{
			//ExternalServiceTransformer opcrit = new OpcritTransformerImpl();
			result = opcrit.transform(input);    
		}
		catch(TransformerException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw AxisFault.makeFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
		return result;
	}

	public String drnNephropathy(String values) throws RemoteException {
		final String METHOD_NAME = "opcrit";
		
		ExternalServiceTransformer nephropathy = (ExternalServiceTransformer) ctx.getBean("drnNephropathyTransformer");

		String result = null;
		try{
			//ExternalServiceTransformer opcrit = new OpcritTransformerImpl();
			result = nephropathy.transform(values);    
		}
		catch(TransformerException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw AxisFault.makeFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
		return result;
	}

	public String tyrercuzick(String values) throws RemoteException {

		final String METHOD_NAME = "tyrercuzick";
		
		String result = null;
		try{
			result = TyrerCuzickTransformer.transform(values);    
		}
		catch(TransformerException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw AxisFault.makeFault(ex);
		}
		catch(RuntimeException ex){
			sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
			throw ex;
		}
		return result;
	}
	
}