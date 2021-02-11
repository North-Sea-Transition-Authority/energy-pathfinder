<#import '/spring.ftl' as spring>

<#--FDS Layout-->
<#include 'fds/objects/layouts/generic.ftl'>
<#import 'fds/objects/grid/grid.ftl' as grid>

<#include 'pathfinderImports.ftl'>

<#function springUrl url>
  <#local springUrl>
    <@spring.url url/>
  </#local>
  <#return springUrl>
</#function>

<#macro defaultPage
  htmlTitle
  mainClasses="govuk-main-wrapper"
  wrapperClasses=""
  pageHeading=""
  pageHeadingClass="govuk-heading-xl"
  caption=""
  captionClass="govuk-caption-xl"
  fullWidthColumn=false
  oneHalfColumn=false
  oneThirdColumn=false
  twoThirdsColumn=true
  twoThirdsOneThirdColumn=false
  oneQuarterColumn=false
  twoThirdsOneThirdContent=""
  backLink=false
  backLinkUrl=""
  backLinkText="Back"
  breadcrumbs=false
  phaseBanner=false
  phaseBannerLink="#"
  topNavigation=true
  wrapperWidth=false
  masthead=false
  headerLogo="GOVUK_CREST"
  errorCheck=false
  noIndex=false
>
  <@genericLayout htmlTitle=htmlTitle htmlAppTitle=service.serviceName errorCheck=errorCheck noIndex=noIndex>

    <#--Header-->
    <@applicationHeader.header
      topNavigation=topNavigation
      wrapperWidth=wrapperWidth
      headerLogo=headerLogo
      logoText=service.customerMnemonic
      serviceName=service.serviceName
      headerNav=true
    />

    <#--Phase banner-->
    <#if phaseBanner>
      <div class="govuk-phase-banner__wrapper">
        <div class="govuk-phase-banner<#if wrapperWidth> govuk-width-container-wide<#else> govuk-width-container</#if><#if topNavigation> govuk-phase-banner--no-border</#if>">
          <p class="govuk-phase-banner__content">
            <strong class="govuk-tag govuk-phase-banner__content__tag ">alpha</strong>
            <span class="govuk-phase-banner__text">This is a new service – your <a class="govuk-link" href="${phaseBannerLink}">feedback</a> will help us to improve it.</span>
          </p>
        </div>
      </div>
    </#if>

    <#--Navigation-->
    <#if topNavigation>
      <@fdsNavigation.navigation navigationItems=navigationItems currentEndPoint=currentEndPoint wrapperWidth=wrapperWidth />
    </#if>

    <#if !masthead>
      <div class="<#if wrapperWidth>govuk-width-container-wide<#else> govuk-width-container </#if>${wrapperClasses}">
    </#if>

    <#--Breadcrumbs-->
    <#if breadcrumbs && !backLink>
      <@fdsBreadcrumbs.breadcrumbs crumbsList=crumbList currentPage=currentPage/>
    </#if>

    <#--Back link-->
    <#if backLink && !breadcrumbs>
      <@fdsBackLink.backLink backLinkUrl=backLinkUrl backLinkText=backLinkText/>
    </#if>

    <main class="${mainClasses}" id="main-content" role="main">
      <#--Grid-->
      <#if fullWidthColumn>
        <@grid.gridRow>
          <@grid.fullColumn>
            <@defaultHeading caption=caption captionClass=captionClass pageHeading=pageHeading pageHeadingClass=pageHeadingClass/>
            <#nested>
          </@grid.fullColumn>
        </@grid.gridRow>
      <#elseif oneHalfColumn>
        <@grid.gridRow>
          <@grid.oneHalfColumn>
            <@defaultHeading caption=caption captionClass=captionClass pageHeading=pageHeading pageHeadingClass=pageHeadingClass/>
            <#nested>
          </@grid.oneHalfColumn>
        </@grid.gridRow>
      <#elseif oneThirdColumn>
        <@grid.gridRow>
          <@grid.oneThirdColumn>
            <@defaultHeading caption=caption captionClass=captionClass pageHeading=pageHeading pageHeadingClass=pageHeadingClass/>
            <#nested>
          </@grid.oneThirdColumn>
        </@grid.gridRow>
      <#elseif twoThirdsColumn>
        <@grid.gridRow>
          <@grid.twoThirdsColumn>
            <@defaultHeading caption=caption captionClass=captionClass pageHeading=pageHeading pageHeadingClass=pageHeadingClass/>
            <#nested>
          </@grid.twoThirdsColumn>
        </@grid.gridRow>
      <#elseif oneQuarterColumn>
        <@grid.gridRow>
          <@grid.oneQuarterColumn>
            <@defaultHeading caption=caption captionClass=captionClass pageHeading=pageHeading pageHeadingClass=pageHeadingClass/>
            <#nested>
          </@grid.oneQuarterColumn>
        </@grid.gridRow>
      <#elseif twoThirdsOneThirdColumn>
        <@grid.gridRow>
          <@grid.twoThirdsColumn>
            <@defaultHeading caption=caption captionClass=captionClass pageHeading=pageHeading pageHeadingClass=pageHeadingClass/>
            <#nested>
          </@grid.twoThirdsColumn>
          <@grid.oneThirdColumn>
            ${twoThirdsOneThirdContent}
          </@grid.oneThirdColumn>
        </@grid.gridRow>
      <#else>
        <@defaultHeading caption=caption captionClass=captionClass pageHeading=pageHeading pageHeadingClass=pageHeadingClass/>
        <#nested>
      </#if>
    </main>

    <#if !masthead>
      </div>
    </#if>

    <#--Footer-->
    <#local footerMetaContent>
      <@fdsFooter.footerMeta footerMetaHiddenHeading="Support links">
        <@fdsFooter.footerMetaLink linkText="Contact" linkUrl=springUrl(contactUrl)/>
      </@fdsFooter.footerMeta>
    </#local>
    <@fdsFooter.footer wrapperWidth=wrapperWidth metaLinks=true footerMetaContent=footerMetaContent/>

    <#--Custom scripts-->

  </@genericLayout>
</#macro>