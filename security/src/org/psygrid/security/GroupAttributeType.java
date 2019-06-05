package org.psygrid.security;

public enum GroupAttributeType {

	PHARMACY(0, null);
	
	private final int id;
	
	private final String alias;

	GroupAttributeType(int id, String alias) {
		this.id = id;
		this.alias = alias;
	}

	public int id() {
		return id;
	}
	
	public String alias(){
		return alias;
	}
	
}
