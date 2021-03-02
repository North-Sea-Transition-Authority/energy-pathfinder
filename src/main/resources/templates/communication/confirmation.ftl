<#include '../layout.ftl'>
<#import '_communicationSummaryView.ftl' as communicationSummary>

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  topNavigation=true
  fullWidthColumn=true
  breadcrumbs=false
  backLink=true
  backLinkUrl=springUrl(previousUrl)
>
  <@communicationSummary.communicationSummary communicationView=communicationView />
  <@fdsForm.htmlForm>
    <@fdsAction.submitButtons
      primaryButtonText="Send email"
      linkSecondaryAction=true
      secondaryLinkText="Cancel"
      linkSecondaryActionUrl=springUrl(previousUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>