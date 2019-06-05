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

package org.psygrid.data.query.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.dto.DataSetDTO;
import org.psygrid.data.model.dto.GroupDTO;
import org.psygrid.data.model.dto.PersistentDTO;

/**
 * @author Rob Harper
 *
 */
public class Query extends PersistentDTO {

    /**
     * The dataset which this query is related to.
     */
    private DataSetDTO dataSet;
    
    /**
     * The name of the query.
     */
    private String name;

    /**
     * The description of the query.
     */
    private String description;

    /**
     * List of groups that will be considered when running the query.
     */
    private GroupDTO[] groups = new GroupDTO[0];

    /**
     * DN of the owner of the query.
     */
    private String owner;
    
    /**
     * The operator used to combine multiple statements (AND or OR)
     */
    private String operator;
    
    /**
     * If True, any user with sufficient privileges may run the query.
     * If False, only the owner may run the query.
     */
    private boolean publiclyVisible;
 
    /**
     * The list of statements contained by the query.
     */
    private Statement[] statements = new Statement[0];

    /**
     * Existing statements that have been deleted during an editing session
     */
    private Statement[] deletedStatements = new Statement[0];

	public DataSetDTO getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSetDTO dataSet) {
		this.dataSet = dataSet;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public GroupDTO[] getGroups() {
		return groups;
	}

	public void setGroups(GroupDTO[] groups) {
		this.groups = groups;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public boolean isPubliclyVisible() {
		return publiclyVisible;
	}

	public void setPubliclyVisible(boolean publiclyVisible) {
		this.publiclyVisible = publiclyVisible;
	}

	public Statement[] getStatements() {
		return statements;
	}

	public void setStatements(Statement[] statements) {
		this.statements = statements;
	}

	public Statement[] getDeletedStatements() {
		return deletedStatements;
	}

	public void setDeletedStatements(Statement[] deletedStatements) {
		this.deletedStatements = deletedStatements;
	}

	public org.psygrid.data.query.hibernate.Query toHibernate(){
        Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
        return toHibernate(hRefs);
 	}
	
    @Override
    public org.psygrid.data.query.hibernate.Query toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs) {
        //check for an already existing instance of a hibernate object for this 
        //report in the map of references
    	org.psygrid.data.query.hibernate.Query hQ = null;
        if ( hRefs.containsKey(this)){
            hQ = (org.psygrid.data.query.hibernate.Query)hRefs.get(this);
        }
        else{
            //an instance of the report has not already
            //been created, so create it, and add it to the
            //map of references
            hQ = new org.psygrid.data.query.hibernate.Query();
            hRefs.put(this, hQ);
            toHibernate(hQ, hRefs);
        }
        
        return hQ;
    }

    public void toHibernate(org.psygrid.data.query.hibernate.Query hQ, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
    	super.toHibernate(hQ, hRefs);
    	if ( null != this.dataSet ){
    		hQ.setDataSet(this.dataSet.toHibernate(hRefs));
    	}
    	hQ.setOwner(this.owner);
    	hQ.setPubliclyVisible(this.publiclyVisible);
    	hQ.setName(this.name);
    	hQ.setDescription(this.description);
    	hQ.setOperator(this.operator);
    	
        List<org.psygrid.data.model.hibernate.Group> hGroups = hQ.getGroups();
        for (GroupDTO g: groups){
            if ( null != g ){
                hGroups.add(g.toHibernate(hRefs));
            }
        }

        List<org.psygrid.data.query.hibernate.Statement> hStatements = hQ.getStatements();
        for (Statement s: statements){
            if ( null != s ){
                hStatements.add(s.toHibernate(hRefs));
            }
        }

        List<org.psygrid.data.query.hibernate.Statement> hDelStats = hQ.getDeletedStatements();
        for (Statement s: deletedStatements){
            if ( null != s ){
            	hDelStats.add(s.toHibernate(hRefs));
            }
        }

    }
    
}
