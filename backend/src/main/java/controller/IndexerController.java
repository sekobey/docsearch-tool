package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import helper.TempFileDeleter;
import model.IndexableInput;
import model.IndexerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.IndexerService;
import service.PdfService;
import service.indexers.ParallelFileIndexer;
import service.indexers.ParallelLinkIndexer;
import service.indexers.ParallelPdfIndexer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class IndexerController {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexerController.class);
  private static final String FILE_NAME = "temp.pdf";

  @Autowired
  PdfService pdfService;

  @Autowired
  IndexerService indexerService;

  @CrossOrigin
  @PostMapping(path = "/index")
  public List<IndexerResponse> indexDocuments(@RequestBody List<IndexableInput> input) {

    List<ParallelLinkIndexer> linkIndexers = input.stream()
            .map(item -> new ParallelLinkIndexer(indexerService, pdfService, item))
            .collect(Collectors.toList());

    return indexInParallel(linkIndexers);
  }

  @CrossOrigin
  @PostMapping(path = "/index-files")
  public List<IndexerResponse> indexDocuments(
          @RequestParam(value = "files") MultipartFile[] files,
          @RequestParam(value = "metadata") String json) throws IOException {

    Arrays.stream(files).forEach(item -> {
      System.out.printf("File Name: %s\n", item.getName());
      System.out.printf("File OriginalName: %s\n", item.getOriginalFilename());
    });

    ObjectMapper mapper = new ObjectMapper();
    IndexableInput[] inputDocuments = mapper.readValue(json, IndexableInput[].class);
    Stream<IndexableInput> stream = Arrays.stream(inputDocuments);
    stream.forEach(item -> System.out.printf("Version:%s, File Name: %s, Url:%s\n", item.getVersion(), item.getFileName(), item.getUrl()));

    Map<MultipartFile, Optional<IndexableInput>> fileMap = new HashMap<>();
    for (MultipartFile file : files) {
      stream = Arrays.stream(inputDocuments);
      fileMap.put(file, stream
              .filter(input -> input.getFileName().equalsIgnoreCase(file.getOriginalFilename()))
              .findFirst()
      );
    }

    List<ParallelFileIndexer> fileIndexers = fileMap.entrySet().stream().map(entry ->
            new ParallelFileIndexer(indexerService, pdfService, entry.getKey(), entry.getValue().orElse(new IndexableInput()))
    ).collect(Collectors.toList());

    return indexInParallel(fileIndexers);
  }

  private List<IndexerResponse> indexInParallel(List<? extends ParallelPdfIndexer> indexers) {
    ExecutorService executorService = Executors.newFixedThreadPool(5);
    List<Future<IndexerResponse>> futures = indexers.stream().map(item ->
            executorService.submit(
                    item
            )
    ).collect(Collectors.toList());
    executorService.shutdown();
    try {
      executorService.awaitTermination(1, TimeUnit.HOURS);
    } catch (InterruptedException e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException("An internal server error occured.");
    }

    List<IndexerResponse> result = futures.stream().map(item -> {
      try {
        return item.get();
      } catch (Exception e) {
        return new IndexerResponse("", "","", "indexing failed !!!");
      }
    }).collect(Collectors.toList());

    TempFileDeleter.deleteTempFiles();

    return result;
  }

}