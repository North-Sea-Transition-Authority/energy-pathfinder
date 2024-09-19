import {Page} from "./page.ts";

export class ManageProjectPage extends Page{
    public async getProjectStatus() {
        return $("//dt[contains(text(), 'Status')]/following-sibling::dd").getText();
    }
}