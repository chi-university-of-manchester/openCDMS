package org.opencdms.web.modules.meds.pages;

import org.apache.wicket.markup.html.panel.Panel;
import org.opencdms.web.core.pages.BasePage;
import org.opencdms.web.modules.meds.panels.VerifyPanel;

public class VerifyMeds extends BasePage<Void> {

	@Override
	public String getPageTitle() {
		return "openCDMS | Meds | Verify Medication Package";
	}

	@Override
	public Panel getContentPanel(String id) {
		return new VerifyPanel(id);
	}

	@Override
	public String getPageGroup() {
		return "Meds";
	}

}
