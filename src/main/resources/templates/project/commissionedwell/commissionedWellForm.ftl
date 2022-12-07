<#include '../../layout.ftl'>

<#-- @ftlvariable name="pageName" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="wellsRestUrl" type="String" -->
<#-- @ftlvariable name="alreadyAddedWells" type="java.util.List<uk.co.ogauthority.pathfinder.model.view.wellbore.WellboreView>" -->

<@defaultPage htmlTitle=pageName pageHeading=pageName breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@minMaxDate.minMaxDateInput
      minFormPath="form.commissioningSchedule.minYear"
      maxFormPath="form.commissioningSchedule.maxYear"
      labelText="Provide the period in which the well commissioning is expected to take place"
      altMinLabel="Earliest start year"
      altMaxLabel="Latest completion year"
      formId="well-commissioning-schedule-min-max"
    />
    <h2 class="govuk-heading-m">Wells</h2>
    <@fdsAddToList.addToList
      pathForList="form.wells"
      pathForSelector="form.wellSelected"
      alreadyAdded=alreadyAddedWells![]
      title=""
      itemName="Well"
      noItemText="No wells added"
      invalidItemText="This well is invalid"
      addToListId="well-table"
      selectorLabelText="Which wells are being commissioned over this period?"
      selectorHintText="For example 16/02b-A1"
      restUrl=springUrl(wellsRestUrl)
    />
    <@fdsAction.submitButtons
      primaryButtonText="Save and complete"
      secondaryButtonText="Save and complete later"
    />
  </@fdsForm.htmlForm>
</@defaultPage>