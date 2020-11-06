<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Select an operator" backLink=backLink breadcrumbs=breadCrumbs  twoThirdsColumn=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest path="form.organisationGroup" labelText="Who is the operator for the project" pageHeading=true labelHeadingClass="govuk-label--l" selectorMinInputLength=0 restUrl=springUrl(operatorsRestUrl)  preselectedItems=preselectedOperator!{} />

    <@fdsDetails.summaryDetails summaryTitle="The operator I want to create a project for is not listed">
      <p class="govuk-body"> If the operator you need to create a project for is not shown in the list then you must contact the operator to provide you with access to their organisation.</p>
    </@fdsDetails.summaryDetails>

    <@fdsAction.submitButtons
      primaryButtonText=primaryButtonText
      linkSecondaryAction=true
      secondaryLinkText="Cancel"
      linkSecondaryActionUrl=springUrl(cancelUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>