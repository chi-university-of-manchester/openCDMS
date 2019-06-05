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
 		<link rel="stylesheet" type="text/css" href="/psygrid/styles/jquery.treeview.css" />
 		 
        <script type="text/javascript" src="/psygrid/scripts/jquery.js"></script>
        <script type="text/javascript" src="/psygrid/scripts/jquery.treeview.js"></script>
 
		<script type="text/javascript">
		   $(document).ready(function(){
				$("#doctree").treeview({
					collapsed: true,
					prerendered: true
				});		
			});
		</script>
        
        <script type="text/javascript">     		
       		<c:forEach items="${docGroups}" var="docGroup">
				function checkDocGroup_${docGroup.id}(){
					checkValue = document.getElementById("docGroup_${docGroup.id}").checked;
					<c:forEach items="${docGroup.docOccs}" var="doc">
						document.getElementById("docOcc_${doc.id}").checked = checkValue;
						checkDoc_${doc.id}();
        			</c:forEach>
       			}
       			
       			<c:forEach items="${docGroup.docOccs}" var="doc">
	       			function checkDoc_${doc.id}(){
	       				checkValue = document.getElementById("docOcc_${doc.id}").checked;
       					<c:forEach items="${doc.sections}" var="section">
	       					document.getElementById("docOcc_${doc.id}_section_${section.id}").checked = checkValue;		
	       					checkSection_${doc.id}_${section.id}();
		       			</c:forEach>
		   			}
        				
				    <c:forEach items="${doc.sections}" var="section">
					    function checkSection_${doc.id}_${section.id}(){
					    	checkValue = document.getElementById("docOcc_${doc.id}_section_${section.id}").checked;
       						<c:forEach items="${section.entries}" var="entry">
   								document.getElementById("docOcc_${doc.id}_section_${section.id}_entry_${entry.id}").checked = checkValue;		
			       			</c:forEach>
		       			}
	       			</c:forEach>
       			</c:forEach>
       		</c:forEach>       		
        	function checkAllDocs(checkValue){
				<c:forEach items="${docGroups}" var="docGroup">
					document.getElementById("docGroup_${docGroup.id}").checked = checkValue;
					checkDocGroup_${docGroup.id}();
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
   				document.getElementById("select_all").style.display=selectGroupStyle;
   				document.getElementById("unselect_all").style.display=unselectGroupStyle;
        	}
		</script>
	</head>
	<body>
		<h1>Data Export</h1>
		<p>Step 3. Please select the documents from which you would like to
		export the data.</p>
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
				<p>
				<span class="label">Format:</span>
				<span class="answer">${format}</span>
				</p>
					
				<br/>
				<span id="select_all" class="select-checks-docs" style="display: block;">
					<a href="javascript:checkAllDocs(true)">Select all documents</a>
				</span>
				<span id="unselect_all" class="select-checks-docs" style="display: none;">
					<a href="javascript:checkAllDocs(false)">Unselect all documents</a>
				</span>
				<br/>	
				<c:if test="${status.error}">
	                <c:forEach items="${status.errorMessages}" var="error">
       		            <span class="error"><c:out value="${error}"/></span>
               		</c:forEach>
	            </c:if>
				<div id="eggtimer" style="display: none;"><br/><br/></div>
				<ul id="doctree" class="filetree">
				<spring:bind path="export.documents">
				<spring:bind path="export.entries">
				<c:forEach items="${docGroups}" var="docGroup">
					<li class="expandable"><div class="hitarea expandable-hitarea"></div>
						<span class="folder">
							<input type="checkbox" class="checkbox3" name="documentGroups" id="docGroup_${docGroup.id}" 
								value="${docGroup.id}" onclick="javascript:checkDocGroup_${docGroup.id}()"></input>
							${docGroup.name}
						</span>
						<ul style="display: none;" class="documents">
							<c:forEach items="${docGroup.docOccs}" var="doc">
								<!-- Attach the document id and the document occurrence id to the li, so the document data can be retrieved -->
								<li class="expandable" id="${doc.documentid}_${doc.id}"><div class="hitarea expandable-hitarea"></div>
									<span class="document">
				    					<input type="checkbox" class="documents" name="documents" id="docOcc_${doc.id}" 
				    						value="${doc.id}" onclick="javascript:checkDoc_${doc.id}()"></input>
										<label class="checkboxlabel3" for="docOcc_${doc.id}">${doc.name}</label>
									</span>
									<ul style="display: none;">
									<c:forEach items="${doc.sections}" var="section">
										<li class="expandable"><div class="hitarea expandable-hitarea"></div>
											<span class="section">
						    					<input type="checkbox" class="sections" name="sections" id="docOcc_${doc.id}_section_${section.id}" 
						    						value="${section.id}" onclick="javascript:checkSection_${doc.id}_${section.id}()"></input>
												<label class="checkboxlabel3" for="docOcc_${doc.id}_section_${section.id}">${section.displayText}</label>			
											</span>
											<ul style="display: none;">
												<c:forEach items="${section.entries}" var="entry">
													<li>
														<span class="entry">
							    							<input type="checkbox" class="entries" name="entries" 
							    								id="docOcc_${doc.id}_section_${section.id}_entry_${entry.id}" value="${doc.id}_${entry.id}"></input>
															<label class="checkboxlabel3" for="docOcc_${doc.id}_section_${section.id}_entry_${entry.id}">${entry.displayText}</label>	
														</span>
													</li>
												</c:forEach>
											</ul>
										</li>
									</c:forEach>
									</ul>
								</li>
							</c:forEach>
						</ul>
					</li>	
				</c:forEach>
				</spring:bind>
				</spring:bind>
				</ul>
				<br />
				<br />
					
				<spring:bind path="export.docStatuses">
				<div>
					Select the document statuses to include in the export:
					<% int count=0; %>			
					<c:forEach items="${docStatuses}" var="docStatus">
	    				<input type="checkbox" class="checkbox3" name="docStatuses" id="${docStatus}" value="${docStatus}" checked></input>
						<label class="checkboxlabel3" for="${docStatus}">${docStatus}</label>
						<% count=count+1; %>
					</c:forEach>
				</div>
				<br />
				</spring:bind>
				<c:if test="${!empty codevalues}">
					<spring:bind path="export.codeValue">
						<label class="" style="margin-left:-20px;width:250px;" 
							for="codevalues">Display codes/values for responses:</label>
							<select style="" name="${status.expression}" id="${status.expression}" >
							<c:forEach items="${codevalues}" var="codevalue">
								<option value="${codevalue}">${codevalue}</option>
							</c:forEach>
						</select>
						<br />
					</spring:bind>
				</c:if>
				
				<c:if test="${!empty sysmissmessage}">
					<i>${sysmissmessage}</i>
					<br />
				</c:if>
				
				<c:if test="${immediateExport}">
					<spring:bind path="export.immediate">
						<div>
    					<input type="checkbox" class="checkbox3" name="${status.expression}" id="${status.expression}" value="true"></input>
						<label class="checkboxlabel3" for="${status.expression}">Process export immediately? (Please use sparingly)</label>
						</div>
						<br />
					</spring:bind>
				</c:if>
				
				<div>
					<label for="submit"></label>
					<input id="submit" type="submit" value="Submit" name="_finish"></input>
				</div>
			</fieldset>
		</form>
	</body>
</html>
