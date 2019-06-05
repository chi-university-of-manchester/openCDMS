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

package org.psygrid.outlook.patches.v1_0_1;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;



	public class Patch1 extends AbstractPatch {

	    public String getName() {
	        return "Add CSSRI at 6 months and Time Use at 12 months documents";
	    }

	    public void applyPatch(DataSet ds, String saml) throws Exception {

	        Factory factory = new HibernateFactory();

	        Document cssrid = ds.getDocument(27);
	        if ( !"CSSRI".equals(cssrid.getName())){
	            throw new RuntimeException("This is not the CSSRI follow up - it is "+cssrid.getName());
	        }

	        DocumentOccurrence cssri6 = factory.createDocumentOccurrence("6 months");
	        cssri6.setDisplayText("6 months");
	        DocumentGroup grp6 = ds.getDocumentGroup(3);
	        if ( !"6 months Group".equals(grp6.getName())){
	            throw new RuntimeException("This is not the 6 months doc group - it is "+grp6.getName());
	        }
	        cssri6.setDocumentGroup(grp6);
	        cssrid.addOccurrence(cssri6);

	        Document tuid = ds.getDocument(23);
	        if ( !"Time Use Interview Score Sheet".equals(tuid.getName())){
	            throw new RuntimeException("This is not the Time Use Interview Score Sheet - it is "+tuid.getName());
	        }

	        DocumentOccurrence tui12 = factory.createDocumentOccurrence("12 months");
	        tui12.setDisplayText("12 months");
	        DocumentGroup grp12 = ds.getDocumentGroup(4);
	        if ( !"12 months".equals(grp12.getName())){
	            throw new RuntimeException("This is not the 12 months doc group - it is "+grp12.getName());
	        }
	        tui12.setDocumentGroup(grp12);
	        tuid.addOccurrence(tui12);
	    }

	}
