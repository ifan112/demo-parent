package com.ifan112.demo.rocketmq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.time.LocalTime;
import java.util.List;

public class DemoRocketMQApp {

    private static final String LOCAL_NAMESRV = "10.10.8.167:9876";

    private static final String TEST_TOPIC_1 = "test_topic_1";

    private static final String TEST_PRODUCER_GROUP_1 = "test_producer_group_1";

    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException, RemotingException, InterruptedException, MQBrokerException {

        DefaultMQProducer testProducer = new DefaultMQProducer(TEST_PRODUCER_GROUP_1);

        // 一个消息发送者默认有4个消息队列
        testProducer.setNamesrvAddr(LOCAL_NAMESRV);
        testProducer.start();

        String content = "Hello, RocketMQ! Second: " + LocalTime.now().getSecond();
        Message message = new Message(TEST_TOPIC_1, content.getBytes(RemotingHelper.DEFAULT_CHARSET));
        // 同步发送
        testProducer.send(message);


        // testMultiSender(testProducer);

        // testThreadSender(testProducer);

        // testSendOrderMessage(testProducer);

        testProducer.shutdown();
    }

    private static void testSendOrderMessage(DefaultMQProducer testProducer) {
        try {
            String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};

            /*
             * 以主题为订单的消息为例，同一个订单消息可以分为创建、支付和完成等不同的标签，并且，它们之间有着严格的前后顺序
             *
             * 在生产端，可以将同一个订单消息放入相同的 MessageQueue 中，这样可以保证按顺序发送
             *
             * 在服务端，
             *
             *
             *
             */

            for (int i = 0; i < 100; i++) {
                int orderId = i % 10;

                Message message = new Message(TEST_TOPIC_1, tags[i % 5], "KEY-" + i, ("Hello RocketMQ! Index: " + i).getBytes());

                testProducer.send(message, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        // 订单 id
                        Integer orderId = (Integer) arg;
                        /*
                         * 属于同一个订单的消息都放入相同的消息队列中，可以保证按序发送
                         *
                         * 值得注意的是，MessageQueue 是指每个 Topic 在 Broker 中存放消息的队列，并非是 Produce 本地
                         * 用于暂存待发送消息的一个内存队列。
                         */
                        return mqs.get(orderId % mqs.size());
                    }
                }, orderId);
            }
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void testThreadSender(DefaultMQProducer testProducer) {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 100; i++) {
                        String content = "Hello, RocketMQ! Second: " + LocalTime.now().getSecond() + ", Index: " + i;
                        Message message = new Message(TEST_TOPIC_1, content.getBytes(RemotingHelper.DEFAULT_CHARSET));

                        // 同步发送
                        SendResult sendResult = testProducer.send(message);
                        System.out.println(Thread.currentThread().getId() + " - Index: " + i + ", msgId: " + sendResult.getMsgId());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MQClientException e) {
                    e.printStackTrace();
                } catch (RemotingException e) {
                    e.printStackTrace();
                } catch (MQBrokerException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 100; i++) {
                        String content = "Hello, RocketMQ! Second: " + LocalTime.now().getSecond() + ", Index: " + i;
                        Message message = new Message(TEST_TOPIC_1, content.getBytes(RemotingHelper.DEFAULT_CHARSET));

                        // 同步发送
                        SendResult sendResult = testProducer.send(message);
                        System.out.println(Thread.currentThread().getId() + " - Index: " + i + ", msgId: " + sendResult.getMsgId());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (MQClientException e) {
                    e.printStackTrace();
                } catch (RemotingException e) {
                    e.printStackTrace();
                } catch (MQBrokerException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread1.start();
        thread2.start();
    }

    private static void testMultiSender(DefaultMQProducer testProducer) {
        try {
            String content = "Hello, RocketMQ! Second: " + LocalTime.now().getSecond();
            Message message = new Message(TEST_TOPIC_1, content.getBytes(RemotingHelper.DEFAULT_CHARSET));
            // 同步发送
            testProducer.send(message);


            String content2 = "Hello, RocketMQ 2 ! Second: " + LocalTime.now().getSecond();
            Message message2 = new Message(TEST_TOPIC_1, content2.getBytes());

            // 异步发送
            testProducer.send(message2, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("async: " + sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable throwable) {
                    System.err.println("async: " + throwable.getMessage());
                }
            });

            String content3 = "Hello, RocketMQ 3 ! Second: " + LocalTime.now().getSecond();
            Message message3 = new Message(TEST_TOPIC_1, content3.getBytes());
            // 单向
            testProducer.sendOneway(message3);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
