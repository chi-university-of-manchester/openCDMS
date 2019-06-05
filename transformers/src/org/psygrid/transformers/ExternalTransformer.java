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

/**
 * Interface specifying transformers utilising remote web services.
 * 
 * @author Lucy Bridges
 *
 */
public interface ExternalTransformer  extends java.rmi.Remote {

	/**
	 * Return the result given by the external Opcrit service to the
	 * results of the Opcrit questionnaire. The input should be 
	 * formatted as a comma separated string. 
	 * 
	 * The method parameters needs to be a single String, otherwise 
	 * a Soap error occurs when CoCoA tries to call this method. 
	 *  
	 * @param input
	 * @return result
	 * @throws RemoteException
	 */
	public String opcrit(String values) throws RemoteException;
	
	/**
	 * Return the nephropathy result for the DRN Address project.
	 * The input should be formatted as a comma separated string.
	 * 
	 * @param values
	 * @return
	 * @throws RemoteException
	 */
	public String drnNephropathy(String values) throws RemoteException;

	/**
	 * Return the TyrerCuzick risk calculation for the PROCAS study.
	 * The input should be formatted as a comma separated string.
	 * 
	 * @param values - CSV 
	 * @return the calculation result as a string
	 * @throws RemoteException
	 */
	public String tyrercuzick(String values) throws RemoteException;

	
	
}
