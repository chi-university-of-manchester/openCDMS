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


package org.psygrid.collection.entry.chooser;

import javax.swing.JOptionPane;

import org.psygrid.collection.entry.externaldocparser.RecognizedFileType;

public class ImportFileTypeValidator {
	
	public ImportFileTypeValidator(){
		super();
	}
	
	private boolean allowForCustomFileSuffixes = false; //TODO: This is a feature suppressor. It should  be removed in future.
	
    class FileTypeAnalyzer{
    	
       	private String fileName = null;
    	public FileTypeAnalyzer(String fileName){
    		this.fileName = fileName;
    	}
   	
    	public RecognizedFileType getRecognizedFileType() throws FileTypeNotRecognizedException{
    		String suffix = getFileSuffix();
    		if(suffix.contentEquals("csv"))
    			return RecognizedFileType.csv;
    		else if(suffix.contentEquals("xml"))
    			return RecognizedFileType.xml;
    		else{
    			String errorString = "The file type '." + suffix + "' is not recognized.";
    			throw new FileTypeNotRecognizedException(errorString);
    		}
    	}
    	
    	private String getFileSuffix(){
    		int suffixDelimeter = fileName.lastIndexOf(".");
    		return fileName.substring(suffixDelimeter+1);
    	}
    }
    
    public RecognizedFileType validateFile(String file) throws FileTypeNotRecognizedException {
     	
    	RecognizedFileType retVal = RecognizedFileType.unknown;
		FileTypeAnalyzer fta = new FileTypeAnalyzer(file);
		RecognizedFileType type;
		try {
			type = fta.getRecognizedFileType();
			if(type == RecognizedFileType.csv){
				retVal = RecognizedFileType.csv;
			}
			else if(type == RecognizedFileType.xml){
				retVal = RecognizedFileType.xml;
			}
		} catch (FileTypeNotRecognizedException e) {
			
			if(allowForCustomFileSuffixes){
			    Object[] options1 = {"XML", "CSV", "Neither XML nor CSV"};
				int n = JOptionPane.showOptionDialog(null,
				"The input file type is not recognized." + 
				"Please specify if this is one of the recognized file types below (csv, xml), or is neither",
				e.getMessage(),
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options1,
				options1[2]);
			
				if(n == JOptionPane.CANCEL_OPTION) //Cancel
					throw new FileTypeNotRecognizedException("File type not recognized.");
			}
			else{
				throw new FileTypeNotRecognizedException("File type not recognized.");
			}
		}
		
		return retVal;
	}

}
