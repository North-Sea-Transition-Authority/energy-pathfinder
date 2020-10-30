<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Project submitted" pageHeading="" breadcrumbs=false>
  <@fdsPanel.panel
    panelTitle="Project submitted"
    panelText="You have submitted project ${projectSubmissionSummaryView.projectTitle}"
  />

  <ul class="govuk-list">
    <li>Submitted date and time: ${projectSubmissionSummaryView.formattedSubmittedTimestamp}</li>
    <li>Submitted by ${projectSubmissionSummaryView.submittedBy}</li>
  </ul>

  <h2 class="govuk-heading-m">What happens next</h2>

  <p class="govuk-body">
    Your project has been sent to ${service.customerMnemonic!"the regulator"} for review.
  </p>

  <@fdsAction.link linkClass="govuk-link govuk-!-font-size-19" linkText="Back to work area" linkUrl=springUrl(workAreaUrl)/>
</@defaultPage>
