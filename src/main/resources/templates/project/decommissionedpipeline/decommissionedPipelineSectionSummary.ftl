<#include '../../layout.ftl'>
<#import '_decommissionedPipelineSummary.ftl' as decommissionedPipelineSummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#if decommissionedPipelineDiffModel?has_content>
    <#list decommissionedPipelineDiffModel as decommissionedPipelineDiff>
      <@decommissionedPipelineSummary.decommissionedPipelineDiffSummary
        decommissionedPipelineDiff=decommissionedPipelineDiff
        showHeader=true
        showActions=false
        headingSize="h3"
        headingClass="govuk-heading-m"
      />
    </#list>
  <#else>
    <@emptySectionSummaryInset.emptySectionSummaryInset
      itemText="pipelines"
      projectTypeDisplayName=projectTypeDisplayNameLowercase
    />
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
