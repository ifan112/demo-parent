package com.ifan112.demo.rocketmq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalField;

public class DemoRocketMQApp {

    private static final String LOCAL_NAMESRV = "localhost:9876";

    private static final String TEST_TOPIC_1 = "test_topic_1";

    private static final String TEST_PRODUCER_GROUP_1 = "test_producer_group_1";

    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException {

        DefaultMQProducer testProducer = new DefaultMQProducer(TEST_PRODUCER_GROUP_1);

        testProducer.setNamesrvAddr(LOCAL_NAMESRV);
        testProducer.start();

        String content = "Hello, RocketMQ! Second: " + LocalTime.now().getSecond();
        Message message = new Message(TEST_TOPIC_1, content.getBytes(RemotingHelper.DEFAULT_CHARSET));

        try {
            SendResult sendResult = testProducer.send(message);

            System.out.println(sendResult.getMsgId());

        } catch (RemotingException | MQBrokerException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            testProducer.shutdown();
        }

    }
}
