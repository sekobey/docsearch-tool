package service;

import model.IndexablePdf;

import java.io.IOException;

public interface IndexerService {

  void initIndex() throws IOException;

  void indexDocument(IndexablePdf indexablePdf) throws IOException;
}
