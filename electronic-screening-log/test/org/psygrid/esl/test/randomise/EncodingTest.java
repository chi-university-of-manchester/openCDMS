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

package org.psygrid.esl.test.randomise;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

/**
 * @author lucy
 *
 */
public class EncodingTest {
		
		public static void main(String[] argv) {
			System.out.println("Test 1");
			String testString = "ab\u3053\u3093\u306B\u3061\u306F\u4E16cd";
			System.out.println(testString);
			String resultString = filterNonAscii(testString);
			System.out.println(resultString);
			System.out.println("String length is "+resultString.length());
			
			System.out.println("Test 2");
			testString = "Although FB has never fully complied with any of the voice’s harmful commands (either directed towards herself or others) she has made some preparatory acts of compliance / appeasement; for instance she currently carries a blade with her wherever she goes because the voice wants her to cut herself (and FB clarifies that she does not carry the blade for the purposes of protection or self-defence, it is purely so that it can be available if / when she ever feels unable to resist the command to cut herself). Additionally, FB admits to sometimes holding a bottle of bleach for hours at a time whilst thinking about complying with the command to, “Drink the bleach”. Furthermore, when FB was first prescribed Anti-Psychotic medication, she delayed collecting the prescription for several " +
					"" +
					"days because the voice forbade her from going to the chemist to collect it. She was, however, able to go to the chemist a few days later in the company of her care-coordinator and myself to collect her medication.";
			System.out.println(testString);
			resultString = filterNonAscii(testString);
			System.out.println(resultString);
			System.out.println("String length is "+resultString.length());
		}
		
		public static String filterNonAscii(String inString) {
			// Create the encoder and decoder for the character encoding
			Charset charset = Charset.forName("US-ASCII");
			CharsetDecoder decoder = charset.newDecoder();
			CharsetEncoder encoder = charset.newEncoder();
			// This line is the key to removing "unmappable" characters.
			encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
			String result = inString;

			try {
				// Convert a string to bytes in a ByteBuffer
				ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(inString));

				// Convert bytes in a ByteBuffer to a character ByteBuffer and then to a string.
				CharBuffer cbuf = decoder.decode(bbuf);
				result = cbuf.toString();
			} catch (CharacterCodingException cce) {
				String errorMessage = "Exception during character encoding/decoding: " + cce.getMessage();
				System.err.println(errorMessage);
				cce.printStackTrace();
			}
			if (result.length() > 160) {
				result = result.substring(0, 160);
			}
			return result;	
		}
	
}
