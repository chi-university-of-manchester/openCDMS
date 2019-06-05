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

import java.io.IOException;
import java.util.Date;

import org.psygrid.collection.entry.chooser.ChoosableType;
import org.psygrid.collection.entry.chooser.DataSetGetter;
import org.psygrid.data.model.hibernate.*;

/**
 * A Wrapper over IDataSet. When
 * {@link org.psygrid.data.repository.client.RepositoryClient#getModifiedDataSets(Date, String)}
 * is called, each IDataSet returned only contains a summary of information
 * that the real IDataSet has. This class basically makes this information
 * available, and provides a way to retrieve the complete IDataSet.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 */
public final class DataSetSummary {
    private DataSet dataSetSummaryDelegate;
    
    //TODO Consider making it a WeakReference.
    private transient DataSet completeDataSet;
    
    private transient DataSetGetter dataSetGetter;
    
    /**
     * Required by XStream when using pure java mode. Should not be used
     * otherwise.
     */
    public DataSetSummary() {
        // Empty constructor
    }
    
    public DataSetSummary(DataSet dataSetSummary) {
        super();
        this.dataSetSummaryDelegate = dataSetSummary;
    }
    
    public DataSetSummary(DataSet dataSetSummary, DataSet completeDataset) {
        super();
        this.dataSetSummaryDelegate = dataSetSummary;
        this.completeDataSet = completeDataset;
    }
    
    public final String getVersionNo() {
        return dataSetSummaryDelegate.getVersionNo();
    }
    
    public final Integer getAutoversionNum() {
    	return dataSetSummaryDelegate.getAutoVersionNo();
    }
    
    public final Date getDateModified() {
        return dataSetSummaryDelegate.getDateModified();
    }
    public final boolean isPublished() {
        return dataSetSummaryDelegate.isPublished();
    }
    public final int numAllConsentFormGroups() {
        return dataSetSummaryDelegate.numAllConsentFormGroups();
    }
    
    public final ConsentFormGroup getAllConsentFormGroup(int index) throws ModelException {
        return dataSetSummaryDelegate.getAllConsentFormGroup(index);
    }
    
    public final int numValidationRules() {
        return dataSetSummaryDelegate.numValidationRules();
    }
    
    public final ValidationRule getValidationRule(int index) throws ModelException {
        return dataSetSummaryDelegate.getValidationRule(index);
    }
    
    public final String getName() {
        return dataSetSummaryDelegate.getName();
    }
    
    public final String getDisplayText() {
        return dataSetSummaryDelegate.getDisplayText();
    }
    
    public final String getDescription() {
        return dataSetSummaryDelegate.getDescription();
    }
    
    public final String getProjectCode() {
        return dataSetSummaryDelegate.getProjectCode();
    }
    
    public final ChoosableType getType() {
        return ChoosableType.DATASET;
    }
    
    public final int numStatus() {
        return dataSetSummaryDelegate.numStatus();
    }
    public final Status getStatus(int index) throws ModelException {
        return dataSetSummaryDelegate.getStatus(index);
    }
    
    public final Long getId() {
        return dataSetSummaryDelegate.getId();
    }
    
    public final BinaryObject getInfoSheet() {
        return dataSetSummaryDelegate.getInfoSheet();
    }
    
    public final boolean isNoReviewAndApprove(){
    	return dataSetSummaryDelegate.isNoReviewAndApprove();
    }
    
    public final String getSecondaryProjectCode(){
    	return dataSetSummaryDelegate.getSecondaryProjectCode();
    }
    
    public boolean getUseExternalIdAsPrimary(){
    	return dataSetSummaryDelegate.getUseExternalIdAsPrimary();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        
        if (!(o instanceof DataSetSummary)) {
            return false;
        }
        
        DataSetSummary dss = (DataSetSummary) o;
        
        return dss.dataSetSummaryDelegate.equals(this.dataSetSummaryDelegate);
    }
    
    @Override
    public int hashCode() {
        return dataSetSummaryDelegate.hashCode();
    }

    public void setDataSetGetter(DataSetGetter getter) {
        this.dataSetGetter = getter;
    }
    
    public DataSetGetter getDataSetGetter() {
        return dataSetGetter;
    }
    
    public DataSet getCompleteDataSet() throws IOException {
        if (completeDataSet == null) {
            if (dataSetGetter == null) {
                synchronized (PersistenceManager.getInstance()) {
                    completeDataSet = PersistenceManager.getInstance().loadDataSet(
                            this);
                }
            }
            else {
                completeDataSet = dataSetGetter.getCompleteDataSet();
            }
        }
        return completeDataSet;
    }

	public boolean isLocked() {
		return false;
	} 
    
	public boolean isComplete() {
		return false;
	}
}
