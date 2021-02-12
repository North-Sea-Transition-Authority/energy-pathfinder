<#include '../../layout.ftl'>
<#import '_plugAbandonmentScheduleSummary.ftl' as plugAbandonmentScheduleSummary>

<@defaultPage
  htmlTitle=pageName
  pageHeading="Are you sure you want to remove plug abandonment schedule ${displayOrder}?"
  breadcrumbs=true
  twoThirdsColumn=true
>
  <@plugAbandonmentScheduleSummary.plugAbandonmentScheduleSummary
    plugAbandonmentScheduleView=plugAbandonmentScheduleView
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
