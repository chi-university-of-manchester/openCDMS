package org.psygrid.security.statementmigration;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.security.DefaultPolicy;
import org.psygrid.security.DefaultPolicy2;
import org.psygrid.security.policyauthority.model.hibernate.Statement;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.StatementType;

public class DefaultPolicyAnalysis {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		/*
		GroupType tokenGroup = new GroupType("CNWL", "005001", "project");
		GroupType[] groups = new GroupType[]{tokenGroup};

		List<StatementType> statementDTOs = DefaultPolicy.buildStatements(groups);
		List<Statement> statements = new ArrayList<Statement>();
		List<Statement> nonTransformables = new ArrayList<Statement>();
		
		for(StatementType t : statementDTOs){
			statements.add(Statement.fromStatementType(t));
		}
		
		
		for(Statement st : statements){
			boolean canTransform = st.getRule().canBeTransformed();
			if(!canTransform){
				nonTransformables.add(st);
			}
		}
		
		int debug = 2;
		*/
		
		List<StatementType> newSimpleStatements = DefaultPolicy2.buildStatements();
		int debug = 2;
		debug *=2;
		
	}

}
