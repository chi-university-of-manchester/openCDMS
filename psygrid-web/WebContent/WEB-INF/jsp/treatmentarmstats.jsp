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
		<title>Participant Register - Allocation statistics</title>
	</head>
	<body>
		<h1>Allocation statistics</h1>
		<h2>Overall allocation statistics</h2>
 		<table class="esl">
			<tr>
	  			<th>Treatment arm</th>
	  			<th>No. allocations</th>
			</tr>
			<c:forEach var="entry" items="${stats.overallStats}">
				<tr>  
		  			<td>${entry.key}</td>
					<td>${entry.value}</td>
				</tr>
			</c:forEach>
 		</table>
 		
 		<h2>Allocation statistics by strata</h2>
 		<c:forEach var="strataStats" items="${stats.strataStats}">
			<h3>${strataStats.strata}</h3>
	  		<table class="esl">
				<tr>
		  			<th>Treatment arm</th>
		  			<th>No. allocations</th>
				</tr>
				<c:forEach var="treatments" items="${strataStats.treatments}">
					<tr>  
			  			<td>${treatments.key}</td>
						<td>${treatments.value}</td>
					</tr>
				</c:forEach>
	  		</table>
 		</c:forEach>

	</body>
</html>
	