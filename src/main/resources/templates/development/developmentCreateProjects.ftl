<#include '../layout.ftl'>

<@defaultPage htmlTitle="Create projects" pageHeading="Create projects" errorItems=errorList>
  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest path="form.organisationGroup" labelText="Select an operator/developer for the project" selectorMinInputLength=0 restUrl=springUrl(operatorsRestUrl)  preselectedItems=preselectedOperator!{} />
    <@fdsTextInput.textInput
      path="form.numberOfProjects"
      labelText="How many projects are you creating?"
      inputClass="govuk-input--width-4"
    />
    <@fdsRadio.radio path="form.projectStatus" labelText="What is the project status" radioItems=statuses/>

    <@fdsAction.submitButtons
      primaryButtonText="Create projects"
      linkSecondaryAction=true
      secondaryLinkText="Cancel"
      linkSecondaryActionUrl=springUrl(cancelUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>
