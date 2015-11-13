package HADS.Server;

import java.util.*;
import java.io.*;

/*queue for holding transactions*/
public class ImageList {
  private final ArrayList<ImageRecord> theQ;

  public ImageList(int initialCapacity) {
    theQ = new ArrayList<ImageRecord>(initialCapacity);
  }

  public synchronized ImageRecord indexOfAndGet(ImageRecord rec) {
    ImageRecord result = null;
    int pos = theQ.indexOf(rec);
    if (pos != -1) {
      result = theQ.get(pos);
    }
    return result;
  }

  // return -1 if added because rec not already there
  // else return position >= 0
  public synchronized int addIfNotThere(ImageRecord rec) {
    int pos = theQ.indexOf(rec);
    if (pos == -1) {
      theQ.add(rec);
    }
    return pos;
  }

  public synchronized ImageRecord replace(ImageRecord o, ImageRecord n) {
    ImageRecord result = null;
    int pos = theQ.indexOf(o);
    if (pos != -1) {
      result = theQ.set(pos, n);
    }
    return result;
  }
}
