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

      <@fdsCheckAnswers.checkAnswersRow keyText="Tender function" actionText="" actionUrl="" screenReaderActionText="">
        <#if view.getTenderFunction()?has_content>
          ${view.getTenderFunction()}
        </#if>
      </@fdsCheckAnswers.checkAnswersRow>

      <@fdsCheckAnswers.checkAnswersRow keyText="Description of work" actionText="" actionUrl="" screenReaderActionText="">
        <#if view.getDescriptionOfWork()?has_content>
          ${view.getDescriptionOfWork()}
        </#if>
      </@fdsCheckAnswers.checkAnswersRow>

      <@fdsCheckAnswers.checkAnswersRow keyText="Estimated tender date" actionText="" actionUrl="" screenReaderActionText="">
        <#if view.getEstimatedTenderDate()?has_content>
          ${view.getEstimatedTenderDate()}
        </#if>
      </@fdsCheckAnswers.checkAnswersRow>

      <@fdsCheckAnswers.checkAnswersRow keyText="Contract band" actionText="" actionUrl="" screenReaderActionText="">
        <#if view.getContractBand()?has_content>
          ${view.getContractBand()}
        </#if>
      </@fdsCheckAnswers.checkAnswersRow>

      <@fdsCheckAnswers.checkAnswersRow keyText="Name" actionText="" actionUrl="" screenReaderActionText="">
        <#if view.getContactName()?has_content>
          ${view.getContactName()}
        </#if>
      </@fdsCheckAnswers.checkAnswersRow>

      <@fdsCheckAnswers.checkAnswersRow keyText="Phone number" actionText="" actionUrl="" screenReaderActionText="">
        <#if view.getPhoneNumber()?has_content>
          ${view.getPhoneNumber()}
        </#if>
      </@fdsCheckAnswers.checkAnswersRow>

      <@fdsCheckAnswers.checkAnswersRow keyText="Job title" actionText="" actionUrl="" screenReaderActionText="">
        <#if view.getJobTitle()?has_content >
          ${view.getJobTitle()}
        </#if>
      </@fdsCheckAnswers.checkAnswersRow>

      <@fdsCheckAnswers.checkAnswersRow keyText="Email address" actionText="" actionUrl="" screenReaderActionText="">
        <#if view.getEmailAddress()?has_content >
          ${view.getEmailAddress()}
        </#if>
      </@fdsCheckAnswers.checkAnswersRow>
  </@fdsCheckAnswers.checkAnswers>
</#macro>