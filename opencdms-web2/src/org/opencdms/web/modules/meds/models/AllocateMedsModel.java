package org.opencdms.web.modules.meds.models;

import org.opencdms.web.core.models.ProjectModel;
import org.psygrid.www.xml.security.core.types.GroupType;

public class AllocateMedsModel extends ProjectModel {
	
	private static final long serialVersionUID = 1L;

	private GroupType centre = null;
	private String participant = null;

	public GroupType getCentre() {
		return centre;
	}

	public void setCentre(GroupType centre) {
		this.centre = centre;
	}

	public String getParticipant() {
		return participant;
	}

	public void setParticipant(String participant) {
		this.participant = participant;
	}
	
}
