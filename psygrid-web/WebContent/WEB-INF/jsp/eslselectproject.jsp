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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
	<head>
		<c:import url="authorization.jsp" />
		<title>Participant Register - Select Study and Centre</title>
        <link rel="stylesheet" type="text/css" href="/psygrid/styles/eslforms.css" />
	</head>
	<body>
		<h1>Select a project and centre</h1>
		<c:choose>
			<c:when test="${fn:length(projectsgroups)>0}">
				<form method="post" class="selectproject" action="">
					<fieldset class="invisible">
						<spring:bind path="pg.projGroup">
							<select name="${status.expression}" id="${status.expression}">
								<c:forEach items="${projectsgroups}" var="projGroup">
									<option value="${projGroup.projectGroupCode}">${projGroup.projectGroupString}</option>
								</c:forEach>
							</select>
							<br />
						</spring:bind>
						<span><input type="submit" value="Submit" id="submit" /></span>
					</fieldset>
				</form>		
			</c:when>
			<c:otherwise>
				<p>No centres or studies were found for your username.</p>
			</c:otherwise>
		</c:choose>							
	</body>
</html>
