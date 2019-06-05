
package org.psygrid.data.importing.plugins;

import java.io.PrintStream;

import org.psygrid.data.utils.wrappers.AAQCWrapper;

/**
 * Interface to dataset-specific import implementations.
 * 
 * To write import code for a specific study implement this interface
 * and add the implementation to the application context xml.
 *
 */
public interface ImportPlugin {

	/*
	 * Return a list of strings indicating the types of import data that this plugin can handle.
	 * The values in the list do not necessarily have to match one-to-one with document occurrences
	 * in the target dataset.
	 */
	public String[] getImportTypes();
	
	/**
	 * Import a single data file.
	 * 
	 * @param projectCode the project the data is for - may be useful if we have multi-project import plugins
	 * @param importType a string from the list returned by getImportTypes()
	 * @param filePath the path to the uploaded file to be imported
	 * @param user the user requesting the import
	 * @param aaqc the AAQCWrapper useful for grabbing saml
	 * @param log anything written to this stream will be emailed to the user who requested the import
	 * @throws Exception this is bad I know - a plugin should not throw checked exceptions
	 */
	public void run(String projectCode,String importType,String filePath,String user,AAQCWrapper aaqc, PrintStream log) throws Exception;

}
