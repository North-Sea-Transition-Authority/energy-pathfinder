<#include '../../layout.ftl'/>

<#-- @ftlvariable name="sectionId" type="String" -->
<#-- @ftlvariable name="sectionTitle" type="String" -->
<#-- @ftlvariable name="projectOperatorDiffModel" type="java.util.Map<String, Object>" -->
<#-- @ftlvariable name="isPublishedAsOperator" type="Boolean" -->

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Project operator/developer"
      diffedField=projectOperatorDiffModel.ProjectOperatorView_operatorName
    />
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Show project operator/developer on supply chain interface"
      diffedField=projectOperatorDiffModel.ProjectOperatorView_isPublishedAsOperator
    />
    <#if !isPublishedAsOperator>
      <@checkAnswers.diffedCheckAnswersRowNoActions
        prompt="Operator/Developer to show on supply chain interface"
        diffedField=projectOperatorDiffModel.ProjectOperatorView_publishableOrganisationName
      />
    </#if>
  </@fdsCheckAnswers.checkAnswers>
</@sectionSummaryWrapper.sectionSummaryWrapper>
