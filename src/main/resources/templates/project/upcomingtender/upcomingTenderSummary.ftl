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

    <@summaryAnswerRow prompt="Tender function" value=view.getTenderFunction()!"" />
    <@summaryAnswerRow prompt="Description of work" value=view.getDescriptionOfWork()!"" />
    <@summaryAnswerRow prompt="Estimated tender date" value=view.getEstimatedTenderDate()!"" />
    <@summaryAnswerRow prompt="Contract band" value=view.getContractBand()!"" />
    <@summaryAnswerRow prompt="Name" value=view.getContactName()!"" />
    <@summaryAnswerRow prompt="Phone number" value=view.getPhoneNumber()!"" />
    <@summaryAnswerRow prompt="Job title" value=view.getJobTitle()!"" />
    <@summaryAnswerRow prompt="Email address" value=view.getEmailAddress()!"" />

  </@fdsCheckAnswers.checkAnswers>
</#macro>

<#macro summaryAnswerRow prompt value>
  <@fdsCheckAnswers.checkAnswersRow keyText=prompt actionText="" actionUrl="" screenReaderActionText="">
    ${value}
  </@fdsCheckAnswers.checkAnswersRow>
</#macro>