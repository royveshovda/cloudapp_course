import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.conf.LongConfOption;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;

import java.io.IOException;

/**
 * Compute shortest paths from a given source.
 */
public class ShortestPathsComputation extends BasicComputation<
    IntWritable, IntWritable, NullWritable, IntWritable> {
  /** The shortest paths id */
  public static final LongConfOption SOURCE_ID =
      new LongConfOption("SimpleShortestPathsVertex.sourceId", 1,
          "The shortest paths id");

  /**
   * Is this vertex the source id?
   *
   * @param vertex Vertex
   * @return True if the source id
   */
  private boolean isSource(Vertex<IntWritable, ?, ?> vertex) {
    return vertex.getId().get() == SOURCE_ID.get(getConf());
  }

  @Override
  public void compute(
      Vertex<IntWritable, IntWritable, NullWritable> vertex,
      Iterable<IntWritable> messages) throws IOException {
    if (getSuperstep() == 0) {
      setValue(new IntWritable(Integer.MAX_VALUE));
    }
    int minDist = isSource() ? 0 : Integer.MAX_VALUE;
    for (IntWritable message : messages) {
      minDist = Math.min(minDist, message.get());
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Vertex " + getId() + " got minDist = " + minDist +
          " vertex value = " + getValue());
    }
    if (minDist < getValue().get()) {
      setValue(new IntWritable(minDist));
      for (Edge<IntWritable, IntWritable> edge : getEdges()) {
        int distance = minDist + edge.getValue().get();
        if (LOG.isDebugEnabled()) {
          LOG.debug("Vertex " + getId() + " sent to " +
              edge.getTargetVertexId() + " = " + distance);
        }
        sendMessage(edge.getTargetVertexId(), new IntWritable(distance));
      }
    }
    voteToHalt();
  }
}
