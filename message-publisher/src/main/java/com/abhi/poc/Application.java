package com.abhi.poc;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusMessageBatch;
import com.azure.messaging.servicebus.ServiceBusSenderClient;

import java.util.ArrayList;
import java.util.List;

public class Application {

  private static final String CONNECTION_STRING = "";
  private static final String QUEUE_NAME = "item-ingestion-queue";

  public static void main(String[] args) throws InterruptedException {
    startSendingMessages();
  }

  private static void startSendingMessages() throws InterruptedException {
    int msgCounter = 1;
    try (ServiceBusSenderClient senderClient = new ServiceBusClientBuilder().connectionString(CONNECTION_STRING).sender().queueName(QUEUE_NAME).buildClient()) {
      Thread.sleep(5000);
      for (; msgCounter <= 100; msgCounter++) {
        senderClient.sendMessage(new ServiceBusMessage("Message " + msgCounter));
      }
      System.out.println("Sent 100 messages one by one to the queue");
      Thread.sleep(5000);

      ServiceBusMessageBatch messageBatch = senderClient.createMessageBatch();
      List<ServiceBusMessage> listOfNext10000Messages = new ArrayList<>();
      for (; msgCounter <= 10100; msgCounter++) {
        listOfNext10000Messages.add(new ServiceBusMessage("Message " + msgCounter));
      }
      for (ServiceBusMessage message : listOfNext10000Messages) {
        if (messageBatch.tryAddMessage(message)) {
          continue;
        }
        senderClient.sendMessages(messageBatch);
        System.out.println("Sent " + messageBatch.getCount() + " batch of messages to the queue: " + QUEUE_NAME);
        messageBatch = senderClient.createMessageBatch();
        if (!messageBatch.tryAddMessage(message)) {
          System.err.printf("Message is too large for an empty batch. Skipping. Max size: %s.", messageBatch.getMaxSizeInBytes());
        }
      }
      if (messageBatch.getCount() > 0) {
        senderClient.sendMessages(messageBatch);
        System.out.println("Sent " + messageBatch.getCount() + " batch of messages to the queue: " + QUEUE_NAME);
      }
    }
  }
}
