<#--FDS Layout-->
<#include 'fds/objects/layouts/generic.ftl'>
<#import 'fds/objects/grid/grid.ftl' as grid>

<#-- @ftlvariable name="feedbackUrl" type="String" -->

<#include 'pathfinderImports.ftl'>

<#macro defaultPagePane
  htmlTitle
  wrapperClasses=""
  wrapperWidth=false
  topNavigation=true
  phaseBanner=true
  phaseBannerLink=springUrl(feedbackUrl)!""
  headerLogo="GOVUK_CREST"
  errorCheck=false
  noIndex=false
  errorItems=[]
>

  <@genericLayout htmlTitle=htmlTitle htmlAppTitle=service.serviceName errorCheck=errorCheck noIndex=noIndex>
    <div class="fds-pane fds-pane--enabled" id="top">
      <#--Header-->
      <@applicationHeader.header
        topNavigation=topNavigation
        wrapperWidth=wrapperWidth
        headerLogo=headerLogo
        logoText=service.customerMnemonic
        serviceName=service.serviceName
        headerNav=true
      />

      <#--Phase banner -->
      <#if phaseBanner>
        <div class="govuk-phase-banner__wrapper">
          <div class="govuk-phase-banner govuk-phase-banner--no-border<#if wrapperWidth> govuk-width-container-wide<#else> govuk-width-container</#if>">
            <p class="govuk-phase-banner__content">
              <strong class="govuk-tag govuk-phase-banner__content__tag ">beta</strong>
              <span class="govuk-phase-banner__text">This is a new service – your <a class="govuk-link" href="${phaseBannerLink}">feedback</a> will help us to improve it.</span>
            </p>
          </div>
        </div>
      </#if>

      <#--Top navigation -->
      <#if topNavigation>
        <@fdsNavigation.navigation navigationItems=navigationItems currentEndPoint=currentEndPoint wrapperWidth=wrapperWidth/>
      </#if>

      <div class="fds-pane__body ${wrapperClasses}<#if wrapperWidth> govuk-width-container-wide<#else> govuk-width-container</#if>">
        <#nested>
        <#--Back to top -->
        <@fdsBackToTop.backToTop/>
      </div>

      <#--Footer -->
      <#local footerMetaContent>
        <@fdsFooter.footerMeta footerMetaHiddenHeading="Support links">
          <@pathfinderFooter.footerLinks/>
        </@fdsFooter.footerMeta>
      </#local>
      <@fdsNstaFooter.nstaFooter wrapperWidth=wrapperWidth metaLinks=true footerMetaContent=footerMetaContent/>

      <#--Custom scripts-->
      <@pathfinderCustomScripts/>

    </div>
  </@genericLayout>
</#macro>

<#macro defaultPagePaneContent
  mainClasses=""
  captionClass="govuk-caption-xl"
  caption=""
  pageHeadingClass="govuk-heading-xl"
  pageHeading="">

  <div class="fds-pane__content">
    <main id="main-content" class="fds-content ${mainClasses}" role="main">
      <div class="fds-content__header">
        <@defaultHeading
          caption=caption
          captionClass=captionClass
          pageHeading=pageHeading
          pageHeadingClass=pageHeadingClass
          errorItems=errorItems
        />
      </div>
      <#nested>
    </main>
  </div>
</#macro>

<#macro defaultPagePaneSubNav smallSubnav=false>
  <div class="fds-pane__subnav <#if smallSubnav>fds-pane__subnav--small</#if>">
    <#nested>
  </div>
</#macro>
