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
		<title>Change Password</title>
        <link rel="stylesheet" type="text/css" href="/psygrid/styles/forms.css" />
        <script src="/psygrid/scripts/passwordmeter.js" type="text/javascript"></script>
	</head>
	<body>
		<h1>Change Password</h1>
		<c:choose>
			<c:when test="${!empty cperror}">
				<p class="warning">${cperror}</p>
			</c:when>
			<c:otherwise>
				<p>You must change your password before you start using the system.</p>
			</c:otherwise>
		</c:choose>
		<form method="post" class="changepwd" name="changePasswordForm" action="">
			<fieldset>
				<label class="medium" for="oldPassword">Current Password: </label>
				<spring:bind path="password.oldPassword">
					<input type="password" name="${status.expression}" id="${status.expression}"></input>
					<br />
		            <c:if test="${status.error}">
		                <c:forEach items="${status.errorMessages}" var="error">
		                    <span class="error"><c:out value="${error}"/></span><br />
		                </c:forEach>
		            </c:if>
				</spring:bind>
				<label class="medium" for="newPassword1">New Password: </label>
				<spring:bind path="password.newPassword1">
					<input type="password" name="${status.expression}" id="${status.expression}" onkeyup="checkPasswordStrength()"></input>
					<br />
		            <c:if test="${status.error}">
		                <c:forEach items="${status.errorMessages}" var="error">
		                    <span class="error"><c:out value="${error}"/></span><br />
		                </c:forEach>
		            </c:if>
		            <script type="text/javascript">
		            function checkPasswordStrength(){
		            	document.getElementById('passwordStrength').innerHTML = testPassword(document.forms.changePasswordForm.${status.expression}.value);
		            }
		            </script>
				</spring:bind>
				<label class="medium" for="newPassword2">Re-enter New Password: </label>
				<spring:bind path="password.newPassword2">
					<input type="password" name="${status.expression}" id="${status.expression}"></input>
					<br />
		            <c:if test="${status.error}">
		                <c:forEach items="${status.errorMessages}" var="error">
		                    <span class="error"><c:out value="${error}"/></span><br />
		                </c:forEach>
		            </c:if>
				</spring:bind>
				<label class="medium" for="passwordStrength">Password Strength:</label>
				<span name="passwordStrength" id="passwordStrength" class="message"></span>
				<br />
				
				<label for="submit" class="medium"></label>
				<input type="submit" value="Submit" id="submit" />
			</fieldset>
		</form>
	</body>
</html>
