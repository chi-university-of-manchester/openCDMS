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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
	<head>
		<c:import url="authorization.jsp" />
		<title>Participant Register - ${title}</title>
		<link rel="stylesheet" type="text/css" href="/psygrid/styles/eslforms.css" />
	</head>
	<body>
		<h1>${heading}</h1>
		<p>${description}</p>
		<p>The participant identifier must be of the format ${exampleStudyNumber}.</p>
 		<spring:bind path="personalDetails">
        	<c:forEach items="${status.errorMessages}" var="error">
        		<p class="error">${error}</p>
        	</c:forEach>
		</spring:bind>
		
		<form method="post" action="" class="bystudycode">
			<fieldset class="invisible">
				<spring:bind path="personalDetails.studyNumber">
					<label for="${status.expression}">Participant identifier:</label>
					<input name="${status.expression}" id="${status.expression}" type="text" value="${status.value}" />
					<br />
					<span class="error"><c:out value="${status.errorMessage}"/></span>
				</spring:bind>
				
				<br />
				<span>
				<c:if test="${'yes' == treatmentarm}">
					<input name="search" value="Search" type="submit" class="submit" />
				</c:if>
				<c:if test="${'yes' == studycode}">
					<input name="_target1" value="Search" type="submit" class="submit" />
					<c:if test="${'yes' == eslWebEditPrivilege }">
						<input name="_target2" value="Edit" type="submit" class="submit" />
					</c:if>
				</c:if>
				</span>
			</fieldset>
		</form>	
	</body>
</html>
