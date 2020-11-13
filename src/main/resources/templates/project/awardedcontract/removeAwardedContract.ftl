<#include '../../layout.ftl'>
<#import '_awardedContractSummary.ftl' as awardedContractSummary>

<@defaultPage
  htmlTitle="Remove awarded contract"
  pageHeading="Are you sure you want to remove awarded contract ${displayOrder}?"
  breadcrumbs=true
  twoThirdsColumn=true
>
  <@awardedContractSummary.awardedContractSummary
    awardedContractView=awardedContractView
    showHeader=false
    showActions=false
  />
  <@fdsForm.htmlForm>
    <@fdsAction.submitButtons
      primaryButtonText="Remove"
      primaryButtonClass="govuk-button govuk-button--warning"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl=springUrl(cancelUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>