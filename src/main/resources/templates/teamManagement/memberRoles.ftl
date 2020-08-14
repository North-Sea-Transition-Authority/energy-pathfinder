<#include '../layout.ftl'>

<@defaultPage htmlTitle=teamName twoThirdsColumn=false backLink=true topNavigation=showTopNav>
  <#if error?has_content>
    <@fdsError.singleErrorSummary errorMessage=error/>
  </#if>
  <@fdsForm.htmlForm>
    <@fdsCheckbox.checkboxes path="form.userRoles" checkboxes=roles fieldsetHeadingText="What actions does " + userName + " perform?" fieldsetHeadingSize="h1" fieldsetHeadingClass="govuk-fieldset__legend--l"/>
    <@fdsAction.submitButtons
      primaryButtonText="Save"
      linkSecondaryAction=true
      secondaryLinkText="Cancel"
      linkSecondaryActionUrl=springUrl(cancelUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>