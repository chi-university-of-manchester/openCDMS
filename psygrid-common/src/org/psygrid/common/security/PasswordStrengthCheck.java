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

import java.util.regex.Pattern;

/**
 * Password strength meter. Based on Javascript implementation
 * by Steve Moitozo <god at zilla dot us>, available from
 * http://www.geekwisdom.com/dyn/passwdmeter
 * 
 * @author Rob Harper
 *
 */
public class PasswordStrengthCheck {

	private static final String LOWER_CASE_PATTERN = ".*[a-z].*";
	private static final String UPPER_CASE_PATTERN = ".*[A-Z].*";
	private static final String ONE_NUMBER_PATTERN = ".*[0-9].*";
	private static final String THREE_NUMBER_PATTERN = ".*[0-9].*[0-9].*[0-9].*";
	private static final String ONE_SPECIAL_CHAR_PATTERN = ".*[!@#$%^&*?_~].*";
	private static final String TWO_SPECIAL_CHAR_PATTERN = ".*[!@#$%^&*?_~].*[!@#$%^&*?_~].*";
	
	/**
	 * Check the strength of a password. The higher the return value, 
	 * the stronger the password.
	 * 
	 * @param password The password to check
	 * @return Int, the strength of the password
	 */
	public static int check(String password){
		/*
		Password Strength Factors and Weightings

		password length:
		level 0 (3 point): less than 5 characters
		level 1 (6 points): between 5 and 7 characters
		level 2 (12 points): between 8 and 15 characters
		level 3 (18 points): 16 or more characters

		letters:
		level 0 (0 points): no letters
		level 1 (1 point): lower case exists
		level 2 (5 points): upper case exists

		numbers:
		level 0 (0 points): no numbers exist
		level 1 (5 points): one number exists
		level 1 (10 points): 3 or more numbers exist

		special characters:
		level 0 (0 points): no special characters
		level 1 (5 points): one special character exists
		level 2 (10 points): more than one special character exists

		combinatons:
		level 1 (2 points): mixed case letters
		level 1 (2 points): letters and numbers exist
		level 1 (2 points): letters, numbers and special 
							characters exist
		 */
		
		int score = 0;
		
		//1. Test password length
		if ( password.length() < 5 ){
			score += 3;
		}
		else if ( password.length() < 8 ){
			score += 6;
		}
		else if ( password.length() < 16 ){
			score += 12;
		}
		else{
			score += 18;
		}
		
		//2. Test contains letters
        boolean containsLower = testRegex(LOWER_CASE_PATTERN, password);
        boolean containsUpper = testRegex(UPPER_CASE_PATTERN, password);
        if ( containsLower ){
        	score += 1;
        }
        if ( containsUpper ){
        	score += 5;
        }
        
        //3. Test contains numbers
        boolean containsOneNumber = testRegex(ONE_NUMBER_PATTERN, password);
        boolean containsThreeNumbers = testRegex(THREE_NUMBER_PATTERN, password);
        if ( containsThreeNumbers ){
        	score += 10;
        }
        else if (containsOneNumber){
        	score += 5;
        }
        
        //4. Special characters exist
        boolean containsOneSpecialChar = testRegex(ONE_SPECIAL_CHAR_PATTERN, password);
        boolean containsTwoSpecialChars = testRegex(TWO_SPECIAL_CHAR_PATTERN, password);
        if ( containsTwoSpecialChars ){
        	score += 10;
        }
        else if (containsOneSpecialChar){
        	score += 5;
        }
        
        //5. Combinations
        //5.1 Both upper and lower case letters
        if ( containsLower && containsUpper ){
        	score += 2;
        }
        //5.2 Letters and number
        if ( (containsLower || containsUpper) && containsOneNumber ){
        	score += 2;
        }
        //5.2 Letters and number and special char
        if ( (containsLower || containsUpper) && containsOneNumber && containsOneSpecialChar ){
        	score += 2;
        }
        
        return score;
	}
	
	public static String textualResult(int strength){
		if ( strength < 16 ){
			return "very weak";
		}
		else if ( strength < 25 ){
			return "weak";
		}
		else if ( strength < 35 ){
			return "mediocre";
		}
		else if ( strength < 45 ){
			return "strong";
		}
		else{
			return "very strong";
		}
	}
	
	private static boolean testRegex(String pattern, String testString){
		return Pattern.compile(pattern).matcher(testString).matches();
	}
	
}
