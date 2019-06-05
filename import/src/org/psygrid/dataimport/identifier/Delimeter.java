package org.psygrid.dataimport.identifier;


/**
 * This abstract class carries information regarding the type of delimeter for a particular information 
 * type. 
 * @author williamvance
 *
 */
public abstract class Delimeter {
	
	
	private final DelimeterType delimeterType;
	private final Delimeter reference;
	private final ReferenceEdge refEdge;
	
	public ReferenceEdge getRefEdge() {
		return refEdge;
	}

	/**
	 * Defines which edge of the data that this object delimits.
	 * @author Bill
	 *
	 */
	public enum ReferenceEdge{
		LH,
		RH
	}
	
	/**
	 * Defines the type of delimeter.
	 * @author williamvance
	 *
	 */
	public enum DelimeterType{
		CHARACTER, //Specify a delimiting character
		POSITIONAL, //Specify a delimiting position relative to the associated delimeter
		RH_EDGE, //The rh edge of the string being parsed.
		UNIQUE_VALUE //This is a delimiter of a unique string
	}
	
	protected Delimeter(DelimeterType type, ReferenceEdge referenceEdge){
		delimeterType = type;
		reference = null;
		refEdge = referenceEdge;
	}
	
	protected Delimeter(DelimeterType type, Delimeter reference, ReferenceEdge referenceEdge){
		delimeterType = type;
		this.reference = reference;
		this.refEdge = referenceEdge;
	}
	
	public DelimeterType getDelimeterType(){
		return delimeterType;
	}
	
	/**
	 * This finds the absolute position of a delimeter within a string. If the delimeter is multi-character, it
	 * needs to return the right-most position held by that delimeter within the string.
	 * @param identifier
	 * @return
	 * @throws DelimeterNotFoundException
	 */
	
	/**
	 * Always returns the left-most position of the delimeter.
	 */
	protected abstract int findPos(String identifier) throws DelimeterNotFoundException;
	
	/**
	 * gets the position of the data adjacent to this delimeter.
	 * @param identifier
	 * @return
	 */
	public abstract int findPosOfAdjacentData(String identifier);
	
	public boolean hasReference(){
		return (reference != null);
	}
	
	
	protected Delimeter getReference(){
		return reference;
	}
	
	/**
	 * Returns the length of the delimeter. 
	 * (If the delimeter is positional, its length is zero).
	 * @return
	 */
	public abstract int getDelimeterLength();

}
