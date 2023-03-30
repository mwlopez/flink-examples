
package io.aeolabs.flink;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class DataStreamJob {

	final static String inputTopic = "countIn";
	final static String outputTopic = "countOut";
	final static String jobTitle = "AeolFlinkWord";


	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();


		env.execute("Flink Java API Skeleton");
	}
}
