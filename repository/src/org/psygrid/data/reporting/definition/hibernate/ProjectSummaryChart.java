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

package org.psygrid.data.reporting.definition.hibernate;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.GenericState;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.reporting.Chart;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.reporting.ChartRow;
import org.psygrid.data.reporting.ChartSeries;
import org.psygrid.data.reporting.definition.IProjectSummaryChart;

/**
 * 
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_prj_summ_charts"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class ProjectSummaryChart extends ManagementChart implements IProjectSummaryChart{

	/**
	 * Show only the total number of people in project.
	 */
	private boolean showTotal = false;

	public ProjectSummaryChart() {
		super();
	}

	public ProjectSummaryChart(String type, String title) {
		super(type, title);
	}

	/**
	 * Show only the total number of people in project.
	 * 	 
	 * @return showTotal
	 * 
	 * @hibernate.property column="c_show_total"
	 */
	public boolean isShowTotal() {
		return showTotal;
	}

	public void setShowTotal(boolean showTotal) {
		this.showTotal = showTotal;
	}

	protected void setAllowedStates() {
		allowedStates.add(GenericState.ACTIVE.toString());
		allowedStates.add(GenericState.COMPLETED.toString());
		allowedStates.add(GenericState.REFERRED.toString());
		allowedStates.add(GenericState.LEFT.toString());
	}
	
	@Override
	public Chart generateChart(Session session) {

		//get counts for statuses of records
		List results = session.createQuery("select r.status.longName, count(r) from Record r "+
				"where r.dataSet.id=? "+
				"and r.deleted=? "+
				"and r.status.enumGenericState in (:states) "+
				"group by r.status.longName order by r.status.longName")
		.setLong(0, this.getReport().getDataSet().getId())
		.setBoolean(1, false)
		.setParameterList("states", this.getAllowedStates())
		.list();

		org.psygrid.data.reporting.Chart chart = new org.psygrid.data.reporting.Chart();
		chart.setTitle(this.title);
		chart.setUsePercentages(usePercentages);
		chart.setRangeAxisLabel(this.rangeAxisLabel);
		String[] types = new String[this.types.size()];
		for ( int i=0; i<this.types.size(); i++ ){
			types[i] = this.types.get(i);
		}
		chart.setTypes(types);

		chart.setRows(new ChartRow[1]);
		ChartRow row = new ChartRow();
		chart.getRows()[0] = row;

		if (isShowTotal()) {
			row.setSeries(new ChartSeries[1]);
			ChartSeries s = new ChartSeries();
			row.getSeries()[0] = s;
			s.setLabel("Total");
			s.setLabelType(IValue.TYPE_STRING);

			ChartPoint point = new ChartPoint();
			s.setPoints(new ChartPoint[1]);
			s.getPoints()[0] = point;
			
			int total = 0;
			for ( int i=0; i<results.size(); i++ ){
				Object[] data = (Object[])results.get(i);
				total += Integer.parseInt(data[1].toString());
			}
			point.setValue(Integer.toString(total));
			point.setValueType(IValue.TYPE_INTEGER);
		}
		else {
			row.setLabel("Clients");
			row.setLabelType(IValue.TYPE_STRING);
			row.setSeries(new ChartSeries[results.size()]);

			for ( int i=0; i<results.size(); i++ ){            
				Object[] data = (Object[])results.get(i);

				ChartSeries s = new ChartSeries();
				row.getSeries()[i] = s;
				s.setLabel((String)data[0]);
				s.setLabelType(IValue.TYPE_STRING);

				ChartPoint point = new ChartPoint();
				s.setPoints(new ChartPoint[1]);
				s.getPoints()[0] = point;
				point.setValue(data[1].toString());
				point.setValueType(IValue.TYPE_INTEGER);
			}
		}
		return chart;
	}

	@Override
	public org.psygrid.data.reporting.definition.dto.ProjectSummaryChart toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		//check for an already existing instance of a dto object for this 
		//management chart in the map of references
		org.psygrid.data.reporting.definition.dto.ProjectSummaryChart dtoMC = null;
		if ( dtoRefs.containsKey(this)){
			dtoMC = (org.psygrid.data.reporting.definition.dto.ProjectSummaryChart)dtoRefs.get(this);
		}
		else {
			//an instance of the management chart has not already
			//been created, so create it, and add it to the
			//map of references
			dtoMC = new org.psygrid.data.reporting.definition.dto.ProjectSummaryChart();
			dtoRefs.put(this, dtoMC);
			toDTO(dtoMC, dtoRefs, depth);
		}

		return dtoMC;
	}

	public void toDTO(org.psygrid.data.reporting.definition.dto.ProjectSummaryChart dtoC, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
		super.toDTO(dtoC, dtoRefs, depth);
		dtoC.setShowTotal(showTotal);
	}

}
