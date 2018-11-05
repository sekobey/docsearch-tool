package service.indexers;

import model.IndexableInput;
import service.IndexerService;
import service.PdfService;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public class ParallelLinkIndexer extends ParallelPdfIndexer {

  public ParallelLinkIndexer(IndexerService indexerService, PdfService pdfService, IndexableInput indexableInput) {
    super(indexerService, pdfService, indexableInput);
  }

  @Override
  public Path pathOfPdf() throws IOException {
    return pdfService.downloadPdf(new URL(indexableInput.getUrl()),
            Thread.currentThread().getId()+".pdf");
  }

}
