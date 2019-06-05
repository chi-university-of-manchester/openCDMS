package org.psygrid.dataimport.identifier;

import java.util.StringTokenizer;

public class CharacterDelimeter extends Delimeter {

	private final String delimeter;
	private final int occurrence; //The nth occurrence in the string.
	
	
	/**
	 * Constructs a character delimeter whose position is the nth occurrence wrt its reference delimeter.
	 * @param delimeter - the delimeter string
	 * @param reference - the delimeter by which this delimeter is referenced.
	 * @param occurrence - the nth occurrence (from the reference) in the string that is being parsed. Negative values
	 * indicate that the delimeter is to the left of the reference.
	 * @param referenceEdge - whether this delimeter is at the rh or lh edge of the info.
	 */
	public CharacterDelimeter(String delimeter, Delimeter reference, int occurrence, ReferenceEdge referenceEdge){
		super(DelimeterType.CHARACTER, reference, referenceEdge);
		
		/*
		if(occurrence < 0 && reference.getRefEdge() == ReferenceEdge.RH){
			throw new IllegalArgumentException("If the delimeter is to the left of its reference, the reference delimeter must be a left-hand one.");
		}else if(occurrence > 0 && reference.getRefEdge() == ReferenceEdge.LH){
			throw new IllegalArgumentException("If the delimeter is to the right of its reference, the reference delimeter must be a right-hand one.");
		}else */if(occurrence == 0){
			throw new IllegalArgumentException("The value of the occurrence argument cannot be zero.");
		}
		
		this.occurrence = occurrence; //Not used because this is a reference.
		this.delimeter = delimeter;
	}
	
	/**
	 * Contructs a character delimeter.
	 * @param delimeter - the string that delimits the desired substring.
	 * @param occurrence - the nth occurrence of the delimeter in the string that is being parsed.
	 * @panam referenceEdge - whether this delimeter delimets the rh or lh edge of the info.
	 */
	public CharacterDelimeter(String delimeter, int occurrence, ReferenceEdge referenceEdge){
		super(DelimeterType.CHARACTER, referenceEdge);
		this.occurrence = occurrence;
		this.delimeter = delimeter;
	}
	
	public String getDelimeter(){
		return delimeter;
	}
	
	/**
	 * Finds the nth occurrence (specified by the 'occurrence' property of this object's delimeter within the string passed in.
	 * @param string
	 * @return - the position of the LAST character of the nth delimeter within the string.
	 */
	private int findNthOccurrenceOfDelimeterInString(String string){
		
		int fromIndex = 0;
		for(int occurrenceCounter = 1; occurrenceCounter <= this.occurrence; occurrenceCounter++){
			fromIndex = string.indexOf(delimeter, fromIndex) + delimeter.length();
		}	
		
		fromIndex -= 1; 
		return fromIndex;
	}

	@Override
	public int findPos(String identifier) throws DelimeterNotFoundException {
		
		if(this.hasReference()){
			
			int referencePosition = this.getReference().findPos(identifier);
			//The returned reference position will be the LEFT-MOST position of the delimeter.
			
			String subString = identifier.substring(referencePosition + getReference().getDelimeterLength()); //Add the 1 so that the the new substring is free and clear of the delimeter preceding it.
			
			int posOfNthOcc = findNthOccurrenceOfDelimeterInString(subString); //This is the position of the right-most delimiter in the substring.
			
			int leftMostPosOfNthOcc = posOfNthOcc - getDelimeterLength() + 1;
			//The above var is relative to a string that is clear of the position of its reference.
			
			return referencePosition + leftMostPosOfNthOcc + this.getReference().getDelimeterLength();
			
		}else{
			return this.findNthOccurrenceOfDelimeterInString(identifier);
		}
	}

	@Override
	public int getDelimeterLength() {
		return delimeter.length();
	}

	@Override
	public int findPosOfAdjacentData(String identifier) {
		int returnValue = 0;
		try {
			int delimeterPosition = findPos(identifier);
			if(this.getRefEdge() == ReferenceEdge.RH){
				returnValue = delimeterPosition - 1;
			}else{
				returnValue = delimeterPosition + delimeter.length();
			}
			
		} catch (DelimeterNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return returnValue;		
	}
	
}
