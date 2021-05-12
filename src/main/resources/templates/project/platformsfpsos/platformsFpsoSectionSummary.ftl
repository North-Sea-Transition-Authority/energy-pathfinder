<#include '../../layout.ftl'>
<#import '_platformFpsoSummary.ftl' as platformFpsoSummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#if platformFpsoDiffModel?has_content>
    <#list platformFpsoDiffModel as platformFpsoDiff>
      <@platformFpsoSummary.platformFpsoDiffSummary
        diffModel=platformFpsoDiff.platformFpsoDiff
        fpso=platformFpsoDiff.fpso
        areSubstructuresExpectedToBeRemoved=platformFpsoDiff.areSubstructuresExpectedToBeRemoved
        showHeader=true
        showActions=false
        headingSize="h3"
        headingClass="govuk-heading-m"
      />
    </#list>
  <#else>
    <@emptySectionSummaryInset.emptySectionSummaryInset
      itemText="platforms or FPSOs"
      projectTypeDisplayName=projectTypeDisplayNameLowercase
    />
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
