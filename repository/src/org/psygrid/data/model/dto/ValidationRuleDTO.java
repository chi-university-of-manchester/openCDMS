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

package org.psygrid.data.model.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.dto.LSIDDTO;


/**
 * Class to represent an abstract base validation rule.
 * <p>
 * Sub-classes of this interface represent specific validation
 * rules.
 * 
 * @author Rob Harper
 * 
 */
public abstract class ValidationRuleDTO extends PersistentDTO{

	/**
	 * Name of the validation rule
	 */
	private String name;
	
    /**
     * Description of the validation rule.
     */
    private String description;
    
    /**
     * Message that will be displayed to the user if validation
     * fails.
     */
    private String message;
    
	protected LSIDDTO lsid = null;
	
	protected LSIDDTO instanceLSID = null;
	
	protected boolean isEditable = false;
	
	protected String elementStatus;
	
	protected boolean isHeadRevision = false;
	
	protected String submisssionContext;
	
	protected boolean isRevisionCandidate = false;
	
	protected SingleVariableTestDTO test;
	
	protected ElementMetaDataDTO[] metaData = new ElementMetaDataDTO[0];
	
    
    private ValidationRuleDTO[] associatedRules = new ValidationRuleDTO[0];

    public ValidationRuleDTO(){};
    
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ValidationRuleDTO[] getAssociatedRules() {
        return associatedRules;
    }

    public void setAssociatedRules(ValidationRuleDTO[] associatedRules) {
        this.associatedRules = associatedRules;
    }
    
	public ElementMetaDataDTO[] getMetaData() {
		return metaData;
	}
	public void setMetaData(ElementMetaDataDTO[] metaData) {
		this.metaData = metaData;
	}

    public abstract org.psygrid.data.model.hibernate.ValidationRule toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs);
    
    public void toHibernate(org.psygrid.data.model.hibernate.ValidationRule hR, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hR, hRefs);
        hR.setName(name);
        hR.setDescription(description);
        hR.setMessage(message);
        hR.setIsEditable(isEditable);
        hR.setEnumStatus(elementStatus);
        hR.setHeadRevision(isHeadRevision);
        hR.setEnumSubmissionContext(submisssionContext);
        hR.setIsRevisionCandidate(isRevisionCandidate);
        
        if(lsid != null){
            hR.setLSID(lsid.toHibernate(hRefs));
        }
        
        if(instanceLSID != null){
        	hR.setInstanceLSID(instanceLSID.toHibernate(hRefs));
        }
        
        if(test != null){
            hR.setTest(this.test.toHibernate(hRefs));
        }
        
        
        if(this.metaData.length > 0){
        	for(int i = 0; i < metaData.length; i++){
        		hR.addMetaData((org.psygrid.data.model.hibernate.ElementMetaData)metaData[i].toHibernate(hRefs));
        	}
        	
        }

        
        List<org.psygrid.data.model.hibernate.ValidationRule> hAssocRules = hR.getAssociatedRules();
        for (int i=0; i<this.associatedRules.length; i++){
            ValidationRuleDTO r = this.associatedRules[i];
            if ( null != r ){
                hAssocRules.add(r.toHibernate(hRefs));
            }
        }
        
    }

	public LSIDDTO getInstanceLSID() {
		return instanceLSID;
	}

	public void setInstanceLSID(LSIDDTO instanceLSID) {
		this.instanceLSID = instanceLSID;
	}

	public LSIDDTO getLSID() {
		return lsid;
	}

	public void setLSID(LSIDDTO lsid) {
		this.lsid = lsid;
	}

	public org.psygrid.data.model.hibernate.ValidationRule toHibernate() {
        Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> dtoRefs = new HashMap<PersistentDTO, org.psygrid.data.model.hibernate.Persistent>();
        org.psygrid.data.model.hibernate.ValidationRule element = toHibernate(dtoRefs);
        dtoRefs = null;
        return element;
	}

	public String getElementStatus() {
		return elementStatus;
	}

	public void setElementStatus(String elementStatus) {
		this.elementStatus = elementStatus;
	}

	public boolean getIsEditable() {
		return isEditable;
	}

	public void setIsEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public boolean getIsHeadRevision() {
		return isHeadRevision;
	}

	public void setIsHeadRevision(boolean isHeadRevision) {
		this.isHeadRevision = isHeadRevision;
	}

	public boolean getIsRevisionCandidate() {
		return isRevisionCandidate;
	}

	public void setIsRevisionCandidate(boolean isRevisionCandidate) {
		this.isRevisionCandidate = isRevisionCandidate;
	}


	public String getSubmisssionContext() {
		return submisssionContext;
	}

	public void setSubmisssionContext(String submisssionContext) {
		this.submisssionContext = submisssionContext;
	}

	public SingleVariableTestDTO getTest() {
		return test;
	}

	public void setTest(SingleVariableTestDTO test) {
		this.test = test;
	}

}
