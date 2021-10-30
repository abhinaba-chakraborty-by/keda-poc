package com.abhi;

import static com.azure.spring.integration.core.AzureHeaders.CHECKPOINTER;

import com.azure.spring.integration.core.api.Checkpointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@SpringBootApplication
public class ConsumerApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(ConsumerApplication.class, args);
  }

  @Bean
  public Consumer<Message<String>> consume() {
    return message -> {
      Checkpointer checkpointer = (Checkpointer) message.getHeaders().get(CHECKPOINTER);
      LOGGER.info("Message received: '{}'. Processing it...", message.getPayload());
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      checkpointer.success();
    };
  }
}
