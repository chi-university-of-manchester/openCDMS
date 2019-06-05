package org.psygrid.collection.entry.utils;

/**
 * Convenience class for parsing the collect version string so that it can be compared easily to other version strings.
 * @author Bill
 *
 */
public class CollectVersionParser {
	
	public class ParsedCollectVersion implements Comparable<ParsedCollectVersion>{
		
		private final Integer major;
		private final Integer minor;
		private final Integer defect;
		
		public ParsedCollectVersion(int major, int minor, int defect){
			this.major = major;
			this.minor = minor;
			this.defect = defect;
		}

		/**
		 * Compares the object with the argument object.
		 * If the version is higher than the argument, an int greater than 0 is returned.
		 * If the version is lower than the argument, an int less than 0 is returned.
		 * If the two versions are equal, 0 is returned.
		 */
		public int compareTo(ParsedCollectVersion o) {
			if(this.major != o.major){
				return this.major.compareTo(o.major);
			}else if(this.minor != o.minor){
				return this.minor.compareTo(o.minor);
			}else if (this.defect != o.defect){
				return this.defect.compareTo(o.defect);
			}else{
				return 0;
			}
		}

		public Integer getMajor() {
			return major;
		}

		public Integer getMinor() {
			return minor;
		}

		public Integer getDefect() {
			return defect;
		}

		
		
		
	}
	
	public CollectVersionParser(){
		
	}
	
	/**
	 * Parses the collect version string.
	 * The expected format is: A.B.C (D) where A, B and C are integers and D is a String.
	 * The only values necessary for comparing collect version strings are A, B and C.
	 * @param collectVersion
	 * @return
	 */
	public ParsedCollectVersion parseCollectVersionString(String collectVersion){
		
		int spaceIndex = collectVersion.indexOf(" ");
		String meaningfulVersion = collectVersion.substring(0, spaceIndex);
		String delims = "[._]";
		String[] tokens = meaningfulVersion.split(delims);
				
		ParsedCollectVersion parsedCollectVersion = new ParsedCollectVersion(Integer.valueOf(tokens[0]), Integer.valueOf(tokens[1]), Integer.valueOf(tokens[2]));
	
		return parsedCollectVersion;
	}

}
