import {Page} from "./page.ts";

export class ProjectInformationPage extends Page{
    public async enterProjectTitle (title: string) {
        await $("#projectTitle").setValue(title);
    }
}