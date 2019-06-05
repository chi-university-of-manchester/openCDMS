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
		<title>Data Export</title>
	</head>
	<body>
		<h1>Data Export</h1>
		<c:if test="${!empty message }">
			<p class="error">${message}</p>
		</c:if>
		<p>Your outstanding and recent data export requests.</p>
		<table>
			<tr>
				<th>ID</th>
				<th>Study</th>
				<th>Request date</th>
				<th>Status</th>
				<th>&nbsp;</th>
			</tr>
			<c:forEach items="${requests}" var="req">
				<tr>
					<td>${req.id}</td>
					<td>${req.projectCode}</td>
					<td><fmt:formatDate value="${req.requestDate}" pattern="HH:mm dd-MMM-yyyy" /></td>
					<td>${req.status}</td>
					<td>
						<c:if test="${'Pending' == req.status}">
							<a href="cancel.html?id=${req.id}">Cancel</a>
						</c:if>
						<c:if test="${'Complete' == req.status}">
							<a href="download.html?id=${req.id}">Download</a>&nbsp;
							(<a href="download.html?id=${req.id}&type=md5">MD5</a>&nbsp;
							<a href="download.html?id=${req.id}&type=sha1">SHA1</a>)
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</table>
	</body>
</html>
