<#include '../../layout.ftl'>
<#import '_platformFpsoSummary.ftl' as platformFpsoSummary>
<#import './_terminology.ftl' as terminology>

<#-- @ftlvariable name="sectionId" type="String" -->
<#-- @ftlvariable name="sectionTitle" type="String" -->
<#-- @ftlvariable name="platformFpsoDiffModel" type="java.util.List<java.util.Map<String, Object>>" -->

<#assign platformLowerCase = terminology.terminology['platformLowerCase'] />
<#assign floatingUnitLowerCase = terminology.terminology['floatingUnitLowerCase'] />

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
      itemText="${platformLowerCase}s or ${floatingUnitLowerCase}s"
      projectTypeDisplayName=projectTypeDisplayNameLowercase
    />
  </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>
