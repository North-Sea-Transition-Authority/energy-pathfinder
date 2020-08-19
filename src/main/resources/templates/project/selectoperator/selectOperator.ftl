<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Select an operator" backLink=backLink breadcrumbs=breadCrumbs twoThirdsColumn=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
      <#if userIsInSingleTeam>
        <@fdsWarning.warning>
          You are only in one team so cannot change the operator the project is for.
        </@fdsWarning.warning>
      </#if>

      <@fdsSearchSelector.searchSelectorRest path="form.organisationGroup" labelText="Select an operator for the project" selectorMinInputLength=0 restUrl=springUrl(operatorsRestUrl)  preselectedItems=preselectedOperator!{} />

      <@fdsAction.submitButtons
        primaryButtonText=primaryButtonText
        linkSecondaryAction=true
        secondaryLinkText="Cancel"
        linkSecondaryActionUrl=springUrl(cancelUrl)
      />
  </@fdsForm.htmlForm>
</@defaultPage>