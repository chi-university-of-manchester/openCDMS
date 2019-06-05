package org.psygrid.data.export.plugins;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.psygrid.data.export.hibernate.ExportRequest;
import org.psygrid.data.model.hibernate.Record;

/**
 * Interface to context specific export results.
 * 
 * To write export code for a specific context implement this interface
 * and add the implementation to the application context xml.
 * 
 * @author "Terry Child"
 */
public interface ExportPlugin {

	/**
	 * Returns true if the plugin is applicable in the given context
	 */
	public boolean isApplicable(ExportRequest request);
	
	/**
	 * Returns column names for results.
	 */
	public String[] getColumnNames();

	/**
	 * Returns export results for a record as a set of name-value pairs.
	 * @param record
	 */
	public Properties getResults(Record record ,ExportRequest request);

}
