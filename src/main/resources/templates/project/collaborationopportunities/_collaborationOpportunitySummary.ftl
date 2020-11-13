<#include '../../layout.ftl'/>

<#macro collaborationOpportunitySummary
  view
  opportunityName="Collaboration opportunity"
  showHeader=false
  showActions=false
  showTag=false
  headingSize="h2"
  headingClass="govuk-heading-l"
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix="collaboration-opportunity"
    headingPrefix=opportunityName
    summaryView=view
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Opportunity function">
      <#if showTag>
        <@stringWithTag.stringWithTag stringWithTag=view.function />
      <#else>
        ${view.function.value!""}
      </#if>
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
    <@checkAnswers.checkAnswersRowNoActions prompt="Description of work" value=view.descriptionOfWork!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Urgent response required" value=view.urgentResponseNeeded!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Name" value=view.contactDetailView.name!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Phone number" value=view.contactDetailView.phoneNumber!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Job title" value=view.contactDetailView.jobTitle!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Email address" value=view.contactDetailView.emailAddress!"" />
    <@checkAnswers.checkAnswersUploadedFileViewNoActions uploadedFileView=view.uploadedFileViews[0]!"" />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>