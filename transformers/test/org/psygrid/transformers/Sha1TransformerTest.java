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

import junit.framework.TestCase;

public class Sha1TransformerTest extends TestCase {

    public void testEncrypt_Success(){
        try{
            Sha1TransformerServiceLocator locator = new Sha1TransformerServiceLocator();
            Sha1Transformer transformer = locator.getsha1transformer();

            String plainText = "M33 6QE";
            String hashed1 = transformer.encrypt(plainText);
            String hashed2 = transformer.encrypt(plainText);
        
            assertFalse("Plain text and hashed text are the same", plainText.equals(hashed1));
            assertTrue("Two hashes of the same plain text are not equal", hashed1.equals(hashed2));
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            fail("Exception: "+ex.toString());
        }
    }
    
}
