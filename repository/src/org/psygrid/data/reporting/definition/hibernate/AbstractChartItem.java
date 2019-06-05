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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.reporting.definition.IAbstractChartItem;
import org.psygrid.data.reporting.definition.ReportException;
import org.psygrid.data.utils.esl.IRemoteClient;

/**
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_abstract_chart_items" 
 * @hibernate.joined-subclass-key column="c_id"
 */
public abstract class AbstractChartItem extends Persistent implements IAbstractChartItem{

	public abstract String getLabel() throws ReportException;

	public abstract ChartPoint getPoint(Session session, IRemoteClient eslClient, Long recordId, String saml) throws ReportException;
	
	public List<ChartPoint> getPoints(Session session, IRemoteClient eslClient, Long recordId, String saml) throws ReportException{
		//By default just call getPoint and add that as the single item in the list
		List<ChartPoint> points = new ArrayList<ChartPoint>();
		points.add(getPoint(session, eslClient, recordId, saml));
		return points;
	}
	
	public boolean getMultiple() throws ReportException{
		return false;
	}
	
    @Override
    public abstract org.psygrid.data.reporting.definition.dto.AbstractChartItem toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth);

    public void toDTO(org.psygrid.data.reporting.definition.dto.AbstractChartItem dtoSCI, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        super.toDTO(dtoSCI, dtoRefs, depth);
    }

}
