<#--FDS Layout-->
<#include 'fds/objects/layouts/generic.ftl'>
<#import 'fds/objects/grid/grid.ftl' as grid>

<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->
<#-- @ftlvariable name="navigationItems" type="java.util.List<uk.co.ogauthority.pathfinder.model.navigation.TopNavigationItem>" -->
<#-- @ftlvariable name="currentEndPoint" type="String" -->
<#-- @ftlvariable name="serviceHomeUrl" type="String" -->

<#include 'pathfinderImports.ftl'>

<#macro defaultPagePane
  htmlTitle
  wrapperClasses=""
  wrapperWidth=false
  topNavigation=true
  headerLogo="GOVUK_CREST"
  errorCheck=false
  noIndex=false
  errorItems=[]
>

  <@genericLayout htmlTitle=htmlTitle htmlAppTitle=service.serviceName errorCheck=errorCheck noIndex=noIndex>
    <div class="fds-pane fds-pane--enabled" id="top">
      <#--Header-->
      <@applicationHeader.header wrapperWidth=wrapperWidth/>

      <#--Top navigation -->
      <#if topNavigation>
        <@fdsNavigation.navigation
          navigationItems=navigationItems
          currentEndPoint=currentEndPoint
          wrapperWidth=wrapperWidth
          serviceName=service.serviceName
          serviceUrl=springUrl(serviceHomeUrl)
        />
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
