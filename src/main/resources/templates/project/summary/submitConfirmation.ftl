<#include '../../layout.ftl'>

<#-- @ftlvariable name="feedbackUrl" type="String" -->
<#-- @ftlvariable name="workAreaUrl" type="String" -->
<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->
<#-- @ftlvariable name="projectSubmissionSummaryView" type="uk.co.ogauthority.pathfinder.model.view.summary.ProjectSubmissionSummaryView" -->
<#-- @ftlvariable name="isUpdate" type="Boolean" -->

<#if isUpdate>
  <#assign title = "Project update submitted">
<#else>
  <#assign title = "Project submitted">
</#if>

<@defaultPage htmlTitle=title pageHeading="" breadcrumbs=false phaseBanner=false>
  <@fdsPanel.panel
    panelTitle=title
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

  <@serviceFeedbackLink.feedbackLink feedbackUrl=feedbackUrl/>

  <@fdsAction.link
    linkClass="govuk-link govuk-!-font-size-19"
    linkText="Back to work area"
    linkUrl=springUrl(workAreaUrl)
  />
</@defaultPage>
