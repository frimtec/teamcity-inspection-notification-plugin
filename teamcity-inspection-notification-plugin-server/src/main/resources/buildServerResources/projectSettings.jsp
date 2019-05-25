<%@ include file="/include.jsp" %>

<c:url value="/configureInspectionNotification.html" var="actionUrl"/>

<form action="${actionUrl}" id="inspectionNotificationProjectForm" method="post"
      onsubmit="return InspectionNotificationProject.save()">
    <div class="editNotificatorSettingsPage">
        <bs:messages key="configurationSaved"/>
        <table class="runnerFormTable">
            <tr>
                <th><label for="projectDisabled">Disable notification:</label></th>
                <td>
                    <forms:checkbox name="projectDisabled" checked="${projectDisabled}" value="${projectDisabled}"/>
                    <span class="smallNote">When checked, the inspection violation notification is disabled for this project.</span>
                </td>
            </tr>
        </table>
        <div class="saveButtonsBlock">
            <forms:submit label="Save"/>
            <input type="hidden" id="projectId" name="projectId" value="${projectId}"/>
            <forms:saving/>
        </div>
    </div>
</form>

<script type="text/javascript">
  var InspectionNotificationProject = {
    save: function () {
      BS.ajaxRequest($("inspectionNotificationProjectForm").action, {
        parameters:
            "project=1" +
            "&projectDisabled=" + $("projectDisabled").checked +
            "&projectId=" + $("projectId").value,
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
  };
</script>