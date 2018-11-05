package service.indexers;

import model.IndexableInput;
import org.springframework.web.multipart.MultipartFile;
import service.IndexerService;
import service.PdfService;

import java.io.IOException;
import java.nio.file.Path;

public class ParallelFileIndexer extends ParallelPdfIndexer {

  private MultipartFile file;

  public ParallelFileIndexer(IndexerService indexerService, PdfService pdfService, MultipartFile file, IndexableInput indexableInput) {
    super(indexerService, pdfService, indexableInput);
    this.file = file;
  }

  @Override
  public Path pathOfPdf() throws IOException {
    return pdfService.downloadPdf(file.getInputStream(),
            Thread.currentThread().getId()+".pdf");
  }

}
