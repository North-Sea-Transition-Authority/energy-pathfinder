<#include '../layout.ftl'>

<@defaultPage htmlTitle="Test template" pageHeading="Template for tests" twoThirdsColumn=false>
  <@fdsAddToList.addToList
    path="form.licenceBlocks"
    alreadyAdded=alreadyAdded
    title="Licence blocks"
    itemName="Licence block" <#-- This could be used for column heading and could be subbed into selector label and inset text in place of those params? -->
    noItemText="No licence blocks added"
    addToListId="licence-block-table"
    selectorLabelText="Add a licence block"
    restUrl=springUrl(licenceBlockUrl)
  />
</@defaultPage>