package com.ifan112.demo.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class DemoRocketMQConsumer {

    private static final String LOCAL_NAMESRV = "localhost:9876";

    private static final String TEST_TOPIC_1 = "test_topic_1";
    private static final String TEST_CONSUMER_GROUP_1 = "test_consumer_group_2";

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer testGroup1Consumer = new DefaultMQPushConsumer(TEST_CONSUMER_GROUP_1);

        testGroup1Consumer.setNamesrvAddr(LOCAL_NAMESRV);

        testGroup1Consumer.subscribe(TEST_TOPIC_1, "*");

        testGroup1Consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgLists,
                                                            ConsumeConcurrentlyContext context) {

                System.out.printf("threadId: %s, threadName: %s",
                        Thread.currentThread().getId(), Thread.currentThread().getName() );

                System.out.println("messageList size: " + msgLists.size());

                msgLists.forEach(msg -> {
                    System.out.println(new String(msg.getBody()));
                });

                System.out.println();

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        testGroup1Consumer.start();

    }
}
