<#include '../../layout.ftl'>

<#macro awardedContractSummary awardedContractView showHeader=true showActions=true headingSize="h2" headingClass="govuk-heading-l" showTag=false>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix="awarded-contract"
    headingPrefix="Awarded contract"
    summaryView=awardedContractView
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@checkAnswers.checkAnswersRowNoActions prompt="Contractor name" value=awardedContractView.contractorName!"" />
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Contract function">
      <#if showTag>
        <@stringWithTag.stringWithTag stringWithTag=awardedContractView.contractFunction />
      <#else>
        ${awardedContractView.contractFunction.value!""}
      </#if>
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
    <@checkAnswers.checkAnswersRowNoActions prompt="Description of work" value=awardedContractView.descriptionOfWork!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Date awarded" value=awardedContractView.dateAwarded!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Contract band" value=awardedContractView.contractBand!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Contact name" value=awardedContractView.contactDetailView.name!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Phone number" value=awardedContractView.contactDetailView.phoneNumber!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Job title" value=awardedContractView.contactDetailView.jobTitle!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Email address" value=awardedContractView.contactDetailView.emailAddress!"" />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>