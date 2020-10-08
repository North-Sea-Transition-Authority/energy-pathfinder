<#include '../../layout.ftl'/>

<#macro minMaxDateInput minFormPath maxFormPath nestedPath="" labelText="" altMinLabel="Minimum" altMaxLabel="Maximum" formId="min-max-values">
  <@fdsNumberInput.twoNumberInputs pathOne=minFormPath pathTwo=maxFormPath formId=formId nestingPath=nestedPath labelText=labelText>
    <@fdsNumberInput.numberInputItem path=minFormPath labelText=altMinLabel inputClass="govuk-input--width-4"/>
    <@fdsNumberInput.numberInputItem path=maxFormPath labelText=altMaxLabel inputClass="govuk-input--width-4"/>
  </@fdsNumberInput.twoNumberInputs>
</#macro>