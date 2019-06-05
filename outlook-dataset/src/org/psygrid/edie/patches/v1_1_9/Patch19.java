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

package org.psygrid.edie.patches.v1_1_9;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DocumentOccurrence;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch19 extends AbstractPatch {

	   @Override
	    public String getName() {
	        return "Reduce timeline by 15 days";
	    }

	    @Override
		public void applyPatch(DataSet ds, String saml) throws Exception {

			for(int i=0;i<ds.numDocuments();i++){
				for(int j=0;j<ds.getDocument(i).numOccurrences();j++){
					setScheduleTime(ds.getDocument(i).getOccurrence(j));
				}
			}
		}

	    public void setScheduleTime(DocumentOccurrence occurrence){
	    	System.out.println(occurrence.getName());
	    		if(occurrence.getName().equals("Baseline 0")){
	    	        occurrence.setScheduleTime(new Integer(15));
	    		}
	    		if(occurrence.getName().equals("1 Month")){
	    	        occurrence.setScheduleTime(new Integer(46));
	    		}
	    		if(occurrence.getName().equals("2 Months")){
	    	        occurrence.setScheduleTime(new Integer(76));
	    		}
	    		if(occurrence.getName().equals("3 Months")){
	    	        occurrence.setScheduleTime(new Integer(107));
	    		}
	    		if(occurrence.getName().equals("4 Months")){
	    	        occurrence.setScheduleTime(new Integer(137));
	    		}
	    		if(occurrence.getName().equals("5 Months")){
	    	        occurrence.setScheduleTime(new Integer(168));
	    		}
	    		if(occurrence.getName().equals("6 Months")){
	    	        occurrence.setScheduleTime(new Integer(198));
	    		}
	    		if(occurrence.getName().equals("9 Months")){
	    	        occurrence.setScheduleTime(new Integer(290));
	    		}
	    		if(occurrence.getName().equals("12 Months")){
	    	        occurrence.setScheduleTime(new Integer(382));
	    		}
	    		if(occurrence.getName().equals("15 Months")){
	    	        occurrence.setScheduleTime(new Integer(474));
	    		}
	    		if(occurrence.getName().equals("18 Months")){
	    	        occurrence.setScheduleTime(new Integer(565));
	    		}
	    		if(occurrence.getName().equals("21 Months")){
	    	        occurrence.setScheduleTime(new Integer(656));
	    		}
	    		if(occurrence.getName().equals("24 Months")){
	    	        occurrence.setScheduleTime(new Integer(747));
	    		}
	    }
}
