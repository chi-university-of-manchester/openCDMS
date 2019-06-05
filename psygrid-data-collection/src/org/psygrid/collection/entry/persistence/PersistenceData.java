/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as 
published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/


package org.psygrid.collection.entry.persistence;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.www.xml.security.core.types.ProjectType;

public class PersistenceData {
    public static final String CAN_LOAD_PENDING_DOCUMENTS_PROPERTY = "canLoadPendingDocuments"; //$NON-NLS-1$
    public static final String ALWAYS_ONLINE_MODE_PROPERTY = "alwaysOnlineMode"; //$NON-NLS-1$
    public static final String LINKED_STUDIES_PROPERTY = "linkedStudies";
    
    /* 
     * Only null for a very short period of time before Launcher sets it to
     * the correct initial value, so no null-checking is not required.
     */
    private Boolean alwaysOnlineMode;
    private boolean canLoadPendingDocuments;
    private boolean linkedStudies;
    private List<DataSetSummary> dataSetSummaries;
    private List<DatedProjectType> projects;
    
    /**
     * Object that contains all the logic required to support the propagation of
     * PropertyChange events.
     */
    private transient PropertyChangeSupport propertyChangeSupport;
    
    public PersistenceData() {
        canLoadPendingDocuments = true;
        dataSetSummaries = new ArrayList<DataSetSummary>();
        projects = new ArrayList<DatedProjectType>();
    }
    
    /**
     * @return whether alwaysOnlineMode has been set at least once.
     */
    public boolean isAlwaysOnlineModeSet() {
        return alwaysOnlineMode != null;
    }
    
    public boolean isAlwaysOnlineMode() {
    	if ( null == alwaysOnlineMode ){
    		return false;
    	}
        return alwaysOnlineMode.booleanValue();
    }
    
    public void setAlwaysOnlineMode(boolean alwaysOnlineMode) {
        boolean oldValue = ( null == this.alwaysOnlineMode ? !alwaysOnlineMode : this.alwaysOnlineMode.booleanValue() );
        this.alwaysOnlineMode = Boolean.valueOf(alwaysOnlineMode);
        propertyChangeSupport.firePropertyChange(ALWAYS_ONLINE_MODE_PROPERTY,
                oldValue, this.alwaysOnlineMode.booleanValue());
    }
    
    public void addDataSetSummary(DataSetSummary dataSetSummary) {
        int location = dataSetSummaries.indexOf(dataSetSummary);
        
        // The equals implementation of the persistent items is based on id and
        // class. As a result, if we get an updated DataSetSummary, equals will
        // return true even though the item is different. We just replace the 
        // existing DataSetSummary with the new one 
        if (location != -1) {
            dataSetSummaries.set(location, dataSetSummary);
        }
        else {
            dataSetSummaries.add(dataSetSummary);
        }
    }
    
    public DataSet getCompleteDataSet(String projectCode) throws IOException {
        DataSetSummary dss = getDataSetSummary(projectCode);
        if (dss == null) {
            return null;
        }
        return dss.getCompleteDataSet();
    }
    
    Date getVeryOldDate() {
        return new GregorianCalendar(1970, 01, 01).getTime();
    }
    
    /**
     * Update the list of projects to match <code>currentProjects</code>. For
     * each item in <code>currentProjects</code> it does the following:
     * 
     * <li>If the projects list contains a DatedProjectType with the same
     * codeId, then a new DatedProjectType is created with the item from
     * currentProjects and the date from DatedProjectsType. This new object
     * is then added to the projects list.
     * <li>If the projects list does not contain a DatedProjectType with the
     * same codeId, then a new DatedProjectType is created with the item from
     * currentProjects and a date returned from getVeryOldDate(). This new object
     * is then added to projects.
     * 
     * Finally, all DatedProjectType objects in the projects list whose idCode 
     * do not match the idCode of any of the items in <code>currentProjects<code> 
     * are removed from the projects list.
     * 
     * Note that the above documentation describes the behaviour of the method,
     * but not necessarily the underlying implementation.
     * 
     * @param currentProjects
     * @return a List of the objects that were removed from projects.
     */
    public List<DatedProjectType> updateProjects(List<ProjectType> currentProjects) {
        List<DatedProjectType> newProjects = 
            new ArrayList<DatedProjectType>(currentProjects.size());
        for (ProjectType currentProject : currentProjects) {
            int  i = getProjectIndex(currentProject);
            
            if (i != -1) {
                DatedProjectType oldProject = projects.get(i);
                newProjects.add(new DatedProjectType(currentProject, 
                        oldProject.getLastModified()));
                projects.remove(i);
            }
            else {
                newProjects.add(new DatedProjectType(currentProject, 
                        getVeryOldDate()));
            }
            
        }
        
        List<DatedProjectType> deletedProjects = projects;
        this.projects = newProjects;
        
        return deletedProjects;
    }
    
    private int getProjectIndex(ProjectType project) {
        for (int i = 0, c = projects.size(); i < c; ++i) {
            DatedProjectType datedProject = projects.get(i);
            if (datedProject.getIdCode().equals(project.getIdCode())) {
                return i;
            }
        }
        return -1;
    }
    
    public void removeDataSetSummary(DataSetSummary dataSetSummary) {
        dataSetSummaries.remove(dataSetSummary);
    }
    
    public DataSetSummary removeDataSetSummary(DatedProjectType project) {
        int index = getDataSetSummaryIndex(project);
        if (index == -1) {
            return null;
        }
        return dataSetSummaries.remove(index);
    }
    
    public DatedProjectType getProject(String projectCode) {
        for (DatedProjectType project : projects) {
            if (project.getIdCode().equals(projectCode)) {
                return project;
            }
        }
        return null;
    }
    
    /**
     * Returns a shallow copy of the DataSetSummary list contained in this
     * object. It is therefore safe to change the list returned by this method.
     */
    public List<DataSetSummary> getDataSetSummaries()   {
        return new ArrayList<DataSetSummary>(dataSetSummaries);
    }
    
    private int getDataSetSummaryIndex(DatedProjectType project) {
        return getDataSetSummaryIndex(project.getIdCode());
    }
    
    public int getDataSetSummaryIndex(String projectCode) {
        for (int i = 0, c = dataSetSummaries.size(); i < c; ++i) {
            DataSetSummary dss = dataSetSummaries.get(i);
            String dssProjectCode  = dss.getProjectCode();
            if (dssProjectCode != null && dssProjectCode.equals(projectCode)) {
                return i;
            }
        }
        return -1;
    }
    
    public DataSetSummary getDataSetSummary(String projectCode) {
        int index = getDataSetSummaryIndex(projectCode);
        if (index == -1) {
            return null;
        }
        return dataSetSummaries.get(index);
    }
    
    public DataSetSummary getDataSetSummary(DatedProjectType project) {
        int index = getDataSetSummaryIndex(project);
        if (index == -1) {
            return null;
        }
        return dataSetSummaries.get(index);
    }
    
    public List<DatedProjectType> getProjects(){
        return Collections.unmodifiableList(projects);
    }

    public boolean canLoadPendingDocuments() {
        return canLoadPendingDocuments;
    }
    
    public synchronized void addPropertyChangeListener(
            PropertyChangeListener listener) {
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    public synchronized void removePropertyChangeListener(
            PropertyChangeListener listener) {
        if (propertyChangeSupport == null) {
            return;
        }
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    public void setCanLoadPendingDocuments(boolean canLoadPendingDocuments) {
        boolean oldValue = this.canLoadPendingDocuments;
        this.canLoadPendingDocuments = canLoadPendingDocuments;
        propertyChangeSupport.firePropertyChange(CAN_LOAD_PENDING_DOCUMENTS_PROPERTY,
                oldValue, this.canLoadPendingDocuments);
    }

	public boolean isLinkedStudies() {
		return linkedStudies;
	}

	public void setLinkedStudies(boolean linkedStudies) {
		boolean oldValue = this.linkedStudies;
		this.linkedStudies = linkedStudies;
        propertyChangeSupport.firePropertyChange(LINKED_STUDIES_PROPERTY,
                oldValue, this.linkedStudies);
	}
}
