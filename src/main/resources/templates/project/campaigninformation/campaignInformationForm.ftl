<#include '../../layout.ftl'>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->
<#-- @ftlvariable name="projectTypeDisplayNameLowercase" type="String" -->

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@fdsTextarea.textarea path="form.scopeDescription" labelText="What is the scope of the campaign?"/>
    <@fdsRadio.radioGroup
      labelText="Is this ${projectTypeDisplayNameLowercase} already part of a campaign with a published ${service.serviceName} ${projectTypeDisplayNameLowercase}?"
      path="form.publishedCampaign"
      hiddenContent=true
    >
      <@fdsRadio.radioYes path="form.publishedCampaign"/>
      <@fdsRadio.radioNo path="form.publishedCampaign"/>
    </@fdsRadio.radioGroup>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>
