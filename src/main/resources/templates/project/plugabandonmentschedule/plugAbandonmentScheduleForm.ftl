<#include '../../layout.ftl'>

<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@minMaxDate.minMaxDateInput
      minFormPath="form.plugAbandonmentDate.minYear"
      maxFormPath="form.plugAbandonmentDate.maxYear"
      labelText="Provide the period in which the well decommissioning is expected to take place"
      altMinLabel="Earliest start year"
      altMaxLabel="Latest completion year"
      formId="plug-abandonment-date-min-max"
    />
    <h2 class="govuk-heading-m">Wells</h2>
    <@fdsAddToList.addToList
      path="form.wells"
      alreadyAdded=alreadyAddedWells
      title=""
      itemName="Well"
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