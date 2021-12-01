<#include '../../layout.ftl'>

<#macro emptySectionSummaryInset itemText projectTypeDisplayName>
  <@fdsInsetText.insetText>
    <p>No ${itemText} have been added to this ${projectTypeDisplayName}</p>
  </@fdsInsetText.insetText>
</#macro>
