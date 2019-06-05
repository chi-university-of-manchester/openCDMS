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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import sqlj.runtime.ref.DefaultContext;

/**
 * Load in a new postcode database the slow way using plain SQL statements
 * instead of hibernate. 
 * 
 * @author Lucy Bridges
 *
 */
public class LoadDatabase2 {

	protected ApplicationContext ctx = null;

	private PostCodeDAO dao = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// I don't know why this version of the postcode import was written - the hibernate one works given enough RAM.
		// This version has not been updated to match the column indexes of the postcode data csv files.
		System.out.println("Please use LoadDatabase.java");
		System.exit(0);

		/*
		//check arguments
		if ( 1 != args.length ){
			System.out.println("Usage: LoadDatabase2 inFile");
		}

		LoadDatabase2 loader = new LoadDatabase2();

		loader.runLoad(args[0]);
		*/
	}

	public LoadDatabase2(){
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

				String pcValue = stripSpeechMarks(data[0]);
				PostCode postCode = new PostCode();
				postCode.setValue(pcValue);

				String oaCode = stripSpeechMarks(data[37]);
				if ( null != oaCode && !oaCode.equals("\"\"") && !oaCode.equals("") && !oaCode.equals("          ")){
					//The postcode has a valid output-area
					OutputArea outputArea = outputAreas.get(oaCode);
					if ( null == outputArea ){
						//this is a new output area not previously loaded into the map
						outputArea = new OutputArea();
						outputArea.setCode(oaCode);
						outputAreas.put(oaCode, outputArea);

						//look for the lower-layer SOA
						String lowerCode = stripSpeechMarks(data[41]);
						if ( lowerCode.equals("Z99999999") ){
							//check for a scottish data zone instead
							String dataZone = stripSpeechMarks(data[42]);
							if ( null != dataZone && !dataZone.equals("\"\"") && !dataZone.equals("") && !dataZone.equals("         ")){
								lowerCode = dataZone;
							}
						}
						if ( lowerCode.equals("Z99999999") && numItems >= 49 ){
							//check for a NI super output area
							String niSoa = stripSpeechMarks(data[48]);
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
								String middleCode = stripSpeechMarks(data[43]);
								if ( middleCode.equals("Z99999999") && numItems >= 48 ){
									//check for a Scottish Intermediate zone
									String interZone = stripSpeechMarks(data[47]);
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

			long newId = 1; 
			/*
			 * Insert Middle SOA
			 */
			System.out.println("Inserting middle SOAs");
			int savedMiddle = 0;
			try {
				String url = "jdbc:db2:postcode";     // URL is jdbc:db2:dbname
				Connection connection = null;          

				// Set the connection with default id/password
				connection = DriverManager.getConnection(url, "db2inst1", "Sq1r431");  

				// Set the default context
				DefaultContext ctx = new DefaultContext(connection);            
				DefaultContext.setDefaultContext(ctx);

				String sql = "INSERT into t_persistents (c_version) VALUES (?)";
				PreparedStatement stmt = connection.prepareStatement(sql);
				try {
					String middleSQL = "INSERT INTO t_middle_soas (c_id, c_code) VALUES (?,?)";
					PreparedStatement pstmt = connection.prepareStatement(middleSQL);
					for ( String entry: middleSOAs.keySet()){

						MiddleSOA soa = middleSOAs.get(entry);
						String code = soa.getCode();

						if (code != null && !code.equals("")) {
							stmt.setInt(1, 0);
							stmt.executeUpdate();
							
							pstmt.setLong(1, newId);
							pstmt.setString(2, code);			    
							// Insert the row
							pstmt.executeUpdate();
							savedMiddle ++;
							newId++;
						}
					}
				}
				catch (SQLException e) {
					System.err.println("Unable to insert Middle SOA");
					e.printStackTrace();
				}
				System.out.println("Saved "+savedMiddle+" middle SOAs");

				/*
				 * Insert Lower SOAs
				 */
				System.out.println("Inserting lower SOAs");
				int savedLower = 0;
				try {
					String lowerSQL = "INSERT INTO t_lower_soas (c_code, c_middle_soa, c_id) VALUES (?,?,?)";
					String middleSQL = "SELECT c_id FROM t_middle_soas where c_code like ?";
					PreparedStatement middleStmt = connection.prepareStatement(middleSQL);
					PreparedStatement lowerStmt = connection.prepareStatement(lowerSQL);

					for ( String entry: middleSOAs.keySet()){

						MiddleSOA soa = middleSOAs.get(entry);
						String code = soa.getCode();

						if (code != null && !code.equals("")) {
							middleStmt.setString(1, code);			    
							ResultSet set = middleStmt.executeQuery();
							//Move the result set to the first row
							System.err.println("Found results for "+code+" : "+set.next());
							Long id = set.getLong("c_id");
							if (id != null) {

								for (LowerSOA lower: soa.getLowerSoas()) {
									if (lower.getCode() != null && !lower.getCode().equals("")) {
										stmt.setInt(1, 0);
										stmt.executeUpdate();
										
										
										lowerStmt.setString(1, lower.getCode());
										lowerStmt.setLong(2, id);
										lowerStmt.setLong(3, newId);
										lowerStmt.executeUpdate();
										//TODO could do batch update
										savedLower++;
										newId++;
									}
								}
							}
						}
					}
				}
				catch (SQLException e) {
					System.err.println("Unable to insert lower SOA");
					e.printStackTrace();
				}
				System.out.println("Saved "+savedLower+" lower SOAs");

				/*
				 * Output areas
				 */
				System.out.println("Inserting output areas");
				int savedOutput = 0;
				
				Set<String> seenBefore = new HashSet<String>();
				try {
					String lowerSQL = "SELECT c_id FROM t_lower_soas where c_code like ?";
					String outputSQL = "INSERT INTO t_output_areas (c_code, c_lower_soa, c_id) VALUES (?,?,?)";
					PreparedStatement lowerStmt = connection.prepareStatement(lowerSQL);
					PreparedStatement outputStmt = connection.prepareStatement(outputSQL);
					for ( String entry: middleSOAs.keySet()){
						MiddleSOA soa = middleSOAs.get(entry);
						for (LowerSOA lower: soa.getLowerSoas()) {
							if (lower.getCode() != null && !lower.getClass().equals("")) {
								lowerStmt.setString(1, lower.getCode());
								ResultSet set = lowerStmt.executeQuery();
								set.next();
								Long id = set.getLong("c_id");

								for (OutputArea area: lower.getOutputAreas()) {
									String areaCode = area.getCode();
									if (areaCode != null && !areaCode.equals("")
											&& !seenBefore.contains(areaCode)) {
										seenBefore.add(areaCode);	//Area codes are not unique?!
										stmt.setInt(1, 0);
										stmt.executeUpdate();
										
										
										outputStmt.setString(1, areaCode);
										outputStmt.setLong(2, id);
										outputStmt.setLong(3, newId);
										outputStmt.executeUpdate();
										//TODO could do batch update
										savedOutput++;
										newId++;
									}
								}
							}
						}
					}
				}
				catch (SQLException e) {
					System.err.println("Unable to insert output area");
					e.printStackTrace();
				}
				System.out.println("Saved "+savedOutput+" output areas");


				/*
				 * Insert Postcodes
				 */
				try {
					System.out.println("Inserting postcodes");
					int savedPostcodes = 0;
					String outputSQL = "SELECT c_id FROM t_output_areas where c_code like ?";
					String postcodeSQL = "INSERT INTO t_postcodes (c_value, c_output_area, c_id) VALUES (?,?,?)";
					PreparedStatement postcodeStmt = connection.prepareStatement(postcodeSQL);
					PreparedStatement outputStmt = connection.prepareStatement(outputSQL);
					for ( String entry: middleSOAs.keySet()){
						MiddleSOA soa = middleSOAs.get(entry);
						for (LowerSOA lower: soa.getLowerSoas()) {
							for (OutputArea area: lower.getOutputAreas()) {
								String areaCode = area.getCode();
								if (areaCode != null && !areaCode.equals("")) {
									outputStmt.setString(1, areaCode);
									ResultSet set = outputStmt.executeQuery();
									set.next();
									Long id = set.getLong("c_id");

									for (PostCode postcode: area.getPostCodes()) {
										if (postcode.getValue() != null && !postcode.getValue().equals("")) {
											stmt.setInt(1, 0);
											stmt.executeUpdate();
											
											postcodeStmt.setString(1, postcode.getValue());
											postcodeStmt.setLong(2, id);
											postcodeStmt.setLong(3, newId);
											postcodeStmt.executeUpdate();
											savedPostcodes++;
											newId++;
										}
									}
								}
							}
						}
					}
					System.out.println("Saved "+savedPostcodes+" postcodes");
				}
				catch (SQLException e) {
					System.err.println("Unable to insert postcode");
					e.printStackTrace();
				}
				

			}
			catch (SQLException e) {
				System.err.println("Unable to create connection");
				e.printStackTrace();
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}    
	}

	private String stripSpeechMarks(String in){
		return in.replaceAll("\"","");
	}

}
