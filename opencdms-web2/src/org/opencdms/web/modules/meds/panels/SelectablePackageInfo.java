package org.opencdms.web.modules.meds.panels;

import java.io.Serializable;

import org.psygrid.meds.medications.PackageInfo;

public class SelectablePackageInfo implements Serializable{
	
	private final PackageInfo pInfo;
	private Boolean selected = Boolean.FALSE;
	
	public SelectablePackageInfo(PackageInfo i){
		pInfo = i;
	}

	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public PackageInfo getpInfo() {
		return pInfo;
	}

}
