<#include '../../../layout.ftl'>

<#-- @ftlvariable name="pageName" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="projectTypeDisplayNameLowercase" type="String" -->
<#-- @ftlvariable name="backToTaskListUrl" type="String" -->

<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <#assign hasCollaborationsToAddFormProperty = "form.hasCollaborationsToAdd"/>
    <@fdsRadio.radioGroup
      path=hasCollaborationsToAddFormProperty
      labelText="Do you have any collaboration opportunities to add to this ${projectTypeDisplayNameLowercase}?"
    >
      <@fdsRadio.radioYes path=hasCollaborationsToAddFormProperty/>
      <@fdsRadio.radioNo path=hasCollaborationsToAddFormProperty/>
    </@fdsRadio.radioGroup>
    <@fdsAction.submitButtons
      primaryButtonText="Continue"
      secondaryLinkText="Back to task list"
      linkSecondaryAction=true
      linkSecondaryActionUrl=springUrl(backToTaskListUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>