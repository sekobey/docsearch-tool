package model;

public class IndexerResponse {
  private String version;
  private String fileName;
  private String url;
  private String message;

  public IndexerResponse(String version, String fileName, String url, String message) {
    this.version = version;
    this.fileName = fileName;
    this.url = url;
    this.message = message;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getFileName() {return fileName;}

  public void setFileName(String fileName) {this.fileName = fileName;}

  public String getUrl() { return url;}

  public void setUrl(String url) {
    this.url = url;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
