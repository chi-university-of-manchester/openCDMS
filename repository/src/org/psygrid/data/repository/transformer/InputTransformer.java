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

package org.psygrid.data.repository.transformer;

import java.rmi.RemoteException;
import java.util.Map;

import org.psygrid.data.model.dto.TransformerDTO;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.data.model.hibernate.Response;

public interface InputTransformer {
    
    public void transform(Record r, Map<Long, TransformerClient> transformerClients)
            throws TransformerException, RemoteException;
    public Object externalTransform(Long dsId, TransformerDTO transformer, String[] variables, String saml) 
	throws TransformerException;
    
    /**
     * 
     * @param resp - the response to transform (using the associated entry's export transfomer(s)
     * @param transformerClients - the transformer clients obtained from the dataset
     * @throws TransformerException
     * @throws RemoteException
     */
    public void transformResponseForExport(Response resp, Map<Long, TransformerClient> transformerClients) throws TransformerException, RemoteException;
}
