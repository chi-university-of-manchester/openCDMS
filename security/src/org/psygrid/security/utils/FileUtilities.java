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


//Created on Nov 1, 2005 by John Ainsworth



package org.psygrid.security.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jda
 *
 */
public class FileUtilities {
	
	/** logger */

	private static Log sLog = LogFactory.getLog(FileUtilities.class);

	public FileUtilities(){};
	
	/**
	 * @param filename
	 * @return
	 */	
	public static String readFileAsString(String filename){	
		
		sLog.debug("filename: "+filename);
		
		String contents = new String("");
		char[] buffoon = new char[2048];
		
		File f = new File(filename);	
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));
			int rc;
			do {
				rc = br.read(buffoon);			
				if(rc>0){			
					contents += new String(buffoon, 0, rc);
				}
			} while (rc!=-1);
			br.close();	
		} catch (FileNotFoundException fnf){
			sLog.error(filename+" does not exist");
			contents = null;
		} catch (IOException io){
			sLog.error("could not read file "+filename);
			contents = null;
		} 
		return contents;			
	}
}
