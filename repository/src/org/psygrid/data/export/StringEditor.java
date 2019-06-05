/**
 * 
 */
package org.psygrid.data.export;

import java.nio.CharBuffer;

/**
 * @author Bill Vance
 * This class allows specified characters to be replaced within an input string.
 * The replacement character can also be specified.
 *
 */
public class StringEditor {
	
	public enum SpecialCharacter{
		lineBreak,
		carriageReturn,
		crlf,
		all
	}
	
	public static String doReplacement(SpecialCharacter whatToReplace, char replaceWith, String text){
		
		if(text == null){
			return null;
		}
		
		String alteredText = text;
		
		switch(whatToReplace){
		case lineBreak:
			//A line break is '\n'
			alteredText = text.replace('\n', replaceWith);
			break;
		case carriageReturn:
			//A carriage return is '\r'
			alteredText = text.replace('\r', replaceWith);
			break;
		case crlf:
			//A crlf is '\r\n'
			alteredText = text.replace("\r\n", String.valueOf(replaceWith));
			break;
		case all:
			alteredText = text.replace("\r\n", String.valueOf(replaceWith));
			alteredText = alteredText.replace('\r', replaceWith);
			alteredText = alteredText.replace('\n', replaceWith);
			break;
		}
		
		return alteredText;
	}
	
	

}
