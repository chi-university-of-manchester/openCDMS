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


package org.psygrid.collection.entry.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.psygrid.collection.entry.security.SecurityHelper;

/**
 * Utility class to encrypt a decrypted file.
 * <p>
 * Usage: EncryptFile &lt;input file&gt; &lt;output file&gt; &lt;password&gt;
 * 
 * @author Rob Harper
 *
 */
public class EncryptFile {

    /**
     * @param args
     */
    public static void main(String[] args) {

        try{
        
            if ( args.length != 3 ){
                System.out.println("Usage: DecryptFile <input file> <output file> <password>");
            }
            
            //load encrypted file
            String plainText = loadToString(args[0]);
            
            //decrypt file
            String cipherText = SecurityHelper.encrypt(plainText, args[2].toCharArray());
            
            //write plaintext to file
            saveString(cipherText, args[1]);
            
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private static String loadToString(String file) throws FileNotFoundException, IOException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder(2500);
            String s = null;
            while ((s = in.readLine()) != null) {
                builder.append(s);
                builder.append(System.getProperty("line.separator"));
            }
            return builder.toString();
        }
        catch (IOException ioe) {
            throw ioe;
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private static void saveString(String string, String file) throws IOException  {
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        try {
            out.write(string);
            out.flush();
        } catch (IOException ioe) {
            throw ioe;
        }
        finally {
            out.close();
        }
    }

}
