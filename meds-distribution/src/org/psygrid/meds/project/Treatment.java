package org.psygrid.meds.project;

/**
 * Holds info about a treatment 
 * @author Bill
 *@hibernate.class table="t_meds_treatment"
 */
public class Treatment  {

	private Long id; //hibernate id
	
	private String treatmentCode;
	private String treatmentName;
	
	protected Treatment(){
		
	}
	
	public Treatment(String treatmentName, String treatmentCode){
		this.setTreatmentCode(treatmentCode);
		this.setTreatmentName(treatmentName);
	}

	public void setTreatmentCode(String treatmentCode) {
		this.treatmentCode = treatmentCode;
	}

	/**
	 * 
	 * @return
	 * @hibernate.property column="c_code"
	 * 								not-null="true"
	 */
	public String getTreatmentCode() {
		return treatmentCode;
	}

	public void setTreatmentName(String treatmentName) {
		this.treatmentName = treatmentName;
	}

	/**
	 * 
	 * @return
	 * @hibernate.property column="c_name"
	 * 									not-null="true"
	 */
	public String getTreatmentName() {
		return treatmentName;
	}


	protected void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the id
	 * @hibernate.id column = "c_id" generator-class="native"
	 */
	public Long getId() {
		return id;
	}
		
}
