import {Page} from "./page.ts";

export class PageUtils extends Page{
    public async signOut () {
        await $(`a=Sign out`).click();
    }
}