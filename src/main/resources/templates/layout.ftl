<#--FDS Layout-->
<#include 'fds/objects/layouts/generic.ftl'>
<#import 'fds/objects/grid/grid.ftl' as grid>
<#import 'fds/utilities/utilities.ftl' as fdsUtil>

<#include 'pathfinderImports.ftl'>

<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->
<#-- @ftlvariable name="serviceHomeUrl" type="String" -->
<#-- @ftlvariable name="navigationItems" type="java.util.List<uk.co.ogauthority.pathfinder.model.navigation.TopNavigationItem>" -->
<#-- @ftlvariable name="currentEndPoint" type="String" -->
<#-- @ftlvariable name="currentPage" type="String" -->
<#-- @ftlvariable name="crumbList" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="accessibilityStatementUrl" type="String" -->
<#-- @ftlvariable name="contactUrl" type="String" -->

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
  headerLogo="GOV_CREST"
  errorCheck=false
  noIndex=false
  errorItems=[]
>
  <@genericLayout htmlTitle=htmlTitle htmlAppTitle=service.serviceName errorCheck=errorCheck noIndex=noIndex>

    <#local serviceHomeUrl = springUrl(serviceHomeUrl) />

    <#--Header-->
    <@applicationHeader.header
      homePageUrl=serviceHomeUrl
      serviceUrl=serviceHomeUrl
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
            <@defaultHeading
              caption=caption
              captionClass=captionClass
              pageHeading=pageHeading
              pageHeadingClass=pageHeadingClass
              errorItems=errorItems
            />
            <#nested>
          </@grid.fullColumn>
        </@grid.gridRow>
      <#elseif oneHalfColumn>
        <@grid.gridRow>
          <@grid.oneHalfColumn>
            <@defaultHeading
              caption=caption
              captionClass=captionClass
              pageHeading=pageHeading
              pageHeadingClass=pageHeadingClass
              errorItems=errorItems
            />
            <#nested>
          </@grid.oneHalfColumn>
        </@grid.gridRow>
      <#elseif oneThirdColumn>
        <@grid.gridRow>
          <@grid.oneThirdColumn>
            <@defaultHeading
              caption=caption
              captionClass=captionClass
              pageHeading=pageHeading
              pageHeadingClass=pageHeadingClass
              errorItems=errorItems
            />
            <#nested>
          </@grid.oneThirdColumn>
        </@grid.gridRow>
      <#elseif twoThirdsColumn>
        <@grid.gridRow>
          <@grid.twoThirdsColumn>
            <@defaultHeading
              caption=caption
              captionClass=captionClass
              pageHeading=pageHeading
              pageHeadingClass=pageHeadingClass
              errorItems=errorItems
            />
            <#nested>
          </@grid.twoThirdsColumn>
        </@grid.gridRow>
      <#elseif oneQuarterColumn>
        <@grid.gridRow>
          <@grid.oneQuarterColumn>
            <@defaultHeading
              caption=caption
              captionClass=captionClass
              pageHeading=pageHeading
              pageHeadingClass=pageHeadingClass
              errorItems=errorItems
            />
            <#nested>
          </@grid.oneQuarterColumn>
        </@grid.gridRow>
      <#elseif twoThirdsOneThirdColumn>
        <@grid.gridRow>
          <@grid.twoThirdsColumn>
            <@defaultHeading
              caption=caption
              captionClass=captionClass
              pageHeading=pageHeading
              pageHeadingClass=pageHeadingClass
              errorItems=errorItems
            />
            <#nested>
          </@grid.twoThirdsColumn>
          <@grid.oneThirdColumn>
            ${twoThirdsOneThirdContent}
          </@grid.oneThirdColumn>
        </@grid.gridRow>
      <#else>
        <@defaultHeading
          caption=caption
          captionClass=captionClass
          pageHeading=pageHeading
          pageHeadingClass=pageHeadingClass
          errorItems=errorItems
        />
        <#nested>
      </#if>
    </main>

    <#if !masthead>
      </div>
    </#if>

    <#--Footer-->
    <#local footerMetaContent>
      <@fdsFooter.footerMeta footerMetaHiddenHeading="Support links">
        <@fdsFooter.footerMetaLink linkText="Accessibility statement" linkUrl=springUrl(accessibilityStatementUrl)/>
        <@fdsFooter.footerMetaLink linkText="Contact" linkUrl=springUrl(contactUrl)/>
      </@fdsFooter.footerMeta>
    </#local>
    <@fdsFooter.footer wrapperWidth=wrapperWidth metaLinks=true footerMetaContent=footerMetaContent/>

    <#--Custom scripts-->
    <#local checkboxTogglerUrl = springUrl('/assets/static/js/pathfinder/checkboxToggler.js') />
    <script src="${checkboxTogglerUrl}"></script>

  </@genericLayout>
</#macro>