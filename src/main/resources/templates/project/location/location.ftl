<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Location" pageHeading="Location" breadcrumbs=true >
    <#if errorList?has_content>
        <@fdsError.errorSummary errorItems=errorList />
    </#if>

    <@fdsForm.htmlForm>
        <@fdsSearchSelector.searchSelectorRest path="form.field" selectorMinInputLength=3  labelText="Which field is the project related to?" restUrl=springUrl(fieldsRestUrl)  preselectedItems=preselectedField!{} />
        <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
    </@fdsForm.htmlForm>
</@defaultPage>