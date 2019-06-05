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
package org.psygrid.datasetdesigner.controllers;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.FilenameFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Singleton to control the creation, deletion and fetching of 
 * temp files.  Tmp files are used to indicate that a file has been
 * opened to other users trying to access the same file.  They
 * are stored in the same location as the original file and 
 * suffixed with the extension .dsdtmp
 * 
 * @author pwhelan
 *
 */
public class TempFileController {
	
	/**
	 * The string used as the extension for a dataset designer temporary file
	 */
	public final static String DSD_EXT = ".dsdtmp";
	
	/**
	 * The logger for this class
	 */
	private static final Log LOG = LogFactory.getLog(TempFileController.class);

	/**
	 * Instance of the temp file controller
	 */
	private static TempFileController tfController;
	
	/**
	 * private File tempFile
	 */
	private File tmpFile = null;
	
	/**
	 * Private Constructor; create the TempFileController
	 */
	private TempFileController() {
		//empty constructor
	}
	
	/**
	 * Return the singleton of this class
	 * @return the DatasetController
	 */
	public synchronized static TempFileController getInstance() {
		if (tfController == null) {
			tfController = new TempFileController();
		}
		return tfController;
	}
	
	/**
	 * Create a temp file based on the original file name
	 * Into the temp file, write the project code (to 
	 * ensure it can be uniquely tested by other DSDs)
	 * 
	 * @param origFile the original file
	 * @return true if creation was successful; false if not
	 */
	public boolean createTmpFile(File origFile, String projectCode) {
		try {
			//first delete the existing file if one exists
			//should only ever be one active temporary file (for one active study)
			if (tmpFile != null) {
				tmpFile.delete();
			}
			
			String fileName = origFile.getName();
			int indexOfDot = fileName.indexOf("."); 
			if ( indexOfDot != -1) {
				fileName = fileName.substring(0, indexOfDot);
			}
			
			//save a tmp file for multi-access; suffix null = .tmp
			File tmp = File.createTempFile(fileName, DSD_EXT, origFile.getParentFile());
			
			//Write to temp file
	        BufferedWriter out = new BufferedWriter(new FileWriter(tmp));
	        out.write(projectCode);
	        out.close();
			tmp.deleteOnExit();
			setTmpFile(tmp);
			return true;
		} catch (Exception ex) {
			LOG.error("Exception occurred creating temp file", ex);
			ex.printStackTrace();
			setTmpFile(null);
			return false;
		}
	}
	
	/**
	 * Delete the temporary file if it exists; needed when
	 * dataset is closed
	 * @return true if temp file existed and was deleted; false if not
	 */
	public boolean deleteTempFile() {
		if (tmpFile != null) {
			tmpFile.delete();
			return true;
		}
		return false;
	}
	
	/**
	 * Checks for the existence of a temp file (with .dsdtmp extension and project code)
	 * @param origFile the original file that wants to be loaded into the DSD
	 * @param projectCode the code of the file to load
	 * @return true if a file with dsdtmp extension exists that contains the project code passed;
	 * 		   false if not
	 */
	public boolean tempFileExists(File origFile, String projectCode) {
		
		String fileName = origFile.getName();
		int indexOfDot = fileName.indexOf("."); 
		if ( indexOfDot != -1) {
			fileName = fileName.substring(0, indexOfDot);
		}
		
		if (origFile.getParentFile().isDirectory()) {
			String listFiles[] = origFile.getParentFile().list(new DSDTempFileFilter());
			
			for (int i=0; i<listFiles.length; i++) {
				
				
				String iterFileName = listFiles[i];

				if (iterFileName.contains(fileName)) {
					
					try {
						//must add the parent file name to this
						String fullName = origFile.getParentFile() + File.separator+ iterFileName;
						
						BufferedReader in = new BufferedReader(new FileReader(fullName));
					    String str;
					    while ((str = in.readLine()) != null) {
					    	if (str.indexOf(projectCode) != -1 ) {
					    		return true;
					       	}
					    }
					    in.close();
					  //if exception occurs, assume the worst case (i.e. file exists)
					  } catch (IOException e) {
						  LOG.error("Exception occurred checking existence of temp file", e);
						  return true;
					  }
				}
			}
		}
		
		//if we get to here, no file was found that matched the criteria and contained
		//the project code
		return false;
	}
	
	/**
	 * Get the current temp file
	 * @return the currently active tmp file
	 */
	public File getTmpFile() {
		return tmpFile;
	}

	/**
	 * Set the current tmp file
	 * @param tmpFile the tmp file to set
	 */
	public void setTmpFile(File tmpFile) {
		this.tmpFile = tmpFile;
	}
	
	/**
	 * Filter files according to the .dsdtmp extension
	 * @author pwhelan
	 */
	private class DSDTempFileFilter implements FilenameFilter {
	    public boolean accept(File dir, String name) {
	        return (name.endsWith(DSD_EXT));
	    }
	}
	
}
