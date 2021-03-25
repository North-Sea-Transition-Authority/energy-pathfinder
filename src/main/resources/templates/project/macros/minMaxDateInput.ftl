<#include '../../layout.ftl'/>
<#macro minMaxDateInput minFormPath maxFormPath nestedPath="" labelText="" altMinLabel="Minimum" altMaxLabel="Maximum" formId="min-max-values">
  <@twoNumberInput.twoNumberInputs
    pathOne=minFormPath
    pathTwo=maxFormPath
    pathOneLabelText=altMinLabel
    pathTwoLabelText=altMaxLabel
    formId=formId
    nestingPath=nestedPath
    labelText=labelText/>
</#macro>