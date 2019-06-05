package org.psygrid.dataimport.identifier;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.dataimport.identifier.AbstractParser.InfoType;
import org.psygrid.dataimport.identifier.Delimeter.DelimeterType;
import org.psygrid.dataimport.identifier.Delimeter.ReferenceEdge;


/**
 * Specifies the a bit of information found within the external participant identifier and exactly where it can be found within
 * the id string.
 * @author williamvance
 */
public class IdentifierInfo {
	

	private final static int DELIMETER_NOT_FOUND = -1;
	
	private final InfoType informationType; //Type of info being specified
	private final Delimeter startDelimeter; //Starting Delimeter
	private final Delimeter endDelimeter; //Ending Delimeter
	private final List<String> uniqueValuesList; //List of unique values (unique to the external id string)
	private final boolean contentDefinedBySetOfUniqueValues;
	
	public IdentifierInfo(InfoType infoType, Delimeter startDelimeter, Delimeter endDelimeter){
		informationType = infoType;
		this.startDelimeter = startDelimeter;
		this.endDelimeter = endDelimeter;
		this.uniqueValuesList = null;
		contentDefinedBySetOfUniqueValues = false;
	}
	
	public IdentifierInfo(InfoType infoType, List<String> uniqueValuesList){
		informationType = infoType;
		this.startDelimeter = new UniqueValueDelimiter(DelimeterType.UNIQUE_VALUE, ReferenceEdge.LH, uniqueValuesList);
		this.endDelimeter = new UniqueValueDelimiter(DelimeterType.UNIQUE_VALUE, ReferenceEdge.RH, uniqueValuesList);
		this.uniqueValuesList = uniqueValuesList;
		contentDefinedBySetOfUniqueValues = true;
	}
	
	public InfoType getInformationType() {
		return informationType;
	}
	
	/**
	 * Returns the information specified by this IdentifierInfo object (i.e. 
	 * unique study id or centre info)
	 * @param identifier - external identifier from which the info is to be extracted.
	 * @return - the extracted information.
	 * @throws DelimeterNotFoundException
	 */
	public Object getInfo(String identifier) throws DelimeterNotFoundException{
		
		String info = null;
		
		if(this.contentDefinedBySetOfUniqueValues){
			
			List<String> matches = new ArrayList<String>();
			
			int lengthOfLongestMatch = -1;
			
			for(String value: this.uniqueValuesList){
				if (identifier.contains(value)){
					if(value.length() > lengthOfLongestMatch){
						lengthOfLongestMatch = value.length();
						info = value;
					}
					
				}
				
				
				
				
				if(info == null){
					
					StringBuffer sB = new StringBuffer();
					for(String value2: this.uniqueValuesList){
						sB.append(value2 + " ");
					}
					
					String errorString = "Identifier: " + identifier + " did not contain any of the specified unique substrings: " + sB.toString(); 
					
					throw new DelimeterNotFoundException(errorString);
				}
				
			}
			
		}else{
			int lhDelimitingPosition, rhDelimitingPosition;
			
			//What I want to know is 
			//a) the lh position of the 1st character in the string.
			//b) the rh position of the last character in the string.
			//So the positions returned from the following mehods need to be inclusive.
			
			//Ultimately we are going to be querying the delimeter, but not asking
			//"Where are YOU, delimeter?" but "Where is the position of the data that you delimit"?
			
			lhDelimitingPosition = getLHPosOfInfo(identifier);
			rhDelimitingPosition = getLastRHPosOfInfo(identifier);
			
			info = identifier.substring(lhDelimitingPosition, rhDelimitingPosition + 1);
			
		}
		
		return info;
		
	}
	
	/**
	 * Retrieves the first position of the information within the identifier string.
	 * @return
	 * @throws DelimeterNotFoundException 
	 */
	private int getLHPosOfInfo(String identifier) throws DelimeterNotFoundException{
		
		int pos = startDelimeter.findPosOfAdjacentData(identifier);
		return pos;
	}
	
	/**
	 * Retrieves the last rh position that the info occupies within the string.
	 * @return
	 * @throws DelimeterNotFoundException 
	 */
	private int getLastRHPosOfInfo(String identifier) throws DelimeterNotFoundException{
		int pos2 = this.endDelimeter.findPosOfAdjacentData(identifier);
		return pos2;
	}
		
	public Delimeter getStartDelimeter() {
		return startDelimeter;
	}

	public Delimeter getEndDelimeter() {
		return endDelimeter;
	}



}
