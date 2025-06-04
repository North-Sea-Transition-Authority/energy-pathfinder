<#include '../layout.ftl'>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="operatorsRestUrl" type="String" -->
<#-- @ftlvariable name="preselectedOperator" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="currentOperator" type="String" -->
<#-- @ftlvariable name="projectHeaderHtml" type="String" -->
<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->
<#-- @ftlvariable name="organisationUnitRestUrl" type="String" -->
<#-- @ftlvariable name="preselectedPublishableOrganisation" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="projectTypeDisplayNameLowercase" type="String" -->

<#assign title = pageTitle>

<@defaultPage htmlTitle=title breadcrumbs=true fullWidthColumn=true errorItems=errorList>

  <@noEscapeHtml.noEscapeHtml html=projectHeaderHtml />
  <@grid.gridRow>
    <@grid.twoThirdsColumn>
      <h2 class="govuk-heading-l">${title}</h2>

      <@fdsInsetText.insetText insetTextClass="govuk-inset-text--yellow">
        By changing this ${projectTypeDisplayNameLowercase}'s operator/developer, any in progress updates will no longer be able to be submitted.
      </@fdsInsetText.insetText>

      <@fdsDataItems.dataItem>
        <@fdsDataItems.dataValues key="Current ${projectTypeDisplayNameLowercase} operator/developer" value=currentOperator />
      </@fdsDataItems.dataItem>

      <@fdsForm.htmlForm>
        <@fdsSearchSelector.searchSelectorRest
          path="form.newOrganisationGroup"
          labelText="Who is the new operator/developer for the ${projectTypeDisplayNameLowercase}?"
          selectorMinInputLength=3
          restUrl=springUrl(operatorsRestUrl)
          preselectedItems=preselectedOperator!{}
        />

        <@fdsTextarea.textarea
          path="form.transferReason"
          labelText="What is the reason you are changing this ${projectTypeDisplayNameLowercase}'s operator/developer?"
        />

        <#assign customerMnemonic = service.customerMnemonic />
        <#assign customerSupplyChainInterfaceText = "${customerMnemonic} supply chain interface" />

        <#assign isPublishedAsOperatorFormBind="form.isPublishedAsOperator" />

        <@fdsRadio.radioGroup
          path=isPublishedAsOperatorFormBind
          labelText="Is this the operator/developer that should be shown on the ${customerSupplyChainInterfaceText}?"
          hiddenContent=true
        >
          <@fdsRadio.radioYes path=isPublishedAsOperatorFormBind/>
          <@fdsRadio.radioNo path=isPublishedAsOperatorFormBind>
            <@fdsSearchSelector.searchSelectorRest
              path="form.publishableOrganisation"
              labelText="Which operator/developer should be shown on the ${customerSupplyChainInterfaceText}?"
              selectorMinInputLength=3
              restUrl=springUrl(organisationUnitRestUrl)
              preselectedItems=preselectedPublishableOrganisation!{}
              nestingPath=isPublishedAsOperatorFormBind
              formGroupClass="govuk-!-margin-bottom-5"
            />
            <@fdsDetails.summaryDetails summaryTitle="The operator/developer I want shown is not listed">
              <p class="govuk-body">
                If the operator/developer that should be shown on the ${customerSupplyChainInterfaceText} is not shown in the list,
                you must check the organisation is registered on the Energy Portal.
              </p>
            </@fdsDetails.summaryDetails>
          </@fdsRadio.radioNo>
        </@fdsRadio.radioGroup>

        <@fdsAction.submitButtons
          primaryButtonText="Save and complete"
          linkSecondaryAction=true
          secondaryLinkText="Cancel"
          linkSecondaryActionUrl=springUrl(cancelUrl)
        />
      </@fdsForm.htmlForm>
    </@grid.twoThirdsColumn>
  </@grid.gridRow>
</@defaultPage>
