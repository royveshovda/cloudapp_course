
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class FileReaderSpout implements IRichSpout {
  private SpoutOutputCollector _collector;
  private TopologyContext context;
  private FileReader _fileReader;
  private Boolean _completed;


  @Override
  public void open(Map conf, TopologyContext context,
                   SpoutOutputCollector collector) {
    _completed = false;
    try
    {
      _fileReader = new FileReader(conf.get("input_file").toString());
      _collector = collector;   
 
    } catch (FileNotFoundException e)
    {
 
      e.printStackTrace();
    }

    this.context = context;
    this._collector = collector;
  }

  @Override
  public void nextTuple() {
    if (_completed)
    {
      try
      {
        Thread.sleep(1000);
      } catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }
 
    BufferedReader reader = new BufferedReader(_fileReader);
    String line;
    try
    {
      while ((line = reader.readLine()) != null)
      {
        _collector.emit(new Values(line), line);
      }
    } catch (IOException e)
    {
      e.printStackTrace();
    } finally
    {
      _completed = true;
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {

    declarer.declare(new Fields("word"));

  }

  @Override
  public void close() {
    try
    {
      _fileReader.close();
    } catch (IOException e)
    {
      e.printStackTrace();
    }

  }


  @Override
  public void activate() {
  }

  @Override
  public void deactivate() {
  }

  @Override
  public void ack(Object msgId) {
  }

  @Override
  public void fail(Object msgId) {
  }

  @Override
  public Map<String, Object> getComponentConfiguration() {
    return null;
  }
}
