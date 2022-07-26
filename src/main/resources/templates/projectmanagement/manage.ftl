<#include '../layout.ftl'>

<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="projectManagementView" type="uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementView" -->
<#-- @ftlvariable name="viewVersionUrl" type="String" -->
<#-- @ftlvariable name="viewableVersions" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="form" type="uk.co.ogauthority.pathfinder.model.form.projectmanagement.ProjectManagementForm" -->
<#-- @ftlvariable name="pageTitle" type="String" -->

<@defaultPage
  htmlTitle=pageTitle
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
      <@differenceChanges.toggler analyticsEventCategory=showDiffsProjectEventCategory/>
    </#if>
  </#if>
  <@noEscapeHtml.noEscapeHtml html=projectManagementView.versionContentHtml />
</@defaultPage>
