<#include '../layout.ftl'>

<@defaultPage htmlTitle="Add user to ${groupName}" backLink=true topNavigation=showTopNav twoThirdsColumn=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@fdsTextInput.textInput
      path="form.emailAddress"
      labelText="What is the email address of the user?"
      hintText="The user must have an account on the ${energyPortalName}"
      pageHeading=true
    />
    <@fdsDetails.summaryDetails summaryTitle="The user I want to add does not have an account">
      <p class="govuk-body">
        A user can register for an account on the ${energyPortalName} using the following link:
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