package org.psygrid.common.strings;

public class StringSubsetDefinition {
	
	private final String subset;
	private final int lhDelimiter;
	private final int rhDelimiter;


	protected StringSubsetDefinition(String s, int lhDel, int rhDel){
		subset = s;
		lhDelimiter = lhDel;
		rhDelimiter = rhDel;
	}
	
	
	
	public String getSubset() {
		return subset;
	}

	public int getLhDelimiter() {
		return lhDelimiter;
	}

	public int getRhDelimiter() {
		return rhDelimiter;
	}

}
