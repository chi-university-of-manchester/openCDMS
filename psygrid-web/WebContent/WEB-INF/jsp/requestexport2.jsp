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
        <script type="text/javascript">
			<c:forEach items="${docGroups}" var="docGroup">
				function checkDocGroup_${docGroup.id}(checkValue){
					<c:forEach items="${docGroup.docOccs}" var="doc">
						document.getElementById("docOcc_${doc.id}").checked = checkValue;
        			</c:forEach>
        			if ( checkValue ){
        				document.getElementById("select_group_${docGroup.id}").style.display="none";
        				document.getElementById("unselect_group_${docGroup.id}").style.display="block";
        			}
        			else{
        				document.getElementById("select_group_${docGroup.id}").style.display="block";
        				document.getElementById("unselect_group_${docGroup.id}").style.display="none";
        			}
       			}
       		</c:forEach>
        	function checkAllDocs(checkValue){
				<c:forEach items="${docGroups}" var="docGroup">
					<c:forEach items="${docGroup.docOccs}" var="doc">
						document.getElementById("docOcc_${doc.id}").checked = checkValue;
	       			</c:forEach>
	       		</c:forEach>
	       		var selectGroupStyle;
	       		var unselectGroupStyle;
	       		if ( checkValue ){
	       			selectGroupStyle = "none";
	       			unselectGroupStyle = "block";
	       		}
	       		else{
	       			selectGroupStyle = "block";
	       			unselectGroupStyle = "none";
	       		}
				<c:forEach items="${docGroups}" var="docGroup">
       				document.getElementById("select_group_${docGroup.id}").style.display=selectGroupStyle;
       				document.getElementById("unselect_group_${docGroup.id}").style.display=unselectGroupStyle;
	       		</c:forEach>
   				document.getElementById("select_all").style.display=selectGroupStyle;
   				document.getElementById("unselect_all").style.display=unselectGroupStyle;
        	}
		</script>
	</head>
	<body>
		<h1>Data Export</h1>
		<p>Step 3. Please select the format of the exported data.</p>
		<form method="post" class="export" action="">
			<fieldset>
				<p>
				<span class="label">Study:</span>
				<span class="answer">${project}</span>
				</p>
				<p>
				<span class="label">Centres:</span>
				<c:forEach items="${groups}" var="group">
					<span class="answer">${group}</span>
					<label for="group"></label>
				</c:forEach>
				</p>

				<span class="label">Format:</span> 
				<spring:bind path="export.format">
		            <c:if test="${status.error}">
		                <c:forEach items="${status.errorMessages}" var="error">
		                    <span class="error"><c:out value="${error}"/></span>
		                </c:forEach>
		            </c:if>
		            
		            <select name="format">
		            <c:forEach items="${formats}" var="format">
			            <option value="${format}" name="${format}">${format}</option>
					</c:forEach>
					</select>
				</spring:bind>
				
				<br /><br />
				<div>
					<label for="submit"></label>
					<input id="submit" type="submit" value="Next" name="_target3"></input>
				</div>
			</fieldset>
		</form>
	</body>
</html>
