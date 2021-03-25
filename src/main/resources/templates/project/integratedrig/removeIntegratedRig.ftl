<#include '../../layout.ftl'>
<#import '_integratedRigSummary.ftl' as integratedRigSummary>

<@defaultPage
  htmlTitle=pageName
  pageHeading="Are you sure you want to remove integrated rig ${displayOrder}?"
  breadcrumbs=true
  twoThirdsColumn=true
>
  <@integratedRigSummary.integratedRigSummary
    integratedRigView=integratedRigView
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
