package model;

import java.util.List;

public class TocContent {
  private String title;
  private List<String> subtitles;

  public TocContent(String title, List<String> subtitles) {
    this.title = title;
    this.subtitles = subtitles;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<String> getSubtitles() {
    return subtitles;
  }

  public void setSubtitles(List<String> subtitles) {
    this.subtitles = subtitles;
  }
}