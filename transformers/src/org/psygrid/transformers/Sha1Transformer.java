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

/**
 * Interface to represent a transformer to provide a hashed
 * representation of the input using the SHA1 algorithm.
 * 
 * @author Rob Harper
 *
 */
public interface Sha1Transformer extends java.rmi.Remote{

    /**
     * Return the hashed value of the input, where the hash is
     * generated using the SHA1 algorithm.
     * 
     * @param input The input string to hash
     * @return The hashed representation.
     * @throws java.rmi.RemoteException
     */
    public String encrypt(String input) throws java.rmi.RemoteException;
    
}
