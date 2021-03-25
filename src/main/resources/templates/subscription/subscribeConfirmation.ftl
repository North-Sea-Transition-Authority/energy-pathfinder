<#include '../layout.ftl'>
<#import '_subscribeSummary.ftl' as subscribe>

<#assign serviceName = service.serviceName />
<#assign pageHeading = "${serviceName} subscription" />

<@defaultPage htmlTitle=pageHeading pageHeading="" breadcrumbs=false topNavigation=false>
  <@fdsFlash.flash
    flashTitle="You have successfully subscribed to ${serviceName}"
    flashClass="fds-flash--green"
  >
    <@subscribe._subscriptionSummaryText pronoun="You" contentHasBottomMargin=false />
  </@fdsFlash.flash>
</@defaultPage>
