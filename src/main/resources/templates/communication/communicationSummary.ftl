<#include '../layout.ftl'>
<#import '_communicationSummaryView.ftl' as communicationSummary>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle topNavigation=true twoThirdsColumn=true breadcrumbs=true>
  <@communicationSummary.sentCommunicationSummary sentCommunicationView=sentCommunicationView />
  <@fdsAction.link
    linkText="Back to communications"
    linkUrl=springUrl(communicationsUrl)
    linkClass="govuk-link--button"/>
</@defaultPage>