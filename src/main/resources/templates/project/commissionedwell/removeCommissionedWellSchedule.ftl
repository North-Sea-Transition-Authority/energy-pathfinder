<#include '../../layout.ftl'>
<#import '_commissionedWellScheduleSummary.ftl' as commissionedWellScheduleSummary>

<#-- @ftlvariable name="pageName" type="String" -->
<#-- @ftlvariable name="displayOrder" type="Integer" -->
<#-- @ftlvariable name="commissionedWellScheduleView" type="uk.co.ogauthority.pathfinder.model.view.commissionedwell.CommissionedWellScheduleView" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->

<@defaultPage
  htmlTitle=pageName
  pageHeading="Are you sure you want to remove well commissioning schedule ${displayOrder}?"
  breadcrumbs=true
  twoThirdsColumn=true
>
  <@commissionedWellScheduleSummary.commissionedWellScheduleSummary
    commissionedWellScheduleView=commissionedWellScheduleView
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
