<#include '../../layout.ftl'>
<#import '_decommissionedPipelineSummary.ftl' as decommissionedPipelineSummary>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <#if decommissionedPipelineViews?has_content>
    <#list decommissionedPipelineViews as decommissionedPipelineView>
      <@decommissionedPipelineSummary.decommissionedPipelineSummary
        decommissionedPipelineView=decommissionedPipelineView
        showHeader=true
        showActions=false
        headingSize="h3"
        headingClass="govuk-heading-m"
      />
    </#list>
  <#else>
    <@emptySectionSummaryInset.emptySectionSummaryInset itemText="pipelines"/>
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
