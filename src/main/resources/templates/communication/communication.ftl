<#include '../layout.ftl'>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle topNavigation=true twoThirdsColumn=true breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
    <@fdsRadio.radio
      path="form.recipientType"
      labelText="Who is this email being sent to?"
      radioItems=recipientTypes
    />
    <@fdsTextInput.textInput path="form.subject" labelText="Email subject"/>
    <@fdsTextarea.textarea
      path="form.body"
      labelText="Email body"
      hintText="Enter the body of the email without a greeting or signature. This will be automatically added before sending the email."
    />
    <@fdsAction.submitButtons
      primaryButtonText="Next"
      linkSecondaryAction=true
      secondaryLinkText="Cancel"
      linkSecondaryActionUrl=springUrl(cancelUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>