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

package org.psygrid.dataimport.sources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;

import org.psygrid.data.model.hibernate.Identifier;
import org.psygrid.dataimport.identifier.AbstractParser;
import org.psygrid.dataimport.jaxb.imp.Csvsourcetype;

import au.com.bytecode.opencsv.CSVReader;

public class CsvSource implements ISource {

    private String csvDir;
    
    private Csvsourcetype source;
    
    private CSVReader csvReader;
    
    private final AbstractParser idParser;
    
    private boolean isQueued = true;
    
    String[] currentRow;
    
    public CsvSource(Csvsourcetype source, String csvDir, String projectCode, int suffixSize) throws SourceException {
        this.source = source;
        this.csvDir = csvDir;
        //initialize the source
        rewind();
        //initialize the identifier parser
        try{
            if ( null != source.getIdentifierclass() ){
                Constructor con = Class.forName(source.getIdentifierclass()).getConstructors()[0];
                idParser = (AbstractParser)con.newInstance(new Object[]{projectCode, new Integer(suffixSize)});
            }
            else{
                idParser = null;
            }
        }
        catch(Exception ex){
            throw new SourceException(ex);
        }
    }
    


	public CsvSource(Csvsourcetype source, String csvDir, String projctCode, int suffixSize, AbstractParser idParser) throws SourceException{
    	this.source = source;
    	this.csvDir = csvDir;
    	//initialize the source
    	rewind();
    	//initialize the identifier parser
    	this.idParser = idParser;
    }
    
    public boolean nextRow() throws SourceException {
        try{
            currentRow = csvReader.readNext();
            if ( null == currentRow ){
                return false;
            }
            else{
                return true;
            }
        }
        catch(IOException ex){
            throw new SourceException(ex);
        }
    }

    public String getColumn(int index) {
        String value = currentRow[index];
        if ( null != value ){
            if ( value.length() > 0 ){
                //remove leading and trailing quote character if present
                if ( 0 == value.indexOf("\"") ){
                    value = value.substring(1);
                }
                if ( (value.length()-1) == value.lastIndexOf("\"") ){
                    value = value.substring(value.length()-1, value.length());
                }
            }
        }        
        //TODO - this code will fall over if value happens to be null! (but maybe null is never returned?
        return value.trim();
    }

    public String getNativeIdentifier() {
        if ( null != source.getIdentifiercolumn() ){
            return currentRow[source.getIdentifiercolumn().intValue()];
        }
        else{
            return null;
        }
    }

    public Identifier getPsygridIdentifier() throws SourceException {
    	
        Identifier psygridId = null;
        try{
            String nativeId = getNativeIdentifier();

            if ( null != nativeId ){
                psygridId =  idParser.createIdentifier(nativeId);
            }
        }
        catch(Exception ex){
            throw new SourceException(ex);
        }
        
        return psygridId;
    }
    
    /**
     * After findRowById is called, this method can be called to tell if the last 'find' was successful or not.
     * @return
     */
    public boolean isQueued() {
		return isQueued;
	}

    public boolean findRowById(String id) throws SourceException {
        boolean foundRow = moveToRowById(id);
        if ( !foundRow ){
            //got to the end of the file without finding the id, so rewind 
            //and try again
            rewind();
            foundRow = moveToRowById(id);
        }
        
        isQueued = foundRow;
        return foundRow;
    }

    private boolean moveToRowById(String id) throws SourceException {
        boolean foundRow = false;
        while ( nextRow() ){
            if ( id.equals(getNativeIdentifier()) ){
                foundRow = true;
                break;
            }
        }
        return foundRow;
    }
        
    public void rewind() throws SourceException {
        try{
            if ( null != csvReader ){
            	csvReader.close();
            }
            csvReader = new CSVReader(new BufferedReader(new FileReader(csvDir+source.getFilename())));
            //fast forward through the header rows
            for ( int i=0; i<source.getHeaderrows().intValue(); i++ ){
                if ( !nextRow() ){
                    //hit the end of the file whilst FFing through the
                    //header rows
                    throw new SourceException("End of file reached whilst still skipping header rows.");
                }
            }
        }
        catch(IOException ex){
            throw new SourceException(ex);
        }
    }

}
