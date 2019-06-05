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

package org.opencdms.web.modules.query.models;

import java.io.Serializable;

import org.psygrid.data.query.IQuery;

/**
 * @author Rob Harper
 *
 */
public class SelectQueryModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private IQuery query;
	private String operation;
	
	public IQuery getQuery() {
		return query;
	}
	public void setQuery(IQuery query) {
		this.query = query;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
}
