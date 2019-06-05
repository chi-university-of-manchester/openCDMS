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
		<p>Step 5. View the entry-level changes that occurred with the selected document change.</p>
		<form method="post" class="export" action="">
			<fieldset>
				<label>Study:</label>
				<span class="answer">${audit.projectText}</span>
				<label>Record:</label>
				<span class="answer">${audit.recordChangeHistoryItem.identifier}</span>
				<label>Document:</label>
				<span class="answer">${audit.docInstChangeHistoryItem.displayText}</span>
				<label>User:</label>
				<span class="answer">${audit.docInstChangeHistoryItem.user}</span>
				<label>When (Local):</label>
				<span class="answer"><fmt:formatDate value="${audit.docInstChangeHistoryItem.when}" pattern="HH:mm dd/MM/yyyy" /></span>
				<label>When (System):</label>
				<span class="answer"><fmt:formatDate value="${audit.docInstChangeHistoryItem.whenSystem}" pattern="HH:mm dd/MM/yyyy" /></span>
				<label>Action:</label>
				<span class="answer">${audit.docInstChangeHistoryItem.action}</span>

				<div>
					<label for="submit"></label>
					<input id="submit" type="submit" value="Back" name="_target3" />
					<br />
				</div>
				
				<c:choose>
					<c:when test="${empty result}">
						<p>No entry-level changes recorded.</p>
					</c:when>
					<c:otherwise>
						<table>
							<tr>
								<th>Entry</th>
								<th>Was</th>
								<th>Now</th>
								<th>Comment</th>
							</tr>
							<c:forEach items="${result}" var="pfcr">
								<tr>
									<td>${pfcr.entry}</td>
									<td>${pfcr.prevValue}</td>
									<td>${pfcr.currentValue}</td>
									<td>${pfcr.comment}</td>
								</tr>
							</c:forEach>
						</table>
						
					</c:otherwise>
				</c:choose>

			</fieldset>
		</form>
		

	</body>
</html>
