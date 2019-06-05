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


// Created on Oct 10, 2006 by John Ainsworth

package org.psygrid.security;

import org.psygrid.security.accesscontrol.AEFGroup;
import org.psygrid.www.xml.security.core.types.TargetType;

/**
 * @author jda
 *
 */
public enum RBACTarget {

	ANY(0, PGSecurityConstants.ANY, PGSecurityConstants.ANY),
	
    //EXPORT SECURITY TAGS.
	EXPORT_LEVEL_0(1,   "EXPORT_LEVEL_0", null), //MOST PERMISSIVE LEVEL OF SECURITY
	EXPORT_LEVEL_1(2,   "EXPORT_LEVEL_1", null),
	EXPORT_LEVEL_2(3,   "EXPORT_LEVEL_2", null),
	EXPORT_LEVEL_3(4,   "EXPORT_LEVEL_3", null),
	EXPORT_LEVEL_4(5,   "EXPORT_LEVEL_4", null),
	EXPORT_LEVEL_5(6,   "EXPORT_LEVEL_5", null),
	EXPORT_LEVEL_6(7,   "EXPORT_LEVEL_6", null),
	EXPORT_LEVEL_7(8,   "EXPORT_LEVEL_7", null),
	EXPORT_LEVEL_8(9,   "EXPORT_LEVEL_8", null),
	EXPORT_LEVEL_9(10,  "EXPORT_LEVEL_9", null),
	EXPORT_LEVEL_10(11, "EXPORT_LEVEL_10", null),
	EXPORT_LEVEL_11(12, "EXPORT_LEVEL_11", null),
	EXPORT_LEVEL_12(13, "EXPORT_LEVEL_12", null),
	EXPORT_LEVEL_13(14, "EXPORT_LEVEL_13", null),
	EXPORT_LEVEL_14(15, "EXPORT_LEVEL_14", null),
	EXPORT_LEVEL_15(16, "EXPORT_LEVEL_15", null), //MOST RESTRICTIVE LEVEL OF SECURITY
	//END EXPORT SECURITY TAGS
	
	GROUP_INCLUSION(17, "GROUP_INCLUSION", null);
	
	private final int id;
	private final String code;
	
	private final String realName;

	RBACTarget(int id, String rn, String code) {
		this.id = id;
		this.realName = rn;
		this.code = code;
	}

	public int id() {
		return id;
	}
	
	public String realName(){
		return realName;
	}

	public String code(){
		return code;
	}
	
	public String idAsString(){
		return new Integer(id).toString();
	}
	
	public TargetType toTargetType(){
		return new TargetType(realName, code());
	}
	
	public AEFGroup toAEFGroup(){
		return new AEFGroup(realName, code(), null);
	}
	
	public static TargetType[] allAsTargets(){
		RBACTarget[] g = RBACTarget.values();
		TargetType[] rta = new TargetType[g.length];
		for(int i=0; i <g.length; i++){
			rta[i]=new TargetType(g[i].realName,g[i].code());
		}
		return rta;
	}
	
}
