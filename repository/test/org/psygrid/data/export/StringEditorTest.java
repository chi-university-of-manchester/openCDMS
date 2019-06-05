package org.psygrid.data.export;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import org.psygrid.data.export.StringEditor;
import org.psygrid.data.export.StringEditor.SpecialCharacter;

public class StringEditorTest {
	
	@Test
	public void lineFeedTest() {
		
		String input = "My name is Lucca.\nI live upstairs from you.\nDon't bother me.";		
		String actualOutput = StringEditor.doReplacement(SpecialCharacter.lineBreak, ' ', input);
		String expectedOutput = "My name is Lucca. I live upstairs from you. Don't bother me.";		
		
		assertEquals(actualOutput,expectedOutput,"Linefeeds not replaced");
	}
	
	@Test
	public void carriageReturnTest() {
		
		String input = "My name is Lucca.\rI live upstairs from you.\rDon't bother me.";
		String actualOutput = StringEditor.doReplacement(SpecialCharacter.carriageReturn, ' ', input);
		String expectedOutput = "My name is Lucca. I live upstairs from you. Don't bother me.";		
		
		assertEquals(actualOutput,expectedOutput,"Carriage returns not replaced");
	}
	
	@Test
	public void crlfTest() {
		
		String input = "My name is Lucca.\r\nI live upstairs from you.\r\nDon't bother me.";
		String actualOutput = StringEditor.doReplacement(SpecialCharacter.crlf, ' ', input);
		String expectedOutput = "My name is Lucca. I live upstairs from you. Don't bother me.";		
		
		assertEquals(actualOutput,expectedOutput,"CRLFs not replaced");
	}
	
	@Test
	public void allTest() {
		
		String input = "This\nis the last\rgreat\r\nadventure\nleft\rto\r\nmankind.";
		String actualOutput = StringEditor.doReplacement(SpecialCharacter.all, ' ', input);
		String expectedOutput = "This is the last great adventure left to mankind.";		
		
		assertEquals(actualOutput,expectedOutput,"All strings not replaced");
	}
	

}
