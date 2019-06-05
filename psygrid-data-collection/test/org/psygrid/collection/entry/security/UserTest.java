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
package org.psygrid.collection.entry.security;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class UserTest extends TestCase {
    
    private String originalString1 = "thisIsARandomString";
    
    private String originalString2 = "yetAnotherString";
    
    private String differentString1 = "aDifferentSequenceOfCharacters";

    private String differentString2 = "somenumberOfLetters";
    
    public void testEqualUsers() {
        User user = new User(originalString1, originalString2);
        
        User equalUser = new User(originalString1, originalString2);
        
        assertEquals(user, equalUser);
    }
    
    public void testEqualUsers2() {
        User user = new User(originalString1, originalString2);
        
        User equalUser = new User(new String(originalString1), new String(originalString2));
        
        assertEquals(user, equalUser);
    }
    
    public void testDifferentUserName() {       
        User user = new User(originalString1, originalString2);
        
        User differentUser = new User(differentString1, originalString2);
        
        assertFalse(user.equals(differentUser));
    }
    
    public void testDifferentPassword() {
        User user = new User(originalString1, originalString2);

        User differentUser = new User(originalString2, differentString1);

        assertFalse(user.equals(differentUser));
    }
    
    public void testDifferentUserNameAndPassword() {
        User user = new User(originalString1, originalString2);

        User differentUser = new User(differentString2, differentString1);

        assertFalse(user.equals(differentUser));
    }
}
