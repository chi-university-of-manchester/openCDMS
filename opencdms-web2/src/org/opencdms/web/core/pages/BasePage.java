/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.opencdms.web.core.pages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.opencdms.web.core.admin.AdminPage;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.modules.audit.pages.AuditLogPage;
import org.opencdms.web.modules.export.pages.ExportMain;
import org.opencdms.web.modules.export.pages.RequestExport;
import org.opencdms.web.modules.export.pages.ViewExports;
import org.opencdms.web.modules.groups.GroupAdminPage;
import org.opencdms.web.modules.imports.pages.ImportMain;
import org.opencdms.web.modules.imports.pages.RequestImport;
import org.opencdms.web.modules.imports.pages.ViewImports;
import org.opencdms.web.modules.meds.pages.MedsMain;
import org.opencdms.web.modules.query.pages.BuildQueryPage;
import org.opencdms.web.modules.query.pages.ExecuteQueryPage;
import org.opencdms.web.modules.query.pages.QueryMain;
import org.opencdms.web.modules.query.pages.ViewQueriesPage;
import org.opencdms.web.modules.register.pages.ArmByDetailsPage;
import org.opencdms.web.modules.register.pages.ArmByNumberPage;
import org.opencdms.web.modules.register.pages.DetailsByNumberPage;
import org.opencdms.web.modules.register.pages.NumberByDetailsPage;
import org.opencdms.web.modules.register.pages.RegisterHomePage;
import org.opencdms.web.modules.reports.pages.MgmtReport;
import org.opencdms.web.modules.reports.pages.RecordReport;
import org.opencdms.web.modules.reports.pages.ReportsMain;
import org.opencdms.web.modules.reports.pages.TrendReport;

/**
 * @author Rob Harper
 *
 */
public abstract class BasePage<T> extends WebPage implements IAjaxIndicatorAware {

	private static final Log LOG = LogFactory.getLog(BasePage.class);

	public BasePage(IModel<T> model) {
		super(model);
		initializePage();
		add(getContentPanel("contentPanel", model));
	}

	public BasePage(){
		super();
		initializePage();
		add(getContentPanel("contentPanel"));
	}

	private void initializePage(){
		try{
			if ( forcePasswordChange() ){
				throw new RestartResponseException(ChangePassword.class);
			}
		}
		catch(Exception ex){
			//can't find out if we need to force a password change or not
			LOG.error("Unable to find if a password change is required.", ex);
		}

		add(new Label("pageTitle", getPageTitle()));

		List<String> infoList = new ArrayList<String>();

		infoList.add("Username: " + getSession().getUser().getUsername());
		add(new ListView<String>("infolist", infoList) {
			private static final long serialVersionUID = 1L;
			protected void populateItem(ListItem<String> item) {
		        String text = item.getModelObject();
		        item.add(new Label("value", text));
		    }
		});

		List<MenuLink> menu1List = getLevel1Menu();
		add(new ListView<MenuLink>("menu1", menu1List){
			private static final long serialVersionUID = 1L;
			@Override
			protected void populateItem(ListItem<MenuLink> item) {
				MenuLink ml = item.getModelObject();
				if ( ml.isActive() ){
					item.add(new SimpleAttributeModifier("class", "selected"));
				}
				BookmarkablePageLink<Object> link = new BookmarkablePageLink<Object>("menu1link", ml.getLinkPage());
				link.add(new Label("menu1text", ml.getLinkText()));
				item.add(link);
			}
		});

		List<MenuLink> menu2List = getLevel2Menu();
		add(new ListView<MenuLink>("menu2", menu2List){
			private static final long serialVersionUID = 1L;
			@Override
			protected void populateItem(ListItem<MenuLink> item) {
				MenuLink ml = item.getModelObject();
				BookmarkablePageLink<Object> link = new BookmarkablePageLink<Object>("menu2link", ml.getLinkPage());
				link.add(new Label("menu2text", ml.getLinkText()));
				item.add(link);
			}
		});
	}

	public abstract String getPageTitle();

	public abstract Panel getContentPanel(String id);

	public Panel getContentPanel(String id, IModel<T> model){
		//default implementation - ignore model. Override when model needs
		//to be considered
		return getContentPanel(id);
	}

	public abstract String getPageGroup();

	@Override
	public OpenCdmsWebSession getSession(){
		return (OpenCdmsWebSession)super.getSession();
	}

	private List<MenuLink> getLevel1Menu(){
		OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
		Roles roles = session.getRoles();
		List<MenuLink> menu1List = new ArrayList<MenuLink>();
		MenuLink homeMenu = new MenuLink(Index.class, "Home");
		menu1List.add(homeMenu);
		if ( "Home".equals(getPageGroup()) ){
			homeMenu.setActive(true);
		}
		if ( roles.contains("ROLE_EXPORT")){
			MenuLink exportMenu = new MenuLink(ExportMain.class, "Export");
			menu1List.add(exportMenu);
			if ( "Export".equals(getPageGroup()) ){
				exportMenu.setActive(true);
			}
		}

		if (roles.contains("ROLE_EXPORT")) {
			MenuLink medsMenu = new MenuLink(MedsMain.class, "Meds");
			menu1List.add(medsMenu);
			if ( "Meds".equals(getPageGroup()) ){
				medsMenu.setActive(true);
			}
		}

		if ( roles.contains("ROLE_IMPORT")){
			MenuLink importMenu = new MenuLink(ImportMain.class, "Import");
			menu1List.add(importMenu);
			if ( "Import".equals(getPageGroup()) ){
				importMenu.setActive(true);
			}
		}
		if ( roles.contains("ROLE_REPORTS")){
			MenuLink reportsMenu = new MenuLink(ReportsMain.class, "Reports");
			menu1List.add(reportsMenu);
			if ( "Reports".equals(getPageGroup()) ){
				reportsMenu.setActive(true);
			}
		}
		if ( roles.contains("ROLE_ESLWEB")){
			MenuLink registerMenu = new MenuLink(RegisterHomePage.class, "Register");
			menu1List.add(registerMenu);
			if ( "Register".equals(getPageGroup()) ){
				registerMenu.setActive(true);
			}
		}
		if ( roles.contains("ROLE_AUDIT")){
			MenuLink auditMenu = new MenuLink(AuditLogPage.class, "Audit");
			menu1List.add(auditMenu);
			if ( "Audit".equals(getPageGroup()) ){
				auditMenu.setActive(true);
			}
		}
		if ( roles.contains("ROLE_QUERY")){
			MenuLink queryMenu = new MenuLink(QueryMain.class, "Query");
			menu1List.add(queryMenu);
			if ( "Query".equals(getPageGroup()) ){
				queryMenu.setActive(true);
			}
		}

		// Allow the user to access the admin page if they can patch a dataset.
		if ( roles.contains("ROLE_PATCH_DATASET")){
			MenuLink adminMenu = new MenuLink(AdminPage.class, "Admin");
			menu1List.add(adminMenu);
			if (getPageGroup().equals("Admin")){
				adminMenu.setActive(true);
			}
		}
		menu1List.add(new MenuLink(SignOut.class, "Logout"));

		return menu1List;
	}

	private List<MenuLink> getLevel2Menu(){
		OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
		Roles roles = session.getRoles();
		List<MenuLink> menu2List = new ArrayList<MenuLink>();

		String pageGroup = getPageGroup();
		if ( "Home".equals(pageGroup)){
			menu2List.add(new MenuLink(ChangePassword.class, "Change Password"));
		}

		if ( "Export".equals(pageGroup)){
			menu2List.add(new MenuLink(RequestExport.class, "Request Export"));
			menu2List.add(new MenuLink(ViewExports.class, "View Exports"));
		}
		if ( "Import".equals(pageGroup)){
			menu2List.add(new MenuLink(RequestImport.class, "Request Import"));
			menu2List.add(new MenuLink(ViewImports.class, "View Imports"));
		}

		if ( "Register".equals(pageGroup) ){
			if ( roles.contains("ROLE_ESL_VIEW_BY_NUMBER")){
				menu2List.add(new MenuLink(DetailsByNumberPage.class, "Details by Number"));
			}
			if ( roles.contains("ROLE_ESL_VIEW_BY_DETAILS")){
				menu2List.add(new MenuLink(NumberByDetailsPage.class, "Number by Details"));
			}
			if ( roles.contains("ROLE_ESL_TREAT_ARM")){
				menu2List.add(new MenuLink(ArmByNumberPage.class, "Arm by Number"));
				menu2List.add(new MenuLink(ArmByDetailsPage.class, "Arm by Details"));
			}
			if ( roles.contains("ROLE_ESL_STATS")){
				menu2List.add(new MenuLink(DetailsByNumberPage.class, "Statistics"));
			}
			if ( roles.contains("ROLE_ESL_BREAK_IN")){
				menu2List.add(new MenuLink(DetailsByNumberPage.class, "Break-In"));
			}
		}

		if ( "Query".equals(pageGroup) ){
			menu2List.add(new MenuLink(BuildQueryPage.class, "Build"));
			menu2List.add(new MenuLink(ViewQueriesPage.class, "View"));
			menu2List.add(new MenuLink(ExecuteQueryPage.class, "Execute"));
		}

		if ( "Reports".equals(pageGroup) ){
			menu2List.add(new MenuLink(RecordReport.class, "Record"));
			if ( roles.contains("ROLE_TRENDS_REPORTS")){
				menu2List.add(new MenuLink(TrendReport.class, "Trend"));
			}
			menu2List.add(new MenuLink(MgmtReport.class, "Management"));
		}

		if ( pageGroup.equals("Admin")){
			if ( roles.contains("ROLE_PATCH_DATASET")){
				menu2List.add(new MenuLink(GroupAdminPage.class, "Centres"));
			}
		}

		if( "Meds".equals(pageGroup)) {

		}

		return menu2List;
	}

	public static class MenuLink implements Serializable {

		private static final long serialVersionUID = 1L;

		private final Class<? extends WebPage> linkPage;
		private final String linkText;
		private boolean active;
		public MenuLink(Class<? extends WebPage> linkPage, String linkText) {
			super();
			this.linkPage = linkPage;
			this.linkText = linkText;
			this.active = false;
		}
		public MenuLink(Class<? extends WebPage> linkPage, String linkText, boolean active) {
			super();
			this.linkPage = linkPage;
			this.linkText = linkText;
			this.active = active;
		}
		public Class<? extends WebPage> getLinkPage() {
			return linkPage;
		}
		public String getLinkText() {
			return linkText;
		}
		public boolean isActive() {
			return active;
		}
		public void setActive(boolean active) {
			this.active = active;
		}
	}

	public String getAjaxIndicatorMarkupId() {
		return "busy-panel";
	}

	protected boolean forcePasswordChange(){
		return getSession().isForcePasswordChange();
	}
}
