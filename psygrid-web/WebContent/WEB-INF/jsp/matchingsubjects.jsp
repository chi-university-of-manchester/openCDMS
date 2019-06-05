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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
	<head>
		<c:import url="authorization.jsp" />
		<title>Participant Register - Participant search results</title>
		
        <link rel="stylesheet" type="text/css" href="/psygrid/styles/eslforms.css" />    
	</head>
	<body>
		<spring:bind path="subjects">
		<h1>Subject search results</h1>
		<h3>Found ${fn:length(subjects)} subjects</h3>
		<c:if test="${!empty message }">
			<p class="message">${message}</p>
		</c:if>
		

 		<table>
 		<th>Participant Identifier</th><th>Title</th><th>First name</th><th>Last name</th><th>Date of birth</th><th>&nbsp;</th>
        	<c:forEach items="${subjects}" var="subject">
        	<form method="post" action="" id="horizontalForm">
 				<input type="hidden" id="studyNumber" name="studyNumber" value="${subject.studyNumber}" /> 		
        	    <tr>
				<td>${subject.studyNumber}</td>
				<td>${subject.title}</td>
				<td>${subject.firstName}</td>
				<td>${subject.lastName}</td>
				<td><c:if test="${!empty subject.dateOfBirth}">
			        	<fmt:formatDate value="${subject.dateOfBirth}" pattern="dd MMMM yyyy" />
			        </c:if>
			    </td>
				<td>
				    <input class="submit" name="_target2" type="submit" value="View" />
				</td>
				<c:if test="${'yes' == eslWebEditPrivilege }">
					<td><input class="submit" name="_target3" type="submit" value="Edit" /></td>
				</c:if>
				</tr>
				
				</form>
        	</c:forEach>
        </table>
		</spring:bind>
		<form method="post" action="" id="">
		<input type="hidden" id="reset" name="reset" value="1" /> 		
 		<input id="back" name="_cancel" type="submit" value="Search Again"  />
 		</form>
	</body>
</html>
