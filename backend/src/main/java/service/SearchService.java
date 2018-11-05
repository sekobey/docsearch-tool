package service;

import model.SearchResult;

import java.io.IOException;
import java.util.List;

public interface SearchService {

  List<SearchResult> search(String phrase) throws IOException;

  List<SearchResult> suggest(String phrase) throws IOException;

}
