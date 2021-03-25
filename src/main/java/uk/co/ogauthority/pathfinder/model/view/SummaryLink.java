package uk.co.ogauthority.pathfinder.model.view;

import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SummaryLink that = (SummaryLink) o;
    return Objects.equals(linkText, that.linkText)
        && Objects.equals(url, that.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(linkText, url);
  }
}
