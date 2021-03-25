<#include '../layout.ftl'>
<#import '_communicationSummaryView.ftl' as communicationSummary>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle topNavigation=true fullWidthColumn=true breadcrumbs=true>
  <@communicationSummary.sentCommunicationSummary sentCommunicationView=sentCommunicationView />
  <@fdsAction.link
    linkText="Back to communications"
    linkUrl=springUrl(communicationsUrl)
    linkClass="govuk-link govuk-link--button"/>
</@defaultPage>