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

package org.psygrid.data.query.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Group;
import org.psygrid.data.model.hibernate.ModelException;
import org.psygrid.data.model.hibernate.Persistent;
import org.psygrid.data.model.hibernate.RetrieveDepth;
import org.psygrid.data.query.IQuery;
import org.psygrid.data.query.IStatement;

/**
 * @author Rob Harper
 *
 * @hibernate.joined-subclass table="t_queries"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class Query extends Persistent implements IQuery {

    /**
     * The dataset which this query is related to.
     */
    private DataSet dataSet;
    
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
    private List<Group> groups = new ArrayList<Group>();

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
    private List<Statement> statements = new ArrayList<Statement>();

    /**
     * Existing statements that have been deleted during an editing session
     */
    private List<Statement> deletedStatements = new ArrayList<Statement>();
    
    /**
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.DataSet"
     *                        column="c_dataset_id"
     *                        not-null="true"
     *                        unique="false"
     *                        cascade="none"
     */
	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = (DataSet)dataSet;
	}

	/**
	 * @hibernate.property column="c_name"
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @hibernate.property column="c_description" type="string" length="1024"
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
     * @hibernate.list cascade="none" 
     *                 table="t_query_groups"
     * @hibernate.key column="c_query_id"
     * @hibernate.many-to-many class="org.psygrid.data.model.hibernate.Group"
     *                         column="c_group_id"
     * @hibernate.list-index column="c_index"
	 */
	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public int groupCount(){
		return groups.size();
	}
	
	public Group getGroup(int index) throws ModelException {
		try{
			return groups.get(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No group exists for index "+index);
		}
	}
	
	public void addGroup(Group group){
		if ( !groups.contains(group) ){
			groups.add((Group)group);
		}
	}
	
	public void removeGroup(Group group) throws ModelException {
		if ( !groups.remove(group) ){
			throw new ModelException("The group "+group.getName()+" is not in the query's list of groups");
		}
	}
	
	/**
	 * @hibernate.property column="c_owner"
	 */
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @hibernate.property column="c_operator"
	 */
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * @hibernate.property column="c_public"
	 */
	public boolean isPubliclyVisible() {
		return publiclyVisible;
	}

	public void setPubliclyVisible(boolean publiclyVisible) {
		this.publiclyVisible = publiclyVisible;
	}

	/**
     * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.data.query.hibernate.Statement"
     * @hibernate.key column="c_query_id" not-null="true"
     * @hibernate.list-index column="c_index"
	 */
	public List<Statement> getStatements() {
		return statements;
	}

	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}
    
	public void addStatement(IStatement statement) {
		statements.add((Statement)statement);
	}

	public IStatement getStatement(int index) throws ModelException {
		try{
			return statements.get(index);
		}
		catch(IndexOutOfBoundsException ex){
			throw new ModelException("No statement exists for index "+index);
		}
	}

	public void deleteStatement(IStatement s){
		if ( statements.remove(s) ){
			deletedStatements.add((Statement)s);
		}
	}
	
	public int statementCount() {
		return statements.size();
	}

	public org.psygrid.data.query.dto.Query toDTO(){
        Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
        return toDTO(dtoRefs, RetrieveDepth.REP_SAVE);
	}
	
    public List<Statement> getDeletedStatements() {
		return deletedStatements;
	}

	public void setDeletedStatements(List<Statement> deletedStatements) {
		this.deletedStatements = deletedStatements;
	}

	@Override
    public org.psygrid.data.query.dto.Query toDTO(Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth) {
        //check for an already existing instance of a dto object for this 
        //report in the map of references
    	org.psygrid.data.query.dto.Query dtoQ = null;
        if ( dtoRefs.containsKey(this)){
            dtoQ = (org.psygrid.data.query.dto.Query)dtoRefs.get(this);
        }
        if ( null == dtoQ ){
            //an instance of the report has not already
            //been created, so create it, and add it to the
            //map of references
            dtoQ = new org.psygrid.data.query.dto.Query();
            dtoRefs.put(this, dtoQ);
            toDTO(dtoQ, dtoRefs, depth);
        }
        
        return dtoQ;
    }

    public void toDTO(org.psygrid.data.query.dto.Query dtoQ, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
    	super.toDTO(dtoQ, dtoRefs, depth);
    	dtoQ.setDataSet(this.dataSet.toDTO(dtoRefs, depth));
    	dtoQ.setOwner(this.owner);
    	dtoQ.setPubliclyVisible(this.publiclyVisible);
    	dtoQ.setName(this.name);
    	dtoQ.setDescription(this.description);
    	dtoQ.setOperator(this.operator);
    	
        org.psygrid.data.model.dto.GroupDTO[] dtoGroups = new org.psygrid.data.model.dto.GroupDTO[this.groups.size()];
        for (int i=0, c=this.groups.size(); i<c; i++){
            Group g = groups.get(i);
            dtoGroups[i] = g.toDTO(dtoRefs, depth);
        }        
        dtoQ.setGroups(dtoGroups);

        org.psygrid.data.query.dto.Statement[] dtoStatements = new org.psygrid.data.query.dto.Statement[this.statements.size()];
        for (int i=0, c=this.statements.size(); i<c; i++){
            Statement s = statements.get(i);
            dtoStatements[i] = s.toDTO(dtoRefs, depth);
        }        
        dtoQ.setStatements(dtoStatements);

        org.psygrid.data.query.dto.Statement[] dtoDelStats = new org.psygrid.data.query.dto.Statement[this.deletedStatements.size()];
        for (int i=0, c=this.deletedStatements.size(); i<c; i++){
            Statement s = deletedStatements.get(i);
            dtoDelStats[i] = s.toDTO(dtoRefs, depth);
        }        
        dtoQ.setDeletedStatements(dtoDelStats);

        
    }
}
