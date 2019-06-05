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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvWriter;

/**
 * Metadata about a composite entry in an export.
 * 
 * @author Rob Harper
 *
 */
public class CompositeEntry extends Entry {

	/**
	 * The list of basic entries (columns) in the composite (table).
	 */
	private List<BasicEntry> entries = new ArrayList<BasicEntry>();

	/**
	 * Map containing the maximum number of rows in the table for each
	 * section instance, across all the records in the export.
	 */
	private Map<Integer, Integer> rows = new HashMap<Integer, Integer>();

	public CompositeEntry(Long id) {
		super(id);
	}

	public List<BasicEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<BasicEntry> entries) {
		this.entries = entries;
	}

	public Map<Integer, Integer> getRows() {
		return rows;
	}

	public void setRows(Map<Integer, Integer> rows) {
		this.rows = rows;
	}

	/**
	 * Add details of the row count for the specified section
	 * instance index.
	 * <p>
	 * If a row count already exists for the given section instance
	 * index then the value is only updated if the new row count
	 * is greater than the existing one.
	 * 
	 * @param index Section instance index.
	 * @param count Number of rows.
	 */
	public void addRow(Integer index, Integer count){
		if ( this.rows.containsKey(index) ){
			Integer currentCount = this.rows.get(index);
			if ( null == currentCount || count.intValue() > currentCount.intValue() ){
				this.rows.put(index, count);
			}
		}
		else{
			this.rows.put(index, count);
		}
	}
	
	@Override
	public int columnCount(boolean showCode, boolean showValue) {
		return columnCount(0, showCode, showValue);
	}

	@Override
	public int columnCount(int instanceIndex, boolean showCode, boolean showValue) {
		return ( getRowCount(instanceIndex) * columnsPerRow(showCode, showValue) );
	}

	/**
	 * Calculate the number of columns required in a tabular representation
	 * of the exported data for one row in the composite.
	 * 
	 * @return
	 */
	public int columnsPerRow(boolean showCode, boolean showValue){
		int count = 0;
		for ( BasicEntry b: entries ){
			count += b.columnCount(showCode, showValue);
		}
		return count;
	}

	@Override
	public void writeHeader(int instanceIndex, CsvWriter output, boolean showCode, boolean showValue)
	throws IOException {
		int rowCount = getRowCount(instanceIndex);
		for ( int i=0; i<rowCount; i++ ){
			String rowSuffix = Integer.toString(i+1);
			for ( BasicEntry e: entries ){
				if (showValue) {
					output.write(getName() + SEPARATOR + e.getName() + VALUE_COLUMN + SEPARATOR + rowSuffix);
				}
				if (showCode) {
					output.write(getName() + SEPARATOR + e.getName()+ CODE_COLUMN + SEPARATOR + rowSuffix);
				}
				if ( e.isUnitPresent() ){
					output.write(getName() + SEPARATOR + e.getName() + UNIT_COLUMN + SEPARATOR + rowSuffix);
				}
				if ( e.isExtraValuePresent() ){
					output.write(getName() + SEPARATOR + e.getName() + EXTRA_COLUMN + SEPARATOR + rowSuffix);
				}
			}
		}

	}

	/**
	 * Locate a basic entry in the list of entries by its database id.
	 * 
	 * @param id The database id.
	 * @return The entry or null if no entry is found with the 
	 * given id
	 */
	public BasicEntry findEntryById(Long id){
		for ( BasicEntry be: entries ){
			if ( be.getId().equals(id) ){
				return be;
			}
		}
		return null;
	}

	/**
	 * Get the maximum number of rows in the composite for the given
	 * section instance
	 * 
	 * @param instanceIndex The index of the section instance.
	 * @return The maximum number of rows.
	 */
	public int getRowCount(int instanceIndex){
		Integer count = rows.get(Integer.valueOf(instanceIndex));
		if ( null == count ){
			return 0;
		}
		else{
			return count.intValue();
		}
	}
}
