<#-- @ftlvariable name="message" type="ch.frimtec.teamcity.plugin.inspectionnotification.NotificationMessage" -->
<#setting number_format="computer">
<html>
  <h4>The following new inspection violations has been reported in
    <a href='${message.getTeamCityRootUrl()}/viewLog.html?buildId=${message.getBuild().getBuildId()}'>${message.build.getFullName()} #${message.getBuild().getBuildNumber()}:</a>
  </h4>
  <table border="1">
    <tr style="font-weight: bold">
      <td>Level</td>
      <td>Inspection</td>
      <td>Location</td>
    </tr>
    <#list message.getNewViolations() as violation>
    <tr>
      <td>${violation.getLevel()}</td>
      <td>${violation.getInspectionName()}</td>
      <td>
        <a href="${message.generateBitbucketUrl(violation)}">${violation.getFileName()}:${violation.getLine()}</a>
      </td>
    </tr>
    </#list>
  </table>

<#if message.getCommitters()?has_content>
  <h4>The following committers have contributed to this build:</h4>
    <ul>
    <#list message.getCommitters() as committer>
      <li>
        <a href='mailto:${committer.getEmail()}'>${committer.getName()}</a>
      </li>
    </#list>
    </ul>
<#else>
  <h4>Possible reasons for the new inspection violations:</h4>
    <ul>
      <li>Changes in the projects inspection profile.</li>
      <li>New IDEA version with improved or new inspections.</li>
      <li>...</li>
    </ul>
</#if>
</html>
