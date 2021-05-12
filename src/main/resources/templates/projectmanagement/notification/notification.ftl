<#include '../../layout.ftl'>

<#if showRegulatorUpdateRequestNotification>
  <@_regulatorUpdateRequestNotification
    regulatorMnemonic=service.customerMnemonic
    regulatorUpdateRequestView=regulatorUpdateRequestView
    projectTypeDisplayNameLowercase=projectTypeDisplayNameLowercase
  />
  <#elseif showUpdateInProgressNotification>
    <@_operatorUpdateInProgressNotification
      updateCreatedByUserName=updateCreatedByUserName
      updateCreatedByUserEmailAddress=updateCreatedByUserEmailAddress
    />
</#if>

<#macro _operatorUpdateInProgressNotification updateCreatedByUserName updateCreatedByUserEmailAddress>
  <@fdsContactPanel.contactPanel headingText="Update in progress" contentHeadingText="Update started by">
    <span>
      ${updateCreatedByUserName}
      <#if updateCreatedByUserEmailAddress?has_content>
        (${updateCreatedByUserEmailAddress})
      </#if>
    </span>
  </@fdsContactPanel.contactPanel>
</#macro>

<#macro _regulatorUpdateRequestNotification
  regulatorMnemonic
  regulatorUpdateRequestView
  projectTypeDisplayNameLowercase
>
  <@panel.panel
    headingText="The ${regulatorMnemonic} have requested an update to this ${projectTypeDisplayNameLowercase}"
  >
    <@panel.panelSection headingText="Update reason">
      <@multiLineText.multiLineText blockClass="govuk-body">
        ${regulatorUpdateRequestView.updateReason}
      </@multiLineText.multiLineText>
    </@panel.panelSection>
    <#if regulatorUpdateRequestView.deadlineDate?has_content>
      <@panel.panelSection headingText="Deadline date">
        ${regulatorUpdateRequestView.deadlineDate}
      </@panel.panelSection>
    </#if>
    <@panel.panelSection headingText="Update requested by">
      <div>
        ${regulatorUpdateRequestView.requestedByUserName}
        <#if regulatorUpdateRequestView.requestedByUserEmailAddress?has_content>
          (${regulatorUpdateRequestView.requestedByUserEmailAddress})
        </#if>
      </div>
      <div>${regulatorUpdateRequestView.requestedDate}</div>
    </@panel.panelSection>
  </@panel.panel>
</#macro>