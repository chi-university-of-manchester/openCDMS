/*
Copyright (c) 2005, The University of Manchester, UK.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301, USA.
*/

//Created on May 27, 2011 by John Ainsworth



package org.psygrid.security.utils;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.security.DefaultPolicy;
import org.psygrid.security.policyauthority.model.hibernate.Statement;
import org.psygrid.www.xml.security.core.types.ArgumentType;
import org.psygrid.www.xml.security.core.types.CompositeRuleType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.StatementType;

/**
 * @author jda
 *
 */
public class PrintPolicy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
				GroupType[] gta = new GroupType[]{new GroupType("Centre_1", null, null)};
		List<StatementType> ls = DefaultPolicy.buildStatements(gta);
		for(StatementType st:ls){
			int depth = 1;
			Statement s = Statement.fromStatementType(st);
			List<String> lr = new ArrayList<String>();
			processRule(st.getRule(), lr, depth);
			for(String rs: lr){
				if(rs.startsWith("And.")){
					rs=rs.substring(4);
				}
				System.out.println(s.getAction().getActionName()+" "+s.getTarget().getTargetName()+" "+rs);
			}
		}
	}

	static void processRule(CompositeRuleType crt, List<String> ls, int d) {
		String prefix = null;
		if(ls.size()!=0&&ls.get((ls.size())-1).startsWith("And")){
			prefix = ls.get((ls.size())-1);
			ls.remove(ls.size()-1);
		}
		for (ArgumentType at : crt.getPrivilege()) {
			StringBuffer result = new StringBuffer();

			if(crt.getOperator().getValue().equals("And")){
				result.append(crt.getOperator().getValue());
				result.append(".");
			}
;
			if(at.getPrivilege().getGroup()!=null){
				result.append(at.getPrivilege().getGroup().getName());
			} else {
				result.append(at.getPrivilege().getRole().getName());			
			}
			if(prefix!=null){
				result.append("."+prefix);
			}
			ls.add(result.toString());
			
		}
		d++;
		if(crt.getChildren()!=null){
			for (CompositeRuleType child : crt.getChildren()) {
				processRule(child, ls, d++);
			}
		}
	}
}
