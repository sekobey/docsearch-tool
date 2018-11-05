package main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import service.IndexerServiceImpl;

import java.io.IOException;

@SpringBootApplication(scanBasePackages = {"controller", "service", "main", "configuration"})
public class Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

    IndexerServiceImpl indexerService = context.getBean(IndexerServiceImpl.class);
    try {
      indexerService.initIndex();
      LOGGER.info("Indexes are created sucessfully.");
    } catch (IOException e) {
      throw new RuntimeException("Indexes are not created correctly", e);
    }
  }
}