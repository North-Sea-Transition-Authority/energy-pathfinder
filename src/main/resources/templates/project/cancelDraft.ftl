<#include '../layout.ftl'>

<#if isUpdate>
  <#assign title = "Cancel draft update">
  <#assign pageHeading = "Are you sure you want to cancel this draft project update?">
  <#assign summaryTitle = "View the draft project update to be cancelled">
<#else>
  <#assign title = "Cancel draft project">
  <#assign pageHeading = "Are you sure you want to cancel this draft project?">
  <#assign summaryTitle = "View the draft project to be cancelled">
</#if>

<@defaultPage
  htmlTitle=title
  pageHeading=pageHeading
  breadcrumbs=false
  twoThirdsColumn=true
>
  <@fdsDetails.summaryDetails summaryTitle=summaryTitle>
    <@noEscapeHtml.noEscapeHtml html=projectSummaryHtml />
  </@fdsDetails.summaryDetails>

  <@fdsForm.htmlForm>
    <@fdsAction.submitButtons
      primaryButtonText=title
      primaryButtonClass="govuk-button govuk-button--warning"
      secondaryLinkText="Back to task list"
      linkSecondaryAction=true
      linkSecondaryActionUrl=springUrl(backToTaskListUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>
