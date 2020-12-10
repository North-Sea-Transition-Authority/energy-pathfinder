<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Project submitted" pageHeading="" breadcrumbs=false>
  <@fdsPanel.panel
    panelTitle="Project submitted"
    panelText="${projectSubmissionSummaryView.projectTitle}"
  />

  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.checkAnswersRowNoActions prompt="Submitted date and time" value=projectSubmissionSummaryView.formattedSubmittedTimestamp!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Submitted by" value=projectSubmissionSummaryView.submittedBy!"" />
  </@fdsCheckAnswers.checkAnswers>

  <h2 class="govuk-heading-m">What happens next</h2>

  <p class="govuk-body">
    Your project has been sent to the ${service.customerName!"regulator"} to review.
  </p>

  <p class="govuk-body">
    Once reviewed your project will be published on their website.
  </p>

  <@fdsAction.link linkClass="govuk-link govuk-!-font-size-19" linkText="Back to work area" linkUrl=springUrl(workAreaUrl)/>
</@defaultPage>
