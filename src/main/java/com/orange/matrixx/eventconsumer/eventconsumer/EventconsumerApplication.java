package com.orange.matrixx.eventconsumer.eventconsumer;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.orange.matrixx.eventconsumer.aggregationclasses.DataAggregation;
import com.orange.matrixx.eventconsumer.objects.DataEvent;

@SpringBootApplication
public class EventconsumerApplication {

	public static void main(String[] args) {
//		SpringApplication.run(EventconsumerApplication.class, args);
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "kafka-1:19092,kafka-2:29092,kafka-3:39092");
		properties.setProperty("group.id", "test");
		properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

		try {
			KafkaSource<DataEvent> source = KafkaSource.<DataEvent>builder()
					.setBootstrapServers("kafka-1:19092,kafka-2:29092,kafka-3:39092").setTopics("MtxTest")
					.setGroupId("test").setStartingOffsets(OffsetsInitializer.earliest())
					.setValueOnlyDeserializer(new DataEvent()).build();

			env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source")
					.keyBy(value -> value.getOEGDataEvent().aggregationId).reduce(new DataAggregation())
					.addSink(new FlinkKafkaProducer<>("flink-test", new DataEvent(), properties));
			env.execute("Kafka Source");
		} catch (Exception ex) {

		}
	}

}
