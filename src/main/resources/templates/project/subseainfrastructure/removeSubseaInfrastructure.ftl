<#include '../../layout.ftl'>
<#import '_subseaInfrastructureSummary.ftl' as subseaInfrastructureSummary>

<@defaultPage
  htmlTitle=pageName
  pageHeading="Are you sure you want to remove subsea infrastructure ${displayOrder}?"
  breadcrumbs=true
  twoThirdsColumn=true
>
  <@subseaInfrastructureSummary.subseaInfrastructureSummary
    subseaInfrastructureView=subseaInfrastructureView
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