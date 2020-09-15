<#include '../../layout.ftl'/>

<#macro upcomingTenderSummary view tenderName="Upcoming tender" showValidationAndActions=false>
  <@fdsCheckAnswers.checkAnswers >
    <#if showValidationAndActions>
      <div class="summary-list__actions">
        <@fdsAction.link linkText=view.getEditLink().getLinkText() linkUrl=springUrl(view.getEditLink().url) linkScreenReaderText=tenderName />
        <@fdsAction.link linkText=view.getDeleteLink().getLinkText() linkUrl=springUrl(view.getDeleteLink().url) linkScreenReaderText=tenderName />
      </div>
      <#if view.isValid()?has_content && !view.isValid()>
        <#assign errorId = "upcoming-tender-" + view.getDisplayOrder()/>
        <span class="govuk-error-message" id=${errorId} >
          <span class="govuk-visually-hidden">Error:</span>${tenderName} is incomplete
        </span>
      </#if>
    </#if>

    <#if view.getTenderFunction()?has_content>
      <@fdsCheckAnswers.checkAnswersRow keyText="Tender function" actionText="" actionUrl="" screenReaderActionText="">
          ${view.getTenderFunction()}
      </@fdsCheckAnswers.checkAnswersRow>
    </#if>

    <#if view.getDescriptionOfWork()?has_content>
      <@fdsCheckAnswers.checkAnswersRow keyText="Description of work" actionText="" actionUrl="" screenReaderActionText="">
          ${view.getDescriptionOfWork()}
      </@fdsCheckAnswers.checkAnswersRow>
    </#if>

    <#if view.getEstimatedTenderDate()?has_content>
      <@fdsCheckAnswers.checkAnswersRow keyText="Estimated tender date" actionText="" actionUrl="" screenReaderActionText="">
          ${view.getEstimatedTenderDate()}
      </@fdsCheckAnswers.checkAnswersRow>
    </#if>

    <#if view.getContractBand()?has_content>
      <@fdsCheckAnswers.checkAnswersRow keyText="Contract band" actionText="" actionUrl="" screenReaderActionText="">
          ${view.getContractBand()}
      </@fdsCheckAnswers.checkAnswersRow>
    </#if>

    <#if view.getContactName()?has_content>
      <@fdsCheckAnswers.checkAnswersRow keyText="Name" actionText="" actionUrl="" screenReaderActionText="">
          ${view.getContactName()}
      </@fdsCheckAnswers.checkAnswersRow>
    </#if>
    <#if view.getPhoneNumber()?has_content>
      <@fdsCheckAnswers.checkAnswersRow keyText="Phone number" actionText="" actionUrl="" screenReaderActionText="">
          ${view.getPhoneNumber()}
      </@fdsCheckAnswers.checkAnswersRow>
    </#if>
    <#if view.getJobTitle()?has_content >
      <@fdsCheckAnswers.checkAnswersRow keyText="Job title" actionText="" actionUrl="" screenReaderActionText="">
          ${view.getJobTitle()}
      </@fdsCheckAnswers.checkAnswersRow>
    </#if>

    <#if view.getEmailAddress()?has_content >
      <@fdsCheckAnswers.checkAnswersRow keyText="Email address" actionText="" actionUrl="" screenReaderActionText="">
          ${view.getEmailAddress()}
      </@fdsCheckAnswers.checkAnswersRow>
    </#if>
  </@fdsCheckAnswers.checkAnswers>
</#macro>