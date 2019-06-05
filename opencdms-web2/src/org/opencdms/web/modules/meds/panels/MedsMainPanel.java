package org.opencdms.web.modules.meds.panels;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.opencdms.web.modules.export.pages.RequestExport;
import org.opencdms.web.modules.export.pages.ViewExports;
import org.opencdms.web.modules.meds.pages.AllocateMeds;
import org.opencdms.web.modules.meds.pages.DistributeMeds;
import org.opencdms.web.modules.meds.pages.VerifyMeds;

public class MedsMainPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param id
	 */
	public MedsMainPanel(String id) {
		super(id);
		add(new BookmarkablePageLink<Object>("allocateMeds", AllocateMeds.class));
		add(new BookmarkablePageLink<Object>("distributeMeds", DistributeMeds.class));
		add(new BookmarkablePageLink<Object>("verifyPackages",  VerifyMeds.class));

	}

}
