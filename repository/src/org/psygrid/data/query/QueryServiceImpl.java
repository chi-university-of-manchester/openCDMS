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

import org.psygrid.data.query.dto.Query;
import org.psygrid.data.repository.dao.DAOException;
import org.psygrid.data.utils.service.AbstractServiceImpl;
import org.psygrid.security.RBACAction;

/**
 * @author Rob Harper
 *
 */
public class QueryServiceImpl extends AbstractServiceImpl implements QueryServiceInternal {

	/**
	 * Name of the component, used for audit logging
	 */
	private static final String COMPONENT_NAME = "Queryservice";

	private QueryDAO queryDAO = null;
	
	protected String getComponentName() {
		return COMPONENT_NAME;
	}

    public QueryDAO getQueryDAO() {
		return queryDAO;
	}

	public void setQueryDAO(QueryDAO queryDAO) {
		this.queryDAO = queryDAO;
	}

	public void saveQuery(Query query, String saml) {
			String userName = findUserName(saml);
			String projectCode = repositoryDAO.getProjectCodeForDataset(query.getDataSet().getId());
			checkPermissionsByProject(saml, "saveQuery", RBACAction.ACTION_DR_SAVE_QUERY, projectCode);
			queryDAO.saveQuery(query, userName);
	}

	public Query[] getMyQueries(String project, String saml) {
			String userName = findUserName(saml);
			checkPermissionsByProject(saml, "getMyQueries", RBACAction.ACTION_DR_GET_MY_QUERIES, project);
			return queryDAO.getMyQueries(project, userName);
	}

	public Query getQuery(long queryId, String saml) {
			String projectCode = queryDAO.getProjectCodeForQuery(queryId);
			String[] groups = queryDAO.getGroupsForQuery(queryId);
			checkPermissionsByGroups(saml, "getQuery", RBACAction.ACTION_DR_EXECUTE_QUERY, projectCode, groups);
			return queryDAO.getQuery(queryId);
	}

	public long executeQueryForCount(long queryId, String saml) {
		String projectCode = queryDAO.getProjectCodeForQuery(queryId);
		String[] groups = queryDAO.getGroupsForQuery(queryId);
		checkPermissionsByGroups(saml, "executeQueryForCount",
				RBACAction.ACTION_DR_EXECUTE_QUERY, projectCode, groups);
		return queryDAO.executeQueryForCount(queryId);
	}

	public String[] executeQueryForIdentifiers(long queryId, String saml) {
		String projectCode = queryDAO.getProjectCodeForQuery(queryId);
		String[] groups = queryDAO.getGroupsForQuery(queryId);
		checkPermissionsByGroups(saml, "executeQueryForIdentifiers",
				RBACAction.ACTION_DR_EXECUTE_QUERY, projectCode, groups);
		return queryDAO.executeQueryForIdentifiers(queryId);
	}

	public String[] executeQueryForExternalIdentifiers(long queryId, String saml) {
		String projectCode = queryDAO.getProjectCodeForQuery(queryId);
		String[] groups = queryDAO.getGroupsForQuery(queryId);
		checkPermissionsByGroups(saml, "executeQueryForExternalIdentifiers",
				RBACAction.ACTION_DR_EXECUTE_QUERY, projectCode, groups);
		return queryDAO.executeQueryForExternalIdentifiers(queryId);
	}

	public String[] executeQueryForIdentifiers(Long queryId) throws DAOException {
		return queryDAO.executeQueryForIdentifiers(queryId);
	}

}
