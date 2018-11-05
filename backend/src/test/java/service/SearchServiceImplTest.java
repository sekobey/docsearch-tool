package service;

import model.SearchResult;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHits;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class SearchServiceImplTest {

  @TestConfiguration
  static class TestConfig {

    static RestHighLevelClient restHighLevelClient = mock(RestHighLevelClient.class);

    @Bean
    public SearchServiceImpl searchService() {
      return new SearchServiceImpl();
    }

    @Bean
    public RestHighLevelClient restHighLevelClient() {
      return restHighLevelClient;
    }

  }

  @Autowired
  SearchService searchService;

  @MockBean
  SearchResponse response;

  @Test
  public void testSearchIfResultNotFound() {
    try {
      when(response.getHits()).thenReturn(SearchHits.empty());
      when(TestConfig.restHighLevelClient.search(any(SearchRequest.class), any(RequestOptions.class))).thenReturn(response);
      List<SearchResult> result = searchService.search("customize");
      Assert.assertEquals(0, result.size());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
