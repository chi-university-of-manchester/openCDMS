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
		<title>Participant Register - Emergency break-in</title>
	</head>
	<body>
		<h1>Emergency break-in</h1>
		<table class="esl">
			<tr>
				<th>Participant Identifier</th>
				<th>Treatment arm</th>
			</tr>
			<c:forEach var="entry" items="${breakInMap}">
     			<tr>  
					<td>${entry.key}</td>
					<td>${entry.value}</td>
				</tr>
			</c:forEach>
		</table>					
	</body>
</html>
