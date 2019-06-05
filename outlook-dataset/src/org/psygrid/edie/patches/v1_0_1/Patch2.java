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

package org.psygrid.edie.patches.v1_0_1;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch2 extends AbstractPatch {

    @Override
    public String getName() {
        return "Turn on monthly summaries";
    }

    @Override
	public void applyPatch(DataSet ds, String saml) throws Exception {
		ds.setSendMonthlySummaries(true);
		for(int i=0;i<ds.numDocuments();i++){
			for(int j=0;j<ds.getDocument(i).numOccurrences();j++){
				int numReminders = ds.getDocument(i).getOccurrence(j).numReminders();
				for(int k=0;k<numReminders;k++){
					ds.getDocument(i).getOccurrence(j).removeReminder(0);
				}
				setScheduleTime(ds.getDocument(i).getOccurrence(j));
			}
		}
	}

    public void setScheduleTime(DocumentOccurrence occurrence){
    	System.out.println(occurrence.getName());
    		if(occurrence.getName().equals("Baseline 0")){
    	        occurrence.setScheduleTime(new Integer(30));
    		}
    		if(occurrence.getName().equals("1 Month")){
    	        occurrence.setScheduleTime(new Integer(61));
    		}
    		if(occurrence.getName().equals("2 Months")){
    	        occurrence.setScheduleTime(new Integer(91));
    		}
    		if(occurrence.getName().equals("3 Months")){
    	        occurrence.setScheduleTime(new Integer(122));
    		}
    		if(occurrence.getName().equals("4 Months")){
    	        occurrence.setScheduleTime(new Integer(152));
    		}
    		if(occurrence.getName().equals("5 Months")){
    	        occurrence.setScheduleTime(new Integer(183));
    		}
    		if(occurrence.getName().equals("6 Months")){
    	        occurrence.setScheduleTime(new Integer(213));
    		}
    		if(occurrence.getName().equals("9 Months")){
    	        occurrence.setScheduleTime(new Integer(305));
    		}
    		if(occurrence.getName().equals("12 Months")){
    	        occurrence.setScheduleTime(new Integer(397));
    		}
    		if(occurrence.getName().equals("15 Months")){
    	        occurrence.setScheduleTime(new Integer(489));
    		}
    		if(occurrence.getName().equals("18 Months")){
    	        occurrence.setScheduleTime(new Integer(580));
    		}
    		if(occurrence.getName().equals("21 Months")){
    	        occurrence.setScheduleTime(new Integer(671));
    		}
    		if(occurrence.getName().equals("24 Months")){
    	        occurrence.setScheduleTime(new Integer(762));
    		}
    }
}
