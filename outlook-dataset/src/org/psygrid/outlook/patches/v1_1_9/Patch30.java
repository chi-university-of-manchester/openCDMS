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

package org.psygrid.outlook.patches.v1_1_9;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.HibernateFactory;
import org.psygrid.data.model.hibernate.Transformer;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch30 extends AbstractPatch {

	@Override
	public String getName() {
		return "Add the new Opcrit transformer to the Outlook dataset";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		Factory factory = new HibernateFactory();
		System.out.println("Creating new Opcrit transformer");
		String WS_URL = "https://localhost/";

		Transformer transformer =
	        		factory.createTransformer(
                            WS_URL + "transformers/services/externaltransformer",
                            "urn:transformers.psygrid.org",
                            "opcrit",
                            "java.lang.String",
                            true);

	    ds.addTransformer(transformer);

	}

}
