<#import '/spring.ftl' as spring>

<#--FDS Layout-->
<#include 'fds/objects/layouts/generic.ftl'>
<#import 'fds/objects/grid/grid.ftl' as grid>
<#import 'fds/utilities/utilities.ftl' as fdsUtil>

<#include 'pathfinderImports.ftl'>

<#function springUrl url>
  <#local springUrl>
    <@spring.url url/>
  </#local>
  <#return springUrl>
</#function>

<#-- @ftlvariable name="feedbackUrl" type="String" -->
<#-- @ftlvariable name="flashTitle" type="String" -->
<#-- @ftlvariable name="flashClass" type="String" -->
<#-- @ftlvariable name="flashMessage" type="String" -->

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
  phaseBanner=true
  phaseBannerLink=feedbackUrl!""
  topNavigation=true
  wrapperWidth=false
  masthead=false
  headerLogo="GOVUK_CREST"
  errorCheck=false
  noIndex=false
  errorItems=[]
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
    <#if phaseBanner && feedbackUrl?has_content>
      <div class="govuk-phase-banner__wrapper">
        <div class="govuk-phase-banner<#if wrapperWidth> govuk-width-container-wide<#else> govuk-width-container</#if><#if topNavigation> govuk-phase-banner--no-border</#if>">
          <p class="govuk-phase-banner__content">
            <strong class="govuk-tag govuk-phase-banner__content__tag ">beta</strong>
            <span class="govuk-phase-banner__text">
              <span>This is a new service – your</span>
              <@fdsAction.link
                linkText="feedback"
                linkUrl=springUrl(phaseBannerLink)
                openInNewTab=true
                linkClass="govuk-link govuk-link--no-visited-state"
              />
              <span> will help us to improve it.</span>
            </span>
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

    <#assign flash>
      <#if flashTitle?has_content>
        <#if flashMessage?has_content>
          <@fdsFlash.flash flashTitle=flashTitle flashClass=flashClass!"">
            <p class="govuk-body govuk-!-margin-bottom-0">
              ${flashMessage}
            </p>
          </@fdsFlash.flash>
          <#else>
            <@fdsFlash.flash flashTitle=flashTitle flashClass=flashClass!""/>
        </#if>
      </#if>
    </#assign>

    <main class="${mainClasses}" id="main-content" role="main">
      <#--Grid-->
      <#if fullWidthColumn>
        <@grid.gridRow>
          <@grid.fullColumn>
            ${flash}
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
            ${flash}
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
            ${flash}
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
            ${flash}
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
            ${flash}
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
            ${flash}
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
        ${flash}
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
    <script src="<@spring.url '/assets/static/js/pathfinder/checkboxToggler.js'/>"></script>

  </@genericLayout>
</#macro>