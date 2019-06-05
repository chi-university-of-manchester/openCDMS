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

package org.psygrid.data.query;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author Rob Harper
 *
 */
public enum QueryOperation {
	EQUALS,
	NOT_EQUALS,
	LESS_THAN,
	LESS_THAN_EQUALS,
	GREATER_THAN,
	GREATER_THAN_EQUALS,
	IS_BEFORE,
	IS_AFTER,
	IS_TRUE,
	IS_FALSE,
	IS_NULL,
	IS_NOT_NULL,
	IS_MISSING,
	IS_NOT_MISSING,
	STARTS_WITH;

	public String getForDisplay() {
		switch(this){
		case EQUALS:
			return "equal to";
		case NOT_EQUALS:
			return "not equal to";
		case LESS_THAN:
			return "less than";
		case LESS_THAN_EQUALS:
			return "less than/equal to";
		case GREATER_THAN:
			return "greater than";
		case GREATER_THAN_EQUALS:
			return "greater than/equal to";
		case IS_NULL:
			return "is null";
		case IS_NOT_NULL:
			return "is not null";
		case IS_MISSING:
			return "is missing code";
		case IS_NOT_MISSING:
			return "is not missing code";
		case IS_BEFORE:
			return "is before";
		case IS_AFTER:
			return "is after";
		case IS_TRUE:
			return "is true";
		case IS_FALSE:
			return "is false";
		case STARTS_WITH:
			return "starts with";
		}
		return null;
	}
	
	public static List<QueryOperation> getOperatorsForOptionEntry(){
		return new ArrayList<QueryOperation>(EnumSet.of(EQUALS, NOT_EQUALS, IS_NULL, IS_NOT_NULL, IS_MISSING, IS_NOT_MISSING));
	}

	public static List<QueryOperation> getOperatorsForTextEntry(){
		return new ArrayList<QueryOperation>(EnumSet.of(EQUALS, NOT_EQUALS, STARTS_WITH, IS_NULL, IS_NOT_NULL, IS_MISSING, IS_NOT_MISSING));
	}

	public static List<QueryOperation> getOperatorsForNumericEntry(){
		return new ArrayList<QueryOperation>(EnumSet.of(EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS, IS_NULL, IS_NOT_NULL, IS_MISSING, IS_NOT_MISSING));
	}

	public static List<QueryOperation> getOperatorsForDateEntry(){
		return new ArrayList<QueryOperation>(EnumSet.of(EQUALS, NOT_EQUALS, IS_BEFORE, IS_AFTER, IS_NULL, IS_NOT_NULL, IS_MISSING, IS_NOT_MISSING));
	}
	
	public static List<QueryOperation> getOperatorsForBooleanEntry(){
		return new ArrayList<QueryOperation>(EnumSet.of(IS_TRUE, IS_FALSE));
	}
	
}
