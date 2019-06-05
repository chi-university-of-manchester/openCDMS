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

package org.psygrid.data.query;

import org.psygrid.data.model.IPersistent;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.ModelException;

/**
 * @author Rob Harper
 *
 */
public interface IQuery extends IPersistent {

	public DataSet getDataSet();

	public void setDataSet(DataSet dataSet);

	public String getName();

	public void setName(String name);

	public String getDescription();

	public void setDescription(String description);

	public String getOwner();

	public void setOwner(String owner);

	public String getOperator();
	
	public void setOperator(String operator);
	
	public boolean isPubliclyVisible();

	public void setPubliclyVisible(boolean publiclyVisible);

	public int groupCount();
	
	public Group getGroup(int index) throws ModelException;
	
	public void addGroup(Group group);
	
	public void removeGroup(Group group) throws ModelException;
	
	public int statementCount();
	
	public IStatement getStatement(int index);
	
	public void addStatement(IStatement statement);
	
	public void deleteStatement(IStatement s);
	
	public org.psygrid.data.query.dto.Query toDTO();
}
