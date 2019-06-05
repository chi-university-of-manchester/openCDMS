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

package org.psygrid.transformers.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of function to hash a string using the SHA1
 * algorithm.
 * <p>
 * The hash is converted into a hexadecimal string before it is
 * returned to the user.
 * 
 * @author Rob Harper
 *
 */
public class Sha1TransformerImpl  {

    private static Log sLog = LogFactory.getLog(Sha1TransformerImpl.class);

    /**
     * SHA1 hashing method.
     * 
     * @param input The input string to hash.
     * @return The hashed value, as a hexadecimal string.
     */
    public static String encrypt(String input) {
        final String METHOD_NAME = "encrypt";

        String hashed = null;
        if ( null != input ){
            try{
                MessageDigest md = MessageDigest.getInstance( "SHA" );
                byte[] result = md.digest(input.getBytes());
                hashed = hexEncode(result);
            }
            catch(NoSuchAlgorithmException ex){
                //just log a warning - this catch block should
                //never be entered, since the algorithm name is
                //hard-coded
                sLog.error(METHOD_NAME+": "+ex.getClass().getSimpleName(),ex);
            }
        }
        return hashed;
    }

    /**
     * The byte[] returned by MessageDigest does not have a nice
     * textual representation, so some form of encoding is usually performed.
     * <p>
     * This implementation follows the example of David Flanagan's book
     * "Java In A Nutshell", and converts a byte array into a String
     * of hex characters.
     * <p>
     * Another popular alternative is to use a "Base64" encoding.
     * 
     * @param aInput Byte array to convert to hex.
     * @return the hex representation of the byte array.
     * @see http://www.javapractices.com/Topic56.cjp
     */
    static private String hexEncode( byte[] aInput){
        StringBuffer result = new StringBuffer();
        char[] digits = {'0', '1', '2', '3', '4','5','6','7','8','9','a','b','c','d','e','f'};
        for ( int idx = 0; idx < aInput.length; ++idx) {
            byte b = aInput[idx];
            result.append( digits[ (b&0xf0) >> 4 ] );
            result.append( digits[ b&0x0f] );
        }
        return result.toString();
    }
     
}
