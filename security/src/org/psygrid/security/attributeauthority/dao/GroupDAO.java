package org.psygrid.security.attributeauthority.dao;

import java.util.List;

import org.psygrid.security.attributeauthority.model.hibernate.GroupAttribute;
import org.psygrid.www.xml.security.core.types.GroupAttributeType;

public interface GroupDAO {
	
	public void addGroupAttributeToGroup(Long groupId, GroupAttribute g) throws DAOException;
	public void addGroupAttributeToGroupLink(Long groupLinkId, Long groupAttributeId) throws DAOException;
	public List<GroupAttribute> getGroupAttributesForGroup(Long groupId) throws DAOException;

}
