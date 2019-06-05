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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
	<head>
		<c:import url="authorization.jsp" />
		<title>Home</title>
	</head>
	<body>
		<h1>Welcome to the openCDMS Clinical Portal</h1>
		<p>Please select one of the options from the menu on the
		left-hand side of the page.</p>
		<p>If you encounter any problems please contact 
		<a href="mailto:support@psygrid.org">support@psygrid.org</a>.</p>
		<h2>Login History</h2>
		<p>The following list shows the most recent accesses to the system using your username. If you notice
		any inconsistencies please contact <a href="mailto:support@psygrid.org">support@psygrid.org</a>.</p>
		<table>
			<tr>
				<th>Date</th>
				<th>Source address</th>
				<th>Authenticated?</th>
			</tr>
			<c:forEach items="${history}" var="hist">
				<tr>
					<c:forEach items="${hist}" var="item">
						<td>${item}</td>
					</c:forEach>
				</tr>
			</c:forEach>
		</table>
	</body>
</html>
