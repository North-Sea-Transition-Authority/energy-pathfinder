<#include '../../layout.ftl'>
<#include '../macros/minMaxDateInput.ftl'>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest
      path="form.pipeline"
      selectorMinInputLength=3
      labelText="What is the pipeline number?"
      restUrl=springUrl(pipelineRestUrl)
      preselectedItems=preSelectedPipelineMap!{}
    />
    <@fdsRadio.radio path="form.status" labelText="What is the status of the pipeline?" radioItems=pipelineStatuses/>
    <@minMaxDateInput
      minFormPath="form.decommissioningDate.minYear"
      maxFormPath="form.decommissioningDate.maxYear"
      labelText="Provide the period in which the decommissioning is expected to take place"
      altMinLabel="Earliest start year"
      altMaxLabel="Latest completion year"
      formId="decommissioning-period"
    />
    <@radio.radioItems
      path="form.removalPremise"
      labelText="Pipeline decommissioning premise"
      selectableItems=pipelineRemovalPremises
    />
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>
