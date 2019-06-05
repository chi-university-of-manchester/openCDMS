package org.psygrid.dataimport.identifier;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.dataimport.identifier.Delimeter.DelimeterType;
import org.psygrid.dataimport.identifier.Delimeter.ReferenceEdge;

public class UniqueValueDelimiter extends Delimeter {

	
	private final List<String> uniqueValuesList;
	
	
	protected UniqueValueDelimiter(DelimeterType type, ReferenceEdge referenceEdge, List<String> uniqueValuesList){
		super(type, referenceEdge);
		this.uniqueValuesList = uniqueValuesList;
	}
	
	@Override
	protected int findPos(String identifier) throws DelimeterNotFoundException {

		
		List<String> matches = new ArrayList<String>();
		
		for(String val : uniqueValuesList){
			if(identifier.contains(val)){
				matches.add(val);
			}
		}
		
		
		String longestMatch = "";
		
		for(String matchItem: matches){
			if(matchItem.length() > longestMatch.length()){
				longestMatch = matchItem;
			}
		}
		
		int index = identifier.indexOf(longestMatch);
		int stringLength = longestMatch.length();
		
		if(this.getRefEdge() == ReferenceEdge.LH){
			index += stringLength;
		}else{
			index -= 1;
		}
		
		return index;

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

	@Override
	public int getDelimeterLength() {
		
		return 0;
	}

}
