<#include '../layout.ftl'>

<#assign title = pageHeading>

<@defaultPage htmlTitle=title breadcrumbs=true fullWidthColumn=true errorItems=errorList>

  <@noEscapeHtml.noEscapeHtml html=projectHeaderHtml />

  <h2 class="govuk-heading-l">${title}</h2>

  <@fdsInsetText.insetText insetTextClass="govuk-inset-text--yellow">
    By changing this ${projectTypeDisplayNameLowercase}'s operator, any in progress updates will no longer be able to be submitted.
  </@fdsInsetText.insetText>

  <@fdsDataItems.dataItem>
    <@fdsDataItems.dataValues key="Current ${projectTypeDisplayNameLowercase} operator" value=currentOperator />
  </@fdsDataItems.dataItem>

  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest
      path="form.newOrganisationGroup"
      labelText="Who is the new operator for the ${projectTypeDisplayNameLowercase}?"
      selectorMinInputLength=0
      restUrl=springUrl(operatorsRestUrl)
      preselectedItems=preselectedOperator!{}
    />

    <@fdsTextarea.textarea
      path="form.transferReason"
      labelText="What is the reason you are changing this ${projectTypeDisplayNameLowercase}'s operator?"
    />

    <@fdsAction.submitButtons
      primaryButtonText="Save and complete"
      linkSecondaryAction=true
      secondaryLinkText="Cancel"
      linkSecondaryActionUrl=springUrl(cancelUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>
