<#include '../../layout.ftl'>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="projectSetupUrl" type="String" -->
<#-- @ftlvariable name="pageName" type="String" -->
<#-- @ftlvariable name="contributorsRestUrl" type="String" -->

<#assign noItemTextContent>
  <p class="govuk-body">No contributor added</p>
    <@setupProjectGuidance.minimumRequirementText itemRequiredText="project contributors" linkUrl=springUrl(projectSetupUrl)/>
</#assign>

<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@fdsAddToList.addToList
      pathForList="form.contributors"
      pathForSelector="form.contributorsSelect"
      alreadyAdded=alreadyAddedContributors
      title=""
      itemName="Contributor"
      noItemText=noItemTextContent
      invalidItemText="This contributor is invalid"
      addToListId="contributor-table"
      selectorLabelText="Add an organisation that can contribute to this project"
      selectorHintText=""
      restUrl=springUrl(contributorsRestUrl)
    />
    <@fdsDetails.summaryDetails summaryTitle="The organisation I want to contribute to this project is not listed">
      <p class="govuk-body">
        If the organisation you want to contribute to this project is not shown in the list then ask the organisation
        in question to contact the <@mailTo.mailToLink linkText=service.customerMnemonic mailToEmailAddress=regulatorEmailAddress />
      </p>
    </@fdsDetails.summaryDetails>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>