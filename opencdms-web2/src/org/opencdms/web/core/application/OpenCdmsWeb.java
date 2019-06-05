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

package org.opencdms.web.core.application;

import java.util.Date;

import net.ftlines.wicketsource.WicketSource;

import org.acegisecurity.AuthenticationManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;
import org.apache.wicket.settings.IExceptionSettings;
import org.opencdms.web.core.admin.AdminPage;
import org.opencdms.web.core.pages.AccessDeniedPage;
import org.opencdms.web.core.pages.ChangePassword;
import org.opencdms.web.core.pages.Index;
import org.opencdms.web.core.pages.InternalErrorPage;
import org.opencdms.web.core.pages.PageExpiredPage;
import org.opencdms.web.core.pages.PageNotFoundPage;
import org.opencdms.web.core.pages.ResetPassword;
import org.opencdms.web.core.pages.SignIn;
import org.opencdms.web.core.pages.SignOut;
import org.opencdms.web.modules.audit.pages.AuditLogPage;
import org.opencdms.web.modules.export.pages.ExportMain;
import org.opencdms.web.modules.export.pages.RequestExport;
import org.opencdms.web.modules.export.pages.ViewExports;
import org.opencdms.web.modules.groups.GroupAdminPage;
import org.opencdms.web.modules.imports.pages.ImportMain;
import org.opencdms.web.modules.meds.pages.MedsMain;
import org.opencdms.web.modules.query.pages.BuildQueryPage;
import org.opencdms.web.modules.query.pages.ExecuteQueryPage;
import org.opencdms.web.modules.query.pages.QueryMain;
import org.opencdms.web.modules.query.pages.ViewQueriesPage;
import org.opencdms.web.modules.register.pages.ArmByDetailsPage;
import org.opencdms.web.modules.register.pages.ArmByNumberPage;
import org.opencdms.web.modules.register.pages.BreakInPage;
import org.opencdms.web.modules.register.pages.DetailsByNumberPage;
import org.opencdms.web.modules.register.pages.NumberByDetailsPage;
import org.opencdms.web.modules.register.pages.RegisterHomePage;
import org.opencdms.web.modules.register.pages.StatisticsPage;
import org.opencdms.web.modules.reports.pages.MgmtReport;
import org.opencdms.web.modules.reports.pages.RecordReport;
import org.opencdms.web.modules.reports.pages.ReportsMain;
import org.opencdms.web.modules.reports.pages.TrendReport;
import org.psygrid.data.repository.RepositoryServiceInternal;
import org.psygrid.esl.services.EslServiceInternal;
import org.psygrid.security.attributeauthority.AttributeAuthorityService;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author Rob Harper
 *
 */
public class OpenCdmsWeb extends AuthenticatedWebApplication {

	private static final Log LOG = LogFactory.getLog(OpenCdmsWeb.class);

	private AuthenticationManager authenticationManager;
    
    private String uploadPath = null;

    /**
     * Injected.
     */
	AAQueryClient aaqc = null;

	/*
	 * Services are configured using their own application contexts.
	 * @see {@link #init()}
	 */
	
	AttributeAuthorityService attributeAuthorityService = null;
	
	RepositoryServiceInternal repositoryService = null;

	EslServiceInternal eslService = null;
	

	/**
	 * Mail sender bean.
	 * Injected.
	 */
	private JavaMailSender mailSender;

    
    /* (non-Javadoc)
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends Page> getHomePage() {
		return Index.class;
	}

	@Override
	protected void init() {
		super.init();
		getMarkupSettings().setStripWicketTags(true);

		getSecuritySettings().setAuthorizationStrategy(new MetaDataRoleAuthorizationStrategy(this));
        MetaDataRoleAuthorizationStrategy.authorize(Index.class, "ROLE_USER");
        MetaDataRoleAuthorizationStrategy.authorize(ChangePassword.class, "ROLE_USER");
        MetaDataRoleAuthorizationStrategy.authorize(ExportMain.class, "ROLE_EXPORT");
        MetaDataRoleAuthorizationStrategy.authorize(MedsMain.class, "ROLE_EXPORT");
        MetaDataRoleAuthorizationStrategy.authorize(RequestExport.class, "ROLE_EXPORT");
        MetaDataRoleAuthorizationStrategy.authorize(ViewExports.class, "ROLE_EXPORT");
        MetaDataRoleAuthorizationStrategy.authorize(ImportMain.class, "ROLE_IMPORT");
        MetaDataRoleAuthorizationStrategy.authorize(ArmByDetailsPage.class, "ROLE_ESL_TREAT_ARM");
        MetaDataRoleAuthorizationStrategy.authorize(ArmByNumberPage.class, "ROLE_ESL_TREAT_ARM");
        MetaDataRoleAuthorizationStrategy.authorize(BreakInPage.class, "ROLE_ESL_BREAK_IN");
        MetaDataRoleAuthorizationStrategy.authorize(DetailsByNumberPage.class, "ROLE_ESL_VIEW_BY_NUMBER");
        MetaDataRoleAuthorizationStrategy.authorize(NumberByDetailsPage.class, "ROLE_ESL_VIEW_BY_DETAILS");
        MetaDataRoleAuthorizationStrategy.authorize(RegisterHomePage.class, "ROLE_ESLWEB");
        MetaDataRoleAuthorizationStrategy.authorize(StatisticsPage.class, "ROLE_ESL_STATS");
        MetaDataRoleAuthorizationStrategy.authorize(QueryMain.class, "ROLE_QUERY");
        MetaDataRoleAuthorizationStrategy.authorize(BuildQueryPage.class, "ROLE_QUERY");
        MetaDataRoleAuthorizationStrategy.authorize(ViewQueriesPage.class, "ROLE_QUERY");
        MetaDataRoleAuthorizationStrategy.authorize(ExecuteQueryPage.class, "ROLE_QUERY");
        MetaDataRoleAuthorizationStrategy.authorize(ReportsMain.class, "ROLE_REPORTS");
        MetaDataRoleAuthorizationStrategy.authorize(RecordReport.class, "ROLE_REPORTS");
        MetaDataRoleAuthorizationStrategy.authorize(TrendReport.class, "ROLE_TRENDS_REPORTS");
        MetaDataRoleAuthorizationStrategy.authorize(MgmtReport.class, "ROLE_REPORTS");
        MetaDataRoleAuthorizationStrategy.authorize(AuditLogPage.class, "ROLE_AUDIT");
        MetaDataRoleAuthorizationStrategy.authorize(AdminPage.class, "ROLE_PATCH_DATASET");
        MetaDataRoleAuthorizationStrategy.authorize(GroupAdminPage.class, "ROLE_PATCH_DATASET");
        
        getApplicationSettings().setPageExpiredErrorPage(PageExpiredPage.class);
        getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
        getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);
        
        //comment this line to get detailed error output
        getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);
        
        mount(new HybridUrlCodingStrategy("/404", PageNotFoundPage.class));
        mount(new HybridUrlCodingStrategy("/500", InternalErrorPage.class));
        
        mountBookmarkablePage("/403", AccessDeniedPage.class);
        mountBookmarkablePage("/login", SignIn.class);
        mountBookmarkablePage("/logout", SignOut.class);
        mountBookmarkablePage("/expired", PageExpiredPage.class);
        mountBookmarkablePage("/changepassword", ChangePassword.class);
        mountBookmarkablePage("/resetpassword", ResetPassword.class);
        mountBookmarkablePage("/export", ExportMain.class);
        mountBookmarkablePage("/export/request", RequestExport.class);
        mountBookmarkablePage("/export/view", ViewExports.class);
        mountBookmarkablePage("/import", ImportMain.class);
       // mountBookmarkablePage("/testupload", UploadPage.class);
        mountBookmarkablePage("/register/treatment/bydetails", ArmByDetailsPage.class);
        mountBookmarkablePage("/register/treatment/bynumber", ArmByNumberPage.class);
        mountBookmarkablePage("/register/treatment/breakin", BreakInPage.class);
        mountBookmarkablePage("/register/details/bynumber", DetailsByNumberPage.class);
        mountBookmarkablePage("/register/number/bydetails", NumberByDetailsPage.class);
        mountBookmarkablePage("/register", RegisterHomePage.class);
        mountBookmarkablePage("/register/treatment/statistics", StatisticsPage.class);
        mountBookmarkablePage("/query", QueryMain.class);
        mountBookmarkablePage("/query/build", BuildQueryPage.class);
        mountBookmarkablePage("/query/view", ViewQueriesPage.class);
        mountBookmarkablePage("/query/execute", ExecuteQueryPage.class);
        mountBookmarkablePage("/reports", ReportsMain.class);
        mountBookmarkablePage("/reports/record", RecordReport.class);
        mountBookmarkablePage("/reports/trend", TrendReport.class);
        mountBookmarkablePage("/reports/management", MgmtReport.class);
        mountBookmarkablePage("/audit", AuditLogPage.class);
        mountBookmarkablePage("/meds", MedsMain.class);
        mountBookmarkablePage("/admin", AdminPage.class);
        mountBookmarkablePage("/admin/centres", GroupAdminPage.class);
   
        // Load the application context from the attribute authority then get the service it defines.
        org.springframework.context.ApplicationContext aacontext = new ClassPathXmlApplicationContext("aa/applicationContext.xml");
        attributeAuthorityService = (AttributeAuthorityService)aacontext.getBean("attributeAuthorityService");

        // Load the application context from the repository then get the service it defines.
        org.springframework.context.ApplicationContext repositoryContext = new ClassPathXmlApplicationContext("repos/servicesContext.xml");
        repositoryService = (RepositoryServiceInternal)repositoryContext.getBean("repositoryService");

        // Load the application context from the esl then get the service it defines.
        org.springframework.context.ApplicationContext eslContext = new ClassPathXmlApplicationContext("esl/servicesContext.xml");
        eslService = (EslServiceInternal)eslContext.getBean("eslService");

        // Wicket-Source is used in development mode to allow easy navigation back to source code.
        // See - https://www.42lines.net/2012/01/31/announcing-wicket-source/
        // This code only gets run when wicket is in development mode:
        // See the web.xml 'configuration' context parameter.
        if(getConfigurationType().equals(Application.DEVELOPMENT)){
        	WicketSource.configure(this);
        }
	}

	public static OpenCdmsWeb get(){
		return (OpenCdmsWeb)Application.get();
	}
	
	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return SignIn.class;
	}

	@Override
	protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
		return OpenCdmsWebSession.class;
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	protected IRequestCycleProcessor newRequestCycleProcessor() {
		return new OpenCdmsWebRequestCycleProcessor();
	}
	
	public void setUploadPath(String uploadPath){
		this.uploadPath = uploadPath;
	}
	
	public String getUploadPath(){
		return uploadPath;
	}

	/**
	 * @return the aaqc
	 */
	public AAQueryClient getAaqc() {
		return aaqc;
	}

	/**
	 * @param aaqc the aaqc to set
	 */
	public void setAaqc(AAQueryClient aaqc) {
		this.aaqc = aaqc;
	}
	
	/**
	 * @param mailSender the mailSender to set
	 */
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	/**
	 * Returns a service with a direct link the attribute authority database.
	 * @return the attributeAuthorityService
	 */
	public AttributeAuthorityService getAttributeAuthorityService() {
		return attributeAuthorityService;
	}

	/**
	 * @return the repositoryService
	 */
	public RepositoryServiceInternal getRepositoryService() {
		return repositoryService;
	}

	/**
	 * @return the eslService
	 */
	public EslServiceInternal getEslService() {
		return eslService;
	}
	
	/**
	 * Convenience method for sending an email.
	 * 
	 * @param from - the from address
	 * @param subject - the subject
	 * @param body - the body
	 * @param recipients - comma separated list of email addresses
	 */
    public void sendEmail(String from, String subject,String body,String recipients) throws MailException {

		SimpleMailMessage message = new SimpleMailMessage();
		String[] addresses = recipients.split(",");
		message.setTo(addresses);
		message.setFrom(from);
		message.setSentDate(new Date());
		message.setSubject(subject);
		message.setText(body);
		try{
			mailSender.send(message);
			LOG.info("Email: To='"+recipients+"' Subject='"+message.getSubject()+"'");
		}
		catch(MailException ex){
			LOG.error("Exception from mailSender when sending email: subject='"+subject+"' recipient='"+recipients+"'", ex);
			throw ex;
		}
	}

}
