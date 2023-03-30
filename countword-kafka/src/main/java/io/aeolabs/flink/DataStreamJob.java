package io.aeolabs.flink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class DataStreamJob {

    final static String inputTopic = "countIn";
    final static String outputTopic = "countOut";
    final static String jobTitle = "AeolFlink Word";

    public static void main(String[] args) throws Exception {

        final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";

        // Set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics(inputTopic)
                .setGroupId("aeol-flink-wordl")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();


        KafkaRecordSerializationSchema<String> serializer = KafkaRecordSerializationSchema.builder()
                .setValueSerializationSchema(new SimpleStringSchema())
                .setTopic(outputTopic)
                .build();

        KafkaSink<String> sink = KafkaSink.<String>builder()
                .setBootstrapServers(bootstrapServers)
                .setRecordSerializer(serializer)
                .build();

        DataStream<String> text = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");


        // Split up the lines in pairs (2-tuples) containing: (word,1)
        DataStream<String> counts = text.flatMap(new Tokenizer())
                // Group by the tuple field "0" and sum up tuple field "1"
                .keyBy(value -> value.f0)
                .sum(1)
                .flatMap(new Reducer());

        counts.sinkTo(sink);

        env.execute(jobTitle);
    }

    public static final class Tokenizer
            implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            String[] tokens = value.toLowerCase().split("\\W+");

            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
                }
            }
        }
    }

    public static final class Reducer
            implements FlatMapFunction<Tuple2<String, Integer>, String> {

        @Override
        public void flatMap(Tuple2<String, Integer> value, Collector<String> out) {
            String count = value.f0 + " " + value.f1;
            out.collect(count);
        }
    }
}
