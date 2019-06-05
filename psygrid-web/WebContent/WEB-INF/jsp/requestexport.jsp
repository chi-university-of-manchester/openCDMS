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
		<title>Data Export</title>
        <link rel="stylesheet" type="text/css" href="/psygrid/styles/forms.css" />
	</head>
	<body>
		<h1>Data Export</h1>
		<p>Step1. Please select the study from which you would like to
		export the data.</p>
		<form method="post" class="export" action="">
			<fieldset>
				<spring:bind path="export.project">
					<label for="${status.expression}">Study: </label>
					<select name="${status.expression}" id="${status.expression}">
						<c:forEach items="${projects}" var="project">
							<option value="${project.idCode}">${project.name}</option>
						</c:forEach>
					</select>
				</spring:bind>
				<br />
				<div>
					<label for="submit"></label>
					<input id="submit" type="submit" value="Next" name="_target1"></input>
				</div>
			</fieldset>
		</form>
	</body>
</html>
