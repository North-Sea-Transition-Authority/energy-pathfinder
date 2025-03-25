<#include '../../layout.ftl'/>

<#-- @ftlvariable name="sectionId" type="String" -->
<#-- @ftlvariable name="sectionTitle" type="String" -->
<#-- @ftlvariable name="projectLocationDiffModel" type="java.util.Map<String, Object>" -->
<#-- @ftlvariable name="hasApprovedFieldDevelopmentPlan" type="Boolean" -->
<#-- @ftlvariable name="hasApprovedDecomProgram" type="Boolean" -->

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Centre of interest latitude in ETRS89"
      diffedField=projectLocationDiffModel.ProjectLocationView_centreOfInterestLatitude
    />
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Centre of interest longitude in ETRS89"
      diffedField=projectLocationDiffModel.ProjectLocationView_centreOfInterestLongitude
    />
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Field"
      diffedField=projectLocationDiffModel.ProjectLocationView_field
    />
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Field UKCS area"
      diffedField=projectLocationDiffModel.ProjectLocationView_ukcsArea
    />
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Field type"
      diffedField=projectLocationDiffModel.ProjectLocationView_fieldType
    />
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Maximum water depth"
      diffedField=projectLocationDiffModel.ProjectLocationView_maximumWaterDepth
    />
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Approved Field Development Plan"
      diffedField=projectLocationDiffModel.ProjectLocationView_approvedFieldDevelopmentPlan
    />
    <#if hasApprovedFieldDevelopmentPlan>
      <@checkAnswers.diffedCheckAnswersRowNoActions
        prompt="FDP approval date"
        diffedField=projectLocationDiffModel.ProjectLocationView_approvedFdpDate
      />
    </#if>
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Approved Decommissioning Programme"
      diffedField=projectLocationDiffModel.ProjectLocationView_approvedDecomProgram
    />
    <#if hasApprovedDecomProgram>
      <@checkAnswers.diffedCheckAnswersRowNoActions
        prompt="DP approval date"
        diffedField=projectLocationDiffModel.ProjectLocationView_approvedDecomProgramDate
      />
    </#if>
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Licence blocks">
      <#list projectLocationDiffModel.ProjectLocationView_licenceBlocks as diffedLicenceBlock>
        <div>
          <@differenceChanges.renderDifference
            diffedField=diffedLicenceBlock
          />
        </div>
      </#list>
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
  </@fdsCheckAnswers.checkAnswers>
</@sectionSummaryWrapper.sectionSummaryWrapper>
