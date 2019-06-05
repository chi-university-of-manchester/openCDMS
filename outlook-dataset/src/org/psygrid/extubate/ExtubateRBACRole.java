package org.psygrid.extubate;

import org.psygrid.security.RBACRole;
import org.psygrid.www.xml.security.core.types.PrivilegeType;
import org.psygrid.www.xml.security.core.types.RoleType;

public enum ExtubateRBACRole {
	
	ExtubateLevel3(21, null);
	
	private final int id;
	private final String alias;

	ExtubateRBACRole(int id, String alias) {
		this.id = id;
		this.alias = alias;
	}

	public int id() {
		return id;
	}
	
	public String alias(){
		return alias;
	}
	
	public String idAsString(){
		return new Integer(id).toString();
	}
	
	public  RoleType toRoleType(){
		return new RoleType(toString(), idAsString());
	}
	
	public static RoleType[] allRoles(){
		RBACRole[] rbacaa = RBACRole.values();
		RoleType[] rta = new RoleType[rbacaa.length];
		for(int i=0; i <rbacaa.length; i++){
			rta[i]=new RoleType(rbacaa[i].toString(),rbacaa[i].idAsString());
		}
		return rta;
	}
	
	public static PrivilegeType[] allAsPrivileges(){
		RBACRole[] r = RBACRole.values();
		PrivilegeType[] rta = new PrivilegeType[r.length];
		for(int i=0; i <r.length; i++){
			RoleType rt =new RoleType(r[i].toString(),r[i].idAsString());
			rta[i] = new PrivilegeType();
			rta[i].setRole(rt);
		}
		return rta;
	}
	
	public static RoleType[] noRoles(){
		return new RoleType[]{};
	}
	public PrivilegeType toPrivilegeType(){
		return new PrivilegeType(new RoleType(toString(), idAsString()), null);
	}

}
