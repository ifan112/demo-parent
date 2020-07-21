package com.ifan112.demo.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class DemoRocketMQConsumer {

    private static final String LOCAL_NAMESRV = "10.10.8.167:9876";

    private static final String TEST_TOPIC_1 = "test_topic_1";
    private static final String TEST_CONSUMER_GROUP_1 = "test_consumer_group_1";

    public static void main(String[] args) throws MQClientException {
        defaultConsumer();

        // 广播
        // broadcastConsumer();

        // fifoConsumer();
    }

    private static void fifoConsumer() throws MQClientException {
        DefaultMQPushConsumer testGroup1Consumer = new DefaultMQPushConsumer(TEST_CONSUMER_GROUP_1);

        testGroup1Consumer.setNamesrvAddr(LOCAL_NAMESRV);

        testGroup1Consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        testGroup1Consumer.subscribe(TEST_TOPIC_1, "TagA || TagC || TagD");

        testGroup1Consumer.registerMessageListener(new MessageListenerOrderly() {

            AtomicLong consumeTimes = new AtomicLong(0);

            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgLists,
                                                       ConsumeOrderlyContext context) {

                context.setAutoCommit(false);

                System.out.printf("threadId: %s, threadName: %s",
                        Thread.currentThread().getId(), Thread.currentThread().getName());

                System.out.println("messageList size: " + msgLists.size());

                msgLists.forEach(msg -> {
                    System.out.println(Thread.currentThread().getId() + " - " + new String(msg.getBody()));
                });

                System.out.println();

                consumeTimes.incrementAndGet();

                if ((this.consumeTimes.get() % 2) == 0) {
                    return ConsumeOrderlyStatus.SUCCESS;
                } else if ((this.consumeTimes.get() % 3) == 0) {
                    return ConsumeOrderlyStatus.ROLLBACK;
                } else if ((this.consumeTimes.get() % 4) == 0) {
                    return ConsumeOrderlyStatus.COMMIT;
                } else if ((this.consumeTimes.get() % 5) == 0) {
                    context.setSuspendCurrentQueueTimeMillis(3000);
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }

                return ConsumeOrderlyStatus.SUCCESS;
            }
        });

        testGroup1Consumer.start();

    }

    private static void broadcastConsumer() throws MQClientException {
        DefaultMQPushConsumer testGroup1Consumer = new DefaultMQPushConsumer(TEST_CONSUMER_GROUP_1);

        testGroup1Consumer.setNamesrvAddr(LOCAL_NAMESRV);

        // 广播
        testGroup1Consumer.setMessageModel(MessageModel.BROADCASTING);
        testGroup1Consumer.subscribe(TEST_TOPIC_1, "*");

        testGroup1Consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgLists,
                                                            ConsumeConcurrentlyContext context) {

                System.out.printf("threadId: %s, threadName: %s",
                        Thread.currentThread().getId(), Thread.currentThread().getName());

                System.out.println("messageList size: " + msgLists.size());

                msgLists.forEach(msg -> {
                    System.out.println(Thread.currentThread().getId() + " - " + new String(msg.getBody()));
                });

                System.out.println();

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        testGroup1Consumer.start();
    }

    private static void defaultConsumer() throws MQClientException {
        DefaultMQPushConsumer testGroup1Consumer = new DefaultMQPushConsumer(TEST_CONSUMER_GROUP_1);

        testGroup1Consumer.setNamesrvAddr(LOCAL_NAMESRV);

        testGroup1Consumer.subscribe(TEST_TOPIC_1, "*");

        testGroup1Consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgLists,
                                                            ConsumeConcurrentlyContext context) {

                System.out.printf("threadId: %s, threadName: %s",
                        Thread.currentThread().getId(), Thread.currentThread().getName());

                System.out.println("messageList size: " + msgLists.size());

                msgLists.forEach(msg -> {
                    System.out.println(Thread.currentThread().getId() + " - " + new String(msg.getBody()));
                });

                System.out.println();

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        testGroup1Consumer.start();
    }
}
