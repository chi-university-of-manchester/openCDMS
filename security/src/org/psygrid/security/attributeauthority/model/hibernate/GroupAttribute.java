package org.psygrid.security.attributeauthority.model.hibernate;

import org.psygrid.www.xml.security.core.types.GroupAttributeType;


/**
 * @author wrv
 *
 * @hibernate.joined-subclass table="t_group_attributes"
 * @hibernate.joined-subclass-key column="c_id"
 */
public class GroupAttribute extends Persistent {

	private String attributeName;
	private String detail1;
	private String detail2;
	private String detail3;
	private String detail4;
	
	protected GroupAttribute(){};
	
	public GroupAttribute(String attributeName){
		this.attributeName = attributeName;
	}
	
	/**
     * Get the groupName
     * 
     * @return The groupName.
     * @hibernate.property column = "c_attribute_name"
     */
	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	
	/**
     * Get the groupName
     * 
     * @return The groupName.
     * @hibernate.property column = "c_detail1"
     */
	public String getDetail1() {
		return detail1;
	}

	public void setDetail1(String detail1) {
		this.detail1 = detail1;
	}

	
	/**
     * Get the groupName
     * 
     * @return The groupName.
     * @hibernate.property column = "c_detail2"
     */
	public String getDetail2() {
		return detail2;
	}

	public void setDetail2(String detail2) {
		this.detail2 = detail2;
	}

	
	/**
     * Get the groupName
     * 
     * @return The groupName.
     * @hibernate.property column = "c_detail3"
     */
	public String getDetail3() {
		return detail3;
	}

	public void setDetail3(String detail3) {
		this.detail3 = detail3;
	}

	
	/**
     * Get the groupName
     * 
     * @return The groupName.
     * @hibernate.property column = "c_detail4"
     */
	public String getDetail4() {
		return detail4;
	}

	public void setDetail4(String detail4) {
		this.detail4 = detail4;
	}

	public GroupAttributeType toGroupAttributeType(){
		GroupAttributeType gAT = new GroupAttributeType(attributeName, detail1, detail2, detail3, detail4);
		return gAT;
	}
	
	public static GroupAttribute fromGroupAttributeType(GroupAttributeType groupAttributeType){
		GroupAttribute gA = new GroupAttribute();
		gA.setAttributeName(groupAttributeType.getName());
		gA.setDetail1(groupAttributeType.getDetail1());
		gA.setDetail2(groupAttributeType.getDetail2());
		gA.setDetail3(groupAttributeType.getDetail3());
		gA.setDetail4(groupAttributeType.getDetail4());
		
		return gA;
	}
	
}
