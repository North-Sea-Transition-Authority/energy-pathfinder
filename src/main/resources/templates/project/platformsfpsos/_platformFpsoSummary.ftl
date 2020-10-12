<#include '../../layout.ftl'/>

<#macro platformFpsoSummary view platformFpsoName="platform or FPSO" showValidationAndActions=false>
  <@fdsCheckAnswers.checkAnswers >
    <#if showValidationAndActions>
      <div class="summary-list__actions">
        <@fdsAction.link linkText=view.getEditLink().getLinkText() linkUrl=springUrl(view.getEditLink().url) linkScreenReaderText=platformFpsoName />
        <@fdsAction.link linkText=view.getDeleteLink().getLinkText() linkUrl=springUrl(view.getDeleteLink().url) linkScreenReaderText=platformFpsoName />
      </div>
      <#if view.valid?has_content && !view.valid>
        <span class="govuk-error-message">
          <span class="govuk-visually-hidden">Error:</span>${platformFpsoName} is incomplete
        </span>
      </#if>
    </#if>

    <@checkAnswers.checkAnswersRowNoActions prompt="Platform or FPSO" value=view.platformFpso!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Topside / FPSO removal mass" value=view.topsideFpsoMass!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Topside removal years" value=view.topsideRemovalYears!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Substructures expected to be removed" value=view.substructuresExpectedToBeRemoved?string("Yes", "No")!"" />
    <#if view.substructuresExpectedToBeRemoved?has_content && view.substructuresExpectedToBeRemoved>
        <@checkAnswers.checkAnswersRowNoActions prompt="Substructure removal premise" value=view.substructureRemovalPremise!"" />
        <@checkAnswers.checkAnswersRowNoActions prompt="Substructure removal mass" value=view.substructureRemovalMass!"" />
        <@checkAnswers.checkAnswersRowNoActions prompt="Substructure removal years" value=view.substructureRemovalYears!"" />
    </#if>
    <@checkAnswers.checkAnswersRowNoActions prompt="FPSO type" value=view.fpsoType!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="FPSO dimensions" value=view.fpsoDimensions!"" />
    <@checkAnswers.checkAnswersRowNoActions prompt="Future plans" value=view.futurePlans!"" />
  </@fdsCheckAnswers.checkAnswers>
</#macro>