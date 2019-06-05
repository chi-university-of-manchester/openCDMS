/*
Copyright (c) 2005, The University of Manchester, UK.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301, USA.
*/

//Created on Jun 27, 2008 by John Ainsworth



package org.psygrid.security.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.logging.Log;
import org.psygrid.security.LDAPPasswordHashScheme;

/**
 * @author jda
 *
 */
public class PasswordUtilities {
	
	public static char[] hashPassword(char[] input, LDAPPasswordHashScheme lphs, Log log){
        String hashed = null;
		byte[] inb = new byte[input.length];
		for (int i = 0; i < inb.length; i++) {
			inb[i] = (byte) input[i];
		}
        if ( null != input ){
            try{
                MessageDigest md = MessageDigest.getInstance( lphs.name() );
                byte[] result = md.digest(inb);
                hashed =  Base64.encode(result);
            }
            catch(NoSuchAlgorithmException ex){
                log.error("encrypt: "+ex.getClass().getSimpleName(),ex);
            }
        }
        return hashed.toCharArray();		
	}
	
	public static byte[] UTFPassword(String in){
		byte[] unicodeBytes = null;
		String str = doubleQuoteString(in);
		try {
			unicodeBytes = str.getBytes("UTF-16LE");
		} catch (Exception e) {
			// This should never happen.
			e.printStackTrace();
		}
		return unicodeBytes;
	}
	private static String doubleQuoteString(String str) {
		StringBuffer sb = new StringBuffer();
		sb.append("\"");
		sb.append(str);
		sb.append("\"");
		return sb.toString();
	}
}
