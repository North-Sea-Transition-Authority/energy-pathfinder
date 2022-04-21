<#include '../../layout.ftl'>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="projectSetupUrl" type="String" -->
<#-- @ftlvariable name="pageName" type="String" -->
<#-- @ftlvariable name="contributorsRestUrl" type="String" -->

<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@fdsRadio.radioGroup
    path="form.hasProjectContributors"
    labelText="Do you want to allow other organisation to contribute to this ${projectTypeDisplayNameLowercase}?"
    hintText="Nominated organisation will be able to add items such as upcoming tenders and collaboration opportunities to this ${projectTypeDisplayNameLowercase}"
    hiddenContent=true
    >
      <@fdsRadio.radioYes path="form.hasProjectContributors">
        <@fdsAddToList.addToList
          pathForList="form.contributors"
          pathForSelector="form.contributorsSelect"
          alreadyAdded=alreadyAddedContributors
          title=""
          itemName="Contributor"
          noItemText="No contributor added"
          invalidItemText="This contributor is invalid"
          addToListId="contributor-table"
          selectorLabelText="Add an organisation that can contribute to this ${projectTypeDisplayNameLowercase}"
          selectorHintText=""
          restUrl=springUrl(contributorsRestUrl)
          selectorNestingPath="form.hasProjectContributors"
          />
      </@fdsRadio.radioYes>
      <@fdsRadio.radioNo path="form.hasProjectContributors"/>
    </@fdsRadio.radioGroup>

    <@fdsDetails.summaryDetails summaryTitle="The organisation I want to contribute to this ${projectTypeDisplayNameLowercase} is not listed">
      <p class="govuk-body">
        If the organisation you want to contribute to this ${projectTypeDisplayNameLowercase} is not shown in the list then ask the organisation
        in question to contact the <@mailTo.mailToLink linkText=service.customerMnemonic mailToEmailAddress=regulatorEmailAddress />
      </p>
    </@fdsDetails.summaryDetails>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>