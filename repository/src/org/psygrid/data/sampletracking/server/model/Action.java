package org.psygrid.data.sampletracking.server.model;


/**
 * 
 * Defines an action to take when a sample from 
 * a given project changes to a given state.
 *
 * @see Sample
 * @author Terry
 * @hibernate.class table="t_sampletracking_action"
 */
public class Action {

	private Long id;	
	private String projectCode;
	private String status;
	private String action;
	private String targets;
	private String subject;
	private String message;
	
	protected Action(){	
	}
	

	/**
	 * @param projectCode
	 * @param status
	 * @param action
	 * @param targets
	 * @param subject
	 * @param message
	 */
	public Action(String projectCode, String status, String action,
			String targets, String subject, String message) {
		super();
		this.projectCode = projectCode;
		this.status = status;
		this.action = action;
		this.targets = targets;
		this.subject = subject;
		this.message = message;
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
	 * @return the projectCode
	 * @hibernate.property column="c_project_code"
	 */
	public String getProjectCode() {
		return projectCode;
	}

	/**
	 * @param projectCode the projectCode to set
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	/**
	 * @return the status
	 * @hibernate.property column="c_status"
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the action
	 * @hibernate.property column="c_action"
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the targets
	 * @hibernate.property column="c_targets"
	 */
	public String getTargets() {
		return targets;
	}

	/**
	 * @param targets the targets to set
	 */
	public void setTargets(String targets) {
		this.targets = targets;
	}

	
	/**
	 * @return the subject
	 * @hibernate.property column="c_subject"
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the message
	 * @hibernate.property column="c_message"
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
}




