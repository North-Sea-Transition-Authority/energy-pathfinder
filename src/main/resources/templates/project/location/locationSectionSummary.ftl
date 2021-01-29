<#include '../../layout.ftl'/>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Field"
      diffedField=projectLocationDiffModel.ProjectLocationView_field
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
      prompt="Approved Decommissioning Program"
      diffedField=projectLocationDiffModel.ProjectLocationView_approvedDecomProgram
    />
    <#if hasApprovedDecomProgram>
      <@checkAnswers.diffedCheckAnswersRowNoActions
        prompt="DP approval date"
        diffedField=projectLocationDiffModel.ProjectLocationView_approvedDecomProgramDate
      />
    </#if>
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="UKCS area"
      diffedField=projectLocationDiffModel.ProjectLocationView_ukcsArea
    />
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
