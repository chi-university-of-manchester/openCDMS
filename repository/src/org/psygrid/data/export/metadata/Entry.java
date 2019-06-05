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

package org.psygrid.data.export.metadata;

import java.io.IOException;

import com.csvreader.CsvWriter;

/**
 * Metadata about an entry in a data export
 * 
 * @author Rob Harper
 *
 */
public abstract class Entry {

	public static final String VALUE_COLUMN = "- Value";
	public static final String CODE_COLUMN = "- Code";
	public static final String UNIT_COLUMN = "- Unit";
	public static final String EXTRA_COLUMN = "- Extra";
	public static final String SEPARATOR = " - ";
	
	/**
	 * The database id of the entry
	 */
	private final Long id;
	
	/**
	 * The name of the entry
	 */
	private String name;

	public Entry(Long id){
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Return the number of columns that will be used
	 * when converting the exported data for this entry
	 * to a tabular format, assuming that the number of columns
	 * is independent of the section instance (true for basic 
	 * entries)
	 * 
	 * @return The number of columns.
	 */
	public abstract int columnCount(boolean showCode, boolean showValue);
	
	/**
	 * Return the number of columns that will be used
	 * when converting the exported data for this entry
	 * to a tabular format, for the specified section instance
	 * index.
	 * 
	 * @param instanceIndex The section instance index.
	 * @return The number of columns.
	 */
	public abstract int columnCount(int instanceIndex, boolean showCode, boolean showValue);
	
	/**
	 * Write the header for this entry in CSV format.
	 * 
	 * @param instanceIndex The section instance index.
	 * @param output CSV output stream.
	 * @throws IOException
	 */
	public abstract void writeHeader(int instanceIndex, CsvWriter output, boolean showCode, boolean showValue) throws IOException;

	@Override
	public boolean equals(Object obj) {
		if ( null != obj && obj instanceof Entry ){
			return ((Entry)obj).getId().equals(this.id);
		}
		return false;
	}
	
}
