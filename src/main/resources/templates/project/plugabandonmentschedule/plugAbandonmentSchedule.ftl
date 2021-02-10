<#include '../../layout.ftl'>

<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>
  <@fdsForm.htmlForm>
    <@minMaxDate.minMaxDateInput
      minFormPath="form.plugAbandonmentDate.minYear"
      maxFormPath="form.plugAbandonmentDate.maxYear"
      labelText="Provide the period in which the plug and abandonment is expected to take place"
      altMinLabel="Earliest start year"
      altMaxLabel="Latest completion year"
      formId="plug-abandonment-date-min-max"
    />
    <@fdsAddToList.addToList
      path="form.wells"
      alreadyAdded=alreadyAddedWells
      title="Wells"
      itemName="Wells"
      noItemText="No wells added"
      invalidItemText="This well is invalid"
      addToListId="well-table"
      selectorLabelText="Which wells are being decommissioned over this period?"
      selectorHintText="For example 16/02b-A1"
      restUrl=springUrl(wellsRestUrl)
    />
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>