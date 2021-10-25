package com.abhi.poc;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusMessageBatch;
import com.azure.messaging.servicebus.ServiceBusSenderClient;

import java.util.Arrays;
import java.util.List;

public class Application {

  private static final String CONNECTION_STRING = "";
  private static final String QUEUE_NAME = "item-ingestion-queue";

  public static void main(String[] args) {
    sendMessageBatch();
  }

  private static void sendMessageBatch() {
    try (ServiceBusSenderClient senderClient = new ServiceBusClientBuilder().connectionString(CONNECTION_STRING).sender().queueName(QUEUE_NAME).buildClient()) {
      ServiceBusMessageBatch messageBatch = senderClient.createMessageBatch();
      List<ServiceBusMessage> listOfMessages = createMessages();
      for (ServiceBusMessage message : listOfMessages) {
        if (messageBatch.tryAddMessage(message)) {
          continue;
        }
        senderClient.sendMessages(messageBatch);
        System.out.println("Sent a batch of messages to the queue: " + QUEUE_NAME);
        messageBatch = senderClient.createMessageBatch();
        if (!messageBatch.tryAddMessage(message)) {
          System.err.printf("Message is too large for an empty batch. Skipping. Max size: %s.", messageBatch.getMaxSizeInBytes());
        }
      }
      if (messageBatch.getCount() > 0) {
        senderClient.sendMessages(messageBatch);
        System.out.println("Sent a batch of messages to the queue: " + QUEUE_NAME);
      }
    }
  }

  private static List<ServiceBusMessage> createMessages() {
    ServiceBusMessage[] messages = {
        new ServiceBusMessage("First message"),
        new ServiceBusMessage("Second message"),
        new ServiceBusMessage("Third message")
    };
    return Arrays.asList(messages);
  }
}
