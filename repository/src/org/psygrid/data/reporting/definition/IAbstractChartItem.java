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


package org.psygrid.data.reporting.definition;

import java.util.List;

import org.hibernate.Session;
import org.psygrid.data.model.IPersistent;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.utils.esl.IRemoteClient;

/**
 * @author Rob Harper
 *
 */
public interface IAbstractChartItem extends IPersistent {

	public String getLabel() throws ReportException;

	/**
	 * Generate the chart point for this chart item.
	 * <p>
	 * This method should be called when getMultiple returns False.
	 * 
	 * @param session Hibernate session.
	 * @param eslClient ESL remote client
	 * @param recordId Unique ID of the record report is being generated for.
	 * @param saml SAML assertion
	 * @return ChartPoint
	 * @throws ReportException
	 */
	public ChartPoint getPoint(Session session, IRemoteClient eslClient, Long recordId, String saml) throws ReportException;
	
	/**
	 * Generate the chart point(s) for this chart item.
	 * <p>
	 * This method should be called when getMultiple returns True.
	 * 
	 * @param session Hibernate session.
	 * @param eslClient ESL remote client
	 * @param recordId Unique ID of the record report is being generated for.
	 * @param saml SAML assertion
	 * @return List&lt;ChartPoint&gt;
	 * @throws ReportException
	 */
	public List<ChartPoint> getPoints(Session session, IRemoteClient eslClient, Long recordId, String saml) throws ReportException;
	
	/**
	 * Find out whether this chart item is expected to have multiple values
	 * associated with it or not.
	 * <p>
	 * In general should return False (i.e. one value per chart item) but can 
	 * return True in special circumstances.
	 * 
	 * @return Boolean.
	 * @throws ReportException
	 */
	public boolean getMultiple() throws ReportException;
}
