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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
	<head>
		<c:import url="authorization.jsp" />
		<title>Audit Log</title>
        <link rel="stylesheet" type="text/css" href="/psygrid/styles/forms.css" />
        <link rel="stylesheet" type="text/css" href="/psygrid/styles/calendar-blue.css" />
		<script type="text/javascript" src="/psygrid/scripts/calendar.js"></script>
		<script type="text/javascript" src="/psygrid/scripts/calendar-en.js"></script>
		<script type="text/javascript" src="/psygrid/scripts/calendar-setup.js"></script>
		<script type="text/javascript">
			function clear(elementId){
				document.getElementById(elementId).value='';
			}
		</script>
	</head>
	<body>
		<h1>Audit Log</h1>
		<p>Step 2. Please select the attributes by which to filter the audit log.</p>
		<form method="post" class="export" action="">
			<fieldset>
				<p>
				<label>Study:</label>
				<span class="answer">${audit.projectText}</span>
				</p>

				<spring:bind path="audit.startDate">
					<label for="${status.expression}">Start Date: </label>
					<input type="text" name="${status.expression}" id="${status.expression}" value="<c:if test="${!empty audit.startDate}">${audit.startDate}</c:if>"></input>
					<span class="clear-link"><a href="javascript:clear('${status.expression}')">Clear</a></span>
					<br />
					<span class="error"><c:out value="${status.errorMessage}"/></span>
					
					<script type="text/javascript">
					  Calendar.setup(
					    {
					      inputField  : "${status.expression}",
					      <c:if test="${!empty audit.startDate}">date : "${audit.startDate}",</c:if>
					      ifFormat    : "%d-%m-%Y"
					    }
					  );
					</script>
				</spring:bind>

				<spring:bind path="audit.endDate">
					<label for="${status.expression}">End Date: </label>
					<input type="text" name="${status.expression}" id="${status.expression}" value="<c:if test="${!empty audit.endDate}">${audit.endDate}</c:if>"></input> 
					<span class="clear-link"><a href="javascript:clear('${status.expression}')">Clear</a></span>
					<br />
					<span class="error"><c:out value="${status.errorMessage}"/></span>
					<script type="text/javascript">
					  Calendar.setup(
					    {
					      inputField  : "${status.expression}",
					      <c:if test="${!empty audit.endDate}">date : "${audit.endDate}",</c:if>
					      ifFormat    : "%d-%m-%Y"
					    }
					  );
					</script>
				</spring:bind>
				
				<spring:bind path="audit.user">
					<label for="${status.expression}">User: </label>
					<select name="${status.expression}" id="${status.expression}">
						<c:forEach items="${userList}" var="user">
							<c:choose>
								<c:when test="${user.dn==audit.user}">
									<option value="${user.dn}" selected="selected">${user.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${user.dn}">${user.name}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</spring:bind>
				<br />

				<spring:bind path="audit.identifier">
					<label for="${status.expression}">Record: </label>
					<select name="${status.expression}" id="${status.expression}">
						<c:forEach items="${identifierList}" var="identifier">
							<c:choose>
								<c:when test="${identifier==audit.identifier}">
									<option value="${identifier}" selected="selected">${identifier}</option>
								</c:when>
								<c:otherwise>
									<option value="${identifier}">${identifier}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</spring:bind>
				<br />

				<spring:bind path="audit.startIndex">
					<input type="hidden" name="${status.expression}" id="${status.expression}" value="0"></input>
				</spring:bind>
					
				<div>
					<label for="submit"></label>
					<input id="submit" type="submit" value="Search" name="_target2" />
				</div>
			</fieldset>
		</form>
	</body>
</html>
