/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.psygrid.data.utils;

import java.io.FileOutputStream;

import org.psygrid.data.reporting.client.ReportsClient;

/**
 * @author Rob Harper
 *
 */
public class GenerateMgmtReport {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			String projectCode = args[0];
			long id = Long.parseLong(args[1]);
		
			ReportsClient client = new ReportsClient();
			
			byte[] report = client.generateMgmtReportById(projectCode, id, null);
			
			FileOutputStream fos = new FileOutputStream(args[2]);
			fos.write(report);
			fos.close();
			
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	

}
