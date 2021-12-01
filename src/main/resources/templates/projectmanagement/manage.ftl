<#include '../layout.ftl'>

<@defaultPage
  htmlTitle="Manage ${projectTypeDisplayNameLowercase}"
  fullWidthColumn=true
  twoThirdsColumn=false
  backLink=true
  backLinkUrl=springUrl(backLinkUrl)
>
  <@noEscapeHtml.noEscapeHtml html=projectManagementView.staticContentHtml />
  <#if (viewableVersions?size > 1)>
    <@fdsForm.htmlForm actionUrl=springUrl(viewVersionUrl)>
      <@inlineInputAction.inlineInputAction>
        <@fdsSelect.select path="form.version" options=viewableVersions labelText="Project version" />
        <@fdsAction.button buttonText="View" buttonClass="govuk-button govuk-button--blue"/>
      </@inlineInputAction.inlineInputAction>
    </@fdsForm.htmlForm>
    <#if form.version != 1>
      <@differenceChanges.toggler/>
    </#if>
  </#if>
  <@noEscapeHtml.noEscapeHtml html=projectManagementView.versionContentHtml />
</@defaultPage>
