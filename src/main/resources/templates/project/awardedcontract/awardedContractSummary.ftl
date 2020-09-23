<#include '../../layout.ftl'>

<#macro awardedContractSummary awardedContractView showHeader=true showActions=true>
  <#assign contractHeading = "Awarded contract " + awardedContractView.displayOrder />
  <div class="summary-list__item">
    <#if showHeader>
      <h2 id="awarded-contract-${awardedContractView.displayOrder}" class="govuk-heading-l summary-list__heading">
        ${contractHeading}
      </h2>
    </#if>
    <@fdsCheckAnswers.checkAnswers>
      <#if showActions>
        <div class="summary-list__actions">
          <#list awardedContractView.summaryLinks as summaryLink>
            <@fdsAction.link
              linkText=summaryLink.linkText
              linkUrl=springUrl(summaryLink.url)
              linkScreenReaderText=contractHeading
            />
          </#list>
        </div>
      </#if>
      <#if awardedContractView.valid?has_content && !awardedContractView.valid>
        <span class="govuk-error-message">
          <span class="govuk-visually-hidden">Error:</span>${contractHeading} is incomplete
        </span>
      </#if>
      <@checkAnswers.checkAnswersRowNoActions prompt="Contractor name" value=awardedContractView.contractorName!"" />
      <@checkAnswers.checkAnswersRowNoActions prompt="Contract function" value=awardedContractView.contractFunction!"" />
      <@checkAnswers.checkAnswersRowNoActions prompt="Description of work" value=awardedContractView.descriptionOfWork!"" />
      <@checkAnswers.checkAnswersRowNoActions prompt="Date awarded" value=awardedContractView.dateAwarded!"" />
      <@checkAnswers.checkAnswersRowNoActions prompt="Contract band" value=awardedContractView.contractBand!"" />
      <@checkAnswers.checkAnswersRowNoActions prompt="Contact name" value=awardedContractView.contactDetailView.name!"" />
      <@checkAnswers.checkAnswersRowNoActions prompt="Phone number" value=awardedContractView.contactDetailView.phoneNumber!"" />
      <@checkAnswers.checkAnswersRowNoActions prompt="Job title" value=awardedContractView.contactDetailView.jobTitle!"" />
      <@checkAnswers.checkAnswersRowNoActions prompt="Email address" value=awardedContractView.contactDetailView.emailAddress!"" />
    </@fdsCheckAnswers.checkAnswers>
  </div>
</#macro>