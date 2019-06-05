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
package org.psygrid.data.dao;


import java.io.UnsupportedEncodingException;

import org.hibernate.HibernateException;
import org.psygrid.data.SearchType;
import org.psygrid.data.UserPrivilegeMatrix;
import org.psygrid.data.model.FailedTestException;
import org.psygrid.data.model.RepositoryModelException;
import org.psygrid.data.model.dto.DELQueryObject;
import org.psygrid.data.model.dto.DataElementContainerDTO;
import org.psygrid.data.model.dto.DocumentDTO;
import org.psygrid.data.model.dto.ElementDTO;
import org.psygrid.data.model.dto.ElementMetaDataDTO;
import org.psygrid.data.model.dto.ElementStatusContainer;
import org.psygrid.data.model.dto.LSIDDTO;
import org.psygrid.data.model.hibernate.AdminInfo;
import org.psygrid.data.model.hibernate.DataElementStatus;
import org.psygrid.data.model.hibernate.LSIDAuthority;
import org.psygrid.data.model.hibernate.LSIDException;
import org.psygrid.data.repository.dao.RelationshipReconstitutionException;

public interface DataElementDAO {
	
	public LSIDAuthority[] getLSIDAuthorityList();
	
    public LSIDDTO saveDataElement(DataElementContainerDTO dataElement, AdminInfo info, String authority, boolean autoApprove) throws HibernateException, UnknownNativeRelationship, UnsupportedEncodingException, LSIDException, FailedTestException, ElementAuthorityNotRecognizedException;
    
    public DataElementContainerDTO getElementAndConstituents(final String lsid, final boolean retrieveConstituents, final boolean eraseDBReferences, UserPrivilegeMatrix userMatrix) throws HibernateException, NoSuchElementException, RelationshipReconstitutionException, LSIDException;
    
    public String getCurrentRevisionLevel(final String lsid, final boolean omitPending);

	public ElementDTO[] getElementByTypeAndName(String elementType, String elementName, SearchType searchType);
	
	public DELQueryObject sophisticatedGetElementByTypeAndName(DELQueryObject queryManager) throws HibernateException, NoSuchElementException, RelationshipReconstitutionException, LSIDException;

	public String reviseElement(DataElementContainerDTO elem, AdminInfo adminInfo, String saml) throws UnknownNativeRelationship, LSIDException, ElementRevisionException, UnsupportedEncodingException, FailedTestException, ElementAuthorityNotRecognizedException, RepositoryModelException;
	
	public ElementMetaDataDTO getMetaData(String lsid, boolean retrieveFullHistory) throws HibernateException, NoSuchElementException, RelationshipReconstitutionException, LSIDException;
	
	public void insertLSIDAuthority(String LSIDAuthority) throws HibernateException, LSIDException;
	
	public DocumentDTO[] getDocumentsSummaryInfo(final String authority);
	
	public void modifyElementStatus(DataElementStatus newStatus, String lsid, AdminInfo adminInfo) throws LSIDException, ElementStatusChangeException;
	
	public ElementStatusContainer [] reportElementStatusChanges(ElementStatusContainer [] elementsInQuestion, boolean reportNonHeadRevisionElements) throws LSIDException;
}
