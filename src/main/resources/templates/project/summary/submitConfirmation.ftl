<#include '../../layout.ftl'>

<#assign projectTypeDisplayName = projectTypeDisplayName />
<#assign projectTypeDisplayNameLowercase = projectTypeDisplayNameLowercase />

<#if isUpdate>
  <#assign title = "${projectTypeDisplayName} update submitted">
<#else>
  <#assign title = "${projectTypeDisplayName} submitted">
</#if>

<@defaultPage htmlTitle=title pageHeading="" breadcrumbs=false>
  <@fdsPanel.panel
    panelTitle=title
    panelText="${projectSubmissionSummaryView.projectDisplayName}"
  />

  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.checkAnswersRowNoActions prompt="Submitted date and time" value=projectSubmissionSummaryView.formattedSubmittedTimestamp!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Submitted by" value=projectSubmissionSummaryView.submittedBy!"" />
  </@fdsCheckAnswers.checkAnswers>

  <h2 class="govuk-heading-m">What happens next</h2>

  <p class="govuk-body">
    Your ${projectTypeDisplayNameLowercase} has been sent to the ${service.customerName!"regulator"} to review.
  </p>

  <p class="govuk-body">
    Once reviewed your ${projectTypeDisplayNameLowercase} will be published on their website.
  </p>

  <@fdsAction.link linkClass="govuk-link govuk-!-font-size-19" linkText="Back to work area" linkUrl=springUrl(workAreaUrl)/>
</@defaultPage>
