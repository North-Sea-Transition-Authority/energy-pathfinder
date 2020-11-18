<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Set up your project" pageHeading="Set up your project" breadcrumbs=true >
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
    <#list sections as section>
      <@fdsRadio.radioGroup path=section.formField labelText=section.displayName >
        <#assign yesMap = {section.yesAnswer : "Yes"}/>
        <#assign noMap = {section.noAnswer : "No"}/>
        <@fdsRadio.radioItem path=section.formField itemMap=yesMap isFirstItem=true/>
        <@fdsRadio.radioItem path=section.formField itemMap=noMap />
      </@fdsRadio.radioGroup>
    </#list>

    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>