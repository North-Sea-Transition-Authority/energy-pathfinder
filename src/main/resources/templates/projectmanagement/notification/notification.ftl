<#include '../../layout.ftl'>

<#if showRegulatorUpdateRequestNotification>
  <@_regulatorUpdateRequestNotificationBanner
    regulatorMnemonic=service.customerMnemonic
    regulatorUpdateRequestView=regulatorUpdateRequestView
    projectTypeDisplayNameLowercase=projectTypeDisplayNameLowercase
    titleString=titleString
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

<#macro _regulatorUpdateRequestNotificationBanner
  regulatorMnemonic
  regulatorUpdateRequestView
  projectTypeDisplayNameLowercase
  titleString
>
  <@fdsNotificationBanner.notificationBannerInfo
    bannerTitleText="${regulatorMnemonic} have requested an update"
    bannerClass="govuk-notification-banner--full-width-content"
  >
    <#if titleString?has_content>
      <h3 class="govuk-notification-banner__heading">
        ${titleString}
      </h3>
    </#if>
    <p class="govuk-body">
      Provide an update in order to make the changes the ${regulatorMnemonic} have requested.
      Confirm no changes if you do not need to change any information on your ${projectTypeDisplayNameLowercase}.
    </p>
    <@fdsDetails.summaryDetails summaryTitle="What have ${regulatorMnemonic} asked me to update?">
      <@multiLineText.multiLineText blockClass="govuk-body">
        ${regulatorUpdateRequestView.updateReason}
      </@multiLineText.multiLineText>
    </@fdsDetails.summaryDetails>
  </@fdsNotificationBanner.notificationBannerInfo>
</#macro>