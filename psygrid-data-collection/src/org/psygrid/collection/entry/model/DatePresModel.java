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


package org.psygrid.collection.entry.model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.data.model.IDateValue;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.DateEntry;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.EntryStatus;
import org.psygrid.data.model.hibernate.ValidationRule;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationResult;

public class DatePresModel extends TextPresModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private ValueModel monthModel;
    private ValueModel yearModel;
    private ValueModel yearTextModel;
    
    public DatePresModel(BasicResponse response, IDateValue bean,
            SectionPresModel sectionOccPresModel,
            String validationPrefix, DocumentInstance docInstance) {
        super(response, bean, sectionOccPresModel, validationPrefix, docInstance);
        if (response.getEntry() instanceof DateEntry == false) {
            throw new IllegalArgumentException("response#getEntry() must " + //$NON-NLS-1$
                    "return an object of type IDateEntry, but it returns: " + //$NON-NLS-1$
                    response.getEntry().getClass());
        }
    }

    public final ValueModel getMonthModel() {
        if (monthModel == null) {
            monthModel = getModel("month"); //$NON-NLS-1$
        }
        return monthModel;
    }
    
    public final ValueModel getYearModel() {
        if (yearModel == null) {
            yearModel = getModel("year"); //$NON-NLS-1$
        }
        return yearModel;
    }
    
    public final ValueModel getYearTextModel() {
        if (yearTextModel == null) {
            yearTextModel = getModel("yearText"); //$NON-NLS-1$
        }
        return yearTextModel;
    }
    
    @Override
    public ValidationResult validate(boolean partial) {
        //FIXME Should validate year
        ValidationResult result = preValidate();
        
        if (result.hasMessages()) {
            return result;
        }
        if (getTransformedModel().getValue().equals(Boolean.TRUE) ) {
            return result;
        }
        if (getYearModel().getValue() == null){
            return super.validate(partial);
        }
        else{
            return validatePartialDate(partial);
        }
    }
    
    protected ValidationResult validatePartialDate(boolean partial) {
        ValidationResult result = preValidate();
        
        if (result.hasMessages()) {
            return result;
        }
        
        if (validationResults != null && validationResults.size() > 0) {
            return validationResults.get(0);
        }
        
        Object month = getMonthModel().getValue();
        Object year = getYearModel().getValue();
        //for validation purposes assume a day of 1, and if there
        //is no month assume January
        Date value = null;
        if ( null == month && null != year ){
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, ((Integer)year).intValue());
            cal.set(Calendar.MONTH, 0);
            cal.set(Calendar.DATE, 1);
            cal.clear(Calendar.MILLISECOND);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.HOUR);
            cal.clear(Calendar.HOUR_OF_DAY);
            cal.clear(Calendar.AM_PM);
            value = cal.getTime();
        }
        else if ( null != month && null != year ){
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, ((Integer)year).intValue());
            cal.set(Calendar.MONTH, ((Integer)month).intValue());
            cal.set(Calendar.DATE, 1);
            cal.clear(Calendar.MILLISECOND);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.HOUR);
            cal.clear(Calendar.HOUR_OF_DAY);
            cal.clear(Calendar.AM_PM);
            value = cal.getTime();
        }
        
        EntryStatus entryStatus = RendererHelper.getInstance().getEntryStatus(this);
        if (entryStatus == EntryStatus.DISABLED) {
            return result;
        }
        if (value == null || value.equals("")) { //$NON-NLS-1$
            if (partial || entryStatus == EntryStatus.OPTIONAL)    {
                return result;
            }
            if (entryStatus == EntryStatus.MANDATORY) {
                String message = Messages
                    .getString("EntryPresentationModel.mandatoryFieldMessage"); //$NON-NLS-1$
                result.addError(message);
                return result;
            }
        }
        
        for (int i = 0, c = getEntry().numValidationRules(); i < c; ++i) {
            ValidationRule vRule = getEntry().getValidationRule(i);
            List<String> validationErrors = vRule.validateAll(value);
            if (validationErrors.size() > 0) {
                for ( String msg: validationErrors){
                    result.addError(msg);
                }
                return result;
            }
        }
        
        return result;
    }

	@Override
	public void reset() {
		super.reset();
		getMonthModel().setValue(null);
		getYearModel().setValue(null);
		getYearTextModel().setValue(null);
	}
    

}
