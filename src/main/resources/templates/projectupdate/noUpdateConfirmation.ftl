<#include '../layout.ftl'>

<#-- @ftlvariable name="feedbackUrl" type="String" -->
<#-- @ftlvariable name="workAreaUrl" type="String" -->
<#-- @ftlvariable name="projectNoUpdateSummaryView" type="uk.co.ogauthority.pathfinder.model.view.projectupdate.ProjectNoUpdateSummaryView" -->

<#assign title = "Confirmed no changes" />

<@defaultPage htmlTitle=title pageHeading="" breadcrumbs=false phaseBanner=false>
  <@fdsPanel.panel
    panelTitle=title
    panelText="${projectNoUpdateSummaryView.projectTitle}"
  />

  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.checkAnswersRowNoActions prompt="Submitted date and time" value=projectNoUpdateSummaryView.formattedSubmittedTimestamp!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Submitted by" value=projectNoUpdateSummaryView.submittedBy!"" />
  </@fdsCheckAnswers.checkAnswers>

  <h2 class="govuk-heading-m">What happens next</h2>

  <p class="govuk-body">
    You will not receive further reminders this quarter to submit an update for this project.
  </p>

  <@serviceFeedbackLink.feedbackLink feedbackUrl=feedbackUrl/>

  <@fdsAction.link linkClass="govuk-link govuk-!-font-size-19" linkText="Back to work area" linkUrl=springUrl(workAreaUrl)/>
</@defaultPage>
