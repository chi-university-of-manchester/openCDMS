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

package org.psygrid.transformers;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.transformers.impl.Sha1TransformerImpl;

/**
 * Class to represent a transformer to provide a hashed
 * representation of the input using the SHA1 algorithm.
 * 
 * @author Rob Harper
 *
 */
public class Sha1TransformerSoapBindingImpl implements Sha1Transformer {

    private static Log sLog = LogFactory.getLog(Sha1TransformerSoapBindingImpl.class);

    public String encrypt(String input) throws RemoteException {
        final String METHOD_NAME = "encrypt";

        String result = null;
        try{
            result = Sha1TransformerImpl.encrypt(input);
        }
        catch(RuntimeException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw ex;
        }
        return result;
    }

}
