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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;

/**
 * 
 * Import routine for populating the postcode database with data from
 * the Office for National Statistics Postcode Directory CSV file.
 * 
 * The data can be downloaded from http://census.ac.uk/ 
 * 
 * See file 'NSPD User Guide 2010 v3.pdf' - included in the csv data download.
 * 
 * You need to register for access - login is federated via your university account.
 * 
 * NB - the column order of the downloaded data file may change between releases.
 *      make sure that the constants below match the columns of the file you are using
 *      
 * You will probably need to give this code ~1Gib RAM using -mx1024m.
 *      
 */
public class LoadDatabase {
	

	/**
	 * Constants for the zero-based column positions of the data in 
	 * the Office for National Statistics Postcode Directory CSV file.
	 * 
	 * These must match the columns in the csv file being imported.
	 * 
	 * The last update matches the source file:
	 * 
	 * NSPDF_AUG_2010_UK_1M_FP.csv
	 * 
	 * Note - the constant names match the 'Field name' column in Annex B of the 'NSPD User Guide 2010 v3.pdf'
	 */
	static final int PCD = 0;
	static final int OACODE = 39;
	static final int SOA1 = 43;
	static final int DZONE1 = 44;
	static final int SOA2 = 45;
	static final int DZONE2 = 49;
	static final int SOA1NI = 50;
	
	/**
	 * A pseudo code used to indicate that a field is defined in another column.
	 */
	static final String PSEUDOCODE = "Z99999999";

    protected ApplicationContext ctx = null;
    
    private PostCodeDAO dao = null;
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        
        //check arguments
        if ( 1 != args.length ){
            System.out.println("Usage: LoadDatabase inFile");
            System.exit(-1);
        }
        
        System.out.println("Please confirm the you have updated the constants in this file to match the source data (type 'yes' to confirm):");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String str = in.readLine();
        if ( str.equals("yes") ){
            System.out.println("Loading postcode data...");
        }
        else{
            System.out.println("Aborting.");
            return;
        }


        LoadDatabase loader = new LoadDatabase();
        
        loader.runLoad(args[0]);
        
    }

    public LoadDatabase(){
        String[] paths = {"applicationCtx.xml"};
        ctx = new ClassPathXmlApplicationContext(paths);
        dao = (PostCodeDAO)ctx.getBean("postcodeDAOService");
    }
    
    private void runLoad(String inFile){

        Map<String, MiddleSOA> middleSOAs = new HashMap<String, MiddleSOA>();
        Map<String, LowerSOA> lowerSOAs = new HashMap<String, LowerSOA>();
        Map<String, OutputArea> outputAreas = new HashMap<String, OutputArea>();

        try {
            BufferedReader in = new BufferedReader(new FileReader(inFile));
            String line;
            int counter = 0;
            while ( (line = in.readLine()) != null) {
                
                String[] data = line.split(",");
                int numItems = data.length;
                
                String pcValue = stripSpeechMarks(data[PCD]);
                PostCode postCode = new PostCode();
                postCode.setValue(pcValue);
                
                String oaCode = stripSpeechMarks(data[OACODE]);
                if ( null != oaCode && !oaCode.equals("\"\"") && !oaCode.equals("") && !oaCode.equals("          ")){
                    //The postcode has a valid output-area
                    OutputArea outputArea = outputAreas.get(oaCode);
                    if ( null == outputArea ){
                        //this is a new output area not previously loaded into the map
                        outputArea = new OutputArea();
                        outputArea.setCode(oaCode);
                        outputAreas.put(oaCode, outputArea);
                        
                        //look for the lower-layer SOA
                        String lowerCode = stripSpeechMarks(data[SOA1]);
                        if ( lowerCode.equals(PSEUDOCODE) ){
                            //check for a scottish data zone instead
                            String dataZone = stripSpeechMarks(data[DZONE1]);
                            if ( null != dataZone && !dataZone.equals("\"\"") && !dataZone.equals("") && !dataZone.equals("         ")){
                                lowerCode = dataZone;
                            }
                        }
                        if ( lowerCode.equals(PSEUDOCODE)){
                            //check for a NI super output area
                            String niSoa = stripSpeechMarks(data[SOA1NI]);
                            if ( null != niSoa && !niSoa.equals("\"\"") && !niSoa.equals("") && !niSoa.equals("         ")){
                                lowerCode = niSoa;
                            }
                        }
                        
                        if ( null != lowerCode && !lowerCode.equals("\"\"") && !lowerCode.equals("") && !lowerCode.equals("         ")){
                            //We have a valid lower-layer SOA
                            LowerSOA lowerSoa = lowerSOAs.get(lowerCode);
                            if ( null == lowerSoa ){
                                //this is a new lower-layer SOA not previously loaded into the map
                                lowerSoa = new LowerSOA();
                                lowerSoa.setCode(lowerCode);
                                lowerSOAs.put(lowerCode, lowerSoa);
                                
                                //look for the middle-layer SOA
                                String middleCode = stripSpeechMarks(data[SOA2]);
                                if ( middleCode.equals(PSEUDOCODE)){
                                    //check for a Scottish Intermediate zone
                                    String interZone = stripSpeechMarks(data[DZONE2]);
                                    if ( null != interZone && !interZone.equals("\"\"") && !interZone.equals("") && !interZone.equals("         ")){
                                        middleCode = interZone;
                                    }
                                }
                                
                                if ( null != middleCode && !middleCode.equals("\"\"") && !middleCode.equals("") && !middleCode.equals("         ")){
                                    //We have a valid middle-layer SOA/intermediate zone
                                    MiddleSOA middleSoa = middleSOAs.get(middleCode);
                                    if ( null == middleSoa ){
                                        //this is a new middle-layer SOA not previously loaded into the map
                                        middleSoa = new MiddleSOA();
                                        middleSoa.setCode(middleCode);
                                        middleSOAs.put(middleCode, middleSoa);
                                    }
                                    
                                    middleSoa.getLowerSoas().add(lowerSoa);
                                    lowerSoa.setMiddleSoa(middleSoa);
                                    
                                }
                            }
                            
                            lowerSoa.getOutputAreas().add(outputArea);
                            outputArea.setLowerSoa(lowerSoa);
                            
                        }
                    }
                    
                    outputArea.getPostCodes().add(postCode);
                    postCode.setOutputArea(outputArea);
                    
                }
                
                counter++;
                if ( 0 == counter % 1000){
                    System.out.println(counter+" postcodes processed");
                }
            }
            in.close();
            
            //save the middle-layer SOAs (which will save everything 
            //below them by cascade)
            Set<Map.Entry<String, MiddleSOA>> mapSet = middleSOAs.entrySet();
            int numMlSoas = mapSet.size();
            int total = 0;
            int successes = 0;
            int failures = 0;
            for ( Map.Entry<String, MiddleSOA> entry: mapSet){
                try{
                    dao.saveMiddleSoa(entry.getValue());
                    successes++;
                }
                catch(DataAccessException ex){
                    System.out.println("Failed to save Middle-Layer SOA '"+entry.getValue().getCode()+"'");
                    int nlsoas = 0;
                    int noas = 0;
                    int npcs = 0;
                    for ( LowerSOA lsoa: entry.getValue().getLowerSoas() ){
                        nlsoas++;
                        for ( OutputArea oa: lsoa.getOutputAreas() ){
                            noas++;
                            for ( PostCode pc: oa.getPostCodes() ){
                                npcs++;
                            }
                        }
                    }
                    System.out.println("Has "+nlsoas+" Lower-Layer SOAs, "+noas+" Output Areas, "+npcs+" Postcodes");
                    ex.printStackTrace();
                    failures++;
                }
                total++;
                if ( 0 == total % 100){
                    System.out.println(successes+"/"+numMlSoas+" ("+failures+" failures) middle-layer SOAs saved to the database");
                }
            }
            
            System.out.println("Complete: loaded "+counter+" postcodes, "+outputAreas.size()+" output areas, "+lowerSOAs.size()+" lower-layer super output areas and "+middleSOAs.size()+" middle-layer super output areas");
            
        } 
        catch (IOException e) {
            e.printStackTrace();
        }        
    }
    
    private String stripSpeechMarks(String in){
        return in.replaceAll("\"","");
    }
    
}
