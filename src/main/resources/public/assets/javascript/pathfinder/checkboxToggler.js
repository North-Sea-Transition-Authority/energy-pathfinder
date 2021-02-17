class CheckboxToggler {

  constructor($togglerContainer) {
    this.$checkboxContainerId = $togglerContainer.attr('data-checkbox-selection-toggler-container-id');

    this.$togglerSelectAllLink = $togglerContainer.find("a.checkbox-selection-toggler__select-all-link");
    this.$togglerSelectNoneLink = $togglerContainer.find("a.checkbox-selection-toggler__select-none-link");

    this._setupTogglerInputs();
  }

  _setupTogglerInputs() {

    if(this.$togglerSelectAllLink){
      this.$togglerSelectAllLink.click((e) => {
        e.preventDefault();
        this._toggleCheckBoxes(this.$checkboxContainerId, true);
      });
    }

    if(this.$togglerSelectNoneLink){
      this.$togglerSelectNoneLink.click((e) => {
        e.preventDefault();
        this._toggleCheckBoxes(this.$checkboxContainerId, false);
      });
    }
  }

  _toggleCheckBoxes(checkboxContainerId, isSelected){
    $(`#${checkboxContainerId} input:checkbox`).prop('checked', isSelected);
  }
}

$(document).ready(() => {
  $('.checkbox-selection-toggler__toggler').each((index, element) => new CheckboxToggler($(element)));
});