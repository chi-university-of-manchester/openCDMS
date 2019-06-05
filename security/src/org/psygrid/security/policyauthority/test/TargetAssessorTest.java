package org.psygrid.security.policyauthority.test;
import org.psygrid.security.RBACTarget;
import org.psygrid.security.utils.TargetAssessor;
import org.psygrid.www.xml.security.core.types.TargetType;

import junit.framework.TestCase;


public class TargetAssessorTest extends TestCase {

	public void testNonCentreTarget(){
		TargetType nonCentreTarget = new TargetType(RBACTarget.EXPORT_LEVEL_0.toString(), String.valueOf(RBACTarget.EXPORT_LEVEL_0.ordinal()));
		boolean isCentre = TargetAssessor.targetIsCentre(nonCentreTarget);
		
		this.assertFalse(isCentre);
	}
	
	public void testCentreTarget(){
		TargetType centreTarget = new TargetType(null, "005001");
		boolean isCentre = TargetAssessor.targetIsCentre(centreTarget);
		
		this.assertTrue(isCentre);
	}
	
}
