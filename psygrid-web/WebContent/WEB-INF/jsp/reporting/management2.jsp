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
		<script type="text/javascript">
		        	        	
        	function selectAllTrusts(curThing, hubcode, trusts){
	        	document.getElementById(hubcode).style.display=(curThing.checked)?'block':'none';
				
				for (var n = 0; n < trusts.length; n++) {
    				var name = hubcode + n;  
					document.getElementById(name).checked = curThing.checked;
				}
        	}
		</script>
		
		<c:import url="../authorization.jsp" />
		<c:import url="header.jsp" />


		<form method="post" action="managementreport.html" class="report">
			<fieldset>
				
					<!-- if a dataset has been selected put in static text field -->
					<p>
						<label for="dataset">Study: </label><span class="answer">${report.dataset}</span>
					</p>
					
					<p>	
						<label for="title">Report: </label><span class="answer">${report.title}</span>
					</p>
				
    				<label for="groups">Centres: </label><br/>
					<c:forEach items="${hubs}" var="hub">
						<div>
						<input type="checkbox" class="checkbox" name="hubs" id="hub_${hub.code}" value="${hub.code}" onclick="javascript:selectAllTrusts(this, '${hub.code}', '${hub.trusts}')" unchecked/>
						<label for="hub_${hub.code}" class="checkboxlabel1">${hub.name}</label>
						</div>
						
						<div id="${hub.code}" style="display:none">

						<spring:bind path="report.groups">	
						<% int count=0; %>				    					
						<c:forEach items="${hub.trusts}" var="trust">
							<div>
							<input type="checkbox" class="checkbox2" name="groups" id="${hub.code}<%=count%>" value="${hub.code}${trust.code}=${trust.name}" unchecked/>
							<label for="${hub.code}<%=count%>" class="checkboxlabel2">${trust.name}</label>
							</div>
							<% count=count+1; %>
						</c:forEach>
	    				</spring:bind>
    				
						</div>
															
					</c:forEach>
					
					<p class="error">
					<spring:bind path="report.groups">
					<c:out value="${status.errorMessage}"/>
					</spring:bind></p>
					
				<br />
				<div>
					<label for="back"></label>
					<input type="submit" name="_target1" value="Back" id="back"/>
					<label for="submit"></label>
					<input type="submit" value="Continue" name="_target3" id="submit"/>
				</div>
				
			</fieldset>
		</form>
		
		
<c:import url="footer.jsp" />