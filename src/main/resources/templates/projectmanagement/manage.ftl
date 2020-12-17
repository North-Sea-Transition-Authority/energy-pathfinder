<#include '../layout.ftl'>
<#import '../project/summary/projectSummary.ftl' as projectSummary/>

<@defaultPage
  htmlTitle="Manage project"
  fullWidthColumn=true
  twoThirdsColumn=false
  backLink=true
  backLinkUrl=springUrl(backLinkUrl)
>
  ${projectManagementView.staticContentHtml?no_esc}
  <#if (viewableVersions?size > 1)>
    <@fdsForm.htmlForm actionUrl=springUrl(viewVersionUrl)>
      <@inlineInputAction.inlineInputAction>
        <@fdsSelect.select path="form.version" options=viewableVersions labelText="Project version" />
        <@fdsAction.button buttonText="View" buttonClass="govuk-button govuk-button--blue"/>
      </@inlineInputAction.inlineInputAction>
    </@fdsForm.htmlForm>
  </#if>
  ${projectManagementView.versionContentHtml?no_esc}
</@defaultPage>
