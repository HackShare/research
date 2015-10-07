package HADS.Server;

import java.util.*;
import java.io.*;

/*queue for holding transactions*/
public class ResultsQueue implements Serializable {
  private final ArrayList<ResultRecord> theQ;

  public ResultsQueue(int initialCapacity) {
    theQ = new ArrayList<ResultRecord>(initialCapacity);
  }

  // direct delegation
  public synchronized int size() {
    return theQ.size();
  }

  public synchronized ResultRecord replace(ResultRecord o, ResultRecord n) {
    ResultRecord result = null;
    int pos = theQ.indexOf(o);
    if (pos != -1) {
      result = theQ.set(pos, n);
    }
    return result;
  }

  // direct delegation
  public synchronized boolean add(ResultRecord rec) {
    return theQ.add(rec);
  }

  // direct delegation
  public synchronized ResultRecord get(int i) {
    return theQ.get(i);
  }
}
