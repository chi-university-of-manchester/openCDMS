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

package org.psygrid.transformers.impl.external;

import java.net.URL;

import org.psygrid.transformers.TransformerException;

/**
 * Interface for a transformer utilising an external web service.
 * 
 * Using the transform method data is passed in, formatted and 
 * sent to the remote web service. The results are then formatted 
 * and returned.
 * 
 * @author Lucy Bridges
 *
 */
public interface ExternalServiceTransformer {

	/**
	 * Get the URL of the external web service.
	 * 
	 * @return url
	 */
	public URL getUrl();
	
	/**
	 * Set the URL of the external web service.
	 * 
	 * @param url
	 */
	public void setUrl(URL url);
	
	/**
	 * Transforms the given data String using the external web service.
	 * 
	 * @param data
	 * @return result
	 * @throws TransformerException
	 */
	public String transform(String data) throws TransformerException;
}
