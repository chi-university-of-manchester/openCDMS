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

import javax.crypto.spec.SecretKeySpec;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class SecurityHelperTest extends TestCase    {
    
    private String originalString1 = "thisIsARandomString";
    
    private String originalString2 = "yetAnotherString";
    
    private String differentString1 = "aDifferentSequenceOfCharacters";
    
    public void testEqualHashes() {
        String hashedString = SecurityHelper.hash(originalString1.toCharArray());
        String equalHashedString = SecurityHelper.hash(originalString1.toCharArray());
        
        assertEquals(hashedString, equalHashedString);
    }
    
    public void testDifferentHashes() {
        String hashedString = SecurityHelper.hash(originalString1.toCharArray());
        String differentHashedString = 
            SecurityHelper.hash(originalString2.toCharArray());

        assertFalse(hashedString.equals(differentHashedString));
    }
    
    public void testPlainTextDifferentThanHash() {
        String plainTextString = differentString1;
        String hashedString = SecurityHelper.hash(plainTextString.toCharArray());
        
        assertFalse("Plain text string is the same as hashed string", 
                hashedString.equals(plainTextString));
    }
    
    public void testEncryptionWithAES() {
        try {
        SecretKeySpec keySpec = SecurityHelper.getRandomKeySpec();
        String cipherText = SecurityHelper.encrypt(originalString1.toCharArray(),
                keySpec);
        String clearText = String.valueOf(SecurityHelper.decrypt(cipherText,
                keySpec));

        assertEquals(originalString1, clearText);
        assertFalse(cipherText.equals(clearText));
        assertNotNull(clearText);
        assertNotNull(cipherText);
    } catch (Exception e) {
        fail(e.getMessage());
    }
    }
    
    public void testEncryption() {
        try {
            String cipherText = SecurityHelper.encrypt(originalString1,
                    originalString2.toCharArray());
            String clearText = SecurityHelper.decrypt(cipherText,
                    originalString2.toCharArray());

            assertEquals(originalString1, clearText);
            assertFalse(cipherText.equals(clearText));
            assertNotNull(clearText);
            assertNotNull(cipherText);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    public void testEncryptionWrongPassword() {
        try {
            String cipherText = SecurityHelper.encrypt(originalString1,
                    originalString2.toCharArray());
            SecurityHelper.decrypt(cipherText, originalString1.toCharArray());
            
            fail("De-encrypted with a different password and no exception was thrown.");
        } catch (Exception e) {
            // Exception thrown as expected
        }
    }
    
    public void testDecryptRandomValue() {
        try {
            SecurityHelper.decrypt(differentString1, originalString1.toCharArray());
            
            fail("De-encrypted random value and no exception was thrown.");
        } catch (Exception e) {
            // Exception thrown as expected
        }
    }
}
