package org.psygrid.extubate;

import org.psygrid.drn.address.ADDGroups;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.RBACRole;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;
import org.psygrid.www.xml.security.core.types.UserType;

public class InstallCustomExtubateRole {

	/**
	 * @param args
	 * @throws PGSecurityException 
	 */
	public static void main(String[] args) throws PGSecurityException {
		
AAManagementClient mc = new AAManagementClient("test.properties");
		
    	//TODO set the Project Type correctly for Extubate.
    	ProjectType project = new ProjectType("Extubate", "EXT", null, null, false);
				RoleType extL3 = ExtubateRBACRole.ExtubateLevel3.toRoleType();
				
		GroupType[] nogroups = new GroupType[]{};
		
    	try{		
    		RoleType[] roles = new RoleType[]{extL3};
    		ProjectDescriptionType pdt = new ProjectDescriptionType(project, nogroups, roles);
    		ProjectDescriptionType[] pdta = new ProjectDescriptionType[]{pdt};
    		mc.getPort().addRoleToProject(pdta);	
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	
	}

}
