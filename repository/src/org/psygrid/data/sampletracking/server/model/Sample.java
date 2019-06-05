package org.psygrid.data.sampletracking.server.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a sample for a record.
 * 
 * The actual sample data is held in SampleRevision objects.
 * 
 * A new revision is added every time sample is saved.
 * 
 * @author Terry
 * @hibernate.class table="t_sampletracking_sample"
 */
public class Sample {

	private Long id;
	
	private Participant participant = null ;
	
	private List<SampleRevision> revisions = new ArrayList<SampleRevision>();

	protected Sample() {
	}	

	public Sample(Participant particpant) {
		this.participant=particpant;
	}	
	
	/**
	 * @return the id
	 * @hibernate.id column = "c_id" generator-class="native"
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	protected void setId(Long id) {
		this.id = id;
	}	
	
	/**
	 * @return the participant
	 * @hibernate.many-to-one class="org.psygrid.data.sampletracking.server.model.Participant"
     *                        column="c_participant_id"
     *                        not-null="true"
     *                        insert="false"
     *                        update="false"
	 */
	public Participant getParticipant() {
		return participant;
	}	
	
	/**
	 * @param participant the participant to set
	 */
	public void setParticipant(Participant participant) {
		this.participant = participant;
	}
	
	/**
     * @hibernate.list cascade="all"
     * @hibernate.one-to-many class="org.psygrid.data.sampletracking.server.model.SampleRevision"
     * @hibernate.key column="c_sample_id" not-null="true"
     * @hibernate.list-index column="c_index"
     */
    public List<SampleRevision> getRevisions() {
        return revisions;
    }


	public void setRevisions(List<SampleRevision> revisions) {
        this.revisions = revisions;
    }
	
}
