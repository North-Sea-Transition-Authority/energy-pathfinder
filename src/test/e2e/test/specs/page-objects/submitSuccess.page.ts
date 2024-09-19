import {Page} from "./page";

export class SubmitSuccessPage extends Page {
    public async getPageTitle() {
        return $("//h1").getText();
    }

    public async getProjectTitle() {
        return $("//*[contains(text(), 'Automation Test Project Number')]").getText();
    }
}