<#include '../../layout.ftl'/>

<#macro upcomingTenderSummary view tenderName="Upcoming tender" showHeader=false showActions=false>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix="upcoming-tender"
    headingPrefix=tenderName
    summaryView=view
    showHeader=showHeader
    showActions=showActions
  >
    <@checkAnswers.checkAnswersRowNoActions prompt="Tender function" value=view.tenderFunction!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Description of work" value=view.descriptionOfWork!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Estimated tender date" value=view.estimatedTenderDate!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Contract band" value=view.contractBand!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Name" value=view.contactDetailView.name!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Phone number" value=view.contactDetailView.phoneNumber!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Job title" value=view.contactDetailView.jobTitle!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Email address" value=view.contactDetailView.emailAddress!"" />
    <@checkAnswers.checkAnswersUploadedFileViewNoActions uploadedFileView=view.uploadedFileViews[0]!"" />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>