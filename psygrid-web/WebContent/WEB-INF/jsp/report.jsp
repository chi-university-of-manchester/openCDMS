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
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<html>
	<head>
		<c:import url="authorization.jsp" />
		<meta name="nav.reports.sub" content="${reportssub}" />
		<meta name="nav.reports.trends" content="${trendsReportsPrivilege}" />

		<title>Reports</title>
	</head>
	<body>
		<h1>Reports</h1>
		<p>This page will give web-based access to reports.</p>
		
		<p>Please select the report type you would like to view.</p>
		<ul>
			<li><a href="reports/recordreport.html">Individual Record</a></li>
			<c:if test="${trendsReportsPrivilege eq 'yes'}">
			<li><a href="reports/trendsreport.html">Show Trends</a></li>
			</c:if>
			<li><a href="reports/managementreport.html">Study Management</a></li>
		</ul>
	</body>
</html>
