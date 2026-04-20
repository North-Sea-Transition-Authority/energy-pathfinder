<#include '../layout.ftl'>

<@defaultPage htmlTitle=teamName twoThirdsColumn=false backLink=true topNavigation=showTopNav errorItems=errorList>
  <#if error?has_content && !errorList?has_content>
    <@fdsError.singleErrorSummary errorMessage=error/>
  </#if>

    <#assign warning>
        <#if !userHasAllowedEmail>
            <@fdsWarning.warning>
              This user's email is not from an approved domain for this team.
            </@fdsWarning.warning>
        </#if>
    </#assign>

  <@fdsForm.htmlForm>
    <@fdsCheckbox.checkboxes
      path="form.userRoles"
      checkboxes=roles
      fieldsetHeadingText="What actions does " + userName + " perform?"
      fieldsetHeadingSize="h1"
      fieldsetHeadingClass="govuk-fieldset__legend--l"
      hintText=warning
    />
    <@fdsAction.submitButtons
      primaryButtonText="Save and complete"
      linkSecondaryAction=true
      secondaryLinkText="Cancel"
      linkSecondaryActionUrl=springUrl(cancelUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>