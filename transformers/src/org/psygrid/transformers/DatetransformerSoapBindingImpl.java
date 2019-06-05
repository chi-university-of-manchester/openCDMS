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

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.transformers.impl.DateTransformerImpl;

public class DatetransformerSoapBindingImpl implements DateTransformer {

    private static Log sLog = LogFactory.getLog(DatetransformerSoapBindingImpl.class);

    public String getMonthAndYear(String inputDate) throws RemoteException {
        final String METHOD_NAME = "getMonthAndYear";
        
        String result = null;
        try{
            result = DateTransformerImpl.getMonthAndYear(inputDate);
        }
        catch(TransformerException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw AxisFault.makeFault(ex);
        }
        catch(RuntimeException ex){
            sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            throw ex;
        }
        return result;
    }

}
