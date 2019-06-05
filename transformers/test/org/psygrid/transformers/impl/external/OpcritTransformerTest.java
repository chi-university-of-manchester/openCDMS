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

package org.psygrid.transformers.impl.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.psygrid.transformers.TransformerException;

public class OpcritTransformerTest extends TestCase {

	/**
	 * Test a HTTP connection to the Opcrit web service
	 *
	 */
	public void testOpcritConnection() {
		
		try {
			URL url = null;
			try {
				//url = new URL("http://sgdp.iop.kcl.ac.uk/opcritonline/rateopcrit.py");
				url = new URL("http://atisha.smb.man.ac.uk/cgi-bin/opcrit/rateopcrit.py");
				
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
				fail("Problem when creating Opcrit URL: "+e.toString());
			}
			
			URLConnection connection = url.openConnection();
			assertNotNull("Opcrit connection is null", connection);
			
			((HttpURLConnection)connection).setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true); 
			connection.setUseCaches(false); 
			connection.setRequestProperty("Content-Type", "text/plain");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			assertNotNull("Connection returned no data", reader);
			
			String line = reader.readLine();
			assertNotNull("Reader returned no data", line);
			
			reader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			fail("Exception was: "+e.toString());
		}
	}
	
	/**
	 * Test that the Opcrit transformer can successfully
	 * transform input using the Opcrit web service.
	 */
	public void testOpcritTransformer() {	
		try {
			ExternalServiceTransformer transformer   = new OpcritTransformerImpl();
			//Map<String,String> data = createOpcritData();
			String data = createOpcritData2();
			System.out.println("Original Data: "+data);
			System.out.println("");
			String results = transformer.transform(data);
			//STATUS,ID,Q1,.....Q90,dsm3,dsm3r,....tsuang,notes.
			System.out.println("Results: "+results);
			
		}
		catch (TransformerException e) {
			e.printStackTrace();
			fail("Transformer Exception: "+e.toString());
		}
	}
	
	private Map<String,String> createOpcritData() {
		LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
		//ID,Q1,.....Q90
		
		for (int i = 0; i < 90; i++) {
			data.put("Q"+Integer.toString(i), "0");
		}
		return data;
	}
	
	private String createOpcritData2() {
		String[] data = new String[90];
		//ID,Q1,.....Q90
		
		for (int i = 0; i < 90; i++) {
			data[i] = Integer.toString(i);
		}
		return exportData(data);
	}
	
	/**
	 * Convert to CSV format
	 * 
	 * @param entries
	 * @return
	 */
	private String exportData(String[] entries) {
		//TODO what's the best input format to provide?
		String csvInput = "";
		boolean first = true;
		for (String name: entries) {	
			if (first) {
				csvInput = name;
				first = false;
			}
			else {
				//should be integer values only so no special formating required
				csvInput += ","+name;
			}
		}

		return csvInput;
	}
}
