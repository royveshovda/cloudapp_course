import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.Collections;

/**
 * a bolt that finds the top n words.
 */
public class TopNFinderBolt extends BaseBasicBolt {
  private HashMap<String, Integer> currentTopWords = new HashMap<String, Integer>();
  private int N;

  private long intervalToReport = 20;
  private long lastReportTime = System.currentTimeMillis();

  public TopNFinderBolt(int N) {
    this.N = N;
  }

  @Override
  public void execute(Tuple tuple, BasicOutputCollector collector) {
    String word = tuple.getStringByField("word");
    Integer count = tuple.getIntegerByField("count");


    addValue(word, count);
    sortValues();
    removeValuesMoreThanN();

    //reports the top N words periodically
    if (System.currentTimeMillis() - lastReportTime >= intervalToReport) {
      collector.emit(new Values(printMap()));
      lastReportTime = System.currentTimeMillis();
    }
  }

  private void addValue(String word, Integer count){
    currentTopWords.put(word, count);
  }

  private void sortValues(){
    LinkedList<Map.Entry<String, Integer>> list = new LinkedList(currentTopWords.entrySet());
    Collections.sort(list, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((Comparable) ((Map.Entry<String, Integer>) (o1)).getValue()).compareTo(((Map.Entry<String, Integer>) (o2)).getValue());
      }
    });

    HashMap<String, Integer> sortedHashMap = new LinkedHashMap<String, Integer>();
    for (Iterator it = list.iterator(); it.hasNext();) {
    //for (Iterator it = list.descendingIterator(); it.hasNext();) {
      Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
      sortedHashMap.put(entry.getKey(), entry.getValue());
    } 
    currentTopWords = sortedHashMap;
  }

  private void removeValuesMoreThanN(){
    while(currentTopWords.size() > this.N){
      String firstKey = getFirstKey();
      if(firstKey == null) break;

      currentTopWords.remove(firstKey);
    }
  }

  private String getFirstKey(){
    String outValue = null;
    for(String key: currentTopWords.keySet()){
      outValue = key;
      break;
    }
    return outValue;
  }


  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {

     declarer.declare(new Fields("top-N"));

  }

  public String printMap() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("top-words = [ ");
    for (String word : currentTopWords.keySet()) {
      stringBuilder.append("(" + word + " , " + currentTopWords.get(word) + ") , ");
    }
    int lastCommaIndex = stringBuilder.lastIndexOf(",");
    stringBuilder.deleteCharAt(lastCommaIndex + 1);
    stringBuilder.deleteCharAt(lastCommaIndex);
    stringBuilder.append("]");
    return stringBuilder.toString();

  }
}
