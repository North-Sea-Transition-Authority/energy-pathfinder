<#include '../../../layout.ftl'>

<#-- @ftlvariable name="pageName" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="projectTypeDisplayNameLowercase" type="String" -->
<#-- @ftlvariable name="backToTaskListUrl" type="String" -->


<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true errorItems=errorList>
    <@fdsForm.htmlForm>
        <@fdsRadio.radioGroup
          path="form.hasContractToAdd"
          labelText="Do you have any awarded contracts to add to this ${projectTypeDisplayNameLowercase}?"
        >
            <@fdsRadio.radioYes path="form.hasContractToAdd"/>
            <@fdsRadio.radioNo path="form.hasContractToAdd"/>
        </@fdsRadio.radioGroup>
        <@fdsAction.submitButtons
          primaryButtonText="Continue"
          secondaryLinkText="Back to task list"
          linkSecondaryAction=true
          linkSecondaryActionUrl=springUrl(backToTaskListUrl)
        />
    </@fdsForm.htmlForm>
</@defaultPage>
