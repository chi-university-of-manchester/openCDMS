package org.psygrid.data.query;

import java.util.Date;

import org.psygrid.data.model.hibernate.Option;

/**
 * Class to hold the value of a query statement. This is used when passing the
 * value across between web client and server.
 * Note: This is not an ideal solution but is much better than what was being 
 * done before and avoided a complete re-write of the client-side code
 * @author MattMachin
 *
 */
public class QueryStatementValue {
	private String textValue;
	private Double doubleValue;
	private Integer integerValue;
	private Option optionValue;
	private Date dateValue;
	
	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}

	public Option getOptionValue() {
		return optionValue;
	}

	public void setOptionValue(Option optionValue) {
		this.optionValue = optionValue;
	}
	
	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public Integer getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}
}
