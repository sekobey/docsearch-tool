package service;

import model.IndexablePdf;
import model.TocContent;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class PdfServiceImpl implements PdfService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PdfServiceImpl.class);

  @Override
  public Path downloadPdf(URL url, String toPath) throws IOException {
    return downloadPdf(url.openStream(), toPath);
  }

  public Path downloadPdf(InputStream is, String toPath) throws IOException {
    ReadableByteChannel readableByteChannel = null;
    FileOutputStream fileOutputStream = null;
    Path filePath;
    try {
      readableByteChannel = Channels.newChannel(is);
      fileOutputStream = new FileOutputStream(toPath);
      fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
      filePath = Paths.get(toPath);
      if (Files.exists(filePath))
        return filePath;
    } finally {
      try {
        if (readableByteChannel != null)
          readableByteChannel.close();
        if (fileOutputStream != null)
          fileOutputStream.close();
      } catch (IOException ex) {
        LOGGER.error("Problem Occured While Closing The Object= " + ex.getMessage());
      }
    }

    return filePath;
  }

  @Override
  public IndexablePdf createIndexableDocument(Path path) throws IOException {
    try (PDDocument document = PDDocument.load(path.toFile())) {
      PDDocumentOutline documentOutline = document.getDocumentCatalog().getDocumentOutline();
      Stream<PDOutlineItem> stream = StreamSupport.stream(documentOutline.children().spliterator(), true);
      List<TocContent> tocContents = stream.map(item ->
              new TocContent(item.getTitle(), readOutlineItem(item.children())
              ))
              .collect(Collectors.toList());

      PDFTextStripperByArea stripper = new PDFTextStripperByArea();
      stripper.setSortByPosition(true);
      PDFTextStripper tStripper = new PDFTextStripper();
      String pdfFileInText = tStripper.getText(document);

      return new IndexablePdf(pdfFileInText, tocContents);
    }
  }

  private List<String> readOutlineItem(Iterable<PDOutlineItem> children) {
    Stream<PDOutlineItem> stream = StreamSupport.stream(children.spliterator(), true);

    return stream.map(item -> {
              List<String> result = new LinkedList<>();
              result.add(item.getTitle());
              result.addAll(readOutlineItem(item.children()));
              return result;
            }
    )
    .flatMap(x -> x.stream())
    .collect(Collectors.toList());
  }
}
