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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
	<head>
		<c:import url="../authorization.jsp" />
		<c:import url="header.jsp" />

		<form method="post" action="managementreport.html" class="report">
			<fieldset>		
					<p>
						<label for="dataset">Study: </label><span class="answer">${report.dataset}</span>
					</p>
					
					<p>	
						<label for="title">Report: </label><span class="answer">${report.title}</span>
					</p>
					
					<p>
					<c:if test="${!empty report.groups}">
					<label for="groups">Centres: </label>
					<c:forEach items="${report.groups}" var="group">
						<span class="answer">${fn:split(group, '=')[1]}</span>
						<label for="group"></label>
					</c:forEach>
					</c:if>
					</p>

					<p>
					<label for="startDate">Start Date: </label>
					<select name="startMonth" >
						<c:forEach items="${months}" var="month">
							<option value="${month}">${month}</option>
						</c:forEach>
					</select>
					<select name="startYear" >
						<c:forEach items="${years}" var="year">
							<option value="${year}">${year}</option>
						</c:forEach>
					</select>
					</p>
					<br />

					<p>
					<label for="endDate">End Date: </label>
					<select name="endMonth" >
						<c:forEach items="${months}" var="month">
							<c:choose>
								<c:when test="${month==currentMonth}">
									<option value="${month}" selected >${month}</option>
								</c:when>
								<c:otherwise>
									<option value="${month}">${month}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
					<select name="endYear" >
						<c:forEach items="${years}" var="year">
							<c:choose>
								<c:when test="${year==currentYear}">
									<option value="${year}" selected >${year}</option>
								</c:when>
								<c:otherwise>
									<option value="${year}">${year}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
					</p>
					<br />
					<spring:bind path="report.endDate">
					<p class="error"><c:out value="${status.errorMessage}"/></p>
					</spring:bind>
					
				<br />
				<div>
					<label for="back"></label>
					<input type="submit" name="_target2" value="Back" id="back" />
					<label for="submit"></label>
					<input type="submit" value="Continue" name="_target4" id="submit" />
				</div>
			</fieldset>
		</form>
		
		
<c:import url="footer.jsp" />