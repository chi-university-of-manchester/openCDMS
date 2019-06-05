package org.psygrid.security.utils;

import java.util.Arrays;
import java.util.List;

import org.psygrid.security.RBACTarget;
import org.psygrid.security.policyauthority.model.hibernate.Target;
import org.psygrid.www.xml.security.core.types.TargetType;

public class TargetAssessor {

	public static boolean targetIsCentre(Target target){
		String targetId = target.getIdCode();
		
		if(targetId == null || targetId.length() == 0)
			return false;
		
		return targetIsCentre(targetId);
	}
	
	public static boolean targetIsCentre(TargetType target){
		//This method compares the target's id code with the ordinal values of the established, enumerated targets. 
		//If the target's id is equal to one of these, it's not a centre but otherwise (not equal) we assume that it is.
		String targetId = target.getIdCode();
		
		if (targetId == null || targetId.length() == 0){
			return false;
		}else{
			return targetIsCentre(targetId);
		}
	}
	
	
	private static boolean targetIsCentre(String targetId){
		
		boolean targetIsCentre = true;
		
		List<RBACTarget> valuesList = Arrays.asList(RBACTarget.values());
		
		int maxOrdinal = -1;
		
		for(RBACTarget t : valuesList){
			maxOrdinal = t.ordinal() > maxOrdinal ? t.ordinal() : maxOrdinal;
		}
		
		for(int i = 0; i <= maxOrdinal; i++){
			String ordinalAsString = new Integer(i).toString();
			if(ordinalAsString.equals(targetId)){
				//It's not a centre but one of the targets defined within RBACTarget
				targetIsCentre = false;
				break;
			}
		}
		
			return targetIsCentre;
	}
	

		
	
	
}
