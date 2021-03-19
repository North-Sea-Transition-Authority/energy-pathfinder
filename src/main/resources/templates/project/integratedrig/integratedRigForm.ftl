<#include '../../layout.ftl'>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest
      path="form.structure"
      selectorMinInputLength=1
      labelText="Which structure is this integrated rig installed on?"
      restUrl=springUrl(facilitiesRestUrl)
      preselectedItems=preSelectedFacilityMap!{}
    />
    <@fdsTextInput.textInput path="form.name" labelText="What is the name of the integrated rig?" hintText="For situations where more than one integrated rig is present on a structure. For example, North Rig"/>
    <@fdsRadio.radio path="form.status" labelText="What is the status of the integrated rig?" radioItems=integratedRigStatuses/>
    <@fdsRadio.radio path="form.intentionToReactivate" labelText="Do you have any intention to reactivate the integrated rig for well abandonment activity?" radioItems=integratedRigIntentionsToReactivate/>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>
