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
package org.psygrid.data.export;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * The format types that can be specified for the data Export.
 * 
 * @author Lucy Bridges
 *
 */
public enum ExportFormat {
	
	/**
	 * Export all data into a single CSV document.
	 */
	SINGLE_CSV,
	
	/**
	 * Export data into one CSV document per document occurrence.
	 */
	MULTIPLE_CSV,
	
	/**
	 * Export data into an Excel document, with one worksheet
	 * per document occurrence.
	 * 
	 */
	EXCEL,
	
	/**
	 * Export data into XML format.
	 */
	XML,
	
	/**
	 * Export data into a format suitable for importing into
	 * STATA. NB. This is not the native STATA file format.
	 */
	STATA,
	
	/**
	 * Export data into a format suitable for importing into
	 * SPSS.
	 */
	SPSS,
	
	/**
	 * Export data into a format suitable for importing into
	 * SAS.
	 */
	SAS;

	@Override
	public String toString() {
		switch(this){
		case SINGLE_CSV:
			return "Single CSV File";
		case MULTIPLE_CSV:
			return "Multiple CSV Files";
		case EXCEL:
			return "Excel";
		case XML:
			return "XML";
		case STATA:
			return "STATA Import";
		case SPSS:
			return "SPSS Import";
		case SAS:
			return "SAS Import";
		default:
			return null;
		}
	}
	
	public String toStringForRequest(){
		return super.toString();
	}
	
	public static List<ExportFormat> getFormatList(){
		return new ArrayList<ExportFormat>(EnumSet.allOf(ExportFormat.class));
	}
	
}
