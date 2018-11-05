package service;

import model.IndexablePdf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

public interface PdfService {

  Path downloadPdf(URL url, String toPath) throws IOException;

  Path downloadPdf(InputStream is, String toPath) throws IOException;

  IndexablePdf createIndexableDocument(Path path) throws IOException;

}
