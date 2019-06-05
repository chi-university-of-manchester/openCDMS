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

package org.psygrid.data.model.dto.extra;

import java.io.Serializable;

/**
 * Result from a call to RecordDAO#searchRecordChangeHistory.
 * 
 * @author Rob Harper
 *
 */
public class SearchRecordChangeHistoryResult implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The total number of results returned by the search.
	 * <p>
	 * This may be greater than the number of results contained in the
	 * results list, which is restricted to a maximum of 30 items.
	 */
	private int totalCount;
	
	/**
	 * The index in the overall list of results of the first item
	 * in the results list.
	 */
	private int firstResult;
	
	/**
	 * The index in the overall list of results of the last item
	 * in the results list.
	 */
	private int lastResult;
	
	/**
	 * The maximum number of results that can be contained in the result.
	 * <p>
	 * Communicated in this way so callers don't have to make assumptions
	 * about about how many results will be returned, so this can just be
	 * changed on the server if necessary without any mods to client code.
	 */
	private int maxResultCount;
	
	/**
	 * The list of record change history item results.
	 */
	private RecordChangeHistoryResult[] results;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public int getLastResult() {
		return lastResult;
	}

	public void setLastResult(int lastResult) {
		this.lastResult = lastResult;
	}

	public int getMaxResultCount() {
		return maxResultCount;
	}

	public void setMaxResultCount(int maxResultCount) {
		this.maxResultCount = maxResultCount;
	}

	public RecordChangeHistoryResult[] getResults() {
		return results;
	}

	public void setResults(RecordChangeHistoryResult[] results) {
		this.results = results;
	}
	
}
