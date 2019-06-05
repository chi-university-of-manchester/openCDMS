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

package org.psygrid.common.security;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;

/**
 * @author Rob Harper
 *
 */
public class PasswordStrengthCheckTest {

	@Test
	public void testCheck(){

		AssertJUnit.assertEquals("Strength incorrect for 'abcd'", 4, PasswordStrengthCheck.check("abcd"));
		AssertJUnit.assertEquals("Strength incorrect for 'abcdefg'", 7, PasswordStrengthCheck.check("abcdefg"));
		AssertJUnit.assertEquals("Strength incorrect for 'abcdefghij'", 13, PasswordStrengthCheck.check("abcdefghij"));
		AssertJUnit.assertEquals("Strength incorrect for 'abcdefghijklmnop'", 19, PasswordStrengthCheck.check("abcdefghijklmnop"));
		
		AssertJUnit.assertEquals("Strength incorrect for 'ABCD'", 8, PasswordStrengthCheck.check("ABCD"));
		AssertJUnit.assertEquals("Strength incorrect for 'ABCDEFG'", 11, PasswordStrengthCheck.check("ABCDEFG"));
		AssertJUnit.assertEquals("Strength incorrect for 'ABCDEFGHIJ'", 17, PasswordStrengthCheck.check("ABCDEFGHIJ"));
		AssertJUnit.assertEquals("Strength incorrect for 'ABCDEFGHIJKLMNOP'", 23, PasswordStrengthCheck.check("ABCDEFGHIJKLMNOP"));
		
		AssertJUnit.assertEquals("Strength incorrect for 'Abcd'", 11, PasswordStrengthCheck.check("Abcd"));
		AssertJUnit.assertEquals("Strength incorrect for 'abcDefg'", 14, PasswordStrengthCheck.check("abcDefg"));
		AssertJUnit.assertEquals("Strength incorrect for 'abcdeFfghij'", 20, PasswordStrengthCheck.check("abcdeFfghij"));
		AssertJUnit.assertEquals("Strength incorrect for 'abcdefghijklmnoP'", 26, PasswordStrengthCheck.check("abcdefghijklmnoP"));
		
		AssertJUnit.assertEquals("Strength incorrect for 'ab3de'", 14, PasswordStrengthCheck.check("ab3de"));
		AssertJUnit.assertEquals("Strength incorrect for '1bcde'", 14, PasswordStrengthCheck.check("1bcde"));
		AssertJUnit.assertEquals("Strength incorrect for 'abcd5'", 14, PasswordStrengthCheck.check("abcd5"));
		
		AssertJUnit.assertEquals("Strength incorrect for 'AB3DE'", 18, PasswordStrengthCheck.check("AB3DE"));
		AssertJUnit.assertEquals("Strength incorrect for '1BCDE'", 18, PasswordStrengthCheck.check("1BCDE"));
		AssertJUnit.assertEquals("Strength incorrect for 'ABCD5'", 18, PasswordStrengthCheck.check("ABCD5"));

		AssertJUnit.assertEquals("Strength incorrect for 'AB3de'", 21, PasswordStrengthCheck.check("AB3de"));
		AssertJUnit.assertEquals("Strength incorrect for '1bcDE'", 21, PasswordStrengthCheck.check("1bcDE"));
		AssertJUnit.assertEquals("Strength incorrect for 'abCD5'", 21, PasswordStrengthCheck.check("abCD5"));

		AssertJUnit.assertEquals("Strength incorrect for '1'", 8, PasswordStrengthCheck.check("1"));
		AssertJUnit.assertEquals("Strength incorrect for '12'", 8, PasswordStrengthCheck.check("12"));
		AssertJUnit.assertEquals("Strength incorrect for '123'", 13, PasswordStrengthCheck.check("123"));

		AssertJUnit.assertEquals("Strength incorrect for 'ab345'", 19, PasswordStrengthCheck.check("ab345"));
		AssertJUnit.assertEquals("Strength incorrect for '1b3d5'", 19, PasswordStrengthCheck.check("1b3d5"));
		AssertJUnit.assertEquals("Strength incorrect for '12c4e'", 19, PasswordStrengthCheck.check("12c4e"));
		
		AssertJUnit.assertEquals("Strength incorrect for '!'", 8, PasswordStrengthCheck.check("!"));
		AssertJUnit.assertEquals("Strength incorrect for '!!'", 13, PasswordStrengthCheck.check("!!"));

		AssertJUnit.assertEquals("Strength incorrect for 'ab3!5'", 21, PasswordStrengthCheck.check("ab3!5"));
		AssertJUnit.assertEquals("Strength incorrect for '1b!3d5!'", 31, PasswordStrengthCheck.check("1b!3d5!"));
		AssertJUnit.assertEquals("Strength incorrect for '12c4e!F!'", 44, PasswordStrengthCheck.check("12c4e!F!"));

	}
	
}
