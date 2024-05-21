import {LoginPage} from "./page-objects/login.page"
import {PageUtils} from "./page-objects/pageUtils.ts";

const loginPage = new LoginPage();
const pageUtils = new PageUtils();
const username = "access-manager.e2e@pathfinder.co.uk";
const noPrivUsername = "IndustryAccessManager@scap.co.uk";
const invalidUsername = "invalid@fivium.co.uk";
const password = "dev";
const invalidPassword = "invalidPassword";

describe('Pathfinder Login', () => {
    it('should not login with invalid username', async () => {
        await loginPage.open();
        await loginPage.login(invalidUsername, password);
        const invalidMessage = await $('div*=Invalid username or password');
        await expect(invalidMessage.isDisplayed());
    });
    it('should not login with invalid password', async () => {
        await loginPage.open();
        await loginPage.login(username, invalidPassword);
        const invalidMessage = await $('div*=Invalid username or password');
        await expect(invalidMessage.isDisplayed);
    });
    it('should log in with valid username and password', async () => {
        await loginPage.open();
        await loginPage.login(username, password);
        await expect(browser).toHaveTitle(expect.stringContaining('Work area - Energy Pathfinder'));
        await pageUtils.signOut();
    });
    it('should log in with valid username and password but no access to Pathfinder', async () => {
        await loginPage.open();
        await loginPage.login(noPrivUsername, password);
        await expect(browser).toHaveTitle(expect.stringContaining('You do not have permission to view this page - Energy Pathfinder'));
        await pageUtils.signOut();
    });

});