<#include '../../layout.ftl'/>

<#macro collaborationOpportunitySummary view opportunityName="Collaboration opportunity" showHeader=false showActions=false>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix="collaboration-opportunity"
    headingPrefix=opportunityName
    summaryView=view
    showHeader=showHeader
    showActions=showActions
  >
    <@checkAnswers.checkAnswersRowNoActions prompt="Opportunity function" value=view.function!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Description of work" value=view.descriptionOfWork!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Urgent response required" value=view.urgentResponseNeeded!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Name" value=view.contactDetailView.name!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Phone number" value=view.contactDetailView.phoneNumber!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Job title" value=view.contactDetailView.jobTitle!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Email address" value=view.contactDetailView.emailAddress!"" />
    <@checkAnswers.checkAnswersUploadedFileViewNoActions uploadedFileView=view.uploadedFileViews[0]!"" />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>