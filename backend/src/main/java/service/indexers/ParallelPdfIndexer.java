package service.indexers;

import controller.IndexerController;
import model.IndexableInput;
import model.IndexablePdf;
import model.IndexerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.IndexerService;
import service.PdfService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;


public abstract class ParallelPdfIndexer implements Callable<IndexerResponse> {

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexerController.class);

  protected IndexerService indexerService;
  protected PdfService pdfService;
  protected IndexableInput indexableInput;

  public ParallelPdfIndexer(IndexerService indexerService, PdfService pdfService, IndexableInput indexableInput) {
    this.indexerService = indexerService;
    this.pdfService = pdfService;
    this.indexableInput = indexableInput;
  }

  public abstract Path pathOfPdf() throws IOException;

  @Override
  public IndexerResponse call() throws Exception {
    LOGGER.info("Version: {}, FileName: {}, Url: {}\n", indexableInput.getVersion(),
            indexableInput.getFileName(),
            indexableInput.getUrl());
    try {
      Path path = pathOfPdf();
      IndexablePdf indexablePdf = pdfService.createIndexableDocument(path);
      indexablePdf.setVersion(indexableInput.getVersion());
      indexablePdf.setFileName(indexableInput.getFileName());
      indexablePdf.setUrl(indexableInput.getUrl());
      indexerService.indexDocument(indexablePdf);
      return new IndexerResponse(indexableInput.getVersion(), indexableInput.getUrl(), indexableInput.getFileName(), "Sucessfully indexed.");
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
      return new IndexerResponse(indexableInput.getVersion(), indexableInput.getUrl(), indexableInput.getFileName(), "Indexing failed !!!");
    }
  }
}
