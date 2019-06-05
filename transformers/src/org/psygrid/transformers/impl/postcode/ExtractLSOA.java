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

package org.psygrid.transformers.impl.postcode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.psygrid.transformers.TransformerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;

/*
 * Takes an input csv file containing recordsID,postcode pairs and 
 * writes an output file containing recordID,postcode,LSOA pairs.
 */
public class ExtractLSOA {

    protected ApplicationContext ctx = null;
    
    private PostCodeDAO dao = null;
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        
        //check arguments
        if (args.length != 2){
            System.out.println("Usage: ExtractLSOA inFile outFile");
            System.exit(-1);
        }

        ExtractLSOA loader = new ExtractLSOA();
        
        loader.run(args[0],args[1]);
        
    }

    public ExtractLSOA(){
        String[] paths = {"applicationCtx.xml"};
        ctx = new ClassPathXmlApplicationContext(paths);
        dao = (PostCodeDAO)ctx.getBean("postcodeDAOService");
    }
    
    private void run(String inFile,String outFile){
    	
        try {
            BufferedReader in = new BufferedReader(new FileReader(inFile));
		    BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
            String line;
            int counter = 0;
            while ( (line = in.readLine()) != null) {
                
                String[] data = line.split(",",-1);
                
                String LSOA="";

                if(data.length!=2) {
                	System.out.println("nhs="+data[0]);
                	out.close();
                	System.exit(0);
                }

                try {
					LSOA = dao.getLowerSoaForPostcode(data[1]);
				} catch (TransformerException e) {
					// Do nothing - the above function should just return null id there is no match.
					// And then maybe have another function to check the postcode validity.
				}

                out.write(data[0]+","+data[1]+","+LSOA+"\n");
                
                counter++;
                if ( 0 == counter % 1000){
                    System.out.println(counter+" postcodes processed");
                }
            }
            in.close();
            out.close();
                        
            System.out.println("Complete: converted "+counter+" postcodes");
            
        } 
        catch (IOException e) {
            e.printStackTrace();
        }        
    }
        
}
