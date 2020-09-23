<#include '../../layout.ftl'/>

<#macro collaborationOpportunitySummary view opportunityName="Collaboration opportunity" showValidationAndActions=false>
  <@fdsCheckAnswers.checkAnswers >
    <#if showValidationAndActions>
      <div class="summary-list__actions">
        <@fdsAction.link linkText=view.getEditLink().getLinkText() linkUrl=springUrl(view.getEditLink().url) linkScreenReaderText=opportunityName />
        <@fdsAction.link linkText=view.getDeleteLink().getLinkText() linkUrl=springUrl(view.getDeleteLink().url) linkScreenReaderText=opportunityName />
      </div>
      <#if view.isValid()?has_content && !view.isValid()>
        <span class="govuk-error-message">
          <span class="govuk-visually-hidden">Error:</span>${opportunityName} is incomplete
        </span>
      </#if>
    </#if>

    <@checkAnswers.checkAnswersRowNoActions prompt="Opportunity function" value=view.getFunction()!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Description of work" value=view.getDescriptionOfWork()!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Estimated service date" value=view.getEstimatedServiceDate()!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Name" value=view.contactDetailView.name!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Phone number" value=view.contactDetailView.phoneNumber!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Job title" value=view.contactDetailView.jobTitle!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Email address" value=view.contactDetailView.emailAddress!"" />

  </@fdsCheckAnswers.checkAnswers>
</#macro>