<#include '../../layout.ftl'/>

<#-- @ftlvariable name="labelText" type="String" -->
<#-- @ftlvariable name="path" type="String" -->
<#-- @ftlvariable name="selectableItems" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.forminput.selectableitem.SelectableItem>" -->

<#macro radioItems
  labelText
  path
  selectableItems
>
  <@fdsRadio.radioGroup
    labelText=labelText
    path=path
    hiddenContent=false
  >
    <#list selectableItems as selectableItem>

      <#local selectableItemMap = {selectableItem.identifier: selectableItem.prompt} />

      <@fdsRadio.radioItem
        path=path
        itemMap=selectableItemMap
        itemHintText=selectableItem.hintText!""
        isFirstItem=selectableItem?is_first
      />

    </#list>
  </@fdsRadio.radioGroup>
</#macro>