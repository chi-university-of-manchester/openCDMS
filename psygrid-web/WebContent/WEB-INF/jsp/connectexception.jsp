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
		<title>Unable to connect</title>
	</head>
	<body>
		<h1>Unable to connect</h1>
		<p>It was not possible to connect to the PsyGrid central services.</p>
		<p>Please try the operation again. If this problem persists please 
		contact PsyGrid support
		<a href="mailto:support@psygrid.org">support@psygrid.org</a>.</p>
	</body>
</html>
