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

import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.utils.esl.IRemoteClient;

/**
 * Interface representing a chart in a management report that displays
 * a list of study participants receiving treatments.
 * 
 * For projects using the psygrid randomisation service.
 * 
 * See bug 662 for report requirements.
 * 
 * @author Lucy Bridges
 */
public interface IReceivingTreatmentChart extends IManagementChart {

	public Calendar getEndDate();
	public Calendar getStartDate();
	public void setTimePeriod(Calendar startDate, Calendar endDate);
	public int numGroups();
	public void addGroup(Group group) throws ReportException;
	public Group getGroup(int index) throws ReportException;
	public List<Group> getGroups();
	public org.psygrid.data.reporting.Chart[] generateChart(Session session, IRemoteClient client, String saml);
	
	
}
