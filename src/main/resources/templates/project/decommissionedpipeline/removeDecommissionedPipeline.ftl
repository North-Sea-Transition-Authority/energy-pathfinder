<#include '../../layout.ftl'>
<#import '_decommissionedPipelineSummary.ftl' as decommissionedPipelineSummary>

<@defaultPage
  htmlTitle=pageName
  pageHeading="Are you sure you want to remove pipeline ${displayOrder}?"
  breadcrumbs=true
  twoThirdsColumn=true
>
  <@decommissionedPipelineSummary.decommissionedPipelineSummary
    decommissionedPipelineView=decommissionedPipelineView
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
