<#include '../../layout.ftl'>
<#include '../macros/minMaxDateInput.ftl'>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest
      path="form.pipeline"
      selectorMinInputLength=1
      labelText="What is the pipeline number?"
      restUrl=springUrl(pipelinesRestUrl)
      preselectedItems=preSelectedPipelineMap!{}
    />
    <@fdsTextInput.textInput path="form.materialType" labelText="What is the material type of the pipeline?"/>
    <@fdsRadio.radio path="form.status" labelText="What is the status of the pipeline?" radioItems=pipelineStatuses/>
    <@minMaxDateInput
      minFormPath="form.decommissioningYears.minYear"
      maxFormPath="form.decommissioningYears.maxYear"
      labelText="Provide the period over which the pipeline will be decommissioned"
      altMinLabel="Earliest year"
      altMaxLabel="Latest year"
      formId="decommissioning-period"
    />
    <@fdsRadio.radio path="form.removalPremise" labelText="Pipeline removal premise" radioItems=pipelineRemovalPremises/>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>
