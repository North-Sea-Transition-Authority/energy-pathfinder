<#include '../../layout.ftl'>

<#-- @ftlvariable name="feedbackUrl" type="String" -->
<#-- @ftlvariable name="workAreaUrl" type="String" -->
<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->
<#-- @ftlvariable name="projectSubmissionSummaryView" type="uk.co.ogauthority.pathfinder.model.view.submission.ProjectSubmissionSummaryView" -->
<#-- @ftlvariable name="isUpdate" type="Boolean" -->
<#-- @ftlvariable name="projectTypeDisplayName" type="String" -->
<#-- @ftlvariable name="projectTypeDisplayNameLowercase" type="String" -->

<#assign projectTypeDisplayName = projectTypeDisplayName />
<#assign projectTypeDisplayNameLowercase = projectTypeDisplayNameLowercase />

<#if isUpdate>
  <#assign title = "${projectTypeDisplayName} update submitted">
<#else>
  <#assign title = "${projectTypeDisplayName} submitted">
</#if>

<@defaultPage htmlTitle=title pageHeading="" breadcrumbs=false phaseBanner=false>
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

  <@serviceFeedbackLink.feedbackLink feedbackUrl=feedbackUrl/>

  <@fdsAction.link
    linkClass="govuk-link govuk-!-font-size-19"
    linkText="Back to work area"
    linkUrl=springUrl(workAreaUrl)
  />
</@defaultPage>
