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


package org.psygrid.outlook;

import org.psygrid.common.ITransformersFactory;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Factory;
import org.psygrid.data.model.hibernate.Transformer;

/**
 * @author Rob Harper
 *
 */
public class TransformersFactory implements ITransformersFactory {

	/* (non-Javadoc)
	 * @see org.psygrid.common.ITransformersFactory#makeInstance()
	 */
	public Transformers makeInstance() {
		// TODO Auto-generated method stub
		return new Transformers();
	}

	private class Transformers extends org.psygrid.common.Transformers {
		private Transformers(){
			super();
		}

	    public void init(Factory factory, DataSet dataSet) {

	        transformersMap.put("date",
	                factory.createTransformer(
	                        WS_URL+"transformers/services/datetransformer",
	                        "urn:transformers.psygrid.org",
	                        "getMonthAndYear",
	                        "org.psygrid.data.model.hibernate.DateValue",
	                        true)
	                );
	        transformersMap.put("postcode",
	                factory.createTransformer(
	                        WS_URL+"transformers/services/postcodetransformer",
	                        "urn:transformers.psygrid.org",
	                        "getSOA",
	                        "org.psygrid.data.model.hibernate.TextValue",
	                        true)
	                );
	        transformersMap.put("sha1",
	                factory.createTransformer(
	                        WS_URL+"transformers/services/sha1transformer",
	                        "urn:transformers.psygrid.org",
	                        "encrypt",
	                        "org.psygrid.data.model.hibernate.TextValue",
	                        false)
	                );

	        transformersMap.put("opcrit",
	        		factory.createTransformer(
	        				WS_URL+"transformers/services/externaltransformer",
	        				"urn:transformers.psygrid.org",
	        				"opcrit",
	        				//"org.psygrid.data.model.hibernate.LongTextValue",
	        				"java.lang.String",
	        				true)
	                );

	        for (Transformer transformer : transformersMap.values()) {
	            dataSet.addTransformer(transformer);
	        }
	    }

	}

}
