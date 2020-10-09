<#include '../../layout.ftl'/>

<#macro upcomingTenderSummary view tenderName="Upcoming tender" showValidationAndActions=false>
  <@fdsCheckAnswers.checkAnswers >
    <#if showValidationAndActions>
      <div class="summary-list__actions">
        <@fdsAction.link linkText=view.getEditLink().getLinkText() linkUrl=springUrl(view.getEditLink().url) linkScreenReaderText=tenderName />
        <@fdsAction.link linkText=view.getDeleteLink().getLinkText() linkUrl=springUrl(view.getDeleteLink().url) linkScreenReaderText=tenderName />
      </div>
      <#if view.valid?has_content && !view.valid>
        <span class="govuk-error-message">
          <span class="govuk-visually-hidden">Error:</span>${tenderName} is incomplete
        </span>
      </#if>
    </#if>

    <@checkAnswers.checkAnswersRowNoActions prompt="Tender function" value=view.tenderFunction!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Description of work" value=view.descriptionOfWork!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Estimated tender date" value=view.estimatedTenderDate!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Contract band" value=view.contractBand!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Name" value=view.contactDetailView.name!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Phone number" value=view.contactDetailView.phoneNumber!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Job title" value=view.contactDetailView.jobTitle!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Email address" value=view.contactDetailView.emailAddress!"" />
    <@checkAnswers.checkAnswersUploadedFileViewNoActions uploadedFileView=view.uploadedFileViews[0]!"" />

  </@fdsCheckAnswers.checkAnswers>
</#macro>