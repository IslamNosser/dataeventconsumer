package com.orange.matrixx.eventconsumer.eventconsumer;

import java.util.Properties;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.orange.matrixx.eventconsumer.aggregationclasses.DataAggregation;
import com.orange.matrixx.eventconsumer.constants.ConfigurationConstants;
import com.orange.matrixx.eventconsumer.objects.OEGEvent;
import com.orange.matrixx.eventconsumer.utils.ConstantUtilities;

@SpringBootApplication
public class EventconsumerApplication {

	public static void main(String[] args) {
//		SpringApplication.run(EventconsumerApplication.class, args);
//		String group = args[0];
		String group = "testgroup";
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		ConstantUtilities.loadConstants();
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", ConfigurationConstants.bootStrapServer);
		properties.setProperty("group.id", group);
		properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

		try {
			KafkaSource<OEGEvent> source = KafkaSource.<OEGEvent>builder()
					.setBootstrapServers(ConfigurationConstants.bootStrapServer)
					.setTopics(ConfigurationConstants.consumerTopicName).setGroupId("test")
					.setStartingOffsets(OffsetsInitializer.earliest()).setValueOnlyDeserializer(new OEGEvent()).build();

			env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source").keyBy(value -> value.aggregationId)
					.reduce(new DataAggregation()).addSink(new FlinkKafkaProducer<>(
							ConfigurationConstants.producerTopicName, new OEGEvent(), properties));
			env.execute("Kafka Source");
		} catch (Exception ex) {

		}
	}
}
