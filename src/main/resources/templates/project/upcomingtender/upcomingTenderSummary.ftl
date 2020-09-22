<#include '../../layout.ftl'/>

<#macro upcomingTenderSummary view tenderName="Upcoming tender" showValidationAndActions=false>
  <@fdsCheckAnswers.checkAnswers >
    <#if showValidationAndActions>
      <div class="summary-list__actions">
        <@fdsAction.link linkText=view.getEditLink().getLinkText() linkUrl=springUrl(view.getEditLink().url) linkScreenReaderText=tenderName />
        <@fdsAction.link linkText=view.getDeleteLink().getLinkText() linkUrl=springUrl(view.getDeleteLink().url) linkScreenReaderText=tenderName />
      </div>
      <#if view.isValid()?has_content && !view.isValid()>
        <span class="govuk-error-message">
          <span class="govuk-visually-hidden">Error:</span>${tenderName} is incomplete
        </span>
      </#if>
    </#if>

    <@summaryAnswer.summaryAnswerRow prompt="Tender function" value=view.tenderFunction!"" />
    <@summaryAnswer.summaryAnswerRow prompt="Description of work" value=view.descriptionOfWork!"" />
    <@summaryAnswer.summaryAnswerRow prompt="Estimated tender date" value=view.estimatedTenderDate!"" />
    <@summaryAnswer.summaryAnswerRow prompt="Contract band" value=view.contractBand!"" />
    <@summaryAnswer.summaryAnswerRow prompt="Name" value=view.contactDetailView.name!"" />
    <@summaryAnswer.summaryAnswerRow prompt="Phone number" value=view.contactDetailView.phoneNumber!"" />
    <@summaryAnswer.summaryAnswerRow prompt="Job title" value=view.contactDetailView.jobTitle!"" />
    <@summaryAnswer.summaryAnswerRow prompt="Email address" value=view.contactDetailView.emailAddress!"" />

  </@fdsCheckAnswers.checkAnswers>
</#macro>