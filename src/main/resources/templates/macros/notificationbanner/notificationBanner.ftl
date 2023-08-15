<#include '../../layout.ftl'>

<#-- @ftlvariable name="notificationBannerView" type="uk.co.ogauthority.pathfinder.model.notificationbanner.NotificationBannerView" -->

<#macro successNotificationBanner notificationBannerView>
    <#if notificationBannerView.title.title?hasContent>
        <@fdsNotificationBanner.notificationBannerSuccess bannerTitleText=notificationBannerView.title.title>
            <@_notificationBannerContent notificationBannerView=notificationBannerView/>
        </@fdsNotificationBanner.notificationBannerSuccess>
    </#if>
</#macro>

<#macro infoNotificationBanner notificationBannerView>
    <#if notificationBannerView.getTitleAsString()?hasContent>
        <@fdsNotificationBanner.notificationBannerInfo bannerTitleText=notificationBannerView.title.title>
            <@_notificationBannerContent notificationBannerView=notificationBannerView/>
        </@fdsNotificationBanner.notificationBannerInfo>
    </#if>
</#macro>

<#macro _notificationBannerContent notificationBannerView>
    <@fdsNotificationBanner.notificationBannerContent headingText=notificationBannerView.heading.heading>
        <#list notificationBannerView.bodyLines as bodyLine>
            <p class="${bodyLine.lineClass!"govuk-body"}">${bodyLine.lineText}</p>
        </#list>
        <#if notificationBannerView.bannerLink?hasContent>
            <@fdsNotificationBanner.notificationBannerLink bannerLinkUrl=springUrl(notificationBannerView.bannerLink.linkUrl) bannerLinkText="${notificationBannerView.bannerLink.linkText}"/>
        </#if>
    </@fdsNotificationBanner.notificationBannerContent>
</#macro>
