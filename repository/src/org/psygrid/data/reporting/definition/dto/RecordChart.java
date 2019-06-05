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

package org.psygrid.data.reporting.definition.dto;

import java.util.List;
import java.util.Map;

import org.psygrid.data.model.dto.PersistentDTO;

public class RecordChart extends SimpleChart {

	private SimpleChartItem[] items = new SimpleChartItem[0];

	private SimpleChartRow[] rows = new SimpleChartRow[0];

	public SimpleChartItem[] getItems() {
		return items;
	}

	public void setItems(SimpleChartItem[] items) {
		this.items = items;
	}

	public SimpleChartRow[] getRows() {
		return rows;
	}

	public void setRows(SimpleChartRow[] rows) {
		this.rows = rows;
	}

	public int numRows() {
		return this.rows.length;
	}
	
	@Override
	public org.psygrid.data.reporting.definition.hibernate.RecordChart toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		//check for an already existing instance of a hibernate object for this 
		//simple chart in the map of references
		org.psygrid.data.reporting.definition.hibernate.RecordChart hSC = null;
		if ( hRefs.containsKey(this)){
			hSC = (org.psygrid.data.reporting.definition.hibernate.RecordChart)hRefs.get(this);
		}
		else{
			//an instance of the simple chart has not already
			//been created, so create it, and add it to the
			//map of references
			hSC = new org.psygrid.data.reporting.definition.hibernate.RecordChart();
			hRefs.put(this, hSC);
			toHibernate(hSC, hRefs);
		}

		return hSC;
	}

	public void toHibernate(org.psygrid.data.reporting.definition.hibernate.RecordChart hC, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
		super.toHibernate(hC, hRefs);

		//going to assume that if the items have been set then the rows haven't been used
		//and will transfer all items across to a single row
		if (items.length > 0) {
			SimpleChartRow[] newrows = new SimpleChartRow[1];
			SimpleChartRow rowitems  = new SimpleChartRow();
			rowitems.setSeries(this.items);
			newrows[0] = rowitems;
			setRows(newrows);
		}

		List<org.psygrid.data.reporting.definition.hibernate.SimpleChartRow> hRows = hC.getRows();
		for ( SimpleChartRow r: rows ){
			if ( r != null ){
				hRows.add(r.toHibernate(hRefs));
			}
		}
		hC.setRows(hRows);
	}

}
