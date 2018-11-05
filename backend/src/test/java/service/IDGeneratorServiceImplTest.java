package service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class IDGeneratorServiceImplTest {

  @TestConfiguration
  static class TestConfig {

    @Bean
    public IDGeneratorServiceImpl idGeneratorService() {
      return new IDGeneratorServiceImpl();
    }
  }

  @Autowired
  IDGeneratorService idGeneratorService;

  @Test
  public void testGenerateIdUsingMD5HashAlgorithmIfURLofPdfExists() {
    String id = idGeneratorService.generateId("http://localhost:8080/test.pdf");
    Assert.assertEquals(32, id.length());
  }

}
