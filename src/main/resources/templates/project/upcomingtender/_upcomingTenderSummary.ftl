<#include '../../layout.ftl'/>

<#assign defaultHeadingPrefix = "Upcoming tender"/>
<#assign idPrefix = "upcoming-tender"/>
<#assign defaultHeadingSize = "h2"/>
<#assign defaultHeadingClass = "govuk-heading-l"/>

<#macro upcomingTenderSummary
  view
  tenderName=defaultHeadingPrefix
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=tenderName
    displayOrder=view.displayOrder
    isValid=view.valid!""
    summaryLinkList=view.summaryLinks
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_upcomingTenderSummaryFields
      useDiffedField=false
      tenderFunction=view.tenderFunction
      descriptionOfWork=view.descriptionOfWork
      estimatedTenderDate=view.estimatedTenderDate
      contractBand=view.contractBand
      contactName=view.contactName
      contactPhoneNumber=view.contactPhoneNumber
      contactJobTitle=view.contactJobTitle
      contactEmailAddress=view.contactEmailAddress
      uploadedFileUrl=(view.uploadedFileViews[0].fileUrl)!""
      uploadedFileName=(view.uploadedFileViews[0].fileName)!""
      uploadedFileDescription=(view.uploadedFileViews[0].fileDescription)!""
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro upcomingTenderDiffSummary
  diffModel
  files=[]
  tenderName=defaultHeadingPrefix
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=tenderName
    displayOrder=diffModel.UpcomingTenderView_displayOrder.currentValue
    isValid=true
    summaryLinkList=[]
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_upcomingTenderSummaryFields
      useDiffedField=true
      tenderFunction=diffModel.UpcomingTenderView_tenderFunction
      descriptionOfWork=diffModel.UpcomingTenderView_descriptionOfWork
      estimatedTenderDate=diffModel.UpcomingTenderView_estimatedTenderDate
      contractBand=diffModel.UpcomingTenderView_contractBand
      contactName=diffModel.UpcomingTenderView_contactName
      contactPhoneNumber=diffModel.UpcomingTenderView_contactPhoneNumber
      contactJobTitle=diffModel.UpcomingTenderView_contactJobTitle
      contactEmailAddress=diffModel.UpcomingTenderView_contactEmailAddress
      uploadedFileUrl=(files[0].UploadedFileView_fileUrl)!""
      uploadedFileName=(files[0].UploadedFileView_fileName)!""
      uploadedFileDescription=(files[0].UploadedFileView_fileDescription)!""
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro _upcomingTenderSummaryFields
  useDiffedField
  tenderFunction=""
  descriptionOfWork=""
  estimatedTenderDate=""
  contractBand=""
  contactName=""
  contactPhoneNumber=""
  contactJobTitle=""
  contactEmailAddress=""
  uploadedFileUrl=""
  uploadedFileName=""
  uploadedFileDescription=""
>
  <@checkAnswers.checkAnswersStandardNestedOrDiffRow
    prompt="Tender function"
    fieldValue=tenderFunction
    isDiffedField=useDiffedField
  >
    <@stringWithTag.stringWithTag stringWithTag=tenderFunction />
  </@checkAnswers.checkAnswersStandardNestedOrDiffRow>
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Description of work"
    fieldValue=descriptionOfWork
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Estimated tender date"
    fieldValue=estimatedTenderDate
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Contract band"
    fieldValue=contractBand
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Name"
    fieldValue=contactName
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Phone number"
    fieldValue=contactPhoneNumber
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Job title"
    fieldValue=contactJobTitle
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Email address"
    fieldValue=contactEmailAddress
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffUploadedFileViewRow
    fileUrlFieldValue=uploadedFileUrl
    fileNameFieldValue=uploadedFileName
    fileDescriptionFieldValue=uploadedFileDescription
    isDiffedField=useDiffedField
  />
</#macro>