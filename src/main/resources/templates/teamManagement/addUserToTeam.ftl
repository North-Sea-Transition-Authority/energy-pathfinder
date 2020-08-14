<#include '../layout.ftl'>

<@defaultPage htmlTitle="Add user to ${groupName}" backLink=true topNavigation=showTopNav twoThirdsColumn=true>
  <@fdsForm.htmlForm>
    <@fdsTextInput.textInput path="form.userIdentifier" labelText="Add user to ${groupName}" hintText="Enter person's email address or login ID" pageHeading=true/>
    <@fdsAction.submitButtons
      primaryButtonText="Next"
      linkSecondaryAction=true
      secondaryLinkText="Cancel"
      linkSecondaryActionUrl=springUrl(cancelUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>