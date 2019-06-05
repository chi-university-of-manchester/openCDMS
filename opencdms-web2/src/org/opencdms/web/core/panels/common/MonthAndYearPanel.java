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

package org.opencdms.web.core.panels.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.opencdms.web.core.models.MonthAndYearModel;

/**
 * @author Rob Harper
 *
 */
public class MonthAndYearPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private final DropDownChoice<String> month;
	private final DropDownChoice<String> year;
	
	/**
	 * @param id
	 * @param model
	 */
	@SuppressWarnings("serial")
	public MonthAndYearPanel(String id, IModel<MonthAndYearModel> model) {
		super(id, model);

		month = 
			new DropDownChoice<String>(
				"month", 
				new PropertyModel<String>(model, "month"),
				MonthAndYearModel.getMonths());
		month.setRequired(true);
		
		Calendar now = Calendar.getInstance();
		int nowYear = now.get(Calendar.YEAR);
		List<String> years = new ArrayList<String>();
		for ( int i=2005, c=nowYear; i<=c; i++ ){
			years.add(Integer.toString(i));
		}
		year = 
			new DropDownChoice<String>(
				"year", 
				new PropertyModel<String>(model, "year"),
				years);
		year.setRequired(true);
		
		FeedbackPanel feedback = new FeedbackPanel("feedback");
		feedback.setFilter(new IFeedbackMessageFilter(){

			public boolean accept(FeedbackMessage message) {
				if ( message.getReporter().equals(month) || message.getReporter().equals(year) ){
					return true;
				}
				return false;
			}
			
		});
		
		add(month);
		add(year);
		add(feedback);
	}

	public DropDownChoice<String> getMonth() {
		return month;
	}

	public DropDownChoice<String> getYear() {
		return year;
	}

}
