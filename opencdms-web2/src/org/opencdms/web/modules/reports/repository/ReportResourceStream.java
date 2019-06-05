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

package org.opencdms.web.modules.reports.repository;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;

/**
 * @author Rob Harper
 *
 */
public class ReportResourceStream extends AbstractResourceStreamWriter {

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(ReportResourceStream.class);
	
	private final byte[] data;
	private final String format;
	
	public ReportResourceStream(byte[] data, String format){
		this.data = data;
		this.format = format;
	}
	
	public void write(OutputStream output) {
		try {
			output.write(data);
		} catch (IOException e) {
			LOG.error(e);
		}
	}

	public String getContentType() {
		if ( format.equals("pdf") ){
			return "application/pdf";
		}
		if ( format.equals("xls") ){
			return "application/vnd.ms-excel";
		}
		if ( format.equals("csv") ){
			return "text/csv";
		}
		return null;
	}

	@Override
	public long length() {
		return data.length;
	}

}
