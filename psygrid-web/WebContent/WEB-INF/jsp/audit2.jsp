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
		<p>Step 3. View the history of changes to records and select a change to examine at
		the document level.</p>
		<form method="post" class="export" action="">
			<fieldset>
				<label>Study:</label>
				<span class="answer">${audit.projectText}</span>
				<label>Start Date:</label>
				<span class="answer">
				<c:choose>
					<c:when test="${empty audit.startDate}">--none--</c:when>
					<c:otherwise>${audit.startDate}</c:otherwise>
				</c:choose>	
				</span>
				<label>End Date:</label>
				<span class="answer">
				<c:choose>
					<c:when test="${empty audit.endDate}">--none--</c:when>
					<c:otherwise>${audit.endDate}</c:otherwise>
				</c:choose>	
				</span>
				<label>User:</label>
				<span class="answer">${audit.user}</span>
				<label>Participant Identifier:</label>
				<span class="answer">${audit.identifier}</span>

				<spring:bind path="audit.startIndex">
					<input type="hidden" name="${status.expression}" id="${status.expression}" value="${audit.startIndex}"</input>
					<script type="text/javascript">
						function setStartIndex(startIndex){
							document.getElementById('${status.expression}').value=startIndex;
						}
					</script>
				</spring:bind>
					
				<div>
					<label for="submit"></label>
					<input id="submit" type="submit" value="Back" name="_target1" />
					<br />
				</div>
				
				<h2>Results</h2>
				<c:choose>
					<c:when test="${empty audit.searchRecordChangeHistoryResult.results}">
						<p>Your search returned no results</p>
					</c:when>
					<c:otherwise>
						<p>Showing ${audit.searchRecordChangeHistoryResult.firstResult} to ${audit.searchRecordChangeHistoryResult.lastResult} of ${audit.searchRecordChangeHistoryResult.totalCount}.</p>
						
						<div>
						<label for="prev"></label>
						<input id="prev" type="submit" value="Prev" name="_target2" onclick="setStartIndex(${prevStartIndex})" <c:if test="${empty prevStartIndex}">disabled="disabled" class="disabled"</c:if>/>
						
						<label for="next"></label>
						<input id="next" type="submit" value="Next" name="_target2" onclick="setStartIndex(${nextStartIndex})" <c:if test="${empty nextStartIndex}">disabled="disabled" class="disabled"</c:if>/>
						<br/>
						</div>
						
						<spring:bind path="audit.recordHistoryId">
						<table>
							<tr>
								<th>&nbsp;</th>
								<th>Participant Identifier</th>
								<th>When<br />(Local)</th>
								<th>When<br />(System)</th>
								<th>User</th>
								<th>Action</th>
							</tr>
							<c:forEach items="${audit.searchRecordChangeHistoryResult.results}" var="hist">
								<tr>
									<td><input type="radio" name="${status.expression}" value="${hist.historyId}"></input></td>
									<td>${hist.identifier}</td>
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
							<input id="view" type="submit" value="View" name="_target3" />
							<br />
						</div>

					</c:otherwise>
				</c:choose>
			</fieldset>
		</form>
		

	</body>
</html>
