package org.psygrid.meds.events;

import java.util.Date;

import org.psygrid.meds.project.Project;

/**
 * Holds info about a project view event
 * @author Bill
 * @hibernate.class table="t_project_view_event"
 */
public class ProjectViewEvent extends Event {
	
	private Project viewedProject;
	
	protected ProjectViewEvent(){
		super();
		viewedProject = null;
	}
	
	public ProjectViewEvent(String sysUser, Date eventDate, Project p) {
		super(sysUser, eventDate);
		viewedProject = p;
	}


	protected void setViewedProject(Project p){
		viewedProject = p;
	}
	
	/**
	 * 
	 * @return - the project that has been viewed
	 * @hibernate.many-to-one class="org.psygrid.meds.project.Project"
	 *                        column="c_project_id"
	 *                        not-null="true"
	 *                        cascade="none"
	 */
	public Project getViewedProject() {
		return viewedProject;
	}

}
