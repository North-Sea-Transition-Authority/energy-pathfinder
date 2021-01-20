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
    displayOrder=view.displayOrder
    isValid=view.valid!""
    summaryLinkList=view.summaryLinks
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
    <@checkAnswers.checkAnswersRowNoActions prompt="Name" value=view.contactName!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Phone number" value=view.contactPhoneNumber!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Job title" value=view.contactJobTitle!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Email address" value=view.contactEmailAddress!"" />
    <@checkAnswers.checkAnswersUploadedFileViewNoActions
      fileUrlFieldValue=(view.uploadedFileViews[0].fileUrl)!""
      fileNameFieldValue=(view.uploadedFileViews[0].fileName)!""
      fileDescriptionFieldValue=(view.uploadedFileViews[0].fileDescription)!""
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>