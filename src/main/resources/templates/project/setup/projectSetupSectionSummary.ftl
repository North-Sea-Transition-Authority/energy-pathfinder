<#include '../../layout.ftl'/>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <@fdsCheckAnswers.checkAnswers >
    <#list projectSetupDiffModel as projectSetupDiff>
      <@checkAnswers.diffedCheckAnswersRowNoActions
        prompt=projectSetupDiff.ProjectSetupSummaryItem_prompt.currentValue
        diffedField=projectSetupDiff.ProjectSetupSummaryItem_answerValue
      />
    </#list>
  </@fdsCheckAnswers.checkAnswers>
</@sectionSummaryWrapper.sectionSummaryWrapper>
