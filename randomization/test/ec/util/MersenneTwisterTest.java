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

package ec.util;

import junit.framework.TestCase;

public class MersenneTwisterTest extends TestCase {

    public void testMersenne(){
        
        MersenneTwister mt = new MersenneTwister(987654321);
        for ( int i=0; i<100; i++ ){
            System.out.println(mt.nextFloat());
        }
        
    }
    
}
