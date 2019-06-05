package org.opencdms.web.modules.meds.pages;

import org.apache.wicket.markup.html.panel.Panel;
import org.opencdms.web.core.pages.BasePage;
import org.opencdms.web.modules.meds.panels.DistributeMedsPanel;

public class DistributeMeds extends BasePage<Void> {

	@Override
	public Panel getContentPanel(String id) {
		// TODO Auto-generated method stub
		return new DistributeMedsPanel(id, "pil/nana");
	}

	@Override
	public String getPageGroup() {
		// TODO Auto-generated method stub
		return "Meds";
	}

	@Override
	public String getPageTitle() {
		// TODO Auto-generated method stub
		return "openCDMS | Meds | Distribute Medication Package";
	}
	


}
