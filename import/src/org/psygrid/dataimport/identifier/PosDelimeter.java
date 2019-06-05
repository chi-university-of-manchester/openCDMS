package org.psygrid.dataimport.identifier;

/**
 * The positional delimeter can either be an absolute position, or it can be a relative one,
 * depending on which constructor is deployed.
 * 
 * If it is an absolute delimeter, then the value returned marks the edge of the substring (inclusive).
 * It is up to the calling code to know whether this value is the r.h. or l.h. delimeter.
 * 
 * @author williamvance
 *
 */
public class PosDelimeter extends Delimeter {

	private final int position;
	
	
	/**
	 * @param pos - the position adjacent to the data that this object delimits.
	 * Therefore, if this is a LH delimeter and the data starts at position zero,
	 * then the 'pos' value needs to be '-1'.
	 * @param refEdge - defines which edge of the info this object delimits - rh or lh.
	 */
	public PosDelimeter(int pos, ReferenceEdge refEdge){
		super(DelimeterType.POSITIONAL, refEdge);
		position = pos;
	}

	/**
	 * 
	 * @param pos - the position of the delimiter relative to the reference. If the value is 
	 * positive, it means that this delimeter object is to the right of its reference. If the value is
	 * negative, it means that this delimeter is to the left of its reference.
	 * @param pos - the position of this delimeter relative to its reference. The pos is always relative to the left-most
	 * position of the reference delimeter.
	 * @param reference - the position relative to CLOSEST character in the reference delimeter.
	 * @param referenceEdge - indicates whether this delimeter identifies the rh or left hand edge of the info.
	 */
	public PosDelimeter(int pos, Delimeter reference, ReferenceEdge referenceEdge){
		super(DelimeterType.POSITIONAL, reference, referenceEdge);
		this.position = pos;
		
		//If this edge pos is negative, but the reference delimeter is rh, then throw an IllegalArgumentException.
		//If this edge pos is positive, but the referunce delimeter is lh, then throw an IllegalArgumentException.
		
		/*
		if(pos < 0 && reference.getRefEdge() == ReferenceEdge.RH){
			throw new IllegalArgumentException("If the reference delimeter is to the right, then it must be a left-hand delimeter.");
		}else if(pos > 0 && reference.getRefEdge() == ReferenceEdge.LH){
			throw new IllegalArgumentException("If the reference delimeter is to the left, then in must be a right-hand delimeter.");
		}else if(pos == 0){
			throw new IllegalArgumentException("The position argument (pos) cannot be zero.");
		}else if(pos > 0 && pos < reference.getDelimeterLength()){
			throw new IllegalArgumentException("If the reference delimeter is to the left, the relative position must be atleast the length of the referenced delimeter.");
		}
		*/
	}
	
	

	public int getPosition() {
		return position;
	}

	@Override
	protected int findPos(String identifier) throws DelimeterNotFoundException {
		if(!this.hasReference())
			return position;
		else{
			int posFromReference = getReference().findPos(identifier);
			
			return posFromReference + position + getReference().getDelimeterLength();
		}
	}
	
	

	@Override
	public int getDelimeterLength() {
		//A positional delimeter has no length.
		return 0;
	}

	@Override
	public int findPosOfAdjacentData(String identifier) {
		int position = 0;
		try {
			position = findPos(identifier);
		} catch (DelimeterNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(this.getRefEdge() == ReferenceEdge.RH){
			position -= 1;
		}else{
			position += 1;
		}
		return position;
	}

}
