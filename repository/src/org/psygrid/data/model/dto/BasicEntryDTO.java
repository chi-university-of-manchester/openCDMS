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

import java.util.List;
import java.util.Map;

import org.psygrid.data.model.hibernate.EntryStatus;

public abstract class BasicEntryDTO extends EntryDTO {

    /**
     * The collection of validation rules associated with the entry.
     */
    protected ValidationRuleDTO[] validationRules = new ValidationRuleDTO[0];
    
    /**
     * The collection of transformers associated with the entry.
     */
    protected TransformerDTO[] transformers = new TransformerDTO[0];
    
    /**
     * The collection of ouptut transformers associated with the entry.
     */
    protected TransformerDTO[] outputTransformers = new TransformerDTO[0];
    
    /**
     * The collection of units that may be selected from for responses
     * to the entry.
     */
    protected UnitDTO[] units = new UnitDTO[0];

	/**
	 * If True then it is not permitted to select a standard
	 * code as the response to this entry.
	 */
	protected boolean disableStandardCodes;
	
    public TransformerDTO[] getTransformers() {
        return transformers;
    }

    public void setTransformers(TransformerDTO[] transformers) {
        this.transformers = transformers;
    }
    
    
    public TransformerDTO[] getOutputTransformers() {
        return outputTransformers;
    }

    public void setOutputTransformers(TransformerDTO[] outputTransformers) {
        this.outputTransformers = outputTransformers;
    }

    public UnitDTO[] getUnits() {
        return units;
    }

    public void setUnits(UnitDTO[] units) {
        this.units = units;
    }

    public ValidationRuleDTO[] getValidationRules() {
        return validationRules;
    }

    public void setValidationRules(ValidationRuleDTO[] validationRules) {
        this.validationRules = validationRules;
    }
    
    public boolean isDisableStandardCodes() {
		return disableStandardCodes;
	}

	public void setDisableStandardCodes(boolean disableStandardCodes) {
		this.disableStandardCodes = disableStandardCodes;
	}

	public abstract org.psygrid.data.model.hibernate.BasicEntry toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs);

    public void toHibernate(org.psygrid.data.model.hibernate.BasicEntry hBE, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
        super.toHibernate(hBE, hRefs);
        
        hBE.setDisableStandardCodes(this.disableStandardCodes);
        
        if ( null != this.entryStatus ){
            hBE.setEntryStatus(EntryStatus.valueOf(this.entryStatus));
        }
        
        List<org.psygrid.data.model.hibernate.Unit> hUnits = hBE.getUnits();
        for (int i=0; i<this.units.length; i++){
            UnitDTO u = this.units[i];
            if ( null != u ){
                hUnits.add(u.toHibernate(hRefs));
            }
        }
        
        List<org.psygrid.data.model.hibernate.ValidationRule> hRules = hBE.getValidationRules();
        for (int i=0; i<this.validationRules.length; i++){
            ValidationRuleDTO r = this.validationRules[i];
            if ( null != r ){
                hRules.add(r.toHibernate(hRefs));
            }
        }

        List<org.psygrid.data.model.hibernate.Transformer> hTransformers = hBE.getTransformers();
        for (int i=0; i<this.transformers.length; i++){
            TransformerDTO t = this.transformers[i];
            if ( null != t ){
                hTransformers.add(t.toHibernate(hRefs));
            }
        }
        
        List<org.psygrid.data.model.hibernate.Transformer> hOutputTransformers = hBE.getOutputTransformers();
        for (int i=0; i<this.outputTransformers.length; i++){
            TransformerDTO t = this.outputTransformers[i];
            if ( null != t ){
                hOutputTransformers.add(t.toHibernate(hRefs));
            }
        }
       
    }

}
