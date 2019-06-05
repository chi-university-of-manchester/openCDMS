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

package org.psygrid.securitymanager.security;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Password generator class.
 * <p>
 * Adapted from http://mindprod.com/products1.html#PASSWORD
 * 
 * @author Rob Harper
 *
 */
public class PasswordGenerator {

    /**
     * high quality random number generator
     */
    private static Random wheel;
    
    static
    {
    try
        {
        wheel = SecureRandom.getInstance( "SHA1PRNG", "SUN" );
        }
    catch ( Exception e )
        {
        System.out
                .println(
                        "Reverting to lower quality random number generator" );
        wheel = new Random();
        }
    }

    public static String generatePassword(int length, boolean caseMatters){
        StringBuffer sb = new StringBuffer( length );
        for ( int i = 0; i < length; i++ )
            {
            int digit = wheel.nextInt( 12 + 10 + 26 + ( caseMatters ? 26 : 0 ) );
            char letter =
                    "!@#$%^&*?_~-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
                            .charAt( digit );
            sb.append( letter );
            }
        return sb.toString();

	}
	
}
