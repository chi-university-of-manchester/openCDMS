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

public interface DateTransformer extends java.rmi.Remote{

    /**
     * Return just the month and year (in mm/yyyy format) from a date
     * presented in dd/mm/yyyy format.
     * 
     * @param inputDate The date to transform, in dd/mm/yyyy format.
     * @return The month and year only, in mm/yyyy format.
     * @throws java.rmi.RemoteException
     */
    public String getMonthAndYear(String inputDate) throws java.rmi.RemoteException;
    
}
