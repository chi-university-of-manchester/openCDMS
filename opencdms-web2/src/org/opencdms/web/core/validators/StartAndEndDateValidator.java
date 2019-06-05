/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.opencdms.web.core.validators;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.ValidationError;
import org.opencdms.web.core.models.MonthAndYearModel;
import org.opencdms.web.core.panels.common.MonthAndYearPanel;

/**
 * @author Rob Harper
 *
 */
public class StartAndEndDateValidator extends AbstractFormValidator {

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(StartAndEndDateValidator.class);
	
	private final MonthAndYearPanel startDate;
	private final MonthAndYearPanel endDate;
	
	public StartAndEndDateValidator(MonthAndYearPanel startDate, MonthAndYearPanel endDate){
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public FormComponent<?>[] getDependentFormComponents() {
		FormComponent<?>[] components = new FormComponent<?>[4];
		components[0] = startDate.getMonth();
		components[1] = startDate.getYear();
		components[2] = endDate.getMonth();
		components[3] = endDate.getMonth();
		return components;
	}

	public void validate(Form<?> form) {
		String startMonth = startDate.getMonth().getConvertedInput();
		String startYear = startDate.getYear().getConvertedInput();
		String endMonth = endDate.getMonth().getConvertedInput();
		String endYear = endDate.getYear().getConvertedInput();
		if ( null != startMonth && null != startYear &&
				null != endMonth && null != endYear ){
			Calendar start = MonthAndYearModel.getDate(startMonth, startYear);
			Calendar end = MonthAndYearModel.getDate(endMonth, endYear);
			if ( !start.before(end) ){
				LOG.info(resourceKey());
				ValidationError error = new ValidationError();
				error.addMessageKey(resourceKey());
				endDate.getMonth().error((IValidationError)error);
			}
		}
	}

}
