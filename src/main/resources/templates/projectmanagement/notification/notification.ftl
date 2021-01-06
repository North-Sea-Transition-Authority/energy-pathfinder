<#include '../../layout.ftl'>

<#if isUpdateInProgress>
  <@fdsContactPanel.contactPanel
    headingText="Update in progress"
    contentHeadingText="Update started by"
  >
    ${updateCreatedByUserName} (${updateCreatedByUserEmailAddress})
  </@fdsContactPanel.contactPanel>
</#if>
