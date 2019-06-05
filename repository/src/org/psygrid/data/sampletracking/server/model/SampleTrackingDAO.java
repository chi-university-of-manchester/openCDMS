package org.psygrid.data.sampletracking.server.model;

import java.util.List;

/**
 * DAO for Sample Tracking. 
 * 
 * A Config represents the configuration of sample tracking for a given study.
 * 
 * A Participant relates to a repository record by the participant identifier.
 * 
 * A Sample contains all the revisions of a given sample as SampleRevision objects.
 * 
 * @author terry
 *
 */
public interface SampleTrackingDAO {

	Config getConfig(String projectCode);

	void saveConfig(Config conf);
	
	Participant getParticipant(String recordID);

	void saveParticipant(Participant participant);
	
	Sample getSample(long ID);

	void saveSample(Sample sample);
		
	List<Action> getActions(String projectCode,String status);

	void saveAction(Action action);
	
}
