<#include '../layout.ftl'>

<#assign pageName = "Confirm no update required">

<@defaultPage htmlTitle=pageName pageHeading=pageName backLink=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
    <@fdsTextarea.textarea path="form.reasonNoUpdateRequired" labelText="What is the reason no update is required?" />
    <@fdsAction.button buttonText="Submit" buttonValue="submit" />
  </@fdsForm.htmlForm>
</@defaultPage>
