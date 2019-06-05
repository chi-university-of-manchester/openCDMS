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
package org.psygrid.collection.entry.chooser;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import org.psygrid.collection.entry.remote.RemoteManager;
import org.psygrid.collection.entry.remote.RemoteServiceFault;
import org.psygrid.collection.entry.security.EntrySAMLException;
import org.psygrid.common.identifier.InvalidIdentifierException;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.data.utils.security.NotAuthorisedFault;

/**
 * A Choosable to represent a Record that is being displayed in
 * a chooser for the purposes of selecting a Report to generate.
 * <p>
 * At the time of construction only the identifier of the Record
 * is known. Upon selection a summary of the Record is retrieved
 * from the central repository and the list of available Reports
 * generated - these are the Choosable's children.
 * 
 * @author Rob Harper
 *
 */
public class ChoosableReportRecord extends AbstractChoosableRecord<ChoosableReport> {

	/**
	 * The identifier of the Record
	 */
	private final String identifier;
	
	/**
	 * The openCDMS identifier of the Record. 'identifier' and 'sysIdentifier' will be identical for most studies, which 
	 * use the openCDMS identfiers to display in the system. But for studies that use their own identifiers in the system
	 * for display, they will differ.
	 */
	private final String sysIdentifier;

	private Record recordSummary;

	private boolean loaded = false;

	public ChoosableReportRecord(String identifier, String sysIdentifier, Choosable parent) {
		super(parent);
		this.identifier = identifier;
		this.sysIdentifier = sysIdentifier;
	}

	public List<ChoosableReport> getChildren() throws ChoosableException {
		if ( !loaded ) {
			try {
				children = loadChildren();
				loaded = true;
			} 
			catch (RemoteServiceFault e) {
				WrappedJOptionPane.showWrappedMessageDialog(null, "Unable to display reports for this record. Please save the record to the database first.", "Error", WrappedJOptionPane.ERROR_MESSAGE);				
			}
			catch (Exception e) {
				// In this case, it doesn't matter what we catch, we have to
				// rethrow as ChoosableException
				throw new ChoosableException(
						"Problem retrieving record's children", e);   
			}
		}
		return children;
	}

	private List<ChoosableReport> loadChildren() throws ConnectException, IOException, RemoteServiceFault, EntrySAMLException, NotAuthorisedFault, InvalidIdentifierException {

		recordSummary = RemoteManager.getInstance().getRecordSummary(sysIdentifier);


		//TODO we should be able to display a report for a local record, but we can't because the repository's generateReport method requires a saved record
		/*if (recordSummary == null) {
			/*
		 * Get locally held documents (both complete and incomplete)
		 */
		/*IRecord comrecord = null;
			try {
				comrecord = PersistenceManager.getInstance().loadRecord(identifier, true);
			}
			catch (FileNotFoundException e) {
				//Safe to ignore
			}
			catch (Exception e) {
				//TODO handle exception
				e.printStackTrace();
			}
			Record increcord = null;
			try {
				increcord = (Record)PersistenceManager.getInstance().loadRecord(identifier, false);
			}
			catch (FileNotFoundException e) {
				//Safe to ignore
			}
			catch (Exception e) {
				//handle exception
				e.printStackTrace();
			}

			if (comrecord != null) {
				if (increcord != null) {
					for (IDocumentInstance inst: increcord.getDocInstances()) {
						comrecord.addDocumentInstance(inst);
					}
				}
				recordSummary = comrecord;
			}
			else if (increcord != null) {
				recordSummary = increcord;
			}
		}    	*/

		List<IReport> reports = 
			RemoteManager.getInstance().getReports(recordSummary.getDataSet());
		List<ChoosableReport> choosableReports = 
			new ArrayList<ChoosableReport>(reports.size());

		for (IReport report : reports) {
			ChoosableReport cReport = new ChoosableReport(report, recordSummary, this);
			choosableReports.add(cReport);
		}
		return choosableReports;
	}

	public String getDisplayText() {
		return identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	@Override
	public Record getRecord() {
		return recordSummary;
	}

	@Override
	public String getSysIdentifier() {
		return sysIdentifier;
	}

}
