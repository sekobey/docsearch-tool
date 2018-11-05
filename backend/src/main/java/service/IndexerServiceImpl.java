package service;

import model.IndexablePdf;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static model.IndexerConstants.*;

@Service
public class IndexerServiceImpl implements IndexerService {

  @Autowired
  IDGeneratorService idGenerator;

  @Autowired
  private RestHighLevelClient client;

  @Override
  public void initIndex() throws IOException {
    GetIndexRequest request = new GetIndexRequest();
    request.indices(SEARCH_INDEX_NAME, SUGGESTION_INDEX_NAME);
    boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
    if (!exists) {
      initSearchIndex();
      initSuggestionIndex();
    }
  }

  @Override
  public void indexDocument(IndexablePdf indexablePdf) throws IOException {
    Map<String, Object> searchIndexJsonMap = new HashMap<>();
    searchIndexJsonMap.put(FIELD_VERSION, indexablePdf.getVersion());
    searchIndexJsonMap.put(FIELD_FILE_NAME, indexablePdf.getFileName());
    searchIndexJsonMap.put(FIELD_URL, indexablePdf.getUrl());
    searchIndexJsonMap.put(FIELD_CONTENT, indexablePdf.getContentInText());
    IndexRequest request = new IndexRequest(SEARCH_INDEX_NAME,
            TYPE_NAME_SEARCHAPI,
            idGenerator.generateId(indexablePdf.getUrl()))
            .source(searchIndexJsonMap);
    client.index(request, RequestOptions.DEFAULT);

    indexablePdf.getTocContents().stream().map(tocContent -> {
      Map<String, Object> suggestionIndexJsonMap = new HashMap<>();
      suggestionIndexJsonMap.put(FIELD_VERSION, indexablePdf.getVersion());
      suggestionIndexJsonMap.put(FIELD_FILE_NAME, indexablePdf.getFileName());
      suggestionIndexJsonMap.put(FIELD_URL, indexablePdf.getUrl());
      suggestionIndexJsonMap.put(FIELD_TOC_TITLE, tocContent.getTitle());
      suggestionIndexJsonMap.put(FIELD_TOC_SUBTITLES, tocContent.getSubtitles());

      return new IndexRequest(SUGGESTION_INDEX_NAME,
              TYPE_NAME_SUGGESTIONAPI,
              idGenerator.generateId(indexablePdf.getUrl() + tocContent.getTitle()))
              .source(suggestionIndexJsonMap);
    })
    .collect(Collectors.toList())
    .forEach(suggestRequest -> {
      try {
        client.index(suggestRequest, RequestOptions.DEFAULT);
      } catch (IOException e) {
      }
    });
  }

  private void initSearchIndex() throws IOException {
    CreateIndexRequest request = new CreateIndexRequest(SEARCH_INDEX_NAME);
    XContentBuilder searchTypeBuilder = XContentFactory.jsonBuilder();
    searchTypeBuilder.startObject();
    {
      searchTypeBuilder.startObject(TYPE_NAME_SEARCHAPI);
      {
        searchTypeBuilder.startObject("properties");
        {
          searchTypeBuilder.startObject(FIELD_VERSION);
          {
            searchTypeBuilder.field("type", "keyword");
          }
          searchTypeBuilder.endObject();
          searchTypeBuilder.startObject(FIELD_URL);
          {
            searchTypeBuilder.field("type", "text");
          }
          searchTypeBuilder.endObject();
          searchTypeBuilder.startObject(FIELD_FILE_NAME);
          {
            searchTypeBuilder.field("type", "text");
          }
          searchTypeBuilder.endObject();
          searchTypeBuilder.startObject(FIELD_CONTENT);
          {
            searchTypeBuilder.field("type", "text");
          }
          searchTypeBuilder.endObject();
        }
        searchTypeBuilder.endObject();
      }
      searchTypeBuilder.endObject();
    }
    searchTypeBuilder.endObject();

    request.mapping(TYPE_NAME_SEARCHAPI, searchTypeBuilder);
    client.indices().create(request, RequestOptions.DEFAULT);
  }

  private void initSuggestionIndex() throws IOException {
    CreateIndexRequest request = new CreateIndexRequest(SUGGESTION_INDEX_NAME);
    XContentBuilder suggestionTypeBuilder = XContentFactory.jsonBuilder();
    suggestionTypeBuilder.startObject();
    {
      suggestionTypeBuilder.startObject(TYPE_NAME_SUGGESTIONAPI);
      {
        suggestionTypeBuilder.startObject("properties");
        {
          suggestionTypeBuilder.startObject(FIELD_VERSION);
          {
            suggestionTypeBuilder.field("type", "keyword");
          }
          suggestionTypeBuilder.endObject();
          suggestionTypeBuilder.startObject(FIELD_URL);
          {
            suggestionTypeBuilder.field("type", "text");
          }
          suggestionTypeBuilder.endObject();
          suggestionTypeBuilder.startObject(FIELD_FILE_NAME);
          {
            suggestionTypeBuilder.field("type", "text");
          }
          suggestionTypeBuilder.endObject();
          suggestionTypeBuilder.startObject(FIELD_TOC_TITLE);
          {
            suggestionTypeBuilder.field("type", "text");
          }
          suggestionTypeBuilder.endObject();
          suggestionTypeBuilder.startObject(FIELD_TOC_SUBTITLES);
          {
            suggestionTypeBuilder.field("type", "text");
          }
          suggestionTypeBuilder.endObject();
        }
        suggestionTypeBuilder.endObject();
      }
      suggestionTypeBuilder.endObject();
    }
    suggestionTypeBuilder.endObject();

    request.mapping(TYPE_NAME_SUGGESTIONAPI, suggestionTypeBuilder);
    client.indices().create(request, RequestOptions.DEFAULT);

  }

}
