<#include '../../layout.ftl'>

<@defaultPage htmlTitle=pageNameSingular pageHeading=pageNameSingular breadcrumbs=true>

    <@fdsForm.htmlForm>
        <@fdsSearchSelector.searchSelectorRest path="form.departmentType" selectorMinInputLength=0 labelText="What department is the tender for?" restUrl=springUrl(departmentTenderRestUrl)  preselectedItems={} />
        <@fdsTextarea.textarea path="form.descriptionOfWork" labelText="Provide a detailed description of the work"/>
        <@fdsDateInput.dateInput
        dayPath="form.estimatedTenderDate.day"
        monthPath="form.estimatedTenderDate.month"
        yearPath="form.estimatedTenderDate.year"
        labelText="Estimated tender date"
        formId="estimatedTenderDate-day-month-year"
        />
        <@contactDetails.standardContactDetails path="form.contactDetail" legendHeading="Tender contact details"/>

    </@fdsForm.htmlForm>

</@defaultPage>