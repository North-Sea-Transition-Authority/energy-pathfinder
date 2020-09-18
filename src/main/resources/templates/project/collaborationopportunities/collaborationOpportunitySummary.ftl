<#include '../../layout.ftl'/>

<#macro collaborationOpportunitySummary view opportunityName="Collaboartion opportunity" showValidationAndActions=false>
  <@fdsCheckAnswers.checkAnswers >
    <#if showValidationAndActions>
      <div class="summary-list__actions">
        <@fdsAction.link linkText=view.getEditLink().getLinkText() linkUrl=springUrl(view.getEditLink().url) linkScreenReaderText=opportunityName />
        <@fdsAction.link linkText=view.getDeleteLink().getLinkText() linkUrl=springUrl(view.getDeleteLink().url) linkScreenReaderText=opportunityName />
      </div>
      <#if view.isValid()?has_content && !view.isValid()>
        <span class="govuk-error-message">
          <span class="govuk-visually-hidden">Error:</span>${opportunityName} is incomplete
        </span>
      </#if>
    </#if>

    <@summaryAnswerRow prompt="Opportunity function" value=view.getFunction()!"" />
    <@summaryAnswerRow prompt="Description of work" value=view.getDescriptionOfWork()!"" />
    <@summaryAnswerRow prompt="Estimated service date" value=view.getEstimatedServiceDate()!"" />
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