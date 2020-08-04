<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Location" pageHeading="Location" >
    <#if errorList?has_content>
        <@fdsError.errorSummary errorItems=errorList />
    </#if>

    <@fdsForm.htmlForm>
        <@fdsSearchSelector.searchSelectorRest path="form.field" labelText="Which field is the project related to?" restUrl=springUrl(fieldsRestUrl)  />
        <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
    </@fdsForm.htmlForm>
</@defaultPage>