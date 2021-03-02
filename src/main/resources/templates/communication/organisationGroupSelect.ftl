<#include '../layout.ftl'>

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=""
  topNavigation=true
  twoThirdsColumn=true
  breadcrumbs=false
  backLink=true
  backLinkUrl=springUrl(previousUrl)
  errorItems=errorList
>
  <@fdsForm.htmlForm>
    <#assign checkboxContainerId="organisation-group-select" />
    <div id="${checkboxContainerId}">
      <@fdsFieldset.fieldset legendHeading=pageTitle legendHeadingClass="govuk-fieldset__legend--xl">
        <@checkboxToggler.checkboxToggler
          checkboxContainerId=checkboxContainerId
          prefixText="Select"
          selectAllLinkText="all"
          selectAllScreenReaderText="Select all of the available operators"
          selectNoneLinkText="none"
          selectNoneScreenReaderText="Select none of the available operators"
        />
        <@fdsCheckbox.checkboxes
          path="form.organisationGroups"
          checkboxes=organisationGroups
          smallCheckboxes=true
        />
      </@fdsFieldset.fieldset>
    </div>
    <@fdsAction.submitButtons
      primaryButtonText="Continue"
      linkSecondaryAction=true
      secondaryLinkText="Cancel"
      linkSecondaryActionUrl=springUrl(previousUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>