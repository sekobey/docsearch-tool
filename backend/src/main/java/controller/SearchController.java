package controller;

import model.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.SearchService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
public class SearchController {

  private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

  @Autowired
  SearchService searchService;

  @CrossOrigin
  @GetMapping(path = "/search")
  public List<SearchResult> search(@RequestParam(value="phrase") String phrase) throws IOException {
    if (phrase.isEmpty())
      return Collections.emptyList();

    LOGGER.info("Phrase: {} is searched.", phrase);
    return searchService.search(phrase);
  }

  @CrossOrigin
  @GetMapping(path = "/suggest")
  public List<SearchResult> suggest(@RequestParam(value="phrase") String phrase) throws IOException {
    if (phrase.isEmpty())
      return Collections.emptyList();

    LOGGER.info("Phrase: {} is searched for suggestion.", phrase);
    return searchService.suggest(phrase);
  }

}
