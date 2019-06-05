package org.psygrid.data.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Dumps databases as files containing insert statements.
 * 
 * The heap space may need to be increased when running against large databases.
 * 
 * This code is based on Squirrel SQL plugin:
 * 
 * net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.CreateDataScriptCommand
 * 
 */
public class ExportDatabases {


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PrintStream log = null;
		
		try{
	        ExportDatabases cds = new ExportDatabases();

	        String[] databases = new String[]{"aa_db","esl","pa_db","postcode","psygrid","random"};
//	        String[] databases = new String[]{"aa_db","del","esl","pa_db","postcode","psygrid","random"};

	        String dbSchema = "DB2INST1";
	        String userName = "db2inst1";
	        String driverName = "com.ibm.db2.jcc.DB2Driver";
	        String baseURL = "jdbc:db2://localhost:50000/";
	        String outDir = "db2";

//	        String dbSchema = null;
//	        String userName = "psygriduser";
//	        String driverName = "com.mysql.jdbc.Driver";
//	        String baseURL = "jdbc:mysql://localhost/";
//	        String outDir = "mysql";
	        
	        // For mysql
	        String prefix="/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;\n"+
	        "/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;\n"+
	        "/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;\n"+
	        "/*!40101 SET NAMES utf8 */;\n"+
	        "/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;\n"+
	        "/*!40103 SET TIME_ZONE='+00:00' */;\n"+
	        "/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;\n"+
	        "/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;\n"+
	        "/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;\n"+
	        "/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;\n";


	        String postfix="/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;\n"+
	        "/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;\n"+
	        "/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;\n"+
	        "/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;\n"+
	        "/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;\n"+
	        "/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;\n"+
	        "/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;\n"+
	        "/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;\n";


	        System.out.print("Enter database password:");
	        Scanner in = new Scanner(System.in);
	        String password = in.nextLine();
	        
	        log = new PrintStream(new FileOutputStream(outDir+File.separator+"logfile.txt"));
	        for(String database:databases){
	        	System.out.println("Processing database: "+database);
		        cds.export(database,dbSchema,userName,password,driverName,baseURL+database,prefix,postfix,outDir,log);	        	
	        }
	        log.println("Done!");
	        System.out.println("Done!");
		}
		catch(Exception ex){
			ex.printStackTrace();
			if(log!=null) ex.printStackTrace(log);
		}
	}
   
 
	public void export(String dbName,String dbSchema,String userName,String password,
			String driverName, String connectionURL,String prefix,String postfix,String outDir,PrintStream log) throws Exception {

      Connection conn = null;

      Class.forName (driverName).newInstance ();
      conn = DriverManager.getConnection (connectionURL, userName, password);
      log.println ("Database connection established:"+connectionURL+"\n");

      DatabaseMetaData meta = conn.getMetaData();

      List<String> tableNames = new ArrayList<String>();
      ResultSet res = meta.getTables(null, null, null, new String[] {"TABLE"});
      while (res.next()) {
         if(dbSchema == null || res.getString("TABLE_SCHEM").equals(dbSchema)){
        	 tableNames.add(res.getString("TABLE_NAME").toLowerCase());
         }
      }
      res.close();
      
      Collections.sort(tableNames);

      String outputFileName = outDir+File.separator+dbName+".sql";
      
      File outputFile = new File(outputFileName);
      
      if(outputFile.exists()) throw new IOException("The file '"+outputFile+"' already exists");
      
      FileWriter out = new FileWriter(outputFile);
      out.write(prefix+"\n");
      for(String table :tableNames){
    	  if(!table.toLowerCase().startsWith("t_")){
          	  System.out.println("\tSkipping table: "+table+" ");
          	  log.println("\tSkipping: "+table);
          	  continue;
    	  }
      	  System.out.print("\tProcessing table: "+table+" ");
      	  log.println("\tProcessing table: "+table);
	      Statement stmt = conn.createStatement();
	      String query = getQuery(meta,table,dbSchema);
	      log.println("\t"+query);
	      out.write("\n-- Table: "+table.toLowerCase()+"\n\n");
	      out.write("/*!40000 ALTER TABLE `"+table.toLowerCase()+"` DISABLE KEYS */;\n");
	      ResultSet srcResult = stmt.executeQuery(query);
	      genInserts(srcResult, table, out,log);
	      srcResult.close();
	      stmt.close();
	      out.write("/*!40000 ALTER TABLE `"+table.toLowerCase()+"` ENABLE KEYS */;\n");
	      log.println();
	      System.out.println();
      }
      out.write(postfix+"\n");
      out.close();
      conn.close();
   }

		/**
		 * Returns an sql query sting for extracting all the data from the given table.
		 * 
		 * NB - The query orders the table columns in alphabetical order - to make it easier to compare exports from
		 *      two different databases.
		 *      Different databases returns columns names in lower/upper case so the code sorts by upper case
		 *      because underscores can change the order e.g.
		 *      
		 *      The following strings are in correct case-sensitive order:
		 *      
		 *      C_GROUPPROJECT_ID
		 *      C_GROUP_NAME
		 *      
		 *      and so are these:
		 *      
		 *      c_group_name
		 *      c_groupproject_id
		 *      
		 */
	   private String getQuery(DatabaseMetaData meta, String table, String dbSchema) throws SQLException{
		   
		   
		    List<String> columnNames = new ArrayList<String>();
		   	ResultSet rsColumns = meta.getColumns(null, dbSchema,table.toUpperCase(), null);
		    while (rsColumns.next()) {
			      //int columnIndex = rsColumns.getInt("ORDINAL_POSITION");
			      String columnName = rsColumns.getString("COLUMN_NAME");
			      columnNames.add(columnName.toUpperCase());
		    }
		    rsColumns.close();	
		    Collections.sort(columnNames);
		    
		   	SortedMap<Integer,String> keyColumnMap=new TreeMap<Integer, String>();
			ResultSet rsPrimaryKeys = meta.getPrimaryKeys(null, dbSchema, table.toUpperCase());
			while(rsPrimaryKeys.next()){
				int keySeq = rsPrimaryKeys.getShort("KEY_SEQ"); 
				String columnName = rsPrimaryKeys.getString("COLUMN_NAME");  
				keyColumnMap.put(keySeq, columnName);
			}
			rsPrimaryKeys.close();	    
						
		    StringBuffer cols = new StringBuffer();
		    for(String columnName:columnNames){
		    	cols.append(columnName.toLowerCase());
		    	cols.append(",");
		    }
		    String query = "select "+cols.substring(0,cols.lastIndexOf(","))+" from "+(dbSchema!=null?dbSchema+".":"")+table;

		    StringBuffer keycols = new StringBuffer();
		    for(String keyColumnName:keyColumnMap.values()){
		    	keycols.append(keyColumnName);
		    	keycols.append(",");
		    }
			if(keycols.length()>0){
		    	query+=" order by "+keycols.substring(0,keycols.lastIndexOf(","));
		    }
		    return query;
	   }		
   
   protected void genInserts(ResultSet srcResult, String sTable,Writer out,PrintStream log)
      throws SQLException, IOException
   {
      ResultSetMetaData metaData = srcResult.getMetaData();

      int iColumnCount = metaData.getColumnCount();
      ColumnInfo[] colInfo = new ColumnInfo[iColumnCount];


      String insert = "insert into "+sTable.toLowerCase()+" (";

      for (int i = 1; i <= iColumnCount; i++)
      {
         colInfo[i-1] = new ColumnInfo(metaData.getColumnName(i).toLowerCase(), metaData.getColumnType(i));
         int iIndexPoint = colInfo[i-1].columnName.lastIndexOf('.');
         insert+=colInfo[i-1].columnName.substring(iIndexPoint + 1);
         insert+=",";
      }

      insert=insert.substring(0,insert.lastIndexOf(','))+") values ";
                  
      int rowCount = 0;
                        
      while (srcResult.next())
      {    	 
    	 if(rowCount%100==0){
    		 if(rowCount>0) out.write(";");
    		 out.write("\n"+insert);
    	 }
    	     	     	     	 
         StringBuffer sbValues = new StringBuffer();
         if(rowCount%100>0) sbValues.append(",\n");
         sbValues.append("  (");
         
    	 rowCount++; 


         for (int i = 0; i < iColumnCount; i++)
         {

            if (Types.TINYINT == colInfo[i].sqlType
               || Types.BIGINT == colInfo[i].sqlType
               || Types.SMALLINT == colInfo[i].sqlType
               || Types.INTEGER == colInfo[i].sqlType
               || Types.FLOAT == colInfo[i].sqlType
               || Types.REAL == colInfo[i].sqlType
               || Types.DOUBLE == colInfo[i].sqlType
               || Types.NUMERIC == colInfo[i].sqlType
               || Types.DECIMAL == colInfo[i].sqlType)
            {
               Object value = srcResult.getObject(i + 1);
               sbValues.append(value);
            }
            else if (Types.DATE == colInfo[i].sqlType
               || Types.TIME == colInfo[i].sqlType
               || Types.TIMESTAMP == colInfo[i].sqlType)
            {
               Calendar calendar = Calendar.getInstance();
               java.util.Date timestamp = null;
               if (Types.DATE == colInfo[i].sqlType)
               {
                  timestamp = srcResult.getDate(i + 1);
               }
               else if (Types.TIME == colInfo[i].sqlType)
               {
                  timestamp = srcResult.getTime(i + 1);
               }
               else if (Types.TIMESTAMP == colInfo[i].sqlType)
               {
                  timestamp = srcResult.getTimestamp(i + 1);
               }

               if (timestamp == null)
               {
                  sbValues.append("null");
               }
               else
               {
                  calendar.setTime(timestamp);

                  if (Types.DATE == colInfo[i].sqlType)
                  {
                     String esc = "{d '" + prefixNulls(calendar.get(Calendar.YEAR), 4) + "-" +
                        prefixNulls(calendar.get(Calendar.MONTH) + 1, 2) + "-" +
                        prefixNulls(calendar.get(Calendar.DAY_OF_MONTH), 2) + "'}";
                     sbValues.append(esc);
                  }
                  else if (Types.TIME == colInfo[i].sqlType)
                  {
                     String esc = "{t '" + prefixNulls(calendar.get(Calendar.HOUR_OF_DAY), 2) + ":" +
                        prefixNulls(calendar.get(Calendar.MINUTE), 2) + ":" +
                        prefixNulls(calendar.get(Calendar.SECOND), 2) + "'}";
                     sbValues.append(esc);
                  }
                  else if (Types.TIMESTAMP == colInfo[i].sqlType)
                  {
                  	Timestamp ts = (Timestamp)timestamp;
                  	
                     StringBuilder esc = new StringBuilder("{ts '");
                     esc.append(prefixNulls(calendar.get(Calendar.YEAR), 4)).append("-");
                     esc.append(prefixNulls(calendar.get(Calendar.MONTH) + 1, 2)).append("-");
                     esc.append(prefixNulls(calendar.get(Calendar.DAY_OF_MONTH), 2)).append(" ");
                     esc.append(prefixNulls(calendar.get(Calendar.HOUR_OF_DAY), 2)).append(":");
                     esc.append(prefixNulls(calendar.get(Calendar.MINUTE), 2)).append(":");
                     esc.append(prefixNulls(calendar.get(Calendar.SECOND), 2)).append(".");
                     esc.append(getNanos(ts));
                     esc.append("'}");
                     sbValues.append(esc);
                  }

               }
            }
            else if (Types.BIT == colInfo[i].sqlType
                     || Types.BOOLEAN == colInfo[i].sqlType)
            {
               boolean iBoolean = srcResult.getBoolean(i + 1);

               if(srcResult.wasNull())
               {
                  sbValues.append("null");
               }
               else if (iBoolean)
               {
                       sbValues.append(1);
               }
               else
               {
                       sbValues.append(0);
               }
            }
            else // Types.CHAR,
                 // Types.VARCHAR,
                 // Types.LONGVARCHAR,
                 // Types.BINARY,
                 // Types.VARBINARY
                 // Types.LONGVARBINARY
                 // Types.NULL
                 // Types.JAVA_OBJECT
                 // Types.DISTINCT
                 // Types.ARRAY
                 // Types.BLOB
                 // Types.CLOB
                 // Types.REF
                 // Types.DATALINK
            {
               String sResult = srcResult.getString(i + 1);
               if (sResult == null)
               {
                  sbValues.append("null");
               }
               else
               {
            	  sResult = escape(sResult);
                  sbValues.append("\'");
                  sbValues.append(sResult);
                  sbValues.append("\'");
               }
            }
            sbValues.append(",");
         }
         // delete last ','
         sbValues.setLength(sbValues.length() - 1);

         // close it.
         sbValues.append(")");
         out.write(sbValues.toString());

      }
      
      out.write(";\n\n");
      srcResult.close();
 	  System.out.print(" "+rowCount+" rows");
 	  log.println("\t"+rowCount+" rows");
   }

   /* 
    * Convert \n,\t,\',\\ characters into their escape sequences 
    */
	public static String escape(String str) {
	    StringBuilder sb = new StringBuilder();
	    for (int i=0; i<str.length(); i++){
	        char c = str.charAt(i);
	        if (c == '\n'){
	            sb.append("\\n");
	        }
	        else if (c == '\t'){
	            sb.append("\\t");
	        }
	        else if (c == '\''){
	            sb.append("\\\'");
	        }
	        else if (c == '\\'){
	            sb.append("\\\\");
	        }
	        else{               
	            sb.append(c);
	        }
	    }
	    return sb.toString();
	}

	/**
	 * Returns the sub-second precision value from the specified timestamp if supported by the session's
	 * dialect.
	 * 
	 * @param ts
	 *           the Timestamp to get the nanosecond value from
	 * @return a string representing the nanosecond value.
	 */
	private String getNanos(Timestamp ts) throws SQLException
	{		
//		String result = "" + ts.getNanos();
		// Mysql DATETIME does not support sub-second accuracy  
		String result = "0";
		return result;
	}
      
   private String prefixNulls(int toPrefix, int digitCount)
   {
      String ret = "" + toPrefix;

      while (ret.length() < digitCount)
      {
         ret = 0 + ret;
      }

      return ret;
   }


   private String getStatementSeparator()
   {
      String statementSeparator = ";";
      return statementSeparator;
   }



   private static class ColumnInfo
   {
      int sqlType; // As in java.sql.Types
      String columnName;

      public ColumnInfo(String columnName, int sqlType)
      {
         this.columnName = columnName;
         this.sqlType = sqlType;
      }
   }


}
