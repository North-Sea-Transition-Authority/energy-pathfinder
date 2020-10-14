<#include '../../layout.ftl'/>

<#macro collaborationOpportunitySummary view opportunityName="Collaboration opportunity" showValidationAndActions=false>
  <@fdsCheckAnswers.checkAnswers >
    <#if showValidationAndActions>
      <div class="summary-list__actions">
        <@fdsAction.link linkText=view.getEditLink().getLinkText() linkUrl=springUrl(view.getEditLink().url) linkScreenReaderText=opportunityName />
        <@fdsAction.link linkText=view.getDeleteLink().getLinkText() linkUrl=springUrl(view.getDeleteLink().url) linkScreenReaderText=opportunityName />
      </div>
      <#if view.valid?has_content && !view.valid>
        <span class="govuk-error-message">
          <span class="govuk-visually-hidden">Error:</span>${opportunityName} is incomplete
        </span>
      </#if>
    </#if>

    <@checkAnswers.checkAnswersRowNoActions prompt="Opportunity function" value=view.function!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Description of work" value=view.descriptionOfWork!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Urgent response required" value=view.urgentResponseNeeded!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Name" value=view.contactDetailView.name!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Phone number" value=view.contactDetailView.phoneNumber!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Job title" value=view.contactDetailView.jobTitle!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Email address" value=view.contactDetailView.emailAddress!"" />
    <@checkAnswers.checkAnswersUploadedFileViewNoActions uploadedFileView=view.uploadedFileViews[0]!"" />

  </@fdsCheckAnswers.checkAnswers>
</#macro>