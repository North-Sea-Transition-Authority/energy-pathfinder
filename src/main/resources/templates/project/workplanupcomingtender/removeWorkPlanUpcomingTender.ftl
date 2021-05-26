<#include '../../layout.ftl'>
<#import './_workPlanUpcomingTenderSummary.ftl' as tenderSummary>

<#assign title = "Are you sure you want to remove upcoming tender ${displayOrder}?"/>

<@defaultPage htmlTitle=title pageHeading=title breadcrumbs=true>

  <@tenderSummary.workPlanUpcomingTenderSummary view=view showHeader=false showActions=false/>

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