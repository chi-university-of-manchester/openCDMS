package org.opencdms.web.modules.meds.models;

import java.io.Serializable;

public class ParticipantRegisterSearchModel implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String lastName = null;
	private String firstName = null;
	
	public ParticipantRegisterSearchModel(){
		lastName = "";
		firstName = "";
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

}
