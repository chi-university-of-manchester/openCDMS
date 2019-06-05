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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
	<head>
		<c:import url="authorization.jsp" />
		<title>Audit Log</title>
        <link rel="stylesheet" type="text/css" href="/psygrid/styles/forms.css" />
	</head>
	<body>
		<h1>Audit Log</h1>
		<p>Step 4. View the document changes that occurred with the selected record change.</p>
		<form method="post" class="export" action="">
			<fieldset>
				<label>Study:</label>
				<span class="answer">${audit.projectText}</span>
				<label>Record:</label>
				<span class="answer">${audit.recordChangeHistoryItem.identifier}</span>
				<label>User:</label>
				<span class="answer">${audit.recordChangeHistoryItem.user}</span>
				<label>When (Local):</label>
				<span class="answer"><fmt:formatDate value="${audit.recordChangeHistoryItem.when}" pattern="HH:mm dd/MM/yyyy" /></span>
				<label>When (System):</label>
				<span class="answer"><fmt:formatDate value="${audit.recordChangeHistoryItem.whenSystem}" pattern="HH:mm dd/MM/yyyy" /></span>
				<label>Action:</label>
				<span class="answer">${audit.recordChangeHistoryItem.action}</span>

				<div>
					<label for="submit"></label>
					<input id="submit" type="submit" value="Back" name="_target2" />
					<br />
				</div>
				
				<c:choose>
					<c:when test="${empty audit.searchDocInstChangeHistoryResults}">
						<p>No document changes recorded.</p>
					</c:when>
					<c:otherwise>
						<spring:bind path="audit.documentHistoryId">
						<table>
							<tr>
								<th>&nbsp;</th>
								<th>Document</th>
								<th>When<br />(Local)</th>
								<th>When<br />(System)</th>
								<th>User</th>
								<th>Action</th>
							</tr>
							<c:forEach items="${audit.searchDocInstChangeHistoryResults}" var="hist">
								<tr>
									<td><input type="radio" name="${status.expression}" value="${hist.historyId}"></input></td>
									<td>${hist.displayText}</td>
									<td><fmt:formatDate value="${hist.when}" pattern="HH:mm dd/MM/yyyy" /></td>
									<td><fmt:formatDate value="${hist.whenSystem}" pattern="HH:mm dd/MM/yyyy" /></td>
									<td>${hist.user}</td>
									<td>${hist.action}</td>
								</tr>
							</c:forEach>
						</table>
						
						<p class="error">${status.errorMessage}</p>

						</spring:bind>
						
						<div>
							<label for="view"></label>
							<input id="view" type="submit" value="View" name="_target4" />
							<br />
						</div>
					</c:otherwise>
				</c:choose>

			</fieldset>
		</form>
		

	</body>
</html>
