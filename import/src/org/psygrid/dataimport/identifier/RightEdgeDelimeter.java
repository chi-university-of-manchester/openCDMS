package org.psygrid.dataimport.identifier;

public class RightEdgeDelimeter extends Delimeter {
	
		public RightEdgeDelimeter(){
		super(DelimeterType.RH_EDGE, ReferenceEdge.RH);
	}

		@Override
		public int findPos(String identifier)
				throws DelimeterNotFoundException {
			return identifier.length();
		}

		@Override
		public int findPosOfAdjacentData(String identifier) {
			return identifier.length() - 1;
		}

		@Override
		public int getDelimeterLength() {
			//A rh edge delimeter has no length.
			return 0;
		}
}
