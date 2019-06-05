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
import java.util.LinkedHashMap;
import java.util.Map;

import com.csvreader.CsvWriter;

/**
 * @author Rob Harper
 *
 */
public class BasicEntry extends Entry {

	public static enum TYPE {Date, Option, Numeric, Integer, Boolean, Text, LongText, Narrative};
	
	private boolean unitPresent;

	private boolean extraValuePresent;
	
	private TYPE entryType;

	private Map<String,String> valueLabels = new LinkedHashMap<String,String>();
	
	private boolean usesMissingCodes;
	
	public BasicEntry(Long id) {
		super(id);
	}

	public boolean isUnitPresent() {
		return unitPresent;
	}

	public void setUnitPresent(boolean unitPresent) {
		this.unitPresent = unitPresent;
	}

	public boolean isExtraValuePresent() {
		return extraValuePresent;
	}

	public void setExtraValuePresent(boolean extraValuePresent) {
		this.extraValuePresent = extraValuePresent;
	}

	@Override
	public int columnCount(boolean showCode, boolean showValue) {
		int count = 0;
		if (showCode) {
			count++;
		}
		if (showValue) {
			count++;
		}
		if ( unitPresent ){
			count++;
		}
		if ( extraValuePresent ){
			count++;
		}
		return count;
	}

	@Override
	public int columnCount(int instanceIndex, boolean showCode, boolean showValue) {
		return columnCount(showCode, showValue);
	}

	public TYPE getEntryType() {
		return entryType;
	}

	public void setEntryType(TYPE entryType) {
		this.entryType = entryType;
	}

	public Map<String, String> getValueLabels() {
		return valueLabels;
	}

	public void setValueLabels(Map<String, String> valueLabels) {
		this.valueLabels = valueLabels;
	}

	public void addValueLabel(String code, String label) {
		this.valueLabels.put(code, label);
	}
	
	public boolean isUsesMissingCodes() {
		return usesMissingCodes;
	}

	public void setUsesMissingCodes(boolean usesMissingCodes) {
		this.usesMissingCodes = usesMissingCodes;
	}

	@Override
	public void writeHeader(int instanceIndex, CsvWriter output, boolean showCode, boolean showValue) throws IOException {
		if (showValue) {
			output.write(getName() + VALUE_COLUMN);
		}
		if (showCode) {
			output.write(getName() + CODE_COLUMN);
		}
		if ( unitPresent ){
			output.write(getName() + UNIT_COLUMN);
		}
		if ( extraValuePresent ){
			output.write(getName() + EXTRA_COLUMN);
		}
	}

}
