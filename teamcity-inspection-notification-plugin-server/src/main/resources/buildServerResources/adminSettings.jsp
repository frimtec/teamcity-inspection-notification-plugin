<%@ include file="/include.jsp" %>

<jsp:useBean id="pluginSettings"
             scope="request"
             type="com.github.frimtec.teamcity.plugin.inspectionnotification.InspectionNotificationConfiguration"/>

<c:url value="/configureInspectionNotification.html" var="actionUrl"/>

<form action="${actionUrl}" id="inspectionNotificationForm" method="POST" onsubmit="return InspectionNotificationAdmin.save()">
    <div class="editNotificatorSettingsPage">
        <bs:messages key="configurationSaved"/>
        <br>
        <table class="runnerFormTable">
            <tr class="groupingTitle">
                <td colspan="2">General Configuration</td>
            </tr>
            <tr>
                <th>
                    <label for="inspectionAdminGroupName">Group name for inspection administrators: <l:star/></label>
                </th>
                <td>
                    <forms:textField name="inspectionAdminGroupName" value="${pluginSettings.inspectionAdminGroupName}" style="width: 300px;"/>
                    <span class="smallNote">New inspections without code changes are notified to all users of this group.</span>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="emailFromAddress">Email from address: <l:star/></label>
                </th>
                <td>
                    <forms:textField name="emailFromAddress" value="${pluginSettings.emailFromAddress}" style="width: 300px;"/>
                    <span class="smallNote">From address used in the generated email notifications.</span>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="emailSmtpHost">SMTP host address:<l:star/></label>
                </th>
                <td>
                    <forms:textField name="emailSmtpHost" value="${pluginSettings.emailSmtpHost}" style="width: 300px;"/>
                    <span class="smallNote">SMTP host address to deliver generated email notifications.</span>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="emailSmtpPort">SMTP port:<l:star/></label>
                </th>
                <td>
                    <forms:textField name="emailSmtpPort" value="${pluginSettings.emailSmtpPort}" style="width: 300px;"/>
                    <span class="smallNote">SMTP port to deliver generated email notifications.</span>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="emailSmtpLogin">SMTP login:</label>
                </th>
                <td>
                    <forms:textField name="emailSmtpLogin" value="${pluginSettings.emailSmtpLogin}" style="width: 300px;"/>
                    <span class="smallNote">SMTP login if SMTP-Auth should be used.</span>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="encryptedEmailSmtpPassword">SMTP password:</label>
                </th>
                <td>
                    <forms:passwordField name="encryptedEmailSmtpPassword" encryptedPassword="${pluginSettings.encryptedEmailSmtpPassword}" style="width: 300px;"/>
                    <span class="smallNote">SMTP password if SMTP-Auth should be used.</span>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="emailSmtpStartTls">SMTP StartTLS:</label>
                </th>
                <td>
                    <forms:checkbox name="emailSmtpStartTls" checked="${pluginSettings.emailSmtpStartTls}" value="${pluginSettings.emailSmtpStartTls}"/>
                    <span class="smallNote">Check to use secure StartTLS protocol.</span>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="emailSubject">Email subject:<l:star/></label>
                </th>
                <td>
                    <forms:textField name="emailSubject" value="${pluginSettings.emailSubject}" style="width: 300px;"/>
                    <span class="smallNote">Email subject.</span>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="emailSubjectNoChanges">Email subject (no changes):<l:star/></label>
                </th>
                <td>
                    <forms:textField name="emailSubjectNoChanges" value="${pluginSettings.emailSubjectNoChanges}" style="width: 300px;"/>
                    <span class="smallNote">Email subject for new violations without changes.</span>
                </td>
            </tr>
            <tr>
                <th><label for="emailTemplate">Email template: </label></th>
                <td>
                    <textarea id="emailTemplate" name="emailTemplate" rows="20" style="width: 92%;">${pluginSettings.emailTemplate}</textarea>
                    <span class="smallNote">Email template (freemarker syntax); clear to reset to default.</span>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="bitbucketRootUrl">Bitbucket root URL:</label>
                </th>
                <td>
                    <forms:textField name="bitbucketRootUrl" value="${pluginSettings.bitbucketRootUrl}" style="width: 300px;"/>
                    <span class="smallNote">Optional Bitbucket root URL used to generate links to the source code.</span>
                </td>
            </tr>
        </table>
        <div class="saveButtonsBlock">
            <forms:submit label="Save"/>
            <input type="hidden" id="publicKey" name="publicKey" value="<c:out value='${pluginSettings.hexEncodedPublicKey}'/>"/>
            <forms:saving/>
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
      var emailSmtpPort = document.forms["inspectionNotificationForm"]["emailSmtpPort"].value;
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
            "&emailSmtpLogin=" + $("emailSmtpLogin").value +
            "&encryptedEmailSmtpPassword=" + $("encryptedEmailSmtpPassword").getEncryptedPassword($("publicKey").value) +
            "&emailSmtpStartTls=" + $("emailSmtpStartTls").checked +
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
