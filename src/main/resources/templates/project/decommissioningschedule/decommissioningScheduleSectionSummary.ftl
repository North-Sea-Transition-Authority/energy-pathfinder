<#include '../../layout.ftl'/>

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Decommissioning work start date"
      diffedField=decommissioningScheduleDiffModel.DecommissioningScheduleView_decommissioningStartDate
    />
    <@checkAnswers.diffedCheckAnswersRowNoActions
      prompt="Cessation of Production date"
      diffedField=decommissioningScheduleDiffModel.DecommissioningScheduleView_cessationOfProductionDate
    />
  </@fdsCheckAnswers.checkAnswers>
</@sectionSummaryWrapper.sectionSummaryWrapper>
