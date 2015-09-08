
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * This topology reads a file and counts the words in that file, then finds the top N words.
 */
public class TopWordFinderTopologyPartD {

  private static final int N = 10;

  public static void main(String[] args) throws Exception {


    TopologyBuilder builder = new TopologyBuilder();

    Config config = new Config();
    config.setDebug(true);

    config.put("input_file", args[0]);

    Integer N = 10;

    builder.setSpout("spout", new FileReaderSpout());
    builder.setBolt("split", new SplitSentenceBolt(), 8).shuffleGrouping("spout");
    builder.setBolt("normalize", new NormalizerBolt(), 10).shuffleGrouping("split");
    builder.setBolt("count", new WordCountBolt(), 12).fieldsGrouping("normalize", new Fields("word"));
    builder.setBolt("top-n", new TopNFinderBolt(N), 1).globalGrouping("1");

    config.setMaxTaskParallelism(3);

    LocalCluster cluster = new LocalCluster();
    cluster.submitTopology("word-count", config, builder.createTopology());

    //wait till the file is read completely
    Thread.sleep(2 * 60 * 1000);

    cluster.shutdown();
  }
}
