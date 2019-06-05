package org.psygrid.security.attributeauthority.model.hibernate;

/**
 * @author wrv
 *
 * @hibernate.joined-subclass table="t_groupattribute_links"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class GroupAttributeLink extends Persistent {

	private GroupAttribute groupAttribute;
	
	protected GroupAttributeLink(){};
	
	public GroupAttributeLink(GroupAttribute gA){
		this.groupAttribute = gA;
	}

	
	/**
     * Get the group
     * 
     * @return A list containing the group.
     * @hibernate.many-to-one class="org.psygrid.security.attributeauthority.model.hibernate.GroupAttribute"
     *                        column="c_groupattribute_id"
     *                        not-null="true"
     *                        cascade="none"
     */
	public GroupAttribute getGroupAttribute() {
		return groupAttribute;
	}
	
	public void setGroupAttribute(GroupAttribute g){
		this.groupAttribute = g;
	}
	
}
