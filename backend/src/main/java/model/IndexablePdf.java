package model;

import java.util.List;

public class IndexablePdf {
  private String version;
  private String fileName;
  private String url;
  private String contentInText;
  private List<TocContent> tocContents;

  public IndexablePdf() {
  }

  public IndexablePdf(String contentInText, List<TocContent> tocContents) {
    this.contentInText = contentInText;
    this.tocContents = tocContents;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getFileName() {return fileName;}

  public void setFileName(String fileName) {this.fileName = fileName;}

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getContentInText() {
    return contentInText;
  }

  public void setContentInText(String contentInText) {
    this.contentInText = contentInText;
  }

  public List<TocContent> getTocContents() {
    return tocContents;
  }

  public void setTocContents(List<TocContent> tocContents) {
    this.tocContents = tocContents;
  }
}