<#include '../../layout.ftl'>

<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="backLink" type="Boolean" -->
<#-- @ftlvariable name="breadCrumbs" type="Boolean" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="operatorsRestUrl" type="String" -->
<#-- @ftlvariable name="preselectedOperator" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="primaryButtonText" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->
<#-- @ftlvariable name="regulatorEmailAddress" type="String" -->
<#-- @ftlvariable name="organisationUnitRestUrl" type="String" -->
<#-- @ftlvariable name="preselectedPublishableOrganisation" type="java.util.Map<String, String>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  backLink=backLink
  breadcrumbs=breadCrumbs
  twoThirdsColumn=true
  errorItems=errorList
>
  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest
      path="form.operator"
      labelText="Who is the operator for the project?"
      selectorMinInputLength=0
      restUrl=springUrl(operatorsRestUrl)
      preselectedItems=preselectedOperator!{}
    />

    <@fdsDetails.summaryDetails summaryTitle="The operator I want to create a project for is not listed">
      <p class="govuk-body">
        If the operator you need to create a project for is not shown in the list then you must contact
        the operator to provide you with access to their organisation.
      </p>
    </@fdsDetails.summaryDetails>

    <#assign customerMnemonic = service.customerMnemonic />
    <#assign customerSupplyChainInterfaceText = "${customerMnemonic} supply chain interface" />

    <#assign isPublishedAsOperatorFormBind="form.isPublishedAsOperator" />
    <@fdsRadio.radioGroup
      path=isPublishedAsOperatorFormBind
      labelText="Is this the operator you want shown on the ${customerSupplyChainInterfaceText}?"
      hiddenContent=true
    >
      <@fdsRadio.radioYes path=isPublishedAsOperatorFormBind/>
      <@fdsRadio.radioNo path=isPublishedAsOperatorFormBind>
        <@fdsSearchSelector.searchSelectorRest
          path="form.publishableOrganisation"
          labelText="Which operator do you want shown on the ${customerSupplyChainInterfaceText}?"
          selectorMinInputLength=0
          restUrl=springUrl(organisationUnitRestUrl)
          preselectedItems=preselectedPublishableOrganisation!{}
          nestingPath=isPublishedAsOperatorFormBind
          formGroupClass="govuk-!-margin-bottom-5"
        />
        <@fdsDetails.summaryDetails summaryTitle="The operator I want shown is not listed">
          <p class="govuk-body">
            If the operator you want shown on the ${customerSupplyChainInterfaceText} is not shown in the list then you must contact
            the <@mailTo.mailToLink linkText=customerMnemonic mailToEmailAddress=regulatorEmailAddress /> to register your organisation on the Energy Portal.
          </p>
        </@fdsDetails.summaryDetails>
      </@fdsRadio.radioNo>
    </@fdsRadio.radioGroup>

    <@fdsAction.submitButtons
      primaryButtonText=primaryButtonText
      linkSecondaryAction=true
      secondaryLinkText="Cancel"
      linkSecondaryActionUrl=springUrl(cancelUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>