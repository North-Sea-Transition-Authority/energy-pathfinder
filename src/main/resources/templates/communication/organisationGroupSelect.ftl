<#include '../layout.ftl'>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle topNavigation=true twoThirdsColumn=true breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
    <@fdsCheckbox.checkboxes
      path="form.organisationGroups"
      checkboxes=organisationGroups
      smallCheckboxes=true
    />
    <@fdsAction.submitButtons
      primaryButtonText="Next"
      linkSecondaryAction=true
      secondaryLinkText="Previous"
      linkSecondaryActionUrl=springUrl(previousUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>