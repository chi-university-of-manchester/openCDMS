

package org.psygrid.data.importing.model;

import java.util.List;


public interface ImportDAO {

	ImportRequest getNextImportRequest();

	void saveImportRequest(ImportRequest request);
	
	ImportRequest getImportRequest(long id);
	
	List<ImportRequest> getImportRequests(String projectCode);
	
//	List<ImportLogEntry> getImportLog(long requestID);
		
}
