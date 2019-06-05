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

package org.opencdms.web.modules.query.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencdms.web.core.models.ProjectAndGroupsModel;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DateEntry;
import org.psygrid.data.model.hibernate.DerivedEntry;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.ExternalDerivedEntry;
import org.psygrid.data.model.hibernate.IntegerEntry;
import org.psygrid.data.model.hibernate.LongTextEntry;
import org.psygrid.data.model.hibernate.NumericEntry;
import org.psygrid.data.model.hibernate.Option;
import org.psygrid.data.model.hibernate.OptionEntry;
import org.psygrid.data.model.hibernate.TextEntry;
import org.psygrid.data.query.IDateStatement;
import org.psygrid.data.query.IEntryStatement;
import org.psygrid.data.query.IIntegerStatement;
import org.psygrid.data.query.INumericStatement;
import org.psygrid.data.query.IOptionStatement;
import org.psygrid.data.query.IQuery;
import org.psygrid.data.query.IStatement;
import org.psygrid.data.query.ITextStatement;
import org.psygrid.data.query.QueryOperation;
import org.psygrid.data.query.QueryStatementValue;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author Rob Harper
 *
 */
public class QueryModel extends ProjectAndGroupsModel {

	private static final long serialVersionUID = 1L;

	private final IQuery query;
	
	private DataSet dataSet;
	
	private String operator = "AND";
	
	private List<QueryStatement> statements = new ArrayList<QueryStatement>();
	
	private String queryName;
	
	private String queryDescription;
	
	private String visibility = "Private";
	
	public QueryModel(){
		super();
		query = null;
	}
	
	public QueryModel(IQuery query, ProjectType study, List<GroupType> centres){
		super(study, centres);
		this.query = query;
		dataSet = query.getDataSet();
		queryName = query.getName();
		queryDescription = query.getDescription();
		operator = query.getOperator();
		if ( query.isPubliclyVisible() ){
			visibility = "Public";
		}
		else{
			visibility = "Private";
		}
		for ( int i=0, c=query.statementCount(); i<c; i++ ){
			IStatement statement = query.getStatement(i);
			this.statements.add(new QueryStatement(statement));
		}
	}
	
	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	public List<QueryStatement> getStatements() {
		return statements;
	}

	public void setStatements(List<QueryStatement> statements) {
		this.statements = statements;
	}
	
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public String getQueryDescription() {
		return queryDescription;
	}

	public void setQueryDescription(String queryDescription) {
		this.queryDescription = queryDescription;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public IQuery getQuery() {
		return query;
	}

	public static class QueryStatement implements Serializable{
		
		private static final long serialVersionUID = 1L;
		
		private final IStatement statement;
		private DocumentOccurrence document;
		private Entry entry;
		private QueryOperation operator;
		private String textValue;
		private Double doubleValue;
		private Integer integerValue;
		private Option optionValue;
		private Date dateValue;
		private Integer index;
		
		public QueryStatement(){
			super();
			statement = null;
		}
		
		public QueryStatement(int index){
			super();
			statement = null;
			this.index = index;
		}
		
		public QueryStatement(IStatement statement){
			super();
			this.statement = statement;
			if ( statement instanceof IEntryStatement ){
				IEntryStatement es = (IEntryStatement)statement;
				this.entry = es.getEntry();
				this.document = es.getDocOcc();
				this.operator = es.getOperator();
				if ( statement instanceof INumericStatement ){
					doubleValue = ((INumericStatement)statement).getValue();
				}
				if ( statement instanceof IIntegerStatement ){
					integerValue = ((IIntegerStatement)statement).getValue();
				}
				if ( statement instanceof IDateStatement ){
					dateValue = ((IDateStatement)statement).getValue();
				}
				if ( statement instanceof IOptionStatement ){
					optionValue = ((IOptionStatement)statement).getValue();
				}
				if ( statement instanceof ITextStatement ){
					textValue = ((ITextStatement)statement).getValue();
				}
			}
		}
		
		public DocumentOccurrence getDocument() {
			return document;
		}

		public void setDocument(DocumentOccurrence document) {
			this.document = document;
		}

		public Entry getEntry() {
			return entry;
		}

		public void setEntry(Entry entry) {
			this.entry = entry;
		}

		public QueryOperation getOperator() {
			return operator;
		}

		public void setOperator(QueryOperation operator) {
			this.operator = operator;
		}

		public String getTextValue() {
			return textValue;
		}

		public void setTextValue(String textValue) {
			this.textValue = textValue;
		}

		public Option getOptionValue() {
			return optionValue;
		}

		public void setOptionValue(Option optionValue) {
			this.optionValue = optionValue;
		}
		
		public Date getDateValue() {
			return dateValue;
		}

		public void setDateValue(Date dateValue) {
			this.dateValue = dateValue;
		}

		public Double getDoubleValue() {
			return doubleValue;
		}

		public void setDoubleValue(Double doubleValue) {
			this.doubleValue = doubleValue;
		}

		public Integer getIntegerValue() {
			return integerValue;
		}

		public void setIntegerValue(Integer integerValue) {
			this.integerValue = integerValue;
		}

		public Integer getIndex() {
			return index;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}

		public IStatement getStatement() {
			return statement;
		}

		public void clearValues(){
			this.textValue = null;
			this.integerValue = null;
			this.doubleValue = null;
			this.optionValue = null;
			this.dateValue = null;
		}
		
		public boolean textValueRequired(){
			if ( entry instanceof TextEntry || entry instanceof LongTextEntry){
				switch ( operator ){
				case EQUALS:
				case NOT_EQUALS:
				case STARTS_WITH:
					return true;
				default:
					//do nothing
				}
			}
			return false;
		}
		
		public boolean doubleValueRequired(){
			if ( entry instanceof NumericEntry ||
					entry instanceof DerivedEntry ||
					entry instanceof ExternalDerivedEntry){
				switch ( operator ){
				case EQUALS:
				case NOT_EQUALS:
				case LESS_THAN:
				case LESS_THAN_EQUALS:
				case GREATER_THAN:
				case GREATER_THAN_EQUALS:
					return true;
				default:
					//do nothing
				}
			}
			return false;
		}
		
		public boolean integerValueRequired(){
			if ( entry instanceof IntegerEntry){
				switch ( operator ){
				case EQUALS:
				case NOT_EQUALS:
				case LESS_THAN:
				case LESS_THAN_EQUALS:
				case GREATER_THAN:
				case GREATER_THAN_EQUALS:
					return true;
				default:
					//do nothing
				}
			}
			return false;
		}
		
		public boolean optionValueRequired(){
			if ( entry instanceof OptionEntry){
				switch ( operator ){
				case EQUALS:
				case NOT_EQUALS:
					return true;
				default:
					//do nothing
				}
			}
			return false;
		}
		
		public boolean dateValueRequired(){
			if ( entry instanceof DateEntry){
				switch ( operator ){
				case EQUALS:
				case NOT_EQUALS:
				case IS_BEFORE:
				case IS_AFTER:
					return true;
				default:
					//do nothing
				}
			}
			return false;
		}
		
		public QueryStatementValue toQueryStatementValue() {
			QueryStatementValue result = new QueryStatementValue();
			
			result.setDateValue(dateValue);
			result.setDoubleValue(doubleValue);
			result.setIntegerValue(integerValue);
			result.setOptionValue(optionValue);
			result.setTextValue(textValue);
			
			return result;
		}
		
	}

}
