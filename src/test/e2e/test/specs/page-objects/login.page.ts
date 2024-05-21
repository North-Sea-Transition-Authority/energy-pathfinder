import {Page} from './page'

const pathfinderSubmitter = "submitter.e2e@pathfinder.co.uk"
const accessManager = "access-manager.e2e@pathfinder.co.uk";
const password = "dev";

/**
 * Page model for the Fox SAML login screen
 */
export class LoginPage extends Page {

    get usernameInput () {
        return $('input[type="text"]');
    }

    get passwordInput () {
        return $('input[type="password"]');
    }

    get submitButton () {
        return $('input[type="submit"]');
    }

    get returnToScapPortalButton(){
        return $('input[type="button"]');
    }

    public async login (username: string, password: string) {
        await this.usernameInput.setValue(username);
        await this.passwordInput.setValue(password);
        await this.submitButton.click();
    }

    open () {
        return super.open('work-area');
    }

    async loginAccessManager() {
        await this.login(accessManager, password);
    }

    async loginPathfinderSubmitter() {
        await this.login(pathfinderSubmitter, password);
    }

}