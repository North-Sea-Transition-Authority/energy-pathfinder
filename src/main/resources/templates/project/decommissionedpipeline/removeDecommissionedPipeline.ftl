<#include '../../layout.ftl'>
<#import 'decommissionedPipelineSummary.ftl' as decommissionedPipelineSummary>

<@defaultPage
  htmlTitle=pageName
  pageHeading="Are you sure you want to remove pipeline ${displayOrder}?"
  breadcrumbs=true
  twoThirdsColumn=true
>
  <div class="summary-list">
    <@decommissionedPipelineSummary.decommissionedPipelineSummary
      decommissionedPipelineView=decommissionedPipelineView
      showHeader=false
      showActions=false
    />
  </div>
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
