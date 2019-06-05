package org.opencdms.web.modules.meds.pages;

import org.apache.wicket.markup.html.panel.Panel;
import org.opencdms.web.core.pages.BasePage;
import org.opencdms.web.modules.meds.panels.MedsMainPanel;

public class MedsMain extends BasePage<Void> {

	@Override
	public Panel getContentPanel(String id) {
		// TODO Auto-generated method stub
		return new MedsMainPanel(id);
	}

	@Override
	public String getPageGroup() {
		// TODO Auto-generated method stub
		return "Meds";
	}

	@Override
	public String getPageTitle() {
		return "openCDMS | Meds | Home";
	}

}
