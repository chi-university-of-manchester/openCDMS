package org.psygrid.security.attributeauthority.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.psygrid.security.attributeauthority.dao.DAOException;
import org.psygrid.security.attributeauthority.dao.GroupDAO;
import org.psygrid.security.attributeauthority.model.hibernate.Group;
import org.psygrid.security.attributeauthority.model.hibernate.GroupAttribute;
import org.psygrid.security.attributeauthority.model.hibernate.GroupLink;
import org.psygrid.security.attributeauthority.model.hibernate.Project;
import org.psygrid.www.xml.security.core.types.GroupAttributeType;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GroupDAOHibernate extends HibernateDaoSupport implements GroupDAO {

	public void addGroupAttributeToGroup(Long groupId, GroupAttribute g)
			throws DAOException {

		Group storedGroup = (Group) getHibernateTemplate().get(
				Group.class, groupId);
		if (null == storedGroup) {
			throw new DAOException(
					"Cannot add GroupAttributeToGroup - the object is out-of-date");
		}
		
		storedGroup.addGroupAttribute(g);
		
		getHibernateTemplate().saveOrUpdate(storedGroup);

	}
	
	public void addGroupAttributeToGroupLink(Long groupLinkId, Long groupAttributeId) throws DAOException{
		
		GroupLink storedGroupLink = (GroupLink) getHibernateTemplate().get(GroupLink.class, groupLinkId);
		if(null == storedGroupLink){
			throw new DAOException("Cannot add GroupAttribute to User's GroupLink - the object is out of date.");
		}
		
		GroupAttribute storedGroupAttribute = (GroupAttribute) getHibernateTemplate().get(GroupAttribute.class, groupAttributeId);
		if(null == storedGroupAttribute){
			throw new DAOException("Cannot add GroupAttribute to User's GroupLink - the object is out of date.");
		}
		storedGroupLink.addGroupAttribute(storedGroupAttribute); 
		
		getHibernateTemplate().saveOrUpdate(storedGroupLink);
		
	}

	public List<GroupAttribute> getGroupAttributesForGroup(Long groupId) throws DAOException {
		Group storedGroup = (Group) getHibernateTemplate().get(
				Group.class, groupId);
		if (null == storedGroup) {
			throw new DAOException(
					"Cannot add GroupAttributeToGroup - the object is out-of-date");
		}
		
		List<GroupAttribute> groupAttributes = storedGroup.getGroupAttributes();
		
		if(groupAttributes == null){
			groupAttributes = new ArrayList<GroupAttribute>();
		}
				
		return groupAttributes;
		
	}

}
