package org.psygrid.meds.medications;

import java.io.Serializable;

public class MedicationPackageId  implements Serializable{

	   /**
	 * 
	 */
	private static final long serialVersionUID = -5021652358734341950L;


	private String packageId;
	private String projectCode;
	
	protected MedicationPackageId(){
		
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}


	public String getPackageId() {
		return packageId;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}


	public String getProjectCode() {
		return projectCode;
	}
	
	   public boolean equals( Object o ) {
		   return true;
	}

	   public int hashCode() {
			   return 0;
	}
	
}
