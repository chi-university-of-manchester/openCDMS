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
 * Load the Office for National Statistics Postcode Directory CSV file
 * into a single table.
 * 
 * This was written to help in investigating the problem of incorrectly imported postcodes.
 * 
 * 
 * @author Terry Child
 *
 */
public class LoadFlatNSPD {

	/*
	 
	CREATE TABLE nspcdb
	(
   		col0 varchar(32) PRIMARY KEY NOT NULL,
   		col37 varchar(32) NOT NULL,
   		col41 varchar(32) NOT NULL,
   		col43 varchar(32) NOT NULL
	);
	CREATE UNIQUE INDEX PRIMARY ON nspcdb(col0);
	CREATE INDEX col37idx ON nspcdb(col37);
	CREATE INDEX col41idx ON nspcdb(col41);
	CREATE INDEX col43idx ON nspcdb(col43);

	 */

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {


		LoadFlatNSPD loader = new LoadFlatNSPD();

		loader.runLoad("C:\\aaapostcodeprobs\\NSPD-postcodes\\pcluts_2010aug\\NSPDF_AUG_2010_UK_1M_FP.csv");
	}

	public LoadFlatNSPD(){
		
	}

	private void runLoad(String inFile) throws Exception {


		String url = "jdbc:mysql://localhost:3306/postcode";     // URL is jdbc:db2:dbname
		Connection connection = null;          

		// Set the connection with default id/password
		connection = DriverManager.getConnection(url, "root", "terrydev");  
		connection.setAutoCommit(false);

		String sql = "INSERT into nspcdb (col0,col37,col41,col43) VALUES (?,?,?,?)";
		PreparedStatement stmt = connection.prepareStatement(sql);

		BufferedReader in = new BufferedReader(new FileReader(inFile));
		String line;
		int counter = 0;
		while ( (line = in.readLine()) != null) {

			String[] data = line.split(",");

			String col0 = stripSpeechMarks(data[0]);
			String col37 = stripSpeechMarks(data[37]);
			String col41 = stripSpeechMarks(data[41]);
			String col43 = stripSpeechMarks(data[43]);
			
			stmt.setString(1, col0);			    
			stmt.setString(2, col37);			    
			stmt.setString(3, col41);			    
			stmt.setString(4, col43);			    
			stmt.executeUpdate();

			counter++;
			if ( 0 == counter % 10000){
				System.out.println(counter+" postcodes processed");
				connection.commit();
			}
		}
		in.close();
		connection.commit();

	}

	private String stripSpeechMarks(String in){
		return in.replaceAll("\"","");
	}

}
