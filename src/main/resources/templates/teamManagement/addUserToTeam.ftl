<#include '../layout.ftl'>

<@defaultPage htmlTitle="Add user to ${groupName}" backLink=true topNavigation=showTopNav twoThirdsColumn=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>
  <@fdsForm.htmlForm>
    <@fdsTextInput.textInput
      path="form.userIdentifier"
      labelText="What is the email address or login ID of the user?"
      hintText="The user must have an account on the Energy Portal"
      pageHeading=true
    />
    <@fdsDetails.summaryDetails summaryTitle="The user I want to add does not have an account">
      <p class="govuk-body">
        A user can register for an account on the Energy Portal using the following link:
      </p>
      <p class="govuk-body">
        ${portalRegistrationUrl}
      </p>
    </@fdsDetails.summaryDetails>
    <@fdsAction.submitButtons
      primaryButtonText="Continue"
      linkSecondaryAction=true
      secondaryLinkText="Cancel"
      linkSecondaryActionUrl=springUrl(cancelUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>