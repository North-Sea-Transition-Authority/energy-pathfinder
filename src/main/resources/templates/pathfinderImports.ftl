<#--Import statements-->
<#import '/spring.ftl' as spring>

<#import 'header.ftl' as applicationHeader>
<#import 'project/macros/contactDetails.ftl' as contactDetails>
<#import 'project/macros/decomissioningPeriodCheckAnswers.ftl' as decomissioningPeriodCheckAnswers>
<#import 'project/macros/differenceChanges.ftl' as differenceChanges>
<#import 'project/macros/emptySectionSummaryInset.ftl' as emptySectionSummaryInset>
<#import 'project/macros/quarterYearInput.ftl' as quarterYear>
<#import 'project/macros/checkAnswers.ftl' as checkAnswers>
<#import 'project/macros/fileUpload.ftl' as fileUpload>
<#import 'project/macros/setupProjectGuidance.ftl' as setupProjectGuidance>
<#import 'project/macros/twoNumberInput.ftl' as twoNumberInput>
<#import 'project/macros/minMaxDateInput.ftl' as minMaxDate>
<#import 'project/macros/summaryViewWrapper.ftl' as summaryViewWrapper>
<#import 'project/macros/sidebarSectionLink.ftl' as sideBarSectionLink>
<#import 'project/macros/stringWithTag.ftl' as stringWithTag>
<#import 'project/macros/sectionSummaryWrapper.ftl' as sectionSummaryWrapper>
<#import 'project/macros/feedback/serviceFeedbackLink.ftl' as serviceFeedbackLink>
<#import 'macros/checkboxtoggler/checkboxToggler.ftl' as checkboxToggler>
<#import 'macros/hiddeninput/hiddenInput.ftl' as hiddenInputs>
<#import 'macros/mailto/mailTo.ftl' as mailTo>
<#import 'macros/servicecontact/serviceContact.ftl' as serviceContact>
<#import 'macros/useraction/userAction.ftl' as userAction>
<#import 'macros/defaultPageWithSidebar.ftl' as defaultPageWithSidebar>
<#import 'macros/inlineInputAction.ftl' as inlineInputAction>
<#import 'macros/multiLineText.ftl' as multiLineText>
<#import 'macros/noEscapeHtml.ftl' as noEscapeHtml>
<#import 'macros/statistic.ftl' as statistic>
<#import 'macros/tag.ftl' as tag>
<#import 'macros/textUtil.ftl' as textUtil>
<#import 'macros/panel.ftl' as panel>
<#import 'macros/footerLinks.ftl' as pathfinderFooter>

<#function springUrl url>
  <#local springUrl>
    <@spring.url url/>
  </#local>
  <#return springUrl>
</#function>

<#macro pathfinderCustomScripts>
  <#local checkboxTogglerUrl = springUrl('/assets/static/js/pathfinder/checkboxToggler.js') />
  <script src="${checkboxTogglerUrl}"></script>
  <script src="<@spring.url'/assets/static/js/pathfinder/googleAnalyticsEventTracking.js'/>"></script>
  <script>
    var PATHFINDER_CONFIG = {
      analyticsMeasurementUrl: "<@spring.url analyticsMeasurementUrl/>",
      globalTag: "${analytics.globalTag}"
    };
  </script>
</#macro>