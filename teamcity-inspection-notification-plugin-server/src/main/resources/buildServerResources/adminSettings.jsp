<%--
  ~  Copyright (c) 2012 - 2019 the original author or authors.
  ~
  ~  Licensed under the Apache License, Version 2.0 (the "License");
  ~  you may not use this file except in compliance with the License.
  ~  You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~  Unless required by applicable law or agreed to in writing, software
  ~  distributed under the License is distributed on an "AS IS" BASIS,
  ~  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~  See the License for the specific language governing permissions and
  ~  limitations under the License.
  --%>

<!--
Copyright 2014 Pieter Rautenbach

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

<%@ include file="/include.jsp"%>

<c:url value="/configureInspectionNotification.html" var="actionUrl" />

<form action="${actionUrl}" id="inspectionNotificationForm" method="POST" onsubmit="return InspectionNotificationAdmin.save()">
	<div class="editNotificatorSettingsPage">
		<bs:messages key="configurationSaved" />
       	<br>		
		<table class="runnerFormTable">
			<tr class="groupingTitle">
          		<td colspan="2">General Configuration</td>
        	</tr>
			<tr>
				<th>
					<label for="inspectionAdminGroupName">Group name for inspection administrators: <l:star /></label>
				</th>
				<td>
					<forms:textField name="inspectionAdminGroupName" value="${inspectionAdminGroupName}" style="width: 300px;" />
					<span class="smallNote">New inspections without code changes are notified to all users of this group.</span>
				</td>
			</tr>
			<tr>
				<th>
					<label for="emailFromAddress">Email from address: <l:star /></label>
				</th>
				<td>
					<forms:textField name="emailFromAddress" value="${emailFromAddress}" style="width: 300px;" />
					<span class="smallNote">From address used in the generated email notifications.</span>
				</td>
			</tr>
			<tr>
				<th>
					<label for="emailSmtpHost">SMTP host address:<l:star /></label>
				</th>
				<td>
					<forms:textField name="emailSmtpHost" value="${emailSmtpHost}" style="width: 300px;" />
					<span class="smallNote">SMTP host address to deliver generated email notifications.</span>
				</td>
			</tr>
			<tr>
				<th>
					<label for="emailSmtpPort">SMTP port:<l:star /></label>
				</th>
				<td>
					<forms:textField name="emailSmtpPort" value="${emailSmtpPort}" style="width: 300px;" />
					<span class="smallNote">SMTP port to deliver generated email notifications.</span>
				</td>
			</tr>
			<tr>
				<th>
					<label for="emailSubject">Email subject:<l:star /></label>
				</th>
				<td>
					<forms:textField name="emailSubject" value="${emailSubject}" style="width: 300px;" />
					<span class="smallNote">Email subject.</span>
				</td>
			</tr>
			<tr>
				<th>
					<label for="emailSubjectNoChanges">Email subject (no changes):<l:star /></label>
				</th>
				<td>
					<forms:textField name="emailSubjectNoChanges" value="${emailSubjectNoChanges}" style="width: 300px;" />
					<span class="smallNote">Email subject for new violations without changes.</span>
				</td>
			</tr>
			<tr>
				<th><label for="emailTemplate">Email template: </label></th>
				<td>
					<textarea id="emailTemplate" name="emailTemplate" rows="20" style="width: 92%;">${emailTemplate}</textarea>
					<span class="smallNote">Email template (freemarker syntax); clear to reset to default.</span>
				</td>
			</tr>
			<tr>
				<th>
					<label for="bitbucketRootUrl">Bitbucket root URL:</label>
				</th>
				<td>
					<forms:textField name="bitbucketRootUrl" value="${bitbucketRootUrl}" style="width: 300px;" />
					<span class="smallNote">Optional Bitbucket root URL used to generate links to the source code.</span>
				</td>
			</tr>
		</table>
		<div class="saveButtonsBlock">
			<forms:submit label="Save" />
			<forms:saving />
		</div>
	</div>
</form>

<script type="text/javascript">
	var InspectionNotificationAdmin = {
		validate: function () {
			var inspectionAdminGroupName = document.forms["inspectionNotificationForm"]["inspectionAdminGroupName"].value;
			if (inspectionAdminGroupName == null || inspectionAdminGroupName === "") {
				alert("You must specify a value for the group name for inspection administrators.");
				return false;
			}
			var emailFromAddress = document.forms["inspectionNotificationForm"]["emailFromAddress"].value;
			if (emailFromAddress == null || emailFromAddress === "") {
				alert("You must specify a value for the email from address.");
				return false;
			}
			var emailSmtpHost = document.forms["inspectionNotificationForm"]["emailSmtpHost"].value;
			if (emailSmtpHost == null || emailSmtpHost === "") {
				alert("You must specify a value for the SMTP host address.");
				return false;
			}
			var emailSmtpHost = document.forms["inspectionNotificationForm"]["emailSmtpPort"].value;
			if (emailSmtpPort == null || emailSmtpPort === "") {
				alert("You must specify a value for the SMTP port.");
				return false;
			}
			var emailSubject = document.forms["inspectionNotificationForm"]["emailSubject"].value;
			if (emailSubject == null || emailSubject === "") {
				alert("You must specify a value for the email subject.");
				return false;
			}
			var emailSubjectNoChanges = document.forms["inspectionNotificationForm"]["emailSubjectNoChanges"].value;
			if (emailSubjectNoChanges == null || emailSubjectNoChanges === "") {
				alert("You must specify a value for the email subject (no changes).");
				return false;
			}
			return true;
		},

		save: function () {
			if (!InspectionNotificationAdmin.validate()) {
				return false;
			}

			BS.ajaxRequest($("inspectionNotificationForm").action, {
				method: "POST",
				parameters:
						"edit=1" +
						"&inspectionAdminGroupName=" + $("inspectionAdminGroupName").value +
						"&bitbucketRootUrl=" + $("bitbucketRootUrl").value +
						"&emailFromAddress=" + $("emailFromAddress").value +
						"&emailSmtpHost=" + $("emailSmtpHost").value +
						"&emailSmtpPort=" + $("emailSmtpPort").value +
						"&emailSubject=" + $("emailSubject").value +
						"&emailSubjectNoChanges=" + $("emailSubjectNoChanges").value +
						"&emailTemplate=" + $("emailTemplate").value,
				onComplete: function (transport) {
					if (transport.responseXML) {
						BS.XMLResponse.processErrors(transport.responseXML, {
							onProfilerProblemError: function (elem) {
								alert(elem.firstChild.nodeValue);
							}
						});
					}
					BS.reload(true);
				}
			});
			return false;
		}
	}
</script>
