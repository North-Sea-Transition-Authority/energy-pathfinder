<#include '../layout.ftl'>
<#import '_communicationSummaryView.ftl' as communicationSummary>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle topNavigation=true twoThirdsColumn=true breadcrumbs=true>
  <@communicationSummary.communicationSummary communicationView=communicationView />
  <@fdsForm.htmlForm>
    <@fdsAction.submitButtons
      primaryButtonText="Send email"
      linkSecondaryAction=true
      secondaryLinkText="Previous"
      linkSecondaryActionUrl=springUrl(previousUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>