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
<html>
	<head>
		<c:import url="authorization.jsp" />
		<title>Data Export</title>
	</head>
	<body>
		<h1>Data Export</h1>
		<p>Provides functionality to export raw data from the openCDMS
		database.</p>
		<p>Exports must be <a href="request.html">requested</a>. These
		requests are then serviced by the openCDMS system and you will be notified
		by email when the request has been completed, at which point it
		will be possible to download the data.</p>
		<p>You may also <a href="status.html">review the status</a> of
		your outstanding export requests.</p>
		<ul>
			<li><a href="request.html">Request export</a></li>
			<li><a href="status.html">View export requests</a></li>
		</ul>
	</body>
</html>
