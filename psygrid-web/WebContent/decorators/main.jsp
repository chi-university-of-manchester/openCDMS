<%--
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
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
	
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>openCDMS - <decorator:title default="Clinical Portal" /></title>
        <link rel="stylesheet" type="text/css" href="/psygrid/styles/default.css" />
        <link rel="shortcut icon" type="image/x-icon" href="/psygrid/images/favicon.ico" />
      
        <decorator:head />
    </head>
    <body>
		<decorator:usePage id="thePage" />
    	<div id="main">
	    	<div id="banner">
		    	<div id="info">
		    		<ul>
		    			<li>Username: <decorator:getProperty property="meta.user.name" /></li>
		    			<% if ("yes".equals(thePage.getProperty("meta.nav.eslweb.sub"))) { %>
		    				<li>Project: <decorator:getProperty property="meta.user.project" /></li>
		    				<li>Centre: <decorator:getProperty property="meta.user.group" /></li>
		    			<% } %>
	    			</ul>
		    	</div>
	    	</div>
	    	<div id="container">
    			<div id="navigation">
	                <ul>
	                    <li><a href="/psygrid/secure/index.html">Home</a></li>
						<% if ("yes".equals(thePage.getProperty("meta.nav.export"))) { %>
						<li><a href="/psygrid/secure/export/index.html">Data export</a></li>
						<% } %>
						<% if ("yes".equals(thePage.getProperty("meta.nav.audit"))) { %>
						<li><a href="/psygrid/secure/audit/index.html">Audit Log</a></li>
						<% } %>
						<% if ("yes".equals(thePage.getProperty("meta.nav.reports"))) { %>
	                    <li><a href="/psygrid/secure/report.html">Reports</a>
	                   		 <% if ("yes".equals(thePage.getProperty("meta.nav.reports.sub"))) { %>
	                    	<ul>
	                    		<li><a href="/psygrid/secure/reports/recordreport.html">Individual Record</a></li>
	                    		 <% if ("yes".equals(thePage.getProperty("meta.nav.reports.trends"))) { %>
		                    		<li><a href="/psygrid/secure/reports/trendsreport.html">Show Trends</a></li>
	                    		<% } %>
	                    		<li><a href="/psygrid/secure/reports/managementreport.html">Study Management</a></li>
	                    	</ul>
	                    	<% } %>
	                    </li>
	                    <% } %>
						<% if ("yes".equals(thePage.getProperty("meta.nav.eslweb"))) { %>
	                    <li><a href="/psygrid/secure/eslweb/index.html">Participant Register</a>
							<% if ("yes".equals(thePage.getProperty("meta.nav.eslweb.sub"))) { %>
	                    	<ul>
	                    		<li><a href="/psygrid/secure/eslweb/index.html">Select study</a></li>
	                    		<% if ( "yes".equals(thePage.getProperty("meta.nav.eslWebViewByDetails"))) { %>
	                    			<li><a href="/psygrid/secure/eslweb/studyCodeByPersonalDetails.html">Participant Identifier by details</a></li>
                    			<% } %>
	                    		<% if ( "yes".equals(thePage.getProperty("meta.nav.eslWebViewByNumber"))) { %>
	                    			<li><a href="/psygrid/secure/eslweb/personalDetailsByStudyCode.html">Details by Participant Identifier</a></li>
                    			<% } %>
	                    		<% if ( "yes".equals(thePage.getProperty("meta.nav.eslWebRndResult"))) { %>
	                    			<li><a href="/psygrid/secure/eslweb/treatmentArmByStudyCode.html">Treatment arm by number</a></li>
                    			<% } %>
	                    		<% if ( "yes".equals(thePage.getProperty("meta.nav.eslWebRndResult"))) { %>
	                    			<li><a href="/psygrid/secure/eslweb/treatmentArmByPersonalDetails.html">Treatment arm by details</a></li>
                    			<% } %>
	                    		<% if ( "yes".equals(thePage.getProperty("meta.nav.eslWebBreakIn"))) { %>
	                    			<li><a href="/psygrid/secure/eslweb/emergencyBreakIn.html">Emergency break-in</a></li>
                    			<% } %>
	                    		<% if ( "yes".equals(thePage.getProperty("meta.nav.eslWebStats"))) { %>
	                    			<li><a href="/psygrid/secure/eslweb/treatmentArmStatistics.html">Treatment arm statistics</a></li>
                    			<% } %>
	                    	</ul>
	                    	<% } %>
	                    </li>
	                    <% } %>
	                    <li><a href="/psygrid/secure/changepwd.html">Change Password</a></li>
	                    <li><a href="/psygrid/j_acegi_logout">Logout</a></li>
	                </ul>
    			</div>
    			<div id="content">
    				<decorator:body />
    			</div>
    			<div id="cleardiv"></div>
	    	</div>
	    	<div id="footer">
	    		<div id="help">
	    			<p><a target="_blank" href="http://www.psygrid.org/psygrid/documents/help/ch06.html">Help</a></p>
	    		</div>
	    		<div id="copyright">
		    		<ul>
		    			<li>&copy; Copyright 2008 University of Manchester</li>
		    		</ul>
		    	</div>
	    	</div>
    	</div>
    </body>
</html>
