package org.psygrid.collection.entry.persistence;

public class ExternalIdGetter {

	public static String get(String openCDMSIdentifier) throws NoExternalIdMappingException{
		
		String result = PersistenceManager.getInstance().getExternalIdMap().get(openCDMSIdentifier);
		
		boolean debug = false;
		
		if(result == null || debug){
			throw new NoExternalIdMappingException("No external id mapping could be found for: " + openCDMSIdentifier + "."); 
		}
		
		return result;
	}
	
}
