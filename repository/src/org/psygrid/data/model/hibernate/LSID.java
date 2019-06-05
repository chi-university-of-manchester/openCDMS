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
package org.psygrid.data.model.hibernate;


import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.lang.StringBuffer;
import java.util.StringTokenizer;



/**
* This class represents a "life science identifier".
* It is used to uniquely identify all objects deriving from
* 'Element' in the data element library.
*  
* @hibernate.joined-subclass table="t_lsid" batch-size="100"
* @hibernate.joined-subclass-key column="c_lsid_id"
**/
public class LSID extends Persistent {
	
	static BitSet dontNeedEncoding; //Set of allowed, unencoded characters.
	
    static {
    	dontNeedEncoding = new BitSet(256);
    	int i;
    	for (i = 'a'; i <= 'z'; i++) {
    	    dontNeedEncoding.set(i);
    	}
    	for (i = 'A'; i <= 'Z'; i++) {
    	    dontNeedEncoding.set(i);
    	}
    	for (i = '0'; i <= '9'; i++) {
    	    dontNeedEncoding.set(i);
    	}
    	
    	dontNeedEncoding.set('(');
    	dontNeedEncoding.set(')');
    	dontNeedEncoding.set('+');
    	dontNeedEncoding.set(',');
    	dontNeedEncoding.set('-');
    	dontNeedEncoding.set('.');
    	dontNeedEncoding.set(':');
    	dontNeedEncoding.set('=');
    	dontNeedEncoding.set('@');
    	dontNeedEncoding.set(';');
    	dontNeedEncoding.set('$');
    	dontNeedEncoding.set('_');
    	dontNeedEncoding.set('!');
    	dontNeedEncoding.set('*');
    	dontNeedEncoding.set('\'');
        }
	
	private static class LSIDValidator{
		public boolean validate_LSID_String(String lsid){
			//Parse the LSID string. Make sure that it has only three idSeparator tokens.
			//After parsing, feed into the respective constituent validation methods.
			StringTokenizer stringTokenizer = new StringTokenizer(lsid, idSeparator, false);
			int tokenCount = stringTokenizer.countTokens();
			//We can have either three or four tokens because the revision is optional
			//We also want to verify that the first two tokens are 'URN' and 'LSID' (case-sensitive)
			if(tokenCount < 5 || tokenCount > 6 || !ensureStandardLSIDPrefixes(lsid)){ 
				return false;
			}
	
			boolean returnValue = false;
			
			//String authorityId = null, namespaceId = null, objectId = null, revisionId = null;
			String[] tokenList = new String[4];
			int elementCount = parse_lsid(lsid, tokenList);
			
			for(int i = 0; i < elementCount; i++){
				if(!validateLSIDElement(tokenList[i])){
					returnValue = false;
					break;
				}else{
					returnValue = true;
				}
			}
			
			
			return returnValue;

		}
		
		protected boolean ensureStandardLSIDPrefixes(String lsid){
			StringTokenizer stringTokenizer = new StringTokenizer(lsid, idSeparator, false);
			String temp = null;
			boolean returnValue = true;
			for(int t = 0; t < 2; t++){
				temp = stringTokenizer.nextToken();
				switch(t){
				case(0):
				{
					int compResult = temp.compareToIgnoreCase(urnToken);
					if(compResult != 0){
						returnValue = false;
					}
				}
				break;
				case(1):
				{
					int compResult = temp.compareToIgnoreCase(lsidToken);
					if(compResult != 0){
						returnValue = false;
					}
				}
				break;
				}
			}
			return returnValue;
		}
		
		/*
		 * Returns the number of LSID elements contained within the LSID string.
		 * This number may vary because the revision element is optional.
		 */
		protected int parse_lsid(String lsid, String[] strings){
			StringTokenizer stringTokenizer = new StringTokenizer(lsid, idSeparator, false);
			int count = 0;
			
			//We don't want the first two tokens, only the last four (or three - version is optional)
			stringTokenizer.nextElement();
			stringTokenizer.nextElement();
			
			while(stringTokenizer.hasMoreElements()){
					strings[count] = stringTokenizer.nextToken();
					count++;
			}
			
			return count;
		}
		
		/*
		 * Validates the element as being alphanumeric, not null, and greater than zero length.
		 */
		protected boolean validateLSIDElement(String LSIDElement){
			boolean response = false;
			if(LSIDElement != null){
				if(verifyNotNullAndNotZeroLength(LSIDElement) && isNSSCompliant(LSIDElement)){
					response = true;
				}else{
					response = false;
				}			
			}
			return response;
		}
		
		
		private boolean verifyNotNullAndNotZeroLength(String string){
			boolean returnValue = false;
			if(string != null
				&& string.length() > 0){
				returnValue = true;
			}else{
				returnValue = false;
			}
			
			return returnValue;
		}
		
		
		private  boolean isNSSCompliant(final String s) {
			boolean isCompliant = true;
			for (int i = 0; i < s.length(); i++) {
			    int c = (int) s.charAt(i);
			    //System.out.println("Examining character: " + c);
			    if (dontNeedEncoding.get(c)) {
			    	continue;
				} else {
			    	//Check to see if this is an already-encoded character
					//If the following sequence is followed, then the character doesn't need encoding:
					// "%"<hex><hex>
					
					if(c == '%'){
						if(i+2 < s.length()){
							//Make sure the next two characters are hexadecimal
							Character first = new Character(s.charAt(i+1));
							Character second = new Character(s.charAt(i+2));
							
							try{
								Integer.parseInt(first.toString(), 16);
								Integer.parseInt(second.toString(), 16);
							}catch(NumberFormatException e){
								//One of these characters wasn't a valid hex!
								isCompliant = false;
								break;
							}
							
						}else{
							isCompliant = false;
							break;
						}
						
						i+=2; //Move forward by two.
					}else{ //It wasn't an escape-sequence. Not valid!
						isCompliant = false;
						break;
					}
			    }
			}
			
			return isCompliant;
		}
	}		

	private String   nextRevision = null;
	private String   previousRevision = null;

	private LSIDAuthority lsidAuthority;
	private LSIDNameSpace lsidNameSpace;
	private String objectId;
	private String revisionId;
	private static final String idSeparator = ":";
	private static final LSIDValidator lsidValidator = new LSIDValidator();
	private static final String urnToken = "URN";
	private static final String lsidToken = "LSID";
	private boolean isTemplate = false;

	public static boolean isNSSCompliant(final String s){
		return lsidValidator.isNSSCompliant(s);
	}
	
	public boolean equals(Object obj){
		boolean returnValue = false;
		if(!(obj instanceof LSID)){
			returnValue = false;
		}else{
			LSID lsidObj = (LSID)obj;
			//compare all Strings
			if(lsidAuthority.getAuthorityID().equals(lsidObj.getAuthorityId())
				&& lsidNameSpace.getNameSpace().equals(lsidObj.getNamespaceId())
				&& getObjectId().equals(lsidObj.getObjectId())
				&& revisionId.equals(lsidObj.getRevisionId())){
				returnValue = true;
			}else{
				returnValue = false;
			}
		}
			
		return returnValue;
	}	
	

	public String getAuthorityId(){
		return this.lsidAuthority.getAuthorityID();
	}
	

	
	public String getNamespaceId(){
		return this.lsidNameSpace.getNameSpace();
	}
	
	
	/**
	 * 
	 * @return - the string representation of the LSID's revision Id.
	 * 
	 * @hibernate.property column="c_revision_id"
	 */
	public String getRevisionId(){
		return revisionId;
	}
	
	/**
	 * This is a dummy function - needed for hibernate. We do not allow the explicit setting
	 * of the revisionId - must be done through the constructor.
	 * @param revisionId
	 */
	public void setRevisionId(String revisionId){
		this.revisionId = revisionId;
	}

	/**
	 * Stores the human-readable part of the objectId. 
	 * @return - the string representation of the LSID's object Id.
	 * 
	 * @hibernate.property column="c_object_id"
	 */
	public String getObjectId(){
		return objectId;
	}
	
	
	/**
	 * This is a dummy function - needed for hibernate. We do not allow the explicit setting
	 * of the objectId - must be done through the constructor.
	 * @param objectId
	 */
	public void setObjectId(String objectId){
		this.objectId = objectId;
	}
	
	/**
	 * 
	 * @return - lsid of the next revision - if any. Can return null!
	 * 
	 * @hibernate.property column="c_next_revision"
	 * 						not-null="false"
	 */
	public String getNextRevision() {
		return nextRevision;
	}

	public void setNextRevision(String nextRevision) {
		this.nextRevision = nextRevision;
	}

	
	/**
	 * 
	 * @return - lsid of the previous revision - if any. Can return null!
	 * 
	 * @hibernate.property column="c_previous_revision"
	 * 						not-null="false"
	 */
	public String getPreviousRevision() {
		return previousRevision;
	}

	public void setPreviousRevision(String previousRevision) {
		this.previousRevision = previousRevision;
	}
	
	public String toString(){
		StringBuffer stringBuilder = new StringBuffer();
		stringBuilder.append(urnToken).append(idSeparator);
		stringBuilder.append(lsidToken).append(idSeparator);
		stringBuilder.append(this.lsidAuthority.getAuthorityID()).append(idSeparator);
		stringBuilder.append(this.lsidNameSpace.getNameSpace()).append(idSeparator);
		stringBuilder.append(getObjectId() != null ? objectId : getId().toString());
		
		
		if(revisionId != null){
			stringBuilder.append(idSeparator);
			stringBuilder.append(revisionId);
		}

		return stringBuilder.toString();
	}
	
	/**
	 * This is a dummy constructor - required by hibernate.
	 *
	 */
	public LSID(){
	}

	private LSID(LSIDAuthority authorityObj, LSIDNameSpace nsObj, String objectId, String revisionId, boolean isTemplate){
		this.lsidAuthority = authorityObj;
		this.lsidNameSpace = nsObj;
		this.objectId = objectId;
		this.revisionId = revisionId;
		this.isTemplate = isTemplate;
	}
	
	private LSID(String lsid){
		String[] stringArray = new String[4];
		lsidValidator.parse_lsid(lsid, stringArray);
		
		this.lsidAuthority = new LSIDAuthority(stringArray[0]);
		this.lsidNameSpace = new LSIDNameSpace(stringArray[1]);
		
		this.objectId = stringArray[2];
		this.revisionId = stringArray[3];
	}
	
	public static LSID valueOf(String lsid) throws LSIDException{
		if(!lsidValidator.validate_LSID_String(lsid)){
			throw new LSIDException("The lsid string is invalid.");
		}
	
		return new LSID(lsid);
	}
		
	
	public static LSID valueOf(String authorityId, String namespaceId, String objectId, String revisionId, boolean isTemplate) throws LSIDException{
		if(!lsidValidator.validateLSIDElement(authorityId)
			|| !lsidValidator.validateLSIDElement(namespaceId)
			|| (objectId != null && !lsidValidator.validateLSIDElement(objectId)) //Don't try and validate objectId if it's null
			|| (revisionId != null  && !lsidValidator.validateLSIDElement(revisionId))){
			throw new LSIDException("One or more of the lsid constituents is invalid.");
		}
		return new LSID(new LSIDAuthority(authorityId), new LSIDNameSpace(namespaceId), objectId, revisionId, isTemplate);
	}
	
	@Override
	public org.psygrid.data.model.dto.LSIDDTO toDTO(
			Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs,
			RetrieveDepth depth) {
		        
		org.psygrid.data.model.dto.LSIDDTO lsid = null;
		if ( Persistent.getPrepareElementForNewRevision() ){
			//Retrieving from the DEL and removing all Persistent IDs - all
			//elements get their own LSID object
			lsid = new org.psygrid.data.model.dto.LSIDDTO();
			lsid.setLsidAuthority(new org.psygrid.data.model.dto.LSIDAuthorityDTO(this.getLsidAuthority().getAuthorityID()));
			lsid.setLsidNameSpace(new org.psygrid.data.model.dto.LSIDNameSpaceDTO(this.getLsidNameSpace().getNameSpace()));
			lsid.setObjectId(this.objectId);
			lsid.setRevisionId(this.revisionId);
		}
		else{        
			//Normal operation
	        if(dtoRefs.containsKey(this)){
	        	lsid = (org.psygrid.data.model.dto.LSIDDTO)dtoRefs.get(this);
	        }
	        if(null == lsid){
	        	lsid = new org.psygrid.data.model.dto.LSIDDTO();
	        	dtoRefs.put(this, lsid);
	        	toDTO(lsid, dtoRefs, depth);
	        }
		}
        return lsid;
	}
	
	
	public org.psygrid.data.model.dto.LSIDDTO toDTO(){
		 Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs = new HashMap<Persistent, org.psygrid.data.model.dto.PersistentDTO>();
		    return (org.psygrid.data.model.dto.LSIDDTO)toDTO(dtoRefs, RetrieveDepth.DS_COMPLETE);
	}
	
	public void toDTO(org.psygrid.data.model.dto.LSIDDTO lsid, Map<Persistent, org.psygrid.data.model.dto.PersistentDTO> dtoRefs, RetrieveDepth depth){
		super.toDTO(lsid, dtoRefs, depth);
		
		lsid.setLsidAuthority(this.lsidAuthority.toDTO(dtoRefs, depth));
		lsid.setLsidNameSpace(this.lsidNameSpace.toDTO(dtoRefs, depth));
		lsid.setObjectId(this.objectId);
		lsid.setRevisionId(this.revisionId);
	}


	 /**
     *  
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.LSIDAuthority"
     *                         column="c_auth_id"
     *                         not-null="true"
     *                         cascade="save-update"
     *                         fetch="join"
     */
	public LSIDAuthority getLsidAuthority() {
		return lsidAuthority;
	}


	public void setLsidAuthority(LSIDAuthority lsidAuthority) {
		this.lsidAuthority = lsidAuthority;
	}


	 /**
     *  
     * @hibernate.many-to-one class="org.psygrid.data.model.hibernate.LSIDNameSpace"
     *                         column="c_ns_id"
     *                         not-null="true"
     *                         cascade="save-update"
     *                         fetch="join"
     */
	public LSIDNameSpace getLsidNameSpace() {
		return lsidNameSpace;
	}


	public void setLsidNameSpace(LSIDNameSpace lsidNameSpace) {
		this.lsidNameSpace = lsidNameSpace;
	}

	/**
	 * Returns whether the lsid object is in template mode. When in 'template' mode, the objectID has
	 * yet to be appended with hibernate identifiers for faster element access.
	 * @return - true if in template mode, false otherwise.
	 */
	public boolean getIsTemplate() {
		return isTemplate;
	}
	
	/**
	 * This completes the lsid's object id with the hibernate id of the element, and the
	 * hibernate id of this object. Then the 'isTemplate' flag is set to 'false'. 
	 * @param elementId
	 */
	public void completeObjectIdWithDBSpecifics(Long elementId){
			
		String newObjectId = objectId.concat("-" + elementId.toString());	
		newObjectId = newObjectId.concat("-" + getId().toString());
		this.objectId = newObjectId;
		isTemplate = false;
	}
	
	/**
	 * Removes any db specifics from the object (if this is not a template) and then
	 * sets 'isTemplate' to 'true'.
	 *
	 */
	public void removeDBSpecificsFromObjectId(){
		if(!isTemplate){
			setObjectId(getObjectId().substring(0, getObjectId().lastIndexOf('-', getObjectId().lastIndexOf('-')-1)));
			isTemplate = true;
		}

	}
}
