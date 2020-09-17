package uk.co.ogauthority.pathfinder.model.view;

public class SummaryLink {

  private String linkText;

  private String url;

  public SummaryLink(String linkText, String url) {
    this.linkText = linkText;
    this.url = url;
  }

  public String getLinkText() {
    return linkText;
  }

  public void setLinkText(String linkText) {
    this.linkText = linkText;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
