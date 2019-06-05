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

import junit.framework.TestCase;

public class Sha1TransformerImplTest extends TestCase {

    public void testEncrypt_Success(){
        try{

            String plainText = "M33 6QE";
            String hashed1 = Sha1TransformerImpl.encrypt(plainText);
            String hashed2 = Sha1TransformerImpl.encrypt(plainText);
            String hashed3 = Sha1TransformerImpl.encrypt(null);
        
            assertFalse("Plain text and hashed text are the same", plainText.equals(hashed1));
            assertTrue("Two hashes of the same plain text are not equal", hashed1.equals(hashed2));
            assertNull("Encrypting a null nput should result in a null output",hashed3);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
}
