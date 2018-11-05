package service;

import model.SearchResult;
import model.SuggestResult;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static model.IndexerConstants.*;

@Service
public class SearchServiceImpl implements SearchService {

  @Autowired
  private RestHighLevelClient client;

  @Override
  public List<SearchResult> search(String phrase) throws IOException {
    QueryBuilder queryBuilder = QueryBuilders.matchQuery(FIELD_CONTENT, phrase);
    SearchHits hits = query(queryBuilder, SEARCH_INDEX_NAME);
    if (hits.totalHits == 0)
      return new LinkedList<>();
    else {
      List<SearchResult> result = new LinkedList<>();
      for (SearchHit hit : hits.getHits()) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        SearchResult searchResult = new SearchResult();
        searchResult.setVersion((String) sourceAsMap.get(FIELD_VERSION));
        searchResult.setFileName((String) sourceAsMap.get(FIELD_FILE_NAME));
        searchResult.setUrl((String) sourceAsMap.get(FIELD_URL));
        result.add(searchResult);
      }
      return result;
    }
  }

  @Override
  public List<SearchResult> suggest(String phrase) throws IOException {
    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
    boolQueryBuilder.minimumShouldMatch(1);
    boolQueryBuilder.should(QueryBuilders.matchQuery(FIELD_TOC_TITLE, phrase));
    boolQueryBuilder.should(QueryBuilders.matchQuery(FIELD_TOC_SUBTITLES, phrase));

    SearchHits hits = query(boolQueryBuilder, SUGGESTION_INDEX_NAME);
    if (hits.totalHits == 0)
      return new LinkedList<>();
    else {
      List<SearchResult> result = new LinkedList<>();
      for (SearchHit hit : hits.getHits()) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        SuggestResult searchResult = new SuggestResult();
        searchResult.setVersion((String) sourceAsMap.get(FIELD_VERSION));
        searchResult.setFileName((String) sourceAsMap.get(FIELD_FILE_NAME));
        searchResult.setUrl((String) sourceAsMap.get(FIELD_URL));
        searchResult.setTitle((String) sourceAsMap.get(FIELD_TOC_TITLE));
        result.add(searchResult);
      }
      return result;
    }
  }

  private SearchHits query(QueryBuilder queryBuilder, String index) throws IOException {
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(queryBuilder);
    sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));

    SearchRequest request = new SearchRequest();
    request.indices(index).source(sourceBuilder);
    SearchResponse response = client.search(request, RequestOptions.DEFAULT);

    return response.getHits();
  }
}
