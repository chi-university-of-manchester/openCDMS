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

package org.psygrid.collection.entry.persistence;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.ExceptionsHelper;
import org.psygrid.collection.entry.util.ResetWaitRunnable;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.Record;

/**
 * @author Rob Harper
 *
 */
public abstract class AbstractSaveRecordWorker extends SwingWorker<String, Object> {

	private static final Log LOG = LogFactory.getLog(AbstractSaveRecordWorker.class);
	
	protected final Application application;
	protected final Record record;
	protected final DocumentInstance docInst;
	
	public AbstractSaveRecordWorker(Application application, Record record, DocumentInstance docInst) {
		this.application = application;
		this.record = record;
		this.docInst = docInst;
	}

	protected void success() {
		// Empty default implementation
	}

	protected void failure() {
		// Empty default implementation
	}

	protected void failure(ExecutionException ee) {
		new ResetWaitRunnable(application).run();
		if (ee.getCause() instanceof IOException) {
			if (LOG.isErrorEnabled()) {
				LOG.error(ee.getCause().getMessage(), ee.getCause());
			}
			String title = Messages.getString("AbstractSaveRecordWorker.errorSavingRecordTitle");
			String message = Messages.getString("AbstractSaveRecordWorker.errorSavingRecordMessage");
			JOptionPane.showMessageDialog(application, message, title, JOptionPane.ERROR_MESSAGE);
		}
		else {
			ExceptionsHelper.handleFatalException(ee.getCause());
		}
		failure();
	}

	@Override
	protected void done() {
		try {
			String message = get();
			if ( null != message ){
				String title = Messages.getString("AbstractSaveRecordWorker.warningTitle");
				JOptionPane.showMessageDialog(application, message, title, JOptionPane.ERROR_MESSAGE);
			}
			new ResetWaitRunnable(application).run();
			success();
		} catch (InterruptedException e) {
			new ResetWaitRunnable(application).run();
			ExceptionsHelper.handleInterruptedException(e);
		} catch (ExecutionException e) {
			failure(e);
		}
	}

}
